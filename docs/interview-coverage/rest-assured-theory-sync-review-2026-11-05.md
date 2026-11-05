# REST Assured — синхронизация теории

Дата: 2026-11-05. Режим `interview-writer / SEMANTIC_UPDATE`. Прочитаны все 15 ответов, 60 вариантов с sections и существовавший diff Markdown. Это авторская сверка с открытыми ключами и первоисточниками; независимая приёмка исправлений, слепые Phase A/B и FINAL **NOT_RUN**. JSON не редактировался.

Входной Markdown SHA `fa11bc76d953d3148cdc040bd5abfb180f0701472d532ba886f28c26e528f6cd`, seed SHA `21c0382eee537f27e2b9d2b0d8b3bd60b7a3f10dccb90f40fff4076e311f4917`. Основание: [отчёт владельца MCQ](../mcq-quality/llm-first/reviews/testing/rest-assured-peer-review-2026-11-05.md). Учтены прежние изменения Q1/Q3/Q10/Q12/Q15, даты, введения и якорей. Свои изменения ограничены Q2/Q3/Q5/Q8/Q9/Q10/Q11/Q13/Q14; идентичность вопросов, frontmatter, TOC и See also сохранены относительно входа.

## Исправления

| Q | Подтверждённое изменение |
|---|---|
| 2 | Given/When/Then описан как стиль; `when()` само не отправляет запрос и сохраняет API настройки. Ожидания можно описать до HTTP-вызова. |
| 3 | Для JSON из Map, как и POJO, нужен доступный или заданный mapper; готовая строка уже сериализована. |
| 5 | Поиск сущности при нефиксированном порядке отделён от проверки сортировки или контрактной позиции. |
| 8 | `responseSpec` стала полем класса; объявлены импорты и адрес предполагаемого тестового сервера. Разделены создание и применение ожиданий, описаны merge/overwrite. |
| 9 | URL encoding по умолчанию и его отключение; указан риск повторного кодирования. |
| 10 | Проза о случайном порте согласована с локальным `given().port(port)` в коде. |
| 11 | Комментарий GPath исправлен; схемная проверка ограничена выраженными правилами, отсутствие JSON-поля требует `required`, тип — ограничения `type`. |
| 13 | Допускающая null HashMap вместо Map.of; добавлена quoted empty CSV-ячейка, разделены JSON null, отсутствующее поле и пустая строка. Статусы обозначены как пример контракта, сохранение null зависит от mapper. |
| 14 | Убрана гарантия отсутствия IDE-поддержки GPath; условное логирование отделено от обычного RequestLoggingFilter, локальное имя — от безопасности общего mutable spec. Индексы допустимы при проверке порядка. |

## Первичные источники

API REST Assured проверен на теге **6.3.0**; это версия исследованных исходников, не утверждение о зависимости проекта. [RequestSpecification](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/specification/RequestSpecification.java) подтверждает тип `when()` и сериализацию; [ResponseSpecification](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/specification/ResponseSpecification.java) — `validate` и правила `spec`. В [ResponseSpecBuilder.build](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/builder/ResponseSpecBuilder.java) возвращается спецификация; прямой запрос через объект, полученный от builder, ограничен, поэтому ожидания до вызова показаны через активную цепочку `expect().spec(...)`.

[RestAssured](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/RestAssured.java) содержит включённый по умолчанию encoding и общие статические defaults; наличие volatile/copy-on-write в отдельных местах не устраняет логические гонки тестов, которые меняют общие значения. [RequestLoggingFilter](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/filter/log/RequestLoggingFilter.java) печатает перед `ctx.next`, [LogSpecification](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/specification/LogSpecification.java) отдельно задаёт `ifValidationFails`.

[JUnit CsvSource 5.10.3](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/CsvSource.html) различает unquoted empty и quoted empty; [JSON Schema object reference](https://json-schema.org/understanding-json-schema/reference/object) различает properties, required и null. Для прежних изменений проверены [RestAssuredMockMvc 6.3.0](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/modules/spring-mock-mvc/src/main/java/io/restassured/module/mockmvc/RestAssuredMockMvc.java), [MockMvcWebTestClient 6.1.11](https://github.com/spring-projects/spring-framework/blob/v6.1.11/spring-test/src/main/java/org/springframework/test/web/servlet/client/MockMvcWebTestClient.java), [WebTestClient 7.0.10](https://docs.spring.io/spring-framework/reference/testing/webtestclient.html), [регистрация фильтров MockMvc 7.0.10](https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/filters.html) и [TestRestTemplate Boot 3.3.13](https://docs.spring.io/spring-boot/3.3/api/java/org/springframework/boot/test/web/client/TestRestTemplate.html). Вывод о необходимости завершения SSE для получения строки — граница примера, не результат HTTP-прогона.

## Проверки и ограничения

На JDK **21.0.10** с кешированными JUnit **5.10.3**, Platform **1.10.3** и Jackson **2.17.2** выполнены **6 тестов, 6 успешных**: четыре строки настоящего CsvSource, различие missing/null/empty, влияние NON_NULL. Аннотация CSV и три инструкции построения payload извлечены из Markdown без изменения; HTTP DSL исключён. Для null воспроизведён ожидаемый NPE старого Map.of. Проверены JSON-результаты стандартного ObjectMapper, а не сериализатор REST Assured или ответы сервиса. Значения 201/400 — метки примера, не наблюдённые HTTP-статусы.

Schema **PASS 15 Q / 15 B**. Linkage **errors=0 / warnings=4**: Q1 A (0.111), Q5 A (0.182), Q11 B (0.125), Q14 D (0.100). Авторская сверка: теория описывает реальный HTTP core, GPath вместо классического JSONPath, встроенный XML и возможность полного логирования тела. Низкое лексическое пересечение не исправлялось вставками ради сканера; независимая проверка предупреждений остаётся открыта. Структура, ссылки на локальные файлы, отсутствие inline MCQ и whitespace — **PASS**. Первый вспомогательный structural-check упал из-за неверного индекса capture tuple; исправлен только проверочный скрипт, повторная проверка прошла.

JAR io.rest-assured в проверенных Gradle/Maven caches не найдены. Полная компиляция примеров Q8/Q13, REST Assured serializer/HTTP/spec merge/logging, XML/XSD/JSON Schema matcher, Spring/MockMvc/SSE и полный проект **NOT_RUN**. Установки, скачивания зависимостей и Docker не выполнялись. Исходники — веб-выдержки с URL, не полные локальные byte snapshots. Артефакты: `/tmp/rest-assured-theory-sync-20261105/` (before, head-theory, seed-before, static-checks, web-sources, runtime, linkage и schema logs); точные SHA передаются манифестом.

## Владельцу MCQ

При исходной сверке Q13 D `edge_cases` описывал старый Map.of, а Q5/Q14 sections не различали поиск и проверку порядка. Владелец MCQ затем исправил Q5/Q13/Q14: текущий seed SHA `0936ff31bc2e72353d395a53da086515707197398dc2f7b9d4dfaa65a9b8753e`. Перед коммитом перечитаны все 12 вариантов изменённых вопросов и их sections; номера, ключи и остальные вопросы сверены с исходным снимком. Теория Q5 дополнительно уточняет выбор адресации, Q13 использует HashMap с оговоркой mapper, Q14 уже различает поиск и сортировку. Эти три замечания согласованы по смыслу; JSON не редактировался. Schema/linkage повторены на финальной паре, код CSV/payload повторно сверен с исполненным фрагментом. Исходный манифест сохранён как snapshot; финальный — `/tmp/prepared-rest-assured-theory-sync-20261105-v2.json`. Пара **NEEDS_REVIEW**, новая независимая приёмка и FINAL не присвоены.
