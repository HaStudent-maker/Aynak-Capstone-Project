package com.capstone.demo.security;

import com.capstone.demo.model.Users;
import com.capstone.demo.model.MunicipalOfficer;
import com.capstone.demo.model.Sponsor;
import com.capstone.demo.ropositary.UserRepository;
import com.capstone.demo.ropositary.MunicipalOfficerRepository;
import com.capstone.demo.ropositary.SponsorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class KeycloakSyncService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MunicipalOfficerRepository officerRepo;

    @Autowired
    private SponsorRepository sponsorRepo;

    private static final String CLIENT_ID = "Aynak-server";

    public void syncFromKeycloak(Jwt jwt) {

        String id = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null || !resourceAccess.containsKey(CLIENT_ID)) {
            System.out.println("User has no client roles, skipping sync...");
            return;
        }

        Map<String, Object> clientData = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
        Collection<String> roles = (Collection<String>) clientData.get("roles");

        if (roles == null) return;

        // ---- USER ROLE ----
        if (roles.contains("User")) {
            userRepo.findById(id).orElseGet(() -> {
                Users user = new Users();
                user.setId(id);
                user.setEmail(email);
                user.setUsername(username);
                user.setPhoneNumber("N/A");
                user.setRewardPoints(0);
                user.setAddress(null);
                return userRepo.save(user);
            });
        }

        // ---- MUNICIPAL OFFICER ROLE ----
        if (roles.contains("MunicipalOfficer")) {
            officerRepo.findById(id).orElseGet(() -> {
                MunicipalOfficer officer = new MunicipalOfficer();
                officer.setId(id);
                officer.setEmail(email);
                officer.setFullName(username);
                officer.setDepartmentName("Unknown");
                officer.setPhoneNumber("N/A");
                return officerRepo.save(officer);
            });
        }

        // ---- SPONSOR ROLE ----
        if (roles.contains("Sponsor")) {
            sponsorRepo.findById(id).orElseGet(() -> {
                Sponsor sponsor = new Sponsor();
                sponsor.setSponsorName(username);
                sponsor.setId(id);
                sponsor.setEmail(email);
                sponsor.setCompanyType("Unknown");
                sponsor.setPhone("N/A");
                sponsor.setAddress(null);
                return sponsorRepo.save(sponsor);
            });
        }

        if (roles.contains("Admin")) {
            System.out.println("Admin detected — no DB entity needed.");
        }
    }
}
