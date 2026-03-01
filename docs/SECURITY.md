# SECURITY

## Threat model

Приложение ориентировано на локальный/self-hosted запуск, но содержит чувствительные операции:

- `/api/admin/**`
- `/api/regenerate`
- `/export`

Для этих endpoint используется токен `X-Admin-Token` через `SensitiveEndpointAccessService`.

## Текущие механизмы защиты

- **Spring Security baseline (`SecurityFilterChain`)**:
  - отключены `formLogin/httpBasic/logout` (нет ложной auth-модели);
  - включены security headers (`CSP`, `Referrer-Policy`, `X-Frame-Options`, `Permissions-Policy`);
  - CORS управляется через `app.security.cors.*` (`@ConfigurationProperties`);
  - CSRF включен для MVC state-changing endpoint и отключен для `/api/**`, `/export`, `/actuator/**`.
- **Admin token guard**:
  - константное сравнение через `MessageDigest.isEqual`;
  - единый `forbiddenIfUnauthorized(...)` для контроллеров.
- **Rate limit**:
  - лимит на `/api/regenerate` в минутном окне;
  - `Retry-After` при `429 RATE_LIMIT_EXCEEDED`.
- **Prod fail-fast**:
  - в профиле `prod` пустой `app.admin-token` блокирует запуск приложения.
- **Centralized error handling**:
  - `GlobalExceptionHandler` нормализует ошибки;
  - исключает утечки stacktrace в API response.

## Конфигурация безопасности

- `app.admin-token`
- `app.regenerate-rate-limit-per-minute`
- `app.trust-forwarded-for-header`
- `app.security.cors.allowed-origins`
- `app.security.cors.allowed-methods`
- `app.security.cors.allowed-headers`
- `app.security.cors.exposed-headers`
- `app.security.cors.allow-credentials`
- `app.security.cors.max-age-seconds`

Рекомендации:

- включать `app.trust-forwarded-for-header=true` только за доверенным reverse proxy;
- всегда задавать токен через env-переменную, не хранить в VCS;
- не использовать короткие/простые токены.

## Логирование

- Не логировать API-ключи и admin-token.
- Ошибки AI и runtime логировать в sanitised виде (компактное сообщение, без секретов).
- Любые audit-логи по админ-операциям должны содержать действие и timestamp, но не credential payload.

## Рекомендации для hardening

- Добавить Spring Security (`SecurityFilterChain`) для строгой авторизации endpoint по ролям.
- Включить CSRF-защиту для state-changing HTTP-операций в web-интерфейсе.
- Ограничить CORS allow-list до доверенных origin.
- Добавить server-level rate-limit для всех чувствительных endpoint.
- Подключить security headers (`X-Content-Type-Options`, `Content-Security-Policy`, `X-Frame-Options`).
