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
