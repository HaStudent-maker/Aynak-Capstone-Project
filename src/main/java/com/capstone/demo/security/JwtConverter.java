package com.capstone.demo.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final JwtConverterProperties properties;
    private final KeycloakSyncService keycloakSyncService;

    public JwtConverter(JwtConverterProperties properties, KeycloakSyncService keycloakSyncService) {
        this.properties = properties;
        this.keycloakSyncService = keycloakSyncService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Sync user data with Keycloak database
        keycloakSyncService.syncFromKeycloak(jwt);

        // Extract roles from JWT and merge with default authorities
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        // Create Authentication token
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        if (properties.getPrincipalAttribute() != null) {
            return jwt.getClaim(properties.getPrincipalAttribute());
        }
        return jwt.getClaim(JwtClaimNames.SUB);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return Set.of();
        }

        Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get(properties.getResourceId());
        if (clientResource == null) return Set.of();

        Collection<String> roles = (Collection<String>) clientResource.get("roles");
        if (roles == null) return Set.of();

        // Convert to Spring authorities
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}