//package com.zekiloni.george.domain.gsm.repository;
//
//import com.zekiloni.george.domain.gsm.entity.GSMBoxLog;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * Repository interface for GSMBoxLog entity.
// */
//@Repository
//public interface GSMBoxLogRepository extends JpaRepository<GSMBoxLog, Long> {
//
//    /**
//     * Find all logs for a specific GSM box.
//     */
//    List<GSMBoxLog> findByGsmBoxConfigId(Long gsmBoxConfigId);
//
//    /**
//     * Find logs for a GSM box with pagination.
//     */
//    Page<GSMBoxLog> findByGsmBoxConfigIdOrderByCreatedAtDesc(Long gsmBoxConfigId, Pageable pageable);
//
//    /**
//     * Find logs by event type.
//     */
//    List<GSMBoxLog> findByGsmBoxConfigIdAndEventType(Long gsmBoxConfigId, String eventType);
//
//    /**
//     * Find logs within a date range.
//     */
//    List<GSMBoxLog> findByGsmBoxConfigIdAndCreatedAtBetween(
//        Long gsmBoxConfigId, LocalDateTime startDate, LocalDateTime endDate
//    );
//
//    /**
//     * Find error logs for a box.
//     */
//    List<GSMBoxLog> findByGsmBoxConfigIdAndEventTypeContains(Long gsmBoxConfigId, String eventType);
//
//    /**
//     * Count logs by event type.
//     */
//    long countByGsmBoxConfigIdAndEventType(Long gsmBoxConfigId, String eventType);
//
//    /**
//     * Get latest log for a box.
//     */
//    java.util.Optional<GSMBoxLog> findFirstByGsmBoxConfigIdOrderByCreatedAtDesc(Long gsmBoxConfigId);
//}
//
