package com.parabrisassi.sist.user_service.models;

/**
 * Enum containing the different roles a {@link User} can have (i.e the different authorities it has).
 */
public enum Role {
    /**
     * Indicates a {@link User} is a normal user.
     */
    ROLE_USER,
    /**
     * Indicates a {@link User} is an administrator.
     */
    ROLE_ADMIN;


    /**
     * @return The {@link String} representation of a {@link Role}, replacing '_' into '-', and in lowercase.
     */
    @Override
    public String toString() {
        return super.toString().replace("_", "-").toLowerCase();
    }

    /**
     * Generates a {@link Role} from the given {@code value},
     * applying the inverse operation performed in the {@link #toString()} method.
     *
     * @param value The {@link String} from which the {@link Role} will be created.
     * @return The {@link Role} created by the {@code value}.
     */
    public static Role fromString(String value) {
        return valueOf(value.replace("-", "_").toUpperCase());
    }
}
