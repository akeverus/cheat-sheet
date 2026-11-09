# AI Safety и Guardrails — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Статус: `VISUAL_PARITY_REVIEWED`. После авторской редакции проведена [отдельная ревизия визуальной симметрии A–D](ai-safety-guardrails-visual-parity-2026-11-09.md); независимого слепого ревью и `FINAL PASS` нет.
- Охват: Q1–Q32, 32 блока, все видимые вопросы, варианты A–D, sections и парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/ai-safety-guardrails-interview.json`; SHA-256 `a4083f0c9658462196349868b41bdb978f1613d425aeff1c822a3d03c30514a5`.
- Парный Markdown: `cheatsheets/interview/ai-ml/ai-safety-guardrails-interview.md`; SHA-256 `e235ccd3811576afe8b585ec486db22ae4bc3645266fbbd7643032d12c1db160`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/ai-safety-guardrails-interview.json`; SHA-256 `2715f259f46fb00301355eca27bd34c55772e62b3154fe1f40005eeb25cf0725`.

## Что исправлено

Вычитаны 128 видимых вариантов и их sections. Неверные ответы стали цельными и более правдоподобными: они отвечают на составные вопросы, но сохраняют распознаваемую ошибку. Правильные ключи сохранены. Длина вариантов выровнена, чтобы подробность сама по себе не подсказывала ответ.

Теория теперь отделяет риски модели от обычной AppSec без утверждения, что одна заменяет другую. Для prompt injection показаны границы доверия и проверка действий у исполнителя. Убраны универсальные проценты успеха атак, фиксированный порог many-shot и обещания, что отдельный классификатор, маркировка данных, RAG или Structured Outputs дают абсолютную защиту. В Q19–Q20 разведены проверка фактов, несколько независимых решений и оценка уверенности.

Версионные факты уточнены по первичным источникам: OWASP описан как редакция 2025, Llama Guard 3 8B — как конкретная модель с категориями S1–S14, NeMo — с пятью типами rails, OpenAI Moderation — с различиями поддержки текста и изображений. Q23 учитывает поэтапные сроки EU AI Act, действующие в проверенной официальной сводке на 2026-11-09, и отдельно формулирует применимость GDPR и HIPAA. В Q30 лабораторный тест DeepSeek-R1 отделён от производственного инцидента, а демонстрации Bing, Chevrolet и Slack AI — от подтверждённого ущерба.

## Проверка и границы результата

Источники проверены по [OWASP Top 10](https://genai.owasp.org/llm-top-10/), [Microsoft Prompt Shields](https://learn.microsoft.com/en-us/azure/foundry/openai/concepts/content-filter-prompt-shields), [карточке Llama Guard 3](https://huggingface.co/meta-llama/Llama-Guard-3-8B), [NVIDIA NeMo](https://docs.nvidia.com/nemo/guardrails/latest/index.html), [OpenAI Moderation](https://developers.openai.com/api/docs/guides/moderation), [OpenAI Structured Outputs](https://developers.openai.com/api/docs/guides/structured-outputs), [HHS](https://www.hhs.gov/hipaa/for-professionals/faq/may-a-hipaa-covered-entity-or-business-associate-use-cloud-service-to-store-or-process-ephi/index.html), [Еврокомиссии](https://ai-act-service-desk.ec.europa.eu/en/ai-act/timeline/timeline-implementation-eu-ai-act) и [исследованию Cisco](https://blogs.cisco.com/security/evaluating-security-risk-in-deepseek-and-other-frontier-reasoning-models). Полный список и риски устаревания находятся в fact-freshness. Прямая страница решения *Moffatt v. Air Canada* вернула HTTP 403 при автоматическом чтении; соответствующий пассаж ограничен выводом по конкретному делу.

Структурная проверка: `PASS: files=1 questions=32 blocks=32; semantic review NOT_RUN`. Дополнительно сверены 32 заголовка Markdown, 128 полных вариантов A–D, единственные правильные ключи, знаки окончания вариантов и длина сопоставляемых ответов; `git diff --check` по тематическим файлам прошёл. Внутренние question_text приведены к видимым заголовкам; результаты ревизии формы и два оставшихся предупреждения linkage записаны в связанном отчёте. Примеры не запускались против внешних API. Для `FINAL PASS` нужен независимый слепой просмотр всех Q/A–D и затем раскрытая проверка sections на этих SHA.
