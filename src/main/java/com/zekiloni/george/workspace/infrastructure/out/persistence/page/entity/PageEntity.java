package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.workspace.domain.page.PageStatus;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormConfigEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"formConfig"})
public class PageEntity extends TenantEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PageStatus status;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "form_config_id")
    private FormConfigEntity formConfig;
}