# DANH SÁCH NHIỆM VỤ TIẾP THEO - SOCIAL MEDIA PLATFORM (DDD & MICROSERVICES)

Chào bạn! Đây là bản lộ trình chi tiết để bạn có thể tiếp tục thực hiện dự án Social Media theo đúng chuẩn DDD và Microservices sau khi tắt chat.

---

## GIAI ĐOẠN 2: USER DOMAIN (TIẾP THEO)

### 📌 Bài 8: Kết nối Identity và Profile (Feign Client)
**Mục tiêu:** Khi đăng ký User thành công bên Identity, Profile sẽ tự động được tạo bên Profile Service.
- [ ] **Tại identity-service:**
    - [ ] Thêm dependency `spring-cloud-starter-openfeign` vào `pom.xml`.
    - [ ] Thêm annotation `@EnableFeignClients` vào class chạy chính (`IdentityServiceApplication`).
    - [ ] Tạo interface `ProfileClient` trong package `infrastructure.client` để định nghĩa việc gọi API sang Profile Service.
    - [ ] Cập nhật `RegisterUseCase`: Inject `ProfileClient` và gọi method tạo profile ngay sau khi lưu User thành công.
- [ ] **Kiểm tra:** Đăng ký 1 user qua Postman và kiểm tra xem cả 2 DB (identity và profile) đều có dữ liệu chưa.

### 📌 Bài 9: API Lấy và Cập nhật hồ sơ (Profile Service)
- [ ] **Tại profile-service:**
    - [ ] Viết `GetProfileUseCase`: Lấy thông tin dựa trên ID.
    - [ ] Viết `UpdateProfileUseCase`: Cho phép người dùng cập nhật `fullName`, `bio`, `avatarUrl`.
    - [ ] Bổ sung các endpoint tương ứng vào `ProfileController`.
    - [ ] **Lưu ý:** Sử dụng `@PreAuthorize` để đảm bảo người dùng chỉ được sửa hồ sơ của chính mình.

---

## GIAI ĐOẠN 4: HẠ TẦNG MICROSERVICES (INFRASTRUCTURE)

### 📌 Bài 10: Service Discovery với Eureka Server
- [ ] **Tại infra/eureka-server:**
    - [ ] Thêm dependency `spring-cloud-starter-netflix-eureka-server`.
    - [ ] Thêm `@EnableEurekaServer` vào class chính.
    - [ ] Cấu hình `application.yml` để server không tự đăng ký chính nó.
- [ ] **Tại Identity & Profile Service:**
    - [ ] Thêm dependency `spring-cloud-starter-netflix-eureka-client`.
    - [ ] Thêm `@EnableDiscoveryClient`.
    - [ ] Cấu hình `eureka.client.service-url.defaultZone` trỏ về server.

### 📌 Bài 11: API Gateway & Routing
- [ ] **Tại infra/api-gateway:**
    - [ ] Thêm dependency `spring-cloud-starter-gateway`.
    - [ ] Cấu hình Routing trong `application.yml` để điều hướng:
        - `/identity/**` -> `identity-service`
        - `/profile/**` -> `profile-service`
- [ ] **Kiểm tra:** Thử gọi API qua cổng của Gateway (thường là 8080) thay vì gọi trực tiếp vào service.

### 📌 Bài 12: Gateway Security (Chốt chặn cuối cùng)
- [ ] **Tại api-gateway:**
    - [ ] Tái sử dụng module `common-security` (add vào pom).
    - [ ] Viết một `AuthenticationFilter` (Global Filter) để kiểm tra JWT Token ngay tại Gateway.
    - [ ] Nếu token hợp lệ, forward request kèm theo thông tin `userId` vào header. Nếu không, trả về lỗi 401 (sử dụng `ApiResponse` chuẩn).

---

## GIAI ĐOẠN MỞ RỘNG (DÀNH CHO TEAM)

### 📌 Bài 13: Post Service & Interaction (Dựa trên cấu trúc đã học)
- [ ] Áp dụng đúng quy trình DDD đã làm cho Identity/Profile để xây dựng Post Service.
- [ ] Sử dụng Kafka (nếu cần) để thông báo khi có bài viết mới.

---

### 👨‍🏫 Lời nhắc của Thầy:
- **Nguyên tắc "Đừng vội":** Hãy làm xong Task 8 và test thật kỹ bằng Postman trước khi nhảy sang Gateway.
- **Log là bạn thân:** Luôn dùng `@Slf4j` để in ra các bước xử lý, giúp bạn dễ dàng tìm lỗi (debug) khi hệ thống Microservices ngày càng phức tạp.
- **Tuân thủ DDD:** Dù vội đến đâu, hãy nhớ luôn để Domain là "vùng cấm", không cho các thư viện bên ngoài xâm lấn vào.

Chúc bạn hoàn thành dự án xuất sắc! Hẹn gặp lại bạn trong những lần trao đổi tới.
