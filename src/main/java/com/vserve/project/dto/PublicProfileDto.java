package com.vserve.project.dto;

import com.vserve.project.dto.PublicParticipationDto;
import com.vserve.project.dto.PublicRequestDto;
import com.vserve.project.dto.user.UserSkillResponseDto;

import java.util.List;

public record PublicProfileDto(

        String type,                 // USER or ORGANIZATION
        String name,                 // username or orgName
        String city,
        String state,

        List<UserSkillResponseDto> skills,

        List<PublicRequestDto> postedRequests,

        List<PublicParticipationDto> participations,

        Double score

) {}