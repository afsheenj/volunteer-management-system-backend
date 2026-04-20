package com.vserve.project.dto.user;

import com.vserve.project.enums.Availability;
import com.vserve.project.enums.Gender;
import com.vserve.project.enums.Role;

import java.time.LocalDate;

public record UserProfileResponseDto(
        Long id,
        String username,
        String email,
        String phone,
        Role role,
        Availability availability,
        Gender gender,
        LocalDate dateOfBirth,
        String bio,
        Double score
) {
}
