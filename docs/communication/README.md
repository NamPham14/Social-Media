# Personal Communication Design - Comment and Interaction

This folder contains the personal communication diagrams for member B.
The diagrams use `Post Service` naming to match the current source code. Older project notes may still say `Article Service`; in this repo, that should be read as `Post Service`.

## Service Ports

| Component | Port | Status |
| --- | ---: | --- |
| API Gateway | 8888 | existing config |
| Identity Service | 8081 | existing config |
| Profile Service | 8082 | existing config |
| Post Service | 8083 | existing config |
| Flow Service | 8084 | planned/not present in repo |
| Comment Service | 8085 | existing config |
| Interaction Service | 8086 | existing config |
| Notification Service | 8087 | existing config |
| Eureka Server | 8761 | existing config |

## Database Per Service

### `comment_db`

Use `post_id`, not `article_id`, so the database model follows the current source-code naming.
The current codebase uses `UUID` ids for posts/users, so the table below follows that convention.

```sql
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_id UUID REFERENCES comments(id),
    content TEXT NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
```

### `interaction_db`

```sql
CREATE TABLE interactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id UUID NOT NULL,
    reaction_type VARCHAR(20) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_interaction_actor_target
ON interactions (user_id, target_type, target_id);

CREATE TABLE interaction_counters (
    target_type VARCHAR(20),
    target_id UUID,
    like_count INT DEFAULT 0 CHECK (like_count >= 0),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (target_type, target_id)
);
```

## Diagrams

- `comment-flow.puml`: create comment/reply flow, including event publication to Post and Notification services.
- `interaction-race-condition.puml`: like/clap flow with duplicate-click idempotency and concurrent request handling.
