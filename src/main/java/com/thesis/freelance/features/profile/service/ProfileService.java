package com.thesis.freelance.features.profile.service;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.exception.UserNotFoundException;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.client.dto.ClientPersonalDto;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.dto.FreelancerCertificationDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerEducationDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerSkillDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerWorkDto;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.profile.converter.PostingToProfileDtoConverter;
import com.thesis.freelance.features.profile.converter.ReviewToProfileDtoConverter;
import com.thesis.freelance.features.profile.dto.PeopleDto;
import com.thesis.freelance.features.profile.dto.ProfileResponseDto;
import com.thesis.freelance.features.posting.dto.PostingProfileDto;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.posting.repository.SavedPostingRepository;
import com.thesis.freelance.features.review.dto.ReviewProfileDto;
import com.thesis.freelance.features.review.entity.Review;
import com.thesis.freelance.features.review.repository.ReviewRepository;
import com.thesis.freelance.features.user.dto.UserDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.work.entity.Work;
import com.thesis.freelance.features.work.repository.WorkRepository;
import com.thesis.freelance.features.education.entity.Education;
import com.thesis.freelance.features.education.repository.EducationRepository;
import com.thesis.freelance.features.certification.entity.Certification;
import com.thesis.freelance.features.certification.repository.CertificationRepository;
import com.thesis.freelance.features.location.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final FreelancerRepository freelancerRepository;
    private final ClientRepository clientRepository;
    private final ReviewRepository reviewRepository;
    private final PostingRepository postingRepository;
    private final SavedPostingRepository savedPostingRepository;
    private final WorkRepository workRepository;
    private final EducationRepository educationRepository;
    private final CertificationRepository certificationRepository;
    private final FreelancerSkillsRepository freelancerSkillsRepository;
    private final PostingToProfileDtoConverter postingToProfileDtoConverter;
    private final ReviewToProfileDtoConverter reviewToProfileDtoConverter;


    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getActivityStatus() != ActivityStatus.ACTIVE) {
            throw new UserNotFoundException("User not found");
        }

        return buildProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getActivityStatus() != ActivityStatus.ACTIVE) {
            throw new UserNotFoundException("User not found");
        }

        return buildProfileResponse(user);
    }


    private ProfileResponseDto buildProfileResponse(User user) {
        String role = user.getRoleName();
        
        if (role == null) {
            return buildBasicProfile(user);
        }
        
        return switch (role) {
            case "FREELANCER" -> buildFreelancerProfile(user);
            case "CLIENT" -> buildClientProfile(user);
            default -> buildBasicProfile(user);
        };
    }

    private ProfileResponseDto buildBasicProfile(User user) {
        return ProfileResponseDto.builder()
                .user(mapUserToDto(user))
                .build();
    }

    private ProfileResponseDto buildFreelancerProfile(User user) {
        Freelancer freelancer = freelancerRepository.findByUserId(user.getId()).orElse(null);

        Double rating = user.getRating() != null ? user.getRating().doubleValue() : null;
        Long reviewCount = user.getReviewsCount() != null ? user.getReviewsCount() : 0L;

        FreelancerPersonalDto freelancerProfile = null;
        if (freelancer != null) {
            freelancerProfile = new FreelancerPersonalDto(
                    freelancer.getId(),
                    freelancer.getTitle(),
                    freelancer.getBio(),
                    freelancer.getWebsite(),
                    freelancer.getExperience(),
                    freelancer.getAvailability(),
                    freelancer.getCompleteness()
            );
        }

        List<FreelancerWorkDto> workExperience = workRepository.findAllByFreelancerUserId(user.getId(), ActivityStatus.ACTIVE)
                .stream()
                .map(this::mapWorkToDto)
                .collect(Collectors.toList());

        List<FreelancerEducationDto> education = educationRepository.findAllByFreelancerUserId(user.getId(), ActivityStatus.ACTIVE)
                .stream()
                .map(this::mapEducationToDto)
                .collect(Collectors.toList());

        List<FreelancerCertificationDto> certifications = certificationRepository.findAllByFreelancerUserId(user.getId(), ActivityStatus.ACTIVE)
                .stream()
                .map(this::mapCertificationToDto)
                .collect(Collectors.toList());

        List<FreelancerSkillDto> skills = freelancerSkillsRepository.findAllByFreelancerUserId(user.getId(), ActivityStatus.ACTIVE)
                .stream()
                .map(this::mapSkillToDto)
                .collect(Collectors.toList());

        return ProfileResponseDto.builder()
                .user(mapUserToDto(user))
                .rating(rating)
                .reviewCount(reviewCount)
                .freelancerProfile(freelancerProfile)
                .workExperience(workExperience)
                .education(education)
                .certifications(certifications)
                .skills(skills)
                .build();
    }

    private ProfileResponseDto buildClientProfile(User user) {
        Client client = clientRepository.findByUserId(user.getId()).orElse(null);

        Double rating = user.getRating() != null ? user.getRating().doubleValue() : null;
        Long reviewCount = user.getReviewsCount() != null ? user.getReviewsCount() : 0L;

        ClientPersonalDto clientProfile = null;
        if (client != null) {
            clientProfile = new ClientPersonalDto(
                    client.getId(),
                    client.getOccupation(),
                    client.getCompanyName(),
                    client.getBio(),
                    client.getWebsite(),
                    client.getCompleteness()
            );
        }

        return ProfileResponseDto.builder()
                .user(mapUserToDto(user))
                .rating(rating)
                .reviewCount(reviewCount)
                .clientProfile(clientProfile)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ReviewProfileDto> getUserReviews(UUID userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByRevieweeId(userId, ActivityStatus.ACTIVE, pageable);
        return reviews.map(review -> reviewToProfileDtoConverter.mapReviewToDto(review));
    }

    @Transactional(readOnly = true)
    public Page<PostingProfileDto> getUserPostings(UUID userId, Pageable pageable) {
        Page<Posting> postings = postingRepository.findAllByClientUserId(userId, ActivityStatus.ACTIVE, pageable);
        return postings.map(posting -> postingToProfileDtoConverter.mapPostingToDto(posting));
    }

    @Transactional(readOnly = true)
    public Page<PostingProfileDto> getSavedPostings(UUID userId, Pageable pageable) {
        Page<Posting> postings = savedPostingRepository.findSavedPostingsByUserId(userId, ActivityStatus.ACTIVE, pageable);
        return postings.map(posting -> postingToProfileDtoConverter.mapPostingToDto(posting));
    }

    @Transactional(readOnly = true)
    public Page<PeopleDto> getPeople(int page, int size, String search, String role, String sortBy, String sortOrder, UUID currentUserId) {
        boolean sortByDistance = "distance".equalsIgnoreCase(sortBy);
        boolean sortByRating = "rating".equalsIgnoreCase(sortBy);
        
        Sort sort;
        if (sortByDistance) {
            sort = Sort.unsorted();
        } else if (sortByRating) {
            sort = "desc".equalsIgnoreCase(sortOrder) ? Sort.by(Sort.Direction.DESC, "rating") : Sort.by(Sort.Direction.ASC, "rating");
        } else {
            sort = buildSort(sortBy, sortOrder);
        }
        
        List<PeopleDto> peopleDtos;
        
        if (sortByDistance && currentUserId != null) {
            List<User> allUsers = userRepository.findAllBySearchAndRole(search, role, sort);
            peopleDtos = allUsers.stream()
                    .map(this::mapUserToPeopleDto)
                    .collect(Collectors.toList());
            
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            if (currentUser != null && currentUser.getLocation() != null) {
                Location currentLocation = currentUser.getLocation();
                final double currentLat = currentLocation.getLatitude().doubleValue();
                final double currentLon = currentLocation.getLongitude().doubleValue();
                
                final boolean descending = "desc".equalsIgnoreCase(sortOrder);
                
                peopleDtos.sort((a, b) -> {
                    Double distA = a.getLocation() != null && a.getLocation().latitude() != null ? 
                        calculateDistanceKm(currentLat, currentLon, 
                            a.getLocation().latitude().doubleValue(),
                            a.getLocation().longitude().doubleValue()) : null;
                    Double distB = b.getLocation() != null && b.getLocation().latitude() != null ? 
                        calculateDistanceKm(currentLat, currentLon,
                            b.getLocation().latitude().doubleValue(),
                            b.getLocation().longitude().doubleValue()) : null;
                    
                    if (distA == null && distB == null) return 0;
                    if (distA == null) return 1;
                    if (distB == null) return -1;
                    
                    return descending ? Double.compare(distB, distA) : Double.compare(distA, distB);
                });
            }
            
            int start = page * size;
            int end = Math.min(start + size, peopleDtos.size());
            List<PeopleDto> pagedContent = start < peopleDtos.size() 
                ? peopleDtos.subList(start, end) 
                : List.of();
            
            return new PageImpl<>(pagedContent, PageRequest.of(page, size), peopleDtos.size());
        } else {
            Page<User> usersPage = userRepository.findAllBySearchAndRole(search, role, PageRequest.of(page, size, sort));
            List<PeopleDto> peopleDtoList = usersPage.getContent().stream()
                    .map(this::mapUserToPeopleDto)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(peopleDtoList, PageRequest.of(page, size), usersPage.getTotalElements());
        }
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; //radiusa na zemqta
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    private Sort buildSort(String sortBy, String sortOrder) {
        String field = switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "rating" -> "rating";
            default -> "name";
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        return Sort.by(direction, field);
    }

    private PeopleDto mapUserToPeopleDto(User user) {
        String role = user.getRoleName();
        
        String bio = null;
        String website = null;
        
        Double rating = user.getRating() != null ? user.getRating().doubleValue() : null;
        Long reviewCount = user.getReviewsCount() != null ? user.getReviewsCount() : 0L;

        if ("FREELANCER".equals(role)) {
            Freelancer freelancer = freelancerRepository.findByUserId(user.getId()).orElse(null);
            if (freelancer != null) {
                bio = freelancer.getBio();
                website = freelancer.getWebsite();
            }
        } else if ("CLIENT".equals(role)) {
            Client client = clientRepository.findByUserId(user.getId()).orElse(null);
            if (client != null) {
                bio = client.getBio();
                website = client.getWebsite();
            }
        }

        LocationDto locationDto = null;
        if (user.getLocation() != null) {
            locationDto = new LocationDto(
                user.getLocation().getId(), 
                user.getLocation().getName(),
                user.getLocation().getLatitude(),
                user.getLocation().getLongitude()
            );
        }

        return PeopleDto.builder()
                .id(user.getId())
                .name(user.getName() + " " + user.getSurname())
                .firstName(user.getName())
                .lastName(user.getSurname())
                .username(user.getUsername())
                .role(role)
                .location(locationDto)
                .bio(bio)
                .rating(rating != null ? java.math.BigDecimal.valueOf(rating) : java.math.BigDecimal.ZERO)
                .reviewCount(reviewCount)
                .website(website)
                .profilePhoto(user.getPhotoUrl())
                .isVerified(user.getIsVerified())
                .build();
    }

    private UserDto mapUserToDto(User user) {
        LocationDto locationDto = null;
        if (user.getLocation() != null) {
            locationDto = new LocationDto(
                user.getLocation().getId(), 
                user.getLocation().getName(),
                user.getLocation().getLatitude(),
                user.getLocation().getLongitude()
            );
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getRoleName(),
                user.getPhone(),
                user.getBirthDate(),
                locationDto,
                user.getPhotoUrl(),
                null,
                null,
                user.getIsVerified()
        );
    }

    private FreelancerWorkDto mapWorkToDto(Work work) {
        return new FreelancerWorkDto(
                work.getId(),
                work.getTitle(),
                work.getCompany(),
                work.getType(),
                work.getStartDate(),
                work.getEndDate(),
                work.isCurrent(),
                work.getDescription()
        );
    }

    private FreelancerEducationDto mapEducationToDto(Education education) {
        return new FreelancerEducationDto(
                education.getId(),
                education.getInstitution(),
                education.getDegree(),
                education.getField(),
                education.getStartDate(),
                education.getEndDate(),
                education.isCurrent(),
                education.getDescription()
        );
    }

    private FreelancerCertificationDto mapCertificationToDto(Certification certification) {
        return new FreelancerCertificationDto(
                certification.getId(),
                certification.getTitle(),
                certification.getOrganization(),
                certification.getDate(),
                certification.getExpiration(),
                certification.getIdentifier()
        );
    }

    private FreelancerSkillDto mapSkillToDto(FreelancerSkills freelancerSkill) {
        return new FreelancerSkillDto(
                freelancerSkill.getId(),
                freelancerSkill.getSkill().getSkill()
        );
    }



}
