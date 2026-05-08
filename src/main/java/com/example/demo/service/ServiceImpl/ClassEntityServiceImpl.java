package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.dto.Response.TeacherInfo;
import com.example.demo.enumeration.Role;
import com.example.demo.mapper.ClassEntityMapper;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.ClassSession;
import com.example.demo.modal.Enrollment;
import com.example.demo.modal.Subject;
import com.example.demo.modal.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.security.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassEntityServiceImpl implements ClassEntityService {

    private final ClassEntityRepository classRepository;
    private final ClassEntityMapper classMapper;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassSessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubjectRepository subjectRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    /** Resolve teacherIds → User entities, validating each has TEACHER role. */
    private Set<User> resolveTeachers(List<Long> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) {
            return new HashSet<>();
        }
        List<User> found = userRepository.findAllById(teacherIds);
        for (User u : found) {
            if (u.getRole() != Role.TEACHER) {
                throw new com.example.demo.exception.AppException(
                        "User id=" + u.getId() + " is not a TEACHER (role=" + u.getRole() + ")", 
                        org.springframework.http.HttpStatus.BAD_REQUEST);
            }
        }
        if (found.size() != teacherIds.size()) {
            throw new com.example.demo.exception.ResourceNotFoundException("One or more teacher IDs were not found");
        }
        return new HashSet<>(found);
    }

    /** Map a ClassEntity → ClassEntityResponse, including teachers list. */
    private ClassEntityResponse toResponse(ClassEntity entity) {
        List<TeacherInfo> teacherInfos = entity.getTeachers().stream()
                .map(t -> new TeacherInfo(t.getId(), t.getName(), t.getEmail()))
                .collect(Collectors.toList());

        return new ClassEntityResponse(
                entity.getId(),
                entity.getClassName(),
                entity.getDescription(),
                entity.getYear(),
                entity.getCreatedDate(),
                teacherInfos
        );
    }

    // ── service methods ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public ClassEntityResponse createClass(ClassEntityRequest classRequest) {
        ClassEntity classEntity = classMapper.toClassEntity(classRequest);
        classEntity.setCreatedDate(LocalDateTime.now());
        classEntity.setTeachers(resolveTeachers(classRequest.teacherIds()));
        return toResponse(classRepository.save(classEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public ClassEntityResponse getClassById(Long id) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Class not found with id: " + id));
        
        // Security check for teachers
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            boolean isTeacher = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            
            if (isTeacher) {
                boolean isAssigned = classEntity.getTeachers().stream()
                        .anyMatch(t -> t.getId() != null && t.getId().equals(userDetails.getId()));
                if (!isAssigned) {
                    throw new org.springframework.security.access.AccessDeniedException("Access Denied: You are not assigned to this class.");
                }
            }
        }
        
        return toResponse(classEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassEntityResponse> getAllClasses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                return classRepository.findAll().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
            } else {
                return classRepository.findByTeachersId(userDetails.getId()).stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public ClassEntityResponse updateClass(Long id, ClassEntityRequest classRequest) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Class not found with id: " + id));

        classEntity.setClassName(classRequest.className());
        classEntity.setDescription(classRequest.description());
        classEntity.setYear(classRequest.year());
        classEntity.setTeachers(resolveTeachers(classRequest.teacherIds()));

        return toResponse(classRepository.save(classEntity));
    }

    @Override
    @Transactional
    public void deleteClass(Long id) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Class not found with id: " + id));

        // 1. Delete Attendance records for all enrollments of this class
        List<Enrollment> enrollments = enrollmentRepository.findByClazzId(id);
        for (Enrollment enrollment : enrollments) {
            List<Attendance> attendances = attendanceRepository.findByEnrollmentId(enrollment.getId());
            attendanceRepository.deleteAll(attendances);
        }
        enrollmentRepository.deleteAll(enrollments);

        // 2. Delete Attendance records for all sessions of this class
        List<ClassSession> sessions = sessionRepository.findByClazzId(id);
        for (ClassSession session : sessions) {
            List<Attendance> attendances = attendanceRepository.findBySessionId(session.getId());
            attendanceRepository.deleteAll(attendances);
        }
        sessionRepository.deleteAll(sessions);

        // 3. Delete all Subjects associated with this class
        List<Subject> subjects = subjectRepository.findByClazzId(id);
        subjectRepository.deleteAll(subjects);

        // 4. Clear the join table entries before deleting
        classEntity.getTeachers().clear();
        classRepository.save(classEntity);
        
        // 5. Delete the ClassEntity
        classRepository.deleteById(id);
    }
}
