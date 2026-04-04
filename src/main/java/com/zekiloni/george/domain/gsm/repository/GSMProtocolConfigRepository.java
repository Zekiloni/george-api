//package com.zekiloni.george.domain.gsm.repository;
//
//import com.zekiloni.george.domain.gsm.entity.GSMProtocolConfig;
//import com.zekiloni.george.domain.gsm.entity.GSMProtocol;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * Repository interface for GSMProtocolConfig entity.
// */
//@Repository
//public interface GSMProtocolConfigRepository extends JpaRepository<GSMProtocolConfig, Long> {
//
//    /**
//     * Find all protocols for a specific GSM box.
//     */
//    List<GSMProtocolConfig> findByGsmBoxConfigId(Long gsmBoxConfigId);
//
//    /**
//     * Find enabled protocols for a GSM box.
//     */
//    List<GSMProtocolConfig> findByGsmBoxConfigIdAndIsEnabledTrue(Long gsmBoxConfigId);
//
//    /**
//     * Find a specific protocol for a GSM box.
//     */
//    java.util.Optional<GSMProtocolConfig> findByGsmBoxConfigIdAndProtocol(Long gsmBoxConfigId, GSMProtocol protocol);
//
//    /**
//     * Find protocols ordered by priority.
//     */
//    List<GSMProtocolConfig> findByGsmBoxConfigIdOrderByPriorityDesc(Long gsmBoxConfigId);
//
//    /**
//     * Count enabled protocols for a box.
//     */
//    long countByGsmBoxConfigIdAndIsEnabledTrue(Long gsmBoxConfigId);
//}
//
