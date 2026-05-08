package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.ClassSessionRequest;
import com.example.demo.dto.Response.ClassSessionResponse;
import com.example.demo.mapper.ClassSessionMapper;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.ClassSession;
import com.example.demo.modal.Subject;
import com.example.demo.modal.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassSessionServiceImpl implements ClassSessionService {

    private final ClassSessionRepository sessionRepository;
    private final ClassEntityRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassSessionMapper sessionMapper;

    @Override
    public ClassSessionResponse createSession(ClassSessionRequest sessionRequest) {
        ClassEntity clazz = classRepository.findById(sessionRequest.clazz())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Subject subject = subjectRepository.findById(sessionRequest.subject())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        User teacher = userRepository.findById(sessionRequest.teacher())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        ClassSession session = sessionMapper.toClassSessionEntity(sessionRequest);
        session.setClazz(clazz);
        session.setSubject(subject);
        session.setTeacher(teacher);

        return sessionMapper.toClassSessionResponse(sessionRepository.save(session));
    }

    @Override
    public ClassSessionResponse getSessionById(Long id) {
        ClassSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return sessionMapper.toClassSessionResponse(session);
    }

    @Override
    public List<ClassSessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toClassSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassSessionResponse> getSessionsByClass(Long classId) {
        return sessionRepository.findByClazzId(classId).stream()
                .map(sessionMapper::toClassSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassSessionResponse updateSession(Long id, ClassSessionRequest sessionRequest) {
        ClassSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        ClassEntity clazz = classRepository.findById(sessionRequest.clazz())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Subject subject = subjectRepository.findById(sessionRequest.subject())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        User teacher = userRepository.findById(sessionRequest.teacher())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        session.setClazz(clazz);
        session.setSubject(subject);
        session.setTeacher(teacher);
        session.setDate(sessionRequest.date());
        session.setTimeSlot(sessionRequest.timeSlot());

        return sessionMapper.toClassSessionResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public void deleteSession(Long id) {
        // Delete associated attendance records first
        List<Attendance> attendances = attendanceRepository.findBySessionId(id);
        attendanceRepository.deleteAll(attendances);

        sessionRepository.deleteById(id);
    }
}
