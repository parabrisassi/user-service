package com.parabrisassi.sist.user_service.services;

import com.parabrisassi.sist.user_service.error_handling.errros.UniqueViolationError;
import com.parabrisassi.sist.user_service.error_handling.helpers.UniqueViolationExceptionThrower;
import com.parabrisassi.sist.user_service.exceptions.NoSuchEntityException;
import com.parabrisassi.sist.user_service.exceptions.UnauthorizedException;
import com.parabrisassi.sist.user_service.exceptions.ValidationException;
import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.models.UserCredential;
import com.parabrisassi.sist.user_service.persistence.daos.UserCredentialDao;
import com.parabrisassi.sist.user_service.persistence.daos.UserDao;
import com.parabrisassi.sist.user_service.persistence.query_helpers.UserQueryHelper;
import com.parabrisassi.sist.user_service.security.PasswordValidator;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Concrete implementation of {@link UserService}.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UniqueViolationExceptionThrower {

    /**
     * DAO for managing {@link User}s data.
     */
    private final UserDao userDao;

    /**
     * DAO for managing user credentials.
     */
    private final UserCredentialDao userCredentialDao;

    /**
     * Object in charge of creating {@link org.springframework.data.jpa.domain.Specification} of {@link User}s.
     */
    private final UserQueryHelper userQueryHelper;

    /**
     * {@link PasswordValidator} used to check whether a password is valid.
     */
    private final PasswordValidator passwordValidator;

    /**
     * {@link PasswordEncoder} used for hashing passwords when creating a new {@link User}.
     */
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserDao userDao, UserCredentialDao userCredentialDao, UserQueryHelper userQueryHelper,
                           PasswordValidator passwordValidator, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.userCredentialDao = userCredentialDao;
        this.userQueryHelper = userQueryHelper;
        this.passwordValidator = passwordValidator;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @PreAuthorize("@userPermissionProvider.isAdmin()")
    public Page<User> findMatching(String username, Pageable pageable) {
        userQueryHelper.validatePageable(pageable);
        final Specification<User> matching = userQueryHelper.createUserSpecification(username);

        return userDao.findAll(matching, pageable);
    }

    @Override
    @PreAuthorize("@userPermissionProvider.readByUsername(#username)")
    public Optional<User> getByUsername(String username) {
        return getInitializing(userDao::findByUsername, username);
    }


    @Override
    @Transactional
    public User register(String username, String password) {
        final List<UniqueViolationError> errorList = new LinkedList<>();
        checkUsernameUniqueness(username, errorList);
        throwUniqueViolationException(errorList);

        final User user = userDao.save(new User(username));
        createCredential(user, password);
        return user;
    }

    @Override
    @Transactional
    @PreAuthorize("@userPermissionProvider.writeByUsername(#oldUsername)")
    public void changeUsername(String oldUsername, String newUsername) {
        final User user = userDao.findByUsername(oldUsername).orElseThrow(NoSuchEntityException::new);

        final List<UniqueViolationError> errorList = new LinkedList<>();
        checkUsernameUniqueness(newUsername, errorList);
        throwUniqueViolationException(errorList);

        user.changeUsername(newUsername);
        userDao.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("@userPermissionProvider.writeByUsername(#username)")
    public void changePassword(String username, String currentPassword, String newPassword) {
        final User user = userDao.findByUsername(username).orElseThrow(NoSuchEntityException::new);
        final UserCredential userCredential = userCredentialDao.findActualByUser(user)
                .orElseThrow(RuntimeException::new); // TODO: define what happens here

        // If currentPassword doesn't match with the actual password, do not change it
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, userCredential.getHashedPassword())) {
            throw new UnauthorizedException("The given current password did not match the user's password");
        }
        createCredential(user, newPassword);
    }

    @Override
    @PreAuthorize("@userPermissionProvider.isAdmin()")
    public Set<Role> getRoles(String username) {
        return getInitializing(userDao::findByUsername, username)
                .map(User::getRoles)
                .orElseThrow(NoSuchEntityException::new);
    }

    @Override
    @Transactional
    @PreAuthorize("@userPermissionProvider.isAdmin()")
    public void addRole(String username, Role role) {
        final User user = userDao.findByUsername(username).orElseThrow(NoSuchEntityException::new);
        user.addRole(role);
        userDao.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("@userPermissionProvider.isAdmin()")
    public void removeRole(String username, Role role) {
        final User user = userDao.findByUsername(username).orElseThrow(NoSuchEntityException::new);
        user.removeRole(role);
        userDao.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("@userPermissionProvider.deleteByUsername(#username)")
    public void deleteByUsername(String username) {
        userDao.findByUsername(username).ifPresent(userDao::delete);
        // TODO: remove all credentials?
    }


    // ================================
    // Helpers
    // ================================

    /**
     * @param username  The username that must be unique.
     * @param errorList A {@link List} of {@link UniqueViolationError}
     *                  that might have occurred before executing the method.
     */
    private void checkUsernameUniqueness(String username, List<UniqueViolationError> errorList) {
        if (userDao.existsByUsername(username)) {
            errorList.add(USERNAME_IN_USE);
        }
    }

    /**
     * Retrieves a {@link User} {@link Optional} using the given {@code searchFunction}, and the given {@code criteria}.
     * Will initialize all LAZY relationships of the retrieved {@link User}.
     * In case no {@link User} was found, the {@link Optional} will be empty.
     *
     * @param searchFunction The {@link Function} to be used to search.
     * @param criteria       The criteria used to find the user (i.e the input for the given {@code searchFunction}.
     * @param <T>            The specific type of the given {@code criteria}.
     * @return A nullable {@link Optional} containing the matching {@link User}.
     */
    private static <T> Optional<User> getInitializing(Function<T, Optional<User>> searchFunction, T criteria) {
        final Optional<User> user = searchFunction.apply(criteria);
        // Initializes LAZY relationships
        user.map(User::getRoles).ifPresent(Hibernate::initialize);

        return user;
    }

    /**
     * Creates a new credential for the given {@link User} using the given {@code password},
     * performing validation of it before.
     *
     * @param user     The {@link User} owning the new credential.
     * @param password The password for the credential.
     * @throws ValidationException In case the password is not valid.
     */
    private void createCredential(User user, String password) throws ValidationException {
        Assert.notNull(user, "The user must not be null");

        passwordValidator.validate(password);
        final String hashedPassword = passwordEncoder.encode(password);
        final UserCredential userCredential = new UserCredential(user, hashedPassword);
        userCredentialDao.save(userCredential);

    }

    private static final UniqueViolationError USERNAME_IN_USE =
            new UniqueViolationError("The username is already in use", "username");
}
