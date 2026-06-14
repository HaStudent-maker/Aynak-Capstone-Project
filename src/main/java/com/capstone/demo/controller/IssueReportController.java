package com.capstone.demo.controller;

import com.capstone.demo.model.IssueReport;
import com.capstone.demo.service.IssueReportService;
import com.capstone.demo.serviceimpl.S3Service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueReportController {

	private final IssueReportService issueService;
	private final S3Service s3Service;

	public IssueReportController(IssueReportService issueService, S3Service s3Service) {
	    this.issueService = issueService;
	    this.s3Service = s3Service;
	}

    // Submit issue WITH image in one request
	@PostMapping(value = "/submit", consumes = "multipart/form-data")
	@PreAuthorize("hasRole('User')")
	public ResponseEntity<String> submitIssueWithImage(
	        @RequestParam("title") String title,
	        @RequestParam("category") String category,
	        @RequestParam("description") String description,
	        @RequestParam("location") String location,
	        @RequestParam("image") MultipartFile imageFile,
	        Authentication authentication
	) {
	    String userId = getKeycloakId(authentication);

	    String imageUrl = s3Service.uploadImage(imageFile);

	    IssueReport issue = new IssueReport();
	    issue.setTitle(title);
	    issue.setCategory(category);
	    issue.setDescription(description);
	    issue.setLocation(location);
	    issue.setImageData(imageUrl);
	    issue.setImageType(imageFile.getContentType());

	    issueService.submitIssue(issue, userId);

	    return ResponseEntity.ok("Report submitted successfully");
	}

    // Assign officer to issue
    @PutMapping("/assign/{issueId}/{officerId}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<IssueReport> assignOfficer(
            @PathVariable Long issueId,
            @PathVariable String officerId
    ) {
        IssueReport updated = issueService.assignOfficer(issueId, officerId);
        return ResponseEntity.ok(updated);
    }

    // Resolve issue
    @PutMapping("/resolve/{issueId}")
    @PreAuthorize("hasRole('MunicipalOfficer')")
    public ResponseEntity<IssueReport> resolveIssue(@PathVariable Long issueId) {
        IssueReport resolved = issueService.resolveIssue(issueId);
        return ResponseEntity.ok(resolved);
    }

    // Get logged-in user's issues
    @GetMapping("/my")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<List<IssueReport>> getMyIssues(Authentication authentication) {
        String userId = getKeycloakId(authentication);
        return ResponseEntity.ok(issueService.getIssuesByUser(userId));
    }

    // Get issues assigned to logged-in officer
    @GetMapping("/officer/my")
    @PreAuthorize("hasRole('MunicipalOfficer')")
    public ResponseEntity<List<IssueReport>> getMyOfficerIssues(Authentication authentication) {
        String officerId = getKeycloakId(authentication);
        return ResponseEntity.ok(issueService.getIssuesByOfficer(officerId));
    }

    // Get issue by ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IssueReport> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    // Get all issues
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<IssueReport>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    // Delete issue
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.ok("Issue deleted successfully");
    }

    private String getKeycloakId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getSubject();
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            Object principal = oauthToken.getPrincipal();

            if (principal instanceof OidcUser oidcUser) {
                return oidcUser.getSubject();
            }
        }

        throw new RuntimeException("Unable to extract Keycloak user ID");
    }
}