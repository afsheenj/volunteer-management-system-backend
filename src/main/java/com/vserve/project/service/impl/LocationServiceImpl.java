package com.vserve.project.service.impl;

import com.vserve.project.dto.location.CityDTO;
import com.vserve.project.dto.location.CountryDTO;
import com.vserve.project.dto.location.StateDTO;
import com.vserve.project.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private WebClient webClient;

    public LocationServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CountryDTO> getCountries() {
        return webClient.get()
                .uri("/countries")
                .retrieve()
                .bodyToFlux(CountryDTO.class)
                .toStream().toList();
    }

    @Override
    public List<StateDTO> getStates(String countryId) {
        return webClient.get()
                .uri("/countries/" + countryId + "/states")
                .retrieve()
                .bodyToFlux(StateDTO.class)
                .toStream().toList();
    }

    @Override
    public List<CityDTO> getCities(String countryId, String stateId) {
        return webClient.get()
                .uri("/countries/" + countryId + "/states/" + stateId + "/cities")
                .retrieve()
                .bodyToFlux(CityDTO.class)
                .toStream().toList();
    }
}
