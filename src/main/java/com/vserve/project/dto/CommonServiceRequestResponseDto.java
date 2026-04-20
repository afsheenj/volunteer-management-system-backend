package com.vserve.project.dto;

import com.vserve.project.enums.Category;

import java.time.LocalDate;
import java.time.LocalTime;

public record CommonServiceRequestResponseDto (
    Long id,
    String title,
    String description,
    Category category,
    String requestType,
    String landmark,
    String location,
    String status,
    LocalDate serviceDate,
    LocalTime serviceStartTime,
    LocalTime serviceEndTime,
    Integer minVolunteers,
    Integer maxVolunteers,
    Integer registeredCount,
    String createdByType,
    Long createdById,
    String postedBy
){
}
