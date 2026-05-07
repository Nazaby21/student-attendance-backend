package com.example.demo.service;

import com.example.demo.dto.Request.ClassSessionRequest;
import com.example.demo.dto.Response.ClassSessionResponse;

import java.util.List;

public interface ClassSessionService {
    ClassSessionResponse createSession(ClassSessionRequest sessionRequest);
    ClassSessionResponse getSessionById(Long id);
    List<ClassSessionResponse> getAllSessions();
    List<ClassSessionResponse> getSessionsByClass(Long classId);
    ClassSessionResponse updateSession(Long id, ClassSessionRequest sessionRequest);
    void deleteSession(Long id);
}
