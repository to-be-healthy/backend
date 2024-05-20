-- 트레이너 등록
insert into "member" ("member_id", "email", "user_id", "password", "name", "member_type", "created_at", "updated_at") values (default, 'healthy-trainer0@gmail.com', 'healthy-trainer0', '$2a$10$V6d4NDg8UFC1ShndqbOysO2aaNnU.o7MeT2c0EBPw.ZXd3xoLICdS', 'healthy-trainer', 'TRAINER', '2024-05-20T15:19:00.000', '2024-05-20T15:19:00.000');

-- 학생 등록
insert into "member" ("member_id", "email", "user_id", "password", "name", "member_type") values (default, 'healthy-student0@gmail.com', 'healthy-student0', '$2a$10$V6d4NDg8UFC1ShndqbOysO2aaNnU.o7MeT2c0EBPw.ZXd3xoLICdS', 'healthy-student', 'STUDENT');

-- 트레이너 기본 근무 시간 등록
insert into "trainer_schedule_info" ("lesson_end_time", "lesson_start_time", "lesson_time", "lunch_end_time", "lunch_start_time", "trainer_id", "trainer_schedule_info_id") values ('20:00:00', '09:00:00', 'ONE_HOUR', '13:00:00', '12:00:00', 1, default);

-- 트레이너 일정 등록
insert into "schedule" ("applicant_id", "created_at", "del_yn", "lesson_dt", "lesson_end_time", "lesson_start_time", "reservation_status", "trainer_id", "updated_at", "schedule_id") values (NULL, '2024-05-17T19:00:49.661', false, '2024-05-31', '20:00:00', '19:00:00', 'AVAILABLE', 1, '2024-05-17T19:00:49.661', default);
insert into "schedule" ("applicant_id", "created_at", "del_yn", "lesson_dt", "lesson_end_time", "lesson_start_time", "reservation_status", "trainer_id", "updated_at", "schedule_id") values (NULL, '2024-05-17T19:00:49.661', false, '2024-05-13', '10:00:00', '11:00:00', 'DISABLED', 1, '2024-05-17T19:00:49.661', default);
insert into "schedule" ("applicant_id", "created_at", "del_yn", "lesson_dt", "lesson_end_time", "lesson_start_time", "reservation_status", "trainer_id", "updated_at", "schedule_id") values (NULL, '2024-05-17T19:00:49.661', false, '2024-05-13', '11:00:00', '12:00:00', 'DISABLED', 1, '2024-05-17T19:00:49.661', default);
insert into "schedule" ("applicant_id", "created_at", "del_yn", "lesson_dt", "lesson_end_time", "lesson_start_time", "reservation_status", "trainer_id", "updated_at", "schedule_id") values (NULL, '2024-05-17T19:00:49.661', false, '2024-05-19', '20:00:00', '21:00:00', 'DISABLED', 1, '2024-05-17T19:00:49.661', default);

-- 헬스장 등록
insert into "gym" ("created_at", "join_code", "name", "updated_at", "gym_id") values ('2024-05-18T00:07:26.622', '190961', '건강해짐 원흥점', '2024-05-18T00:07:26.622', default);