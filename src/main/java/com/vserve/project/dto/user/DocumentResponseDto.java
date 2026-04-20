package com.vserve.project.dto.user;

import com.vserve.project.enums.DocumentStatus;
import com.vserve.project.enums.DocumentType;

import java.time.LocalDateTime;

public record DocumentResponseDto(
        Long id,
        Long userId,
        String username,
        String email,
        DocumentType documentType,
        DocumentStatus documentStatus,
        String documentURL,
        LocalDateTime uploadedAt,
        LocalDateTime reviewedAt
) {}