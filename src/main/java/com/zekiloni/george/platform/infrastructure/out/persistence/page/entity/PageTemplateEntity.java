package com.zekiloni.george.platform.infrastructure.out.persistence.page.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Templates are global — every tenant sees the same library. We extend
 * {@link BaseEntity} (no {@code @TenantId}) on purpose so Hibernate's tenant
 * filter doesn't hide BUILTIN rows seeded by {@code DevPageTemplateLoader}
 * (those rows have no tenant context at startup).
 */
@Entity
@Table(name = "page_templates", uniqueConstraints = @UniqueConstraint(columnNames = {"title", "source"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"definition"})
public class PageTemplateEntity extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug")
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private TemplateSource source;

    @Column(name = "definition", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private PageDefinition definition;
}
