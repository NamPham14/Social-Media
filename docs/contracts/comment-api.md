# Comment API contract V1

All command endpoints require `X-Auth-User-Id: <uuid>`. Responses include `X-Correlation-Id`.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/comments` | Create comment/reply; body: `postId`, optional `parentId`, `content` |
| PATCH | `/api/v1/comments/{commentId}` | Edit owner comment; body: `content` |
| DELETE | `/api/v1/comments/{commentId}` | Idempotent owner soft delete |
| GET | `/api/v1/comments/{commentId}` | Get a comment |
| GET | `/api/v1/posts/{postId}/comments?page=0&size=20` | Page a post discussion, oldest first |
| GET | `/api/v1/posts/{postId}/comments/count` | Count active comments and replies for one post; missing local rows return zero |
| POST | `/api/v1/comments/counts/batch` | Count active comments for up to 100 post UUIDs in one query |
| GET | `/internal/v1/comments/{commentId}/availability` | Interaction target validation; requires `X-Internal-Service-Token` |

Errors use the common API envelope with status 400, 403, 404, 409 or 503 and a correlation `traceId`.

Comment counts exclude soft-deleted comments, including deleted parents that remain visible as discussion placeholders. Batch results de-duplicate post IDs in first-seen order and return zero for posts with no local active comments; count queries do not call Post Service.
