<p align="center">
    <img src="https://github.com/to-be-healthy/FrontEnd/assets/102174146/f0629a08-f862-4b67-bf93-d52df57acb79" alt="건강해짐 로고 이미지">
    <br />
    <h1 align="center">건강해짐 Backend</h1>
    <p align="center">피트니스 센터, 트레이너와 회원을 위한 일정 관리 앱의 백엔드 API 서버</p>
    <br />
    <p align="center">
      <a href="https://geonganghaejim.site/">웹 사이트</a>
      ·
      <a href="https://geonganghaejim.site/swagger-ui/index.html">API 문서</a>
<!--       ·
      <a href="#">App</a> -->
    </p align="center">
</p>

<br />

## 서비스 개요

![건강해짐 배너 sns](https://github.com/to-be-healthy/FrontEnd/assets/102174146/d1682aea-4a3e-4c3e-84fc-9c55b3626547)

### PT 스케줄 관리

![건강해짐 배너 sns (1)](https://github.com/to-be-healthy/FrontEnd/assets/102174146/96784978-d903-47bf-832d-8433da311ae8)

수기 메모와 카카오톡으로 흩어져 있던 트레이너–회원 간 일정 관리를 한곳으로 모았어요.

트레이너는 헬스장 회원 스케줄을 한 화면에서 잡고 변경할 수 있고, 회원은 미리 트레이너의 빈 슬롯을 보고 예약할 수 있어요.

<br />

### 체계적인 회원 관리

![건강해짐 배너 sns (3)](https://github.com/to-be-healthy/FrontEnd/assets/102174146/05e70f40-4c75-4349-bfaa-fedc69cbc923)

회원별 메모, PT 피드백, 수강권 잔여 회차 관리까지 한 도메인 안에서 다뤄요.

데이터를 흘려보내지 않고 쌓아두니, 트레이너가 회원을 더 세심하게 관리할 수 있어요.

<br />

## 실행 방법

### Production

[운영 환경](https://geonganghaejim.site/) · [API 문서](https://geonganghaejim.site/swagger-ui/index.html)

### Development

<!-- TODO: dev 운영 도메인 확정되면 채우기 -->

### 테스트계정

> 학생 계정 : healthy-student0 / 12345678a

> 트레이너 계정 : healthy-trainer0 / 12345678a

### Local

```bash
git clone https://github.com/to-be-healthy/backend.git
cd backend

# MySQL, Redis 등 의존 서비스 기동
docker compose up -d

# application-{profile}.yml 또는 환경변수 설정 후 빌드 & 실행
./gradlew clean bootRun
```

> 실행에는 Java 17 이상이 필요해요. JWT 시크릿, MySQL/Redis 접속 정보, OAuth2 클라이언트 키, Firebase 서비스 계정 키 등은 `application-{profile}.yml` 또는 환경변수로 주입합니다.

API 문서는 [https://geonganghaejim.site/swagger-ui/index.html](https://geonganghaejim.site/swagger-ui/index.html) 에서 확인할 수 있어요.

<br />

## 백엔드

### 기술 스택

- **Language / Framework** — Java 17, Spring Boot 3.5
- **Persistence** — Spring Data JPA, QueryDSL 5.1, MySQL 8, Redis
- **Auth** — Spring Security, OAuth2 Client, JWT (jjwt 0.12)
- **API Docs** — SpringDoc OpenAPI (Swagger UI)
- **Notification** — Firebase Admin SDK (FCM), Spring Mail
- **Async / External** — Spring WebFlux (외부 API 호출용 WebClient)
- **Observability** — Spring Actuator, Micrometer + Prometheus, P6Spy

### 인프라

- **Container** — Docker, docker-compose (멀티스테이지 빌드)
- **CI/CD** — GitHub Actions (`.github/workflows`)
- **Monitoring** — Prometheus exporter via Micrometer
<!-- TODO: 운영 인프라(클라우드 제공자, 배포 토폴로지 등) 한두 줄 추가 -->

### 프로젝트 구조

```
src/main/
├── java/com/tobe/healthy/        # 레거시 + 신규 Java 모듈
│   ├── member/                   # 회원·트레이너 (인증/프로필/권한)
│   ├── schedule/                 # PT 스케줄 예약·변경
│   ├── lessonhistory/            # 수업 이력·피드백·메모
│   ├── gym/                      # 헬스장(센터) 정보·소속
│   ├── notification/             # FCM·메일 알림
│   └── config/                   # 공통 설정 (Security / Redis / OpenAPI 등)
└── kotlin/com/tobe/healthy/      # Kotlin으로 새로 작성된 모듈 (점진 이전)
```

**피처-퍼스트 → 5계층 패키징.** 각 feature 하위에 동일한 5개 계층을 일관되게 둡니다.

| 계층 | 역할 |
|---|---|
| `presentation` | `*Controller` — 요청 매핑, DTO 바인딩, 인증된 사용자 추출 |
| `application` | `*Service` — 트랜잭션 경계, 도메인 오케스트레이션, 외부 시스템 호출 |
| `domain` | 엔티티·값 객체·도메인 규칙 |
| `repository` | Spring Data JPA + QueryDSL. 동적 쿼리는 `*RepositoryCustom` / `*RepositoryImpl`로 분리 |
| `config` | 모듈 단위 Spring 설정 |

> DTO 컨벤션: 쓰기 명령은 `Command*`, 조회 응답은 `Retrieve*` 접두사를 사용합니다.

### 요청 처리 흐름

```
HTTP ─▶ Spring Security Filter Chain ─▶ JWT 인증 ─▶ Controller (presentation)
                                                          │
                                                          ▼
                                                  Service (application)
                                                          │
                              ┌───────────────────────────┼───────────────────────────┐
                              ▼                           ▼                           ▼
                  Repository (JPA / QueryDSL)        외부 통합                    Domain 객체
                              │                     - FCM (Firebase Admin)
                              ▼                     - SMTP (Spring Mail)
                          MySQL 8                   - WebClient (외부 API)
                              │
                              ▼  (캐시 · 일회성 토큰)
                            Redis 7
```

도메인 코드는 인증/메시징 구현 세부에 묶이지 않도록 횡단 관심사를 별도 모듈로 분리합니다. QueryDSL은 동적 검색·집계 쿼리에서 타입 안전한 빌더로 활용하고, 단순 CRUD는 Spring Data JPA 메서드 네이밍을 그대로 사용합니다.

### 횡단 관심사

- **인증 / 인가** — Spring Security 필터 체인 + JWT(jjwt 0.12)로 stateless 인증. OAuth2 Client로 소셜 로그인 통합.
- **알림** — Firebase Admin SDK로 FCM 푸시, Spring Mail로 이메일. `notification` 모듈에서 통합 관리.
- **로깅 / 디버깅** — P6Spy가 SQL을 정제된 형태로 로깅하여 N+1·슬로우 쿼리 추적을 쉽게 합니다.
- **API 문서** — SpringDoc OpenAPI가 런타임에 OpenAPI 스펙과 Swagger UI를 자동 생성합니다 (`/swagger-ui/index.html`).
- **모니터링** — Spring Actuator + Micrometer Prometheus 레지스트리. 컨테이너에서 관리 포트(`7070`)를 별도 노출하여 외부 API 포트와 분리.

### 런타임 구성

| 컴포넌트 | 역할 | 기본 포트 |
|---|---|---|
| API 서버 | Spring Boot 애플리케이션 (`app.jar`) | 8080 |
| Management | Actuator / Prometheus 메트릭 | 7070 |
| MySQL 8 | 메인 데이터 저장 (DB: `healthy`) | 3306 |
| Redis 7 | 캐시 · 일회성 토큰 · 분산 락 | 6379 |
| 파일 저장 | 업로드 파일 (`FILE_UPLOAD_DIR`, 기본 `/data/files`) | — |

컨테이너 이미지는 멀티스테이지 Dockerfile (`gradle:8.5-jdk17` → `eclipse-temurin:17-jre-jammy`)로 빌드하고, 보안을 위해 non-root `appuser`(uid 1001)로 실행합니다. 로그는 `/app/logs/{info,warn,error}`로 레벨별 분리됩니다.

### 테스트

- **JUnit 5 + Spring Boot Test** — 통합 / 컨트롤러 슬라이스 테스트
- **Kotest · MockK** — Kotlin 모듈의 BDD 스타일 · 모킹
- **Reactor Test** — WebClient / 리액티브 흐름 검증
- **Rest Assured** — 엔드포인트 통합 테스트
- **H2** — 런타임 테스트용 인메모리 DB

```bash
./gradlew test                                   # 전체 테스트
./gradlew test --tests '*ScheduleRegisterTest'   # 단일 클래스
```
