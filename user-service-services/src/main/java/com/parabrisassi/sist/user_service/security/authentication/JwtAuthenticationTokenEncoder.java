package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.commons.authentication.TokenData;
import com.parabrisassi.sist.commons.roles.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AuthenticationTokenEncoder}, using JSON Web Tokens as the token encoding.
 */
@Component
public class JwtAuthenticationTokenEncoder implements AuthenticationTokenEncoder {

    private final static String ROLES_CLAIM_NAME = "roles";

    /**
     * The private key used to sign tokens.
     */
    private final PrivateKey privateKey;

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
     * @param keyFactory       The {@link KeyFactory} used to generate the {@link PrivateKey} instance.
     * @param privateKeyString The private key (base64 encoded) used to sign the tokens.
     * @param duration         The duration of tokens, in seconds.
     * @throws InvalidKeySpecException In case the key is invalid.
     */
    /* package */ JwtAuthenticationTokenEncoder(KeyFactory keyFactory,
                                                @Value("${com.parabrisassi.sist.authentication.jwt.key.private}")
                                                        String privateKeyString,
                                                @Value("${com.parabrisassi.sist.authentication.jwt.duration}")
                                                        Long duration)
            throws InvalidKeySpecException {
        this.duration = duration * 1000;
        this.signatureAlgorithm = SignatureAlgorithm.RS512;
        this.privateKey =
                keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKeyString)));
    }

    @Override
    public String encode(TokenData token) {
        Objects.requireNonNull(token, "The token must not be null");

        final Claims claims = Jwts.claims();
        claims.setId(String.valueOf(token.getId()));
        claims.setSubject(token.getUsername());
        claims.put(ROLES_CLAIM_NAME, token.getRoles().stream().map(Role::toString).collect(Collectors.toList()));
        final Instant now = Instant.now();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(duration)))
                .signWith(signatureAlgorithm, privateKey)
                .compact();
    }
}
