package com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leads")
public class LeadEntity extends BaseEntity {
    @Column
    private String country;

    @Column
    private String areaCode;

    @Column
    private String regionCode;

    @Column
    private String location;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
}
