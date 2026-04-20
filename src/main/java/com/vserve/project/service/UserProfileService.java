package com.vserve.project.service;

import com.vserve.project.dto.user.*;

import java.util.List;

public interface UserProfileService {

    UserProfileResponseDto getUserProfile(Long userId);
    String updateUserProfile(Long userId, UserProfileUpdateDto dto);
    UserAddressDto getUserAddress(Long userId);
    List<UserSkillResponseDto> getUserSkills(Long userId);
    UserPublicProfileDto getPublicProfile(Long userId);
}
