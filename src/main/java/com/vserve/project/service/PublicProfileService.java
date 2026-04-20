package com.vserve.project.service;

import com.vserve.project.dto.PublicProfileDto;

import java.util.Map;

public interface PublicProfileService {
    PublicProfileDto getPublicProfile(String username);

    PublicProfileDto getPublicProfileById(Long id);
}
