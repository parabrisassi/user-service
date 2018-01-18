package com.parabrisassi.sist.user_service.web.controller.rest_endpoints;

import com.parabrisassi.sist.user_service.services.AuthenticationTokenService;
import com.parabrisassi.sist.user_service.services.AuthenticationTokenService.TokenData;
import com.parabrisassi.sist.user_service.web.controller.dtos.entities.TokenDataDto;
import com.parabrisassi.sist.user_service.web.support.annotations.JerseyController;
import com.parabrisassi.sist.user_service.web.support.annotations.PaginationParam;
import com.parabrisassi.sist.user_service.web.support.exceptions.IllegalParamValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Juan Marcos Bellini on 18/1/18.
 * Questions at jbellini@bellotapps.com
 */
@Path(UserEndpoint.USERS_ENDPOINT + "/{username : .+}" + AuthenticationTokenEndpoint.TOKENS_ENDPOINT)
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class UserTokensEndpoint {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

    /**
     * An {@link AuthenticationTokenService} to manage tokens data.
     */
    private final AuthenticationTokenService authenticationTokenService;

    @Autowired
    public UserTokensEndpoint(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }

    @GET
    public Response listTokens(@SuppressWarnings("RSReferenceInspection")
                               @PathParam("username") final String username,
                               @PaginationParam final Pageable pageable) {
        if (username == null) {
            throw new IllegalParamValueException(Collections.singletonList("username"));
        }
        LOGGER.debug("Getting {} tokens", username);

        final Page<TokenData> tokens = authenticationTokenService.listTokens(username, pageable);
        return Response.ok(tokens.getContent().stream()
                .map(TokenDataDto::new)
                .collect(Collectors.toList()))
                .build();
    }
}
