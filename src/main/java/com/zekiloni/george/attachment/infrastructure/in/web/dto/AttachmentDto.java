package com.zekiloni.george.attachment.infrastructure.in.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto implements Serializable {
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
    public boolean isValid;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
