package com.zekiloni.george.common.infrastructure.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefDto {
    private String id;
    private String name;
    private String href;
}
