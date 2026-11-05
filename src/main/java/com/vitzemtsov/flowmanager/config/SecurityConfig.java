package com.vitzemtsov.flowmanager.config;

import com.vitzemtsov.flowmanager.constants.SecurityPaths;
import com.vitzemtsov.flowmanager.constants.SecurityRoles;
import com.vitzemtsov.flowmanager.security.KeycloakRealmRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                SecurityPaths.ACTUATOR_HEALTH,
                                SecurityPaths.ACTUATOR_INFO,
                                SecurityPaths.ACTUATOR_PROMETHEUS).permitAll()
                        .requestMatchers(SecurityPaths.API_ADMIN_ALL).hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(SecurityPaths.API_ALL).hasAnyRole(SecurityRoles.USER, SecurityRoles.ADMIN)
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}