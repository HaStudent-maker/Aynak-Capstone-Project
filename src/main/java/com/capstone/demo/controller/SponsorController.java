package com.capstone.demo.controller;

import com.capstone.demo.model.Sponsor;
import com.capstone.demo.service.SponsorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sponsors")
@CrossOrigin("*")
public class SponsorController {

    @Autowired
    private SponsorService sponsorService;

    // ➤ CREATE Sponsor
    @PostMapping
    public ResponseEntity<Sponsor> createSponsor(@RequestBody Sponsor sponsor) {
        Sponsor created = sponsorService.createSponsor(sponsor);
        return ResponseEntity.ok(created);
    }

    // ➤ UPDATE Sponsor
    @PutMapping("/{id}")
    public ResponseEntity<Sponsor> updateSponsor(
            @PathVariable String id,
            @RequestBody Sponsor updatedSponsor) {

        Sponsor updated = sponsorService.updateSponsor(id, updatedSponsor);
        return ResponseEntity.ok(updated);
    }

    // ➤ GET Sponsor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Sponsor> getSponsorById(@PathVariable String id) {
        Sponsor sponsor = sponsorService.getSponsorById(id);
        return ResponseEntity.ok(sponsor);
    }

    // ➤ GET All Sponsors
    @GetMapping
    public ResponseEntity<List<Sponsor>> getAllSponsors() {
        return ResponseEntity.ok(sponsorService.getAllSponsors());
    }

    // ➤ DELETE Sponsor
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSponsor(@PathVariable String id) {
        sponsorService.deleteSponsor(id);
        return ResponseEntity.ok("Sponsor deleted successfully");
    }
}
