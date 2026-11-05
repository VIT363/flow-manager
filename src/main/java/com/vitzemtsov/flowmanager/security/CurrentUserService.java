package com.vitzemtsov.flowmanager.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public String getLogin() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new IllegalStateException(
                    "Authenticated user JWT is not available"
            );
        }

        String login = jwtAuthentication.getToken()
                .getClaimAsString("preferred_username");

        if (login == null || login.isBlank()) {
            throw new IllegalStateException(
                    "JWT does not contain preferred_username"
            );
        }

        return login;
    }
}