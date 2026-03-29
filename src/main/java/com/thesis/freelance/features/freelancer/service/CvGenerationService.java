package com.thesis.freelance.features.freelancer.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.certification.entity.Certification;
import com.thesis.freelance.features.certification.repository.CertificationRepository;
import com.thesis.freelance.features.education.entity.Education;
import com.thesis.freelance.features.education.repository.EducationRepository;
import com.thesis.freelance.features.freelancer.dto.FreelancerCVDto;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.features.uploadedCV.entity.UploadedCV;
import com.thesis.freelance.features.uploadedCV.repository.UploadedCVRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.work.entity.Work;
import com.thesis.freelance.features.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CvGenerationService {

    private final FreelancerRepository freelancerRepository;
    private final UserRepository userRepository;
    private final FreelancerSkillsRepository freelancerSkillsRepository;
    private final WorkRepository workRepository;
    private final EducationRepository educationRepository;
    private final CertificationRepository certificationRepository;
    private final UploadedCVRepository uploadedCVRepository;

    @Transactional
    public byte[] generateCvPdf(UUID freelancerId) throws IOException {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found: " + freelancerId));

        User user = userRepository.findById(freelancer.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + freelancer.getUserId()));

        List<String> skills = freelancerSkillsRepository.findByFreelancerIdWithSkill(freelancerId, ActivityStatus.ACTIVE)
                .stream()
                .map(fs -> fs.getSkill().getSkill())
                .toList();

        List<Work> work = workRepository.findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(freelancerId, ActivityStatus.ACTIVE);

        List<Education> education = educationRepository.findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(freelancerId, ActivityStatus.ACTIVE);

        List<Certification> certifications = certificationRepository.findAllByFreelancer_IdOrderByDateDesc(freelancerId, ActivityStatus.ACTIVE);

        Map<String, Object> data = new HashMap<>();
        data.put("firstName", getOrEmpty(user.getName()));
        data.put("lastName", getOrEmpty(user.getSurname()));
        data.put("photoUrl", getOrEmpty(user.getPhotoUrl()));
        data.put("email", getOrEmpty(user.getEmail()));
        data.put("phone", getOrEmpty(user.getPhone()));
        data.put("location", user.getLocation() != null ? getOrEmpty(user.getLocation().getName()) : "");
        data.put("title", getOrEmpty(freelancer.getTitle()));
        data.put("experienceLevel", freelancer.getExperience() != null ? freelancer.getExperience().getValue() : "");
        data.put("website", getOrEmpty(freelancer.getWebsite()));
        data.put("description", getOrEmpty(freelancer.getBio()));
        data.put("skills", skills);
        data.put("work", work.stream().map(this::toWorkMap).toList());
        data.put("education", education.stream().map(this::toEducationMap).toList());
        data.put("certifications", certifications.stream().map(this::toCertMap).toList());
        data.put("initials", getInitials(user.getName(), user.getSurname()));

        log.info("CV data prepared - skills: {}, work: {}, education: {}, certifications: {}", 
            skills.size(), work.size(), education.size(), certifications.size());

        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/templates", ".html"));
        Template template = handlebars.compile("resume/resume");
        String html = template.apply(data);
        log.info("HTML template compiled, length: {}", html.length());

        return generatePdfFromHtml(html);
    }

    private Map<String, Object> toWorkMap(Work work) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", getOrEmpty(work.getTitle()));
        map.put("company", getOrEmpty(work.getCompany()));
        map.put("type", getOrEmpty(work.getType()));
        map.put("startDate", formatDate(work.getStartDate()));
        map.put("endDate", work.isCurrent() ? "Present" : formatDate(work.getEndDate()));
        map.put("description", getOrEmpty(work.getDescription()));
        return map;
    }

    private Map<String, Object> toEducationMap(Education edu) {
        Map<String, Object> map = new HashMap<>();
        map.put("degree", getOrEmpty(edu.getDegree()));
        map.put("field", getOrEmpty(edu.getField()));
        map.put("institution", getOrEmpty(edu.getInstitution()));
        map.put("startDate", formatDate(edu.getStartDate()));
        map.put("endDate", edu.isCurrent() ? "Present" : formatDate(edu.getEndDate()));
        map.put("description", getOrEmpty(edu.getDescription()));
        return map;
    }

    private Map<String, Object> toCertMap(Certification cert) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", getOrEmpty(cert.getTitle()));
        map.put("organization", getOrEmpty(cert.getOrganization()));
        map.put("date", formatDate(cert.getDate()));
        map.put("expiration", cert.getExpiration() != null ? formatDate(cert.getExpiration()) : "No expiration");
        map.put("identifier", getOrEmpty(cert.getIdentifier()));
        return map;
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.toString();
    }

    private String getOrEmpty(String value) {
        return value != null ? value : "";
    }

    private String getInitials(String name, String surname) {
        StringBuilder sb = new StringBuilder();
        if (name != null && !name.isEmpty()) sb.append(name.charAt(0));
        if (surname != null && !surname.isEmpty()) sb.append(surname.charAt(0));
        return sb.toString().toUpperCase();
    }

    private byte[] generatePdfFromHtml(String html) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder = new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
            builder.withHtmlContent(html, "");
            builder.toStream(baos);
            builder.run();
            return baos.toByteArray();
        }
    }

    public String generateCvFileName(String firstName, String surname) {
        String name = (firstName != null ? firstName : "") + "_" + (surname != null ? surname : "");
        name = name.replaceAll("[^a-zA-Z0-9_]", "");
        if (name.equals("_")) {
            name = "TalentFlowCV";
        }
        return name + "_TalentFlowCV.pdf";
    }

}
