package com.zekiloni.george.provisioning.infrastructure.in.web.inventory.dto;

import com.zekiloni.george.platform.domain.model.Lead;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadServiceAccessDto extends ServiceAccessDto {
    private List<Lead> leads;
}

