# CR-NOTIFICATION-001: UUID comment/reaction notification events

- Status: PROPOSED
- Requester: comment-service, interaction-service
- Provider owner: notification-service
- Motivation/business use case: notify post/comment owners without coupling command transactions to Notification.
- Current behavior: Notification contracts use `Long`; these services use UUID.
- Proposed contract: versioned envelopes for `CommentCreatedV1`, `CommentRepliedV1`, `ReactionCreatedV1`, `ReactionRemovedV1` with UUID strings and `eventId` deduplication.
- Error semantics: consumer retry topic followed by DLT; poison events do not block the partition indefinitely.
- Authentication: Kafka ACLs; no end-user credential in payload.
- Backward compatibility: new V1 topics/schema; existing Long consumers remain unchanged during migration.
- Database impact: Notification stores UUID target/actor/recipient or introduces parallel UUID columns.
- Rollout order: accepted schema, Notification UUID migration/idempotency, producer outbox, E2E.
- Rollback plan: stop new producers; preserve outbox rows for replay after correction.
- Consumer tests: duplicate event, self-action suppression, retry and DLT.
- Provider tests: outbox commit/replay and envelope serialization.
- Observability: event ID, correlation ID, consumer lag, retry/DLT counts.
- Decision/date/approver: pending Notification owner review.
