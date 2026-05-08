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
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlacklistResponse> addToBlacklist(@jakarta.validation.Valid @RequestBody BlacklistRequest blacklistRequest) {
        return ResponseEntity.ok(blacklistService.addToBlacklist(blacklistRequest));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<BlacklistResponse>> getBlacklist(
            @RequestParam(required = false) Integer months,
            @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(blacklistService.getFilteredBlacklist(months, classId));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<BlacklistResponse>> getBlacklistHistory(
            @RequestParam(required = false) Integer months,
            @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(blacklistService.getFilteredBlacklistHistory(months, classId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeFromBlacklist(@PathVariable Long id) {
        blacklistService.removeFromBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Boolean> isBlacklisted(@PathVariable Long studentId) {
        return ResponseEntity.ok(blacklistService.isBlacklisted(studentId));
    }
}
