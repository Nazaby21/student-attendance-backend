package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.BlacklistRequest;
import com.example.demo.dto.Response.BlacklistResponse;
import com.example.demo.mapper.BlacklistMapper;
import com.example.demo.modal.Blacklist;
import com.example.demo.modal.BlacklistHistory;
import com.example.demo.modal.User;
import com.example.demo.repository.BlacklistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.security.UserDetailsImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final com.example.demo.repository.ClassEntityRepository classRepository;
    private final com.example.demo.repository.BlacklistHistoryRepository blacklistHistoryRepository;
    private final BlacklistMapper blacklistMapper;

    @Override
    public BlacklistResponse addToBlacklist(BlacklistRequest blacklistRequest) {
        User student = userRepository.findById(blacklistRequest.studentId())
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Student not found"));

        com.example.demo.modal.ClassEntity classEntity = null;
        if (blacklistRequest.classId() != null) {
            classEntity = classRepository.findById(blacklistRequest.classId())
                    .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Class not found"));
        }

        Blacklist blacklist = blacklistMapper.toBlacklistEntity(blacklistRequest);
        blacklist.setStudent(student);
        blacklist.setClazz(classEntity);
        blacklist.setAddedDate(LocalDateTime.now());

        return blacklistMapper.toBlacklistResponse(blacklistRepository.save(blacklist));
    }

    @Override
    public List<BlacklistResponse> getFilteredBlacklist(Integer months, Long classId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Blacklist> allBlacklist = blacklistRepository.findAll();

        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            boolean isTeacher = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

            if (isTeacher) {
                // Filter by teacher's assigned classes
                allBlacklist = allBlacklist.stream()
                        .filter(b -> b.getClazz() != null && 
                                b.getClazz().getTeachers().stream()
                                        .anyMatch(t -> t.getId() != null && t.getId().equals(userDetails.getId())))
                        .collect(Collectors.toList());
            }
        }

        // Apply Month Filter
        if (months != null && months > 0) {
            LocalDateTime cutoff = LocalDateTime.now().minusMonths(months);
            allBlacklist = allBlacklist.stream()
                    .filter(b -> b.getAddedDate().isAfter(cutoff))
                    .collect(Collectors.toList());
        }

        // Apply Class Filter
        if (classId != null) {
            allBlacklist = allBlacklist.stream()
                    .filter(b -> b.getClazz() != null && b.getClazz().getId().equals(classId))
                    .collect(Collectors.toList());
        }

        return allBlacklist.stream()
                .map(b -> blacklistMapper.toBlacklistResponse(b))
                .collect(Collectors.toList());
    }

    @Override
    public List<BlacklistResponse> getFilteredBlacklistHistory(Integer months, Long classId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<BlacklistHistory> allHistory = blacklistHistoryRepository.findAll();

        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            boolean isTeacher = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

            if (isTeacher) {
                allHistory = allHistory.stream()
                        .filter(h -> h.getClazz() != null && 
                                h.getClazz().getTeachers().stream()
                                        .anyMatch(t -> t.getId() != null && t.getId().equals(userDetails.getId())))
                        .collect(Collectors.toList());
            }
        }

        if (months != null && months > 0) {
            LocalDateTime cutoff = LocalDateTime.now().minusMonths(months);
            allHistory = allHistory.stream()
                    .filter(h -> h.getCreatedAt().isAfter(cutoff))
                    .collect(Collectors.toList());
        }

        if (classId != null) {
            allHistory = allHistory.stream()
                    .filter(h -> h.getClazz() != null && h.getClazz().getId().equals(classId))
                    .collect(Collectors.toList());
        }

        return allHistory.stream()
                .map(h -> blacklistMapper.toBlacklistResponse(h))
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
