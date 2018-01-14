package com.parabrisassi.sist.user_service.security;

import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.models.UserCredential;
import com.parabrisassi.sist.user_service.persistence.daos.UserCredentialDao;
import com.parabrisassi.sist.user_service.persistence.daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation of a {@link UserDetailsService},
 * which loads users using a {@link UserDao}, and credentials with a {@link UserCredentialDao}.
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * DAO to get user information.
     */
    private final UserDao userDao;

    /**
     * DAO to get a given {@link User}'s actual credentials.
     */
    private final UserCredentialDao userCredentialDao;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao, UserCredentialDao userCredentialDao) {
        this.userDao = userDao;
        this.userCredentialDao = userCredentialDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username " + username));
        final UserCredential userCredential = userCredentialDao.findActualByUser(user)
                .orElseThrow(RuntimeException::new); // TODO: define exception?

        final List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // TODO: check enable and stuff
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                userCredential.getHashedPassword(), authorities);
    }
}
