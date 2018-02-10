package com.parabrisassi.sist.user_service.persistence.daos;

import com.parabrisassi.sist.commons.repositories.ExtendedJpaRepository;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Defines behaviour of the DAO in charge of managing {@link AuthenticationToken}s data.
 */
@Repository
public interface AuthenticationTokenDao extends ExtendedJpaRepository<AuthenticationToken, Long> {

    /**
     * Retrieves a {@link Page} of valid {@link AuthenticationToken}s belonging to the given {@code user},
     * according to the given {@code pageable}.
     *
     * @param user     The {@link User} owning the resultant {@link AuthenticationToken}s.
     * @param pageable The {@link Pageable} used to set page stuff.
     * @return The resultant {@link Page}.
     */
    Page<AuthenticationToken> findByUserAndValidTrue(User user, Pageable pageable);
}
