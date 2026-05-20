package com.capstone.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reward_transactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "reward_id"})
        }
)
public class RewardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "points_spent", nullable = false)
    private int pointsSpent;

    // REDEEMED, CANCELLED, EXPIRED
    @Column(nullable = false)
    private String status = "REDEEMED";

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt = LocalDateTime.now();

    @Column(name = "redemption_code", unique = true)
    private String redemptionCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonIgnoreProperties({
            "sponsor",
            "rewardTransactions",
            "hibernateLazyInitializer",
            "handler"
    })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    public Long getId() {
        return id;
    }

    public int getPointsSpent() {
        return pointsSpent;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public Users getUser() {
        return user;
    }

    public Reward getReward() {
        return reward;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPointsSpent(int pointsSpent) {
        this.pointsSpent = pointsSpent;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }
}