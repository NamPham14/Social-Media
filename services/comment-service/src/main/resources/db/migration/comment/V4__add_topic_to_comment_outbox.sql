ALTER TABLE comment_outbox
    ADD COLUMN IF NOT EXISTS topic VARCHAR(255);

UPDATE comment_outbox
SET topic = 'post-comments-deleted-topic'
WHERE topic IS NULL;

ALTER TABLE comment_outbox
    ALTER COLUMN topic SET NOT NULL;
