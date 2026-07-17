DELETE FROM interactions WHERE reaction_type = 'BOOKMARK';
ALTER TABLE interaction_counters DROP COLUMN IF EXISTS bookmark_count;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_reaction_type' AND conrelid = 'interactions'::regclass
    ) THEN
        ALTER TABLE interactions ADD CONSTRAINT chk_reaction_type CHECK (reaction_type IN ('LIKE', 'CLAP'));
    END IF;
END $$;
