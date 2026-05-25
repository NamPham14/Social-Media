PHẦN 1: THIẾT KẾ DATABASE TỔNG THỂ (Mô hình Database per Service)

Lưu ý: Các ID được tham chiếu giữa các service được gọi là "Logical ID" (không có Ràng buộc Khóa ngoại - Foreign Key).

1. User Service (Database: user_db)
   Quản lý thông tin hồ sơ và định danh.

   CREATE TABLE users (                                                                                                                                                                                                                                                                                                                                      
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),                                                                                                                                                                                                                                                                                                        
   username VARCHAR(50) UNIQUE NOT NULL,                                                                                                                                                                                                                                                                                                                 
   email VARCHAR(100) UNIQUE NOT NULL,                                                                                                                                                                                                                                                                                                                   
   password_hash VARCHAR(255) NOT NULL,                                                                                                                                                                                                                                                                                                                  
   full_name VARCHAR(100),                                                                                                                                                                                                                                                                                                                               
   bio TEXT,                                                                                                                                                                                                                                                                                                                                             
   avatar_url VARCHAR(500),                                                                                                                                                                                                                                                                                                                              
   is_private BOOLEAN DEFAULT FALSE, -- Tính năng acc private của Insta                                                                                                                                                                                                                                                                                  
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                        
   );

2. Post Service (Database: post_db)
   Quản lý bài đăng dạng hình ảnh/video kèm caption.

    CREATE TABLE posts (                                                                                                                                                                                                                                                                                                                                      
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),                                                                                                                                                                                                                                                                                                        
        user_id UUID NOT NULL,          -- Lấy từ User Service                                                                                                                                                                                                                                                                                                
        image_url VARCHAR(500) NOT NULL,                                                                                                                                                                                                                                                                                                                      
        caption TEXT,                                                                                                                                                                                                                                                                                                                                         
        location_name VARCHAR(100),                                                                                                                                                                                                                                                                                                                           
                                                                                                                                                                                                                                                                                                                                                                   -- Dữ liệu thống kê (Cache) để truy vấn Feed nhanh hơn                                                                                                                                                                                                                                                                                                
       likes_count INT DEFAULT 0,                                                                                                                                                                                                                                                                                                                            
       comments_count INT DEFAULT 0,                                                                                                                                                                                                                                                                                                                         
                                                                                                                                                                                                                                                                                                                                                             
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                        
);                                                                                                                                                                                                                                                                                                                                                        
CREATE INDEX idx_posts_user_id ON posts(user_id);

3. Interaction & Comment Service (Database: interaction_db)
   Quản lý tương tác của người dùng lên bài đăng.

-- Bảng quản lý Comment                                                                                                                                                                                                                                                                                                                                   
CREATE TABLE comments (                                                                                                                                                                                                                                                                                                                                   
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),                                                                                                                                                                                                                                                                                                        
post_id UUID NOT NULL,          -- Lấy từ Post Service                                                                                                                                                                                                                                                                                                
user_id UUID NOT NULL,          -- Người comment (từ User Service)                                                                                                                                                                                                                                                                                    
content TEXT NOT NULL,                                                                                                                                                                                                                                                                                                                                
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                        
);                                                                                                                                                                                                                                                                                                                                                        
CREATE INDEX idx_comments_post_id ON comments(post_id);

-- Bảng quản lý Like                                                                                                                                                                                                                                                                                                                                      
CREATE TABLE likes (                                                                                                                                                                                                                                                                                                                                      
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),                                                                                                                                                                                                                                                                                                        
post_id UUID NOT NULL,                                                                                                                                                                                                                                                                                                                                
user_id UUID NOT NULL,                                                                                                                                                                                                                                                                                                                                
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                                                                                                                                                                                                                                                                                       
UNIQUE (post_id, user_id)       -- 1 người chỉ like 1 bài 1 lần                                                                                                                                                                                                                                                                                       
);

4. Follower Service (Database: follower_db)
   Quản lý đồ thị xã hội (ai theo dõi ai).

    CREATE TABLE follows (                                                                                                                                                                                                                                                                                                                                     
       follower_id UUID NOT NULL,      -- Người đi follow                                                                                                                                                                                                                                                                                                     
       following_id UUID NOT NULL,     -- Người được follow                                                                                                                                                                                                                                                                                                   
       status VARCHAR(20) DEFAULT 'ACCEPTED', -- ACCEPTED, PENDING (nếu acc private)                                                                                                                                                                                                                                                                          
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                                                                                                                                                                                                                                                                                        
       PRIMARY KEY (follower_id, following_id)                                                                                                                                                                                                                                                                                                                
);                                                                                                                                                                                                                                                                                                                                                         
CREATE INDEX idx_follower ON follows(follower_id);                                                                                                                                                                                                                                                                                                         
CREATE INDEX idx_following ON follows(following_id);

5. Notification Service (Database: notification_db)
   Lưu trữ thông báo để hiển thị trên app.

   CREATE TABLE notifications (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   recipient_id UUID NOT NULL,     -- Người nhận thông báo
   actor_id UUID NOT NULL,         -- Người tạo ra hành động (VD: người like/comment)
   post_id UUID,                   -- Bài viết liên quan (nếu có)
   type VARCHAR(20) NOT NULL,      -- LIKE, COMMENT, FOLLOW
   content TEXT,                   -- Tóm tắt (VD: "đã bình luận bài viết của bạn")
   is_read BOOLEAN DEFAULT FALSE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   )


3 Quy tắc "Sống còn" khi thiết kế DB Microservices:

1. KHÔNG dùng Foreign Key giữa các Database: Article Service không thể tạo constraint FK tới bảng User. Bạn chỉ lưu author_id và khi cần lấy tên tác giả, Article Service sẽ gọi User Service qua API (hoặc dùng pattern Data Synchronization).
2. Sử dụng UUID thay vì Long (Khuyên dùng): Trong hệ thống phân tán, dùng UUID giúp tránh xung đột ID khi bạn cần đồng bộ hoặc gộp dữ liệu sau này.
3. Dữ liệu dư thừa có kiểm soát (Data Redundancy): Để tăng tốc độ, đôi khi bạn có thể lưu luôn author_name vào bảng articles của Article Service. Tuy nhiên, bạn phải xử lý bài toán: "Khi User đổi tên thì Article Service có cập nhật theo không?" (Đây là lúc dùng Event-driven/Kafka)
                                                                                                                                                                                                                                                                                       


