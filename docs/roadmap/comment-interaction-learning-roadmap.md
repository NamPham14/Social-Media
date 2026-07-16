# Lộ trình hoàn thiện Comment Service và Interaction Service

> Mục tiêu kép: hoàn thiện đúng nghiệp vụ hai bounded context và dùng quá trình đó để học Tactical DDD, Spring Cloud, giao tiếp đồng bộ/bất đồng bộ, resilience, observability và testing.

## 1. Nguyên tắc thực hiện

1. Nghiệp vụ quyết định kiến trúc; framework và pattern chỉ được thêm khi giải quyết một nhu cầu cụ thể.
2. `comment-service` và `interaction-service` là phạm vi code chính.
3. Không sửa trực tiếp service do thành viên khác sở hữu nếu chưa có change request/contract được đồng thuận.
4. Mỗi dữ liệu chỉ có một source of truth.
5. Domain không phụ thuộc Feign, Kafka, HTTP, Spring Security hoặc persistence framework.
6. Mọi outbound call phải có failure semantics rõ ràng trước khi thêm retry/circuit breaker/fallback.
7. Mọi event phải có owner, schema, version, event ID và consumer rõ ràng.
8. Mỗi phase phải có test và tài liệu; không để test đến cuối dự án.

## 2. Phạm vi và ownership đề xuất

### 2.1 Comment bounded context

Comment Service sở hữu:

- Nội dung comment và reply.
- Quan hệ comment–parent comment.
- Comment thuộc post nào.
- Tác giả, thời gian tạo/cập nhật và trạng thái deleted.
- Quyền sửa/xóa comment.
- Comment count chính xác theo post nếu dự án cần counter này.

Comment Service không sở hữu:

- Nội dung/trạng thái chính thức của Post.
- Tài khoản và Profile người dùng.
- Reaction của comment.
- Notification.

### 2.2 Interaction bounded context

Interaction Service sở hữu:

- Reaction ledger: user nào reaction lên target nào.
- Quy tắc duplicate/idempotency.
- Thêm, bỏ và thay đổi reaction.
- Reaction counter chính xác của Post/Comment.
- Trạng thái reaction hiện tại của một user.

Interaction Service không sở hữu:

- Nội dung Post/Comment.
- Profile người dùng.
- Notification.
- Bookmark nếu Post Service tiếp tục là owner của Bookmark.

### 2.3 Quyết định cần chốt trước khi code

Tạo ADR cho từng quyết định, trạng thái ban đầu là `PROPOSED`:

| ADR | Câu hỏi cần chốt | Khuyến nghị ban đầu |
| --- | --- | --- |
| ADR-001 | Bookmark thuộc Post hay Interaction? | Giữ tại Post; bỏ `BOOKMARK` khỏi Interaction |
| ADR-002 | Một user được LIKE và CLAP đồng thời không? | Chốt theo yêu cầu UI trước khi đổi unique constraint |
| ADR-003 | Xóa parent comment có giữ replies không? | Soft-delete parent, hiển thị placeholder nếu còn replies |
| ADR-004 | Reply được sâu bao nhiêu tầng? | Một tầng cho MVP, hoặc hỗ trợ tree có giới hạn rõ ràng |
| ADR-005 | Kiểm tra target bằng Feign hay local projection? | Feign ở giai đoạn học sync; projection là bài nâng cao |
| ADR-006 | Counter hiển thị từ API composition hay read model? | Batch API trước; denormalized read model khi làm feed |
| ADR-007 | Chuẩn ID liên service? | UUID trong domain và JSON contract |

Không bắt đầu phase giao tiếp liên service trước khi ADR-001, ADR-002, ADR-003 và ADR-005 được chấp nhận.

## 3. Kiến trúc code mục tiêu

Giữ phong cách Tactical DDD hiện tại, nhưng làm rõ dependency direction:

```text
api
  controller
  dto
  path
application
  command
  query
  usecase
  mapper
  port
    in
    out
domain
  model
    aggregate
    entity
    valueobject
  event
  exception
  repository
infrastructure
  persistence
    entity
    repository
    adapter
    mapper
  client
    feign
    config
  messaging
    kafka
      producer
      consumer
      event
      config
  security
  web
  config
```

Quy tắc dependency:

```text
api -> application -> domain
infrastructure -> application/domain
domain -> không phụ thuộc layer khác
```

### Tactical DDD cần thực hành

- Aggregate root bảo vệ invariant.
- Value object thay cho UUID/String rời rạc ở domain khi có ý nghĩa nghiệp vụ.
- Repository interface là outbound port, implementation nằm ở infrastructure.
- Command use case thay đổi trạng thái; query use case chỉ đọc.
- Domain exception biểu diễn lỗi nghiệp vụ, không ném exception HTTP/Feign từ domain.
- Domain event biểu diễn sự kiện đã xảy ra; integration event là contract phát ra ngoài service.
- Persistence entity không nên là domain aggregate. Có thể tách dần để tránh refactor quá lớn trong một lần.

## 4. Lộ trình theo phase

Mỗi phase nên là một PR nhỏ, có thể review và rollback độc lập.

### Phase 0 — Baseline và governance

**Mục tiêu học:** hiểu bounded context, ubiquitous language, ADR và contract ownership.

**Việc thực hiện:**

1. Viết ADR-001 đến ADR-007.
2. Lập glossary: Comment, Reply, Reaction, Target, Actor, Owner, Counter, Duplicate.
3. Ghi lại API/event hiện tại trước khi thay đổi.
4. Xác nhận UUID là kiểu ID chuẩn của hai service.
5. Tạo changelog riêng cho Comment và Interaction.
6. Ghi baseline build/test hiện tại.

**Deliverables:**

- `docs/adr/ADR-xxx-*.md`
- `docs/contracts/comment-api.md`
- `docs/contracts/interaction-api.md`
- `services/comment-service/CHANGELOG.md`
- `services/interaction-service/CHANGELOG.md`

**Definition of Done:**

- Ownership không còn mơ hồ.
- Bookmark chỉ có một owner trên tài liệu.
- Reaction semantics và comment deletion semantics được chấp nhận.
- Không sửa code service khác.

---

### Phase 1 — Authentication boundary và error model

**Mục tiêu học:** Gateway authentication, trust boundary, header propagation và exception mapping.

**Việc thực hiện:**

1. Lấy actor ID từ `X-Auth-User-Id`, không lấy `userId` từ body/query.
2. Parse UUID tại API boundary và tạo `AuthenticatedActor` hoặc `UserId` cho application/domain.
3. Xóa `userId` khỏi create request DTO sau khi contract được cập nhật.
4. Thiết kế domain exceptions:
   - `CommentNotFoundException`
   - `CommentAccessDeniedException`
   - `CommentAlreadyDeletedException`
   - `TargetNotFoundException`
   - `ReactionConflictException`
   - `DependencyUnavailableException`
5. Map lỗi thành HTTP status ổn định: 400, 403, 404, 409, 503.
6. Đảm bảo correlation ID được nhận, log và trả về response.

**Lưu ý cross-service:**

- Nếu sửa `common-web` để biến exception/filter thành auto-configuration, phải tạo change request riêng vì thay đổi đó ảnh hưởng toàn hệ thống.
- Phương án an toàn trong phạm vi là cấu hình tại hai service; phương án shared module chỉ làm sau khi team đồng thuận.

**Test bắt buộc:**

- Thiếu header -> 401/400 theo policy.
- Header không phải UUID -> 400.
- Body cố gửi user ID khác không thay đổi actor thật.
- Domain exceptions map đúng response code và trace ID.

**Definition of Done:** client không thể giả mạo actor bằng request body/query parameter.

---

### Phase 2 — Hoàn thiện Comment core use cases

**Mục tiêu học:** aggregate invariant, command/query separation, repository port và transaction boundary.

#### 2.1 Create top-level comment

- Validate content.
- Nhận authenticated actor.
- Validate target qua outbound port `PostAvailabilityPort`.
- Lưu trong một transaction.
- Chưa publish Kafka trực tiếp trong phase này.

#### 2.2 Reply to comment

- Parent phải tồn tại.
- Parent phải thuộc cùng post.
- Parent phải hợp lệ theo ADR-003/ADR-004.
- Phân biệt kết quả `COMMENT_CREATED` và `COMMENT_REPLIED` ở application/domain event.

#### 2.3 Edit comment

- Chỉ owner được sửa, trừ khi policy cho moderator.
- Không sửa comment đã xóa.
- Cập nhật `updatedAt`.

#### 2.4 Delete comment

- Idempotent hoặc conflict phải được quyết định rõ.
- Soft delete theo ADR-003.
- Không nhận actor từ query parameter.

#### 2.5 Query comment

- Lấy comment theo ID cho internal validation.
- List comment theo post có pagination.
- Quy định sort order.
- Trả replies theo strategy đã chọn; tránh load toàn bộ post discussion.

**Test bắt buộc:**

- Domain unit tests cho mọi invariant.
- Application tests mock repository và `PostAvailabilityPort`.
- Repository integration tests bằng PostgreSQL Testcontainers.
- Controller tests cho validation, actor và response contract.

**Definition of Done:** toàn bộ use case chạy độc lập với Kafka và có test cho happy path, invalid input, unauthorized và not found.

---

### Phase 3 — Hoàn thiện Interaction core use cases

**Mục tiêu học:** idempotency, concurrency, aggregate counter và transaction correctness.

#### 3.1 Create reaction

- Actor từ authenticated header.
- Validate target qua `TargetAvailabilityPort`.
- Unique constraint bám ADR-002.
- Duplicate hợp lệ trả trạng thái hiện tại, không che giấu mọi `DataIntegrityViolationException`.

#### 3.2 Remove reaction

- Idempotent khi reaction không tồn tại.
- Decrement đúng counter trong cùng transaction.
- Counter không bao giờ âm.

#### 3.3 Change reaction

- Chỉ triển khai nếu ADR-002 yêu cầu một reaction duy nhất trên mỗi target.
- Decrement loại cũ và increment loại mới atomically.

#### 3.4 Query reaction

- Lấy reaction hiện tại của actor trên một target.
- Lấy counter summary của một target.
- Batch counter endpoint cho nhiều target nhằm tránh N+1 trong Feed.

#### 3.5 Bookmark cleanup

- Nếu ADR-001 giữ Bookmark tại Post, xóa `BOOKMARK` khỏi Interaction domain/counter/migration.
- Không sửa Post Service.
- Ghi breaking change vào API contract/changelog nếu client từng dùng Interaction bookmark.

**Test bắt buộc:**

- 50–100 request đồng thời cùng reaction chỉ tạo một row và tăng counter một lần.
- Hai user khác nhau tăng counter hai lần.
- Remove lặp lại không làm counter âm.
- Lỗi constraint khác không bị trả nhầm thành duplicate.
- Transaction rollback không để ledger và counter lệch nhau.

**Definition of Done:** có bằng chứng test rằng ledger và counter nhất quán dưới concurrency.

---

### Phase 4 — Giao tiếp đồng bộ bằng OpenFeign

**Mục tiêu học:** service discovery, consumer contract, timeout, header propagation và failure classification.

Chỉ thêm Feign sau khi `PostAvailabilityPort`/`TargetAvailabilityPort` đã tồn tại. Application phụ thuộc port, không phụ thuộc `@FeignClient`.

#### 4.1 Comment -> Post

Nhu cầu nghiệp vụ: xác nhận post tồn tại, chưa bị xóa và actor được phép comment.

Contract mong muốn:

```text
GET /internal/posts/{postId}/comment-availability
-> postId, exists, commentable, reason
```

Nếu Post Service chưa có endpoint phù hợp:

1. Tạo `CR-POST-001`.
2. Ghi request/response schema và status codes.
3. Gửi owner Post Service review.
4. Trong Comment Service chỉ implement port + stub/adapter khi contract được chấp nhận.
5. Không tự sửa Post Service trong cùng PR.

#### 4.2 Interaction -> target provider

- Target POST: kiểm tra qua Post provider.
- Target COMMENT: Interaction gọi internal availability endpoint do Comment Service sở hữu; phần này nằm trong phạm vi hai service.

#### 4.3 Header interceptor

Propagate tối thiểu:

- `X-Correlation-Id`
- Internal service credential/header theo policy của team
- Actor ID chỉ khi contract thực sự cần authorization context

Không forward toàn bộ inbound headers.

**Test bắt buộc:**

- WireMock/MockWebServer contract tests.
- Eureka name được dùng thay cho hard-coded host trong runtime config.
- 404 target, 403 not commentable, 5xx và timeout được phân loại khác nhau.

**Definition of Done:** Feign chỉ nằm trong infrastructure; use case test được mà không cần chạy remote service.

---

### Phase 5 — Resilience cho outbound calls

**Mục tiêu học:** timeout budget, retry safety, circuit breaker state và fallback semantics.

#### Timeout

- Đặt connect timeout và read timeout rõ ràng.
- Không dùng timeout mặc định vô hạn.
- Ghi lý do chọn con số trong config/ADR, không copy máy móc từ service khác.

#### Retry

- Chỉ retry request đọc/idempotent.
- Retry lỗi mạng, timeout, 502/503/504 nếu phù hợp.
- Không retry 400/401/403/404/409.
- Số lần nhỏ, có backoff; tổng thời gian không vượt request budget.

#### Circuit breaker

- Một instance theo dependency/use case, ví dụ `postAvailability`.
- Ghi sliding window, failure threshold và open-state duration.
- Expose health/metrics để quan sát state transition.

#### Fallback

- Create Comment/Reaction phải fail closed khi không xác minh được target: trả 503.
- Không fallback thành “target tồn tại”.
- Query dữ liệu cục bộ không cần bị chặn chỉ vì Post Service đang down.

#### TimeLimiter

- Chỉ thêm nếu call model hỗ trợ async/reactive và có lý do cụ thể.
- Với blocking Feign, connect/read timeout là lớp đầu tiên; không thêm annotation chỉ để đủ danh sách.

**Test bắt buộc:** timeout, retry count, circuit open/half-open/closed và fallback 503.

**Definition of Done:** failure behavior được chứng minh bằng test và metric, không chỉ bằng annotation.

---

### Phase 6 — Event-driven integration

**Mục tiêu học:** eventual consistency, integration event, outbox, idempotent consumer và DLT.

#### 6.1 Chuẩn event envelope

```json
{
  "eventId": "uuid",
  "eventType": "CommentCreated",
  "eventVersion": 1,
  "occurredAt": "ISO-8601 UTC",
  "producer": "comment-service",
  "correlationId": "uuid",
  "aggregateId": "uuid",
  "payload": {}
}
```

ID nghiệp vụ trong payload dùng UUID string. Không dùng `Long` cho cùng một entity ở service khác.

#### 6.2 Events do Comment phát

- `CommentCreatedV1`
- `CommentRepliedV1`
- `CommentUpdatedV1` chỉ khi có consumer thật sự cần
- `CommentDeletedV1`

#### 6.3 Events do Interaction phát

- `ReactionCreatedV1`
- `ReactionRemovedV1`
- `ReactionChangedV1` nếu use case tồn tại

#### 6.4 Events hai service consume

- `PostDeletedV1`
- `PostVisibilityChangedV1` nếu policy yêu cầu
- Interaction consume `CommentDeletedV1`

#### 6.5 Delivery reliability

Triển khai theo thứ tự học:

1. Producer/consumer cơ bản trong local environment.
2. Event ID và consumer deduplication.
3. Retry topic/DLT.
4. Transactional outbox để tránh DB commit thành công nhưng publish thất bại.
5. Outbox relay/retry và cleanup policy.

#### 6.6 Tích hợp Notification

Notification Service hiện có contract dùng `Long`, không tương thích UUID. Không sửa trực tiếp.

Tạo change request `CR-NOTIFICATION-001` gồm:

- Event schema UUID V1.
- Mapping `POST_COMMENTED`, `COMMENT_REPLIED`, `POST_LIKED`, `COMMENT_LIKED`.
- Quy tắc không thông báo self-action.
- Consumer idempotency theo `eventId`.
- Migration/backward compatibility nếu consumer cũ vẫn chạy.

**Test bắt buộc:** embedded/Testcontainers Kafka, duplicate event, consumer retry, DLT và outbox replay.

**Definition of Done:** restart producer/consumer hoặc gửi event lặp không tạo dữ liệu sai/trùng.

---

### Phase 7 — Read model và API composition

**Mục tiêu học:** tránh distributed join/N+1 và hiểu controlled data duplication.

1. Interaction cung cấp batch counter API.
2. Comment cung cấp comment count/batch API nếu cần.
3. Không gọi Profile Service một lần cho mỗi comment.
4. Chọn một trong các chiến lược hiển thị Feed:
   - Gateway/BFF gọi batch APIs.
   - Feed/Post projection consume events và giữ counter cache.
5. Nếu cần author display data, dùng batch Profile API hoặc projection theo event; Comment domain chỉ giữ `userId`.

Không thêm denormalized table trước khi có use case Feed thực tế và benchmark/query evidence.

**Definition of Done:** số remote calls không tăng tuyến tính theo số post/comment trong một trang.

---

### Phase 8 — Observability và vận hành

**Mục tiêu học:** correlation, structured logs, metrics, health và troubleshooting distributed flow.

1. Correlation ID xuyên Gateway -> Feign -> Kafka event -> consumer log.
2. Structured log fields: service, trace/correlation ID, actor ID đã mask nếu cần, aggregate ID, event ID.
3. Không log JWT, password, secret hoặc toàn bộ dữ liệu cá nhân.
4. Actuator health cho DB, Kafka và circuit breaker.
5. Metrics tối thiểu:
   - comment create/delete latency và error count
   - reaction create/remove latency
   - duplicate reaction count
   - counter update failure
   - Feign timeout/retry/circuit state
   - Kafka publish/consume/DLT count
   - outbox pending/oldest age
6. Viết runbook cho Post unavailable, Kafka unavailable, counter mismatch và poison event.

**Definition of Done:** có thể lần theo một request từ Gateway đến DB/event bằng correlation/event ID.

---

### Phase 9 — End-to-end và hardening

**Mục tiêu học:** kiểm chứng behavior toàn hệ thống và failure scenarios.

Kịch bản E2E tối thiểu:

1. User A tạo Post; User B comment; notification được tạo cho A.
2. User C reply B; notification đúng recipient.
3. User B double-click Like; chỉ một reaction và một counter increment.
4. User B unlike hai lần; counter không âm.
5. Post bị xóa; comment/reaction không còn xuất hiện theo policy.
6. Post Service down; create comment/reaction trả 503 trong timeout budget.
7. Kafka down; dữ liệu nghiệp vụ vẫn commit cùng outbox và được publish lại khi Kafka phục hồi.
8. Duplicate Kafka delivery không tạo duplicate notification/read model.
9. Actor giả mạo trong body bị bỏ qua/từ chối.

**Definition of Done cuối:**

- Build/test hai module xanh.
- Không còn test chỉ `contextLoads()` làm bằng chứng duy nhất.
- API/event contract đã version hóa.
- Không có ownership trùng.
- Không có secret hard-coded mới.
- Có ADR, changelog, integration request và runbook.

## 5. Chiến lược test pyramid

| Loại test | Mục tiêu | Công cụ gợi ý |
| --- | --- | --- |
| Domain unit | Invariant, state transition | JUnit 5, AssertJ |
| Application unit | Use case orchestration, ports | JUnit 5, Mockito |
| Web slice | Validation, auth header, error response | MockMvc |
| Persistence integration | Query, constraint, transaction | Testcontainers PostgreSQL |
| Feign integration | Contract, timeout, error mapping | WireMock/MockWebServer |
| Kafka integration | Serialize, consume, retry, DLT | Testcontainers Kafka |
| Concurrency | Duplicate reaction/counter | Executor-based integration test |
| E2E | Cross-service business flow | Docker Compose + Bruno/Postman |

Không mock mọi thứ trong integration test; unique constraint và native atomic query phải chạy trên PostgreSQL thật.

## 6. Quy trình khi cần tác động service khác

### 6.1 Không sửa ngay

Khi phát hiện cần thay đổi Post, Notification, Gateway, Profile hoặc shared module:

1. Tạo integration change request.
2. Đánh dấu `PROPOSED`.
3. Gửi owner service review.
4. Chỉ implement consumer side dựa trên contract đã `ACCEPTED`.
5. Thay đổi provider nằm trong PR/commit riêng do owner thực hiện hoặc đồng ý rõ ràng.

### 6.2 Template change request

Đường dẫn đề xuất:

```text
docs/integration/requests/CR-<SERVICE>-<NUMBER>-<short-name>.md
```

Nội dung bắt buộc:

```markdown
# CR-POST-001: Comment availability contract

- Status: PROPOSED | ACCEPTED | IMPLEMENTED | REJECTED
- Requester: comment-service
- Provider owner: post-service
- Motivation/business use case:
- Current behavior:
- Proposed API/event contract:
- Request/response examples:
- Error/status semantics:
- Authentication/internal authorization:
- Backward compatibility:
- Database/migration impact:
- Rollout order:
- Rollback plan:
- Consumer tests:
- Provider tests:
- Observability:
- Decision/date/approver:
```

### 6.3 Contract ownership

- API provider sở hữu API contract, consumer có consumer-driven contract test.
- Event producer sở hữu event schema; consumers không tự thay đổi schema.
- Breaking change phải tạo version mới hoặc có migration window.
- Không copy các Java event class khác nhau nhưng cùng topic sang nhiều service mà không có schema tài liệu chung.

### 6.4 Changelog

Mỗi thay đổi ghi:

- `Added`
- `Changed`
- `Fixed`
- `Deprecated`
- `Removed`
- `Security`
- Migration/compatibility notes

Changelog không thay thế ADR hoặc contract; nó chỉ là lịch sử thay đổi dễ đọc.

## 7. Thứ tự PR/commit khuyến nghị

1. `docs: define comment and interaction bounded contexts`
2. `test: add domain behavior baselines`
3. `feat: secure authenticated actor boundary`
4. `feat(comment): complete comment and reply use cases`
5. `feat(comment): add edit delete and paginated queries`
6. `feat(interaction): finalize reaction ownership and semantics`
7. `feat(interaction): add remove reaction and counter queries`
8. `test(interaction): verify concurrent idempotency`
9. `feat: add target availability outbound ports`
10. `feat: implement Feign adapters and contract tests`
11. `feat: add timeout retry and circuit breaker policies`
12. `docs: propose notification and post integration contracts`
13. `feat: publish versioned integration events`
14. `feat: add idempotent consumers retry and DLT`
15. `feat: add transactional outbox`
16. `ops: add metrics health and runbooks`
17. `test: add cross-service end-to-end scenarios`

Không gộp domain refactor, database migration, Feign, Kafka và resilience vào một PR lớn.

## 8. Nhịp học đề xuất

| Chặng | Nội dung học | Sản phẩm quan sát được |
| --- | --- | --- |
| 1 | Bounded context, ADR, ubiquitous language | Ownership và business rules rõ ràng |
| 2 | Aggregate, VO, repository port | Domain tests chạy không cần Spring |
| 3 | Application use case, transaction | Comment/Interaction core hoàn chỉnh |
| 4 | Security boundary, exception | Không spoof actor; lỗi API nhất quán |
| 5 | Feign, discovery, contract | Sync validation hoạt động và test được |
| 6 | Timeout, retry, circuit breaker | Dependency failure có behavior rõ |
| 7 | Kafka, idempotency, DLT | Event flow chịu duplicate/failure |
| 8 | Outbox, eventual consistency | Không mất event sau DB commit |
| 9 | Metrics, tracing, runbook | Debug được distributed flow |
| 10 | E2E và failure testing | Bằng chứng hoàn thành thay vì cảm tính |

Sau mỗi chặng, ghi lại ba câu:

1. Vấn đề nghiệp vụ/kỹ thuật nào vừa giải quyết?
2. Pattern vừa dùng đem lại lợi ích gì và trade-off gì?
3. Nếu bỏ pattern đó, hệ thống sẽ hỏng hoặc khó vận hành ở đâu?

## 9. Checklist review cho mỗi PR

### Domain

- Invariant nằm trong aggregate/value object phù hợp?
- Có để annotation HTTP/Feign/Kafka lọt vào domain không?
- Exception có ý nghĩa nghiệp vụ không?
- Ownership có bị trùng service khác không?

### Application

- Use case có một mục đích rõ ràng?
- Transaction boundary đúng chỗ?
- Phụ thuộc remote system qua port?
- Command và query có bị trộn không?

### Infrastructure

- Timeout/retry/circuit chỉ áp dụng cho outbound call cần thiết?
- Retry có an toàn/idempotent?
- Event có version/event ID?
- Consumer có idempotent và DLT?

### API/Security

- Actor lấy từ trusted boundary?
- Validation và status code đúng?
- Internal endpoint có được bảo vệ?
- Contract có backward compatible?

### Test/Docs

- Có test cho failure path và concurrency?
- ADR/contract/changelog đã cập nhật?
- Có ảnh hưởng service khác và change request chưa?
- Có migration/rollback plan chưa?

## 10. Điểm dừng hợp lý theo mục tiêu môn học

Nếu thời gian giới hạn, hoàn thành đến Phase 5 đã đủ minh họa tốt:

- Tactical DDD đúng ranh giới.
- Comment và reaction core hoàn chỉnh.
- Security boundary đúng.
- Feign + Eureka.
- Timeout + Retry + Circuit Breaker có test.

Sau đó chọn một vertical slice async hoàn chỉnh thay vì làm nhiều event nửa vời:

```text
Create Reaction
-> DB transaction + outbox
-> ReactionCreatedV1
-> Notification consumer idempotent
-> retry/DLT
-> E2E test
```

Một flow end-to-end đáng tin cậy có giá trị học tập cao hơn việc khai báo nhiều topic/annotation nhưng không xử lý failure.
