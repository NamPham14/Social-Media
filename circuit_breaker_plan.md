# Kế hoạch Triển khai Circuit Breaker cho Identity Service

Dưới đây là kế hoạch 5 bước chi tiết để đưa Circuit Breaker vào `identity-service` nhằm bảo vệ nó khi gọi sang `profile-service`.

### Bước 1: Kiểm tra và bổ sung thư viện (Dependencies)
- **Mục tiêu:** Đảm bảo `identity-service` đã có đủ vũ khí để chạy Resilience4j.
- **Hành động:** Kiểm tra file `pom.xml` của `identity-service`. Thêm thư viện `spring-cloud-starter-circuitbreaker-resilience4j` (và `spring-boot-starter-aop` nếu chưa có, vì các annotation như `@CircuitBreaker` cần AOP để hoạt động).

### Bước 2: Khởi tạo Cấu hình Resilience (ResilienceConfig)
- **Mục tiêu:** Khai báo các thông số chịu lỗi (ngưỡng ngắt mạch, thời gian timeout, retry).
- **Hành động:** Tạo một file `ResilienceConfig.java` tại `infrastructure/client/config/` của `identity-service` (mô hình chuẩn giống hệt như bạn đã làm ở `post-service`).
- **Nội dung:** Khai báo 2 Bean:
  - `profileRetry`: Cấu hình thử lại (Retry) 3 lần cho các lỗi do chập chờn mạng (Timeout, 502, 503, 504).
  - `profileCircuitBreaker`: Cấu hình ngắt mạch dựa trên số lượng (Count-based), ngắt mạch nếu lỗi 50% hoặc chậm quá 3s.

### Bước 3: Xây dựng lớp lá chắn `ProfileServiceHelper` (Wrapper)
- **Mục tiêu:** Tách biệt logic gọi ngoại tuyến và logic dự phòng (fallback) ra khỏi logic nghiệp vụ chính, tuân thủ nguyên tắc SOLID.
- **Hành động:** Tạo lớp `ProfileServiceHelper.java` (gắn `@Component`) nằm cùng thư mục với `ProfileClient`.
- **Nội dung:** 
  - Inject `ProfileClient` vào lớp này.
  - Viết hàm `createSafeProfile` bọc quanh lệnh `profileClient.createProfile(...)`.
  - Gắn annotation `@Retry(name = "profileRetry")` và `@CircuitBreaker(name = "profileCircuitBreaker", fallbackMethod = "fallbackCreateProfile")` lên hàm này.

### Bước 4: Viết kịch bản dự phòng (Fallback Logic)
- **Mục tiêu:** Xử lý êm đẹp khi `profile-service` sập, giữ cho luồng tạo tài khoản của `identity-service` không bị văng lỗi màn hình trắng.
- **Hành động:** Viết hàm `fallbackCreateProfile` bên trong `ProfileServiceHelper`.
- **Logic:** 
  - Log ra lỗi nguyên nhân gốc để dev dễ trace.
  - Nếu lỗi là do timeout hoặc sập mạng, ta không ném 500. Tuỳ theo yêu cầu hệ thống:
    - *Cách A:* Trả về một Response giả lập báo hiệu "Thành công một phần" (User tạo được nhưng Profile sẽ tạo sau).
    - *Cách B:* Vẫn quăng lỗi nhưng là một lỗi thân thiện (VD: `ServiceUnavailableException`) đã được định nghĩa để GlobalExceptionHandler bắt và trả về Http Status `503` thân thiện cho Frontend.
    - *Cách C (Tối ưu nhất cho tương lai):* Ném một Event vào Message Queue (Kafka) báo "Tạo Profile bị lỡ hẹn" để một Job chạy ngầm tạo lại sau. Tạm thời ở bước này, mình sẽ cài đặt theo Cách B (Fail-Fast tiêu chuẩn).

### Bước 5: Cập nhật luồng Nghiệp vụ chính (Use Case / Service)
- **Mục tiêu:** Ráp lá chắn vào luồng chạy thực tế.
- **Hành động:** 
  - Tìm lớp UseCase/Service đang phụ trách việc đăng ký User (nơi đang inject trực tiếp `ProfileClient`).
  - Thay thế việc gọi trực tiếp `ProfileClient` bằng việc gọi qua `ProfileServiceHelper.createSafeProfile()`.
