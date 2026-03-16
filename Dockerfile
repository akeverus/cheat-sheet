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
LABEL org.opencontainers.image.version="${VERSION}"

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
ENTRYPOINT ["java", "-jar", "app.jar"]
