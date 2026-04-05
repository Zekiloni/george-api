//package com.zekiloni.george.domain.link.repository;
//
//import com.zekiloni.george.domain.link.TrackingLink;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Repository for TrackingLink entity.
// * Provides database operations for tracking links.
// */
//@Repository
//public interface TrackingLinkRepository extends JpaRepository<TrackingLink, Long> {
//
//    /**
//     * Find tracking link by unique token
//     */
//    Optional<TrackingLink> findByToken(String token);
//
//    /**
//     * Find all tracking links for a specific lead
//     */
//    List<TrackingLink> findByLeadId(Long leadId);
//
//    /**
//     * Find all tracking links for a specific campaign
//     */
//    List<TrackingLink> findByCampaignId(Long campaignId);
//
//    /**
//     * Find all clicked links in a campaign
//     */
//    @Query("SELECT tl FROM TrackingLink tl WHERE tl.campaign.id = :campaignId AND tl.isClicked = true")
//    List<TrackingLink> findClickedLinksByCampaignId(@Param("campaignId") Long campaignId);
//
//    /**
//     * Find all unclicked links in a campaign
//     */
//    @Query("SELECT tl FROM TrackingLink tl WHERE tl.campaign.id = :campaignId AND tl.isClicked = false")
//    List<TrackingLink> findUnclickedLinksByCampaignId(@Param("campaignId") Long campaignId);
//
//    /**
//     * Find active tracking links
//     */
//    @Query("SELECT tl FROM TrackingLink tl WHERE tl.isActive = true AND tl.expiresAt > :now")
//    List<TrackingLink> findActiveLinks(@Param("now") LocalDateTime now);
//
//    /**
//     * Find expired tracking links
//     */
//    @Query("SELECT tl FROM TrackingLink tl WHERE tl.expiresAt IS NOT NULL AND tl.expiresAt <= :now")
//    List<TrackingLink> findExpiredLinks(@Param("now") LocalDateTime now);
//
//    /**
//     * Find tracking links clicked within a date range
//     */
//    @Query("SELECT tl FROM TrackingLink tl WHERE tl.lastClickedAt BETWEEN :startDate AND :endDate")
//    List<TrackingLink> findLinksByClickDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
//
//    /**
//     * Count total clicks for a campaign
//     */
//    @Query("SELECT SUM(tl.clickCount) FROM TrackingLink tl WHERE tl.campaign.id = :campaignId")
//    Long getTotalClicksByCampaignId(@Param("campaignId") Long campaignId);
//
//    /**
//     * Find tracking link with its associated form submission
//     */
//    @Query("SELECT tl FROM TrackingLink tl LEFT JOIN FETCH tl.formSubmission WHERE tl.id = :id")
//    Optional<TrackingLink> findByIdWithFormSubmission(@Param("id") Long id);
//}
//
