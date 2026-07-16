# Changelog

## Unreleased

### Added

- Idempotent add/remove reaction flows and single/batch counter queries.
- Target availability outbound port.

### Removed

- `BOOKMARK`; bookmarks are owned by Post Service.

### Security

- Actor ID is read only from the trusted gateway header.
