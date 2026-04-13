package com.zekiloni.george.workspace.infrastructure.input.web.page.dto;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.FormCreateDto;

public record PageCreateDto(String title, String description, String keywords, String faviconUrl, FormCreateDto form) {
}
