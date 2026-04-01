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
@Table(name="MunicipalOfficer")
public class MunicipalOfficer {
	
	@Id
    private String id;
	
	@Column(name="fullName", nullable = false)
    private String fullName;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="phoneNumber", nullable = true)
    private String phoneNumber;

    // Example: "Waste Management", "Roads & Transport", "Environment", etc.
    @Column(name="departmentName", nullable = false)
    private String departmentName;
    
    @OneToMany(mappedBy = "assignedOfficer", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<IssueReport> assignedReports = new ArrayList<>();
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public List<IssueReport> getAssignedReports() {
        return assignedReports;
    }

    public void setAssignedReports(List<IssueReport> assignedReports) {
        this.assignedReports = assignedReports;
    }

}
