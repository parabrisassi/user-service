package com.parabrisassi.sist.user_service.services;

import com.parabrisassi.sist.commons.authentication.TokenData;
import com.parabrisassi.sist.commons.errors.ValidationError;
import com.parabrisassi.sist.commons.exceptions.NoSuchEntityException;
import com.parabrisassi.sist.commons.exceptions.UnauthenticatedException;
import com.parabrisassi.sist.commons.exceptions.ValidationException;
import com.parabrisassi.sist.commons.validation.ValidationExceptionThrower;
import com.parabrisassi.sist.commons.validation.ValidationHelper;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.models.UserCredential;
import com.parabrisassi.sist.user_service.persistence.daos.AuthenticationTokenDao;
import com.parabrisassi.sist.user_service.persistence.daos.UserCredentialDao;
import com.parabrisassi.sist.user_service.persistence.daos.UserDao;
import com.parabrisassi.sist.user_service.security.authentication.AuthenticationTokenEncoder;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import static com.parabrisassi.sist.commons.errors.ValidationError.ErrorCause.MISSING_VALUE;
import static com.parabrisassi.sist.user_service.models.constants.ValidationErrorConstants.MISSING_USERNAME;

/**
 * Concrete implementation of {@link AuthenticationTokenService}.
 */
@Service
@Transactional(readOnly = true)
public class AuthenticationTokenServiceImpl implements AuthenticationTokenService, ValidationExceptionThrower {

    /**
     * Amount of tries to perform the session creation
     */
    private static final int MAX_TRIES = 10;

    /**
     * A DAO in charge of loading {@link User}s.
     */
    private final UserDao userDao;

    /**
     * A DAO in charge of loading {@link AuthenticationToken}s.
     */
    private final AuthenticationTokenDao authenticationTokenDao;

    /**
     * A DAO in charge of loading {@link UserCredential}s.
     */
    private final UserCredentialDao userCredentialDao;

    /**
     * A {@link PasswordEncoder} to match credentials when issuing an {@link AuthenticationToken}.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * An {@link AuthenticationTokenEncoder}
     * which defines how an {@link AuthenticationToken} is encoded/decoded into/from a {@link String}.
     * This will actually define the "token protocol".
     */
    private final AuthenticationTokenEncoder authenticationTokenEncoder;

    @Autowired
    public AuthenticationTokenServiceImpl(UserDao userDao, AuthenticationTokenDao authenticationTokenDao,
                                          UserCredentialDao userCredentialDao, PasswordEncoder passwordEncoder,
                                          AuthenticationTokenEncoder authenticationTokenEncoder) {
        this.userDao = userDao;
        this.authenticationTokenDao = authenticationTokenDao;
        this.userCredentialDao = userCredentialDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationTokenEncoder = authenticationTokenEncoder;
    }

    @Override
    @PreAuthorize("@userPermissionProvider.readByUsername(#username)")
    public Page<TokenData> listTokens(String username, Pageable pageable) {
        final User user = userDao.findByUsername(username).orElseThrow(NoSuchEntityException::new);
        // Remove all sorting stuff from pageable
        final Pageable notSortablePageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
        return authenticationTokenDao.findByUserAndValidTrue(user, notSortablePageable)
                .map(token -> new TokenData(token.getId(), token.getUser().getUsername(), token.getUser().getRoles()));
    }

    @Override
    @Transactional
    public TokenWrapper createToken(String username, String password) {
        validateTokenCreationArguments(username, password);
        final User user = userDao.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Unknown user"));
        validateCredentials(user, password);
        Hibernate.initialize(user.getRoles());

        final AuthenticationToken token = doCreateToken(user);
        final TokenData tokenData = new TokenData(token.getId(),
                token.getUser().getUsername(),
                token.getUser().getRoles());

        return new TokenWrapper(tokenData.getId(), authenticationTokenEncoder.encode(tokenData));
    }

    @Override
    public boolean isBlacklisted(long tokenId) {
        return authenticationTokenDao.findById(tokenId)
                .filter(AuthenticationToken::isValid)
                .isPresent();
    }

    @Override
    @Transactional
    @PreAuthorize("@authenticationTokenPermissionProvider.isOwnerOrAdmin(#id)")
    public void blacklistToken(long id) {
        authenticationTokenDao.findById(id)
                .ifPresent(token -> {
                    token.blacklist();
                    authenticationTokenDao.save(token);
                });
    }


    // ================================
    // Helpers
    // ================================

    /**
     * Validates the given arguments for {@link AuthenticationToken} creation.
     *
     * @param username The username to be validated.
     * @param password The password to be validated.
     * @throws ValidationException If the credentials are not valid.
     */
    private void validateTokenCreationArguments(String username, String password) throws ValidationException {
        final List<ValidationError> errorList = new LinkedList<>();
        ValidationHelper.objectNotNull(username, errorList, MISSING_USERNAME);
        ValidationHelper.objectNotNull(password, errorList, MISSING_PASSWORD);

        throwIfNotEmpty(errorList);
    }

    /**
     * Validates that the given {@code password} matches the given {@link User}s actual password.
     *
     * @param user     The {@link User} whose password must be validated.
     * @param password The provided password, which must be matched against the given {@link User}s actual password.
     * @throws InvalidCredentialsException If the password does not match.
     */
    private void validateCredentials(User user, String password) throws InvalidCredentialsException {
        final UserCredential userCredential = userCredentialDao.findActualByUser(user)
                .orElseThrow(RuntimeException::new); // TODO: define exception?

        if (!passwordEncoder.matches(password, userCredential.getHashedPassword())) {
            throw new InvalidCredentialsException("Password does not match");
        }
    }

    /**
     * Tries to create an {@link AuthenticationToken}. Might fail if it already exists a {@link AuthenticationToken}
     * with the same id, although this is a rare situation.
     *
     * @param user The {@link User} to which the {@link AuthenticationToken} belongs to.
     * @return The created token.
     * @throws RuntimeException If the session could not be created.
     */
    private AuthenticationToken doCreateToken(User user) throws RuntimeException {
        int tries = 0;
        while (tries < MAX_TRIES) {
            final long randomId = new SecureRandom().nextLong();
            if (!authenticationTokenDao.exists(randomId)) {
                final AuthenticationToken token = new AuthenticationToken(randomId, user);
                return authenticationTokenDao.save(token);
            }
            tries++;
        }
        throw new RuntimeException("Could not create an authentication token after " + MAX_TRIES + "tries");
    }

    private static final ValidationError MISSING_PASSWORD = new ValidationError(MISSING_VALUE, "password",
            "The password is missing");

    /**
     * Exception thrown when any of the authentication credentials (user and password) are not correct.
     */
    private class InvalidCredentialsException extends UnauthenticatedException {

        /**
         * Default constructor.
         */
        private InvalidCredentialsException() {
            super();
        }

        /**
         * Constructor which can set a {@code message}.
         *
         * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
         */
        private InvalidCredentialsException(String message) {
            super(message);
        }

        /**
         * Constructor which can set a mes{@code message} and a {@code cause}.
         *
         * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
         * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
         *                For more information, see {@link RuntimeException#RuntimeException(Throwable)}.
         */
        private InvalidCredentialsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
