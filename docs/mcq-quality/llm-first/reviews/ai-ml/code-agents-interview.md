# Code Agents — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Статус: `VISUAL_PARITY_REVIEWED`; [отдельная ревизия всех A–D](code-agents-visual-parity-2026-11-09.md) завершена. Независимое слепое ревью и `FINAL PASS` не проводились.
- Охват: Q1–Q31, 31 блок, все 124 A–D, sections и парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/code-agents-interview.json`; SHA-256 `b0318b4207c8a290871ddfe9ff108ab0af1a718f939e852c095a345914bbaa90`.
- Markdown: `cheatsheets/interview/ai-ml/code-agents-interview.md`; SHA-256 `ed7b2677425243a9403904035fc93e756480a7482691c28cf32536827dcd879b`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/code-agents-interview.json`; SHA-256 `e5b05756559de14d4c79e240c645353d365ae9a5785a4132e988b2399c5187fa`.

## Смысловые правки

Отделены обычные inline/chat-подсказки от агентного цикла с инструментами. Неверные варианты в составных вопросах теперь отвечают на все части вопроса, сохраняя конкретное заблуждение. Правильные ключи не менялись. Пояснения приведены к текущим вариантам; убраны круговые аргументы и безусловные утверждения о sandbox, git-коммитах, тестах и human approval.

SWE-bench Verified описан как 500 вручную проверенных задач; проценты без общего split, harness, бюджета и числа попыток не сравниваются. Историческая fast-apply архитектура Cursor 2024 года отделена от текущего Agent. `.cursorrules` отмечен как legacy, а Aider repo map и Architect/Editor описаны с действующими настройками. Claude Code, Cline, OpenHands и Continue показаны с их актуальными поверхностями. Copilot Workspace оставлен как preview 2024 года, Devin — как вендорское демо, не независимое подтверждение нынешнего результата. Убраны устаревшие долларовые оценки и прогнозы, выданные за факты.

После выборочной проверки усилены дистракторы Q4:C, Q7:D и Q24:B; sections согласованы с новым смыслом. Подробности и актуальные измерения — в отчёте о визуальной ревизии.

## Источники и проверка

Проверены первичные страницы [SWE-bench](https://www.swebench.com/verified), [Cursor Agent](https://cursor.com/help/ai-features/agent), [Cursor fast apply](https://cursor.com/blog/instant-apply), [Aider](https://aider.chat/docs/repomap.html), [Claude Code](https://code.claude.com/docs/en/overview), [Cline](https://docs.cline.bot/cline-overview), [Cognition](https://cognition.com/blog/introducing-devin), [OpenHands](https://docs.openhands.dev/overview/introduction), [GitHub](https://github.blog/changelog/2026-06-17-github-copilot-app-generally-available/) и [Continue](https://docs.continue.dev/cli/quickstart). Версии и ограничения перечислены в fact-freshness. Детали текущей внутренней реализации Cursor Agent и Devin не раскрыты публично; текст не делает о них категоричных выводов.

Адресный валидатор: `PASS: files=1 questions=31 blocks=31; semantic review NOT_RUN`. Дополнительно сверены 31 заголовок, 124 варианта, единственный ключ в каждом блоке, полнота sections, пунктуация и сопоставимость длины; `git diff --check` прошёл. Для `FINAL` требуется независимый слепой просмотр Q/A–D и раскрытая проверка sections на зафиксированных SHA.
