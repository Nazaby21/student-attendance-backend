package com.example.demo.dto.Request;

import java.util.List;

public record ClassEntityRequest(
        String className,
        String description,
        int year,
        List<Long> teacherIds
) {
}
