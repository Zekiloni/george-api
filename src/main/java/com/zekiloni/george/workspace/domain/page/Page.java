package com.zekiloni.george.workspace.domain.page;

import com.zekiloni.george.workspace.domain.page.form.FormConfig;

import java.time.OffsetDateTime;

public class Page {
    private String id;
    private String title;
    private String description;
    private String faviconUrl;
    private PageStatus status;
    private FormConfig formConfig;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
