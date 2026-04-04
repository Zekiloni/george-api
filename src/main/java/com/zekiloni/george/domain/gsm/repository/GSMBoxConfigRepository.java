//package com.zekiloni.george.domain.gsm.repository;
//
//import com.zekiloni.george.domain.gsm.entity.GSMBoxConfig;
//import com.zekiloni.george.domain.gsm.entity.GSMBoxStatus;
//import com.zekiloni.george.domain.gsm.entity.GSMBoxType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Repository interface for GSMBoxConfig entity.
// * Provides CRUD operations and custom queries for GSM box configurations.
// */
//@Repository
//public interface GSMBoxConfigRepository extends JpaRepository<GSMBoxConfig, Long> {
//
//    /**
//     * Find a GSM box by its unique box name.
//     */
//    Optional<GSMBoxConfig> findByBoxName(String boxName);
//
//    /**
//     * Find all active GSM boxes.
//     */
//    List<GSMBoxConfig> findByIsActiveTrue();
//
//    /**
//     * Find all GSM boxes by type.
//     */
//    List<GSMBoxConfig> findByBoxType(GSMBoxType boxType);
//
//    /**
//     * Find all GSM boxes by status.
//     */
//    List<GSMBoxConfig> findByStatus(GSMBoxStatus status);
//
//    /**
//     * Find all online GSM boxes.
//     */
//    List<GSMBoxConfig> findByStatusAndIsActiveTrue(GSMBoxStatus status);
//
//    /**
//     * Find GSM box by IMEI.
//     */
//    Optional<GSMBoxConfig> findByImei(String imei);
//
//    /**
//     * Find GSM box by phone number.
//     */
//    Optional<GSMBoxConfig> findByPhoneNumber(String phoneNumber);
//
//    /**
//     * Find GSM box by IP address.
//     */
//    Optional<GSMBoxConfig> findByIpAddress(String ipAddress);
//
//    /**
//     * Check if box name exists.
//     */
//    boolean existsByBoxName(String boxName);
//
//    /**
//     * Check if IMEI already exists.
//     */
//    boolean existsByImei(String imei);
//
//    /**
//     * Get count of active boxes.
//     */
//    long countByIsActiveTrue();
//
//    /**
//     * Get count of online boxes.
//     */
//    long countByStatus(GSMBoxStatus status);
//}
//
