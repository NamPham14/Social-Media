# Changelog

## Unreleased

### Added

- Authenticated actor boundary, stable domain errors, reply validation, edit/delete/get and paginated discussion APIs.
- Internal comment availability contract and target validation port.
- PostgreSQL Testcontainers coverage for Flyway ownership, deleted-parent visibility and exact pagination.

### Changed

- Comment commands no longer accept actor IDs from body/query.
- Deleted parents are returned as `[deleted]` placeholders only while replies exist.
