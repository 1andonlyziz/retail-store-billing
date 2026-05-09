package com.retailstore.billing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${configuration.security.api-key}")
    private String apiKey;

    /**
     * Configures the security filter chain.
     * <p>
     * CSRF protection is intentionally disabled because this application is a stateless REST API
     * authenticated via an API key (X-API-KEY header). CSRF attacks rely on browsers automatically
     * sending cookies/session credentials with cross-site requests. Since this API uses neither
     * sessions nor cookies for authentication, CSRF is not applicable.
     * </p>
     */
    @Bean
    @SuppressWarnings("java:S4502") // Safe: stateless API with API key auth, no session/cookies
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(new ApiKeyAuthFilter(apiKey), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
