package com.capstone.demo.controller;

import com.capstone.demo.model.Sponsor;
import com.capstone.demo.service.SponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sponsors")
@CrossOrigin("*")
public class SponsorController {

    @Autowired
    private SponsorService sponsorService;

    // SPONSOR: view own sponsor profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Sponsor> getMySponsorProfile(Authentication authentication) {
        String sponsorId = getKeycloakId(authentication);
        return ResponseEntity.ok(sponsorService.getSponsorById(sponsorId));
    }

    // SPONSOR: update own sponsor profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Sponsor> updateMySponsorProfile(
            @RequestBody Sponsor updatedSponsor,
            Authentication authentication
    ) {
        String sponsorId = getKeycloakId(authentication);
        Sponsor updated = sponsorService.updateSponsor(sponsorId, updatedSponsor);
        return ResponseEntity.ok(updated);
    }

    // ADMIN: create sponsor manually if needed
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Sponsor> createSponsor(@RequestBody Sponsor sponsor) {
        Sponsor created = sponsorService.createSponsor(sponsor);
        return ResponseEntity.ok(created);
    }

    // ADMIN: update any sponsor
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Sponsor> updateSponsor(
            @PathVariable String id,
            @RequestBody Sponsor updatedSponsor
    ) {
        Sponsor updated = sponsorService.updateSponsor(id, updatedSponsor);
        return ResponseEntity.ok(updated);
    }

    // ADMIN: get sponsor by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Sponsor> getSponsorById(@PathVariable String id) {
        Sponsor sponsor = sponsorService.getSponsorById(id);
        return ResponseEntity.ok(sponsor);
    }

    // ADMIN: get all sponsors
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Sponsor>> getAllSponsors() {
        return ResponseEntity.ok(sponsorService.getAllSponsors());
    }

    // ADMIN: delete sponsor
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> deleteSponsor(@PathVariable String id) {
        sponsorService.deleteSponsor(id);
        return ResponseEntity.ok("Sponsor deleted successfully");
    }

    private String getKeycloakId(Authentication authentication) {

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getSubject();
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            Object principal = oauthToken.getPrincipal();

            if (principal instanceof OidcUser oidcUser) {
                return oidcUser.getSubject();
            }
        }

        throw new RuntimeException("Unable to extract Keycloak ID from authentication");
    }
}