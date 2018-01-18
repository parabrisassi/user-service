package com.parabrisassi.sist.user_service.web.controller.dtos.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.services.AuthenticationTokenService.TokenData;
import com.parabrisassi.sist.user_service.web.support.data_transfer.Base64UrlHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Data transfer object for {@link TokenData} class.
 */
public final class TokenDataDto {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String id;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final List<Role> roles;

    /**
     * Constructor.
     *
     * @param token The token from where data is taken.
     */
    public TokenDataDto(TokenData token) {
        this.id = Base64UrlHelper.encodeFromNumber(token.getId(), Object::toString);
        this.roles = new LinkedList<>(token.getRoles());
    }
}
