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
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
@Table(name = "issue_reports")
public class IssueReport {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "category", nullable = false)
    private String category;
	
	@Column(name = "description", nullable = false)
    private String description;

	@Column(name = "imageData", nullable = false, columnDefinition = "LONGBLOB")
	private String imageData;

	@Column(name="imageType", nullable=false)
	private String imageType;
	
	@Column(name="location", nullable=false)
	private String location;

	@Column(name="status", nullable = false)
    private String status = "PENDING";
	
	@Column(name="longitude", nullable = false)
    private String Longitude;
	
	@Column(name="latitude", nullable = false)
    private String Latitude;
	
	
	// ai attributes
	@Column(name = "urgency")
	private String urgency;

	@Column(name = "ai_objects")
	private String aiObjects;

	

	@ManyToOne
	@JoinColumn(name = "assigned_officer_id")
	private MunicipalOfficer assignedOfficer;
	
    @ManyToOne
    @JoinColumn(name = "reported_by")  // the foreign key
    private Users reportedBy;

   

    // ---- getters and setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String title) {
        this.category = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

    public Users getUser() {
        return reportedBy;
    }

    public void setUser(Users user) {
        this.reportedBy = user;
    }
    public MunicipalOfficer getAssignedOfficer() {
		return assignedOfficer;
	}

	public void setAssignedOfficer(MunicipalOfficer assignedOfficer) {
		this.assignedOfficer = assignedOfficer;
	}

	public Users getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(Users reportedBy) {
		this.reportedBy = reportedBy;
	}
	
	

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		this.Longitude = longitude;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		this.Latitude = latitude;
	}

	public String getImageType() {
	    return imageType;
	}

	public void setImageType(String imageType) {
	    this.imageType = imageType;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUrgency() { return urgency; }
	public void setUrgency(String urgency) { this.urgency = urgency; }
	public String getAiObjects() { return aiObjects; }
	public void setAiObjects(String aiObjects) { this.aiObjects = aiObjects; }
	

	

	
}


