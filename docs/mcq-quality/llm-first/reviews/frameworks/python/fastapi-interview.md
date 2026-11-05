# FastAPI — авторская редакция MCQ

- Дата: 2026-11-05; исполнитель: Codex (точный model-id не экспортирован).
- Статус: `LLM_READY`. Авторский проход с доступным ключом; независимое ревью и `FINAL PASS` не заявлены.
- Охват: Q1–Q7, 7 блоков B0; прочитаны видимые вопросы, теория, A/B/C/D и все sections.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/frameworks/python/fastapi-interview.json`; SHA-256 `923eb6d278707fca2187453e66d766263f4dd87a1664f7a0606c1cb6afa60fa0`.
- Парный Markdown: `cheatsheets/interview/frameworks/python/fastapi-interview.md`; SHA-256 `eff5593686a716b6cfda27e14b031763e547babd6aef77de4228d254ab97db0d`. Теория не изменялась.

## Изменения

Q1: отделено место исполнения sync-вызова от наличия блокирующей работы, исправлено не следовавшее из wrong контрфактическое объяснение про ASGI workers. Q2/Q4: повторяющиеся заглушки заменены конкретными механизмами и действиями. Q3: четыре модели сравнивают scope и момент cleanup; B больше не смешивает завершение ресурса с коммитом транзакции. Q5: каждый wrong имеет одну основную ошибку в статусе или источнике 404. Q6: явно разделены startup, запрос и lazy-инициализация, исправлено неприменимое when_to_apply. Q7: варианты сравнивают транспорт, замену callable и восстановление словаря; описано отличие override от вызова оригинала с fake после него.

## Сопоставление вариантов

Ключ был доступен. Таблица описывает проверенные модели A/B/C/D; это авторское сравнение содержания, полноты, формы и русского текста, а не независимый blind-проход.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q1/B0 | оба вида handler в loop | def в пуле, async в loop, прямой вызов локален | utility автоматически переносится в пул | процессы выбираются по синтаксису handler |
| Q2/B0 | вложенный граф, кеш запроса, use_cache | кеш между запросами | вложенные зависимости запрещены | use_cache меняет scope на singleton |
| Q3/B0 | cleanup до окончания handler | request cleanup до отправки | function cleanup между handler и response | function cleanup после streaming |
| Q4/B0 | response_model нужен только для схемы | входная модель не валидирует | фильтруется также явный Response | обычный вход/выход проверяются, поля фильтруются |
| Q5/B0 | входная валидация 404 | стандартный 422 и прикладной HTTPException 404 | фреймворк сам распознаёт отсутствующий заказ | стандартная валидация 400 |
| Q6/B0 | startup/shutdown без старых handlers | жизненный цикл запроса | lifespan вместе с on_event | создание на первом запросе |
| Q7/B0 | отдельный TCP-сервер | автоматическое восстановление overrides | сначала вызывается оригинал | ASGI, замена оригинала, явное восстановление |

## Первичные источники

- [FastAPI async technical details](https://fastapi.tiangolo.com/async/#very-technical-details)
- [FastAPI sub-dependency cache](https://fastapi.tiangolo.com/tutorial/dependencies/sub-dependencies/)
- [FastAPI yield and scope, 0.121.0+](https://fastapi.tiangolo.com/advanced/advanced-dependencies/#dependencies-with-yield-and-scope)
- [FastAPI response model](https://fastapi.tiangolo.com/tutorial/response-model/)
- [FastAPI lifespan](https://fastapi.tiangolo.com/advanced/events/)
- [FastAPI testing overrides](https://fastapi.tiangolo.com/advanced/testing-dependencies/)
- [FastAPI testing lifespan](https://fastapi.tiangolo.com/advanced/testing-events/)

## Проверки и ограничения

`verify-mcq-json`: PASS. Структура, единственность ключа и локальные ссылки проверены; validator пишет semantic review NOT_RUN. Номера Q/B, видимые вопросы и позиции ключей сохранены.

`gates.sh`: exit 1; эвристические результаты не переименованы в PASS, пороги и правила не менялись. DUPLICATE_NGRAMS=9: внутри одного блока зафиксированы общие условия и изменяется спорная операция (scope/cleanup, override/восстановление). Q7A дополнен до полного описания подмены, исходная разница полноты устранена. SKEL hard_fails=0. Прочитаны сигналы Q1A/D, Q2B/C/D, Q4B/C, Q5C, Q7A: сравнение охватывает одинаковые действия, а неверность не сводится к отсутствию части ответа. Статистический chars:min=0.476 на семи блоках сохраняется; он не объявлен доказательством неразличимости или приёмкой. Ключи не переставлялись и длина не подгонялась. Независимая проверка формы остаётся открытой; остальные анти-подсказочные и детерминированные linkage-сканы без сигналов.

В этой задаче FastAPI не устанавливался и HTTP-приложение не запускалось. Версии и механизмы сверены с официальной документацией, включая начало поддержки scope="function" в 0.121.0. Отчёт не подтверждает интеграцию с конкретной БД или сервером.

Независимая SFPL/ANSC/RU-приёмка и слепой пользовательский проход по этим хешам остаются открытыми.
