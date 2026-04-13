package com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageServiceAccessDto extends ServiceAccessDto {
    private int maxConcurrent;
}

