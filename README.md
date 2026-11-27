# RoomS - Hệ thống quản lý sử dụng phòng (ĐH Nguyễn Trãi)

## Công nghệ
Java 21, Spring Boot 4.0.9, MySQL 8, ReactJS (Vite)

## Chạy dự án
1. Tạo database: `CREATE DATABASE rooms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. Trong `backend/src/main/resources/` copy `application-local.properties.example`
   thành `application-local.properties` và điền thông tin.
3. Backend: `cd backend` rồi `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"`
4. Chạy `database/demo-data.sql` trong MySQL Workbench để có dữ liệu mẫu
   (4 vai trò được tạo khi chạy lần đầu, xem mục dưới).
5. Frontend: `cd frontend`, `npm install`, `npm run dev` rồi mở http://localhost:5173

## Tài khoản demo (mật khẩu 123456)
admin, quanly, giangvien, sinhvien