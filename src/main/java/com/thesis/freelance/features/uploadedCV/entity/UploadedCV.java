package com.thesis.freelance.features.uploadedCV.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
        name = "uploaded_cv",
        indexes = {
                @Index(name = "idx_uploaded_cv_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_uploaded_cv_uploaded_at", columnList = "uploaded_at")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UploadedCV extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "freelancer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_uploaded_cv_freelancer")
    )
    private Freelancer freelancer;

    @Column(name = "attachment_url", nullable = false, length = 1000)
    private String attachmentUrl;

    @Column(name = "attachment_name", nullable = false, length = 255)
    private String attachmentName;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_data", columnDefinition = "json", nullable = false)
    private Map<String, Object> extractedData;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
        if (extractedData == null) extractedData = Map.of();
    }
}

