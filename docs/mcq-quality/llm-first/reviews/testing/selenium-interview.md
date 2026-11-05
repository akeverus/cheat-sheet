# Selenium — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого содержательного ревью и без `FINAL PASS`.
- Охват: Q1–Q15, 15 блоков B0, все варианты A–D и их `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/testing/selenium-interview.json`; SHA-256 `fa2a1ab29acc760fa9ec25a1a3b7de27c9e5e0675fcdaa1a5ebc74bfd6eddd12`.
- Парный Markdown: `cheatsheets/interview/testing/selenium-interview.md`; SHA-256 `a209ee6d8921727eae61363b00a7b1fb6262ee09a9839ae19d18c82b26b0bafd`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/selenium-interview.json`; SHA-256 `dbfdc931028554c6a72517db258545c2c9f4276bbf4ec609b860e7f7a60ce30d`.

## Что исправлено

Прочитаны все 15 вопросов, варианты и объяснения вместе с парным Markdown. Q1 заменяет устаревшую схему JSON Wire Protocol на W3C WebDriver. Q2 уточняет выбор устойчивых локаторов без обещания, что один способ всегда быстрее. Q6 различает ленивый прокси PageFactory и `@CacheLookup`. Q8 описывает Router, Distributor и Node в Grid 4 без гарантии линейного ускорения.

Q9 показывает рабочие настройки параллельности JUnit и отдельную сессию WebDriver для теста. Q10 разделяет исправление причины флейков, диагностику и временный карантин. Q11 исправляет браузерную поддержку Cypress и Puppeteer. Q12 использует встроенный Selenium Manager вместо обязательной сторонней библиотеки WebDriverManager. В Q15 удалены неподтверждённые проценты тестовой пирамиды.

В Markdown добавлены содержание и якоря Q1–Q15. Исправлены примеры переключения frame, настройки JUnit, ретрая и Cucumber steps. Варианты MCQ выровнены по форме; пояснения синхронизированы с исправленными фактами.

## Проверка и границы результата

Первичные источники: [Selenium Getting Started](https://www.selenium.dev/documentation/webdriver/getting_started/), [Selenium Locators](https://www.selenium.dev/documentation/test_practices/encouraged/locators/), [Selenium Waits](https://www.selenium.dev/documentation/webdriver/waits/), [Selenium Grid Components](https://www.selenium.dev/documentation/grid/components/), [Cypress Browsers](https://docs.cypress.io/app/references/launching-browsers), [Puppeteer Browsers](https://pptr.dev/chromium-support/).

`verify-mcq-json.sh` → `PASS: files=1 questions=15 blocks=15; semantic review NOT_RUN`. `mcq-skel-gate.py` → hard fails 0, предупреждений 0. `mcq-anti-tell-scan.py` → 0 блоков с формальными подсказками. `mcq-answer-parity-gate.py` → PASS. Содержание, якоря и номера JSON совпадают; `git diff --check` прошёл.

Эти проверки не дают независимого содержательного вердикта. Слепой проход по указанным хешам и подтверждение `FINAL` остаются открытыми.


## Peer follow-up — 2026-11-05

Исходный отчёт выше относится к MCQ `fa2a1ab29acc760fa9ec25a1a3b7de27c9e5e0675fcdaa1a5ebc74bfd6eddd12`. После distinct-author open-key аудита всех 15 вопросов исправлены варианты Q1/Q3/Q5/Q7/Q13/Q14 и sections 37 вариантов; ключи и идентификаторы сохранены. Текущий MCQ: `f6576f3983129e197470fca6bd3665c7d21d352a8173806672de3579c3acc61c`.

Подробные находки, первичные источники, три ограниченных Java-воспроизведения и оставшиеся проблемы теории: [peer report](selenium-peer-review-2026-11-05.md). Статус текущей пары **NEEDS_THEORY_SYNC / NEEDS_REVIEW**. Собственные исправления не получили независимую Phase A/B приёмку. Selenium API runtime, браузеры, Grid, Manager и CI **NOT_RUN**. Linkage остаётся с exit 1; зелёные pre-checks не заменяют FINAL.
