package com.thesis.freelance.features.location.service;

import com.thesis.freelance.features.location.dto.LocationDto;
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
                .map(l -> new LocationDto(l.getId(), l.getName()))
                .toList();
    }
}