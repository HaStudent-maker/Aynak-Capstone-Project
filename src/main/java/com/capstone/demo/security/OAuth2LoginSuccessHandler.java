package com.capstone.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtDecoder jwtDecoder;
    private final KeycloakSyncService syncService;

    public OAuth2LoginSuccessHandler(
            OAuth2AuthorizedClientService authorizedClientService,
            JwtDecoder jwtDecoder,
            KeycloakSyncService syncService
    ) {
        this.authorizedClientService = authorizedClientService;
        this.jwtDecoder = jwtDecoder;
        this.syncService = syncService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {

            OAuth2AuthorizedClient client =
                    authorizedClientService.loadAuthorizedClient(
                            oauthToken.getAuthorizedClientRegistrationId(),
                            oauthToken.getName()
                    );

            if (client != null && client.getAccessToken() != null) {
                String tokenValue = client.getAccessToken().getTokenValue();

                Jwt jwt = jwtDecoder.decode(tokenValue);

                syncService.syncFromKeycloak(jwt);
            }
        }

        response.sendRedirect("/Home.html");
    }
}