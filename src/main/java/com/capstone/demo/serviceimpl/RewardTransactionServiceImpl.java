package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.Reward;
import com.capstone.demo.model.RewardTransaction;
import com.capstone.demo.model.Users;
import com.capstone.demo.ropositary.RewardRepository;
import com.capstone.demo.ropositary.RewardTransactionRepository;
import com.capstone.demo.ropositary.UserRepository;
import com.capstone.demo.service.RewardTransactionService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RewardTransactionServiceImpl implements RewardTransactionService {

    private final RewardTransactionRepository rewardTransactionRepository;
    private final RewardRepository rewardRepository;
    private final UserRepository usersRepository;

    public RewardTransactionServiceImpl(
            RewardTransactionRepository rewardTransactionRepository,
            RewardRepository rewardRepository,
            UserRepository usersRepository
    ) {
        this.rewardTransactionRepository = rewardTransactionRepository;
        this.rewardRepository = rewardRepository;
        this.usersRepository = usersRepository;
    }

    // =========================
    // USER REDEEM METHOD
    // =========================

    @Override
    @Transactional
    public RewardTransaction redeemReward(String userId, Long rewardId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (!"ACTIVE".equalsIgnoreCase(reward.getStatus())) {
            throw new RuntimeException("Reward is not active");
        }

        if (reward.getExpiryDate() != null &&
                reward.getExpiryDate().isBefore(LocalDate.now())) {

            reward.setStatus("EXPIRED");
            rewardRepository.save(reward);

            throw new RuntimeException("Reward has expired");
        }

        if (reward.getQuantity() <= 0) {
            throw new RuntimeException("Reward is out of stock");
        }

        if (user.getRewardPoints() < reward.getPointsRequired()) {
            throw new RuntimeException("Not enough reward points");
        }

        boolean alreadyRedeemed =
                rewardTransactionRepository.existsByUserIdAndRewardId(userId, rewardId);

        if (alreadyRedeemed) {
            throw new RuntimeException("You already redeemed this reward");
        }

        user.setRewardPoints(user.getRewardPoints() - reward.getPointsRequired());
        reward.setQuantity(reward.getQuantity() - 1);

        RewardTransaction transaction = new RewardTransaction();
        transaction.setUser(user);
        transaction.setReward(reward);
        transaction.setPointsSpent(reward.getPointsRequired());
        transaction.setStatus("REDEEMED");
        transaction.setRedeemedAt(LocalDateTime.now());
        transaction.setRedemptionCode(generateRedemptionCode());

        usersRepository.save(user);
        rewardRepository.save(reward);

        return rewardTransactionRepository.save(transaction);
    }

    // =========================
    // USER METHODS
    // =========================

    @Override
    public List<RewardTransaction> getMyTransactions(String userId) {
        return rewardTransactionRepository.findByUserId(userId);
    }

    @Override
    public List<RewardTransaction> getMyTransactionsByCompanyName(String userId, String companyName) {
        return rewardTransactionRepository
                .findByUserIdAndRewardSponsorCompanyNameContainingIgnoreCase(userId, companyName);
    }

    // =========================
    // SPONSOR METHODS
    // =========================

    @Override
    public List<RewardTransaction> getSponsorRewardTransactions(String sponsorId) {
        return rewardTransactionRepository.findByRewardSponsorId(sponsorId);
    }

    // =========================
    // ADMIN METHODS
    // =========================

    @Override
    public RewardTransaction saveTransaction(RewardTransaction transaction) {
        return rewardTransactionRepository.save(transaction);
    }

    @Override
    public List<RewardTransaction> getAllTransactions() {
        return rewardTransactionRepository.findAll();
    }

    @Override
    public RewardTransaction getTransactionById(Long id) {
        return rewardTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward transaction not found"));
    }

    @Override
    public void deleteTransaction(Long id) {
        RewardTransaction transaction = rewardTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward transaction not found"));

        rewardTransactionRepository.delete(transaction);
    }

    // =========================
    // SHARED METHODS
    // =========================

    @Override
    public List<RewardTransaction> getRewardTransactions(Long rewardId) {
        return rewardTransactionRepository.findByRewardId(rewardId);
    }

    @Override
    public RewardTransaction getTransactionByCode(String redemptionCode) {
        return rewardTransactionRepository.findByRedemptionCode(redemptionCode)
                .orElseThrow(() -> new RuntimeException("Reward transaction not found"));
    }

    private String generateRedemptionCode() {
        return "AYNAK-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}