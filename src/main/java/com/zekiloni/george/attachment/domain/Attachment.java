package com.zekiloni.george.attachment.domain;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    private UUID id;
    private String description;
    private String fileName;
    private String mimeType;
    private Long size;
    private String href;
    private String type;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private Boolean isPrimary;
    private boolean isValid;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
