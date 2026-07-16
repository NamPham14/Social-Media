# Comment API contract V1

All command endpoints require `X-Auth-User-Id: <uuid>`. Responses include `X-Correlation-Id`.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/comments` | Create comment/reply; body: `postId`, optional `parentId`, `content` |
| PATCH | `/api/v1/comments/{commentId}` | Edit owner comment; body: `content` |
| DELETE | `/api/v1/comments/{commentId}` | Idempotent owner soft delete |
| GET | `/api/v1/comments/{commentId}` | Get a comment |
| GET | `/api/v1/posts/{postId}/comments?page=0&size=20` | Page a post discussion, oldest first |
| GET | `/internal/v1/comments/{commentId}/availability` | Interaction target validation; requires `X-Internal-Service-Token` |

Errors use the common API envelope with status 400, 403, 404, 409 or 503 and a correlation `traceId`.
