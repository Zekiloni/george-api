-- Dev-only seed: one page + one campaign + one outreach under tenant
-- ae8c8fe9-7cad-407e-a47a-c671c2001524. Idempotent (ON CONFLICT DO NOTHING).
--
-- Run with:
--   psql -h localhost -U g-user -d george -f src/main/resources/db/seed/dev-seed.sql
--
-- The visitor token is 'devtest1' so you can hit /p/devtest1 in the simulator.

-- ─── Page ────────────────────────────────────────────────────────────────
INSERT INTO pages (
    id, tenant_id, created_at, updated_at, created_by, updated_by,
    title, slug, description, keywords, favicon_url, status, definition
) VALUES (
    '11111111-1111-1111-1111-000000000001',
    'ae8c8fe9-7cad-407e-a47a-c671c2001524',
    now(), now(), 'seed', 'seed',
    'Dev Landing',
    'dev-landing',
    'Seeded landing page for dev testing',
    NULL,
    NULL,
    'PUBLISHED',
    '{"html":"<h1>Hello {{name}}</h1>","css":"","form":{"fields":[]},"variables":{}}'::jsonb
) ON CONFLICT (id) DO NOTHING;

-- ─── Campaign ────────────────────────────────────────────────────────────
-- service_access_id has no DB-level FK (it's an entity-only relation), so
-- a synthetic UUID is fine for a smoke seed.
INSERT INTO campaigns (
    id, tenant_id, created_at, updated_at, created_by, updated_by,
    name, token_strategy, token_length, message_template,
    status, base_url, flow_page_ids, service_access_id, scheduled_at,
    source_template_id, source_template_version
) VALUES (
    '22222222-2222-2222-2222-000000000001',
    'ae8c8fe9-7cad-407e-a47a-c671c2001524',
    now(), now(), 'seed', 'seed',
    'Dev Campaign',
    'ALPHANUMERIC',
    8,
    'Hi {{name}} — visit {{link}}',
    'ACTIVE',
    'http://localhost:3000',
    '["11111111-1111-1111-1111-000000000001"]'::jsonb,
    '44444444-4444-4444-4444-000000000001',
    NULL,
    NULL,
    NULL
) ON CONFLICT (id) DO NOTHING;

-- ─── Outreach ────────────────────────────────────────────────────────────
INSERT INTO outreach (
    id, tenant_id, created_at, updated_at, created_by, updated_by,
    session_token, recipient, message, status,
    external_id, country, carrier,
    campaign_id, scheduled_at, dispatched_at, delivered_at,
    failed_at, bounced_at, complained_at, failure_reason
) VALUES (
    '33333333-3333-3333-3333-000000000001',
    'ae8c8fe9-7cad-407e-a47a-c671c2001524',
    now(), now(), 'seed', 'seed',
    'devtest1',
    'test@example.com',
    'Hi Test — visit http://localhost:3000/p/devtest1',
    'DELIVERED',
    NULL, NULL, NULL,
    '22222222-2222-2222-2222-000000000001',
    now(), now(), now(),
    NULL, NULL, NULL, NULL
) ON CONFLICT (id) DO NOTHING;
