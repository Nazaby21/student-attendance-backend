package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.mapper.ClassEntityMapper;
import com.example.demo.modal.ClassEntity;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassEntityServiceImpl implements ClassEntityService {

    private final ClassEntityRepository classRepository;
    private final ClassEntityMapper classMapper;

    @Override
    public ClassEntityResponse createClass(ClassEntityRequest classRequest) {
        ClassEntity classEntity = classMapper.toClassEntity(classRequest);
        return classMapper.toClassEntityResponse(classRepository.save(classEntity));
    }

    @Override
    public ClassEntityResponse getClassById(Long id) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        return classMapper.toClassEntityResponse(classEntity);
    }

    @Override
    public List<ClassEntityResponse> getAllClasses() {
        return classRepository.findAll().stream()
                .map(classMapper::toClassEntityResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClassEntityResponse updateClass(Long id, ClassEntityRequest classRequest) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        classEntity.setClassName(classRequest.className());
        classEntity.setCode(classRequest.code());
        classEntity.setSection(classRequest.section());
        classEntity.setDescription(classRequest.description());
        classEntity.setYear(classRequest.year());
        return classMapper.toClassEntityResponse(classRepository.save(classEntity));
    }

    @Override
    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }
}
