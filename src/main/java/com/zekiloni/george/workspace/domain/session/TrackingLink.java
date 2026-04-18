package com.zekiloni.george.workspace.domain.session;

import com.zekiloni.george.workspace.domain.page.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingLink {
    private String id;
    private String code;
    private Boolean isActive;
    private Page page;
    private String source;
    private OffsetDateTime lastClickedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

