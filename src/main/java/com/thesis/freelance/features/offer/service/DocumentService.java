package com.thesis.freelance.features.offer.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final UserRepository userRepository;

    public byte[] generateContract(Offer offer, boolean freelancerSigned, boolean clientSigned, java.time.LocalDateTime clientSignedAt) {
        try {
            Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/templates", ".html"));
            Template template = handlebars.compile("legal/contract");
            
            User freelancerUser = userRepository.findById(offer.getFreelancer().getUserId()).orElseThrow();
            User clientUser = userRepository.findById(offer.getPosting().getClient().getUserId()).orElseThrow();
            
            Map<String, Object> data = new HashMap<>();
            data.put("offerId", offer.getId());
            data.put("postingTitle", offer.getPosting().getTitle());
            data.put("postingDescription", offer.getPosting().getDescription());
            data.put("price", offer.getPrice());
            data.put("deliveryDate", offer.getDeliveryTime());
            data.put("freelancerName", freelancerUser.getName() + " " + freelancerUser.getSurname());
            data.put("clientName", clientUser.getName() + " " + clientUser.getSurname());
            data.put("date", LocalDate.now().toString());
            data.put("freelancerSigned", freelancerSigned);
            data.put("clientSigned", clientSigned);
            if (clientSignedAt != null) {
                data.put("clientSignedAt", clientSignedAt.toLocalDate().toString());
            }

            String html = template.apply(data);
            return generatePdfFromHtml(html);
        } catch (Exception e) {
            log.error("Failed to generate contract PDF", e);
            throw new RuntimeException("Failed to generate contract PDF", e);
        }
    }

    public byte[] generateReceipt(Offer offer, BigDecimal settledAmount) {
        try {
            Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/templates", ".html"));
            Template template = handlebars.compile("legal/receipt");
            
            User freelancerUser = userRepository.findById(offer.getFreelancer().getUserId()).orElseThrow();
            User clientUser = userRepository.findById(offer.getPosting().getClient().getUserId()).orElseThrow();
            
            Map<String, Object> data = new HashMap<>();
            data.put("offerId", offer.getId());
            data.put("postingTitle", offer.getPosting().getTitle());
            data.put("settledAmount", settledAmount != null ? settledAmount : offer.getPrice());
            data.put("freelancerName", freelancerUser.getName() + " " + freelancerUser.getSurname());
            data.put("clientName", clientUser.getName() + " " + clientUser.getSurname());
            data.put("date", LocalDate.now().toString());

            String html = template.apply(data);
            return generatePdfFromHtml(html);
        } catch (Exception e) {
            log.error("Failed to generate receipt PDF", e);
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }
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
}
