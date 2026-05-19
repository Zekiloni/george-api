-- Align ConversionEntity id column types with the rest of the codebase
-- (UUID instead of String). PostgreSQL will cast existing varchar values
-- to UUID automatically if they're valid UUIDs.

ALTER TABLE conversions
    ALTER COLUMN session_id TYPE UUID USING session_id::UUID,
    ALTER COLUMN outreach_id TYPE UUID USING outreach_id::UUID,
    ALTER COLUMN campaign_id TYPE UUID USING campaign_id::UUID,
    ALTER COLUMN page_id TYPE UUID USING page_id::UUID;
