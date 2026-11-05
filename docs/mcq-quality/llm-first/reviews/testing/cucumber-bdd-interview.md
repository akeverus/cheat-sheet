# Cucumber и BDD — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого содержательного ревью и без `FINAL PASS`.
- Охват: Q1–Q15, 15 блоков B0, все варианты A–D и их `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/testing/cucumber-bdd-interview.json`; SHA-256 `9ca9bbbb5990d11e35ce4b9fbd2f51348ee1b05fbf7f70672916840a8a01ce73`.
- Парный Markdown: `cheatsheets/interview/testing/cucumber-bdd-interview.md`; SHA-256 `14eef76415e2583c1081e1b5e3bb8f734d64413af8647ee7b1341e66e5d4ced1`.

## Что исправлено

Прочитаны все 15 вопросов, варианты, объяснения и связанная теория. Q1 теперь различает цикл TDD и совместное уточнение примеров BDD без утверждения, что TDD проверяет только внутреннюю реализацию. Q3 ограничивает пользу исполняемой документации покрытым поведением: зелёный прогон сам по себе не доказывает верность требований. Q4 уточняет, что ключевое слово `And` не участвует в поиске step definition, но за шагом стоит исполняемый обработчик.

Q6 разделяет устойчивость текста сценария и необходимость поддерживать код шага после смены UI. Q9 убирает недоказанное обещание ошибки при `Scenario Outline` без `Examples`: без строк данных нет прогонов по шаблону. В Q11 правильный вариант теперь перечисляет порядок сценарных и шаговых хуков; неверные варианты проверяют конкретные ошибки в этом порядке. Q13 показывает, что `@Suite` удобен для явного выбора ресурсов, но не обязателен для самого `cucumber-junit-platform-engine`. В Q15 видимый вопрос сужен до того, что действительно различают ответы. Версии Cucumber в примере Maven вынесены в свойство.

В Markdown добавлены явные якоря и ссылки содержания Q1–Q15, сняты старые метки `(!)`. Заголовки, якоря и номера JSON совпадают.

## Проверка и границы результата

Первичные источники: [Gherkin Reference](https://cucumber.io/docs/gherkin/reference/), [Cucumber Step Definitions](https://cucumber.io/docs/cucumber/step-definitions/), [Cucumber Expressions](https://cucumber.io/docs/cucumber/cucumber-expressions/), [Cucumber API и Hooks](https://cucumber.io/docs/cucumber/api/?lang=java), [Cucumber JUnit Platform Engine](https://github.com/cucumber/cucumber-jvm/blob/main/cucumber-junit-platform-engine/README.md).

`verify-mcq-json.sh` → `PASS: files=1 questions=15 blocks=15; semantic review NOT_RUN`. `mcq-skel-gate.py` → hard fails 0, warnings 0. `mcq-anti-tell-scan.py` → 0 блоков с формальными подсказками. `mcq-answer-parity-gate.py` → PASS. `git diff --check` для пары файлов прошёл.

Проверки структуры и формы не заменяют независимое содержательное ревью. Слепой проход по указанным хешам и подтверждение `FINAL` остаются открытыми.
