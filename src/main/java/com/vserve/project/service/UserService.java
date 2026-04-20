package com.vserve.project.service;

import com.vserve.project.dto.admin.AdminProfileDto;
import com.vserve.project.dto.admin.AdminUserResponseDto;
import com.vserve.project.dto.admin.CreateAdminDto;
import com.vserve.project.dto.user.UserRegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    String registerUser(UserRegisterRequestDto dto);

    String updateUser(Long id, UserRegisterRequestDto dto);

    Page<AdminUserResponseDto> getAllUsers(int page, int size, String username, String role, Boolean availability);

    AdminProfileDto getAdminProfile(Long id);

    List<AdminProfileDto> getAllAdmins();

    void createAdmin(@Valid CreateAdminDto dto);
}