package com.zekiloni.george.platform.infrastructure.out.persistence.template.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.template.entity.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TemplateJpaRepository extends JpaRepository<TemplateEntity, UUID> {

    /**
     * Template-library search. The visibility predicate is:
     *   (categoryId IS NOT NULL)          -- public catalog
     *   OR (tenantId = :currentTenant)    -- this tenant's private locker
     *
     * When {@code ownedOnly} is true the public branch is dropped; when
     * {@code categoryId} is supplied the owned branch is dropped (private
     * templates have no category).
     */
    @Query("""
            SELECT t FROM TemplateEntity t
            WHERE
              (
                (:ownedOnly = false AND :categoryId IS NULL AND (t.categoryId IS NOT NULL OR t.tenantId = :currentTenant))
                OR
                (:ownedOnly = false AND :categoryId IS NOT NULL AND t.categoryId = :categoryId)
                OR
                (:ownedOnly = true  AND t.tenantId = :currentTenant)
              )
              AND (CAST(:query AS string) IS NULL
                   OR LOWER(t.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')))
            """)
    Page<TemplateEntity> search(@Param("currentTenant") String currentTenant,
                                @Param("query") String query,
                                @Param("categoryId") UUID categoryId,
                                @Param("ownedOnly") boolean ownedOnly,
                                Pageable pageable);

    /** Atomic counter bump — avoids the read-modify-write race when popular templates are instantiated concurrently. */
    @Modifying
    @Query("UPDATE TemplateEntity t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsage(@Param("id") UUID id);
}
