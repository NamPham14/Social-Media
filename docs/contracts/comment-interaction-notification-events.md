# Comment/Interaction notification event contract V1

Comment and Interaction publish notification facts through transactional outboxes. Delivery is
at-least-once; Notification must treat `eventId` as its idempotency key. All identifiers are UUID
strings and timestamps are UTC ISO-8601 values.

| Topic | Key | Event type | When emitted |
| --- | --- | --- | --- |
| `comment-created-topic` | `commentId` | `CommentCreatedV1` | A root comment is committed for another user's post |
| `comment-replied-topic` | `commentId` | `CommentRepliedV1` | A reply is committed for another user's parent comment |
| `reaction-created-topic` | `interactionId` | `ReactionCreatedV1` | A new LIKE/CLAP ledger row is committed for another user's target |

Duplicate reaction requests and self-actions do not produce notification events. Removing a
reaction does not retract an already delivered notification and therefore has no V1 notification
event.

## CommentCreatedV1 and CommentRepliedV1

```json
{
  "eventId": "uuid",
  "eventType": "CommentCreatedV1",
  "version": 1,
  "occurredAt": "2026-07-20T16:00:00Z",
  "commentId": "uuid",
  "postId": "uuid",
  "parentCommentId": null,
  "actorId": "uuid",
  "recipientId": "uuid"
}
```

`CommentRepliedV1` uses the same schema with `eventType=CommentRepliedV1` and a non-null
`parentCommentId`. The recipient is the post owner for a root comment and the parent-comment owner
for a reply.

## ReactionCreatedV1

```json
{
  "eventId": "uuid",
  "eventType": "ReactionCreatedV1",
  "version": 1,
  "occurredAt": "2026-07-20T16:00:00Z",
  "interactionId": "uuid",
  "targetType": "POST",
  "targetId": "uuid",
  "reactionType": "LIKE",
  "actorId": "uuid",
  "recipientId": "uuid"
}
```

`targetType` is `POST` or `COMMENT`; `reactionType` is `LIKE` or `CLAP`. Display names and message
text are intentionally excluded because they are mutable presentation data owned outside these
bounded contexts.

## Consumer requirements

- Persist `eventId` under a unique constraint in the same transaction as the notification.
- Ignore an already processed `eventId` and suppress `actorId == recipientId` defensively.
- Validate `eventType`, `version`, UUID fields and enums; route poison records directly to a DLT.
- Use bounded retry for transient database/broker failures and expose consumer lag/retry/DLT metrics.
- Do not consume these topics until Notification supports UUID actor, recipient and target IDs.
