package com.example.demo.service;

public interface BlacklistCalculationService {
    void updateStudentBlacklistPoints(Long studentId);
    void recalculateAllStudentsPoints();
}
