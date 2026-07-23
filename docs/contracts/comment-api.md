# Comment API contract V1

All command endpoints require `X-Auth-User-Id: <uuid>`. Responses include `X-Correlation-Id`.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/comments` | Create comment/reply; body: `postId`, optional `parentId`, `content` |
| PATCH | `/api/v1/comments/{commentId}` | Edit owner comment; body: `content` |
| DELETE | `/api/v1/comments/{commentId}` | Idempotent soft delete by comment author or post owner |
| GET | `/api/v1/comments/{commentId}` | Get one comment with engagement summary |
| GET | `/api/v1/posts/{postId}/comments?page=0&size=20` | Page root comments only, oldest first |
| GET | `/api/v1/comments/{commentId}/replies?page=0&size=20` | Page active direct replies, oldest first |
| GET | `/api/v1/posts/{postId}/comments/count` | Count active comments and replies for one post; missing local rows return zero |
| POST | `/api/v1/comments/counts/batch` | Count active comments for up to 100 post UUIDs in one query |
| GET | `/internal/v1/comments/{commentId}/availability` | Interaction target and containing-post validation; requires actor and internal token |

The three public GET comment read endpoints accept an optional `X-Auth-User-Id`. Without an actor,
`likedByMe` is always `false`; with an actor it reflects the current LIKE. Each comment response includes
`replyCount`, `reactionCount`, and `likedByMe`. A page is enriched by one Interaction batch call, never one
call per comment. If Interaction is unavailable, comment content remains readable and engagement fields
degrade to zero/false.

Comment validates the containing Post on all detail/root/reply reads. The current adapter permits only
active `PUBLIC` posts, so anonymous reads fail closed for private/deleted posts. The provider contract in
`CR-POST-001` is still required for richer authenticated visibility (owner/friend/audience) and a stable
internal API. Commands always require authentication.

Successful public responses use common API code `1000`. Errors use stable Comment codes in the
`45000..45999` range, the matching HTTP status, a safe message and correlation `traceId`.

| Code | HTTP status | Meaning |
| --- | --- | --- |
| `45000` | 400 | Missing, malformed or invalid request value |
| `45001` | 400 | Bean or method validation failed |
| `45002` | 404 | Comment does not exist |
| `45003` | 404 | Post or other target does not exist |
| `45004` | 403 | Actor does not own the comment |
| `45005` | 403 | Internal service credential is invalid |
| `45006` | 409 | Comment or target state conflicts with the command |
| `45007` | 503 | Required downstream service is unavailable or unverifiable |
| `45008` | 404 | API route does not exist |
| `45009` | 405 | HTTP method is unsupported for the route |
| `45010` | 415 | Request content type is unsupported |
| `45999` | 500 | Unexpected internal failure; details are logged by `traceId` only |

Comment counts exclude soft-deleted comments, including deleted parents that remain visible as discussion placeholders. Batch results de-duplicate post IDs in first-seen order and return zero for posts with no local active comments; count queries do not call Post Service.

Committed root comments, replies, and effective soft deletions publish the UUID V1 events defined in
`comment-interaction-notification-events.md` through the Comment transactional outbox.
