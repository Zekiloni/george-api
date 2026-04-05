package com.zekiloni.george.workspace.domain.campaign;

import com.zekiloni.george.workspace.domain.campaign.form.entity.FormConfig;
import com.zekiloni.george.workspace.domain.link.TrackingLink;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a marketing campaign.
 * Campaigns are used to organize leads, forms, and tracking links.
 * Each campaign belongs to a platform user (owner/admin).
 */
@Entity
@Table(name = "campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"trackingLinks"})
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner of the campaign (admin or user who created it)
     * Will reference PlatformUser when it's created
     */
    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Message template that will be sent to leads
     */
    @Column(columnDefinition = "TEXT")
    private String messageTemplate;

    /**
     * Associated form configuration for this campaign
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_config_id", nullable = true)
    private FormConfig formConfig;

    /**
     * Status of the campaign
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status = CampaignStatus.DRAFT;

    /**
     * List of tracking links associated with this campaign
     */
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TrackingLink> trackingLinks = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

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
     * Helper method to check if campaign can be launched
     */
    public boolean canLaunch() {
        return status == CampaignStatus.DRAFT || status == CampaignStatus.PAUSED;
    }

    /**
     * Helper method to check if campaign is running
     */
    public boolean isRunning() {
        return status == CampaignStatus.ACTIVE;
    }

    /**
     * Helper method to check if campaign is editable
     */
    public boolean isEditable() {
        return status.isEditable();
    }
}

