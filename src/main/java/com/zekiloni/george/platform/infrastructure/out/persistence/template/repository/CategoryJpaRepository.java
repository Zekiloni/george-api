package com.zekiloni.george.platform.infrastructure.out.persistence.template.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.template.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    /** All categories ordered for stable tree assembly. parent_id NULLs first (roots). */
    @Query("""
            SELECT c FROM CategoryEntity c
            ORDER BY c.parentId NULLS FIRST, c.sortOrder, c.name
            """)
    List<CategoryEntity> findAllOrdered();

    Optional<CategoryEntity> findByParentIdAndSlug(UUID parentId, String slug);

    /** Counts non-deleted templates referencing the given category — soft-delete is filtered by Hibernate. */
    @Query("SELECT COUNT(t) FROM TemplateEntity t WHERE t.categoryId = :categoryId")
    long countTemplates(@Param("categoryId") UUID categoryId);
}
