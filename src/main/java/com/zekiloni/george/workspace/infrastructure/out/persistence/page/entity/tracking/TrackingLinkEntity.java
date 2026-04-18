package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.tracking;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tracking_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"formSubmission"})
public class TrackingLinkEntity extends TenantEntity {

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "short_code", unique = true)
    private String shortCode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "click_count")
    private Integer clickCount = 0;

    @Column(name = "last_clicked_at")
    private java.time.OffsetDateTime lastClickedAt;

    @Column(name = "expires_at")
    private java.time.OffsetDateTime expiresAt;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
}

