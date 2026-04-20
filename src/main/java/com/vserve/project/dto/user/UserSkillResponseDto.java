package com.vserve.project.dto.user;

import com.vserve.project.enums.ProficiencyLevel;

public record UserSkillResponseDto(
        Long skillId,
        String skillName,
        ProficiencyLevel proficiencyLevel
) {
}
