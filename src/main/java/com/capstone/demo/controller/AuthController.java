package com.capstone.demo.controller;

import com.capstone.demo.security.KeycloakSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private KeycloakSyncService syncService;

    // Manually trigger sync
    @GetMapping("/sync")
    public ResponseEntity<?> sync(@AuthenticationPrincipal Jwt jwt) {
        syncService.syncFromKeycloak(jwt);
        return ResponseEntity.ok("Sync completed.");
    }
}
