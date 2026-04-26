package com.zekiloni.george.attachment.infrastructure.out.persistence;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class AttachmentEntity extends BaseEntity {
    @Column
    private String description;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long size;

    @Column
    private String href;

    @Column
    private String type;

    @Column
    private OffsetDateTime validFrom;

    @Column
    private OffsetDateTime validTo;

    @Column
    private Boolean isPrimary;

    public boolean getIsValid() {
        OffsetDateTime now = OffsetDateTime.now();
        return (validFrom == null || !now.isBefore(validFrom)) &&
                (validTo == null || !now.isAfter(validTo));
    }
}
