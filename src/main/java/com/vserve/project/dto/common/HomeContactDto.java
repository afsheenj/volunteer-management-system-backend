package com.vserve.project.dto.common;

public record HomeContactDto(
    String fullName,
    String mailId,
    String natureOfService,
    String description
) {}