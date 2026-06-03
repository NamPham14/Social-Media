# TÀI LIỆU HƯỚNG DẪN KIẾN TRÚC MICROSERVICES (MSS301)

Tài liệu này tổng hợp các kiến thức cốt lõi về hệ thống Microservices cho dự án **Social Media Platform (FUBlog)**.

---

## 1. Phân biệt các thành phần cốt lõi

Trong hệ thống Spring Cloud, chúng ta có 3 thành phần quan trọng xử lý các bài toán khác nhau:

### A. API Gateway (Lễ tân & Bảo vệ)
*   **Vị trí:** Đứng trước toàn bộ hệ thống (ngoài cùng).
*   **Chức năng:** 
    *   **Định tuyến (Routing):** Chuyển request từ Client (Web/App) đến đúng Service cần thiết.
    *   **Xác thực (Authentication):** Kiểm tra JWT Token ngay tại cửa ngõ.
*   **Giao tiếp:** Đồng bộ (Synchronous).
*   **Ẩn dụ:** Người lễ tân tòa nhà, ai muốn vào đều phải qua đây để kiểm tra thẻ và chỉ đường.

### B. Eureka Server (Danh bạ điện thoại)
*   **Vị trí:** Nằm bên trong hệ thống.
*   **Chức năng:**
    *   **Service Discovery:** Quản lý danh sách IP và Port của tất cả các Microservices đang chạy.
    *   **Health Check:** Biết được service nào đang sống, service nào đã chết.
*   **Giao tiếp:** Đồng bộ (Synchronous).
*   **Ẩn dụ:** Cuốn danh bạ nội bộ, giúp các phòng ban (services) tìm thấy số điện thoại (IP) của nhau để gọi.

### C. RabbitMQ / Kafka (Bưu điện & Loa phát thanh)
*   **Vị trí:** Kênh truyền tin giữa các Microservices.
*   **Chức năng:**
    *   **Event-Driven:** Cho phép các service bắn "Sự kiện" ra ngoài mà không cần chờ phản hồi.
    *   **Decoupling:** Các service không cần biết nhau, chỉ cần gửi/nhận tin nhắn qua hàng đợi (Queue).
*   **Giao tiếp:** Bất đồng bộ (Asynchronous).
*   **Ẩn dụ:** Hệ thống bưu điện hoặc loa thông báo, nhắn tin xong thì đi làm việc khác, người nhận sẽ xử lý sau.

---

## 2. Bảng so sánh tóm tắt

| Đặc điểm | API Gateway | Eureka | RabbitMQ/Kafka |
| :--- | :--- | :--- | :--- |
| **Đối tượng gọi** | Client gọi vào hệ thống | Service gọi Service | Service báo tin cho Service |
| **IP/Address** | Chỉ cần 1 IP duy nhất | Tự động cập nhật IP | Không dùng IP, dùng Queue/Topic |
| **Trạng thái** | Phải chờ (Wait) | Phải chờ (Wait) | Không cần chờ (Fire & Forget) |

---

## 3. Kiến trúc Database-per-service
Nguyên tắc vàng của Microservices mà dự án đang áp dụng:
1.  **Tính độc lập:** Mỗi service sở hữu Database riêng (PostgreSQL/MySQL).
2.  **Không join database:** Tuyệt đối không viết câu lệnh SQL Join giữa các Database của service khác nhau.
3.  **Dùng ID làm tham chiếu:** Chỉ lưu ID của User hoặc Article ở các database khác, không lưu toàn bộ thông tin.

---

## 4. Saga Pattern - Bài toán Xóa bài viết (Article Delete Saga)
Để đảm bảo tính nhất quán khi xóa một bài viết mà không dùng khóa ngoại (FK) chéo Database:

1.  **Bước 1 (Article Service):** Đổi status bài viết thành `DELETE_PENDING`. Bắn event `ArticleDeleteRequested`.
2.  **Bước 2 (Comment Service):** Nghe event, thực hiện xóa (hoặc ẩn) tất cả comment của bài viết đó.
3.  **Bước 3 (Interaction Service):** Nghe event, thực hiện xóa Likes/Claps.
4.  **Bước 4 (Saga Coordinator):** Nếu tất cả báo thành công -> `Article Service` đổi status sang `DELETED`.
5.  **Rollback:** Nếu bất kỳ service nào lỗi (ví dụ Comment Service chết) -> Bắn event rollback để `Article Service` khôi phục bài viết về `PUBLISHED`.

---

## 5. Các Task quan trọng cho nhóm

*   **Bạn A (Tech Lead):** Cấu hình Saga Coordinator trong Article Service.
*   **Bạn B (Logic):** Xử lý Race Condition (Like 2 lần) bằng `UNIQUE` constraint trong Interaction Service.
*   **Bạn D (Infra):** Setup Docker Compose để chạy RabbitMQ và các DB cùng lúc.
*   **Bạn E (Security):** Cấu hình Gateway để check JWT và phân quyền (Role-based).

---
*Tài liệu này được tạo vào ngày 30/05/2026 để hỗ trợ học tập cho môn MSS301.*
