CREATE TABLE IF NOT EXISTS interactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id UUID NOT NULL,
    reaction_type VARCHAR(20) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT idx_user_reaction UNIQUE(user_id, target_type, target_id, reaction_type),
    CONSTRAINT chk_reaction_type CHECK (reaction_type IN ('LIKE', 'CLAP'))
);

CREATE TABLE IF NOT EXISTS interaction_counters (
    target_type VARCHAR(20) NOT NULL,
    target_id UUID NOT NULL,
    like_count INTEGER NOT NULL DEFAULT 0 CHECK (like_count >= 0),
    clap_count INTEGER NOT NULL DEFAULT 0 CHECK (clap_count >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(target_type, target_id)
);
