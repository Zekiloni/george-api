package com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leads")
@FilterDef(
        name = "tenantAccessFilter",
        parameters = @ParamDef(name = "tenantId", type = String.class)
)
@Filter(
        name = "tenantAccessFilter",
        condition = "id IN (SELECT lsal.lead_id FROM lead_service_access_leads lsal " +
                "JOIN service_access sa ON sa.id = lsal.service_access_id " +
                "WHERE sa.tenant_id = :tenantId)"
)
public class LeadEntity extends BaseEntity {
    @Column
    private String country;

    @Column
    private String areaCode;

    @Column
    private String regionCode;

    @Column
    private String carrier;

    @Column
    private String location;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
}
