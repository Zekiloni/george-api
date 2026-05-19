package com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicSpecification {
    private String id;
    private String name;
    private String description;
    private String valueType;
    private Boolean configurable;
    private Boolean extensible;
    private Boolean isUnique;
    private Integer maxCardinality;
    private Integer minCardinality;
    private String regex;
    private TimePeriod validFor;
    @Builder.Default
    private PriceImpact priceImpact = PriceImpact.NONE;
    private List<CharacteristicValueSpecification> characteristicValueSpecification;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
