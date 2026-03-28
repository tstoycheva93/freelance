package com.thesis.freelance.features.location.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LocationDto(UUID id,
                   String name,
                   BigDecimal lat,
                   BigDecimal lng,
                   CountryDto country) {}
