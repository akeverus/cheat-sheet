# REST Assured — ревью исходной редакции и исправления MCQ

Дата: 2026-11-05. Codex, контекст координатора MCQ. Прочитаны Q1–Q15, все 60 вариантов с sections и соответствующая теория другого автора. Режим — **distinct-author independent open-key review** исходной редакции. Строгие слепые Phase A/B и workflow record **NOT_RUN**. После исправлений исполнитель является их автором и не присваивает им независимую приёмку.

Исходный JSON `modules/quiz-app/src/main/resources/seed/mcq/testing/rest-assured-interview.json`: SHA `802a8fd5fc61e3e6eb03a07210d07389db825a9839ab44dc3a188ac5c422e335`. Теория `cheatsheets/interview/testing/rest-assured-interview.md`: SHA `fa11bc76d953d3148cdc040bd5abfb180f0701472d532ba886f28c26e528f6cd`; в этой задаче не менялась.

Вердикт исходной редакции **NEEDS_REPAIR**. Исправленный JSON SHA `21c0382eee537f27e2b9d2b0d8b3bd60b7a3f10dccb90f40fff4076e311f4917`: изменены Q2/Q3/Q8/Q9/Q11/Q13/Q14. Q1/Q4/Q5/Q6/Q7/Q10/Q12/Q15 сохранены относительно авторского снимка. Все q_number, block_idx, question_text, order, labels и correct сохранены. Исправленная пара **NEEDS_THEORY_SYNC**, новая независимая приёмка и FINAL **NOT_RUN**. Старый авторский LLM_READY относится к старому SHA.

## Подтверждённые находки

1. **Q2/Q8 — стиль DSL и API.** `when()` возвращает RequestSpecification; настройку можно продолжать до HTTP-метода. `then/expect` позволяют описать ожидания до запроса, что не означает проверку ответа до его получения. В Q2 A теперь неверно утверждает, что `when()` уже отправляет запрос. В Q8 D теперь путает создание спецификации с проверкой ответа. Исправлены пояснения; ключи D и A сохранены. Для Map, как и POJO, нужен mapper; автоматическое URL encoding действует по умолчанию и отключается. [RequestSpecification 6.3.0](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/specification/RequestSpecification.java).
2. **Q8 — scope и merge.** В исходной теории responseSpec локальна в setUp, а fetchUser использует её в другом методе. Нужно поле класса. В sections исправлено и обещание безусловного сложения ожиданий: часть настроек spec может перезаписывать текущие значения, включая status/content type. [ResponseSpecification 6.3.0](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/specification/ResponseSpecification.java).
3. **Q13 — отрицательный кейс не доходит до HTTP.** Строка `", 400"` передаёт email=null. Map.of бросает NPE до вызова body, поэтому API не получает предполагаемый вход. В sections разделены JSON null, отсутствующее поле и пустая строка. Для null предлагается null-capable Map/DTO; quoted empty cell представляет другой случай. [CsvSource 5.10.3](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/CsvSource.html).
4. **Q14 — логирование и проверка GPath.** Обычный RequestLoggingFilter не является фильтром «только при падении»; условие нужно задать отдельно либо использовать ifValidationFails. Исправлено универсальное отрицание возможностей IDE: Java-компилятор не проверяет содержимое GPath, возможности IDE зависят от поддержки. Локальная конфигурация не означает безопасность общего mutable spec. [RequestLoggingFilter 6.3.0](https://github.com/rest-assured/rest-assured/blob/rest-assured-6.3.0/rest-assured/src/main/java/io/restassured/filter/log/RequestLoggingFilter.java).
5. **Q11 — границы схемной валидации.** Нельзя обещать обнаружение любого исчезнувшего поля: в JSON Schema properties не делает поле обязательным; нужен required. Проверяются выраженные в схеме ограничения, не все правила совместимости или бизнеса. [Официальная JSON Schema object reference](https://json-schema.org/understanding-json-schema/reference/object).

## Полный охват и альтернативы

| Q | Ключ и результат проверки четырёх вариантов |
|---|---|
| 1 | B: обычный core DSL и HTTP; stub-only, raw-only и автоматическая полнота контракта отвергнуты. |
| 2 | D: роли given/when/then как стиль; A исправлен на ложную отправку в when, B/C ложно ограничивают assertions. |
| 3 | A: строка, Map и POJO; остальные отрицают эти способы, условие mapper уточнено. |
| 4 | C: extract и преобразование; запрет сочетания assertions/extraction и отрицание POJO неверны. |
| 5 | B: Groovy GPath, не стандартный JSONPath; отрицание closures и безусловное преимущество индексов неверны. |
| 6 | D: challenged и preemptive Basic различаются; ручной Authorization допустим. |
| 7 | C: общая конфигурация запроса; не только URI, не response expectations, локальные дополнения разрешены. |
| 8 | A: общие ожидания и дополнительные assertions; время поддерживается, build не проверяет отсутствующий ответ. |
| 9 | D: отдельные query/path/form/multipart API; URL encoding уточнён как default. |
| 10 | B: HTTP с сервером и MockMvc без сети; зарегистрированные фильтры возможны, случайный порт доступен. |
| 11 | A: XML GPath/XSD; XML, атрибуты и JSON Schema поддерживаются, границы схемы уточнены. |
| 12 | C: завершаемый SSE как строка; не native WS и не последовательный reader непрерывных событий. |
| 13 | D: ресурсы, фабрики, параметризация; отрицательный null-кейс примера требует исправления payload. |
| 14 | B: гонки глобальных настроек; Java не проверяет строковый GPath, индексы и безусловное полное логирование имеют ограничения. |
| 15 | A: RA вне Spring, WebTestClient mock/live MVC/WebFlux, TestRestTemplate для живого сервера. |

SFPL/ANSC/RU: просмотрены предмет, условность, отрицания, API-маркеры и видимый вопрос. В новой Q2 код присутствует в A и D, двоеточие только у ключа устранено. Скрытое question_text не добавляет условие правильности. Объяснения правок описывают механизм; новые авторские corrections не получают независимый SFPL/ANSC/RU PASS.

## Проверки и ограничения

Schema **PASS 15 Q / 15 B**. Parity **PASS**, max word ratio 1.27 (Q8), correct-longest 1/15, style guessability 0.25. Anti-tell **0/15**. SKEL **hard=0 / warn=3**: скобки Q2/Q8, тире Q10. Linkage **errors=0 / warnings=3** (Q1 A, Q5 A, Q11 B): теория прямо описывает HTTP core, отличие GPath и поддержку XML; пересечение слов низкое, ручная сверка выполнена. Все семь диагностик завершаются exit 0. Они не заменяют семантическую приёмку.

В отдельной локальной JVM исполнены три случая с настоящим JUnit **5.10.3** и JDK21: исходная аннотация CsvSource, ожидаемый NPE Map.of для null и сохранение null в HashMap. **3 passed, 0 failed**. REST Assured/HTTP/MockMvc/SSE end-to-end **NOT_RUN**: JAR io.rest-assured не найдены в проверенных Gradle/Maven caches; установки и загрузки не выполнялись. Java scope Q8 проверен чтением, не полной компиляцией примера. Исходники REST Assured для API-проверки закреплены на **6.3.0**, это не утверждение о версии зависимости проекта.

Артефакты task-2: `rest-assured-peer-original-findings.json`, baseline seed/theory, `rest-assured-peer-prepared.json`, candidate/patch, `rest-assured-peer-final-checks.json` и логи 0..6, `rest-assured-csv-runtime-check.json`, `rest-assured-csv-check/RestAssuredCsvCheck.java`. Исходные findings сохранены до коррекции, ключи и идентичность проверены сравнением.

**Владельцу теории:** синхронизировать Q2 (стиль, не запрет API), Q3 (mapper для Map/POJO), Q8 (поле responseSpec, ожидания до запроса, merge/overwrite), Q9 (encoding default), Q11 (required/type и границы контракта), Q13 (null payload до HTTP), Q14 (условное логирование, Java compiler/IDE, локальность mutable spec). Сначала исправить Q8 и Q13 как препятствия воспроизведению примеров. Пока теория не обновлена, пара не готова к FINAL. Staging, коммиты и обновление работающего сервиса в этом проходе не выполнялись.
