package com.thesis.freelance.features.freelancer.web;

import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalUpdateDto;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.service.FreelancerPersonalService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/freelancer/personal")
@RequiredArgsConstructor
public class FreelancerPersonalController {

    private final FreelancerPersonalService freelancerPersonalService;
    private final UserRepository userRepository;
    private final FreelancerRepository freelancerRepository;

    @GetMapping
    public ResponseEntity<?> getPersonal(@AuthenticationPrincipal UserDetails userDetails) {
        Freelancer freelancer = getFreelancer(userDetails);
        return ResponseEntity.ok(freelancerPersonalService.getPersonal(freelancer.getId()));
    }

    @PutMapping
    public ResponseEntity<?> updatePersonal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FreelancerPersonalUpdateDto dto) {
        Freelancer freelancer = getFreelancer(userDetails);
        return ResponseEntity.ok(freelancerPersonalService.updatePersonal(freelancer.getId(), dto));
    }

    private Freelancer getFreelancer(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return freelancerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Freelancer profile not found"));
    }
}
