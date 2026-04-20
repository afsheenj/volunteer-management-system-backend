package com.vserve.project.dto.admin;

import com.vserve.project.enums.Category;

import java.time.LocalDate;
import java.time.LocalTime;

public record AdminServiceRequestDto(
        Long id,
        String title,
        String description,
        Category category,
        String requestType,

        String landmark,
        String city,
        String state,

        String status,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,

        Integer minVolunteers,
        Integer maxVolunteers,
        Integer registeredCount,

        String createdByType,
        Long createdById,
        String postedBy,

        LocalDate createdDate
) {
}
