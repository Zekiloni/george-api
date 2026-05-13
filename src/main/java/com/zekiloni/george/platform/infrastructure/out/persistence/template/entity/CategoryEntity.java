package com.zekiloni.george.platform.infrastructure.out.persistence.template.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.util.UUID;

/**
 * Category entity for the template library taxonomy. No @TenantId — these
 * are system-wide. parent_id is a self-reference for arbitrary-depth trees;
 * children are loaded by separate query rather than @OneToMany so the JPA
 * model stays flat and tree assembly happens in the service layer.
 */
@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(name = "category_slug_unique_per_parent", columnNames = {"parent_id", "slug"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.TIMESTAMP)
public class CategoryEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
