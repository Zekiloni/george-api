package com.zekiloni.george.platform.infrastructure.out.persistence.template.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.platform.domain.model.template.TemplateStep;
import com.zekiloni.george.platform.domain.model.template.TemplateVariable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Template entity. Deliberately does NOT extend {@code TenantEntity} because
 * the standard {@code @TenantId} filter rejects rows with a NULL tenant_id —
 * and public catalog templates always have NULL tenant_id. Templates need a
 * custom visibility rule applied at the repository level instead:
 *
 *   WHERE category_id IS NOT NULL          -- public to everyone
 *      OR tenant_id = :currentTenant       -- this tenant's private templates
 *
 * The ownership invariant (categoryId XOR tenantId) is enforced by a DB
 * CHECK constraint declared in V3, so it's impossible to wedge a row into
 * neither bucket or both.
 */
@Entity
@Table(name = "template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.TIMESTAMP)
public class TemplateEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "steps", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private List<TemplateStep> steps = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private List<TemplateVariable> variables = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private int version = 1;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private long usageCount = 0;

    /** Non-null for public catalog templates. */
    @Column(name = "category_id")
    private UUID categoryId;

    /**
     * Non-null for tenant-owned (private) templates. Plain column — not
     * managed by Hibernate's multi-tenancy filter — because public templates
     * have it NULL and standard {@code @TenantId} would hide them from every
     * tenant including SYSTEM.
     */
    @Column(name = "tenant_id")
    private String tenantId;
}
