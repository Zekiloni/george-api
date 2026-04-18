package com.zekiloni.george.workspace.domain.page.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentNode {
    private String id;
    private String type;
    private Integer displayOrder;
    @Builder.Default
    private Map<String, Object> props = new HashMap<>();
    @Builder.Default
    private List<ComponentNode> children = new ArrayList<>();
}

