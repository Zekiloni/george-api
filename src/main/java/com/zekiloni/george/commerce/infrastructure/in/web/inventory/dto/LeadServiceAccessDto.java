package com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadServiceAccessDto extends ServiceAccessDto {
    private List<String> leadIds;
}
