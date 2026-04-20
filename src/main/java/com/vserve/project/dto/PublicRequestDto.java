package com.vserve.project.dto;

public record PublicRequestDto(
        Long id,
        String title,
        String category,
        String status,
        String State,
        String city) {}