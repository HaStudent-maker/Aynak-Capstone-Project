package com.capstone.demo.model;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name="Rewards")
public class Reward {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "title", nullable = false)
    private String title;
	
	@Column(name = "description", nullable = false)
    private String description;
	
	@Column(name = "pointsRequired", nullable = false)
	private int pointsRequired;
	
	@Column(name = "expiryDate", nullable = false)
	private LocalDate expiryDate;
	
	@Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE or EXPIRED
	
	@Column(name = "quantity", nullable = false)
	private int quantity;

	@ManyToOne
    @JoinColumn(name = "sponsor_id")
    private Sponsor sponsor;

	@OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RewardTransaction> rewardTransactions = new ArrayList<>();

   
 // --- STATUS LOGIC ---
    public void updateStatus() {
        if ((expiryDate != null && expiryDate.isBefore(LocalDate.now())) || this.quantity <= 0) {
            this.status = "EXPIRED";
        } else {
            this.status = "ACTIVE";
        }
    }

    
	public boolean redeem() {
        if (this.quantity > 0 && this.status.equals("ACTIVE")) {
            this.quantity--;
            updateStatus(); // refresh status if quantity hits zero
            return true;
        }
        return false;
    }


    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Sponsor getSponsor() { return sponsor; }
    public void setSponsor(Sponsor sponsor) { this.sponsor = sponsor; }

    public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	public List<RewardTransaction> getRewardTransactions() {
		return rewardTransactions;
	}


	public void setRewardTransactions(List<RewardTransaction> rewardTransactions) {
		this.rewardTransactions = rewardTransactions;
	}
	
}

