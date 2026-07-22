# Развёртывание и модель доступа

## Локальный / приватный single-user режим

Приложение по умолчанию рассчитано на **одного пользователя** на инстанс: локальная подготовка к собеседованиям или приватный сервер в домашней сети.

- HTTP-сессия Spring хранит прогресс интервью; отдельная аутентификация пользователей **не требуется**.
- Конфигурация `SecurityConfig` с `permitAll` для основных MVC/API-маршрутов **допустима** в этом режиме.
- CSRF включён для мутирующих `/api/**` запросов (cookie `XSRF-TOKEN` + заголовок `X-XSRF-TOKEN` на клиенте).
- Чувствительные операции (экспорт, админ-обслуживание) защищены `X-Admin-Token`, а не пользовательским логином.

## Статика и кэш (OPS-004)

- **Prod:** `spring.web.resources.chain.strategy.content.enabled=true` — Spring добавляет content-hash к self-hosted CSS/JS (`/css/app-<hash>.css`) через `ResourceUrlEncodingFilter` и Thymeleaf `@{...}`.
- **Dev/default:** ручной cache 86400s без content-hash chain; удобно править статику локально.
- Entry-point'ы `head.html` / `fragments/scripts.html` используют `th:href` / `th:src` без `?v=N`.

## Self-hosted frontend deps (OPS-005)

highlight.js 11.9.0, Mermaid 11.12.0, Chart.js 4.5.0 и шрифты Inter / JetBrains Mono **self-hosted** под `static/vendor/` и `static/fonts/`. Страницы работают **без интернета** для этих ресурсов.

| Ресурс | Путь | Обновление |
|--------|------|------------|
| highlight.js 11.9.0 (JS + atom-one-dark CSS) | `/vendor/hljs/` | `bash scripts/vendor-frontend.sh` |
| Mermaid 11.12.0 | `/vendor/mermaid/mermaid.min.js` | то же |
| Chart.js 4.5.0 | `/vendor/chart/chart.umd.min.js` | то же |
| Inter (wght 400..700) + JetBrains Mono (400..600) | `/fonts/*.woff2`, `/css/fonts-local.css` | то же |

CSP: только `'self'` (без cdnjs/jsdelivr/Google Fonts). SRI на self-hosted не используется.

При смене версии — править pins в `head.html` и в `scripts/vendor-frontend.sh`, затем перезапустить скрипт.

## Visual matrix E2E (TEST-005)

```bash
./scripts/run-visual-e2e.sh
# или при уже запущенном приложении:
(cd e2e && npm install && npx playwright install chromium && npm run test:visual)
```

Урезанная матрица (7 кадров): `theme` × `pixelIntensity` × `motion` через `localStorage` + проверка `html[data-theme|data-pixel|data-motion]`. Страницы: `/`, `/settings`, `/stats`. PNG складываются в `e2e/visual-results/` (gitignored); golden baseline / pixelmatch в CI не требуются.

## Keyboard E2E (TEST-006)

```bash
./scripts/run-keyboard-e2e.sh
# или при уже запущенном приложении:
(cd e2e && npm install && npx playwright install chromium && npm run test:keyboard)
```

Покрывает: skip-link → выбор варианта цифрой → Enter → feedback; ArrowDown без мыши.

## Вне scope текущего трека

Multi-user deployment, OAuth/OIDC, изоляция данных между пользователями и полноценный auth-провайдер **не входят** в Wave A review-package. Для публичного multi-tenant хостинга потребуется отдельный security/design track.
