package com.zekiloni.george.platform.domain.model.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin-curated node in the template library taxonomy. No tenant owns a
 * category — they're system-wide and only mutable by users with the ADMIN
 * role. The tree is arbitrary-depth via {@code parentId}; UIs typically
 * load it via {@code findTree()} which returns the roots populated with
 * their {@code children} lists.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private String id;
    private String name;
    /** URL-safe slug, unique within a parent. */
    private String slug;
    private String parentId;
    private int sortOrder;

    /** Populated only by tree-shaped query results; null on flat queries. */
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
