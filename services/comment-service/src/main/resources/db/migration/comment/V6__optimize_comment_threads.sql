CREATE INDEX IF NOT EXISTS idx_comments_root_page
    ON comments(post_id, created_at, id)
    WHERE parent_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_comments_active_reply_page
    ON comments(parent_id, created_at, id)
    WHERE is_deleted = FALSE AND parent_id IS NOT NULL;

ALTER TABLE comments
    ALTER COLUMN author_avatar_url TYPE VARCHAR(500);
