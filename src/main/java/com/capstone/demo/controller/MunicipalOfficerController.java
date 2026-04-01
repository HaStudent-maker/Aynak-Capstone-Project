package com.capstone.demo.controller;

import com.capstone.demo.model.IssueReport;
import com.capstone.demo.model.MunicipalOfficer;
import com.capstone.demo.service.MunicipalOfficerService;
import com.capstone.demo.service.IssueReportService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officers")
@CrossOrigin("*")
public class MunicipalOfficerController {

    @Autowired
    private MunicipalOfficerService officerService;

    private IssueReportService issueReportService;

    // ➤ CREATE a new Municipal Officer
    @PostMapping
    public ResponseEntity<MunicipalOfficer> createOfficer(@RequestBody MunicipalOfficer officer) {
        MunicipalOfficer saved = officerService.createOfficer(officer);
        return ResponseEntity.ok(saved);
    }

    // ➤ GET Officer by ID
    @GetMapping("/{id}")
    public ResponseEntity<MunicipalOfficer> getOfficerById(@PathVariable String id) {
        MunicipalOfficer officer = officerService.getOfficerById(id);

        if (officer == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(officer);
    }

    // ➤ GET ALL Officers
    @GetMapping
    public ResponseEntity<List<MunicipalOfficer>> getAllOfficers() {
        List<MunicipalOfficer> officers = officerService.getAllOfficers();
        return ResponseEntity.ok(officers);
    }

    // ➤ UPDATE Officer
    @PutMapping("/{id}")
    public ResponseEntity<MunicipalOfficer> updateOfficer(
            @PathVariable String id,
            @RequestBody MunicipalOfficer updatedOfficer) {

        MunicipalOfficer updated = officerService.updateOfficer(id, updatedOfficer);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    // ➤ DELETE Officer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOfficer(@PathVariable String id) {
        officerService.deleteOfficer(id);
        return ResponseEntity.ok("Officer deleted successfully");
    }
    
    @GetMapping("/issues/department")
    public ResponseEntity<List<IssueReport>> getDepartmentIssues(Authentication auth) {

        String officerId = auth.name(); // Keycloak subject ID

        
		List<IssueReport> issues = issueReportService.getIssuesByOfficerDepartment(officerId);

        return ResponseEntity.ok(issues);
    }
}
