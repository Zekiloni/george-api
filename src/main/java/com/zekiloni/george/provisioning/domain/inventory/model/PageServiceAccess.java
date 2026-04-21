package com.zekiloni.george.provisioning.domain.inventory.model;

import com.zekiloni.george.platform.domain.page.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PageServiceAccess extends ServiceAccess {
    private List<Page> page;
    private int maxConcurrent;
}
