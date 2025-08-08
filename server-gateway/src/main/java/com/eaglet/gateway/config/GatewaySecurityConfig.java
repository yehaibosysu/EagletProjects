package com.eaglet.gateway.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class GatewaySecurityConfig {

    @Value("${app.base-uri}")
    private String appBaseUri;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("X-XSRF-TOKEN");
        config.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
        config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedOrigins(Collections.singletonList(this.appBaseUri));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        /*
         * IMPORTANT:
         * Set the csrfRequestAttributeName to null, to opt-out of deferred tokens,
         * resulting in the CsrfToken to be loaded on every request.
         * If it does not exist, the CookieCsrfTokenRepository will automatically
         * generate a new one and add the Cookie to the response.
         * See the reference:
         * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#
         * deferred-csrf-token
         */
        csrfTokenRequestAttributeHandler.setCsrfRequestAttributeName(null);

        // @formatter:off
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(cookieCsrfTokenRepository)
                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
            )
            .cors(Customizer.withDefaults())
            .logout(logout -> logout
                .addLogoutHandler(logoutHandler(cookieCsrfTokenRepository))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            )
			.oauth2Login(oauth2Login -> oauth2Login
				.successHandler(new SimpleUrlAuthenticationSuccessHandler(this.appBaseUri))
            )
			.oauth2Client(Customizer.withDefaults())
			.exceptionHandling(exceptionHandling -> exceptionHandling
					.authenticationEntryPoint(authenticationEntryPoint())
			)
        ;
        // @formatter:on

        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        AuthenticationEntryPoint authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(
                "/oauth2/authorization/messaging-client-oidc");
        MediaTypeRequestMatcher textHtmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        textHtmlMatcher.setUseEquals(true);

        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(textHtmlMatcher, authenticationEntryPoint);

        DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint = new DelegatingAuthenticationEntryPoint(
                entryPoints);
        delegatingAuthenticationEntryPoint.setDefaultEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return delegatingAuthenticationEntryPoint;
    }

    private LogoutHandler logoutHandler(CsrfTokenRepository csrfTokenRepository) {
        return new CompositeLogoutHandler(
                new SecurityContextLogoutHandler(),
                new CsrfLogoutHandler(csrfTokenRepository));
    }

}
