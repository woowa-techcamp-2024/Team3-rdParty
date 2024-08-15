DELETE
FROM seat;
DELETE
FROM seat_grade;
DELETE
FROM zone;
DELETE
FROM performance;
DELETE
FROM member;

-- Member 테이블에 데이터 삽입
INSERT INTO member (member_id, email, password, member_role, created_at, updated_at)
VALUES (1, 'test@gmail.com', 'testpassword', 'USER', NOW(), NOW());

-- Performance 테이블에 데이터 삽입
INSERT INTO performance (performance_id, performance_name, performance_place, performance_showtime, created_at,
                         updated_at)
VALUES (1, '공연', '장소', '2024-08-23 14:30:00', NOW(), NOW());

-- Zone 테이블에 데이터 삽입
INSERT INTO zone (zone_id, zone_name, performance_id, created_at, updated_at)
VALUES (1, 'VIP', 1, NOW(), NOW());

-- SeatGrade 테이블에 데이터 삽입
INSERT INTO seat_grade (seat_grade_id, grade_name, price, performance_id, created_at, updated_at)
VALUES (1, 'Grade1', 10000, 1, NOW(), NOW());

-- Seat 테이블에 데이터 삽입
INSERT INTO seat (seat_id, seat_code, seat_status, member_id, zone_id, seat_grade_id, version, created_at, updated_at)
VALUES (1, 'A01', 'SELECTABLE', 1, 1, 1, 0, NOW(), NOW());
