# Reverse Proxy — авторский проход MCQ

Дата: 2026-11-09. Модель: GPT-6. Статус: **LLM_READY**.

Пара и SHA-256:

- `cheatsheets/interview/architecture/reverse-proxy-interview.md` — `171d8f87ef7033903328585d8b20b903bb7560858ca8a851004fe4bb1ccf7aff`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/reverse-proxy-interview.json` — `79f2960dc09026c425d51a8d6f25395591373f68aa63b536c571ff398de636f5`.
- `docs/mcq-quality/fact-freshness/reverse-proxy-interview.json` — `c9a08732c97ea5d8974efbe8e746d00d959286334515611ed45aa79d78105027`.

Прочитаны пролог, оглавление, видимые Q1–Q30, парная теория, 120 вариантов A–D и пояснения. Идентификаторы B0 и ключи сохранены. Правильные варианты приведены к сопоставимому с неверными объёму; слабые дистракторы переписаны как полные ошибочные модели. Самопроверка не заменяет независимое ревью.

Смысловые исправления: уточнена граница HTTP reverse proxy и L4-балансировки; восстановлена цепочка доверия для X-Forwarded-For; сняты устаревшие универсальные утверждения об Nginx upstream HTTP/1.1, HTTP/2, keepalive и sticky cookie. Исправлен код ответа limit_req по умолчанию, правило замены URI в proxy_pass, модель active/passive health checks, архитектура Linkerd и Istio ambient, условия WebSocket Upgrade, gRPC через L4 и L7, кэширование динамики и POST, измерение hit ratio, буферизация streaming и трассировка. Убраны универсальные оценки производительности, таймаутов и степени сжатия без измерений.

Проверенные первичные источники:

- [Nginx proxy module](https://nginx.org/en/docs/http/ngx_http_proxy_module.html), [upstream module](https://nginx.org/en/docs/http/ngx_http_upstream_module.html), [limit_req module](https://nginx.org/en/docs/http/ngx_http_limit_req_module.html), [real IP module](https://nginx.org/en/docs/http/ngx_http_realip_module.html), [gRPC module](https://nginx.org/en/docs/http/ngx_http_grpc_module.html) и [WebSocket proxying](https://nginx.org/en/docs/http/websocket.html).
- [Envoy life of a request](https://www.envoyproxy.io/docs/envoy/latest/intro/life_of_a_request), [Linkerd architecture](https://linkerd.io/docs/reference/architecture/index.html) и [Traefik providers](https://doc.traefik.io/traefik/providers/overview/).

Открытых критических сомнений по ключам нет. Конфигурационные примеры Nginx, HAProxy, Envoy и Traefik не исполнялись в конкретной инфраструктуре; перед переносом нужно проверить версии, модули и фактическую схему трафика.

Структурная проверка: `PASS: files=1 questions=30 blocks=30; semantic review NOT_RUN`. `git diff --check` — без ошибок. Независимое ревью и прежний `FINAL PASS` не проводились.

Дополнительная ревизия визуальной симметрии A–D: [отчёт](reverse-proxy-visual-parity-2026-11-09.md). Хеши выше обновлены после неё; статус остаётся LLM_READY.
