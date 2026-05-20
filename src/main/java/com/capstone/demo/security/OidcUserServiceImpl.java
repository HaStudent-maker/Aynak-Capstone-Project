package com.capstone.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom OidcUserService that maps ONLY client roles from
 * resource_access["Aynak-server"].roles to Spring authorities (ROLE_*).
 * Ignores realm roles.
 */
@Service
public class OidcUserServiceImpl extends OidcUserService {

    private final String clientId = "Aynak-server"; // Keycloak client ID

    @Override
    @SuppressWarnings("unchecked")
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        Set<GrantedAuthority> authorities = new HashSet<>();

        // --- Extract ONLY client roles ---
        Map<String, Object> resourceAccess = oidcUser.getAttribute("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get(clientId);
            if (clientResource != null && clientResource.containsKey("roles")) {
                List<String> roles = (List<String>) clientResource.get("roles");

                // Map Keycloak client roles to Spring authorities
                authorities.addAll(
                        roles.stream()
                             .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                             .collect(Collectors.toSet())
                );
            }
        }

        // --- Keep OIDC default authorities like SCOPE_email ---
        authorities.addAll(oidcUser.getAuthorities());

        // --- Return a new OidcUser with mapped authorities ---
        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "preferred_username" // principal name
        );
    }
}