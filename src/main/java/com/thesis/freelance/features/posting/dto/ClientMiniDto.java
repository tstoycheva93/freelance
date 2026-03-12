package com.thesis.freelance.features.posting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientMiniDto {
    private UUID id;
    private String name;
    private String role;
    private String companyName;
    private String photoUrl;
    private BigDecimal rating;
    private Long reviewsCount;
    private Boolean isVerified;
}
