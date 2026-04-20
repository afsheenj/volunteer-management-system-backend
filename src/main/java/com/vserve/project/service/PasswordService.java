package com.vserve.project.service;

import com.vserve.project.dto.PasswordUpdateRequestDto;

public interface PasswordService {

    String updatePassword(PasswordUpdateRequestDto dto);
}
