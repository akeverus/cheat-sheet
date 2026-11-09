# AI Compliance & Governance — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Статус: `VISUAL_PARITY_REVIEWED`. После авторской редакции проведена [отдельная ревизия визуальной симметрии A–D](ai-compliance-governance-visual-parity-2026-11-09.md); независимого слепого ревью и `FINAL PASS` нет.
- Охват: Q1–Q32, 32 блока, все видимые вопросы, варианты A–D, sections и парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/ai-compliance-governance-interview.json`; SHA-256 `2c2e5e768030418d742bf5fa2d6dacf06efc66170251737ce5cadc4210505dba`.
- Парный Markdown: `cheatsheets/interview/ai-ml/ai-compliance-governance-interview.md`; SHA-256 `41f033b905a60ea5ab358754e272e150f2d6f495142656738be782102250f07f`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/ai-compliance-governance-interview.json`; SHA-256 `c38e649d4fa22271500c5b84b3c20ad5e348e33961b469289225bb3cfab0f6c2`.

## Что исправлено

Вычитаны 128 вариантов и пояснения к ним. Неверные варианты сформулированы как полные ошибочные ответы на вопрос; правильные ключи сохранены. Длина каждого набора сопоставима: самый длинный вариант меньше чем вдвое длиннее самого короткого. Теория Q1–Q32 переписана вместе с MCQ, чтобы прежние даты и категоричные утверждения не противоречили ответам.

Сроки EU AI Act уточнены по действующей редакции после Regulation (EU) 2026/1744: Annex III high-risk — декабрь 2027, Annex I — август 2028. Четыре «уровня риска» представлены как учебное упрощение, а не формальная универсальная классификация. Уточнены частичное open-source исключение для GPAI, отдельный режим штрафов Article 101 и границы FRIA. Требование к возможности логирования Article 12 отделено от хранения провайдером по Article 19. В reporting Article 73 разведены сроки 15, 2 и 10 дней и учтена разумно предполагаемая причинная связь.

Для США исправлены действующий статус EO 14110 и замена Colorado SB24-205 законом SB26-189 с новой датой 1 января 2027. В GDPR убраны утверждения об автоматической обязательности DPIA для любого AI и о безусловном EU-only хранении. Для C2PA отделена проверка подписанной истории от истинности сцены. Для copyright оставлена оценка по юрисдикции, лицензии и человеческому авторству без обещания общего исхода споров.

## Проверка и границы результата

Правовые и версионные факты проверены по [EU AI Act Service Desk](https://ai-act-service-desk.ec.europa.eu/en/ai-act/timeline/timeline-implementation-eu-ai-act), [Еврокомиссии](https://commission.europa.eu/law/law-making-process/better-regulation/simplification-implementation-and-enforcement/simplification_en), [GDPR на EUR-Lex](https://eur-lex.europa.eu/eli/reg/2016/679/oj/eng/), [Colorado Attorney General](https://coag.gov/ai/), [Белому дому](https://www.whitehouse.gov/presidential-actions/2025/01/removing-barriers-to-american-leadership-in-artificial-intelligence/), [NIST AI RMF](https://nvlpubs.nist.gov/nistpubs/AI/NIST.AI.100-1.pdf), [ISO](https://www.iso.org/insights/iso-42001-explained), [C2PA](https://spec.c2pa.org/specifications/specifications/2.5/specs/C2PA_Specification.html) и [US Copyright Office](https://www.copyright.gov/ai/). Полный перечень и риски устаревания записаны в fact-freshness. Обзор UK и Китая оставлен на уровне подходов; для конкретного сервиса требуется отдельная проверка действующих отраслевых правил.

Структурная проверка: `PASS: files=1 questions=32 blocks=32; semantic review NOT_RUN`. Дополнительно сверены 32 заголовка Markdown, 128 вариантов A–D, единственные ключи, полнота sections, пунктуация и соотношение длины вариантов; `git diff --check` по тематическим файлам прошёл. Результаты отдельной ревизии формы и оставшиеся предупреждения записаны в связанном отчёте. Для `FINAL PASS` нужен независимый слепой просмотр всех Q/A–D и затем раскрытая проверка sections на указанных SHA.
