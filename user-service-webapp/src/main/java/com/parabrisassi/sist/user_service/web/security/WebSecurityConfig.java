package com.parabrisassi.sist.user_service.web.security;

import com.parabrisassi.sist.commons.authentication.AbstractWebSecurityConfig;
import com.parabrisassi.sist.commons.authentication.EnableTokenAuthentication;
import com.parabrisassi.sist.commons.authentication.TokenAuthenticationFilter;
import com.parabrisassi.sist.commons.authentication.TokenAuthenticationProvider;
import com.parabrisassi.sist.user_service.web.controller.rest_endpoints.AuthenticationTokenEndpoint;
import com.parabrisassi.sist.user_service.web.controller.rest_endpoints.UserEndpoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Security web configuration.
 */
@Configuration
@EnableWebSecurity
@EnableTokenAuthentication
public class WebSecurityConfig extends AbstractWebSecurityConfig implements InitializingBean {

    /**
     * The root path in which the jersey application is listening.
     */
    private final String jerseyApplicationPath;


    @Autowired
    public WebSecurityConfig(@Value("${spring.jersey.application-path:}") String jerseyApplicationPath,
                             TokenAuthenticationFilter tokenAuthenticationFilter,
                             TokenAuthenticationProvider tokenAuthenticationProvider)
            throws Exception {
        super(tokenAuthenticationFilter, tokenAuthenticationProvider);
        this.jerseyApplicationPath = jerseyApplicationPath;
    }

    /**
     * @return An {@link AntPathRequestMatcher} for the password authentication endpoint.
     */
    private RequestMatcher passwordAuthenticationMatcher() {
        return new RegexRequestMatcher(jerseyApplicationPath +
                AuthenticationTokenEndpoint.TOKENS_ENDPOINT, "POST");
    }

    /**
     * @return An {@link AntPathRequestMatcher} for the token validation endpoint.
     */
    private RequestMatcher tokenValidationMatcher() {
        return new RegexRequestMatcher(jerseyApplicationPath +
                AuthenticationTokenEndpoint.TOKENS_ENDPOINT + "/.+", "GET");
    }

    /**
     * @return An {@link AntPathRequestMatcher} for the user creation endpoint.
     */
    private RequestMatcher userCreationMatcher() {
        return new AntPathRequestMatcher(jerseyApplicationPath + UserEndpoint.USERS_ENDPOINT, "POST");
    }

    @Override
    protected List<RequestMatcher> optionalAuthenticationMatchers() {
        return Stream.of(passwordAuthenticationMatcher(), tokenValidationMatcher(), userCreationMatcher())
                .collect(Collectors.toList());
    }
}
