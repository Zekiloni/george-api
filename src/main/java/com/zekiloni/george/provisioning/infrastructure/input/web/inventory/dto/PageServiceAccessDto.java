package com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PageServiceAccessDto extends ServiceAccessDto {
    private int maxConcurrent;
}

