package com.zekiloni.george.platform.infrastructure.out.persistence.page.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPARepository za PageEntity.
 * Omogućava čuvanje i čitanje stranica iz baze podataka.
 */
@Repository
public interface PageJpaRepository extends JpaRepository<PageEntity, UUID> {

    /**
     * Pronalazi stranicu po URL-friendly slug-u.
     */
    Optional<PageEntity> findBySlug(String slug);

    /**
     * Pronalazi stranicu po slug-u i tenant ID-u.
     */
    Optional<PageEntity> findBySlugAndTenantId(String slug, String tenantId);
}
