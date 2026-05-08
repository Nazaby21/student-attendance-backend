package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Response.StudentReportResponse;
import com.example.demo.enumeration.Role;
import com.example.demo.enumeration.Status;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.Enrollment;
import com.example.demo.modal.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassEntityRepository classRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StudentReportResponse> getStudentReports(Long classId) {
        UserDetailsImpl authUser = getAuthenticatedUser();
        boolean isTeacher = authUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

        // If teacher: only allow access to their assigned classes
        if (isTeacher) {
            // Get all classes assigned to this teacher (supports multi-class teachers)
            Set<Long> teacherClassIds = classRepository.findByTeachersId(authUser.getId()).stream()
                    .map(ClassEntity::getId)
                    .collect(Collectors.toSet());

            if (teacherClassIds.isEmpty()) {
                throw new RuntimeException("Access Denied: You are not assigned to any class.");
            }

            if (classId != null && !teacherClassIds.contains(classId)) {
                throw new RuntimeException("Access Denied: You can only view reports for your assigned class(es).");
            }

            // If no classId specified, use teacher's first assigned class
            if (classId == null) {
                classId = teacherClassIds.iterator().next();
            }
        }

        List<User> students;
        if (classId != null) {
            students = enrollmentRepository.findByClazzId(classId).stream()
                    .map(Enrollment::getStudent)
                    .collect(Collectors.toList());
        } else {
            students = userRepository.findByRole(Role.STUDENT);
        }

        return students.stream()
                .map(this::calculateStudentReport)
                .collect(Collectors.toList());
    }

    private StudentReportResponse calculateStudentReport(User student) {
        List<Attendance> attendances = attendanceRepository.findByEnrollmentStudentId(student.getId());

        long present = attendances.stream().filter(a -> a.getStatus() == Status.PRESENT).count();
        long absent = attendances.stream().filter(a -> a.getStatus() == Status.ABSENT).count();
        long late = attendances.stream().filter(a -> a.getStatus() == Status.LATE).count();
        long permission = attendances.stream().filter(a -> a.getStatus() == Status.PERMISSION).count();
        long total = attendances.size();

        double percentage = 0.0;
        if (total > 0) {
            // Attendance percentage = (present + late + permission) / total * 100
            // Absent is the only status that counts against the student
            percentage = Math.round(((double)(total - absent) / total) * 100.0 * 100.0) / 100.0;
        }

        Long classId = enrollmentRepository.findByStudentId(student.getId()).stream()
                .findFirst()
                .map(e -> e.getClazz().getId())
                .orElse(null);

        String className = enrollmentRepository.findByStudentId(student.getId()).stream()
                .findFirst()
                .map(e -> e.getClazz().getClassName())
                .orElse("N/A");

        return new StudentReportResponse(
                student.getId(),
                student.getName(),
                classId,
                className,
                present,
                absent,
                late,
                permission,
                total,
                percentage
        );
    }

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) auth.getPrincipal();
        }
        throw new RuntimeException("Unauthorized");
    }
}
