-- Anti-bot / anti-VPN signal columns. Populated by the simulator's pre-render
-- security gate (see apps/simulator/src/server/flags.ts on the UI side):
--
--   • flags        — JSONB array of risk tags: 'vpn', 'proxy', 'tor',
--                    'datacenter', 'bot', 'replay', 'geo_mismatch',
--                    'velocity', 'headless'. Empty array on clean sessions.
--   • country/city — resolved from MaxMind GeoLite2 at session-create time.
--   • asn / asn_org — AS routing the visitor's IP (datacenter ASNs imply VPN).
--   • risk_score   — external reputation provider score (IPQS 0–100; nullable
--                    when no provider is wired in dev).
--
-- The simulator also stamps outreach.kick_reason when the bot gate refuses to
-- render the page — the visitor never gets a UserSession in that case, just a
-- kicked outreach. Values mirror the simulator-side BotReason enum
-- (UA_DENYLIST, HEADER_GAUNTLET, JA4_KNOWN_BOT, VELOCITY_TOKEN, VELOCITY_IP).

ALTER TABLE user_sessions ADD COLUMN flags       JSONB;
ALTER TABLE user_sessions ADD COLUMN country     VARCHAR(2);
ALTER TABLE user_sessions ADD COLUMN city        VARCHAR(255);
ALTER TABLE user_sessions ADD COLUMN asn         INTEGER;
ALTER TABLE user_sessions ADD COLUMN asn_org     VARCHAR(255);
ALTER TABLE user_sessions ADD COLUMN risk_score  INTEGER;

ALTER TABLE outreach ADD COLUMN kick_reason VARCHAR(64);
