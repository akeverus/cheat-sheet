---
title: "Вопросы на собеседовании: Edge Computing"
description: "Edge computing: Cloudflare Workers, Lambda@Edge, CDN edge functions, global latency, use cases, architecture, cold starts, state management"
tags:
  - interview
  - architecture
  - edge-computing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Edge Computing"
  - "Edge Computing interview"
  - "Edge functions"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Edge Computing`

`Edge Computing` — выполнение логики **близко к пользователям** (edge-локации CDN, IoT-шлюзы). Cloudflare Workers, AWS Lambda@Edge, Fastly Compute@Edge: latency 10-50 мс (против 100-300 мс у origin), глобальный масштаб, serverless. Роль растёт: API gateway, персонализация, A/B-тестирование, оптимизация изображений, авторизация на edge.

## Полезные ссылки

- [Cloudflare Workers docs](https://developers.cloudflare.com/workers/)
- [AWS Lambda@Edge](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/lambda-at-the-edge.html)
- [Fastly Compute@Edge](https://www.fastly.com/products/edge-compute)
- [Vercel Edge Functions](https://vercel.com/docs/functions/edge-functions)
- [Cloudflare Durable Objects](https://developers.cloudflare.com/durable-objects/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Edge Computing?](#q1--что-такое-edge-computing)
- [Q2. (!) Edge vs CDN vs серверlessless?](#q2--edge-vs-cdn-vs-серверlessless)
- [Q3. (!) Benefits и когда применять?](#q3--benefits-и-когда-применять)

**Platforms**
- [Q4. (!) Cloudflare Workers — как работает?](#q4--cloudflare-workers--как-работает)
- [Q5. (!) Lambda@Edge vs CloudFront Functions?](#q5--lambdaedge-vs-cloudfront-functions)
- [Q6. V8 isolates vs containers?](#q6-v8-isolates-vs-containers)

**Use cases**
- [Q7. (!) Typical use cases?](#q7--typical-use-cases)
- [Q8. Auth/JWT validation at edge?](#q8-authjwt-validation-at-edge)
- [Q9. Image optimization, resize?](#q9-image-optimization-resize)
- [Q10. A/B testing, personalization?](#q10-ab-testing-personalization)

**State и data**
- [Q11. (!) Как handle state at edge?](#q11--как-handle-state-at-edge)
- [Q12. Cloudflare Durable Objects?](#q12-cloudflare-durable-objects)
- [Q13. KV stores (Workers KV, DynamoDB Global)?](#q13-kv-stores-workers-kv-dynamodb-global)

**Ограничения**
- [Q14. (!) Ограничения edge runtime?](#q14--ограничения-edge-runtime)
- [Q15. Cold starts?](#q15-cold-starts)
- [Q16. Compute cost vs traditional?](#q16-compute-cost-vs-traditional)

**Production**
- [Q17. (!) Debugging и observability?](#q17--debugging-и-observability)
- [Q18. Deployment strategies?](#q18-deployment-strategies)

## Q1. (!) Что такое Edge Computing?

**Edge Computing** — выполнение прикладной логики **на краю сети** (близко к пользователям), а не в центральном DC/облаке.

**Origin (традиционный подход):**
- Централизованно: один или несколько регионов
- Пользователь → 150 мс RTT по миру

**Edge:**
- Распределённо: 100-300+ точек присутствия (POP)
- Пользователь → 10-50 мс RTT из любой точки

**Типы:**
1. **Edge-функции CDN** (Cloudflare Workers, Lambda@Edge)
2. **IoT edge** (обработка на уровне устройства, AWS Greengrass)
3. **Telco edge / MEC** (вычисления на базовой станции 5G)

**Фокус здесь:** edge-функции CDN — наиболее релевантны для backend-собеседований.

**Architecture:**
```
User → CDN Edge (runs edge function) → optionally → Origin
         ↓ 10ms                             ↓ 150ms
```

**Ключевой момент:** edge-функция может ответить, не обращаясь к origin. Полный roundtrip исключается.

## Q2. (!) Edge vs CDN vs серверlessless?

**CDN:** кэширование статического контента на edge.
- Хранимые объекты (изображения, CSS, JS)
- Логика не выполняется

**Edge compute:** CDN + выполнение кода на edge.
- Модификация request/response
- Динамическая персонализация
- Авторизация, решения о маршрутизации

**Serverless (Lambda, Cloud Functions):**
- Централизованные регионы (us-east-1 и т.д.)
- Полноценный runtime (контейнеры)
- Дольше cold start, меньше распределённость

**Edge serverless (Workers, Lambda@Edge):**
- Serverless + на edge
- V8-изоляты (Workers) — старт за микросекунды
- Полная платформа: KV store, cron-ы, durable objects

**Сравнение:**

| Аспект | CDN | Lambda | Edge Worker |
|--------|-----|--------|-------------|
| Расположение | Edge | Регион | Edge |
| Логика | Нет | Полная | Ограниченная |
| Старт | — | сотни мс | ~0 мс |
| Runtime | — | Полная ОС | Только JS/Wasm |
| Состояние | Статика | Внешнее | KV / DO |

## Q3. (!) Benefits и когда применять?

**Преимущества:**

**1. Низкая latency:**
- 10-50 мс против 100-300 мс
- Критично для воспринимаемой скорости (LCP < 2.5 с)

**2. Разгрузка origin:**
- Edge берёт на себя логику → origin обрабатывает меньше трафика → дешевле и стабильнее

**3. Мгновенный глобальный масштаб:**
- Деплой один раз; работает в 300 POP
- Нет сложности мультирегионального деплоя

**4. Стоимость (зачастую):**
- Оплата за вызов; нет платы за простой
- Cloudflare Workers: $5 за 10M запросов

**5. Поглощение DDoS:**
- Сеть Cloudflare поглощает атаки в 71 Tbps
- Edge-функции выполняются уже после митигации

**Когда применять:**
- Персонализация статичного сайта (внедрение данных под пользователя)
- Авторизация на edge (отсечь неавторизованных до origin)
- A/B-тестирование, feature-флаги
- Преобразование изображений/видео
- Проксирование / агрегация API (BFF на edge)
- Приём данных IoT

**Когда НЕ стоит:**
- Тяжёлые вычисления (ML-инференс под нагрузкой) — лимиты CPU
- Долгие операции (> 30 с) — не поддерживаются
- Stateful-приложения с высокой консистентностью — сложно

## Q4. (!) Cloudflare Workers — как работает?

**V8-изолят** — лёгкая JavaScript-песочница (та же, что у вкладок Chrome).

**Архитектура:**
- Код воркера (JS/TS/Wasm)
- Развёрнут на всех POP Cloudflare (300+)
- Первый запрос к POP → загрузка воркера (~1 мс)
- Последующие запросы → переиспользование изолята (микросекунды)

**Пример:**
```js
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (url.pathname === '/api/hello') {
      return new Response(JSON.stringify({ msg: 'Hello from ' + request.cf.city }));
    }
    return fetch(request);  // passthrough к origin
  }
}
```

**Возможности:**
- Fetch API (веб-стандарт)
- KV store (eventually consistent key-value)
- Durable Objects (strongly consistent, stateful)
- Cron-триггеры
- D1 (SQLite на edge)
- R2 (S3-совместимое хранилище, нулевой egress)
- Queues, AI

**Лимиты runtime:**
- CPU-время: 10 мс бесплатно / 30 с на платном тарифе
- Память: 128MB
- Размер запроса: 100MB

**Языки:** JS, TS, WASM (Rust, C, Go → wasm)

## Q5. (!) Lambda@Edge vs CloudFront Functions?

**CloudFront Functions:**
- Новее (2021)
- Только JavaScript
- Выполнение < 1 мс
- Только viewer request/response
- Простые сценарии (манипуляции с заголовками, редиректы)
- Дёшево ($0.10 за миллион)

**Lambda@Edge:**
- Старше и мощнее
- Node.js, Python
- Выполнение до 5 с (viewer) / 30 с (origin)
- Полные возможности Lambda (вызов сервисов, дольше работа)
- 4 точки-триггера:
  - Viewer request (до кэша)
  - Origin request (после промаха кэша)
  - Origin response (до кэша)
  - Viewer response (после кэша)
- Дороже ($0.6 за миллион)

**Сценарии:**
- CloudFront Functions: переписывание URL, манипуляции с заголовками, простая авторизация
- Lambda@Edge: персонализация, сложная маршрутизация, ресайз изображений

**Cold start:** Lambda@Edge медленнее (50-200 мс на холодную); Functions запускаются мгновенно.

**Распространение деплоя:**
- Lambda@Edge: 2-5 мин по миру
- CF Functions: быстрее

## Q6. V8 isolates vs containers?

**V8-изолят (Workers):**
- Лёгкая песочница внутри движка V8
- Изоляция по памяти, а не по процессам
- Несколько изолятов делят один V8-процесс
- Старт: микросекунды
- Подходит для короткого JS/WASM-кода

**Контейнер (Lambda):**
- Полноценная ОС / runtime
- Изоляция процессов, разделение на уровне VM (Firecracker)
- Старт: от 100 мс до нескольких секунд
- Поддержка любого языка / бинаря

**Компромиссы:**

| Аспект | Изолят | Контейнер |
|--------|---------|-----------|
| Старт | мкс | 100 мс+ |
| Плотность | тысячи на машину | десятки |
| Безопасность | Общий процесс (V8) | Уровень VM |
| Языки | JS, WASM | Любые |
| Runtime | Ограниченный | Полный |

**Безопасность:** изоляты опираются на корректность V8. Эксплойты V8 → пересечение между тенантами. Редко, но возможно.

**Современный тренд:** гибрид — Workers для горячих путей, Lambda для сложного.

## Q7. (!) Typical use cases?

**1. Авторизация / валидация JWT:**
- Отклонять неавторизованные запросы на edge
- Беречь нагрузку origin (не тратить вычисления на 401)

**2. Переписывание URL / маршрутизация:**
- A/B-тест: 50% пользователей → backend v2
- Географическая маршрутизация: пользователи из EU → EU-origin

**3. Персонализация:**
- Вставка пользовательских сниппетов в HTML
- Закэшированная базовая страница + динамическая вставка на edge

**4. Оптимизация изображений:**
- Ресайз на лету в зависимости от устройства/экрана
- Конвертация в WebP/AVIF
- Cloudflare Image Resizing, AWS Lambda@Edge

**5. Детектирование ботов / WAF:**
- Анализ паттернов запросов
- Блокировка ботов без обращения к origin

**6. API gateway / BFF на edge:**
- Агрегация нескольких бэкендов
- Преобразование ответов под клиента
- См. [паттерн BFF](bff-pattern-interview.md)

**7. Гео-блокировка / комплаенс:**
- Блокировка конкретных стран
- Показ GDPR-баннеров согласия только в EU

**8. Кэширование с кастомной логикой:**
- Кастомизация ключа кэша
- Stale-while-revalidate
- Edge-purge по требованию

**9. Real-time-функции:**
- Терминация WebSocket на edge
- Pub/sub (Cloudflare Realtime)

**10. AI-инференс:**
- Лёгкие модели на edge (Cloudflare AI, Vercel AI)
- Low-latency-генерация

## Q8. Auth/JWT validation at edge?

**Поток:**
```
Request → Edge Worker
  ├─ No token → 401
  ├─ Invalid token → 401
  ├─ Valid → forward to origin with verified claims
```

**Код (Cloudflare Workers):**
```js
export default {
  async fetch(request, env) {
    const token = request.headers.get('Authorization')?.replace('Bearer ', '');
    if (!token) return new Response('Unauthorized', { status: 401 });
    
    try {
      const payload = await verifyJWT(token, env.JWT_SECRET);
      const headers = new Headers(request.headers);
      headers.set('X-User-ID', payload.sub);
      return fetch(new Request(request.url, { method: request.method, headers, body: request.body }));
    } catch (e) {
      return new Response('Invalid token', { status: 401 });
    }
  }
};
```

**Преимущества:**
- Origin всегда получает уже аутентифицированные запросы
- Атакующий трафик не доходит до origin
- Упрощает backend (доверие заголовку от edge)

**Подводные камни:**
- Управление секретами (безопасно раздать на edge) — использовать Cloudflare Secrets
- Отзыв токенов сложен (кэш на каждом edge) — короткие TTL
- Координация ротации ключей

## Q9. Image optimization, resize?

**Требование:** отдавать правильный размер/формат под устройство/соединение.

**Без edge:** клиент скачивает крупный файл → теряет трафик и время.

**С edge:**
```
Request /image.jpg?w=500&format=webp
  ↓
Edge worker:
  - Fetch original from origin
  - Resize to 500px width
  - Convert to WebP
  - Cache result at edge
  - Return
```

**Инструменты:**
- Cloudflare Image Resizing (managed)
- AWS Lambda@Edge + Sharp
- Fastly Image Optimizer
- Vercel Image Optimization

**Преимущества:**
- Экономия трафика (на 10-50% меньше)
- Быстрее загрузка страниц
- Соответствие устройству (retina против 1x)

**Кэширование:**
- Кэш на каждый вариант (параметры в URL)
- За hit ratio отвечает CDN-кэш

**Согласование формата:**
- `Accept: image/webp` → edge отдаёт WebP
- `Accept: image/avif` → AVIF (ещё меньше)
- Откат на JPEG для старых браузеров

## Q10. A/B testing, personalization?

**Проблема:** A/B-тесты требуют динамического контента, но кэширование это ломает.

**Подход на edge:**
1. Кэшировать базовый HTML на edge
2. Edge-функция определяет вариант (cookie пользователя, случайно)
3. Внедряет в ответ контент конкретного варианта
4. Возвращает изменённый HTML

**Код:**
```js
export default {
  async fetch(request, env) {
    let variant = getCookie(request, 'ab_variant');
    if (!variant) {
      variant = Math.random() < 0.5 ? 'A' : 'B';
    }
    
    const origResp = await fetch(request);
    const html = await origResp.text();
    const modified = html.replace('__CTA__', variant === 'A' ? 'Buy Now' : 'Get Started');
    
    const resp = new Response(modified, origResp);
    resp.headers.append('Set-Cookie', `ab_variant=${variant}; Max-Age=86400`);
    return resp;
  }
};
```

**HTMLRewriter** (Cloudflare): потоковый API в стиле DOM, производительнее, чем replace.

**Feature-флаги:**
- Edge опрашивает сервис флагов (закэшированно)
- Включает функции по пользователю / по региону

**Персонализация без мерцания:**
- Не нужен клиентский fetch (контент готов сразу при доставке)
- Нет FOUC (flash of unstyled content)

## Q11. (!) Как handle state at edge?

**Сложность:** edge-узлы по умолчанию stateless; 300+ POP.

**Варианты:**

**1. Без состояния:**
- Всё через origin
- Простейший вариант

**2. Кэш (эфемерный):**
- Edge-кэш (уровень CDN)
- На основе TTL
- Multi-POP: у каждого POP свой кэш → дублирование, но быстро

**3. KV store (eventually consistent):**
- Cloudflare Workers KV, DynamoDB global tables
- Запись → со временем реплицируется на все POP
- Latency чтения ~1 мс
- Низкая пропускная способность записи (лимиты на ключ)

**4. Durable Objects (strongly consistent):**
- Один инстанс на объект, в любой точке сети
- Привязка к конкретному POP
- Полезно для координации (чат-комната, счётчик)

**5. Внешняя БД:**
- Вызов Postgres, DynamoDB из воркера
- Добавляет round-trip (edge→БД)

**Выбор по сценарию:**
- Токены сессий пользователей: KV (много чтения, eventual допустим)
- Состояние чат-комнаты: Durable Objects (строгая консистентность)
- Счётчик по всем узлам: Durable Objects или атомарный Redis

## Q12. Cloudflare Durable Objects?

**Durable Object:** stateful-сервис в одном инстансе, привязанный к одному POP.

**Модель:**
- Именованный объект (`getDurableObject("chat-room-123")`)
- Один инстанс глобально; все записи идут туда
- Сохраняется в storage (key-value, транзакционно)
- Поддержка WebSocket

**Сценарии:**
- Чат-комнаты (общее состояние)
- Real-time-совместная работа (как Google Docs)
- Атомарные счётчики (без гонок)
- Координация (выбор лидера)

**Код:**
```js
export class ChatRoom {
  constructor(state, env) {
    this.state = state;
    this.sessions = [];
  }
  
  async fetch(request) {
    // WebSocket upgrade
    const [client, server] = Object.values(new WebSocketPair());
    this.sessions.push(server);
    server.accept();
    server.addEventListener('message', (e) => {
      for (const s of this.sessions) s.send(e.data);
    });
    return new Response(null, { status: 101, webSocket: client });
  }
}
```

**Компромисс:** DO привязан к одному POP → выше latency для пользователей в других регионах, зато строгая консистентность.

**Стоимость:** $0.20 за миллион запросов.

## Q13. KV stores (Workers KV, DynamoDB Global)?

**Workers KV:**
- Eventually consistent key-value
- Чтение: ~1 мс (кэшируется на edge)
- Запись: ~1 мин до распространения по миру
- Оптимизирован под чтение (терпит устаревшие данные)
- Лимит 100 записей/сек на ключ

**Сценарии:**
- Feature-флаги
- Конфигурация
- Статический контент
- Маппинги маршрутов

**DynamoDB Global Tables:**
- Мультирегиональная репликация
- Eventually consistent между регионами
- Более тяжёлый API (медленнее KV)
- Используется из Lambda@Edge

**FaunaDB, PlanetScale:**
- Глобальные SQL-базы
- Строгая консистентность внутри региона, eventual между регионами

**Выбор по:**
- Нужна ли консистентность?
- Частота записи?
- Сложность (KV против SQL)?

## Q14. (!) Ограничения edge runtime?

**Cloudflare Workers:**
- CPU: 10-30 с (в зависимости от тарифа)
- Память: 128MB
- Нет доступа к файловой системе (кроме встроенных в бандл ассетов)
- Нет нативных Node-API (workerd — подмножество)
- Часть npm-пакетов не поддерживается

**Lambda@Edge:**
- CPU: 5-30 с
- Память: 128MB-10GB
- Viewer request/response: 5 с, тело 1MB

**Возможности runtime (Workers):**
- Fetch API ✅
- Web Streams ✅
- Crypto (SubtleCrypto) ✅
- TextEncoder/Decoder ✅
- Нет `fs`, нет прямого TCP (только fetch)
- Нет дочерних процессов

**Языки:**
- В основном JS/TS
- WASM (Rust, Go, C++) — но ограниченные bindings
- Python (новее, через Pyodide в Workers)

**Размер деплоя:**
- Workers: 10MB в сжатом виде
- Lambda@Edge: 50MB

**Известные ограничения:**
- Нет long-polling / долгоживущего HTTP (максимум 30 с)
- Нет диска — нужно использовать KV/DO/R2
- Региональный комплаенс (резидентность данных)

## Q15. Cold starts?

**Workers:**
- Практически нулевой (V8-изолят = мкс)
- Первый запрос к POP может дать ~1-5 мс на загрузку

**Lambda@Edge:**
- Cold start 50-500 мс
- Node.js прогревается быстрее, чем Java/.NET
- Снизить: меньше пакет, Provisioned Concurrency

**Митигация:**
- Держать пакеты маленькими
- Переиспользовать runtime (не импортировать крупные модули динамически)
- Для Workers это не проблема

**Тёплый инстанс:**
- После первого запроса POP держит изолят/контейнер тёплым ~5-15 мин
- Частый трафик = всегда тёплый

## Q16. Compute cost vs traditional?

**Cloudflare Workers (2024):**
- Платный: $5/мес, 10M запросов включено
- Сверх лимита: $0.50 за миллион
- CPU: максимум 30 с, медианно тарифицируется 50 мс

**AWS Lambda@Edge:**
- $0.60 за миллион запросов
- + $0.0000125128 за GB-секунду

**Сравнение (100M запросов/мес):**
- Workers: ~$50
- Lambda@Edge: ~$60 + вычисления
- EC2 (always-on): зависит от размера (сотни-тысячи $)

**Edge дешевле для:**
- Трафика, на 90%+ кэшируемого на edge
- Пиковых нагрузок (масштабируется до 0)
- Глобальной распределённости (вместо деплоя мультирегионального EC2)

**Edge дорог для:**
- Тяжёлых вычислений (CPU-bound)
- Долгих запросов
- Stateful-приложений

## Q17. (!) Debugging и observability?

**Логи:**
- Cloudflare: live-стрим `wrangler tail`
- Lambda@Edge: CloudWatch (из региона, где выполнялось — их может быть много!)

**Метрики:**
- Дашборд Cloudflare: запросы, ошибки, CPU-время на воркер
- Метрики CloudFront: cache hit ratio, ошибки

**Трассировка:**
- Сложнее, чем обычно — кросс-регионально
- Экспорт OpenTelemetry в Honeycomb, Datadog
- Workers Trace Events API

**Локальная разработка:**
- `wrangler dev` — запускается локально с miniflare (эмулятор workerd)
- `sam local` для Lambda@Edge — ограничен

**Тестирование:**
- Unit: мокать Fetch, KV
- Integration: деплой на staging-воркер

**Обработка ошибок:**
- Try/catch в воркере; репорт в Sentry
- Возвращать корректный ответ об ошибке (не 5xx)

## Q18. Deployment strategies?

**Cloudflare Workers:**
- `wrangler deploy` — мгновенно, глобально (распространение 5-30 с)
- Постепенный rollout (canary) через %-маршрутизацию:
```
routes: [
  { pattern: "example.com/*", script: "worker-v2", percent: 10 }
]
```
- Откат быстрый (передеплой предыдущей версии)

**Lambda@Edge:**
- Пиннинг версии (1 версия на путь дистрибуции)
- Распространение 2-5 мин по миру
- Откат: переключить CloudFront на прежнюю версию

**CI/CD:**
- GitHub Actions → `wrangler deploy`
- Сначала тесты в preview-окружении

**Переменные окружения / секреты:**
- Конфиги под каждое окружение
- `wrangler secret put` — зашифрованное хранилище

**A/B-деплой (feature-флаг):**
- Тот же воркер, ветвление по флагу из KV
- Мгновенное переключение без деплоя

---

## See also

- [BFF Pattern](bff-pattern-interview.md) — API aggregation at edge
- [API Gateway](api-gateway-interview.md) — edge gateway
- [Caching Strategies](caching-strategies-interview.md) — CDN edge cache
- [Caching Performance](../performance/caching-performance-interview.md) — CDN hit ratio
- [Network Performance](../performance/network-performance-interview.md) — latency, CDN
- [Serverless](../cloud/serverless-interview.md) — Lambda, related model
- [Microservices](microservices-interview.md) — distribution patterns
- [Scalability Patterns](scalability-patterns-interview.md) — global scaling
- [Distributed Systems](distributed-systems-interview.md) — edge = distributed
- [Observability](../monitoring/observability-interview.md) — debug edge workloads
