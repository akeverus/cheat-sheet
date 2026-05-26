# Сборка JAR: кэширование слоя зависимостей
FROM gradle:8.9-jdk17 AS builder
WORKDIR /build

COPY build.gradle.kts settings.gradle.kts ./
COPY modules modules
COPY gradle gradle
RUN gradle :quiz-app:dependencies --no-daemon -q || true

RUN gradle :quiz-app:bootJar --no-daemon -q

# Финальный образ от непривилегированного пользователя
FROM eclipse-temurin:17-jre
ARG VERSION=0.0.1-SNAPSHOT
# OCI-стандарт labels — registries (GHCR, Docker Hub) их подхватывают для
# карточки образа и интеграции с GitHub.
LABEL org.opencontainers.image.title="cheat-sheet-quiz" \
      org.opencontainers.image.description="Interview prep app with MCQ engine and spaced repetition" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.source="https://github.com/sergeyvoronin/cheat-sheet" \
      org.opencontainers.image.documentation="https://github.com/sergeyvoronin/cheat-sheet#readme"

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -r app && useradd -r -g app -s /bin/false app
WORKDIR /app

COPY --from=builder /build/modules/quiz-app/build/libs/*.jar app.jar
# Для сборки образа нужна папка cheatsheets в корне проекта (см. README)
COPY cheatsheets /app/cheatsheets

RUN chown -R app:app /app

USER app
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5 \
  CMD curl -fsS http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
