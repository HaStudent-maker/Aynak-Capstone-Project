package com.capstone.demo.controller;

import com.capstone.demo.model.IssueReport;
import com.capstone.demo.service.IssueReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin("*")
public class IssueReportController {

    @Autowired
    private IssueReportService issueService;

    // ➤ SUBMIT ISSUE (User creates a report)
    @PostMapping("/submit/{userId}")
    public ResponseEntity<IssueReport> submitIssue(
            @RequestBody IssueReport issue,
            @PathVariable String userId) {

        IssueReport created = issueService.submitIssue(issue, userId);
        return ResponseEntity.ok(created);
    }

    // ➤ ASSIGN OFFICER TO ISSUE
    @PutMapping("/assign/{issueId}/{officerId}")
    public ResponseEntity<IssueReport> assignOfficer(
            @PathVariable Long issueId,
            @PathVariable String officerId) {

        IssueReport updated = issueService.assignOfficer(issueId, officerId);
        return ResponseEntity.ok(updated);
    }

    // ➤ RESOLVE ISSUE (Officer marks completed)
    @PutMapping("/resolve/{issueId}")
    public ResponseEntity<IssueReport> resolveIssue(@PathVariable Long issueId) {
        IssueReport resolved = issueService.resolveIssue(issueId);
        return ResponseEntity.ok(resolved);
    }

    // ➤ GET ISSUES REPORTED BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IssueReport>> getIssuesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(issueService.getIssuesByUser(userId));
    }

    // ➤ GET ISSUES ASSIGNED TO OFFICER
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<IssueReport>> getIssuesByOfficer(@PathVariable String officerId) {
        return ResponseEntity.ok(issueService.getIssuesByOfficer(officerId));
    }

    // ➤ GET ISSUE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<IssueReport> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    // ➤ GET ALL ISSUES
    @GetMapping
    public ResponseEntity<List<IssueReport>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    // ➤ DELETE ISSUE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.ok("Issue deleted successfully");
    }
    
    @PostMapping(value = "/{issueId}/upload-image", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadIssueImage(
            @PathVariable Long issueId,
            @RequestParam("image") MultipartFile imageFile) {

    	issueService.saveIssueImage(issueId, imageFile);

        return ResponseEntity.ok("Image uploaded successfully");
    }

}
