-- Template library: nested category taxonomy curated by admins, plus
-- templates that live either in the public catalog (categorized, no tenant)
-- or in a tenant's private locker (uncategorized, tenant-owned).
--
-- The ownership rule is enforced by a CHECK constraint so it's impossible
-- to wedge a template into both buckets or neither — the application layer
-- only needs to set the right column and the DB rejects bad combinations.

-- ─── Category tree ───────────────────────────────────────────────────────
-- Admin-curated, system-wide. No tenant column; categories are shared.
CREATE TABLE category (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        TEXT        NOT NULL,
    slug        TEXT        NOT NULL,
    parent_id   UUID        REFERENCES category(id) ON DELETE RESTRICT,
    sort_order  INT         NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by  TEXT,
    updated_by  TEXT,
    -- Slug uniqueness scoped to siblings: two top-level categories can't
    -- share a slug, but "auth/login" and "commerce/login" can coexist.
    CONSTRAINT category_slug_unique_per_parent UNIQUE (parent_id, slug)
);

-- Fast tree traversal: list children of a node.
CREATE INDEX category_parent_idx ON category(parent_id)
    WHERE deleted_at IS NULL;

-- ─── Template ────────────────────────────────────────────────────────────
-- A reusable flow blueprint. `steps` is an ordered JSONB array of
--   { name, order, definition: { html, css, form, variables } }
-- `variables` declares the placeholders this template exposes so the
-- "Use template" dialog can prompt for values before instantiation:
--   [ { name, label, type, defaultValue, required } ]
CREATE TABLE template (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            TEXT        NOT NULL,
    description     TEXT,
    thumbnail_url   TEXT,
    steps           JSONB       NOT NULL,
    variables       JSONB       NOT NULL DEFAULT '[]'::jsonb,
    -- Monotonically incremented on every admin edit so campaigns can
    -- detect when their source template has new changes available.
    version         INT         NOT NULL DEFAULT 1,
    -- Popularity counter, incremented at create-from-template time.
    usage_count     BIGINT      NOT NULL DEFAULT 0,
    category_id     UUID        REFERENCES category(id) ON DELETE RESTRICT,
    tenant_id       TEXT,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      TEXT,
    updated_by      TEXT,
    -- Hard invariant: exactly one of (category_id, tenant_id) is set.
    -- Categorized = public catalog template, owned by admins.
    -- Tenant-set  = private template, never appears in another tenant's view.
    CONSTRAINT template_ownership_check CHECK (
        (category_id IS NOT NULL AND tenant_id IS NULL)
        OR
        (category_id IS NULL AND tenant_id IS NOT NULL)
    )
);

-- Per-axis partial indexes so a `category_id = ?` lookup hits only public
-- rows and a `tenant_id = ?` lookup hits only that tenant's rows. Partial
-- (deleted_at IS NULL) so soft-deleted templates don't bloat the indexes.
CREATE INDEX template_category_idx ON template(category_id)
    WHERE category_id IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX template_tenant_idx ON template(tenant_id)
    WHERE tenant_id IS NOT NULL AND deleted_at IS NULL;

-- Trigram index for fuzzy ILIKE name search across the library. The
-- extension is created idempotently in case another module already added it.
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX template_name_trgm_idx ON template USING gin (name gin_trgm_ops)
    WHERE deleted_at IS NULL;

-- ─── Campaign → source template link ─────────────────────────────────────
-- Optional pointers so a campaign can detect "your template author published
-- a v2, want to see the diff?" without coupling the campaign to the template
-- at runtime — the cloned Pages are still tenant-owned and editable.
ALTER TABLE campaigns ADD COLUMN source_template_id      UUID
    REFERENCES template(id) ON DELETE SET NULL;
ALTER TABLE campaigns ADD COLUMN source_template_version INT;
