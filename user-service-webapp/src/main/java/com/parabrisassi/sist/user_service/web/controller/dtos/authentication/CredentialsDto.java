package com.parabrisassi.sist.user_service.web.controller.dtos.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object to be used to transfer user credentials from a client.
 */
public final class CredentialsDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
