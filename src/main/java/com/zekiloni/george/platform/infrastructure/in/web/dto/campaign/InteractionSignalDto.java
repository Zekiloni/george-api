package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionSignalDto {
    private String id;
    private InteractionType type;
    private Map<String, Object> payload;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
