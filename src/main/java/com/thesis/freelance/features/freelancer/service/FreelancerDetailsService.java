package com.thesis.freelance.features.freelancer.service;

import com.thesis.freelance.features.certification.entity.Certification;
import com.thesis.freelance.features.certification.repository.CertificationRepository;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.education.entity.Education;
import com.thesis.freelance.features.education.repository.EducationRepository;
import com.thesis.freelance.features.freelancer.dto.*;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.features.skill.repository.SkillRepository;
import com.thesis.freelance.features.uploadedCV.entity.UploadedCV;
import com.thesis.freelance.features.uploadedCV.repository.UploadedCVRepository;
import com.thesis.freelance.features.work.entity.Work;
import com.thesis.freelance.features.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreelancerDetailsService {

    private final FreelancerRepository freelancerRepository;
    private final FreelancerSkillsRepository freelancerSkillsRepository;
    private final EducationRepository educationRepository;
    private final WorkRepository workRepository;
    private final CertificationRepository certificationRepository;
    private final UploadedCVRepository uploadedCVRepository;
    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;

    public List<CategoryWithSkillsDto> getSkillCategoriesWithSkills() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    List<SkillDto> skills = skillRepository.findByCategory_Id(category.getId())
                            .stream()
                            .map(skill -> new SkillDto(
                                    skill.getId(),
                                    skill.getSkill(),
                                    category.getId(),
                                    category.getName()
                            ))
                            .collect(Collectors.toList());
                    return new CategoryWithSkillsDto(category.getId(), category.getName(), skills);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FreelancerDetailsDto getFreelancerDetails(UUID freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        List<FreelancerSkillDto> skills = freelancerSkillsRepository.findByFreelancerIdWithSkill(freelancerId)
                .stream()
                .map(fs -> new FreelancerSkillDto(
                        fs.getSkill().getId(),
                        fs.getSkill().getSkill()
                ))
                .toList();

        List<FreelancerEducationDto> education = educationRepository.findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(freelancerId)
                .stream()
                .map(this::toEducationDto)
                .collect(Collectors.toList());

        List<FreelancerWorkDto> work = workRepository.findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(freelancerId)
                .stream()
                .map(this::toWorkDto)
                .collect(Collectors.toList());

        List<FreelancerCertificationDto> certifications = certificationRepository.findAllByFreelancer_IdOrderByDateDesc(freelancerId)
                .stream()
                .map(this::toCertificationDto)
                .collect(Collectors.toList());

        Optional<UploadedCV> cv = uploadedCVRepository.findFirstByFreelancer_IdOrderByUploadedAtDesc(freelancerId);
        FreelancerCVDto cvDto = cv.map(this::toCVDto).orElse(null);

        return new FreelancerDetailsDto(
                freelancer.getId(),
                freelancer.getTitle(),
                freelancer.getBio(),
                freelancer.getExperience(),
                freelancer.getAvailability(),
                freelancer.getWebsite(),
                skills,
                education,
                work,
                certifications,
                cvDto
        );
    }

    @Transactional
    public FreelancerSkillDto addSkill(UUID freelancerId, UUID skillId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        Optional<FreelancerSkills> existing = freelancerSkillsRepository.findByFreelancerAndSkill(freelancer, skill);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Skill already added");
        }

        FreelancerSkills freelancerSkill = FreelancerSkills.builder()
                .freelancer(freelancer)
                .skill(skill)
                .build();
        freelancerSkillsRepository.save(freelancerSkill);

        skill.incrementPopularity();
        skillRepository.save(skill);

        return new FreelancerSkillDto(skill.getId(), skill.getSkill());
    }

    @Transactional
    public void removeSkill(UUID freelancerId, UUID skillId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        FreelancerSkills freelancerSkill = freelancerSkillsRepository.findByFreelancerAndSkill(freelancer, skill)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found for this freelancer"));

        freelancerSkillsRepository.delete(freelancerSkill);
    }

    @Transactional
    public FreelancerEducationDto addEducation(UUID freelancerId, FreelancerEducationDto dto) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        validateEducationDates(dto);

        Education education = Education.builder()
                .freelancer(freelancer)
                .institution(dto.institution())
                .degree(dto.degree())
                .field(dto.field())
                .startDate(dto.startDate())
                .endDate(dto.isCurrent() != null && dto.isCurrent() ? null : dto.endDate())
                .isCurrent(dto.isCurrent() != null && dto.isCurrent())
                .description(dto.description())
                .build();

        education = educationRepository.save(education);
        return toEducationDto(education);
    }

    @Transactional
    public FreelancerEducationDto updateEducation(UUID educationId, FreelancerEducationDto dto) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found"));

        validateEducationDates(dto);

        if (dto.institution() != null) education.setInstitution(dto.institution());
        if (dto.degree() != null) education.setDegree(dto.degree());
        if (dto.field() != null) education.setField(dto.field());
        if (dto.startDate() != null) education.setStartDate(dto.startDate());
        education.setEndDate(dto.isCurrent() != null && dto.isCurrent() ? null : dto.endDate());
        education.setCurrent(dto.isCurrent() != null && dto.isCurrent());
        if (dto.description() != null) education.setDescription(dto.description());

        education = educationRepository.save(education);
        return toEducationDto(education);
    }

    @Transactional
    public void deleteEducation(UUID educationId) {
        if (!educationRepository.existsById(educationId)) {
            throw new IllegalArgumentException("Education not found");
        }
        educationRepository.deleteById(educationId);
    }

    @Transactional
    public FreelancerWorkDto addWork(UUID freelancerId, FreelancerWorkDto dto) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        validateWorkDates(dto);

        Work work = Work.builder()
                .freelancer(freelancer)
                .title(dto.title())
                .company(dto.company())
                .type(dto.type())
                .startDate(dto.startDate())
                .endDate(dto.isCurrent() != null && dto.isCurrent() ? null : dto.endDate())
                .isCurrent(dto.isCurrent() != null && dto.isCurrent())
                .description(dto.description())
                .build();

        work = workRepository.save(work);
        return toWorkDto(work);
    }

    @Transactional
    public FreelancerWorkDto updateWork(UUID workId, FreelancerWorkDto dto) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));

        validateWorkDates(dto);

        if (dto.title() != null) work.setTitle(dto.title());
        if (dto.company() != null) work.setCompany(dto.company());
        if (dto.type() != null) work.setType(dto.type());
        if (dto.startDate() != null) work.setStartDate(dto.startDate());
        work.setEndDate(dto.isCurrent() != null && dto.isCurrent() ? null : dto.endDate());
        work.setCurrent(dto.isCurrent() != null && dto.isCurrent());
        if (dto.description() != null) work.setDescription(dto.description());

        work = workRepository.save(work);
        return toWorkDto(work);
    }

    @Transactional
    public void deleteWork(UUID workId) {
        if (!workRepository.existsById(workId)) {
            throw new IllegalArgumentException("Work not found");
        }
        workRepository.deleteById(workId);
    }

    @Transactional
    public FreelancerCertificationDto addCertification(UUID freelancerId, FreelancerCertificationDto dto) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        Certification certification = Certification.builder()
                .freelancer(freelancer)
                .title(dto.title())
                .organization(dto.organization())
                .date(dto.date())
                .expiration(dto.expiration())
                .identifier(dto.identifier())
                .build();

        certification = certificationRepository.save(certification);
        return toCertificationDto(certification);
    }

    @Transactional
    public FreelancerCertificationDto updateCertification(UUID certificationId, FreelancerCertificationDto dto) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("Certification not found"));

        if (dto.title() != null) certification.setTitle(dto.title());
        if (dto.organization() != null) certification.setOrganization(dto.organization());
        if (dto.date() != null) certification.setDate(dto.date());
        if (dto.expiration() != null) certification.setExpiration(dto.expiration());
        if (dto.identifier() != null) certification.setIdentifier(dto.identifier());

        certification = certificationRepository.save(certification);
        return toCertificationDto(certification);
    }

    @Transactional
    public void deleteCertification(UUID certificationId) {
        if (!certificationRepository.existsById(certificationId)) {
            throw new IllegalArgumentException("Certification not found");
        }
        certificationRepository.deleteById(certificationId);
    }

    // CV CRUD
    @Transactional(readOnly = true)
    public FreelancerCVDto getCV(UUID freelancerId) {
        UploadedCV cv = uploadedCVRepository.findFirstByFreelancer_IdOrderByUploadedAtDesc(freelancerId).orElse(null);
        return cv == null ? null : toCVDto(cv);
    }

    @Transactional
    public FreelancerCVDto saveOrReplaceCV(UUID freelancerId, FreelancerCVCreateDto dto) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        uploadedCVRepository.findFirstByFreelancer_IdOrderByUploadedAtDesc(freelancerId)
                .ifPresent(existing -> uploadedCVRepository.deleteById(existing.getId()));

        UploadedCV cv = UploadedCV.builder()
                .freelancer(freelancer)
                .attachmentUrl(dto.attachmentUrl())
                .attachmentName(dto.attachmentName())
                .sizeBytes(dto.sizeBytes())
                .build();
        cv = uploadedCVRepository.save(cv);
        return toCVDto(cv);
    }

    @Transactional
    public void deleteCV(UUID cvId, UUID freelancerId) {
        UploadedCV cv = uploadedCVRepository.findById(cvId)
                .orElseThrow(() -> new IllegalArgumentException("CV not found"));
        if (!cv.getFreelancer().getId().equals(freelancerId)) {
            throw new IllegalArgumentException("CV does not belong to freelancer");
        }
        uploadedCVRepository.deleteById(cvId);
    }

    private void validateEducationDates(FreelancerEducationDto dto) {
        if (dto.startDate() != null && dto.endDate() != null && !Boolean.TRUE.equals(dto.isCurrent())) {            if (dto.endDate().isBefore(dto.startDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }
    }

    private void validateWorkDates(FreelancerWorkDto dto) {
        if (dto.startDate() != null && dto.endDate() != null && !Boolean.TRUE.equals(dto.isCurrent())) {            if (dto.endDate().isBefore(dto.startDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }
    }

    private FreelancerEducationDto toEducationDto(Education education) {
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

    private FreelancerWorkDto toWorkDto(Work work) {
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

    private FreelancerCertificationDto toCertificationDto(Certification certification) {
        return new FreelancerCertificationDto(
                certification.getId(),
                certification.getTitle(),
                certification.getOrganization(),
                certification.getDate(),
                certification.getExpiration(),
                certification.getIdentifier()
        );
    }

    private FreelancerCVDto toCVDto(UploadedCV cv) {
        return new FreelancerCVDto(
                cv.getId(),
                cv.getAttachmentName(),
                cv.getAttachmentUrl(),
                cv.getUploadedAt()
        );
    }
}
