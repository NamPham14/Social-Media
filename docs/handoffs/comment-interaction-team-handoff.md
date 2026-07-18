# Comment/Interaction integration handoff

This checklist tells other service owners exactly what Comment and Interaction need. Provider changes should stay in provider-owned PRs; Comment/Interaction consumer adapters are updated only after the contracts are accepted.

## P0 — API Gateway owner

- Route `/comment/**` and `/interaction/**` with the existing prefix removal and preserve `X-Correlation-Id` in request and response.
- Remove any client-supplied `X-Auth-User-Id`, then set exactly one value from the verified JWT subject. Reject a subject that is not a UUID.
- Do not expose `/internal/**` as a public route. Internal calls use Eureka/service networking.
- Verify 401 for missing/invalid JWT, 400 for malformed identifiers, and rate-limit command endpoints without breaking idempotent retries from clients.
- Add a Gateway integration test proving a forged actor header cannot override the JWT actor.

## P0 — Post Service owner

- Review and accept `CR-POST-001`.
- Implement `GET /internal/v1/posts/{postId}/availability?action=COMMENT|REACT` with an internal credential, correlation propagation and the agreed visibility/deletion policy.
- Return a classified 200 result for known unavailable business states; reserve 5xx for provider failure.
- Keep the current public endpoint during rollout. After provider tests pass, Comment/Interaction owners will switch their Feign adapters and then retire the temporary dependency.
- Decide whether availability also returns `ownerId`. Notification recipient resolution must be settled before event schemas are accepted.
- Publish a versioned, UUID-based `PostDeletedV1` through an outbox if the team wants existing comments/reactions hidden, archived or cleaned after deletion.

## P0 — Notification Service owner

- Review and accept `CR-NOTIFICATION-001`; the current `Long` model is incompatible with UUID-owned entities.
- Agree on recipient resolution for post comment, reply, post reaction and comment reaction. Recommended: the producer event contains an already-resolved `recipientId`; otherwise document the internal ownership lookup explicitly.
- Store and deduplicate `eventId`; suppress self-notifications; make handlers idempotent.
- Implement retry with a bounded policy and DLT/parking-lot handling for poison events.
- Persist UUID actor, recipient and target identifiers or introduce backward-compatible UUID columns/topics.
- Expose consumer lag, processing failures, retry and DLT metrics.

## P1 — Feed/BFF owner

- For a page of posts, call Comment `POST /api/v1/comments/counts/batch` once and Interaction `POST /api/v1/interactions/counters/batch` once. Do not call once per post.
- For the signed-in actor, obtain reaction state only for targets the UI needs. If this becomes N+1, request an actor-reaction batch contract instead of duplicating the ledger.
- Treat missing count rows as zero and preserve target UUID/type when merging results.
- Do not persist Comment/Interaction counters as a second source of truth unless a separate projection ADR defines reconciliation.

## P1 — Profile Service or BFF owner

- Comment returns author UUID, not display name/avatar. Provide a batch Profile lookup or a profile projection for all distinct author UUIDs on the page.
- Do not add profile snapshots to the Comment write model merely to render a page.

## P1 — Frontend owner

- Send JWT only; never send an authoritative actor ID in command bodies.
- Model LIKE and CLAP as independent toggles; both may be active simultaneously.
- Treat duplicate add and repeated remove as successful idempotent outcomes.
- Render deleted parents as `[deleted]` only when returned as placeholders; deleted leaves are omitted.
- Use batch count responses for feed rendering and retain the correlation header when reporting failures.

## P1 — Platform/DevOps owner

- Provide separate Comment and Interaction PostgreSQL databases and run each service's namespaced Flyway migrations.
- Supply the same non-empty `INTERNAL_SERVICE_TOKEN` through secret/config management; do not commit it.
- Register Post, Comment and Interaction in Eureka and retain the documented Feign timeout budget.
- Collect Actuator, Resilience4j and business metrics; alert on dependency circuit state and counter-update failures.
- When Phase 6 is accepted, provide Kafka ACLs/topics plus outbox relay monitoring; Kafka availability must not decide the database transaction outcome.

## Work that returns to Comment/Interaction owners after acceptance

1. Replace the temporary Post public-API adapters with the accepted internal availability contract.
2. Add outbox tables and relay only after event payload and recipient ownership are accepted.
3. Publish `CommentCreatedV1`, `CommentRepliedV1`, `CommentDeletedV1`, `ReactionCreatedV1` and `ReactionRemovedV1` only where a real consumer exists.
4. Add idempotent consumers for accepted `PostDeletedV1` and `CommentDeletedV1` cleanup/tombstone policies.
5. Extend E2E to Gateway → Post → Comment/Interaction → outbox/Kafka → Notification, including Kafka outage and duplicate delivery.

## Recommended rollout order

1. Gateway trust-boundary tests.
2. Post internal availability provider and tests.
3. Comment/Interaction Feign adapter switch.
4. UUID Notification schema, recipient decision and idempotent consumer.
5. Comment/Interaction outbox producers.
6. Deletion consumers and reconciliation policy.
7. Full-system E2E and failure drills.

## Copy/paste briefs for teammates

### Gateway teammate

```text
Please harden the Gateway contract for Comment and Interaction. Preserve X-Correlation-Id, strip any client-supplied X-Auth-User-Id, set exactly one actor UUID from the verified JWT subject, reject non-UUID subjects, and prevent public routing to /internal/**. Add an integration test proving a forged actor header cannot override the JWT actor. Keep provider changes in the Gateway PR and report the final route/header contract back to us.
```

### Post teammate

```text
Please review docs/integration/requests/CR-POST-001-comment-target-availability.md and implement the accepted provider-side internal availability endpoint for COMMENT and REACT actions. It must classify missing/deleted/visibility/business states, require an internal credential, propagate X-Correlation-Id, use UUIDs, and include provider contract tests. Please also decide whether ownerId belongs in this response for future notification recipient resolution and whether PostDeletedV1 will be emitted through an outbox. Do not remove the current public endpoint until Comment/Interaction confirm their Feign migration.
```

### Notification teammate

```text
Please review docs/integration/requests/CR-NOTIFICATION-001-comment-reaction-events.md. We need an accepted UUID event envelope, a clear recipient-resolution rule for post comment/reply and post/comment reaction, eventId deduplication, self-action suppression, bounded retry and DLT. Please provide the provider-owned schema/migration and consumer tests before Comment/Interaction add outbox producers; the existing Long contract is not compatible.
```

### Feed/BFF/Profile teammate

```text
For each feed page, compose Comment POST /api/v1/comments/counts/batch and Interaction POST /api/v1/interactions/counters/batch once per page, not once per post. Resolve comment author UUIDs through one batch Profile call or an approved projection. Treat missing counters as zero and keep Comment/Interaction as the source of truth. If actor-reaction state becomes N+1, request a batch contract instead of copying the ledger.
```

### DevOps teammate

```text
Please provision separate Comment and Interaction PostgreSQL databases, run their namespaced Flyway migrations, register Post/Comment/Interaction in Eureka, and inject the same non-empty INTERNAL_SERVICE_TOKEN from secret management. Collect Actuator/Resilience4j/business metrics. When the async CRs are accepted, add Kafka ACLs/topics and outbox relay monitoring without coupling Kafka availability to command database commits.
```
