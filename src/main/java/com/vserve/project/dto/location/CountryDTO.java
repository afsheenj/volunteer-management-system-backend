package com.vserve.project.dto.location;

public record CountryDTO(
        Long id,
        String name,
        String iso2,
        String iso3,
        Long phonecode
) {
}
