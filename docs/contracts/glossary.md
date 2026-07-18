# Comment and Interaction glossary

- **Comment**: content authored directly on a post.
- **Reply**: a comment referencing one top-level parent comment.
- **Reaction**: one `LIKE` or `CLAP` applied by an actor to a target.
- **Target**: a Post or Comment identified by UUID.
- **Actor**: authenticated user from `X-Auth-User-Id`; never accepted from a body/query field.
- **Owner**: actor who authored a comment.
- **Counter**: exact per-target totals derived transactionally from the reaction ledger.
- **Duplicate**: an already-active identical actor/target/reaction tuple; treated idempotently.
