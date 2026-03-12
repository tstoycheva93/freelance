package com.thesis.freelance.features.posting.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.location.entity.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posting extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_posting_client")
    )
    private Client client;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID categoryId;

    @Column(name = "photo_url", length = 500)
    private String photoUrl = "";

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

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(
                name = "location_id",
                foreignKey = @ForeignKey(name = "fk_posting_location")
        )
        private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    private ExperienceLevel experienceLevel;


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