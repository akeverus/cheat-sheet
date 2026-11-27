# План проверки и улучшения MCQ

> **Действующий порядок (21.11.2026):** [LLM полностью перегенерирует production-файл](docs/mcq-quality/workflow.md).
> Отдавай целый исходный JSON, всю парную теорию и единый промпт. Сохраняй полученный полный JSON как есть. Готовый файл принимается без самопроверки, вычитки, сверки хеша и проверок при сборке, в CI или при загрузке приложением.
> Не запускай старые команды и не считай архивные статусы действующей очередью.
> Пилот: `databases/jdbc-connection-pool-interview.json`, Q1–Q6/B0 и все 24 варианта.
> `LLM_READY`: Сергей одобрил качество Q5 и Q6; для следующих тем действует контракт Jackson без изменений.
> Версия пилота SHA-256: `06d7e8fa9a25383024ffeededf506dfb9a4272a99c37d0c40ddc06835db6100b`.
> Записи ниже сохраняются как история предыдущих проходов, в том числе старые команды.

## Работа с новой темой

Передай одну тему целиком по [шаблону поручения MCQ-чату](docs/mcq-quality/workflow.md#шаблон-поручения-mcq-чату).
Шаблон фиксирует версии входов, известные причины возврата и путь к результату.
Полный сгенерированный JSON переносится побайтно. Частичные патчи, вычитка и
проверки после записи в процесс не входят. `LLM_READY` означает, что автор
передал полный файл. Перенос в production, коммит и push фиксируются отдельно.

## Очередь восстановления по разовому аудиту

Исходник для глубины, структуры и оформления — JSON каждой темы из
`b28a1c807`. На каждый файл выдаётся одна задача на полную перегенерацию.
При восстановлении сохраняй все существующие Q/B, обязательные секции,
полезные детали и развёрнутое оформление. Исправления уже установленных
дефектов входят в одно задание по теме.

Историческая партия из 55 файлов определяется четырьмя коммитами переноса
`d11a02c28`, `463b9f806`, `4fc62a7ad` и `08d4d8c15`. Список путей
воспроизводится командой `git diff --name-only b28a1c807 08d4d8c15 -- modules/quiz-app/src/main/resources/seed/mcq`.
После перечисленных ниже приоритетов продолжай по ещё не обработанным путям
этой партии, сверяясь с записями передачи и переноса. Адресные темы вне
диапазона, включая Notification System и Java Functional Interface, не входят
в счётчик 55. Для учёта достаточно путей и записей о передаче полного файла.

Из 55 файлов партии 38 восстановлены в `master`. Вне этой партии уже
закоммичены Notification System, Java Functional Interface, Vector Databases
и JDBC и пулы соединений.

- **Spring Boot Actuator**. MCQ-чат передал полный JSON в ходе
  `01a21df8-b0ea-7033-b0e2-8a7ef44eb605`. Использованы исходный JSON из
  `b28a1c807` и согласованный Markdown (SHA-256
  `2880a5a08c9b552b71d90de0629aa7aa9cfc7444b571150b7be9ae8e06d5819b`).
  Полный JSON перенесён как есть из
  `work/spring-boot-actuator-recovery-full-regeneration/spring-boot-actuator-interview.json`
  вместе с согласованной теорией.

- **Spring Boot 3 Migration**. MCQ-чат передал полный JSON в ходе
  `01a21de6-03db-7ca0-9f7d-dcdae3646fbb`. Использованы исходный JSON из
  `b28a1c807` и согласованный Markdown (SHA-256
  `e5e9724b0755703f9d30e289d9f10f6202c15d30272f40ccab53d00bbcf49535`).
  Полный JSON перенесён как есть из
  `work/spring-boot-3-migration-recovery-full-regeneration/spring-boot-3-migration-interview.json`
  вместе с согласованной теорией.

- **Spring Async**. MCQ-чат передал полный JSON в ходе
  `01a21ddf-e04c-7db0-b7f5-a3134363f663`. Использованы исходный JSON из
  `b28a1c807` и согласованный Markdown (SHA-256
  `5c98c896128dcbf70f72aca6dc9e7c147bceac054e96128c43e0734071af04bf`).
  Полный JSON перенесён как есть из
  `work/spring-async-recovery-full-regeneration/spring-async-interview.json`
  вместе с согласованной теорией.

- **Spring AOP**. MCQ-чат передал полный JSON в ходе
  `01a21dd3-f190-7b71-895b-5a7daee62c7a`. Использованы исходный JSON из
  `b28a1c807` и согласованный Markdown (SHA-256
  `976cff0ffb4fad781ed8ea715c3b0f64594bf96bdd49d2d79a4af85881a181a3`).
  Полный JSON перенесён как есть из
  `work/spring-aop-recovery-full-regeneration/spring-aop-interview.json`
  вместе с согласованной теорией.

- **Kubernetes**. MCQ-чат передал полный JSON в ходе
  `01a21dc6-0e84-70c2-968b-e39c2408864e`. Использованы исходный JSON из
  `b28a1c807` и согласованный Markdown (SHA-256
  `39ead55e5b81e893fec58fc52a852a8505609e80936798cda5db93b50deebd12`).
  Полный JSON перенесён как есть из
  `work/kubernetes-recovery-full-regeneration/kubernetes-interview.json`
  вместе с согласованной теорией.

- **Database Transactions**. MCQ-чат передал полный JSON в ходе
  `01a21db9-8389-7e31-9266-ca70fed1622b`. Использованы исходный JSON из
  `b28a1c807` и согласованный Markdown (SHA-256
  `4cdc5595b9ccb103b1742b728600dbea6445f25d0971b670ea3f054ec7c7e67a`).
  Полный JSON перенесён как есть из
  `work/database-transactions-recovery-full-regeneration/database-transactions-interview.json`
  вместе с согласованной теорией.

- **Database Replication**. MCQ-чат передал полный JSON в ходе
  `01a21da7-2237-7df0-83f0-4f1aadcf626a`. Использованы согласованная теория
  из `9a64cd61e` (SHA-256
  `86beb2f363c0c8aa38f0b5ffb90012b0b884852c40b80d1ba56792ddc1d2dfae`)
  и исходный JSON из `b28a1c807`. Полный файл перенесён как есть из
  `work/database-replication-recovery-full-regeneration/database-replication-interview.json`.

- **Hibernate JPQL and Criteria**. MCQ-чат передал полный JSON в ходе
  `01a21d9f-4e44-7fa2-9449-504f608c8220`. Использованы исходный JSON темы
  из `b28a1c807` и согласованный Markdown (SHA-256
  `2dd34fe9c5042dc66ef9c57db6b41289c145d2f99f18256f5ffbf1375fe45884`).
  Полный JSON перенесён как есть из
  `work/hibernate-jpql-criteria-recovery-full-regeneration/hibernate-jpql-criteria-interview.json`
  вместе с согласованной теорией.

- **MongoDB**. MCQ-чат передал полный JSON в ходе
  `01a21d91-256e-7283-9a49-950b1c9a0c69`. Использованы согласованная теория
  из `8d65aa08d` (SHA-256 `4494fa1b373e4d51ad4c6e3a27bc1464914e11db20e056b9472273dd3770f3d2`)
  и исходный JSON темы из `b28a1c807`. Полный файл перенесён как есть из
  `work/mongodb-recovery-full-regeneration/mongodb-interview.json`.

- **Cassandra**. Автор шпаргалок передал полную теорию с правками формулировок
  Q2/Q8/Q16/Q22/Q42, SHA-256 `c16044222d9b735813b1c9b3faf2ffec2f5dacea283c1d11a4e7e804a5ef091a`.
  MCQ-чат передал полный JSON в ходе `01a21d81-0ae1-7e61-ab55-b5b69183bd62`.
  Оба файла перенесены в production в составе одного коммита.

- **Spring WebFlux** — MCQ-чат передал полный JSON в ходе
  `01a204ea-106d-7092-ad10-323966203352` по согласованному Markdown
  `work/spring-webflux-theory-recovery/spring-webflux-interview.md`
  (SHA-256 `2d60a09951071153b47cb65b5eb7a04e5aa9f40a69edc557dae540aaee4a0c35`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-webflux-recovery-full-regeneration/spring-webflux-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Vault** — MCQ-чат передал полный JSON в ходе
  `01a204df-4c5f-7210-b02c-8a994eefff40` по согласованному Markdown
  `work/spring-vault-theory-recovery/spring-vault-interview.md`
  (SHA-256 `41e374bd3308ef151cba5ae9a517383566b22803f18aef57a4860402c28a9a84`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-vault-recovery-full-regeneration/spring-vault-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Validation** — MCQ-чат передал полный JSON в ходе
  `01a204cf-8aec-7a41-8eae-351bfe63d8e1` по согласованному Markdown
  `work/spring-validation-theory-recovery/spring-validation-interview.md`
  (SHA-256 `e93ef7074167715d2689d24f6f21f85bd3fe557fae86d5d576f8f58899d1063d`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-validation-recovery-full-regeneration/spring-validation-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Transaction** — MCQ-чат передал полный JSON в ходе
  `01a204c9-fc7d-7621-abc1-3ed5819f119d` по согласованному Markdown
  `work/spring-transaction-theory-recovery/spring-transaction-interview.md`
  (SHA-256 `8fc37a7d9152d9036c2f35e652dac4aaac4dcd63d1e9347676edc35ddecd8e24`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-transaction-recovery-full-regeneration/spring-transaction-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Security** — MCQ-чат передал полный JSON в ходе
  `01a204b6-b85c-7c01-9d50-8338f4809dc3` по согласованному Markdown
  `work/spring-security-theory-recovery-v3/spring-security-interview.md`
  (SHA-256 `457febcc5437998ed4d0a92360407118209c49255478d37eb3cdbd69bfd60b6c`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-security-recovery-full-regeneration/spring-security-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring State Machine** — MCQ-чат передал полный JSON в ходе
  `01a204af-e7be-7003-a7e2-9e983d5e2d6b` по согласованному Markdown
  `work/spring-state-machine-theory-recovery/spring-state-machine-interview.md`
  (SHA-256 `28dbf89e26cdd5b09f9f85faa11d12353e6c49c86895938fcc7a959f65647359`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-state-machine-recovery-full-regeneration/spring-state-machine-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Session** — MCQ-чат передал полный JSON в ходе
  `01a204a1-ac42-7431-a9e2-8c483796f00e` по согласованному Markdown
  `work/spring-session-theory-recovery/spring-session-interview.md`
  (SHA-256 `284e9478d1d4b51762552f981046d93f4e20a99396a32da88c3bffedf60d0309`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-session-recovery-full-regeneration/spring-session-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Modulith** — MCQ-чат передал полный JSON в ходе
  `01a20491-81be-75b2-82ff-1d2b4a98a07c` по согласованному Markdown
  `work/spring-modulith-theory-recovery/spring-modulith-interview.md`
  (SHA-256 `574d1a6cf5d24b01b627317aae72874ccb392eccbb915f8b35fcf0ca373185d7`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-modulith-recovery-full-regeneration/spring-modulith-interview.json`.
  Оба production-файла входят в этот коммит.

- **Spring Kafka** — MCQ-чат передал полный JSON в ходе
  `01a2048b-ef1f-75d0-91de-e27a89925034` по согласованному Markdown
  `work/spring-kafka-theory-recovery/spring-kafka-interview.md`
  (SHA-256 `2e359709c7e1715ebd2ae886c9f4e79eee8f3bfaa8fd7dc53d15847d3feb8269`)
  и историческому JSON из `b28a1c807`. Полный JSON перенесён как есть из
  `work/spring-kafka-recovery-full-regeneration/spring-kafka-interview.json`.
  Оба production-файла входят в этот коммит.

- **Notification System** — в Q2/B0 отсутствовал правильный вариант. Автор
  передал полный новый файл по согласованной теории. Файл перенесён
  в production-путь в рабочем дереве.
- **Java Functional Interface** — пропали обязательные секции у всех 56
  вариантов в 14 блоках. Автор передал полный новый файл по обновлённому
  промпту. Файл перенесён в production-путь в рабочем дереве.
- **Java IO/NIO** — потеря секций по теме, в том числе объяснений Q39 о
  `Pipe`. Автор передал полный новый файл. Файл перенесён в production-путь
  в рабочем дереве.
- **Коммит MCQ** `612b40edba39a24423c8f3f6fedb902eb2b0304d` на `master`
  содержит production JSON Notification System, Java Functional Interface
  и Java IO/NIO.
- **Java Annotations** — потеря обязательных секций. Исправленный Markdown
  Q11/Q17/Q35 перенесён в рабочее дерево. MCQ-чату передан закреплённый полный
  снимок (`c8811c8f…`) для перегенерации всего JSON. Автор передал полный
  файл. Пара Markdown и JSON в коммите `0e66efe1b`.
- **Java Collections** — потеря обязательных секций. MCQ-чат вернул тему
  на доработку Markdown по Q9/Q13/Q23/Q26/Q29/Q31/Q32/Q35/Q37/Q41/Q42
  (ход `01a2033b-80b0-7fa0-be25-652ee60a6d56`). Автор шпаргалок исправил все
  11 мест. Полный Markdown (`702df945…`) перенесён в рабочее дерево и передан
  MCQ-чату для полной перегенерации темы. Автор передал полный JSON в ходе
  `01a2034e-5f9e-7f01-a51c-0c6008cf0012`. Пара Markdown и JSON в коммите
  `0e66efe1b`.
- **Java Concurrency** — MCQ-чат вернул Markdown `dc8e12b6…` в ходе
  `01a2035d-c6a2-79c3-86f3-0c2024b34fff`. Очередь чата шпаргалок после Spring
  Batch: Q3/Q15/Q27/Q36/Q38/Q39/Q42/Q44/Q45/Q48–Q50/Q52/Q56. В возврате
  перечислены фактические ошибки, некомпилируемые примеры, условия версии и
  подсказки в материалах до ответа. Автор шпаргалок передал полный снимок
  `work/java-concurrency-audit-theory-return/java-concurrency-interview.md`
  в ходе `01a20372-8eb8-7641-b4dd-ef2f73458eda`. SHA-256
  `b4774ed04b5eeede76e6a91c8be05a98a3cf799a6af0b7f31b072cca3da969e9`.
  Совпадают вводная часть и все 56 заголовков. Изменены только 14
  перечисленных блоков. MCQ-чат взял снимок и JSON из `b28a1c807` в ходе
  `01a20376-968d-7f73-9d6f-1048a501d319`. Ожидаемый полный файл —
  `work/java-concurrency-audit-restoration-full-regeneration/java-concurrency-interview.json`.
  В завершённом ходе автор передал единый повторный возврат по этому снимку.
  Для Q36/Q38/Q42/Q45/Q48/Q52/Q56 нужно уточнить видимое условие или
  сохранить исходное понятие. В Q2, Q5–Q8, Q10–Q11, Q13–Q14, Q16,
  Q18–Q23, Q25–Q26, Q28, Q30–Q31, Q33–Q35, Q37, Q40, Q44, Q46–Q47,
  Q49–Q50 и Q53–Q55 нужно убрать раскрытие ключа и исправить указанные
  примеры. Полный JSON не записан. Координатор отправил весь финальный
  возврат автору шпаргалок одним пакетом на базе `b4774ed0…`. Ожидаемый
  Markdown — `work/java-concurrency-second-theory-return/java-concurrency-interview.md`.
  Чат шпаргалок подтвердил начало работы в ходе
  `01a2037e-b78a-7f22-beb4-697277a60c18`.
  Полный Markdown передан в том же ходе. SHA-256
  `30ec993003d93402e6d4dd3f35a58694ac5cb23e1da2574cee6c6defc365d170`.
  Сверены SHA исходника и результата, вводная и 56 заголовков. MCQ-чат
  подтвердил полную генерацию по этому снимку в ходе
  `01a2038a-1288-77f1-838f-31ae93003c2d`. В том же ходе автор передал полный файл
  `work/java-concurrency-restoration-second-theory-full-regeneration/java-concurrency-interview.json`.
  Пара Markdown и JSON в коммите `0e66efe1b`.
- **Java Core** — в ходе `01a20363-e7f7-7920-96b8-790ccd4640b1` MCQ-чат
  вернул Markdown `a04fd147…`. Полный JSON не записан. Координатор передал
  автору шпаргалок собранные замечания из этого хода: убрать
  подсказки до выбора, исправить примеры и уточнить версии Java, правила
  конструкторов, загрузчиков, доступа через reflection и синтаксис text blocks.
  Автор шпаргалок передал полный снимок
  `work/java-core-audit-theory-return/java-core-interview.md` в ходе
  `01a20379-28f9-7132-b02c-8f0b11ea4838`. SHA-256
  `cfea3be7fd7b0ad7793c6176cfaf2bbe34979c38b9f5925c31cb49f183320f79`.
  Координатор подтвердил сохранность вводной части и всех 39 заголовков.
  MCQ-чат взял полную генерацию по этому снимку и исходному JSON из
  `b28a1c807` в ходе `01a2037b-65f6-7ff3-a8af-8780e98993db`.
  В том же ходе автор передал полный файл
  `work/java-core-audit-restoration-full-regeneration/java-core-interview.json`.
  Пара Markdown и JSON в коммите `0e66efe1b`.
- **Java Exceptions** — собран возврат Markdown `deb502ad…` в ходе
  `01a20368-7d70-72c0-91a6-d7d625b679e9`. Полный JSON пока не записан.
  Автору шпаргалок переданы замечания к Q1, Q5, Q12–Q13, Q22–Q23, Q27,
  Q32–Q33, Q35–Q40 и Q42. Пакет взят в ходе
  `01a20386-d506-7fc2-b62b-5d0b3a344116`. В том же ходе передан полный снимок
  `work/java-exceptions-audit-theory-return/java-exceptions-interview.md`.
  SHA-256 `d0d08962e68993ee2bedfd9874c1b706de3d9b0f31be16cb4e9e31d9f055ca22`.
  Сверены вводная, 42 заголовка и охват изменений в 16 назначенных блоках.
  Снимок готов для полной генерации. SHA-256 JSON из `b28a1c807` —
  `e549449bd313a0afaedd1b3f580f937e7a86b6f91f904854c7df6a690cc57b67`.
  MCQ-чат подтвердил начало полной генерации Q1–Q42 в ходе
  `01a203a9-0f71-7921-af63-7d7ac31c69be` и вернул Q16 и Q18. Видимые
  примеры раскрывают допустимое переопределение и результат вывода типов.
  Чат шпаргалок подтвердил правку в ходе `01a203ac-733d-78d1-9325-de3f5eab56c9`.
  В том же ходе передан полный Markdown —
  `work/java-exceptions-visible-code-theory-return/java-exceptions-interview.md`.
  SHA-256 `2f4a4a9da7e6584b6f2abfa08f2ea4419fdd7681b267d4a2f47ba9c7a9c23ce0`.
  Сверены вводная, 42 заголовка и видимые примеры. Изменены только Q16 и Q18.
  MCQ-чат подтвердил начало полной генерации по этому снимку в ходе
  `01a203af-9134-7020-b958-bcf907448ab0`. Автор передал полный результат —
  `work/java-exceptions-visible-code-full-regeneration/java-exceptions-interview.json`.
  Пара Markdown и JSON в коммите `0e66efe1b`.
- **Java Generics** — MCQ-чат передал возврат по Markdown `7df2fae8…`
  в ходе `01a2036b-de6e-7ba3-8e8e-afc069ef4eaf`.
  Собранные замечания переданы автору шпаргалок. Он подтвердил начало
  работы в ходе `01a2038d-83d1-70e0-b783-b6369e3aa3d1`. Ожидаемый снимок —
  `work/java-generics-audit-theory-return/java-generics-interview.md`.
  Исправить примеры reflection и `Collections.copy`, уточнить
  `instanceof` и `Comparable`, перенести раскрывающие ключ комментарии и
  схемы в разбор. Затем назначить полную генерацию по Markdown Q1–Q40
  и исходному JSON из `b28a1c807`.
  Автор передал полный Markdown в ходе `01a2038d-83d1-70e0-b783-b6369e3aa3d1`.
  SHA-256 `401be5b081da742c644fa3dfceb3ab8a78857e49b431b3a7423243bae5657b89`.
  Совпадают вводная и все 40 заголовков. Изменены тела Q2–Q4, Q6–Q22,
  Q24–Q29 и Q31–Q40. Пара готова для очереди полной генерации. SHA-256
  исходного JSON `1538805449edb9d647ec060f9cfe6aaedfb764c6685b45657622772b139c20ff`.
  Автор передал полный JSON в завершённом ходе
  `01a203c5-789a-7c02-9fe5-e2db1e25a933`. Файл —
  `work/java-generics-audit-restoration-full-regeneration/java-generics-interview.json`.
  Пара Markdown и JSON в коммите `0e66efe1b`.
- **Resilience4j** — MCQ-чат вернул Markdown `46a866b0…` в ходе
  `01a2036e-9552-7c02-abcd-8b5dddb322df`.
  Единый возврат передан автору шпаргалок после Java Generics. Автор
  подтвердил входной SHA и начал работу в ходе `01a20390-d455-7172-af97-f2e6feec812d`.
  Ожидаемый снимок — `work/resilience4j-audit-theory-return/resilience4j-interview.md`.
  Автор передал полный Markdown в том же ходе. SHA-256
  `5f95844602753139fe0e3f43779841718e39c3f59e1e89a0936722a91f78c7b0`.
  Совпадают вводная и все 20 заголовков. Изменены тела Q3–Q7, Q9–Q11
  и Q13–Q20. Снимок готов для очереди полной генерации. SHA-256 исходного
  JSON из `b28a1c807` — `6867e6e367630ec54e116af91b6eef060e3314c7a6b5159db46f59a7f9fdd570`.
  Координатор подтвердил Resilience4j следующим пакетом MCQ после Java Generics.
  Автор MCQ берёт весь Q1–Q20 одной задачей по закреплённым входам,
  `PROMPT_PLAN_INTERVIEW.md` и `docs/mcq-quality/workflow.md`. Полный результат —
  `work/resilience4j-audit-restoration-full-regeneration/resilience4j-interview.json`.
  План и общий checkpoint пишет координатор, автор MCQ пишет только итоговый JSON.
  Автор передал полный JSON в завершённом ходе `01a203d5-782b-7302-88b1-03c07216b0fa`.
  Пара Markdown и JSON в коммите `6b8abb290`.
- **Kafka** — автор шпаргалок передал накопительный Markdown в ходе
  `01a20394-3de2-7273-a1b4-74d98b944e26`. Снимок —
  `work/kafka-async-offset-theory-return/kafka-interview.md`, SHA-256
  `27bfbca61a51ec71d02e99b36ac88a838d10f2160b88201dddd8dd9e956219a1`.
  Относительно предыдущего снимка добавлен только сценарий Q16 о фиксации
  offsets при асинхронном завершении обработки. Закрепить эту версию для
  полной генерации Kafka в партии восстановления. SHA-256 исходного JSON
  из `b28a1c807` — `23b30cb31b7fd2d796d1b52dce55c0461e240161e5310a293649e7fd682a2990`.
  MCQ-чат получил и подтвердил назначение в ходе `01a203dc-65e0-7c23-9461-02bbeddaae8a`.
  Он единственный писатель `work/kafka-async-offset-restoration-full-regeneration/kafka-interview.json`.
  План и общий checkpoint пишет координатор.
- Автор MCQ передал полный файл Kafka в ходе
  `01a203dc-65e0-7c23-9461-02bbeddaae8a`. Пара Markdown и JSON в коммите
  `add8afaab`.
- **MySQL Q5** — полный накопительный Markdown из завершённого хода
  `01a203ec-14c4-7901-97a5-e8809e4a02c1`.
  `work/mysql-charset-byte-diagnostics-theory-return/mysql-interview.md`
  (SHA-256 `d320f61026e7e897b2e927923e5fedca4bcb0f1aa92a6357dce38c05d4761adc`).
  По сравнению со снимком `409aa04a…` добавлен только пример Q5 с `HEX`
  и `CONVERT`. Он различает перекодирование из объявленной кодировки и
  чтение сохранённых байтов как UTF-8. Пример сверён по документации MySQL 8.4.
- **MySQL Q6** — следующий полный накопительный Markdown из завершённого
  хода `01a203ed-59bc-7770-9f96-efc732694465`.
  `work/mysql-commit-retry-theory-return/mysql-interview.md`
  (SHA-256 `b8ff97d100c0bbaac96eabfc606d35e979d07caa739b6988ec87a27e03ba324f`).
  Относительно снимка Q5 добавлен только сценарий повтора после потери ответа
  на `COMMIT`. Уникальный ключ связывает конкурирующие попытки, а результат
  сохраняется в одной транзакции InnoDB с записью заказа. Этот снимок включает Q5.
- **MySQL Q4** — полный накопительный Markdown из завершённого хода
  `01a203ee-1e80-7bc2-96d6-545f7bb2b255`.
  `work/mysql-repeatable-read-own-write-theory-return/mysql-interview.md`
  (SHA-256 `8b9d9d47ad1f5985b65756f15d1a55f0bc6223557a47d74b67bf1b1ae2c4791b`).
  Снимок включает Q5 и Q6. Изменилось только тело Q4: сценарий с двумя
  сессиями показывает, как при `REPEATABLE READ` обычный `SELECT` видит
  собственное изменение строки, которую исходный снимок ещё не содержал.
- **MySQL Q3** — полный накопительный Markdown из завершённого хода
  `01a203ee-db2b-73a2-ad25-a97077dc9f18`.
  `work/mysql-optimizer-trace-theory-return/mysql-interview.md`
  (SHA-256 `7bff7f2ba7236ab09e5aad662ef49a6531561c1fdc97439411849becb69ef00e`).
  Снимок включает Q4–Q6. Изменилось только тело Q3. Пример с
  `OPTIMIZER_TRACE` показывает, как выяснить причину отказа от индекса и
  распознать усечение трассы или недостаток прав. SQL на сервере не запускался.
- **PostgreSQL** — чат шпаргалок передал полный накопительный Markdown
  `work/postgresql-partial-generic-plan-theory-return/postgresql-interview.md`
  в ходе `01a20395-beb8-7603-9594-5ea91b81f301`. SHA-256
  `95067b52e88c30735245e3c9060af23ac2174fffc15b3586ad5c29524b5598c6`.
  Сверены вводная и 55 заголовков. Относительно снимка с правкой блокировок
  при проверке внешних ключей изменён только Q16 о частичном индексе
  в custom и generic plan. Последующие накопительные снимки уточнили Q17
  (индекс по выражению и HOT), Q18–Q19 (`INCLUDE` и повторное построение
  индекса после сбоя) и Q21 (`EXPLAIN` для памяти планировщика и сериализации).
  В Q24 дополнительно уточнены `effective_cache_size`, `random_page_cost`
  и настройки tablespace. Предыдущий полный вход для генерации —
  `work/postgresql-wait-transaction-theory-return/postgresql-interview.md`,
  передан в ходе `01a203a7-ebf9-7553-9fb5-eb126786656c`. SHA-256
  `918e2585b7f7558e56bdd3c8d76b8b173166410070b97eb0bacbc5ce69a91ef3`.
  Следующий накопительный снимок —
  `work/postgresql-slot-positions-theory-return/postgresql-interview.md`
  из завершённого хода `01a203ea-49b5-70f0-9bec-50e32c923dd0`.
  SHA-256 `6eab96c8280f0ab76051ee71a9418801848b882591818a2e0733d60efb709fc0`.
  Сверены все 55 заголовков. Изменилось только тело Q46. Оно различает
  позицию удержания WAL и подтверждение логического потребителя и показывает,
  как в CDC возникают пропуски после раннего подтверждения и повторы после сбоя.
  Предыдущий подтверждённый вход —
  `work/postgresql-standby-decoding-theory-return/postgresql-interview.md`
  из завершённого хода `01a203eb-6141-77e3-99fa-a44566cd6099`.
  SHA-256 `34fab304b13316265faad48c7834f28e45b5b4f3208d6890bfe2ae0c1bad8d75`.
  Сверены все 55 заголовков. Изменились только тела Q3 и Q46. Они объясняют
  снижение фактического уровня WAL в PostgreSQL 19 и инвалидирование
  логического слота CDC на standby при сохранении физической репликации.
  Следующий накопительный вход —
  `work/postgresql-publication-row-filter-theory-return/postgresql-interview.md`
  из завершённого хода `01a203f0-e600-7981-9e83-b26cc44d5ef9`.
  SHA-256 `35355042b7bb361c6a0f6000e286eb9b4a95c9e5ee40642d08e6bfa6e4ef847f`.
  Сверены все 55 заголовков. Относительно `34fab304…` изменилось только тело Q45.
  Пример разбирает фильтр строк публикации, превращение `UPDATE` в `INSERT`
  или `DELETE`, `REPLICA IDENTITY`, начальное копирование и `TRUNCATE`.
  SQL на сервере не запускался.
  Следующий накопительный вход —
  `work/postgresql-publication-partition-root-theory-return/postgresql-interview.md`
  из завершённого хода `01a203f2-640f-7f73-86b5-9deef8c07cc3`.
  SHA-256 `a8a314ef03eb3c55c62fbaeb5c772ac844e9ddc263d388d805a0ce4766f04896`.
  Относительно `35355042…` уточнено только тело Q45. Добавлен сценарий
  репликации через `publish_via_partition_root`, включая выбор схемы,
  фильтра, списка столбцов и поведение при `ATTACH PARTITION` и `TRUNCATE`.
  SQL на сервере не запускался.
  Актуальный полный вход —
  `work/postgresql-subscription-conflict-theory-return/postgresql-interview.md`
  из завершённого хода `01a203f3-5d6a-7d61-9ba6-bde7b047d2d7`.
  SHA-256 `60f57c588f0b39a027daab2795dfdb9278b077da79d1472c2857dc83262159b6`.
  Относительно `a8a314ef…` добавлен только сценарий Q45 про конфликт
  `insert_exists` и пропуск всей транзакции через `ALTER SUBSCRIPTION … SKIP`.
  MCQ-чат завершил полный пакет Q1–Q55 в ходе
  `01a203fc-015d-73b3-bc3d-8df865bc56a6`. Источник JSON — файл темы из
  `b28a1c807`. Автор передал один полный JSON
  `work/postgresql-subscription-conflict-full-regeneration/postgresql-interview.json`.
  Пара Markdown и JSON в коммите `add8afaab`.
  После снимка с правкой Q50 изменились тела Q13, Q15, Q32, Q44 и Q45. Q13 поясняет
  очистку накопленного списка GIN после отключения `fastupdate`. Q15 добавляет
  пересчёт сводки отдельного диапазона BRIN после исправления выброса.
  Q32 уточняет ограничения `REPACK` для невалидного индекса и секционированной
  таблицы. Q45 разграничивает поток изменений публикации и начальное копирование.
  В Q44 раскрыты вызов `WAIT FOR LSN` до первого чтения и последствия таймаута
  в явной транзакции.
  В Q47 добавлен пример фразового поиска на стыке полей
  объединённого `tsvector` и способ ограничить поиск одним полем.
  Q49 уточняет границы пулов PgBouncer, `max_db_connections` и
  отключение клиента по `query_wait_timeout`.
  Q50 добавляет сценарий изменения схемы при протокольных prepared
  statements, явную проекцию и обновление соединений через `RECONNECT`.
  Сверены вводная и все 55 заголовков. После снимка с `jsonb_set` в Q48
  также изменились Q52–Q55. Q52 уточняет повтор операции после отката
  к точке сохранения, Q53 — оценку памяти параллельных узлов, Q54 —
  порог журналирования отдельных этапов `Parse`, `Bind` и `Execute`.
  В Q55 уточнены права для установки `pg_trgm` и владение объектами расширения.
  Предыдущие снимки добавили изменения Q41
  (оконные рамки при пропусках дат), Q43 (обход графа через `UNION` и `CYCLE`)
  и Q44 (отмена ожидания синхронной реплики после локальной фиксации).
  База `4dae2a862…` уже включает сценарий `ATTACH PARTITION` в Q35 и
  правки Q25–Q28, Q30 и Q32. SHA-256 JSON из `b28a1c807` —
  `df0cd25b5735079146d9e14616463ba1f8956cfe8ba2f09a677bfb818b639028`.
- **RabbitMQ** — чат шпаргалок передал полный Markdown
  `work/rabbitmq-dlx-confirms-theory-return/rabbitmq-interview.md`
  в ходе `01a20395-1750-7032-88c6-9100fabd1192`. SHA-256
  `1908a3b5e77bdc07ed5a6d37d7a2fbc33be8213a1ce299436b25f1b40e7e8f0f`.
  Сверены вводная и 42 заголовка. Изменён только Q24 о доставке
  в DLQ и подтверждениях. Снимок готов к полной генерации. SHA-256
  JSON из `b28a1c807` — `fb63e6f00d650503589343698db7cd46f3d1cf50ba263e53822b6e9d0bc7fd2f`.
- **Остальные темы с потерей секций.** Spring Events, Spring Kafka,
  Spring Modulith, Spring Security, Spring
  Session, Spring State Machine, Spring Transaction, Spring Validation и
  Spring Vault. Координатор назначает темы по одной и отмечает путь готового
  полного файла.
  Пока Java Exceptions ждёт Markdown, MCQ-чат взял Spring Events в ходе
  `01a203ac-7bd4-72a1-bc54-b6922348f361`.
  SHA-256 полного Markdown `76f09d720f00a564243f3f7eb9135a3e5a9ccebb6920482bf849dd6b87639455`.
  SHA-256 исходного JSON из `b28a1c807` —
  `22285aebdf4b352361b2d96119f5df4a2a61cdcd9fe77a101b62b9b75b469f10`.
  Ожидаемый полный файл — `work/spring-events-audit-restoration-full-regeneration/spring-events-interview.json`.
  Автор сообщил, что итоговый JSON не записан. Собран возврат теории по Q5, Q6,
  Q8, Q9 и Q10. Нужно перенести подсказки из видимых примеров в разбор и
  уточнить в Q9 отличие аргумента слушателя от обёртки события.
  Чат шпаргалок передал полный исправленный Markdown
  в ходе `01a203b0-7c4d-7271-98b3-c195cfff40c0` —
  `work/spring-events-visible-code-theory-return/spring-events-interview.md`.
  SHA-256 `8d9fae3f49ebebc40bf4e4f3f8713172192f2e93aa9fab07ab199f6fcf41cf9f`.
  Сверены вводная, 16 заголовков и видимые примеры. Изменены только тела
  Q5, Q6 и Q8–Q10. Следующий накопительный снимок —
  `work/spring-events-publication-recovery-theory-return/spring-events-interview.md`,
  переданный в ходе `01a203b2-9857-75d0-b10a-98c3da5938c7`.
  SHA-256 `516ce9586dd737fde0f8d086b08a04480bfbffeb5ddb72763ec9b7bc225eed1f`.
  По сравнению с предыдущим снимком изменено только тело Q16. Добавлены
  условия восстановления публикации после сбоя и пример обработки дубля.
  В ходе `01a203b5-d514-7832-91aa-e766c65303bf` автор добавил к разбору
  Q6 реактивную публикацию в транзакции:
  `work/spring-events-reactive-transaction-theory-return/spring-events-interview.md`,
  SHA-256 `2b1558a67e2a5354196c55440b293fd5aa1f2f5d4f0b3747e1b765a62220d50b`.
  Следующий накопительный снимок —
  `work/spring-events-transaction-phase-theory-return/spring-events-interview.md`
  из хода `01a203b6-ac77-7363-b800-4609c3c58231`,
  SHA-256 `d88eb3e9c427eb6185a5dde656425bb29da0d643063ecbd5dc2e9f9101904be7`.
  В Q7 добавлен сценарий `STATUS_UNKNOWN` и уточнён `fallbackExecution`.
  Хеши снимков совпали с отчётами, изменения после Q16 ограничены Q6 и Q7.
  Для полной генерации после Java Exceptions закреплён снимок из хода
  `01a203b7-b006-73b1-bc4e-9fa1f82266f3` —
  `work/spring-events-domain-events-theory-return/spring-events-interview.md`.
  SHA-256 `53453d72d4edab806c83bb6c3c2999b4c473522b7adfdbf598132ea30d4deee0`.
  По сравнению с предыдущим снимком изменилось только тело Q11. Уточнены
  публикация через `@DomainEvents` для `delete(order)` и `deleteById(id)`
  и её связь с commit внешней транзакции.
  Более свежий накопительный снимок из завершённого хода
  `01a203ba-0d21-7180-8483-d3a4e86e1c37` —
  `work/spring-events-startup-lifecycle-theory-return/spring-events-interview.md`.
  SHA-256 `8045d1258028cc77d0be3d04ba177ff2157df4fb847a0a28e88167b44772da8e`.
  Он включает промежуточную редакцию границ доставки событий. Последний ход
  уточнил синхронную доставку, границу транзакции и порядок событий запуска.
  Следующий накопительный вход для полной генерации —
  `work/spring-events-test-commit-theory-return/spring-events-interview.md`,
  переданный в ходе `01a203bc-8beb-7593-bdca-e499a7cddd0f`.
  SHA-256 `8a06027b2562a6dce84689112d78ac1d111439dede2f4d614293ba311bc36893`.
  После снимка startup-lifecycle уточнены Q3 и Q13 и добавлено различие
  между проверкой публикации и вызовом `AFTER_COMMIT` в Q14.
  MCQ-чат взял полную генерацию по этому входу в ходе
  `01a203bb-d056-7ff3-a33c-57fed749d5ca` после передачи Java Exceptions.
  В этом завершённом ходе автор передал полный JSON
  `work/spring-events-audit-restoration-full-regeneration/spring-events-interview.json`.
  Пара Markdown и JSON для этого снимка в коммите `6b8abb290`.
- **Spring Events Q2–Q8/Q10** — завершённые ходы автора Markdown после
  передачи предыдущего JSON. В ходе `01a20401-d109-7492-afeb-98cf31da641d`
  уточнено поведение `@Async` при переполнении пула в Q5
  (`work/spring-events-executor-saturation-theory-return/spring-events-interview.md`,
  SHA-256 `f241e67cd7fce0a61f8a6d4aa1485c1e3745bbec175997429f77401282ea88f1`).
  В ходе `01a20402-99df-7c42-80be-878778ac55f9` в Q6 добавлен сценарий
  исчерпания общего JDBC-пула при `AFTER_COMMIT` и `REQUIRES_NEW`
  (`work/spring-events-after-commit-pool-theory-return/spring-events-interview.md`,
  SHA-256 `94eb72fd76f829e783a51a27b82fd2baddc72d36522b0ca77294b2967046dfe1`).
  В ходе `01a20403-31f5-7ff0-a85a-f281431c49bc` уточнена вложенная публикация
  результата синхронного слушателя и явная публикация при `@Async` в Q10
  (`work/spring-events-result-dispatch-theory-return/spring-events-interview.md`,
  SHA-256 `aed84deae622fbae3d06e2c32043e6c4d5ce94b817c01099adb941e4fd02fdc1`).
  В ходе `01a20403-fa63-7ac2-8f6b-226bdf307d7c` в Q4 уточнено, что
  `ServletRequestHandledEvent` публикуется на каждый проход через
  `processRequest()`, включая проход с ошибкой. Один асинхронный HTTP-запрос
  может дать несколько событий. Накопительный снимок
  `work/spring-events-servlet-dispatch-theory-return/spring-events-interview.md`,
  SHA-256 `4f82038e053254bac14a3ec92b0a3dd21f80d0653f05a82c52fa4f2645c64842`.
  В ходе `01a20405-ca17-7a01-b1d8-17d9e5cf5afc` в Q8 добавлен сценарий
  сбоя первого синхронного слушателя и объяснено влияние `ErrorHandler`
  на вызов остальных слушателей. Накопительный снимок
  `work/spring-events-listener-failure-theory-return/spring-events-interview.md`,
  SHA-256 `b2e1f593a55fcf540d5d91dad3c405a5161d21e577910cc7186c70b0cc5d93bd`.
  В ходе `01a20406-af75-78a0-8a69-3996573f6926` в Q7 добавлен сценарий
  ошибки синхронного слушателя JDBC-транзакции. Разобраны откат при сбое
  в `BEFORE_COMMIT` и журналирование сбоя в `AFTER_COMMIT`, когда заказ
  уже сохранён. Накопительный снимок
  `work/spring-events-phase-failure-theory-return/spring-events-interview.md`,
  SHA-256 `ae499202374e0c3bb410ee9bd0d9ef044d3f54682fd5dc2fd1d817f1fbe2c0b7`.
  В ходе `01a20407-7f01-7f81-96d8-c52b33bed6f0` в Q2 согласованы тип
  события, аргумент конструктора и чтение идентификатора слушателем.
  Уточнена регистрация `@EventListener` на управляемом бине. Накопительный
  снимок `work/spring-events-basic-publication-theory-return/spring-events-interview.md`,
  SHA-256 `83c6af6f0ed9e7daf3425229d418044ea7f21d608d7f3bb594992d40b2b4088e`.
  В ходе `01a20409-006e-71d2-bcdb-30c94d7eb729` в Q3 добавлен пример
  маршрутизации события по параметру типа и объяснено поведение при его
  стирании в Spring Framework 6.2.19. Накопительный снимок
  `work/spring-events-generic-dispatch-theory-return/spring-events-interview.md`,
  SHA-256 `0520eac809e07b91b53f9e0afdda4dea3154fc7a51cc58e7cb0e0ec6a52b9bd4`.
  Сверены хеши, все 16 заголовков и границы правок. Для обновления MCQ
  передавать последнюю накопительную версию после завершения текущего
  Markdown-пакета по этой теме.
- **FastAPI Q7** — готов полный вход для генерации из завершённого хода
  `01a203be-2d81-72c1-82dd-7952827e2e11`.
  `work/fastapi-override-lifespan-visible-theory-return/fastapi-interview.md`
  (SHA-256 `7e0a53c1c4681efc625ec3cc661a4828a3a63c1da6913be42439202f488a4b77`).
  Видимый пример задаёт маршрут, исходную зависимость и состояние из lifespan.
  SHA-256 исходного JSON из `b28a1c807` —
  `923eb6d278707fca2187453e66d766263f4dd87a1664f7a0606c1cb6afa60fa0`.
- **Python Core Q2** — готов полный вход из завершённого хода
  `01a203be-c18c-77f3-ab63-d31d09680d7e`.
  `work/python-core-q2-q10-q12-theory-return/python-interview.md`
  (SHA-256 `7988d248df0ceccdbba8371cd6e5abd2f3df93b71168e512d3c395fd00560ba1`).
  Видимый пример задаёт условия сравнения копий и семантику `copy.replace`
  для Python 3.13+. SHA-256 исходного JSON из `b28a1c807` —
  `025be2e2e73e1396f043f666fd35390a4f058216369a6c89fc7464a8b6768f51`.
- **Spring Data JPA Q15** — готов полный вход из завершённого хода
  `01a203bd-74b7-7ba1-b4cc-2028ddb133c7`.
  `work/spring-data-jpa-fetch-pagination-theory-return/spring-data-jpa-interview.md`
  (SHA-256 `1b2613d7f00ae0d918a6020d7f05cad7bb991a24e1242882a1eab422f2162ab1`).
  Видимый пример сравнивает пагинацию с fetch join коллекции в двух версиях
  Hibernate и спрашивает о роли `countQuery`. SHA-256 исходного JSON из
  `b28a1c807` — `ac0307531bcf3356497c350d1c3b79d8e08d5dc14a7a257c3363df3b81445f03`.
- **Java CompletableFuture** — получен накопительный Markdown-снимок из
  завершённого хода `01a203c1-7c5c-7e40-9dd0-bc75afe6f195`.
  `work/java-completable-future-executor-version-theory-return/java-completable-future-interview.md`
  (SHA-256 `03d6d3ee227db3855cc004fda987a44abe9dd85dcb13463e57438e3b30cce0b3`).
  Снимок включает видимые сценарии Q6 про ошибку первого завершившегося
  запроса и Q11 про общий future с отдельным дедлайном API. Относительно
  предыдущего снимка изменены только Q2 и Q5: различия исполнителей между
  версиями JDK и пример с `Runnable::run`. Следующий накопительный снимок
  `work/java-completable-future-wait-contract-theory-return/java-completable-future-interview.md`
  (SHA-256 `8c7d375e762b4d9249a98139219a358f21430c545fda2fb6ab63bb55976ad469`,
  ход `01a203c2-dfac-75a3-9899-7f7911c0c571`) добавляет изменения Q9 и Q10.
  В Q9 видимый пример показывает вызов с проверяемым исключением внутри
  `supplyAsync`. В Q10 сравниваются таймаут ожидания и последующее получение
  результата того же future. Решение о выборе блока Q3/Q8 в тренажёре
  остаётся отдельной задачей.
  Следующий полный снимок из завершённого хода
  `01a203c8-5dac-74d1-9fb4-c8e549f03a1c` включает Q7 и Q12–Q14:
  `work/java-completable-future-cancellation-scenario-theory-return/java-completable-future-interview.md`
  (SHA-256 `3402df72f190987b5475c2171a595d580466167ca16c6908aeb7b0bb513104f9`).
  В Q14 два независимых запуска отделяют отмену результата от запроса
  на прерывание рабочей задачи. Автор подтвердил поведение по локальным
  исходникам OpenJDK. Онлайн-сверка Javadoc Java 26 осталась открытой.
  Актуальный полный снимок из завершённого хода
  `01a203ca-0b0b-7083-8985-1a4ed2e62aa4`:
  `work/java-completable-future-dashboard-fallback-theory-return/java-completable-future-interview.md`
  (SHA-256 `9d926cebd8c433b9516c5afafda3c9e22491f3d76a05da4fd409316e7d2912da`).
  В Q7 сохранены три ветки одной панели: профиль и счёт обязательны, при
  сбое загрузки заказов допустим пустой список. Остальная теория совпадает
  с предыдущим снимком.
- **Python Typing Q4** — полный вход из завершённого хода
  `01a203ca-c1e8-7d92-ad09-e0c4ac8629ee`.
  `work/python-typing-openness-scenario-theory-return/python-typing-interview.md`
  (SHA-256 `871bac1d8235b420d43d12c3f5fd64aa8ee1cb676b1c7ddf85d881982936b2ce`).
  Сценарий различает открытый `TypedDict`, `closed=True`, `extra_items` и
  поведение словаря при выполнении. Исходный JSON — из `b28a1c807`.
- **JDBC и пулы соединений** — полный накопительный Markdown из хода
  `01a203f5-9b4e-74b1-b928-8557d3750421`.
  `work/jdbc-pool-metric-snapshot-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `c1a244e692c80eaed41720841eed5124d34b7fd292d47f6ddd4117017ac422c8`).
  Q6 объясняет независимость показаний JMX и диагностику таймаутов пула.
  Сохранён сценарий Q4 о generated keys и подтверждении commit из хода
  `01a203f4-a3e5-7770-b240-73210e21bcdd` (снимок SHA-256 `afd6ad9992703ca02a90f8aeb22ba31d584dc18131a277b7326d3a3555b0391c`).
  В завершённом ходе `01a203fd-c0a1-7801-ad93-3f1d294ce872` автор передал
  новый накопительный снимок `work/jdbc-cursor-idle-timeout-theory-return/jdbc-connection-pool-interview.md`.
  SHA-256 `59b3762000204b8b6b51e06e5faea4f14bb887b8779892adf0f51e178c007e3b`.
  В Q5 добавлен сценарий завершения сессии PostgreSQL между порциями
  курсорного экспорта и объяснено, почему повтор с начала нельзя дописывать
  в открытый CSV-ответ. Сверены хеши Markdown, все шесть заголовков и
  изменение только Q5.
  Следующий накопительный снимок из завершённого хода
  `01a2040a-0b22-71b2-b2a8-db513b3bf1c2` —
  `work/jdbc-connection-init-sql-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `d8f45cf4d703fd3f09873c00634e4bb1b1b2e9a0040b58edbfbe15fb1124fe65`).
  Относительно предыдущего снимка добавлен только абзац в Q1 о выполнении
  `connectionInitSql` при создании физического соединения и о границах
  `SET LOCAL` при повторной выдаче сессии. Остальные Q сохранены.
  Следующий накопительный снимок из завершённого хода
  `01a2040b-e7f0-78a0-84f7-510da1351b99` —
  `work/jdbc-resource-cleanup-error-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `4ea01d815d3ec24b9ecae439dee6b23cffd4742d279d3b716bd5eba5014267d0`).
  В Q2 добавлен сценарий одновременной ошибки чтения и закрытия JDBC-ресурса.
  Разъяснены подавленные исключения и отдельная цепочка `getNextException()`.
  Остальной Markdown совпадает с предыдущим снимком.
  Следующий накопительный снимок из завершённого хода
  `01a2040c-ca69-7463-ad0c-a67b08a17de2` —
  `work/jdbc-savepoint-recovery-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `11cc5ed21c85aa1dd16fabe31e78fb8395eb3f2c39c945056255fb29b741d2fb`).
  В Q3 добавлен сценарий отката к `Savepoint` после нарушения уникальности
  и продолжения импорта в общей транзакции. Изменилось только тело Q3.
  Следующий накопительный снимок из завершённого хода
  `01a2040d-67c7-7ca0-89da-6e176d13a7d2` —
  `work/jdbc-multirow-upsert-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `20579e3edee705dbad6dd8e5131ff15aabe35df4699eb6d03e2be562607c8efb`).
  В Q4 добавлен пример ошибки кардинальности при двух строках с одним ключом
  в многорядном `INSERT … ON CONFLICT DO UPDATE`. Остальной Markdown совпадает
  с предыдущим снимком.
  Актуальный полный вход из завершённого хода
  `01a2040e-0fb4-76d2-97bd-286a83280748` —
  `work/jdbc-mysql-streaming-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `77bcba586166cd652916d82200920e993112a4f951fe129bab570c73fbf319d9`).
  В Q5 добавлены режимы потокового чтения Connector/J и пример ошибки
  второго запроса на том же соединении при открытом построчном результате.
  Остальной Markdown совпадает с предыдущим снимком.
  Для полной генерации закреплён снимок из завершённого хода
  `01a20410-ec81-71e0-85d6-718e8312a0ad`:
  `work/jdbc-read-only-routing-theory-return/jdbc-connection-pool-interview.md`
  (SHA-256 `c0f2eb1534bb86d781826d9b8d335e52eae4636ef164ad1afd22cacbdc6cc487`).
  Сохранены все шесть заголовков и сценарий Connector/J. Добавлены `unwrap`,
  границы `readOnly` и обработка сломанного соединения пулом.
  Автор MCQ подтвердил назначение в ходе `01a2040f-9e09-7c11-9060-fd2fe1408979`.
  В том же завершённом ходе он передал один полный файл
  `work/jdbc-cumulative-streaming-full-regeneration/jdbc-connection-pool-interview.json`.
  Пара Markdown и JSON в коммите `46d97e3ac`.
- **Python Database Q4** — полный накопительный Markdown из хода
  `01a203f6-3152-76e1-a09e-281811450bc5`.
  `work/python-database-executescript-theory-return/python-database-interview.md`
  (SHA-256 `962153c58497e4605170319c238def42abc00fc7951a30a3b13d8a3f49d48dca`).
  Относительно `f459b1c3…` добавлен только сценарий `executescript()` в Q4.
  Автор воспроизвёл различие границ отката на Python 3.14.4.
  В завершённом ходе `01a203f7-be4d-7430-aeaf-1b831ccdb736` автор передал
  более новый накопительный снимок
  `work/python-database-returning-theory-return/python-database-interview.md`
  (SHA-256 `5b84cd63f5ef0d1abe1def93a689008597fb77bcca64b42d7d34f48fb3c67722`).
  Он сохраняет сценарий `executescript()` и добавляет в Q4 пример
  `execute()` с `INSERT … RETURNING`, где идентификаторы сопоставляются по
  уникальному ключу. В очередь полной генерации идёт новый снимок.
- **Vector Databases Q1–Q28** — накопительный Markdown из завершённого
  хода `01a203e7-dd94-7581-a5c0-4fd1568de952`.
  `work/vector-databases-ann-theory-return/vector-databases-interview.md`
  (SHA-256 `4492cae1b7dbfa5e22822ea61056133148a956ab0d461df1873c11f829da66f3`).
  MCQ-чат подтвердил назначение в ходе `01a203ed-8163-7293-92d8-494a8ae19839`.
  SHA-256 исходного JSON темы из `b28a1c807` —
  `21535023af360f6ac1e4111c6fd8683ab7c8c150b7dc0d2a268076862959213f`.
  Единственный писатель результата — автор MCQ. Путь полного файла —
  `work/vector-databases-ann-restoration-full-regeneration/vector-databases-interview.json`.
  Автор передал полный JSON в финальном сообщении того же хода. Пара Markdown
  и JSON для этого снимка в коммите `add8afaab`.
  По сравнению со снимком `82756c91…` изменилось только тело Q6. Оно
  различает пропуск кандидата при обходе HNSW и изменение ранжирования
  из-за PQ и показывает, как считать `recall@3` относительно точного поиска.
  В Q10 добавлен числовой пример расчёта приближённого квадрата расстояния
  L2 по PQ-коду. Снимок из хода `01a203e6-8bf9-72f1-a747-58d241a1f663`
  (SHA-256 `1fa83d978083f3ba2b8ee5e0616075740909bbcb8ce7b98d12ba75493409c88b`)
  уточнил Q5 и Q9: выбор метрики и класса операторов HNSW, выбор между HNSW
  и IVFFlat при ограниченном окне пересборки. Q17 показывает запись и поиск
  в Chroma по тексту и готовому вектору с локальным или серверным хранением.
  Q12 показывает поиск в namespace Pinecone и проверку доступа на стороне
  приложения. Q13 показывает поиск по именованным векторам Weaviate для
  текста и фотографий с выбором совместимой модели и целевого вектора.
  Q26 показывает, как сравнить pgvector с отдельным поисковым сервисом
  при одинаковом `recall@10` по задержке поиска, записи и появления обновлений.
  Q15 сопоставляет `Bounded`, `Session` и `Strong` в Milvus и объясняет,
  как `GuaranteeTs` задаёт порог видимости записи.
  Q11 сопоставляет обновление индексов Annoy и Faiss `IndexHNSWFlat`.
  Q14 показывает, когда создавать payload-индекс Qdrant для фильтрации через HNSW.
  Q1 показывает поиск в Qdrant с фильтром и передачу найденных фрагментов в RAG.
  Q3 показывает роли кодирования запроса и документа. Q7 различает настройку
  поиска по существующему графу HNSW и настройку построения графа.
  Q2 показывает точный поиск в узкой категории. Q25 даёт оценку объёма
  координат с репликами и различает расход диска и памяти. Q28 добавляет
  сценарий обслуживания HNSW после массовой замены эмбеддингов.
  Q22 показывает, как Elasticsearch подбирает кандидатов на каждом шарде
  и собирает глобальный top-10.
  Q19 на числовом примере различает глубину слияния RRF, лимит векторной
  ветки и размер итоговой страницы.
  Q8 показывает, как увидеть в `EXPLAIN` отказ от индекса IVFFlat при
  равенстве `ivfflat.probes` и `lists`.
  Q4 показывает, как cosine similarity, dot product и L2 ранжируют один
  набор векторов до и после нормализации. Предыдущий завершённый ход
  `01a203d0-1edb-7b42-ae1c-66ad67d251ca` добавил в Q27 сценарий смены
  embedding-модели при продолжающейся записи.
  Q16 показывает форму `ORDER BY`, пригодную для HNSW, и зависимость выбора
  индекса от плана. Q18 различает предварительный и последующий фильтры kNN
  в Elasticsearch. Q20 разбирает фильтрацию и итеративное сканирование.
  Q21 поясняет действие RLS для арендатора и владельца таблицы. Q23
  сопоставляет полноту и задержку при фильтрации. Q24 показывает предел
  полноты при повторном ранжировании кандидатов после бинарного поиска.
  Исходный JSON — из `b28a1c807`. MCQ пока не назначен.
- **Остальные темы со сжатым оформлением.** Cassandra, Hibernate JPQL and
  Criteria, MongoDB, Spring AI, Spring AOP, Spring Async, Spring Batch,
  Spring Boot 3 Migration, Spring Boot Actuator, Spring Cache, Spring Cloud,
  Spring Framework, Spring GraphQL, Spring Integration, Spring REST Client,
  Spring Retry, Spring Scheduling, Spring Testing, Spring WebFlux, Metrics
  and Tracing, Digital Wallet, Google Maps и Uber. В production-версии из
  `e8fd2b9ef` поля объектов сжаты на одну строку, а в исходном JSON развёрнуты.
  Для этих тем восстанавливай развёрнутое оформление при полной генерации
  по общему промпту.
  По Spring Batch автор шпаргалок передал промежуточный накопительный снимок
  `work/spring-batch-jpa-page-offset-theory-return/spring-batch-interview.md`
  (SHA-256 `a1091f61923d6c2fbabb9b51cd2202648faf05f877d455e730228d8411a728c6`).
  После снимка с правкой Q37 изменились Q21, Q22, Q25, Q26, Q29 и Q31.
  Следующие снимки обновили Q1, Q2, Q5, Q8–Q13, Q16–Q19, Q39, Q40 и Q43.
  Следующий снимок уточнил обработку элементов и вызовы слушателей при записи.
  Дальше уточнены Q28 (JDBC-метаданные и префикс таблиц), Q7 (результат
  отказа асинхронного launcher), Q4 (перезапуск задания и шага) и Q6
  (параллельный запуск одного экземпляра). В Q23–Q24 уточнены границы
  удалённой обработки и порядок заполнения пула и очереди.
  В следующих снимках уточнены Q3 (контексты шага и задания) и Q14
  (восстановление позиции JDBC-курсора и условия порционной выборки).
  Следующие снимки уточнили Q42 (`@SpringBatchTest` и тестирование `@StepScope`)
  и Q38 (история запусков и сериализация checkpoint при миграции с Batch 4
  на Batch 5). Дальше уточнены Q43 (счётчики шага и метрики партиций)
  и Q15 (пропуск строк при offset-пагинации изменяемой выборки).
  MCQ-чат взял этот снимок и дорегенерационный JSON из `b28a1c807` на
  полную генерацию в ходе `01a20371-40b5-79e1-951e-bdefcd7a621d`.
  В том же ходе автор вернул вопросы Q3–Q4, Q9–Q10, Q15–Q16, Q21–Q22,
  Q24–Q27, Q35–Q37 и Q39–Q40 на доработку видимых схем, кода и условий
  версий. Полный JSON не записан. Координатор отправил автору шпаргалок
  единый возврат и назначил выход
  `work/spring-batch-audit-theory-return/spring-batch-interview.md`.
  Автор передал полный Markdown в ходе `01a20385-0e15-7371-a47e-97aee7705aeb`.
  SHA-256 `105581ff1b152f4c97d06258e15ff1ba708f9221a10b62b885da046b474a94fc`.
  Сверены вводная, 43 заголовка и охват изменений в 17 назначенных блоках.
  Снимок готов для полной генерации. SHA-256 JSON из `b28a1c807` —
  `40b3ca7cf690ee09892dd0743f159dd82b35dc3c90309b25c00c52082be53db5`.
  В ходе `01a2039b-95f3-7a71-b1e1-af6cfc1e6657` MCQ-чат выявил ошибку
  в карточке. Координатор отправил уточнение `topic_slug: spring-batch-interview`
  по исходному JSON. MCQ-чат подтвердил продолжение с этим значением и
  обоими блоками Q6 в ходе `01a2039c-7da8-7750-96df-dd889608a019`.
  В следующих карточках значение `topic_slug` бери
  из корня дорегенерационного JSON. В финальном сообщении того же хода
  автор передал полный Spring Batch JSON —
  `work/spring-batch-audit-restoration-full-regeneration/spring-batch-interview.json`.
  Пара Markdown и JSON в коммите `6b8abb290`.

- **SQL Q16** — чат шпаргалок передал накопительный Markdown в завершённом
  ходе `01a203e8-7a3e-76e0-b5ce-37d98fa9ba33`.
  `work/sql-get-or-create-snapshot-theory-return/sql-interview.md`
  (SHA-256 `8b692ab29cc870c7e18a8024a793dfb875c02a4f7812771ade4d54fa7e06b586`).
  Относительно базы `d8e69deb…` изменилось только тело Q16. Добавлен сценарий,
  где при конкурентной вставке проверка уникальности учитывает строку раньше,
  чем она становится видна снимку команды. Снимок доступен для следующего
  назначения SQL на полную генерацию.

- **Go Standard Library Q4** — чат шпаргалок передал накопительный Markdown
  в завершённом ходе `01a203f9-236a-7583-a000-920d85ba7aca`.
  `work/go-stdlib-header-timeout-theory-return/go-stdlib-interview.md`
  (SHA-256 `a72846e462f296583c31804fe47b1bb29dddbb83ca9d5c01302cc197ceef8111`).
  Относительно `872b3fd2…` добавлен только сценарий задержки тела ответа
  после `200 OK`. Он различает таймаут ожидания заголовков, общий срок
  запроса и предел паузы при чтении потока. Снимок доступен для следующего
  назначения темы на полную генерацию.

- **Spring AI Q5/Q8/Q12** — чат шпаргалок передал накопительный Markdown в
  завершённом ходе `01a203fa-b3b3-7f91-a161-0824105788d1`.
  `work/spring-ai-pgvector-filter-theory-return/spring-ai-interview.md`
  (SHA-256 `9855db098a1a5fb3798c2c3880a689c2a0a5cd41f439b16fb8b81efa2e6663f5`).
  В Q5 относительно `3a91b544…` уточнено, что `topK` задаёт верхнюю границу.
  Добавлен пример, когда фильтр после приближённого HNSW-поиска оставляет
  меньше результатов, и описано итеративное сканирование pgvector 0.8.0+.
  В ходе `01a203fb-99bd-75e0-91bb-9b79990a21be` добавлен сценарий
  повторного запроса после частичной SSE-выдачи в Q8. Накопительный снимок
  `work/spring-ai-sse-retry-theory-return/spring-ai-interview.md`, SHA-256
  `a70fd446530fef53e536006cc5c5338424627c2d912c5511a3e5bd578de537ed`.
  В ходе `01a203fc-471a-73d2-86df-e83b831e6cee` автор передал следующий
  накопительный снимок `work/spring-ai-pdf-ocr-theory-return/spring-ai-interview.md`,
  SHA-256 `c2766f8ce9bc1ab576cfc83f924f260427170667319b18175567621eaca7c90f`.
  В Q12 уточнена группировка PDF-страниц и добавлен сценарий OCR для скана.
  Локально подтверждены хеши Markdown, сохранность 18 заголовков и то, что
  каждый снимок меняет только свой блок. Для следующей полной генерации
  Spring AI использован последний снимок, включающий также сценарий
  одновременной записи истории одного диалога. MCQ-чат передал полный JSON
  в ходе `01a20416-0c97-7861-9961-989f0a8b4f98` по пути
  `work/spring-ai-recovery-full-regeneration/spring-ai-interview.json`.
  JSON перенесён в production побайтно. Пара Markdown и JSON включена
  в коммит вместе с этой записью.

- **Spring REST Client Q3/Q4/Q12** — накопительная теория для следующей
  полной генерации. Уточнены терминальная операция `retrieve()`, время жизни
  ответа в `exchange()` и таймаут ожидания соединения из пула. Последний
  согласованный снимок `work/spring-rest-client-terminal-operation-theory-return/spring-rest-client-interview.md`,
  SHA-256 `eaec20aca46c476b28076c9a01a5ee7bc8cc1e77d7fab3d43e2da2f3f827dda2`.
  Ход автора Markdown `01a2041b-e663-74f3-88ff-56b49bec1593`.
  MCQ-чат передал полный JSON в ходе `01a20422-d505-7e73-b52b-c85e1dd09954`
  по пути `work/spring-rest-client-recovery-full-regeneration/spring-rest-client-interview.json`.
  Пара JSON и Markdown перенесена в production и включена в коммит вместе с этой записью.

- **Spring Testing** — полный согласованный Markdown
  `work/spring-testing-mockito-primary-theory-return/spring-testing-interview.md`,
  SHA-256 `e94722d49eb16785795f2c6994a36e75e64bd9665269a045e57a7429a51a968d`.
  MCQ-чат передал полный JSON в ходе `01a20429-d6ae-7142-9310-3cdadc8ee338`
  по пути `work/spring-testing-recovery-full-regeneration/spring-testing-interview.json`.
  JSON перенесён в production побайтно. Пара Markdown и JSON включена в
  коммит вместе с этой записью.

- **RabbitMQ** — полный согласованный Markdown
  `work/rabbitmq-visible-conditions-theory-return/rabbitmq-interview.md`,
  SHA-256 `d0ddc98a8a7bd7fcc05aa4dce33131696424c2b0a9e4c93c5b79d6e76a5353ae`.
  MCQ-чат передал полный JSON в ходе `01a2043c-3a7f-7fd1-aa44-fd94b9982069`
  по пути `work/rabbitmq-recovery-full-regeneration/rabbitmq-interview.json`.
  JSON перенесён в production побайтно. Пара Markdown и JSON включена в
  коммит вместе с этой записью.

- **Hibernate Q48** — накопительный Markdown передан в завершённом ходе
  `01a203ff-57ce-7f03-bd10-93b3990e6060`.
  `work/hibernate-test-thread-theory-return/hibernate-interview.md`, SHA-256
  `6cec7164e7f43457b1a8db32e265365311ec12c1a28dd62f9b8dd1dded34705f`.
  В Q48 добавлен сценарий `@DataJpaTest` с `assertTimeoutPreemptively()`:
  запись из отдельного потока может зафиксироваться в своей транзакции,
  и откат тестовой транзакции её не затронет. Сверены SHA Markdown, все
  50 заголовков и изменение только Q48 относительно снимка `85f975b8…`.
  Этот снимок доступен для следующей полной генерации Hibernate.

- **Python Testing Q3** — чат шпаргалок передал накопительный Markdown в
  ходе `01a20400-2e68-7b53-b7d4-7a58d70384d7`.
  `work/python-testing-param-xfail-theory-return/python-testing-interview.md`,
  SHA-256 `6fd9c8163160dbfca18a844723dbf71d56ae1587a760a3aa75071d135c8e5fa7`.
  Добавлен пример `pytest.mark.xfail(raises=ValueError, strict=True)` для
  одного параметризованного случая. Сверены семь заголовков и изменение
  только Q3 относительно снимка `0d6a844a…`. Использовать этот накопительный
  снимок для следующей полной генерации Python Testing.

Сокращение текста само по себе не доказывает потерю смысла. Применяй правила
полноты и оформления при генерации перечисленных тем. Дополняй очередь
конкретными уже известными дефектами, без сплошной повторной вычитки.

## История предыдущего процесса

Ниже сохранены прежний план, результаты, очереди и команды для справки. Действующий порядок описан выше.

Сверен с текущим деревом 2026-11-03. Единый план корпуса; новый конкурирующий реестр не нужен.
Сначала закрывается исторически обновлённое множество, затем все TODO.
Приоритет качества: фактическая корректность, полноценность каждого A/B/C/D,
сопоставимость формы и естественный русский язык.

- [Полный промпт для другой LLM](PROMPT_PLAN_INTERVIEW.md).
- [Команды и передача работы](docs/mcq-quality/workflow.md).
- [Проверки каждого файла](docs/mcq-quality/file-checklist.md).
- [Компактный checkpoint](docs/mcq-quality/checkpoint.json).
- [Производный снимок](docs/mcq-quality/audit-state.json).
- [Контракт доказательств](docs/mcq-quality/reviews/README.md).
- [История аудита](docs/mcq-refactoring-state.md).

## Актуальное покрытие

`sync` 2026-11-03 показывает **337 production-пар / 9 921 блок**.
Полная приёмка из действующих evidence: **9 файлов / 226 блоков**;
осталось **328 файлов / 9 695 блоков**. Тема MLOps полностью принята:
все Q1–Q28 имеют действующее evidence.
Состав корпуса меняется из-за параллельных изменений рабочего каталога;
перед дальнейшей работой живой `status` снова имеет приоритет.

| Этап | Всего файлов / блоков | Полных файлов / принятых блоков | Осталось |
|---|---:|---:|---:|
| Исторически обновлённое множество | 176 / 5 032 | 9 / 226 | 167 / 4 806 |
| Остальные TODO | 161 / 4 889 | 0 / 0 | 161 / 4 889 |
| Весь корпус | 337 / 9 921 | 9 / 226 | 328 / 9 695 |

Числа в этом разделе — сверенный ориентир, не источник статуса. `audit-state.json`,
`file-checklist.md` и `checkpoint.json` обновлены успешным `sync` 2026-11-03;
перед продолжением `python3 scripts/mcq-workflow.py status` и `next --size 1`
пересчитывают состояние текущего дерева. Старый AUDITED, исторический факт чтения и
текущий FINAL учитываются раздельно; исторические 44 блока нельзя прибавлять к 226 принятым.

Принятые темы при сохранении хешей: Java functional interfaces Q1–Q14,
Spring transactions Q1–Q15, Hibernate caching Q1–Q15, Agentic Patterns Q1–Q30,
AI Observability Q1–Q28, Inference Optimization Q1–Q36, LLM Basics Q1–Q30,
Long Context vs RAG Q1–Q30 и MLOps Q1–Q28. По каждой подтверждены
CONTENT, FACTS, LINKAGE, SECTIONS, SFPL, ANSC, RU, INDEPENDENT, BLIND, FINAL.
SCHEMA выполняется автоматически. Evidence находятся в reviews/checks, исходные
слепые/независимые отчёты — в reviews/blind. Полный аудит этих тем повторять не нужно
без изменения реальных зависимостей или конкретной новой находки.
В AI Observability исправлены фактические ошибки о GenAI-конвенциях, SDK,
расчёте затрат, защите данных, continuous eval, RAG и A/B-тестах. Для спорных
вариантов сохранены неуспешные проходы, повторные слепые проверки и закрытые findings.
В Inference Optimization исправлены расчёт памяти KV-кэша, разграничение видов
фрагментации, формулировки PagedAttention и неточные утверждения о speculative decoding,
FP16/BF16 и GPTQ. После правки оглавления и якорей Q1–Q36 все блоки повторно прошли
независимые фазы A/B на актуальном снимке; тема полностью принята. История FAIL,
последующих исправлений и итоговых проверок сохранена в `reviews/checks`.

В LLM Basics приняты Q1–Q30. Исправлены определения LLM, Transformer, self-attention
и архитектур Encoder/Decoder, а также ссылки оглавления Q1–Q30. Q5–Q8 авторски
переработаны по первичным источникам: убраны ложные универсальные оценки токенов,
исправлено описание SentencePiece и разграничены локальный подсчёт текста и подсчёт
полного запроса. Первоначальная слепая фаза Q5–Q8 сохранилась как незавершённый run;
после правок новый независимый A/B проход на другой дешёвой модели подтвердил все
десять проверок каждого блока. Принятое evidence находится в
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q5-q8-edited-r2.json`.
В Q9–Q12 исправлены определения контекстного окна и настройки генерации, удалены
устаревающие таблицы и универсальные советы без источника. В Q13–Q16 устранена
ошибка о смысловой остановке по `stop`, уточнены параметры сэмплирования и штрафы,
а сравнение GPT-4/GPT-4o/o1 привязано к историческим возможностям. В Q17–Q20
исправлены обобщения о семействе Claude, лицензии Llama, архитектуре Mixtral,
экономике открытых моделей и связи числа параметров с качеством. Все эти пакеты
прошли отдельные слепые фазы A, независимые фазы B, gates и сохранены в
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q9-q12-edited-r1.json`,
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q13-q16-edited-r1.json` и
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q17-q20-edited-r1.json`.
В Q21–Q24 уточнены галлюцинации, ограниченность RAG и проверки формата,
потоковая выдача и расчёт стоимости без устаревающих тарифов. В Q25–Q30
уточнены rate limits и quota, два вида кеша, методы обучения и выбора RAG,
границы prompt injection и продуктовые риски. Слепые и независимые отчёты,
первичные источники и итоговые verdict каждого блока находятся в evidence
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q21-q24-edited-r1.json`,
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q25-q28-edited-r1.json` и
`docs/mcq-quality/reviews/checks/20261008-llm-basics-q29_q30-edited-r1.json`.

В Long Context vs RAG после исправлений и повторной независимой проверки
приняты все Q1–Q30. Неуспешные проходы и закрытые findings сохранены в
`reviews/checks` и `runs`. В MLOps переработаны теория и 28 production MCQ:
уточнены выпуск и откат моделей, признаки и их свежесть, мониторинг срезов,
эксперименты и LLMOps. После изменения общего оглавления все 28 блоков прошли
новый слепой и раскрытый проход; последний Q22 принят после исправления дистракторов.
Действующие evidence: `20261103-mlops-final-full-r1` и
`20261103-mlops-q22-final-r2` в `docs/mcq-quality/reviews/checks/`.

Техническая находка для инфраструктуры: изменение только навигационной строки
общего оглавления изменило блочные хеши Q1–Q22 и сделало их evidence устаревшими.
Точечная инвалидация навигации требует отдельных тестов и проверки совместимости
со всеми существующими evidence; алгоритм хеширования в этом проходе не менялся.

## Этапы работ

| Приоритет | Работа | Состояние | Проверяемое условие завершения |
|---|---|---|---|
| P0 | Структурные проверки, ошибки CLI, корректный blind-export | Выполнено | Контрпримеры и fail-closed проверка исполнения |
| P0 | Отдельные доказательства всех проверок и точные Q/B | Выполнено | PASS переживает регенерацию; частичный охват, STALE и findings не скрываются |
| P0 | Переносимый запуск без конкретного LLM runtime | Выполнено | doctor/next/packet/prepare/phase-a/reveal/record/checkpoint/sync и тесты полного цикла |
| P0 | План, подробный промпт и очистка противоречивых сценариев | Выполнено | Реальные команды, понятный handoff, Git-версия удалённых файлов |
| P1 | Полное ревью оставшихся обновлённых тем | 167 файлов / 4 806 блоков в очереди | Все 11 проверок каждого блока, отдельные SFPL/ANSC/RU |
| P1 | Исправление подтверждённых дефектов и независимая приёмка | В составе каждого пакета | Действительный PASS после исправления, сохранённые прошлые результаты |
| P1 | Выбор одного MCQ-блока в runtime CompletableFuture Q3/Q8 | BLOCKED по затронутому скоупу | Продуктовое решение, исправление и относящиеся runtime-тесты |
| P2 | Остальные TODO | 161 файл / 4 889 блоков | Тот же полный цикл после обновлённых тем |
| P3 | Итоговая сверка всего корпуса | После P1/P2 | Полные gates, актуальные evidence, sync --require-complete |

Подготовка инфраструктуры сама по себе не выполняет P1/P2. Числа приёмки не увеличены
синтетическими тестами инструмента. Полный содержательный проход выполняет следующий
исполнитель по промпту; работа не объявлена завершённой для всего корпуса.

## Учёт каждого файла и каждой проверки

`file-checklist.md` содержит строку на каждую пару JSON + Markdown с полными путями,
хешами, количеством Q/B, отдельными проверками, покрытием и ссылками на evidence.
Точные прошедшие и оставшиеся блоки — `files[].checks` в audit-state.

Обязательны: SCHEMA, CONTENT, FACTS, LINKAGE, SECTIONS, SFPL, ANSC, RU,
INDEPENDENT, BLIND, FINAL. Их нельзя объединить одной галочкой «просмотрено».
Сигналы эвристик отображаются отдельно. Таблица не редактируется вручную.

Статусы: NOT_RUN, IN_PROGRESS, PARTIAL, PASS, FAIL, STALE, BLOCKED, UNVERIFIED.
Все непринятые блоки остаются в общем check_queue, включая TODO и BLOCKED.
Очереди form_review_queue и readability_review_queue служат приоритетами внутри
обновлённого множества. Общее покрытие определяется всеми проверками, а не одной очередью.

Текущие evidence хранятся по пакетам в `docs/mcq-quality/reviews/checks/*.json`.
Открытый finding закрывается только явным RESOLVED с актуальным результатом той же
проверки. Повторное использование его id для другой проверки/блоков отклоняется.
Исторические замечания в audit-evidence и runtime-находки также нельзя скрыть новым PASS.

## Следующий пакет

Тема `ai-ml/long-context-vs-rag-interview.json` полностью принята: живой
`status --topic` после evidence `20261102-long-context-q29-r6` показывает
30/30 актуальных `FINAL PASS`, без оставшихся блоков и открытых findings.
Production JSON, Markdown-теория и слепые двухфазные проверки сохранены в
`docs/mcq-quality/runs/` и `docs/mcq-quality/reviews/checks/`.
Точечные gates JSON/Markdown и `git diff --check` прошли. Действующие хеши
полных файлов: MCQ `f1b9c1d7bf84d7126fd655f0329afa0eb4130287f389294b8c81be22f93e8850`,
теория `67ecba0a526096f6b329a8aaa244085d0a129d4e189aa3f19f319d0c54141aec`.

Тема `ai-ml/mlops-interview.json` полностью принята: живой `status --topic`
показывает 28/28 актуальных `FINAL PASS`, без оставшихся блоков и открытых findings.
Точечные gates JSON/Markdown и `git diff --check` прошли. Действующие хеши
полных файлов: MCQ `d772745e338116e5a25c11a06f30190ae4615287bb2eafa3196087de50f68d75`,
теория `ba8f05e1a6ff1c9ef1e7de7833fe07574b041b3d0188c9b4d19586d316c259d0`.

Следующая тема по `sync` — `ai-ml/vector-databases-interview.json`, пакет
`Q1:B0,Q2:B0,Q3:B0,Q4:B0`. Перед слепым ревью проверить общие заголовки,
оглавление, якоря, пролог и источники, затем содержательно проверить Q1–Q4.
Статус и хеши новой темы брать из живого `status --topic`.

Первоначальный пилот на 5 блоках и приёмка трёх тем уже выполнены. При смене модели
начать с небольшого пакета 2–4 блока и оценить возвраты. Большие авторские пакеты дешёвой
модели раньше требовали исправления ключа, полноты и второго истинного варианта.
Экономить на повторной работе, сохраняя полный обязательный охват.

Дополнительные агенты только с явно выбранной дешёвой моделью; максимум два на старте.
Без наследования неизвестной дорогой модели, без собственных деревьев субагентов.
Если такого выбора нет, доступная работа продолжается последовательно, а отсутствующее
независимое ревью остаётся честно незакрытым.

## Контрольные точки

После пакета: topic status и checkpoint. После законченной темы или группы пакетов,
после изменения общих валидаторов и перед передачей: полный sync. Если передача
требует конкретного `--next-action`, запиши его **после** sync: sync перезаписывает
checkpoint стандартным действием.

```bash
python3 scripts/mcq-workflow.py status --topic ai-ml/agentic-patterns-interview.json
python3 scripts/mcq-workflow.py checkpoint
python3 scripts/mcq-workflow.py sync
```

Новый исполнитель получает этот план, полный промпт, workflow, checkpoint, текущий
репозиторий и все evidence. История чата и локальные логи work/ не обязательны.
Обязательные артефакты новых проходов сохраняются в docs/mcq-quality/runs/.
Пока изменения не закоммичены, один только `git clone`/HEAD не содержит новой
инфраструктуры, удалений и результатов приёмки. Для передачи нужен текущий рабочий
каталог вместе со staged, unstaged и untracked файлами; перед началом исполнитель
сверяет `git status --short`, `doctor`, `status` и `next`. Незавершённый run передаётся
вместе с его каталогом либо явно отмечается в checkpoint как незакрытый.
Ни один инструмент не выполняет commit/push, не запускает LLM и не тратит reset-кредиты.

Итоговый успех требует `sync --require-complete`, актуального PASS каждого блока и
отсутствия скрытых незавершённых состояний. При прерывании сохраняется точный остаток;
достижение лимита не является завершением задачи.


- **Spring Data JPA** — полный Markdown
  `work/spring-data-jpa-visible-conditions-theory-return/spring-data-jpa-interview.md`
  (SHA-256 `d329f2ba394ea8cf821907293da84e3429f896373e9988e9d7c40d2d3a57538c`).
  MCQ-чат передал полный JSON в завершённом ходе
  `01a2044c-3818-7693-a56b-cf6c3e033e3d` по этому снимку и старому JSON
  из `b28a1c807`. JSON перенесён побайтно из
  `work/spring-data-jpa-recovery-full-regeneration/spring-data-jpa-interview.json`.
  Этот коммит содержит оба production-файла.

- **Go Standard Library** — чат MCQ передал полный JSON после генерации
  из исторического файла темы на `b28a1c807` и полного Markdown
  `work/go-stdlib-header-timeout-theory-return/go-stdlib-interview.md`
  (SHA-256 `a72846e462f296583c31804fe47b1bb29dddbb83ca9d5c01302cc197ceef8111`).
  Результат передан в ходе `01a20463-18bc-78b1-bbcc-3283742473fc` по пути
  `work/go-stdlib-recovery-full-regeneration/go-stdlib-interview.json`.
  Полный JSON перенесён как есть вместе с согласованным Markdown.

- **SQL** — чат шпаргалок передал накопительный Markdown
  `work/sql-visible-conditions-theory-return/sql-interview.md` в ходе
  `01a20462-ffbe-7c52-a632-2bffa48ff427`.
  SHA-256 `5b65aed2c93575219082d8a82278f45b716cbc46e7d4e791d848f1cc25b6727e`.
  Изменены Q1/Q4/Q5/Q14/Q21/Q22/Q24/Q27/Q29/Q31, прежний Q16 сохранён.
  Снимок готов для полной генерации темы из исторического JSON.

- **SQL** — MCQ-чат передал полный JSON в ходе
  `01a2046f-9cb1-77a2-afbd-9a5a1e531e2f` по согласованной теории и
  исходному JSON из `b28a1c807`. Полный файл перенесён из
  `work/sql-recovery-full-regeneration/sql-interview.json`
  вместе с согласованным Markdown. Оба production-файла входят в этот коммит.
