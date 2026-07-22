# CR-POST-001: Comment and reaction target availability

- Status: PROPOSED
- Requester: comment-service, interaction-service
- Provider owner: post-service
- Motivation/business use case: Prevent commands and authenticated reads for missing, deleted, or invisible posts, while allowing anonymous reads only for public posts.
- Current behavior: consumers temporarily call the existing `GET /api/v1/posts/{postId}` API through an outbound port and treat only `PUBLIC` as available. Anonymous Comment reads cannot yet ask Post to classify visibility.
- Proposed contract: `GET /internal/v1/posts/{postId}/availability?action=COMMENT|REACT`
- Request actor: optional `X-Auth-User-Id`; absent means anonymous. It must never be trusted directly from an internet client.
- Success response: `{"targetId":"uuid","ownerId":"uuid","exists":true,"available":true,"visibility":"PUBLIC","reason":null}`
- Status semantics: 200 for a classified result; 5xx/timeout means dependency unavailable.
- Policy: anonymous is available only for `PUBLIC`; authenticated availability follows Post-owned visibility rules (public, owner, accepted audience/friendship). Deleted/hidden posts are unavailable.
- Authentication: required internal service credential plus optional trusted actor header.
- Backward compatibility: additive internal endpoint.
- Rollout order: provider, provider tests, consumer adapters, enable validation.
- Rollback plan: disable consumer Feign adapter and fail closed; do not assume existence.
- Consumer tests: 200 available/unavailable, 404 mapping, timeout and 5xx.
- Provider tests: anonymous/public, anonymous/private, owner/private, allowed/denied audience, deleted, and COMMENT/REACT policy matrix.
- Observability: correlation ID propagated; latency/error metrics by action.
- Decision/date/approver: pending Post owner review.
