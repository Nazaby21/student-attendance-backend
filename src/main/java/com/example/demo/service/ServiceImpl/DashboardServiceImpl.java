package com.example.demo.service.ServiceImpl;

import com.example.demo.enumeration.Role;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ClassEntityRepository classRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", (long) userRepository.findByRole(Role.STUDENT).size());
        stats.put("totalClasses", classRepository.count());
        stats.put("totalAttendance", attendanceRepository.count());
        return stats;
    }
}
