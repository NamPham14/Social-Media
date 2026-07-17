# PlantText diagrams for Comment and Interaction

Open PlantText, replace the editor contents with one complete `.puml` file (including `@startuml` and `@enduml`), then refresh/render the diagram.

Recommended viewing order:

1. `comment-interaction-system-overview.puml` — ownership, databases and all current/future dependencies.
2. `comment-command-flow.puml` — create/reply, error branches, edit/delete and local reads.
3. `interaction-command-flow.puml` — target validation, ledger/counter transaction, duplicate/remove behavior and provider outage.
4. `comment-interaction-future-events.puml` — proposed outbox, Kafka, deletion propagation and idempotent Notification flow.

Solid arrows in the overview are implemented. Dotted arrows and the entire future-events diagram are proposed cross-team work, not current runtime behavior.
