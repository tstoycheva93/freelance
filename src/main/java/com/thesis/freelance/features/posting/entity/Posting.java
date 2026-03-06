package com.thesis.freelance.features.posting.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.PostingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(    name = "posting",
        indexes = {
                @Index(name = "idx_posting_client_id", columnList = "client_id"),
                @Index(name = "idx_posting_category_id", columnList = "category_id"),
                @Index(name = "idx_posting_status", columnList = "status"),
                @Index(name = "idx_posting_created_at", columnList = "created_at"),
                @Index(name = "idx_posting_deadline", columnList = "deadline"),
                @Index(name = "idx_posting_status_created", columnList = "status, created_at")
        }
)
@Getter
@Setter
public class Posting extends BaseEntity {

    @Column(name = "client_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID clientId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID categoryId;

    @Column(name = "budget", nullable = false)
    private Integer budget;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostingStatus status;

    @Column(name = "views_count", nullable = false)
    private Long viewsCount = 0L;

    // Helper methods

    public void incrementViews() {
        this.viewsCount++;
    }

    //<-------------------- ATTACHMENTS ----------------------->

    @ElementCollection
    @CollectionTable(
            name = "posting_attachments",
            joinColumns = @JoinColumn(name = "posting_id")
    )
    @Column(name = "attachment_url", length = 255)
    private List<String> attachmentUrls = new ArrayList<>();

}