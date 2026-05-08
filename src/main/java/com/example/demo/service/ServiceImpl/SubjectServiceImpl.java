package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.SubjectRequest;
import com.example.demo.dto.Response.SubjectResponse;
import com.example.demo.mapper.SubjectMapper;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.ClassSession;
import com.example.demo.modal.Subject;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final ClassEntityRepository classRepository;
    private final ClassSessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubjectMapper subjectMapper;

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectRequest subjectRequest) {
        Subject subject = subjectMapper.toSubjectEntity(subjectRequest);
        if (subjectRequest.classId() != null) {
            ClassEntity clazz = classRepository.findById(subjectRequest.classId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            subject.setClazz(clazz);
        }
        return subjectMapper.toSubjectResponse(subjectRepository.save(subject));
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        return subjectMapper.toSubjectResponse(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toSubjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest subjectRequest) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        
        subject.setSubjectName(subjectRequest.subjectName());
        subject.setCode(subjectRequest.code());
        subject.setCredits(subjectRequest.credits());
        
        if (subjectRequest.classId() != null) {
            ClassEntity clazz = classRepository.findById(subjectRequest.classId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            subject.setClazz(clazz);
        }

        return subjectMapper.toSubjectResponse(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        // 1. Delete Attendance records for all sessions of this subject
        List<ClassSession> sessions = sessionRepository.findBySubjectId(id);
        for (ClassSession session : sessions) {
            List<Attendance> attendances = attendanceRepository.findBySessionId(session.getId());
            attendanceRepository.deleteAll(attendances);
        }
        sessionRepository.deleteAll(sessions);

        // 2. Delete the Subject
        subjectRepository.deleteById(id);
    }
}
