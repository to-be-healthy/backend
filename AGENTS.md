# Repository Guidelines

## Project Structure & Module Organization
This repository is a Spring Boot 3.2 service named `healthy`, built with Gradle Kotlin DSL and Java 17. Application code lives in both `src/main/java/com/tobe/healthy` and `src/main/kotlin/com/tobe/healthy`; tests mirror that split under `src/test/java` and `src/test/kotlin`. Packages are organized by feature (`member`, `schedule`, `lessonhistory`, `gym`, `notification`) and then by layer: `presentation`, `application`, `domain`, `repository`, and `config`. Runtime config is in `src/main/resources/application.yml`; local infrastructure is defined in `docker-compose.yml`.

## Build, Test, and Development Commands
Use the Gradle wrapper only.

- `./gradlew clean build`: compile, run tests, and assemble the application.
- `./gradlew test`: run the JUnit 5 test suite without rebuilding everything.
- `./gradlew bootRun`: start the API locally on port `8080`.
- `./gradlew test --tests '*ScheduleRegisterTest'`: run a single test class while iterating.
- `docker compose up -d mysql redis`: start the local MySQL 8 and Redis 7 services expected by the app.

## Coding Style & Naming Conventions
Follow the existing mixed-language style: 4-space indentation in Kotlin, tab-indented legacy Java where already present, and no package renames unless the feature is moving. Keep package names lowercase and class names explicit: `*Controller`, `*Service`, `*Repository`, `*Config`. DTOs commonly use `Command*` for writes and `Retrieve*` for reads. Prefer feature-local changes over cross-package utility growth.

## Testing Guidelines
Tests run on JUnit Platform with Spring Boot Test, Kotest, MockK, Reactor Test, and Rest Assured. Add tests beside the feature you changed, and name them `*Test` to match the current suite (for example, `NotificationServiceTest` or `ScheduleSettingTest`). Cover service-layer behavior and repository queries when logic changes; run `./gradlew test` before opening a PR.

## Commit & Pull Request Guidelines
Use the commit template in `.github/.gitmessage.txt`: `<type>: "summary"` with lower-case types such as `feat`, `fix`, `refactor`, `test`, `docs`, `style`, and `chore`. Keep the subject short and specific. PRs should follow `.github/PULL_REQUEST_TEMPLATE.md`: include the Jira issue in the title, summarize the change, mark the work type, note follow-up concerns, and list completed tests.

## Security & Configuration Tips
`application.yml` is environment-driven. Keep secrets in local env files or shell variables, not in Git. The default active profile is `DEV`; verify `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, Redis host, JWT, OAuth, AWS S3, mail, and Firebase settings before running locally.
