package com.thesis.freelance.features.freelancer.web;

import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalUpdateDto;
import com.thesis.freelance.features.freelancer.service.FreelancerPersonalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/freelancer/personal")
@RequiredArgsConstructor
public class FreelancerPersonalController {

    private final FreelancerPersonalService freelancerPersonalService;

    @GetMapping
    public ResponseEntity<?> getPersonal(@RequestParam UUID freelancerId) {
        return ResponseEntity.ok(freelancerPersonalService.getPersonal(freelancerId));
    }

    @PutMapping
    public ResponseEntity<?> updatePersonal(
            @RequestParam UUID freelancerId,
            @Valid @RequestBody FreelancerPersonalUpdateDto dto) {
        return ResponseEntity.ok(freelancerPersonalService.updatePersonal(freelancerId, dto));
    }
}
