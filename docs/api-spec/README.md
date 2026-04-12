# API Specification

이 문서는 Flutter 클라이언트의 API 연동 검증을 위한 백엔드 API 명세서입니다.

## 검증 가이드 (AI용)

### 검증 항목
1. **URL 경로**: Flutter에서 호출하는 URL이 명세의 `URL`과 정확히 일치하는지
2. **HTTP 메서드**: GET/POST/PATCH/PUT/DELETE가 일치하는지
3. **요청 파라미터**: query parameter 이름과 타입이 일치하는지
4. **요청 바디**: JSON 필드명, 타입, 필수 여부가 일치하는지
5. **응답 파싱**: 응답 JSON 구조와 필드명이 정확히 일치하는지
6. **인증 헤더**: 인증이 필요한 API에 Authorization 헤더를 포함하는지
7. **Enum 값**: 서버가 기대하는 enum 문자열과 정확히 일치하는지

### 공통 규칙
- 인증 필요 API는 `Authorization: Bearer {accessToken}` 헤더 필수
- 모든 성공 응답은 `ApiResult<T>` 래퍼로 감싸짐
- 페이징 응답은 `CustomPaging<T>` 또는 `KotlinCustomPaging<T>` 구조 사용

### 공통 응답 래퍼

```json
// ApiResult<T> - 성공 응답
{
  "status": "OK",
  "message": null,
  "data": T
}
```

```json
// CustomPaging<T> - 페이징 응답
{
  "content": [T],
  "pageNumber": 0,
  "pageSize": 20,
  "totalPages": 1,
  "totalElements": 5,
  "isLast": true,
  "mainData": null
}
```

```json
// KotlinCustomPaging<T> - 페이징 응답 (알림 등)
{
  "content": [T],
  "pageNumber": 0,
  "pageSize": 20,
  "totalPages": 1,
  "totalElements": 5,
  "isLast": true,
  "redDotStatus": [],
  "sender": null
}
```

### 공통 Enum 정의

| Enum | 값 |
|---|---|
| MemberType | `STUDENT`, `TRAINER` |
| AlarmStatus | `ENABLED`, `DISABLE` |
| SocialType | `NONE`, `KAKAO`, `NAVER`, `GOOGLE`, `APPLE` |
| ReservationStatus | `COMPLETED`, `AVAILABLE`, `NO_SHOW`, `SOLD_OUT`, `LUNCH_TIME`, `DISABLED` |
| DietType | `BREAKFAST`, `LUNCH`, `DINNER` |
| ExerciseCategory | `CORE`, `LEG`, `ARM`, `SHOULDER`, `CHEST`, `BACK`, `TRAPEZIUS`, `STRETCHING` |
| Calculation | `PLUS`, `MINUS` |
| CourseHistoryType | `COURSE_CREATE`, `PLUS_CNT`, `MINUS_CNT`, `ONE_LESSON`, `RESERVATION`, `RESERVATION_CANCEL` |
| DeviceType | `WEB`, `AOS`, `IOS` |
| WritingStatus | `WRITTEN`, `UNWRITTEN` |
| LessonHistoryReadStatus | `READ`, `UNREAD` |
| NotificationCategory | `SCHEDULE`, `COMMUNITY` |

### 공통 DTO

#### MemberDto
```json
{
  "id": 1,
  "userId": "string",
  "email": "string",
  "name": "string",
  "delYn": false,
  "profile": { "id": 1, "fileUrl": "string" },
  "memberType": "STUDENT",
  "pushAlarmStatus": "ENABLED",
  "feedbackAlarmStatus": "ENABLED",
  "gym": { "id": 1, "name": "string" },
  "socialType": "NONE"
}
```

#### CourseDto
```json
{
  "courseId": 1,
  "totalLessonCnt": 30,
  "remainLessonCnt": 20,
  "completedLessonCnt": 10,
  "createdAt": "2026-01-01T00:00:00"
}
```

#### ProfileDto
```json
{
  "id": 1,
  "fileUrl": "string"
}
```

#### GymDto
```json
{
  "id": 1,
  "name": "string"
}
```

## 버그 수정 이력

| 날짜 | 대상 파일 | 문제 | 수정 내용 |
|---|---|---|---|
| 2026-04-12 | `MemberAuthController.java` | `GET /auth/validation/email?email=xxx` 호출 시 500 반환 | `@RequestParam @Valid` → `@ModelAttribute @Valid` 변경 (query parameter `email=` 로 정상 바인딩) |
| 2026-04-12 | `ErrorCode.java` | `POST /auth/login` 실패 시 404 반환 | `MEMBER_LOGIN_FAILED` HTTP status `NOT_FOUND` → `BAD_REQUEST` 변경 |
| 2026-04-12 | `GlobalExceptionHandler.java` | Trailing slash(`/api/v1/gyms/` 등) 요청 시 500 반환 | `NoResourceFoundException` 핸들러 추가 → 404 반환 |
| 2026-04-12 | `GlobalExceptionHandler.java` | `GET /notification/DIET` 등 잘못된 enum 값 전달 시 500 반환 | `MethodArgumentTypeMismatchException` 핸들러 추가 → 400 반환 |

---

## 도메인별 명세

| 파일 | 도메인 | 엔드포인트 수 |
|---|---|---|
| [auth.md](./auth.md) | 인증 (로그인/회원가입/소셜로그인) | 14 |
| [member.md](./member.md) | 회원 정보/설정 | 25 |
| [home.md](./home.md) | 홈 화면 | 2 |
| [schedule.md](./schedule.md) | 수업 일정 | 22 |
| [lesson-history.md](./lesson-history.md) | 수업 일지 | 12 |
| [diet.md](./diet.md) | 식단 | 13 |
| [workout.md](./workout.md) | 운동 기록/커뮤니티 | 12 |
| [exercise.md](./exercise.md) | 운동 종목 | 4 |
| [trainer.md](./trainer.md) | 트레이너 전용 | 12 |
| [course.md](./course.md) | 수강권 | 3 |
| [gym.md](./gym.md) | 체육관 | 4 |
| [notification.md](./notification.md) | 알림 | 3 |
| [push.md](./push.md) | 푸시 알림 | 4 |
| [file.md](./file.md) | 파일 업로드 | 1 |
