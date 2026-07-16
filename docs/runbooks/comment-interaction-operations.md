# Comment and Interaction runbook

## Required runtime settings

- Set the same non-empty `INTERNAL_SERVICE_TOKEN` for Comment and Interaction.
- Register `post-service`, `comment-service` and `interaction-service` in Eureka.
- Apply Flyway migrations using a database role allowed to create/alter the owned schema.

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

Start Docker, then run `mvn -pl services/comment-service,services/interaction-service -am clean test` from the repository root. This suite starts disposable PostgreSQL 16 containers and verifies Comment queries, Interaction concurrency, transaction rollback and Flyway migration behavior against the production database engine.

## Metrics and command tracing

- Query `/actuator/metrics/social.comment.command.duration` for create/delete latency and `/actuator/metrics/social.comment.command.errors` for failures.
- Query `/actuator/metrics/social.interaction.command.duration`, `/actuator/metrics/social.interaction.duplicates` and `/actuator/metrics/social.interaction.counter.update.failures` for reaction behavior.
- Filter metrics by the common `service` tag plus bounded `operation`, `outcome`, `target_type` or `reaction_type` tags. UUIDs, actor IDs, content and credentials are intentionally excluded from metric tags.
- Search logs by `correlationId`, then `aggregateId` and `operation`. Command logs never include actor ID, comment content or internal credentials.
- Continue using `/actuator/circuitbreakers` and Resilience4j metrics for outbound dependency state; an open dependency circuit does not imply local read endpoints are unhealthy.

## Kafka

Comment/Interaction event publishing is not enabled until CR-NOTIFICATION-001 is accepted. There is therefore no command path that can lose an event silently in the current release.
