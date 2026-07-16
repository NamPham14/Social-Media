# Changelog

## Unreleased

### Added

- Idempotent add/remove reaction flows and single/batch counter queries.
- Target availability outbound port.
- PostgreSQL Testcontainers coverage for concurrent writes, transaction rollback, safe removal and legacy BOOKMARK migration.

### Removed

- `BOOKMARK`; bookmarks are owned by Post Service.

### Security

- Actor ID is read only from the trusted gateway header.
