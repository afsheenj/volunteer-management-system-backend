package com.vserve.project.controller;

import com.vserve.project.dto.location.CityDTO;
import com.vserve.project.dto.location.CountryDTO;
import com.vserve.project.dto.location.StateDTO;
import com.vserve.project.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class LocationController {

    private LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<CountryDTO> getCountries() {
        return locationService.getCountries();
    }

    @GetMapping("/{countryId}/states")
    public List<StateDTO> getStates(@PathVariable(name = "countryId") String countryId) {
        return locationService.getStates(countryId);
    }

    @GetMapping("/{countryId}/states/{stateId}/cities")
    public List<CityDTO> getCities(
            @PathVariable(name = "countryId") String countryId,
            @PathVariable(name = "stateId") String stateId
    ) {
        return locationService.getCities(countryId, stateId);
    }
}
