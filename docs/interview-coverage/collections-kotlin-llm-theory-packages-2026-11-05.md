# Java Collections, Kotlin и token bucket: следующий пакет теории

Дата: 2026-11-05. Подготовлены прежние изменения трёх шпаргалок и
небольшие уточнения после проверки. После передачи индекса материалы
разделены на адресный JVM-коммит и адресный пакет LLM с этим отчётом.
JSON не редактировался.

Область: Java Collections Q19/Q20/Q43/Q46, Kotlin Q5/Q30,
LLM integration Q14 — **7 из 119** вопросов трёх тем. Остальные
112 вопросов **NOT REVIEWED**. Это не MCQ `FINAL`.

## Java Collections

[Source peer review](java-collections-cache-theory-review-2026-11-05.md)
проверяет access-order LRU, момент eviction, синхронизацию обхода,
best-effort fail-fast, сильные значения WeakHashMap и отличие
Caffeine weakKeys по identity. Четыре секции после peer review не
менялись; позднее обновлена только metadata `updated`.

Peer SHA теории: `6cac3895f59669fe8a745e60b67b26720d66485311dd7838065b5d0003bae1fb`.
Текущий SHA: `d1464fb0c5e4fef58f14bb59751b6ff52f85e9850f353d536efc03da8d64d29d`.
Metadata-only delta проверена сравнением с сохранённой исходной копией.

Координатор отдельно извлёк класс LRUCache из Q19 без изменений,
добавил imports и standalone harness. `javac` и runtime Corretto
**22.0.2**: PASS — get/put меняют порядок вытеснения при лимите 2,
нулевой maxSize отклоняется, обход synchronizedMap выполняется под
монитором обёртки. Также проверены null key и сильный путь
`map -> value -> key`. Никаких ожиданий очистки после System.gc()
не проверялось. Дополнительная проверка явного позиционирования
показала на JDK 22: putFirst может вставить запись первой и тут же
вытеснить её, putLast оставляет новую; это не доказательство всех
путей реализации кэша. API-основание — [LinkedHashMap Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/LinkedHashMap.html).

Concurrency stress, GC timing, Caffeine runtime и весь проект
**NOT RUN**. Source peer review сам код не запускал; его прежняя
отметка NOT RUN относится к работе reviewer, результаты выше —
отдельная проверка координатора.

## Kotlin

[Source peer review](kotlin-null-equality-theory-review-2026-11-05.md)
покрывает safe calls, !!, JVM NPE начиная с Kotlin 1.4, equals и
identity с оговоркой о runtime primitive representation.
Исходный peer SHA: `08e44bc63166213a2d76e13f2d2dae4a3ce644e64363007119db7590dd545448`.

После review координатор уточнил Q30: сгенерированный equals data
class использует свойства первичного конструктора, а свойства только
в теле класса не входят в сравнение. Это сверено с первичной
[Kotlin Data classes documentation](https://kotlinlang.org/docs/data-classes.html#properties-declared-in-the-class-body).
Также обновлена metadata `updated`; Q5 не менялся после peer review.
Текущий SHA: `c560ecf4a5a2744667b9008bc69fcf07c85d5574a8e0ebdafa359b881c6731a5`.
Поздняя дельта проверена координатором, не выдана за новый независимый review.

Три Kotlin-фрагмента Q5/Q30 скомпилированы в отдельных функциях без
изменения их содержимого. Компилятор **kotlinc-jvm 2.4.10**, compiler
JRE 26.0.2, `-jvm-target 22`; выполнение jar — Corretto **22.0.2**.
Получены ожидаемые `null`, `null`, NPE, `true`, `false`, `false`.
Дополнительные assertions для primary-constructor properties,
boxed значений через Any и массивов прошли. Это ограниченный JVM
прогон; Native/JS и остальные примеры **NOT RUN**. Исходная отметка
peer NOT RUN не изменена и не подменена результатом координатора.

## LLM integration

[Авторский source review Q14](llm-token-bucket-theory-review-2026-11-05.md)
фиксирует источники OpenAI/Claude, квоты запросов/токенов, неизвестный
расход при прерывании, повторный контроль допуска для retries и
различие полного лимита в каждой реплике от заранее выделенных долей.
Независимого review после прерывания среды нет; API/Redis **NOT RUN**.

## Передать владельцу MCQ

- **LLM Q14:B0 — RESOLVED_AUTHOR_REREAD:** владелец MCQ
  исправил A.edge_cases и D.text/sections. Текущий рабочий блок
  перечитан целиком; безопасные доли квоты отделены от полного лимита
  в каждой реплике. Seed SHA:
  `cff06eac4a46165b221b0169ebbf2e6967f66130c52f095167c96fa539e1ca9b`.
  MCQ-исправление сохранено в `1564e2a8d`, текущий блок совпадает
  с этим коммитом. Независимая приёмка не заявляется; история
  и точные поля сохранены в отчёте Q14.
- **Java Collections Q43:B0 — INFO:** правильный B говорит, что
  достаточно синхронизировать весь обход на обёртке, но лишь sections
  явно требуют направлять все обращения через одну обёртку. Source
  peer считает это необязательным уточнением, не ошибкой ключа.
- Kotlin Q5/Q30: существенного рассинхрона не найдено; поздняя деталь
  о свойствах тела data class дополняет объяснение и не меняет ключ.

## Проверки и сохранность

`verify-mcq-json`: PASS, 3 файла / 119 Q / 119 блоков. Это структурный
прогон. Frontmatter трёх MD разобран Ruby Psych; номера Q, явные IDs,
локальные файлы/anchors, fences и адресный diff --check проверены.
Новые правки ограничены Q30 Kotlin, Q14 LLM и updated metadata;
прежние изменения всех семи секций сохранены. Peer reports сохраняют
исторические SHA; текущие дельты отдельно описаны выше.

Исходные копии, standalone harnesses и результаты проверки сохранены в
`/tmp/collections-kotlin-llm-theory-20261105/`. Список семи файлов и их
SHA-256: `/tmp/prepared-collections-kotlin-llm-theory-20261105.json`.
В пакеты входят только проверенные материалы; общие планы/checkpoints/
registries, публикация и развёртывание не затронуты. Docker отказ
не обходился.
