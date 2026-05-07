package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.BlacklistRequest;
import com.example.demo.dto.Response.BlacklistResponse;
import com.example.demo.mapper.BlacklistMapper;
import com.example.demo.modal.Blacklist;
import com.example.demo.modal.User;
import com.example.demo.repository.BlacklistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final BlacklistMapper blacklistMapper;

    @Override
    public BlacklistResponse addToBlacklist(BlacklistRequest blacklistRequest) {
        User student = userRepository.findById(blacklistRequest.studentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Blacklist blacklist = blacklistMapper.toBlacklistEntity(blacklistRequest);
        blacklist.setStudent(student);
        blacklist.setAddedDate(LocalDateTime.now());

        return blacklistMapper.toBlacklistResponse(blacklistRepository.save(blacklist));
    }

    @Override
    public List<BlacklistResponse> getAllBlacklisted() {
        return blacklistRepository.findAll().stream()
                .map(blacklistMapper::toBlacklistResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromBlacklist(Long id) {
        blacklistRepository.deleteById(id);
    }

    @Override
    public boolean isBlacklisted(Long studentId) {
        return blacklistRepository.existsByStudentId(studentId);
    }
}
