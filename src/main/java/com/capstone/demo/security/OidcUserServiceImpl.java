package com.capstone.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OidcUserServiceImpl extends OidcUserService {

    @Override
    @SuppressWarnings("unchecked")
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUser oidcUser = super.loadUser(userRequest);

        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());

        Map<String, Object> resourceAccess =
                oidcUser.getAttribute("resource_access");

        if (resourceAccess != null) {
            Map<String, Object> client =
                    (Map<String, Object>) resourceAccess.get("Aynak-server");

            if (client != null) {
                Collection<String> roles =
                        (Collection<String>) client.get("roles");

                if (roles != null) {
                    roles.forEach(role ->
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                }
            }
        }

        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "preferred_username"
        );
    }
}