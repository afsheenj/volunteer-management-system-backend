package com.vserve.project.service;

import com.vserve.project.dto.location.CityDTO;
import com.vserve.project.dto.location.CountryDTO;
import com.vserve.project.dto.location.StateDTO;

import java.util.List;

public interface LocationService {
    List<CountryDTO> getCountries();

    List<StateDTO> getStates(String countryId);

    List<CityDTO> getCities(String countryId, String stateId);
}
