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
# CI пробрасывает --build-arg BUILD_DATE=$(date -u +%Y-%m-%dT%H:%M:%SZ)
# и --build-arg VCS_REF=$(git rev-parse --short HEAD).
ARG BUILD_DATE=unknown
ARG VCS_REF=unknown
# OCI-стандарт labels — registries (GHCR, Docker Hub) их подхватывают для
# карточки образа и интеграции с GitHub. `docker inspect <image>` покажет
# created/revision — удобно для post-mortem «какая сборка в проде».
LABEL org.opencontainers.image.title="cheat-sheet-quiz" \
      org.opencontainers.image.description="Interview prep app with MCQ engine and spaced repetition" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.source="https://github.com/sergeyvoronin/cheat-sheet" \
      org.opencontainers.image.documentation="https://github.com/sergeyvoronin/cheat-sheet#readme"

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Explicit UID/GID (10001) делает образ совместимым с K8s
# `securityContext.runAsUser: 10001`/`runAsGroup: 10001` и предсказуемым
# на bind-mounted volumes — chown снаружи знает конкретные числа.
RUN groupadd -r -g 10001 app && useradd -r -u 10001 -g app -s /bin/false app
WORKDIR /app

COPY --from=builder /build/modules/quiz-app/build/libs/*.jar app.jar
# Для сборки образа нужна папка cheatsheets в корне проекта (см. README)
COPY cheatsheets /app/cheatsheets

RUN chown -R app:app /app

USER app
ENV SPRING_PROFILES_ACTIVE=prod
# JVM container-memory defaults НА УРОВНЕ ОБРАЗА (а не только в compose) —
# чтобы `docker run` и K8s `image:` тоже были cgroup-aware без доп. настройки.
# Без этого JVM берёт лишь ~25% лимита памяти под heap (дефолт MaxRAMPercentage),
# а остальное простаивает. 75% оставляет место под metaspace/threads/direct-буферы.
# ExitOnOutOfMemoryError: на OOM JVM сразу exit → оркестратор рестартует контейнер,
#   а не держит degraded-инстанс в GC-thrashing. HeapDump в /tmp для post-mortem.
# docker-compose.yml и K8s env переопределяют это значение (env > Dockerfile ENV).
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof"
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5 \
  CMD curl -fsS http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
