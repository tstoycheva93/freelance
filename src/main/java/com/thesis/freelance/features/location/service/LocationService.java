package com.thesis.freelance.features.location.service;

import com.thesis.freelance.features.location.dto.CountryDto;
import com.thesis.freelance.features.location.dto.LocationDto;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(this::toLocationDto)
                .toList();
    }

    public List<LocationDto> searchLocations(String query) {
        return locationRepository.searchByName(query)
                .stream()
                .map(this::toLocationDto)
                .toList();
    }

    private LocationDto toLocationDto(Location location) {
        CountryDto countryDto = null;
        if (location.getCountry() != null) {
            countryDto = new CountryDto(
                location.getCountry().getId(),
                location.getCountry().getName(),
                location.getCountry().getInitials()
            );
        }
        return new LocationDto(
            location.getId(),
            location.getName(),
            location.getLatitude(),
            location.getLongitude(),
            countryDto
        );
    }
}
