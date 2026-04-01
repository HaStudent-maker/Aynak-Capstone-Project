package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.Reward;
import com.capstone.demo.model.Users;
import com.capstone.demo.ropositary.RewardRepository;
import com.capstone.demo.ropositary.UserRepository;
import com.capstone.demo.service.RewardService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Reward createReward(Reward reward) {
        reward.updateStatus(); // ensure status is correct before saving
        return rewardRepository.save(reward);
    }

    @Override
    public Reward updateReward(Long id, Reward updatedReward) {

        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        reward.setTitle(updatedReward.getTitle());
        reward.setDescription(updatedReward.getDescription());
        reward.setPointsRequired(updatedReward.getPointsRequired());
        reward.setExpiryDate(updatedReward.getExpiryDate());
        reward.setQuantity(updatedReward.getQuantity());

        reward.updateStatus(); // refresh status

        return rewardRepository.save(reward);
    }

    @Override
    public Reward getRewardById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
    }

    @Override
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Override
    public List<Reward> getActiveRewards() {
        return rewardRepository.findByStatus("ACTIVE");
    }

    @Override
    @Transactional
    public Reward redeemReward(Long rewardId, String userId) {

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Refresh reward status before redeem
        reward.updateStatus();

        if (!reward.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Reward is not active or expired");
        }

        if (user.getRewardPoints() < reward.getPointsRequired()) {
            throw new RuntimeException("Insufficient points");
        }

        // Deduct user points
        user.setRewardPoints(user.getRewardPoints() - reward.getPointsRequired());

        // Reduce reward quantity using entity logic
        boolean success = reward.redeem();
        if (!success) {
            throw new RuntimeException("Reward is out of stock");
        }

        // Link redeemed user
        reward.setRedeemedBy(user);

        userRepository.save(user);
        return rewardRepository.save(reward);
    }

    @Override
    public void deleteReward(Long id) {
        rewardRepository.deleteById(id);
    }

	@Override
	public List<Reward> getRewardsBySponsor(String sponsorId) {
		// TODO Auto-generated method stub
		return null;
	}
}