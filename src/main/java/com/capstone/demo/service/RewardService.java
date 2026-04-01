package com.capstone.demo.service;

import com.capstone.demo.model.Reward;
import java.util.List;

public interface RewardService {

    Reward createReward(Reward reward);

    Reward updateReward(Long id, Reward reward);

    Reward getRewardById(Long id);

    List<Reward> getAllRewards();
    List<Reward> getActiveRewards();
    List<Reward> getRewardsBySponsor(String sponsorId);
    Reward redeemReward(Long rewardId, String userId);


    void deleteReward(Long id);
}

