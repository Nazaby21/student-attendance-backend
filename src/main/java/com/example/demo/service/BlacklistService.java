package com.example.demo.service;

import com.example.demo.dto.Request.BlacklistRequest;
import com.example.demo.dto.Response.BlacklistResponse;
import java.util.List;

public interface BlacklistService {
    BlacklistResponse addToBlacklist(BlacklistRequest blacklistRequest);
    List<BlacklistResponse> getFilteredBlacklist(Integer months, Long classId);
    List<BlacklistResponse> getFilteredBlacklistHistory(Integer months, Long classId);
    void removeFromBlacklist(Long id);
    boolean isBlacklisted(Long studentId);
}
