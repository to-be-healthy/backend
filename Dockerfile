FROM gradle:8.5-jdk17 AS builder
  WORKDIR /build
  COPY build.gradle.kts settings.gradle gradlew ./
  COPY gradle ./gradle
  RUN ./gradlew dependencies --no-daemon
  COPY . .
  RUN ./gradlew build -x test --no-daemon

  FROM eclipse-temurin:17-jre-jammy
  WORKDIR /app
  RUN apt-get update && apt-get install -y --no-install-recommends curl \
   && rm -rf /var/lib/apt/lists/* \
   && useradd --system --uid 1001 --create-home --shell /usr/sbin/nologin appuser \
   && mkdir -p /logs/info /logs/warn /logs/error \
   && chown -R appuser:appuser /logs
  COPY --chown=appuser:appuser --from=builder /build/build/libs/*.jar /app/app.jar
  USER appuser
  EXPOSE 8080 7070
  ENTRYPOINT ["java", "-jar", "/app/app.jar"]