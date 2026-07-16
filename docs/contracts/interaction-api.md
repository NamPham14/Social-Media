# Interaction API contract V1

All actor-specific endpoints require `X-Auth-User-Id: <uuid>`. Valid reaction types are `LIKE` and `CLAP`.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/interactions` | Idempotently add a reaction; body: `targetType`, `targetId`, `reactionType` |
| DELETE | `/api/v1/interactions/{targetType}/{targetId}/{reactionType}` | Idempotently remove actor reaction |
| GET | `/api/v1/interactions/me/{targetType}/{targetId}` | Actor's active reactions |
| GET | `/api/v1/interactions/counters/{targetType}/{targetId}` | Counter summary |
| POST | `/api/v1/interactions/counters/batch` | Counter summaries for target references |

Bookmark is not part of this contract; Post Service owns it.
