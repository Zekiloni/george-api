package com.zekiloni.george.commerce.domain.order.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderItem {
    private String id;
    private Offering offering;
    private Integer units;
    private List<Characteristic> characteristic;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
