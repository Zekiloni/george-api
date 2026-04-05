//package com.zekiloni.george.domain.campaign.repository;
//
//import com.zekiloni.george.domain.campaign.Campaign;
//import com.zekiloni.george.domain.campaign.CampaignStatus;
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
// * Repository for Campaign entity.
// * Provides database operations for campaigns.
// */
//@Repository
//public interface CampaignRepository extends JpaRepository<Campaign, Long> {
//
//    /**
//     * Find all campaigns by owner ID
//     */
//    List<Campaign> findByOwnerId(Long ownerId);
//
//    /**
//     * Find all campaigns by owner ID and status
//     */
//    List<Campaign> findByOwnerIdAndStatus(Long ownerId, CampaignStatus status);
//
//    /**
//     * Find campaigns by name
//     */
//    Optional<Campaign> findByNameAndOwnerId(String name, Long ownerId);
//
//    /**
//     * Find all active campaigns for an owner
//     */
//    @Query("SELECT c FROM Campaign c WHERE c.ownerId = :ownerId AND c.status = 'ACTIVE'")
//    List<Campaign> findActiveCampaignsByOwnerId(@Param("ownerId") Long ownerId);
//
//    /**
//     * Find all campaigns that have started
//     */
//    @Query("SELECT c FROM Campaign c WHERE c.startedAt IS NOT NULL AND c.endedAt IS NULL")
//    List<Campaign> findAllRunningCampaigns();
//
//    /**
//     * Find campaigns by date range
//     */
//    @Query("SELECT c FROM Campaign c WHERE c.createdAt BETWEEN :startDate AND :endDate")
//    List<Campaign> findCampaignsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
//}
//
