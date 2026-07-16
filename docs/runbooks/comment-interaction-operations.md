# Comment and Interaction runbook

## Required runtime settings

- Set the same non-empty `INTERNAL_SERVICE_TOKEN` for Comment and Interaction.
- Register `post-service`, `comment-service` and `interaction-service` in Eureka.
- Apply Flyway migrations using a database role allowed to create/alter the owned schema.

## Post Service unavailable

Create comment/reaction fails closed with 503 after the configured timeout/retry budget. Local comment and counter reads remain available. Check `postAvailability` circuit state and dependency latency before restarting anything.

## Comment Service unavailable

Comment-target reactions fail with 503; post reactions and all local queries continue. Verify internal token parity before treating 403 responses as an outage.

## Counter mismatch

Stop writes for the affected target, count rows in `interactions` grouped by `reaction_type`, compare with `interaction_counters`, then repair in one transaction. A repair command/API is intentionally not exposed publicly.

## Migration failure

Do not set Hibernate back to `ddl-auto=update`. Inspect `flyway_schema_history`, correct the migration or data precondition, and rerun. The V2 Interaction migration removes legacy BOOKMARK rows and its counter column.

## Persistence verification

Start Docker, then run `mvn -pl services/comment-service,services/interaction-service -am clean test` from the repository root. This suite starts disposable PostgreSQL 16 containers and verifies Comment queries, Interaction concurrency, transaction rollback and Flyway migration behavior against the production database engine.

## Kafka

Comment/Interaction event publishing is not enabled until CR-NOTIFICATION-001 is accepted. There is therefore no command path that can lose an event silently in the current release.
