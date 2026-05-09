package com.zekiloni.george.platform.domain.model.page.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormConfig {
    @Builder.Default
    private List<FormField> fields = new ArrayList<>();
    private String submitLabel;
    private String successMessage;
}
