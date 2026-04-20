package com.vserve.project.dto.user;

import com.vserve.project.enums.Availability;
import com.vserve.project.enums.Gender;

import java.time.LocalDate;

public record UserProfileUpdateDto(
        String username,
        String phone,
        Gender gender,
        LocalDate dateOfBirth,
        Availability availability,
        String bio
) {
}
