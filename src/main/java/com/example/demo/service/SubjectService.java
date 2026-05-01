package com.example.demo.service;

import com.example.demo.dto.Request.SubjectRequest;
import com.example.demo.dto.Response.SubjectResponse;

import java.util.List;

public interface SubjectService {
    SubjectResponse createSubject(SubjectRequest subjectRequest);
    SubjectResponse getSubjectById(Long id);
    List<SubjectResponse> getAllSubjects();
    SubjectResponse updateSubject(Long id, SubjectRequest subjectRequest);
    void deleteSubject(Long id);
}
