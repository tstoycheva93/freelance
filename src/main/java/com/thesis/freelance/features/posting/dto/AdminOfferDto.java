package com.thesis.freelance.features.posting.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminOfferDto(
    UUID id,
    String freelancerName,
    String freelancerPhotoUrl,
    BigDecimal price,
    String status,
    String submittedAt,
    String deliveryTime
) {}
