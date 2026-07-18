CREATE INDEX IF NOT EXISTS idx_comments_active_post_id
    ON comments(post_id)
    WHERE is_deleted = FALSE;
