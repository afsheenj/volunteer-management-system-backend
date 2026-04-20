package com.vserve.project.dto;

import com.vserve.project.enums.Category;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateServiceRequestDto(
        String title,
        String description,
        Category category,
        String requestType,
        String landmark,
        String state,
        String city,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Integer minVolunteers,
        Integer maxVolunteers,
        Long organizationId
) {
}
