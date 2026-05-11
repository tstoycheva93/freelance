package com.thesis.freelance.features.catalog.service;

import com.thesis.freelance.features.catalog.dto.CatalogCategoryDto;
import com.thesis.freelance.features.catalog.dto.CatalogSkillDto;
import com.thesis.freelance.features.catalog.dto.CreateCategoryRequest;
import com.thesis.freelance.features.catalog.dto.CreateSkillRequest;
import com.thesis.freelance.features.catalog.repository.AdminCatalogRepository;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.features.skill.repository.SkillRepository;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCatalogService {

    private final AdminCatalogRepository adminCatalogRepo;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public List<CatalogCategoryDto> getCategories() {
        List<Object[]> rows = adminCatalogRepo.findAllCategories();
        List<CatalogCategoryDto> result = new ArrayList<>();
        for (Object[] r : rows) {
            UUID id = toUUID(r[0]);
            String name = (String) r[1];
            boolean inUse = toBool(r[2]);
            result.add(new CatalogCategoryDto(id, name, inUse));
        }
        return result;
    }

    public List<CatalogSkillDto> getSkills() {
        List<Object[]> rows = adminCatalogRepo.findAllSkills();
        List<CatalogSkillDto> result = new ArrayList<>();
        for (Object[] r : rows) {
            UUID id = toUUID(r[0]);
            String name = (String) r[1];
            String categoryName = (String) r[2];
            boolean inUse = toBool(r[3]);
            result.add(new CatalogSkillDto(id, name, categoryName, inUse));
        }
        return result;
    }

    @Transactional
    public CatalogCategoryDto createCategory(CreateCategoryRequest req) {
        if (categoryRepository.findByName(req.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        Category category = new Category();
        category.setName(req.name());
        category = categoryRepository.save(category);
        return new CatalogCategoryDto(category.getId(), category.getName(), false);
    }

    @Transactional
    public CatalogSkillDto createSkill(CreateSkillRequest req) {
        if (skillRepository.findBySkill(req.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill already exists");
        }
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Skill skill = new Skill();
        skill.setSkill(req.name());
        skill.setCategory(category);
        skill = skillRepository.save(skill);
        return new CatalogSkillDto(skill.getId(), skill.getSkill(), category.getName(), false);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (adminCatalogRepo.isCategoryInUse(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete category — skills are assigned to it");
        }
        adminCatalogRepo.deleteCategory(id);
    }

    @Transactional
    public void deleteSkill(UUID id) {
        if (adminCatalogRepo.isSkillInUse(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete skill — it is in use by freelancers or postings");
        }
        adminCatalogRepo.deleteSkill(id);
    }

    private static UUID toUUID(Object val) {
        if (val instanceof UUID) return (UUID) val;
        if (val instanceof byte[]) {
            byte[] bytes = (byte[]) val;
            long msb = 0, lsb = 0;
            for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
            return new UUID(msb, lsb);
        }
        return UUID.fromString(val.toString());
    }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() == 1;
        return "1".equals(val.toString()) || "true".equalsIgnoreCase(val.toString());
    }
}
