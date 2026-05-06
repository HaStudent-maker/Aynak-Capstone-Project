package com.capstone.demo.security;

import com.capstone.demo.model.MunicipalOfficer;
import com.capstone.demo.model.Sponsor;
import com.capstone.demo.model.Users;
import com.capstone.demo.ropositary.MunicipalOfficerRepository;
import com.capstone.demo.ropositary.SponsorRepository;
import com.capstone.demo.ropositary.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeycloakSyncService {

    private final UserRepository usersRepository;
    private final SponsorRepository sponsorRepository;
    private final MunicipalOfficerRepository officerRepository;

    public KeycloakSyncService(
            UserRepository usersRepository,
            SponsorRepository sponsorRepository,
            MunicipalOfficerRepository officerRepository
    ) {
        this.usersRepository = usersRepository;
        this.sponsorRepository = sponsorRepository;
        this.officerRepository = officerRepository;
    }

    @Transactional
    public void syncFromKeycloak(Jwt jwt) {

        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        Set<String> roles = extractRolesFromJwt(jwt);

        System.out.println("SYNCING FROM JWT");
        System.out.println("KEYCLOAK ID: " + keycloakId);
        System.out.println("USERNAME: " + username);
        System.out.println("EMAIL: " + email);
        System.out.println("ROLES: " + roles);

        Users user = usersRepository.findById(keycloakId)
                .orElse(new Users());

        user.setId(keycloakId);
        user.setUsername(username);
        user.setEmail(email);

        usersRepository.save(user);

        if (roles.contains("Sponsor")) {
            Sponsor sponsor = sponsorRepository.findById(keycloakId)
                    .orElse(new Sponsor());

            sponsor.setId(keycloakId);
            sponsor.setSponsorName(username);
            sponsor.setEmail(email);

            sponsorRepository.save(sponsor);
        }

        if (roles.contains("MunicipalOfficer")) {
            MunicipalOfficer officer = officerRepository.findById(keycloakId)
                    .orElse(new MunicipalOfficer());

            officer.setId(keycloakId);
            officer.setFullName(email);
            officer.setEmail(email);

            officerRepository.save(officer);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractRolesFromJwt(Jwt jwt) {

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            System.out.println("NO resource_access FOUND");
            return Set.of("User");
        }

        Map<String, Object> clientAccess =
                (Map<String, Object>) resourceAccess.get("Aynak-server");

        if (clientAccess == null) {
            System.out.println("NO Aynak-server FOUND");
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