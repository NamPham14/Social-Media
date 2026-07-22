-- V1 allowed LIKE and CLAP simultaneously because reaction_type was part of the unique key.
-- V1 of the product now supports LIKE only and enforces one reaction per actor/target.
DELETE FROM interactions WHERE reaction_type <> 'LIKE';

ALTER TABLE interactions DROP CONSTRAINT IF EXISTS chk_reaction_type;
ALTER TABLE interactions DROP CONSTRAINT IF EXISTS idx_user_reaction;
ALTER TABLE interactions
    ADD CONSTRAINT chk_reaction_type CHECK (reaction_type = 'LIKE'),
    ADD CONSTRAINT uq_interaction_actor_target UNIQUE (user_id, target_type, target_id);

ALTER TABLE interaction_counters DROP COLUMN IF EXISTS clap_count;
