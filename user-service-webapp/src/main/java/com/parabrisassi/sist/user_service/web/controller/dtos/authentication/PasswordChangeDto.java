package com.parabrisassi.sist.user_service.web.controller.dtos.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for getting password change values (i.e current password and new password)
 */
public final class PasswordChangeDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentPassword;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    public PasswordChangeDto() {
        // For Jersey
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
