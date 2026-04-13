package com.zekiloni.george.workspace.infrastructure.input.web.page.dto;

import com.zekiloni.george.workspace.domain.page.PageStatus;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.FormDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    private String id;
    private String title;
    private String description;
    private String keywords;
    private String faviconUrl;
    private PageStatus status;
    private FormDto form;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
