# Kế hoạch Nâng cấp Bảo mật: Quản lý Định danh Người dùng (Identity Management)

## 1. Vấn đề Hiện tại (Current Issues)
- **Rủi ro Giả mạo (Identity Spoofing):** `userId` hiện đang được gửi từ Client thông qua Request Body hoặc Path Variable. Một người dùng xấu có thể thay đổi `userId` trong request để thực hiện hành động (đăng bài, xóa bài, bookmark...) dưới danh nghĩa người khác.
- **Dữ liệu dư thừa:** Client phải quản lý và gửi `userId` lên trong mọi request nhạy cảm, làm tăng khả năng sai sót và lộ lọt thông tin.

---

## 2. Các bước Cần Sửa đổi (Action Plan)

### Bước 2.1: Cập nhật JWT Token (Shared / Identity Service)
- **File:** `JwtProvider.java` (common-security), `LoginUseCase.java` (identity-service).
- **Nội dung:**
    - Chỉnh sửa hàm `generateToken` để nhận thêm `userId`.
    - Thêm `userId` vào Claims của JWT (ví dụ: `claim("userId", user.getId())`).
    - Việc này giúp `userId` được đóng gói an toàn bên trong Token đã được ký số (signed).

### Bước 2.2: Xây dựng Tiện ích Bảo mật (Common Security)
- **Tạo mới:** `SecurityUtils.java` trong `com.social_media.commonsecurity.util`.
- **Nội dung:**
    - Viết phương thức `getCurrentUserId()` để trích xuất `userId` từ `SecurityContextHolder`.
    - Sử dụng `Jwt` object của Spring Security để lấy claim `userId`.

### Bước 2.3: Refactor API DTOs (Post Service)
- **Files:** `CreatePostRequest.java`, `UpdatePostRequest.java`, `DeletePostRequest.java`, `BookmarkRequest.java`, `ReportPostRequest.java`.
- **Nội dung:**
    - Loại bỏ trường `userId` khỏi các lớp `@RequestBody` hoặc `@ModelAttribute`.
    - Client không còn quyền tự quyết định `userId` gửi lên.

### Bước 2.4: Refactor Controllers (Post Service)
- **Files:** `PostController.java`, `BookmarkController.java`, `ReportController.java`.
- **Nội dung:**
    - Tại mỗi phương thức xử lý, gọi `SecurityUtils.getCurrentUserId()` để lấy ID của người dùng đang thực hiện request.
    - Truyền ID này vào các Command (CreatePostCommand, DeletePostCommand...).
    - **Lưu ý:** Một số endpoint GET (ví dụ: `getPostsByAuthor`) vẫn có thể giữ `userId` trên Path nếu đó là tính năng xem profile công khai của người khác.

---

## 3. Lợi ích sau khi sửa đổi
- **Bảo mật tuyệt đối:** Người dùng chỉ có thể thao tác trên dữ liệu của chính mình (dựa trên Token không thể giả mạo).
- **Code sạch hơn (Cleaner Code):** Loại bỏ việc truyền `userId` thủ công từ Controller sang DTO.
- **Tuân thủ chuẩn REST & Clean Architecture:** Tách biệt thông tin định danh (Identity) khỏi dữ liệu nghiệp vụ (Business Data).
