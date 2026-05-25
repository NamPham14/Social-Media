1. Module Shared (Em nên nắm giữ)
   Em đã làm common-web, hãy tiếp tục hoàn thiện nó và các module common-security, common-events mà em vừa tạo.
    * Tại sao: Đây là "luật chơi" của cả dự án. Nếu em viết chuẩn Exception Handling (xử lý lỗi chung), Response Wrapper (định dạng JSON trả về thống nhất), thì các thành viên khác chỉ việc dùng theo. Điều này giúp code của cả nhóm trông như do một người viết vậy.

2. User Service (Hoặc Auth Service)
   Thầy khuyên em nên code User Service tích hợp bảo mật JWT.
* Tại sao: Đây là service đầu tiên mà mọi service khác đều phải dựa vào. Một bài đăng (Post) hay một lượt Like đều cần biết userId. Nếu em làm xong phần này sớm và cấp được JWT token cho cả nhóm, các thành viên khác sẽ có "chìa khóa" để test API của họ.

3. API Gateway & Routing Logic
   Em đã setup Gateway, nhưng sau này em cần code thêm phần Routing và Security Filter tại Gateway.
* Tại sao: Đây là chốt chặn cuối cùng. Em sẽ là người quyết định request nào được phép đi vào hệ thống, request nào bị chặn lại do thiếu token.   