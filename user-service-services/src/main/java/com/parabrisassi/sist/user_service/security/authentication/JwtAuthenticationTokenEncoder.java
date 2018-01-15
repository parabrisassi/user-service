package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.services.AuthenticationTokenService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AuthenticationTokenEncoder}, using JSON Web Tokens as the token encoding.
 */
@Component
public class JwtAuthenticationTokenEncoder implements AuthenticationTokenEncoder {

    private final static String ROLES_CLAIM_NAME = "roles";

    /**
     * The secret key used to sign the tokens, encoded in base 64.
     */
    private final String base64EncodedSecretKey;

    /**
     * The duration of tokens, in milliseconds.
     */
    private final long duration;

    /**
     * THe signing algorithm used to sign tokens.
     */
    private final SignatureAlgorithm signatureAlgorithm;

    /**
     * Constructor.
     *
     * @param secretKey The secret key used to sign the tokens
     * @param duration  The duration of tokens, in seconds
     */
    /* package */ JwtAuthenticationTokenEncoder(@Value("${custom.security.jwt.signing-key}") String secretKey,
                                                @Value("${custom.security.jwt.duration}") Long duration) {
        this.base64EncodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.duration = duration * 1000;
        this.signatureAlgorithm = SignatureAlgorithm.HS512;
    }

    @Override
    public String encode(AuthenticationTokenService.TokenData token) {
        Objects.requireNonNull(token, "The token must not be null");

        final Claims claims = Jwts.claims();
        claims.setId(String.valueOf(token.getId()));
        claims.setSubject(token.getUsername());
        claims.put(ROLES_CLAIM_NAME, token.getRoles());
        final Instant now = Instant.now();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(duration)))
                .signWith(signatureAlgorithm, base64EncodedSecretKey)
                .compact();
    }

    @Override
    public AuthenticationTokenService.TokenData decode(String encodedToken) throws TokenDecodingException {
        if (!StringUtils.hasText(encodedToken)) {
            throw new IllegalArgumentException("The token must not be null or empty");
        }
        try {
            final Claims claims = Jwts.parser()
                    .setSigningKey(base64EncodedSecretKey)
                    .parse(encodedToken, CustomJwtHandlerAdapter.getInstance())
                    .getBody();

            // Previous step validated the following values
            final long tokenId = Long.valueOf(claims.getId());
            final String username = claims.getSubject();
            @SuppressWarnings("unchecked") final Set<Role> roles = (Set<Role>) claims.get(ROLES_CLAIM_NAME);

            return new AuthenticationTokenService.TokenData(tokenId, username, roles);

        } catch (MalformedJwtException | SignatureException | ExpiredJwtException | UnsupportedJwtException
                | MissingClaimException e) {
            throw new TokenDecodingException("There was a problem with the jwt token", e);
        }
    }

    /**
     * Custom implementation of {@link JwtHandlerAdapter}.
     */
    private static class CustomJwtHandlerAdapter extends JwtHandlerAdapter<Jws<Claims>> {

        /**
         * Single instance of this class.
         */
        private static CustomJwtHandlerAdapter singleton = new CustomJwtHandlerAdapter();

        /**
         * @return The single instance of this class.
         */
        private static CustomJwtHandlerAdapter getInstance() {
            return singleton;
        }


        @Override
        public Jws<Claims> onClaimsJws(Jws<Claims> jws) {
            final JwsHeader<?> header = jws.getHeader();
            final Claims claims = jws.getBody();

            // Check jti is not missing
            final String jtiString = claims.getId();
            if (!StringUtils.hasText(jtiString)) {
                throw new MissingClaimException(header, claims, "Missing \"jwt id\" claim");
            }
            // Check if the jtiString is a long
            try {
                //noinspection ResultOfMethodCallIgnored
                Long.valueOf(jtiString);
            } catch (NumberFormatException e) {
                throw new MalformedJwtException("The \"jwt id\" claim must be an integer or a long", e);
            }

            // Check roles is not missing
            final Object rolesObject = claims.get(ROLES_CLAIM_NAME);
            if (rolesObject == null) {
                throw new MissingClaimException(header, claims, "Missing \"roles\" claim");
            }
            // Check roles is a Collection
            if (!(rolesObject instanceof Collection)) {
                throw new MalformedJwtException("The \"roles\" claim must be a collection");
            }
            // Transform the collection into a Set of Role
            @SuppressWarnings("unchecked") final Set<Role> roles = ((Collection<String>) rolesObject).stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
            claims.put(ROLES_CLAIM_NAME, roles);

            // Check issued at date is present and it is not a future date
            final Date issuedAt = Optional.ofNullable(claims.getIssuedAt())
                    .orElseThrow(() ->
                            new MissingClaimException(header, claims, "Missing \"issued at\" date"));
            if (issuedAt.after(new Date())) {
                throw new MalformedJwtException("The \"issued at\" date is a future date");
            }
            // Check expiration date is not missing
            if (claims.getExpiration() == null) {
                throw new MissingClaimException(header, claims, "Missing \"expiration\" date");
            }

            return jws;
        }
    }
}
