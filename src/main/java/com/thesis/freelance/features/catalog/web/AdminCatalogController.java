package com.thesis.freelance.features.catalog.web;

import com.thesis.freelance.features.catalog.dto.CatalogCategoryDto;
import com.thesis.freelance.features.catalog.dto.CatalogSkillDto;
import com.thesis.freelance.features.catalog.dto.CreateCategoryRequest;
import com.thesis.freelance.features.catalog.dto.CreateSkillRequest;
import com.thesis.freelance.features.catalog.service.AdminCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/office/catalog")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;

    @GetMapping("/categories")
    public ResponseEntity<List<CatalogCategoryDto>> getCategories() {
        return ResponseEntity.ok(adminCatalogService.getCategories());
    }

    @GetMapping("/skills")
    public ResponseEntity<List<CatalogSkillDto>> getSkills() {
        return ResponseEntity.ok(adminCatalogService.getSkills());
    }

    @PostMapping("/categories")
    public ResponseEntity<CatalogCategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCatalogService.createCategory(req));
    }

    @PostMapping("/skills")
    public ResponseEntity<CatalogSkillDto> createSkill(@Valid @RequestBody CreateSkillRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCatalogService.createSkill(req));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable UUID id) {
        adminCatalogService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "Category deleted"));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Map<String, String>> deleteSkill(@PathVariable UUID id) {
        adminCatalogService.deleteSkill(id);
        return ResponseEntity.ok(Map.of("message", "Skill deleted"));
    }
}
