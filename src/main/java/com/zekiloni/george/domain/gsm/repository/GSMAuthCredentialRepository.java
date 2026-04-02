package com.zekiloni.george.domain.gsm.repository;

import com.zekiloni.george.domain.gsm.entity.GSMAuthCredential;
import com.zekiloni.george.domain.gsm.entity.GSMAuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for GSMAuthCredential entity.
 */
@Repository
public interface GSMAuthCredentialRepository extends JpaRepository<GSMAuthCredential, Long> {

    /**
     * Find all credentials for a specific GSM box.
     */
    List<GSMAuthCredential> findByGsmBoxConfigId(Long gsmBoxConfigId);

    /**
     * Find active credentials for a GSM box.
     */
    List<GSMAuthCredential> findByGsmBoxConfigIdAndIsActiveTrue(Long gsmBoxConfigId);

    /**
     * Find primary credential for a GSM box.
     */
    Optional<GSMAuthCredential> findByGsmBoxConfigIdAndIsPrimaryTrue(Long gsmBoxConfigId);

    /**
     * Find credentials by authentication type.
     */
    List<GSMAuthCredential> findByGsmBoxConfigIdAndAuthType(Long gsmBoxConfigId, GSMAuthType authType);

    /**
     * Find credential by name for a box.
     */
    Optional<GSMAuthCredential> findByGsmBoxConfigIdAndCredentialName(Long gsmBoxConfigId, String credentialName);

    /**
     * Count active credentials.
     */
    long countByGsmBoxConfigIdAndIsActiveTrue(Long gsmBoxConfigId);

    /**
     * Check if credential name exists for a box.
     */
    boolean existsByGsmBoxConfigIdAndCredentialName(Long gsmBoxConfigId, String credentialName);
}

