package com.thesis.freelance.features.offer.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOfferRequestDto {
    private BigDecimal price;
    private String message;
    private LocalDate deliveryTime;
    private List<String> attachmentUrls;
}