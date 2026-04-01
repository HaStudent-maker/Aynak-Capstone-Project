package com.capstone.demo.controller;

import com.capstone.demo.model.Reward;
import com.capstone.demo.service.RewardService;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String sponsorId = token.getToken().getSubject();
        return ResponseEntity.ok(rewardService.getRewardsBySponsor(sponsorId));
    }

    // ADMIN → ALL REWARDS
    @GetMapping("/admin")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Reward>> getAllRewardsForAdmin() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    // CREATE Reward
    @PostMapping
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Reward> createReward(@RequestBody Reward reward) {
        Reward created = rewardService.createReward(reward);
        return ResponseEntity.ok(created);
    }

    // UPDATE Reward
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Reward> updateReward(
            @PathVariable Long id,
            @RequestBody Reward updatedReward) {

        Reward updated = rewardService.updateReward(id, updatedReward);
        return ResponseEntity.ok(updated);
    }

    // GET Reward by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<Reward> getRewardById(@PathVariable Long id) {
        Reward reward = rewardService.getRewardById(id);
        return ResponseEntity.ok(reward);
    }

    // DELETE Reward
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Sponsor')")
    public ResponseEntity<String> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.ok("Reward deleted successfully");
    }

    // REDEEM Reward
    @PostMapping("/redeem/{rewardId}")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<?> redeemReward(
            @PathVariable Long rewardId,
            Authentication authentication) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String userId = token.getToken().getSubject();

        Reward redeemed = rewardService.redeemReward(rewardId, userId);
        return ResponseEntity.ok(redeemed);
    }
}