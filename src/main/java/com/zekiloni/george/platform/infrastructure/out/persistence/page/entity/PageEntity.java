package com.zekiloni.george.platform.infrastructure.out.persistence.page.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.platform.domain.model.page.PageStatus;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"definition"})
public class PageEntity extends TenantEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PageStatus status;

    @Column(name = "definition", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private PageDefinition definition;
}