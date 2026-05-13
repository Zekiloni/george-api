package com.zekiloni.george.platform.domain.model.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable flow blueprint. Two flavours, enforced by a DB CHECK constraint:
 * <ul>
 *   <li><b>Public</b> — {@code categoryId} set, {@code tenantId} null.
 *       Admin-curated, visible to every tenant.</li>
 *   <li><b>Owned</b>  — {@code tenantId} set, {@code categoryId} null.
 *       Private to one tenant, never appears in another tenant's library.</li>
 * </ul>
 *
 * The {@code steps} list is ordered and stored verbatim (JSONB). When a user
 * picks "Use this template" the application clones each step's
 * {@link com.zekiloni.george.platform.domain.model.page.definition.PageDefinition}
 * into a tenant-owned Page after substituting any variables, then assembles
 * a Campaign whose flow references those new Pages.
 *
 * The {@code version} integer ticks up on every admin edit so campaigns that
 * remember their {@code sourceTemplateId}/{@code sourceTemplateVersion} can
 * detect when an update is available and offer a sync prompt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {
    private String id;
    private String name;
    private String description;
    private String thumbnailUrl;

    @Builder.Default
    private List<TemplateStep> steps = new ArrayList<>();

    @Builder.Default
    private List<TemplateVariable> variables = new ArrayList<>();

    /** Monotonically incremented on every admin edit. Starts at 1. */
    @Builder.Default
    private int version = 1;

    /** Incremented each time the template is instantiated into a campaign. */
    @Builder.Default
    private long usageCount = 0;

    /** Non-null for public templates; null for tenant-owned. */
    private String categoryId;
    /** Non-null for tenant-owned templates; null for public. */
    private String tenantId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;

    /** True if this template lives in the public catalog. */
    public boolean isPublic() {
        return categoryId != null;
    }
}
