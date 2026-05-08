package com.example.demo.controller;

import com.example.demo.dto.Request.BlacklistRequest;
import com.example.demo.dto.Response.BlacklistResponse;
import com.example.demo.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping
    public ResponseEntity<BlacklistResponse> addToBlacklist(@RequestBody BlacklistRequest blacklistRequest) {
        return ResponseEntity.ok(blacklistService.addToBlacklist(blacklistRequest));
    }

    @GetMapping
    public ResponseEntity<List<BlacklistResponse>> getAllBlacklisted() {
        return ResponseEntity.ok(blacklistService.getAllBlacklisted());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromBlacklist(@PathVariable Long id) {
        blacklistService.removeFromBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{studentId}")
    public ResponseEntity<Boolean> isBlacklisted(@PathVariable Long studentId) {
        return ResponseEntity.ok(blacklistService.isBlacklisted(studentId));
    }
}
