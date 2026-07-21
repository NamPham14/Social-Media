# Comment and Interaction runbook

## Required runtime settings

- Set the same non-empty `INTERNAL_SERVICE_TOKEN` for Comment and Interaction.
- Register `post-service`, `comment-service` and `interaction-service` in Eureka.
- Apply Flyway migrations using a database role allowed to create/alter the owned schema.
- Supply `SPRING_DATASOURCE_URL` and `SPRING_DATASOURCE_PASSWORD` outside the local profile.
- Provision Kafka topics with each DLT having at least as many partitions as its source topic:
  - `post-deleted-topic`
  - `post-comments-deleted-topic`
  - `comment-created-topic`
  - `comment-replied-topic`
  - `reaction-created-topic`
  - `post-deleted-topic-comment-dlt`
  - `post-deleted-topic-interaction-dlt`
  - `post-comments-deleted-topic-interaction-dlt`

## Local development profile

Start the root Docker Compose databases, then run each application with
`SPRING_PROFILES_ACTIVE=local` and the same `INTERNAL_SERVICE_TOKEN`. The local profile connects
Comment to `localhost:5435/comment_service` and Interaction to
`localhost:5436/interaction_service`; SQL logging is enabled only in this profile.

## Post Service unavailable

Create comment/reaction fails closed with 503 after the configured timeout/retry budget. Local comment and counter reads remain available. Check `postAvailability` circuit state and dependency latency before restarting anything.

## Comment Service unavailable

Comment-target reactions fail with 503; post reactions and all local queries continue. Verify internal token parity before treating 403 responses as an outage.

## Outbound availability classification

- Target 404 and business 403 responses are not retried and do not count toward circuit failure rate.
- Other 4xx responses mean the provider rejected the consumer request. They return fail-closed 503 with the upstream status in the error message, but are not retried and do not open the circuit.
- Network errors, timeouts and 5xx responses are retried once. Repeated failures count toward the dependency-specific circuit breaker.
- A successful half-open probe closes the circuit; local read endpoints remain independent of provider circuit state.

## Counter mismatch

Stop writes for the affected target, count rows in `interactions` grouped by `reaction_type`, compare with `interaction_counters`, then repair in one transaction. A repair command/API is intentionally not exposed publicly.

## Migration failure

Do not set Hibernate back to `ddl-auto=update`. Inspect `flyway_schema_history`, correct the migration or data precondition, and rerun. The V2 Interaction migration removes legacy BOOKMARK rows and its counter column.

## Persistence verification

Start Docker, then run `mvn -pl services/comment-service,services/interaction-service,qa/comment-interaction-e2e -am clean test` from the repository root. This suite starts disposable PostgreSQL 16 containers and verifies Comment queries, Interaction concurrency, transaction rollback, Flyway migrations and the synchronous Comment-target reaction flow against the production database engine.

## Synchronous E2E verification

The `comment-interaction-e2e` module starts both real applications on random local ports with separate databases and Eureka disabled. It verifies correlation response, Comment internal-token validation through Feign, duplicate reaction idempotency, repeated removal, deleted-target rejection, fail-closed 503 behavior and continued local counter reads while Comment Service is unavailable. It does not require Post, Kafka or Notification.

## Metrics and command tracing

- Query `/actuator/metrics/social.comment.command.duration` for create/delete latency and `/actuator/metrics/social.comment.command.errors` for failures.
- Query `/actuator/metrics/social.interaction.command.duration`, `/actuator/metrics/social.interaction.duplicates` and `/actuator/metrics/social.interaction.counter.update.failures` for reaction behavior.
- Filter metrics by the common `service` tag plus bounded `operation`, `outcome`, `target_type` or `reaction_type` tags. UUIDs, actor IDs, content and credentials are intentionally excluded from metric tags.
- Search logs by `correlationId`, then `aggregateId` and `operation`. Command logs never include actor ID, comment content or internal credentials.
- Continue using `/actuator/circuitbreakers` and Resilience4j metrics for outbound dependency state; an open dependency circuit does not imply local read endpoints are unhealthy.

## Kafka

- Comment and Interaction consume `post-deleted-topic` independently. Comment soft-deletes local
  comments and writes `PostCommentsDeletedV1` to `comment_outbox` in the same database transaction.
- The Comment outbox relay publishes `post-comments-deleted-topic` with at-least-once delivery.
  A Kafka acknowledgement followed by a database commit failure can publish a duplicate; the
  Interaction cleanup remains idempotent by deleting by target ID.
- Transient listener failures receive five total delivery attempts with one-second backoff.
  Invalid JSON, missing fields and invalid UUIDs are non-retryable and go directly to the
  service-specific DLT.
- DLT publishing waits for broker acknowledgement. If publishing fails, the original record is
  not treated as recovered and remains eligible for redelivery.
- Comment writes `CommentCreatedV1`/`CommentRepliedV1` to `comment_outbox`, while Interaction
  writes `ReactionCreatedV1` to `interaction_outbox`, in the same transaction as the command data.
  Relays publish only after commit and retry failed sends without failing the client request.
- Root comments target the post owner; replies target the parent-comment owner; reactions target
  the Post/Comment owner. Duplicate reactions and self-actions intentionally create no event.
- Monitor pending-row count, oldest pending `created_at`, attempts and `last_error` in both outbox
  tables. A growing oldest age indicates broker/configuration failure and must alert operations.

### DLT triage and replay

1. Identify the owning consumer from the DLT topic suffix and inspect the original topic,
   partition, offset and exception headers.
2. Correct the payload producer or transient database/configuration failure before replay.
3. Republish the original key and value to the original topic. Do not replay both service DLTs for
   the same failure unless both consumers actually failed.
4. Confirm an idempotent no-op or the expected deleted-row counts in service logs, then retain the
   DLT record according to the platform retention policy.

Notification consumption remains gated by the UUID migration in CR-NOTIFICATION-001. Do not point
the current Long-based Notification consumers at these UUID V1 topics before that migration.
