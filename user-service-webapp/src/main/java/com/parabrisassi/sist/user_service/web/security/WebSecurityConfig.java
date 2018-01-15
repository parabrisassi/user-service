package com.parabrisassi.sist.user_service.web.security;

import com.parabrisassi.sist.user_service.web.controller.rest_endpoints.AuthenticationTokenEndpoint;
import com.parabrisassi.sist.user_service.web.controller.rest_endpoints.UserEndpoint;
import com.parabrisassi.sist.user_service.web.security.authentication.TokenAuthenticationFailureHandler;
import com.parabrisassi.sist.user_service.web.security.authentication.TokenAuthenticationFilter;
import com.parabrisassi.sist.user_service.web.security.authentication.TokenAuthenticationProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements InitializingBean {

    /**
     * The root path in which the jersey application is listening.
     */
    private final String jerseyApplicationPath;

    /**
     * The {@link TokenAuthenticationFilter} to perform token authentication.
     */
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * The {@link TokenAuthenticationProvider} that authenticates request by a given token.
     */
    private final TokenAuthenticationProvider tokenAuthenticationProvider;


    @Autowired
    public WebSecurityConfig(@Value("${spring.jersey.application-path:}") String jerseyApplicationPath,
                             TokenAuthenticationProvider tokenAuthenticationProvider,
                             TokenAuthenticationFailureHandler tokenAuthenticationFailureHandler)
            throws Exception {
        this.jerseyApplicationPath = jerseyApplicationPath;

        this.tokenAuthenticationFilter =
                new TokenAuthenticationFilter(passwordAuthenticationMatcher(),
                        tokenValidationMatcher(),
                        optionalAuthenticationMatchers(),
                        tokenAuthenticationFailureHandler);
        this.tokenAuthenticationProvider = tokenAuthenticationProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.tokenAuthenticationFilter.setAuthenticationManager(this.authenticationManager());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .rememberMe().disable()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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
     * @return A {@link List} of {@link AntPathRequestMatcher}
     * for those endpoints where authentication is not mandatory.
     */
    private List<RequestMatcher> optionalAuthenticationMatchers() {
        return Stream.of(
                new AntPathRequestMatcher(jerseyApplicationPath + UserEndpoint.USERS_ENDPOINT, "POST")
        ).collect(Collectors.toList());
    }
}
