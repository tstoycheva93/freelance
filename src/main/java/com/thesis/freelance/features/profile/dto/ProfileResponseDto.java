package com.thesis.freelance.features.profile.dto;

import com.thesis.freelance.features.client.dto.ClientPersonalDto;
import com.thesis.freelance.features.freelancer.dto.*;
import com.thesis.freelance.features.posting.dto.PostingProfileDto;
import com.thesis.freelance.features.review.dto.ReviewProfileDto;
import com.thesis.freelance.features.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {
    private UserDto user;
    private Double rating;
    private Long reviewCount;
    
    // Client-specific
    private ClientPersonalDto clientProfile;
    
    // Freelancer-specific
    private FreelancerPersonalDto freelancerProfile;
    private List<FreelancerWorkDto> workExperience;
    private List<FreelancerEducationDto> education;
    private List<FreelancerCertificationDto> certifications;
    private List<FreelancerSkillDto> skills;
    
    // Reviews and Postings
    private List<ReviewProfileDto> reviews;
    private List<PostingProfileDto> postings;
}
