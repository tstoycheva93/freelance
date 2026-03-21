package com.thesis.freelance.features.notification.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.NotificationType;
import com.thesis.freelance.features.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 255)
    private String pageUrl;

    @Column(nullable = false)
    private boolean isRead = false;

}
