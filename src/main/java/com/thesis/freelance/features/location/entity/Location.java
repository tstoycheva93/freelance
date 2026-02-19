package com.thesis.freelance.features.location.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.thesis.freelance.common.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String name;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Transient
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public record Coordinates(BigDecimal lat, BigDecimal lng) {}

    @Transient
    public Coordinates getCoordinates() {
        if (!hasCoordinates()) return null;
        return new Coordinates(latitude, longitude);
    }


}
