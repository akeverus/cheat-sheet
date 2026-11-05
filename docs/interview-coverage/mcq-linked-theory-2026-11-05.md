# Проверка 13 шпаргалок, связанных с MCQ-коммитами — 05.11.2026

Проверены изменения Markdown к MCQ-коммитам `e0fc92447` (embeddings/vector),
`76b9f1a69` (структуры данных) и `740d896b3` (API). Это авторская проверка
изменённых заголовков, ответов и их связи с MCQ, а не полная независимая
приёмка старого корпуса. Неизменённые ответы старых файлов повторно целиком
не сертифицировались. Номера Q сохранены; оглавления ведут на явные якоря `qN`.

Сохранённые тематические коммиты: Python testing `08b8a8418` (отдельный
пакет), embeddings/vector theory `240a0d93e`, структуры данных `042535aba`.
Оставшиеся шесть API-шпаргалок и связанные точечные исправления передаются
отдельной группой вместе с этим отчётом.

## Изменения и исправления

- Embeddings и vector databases: проверены переписанные ответы, ограничения
  метрик/нормализации, память сырых векторов, ANN/recall и границы продуктовых
  утверждений. Конкретные сведения Voyage 4/Cohere Embed 5, Milvus и pgvector
  сверены с первичными источниками; примеры внешних SDK и обучение моделей
  не запускались.
- Структуры данных: проверены смысловые изменения и уточнения условий
  вопросов. В heaps Q24 восстановлена явная привязка к началу координат,
  указаны допустимые k и диапазон координат, при котором сумма квадратов
  помещается в long. MCQ больше не показывает старый компаратор через
  вычитание: объяснения согласованы с Long.compare и приведением до умножения.
- API: проверены изменённые ответы о HTTP, совместимости, GraphQL, gRPC,
  OpenAPI и гипермедиа. В Sunset Q17 исправлен день недели 31.12.2028 в обеих
  поверхностях, убрана ложная гарантия, что заголовок обязательно прочтут.
  Удалена ссылка-заглушка на отсутствующую шпаргалку о backwards compatibility.
- GraphQL Q31: разведены библиотека graphql-ws и подпротокол graphql-transport-ws,
  SSE признан допустимым транспортом типизированных подписок; исправлены
  таблица и MCQ-пояснения. Flux.timeout ограничивает паузу между событиями,
  а не общий срок подписки. Код подписки по реальной сети не запускался.
- Сомнение по OpenAPI Q42 снято: OAS 3.1.1 прямо допускает contentMediaType
  для сырого бинарного содержимого; утверждение сохранено.
- У шести sidecar (graphs, heaps, linked-lists, api-versioning, graphql, grpc)
  явно оставлен TODO. Прежние CHECKED и устаревшие заявления о бессрочной
  поддержке Stripe/обязательном устаревании stitching не переносятся на новые
  тексты. Общие audit-state/checkpoint MCQ не редактировались.

## Проверки

`verify-mcq-json` для 13 пар: **PASS, 394 Q / 394 блока**; semantic review
инструмента — NOT_RUN. Проверка frontmatter (Ruby Psych), стабильных Q,
оглавлений, явных якорей, существования относительных Markdown-ссылок,
See also и отсутствия inline MCQ — PASS. Адресный git diff --check — PASS.

Извлечённый Java-пример heaps Q24 исполнен на 101 наборе данных для всех
k от 1 до n, включая координаты ±10^9. Результат сравнивался с точной
сортировкой расстояний. Контрольная замена Long.compare на вычитание
с приведением к int обнаружила ошибку. Числовые оценки памяти векторных
представлений проверены арифметически.

Parity gate — диагностический результат, **12 FAIL / 1 PASS**. Ничего не
объявляется FINAL/AUDITED. Отсутствие checked_date у TODO означает, что
полная актуальность ещё не подтверждена; это не пропущенная зелёная проверка.

| Тема | Блоков | Parity | Незакрытые сигналы |
|---|---:|---|---|
| `embeddings-interview` | 29 | FAIL | OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, ABSOLUTE_MARKER_GAP, SOURCE_COVERAGE |
| `vector-databases-interview` | 28 | FAIL | OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, ABSOLUTE_MARKER_GAP |
| `graphs-interview` | 34 | FAIL | CORRECT_LONGEST_RATE, OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, SOURCE_COVERAGE, STYLE_GUESSABILITY |
| `heaps-interview` | 29 | FAIL | OPTION_LENGTH_RATIO |
| `linked-lists-interview` | 32 | FAIL | OPTION_LENGTH_RATIO, SOURCE_COVERAGE |
| `stacks-queues-interview` | 25 | PASS | — |
| `tries-interview` | 28 | FAIL | OPTION_LENGTH_RATIO, SOURCE_COVERAGE |
| `api-design-best-practices-interview` | 30 | FAIL | OPTION_LENGTH_RATIO, ABSOLUTE_MARKER_GAP, SOURCE_COVERAGE |
| `api-versioning-interview` | 20 | FAIL | CORRECT_LONGEST_RATE, OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, SOURCE_COVERAGE, STYLE_GUESSABILITY |
| `graphql-interview` | 40 | FAIL | CORRECT_LONGEST_RATE, OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, SOURCE_COVERAGE, STYLE_GUESSABILITY |
| `grpc-interview` | 40 | FAIL | CORRECT_LONGEST_RATE, OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, ABSOLUTE_MARKER_GAP, SOURCE_COVERAGE, STYLE_GUESSABILITY |
| `openapi-swagger-interview` | 42 | FAIL | CORRECT_LONGEST_RATE, OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, DETAIL_PARITY, ABSOLUTE_MARKER_GAP, SOURCE_COVERAGE, STYLE_GUESSABILITY |
| `rest-maturity-interview` | 17 | FAIL | CORRECT_LONGEST_RATE, OPTION_LENGTH_RATIO, CORRECT_WRONG_AVG_RATIO, SOURCE_COVERAGE, STYLE_GUESSABILITY |

Независимые semantic/SFPL/ANSC/RU/blind-приёмка остаются NOT_RUN. Подробные
хеши экспортов и формальное принятие относятся к отдельной MCQ-очереди.
В OpenAPI question_text некоторых JSON отличается от заголовка вопросительным
знаком, Q4 — равнозначной формулировкой; скрытого дополнительного условия нет.
Они не переписывались ради буквального совпадения неиспользуемого поля.

## Первичные источники

- [Voyage embeddings](https://docs.voyageai.com/docs/embeddings),
  [Cohere Embed 5](https://cohere.com/blog/embed-5),
  [Cohere models](https://docs.cohere.com/docs/cohere-embed),
  [Milvus consistency](https://milvus.io/docs/consistency.md),
  [pgvector](https://github.com/pgvector/pgvector).
- [Princeton Prim](https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/PrimMST.html),
  [Kruskal](https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/KruskalMST.html),
  [pairing heap paper](https://www.cs.cmu.edu/~sleator/papers/pairing-heaps.pdf),
  [Java PriorityQueue](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/PriorityQueue.html),
  [Stack](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Stack.html),
  [JLS integer operations](https://docs.oracle.com/javase/specs/jls/se25/html/jls-4.html#jls-4.2.2).
- [OpenAPI 3.1.1 binary data](https://spec.openapis.org/oas/v3.1.1.html#working-with-binary-data),
  [GraphQL specification](https://spec.graphql.org/September2025/),
  [Spring transports](https://docs.spring.io/spring-graphql/reference/transports.html),
  [graphql-ws protocol](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md),
  [Reactor timeout](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#timeout-java.time.Duration-),
  [gRPC retry](https://grpc.io/docs/guides/retry/),
  [gRPC service config](https://grpc.io/docs/guides/service-config/),
  [Pact](https://docs.pact.io/).
- [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html),
  [RFC 8594](https://www.rfc-editor.org/rfc/rfc8594.html),
  [RFC 9745](https://www.rfc-editor.org/rfc/rfc9745.html),
  [Stripe versioning](https://docs.stripe.com/api/versioning),
  [RateLimit Internet-Draft](https://datatracker.ietf.org/doc/draft-ietf-httpapi-ratelimit-headers/).

Следующая работа: отдельная независимая приёмка этих MCQ и разбор оставшихся
незакоммиченных тем по актуальному authoring-checkpoint. Ни остальные материалы,
ни весь массив из прежнего аудита не получили принятие по результатам этой проверки.
