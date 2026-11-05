# Кучи — редакция MCQ

- Дата: 2026-11-04; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция, без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q29, 29 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/heaps-interview.json`; SHA-256 `27e4bf6a29c818d5a73399dc7ac3679193509ec3b421e14577b462abcd975977`.
- Парный Markdown: `cheatsheets/interview/algorithms/data-structures/heaps-interview.md`; SHA-256 `716dec9eca4c9d9020072dfcbff3329bc0cb88045f62a60d9a820fbb4123b064`.

## Что исправлено

Прочитаны все 29 блоков. В Q7, Q12, Q18, Q20, Q21, Q23 и Q24 ограничение по времени, памяти или порядку результата перенесено в видимый вопрос: ранее неверный вариант местами описывал другой корректный алгоритм. Соответствующие `sections` различают работающий альтернативный способ и нарушение указанного ограничения.

В Q16 ответ согласован с контрактом Java `PriorityQueue.iterator()`: порядок итерации не гарантирован. В Q19 уточнена верхняя оценка Heap Sort, не утверждается одинаковое число обменов на каждом входе. В Q4 и Q28 убраны безусловные оценки и сравнения pairing heap с Fibonacci heap. В примере Q24 расстояние вычисляется в `long`, компаратор использует `Long.compare`.

Из заголовков и оглавления убраны редакционные метки `(!)`, для Q1–Q29 добавлены стабильные якоря.

## Источники и проверка

- [Java PriorityQueue](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/PriorityQueue.html): контракт итератора и очереди.
- [Fredman, Sedgewick, Sleator, Tarjan — Pairing Heaps](https://www.cs.cmu.edu/~sleator/papers/Pairing-Heaps.htm): устройство сливающейся самонастраивающейся кучи и границы исходного анализа.

Самопроверка: 29 вопросов JSON совпадают с заголовками Markdown, в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/heaps-interview.json` → `PASS: files=1 questions=29 blocks=29; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
