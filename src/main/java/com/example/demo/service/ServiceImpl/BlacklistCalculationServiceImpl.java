package com.example.demo.service.ServiceImpl;

import com.example.demo.enumeration.Role;
import com.example.demo.enumeration.Status;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.BlacklistHistory;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.User;
import com.example.demo.modal.Blacklist;
import com.example.demo.modal.Enrollment;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.BlacklistHistoryRepository;
import com.example.demo.repository.BlacklistRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BlacklistCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistCalculationServiceImpl implements BlacklistCalculationService {

    private final AttendanceRepository attendanceRepository;
    private final BlacklistHistoryRepository blacklistHistoryRepository;
    private final BlacklistRepository blacklistRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    public void updateStudentBlacklistPoints(Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) return;

        // Rolling 3-month window: current date - 3 months
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        String dateThreshold = threeMonthsAgo.toString(); // YYYY-MM-DD

        // Calculate current points from all attendance records in the last 3 months
        List<Attendance> last3MonthsAttendance = attendanceRepository
                .findByEnrollmentStudentIdAndSessionDateGreaterThanEqual(student.getId(), dateThreshold);

        double points = 0.0;
        for (Attendance a : last3MonthsAttendance) {
            if (a.getStatus() == Status.ABSENT) points += 1.0;
            else if (a.getStatus() == Status.PERMISSION) points += 0.5;
            else if (a.getStatus() == Status.LATE) points += 0.25;
        }

        student.setCurrentBlacklistPoints(points);

        boolean wasBlacklisted = student.getBlacklisted() != null ? student.getBlacklisted() : false;
        int blacklistCount = student.getBlacklistCount() != null ? student.getBlacklistCount() : 0;

        if (points >= 20.0) {
            if (!wasBlacklisted) {
                // Transition to blacklisted status
                // Check if they already have a blacklist record in the last 3 months to prevent duplicates
                List<BlacklistHistory> recentHistory = blacklistHistoryRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
                boolean hasRecentBlacklist = false;
                if (!recentHistory.isEmpty()) {
                    LocalDateTime threeMonthsAgoDT = LocalDateTime.now().minusMonths(3);
                    if (recentHistory.get(0).getCreatedAt().isAfter(threeMonthsAgoDT)) {
                        hasRecentBlacklist = true;
                    }
                }

                if (!hasRecentBlacklist) {
                    student.setBlacklistCount(blacklistCount + 1);
                    
                    // Get class for history
                    ClassEntity currentClass = enrollmentRepository.findByStudentId(student.getId()).stream().findFirst()
                            .map(Enrollment::getClazz).orElse(null);

                    // Create Blacklist History Record
                    BlacklistHistory history = new BlacklistHistory();
                    history.setStudent(student);
                    history.setClazz(currentClass);
                    history.setPoints(points);
                    history.setBlacklistNumber(student.getBlacklistCount());
                    history.setCreatedAt(LocalDateTime.now());
                    history.setReason("Automatic blacklist: Accumulated " + points + " points in the last 3 months.");
                    blacklistHistoryRepository.save(history);

                    // Add to active Blacklist table if not already there
                    if (!blacklistRepository.existsByStudentId(student.getId())) {
                        Blacklist blacklist = new Blacklist();
                        blacklist.setStudent(student);
                        blacklist.setClazz(currentClass);
                        blacklist.setAddedDate(LocalDateTime.now());
                        blacklist.setReason("Automatic blacklist: " + points + " points.");
                        blacklistRepository.save(blacklist);
                    }
                }
                student.setBlacklisted(true);
            }
        } else {
            // Points < 20: automatically mark student as not blacklisted
            if (wasBlacklisted) {
                student.setBlacklisted(false);
                // Remove from active Blacklist table
                blacklistRepository.deleteByStudentId(student.getId());
            }
        }

        userRepository.save(student);
    }

    @Override
    public void recalculateAllStudentsPoints() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        for (User student : students) {
            updateStudentBlacklistPoints(student.getId());
        }
    }
}
