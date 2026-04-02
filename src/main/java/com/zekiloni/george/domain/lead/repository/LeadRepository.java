package com.zekiloni.george.domain.lead.repository;

import com.zekiloni.george.domain.lead.Lead;
import com.zekiloni.george.domain.lead.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Lead entity.
 * Provides database operations for leads.
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    /**
     * Find all leads by campaign ID
     */
    List<Lead> findByCampaignId(Long campaignId);

    /**
     * Find all leads by campaign ID and status
     */
    List<Lead> findByCampaignIdAndStatus(Long campaignId, LeadStatus status);

    /**
     * Find lead by phone number
     */
    Optional<Lead> findByPhoneNumber(String phoneNumber);

    /**
     * Find leads by campaign and phone number
     */
    Optional<Lead> findByCampaignIdAndPhoneNumber(Long campaignId, String phoneNumber);

    /**
     * Find all active leads in a campaign
     */
    @Query("SELECT l FROM Lead l WHERE l.campaign.id = :campaignId AND l.status IN ('NEW', 'INVITED', 'ENGAGED', 'SUBMITTED')")
    List<Lead> findActiveLessByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * Find all converted leads in a campaign
     */
    @Query("SELECT l FROM Lead l WHERE l.campaign.id = :campaignId AND l.status = 'CONVERTED'")
    List<Lead> findConvertedLeadsByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * Find all bounced leads in a campaign
     */
    @Query("SELECT l FROM Lead l WHERE l.campaign.id = :campaignId AND l.status = 'BOUNCED'")
    List<Lead> findBouncedLeadsByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * Find leads by engagement date range
     */
    @Query("SELECT l FROM Lead l WHERE l.lastEngagedAt BETWEEN :startDate AND :endDate")
    List<Lead> findLeadsByEngagementDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count leads by status in a campaign
     */
    long countByCampaignIdAndStatus(Long campaignId, LeadStatus status);

    /**
     * Find leads that haven't been engaged yet
     */
    @Query("SELECT l FROM Lead l WHERE l.campaign.id = :campaignId AND l.lastEngagedAt IS NULL")
    List<Lead> findUnengagedLeadsByCampaignId(@Param("campaignId") Long campaignId);
}

