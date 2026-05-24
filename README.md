<p align="center">
    <img src="https://github.com/to-be-healthy/FrontEnd/assets/102174146/f0629a08-f862-4b67-bf93-d52df57acb79" alt="건강해짐 로고 이미지">
    <br />
    <h1 align="center">건강해짐 Backend</h1>
    <p align="center">피트니스 센터, 트레이너와 회원을 위한 일정 관리 앱의 백엔드 API 서버</p>
    <br />
    <p align="center">
      <a href="https://main.to-be-healthy.shop/">웹 사이트</a>
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

[운영 환경](https://main.to-be-healthy.shop/) · [API 서버](https://geonganghaejim.site/) · [API 문서](https://geonganghaejim.site/swagger-ui/index.html)

### Development

[개발 환경 데모](https://www.dev.to-be-healthy.shop/)

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

로컬 실행 후 API 문서는 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) 에서 확인할 수 있어요.

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

- **Container** — Docker, docker-compose
- **CI/CD** — GitHub Actions (`.github/workflows`)
- **Monitoring** — Prometheus exporter via Micrometer
<!-- TODO: 운영 인프라(클라우드 제공자, 배포 토폴로지 등) 한두 줄 추가 -->

### 프로젝트 구조

<!-- TODO: src/main/java 하위 폴더 구조 캡쳐 이미지 첨부 -->

도메인 단위로 패키지를 나누고, 각 도메인 안에서 `controller / service / domain / repository` 계층을 분리했습니다.

Spring Security 필터 체인 + JWT 인증, OAuth2 소셜 로그인, FCM 푸시 알림 같은 횡단 관심사는 별도 모듈로 떼어내어 도메인 코드가 인증/메시징 구현 세부에 묶이지 않도록 했습니다.

QueryDSL은 복잡한 검색·집계 쿼리에서 타입 안전한 동적 쿼리를 위해 사용하고, 단순 CRUD는 Spring Data JPA의 메서드 네이밍을 그대로 활용합니다.
