package com.capstone.demo.service;

import com.capstone.demo.model.RewardTransaction;

import java.util.List;

public interface RewardTransactionService {

    // User redeem operation
    RewardTransaction redeemReward(String userId, Long rewardId);

    // User methods
    List<RewardTransaction> getMyTransactions(String userId);

    List<RewardTransaction> getMyTransactionsByCompanyName(String userId, String companyName);

    // Sponsor methods
    List<RewardTransaction> getSponsorRewardTransactions(String sponsorId);

    // Admin methods
    RewardTransaction saveTransaction(RewardTransaction transaction);

    List<RewardTransaction> getAllTransactions();

    RewardTransaction getTransactionById(Long id);

    void deleteTransaction(Long id);

    // Shared/helper methods
    List<RewardTransaction> getRewardTransactions(Long rewardId);

    RewardTransaction getTransactionByCode(String redemptionCode);
}