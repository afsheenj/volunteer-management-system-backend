package com.vserve.project.dto.user;

import com.vserve.project.enums.DocumentType;

public record DocumentUploadRequestDto(
        Long userId,
        DocumentType documentType,
        String documentUrl
) {}
