# CR-POST-001: Comment and reaction target availability

- Status: PROPOSED
- Requester: comment-service, interaction-service
- Provider owner: post-service
- Motivation/business use case: Prevent commands for missing/deleted/non-commentable posts.
- Current behavior: consumers temporarily call the existing `GET /api/v1/posts/{postId}` API through an outbound port and treat only `PUBLIC` as available.
- Proposed contract: `GET /internal/v1/posts/{postId}/availability?action=COMMENT|REACT`
- Success response: `{"targetId":"uuid","exists":true,"available":true,"reason":null}`
- Status semantics: 200 for a classified result; 5xx/timeout means dependency unavailable.
- Authentication: internal service credential; actor header only if Post authorization requires it.
- Backward compatibility: additive internal endpoint.
- Rollout order: provider, provider tests, consumer adapters, enable validation.
- Rollback plan: disable consumer Feign adapter and fail closed; do not assume existence.
- Consumer tests: 200 available/unavailable, 404 mapping, timeout and 5xx.
- Provider tests: visibility/deleted/action policy matrix.
- Observability: correlation ID propagated; latency/error metrics by action.
- Decision/date/approver: pending Post owner review.
