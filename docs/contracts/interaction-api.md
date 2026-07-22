# Interaction API contract V1

Commands, `/me`, and reactor-list endpoints require `X-Auth-User-Id: <uuid>`. Summary endpoints accept
an optional actor. The only valid reaction type in V1 is `LIKE`; one actor can have at most one reaction
per target, regardless of reaction type.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/interactions` | Idempotently add a reaction; body: `targetType`, `targetId`, `reactionType` |
| DELETE | `/api/v1/interactions/{targetType}/{targetId}/{reactionType}` | Idempotently remove actor reaction |
| GET | `/api/v1/interactions/me/{targetType}/{targetId}` | Actor's active reactions |
| GET | `/api/v1/interactions/counters/{targetType}/{targetId}` | Counter summary |
| POST | `/api/v1/interactions/counters/batch` | Counter summaries for target references |
| GET | `/api/v1/interactions/summaries/{targetType}/{targetId}` | Combined `reactionCount` and `likedByMe` for one target |
| POST | `/api/v1/interactions/summaries/batch` | Combined summaries for up to 100 targets without N+1 requests |
| GET | `/api/v1/interactions/reactors/{targetType}/{targetId}?page=0&size=20` | Page actors who currently LIKE the visible target |

Bookmark is not part of this contract; Post Service owns it.

The batch summary request is:

```json
{
  "targets": [
    { "targetType": "COMMENT", "targetId": "00000000-0000-0000-0000-000000000001" }
  ]
}
```

Each response item has this shape:

```json
{
  "targetType": "COMMENT",
  "targetId": "00000000-0000-0000-0000-000000000001",
  "reactionCount": 20,
  "likedByMe": true
}
```

`commentCount` is intentionally not owned by Interaction Service. A feed/BFF should compose this endpoint
with Comment Service's `/api/v1/comments/counts/batch`, once each per page. Missing counter/interaction rows
produce `reactionCount: 0` and `likedByMe: false`, and duplicate target references are de-duplicated while
preserving first-seen order. For anonymous requests, `likedByMe` is deterministically `false` and the
actor-ledger query is skipped. Matching uses the composite `(targetType,targetId)`, including when a Post
and Comment happen to share the same UUID.

`CommentDeletedV1` is consumed idempotently to remove the deleted comment's reaction ledger and counter.

A newly created (non-duplicate, non-self) reaction publishes `ReactionCreatedV1` through the
Interaction transactional outbox. See `comment-interaction-notification-events.md`.

Successful responses use common API code `1000`. Errors use stable Interaction codes in the
`46000..46999` range, the matching HTTP status, a safe message and correlation `traceId`.

| Code | HTTP status | Meaning |
| --- | --- | --- |
| `46000` | 400 | Missing, malformed or invalid request value |
| `46001` | 400 | Bean or method validation failed |
| `46002` | 404 | Reaction target does not exist |
| `46003` | 409 | Target state conflicts with the reaction command |
| `46004` | 503 | Required downstream service is unavailable or unverifiable |
| `46005` | 404 | API route does not exist |
| `46006` | 405 | HTTP method is unsupported for the route |
| `46007` | 415 | Request content type is unsupported |
| `46999` | 500 | Unexpected internal failure; details are logged by `traceId` only |
