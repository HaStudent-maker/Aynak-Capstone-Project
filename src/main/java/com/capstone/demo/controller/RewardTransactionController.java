package com.capstone.demo.controller;

import com.capstone.demo.model.RewardTransaction;
import com.capstone.demo.service.RewardTransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reward-transactions")
public class RewardTransactionController {

    private final RewardTransactionService rewardTransactionService;

    public RewardTransactionController(RewardTransactionService rewardTransactionService) {
        this.rewardTransactionService = rewardTransactionService;
    }

    // =====================================================
    // USER ENDPOINTS
    // =====================================================

    // User redeems a reward
    @PostMapping("/user/redeem/{rewardId}")
    @PreAuthorize("hasRole('User')")
    public RewardTransaction redeemReward(
            @PathVariable Long rewardId,
            Authentication authentication
    ) {
        String userId = getKeycloakId(authentication);
        return rewardTransactionService.redeemReward(userId, rewardId);
    }

    // User gets only their own reward transactions
    @GetMapping("/user/my")
    @PreAuthorize("hasRole('User')")
    public List<RewardTransaction> getMyTransactions(Authentication authentication) {
        String userId = getKeycloakId(authentication);
        return rewardTransactionService.getMyTransactions(userId);
    }

    // User filters their own redeemed rewards by sponsor/company name
    @GetMapping("/user/my/company")
    @PreAuthorize("hasRole('User')")
    public List<RewardTransaction> getMyTransactionsByCompanyName(
            @RequestParam String companyName,
            Authentication authentication
    ) {
        String userId = getKeycloakId(authentication);
        return rewardTransactionService.getMyTransactionsByCompanyName(userId, companyName);
    }

    // User can search one of their own transactions by redemption code
    @GetMapping("/user/my/code/{redemptionCode}")
    @PreAuthorize("hasRole('User')")
    public RewardTransaction getMyTransactionByCode(
            @PathVariable String redemptionCode,
            Authentication authentication
    ) {
        String userId = getKeycloakId(authentication);

        RewardTransaction transaction =
                rewardTransactionService.getTransactionByCode(redemptionCode);

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only view your own reward transaction");
        }

        return transaction;
    }

    // =====================================================
    // SPONSOR ENDPOINTS
    // =====================================================

    // Sponsor gets transactions for rewards created by them
    @GetMapping("/sponsor/mine")
    @PreAuthorize("hasRole('Sponsor')")
    public List<RewardTransaction> getSponsorRewardTransactions(
            Authentication authentication
    ) {
        String sponsorId = getKeycloakId(authentication);
        return rewardTransactionService.getSponsorRewardTransactions(sponsorId);
    }

    // =====================================================
    // ADMIN ENDPOINTS
    // =====================================================

    // Admin creates/saves a transaction manually
    @PostMapping("/admin")
    @PreAuthorize("hasRole('Admin')")
    public RewardTransaction saveTransaction(@RequestBody RewardTransaction transaction) {
        return rewardTransactionService.saveTransaction(transaction);
    }

    // Admin sees all reward transactions
    @GetMapping("/admin")
    @PreAuthorize("hasRole('Admin')")
    public List<RewardTransaction> getAllTransactions() {
        return rewardTransactionService.getAllTransactions();
    }

    // Admin gets any transaction by ID
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('Admin')")
    public RewardTransaction getTransactionById(@PathVariable Long id) {
        return rewardTransactionService.getTransactionById(id);
    }

    // Admin deletes any transaction
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('Admin')")
    public String deleteTransaction(@PathVariable Long id) {
        rewardTransactionService.deleteTransaction(id);
        return "Reward transaction deleted successfully";
    }

    // Admin gets transactions by reward ID
    @GetMapping("/admin/reward/{rewardId}")
    @PreAuthorize("hasRole('Admin')")
    public List<RewardTransaction> getTransactionsByReward(@PathVariable Long rewardId) {
        return rewardTransactionService.getRewardTransactions(rewardId);
    }

    // Admin gets transaction by redemption code
    @GetMapping("/admin/code/{redemptionCode}")
    @PreAuthorize("hasRole('Admin')")
    public RewardTransaction getTransactionByCode(@PathVariable String redemptionCode) {
        return rewardTransactionService.getTransactionByCode(redemptionCode);
    }

    // =====================================================
    // HELPER METHOD
    // Works for both:
    // 1. Postman JWT Bearer token
    // 2. Browser Keycloak OIDC login
    // =====================================================

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