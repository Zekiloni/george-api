package com.zekiloni.george.domain.link;

import com.zekiloni.george.domain.campaign.Campaign;
import com.zekiloni.george.domain.form.entity.FormSubmission;
import com.zekiloni.george.domain.lead.Lead;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Entity representing a tracking link.
 * Tracking links are unique URLs assigned to leads to track their engagement
 * and form submissions.
 */
@Entity
@Table(name = "tracking_links", indexes = {
    @Index(name = "idx_token", columnList = "token", unique = true),
    @Index(name = "idx_lead_id", columnList = "lead_id"),
    @Index(name = "idx_campaign_id", columnList = "campaign_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"lead", "campaign", "formSubmission"})
public class TrackingLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique token for this tracking link
     * Generated during link creation
     */
    @Column(nullable = false, unique = true, length = 128)
    private String token;

    /**
     * Lead associated with this tracking link
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    /**
     * Campaign associated with this tracking link
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    /**
     * Short URL version of the tracking link
     * Can be used for SMS distribution
     */
    @Column(name = "short_url", nullable = false)
    private String shortUrl;

    /**
     * Full tracking URL
     */
    @Column(name = "full_url", columnDefinition = "TEXT", nullable = false)
    private String fullUrl;

    /**
     * Number of times this link has been clicked
     */
    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    /**
     * Timestamp of the last click
     */
    @Column(name = "last_clicked_at")
    private LocalDateTime lastClickedAt;

    /**
     * Form submission associated with this tracking link (if any)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_submission_id")
    private FormSubmission formSubmission;

    /**
     * Whether this link is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Whether this link has been clicked
     */
    @Column(name = "is_clicked", nullable = false)
    @Builder.Default
    private Boolean isClicked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Metadata about the link (user agent, IP address on click, etc.)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Helper method to record a click on this link
     */
    public void recordClick() {
        this.clickCount++;
        this.lastClickedAt = LocalDateTime.now();
        this.isClicked = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Helper method to check if link is expired
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Helper method to check if link is valid for use
     */
    public boolean isValid() {
        return isActive && !isExpired();
    }
}

