# gRPC — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q40, 40 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/api/grpc-interview.json`; SHA-256 `48ea414e82a0023b077780599b91b4c58c0dd2b6f5027e3d7245fabc4719002d`.
- Парный Markdown: `cheatsheets/interview/api/grpc-interview.md`; SHA-256 `72965958ae1dbce984e26384097a9f6146e345aa7c89b610b0eea8945d5e89bc`.

## Что исправлено

Прочитаны все 40 блоков. Q18 и Q38 теперь различают включённую по умолчанию подсистему повторов, отсутствие retry policy по умолчанию и источники ServiceConfig. Q21 отделяет защиту канала от проверки полномочий RPC. Q24 не выдаёт in-process тест за проверку TLS и HTTP/2. Q27 и Q34 уточняют возможности gRPC-Web и совместимого сервера.

В Q29, Q32 и Q36 совместимость protobuf учитывает номера полей, wire type, имена в ProtoJSON и сгенерированном коде, а также резервирование удалённых полей. Q30 и Q35 убирают жёсткое правило выбора протокола по публичности API. Q31 уточняет срок жизни канала. Q33 связывает observability с RPC-aware метриками, OpenTelemetry и interceptors. Q37 требует политику `round_robin` вместе с адресами подов; Q39 привязан к конкретному `net.devh` стартеру.

В Markdown исправлены соответствующие разделы, пример `reserved` и сломанный Java-фрагмент в разделе observability. Оглавление и явные якоря Q1–Q40 обновлены, редакционные метки `(!)` удалены.

## Проверка и открытые вопросы

Первичные источники: [gRPC Retry](https://grpc.io/docs/guides/retry/), [Deadlines](https://grpc.io/docs/guides/deadlines/), [Service Config](https://grpc.io/docs/guides/service-config/), [Authentication](https://grpc.io/docs/guides/auth/), [gRPC Java InProcess](https://github.com/grpc/grpc-java/blob/master/examples/README.md), [Protocol Buffers proto3](https://protobuf.dev/programming-guides/proto3/), [Proto Best Practices](https://protobuf.dev/programming-guides/dos-donts/), [gRPC OpenTelemetry Metrics](https://grpc.io/docs/guides/opentelemetry-metrics/), [grpc-spring starter](https://github.com/grpc-ecosystem/grpc-spring) и [Spring Boot gRPC](https://docs.spring.io/spring-boot/reference/io/grpc.html).

Самопроверка: 40 вопросов JSON совпадают с заголовками Markdown, у каждого есть якорь и ссылка из оглавления; в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/api/grpc-interview.json` → `PASS: files=1 questions=40 blocks=40; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
