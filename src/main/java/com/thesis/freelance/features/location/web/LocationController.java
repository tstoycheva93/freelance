package com.thesis.freelance.features.location.web;

import com.thesis.freelance.features.location.dto.LocationDto;
import com.thesis.freelance.features.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
}