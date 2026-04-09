package com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto;

import com.zekiloni.george.platform.domain.model.Lead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LeadServiceAccessDto extends ServiceAccessDto {
    private List<Lead> leads;
}

