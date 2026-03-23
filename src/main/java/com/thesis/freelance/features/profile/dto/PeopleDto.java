package com.thesis.freelance.features.profile.dto;

import com.thesis.freelance.features.location.dto.LocationDto;
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
public class PeopleDto {
    private UUID id;
    private String name;
    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private LocationDto location;
    private String bio;
    private BigDecimal rating;
    private Long reviewCount;
    private String website;
    private String profilePhoto;
    private Boolean isVerified;
    private String title;
    private String occupation;
}
