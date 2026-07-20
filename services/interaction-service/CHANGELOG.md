# Changelog

## Unreleased

### Added

- Stable service-specific API error codes with correlation-aware 400/404/405/409/415/500/503 handling.
- A `local` Spring profile aligned with the root Compose Interaction database port.
- Bounded retry and service-specific DLT recovery for Post and Comment target cleanup events.
- Transactional Interaction outbox and `ReactionCreatedV1` notification events with resolved UUID
  recipients plus duplicate/self-action suppression.
- Idempotent add/remove reaction flows and single/batch counter queries.
- Target availability outbound port.
- PostgreSQL Testcontainers coverage for concurrent writes, transaction rollback, safe removal and legacy BOOKMARK migration.
- Correlation-aware reaction logs and Micrometer command, duplicate and counter-failure metrics with bounded tags.
- Real-service E2E coverage for Comment-target reaction, duplicate/remove idempotency and dependency failure.

### Removed

- `BOOKMARK`; bookmarks are owned by Post Service.

### Security

- Actor ID is read only from the trusted gateway header.

### Changed

- Successful API envelopes now consistently use common code `1000`.
- Default runtime database credentials are externalized; SQL logging is local-profile only.
- Poison Kafka records bypass retry and DLT publishing must receive broker acknowledgement.
- Target-provider 4xx responses are non-retriable and excluded from circuit failure metrics; 403 Post responses remain reaction conflicts.
- Namespaced Flyway resources under the Interaction bounded context so composed test/runtime classpaths cannot discover another service's migrations.

### Fixed

- Declared Feign path variables explicitly so Post and Comment clients can start without Java parameter-name metadata.
- Declared Interaction controller path variables explicitly and required the shared internal token in Compose.
- Applied Feign timeout settings by client context ID while preserving Eureka service names.
- Added WireMock proof for actor, internal credential and correlation headers, plus retry and circuit behavior.
