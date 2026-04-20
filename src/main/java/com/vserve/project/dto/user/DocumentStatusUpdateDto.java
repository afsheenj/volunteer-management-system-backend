package com.vserve.project.dto.user;

import com.vserve.project.enums.DocumentStatus;

public record DocumentStatusUpdateDto(
        DocumentStatus documentStatus
) {}