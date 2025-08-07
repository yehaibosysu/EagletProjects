package com.eaglet.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class BusinessSecurityConfig {

    // @formatter:off
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/messages/**")
				.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/messages/**").hasAuthority("SCOPE_message.read")
				)
				.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                    .jwt(Customizer.withDefaults())
				)
        ;
		return http.build();
	}
	// @formatter:on

}
