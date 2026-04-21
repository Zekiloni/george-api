package com.zekiloni.george.common.infrastructure.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "characteristics")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicEntity extends BaseEntity {
    private UUID id;
    private String name;
    private Object value;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

