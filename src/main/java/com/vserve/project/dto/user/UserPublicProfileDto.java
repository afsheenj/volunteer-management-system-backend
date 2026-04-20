package com.vserve.project.dto.user;

import java.util.List;

public record UserPublicProfileDto(
        Long id,
        String username,
        String email,
        String phone,
        String role,
        String city,
        String state,
        List<UserSkillResponseDto> skills
) {
}
