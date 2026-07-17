# Changelog

## Unreleased

### Added

- Idempotent add/remove reaction flows and single/batch counter queries.
- Target availability outbound port.
- PostgreSQL Testcontainers coverage for concurrent writes, transaction rollback, safe removal and legacy BOOKMARK migration.
- Correlation-aware reaction logs and Micrometer command, duplicate and counter-failure metrics with bounded tags.

### Removed

- `BOOKMARK`; bookmarks are owned by Post Service.

### Security

- Actor ID is read only from the trusted gateway header.

### Changed

- Target-provider 4xx responses are non-retriable and excluded from circuit failure metrics; 403 Post responses remain reaction conflicts.

### Fixed

- Declared Feign path variables explicitly so Post and Comment clients can start without Java parameter-name metadata.
- Declared Interaction controller path variables explicitly and required the shared internal token in Compose.
- Applied Feign timeout settings by client context ID while preserving Eureka service names.
- Added WireMock proof for actor, internal credential and correlation headers, plus retry and circuit behavior.
