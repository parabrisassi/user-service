package com.parabrisassi.sist.user_service.persistence;

import com.parabrisassi.sist.commons.exceptions.InvalidPropertiesException;
import com.parabrisassi.sist.commons.persistence.PersistenceHelper;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.persistence.query_helpers.UserQueryHelper;
import org.hibernate.criterion.MatchMode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Concrete implementation of a {@link UserQueryHelper}.
 */
@Component
public class UserQueryHelperImpl implements UserQueryHelper {

    @Override
    public Specification<User> createUserSpecification(String username) {
        return (root, query, cb) ->
                Optional.ofNullable(username)
                        .map(str -> PersistenceHelper
                                .toLikePredicate(cb, root, "username", str, MatchMode.ANYWHERE, false))
                        .orElse(cb.and());
    }

    @Override
    public void validatePageable(Pageable pageable) throws InvalidPropertiesException {
        PersistenceHelper.validatePageable(pageable, User.class);
    }
}
