package com.vserve.project.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserAddressDto(
        @NotNull(message = "User id is must")
        Long userId,
        @NotBlank(message = "State is must")
        String state,
        @NotBlank(message = "District is must")
        String district,
        String pincode,
        @NotBlank(message = "City is must")
        String city,
        @NotBlank(message = "Street is must")
        String street,
        String doorNo

) { }
