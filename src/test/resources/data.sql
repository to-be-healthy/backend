insert into "member" ("member_id", "email", "user_id", "password", "name", "member_type")
values (default, 'healthy-trainer0@gmail.com', 'healthy-trainer0', '$2a$10$V6d4NDg8UFC1ShndqbOysO2aaNnU.o7MeT2c0EBPw.ZXd3xoLICdS', 'healthy-trainer', 'TRAINER');

insert into "member" ("member_id", "email", "user_id", "password", "name", "member_type")
values (default, 'healthy-student0@gmail.com', 'healthy-student0', '$2a$10$V6d4NDg8UFC1ShndqbOysO2aaNnU.o7MeT2c0EBPw.ZXd3xoLICdS', 'healthy-student', 'STUDENT');

insert into "trainer_schedule_info" ("lesson_end_time", "lesson_start_time", "lesson_time", "lunch_end_time", "lunch_start_time", "trainer_id", "trainer_schedule_info_id")
values ('21:00:00', '09:00:00', 'ONE_HOUR', '13:00:00', '12:00:00', 1, default);

insert into "trainer_schedule_closed_days_info" ("closed_days", "trainer_schedule_info_id", "trainer_schedule_closed_days_id")
values ('FRIDAY', 1, default);