package com.capstone.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ControllerHello {

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/admin/test")
    public ResponseEntity<String> sayHelloToAdmin() {
        return ResponseEntity.ok("Hello Admin");
    }

    @GetMapping("/municipal/test")
    public ResponseEntity<String> sayHelloToMunicipal() {
        return ResponseEntity.ok("Hello Gov");
    }

    @GetMapping("/sponsor/test")
    public ResponseEntity<String> sayHelloToSponsors() {
        return ResponseEntity.ok("Hello Sponsor");
    }

    @GetMapping("/user/test")
    public ResponseEntity<String> sayHelloToUser() {
        return ResponseEntity.ok("Hello User");
    }
}
