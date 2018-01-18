package com.parabrisassi.sist.user_service.web.controller.rest_endpoints;

import com.parabrisassi.sist.user_service.services.AuthenticationTokenService;
import com.parabrisassi.sist.user_service.web.controller.dtos.authentication.CredentialsDto;
import com.parabrisassi.sist.user_service.web.support.annotations.Base64url;
import com.parabrisassi.sist.user_service.web.support.annotations.JerseyController;
import com.parabrisassi.sist.user_service.web.support.exceptions.IllegalParamValueException;
import com.parabrisassi.sist.user_service.web.support.exceptions.MissingJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

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

/**
 * API endpoint for sessions management.
 */
@Path(AuthenticationTokenEndpoint.TOKENS_ENDPOINT)
@Produces(MediaType.APPLICATION_JSON)
@JerseyController
public class AuthenticationTokenEndpoint {

    /**
     * Indicates the header in which the token will be sent to the consumer.
     */
    private static final String TOKEN_HEADER = "X-Token";

    /**
     * Endpoint for token management (i.e issue, validation and blacklisting).
     */
    public static final String TOKENS_ENDPOINT = "/tokens";

    /**
     * Indicates the query param through which the username is indicated when validating a token.
     */
    public static final String TOKENS_OWNER_QUERY_PARAM = "username";


    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

    /**
     * Service in charge of managing authentication tokens.
     */
    private final AuthenticationTokenService authenticationTokenService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public AuthenticationTokenEndpoint(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }

    @POST
    public Response issueToken(CredentialsDto credentialsDto) {
        if (credentialsDto == null) {
            throw new MissingJsonException();
        }
        LOGGER.debug("Issuing a new token for user {}", credentialsDto.getUsername());
        final AuthenticationTokenService.TokenWrapper tokenWrapper =
                authenticationTokenService.createToken(credentialsDto.getUsername(), credentialsDto.getPassword());
        LOGGER.debug("User {} successfully logged in", credentialsDto.getUsername());
        final URI tokenUri = uriInfo.getBaseUriBuilder()
                .path(TOKENS_ENDPOINT)
                .path(Base64Utils.encodeToUrlSafeString(Long.toString(tokenWrapper.getId()).getBytes()))
                .build();
        return Response.created(tokenUri)
                .header(TOKEN_HEADER, tokenWrapper.getRawToken())
                .build();

    }

    @GET
    @Path("{tokenId : .+}")
    public Response validateToken(@PathParam("tokenId") @Base64url final Long tokenId,
                                  @QueryParam(TOKENS_OWNER_QUERY_PARAM) final String username) {
        final List<String> invalidParams = new LinkedList<>();
        if (tokenId == null) {
            invalidParams.add("tokenId");
        }
        if (username == null) {
            invalidParams.add("username");
        }
        if (!invalidParams.isEmpty()) {
            throw new IllegalParamValueException(invalidParams);
        }
        LOGGER.debug("Validating authentication token with id {}", tokenId);
        //noinspection ConstantConditions  Previous statements would have thrown exception in case of null tokenId
        return Optional.of(authenticationTokenService.isValidToken(tokenId, username))
                .filter(Boolean::booleanValue)
                .map(flag -> Response.noContent())
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .entity("")
                .build();
    }

    @DELETE
    @Path("{tokenId : .+}")
    public Response blacklistToken(@PathParam("tokenId") @Base64url final Long tokenId) {
        if (tokenId == null) {
            throw new IllegalParamValueException(Collections.singletonList("tokenId"));
        }
        LOGGER.debug("Blacklisting authentication token with id {}", tokenId);
        authenticationTokenService.blacklistToken(tokenId);
        return Response.noContent().build();
    }
}
