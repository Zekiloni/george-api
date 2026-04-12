package com.zekiloni.george.workspace.domain.page;

import com.zekiloni.george.workspace.domain.page.form.Form;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {
    private String id;
    private String title;
    private String description;
    private String keywords;
    private String faviconUrl;
    private PageStatus status;
    private Form form;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
