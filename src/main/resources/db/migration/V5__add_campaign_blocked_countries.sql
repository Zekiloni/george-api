-- Geo block list per campaign. ISO-3166-1 alpha-2 country codes stored as a
-- JSONB array. Empty / NULL means "no block." Checked by
-- UserSessionCreateService against the simulator-computed enrichment.country
-- at session-create time — match → 401 + no page render.

ALTER TABLE campaigns ADD COLUMN blocked_countries JSONB;
