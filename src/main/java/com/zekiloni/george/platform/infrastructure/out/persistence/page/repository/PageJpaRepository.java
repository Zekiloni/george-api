package com.zekiloni.george.platform.infrastructure.out.persistence.page.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PageJpaRepository extends JpaRepository<PageEntity, UUID> {

    Optional<PageEntity> findBySlug(String slug);

    Optional<PageEntity> findBySlugAndTenantId(String slug, String tenantId);

    /**
     * Tenant-agnostic slug lookup. The {@code slug} column has a global unique
     * constraint so this can't collide across tenants. Used by the anonymous
     * preview/render path ({@code GET /api/v1/page/slug/{slug}}) where the
     * caller has no JWT — Hibernate's {@code @TenantId} auto-filter would
     * otherwise return empty.
     */
    @Query(value = "SELECT * FROM pages WHERE slug = :slug LIMIT 1", nativeQuery = true)
    Optional<PageEntity> findBySlugAcrossTenants(@Param("slug") String slug);
}
