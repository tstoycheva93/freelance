package com.thesis.freelance.features.freelancer.web;

import com.thesis.freelance.features.category.service.CategoryService;
import com.thesis.freelance.features.freelancer.dto.*;
import com.thesis.freelance.features.freelancer.service.CvGenerationService;
import com.thesis.freelance.features.freelancer.service.FreelancerDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/freelancer/details")
@RequiredArgsConstructor
@Slf4j
public class FreelancerDetailsController {

    private final FreelancerDetailsService freelancerDetailsService;
    private final CvGenerationService cvGenerationService;

    @GetMapping
    public ResponseEntity<?> getFreelancerDetails(@RequestParam UUID freelancerId) {
        return ResponseEntity.ok(freelancerDetailsService.getFreelancerDetails(freelancerId));
    }

    @PostMapping("/skills/{skillId}")
    public ResponseEntity<?> addSkill(
            @RequestParam UUID freelancerId,
            @PathVariable UUID skillId) {
        return ResponseEntity.ok(freelancerDetailsService.addSkill(freelancerId, skillId));
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<?> removeSkill(
            @RequestParam UUID freelancerId,
            @PathVariable UUID skillId) {
        freelancerDetailsService.removeSkill(freelancerId, skillId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/education")
    public ResponseEntity<?> addEducation(
            @RequestParam UUID freelancerId,
            @Valid @RequestBody FreelancerEducationDto dto) {
        return ResponseEntity.ok(freelancerDetailsService.addEducation(freelancerId, dto));
    }

    @PutMapping("/education")
    public ResponseEntity<?> updateEducation(@Valid @RequestBody FreelancerEducationDto dto) {
        if (dto.id() == null) {
            return ResponseEntity.badRequest().body("Education ID is required for update");
        }
        return ResponseEntity.ok(freelancerDetailsService.updateEducation(dto.id(), dto));
    }

    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<?> deleteEducation(@PathVariable UUID educationId) {
        freelancerDetailsService.deleteEducation(educationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/work")
    public ResponseEntity<?> addWork(
            @RequestParam UUID freelancerId,
            @Valid @RequestBody FreelancerWorkDto dto) {
        return ResponseEntity.ok(freelancerDetailsService.addWork(freelancerId, dto));
    }

    @PutMapping("/work")
    public ResponseEntity<?> updateWork(@Valid @RequestBody FreelancerWorkDto dto) {
        if (dto.id() == null) {
            return ResponseEntity.badRequest().body("Work ID is required for update");
        }
        return ResponseEntity.ok(freelancerDetailsService.updateWork(dto.id(), dto));
    }

    @DeleteMapping("/work/{workId}")
    public ResponseEntity<?> deleteWork(@PathVariable UUID workId) {
        freelancerDetailsService.deleteWork(workId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/certifications")
    public ResponseEntity<?> addCertification(
            @RequestParam UUID freelancerId,
            @Valid @RequestBody FreelancerCertificationDto dto) {
        return ResponseEntity.ok(freelancerDetailsService.addCertification(freelancerId, dto));
    }

    @PutMapping("/certifications")
    public ResponseEntity<?> updateCertification(@Valid @RequestBody FreelancerCertificationDto dto) {
        if (dto.id() == null) {
            return ResponseEntity.badRequest().body("Certification ID is required for update");
        }
        return ResponseEntity.ok(freelancerDetailsService.updateCertification(dto.id(), dto));
    }

    @DeleteMapping("/certifications/{certificationId}")
    public ResponseEntity<?> deleteCertification(@PathVariable UUID certificationId) {
        freelancerDetailsService.deleteCertification(certificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cv")
    public ResponseEntity<?> getCV(@RequestParam UUID freelancerId) {
        return ResponseEntity.ok(freelancerDetailsService.getCV(freelancerId));
    }

    @PostMapping("/cv")
    public ResponseEntity<?> saveOrReplaceCV(
            @RequestParam UUID freelancerId,
            @Valid @RequestBody FreelancerCVCreateDto dto) {
        return ResponseEntity.ok(freelancerDetailsService.saveOrReplaceCV(freelancerId, dto));
    }

    @DeleteMapping("/cv/{cvId}")
    public ResponseEntity<?> deleteCV(
            @RequestParam UUID freelancerId,
            @PathVariable UUID cvId) {
        freelancerDetailsService.deleteCV(cvId, freelancerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-cv")
    public ResponseEntity<?> generateCv(
            @RequestParam UUID freelancerId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        log.info("Received generate-cv request for freelancer: {}", freelancerId);
        try {
            byte[] pdfBytes = cvGenerationService.generateCvPdf(freelancerId);
            log.info("CV generated successfully, size: {} bytes", pdfBytes.length);
            String fileName = cvGenerationService.generateCvFileName(firstName, lastName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("CV generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
