package com.capstone.demo.controller;

import com.capstone.demo.model.Reward;
import com.capstone.demo.service.RewardService;
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
@RequestMapping("/api/rewards")
@CrossOrigin("*")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    // USER → ACTIVE ONLY
    @GetMapping("/user")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<List<Reward>> getActiveRewardsForUsers() {
        return ResponseEntity.ok(rewardService.getActiveRewards());
    }

    // SPONSOR → ALL REWARDS CREATED BY THEM
    @GetMapping("/sponsor")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<List<Reward>> getAllRewardsForSponsor(Authentication authentication) {

        String sponsorId = getKeycloakId(authentication);

        return ResponseEntity.ok(rewardService.getRewardsBySponsor(sponsorId));
    }

    // ADMIN → ALL REWARDS
    @GetMapping("/admin")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Reward>> getAllRewardsForAdmin() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    // SPONSOR → CREATE REWARD
    @PostMapping
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Reward> createReward(
            @RequestBody Reward reward,
            Authentication authentication
    ) {
        String sponsorId = getKeycloakId(authentication);

        Reward created = rewardService.createReward(reward, sponsorId);

        return ResponseEntity.ok(created);
    }

    // SPONSOR → UPDATE OWN REWARD
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Reward> updateReward(
            @PathVariable Long id,
            @RequestBody Reward updatedReward,
            Authentication authentication
    ) {
        String sponsorId = getKeycloakId(authentication);

        Reward updated = rewardService.updateReward(id, updatedReward, sponsorId);

        return ResponseEntity.ok(updated);
    }

    // SPONSOR → GET OWN REWARD BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Reward> getRewardById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String sponsorId = getKeycloakId(authentication);

        Reward reward = rewardService.getRewardByIdForSponsor(id, sponsorId);

        return ResponseEntity.ok(reward);
    }

    // SPONSOR → DELETE OWN REWARD
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<String> deleteReward(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String sponsorId = getKeycloakId(authentication);

        rewardService.deleteReward(id, sponsorId);

        return ResponseEntity.ok("Reward deleted successfully");
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

        throw new RuntimeException("Unable to extract Keycloak user ID from authentication");
    }
}