---
title: "Вопросы на собеседовании: Безопасность ИИ и guardrails"
description: "Безопасность LLM в production: prompt injection, jailbreaks, hallucinations, Llama Guard, NeMo Guardrails, OWASP Top 10 for LLM, PII redaction."
tags:
  - interview
  - ai-ml
  - ai-safety
type: "interview"
difficulty: "advanced"
aliases:
  - "AI Safety interview"
  - "LLM Guardrails собеседование"
  - "Prompt injection jailbreak"
  - "Llama Guard NeMo"
updated: "2026-11-09"
---
# Вопросы на собеседовании: Безопасность ИИ и guardrails

Безопасность LLM-приложения охватывает доверие к данным, качество ответа, полномочия инструментов, приватность и наблюдаемость. Нужные проверки выбирают по задаче и цене ошибки. Ни одна инструкция модели или отдельный классификатор не заменяет контроль доступа в приложении.

## Полезные ссылки

### Стандарты и фреймворки

- [OWASP Top 10 for LLM Applications 2025](https://genai.owasp.org/llm-top-10/)
- [NIST AI Risk Management Framework (AI RMF 1.0)](https://www.nist.gov/itl/ai-risk-management-framework)
- [MITRE ATLAS — adversarial threat landscape for AI](https://atlas.mitre.org/)
- [EU AI Act Service Desk — сроки и требования](https://ai-act-service-desk.ec.europa.eu/en/ai-act/timeline/timeline-implementation-eu-ai-act)

### Guardrails frameworks

- [Llama Guard 3 paper (Meta, 2024)](https://arxiv.org/abs/2411.10414)
- [NeMo Guardrails (NVIDIA)](https://github.com/NVIDIA/NeMo-Guardrails)
- [Guardrails AI](https://github.com/guardrails-ai/guardrails)
- [Microsoft Presidio — PII detection](https://microsoft.github.io/presidio/)
- [OpenAI Moderation API](https://developers.openai.com/api/docs/guides/moderation)
- [Anthropic Constitutional AI](https://www.anthropic.com/research/constitutional-ai-harmlessness-from-ai-feedback)

### Red-teaming и benchmarks

- [Microsoft PyRIT — Python Risk Identification Tool](https://github.com/Azure/PyRIT)
- [HarmBench benchmark](https://www.harmbench.org/)
- [JailbreakBench](https://jailbreakbench.github.io/)
- [Anthropic Many-shot jailbreaking (2024)](https://www.anthropic.com/research/many-shot-jailbreaking)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Категории рисков**
- [Q1. (!) Какие категории рисков у LLM в production?](#q1--какие-категории-рисков-у-llm-в-production)
- [Q2. (!) Что входит в OWASP Top 10 for LLM 2025?](#q2--что-входит-в-owasp-top-10-for-llm-2025)
- [Q3. Threat model для LLM-приложения?](#q3-threat-model-для-llm-приложения)

**Prompt Injection и Jailbreaks**
- [Q4. (!) Что такое prompt injection? Direct vs indirect?](#q4--что-такое-prompt-injection-direct-vs-indirect)
- [Q5. (!) Защита от prompt injection: какие слои?](#q5--защита-от-prompt-injection-какие-слои)
- [Q6. Что такое spotlighting и data marking?](#q6-что-такое-spotlighting-и-data-marking)
- [Q7. (!) Jailbreaking — основные техники?](#q7--jailbreaking--основные-техники)
- [Q8. Как работает many-shot jailbreaking (Anthropic 2024)?](#q8-как-работает-many-shot-jailbreaking-anthropic-2024)
- [Q9. Что такое автоматические adversarial-суффиксы (GCG / AutoDAN)?](#q9-что-такое-автоматические-adversarial-суффиксы-gcg--autodan)
- [Q10. System prompt leak — как защищаться?](#q10-system-prompt-leak--как-защищаться)

**Guardrails frameworks**
- [Q11. (!) Llama Guard — что это и какие категории?](#q11--llama-guard--что-это-и-какие-категории)
- [Q12. (!) NeMo Guardrails — архитектура и Colang DSL?](#q12--nemo-guardrails--архитектура-и-colang-dsl)
- [Q13. Как устроены validators в Guardrails AI?](#q13-как-устроены-validators-в-guardrails-ai)
- [Q14. OpenAI Moderation API — категории и Multimodal?](#q14-openai-moderation-api--категории-и-multimodal)
- [Q15. Сравнение Llama Guard vs NeMo vs Guardrails AI?](#q15-сравнение-llama-guard-vs-nemo-vs-guardrails-ai)
- [Q16. Constitutional AI (Anthropic) — как работает?](#q16-constitutional-ai-anthropic--как-работает)

**Hallucination mitigation**
- [Q17. (!) Hallucination — типы и причины?](#q17--hallucination--типы-и-причины)
- [Q18. (!) RAG как способ снизить hallucinations?](#q18--rag-как-способ-снизить-hallucinations)
- [Q19. Как снижают hallucinations через Chain-of-Verification (CoVe), self-consistency и LLM-as-judge?](#q19-как-снижают-hallucinations-через-chain-of-verification-cove-self-consistency-и-llm-as-judge)
- [Q20. Как оценить уверенность модели: logprobs vs verbalized uncertainty?](#q20-как-оценить-уверенность-модели-logprobs-vs-verbalized-uncertainty)

**PII и output validation**
- [Q21. (!) PII detection и redaction — Presidio vs LLM-based?](#q21--pii-detection-и-redaction--presidio-vs-llm-based)
- [Q22. Как валидировать структуру output (JSON schema, Pydantic)?](#q22-как-валидировать-структуру-output-json-schema-pydantic)
- [Q23. Что требует compliance: GDPR, HIPAA, EU AI Act?](#q23-что-требует-compliance-gdpr-hipaa-eu-ai-act)

**Tool use safety и agent loops**
- [Q24. (!) Как обезопасить tool use: least privilege, sandbox, confirmation?](#q24--как-обезопасить-tool-use-least-privilege-sandbox-confirmation)
- [Q25. Agent loops — как ограничивать?](#q25-agent-loops--как-ограничивать)
- [Q26. Indirect prompt injection в RAG / agent?](#q26-indirect-prompt-injection-в-rag--agent)

**Red-teaming и observability**
- [Q27. (!) Red-teaming и PyRIT?](#q27--red-teaming-и-pyrit)
- [Q28. Какие бенчмарки оценивают jailbreak-устойчивость: HarmBench, JailbreakBench, AdvBench?](#q28-какие-бенчмарки-оценивают-jailbreak-устойчивость-harmbench-jailbreakbench-advbench)
- [Q29. Observability для safety: логирование, алерты?](#q29-observability-для-safety-логирование-алерты)

**Real incidents**
- [Q30. (!) Какие случаи показывают риски: Bing Sydney, Air Canada, Chevrolet, Slack AI и DeepSeek-R1?](#q30--какие-случаи-показывают-риски-bing-sydney-air-canada-chevrolet-slack-ai-и-deepseek-r1)
- [Q31. Data poisoning при fine-tuning?](#q31-data-poisoning-при-fine-tuning)
- [Q32. Чек-лист safety review перед запуском LLM-фичи в прод?](#q32-чек-лист-safety-review-перед-запуском-llm-фичи-в-прод)

## Q1. (!) Какие категории рисков у LLM в production?

LLM-приложение сохраняет обычные риски API и хранилищ. Дополнительно оно может принять текст из недоверенного источника за инструкцию, выдать неподкреплённый ответ, раскрыть данные или предложить опасный вызов инструмента. Риски предвзятости, вредного контента и чрезмерного доверия пользователя также зависят от назначения продукта.

Классические меры AppSec остаются необходимыми: авторизация, экранирование вывода, параметризованные запросы и контроль ресурсов. Для модели добавляют проверку источников, ответа и полномочий на действия. Разные риски могут сочетаться: injection в документе способен привести к утечке через tool.

Источник: [OWASP Top 10 for LLM Applications](https://genai.owasp.org/llm-top-10/).

## Q2. (!) Что входит в OWASP Top 10 for LLM 2025?

**OWASP Top 10 for LLM Applications 2025** — таксономия рисков для обсуждения угроз и мер защиты. Это рекомендация, а не закон и не сертификат безопасности. Список относится именно к редакции 2025; при проектировании новой системы нужно проверять более поздние публикации OWASP.

| Код | Риск |
|---|---|
| LLM01 | Prompt Injection |
| LLM02 | Sensitive Information Disclosure |
| LLM03 | Supply Chain |
| LLM04 | Data and Model Poisoning |
| LLM05 | Improper Output Handling |
| LLM06 | Excessive Agency |
| LLM07 | System Prompt Leakage |
| LLM08 | Vector and Embedding Weaknesses |
| LLM09 | Misinformation |
| LLM10 | Unbounded Consumption |

Например, LLM05 требует считать ответ модели недоверенным вводом: безопасно рендерить HTML и проверять данные перед передачей другим компонентам. LLM06 требует ограничивать права tools независимо от решения модели. [Первичный список OWASP](https://genai.owasp.org/llm-top-10/).

## Q3. Threat model для LLM-приложения?

Threat model начинается с данных и полномочий: кто управляет запросом пользователя, документами RAG, результатом поиска, системными инструкциями, инструментами и каналом вывода. Для каждого перехода отмечают владельца, доверие, доступ к данным и возможный побочный эффект.

Модель может неверно принять инструкцию внутри данных за команду. Роли сообщений и маркировка помогают, но границу доверия закрепляет приложение: оно проверяет права на инструмент, его аргументы и результат. Внутренний документ тоже может содержать текст атакующего после импорта или компрометации.

Источник: [OWASP Prompt Injection](https://genai.owasp.org/glossary/prompt-injection/).

## Q4. (!) Что такое prompt injection? Direct vs indirect?

**Prompt injection** пытается изменить поведение модели текстом меньшего доверия. При **direct injection** атакующий размещает текст в запросе пользователя. При **indirect injection** он попадает в документ, страницу, письмо или результат инструмента, который читает ассистент. Важен источник текста, а не язык или длина сообщения.

Пример indirect-атаки: ассистенту поручено кратко изложить страницу, но страница содержит команду вызвать другой инструмент. Приложение должно сохранить исходную задачу и проверить любое действие отдельно. Роль `system` и фраза «не слушай документ» уменьшают риск, но не делают документ доверенным.

Источники: [OWASP](https://genai.owasp.org/glossary/prompt-injection/), [Microsoft](https://learn.microsoft.com/en-us/security/zero-trust/sfi/defend-indirect-prompt-injection).

## Q5. (!) Защита от prompt injection: какие слои?

Защиту строят вокруг границ доверия. Приложение отделяет инструкции от внешних данных, помечает источник, проверяет подозрительный ввод и ответ, а перед действием повторно проверяет права и аргументы. Для операций с высокой ценой ошибки действует правило подтверждения, установленное пользователем или организацией.

Классификатор может пропустить новую атаку, а модель — проигнорировать маркировку. Поэтому их используют как дополнительные барьеры. Основное ограничение последствий находится у исполнителя инструмента: недоверенный документ не может получить право отправить письмо или прочитать чужие данные.

Источник: [Microsoft, defense against indirect prompt injection](https://learn.microsoft.com/en-us/security/zero-trust/sfi/defend-indirect-prompt-injection).

## Q6. Что такое spotlighting и data marking?

**Spotlighting** помечает сторонний документ как данные меньшего доверия. Исследование Microsoft рассматривает delimiting, data marking и encoding. В текущем продукте Prompt Shields Spotlighting документ форматируется специальным образом и кодируется base64. Это не то же самое, что шифрование или разрешение на действие.

Маркировка может повысить число токенов и ухудшить работу с документом. Её эффективность проверяют на своей модели и задаче; проверка tools и результата остаётся нужна.

Источники: [Microsoft Prompt Shields](https://learn.microsoft.com/en-us/azure/foundry/openai/concepts/content-filter-prompt-shields), [защита от indirect injection](https://learn.microsoft.com/en-us/security/zero-trust/sfi/defend-indirect-prompt-injection).

## Q7. (!) Jailbreaking — основные техники?

**Jailbreak** добивается ответа вопреки ограничениям безопасности модели. **Prompt injection** добивается отклонения поведения приложения через недоверенный текст. Один запрос может подпадать под оба описания, поэтому на практике оценивают цель атакующего, канал и последствия.

Техники включают roleplay, гипотетические формулировки, многоходовый диалог, маскировку текста, many-shot примеры и автоматический подбор суффиксов. Проверки на входе и выходе снижают риск, но не гарантируют блокировку. Для системы с tools решающим остаётся независимый контроль действий у исполнителя.

Источники: [OWASP Prompt Injection](https://genai.owasp.org/glossary/prompt-injection/), [Anthropic many-shot](https://www.anthropic.com/research/many-shot-jailbreaking).

## Q8. Как работает many-shot jailbreaking (Anthropic 2024)?

В **many-shot jailbreaking** атакующий помещает в длинный контекст множество примеров запрещённого поведения, а затем задаёт целевой запрос. Модель может продолжить показанный шаблон. Исследование Anthropic показало зависимость эффекта от числа примеров и модели; универсального порога «256 примеров» нет.

Простое сокращение контекста может испортить законные задачи и не доказывает защиту. Проверяют длинные сценарии на выбранной модели, распознают подозрительные серии и сохраняют контроль итогового ответа и действий.

Источник: [Anthropic, Many-shot jailbreaking](https://www.anthropic.com/research/many-shot-jailbreaking).

## Q9. Что такое автоматические adversarial-суффиксы (GCG / AutoDAN)?

**GCG** использует градиентный поиск на доступной модели для подбора adversarial-суффикса. Работа показывает возможность переноса некоторых суффиксов на другие модели. **AutoDAN** исследует более читаемые варианты. Ни перенос, ни успех не гарантированы для конкретной версии модели.

Фильтр необычных токенов может находить часть суффиксов, но и пропускать читаемые атаки. Поэтому устойчивость измеряют на наборе атак, результат проверяют отдельно, а полномочия tools ограничивают независимо от текста промпта.

Источники: [GCG](https://arxiv.org/abs/2307.15043), [AutoDAN](https://arxiv.org/abs/2310.15140).

## Q10. System prompt leak — как защищаться?

**System prompt leakage** — раскрытие части скрытых инструкций через ответ модели. Запрет повторять промпт и фильтр дословного совпадения могут уменьшить вероятность утечки, но не дают гарантии конфиденциальности.

Секреты хранят вне промпта. Доступ к данным и инструментам проверяет код приложения. Даже если текст инструкции раскрыт, это не должно выдавать пользователю новые права или чужие данные. В редакции OWASP 2025 риск выделен как LLM07.

Источник: [OWASP LLM07](https://genai.owasp.org/llmrisk/llm072025-system-prompt-leakage/).

## Q11. (!) Llama Guard — что это и какие категории?

**Llama Guard** — семейство моделей Meta, которые классифицируют запрос или ответ по политике безопасности. Они возвращают verdict и при необходимости категорию; это дополнительная модель, а не сетевой firewall и не замена основной модели.

Таксономия зависит от версии. Например, карточка **Llama Guard 3 8B** перечисляет 14 категорий `S1`–`S14`, включая privacy, hate, self-harm и code interpreter abuse. Набор языков и качество тоже зависят от версии: перед запуском проверяют модель на текстах своего домена и выбирают пороги с учётом ложных срабатываний.

Источник: [карточка Llama Guard 3 8B от Meta](https://huggingface.co/meta-llama/Llama-Guard-3-8B).

## Q12. (!) NeMo Guardrails — архитектура и Colang DSL?

**NeMo Guardrails** — библиотека NVIDIA для программируемых проверок вокруг LLM. В текущей документации описаны input, retrieval, dialog, execution и output rails. Они работают на разных этапах: например, retrieval rail проверяет найденный фрагмент, а execution rail — действие перед исполнением.

Конфигурация задаётся YAML, а диалоговые потоки можно описывать на **Colang**. Доступность функций зависит от версии Colang и выбранного runtime, поэтому фрагменты конфигурации берут из документации установленного выпуска. Дополнительная модельная проверка увеличивает время и стоимость вызова.

Источник: [NVIDIA NeMo Guardrails](https://docs.nvidia.com/nemo/guardrails/latest/index.html).

## Q13. Как устроены validators в Guardrails AI?

**Guardrails AI** валидирует входные или выходные данные по выбранным правилам. При провале валидатора проверенная версия SDK поддерживает политики обработки, среди которых exception, fix, filter и reask. Это не гарантирует, что исправленный ответ соответствует фактам.

Политики `on_fail` и API меняются между выпусками, поэтому при внедрении сверяют документацию с установленной версией. Проверка структуры, PII и предметных ограничений выполняется разными валидаторами; они не заменяют авторизацию tools.

Источник: [репозиторий Guardrails AI](https://github.com/guardrails-ai/guardrails).

## Q14. OpenAI Moderation API — категории и Multimodal?

**OpenAI Moderation API** классифицирует поддерживаемые категории вредного контента в запросах и ответах. Документация на 2026-11-09 указывает, что `omni-moderation-latest` принимает текст и изображения: `hate` и `harassment` поддерживают только текст, а `violence`, `sexual` и `self-harm` — также изображения. Неподдерживаемая категория для image-only входа может возвращать нулевой score. Сам endpoint бесплатен; квоты и условия использования нужно сверять отдельно.

Moderation не является специализированной защитой от prompt injection, проверкой фактов или прав на tool. Verdict нужно интерпретировать по политике продукта, включая ложные срабатывания и обработку ошибок API.

Источник: [OpenAI Moderation](https://developers.openai.com/api/docs/guides/moderation).

## Q15. Сравнение Llama Guard vs NeMo vs Guardrails AI?

Инструменты решают разные задачи. **Llama Guard** и **OpenAI Moderation** классифицируют контент, но различаются размещением, версиями и таксономиями. **NeMo Guardrails** организует проверки на этапах диалога, retrieval и действий. **Guardrails AI** запускает валидаторы для данных и результатов.

Стек выбирают после threat model: какие источники и tools есть, где можно обрабатывать данные, сколько времени допустимо тратить на проверку и как измерять ошибки классификации. Несколько компонентов можно сочетать, но один продукт не покрывает автоматически все риски.

Источники: [Meta](https://huggingface.co/meta-llama/Llama-Guard-3-8B), [NVIDIA](https://docs.nvidia.com/nemo/guardrails/latest/index.html), [Guardrails AI](https://github.com/guardrails-ai/guardrails), [OpenAI](https://developers.openai.com/api/docs/guides/moderation).

## Q16. Constitutional AI (Anthropic) — как работает?

**Constitutional AI** — исследованный Anthropic метод обучения, а не runtime-фильтр ответа. В первой фазе модель критикует и пересматривает свои ответы по текстовым принципам. В следующей фазе модельные предпочтения используются для обучения через AI feedback.

Этот метод может улучшить базовое поведение модели, но не проверяет права приложения и не подтверждает конкретный вызов tool. Runtime-guardrails и контролирующий код остаются отдельными решениями.

Источник: [Anthropic, Constitutional AI](https://www.anthropic.com/research/constitutional-ai-harmlessness-from-ai-feedback).

## Q17. (!) Hallucination — типы и причины?

**Hallucination** — утверждение, не подтверждённое доступными данными, противоречащее источнику или неверное в реальном мире. В исследовании конкретной задачи полезно различать ошибку относительно предоставленного документа и ошибку относительно внешнего факта. Не всякий неверный ответ звучит уверенно.

Причины различны: модель не располагает нужным фактом, поиск вернул плохой документ, данные устарели или при генерации сделан неверный вывод. `temperature=0` снижает вариативность, но не делает ответ истинным. Защита зависит от задачи: качественные источники, сверка утверждений, отказ при недостатке данных и измерение ошибок на эталоне.

## Q18. (!) RAG как способ снизить hallucinations?

**RAG** получает релевантные документы во время ответа, не изменяя веса модели. Такая опора на источники может снизить число выдумок и позволяет показывать происхождение фактов. Результат зависит от качества retrieval, свежести документов и того, правильно ли модель использовала найденное.

Документ может быть нерелевантным, устаревшим или отравленным. Цитата сама по себе не доказывает, что утверждение из неё следует. Поэтому проверяют права доступа к фрагментам, релевантность, соответствие ответа источнику и умение отказаться при отсутствии основания.

## Q19. Как снижают hallucinations через Chain-of-Verification (CoVe), self-consistency и LLM-as-judge?

**Chain-of-Verification (CoVe)** сначала строит черновой ответ, затем отдельные проверочные вопросы, отвечает на них и пересматривает итог. **Self-consistency** сравнивает несколько независимо полученных решений. **LLM-as-judge** оценивает результат по заданной рубрике; это метод оценки, а не независимый источник истины.

Методы способны улучшить качество на некоторых задачах, но могут разделять одну ошибку. Для фактов важны первичные источники и эталонные кейсы; судью калибруют человеком. Дополнительные генерации увеличивают стоимость и задержку.

Источники: [CoVe](https://arxiv.org/abs/2309.11495), [Self-Consistency](https://arxiv.org/abs/2203.11171).

## Q20. Как оценить уверенность модели: logprobs vs verbalized uncertainty?

**Logprobs** отражают вероятность выбранных токенов в данном контексте. **Verbalized uncertainty** — уверенность, которую модель сообщает словами. Оба сигнала можно исследовать, но ни один сам по себе не равен вероятности того, что факт верен.

Модель бывает уверенно неправа, а низкая вероятность токена может быть вызвана редким именем при правильном ответе. Если требуется порог для маршрутизации к человеку или отказа, его калибруют на размеченных примерах конкретной задачи и перепроверяют после смены модели или промпта.

## Q21. (!) PII detection и redaction — Presidio vs LLM-based?

**Microsoft Presidio** содержит analyzer для поиска PII и anonymizer для обработки найденных сущностей. Распознаватели используют шаблоны, контекст и NLP; при необходимости добавляют правила своего домена. LLM может помочь со сложными случаями, но передача ей текста с PII сама требует оценки прав и договора.

Проверки проводят там, где данные входят в систему, уходят к внешнему провайдеру, возвращаются пользователю и попадают в журналы. Ни Presidio, ни LLM не находят все сущности без ошибок: измеряют пропуски и ложные срабатывания на своих данных. Цель обработки определяется юридическими и продуктовыми требованиями, а не универсальным правилом «никогда не передавать PII».

Источник: [Microsoft Presidio](https://microsoft.github.io/presidio/).

## Q22. Как валидировать структуру output (JSON schema, Pydantic)?

Ответ модели — недоверенный ввод для приложения. **JSON mode** помогает получить синтаксически корректный JSON, но не проверяет нужные поля. **Structured Outputs** в поддерживаемых моделях и схемах ограничивает форму. **Pydantic** или **Zod** проверяет объект в коде приложения; при ошибке можно ограниченно повторить запрос или завершить его понятной ошибкой.

Схема не проверяет смысл: сумма может иметь правильный тип и неправильное значение. Отказ модели может прийти вне схемы, а часть конструкций JSON Schema не поддерживается. Перед действием дополнительно проверяют предметные ограничения и права.

Источник: [OpenAI Structured Outputs](https://developers.openai.com/api/docs/guides/structured-outputs).

## Q23. Что требует compliance: GDPR, HIPAA, EU AI Act?

Применимость законов зависит от того, какие данные обрабатываются, где находятся участники и какую роль выполняет продукт. **GDPR** требует определить основание обработки персональных данных, ограничить их объём и учитывать права людей, включая право на удаление с предусмотренными исключениями. **HIPAA** относится к определённым участникам в США и PHI; облачный обработчик ePHI для covered entity или business associate обычно требует соответствующий BAA.

**EU AI Act** вводит разные обязанности по роли и риску системы, с поэтапными сроками. На 2026-11-09 официальный сервис ЕС указывает применение требований к GPAI с августа 2025 года, требований прозрачности с августа 2026 года, правил для high-risk систем Annex III с декабря 2027 года и Annex I с августа 2028 года. Для конкретного продукта сроки и применимость проверяют отдельно.

Источники: [Еврокомиссия о правах GDPR](https://commission.europa.eu/law/law-topic/data-protection/information-individuals_en), [HHS о BAA для облака](https://www.hhs.gov/hipaa/for-professionals/faq/may-a-hipaa-covered-entity-or-business-associate-use-cloud-service-to-store-or-process-ephi/index.html), [EU AI Act Service Desk](https://ai-act-service-desk.ec.europa.eu/en/ai-act/timeline/timeline-implementation-eu-ai-act).

## Q24. (!) Как обезопасить tool use: least privilege, sandbox, confirmation?

Модель может предложить вызов инструмента, но исполнение остаётся решением приложения. Для каждого tool задают минимальные права, проверяют аргументы и принадлежность данных пользователю. Недоверенный код выполняют изолированно, с ограничением файловой системы и сети. Результат инструмента снова считается внешними данными.

Подтверждение человека ставят там, где его требуют полномочия и правила операции: например, перед отправкой сообщения от имени пользователя или удалением данных. Без ответа человека требующее одобрения действие не выполняется. Чтение доступной информации не требует того же процесса автоматически.

Источник: [OWASP Excessive Agency](https://genai.owasp.org/llmrisk/llm062025-excessive-agency/).

## Q25. Agent loops — как ограничивать?

Цикл агента может повторять запросы к модели и инструментам, даже если задача не решается. Приложение задаёт отдельные лимиты шагов, времени, токенов, стоимости и частоты вызовов. При достижении порога агент останавливается с частичным результатом или запросом дальнейшего решения.

Таймаут не защищает от быстрого дорогого вызова, а лимит шагов — от одного долгого запроса. Учёт затрат после ответа не создаёт точного предоплатного потолка, поэтому перед дорогим шагом оставляют запас. Пороги проверяют на реальных сценариях задачи.

Источник: [OWASP Unbounded Consumption](https://genai.owasp.org/llmrisk/llm102025-unbounded-consumption/).

## Q26. Indirect prompt injection в RAG / agent?

**Indirect prompt injection** возникает, когда инструкция атакующего помещена в документ RAG, веб-страницу или ответ инструмента. Агент читает её для задачи пользователя и может принять за новое указание. Внутренний документ не гарантирует безопасность: он мог содержать чужой комментарий или импортированный текст.

При ingestion сохраняют происхождение и права на документ. При retrieval помечают текст как данные меньшего доверия. Перед вызовом tool приложение независимо проверяет разрешения и аргументы; мониторинг и проверка результата помогают обнаружить попытку обхода. Более точный embedding-поиск не удаляет вредную инструкцию из корректно найденного фрагмента.

Источники: [OWASP Prompt Injection](https://genai.owasp.org/glossary/prompt-injection/), [Microsoft](https://learn.microsoft.com/en-us/security/zero-trust/sfi/defend-indirect-prompt-injection).

## Q27. (!) Red-teaming и PyRIT?

**Red-teaming** проверяет, как систему можно заставить нарушить её правила и границы доступа. Тестируют модель и всё приложение: внешние документы, tools, долгие диалоги и реакцию на отказ. После изменения модели, промпта или инструмента набор атак пересматривают.

**PyRIT** от Microsoft предоставляет компоненты для целей, преобразований запроса, сценариев атаки и оценщиков результата. Автоматические прогоны находят часть ошибок и дают регрессионный набор; человеческое исследование и проверка спорных verdict остаются нужны.

Источник: [PyRIT](https://github.com/microsoft/PyRIT).

## Q28. Какие бенчмарки оценивают jailbreak-устойчивость: HarmBench, JailbreakBench, AdvBench?

**AdvBench**, **JailbreakBench** и **HarmBench** предлагают разные наборы задач и процедуры оценки атак. **ASR** (attack success rate) зависит от запросов, метода атаки, защиты и оценщика ответа. HarmBench, например, предоставляет воспроизводимый конвейер генерации тестов, ответов и классификации результата.

Балл одного бенчмарка полезен для сравнения при одинаковых условиях, но не доказывает безопасность будущих атак, RAG-корпуса и tools конкретного продукта. Помимо общих наборов нужны собственные сценарии и ручная проверка ошибок судьи.

Источники: [AdvBench/GCG](https://arxiv.org/abs/2307.15043), [JailbreakBench](https://jailbreakbench.github.io/), [HarmBench](https://github.com/centerforaisafety/HarmBench).

## Q29. Observability для safety: логирование, алерты?

Для разбора safety-событий связывают запрос, результат проверок, предложенные и исполненные вызовы tools, отказ и итоговый ответ. Логируют минимально необходимые сведения и сохраняют метки версии модели, промпта и политики. Сырые тексты и PII хранят только при обоснованной необходимости и с ограничением доступа и срока.

Алерты настраивают по измеренным всплескам опасных действий, отказов, ошибок проверок и расхода ресурсов. Порог зависит от нормальной нагрузки и частоты ложных срабатываний. Журнал нужен, чтобы понять цепочку действий, а не только сосчитать общий процент unsafe.

Источники: [NIST AI RMF](https://www.nist.gov/itl/ai-risk-management-framework), [OWASP Top 10](https://genai.owasp.org/llm-top-10/).

## Q30. (!) Какие случаи показывают риски: Bing Sydney, Air Canada, Chevrolet, Slack AI и DeepSeek-R1?

Эти примеры показывают разные границы контроля, и статус их свидетельств различается:

- **Bing Sydney:** публично демонстрировалось раскрытие внутренних инструкций. Урок — секреты и авторизацию не держат только в промпте.
- **Air Canada:** трибунал Британской Колумбии в деле *Moffatt v. Air Canada* рассмотрел ошибочный ответ чат-бота о скидке на перелёт и обязал компанию возместить ущерб. Это решение по конкретному делу, а не универсальное правило для любого чат-бота.
- **Chevrolet:** публичная демонстрация показала, как чат-бот дилера согласился на неуместное предложение о цене. Она не доказывает заключение сделки за эту цену.
- **Slack AI:** исследователи показали возможность indirect prompt injection через сообщения канала; отдельно нужно отличать демонстрацию атаки от доказанной массовой утечки.
- **DeepSeek-R1:** Cisco/Robust Intelligence опубликовали результат red-team теста на 50 выбранных заданиях HarmBench. Это результат испытания конкретной методикой, а не подтверждённый production-инцидент.

Источники: [решение Moffatt v. Air Canada](https://www.canlii.org/en/bc/bccrt/doc/2024/2024bccrt149/2024bccrt149.html), [исследование Cisco](https://blogs.cisco.com/security/evaluating-security-risk-in-deepseek-and-other-frontier-reasoning-models), [документация Slack о prompt injection](https://docs.slack.dev/concepts/security/).

## Q31. Data poisoning при fine-tuning?

**Data poisoning** — внедрение враждебных примеров в данные обучения или дообучения. Цель может состоять в устойчивом неверном ответе, нежелательном смещении или поведении по редкому триггеру. Пользовательские данные и сторонние датасеты не становятся безопасными только из-за наличия договора или доступа по ролям.

Для fine-tuning сохраняют происхождение данных, контролируют права на их изменение, ищут аномалии и сравнивают модель до и после обучения. Обычный eval может пропустить поведение, которое возникает только на редком триггере; его тестируют отдельно. Не всякая ошибка ответа означает poisoning.

Источник: [OWASP Data and Model Poisoning](https://genai.owasp.org/llmrisk/llm042025-data-and-model-poisoning/).

## Q32. Чек-лист safety review перед запуском LLM-фичи в прод?

Перед запуском связывают threat model с проверками конкретной функции: кто может влиять на запрос и внешние документы, что модель видит, куда уходит ответ, какие tools доступны и кто подтверждает действия. Проверяют авторизацию, PII, обработку ошибок и отказов, лимиты ресурсов, журналирование и реакцию на инцидент.

Тесты включают обычные задачи, ошибочные данные и атаки на границы доверия. Для read-only чата без tools нет смысла требовать песочницу исполнения кода; для агента с записью в системы она может быть необходима. Review повторяют после существенного изменения модели, промпта, источников или полномочий.

Источники: [NIST AI RMF](https://www.nist.gov/itl/ai-risk-management-framework), [OWASP Top 10](https://genai.owasp.org/llm-top-10/).

---

## See also

- [Prompt Engineering](prompt-engineering-interview.md)
- [AI Agents](ai-agents-interview.md)
- [LLM Basics](llm-basics-interview.md)
- [RAG](rag-interview.md)
- [MCP (Model Context Protocol)](mcp-interview.md)
- [Function Calling](function-calling-interview.md)
- [OWASP Top 10](../security/owasp-top10-interview.md)
- [Application Security](../security/application-security-interview.md)
- [System Design Interview](../system-design/system-design-interview.md)
