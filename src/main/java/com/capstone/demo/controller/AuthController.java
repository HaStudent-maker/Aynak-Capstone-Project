package com.capstone.demo.controller;

import com.capstone.demo.security.KeycloakSyncService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final KeycloakSyncService syncService;

    public AuthController(KeycloakSyncService syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/sync")
    public Map<String, Object> sync(Authentication authentication) {

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return Map.of(
                    "status", "FAILED",
                    "message", "No JWT found. Send Bearer access_token from Postman."
            );
        }

        Jwt jwt = jwtAuth.getToken();

        syncService.syncFromKeycloak(jwt);

        return Map.of(
                "status", "SUCCESS",
                "message", "User synced from JWT",
                "keycloakId", jwt.getSubject(),
                "username", jwt.getClaimAsString("preferred_username"),
                "email", jwt.getClaimAsString("email"),
                "roles", extractRoles(jwt)
        );
    }
    @RestController
    @RequestMapping("/api/debug")
    public class DebugController {

        @GetMapping("/whoami")
        public Map<String,Object> whoAmI(@AuthenticationPrincipal OidcUser oidcUser) {
            Map<String,Object> map = new HashMap<>();
            map.put("username", oidcUser.getPreferredUsername());
            map.put("sub", oidcUser.getSubject());
            map.put("authorities", oidcUser.getAuthorities()); // Spring session authorities
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractRoles(Jwt jwt) {

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return Set.of("User");
        }

        Map<String, Object> clientAccess =
                (Map<String, Object>) resourceAccess.get("Aynak-server");

        if (clientAccess == null) {
            return Set.of("User");
        }

        Collection<String> roles =
                (Collection<String>) clientAccess.get("roles");

        if (roles == null || roles.isEmpty()) {
            return Set.of("User");
        }

        return new HashSet<>(roles);
    }
}