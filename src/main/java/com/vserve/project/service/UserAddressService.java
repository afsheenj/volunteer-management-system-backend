package com.vserve.project.service;

import com.vserve.project.dto.user.UserAddressDto;

public interface UserAddressService {
    String userAddress(UserAddressDto dto);

    String updateUserAddress(UserAddressDto dto);

    UserAddressDto getUserAddress(Long userId);
}
