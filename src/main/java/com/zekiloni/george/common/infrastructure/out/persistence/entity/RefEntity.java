package com.zekiloni.george.common.infrastructure.out.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
public class RefEntity {
    private UUID id;
    private String name;
    private String href;
}
