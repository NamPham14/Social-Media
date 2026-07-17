# Changelog

## Unreleased

### Added

- Authenticated actor boundary, stable domain errors, reply validation, edit/delete/get and paginated discussion APIs.
- Internal comment availability contract and target validation port.
- PostgreSQL Testcontainers coverage for Flyway ownership, deleted-parent visibility and exact pagination.
- Correlation-aware create/delete command logs and Micrometer latency/error metrics with bounded tags.
- Single and batch active-comment count APIs for Feed/BFF composition, backed by one grouped query and a partial PostgreSQL index.
- Cross-service synchronous E2E coverage for the owned Comment availability contract.

### Changed

- Comment commands no longer accept actor IDs from body/query.
- Deleted parents are returned as `[deleted]` placeholders only while replies exist.
- Post availability 403/404 and other 4xx responses are classified as non-retriable outcomes and excluded from circuit failure metrics.
- Namespaced Flyway resources under the Comment bounded context so composed test/runtime classpaths cannot discover another service's migrations.

### Fixed

- Declared the Post Feign path variable explicitly so the client can start without Java parameter-name metadata.
- Declared Comment controller path variables explicitly so endpoints do not depend on Java parameter-name metadata.
- Aligned the Docker image port with `server.port` and required the shared internal token in Compose.
- Added WireMock proof for outbound headers, status mapping, timeout, retry budget and circuit recovery.
