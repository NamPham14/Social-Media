# CR-NOTIFICATION-001: UUID comment/reaction notification events

- Status: PRODUCER_IMPLEMENTED / CONSUMER_PENDING
- Requester: comment-service, interaction-service
- Provider owner: notification-service
- Motivation/business use case: notify post/comment owners without coupling command transactions to Notification.
- Current behavior: Notification contracts use `Long`; these services use UUID.
- Producer contract: versioned `CommentCreatedV1`, `CommentRepliedV1` and `ReactionCreatedV1` envelopes with UUID strings and `eventId` deduplication. See `../../contracts/comment-interaction-notification-events.md`.
- Error semantics: consumer retry topic followed by DLT; poison events do not block the partition indefinitely.
- Authentication: Kafka ACLs; no end-user credential in payload.
- Backward compatibility: new V1 topics/schema; existing Long consumers remain unchanged during migration.
- Database impact: Notification stores UUID target/actor/recipient or introduces parallel UUID columns.
- Rollout order: producer outboxes and topics, Notification UUID migration/idempotent consumers, then Kafka E2E. Producers may deploy before consumers because Kafka retains the events.
- Rollback plan: stop new producers; preserve outbox rows for replay after correction.
- Consumer tests: duplicate event, self-action suppression, retry and DLT.
- Provider tests: transaction rollback/commit, relay acknowledgement/retry and envelope serialization.
- Observability: event ID, event type, topic, aggregate ID, producer attempts, consumer lag and retry/DLT counts.
- Decision/date/approver: producer implementation completed 2026-07-20; Notification UUID consumer acceptance remains pending with the Notification owner.
