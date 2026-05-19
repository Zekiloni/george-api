package com.zekiloni.george.commerce.domain.inventory.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PageServiceAccess extends ServiceAccess {
    @Builder.Default
    private List<String> pageIds = new ArrayList<>();
    private int maxConcurrent;
}
