-- Prevent duplicate outreach records for the same recipient within a campaign.
-- The (campaign_id, recipient) pair must be unique — re-uploading the same
-- phone list for the same campaign would otherwise create duplicates.
--
-- During migration, any existing duplicates are resolved by keeping the first
-- created row per (campaign_id, recipient); later duplicates are deleted.

DELETE FROM outreach
WHERE id NOT IN (
    SELECT id
    FROM (
        SELECT DISTINCT ON (campaign_id, recipient) id
        FROM outreach
        ORDER BY campaign_id, recipient, created_at ASC
    ) sub
);

ALTER TABLE outreach ADD CONSTRAINT uq_outreach_campaign_recipient UNIQUE (campaign_id, recipient);
