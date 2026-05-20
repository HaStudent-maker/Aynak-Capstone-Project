package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.Reward;
import com.capstone.demo.model.Sponsor;
import com.capstone.demo.ropositary.RewardRepository;
import com.capstone.demo.ropositary.SponsorRepository;
import com.capstone.demo.service.RewardService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final SponsorRepository sponsorRepository;

    public RewardServiceImpl(
            RewardRepository rewardRepository,
            SponsorRepository sponsorRepository
    ) {
        this.rewardRepository = rewardRepository;
        this.sponsorRepository = sponsorRepository;
    }

    // =========================
    // SPONSOR: CREATE REWARD
    // =========================

    @Override
    @Transactional
    public Reward createReward(Reward reward, String sponsorId) {

    	Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));

        // Validate sponsor attributes
        if (sponsor.getSponsorName() == null || sponsor.getEmail() == null ||
            sponsor.getPhone() == null || sponsor.getCompanyName() == null) {
            throw new RuntimeException("Sponsor attributes incomplete. Cannot create reward.");
        }

        // Set sponsor and default values
        reward.setSponsor(sponsor);
        reward.setStatus("ACTIVE");
        if (reward.getExpiryDate() == null) {
            reward.setExpiryDate(LocalDate.now().plusMonths(1));
        }

        return rewardRepository.save(reward);
    }

    // =========================
    // SPONSOR: UPDATE OWN REWARD
    // =========================

    @Override
    @Transactional
    public Reward updateReward(Long id, Reward updatedReward, String sponsorId) {

        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (reward.getSponsor() == null || !reward.getSponsor().getId().equals(sponsorId)) {
            throw new RuntimeException("You can only update your own rewards");
        }

        reward.setTitle(updatedReward.getTitle());
        reward.setDescription(updatedReward.getDescription());
        reward.setPointsRequired(updatedReward.getPointsRequired());
        reward.setExpiryDate(updatedReward.getExpiryDate());
        reward.setQuantity(updatedReward.getQuantity());

        if (updatedReward.getStatus() != null && !updatedReward.getStatus().isBlank()) {
            reward.setStatus(updatedReward.getStatus());
        }

        reward.updateStatus();

        return rewardRepository.save(reward);
    }

    // =========================
    // SPONSOR: GET OWN REWARD BY ID
    // =========================

    @Override
    public Reward getRewardByIdForSponsor(Long id, String sponsorId) {

        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (reward.getSponsor() == null || !reward.getSponsor().getId().equals(sponsorId)) {
            throw new RuntimeException("You can only view your own rewards");
        }

        return reward;
    }

    // =========================
    // ADMIN / SHARED
    // =========================

    @Override
    public Reward getRewardById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
    }

    @Override
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    // =========================
    // USER: ACTIVE REWARDS ONLY
    // =========================

    @Override
    public List<Reward> getActiveRewards() {
        return rewardRepository.findByStatus("ACTIVE");
    }

    // =========================
    // SPONSOR: GET OWN REWARDS
    // =========================

    @Override
    public List<Reward> getRewardsBySponsor(String sponsorId) {
        return rewardRepository.findBySponsorId(sponsorId);
    }

    // =========================
    // SPONSOR: DELETE OWN REWARD
    // =========================

    @Override
    @Transactional
    public void deleteReward(Long id, String sponsorId) {

        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (reward.getSponsor() == null || !reward.getSponsor().getId().equals(sponsorId)) {
            throw new RuntimeException("You can only delete your own rewards");
        }

        rewardRepository.delete(reward);
    }
}