package com.capstone.demo.serviceimpl;

import com.capstone.demo.ai.AiAgentClient;

import com.capstone.demo.model.*;
import com.capstone.demo.ropositary.IssueReportRepository;
import com.capstone.demo.ropositary.MunicipalOfficerRepository;
import com.capstone.demo.ropositary.UserRepository;
import com.capstone.demo.service.IssueReportService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.capstone.demo.exception.BadRequestException;
import java.util.Map;

import java.io.IOException;
import java.util.List;
import java.util.Base64;

@Service
public class IssueReportServiceImpl implements IssueReportService {

    private final IssueReportRepository issueReportRepo;
    private final UserRepository usersRepo;
    private final MunicipalOfficerRepository officerRepo;
    private final AiAgentClient aiAgentClient;   // new field



    public IssueReportServiceImpl(IssueReportRepository issueReportRepo,
                                  UserRepository usersRepo,
                                  MunicipalOfficerRepository officerRepo,
                                  AiAgentClient aiAgentClient) {
        this.issueReportRepo = issueReportRepo;
        this.usersRepo = usersRepo;
        this.officerRepo = officerRepo;
        this.aiAgentClient = aiAgentClient;
    }
    @Override
    public IssueReport submitIssue(IssueReport issue, String userId) {
    	Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

   //Award points for submitting a report
        int currentPoints = user.getRewardPoints();
        user.setRewardPoints(currentPoints + 10);  // Add +10 points

        usersRepo.save(user);  // Save updated user points

// Save the issue
        issue.setReportedBy(user);
        issue.setStatus("PENDING");

        return issueReportRepo.save(issue);
    }
    

    @Override
    public IssueReport assignOfficer(Long issueId, String officerId) {
        IssueReport issue = issueReportRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        MunicipalOfficer officer = officerRepo.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        issue.setAssignedOfficer(officer);
        issue.setStatus("ASSIGNED");

        return issueReportRepo.save(issue);
    }

    @Override
    public IssueReport resolveIssue(Long issueId) {
        IssueReport issue = issueReportRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setStatus("RESOLVED");
        

        return issueReportRepo.save(issue);
    }

    @Override
    public List<IssueReport> getIssuesByUser(String userId) {
        return issueReportRepo.findByReportedBy_Id(userId);
    }

    @Override
    public List<IssueReport> getIssuesByOfficer(String officerId) {
        return issueReportRepo.findByAssignedOfficer_Id(officerId);
    }
    
    @Override
    public List<IssueReport> getIssuesByOfficerDepartment(String officerId) {
        MunicipalOfficer officer = officerRepo.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        String department = officer.getDepartmentName();

        return issueReportRepo.findByCategory(department);
    }
    @Override
    public IssueReport getIssueById(Long id) {
        return issueReportRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
    }
    
    @Override
    public void saveIssueImage(Long issueId, MultipartFile imageFile) {
        IssueReport issue = issueReportRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        String imageUrl = "https://storage.example.com/issues/" + imageFile.getOriginalFilename();

        issue.setImageData(imageUrl);
        issue.setImageType(imageFile.getContentType());

        issueReportRepo.save(issue);
    }


    @Override
    public List<IssueReport> getAllIssues() {
        return issueReportRepo.findAll();
    }

    @Override
    public void deleteIssue(Long id) {
        issueReportRepo.deleteById(id);
    }
    


    
    @Override
    public IssueReport submitIssueWithImage(String userId, String description,
                                            String location, String latitude,
                                            String longitude, MultipartFile image) {

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. run the AI pipeline
        Map<String, Object> ai = aiAgentClient.analyze(image, latitude, longitude, description);

        // 2. duplicate? reject
        if (Boolean.TRUE.equals(ai.get("is_duplicate"))) {
            throw new BadRequestException("This incident appears to already be reported nearby.");
        }

        // 3. build the report from AI results
        IssueReport issue = new IssueReport();
        issue.setReportedBy(user);
        issue.setDescription(description);
        issue.setLocation(location);
        issue.setLatitude(latitude);
        issue.setLongitude(longitude);
        issue.setStatus("PENDING");
        issue.setCategory((String) ai.get("category"));
        issue.setUrgency((String) ai.get("urgency"));

        Object objects = ai.get("objects_detected");
        if (objects instanceof java.util.List<?> list) {
            issue.setAiObjects(String.join(", ",
                    list.stream().map(String::valueOf).toList()));
        }

        // 4. store the BLURRED image, not the original
        issue.setImageData((String) ai.get("blurred_image_base64"));
        issue.setImageType("image/jpeg");

        return issueReportRepo.save(issue);
        // deliberately NO +10 points here — points come on admin approval later
    }

	
}
