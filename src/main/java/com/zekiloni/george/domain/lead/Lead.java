package com.zekiloni.george.domain.lead;

import com.zekiloni.george.domain.campaign.Campaign;
import com.zekiloni.george.domain.link.TrackingLink;
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
 * Entity representing a lead in a campaign.
 * A lead is a target contact that can be engaged through the campaign.
 */
@Entity
@Table(name = "leads", indexes = {
    @Index(name = "idx_phone_number", columnList = "phone_number"),
    @Index(name = "idx_campaign_id", columnList = "campaign_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"campaign", "trackingLinks"})
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    /**
     * Campaign this lead belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    /**
     * Status of the lead
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status = LeadStatus.NEW;

    /**
     * Tracking links associated with this lead
     */
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TrackingLink> trackingLinks = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_engaged_at")
    private LocalDateTime lastEngagedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    /**
     * Additional metadata about the lead
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
     * Helper method to check if lead is active
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * Helper method to check if lead can be converted
     */
    public boolean canConvert() {
        return status.isConvertible();
    }

    /**
     * Helper method to mark lead as engaged
     */
    public void markEngaged() {
        this.lastEngagedAt = LocalDateTime.now();
        if (this.status == LeadStatus.NEW) {
            this.status = LeadStatus.ENGAGED;
        }
    }

    /**
     * Helper method to mark lead as submitted
     */
    public void markSubmitted() {
        this.submittedAt = LocalDateTime.now();
        this.status = LeadStatus.SUBMITTED;
    }

    /**
     * Helper method to mark lead as converted
     */
    public void markConverted() {
        this.convertedAt = LocalDateTime.now();
        this.status = LeadStatus.CONVERTED;
    }
}

