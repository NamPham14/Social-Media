CREATE TABLE IF NOT EXISTS comment_outbox (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,
    last_error VARCHAR(2000),
    CONSTRAINT chk_comment_outbox_status CHECK (status IN ('PENDING', 'PUBLISHED'))
);

CREATE INDEX IF NOT EXISTS idx_comment_outbox_pending
    ON comment_outbox(next_attempt_at, created_at)
    WHERE status = 'PENDING';
