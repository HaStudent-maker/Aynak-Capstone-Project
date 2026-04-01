package com.capstone.demo.model;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name="Users")
public class Users {
	
	@Id
    private String id;
	
	@Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phonenumber", nullable = true)
    private String phonenumber;
    
    @Column(name = "address", nullable = true)
    private String address;
    
    @Column(name = "rewardPoints", nullable = true)
    private int rewardPoints = 0; // Points earned from reporting issues

     // Account status
    
    
    @OneToMany(mappedBy = "reportedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueReport> issueReports;

    
    @OneToMany(mappedBy = "redeemedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reward> rewards;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber()
 {
		return phonenumber;
	}

	public void setPhoneNumber(String phonenumber) {
		this.phonenumber = phonenumber;

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	
    
    
    
    

}
