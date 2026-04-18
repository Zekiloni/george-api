package com.zekiloni.george.workspace.domain.page.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDefinition {
    private ComponentNode root;
    @Builder.Default
    private Map<String, Object> globalStyles = new HashMap<>();
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();
}