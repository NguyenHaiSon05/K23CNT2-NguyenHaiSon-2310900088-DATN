USE rooms;

-- 1. Thêm phòng demo (bỏ qua nếu đã có mã phòng)
INSERT IGNORE INTO rooms (room_code, name, capacity, location, status, room_type_id) VALUES
('C101', 'Phòng học C101', 50, 'Tòa C - Tầng 1', 'ACTIVE', (SELECT id FROM room_types WHERE name = 'Phòng học lý thuyết')),
('C102', 'Phòng học C102', 50, 'Tòa C - Tầng 1', 'ACTIVE', (SELECT id FROM room_types WHERE name = 'Phòng học lý thuyết')),
('C201', 'Phòng máy C201', 40, 'Tòa C - Tầng 2', 'ACTIVE', (SELECT id FROM room_types WHERE name = 'Phòng máy tính')),
('D101', 'Phòng thực hành D101', 30, 'Tòa D - Tầng 1', 'ACTIVE', (SELECT id FROM room_types WHERE name = 'Phòng thực hành'));

-- 2. Xóa lượt đặt cũ để dữ liệu demo sạch
DELETE FROM bookings;

-- 3. Lượt đặt demo (ngày 08-10/12/2026 là sau mốc nộp, nên hiển thị tốt trong lịch tuần)
SET @sv = (SELECT id FROM users WHERE username = 'sinhvien');
SET @gv = (SELECT id FROM users WHERE username = 'giangvien');

INSERT INTO bookings (room_id, user_id, booking_date, start_time, end_time, purpose, status, created_at) VALUES
((SELECT id FROM rooms WHERE room_code='A101'), @gv, '2026-12-08', '07:00:00', '09:00:00', 'Day lop Lap trinh Java', 'APPROVED', NOW()),
((SELECT id FROM rooms WHERE room_code='A101'), @gv, '2026-12-08', '09:00:00', '11:00:00', 'Day lop Co so du lieu', 'APPROVED', NOW()),
((SELECT id FROM rooms WHERE room_code='A102'), @gv, '2026-12-09', '13:00:00', '15:00:00', 'Day lop Mang may tinh', 'APPROVED', NOW()),
((SELECT id FROM rooms WHERE room_code='B101'), @sv, '2026-12-09', '08:00:00', '10:00:00', 'Hoc nhom do an', 'PENDING', NOW()),
((SELECT id FROM rooms WHERE room_code='H1'),   @sv, '2026-12-10', '14:00:00', '17:00:00', 'Sinh hoat cau lac bo', 'PENDING', NOW()),
((SELECT id FROM rooms WHERE room_code='A201'), @sv, '2026-12-10', '07:00:00', '09:00:00', 'On thi', 'REJECTED', NOW()),
((SELECT id FROM rooms WHERE room_code='C101'), @sv, '2026-12-11', '09:00:00', '11:00:00', 'Hop nhom', 'CANCELLED', NOW()),
((SELECT id FROM rooms WHERE room_code='C201'), @gv, '2026-12-11', '13:00:00', '16:00:00', 'Thuc hanh lap trinh', 'APPROVED', NOW());

SELECT status, COUNT(*) AS so_luot FROM bookings GROUP BY status;