# Стек и очередь — редакция MCQ

- Дата: 2026-11-04; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q25, 25 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/stacks-queues-interview.json`; SHA-256 `556d6c4356e7b858930b3c235d5f6cd5481f6e901e0b5dd62bdbc0602f204492`.
- Парный Markdown: `cheatsheets/interview/algorithms/data-structures/stacks-queues-interview.md`; SHA-256 `3bca0b458d567a7ff710429dcee56960138a0a5a458f1fe263fb7b37d0cbbc49`.

## Что исправлено

Прочитаны все 25 блоков. Q3 теперь различает legacy-класс `Stack` и аннотацию `@Deprecated`; вопрос формулирует рекомендацию `Deque`. В Q4 уточнён приоритет, задаваемый компаратором. Q17 ограничен очередью с заданной вместимостью, где `put` действительно ждёт место; пример обрабатывает прерывание потоков. Q18 полностью сравнивает `ArrayBlockingQueue`, `LinkedBlockingQueue` и `SynchronousQueue` в каждом варианте. Q23 явно задаёт политику перезаписи при заполнении; теория больше не приписывает её всем кольцевым буферам. Поля `sections` обновлены для изменённых ответов.

В Markdown обновлены пролог, оглавление и якоря Q1–Q25; редакционные метки `(!)` удалены.

## Источники и проверка

- [Java Deque](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Deque.html): рекомендация использовать интерфейс вместо legacy `Stack`.
- [Java BlockingQueue](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/BlockingQueue.html), [SynchronousQueue](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/SynchronousQueue.html): ожидающие операции и очередь без внутренней ёмкости.

Самопроверка: 25 вопросов JSON совпадают с заголовками Markdown, в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/stacks-queues-interview.json` → `PASS: files=1 questions=25 blocks=25; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
