Bài 1: Cấu hình thư mục chuẩn DDD (Hexagonal).

📚 Giải thích kiến thức (Mentor's Insight)
Trong kiến trúc Hexagonal (Củ hành), chúng ta chia mã nguồn thành các lớp đồng tâm. Quy tắc vàng là: Sự phụ thuộc chỉ được hướng vào bên trong (Dependency Rule).
- Domain (Lõi): Chứa Entity và logic nghiệp vụ thuần túy. Nó không được biết gì về Database hay Web.
- Application (Lớp bao): Chứa các Use Case (luồng xử lý). Nó điều phối dữ liệu từ Domain.
- Infrastructure & API (Lớp ngoài cùng): Đây là các "Adapter". API giúp người dùng gọi vào, Infrastructure giúp hệ thống lưu trữ dữ liệu hoặc gọi service khác.                                                                                                                                                                                              
