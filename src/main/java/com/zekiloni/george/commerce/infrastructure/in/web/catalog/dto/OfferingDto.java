package com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.config.jackson.ChronoUnitFlexibleDeserializer;
import com.zekiloni.george.common.infrastructure.config.jackson.ChronoUnitNameSerializer;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.TierMode;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic.CharacteristicSpecificationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferingDto {
    private String id;
    private String name;
    private String description;
    private String identifier;
    private ServiceSpecification serviceSpecification;
    private OfferingStatus status;

    private Money unitAmount;
    private String unitLabel;
    @JsonDeserialize(using = ChronoUnitFlexibleDeserializer.class)
    @JsonSerialize(using = ChronoUnitNameSerializer.class)
    private ChronoUnit timeUnit;
    private int intervalCount;
    private Integer minUnits;
    private Integer maxUnits;
    private TierMode tierMode;
    private List<DiscountTierDto> tiers;

    private List<CharacteristicSpecificationDto> characteristicSpecification;

    private Money startingPrice;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
