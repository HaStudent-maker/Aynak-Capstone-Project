package com.capstone.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final KeycloakSyncService syncService;

    private final JwtDecoder jwtDecoder;

    public OAuth2LoginSuccessHandler(KeycloakSyncService syncService, JwtDecoder jwtDecoder) {
        this.syncService = syncService;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if(authentication instanceof OAuth2AuthenticationToken oauthToken){
            // Get ID token
        	OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
        	String tokenValue = oidcUser.getIdToken().getTokenValue();

        	// Decode it to Spring Jwt if needed
        	Jwt jwt = jwtDecoder.decode(tokenValue);

        	syncService.syncFromKeycloak(jwt);
        }

        // Optional: redirect based on role
        String targetUrl = "/Home.html";
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_Sponsor"))) {
            targetUrl = "/sponsor-dashboard.html";
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}