package com.parabrisassi.sist.user_service.web.controller.rest_endpoints;

import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.services.UserService;
import com.parabrisassi.sist.user_service.web.support.annotations.JerseyController;
import com.parabrisassi.sist.user_service.web.support.exceptions.IllegalParamValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.parabrisassi.sist.user_service.web.controller.rest_endpoints.UserRolesEndpoint.ROLES_ENDPOINT;

/**
 * API endpoint for {@link User}s {@link com.parabrisassi.sist.user_service.models.Role}s management.
 */
@Path(UserEndpoint.USERS_ENDPOINT + "/{username : .+}" + ROLES_ENDPOINT)
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class UserRolesEndpoint {

    public final static String ROLES_ENDPOINT = "/roles";

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRolesEndpoint.class);

    /**
     * The {@link UserService}.
     */
    private final UserService userService;

    @Autowired
    public UserRolesEndpoint(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response getUserRoles(@SuppressWarnings("RSReferenceInspection")
                                 @PathParam("username") final String username) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Getting roles for user {}", username);
        final Set<Role> roles = userService.getRoles(username);
        return Response.ok(roles).build();

    }

    @PUT
    @Path("{role: .+}")
    public Response addRoleToUser(@SuppressWarnings("RSReferenceInspection")
                                  @PathParam("username") final String username,
                                  @PathParam("role") final Role role) {
        validateRoleParams(username, role);
        LOGGER.debug("Adding role {} to user {}", role, username);
        userService.addRole(username, role);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{role: .+}")
    public Response removeRoleToUser(@SuppressWarnings("RSReferenceInspection")
                                     @PathParam("username") final String username,
                                     @PathParam("role") final Role role) {
        validateRoleParams(username, role);
        LOGGER.debug("Removing role {} to user {}", role, username);
        userService.removeRole(username, role);
        return Response.noContent().build();
    }


    /**
     * Performs validation over the given params.
     *
     * @param username The username param to be validated.
     * @param role     The {@link Role} param to be validated.
     * @throws IllegalParamValueException If any of the params is not valid.
     */
    private static void validateRoleParams(String username, Role role) throws IllegalParamValueException {
        final List<String> paramErrors = new LinkedList<>();
        if (username == null) {
            paramErrors.add("username");
        }
        if (role == null) {
            paramErrors.add("role");
        }
        if (!paramErrors.isEmpty()) {
            throw new IllegalParamValueException(paramErrors);
        }
    }

}
