package com.capstone.demo.service;

import com.capstone.demo.model.Reward;

import java.util.List;

public interface RewardService {

    // Sponsor creates reward
    Reward createReward(Reward reward, String sponsorId);

    // Sponsor updates only their own reward
    Reward updateReward(Long id, Reward reward, String sponsorId);

    // Sponsor gets only their own reward by ID
    Reward getRewardByIdForSponsor(Long id, String sponsorId);

    // Admin or shared use
    Reward getRewardById(Long id);

    // Admin sees all rewards
    List<Reward> getAllRewards();

    // User sees active rewards only
    List<Reward> getActiveRewards();

    // Sponsor sees their own rewards
    List<Reward> getRewardsBySponsor(String sponsorId);

    // Sponsor deletes only their own reward
    void deleteReward(Long id, String sponsorId);
}