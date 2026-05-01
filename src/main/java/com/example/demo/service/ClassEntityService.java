package com.example.demo.service;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;

import java.util.List;

public interface ClassEntityService {
    ClassEntityResponse createClass(ClassEntityRequest classRequest);
    ClassEntityResponse getClassById(Long id);
    List<ClassEntityResponse> getAllClasses();
    ClassEntityResponse updateClass(Long id, ClassEntityRequest classRequest);
    void deleteClass(Long id);
}
