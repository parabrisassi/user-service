package com.parabrisassi.sist.user_service.web.controller.dtos.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.web.support.data_transfer.json.serializers.URISerializer;
import org.hibernate.Hibernate;

import java.net.URI;
import java.util.Set;

/**
 * Data transfer object for {@link User} class.
 */
public class UserDto {

    @JsonProperty
    private String username;

    @SuppressWarnings("unused")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Role> roles;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = URISerializer.class)
    private URI locationUrl;

    public UserDto() {
        // For Jersey
    }

    /**
     * Constructor.
     *
     * @param user        The {@link User} from which the dto will be built.
     * @param locationUrl The location url (in {@link URI} format) of the given {@link User}.
     */
    public UserDto(User user, URI locationUrl) {
        this.username = user.getUsername();
        this.roles = Hibernate.isInitialized(user.getRoles()) ? user.getRoles() : null;

        this.locationUrl = locationUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
