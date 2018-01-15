package com.parabrisassi.sist.user_service.persistence.daos;

import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.models.UserCredential;
import com.parabrisassi.sist.user_service.persistence.custom_repositories.ExtendedJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Defines behaviour of the DAO in charge of managing {@link UserCredential}s data.
 */
@Repository
public interface UserCredentialDao extends ExtendedJpaRepository<UserCredential, Long> {

    @Query("SELECT uc FROM UserCredential uc WHERE uc.user = :user " +
            "AND uc.createdAt = (SELECT MAX(aux.createdAt) FROM UserCredential aux WHERE aux.user = :user)")
    Optional<UserCredential> findActualByUser(@Param("user") User user);
}
