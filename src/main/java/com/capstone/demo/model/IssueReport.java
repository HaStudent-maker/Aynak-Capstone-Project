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

	@Column(name = "title", nullable = false)
    private String Title;
	
	@Column(name = "category", nullable = false)
    private String category;
	
	@Column(name = "description", nullable = false)
    private String description;

	@Column(name = "imageData", nullable = false, columnDefinition = "LONGTEXT")
	private String imageData;

	@Column(name="imageType", nullable=false)
	private String imageType;
	
	@Column(name="location", nullable=false)
	private String location;

	@Column(name="status", nullable = false)
    private String status = "PENDING";
	
	@Column(name="longitude", nullable = true)
    private String Longitude;
	
	@Column(name="latitude", nullable = true)
    private String Latitude;
	
	

	

	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "assigned_officer_id")
	private MunicipalOfficer assignedOfficer;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "reported_by")
	private Users reportedBy;

   

    // ---- getters and setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

    public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		this.Title = title;
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
	
}


