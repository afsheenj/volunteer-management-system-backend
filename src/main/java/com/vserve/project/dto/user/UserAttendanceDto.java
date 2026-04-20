package com.vserve.project.dto.user;

public record UserAttendanceDto(
        Long serviceId,
        Long userId,
        boolean attended) {
}
