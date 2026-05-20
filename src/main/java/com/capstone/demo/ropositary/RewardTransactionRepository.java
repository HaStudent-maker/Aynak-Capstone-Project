package com.capstone.demo.ropositary;



import com.capstone.demo.model.RewardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, Long> {

    List<RewardTransaction> findByRewardId(Long rewardId);

    boolean existsByUserIdAndRewardId(String userId, Long rewardId);

        List<RewardTransaction> findByUserId(String userId);

        Optional<RewardTransaction> findByRedemptionCode(String redemptionCode);
        // User: get own transactions filtered by company/sponsor name
        List<RewardTransaction> findByUserIdAndRewardSponsorCompanyNameContainingIgnoreCase(
                String userId,
                String companyName
        );

        // Sponsor: get all transactions for rewards owned by this sponsor
        List<RewardTransaction> findByRewardSponsorId(String sponsorId);
    }
    



