package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.IssueReport;
import com.capstone.demo.model.MunicipalOfficer;
import com.capstone.demo.model.Users;
import com.capstone.demo.ropositary.IssueReportRepository;
import com.capstone.demo.ropositary.MunicipalOfficerRepository;
import com.capstone.demo.ropositary.UserRepository;
import com.capstone.demo.service.IssueReportService;
import com.capstone.demo.serviceimpl.S3Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class IssueReportServiceImpl implements IssueReportService {

    private final IssueReportRepository issueReportRepo;
    private final UserRepository usersRepo;
    private final MunicipalOfficerRepository officerRepo;
    private final S3Service s3Service;

    public IssueReportServiceImpl(IssueReportRepository issueReportRepo,
                                  UserRepository usersRepo,
                                  MunicipalOfficerRepository officerRepo,
                                  S3Service s3Service) {
        this.issueReportRepo = issueReportRepo;
        this.usersRepo = usersRepo;
        this.officerRepo = officerRepo;
        this.s3Service = s3Service;
    }

    @Override
    public IssueReport submitIssue(IssueReport issue, String userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int currentPoints = user.getRewardPoints();
        user.setRewardPoints(currentPoints + 10);
        usersRepo.save(user);

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

        String imageUrl = s3Service.uploadImage(imageFile);

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
}