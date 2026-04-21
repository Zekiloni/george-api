package com.zekiloni.george.provisioning.infrastructure.out.persistence.inventory.entity;

import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.PageEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "page_service_access")
public class PageServiceAccessEntity extends ServiceAccessEntity {
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "page_service_access_pages",
            joinColumns = @JoinColumn(name = "page_access_id"),
            inverseJoinColumns = @JoinColumn(name = "page_id")
    )
    private Set<PageEntity> page  = new java.util.HashSet<>();

    @Column(name = "max_concurrent")
    private Integer maxConcurrent;
}
