package com.capstone.demo.service;

import com.capstone.demo.model.IssueReport;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IssueReportService {

    IssueReport submitIssue(IssueReport issue, String userId);

    IssueReport assignOfficer(Long issueId, String officerId);

    IssueReport resolveIssue(Long issueId);

    List<IssueReport> getIssuesByUser(String userId);

    List<IssueReport> getIssuesByOfficer(String officerId);
    List<IssueReport> getIssuesByOfficerDepartment(String officerId);

    IssueReport getIssueById(Long id);

    List<IssueReport> getAllIssues();
    void saveIssueImage(Long issueId, MultipartFile imageFile);


    void deleteIssue(Long id);
}
