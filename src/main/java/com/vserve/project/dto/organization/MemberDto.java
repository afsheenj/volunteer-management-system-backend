package com.vserve.project.dto.organization;

public record MemberDto(
        Long id,
        String username,
        String email,
        String phone
) {
}
