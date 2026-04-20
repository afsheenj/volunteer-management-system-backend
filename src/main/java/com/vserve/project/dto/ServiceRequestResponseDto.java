package com.vserve.project.dto;

import com.vserve.project.enums.Category;

import java.time.LocalDate;
import java.time.LocalTime;

public record ServiceRequestResponseDto(
        Long id,
        String title,
        String description,
        Category category,
        String requestType,
        String landmark,
        String state,
        String city,
        String status,
        LocalDate serviceDate,
        LocalTime serviceTime,
        Integer minVolunteers,
        Integer maxVolunteers,
        Integer registeredCount,
        Integer approvedCount

) {
}
