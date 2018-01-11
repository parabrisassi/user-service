package com.parabrisassi.sist.user_service.web.controller.rest_endpoints;

import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationExceptionThrower;
import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.services.UserService;
import com.parabrisassi.sist.user_service.web.controller.dtos.entities.StringValueDto;
import com.parabrisassi.sist.user_service.web.controller.dtos.entities.UserDto;
import com.parabrisassi.sist.user_service.web.support.annotations.JerseyController;
import com.parabrisassi.sist.user_service.web.support.annotations.PaginationParam;
import com.parabrisassi.sist.user_service.web.support.exceptions.IllegalParamValueException;
import com.parabrisassi.sist.user_service.web.support.exceptions.MissingJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * API endpoint for {@link User}s management.
 */
@Path(UserEndpoint.USERS_ENDPOINT)
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class UserEndpoint implements ValidationExceptionThrower {

    /**
     * Endpoint for {@link User} management.
     */
    public static final String USERS_ENDPOINT = "/users";

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

    @Context
    private UriInfo uriInfo;

    /**
     * The {@link UserService}.
     */
    private final UserService userService;


    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }


    // ================================================================
    // API Methods
    // ================================================================

    // ======================================
    // Basic User Operation
    // ======================================


    @GET
    public Response findMatching(@QueryParam("username") final String username,
                                 @PaginationParam final Pageable pageable) {
        LOGGER.debug("Getting users matching");

        final Page<User> users = userService
                .findMatching(username, pageable);
        return Response.ok(users.getContent().stream()
                .map(user -> new UserDto(user, getLocationUri(user, uriInfo)))
                .collect(Collectors.toList()))
                .build();
    }

    @GET
    @Path("{username : .+}")
    public Response getUserByUsername(@PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Getting user by username {}", username);
        return getUserBySomePropertyResponse(userService.getByUsername(username), uriInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(final UserDto userDto) {
        return Optional.ofNullable(userDto)
                .map(dto -> {
                    LOGGER.debug("Creating user with username {}", userDto.getUsername());
                    return userService.register(userDto.getUsername(), userDto.getPassword());
                })
                .map(user -> Response
                        .created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getId())).build())
                        .build())
                .orElseThrow(MissingJsonException::new);
    }

    @PUT
    @Path("{username : .+}/username")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeUsername(@PathParam("username") final String username, final StringValueDto newUsernameDto) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        return Optional.ofNullable(newUsernameDto)
                .map(StringValueDto::getValue)
                .map(newUsername -> {
                    LOGGER.debug("Changing username to {} to user with username {}", newUsername, username);
                    userService.changeUsername(username, newUsername);
                    return Response.noContent().build(); // TODO: add location header as username changed
                })
                .orElseThrow(MissingJsonException::new);
    }

    @PUT
    @Path("{username : .+}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@PathParam("username") final String username,
                                   final UserDto.PasswordChangeDto passwordChangeDto) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        return Optional.ofNullable(passwordChangeDto)
                .map(dto -> {
                    LOGGER.debug("Changing password to user with username {} ", username);
                    userService.changePassword(username, dto.getCurrentPassword(), dto.getNewPassword());
                    return Response.noContent().build();
                })
                .orElseThrow(MissingJsonException::new);
    }

    // TODO: add controller for roles

//    @GET
//    @Path("{id : \\d+}/authorities")
//    public Response getAuthorities(@PathParam("id") final long id) {
//        if (id <= 0) {
//            throw new IllegalParamValueException(Collections.singletonList("id"));
//        }
//        LOGGER.debug("Getting authorities for user with id {} ", id);
//        final Set<Role> roles = userService.getRoles(id);
//        return Response.ok(roles).build();
//    }
//
//    @PUT
//    @Path("{id : \\d+}/authorities/{role: .+}")
//    public Response addAuthority(@PathParam("id") final long id, @PathParam("role") final Role role) {
//        validateRoleParams(id, role);
//        userService.addRole(id, role);
//        LOGGER.debug("Adding role {} to user with id {} ", role, id);
//        return Response.noContent().build();
//    }
//
//    @DELETE
//    @Path("{id : \\d+}/authorities/{role: .+}")
//    public Response removeAuthority(@PathParam("id") final long id, @PathParam("role") final Role role) {
//        validateRoleParams(id, role);
//        userService.removeRole(id, role);
//        LOGGER.debug("Removing role {} to user with id {} ", role, id);
//        return Response.noContent().build();
//    }

    @DELETE
    @Path("{username : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteByUsername(@PathParam("username") final String username) {
        LOGGER.debug("Removing user with username {}", username);
        userService.deleteByUsername(username);
        return Response.noContent().build();
    }


    // ======================================
    // Helper Methods
    // ======================================

    /**
     * Returns a {@link Response} according to the given {@code userOptional} content.
     *
     * @param userOptional The {@link Optional} that might hold a {@link User}.
     * @return A {@link Response} containing the {@link UserDto}
     * created with the held in the given {@link Optional} if present,
     * or a {@link Response.Status#NOT_FOUND} {@link Response} otherwise.
     */
    private static Response getUserBySomePropertyResponse(@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                                                                  Optional<User> userOptional, UriInfo uriInfo) {
        return userOptional.map(user -> Response.ok(new UserDto(user, getLocationUri(user, uriInfo))))
                .orElse(Response.status(Response.Status.NOT_FOUND).entity(""))
                .build();
    }

    /**
     * Returns the location {@link URI} of the given {@link User}
     * according to the context hold by the given {@link UriInfo}
     *
     * @param user    The {@link User}'s whose location {@link URI} must be retrieved.
     * @param uriInfo The {@link UriInfo} holding the context.
     * @return The location {@link URI} of the given {@link User}
     */
    private static URI getLocationUri(User user, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().clone()
                .path(USERS_ENDPOINT)
                .path(Long.toString(user.getId()))
                .build();
    }

    /**
     * Performs validation over the given params.
     *
     * @param id   The {@link User} id param to be validated.
     * @param role The {@link Role} param to be validated.
     * @throws IllegalParamValueException If any of the params is not valid.
     */
    private static void validateRoleParams(long id, Role role) throws IllegalParamValueException {
        final List<String> paramErrors = new LinkedList<>();
        if (id <= 0) {
            paramErrors.add("id");
        }
        if (role == null) {
            paramErrors.add("role");
        }
        if (!paramErrors.isEmpty()) {
            throw new IllegalParamValueException(paramErrors);
        }
    }
}
