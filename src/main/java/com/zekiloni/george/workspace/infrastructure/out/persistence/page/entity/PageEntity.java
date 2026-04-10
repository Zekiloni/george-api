package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pages")
public class PageEntity extends TenantEntity {

}