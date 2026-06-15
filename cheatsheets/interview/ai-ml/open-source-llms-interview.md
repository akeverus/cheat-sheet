---
title: "Вопросы на собеседовании: Open-Source LLM Ecosystem 2025-2026"
description: "Open-source LLMs: Llama 3.x/4, Mistral/Mixtral, Qwen 2.5/3, DeepSeek-V3/R1, Gemma 3, Phi-4; licensing, MoE economics, fine-tune-ability, benchmarks."
tags:
  - interview
  - ai-ml
  - open-source-llms
type: "interview"
difficulty: "intermediate"
aliases:
  - "Open Source LLM interview"
  - "Llama Mistral Qwen DeepSeek собеседование"
  - "MoE models open weights"
  - "Open-weight LLM ecosystem"
updated: "2026-05-25"
---
# Вопросы на собеседовании: `Open-Source LLM Ecosystem 2025-2026`

С 2023 года индустрия LLM раскололась на два лагеря: **закрытые frontier-модели** (GPT-4o, Claude, Gemini) и **open-weight ecosystem** — Llama, Mistral, Qwen, DeepSeek, Gemma, Phi. К 2026 разрыв в качестве на популярных бенчмарках почти стёрся: `DeepSeek-V3` соревнуется с `GPT-4o`, `Llama 3.3 70B` догоняет `405B`, а `Qwen 2.5` стал стандартом для self-host в Азии.

На интервью спрашивают: чем `open-source` отличается от `open-weight`, что такое `MoE` и почему `total params ≠ active params`, как читать `Llama Community License`, когда self-host дешевле API, и какие подводные камни у дообучения этих моделей.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Llama 3 Paper (Meta)](https://ai.meta.com/research/publications/the-llama-3-herd-of-models/) — 92-страничный технический отчёт
- [Mistral AI Models Documentation](https://docs.mistral.ai/getting-started/models/) — официальные карточки моделей
- [DeepSeek-V3 Paper (arXiv)](https://arxiv.org/abs/2412.19437) — 671B MoE, заявленная стоимость обучения $5.5M
- [DeepSeek-R1 Paper](https://arxiv.org/abs/2501.12948) — reasoning через GRPO
- [Qwen 2.5 Technical Report](https://arxiv.org/abs/2412.15115) — флагманское семейство Alibaba
- [Gemma 3 Technical Report](https://ai.google.dev/gemma) — открытые веса Google
- [Phi-4 Technical Report (Microsoft)](https://arxiv.org/abs/2412.08905) — упор на качество данных
- [HuggingFace Open LLM Leaderboard](https://huggingface.co/spaces/HuggingFaceH4/open_llm_leaderboard) — стандартный хаб бенчмарков
- [LMSYS Chatbot Arena](https://chat.lmsys.org/) — ранжирование по предпочтениям людей
- [Llama 3 Community License (текст)](https://www.llama.com/llama3/license/) — пункт про 700M DAU

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**

- [Q1. В чём разница между `open-source`, `open-weight` и `open-source-friendly` моделью?](#q1-в-чём-разница-между-open-source-open-weight-и-open-source-friendly-моделью)
- [Q2. Почему `Llama` и `Gemma` называют open-weight, а не open-source?](#q2-почему-llama-и-gemma-называют-open-weight-а-не-open-source)
- [Q3. Что такое `model card` и почему он обязателен на HuggingFace? `!`](#q3-что-такое-model-card-и-почему-он-обязателен-на-huggingface-)

**Llama family (Meta)**

- [Q4. Какие версии `Llama` существуют и чем они отличаются по архитектуре и лицензии? `!`](#q4-какие-версии-llama-существуют-и-чем-они-отличаются-по-архитектуре-и-лицензии-)
- [Q5. Что особенного в `Llama 3.3 70B` vs `Llama 3.1 405B`?](#q5-что-особенного-в-llama-33-70b-vs-llama-31-405b)
- [Q6. Что изменилось в `Llama 4` (Scout, Maverick, Behemoth)? `!`](#q6-что-изменилось-в-llama-4-scout-maverick-behemoth-)
- [Q7. Что означает clause «700M monthly active users» в Llama Community License?](#q7-что-означает-clause-700m-monthly-active-users-в-llama-community-license)

**Mistral / Mixtral**

- [Q8. Чем `Mistral 7B` отличался от `Llama 2 7B` на момент выхода?](#q8-чем-mistral-7b-отличался-от-llama-2-7b-на-момент-выхода)
- [Q9. Как устроен `Mixtral 8x7B` — почему это не 56B параметров? `!`](#q9-как-устроен-mixtral-8x7b--почему-это-не-56b-параметров-)
- [Q10. Какие модели Mistral остались Apache 2.0, а какие ушли под коммерческую лицензию?](#q10-какие-модели-mistral-остались-apache-20-а-какие-ушли-под-коммерческую-лицензию)

**Qwen family (Alibaba)**

- [Q11. Какие размеры и варианты есть в семействе `Qwen 2.5`? `!`](#q11-какие-размеры-и-варианты-есть-в-семействе-qwen-25-)
- [Q12. Что такое `QwQ-32B` и как он связан с reasoning?](#q12-что-такое-qwq-32b-и-как-он-связан-с-reasoning)
- [Q13. В чём лицензионная особенность `Qwen 72B`?](#q13-в-чём-лицензионная-особенность-qwen-72b)

**DeepSeek family**

- [Q14. Что такое `DeepSeek-V3` и почему его training cost ($5.5M) шокировал индустрию? `!`](#q14-что-такое-deepseek-v3-и-почему-его-training-cost-55m-шокировал-индустрию-)
- [Q15. Чем `DeepSeek-R1` отличается от `DeepSeek-V3`?](#q15-чем-deepseek-r1-отличается-от-deepseek-v3)
- [Q16. Что такое `DeepSeek-R1-Distill` и почему дистиллированный 70B доступнее R1?](#q16-что-такое-deepseek-r1-distill-и-почему-дистиллированный-70b-доступнее-r1)

**Gemma / Phi**

- [Q17. Что входит в семейство `Gemma 3` от Google? `!`](#q17-что-входит-в-семейство-gemma-3-от-google-)
- [Q18. Что такое `PaliGemma`, `CodeGemma`, `ShieldGemma`?](#q18-что-такое-paligemma-codegemma-shieldgemma)
- [Q19. Чем отличается `Phi-4` от `Phi-3.5-MoE`?](#q19-чем-отличается-phi-4-от-phi-35-moe)

**Другие open-weight модели**

- [Q20. Что такое `OLMo` и почему его называют «fully open»? `!`](#q20-что-такое-olmo-и-почему-его-называют-fully-open-)
- [Q21. Какие ещё open-weight семейства стоит знать (Yi, DBRX, Command R+, Granite)?](#q21-какие-ещё-open-weight-семейства-стоит-знать-yi-dbrx-command-r-granite)

**MoE economics**

- [Q22. Почему в MoE-моделях `total params` важен для VRAM, а `active params` — для compute? `!`](#q22-почему-в-moe-моделях-total-params-важен-для-vram-а-active-params--для-compute-)
- [Q23. Что такое `routing overhead` в MoE и почему MoE не всегда дешевле dense?](#q23-что-такое-routing-overhead-в-moe-и-почему-moe-не-всегда-дешевле-dense)

**Self-host vs API trade-offs**

- [Q24. Когда self-host open-source выгоднее API? Когда — наоборот? `!`](#q24-когда-self-host-open-source-выгоднее-api-когда--наоборот-)
- [Q25. Какой production-стек для self-host open-source LLM на vLLM/TensorRT-LLM?](#q25-какой-production-стек-для-self-host-open-source-llm-на-vllmtensorrt-llm)
- [Q26. Что такое `GGUF`, `AWQ`, `GPTQ` и почему их выкладывает community, а не авторы?](#q26-что-такое-gguf-awq-gptq-и-почему-их-выкладывает-community-а-не-авторы)

**Licensing deep dive**

- [Q27. Сравни `Apache 2.0`, `MIT`, `Llama Community`, `Gemma Terms`, `Mistral Research License`. `!`](#q27-сравни-apache-20-mit-llama-community-gemma-terms-mistral-research-license-)
- [Q28. Что такое `Open RAIL License` и где применяется?](#q28-что-такое-open-rail-license-и-где-применяется)
- [Q29. Какие риски security при загрузке моделей с HuggingFace? `!`](#q29-какие-риски-security-при-загрузке-моделей-с-huggingface-)

**Fine-tuning, benchmarks, outlook**

- [Q30. Какие open-source модели реально fine-tune-нуть на consumer GPU (4090 24GB)?](#q30-какие-open-source-модели-реально-fine-tune-нуть-на-consumer-gpu-4090-24gb)
- [Q31. Какие подводные камни при оценке open-source моделей по бенчмаркам?](#q31-какие-подводные-камни-при-оценке-open-source-моделей-по-бенчмаркам)
- [Q32. Decision tree: как выбрать open-source модель под задачу? `!`](#q32-decision-tree-как-выбрать-open-source-модель-под-задачу-)
- [Q33. Какие code-specific open-source модели есть и какая под что?](#q33-какие-code-specific-open-source-модели-есть-и-какая-под-что)
- [Q34. Outlook 2026: где open-source догоняет frontier, а где отстаёт?](#q34-outlook-2026-где-open-source-догоняет-frontier-а-где-отстаёт)

## Timeline open-source LLM releases 2023-2026

Хронология ключевых релизов по годам:

**2023**

- Feb — Llama 1 (research-only)
- Jul — Llama 2 (commercial OK)
- Sep — Mistral 7B (Apache 2.0)
- Dec — Mixtral 8x7B (MoE Apache 2.0)

**2024**

- Feb — Gemma 1 7B
- Apr — Llama 3 8B/70B; Mixtral 8x22B; Phi-3 family
- May — DeepSeek-V2 (236B MoE)
- Jun — Gemma 2 9B/27B
- Jul — Llama 3.1 405B + 128K context; Mistral Large 2 (research license)
- Sep — Llama 3.2 vision + edge 1B/3B; Qwen 2.5 family
- Dec — Llama 3.3 70B; DeepSeek-V3 671B MoE; Phi-4 14B

**2025**

- Jan — DeepSeek-R1 (reasoning, MIT); Qwen 2.5-Max
- Mar — Gemma 3 (multimodal)
- Apr — Llama 4 Scout/Maverick MoE
- H2 — Qwen 3 family

**2026**

- Q1-Q2 — Llama 4 Behemoth (training); continued frontier closing

---

## Q1. В чём разница между `open-source`, `open-weight` и `open-source-friendly` моделью?

Три термина описывают **три разных уровня открытости** — путать их на интервью нельзя, потому что от уровня зависит, что вы юридически и технически можете с моделью сделать.

**Open-source LLM** — открыто **всё**: веса, код обучения, данные, training recipe, под Apache 2.0 / MIT. Можно полностью воспроизвести pre-training с нуля. Примеры: `OLMo`, `BLOOM`.

**Open-weight LLM** — открыты только **веса** и (обычно) inference-код; данные, training-код и recipe закрыты или описаны в paper лишь в общих чертах. Веса можно скачать, дообучить, развернуть, но не воспроизвести. Примеры: `Llama 3`, `Mistral`, `Qwen`, `Gemma`, `DeepSeek`. Именно это **большинство** того, что в индустрии по инерции называют «open-source», хотя строго это open-weight.

**Open-source-friendly** — закрытая модель с либеральным API (открытый SDK, без vendor lock-in на формат), но веса недоступны. К open-source не относится вовсе — термин маркетинговый.

```text
                    весА    КОД ОБУЧЕНИЯ   ДАННЫЕ   recipe
Open-source         OPEN    OPEN           OPEN     OPEN     OLMo, BLOOM
Open-weight         OPEN    closed         closed   partial  Llama, Mistral
Open-source-friendly closed closed         closed   doc'd    Anthropic API
```

**Практический смысл.** С **open-weight** можно развернуть у себя, дообучить, проверить модель экспертно (forensic-анализ). С **open-source** к этому добавляется возможность воспроизвести pre-training с нуля — но это требует вычислений на $1M-$60M, поэтому ценно в основном для исследований и compliance, а не для прода.

## Q2. Почему `Llama` и `Gemma` называют open-weight, а не open-source?

Коротко: открыты веса, но не всё остальное, и лицензия ограничивает использование — а это нарушает классическое определение open-source. Разбор по пунктам:

1. **Веса доступны под лицензией**, но это не одобренная OSI open-source лицензия.
2. **Обучающие данные закрыты**: Meta опубликовала состав корпуса Llama 3 только в общих терминах («15T токенов, web crawl + код + математика»), без самого датасета.
3. **Код обучения частично закрыт**: в paper — высокоуровневые описания, но не готовый repo для воспроизведения.
4. **Лицензия ограничивает использование**: для Llama — пункт про 700M MAU и acceptable use policy; для Gemma — `Gemma Terms of Use` запрещают ряд сценариев.

Формальный критерий: OSI (Open Source Initiative) в 2024 опубликовал `OSAID` (Open Source AI Definition). Под него `Llama` и `Gemma` **не подпадают** (закрытые данные + ограничения на использование), а `OLMo` подпадает. Поэтому корректный термин — **open-weight**.

**Когда это важно.** «Open-source-like» допустимо в неформальной речи, но в юридическом / compliance-разговоре путать нельзя: от точного класса лицензии зависит, можно ли модель использовать в продукте.

## Q3. Что такое `model card` и почему он обязателен на HuggingFace? `!`

`Model card` — структурированный «паспорт» модели в `README.md` репозитория HuggingFace: назначение (intended use), сводка по обучающим данным, результаты оценки, ограничения, лицензия, цитирование. Концепцию ввёл paper «Model Cards for Model Reporting» (Mitchell et al., 2018); сейчас это индустриальный стандарт.

**Зачем обязателен.** Без model card модель — «чёрный ящик неизвестного происхождения», который нельзя ни легально использовать, ни воспроизвести, ни проверить:
- **Ясность лицензии** — без явной лицензии модель нельзя законно применять в продакшене.
- **Воспроизводимость** — пользователь должен знать, на чём обучали.
- **Раскрытие bias / safety** — известные failure modes.
- **Compliance** (EU AI Act, NIST AI RMF) — регуляторы прямо требуют такую документацию.

HuggingFace с 2024 в Spaces и enterprise-тарифах **блокирует** загрузку моделей без model card. На интервью тема всплывает в контексте AI governance и закупок (procurement) — см. `ai-compliance-governance-interview.md`.

Пример минимального model card:

```markdown
---
license: apache-2.0
language: en
tags: [llama, instruct]
base_model: meta-llama/Llama-3.1-8B
---
# Model name
## Intended use
Chat assistant for general-purpose Q&A...
## Training data
Fine-tuned on 50k samples of OASST...
## Evaluation
MMLU: 65.2 | HumanEval: 42.1
## Limitations
- Hallucinates on factual questions.
- English-primary, weak on Russian.
```

## Q4. Какие версии `Llama` существуют и чем они отличаются по архитектуре и лицензии? `!`

| Версия | Дата | Размеры | Context | Лицензия | Особенности |
|---|---|---|---|---|---|
| `Llama 1` | фев 2023 | 7B / 13B / 33B / 65B | 2K | только research | Утечка на 4chan запустила экосистему |
| `Llama 2` | июл 2023 | 7B / 13B / 70B | 4K | Llama 2 Community (коммерция OK с ограничением >700M DAU) | RLHF, chat-вариант |
| `Code Llama` | авг 2023 | 7B/13B/34B/70B | 16K-100K | лицензия Llama 2 | На основе Llama 2 + код |
| `Llama 3` | апр 2024 | 8B / 70B | 8K | Llama 3 Community License | 15T токенов обучения, GQA |
| `Llama 3.1` | июл 2024 | 8B / 70B / 405B | 128K | Llama 3.1 Community | Длинный контекст, мультиязычность |
| `Llama 3.2` | сен 2024 | 1B / 3B / 11B / 90B | 128K | Llama 3.2 (vision ограничен в ЕС) | Мультимодальные (11B/90B) + edge (1B/3B) |
| `Llama 3.3` | дек 2024 | 70B (только текст) | 128K | Llama 3.3 Community | По качеству ≈ 3.1 405B |
| `Llama 4` | апр 2025 | Scout 17Bx16 MoE / Maverick 17Bx128 MoE / Behemoth (в обучении) | 10M+ | Llama 4 Community | Архитектура sparse MoE |

`Llama` — это семья из шести поколений (2023-2025), которые шли по двум осям: рост контекста (2K → 128K → 10M) и смена архитектуры (dense → MoE). Лицензия при этом почти не менялась — Community License с пунктом про 700M MAU.

**Что важно для архитектурного вопроса.** Все Llama 2/3/4 — decoder-only Transformer с `RoPE`, `RMSNorm`, `SwiGLU`. Две ключевые вехи:
- **Llama 3** распространил `Grouped Query Attention` (GQA) на все размеры (в Llama 2 он был только в 70B) — это сократило KV-cache и удешевило inference у младших моделей.
- **Llama 4** — первое поколение на sparse MoE; dense-версии Llama на этом закончились.

## Q5. Что особенного в `Llama 3.3 70B` vs `Llama 3.1 405B`?

Главная идея: `Llama 3.3 70B` (дек 2024) при **тех же 70B параметрах**, что у Llama 3.1 70B, выдаёт качество **на уровне 405B** на reasoning / math-бенчмарках. То есть прирост достигнут не размером, а обучением — лучшим post-training, а не новыми параметрами:

- улучшенный recipe `post-training` (RLHF / DPO);
- больше высококачественных SFT-данных;
- заметно сильнее tool use и мультиязычность.

**Почему это сделало 3.3 70B выбором по умолчанию.** 405B при близком качестве куда дороже в эксплуатации: inference 70B разумно крутится на 2× H100 в `fp8`, а 405B требует 8× H100 даже в `fp8`, и стоимость токена выше в 5-6×. Поэтому **70B покрывает большинство production-сценариев**, а 405B берут только когда критична точность «последней мили» на самых сложных задачах.

```text
                     MMLU  HumanEval  GPQA  Cost/1M tokens (self-host estimate)
Llama 3.1 70B        82.0  80.5       46.7  ~$0.30
Llama 3.3 70B        86.0  88.4       50.5  ~$0.30
Llama 3.1 405B       88.6  89.0       50.7  ~$1.50-2.00
```

## Q6. Что изменилось в `Llama 4` (Scout, Maverick, Behemoth)? `!`

`Llama 4` (апрель 2025) — первое поколение Meta на **sparse MoE** (Mixture of Experts) с нативной мультимодальностью (текст + изображения).

| Модель | Архитектура | Active params | Total params | Контекст | Сценарий |
|---|---|---|---|---|---|
| `Llama 4 Scout` | 16 экспертов × 17B | 17B | 109B | 10M токенов (заявлено) | Один H100, длинный контекст |
| `Llama 4 Maverick` | 128 экспертов × 17B | 17B | 400B | 1M+ | Production на нескольких GPU |
| `Llama 4 Behemoth` | ~16 экспертов × 288B | 288B | ~2T | — | Обучение (frontier) |

Четыре изменения относительно Llama 3, и каждое — это сдвиг в том, как модель эксплуатируется:
- **MoE везде**: dense-версии Llama закончились; маршрутизация идёт на уровне токена через обучаемый gate.
- **Нативная мультимодальность**: текст + изображения в одной модели, encoder с early fusion (раньше vision был отдельной моделью).
- **Огромный контекст**: у Scout заявлено 10M токенов — через `interleaved RoPE` плюс специальное pre-training на длинных документах.
- **Active = 17B при total 109B-400B**: вычисления на inference — как у dense-модели 17B (дёшево по compute), но `VRAM` нужен под **все** веса (дорого по памяти). Это и есть фирменный компромисс MoE — см. Q22.

Лицензия — Llama 4 Community License (тот же пункт про 700M MAU + дополнительные ограничения для ЕС из-за Digital Markets Act).

## Q7. Что означает clause «700M monthly active users» в Llama Community License?

Это **единственное серьёзное ограничение** в иначе разрешительной лицензии. Llama Community License по умолчанию **разрешает коммерческое использование**, но с оговоркой: если ваш продукт прямо или через аффилированных лиц имеет **более 700 миллионов monthly active users** (для Llama 3 это MAU на дату релиза модели), то для коммерции **требуется отдельная лицензия от Meta**.

**Зачем этот пункт существует.** 700M+ MAU — это уровень `Apple`, `Google`, `Microsoft`, `Amazon`, `ByteDance`, `Tencent`, то есть прямых конкурентов Meta. Пункт — «отравленная пилюля» (poison pill): он не даёт `BigTech` бесплатно строить AI-продукты на труде Meta, оставляя модель открытой для всех остальных.

**Практический вывод.** Для 99.99% компаний пункт нерелевантен — порог недостижим. Реальное ограничение, на которое стоит смотреть всем, — это сопутствующая `Acceptable Use Policy`: она запрещает военное применение, CSAM, незаконное насилие, слежку без правовых оснований и т.п. Именно из-за ограничений на использование (а не из-за порога MAU) Llama по OSI **не open-source** — нарушается принцип свободы использования (freedom-of-use).

## Q8. Чем `Mistral 7B` отличался от `Llama 2 7B` на момент выхода?

`Mistral 7B` (сент 2023) обогнал `Llama 2 13B` на большинстве бенчмарков, будучи в 2× меньше. Ключевые отличия:

| Аспект | Mistral 7B | Llama 2 7B |
|---|---|---|
| Attention | `Sliding Window Attention` (окно 4K) + `GQA` | Full attention + MHA |
| Лицензия | Apache 2.0 (полностью permissive) | Llama 2 Community (с пунктом про 700M) |
| Контекст | 8K (расширяется через SWA) | 4K |
| Токенизатор | Собственный BPE | Токенизатор Llama |
| Обучающие данные | Не раскрыты | 2T токенов |

Два архитектурных приёма дали Mistral 7B экономичность, а лицензия — распространение:

- **`Sliding Window Attention`** ограничивает attention локальным окном (4096 токенов), снижая память и вычисления до `O(n × w)` вместо `O(n²)`. Для длинного контекста это экономичное приближение: каждый токен «видит» только ближайшее окно, а дальние зависимости передаются послойно.
- **`GQA`** (Grouped Query Attention) — несколько query-голов делят одну пару key/value, что уменьшает KV-cache. У `Llama 2` это было только в 70B; Mistral применил во всех размерах.

**Почему Apache 2.0 решила исход.** Лицензия стала критичным фактором распространения: Mistral 7B можно использовать в коммерции без оговорок, форкать, продавать, встраивать в продукт. Именно это, а не только бенчмарки, превратило его в стандартный baseline для community fine-tunes.

## Q9. Как устроен `Mixtral 8x7B` — почему это не 56B параметров? `!`

Короткий ответ: «8x7B» — маркетинговое название, не сумма. `Mixtral 8x7B` (дек 2023) — sparse MoE с **47B total / 12.9B active**. Восемь экспертов по 7B не дают 56B, потому что общие слои не реплицируются, а активны на каждый токен лишь два эксперта из восьми.

**Архитектура.** Каждый Transformer-блок имеет 8 параллельных `FFN` (feed-forward) экспертов вместо одного. На каждый токен router выбирает top-2 эксперта (`top-k=2`), и их выходы взвешенно суммируются.

```text
Token → Attention → Router (8 experts) → top-2 → FFN_3 + FFN_7 → output
                                                  ↑ выбор зависит от токена
```

Почему 47B, а не 56B:
- Слой `Attention` — общий (не реплицируется на 8 экспертов).
- Реплицируются только **FFN-веса** (≈80% параметров блока).
- Итого 8× FFN + 1× attention + эмбеддинги = ~47B, не 56B.

Почему активны 12.9B:
- На каждый токен — только 2 из 8 экспертов (`top-2`).
- 2/8 = 25% от FFN, плюс полный attention.
- Вычисления на токен ≈ эквивалент dense-модели 12.9B.

**Итог — в чём выгода.** `Mixtral 8x7B` по качеству конкурирует с `Llama 2 70B`, но inference в 4-6× дешевле (вычисления как у 13B). Расплата — память: VRAM нужен под все 47B (≈90GB в fp16, ~24GB в 4-bit GGUF), потому что router на каждом токене может выбрать любого эксперта, и выгрузить «лишние» нельзя.

## Q10. Какие модели Mistral остались Apache 2.0, а какие ушли под коммерческую лицензию?

С 2024 Mistral разделил линейку по простому принципу: **малые и средние модели — Apache 2.0** (для распространения), **frontier — research-лицензия** (для монетизации).

| Модель | Лицензия | Назначение |
|---|---|---|
| `Mistral 7B v0.1/v0.2/v0.3` | Apache 2.0 | Открытый baseline |
| `Mixtral 8x7B` | Apache 2.0 | Открытая MoE |
| `Mixtral 8x22B` (141B/39B active) | Apache 2.0 | Более крупная открытая MoE |
| `Mistral 7B Instruct` | Apache 2.0 | Чат |
| `Mistral NeMo 12B` (с NVIDIA) | Apache 2.0 | Мультиязычная |
| `Mistral Small 3` (2025) | Apache 2.0 | 24B dense, низкая задержка |
| `Codestral` | Mistral Non-Production License (MNPL) | Код, только research/внутреннее использование |
| `Mistral Large 2` | Mistral Research License | Research / некоммерческое |
| `Pixtral 12B` | Apache 2.0 | Мультимодальная (vision-text) |
| `Pixtral Large` | Mistral Research License | Frontier-vision |

**Бизнес-логика.** Small/medium под Apache 2.0 расходятся по сообществу и создают экосистему; frontier (Large) под research-лицензией остаётся источником дохода через API/enterprise. Открытость младших моделей — это маркетинг для платных старших.

**Что разрешает `Mistral Research License`.** Исследования, внутренняя оценка, академические публикации — да. Коммерческий продукт или предоставление модели как сервиса — нет; для этого нужна enterprise-лицензия от Mistral.

## Q11. Какие размеры и варианты есть в семействе `Qwen 2.5`? `!`

Главное про `Qwen 2.5` (сент 2024, Alibaba): это самая **полная по размерному ряду** open-weight семья — от 0.5B для edge до 72B флагмана, плюс специализированные ветки (Math, Coder, VL, reasoning). Контекст 128K (131072 через scaling `YaRN`) — у вариантов 7B и крупнее; мелкие 0.5B/1.5B/3B — 32K.

| Размер | Базовая лицензия | Назначение |
|---|---|---|
| `Qwen 2.5 0.5B` | Apache 2.0 | Edge, mobile |
| `Qwen 2.5 1.5B` | Apache 2.0 | Edge |
| `Qwen 2.5 3B` | Qwen Research License | Edge, встраиваемые системы |
| `Qwen 2.5 7B` | Apache 2.0 | Массовый сегмент |
| `Qwen 2.5 14B` | Apache 2.0 | Средний сегмент |
| `Qwen 2.5 32B` | Apache 2.0 | Высокое качество |
| `Qwen 2.5 72B` | Qwen License (не Apache) | Флагманская dense |

Специализированные варианты (все на базе Qwen 2.5):
- `Qwen 2.5 Math` (1.5B/7B/72B) — математический reasoning.
- `Qwen 2.5 Coder` (0.5B → 32B) — автодополнение кода / чат, конкурент `DeepSeek-Coder`.
- `Qwen 2.5 VL` — vision-language.
- `QwQ-32B` — reasoning-модель (chain-of-thought, обучена через RL).
- `Qwen 2.5-Max` (янв 2025) — frontier-MoE через API (веса не выложены).

`Qwen 3` (2025) — следующее поколение с нативными reasoning-режимами (`thinking` / `non-thinking`, переключаемыми на каждый запрос).

**Где Qwen выбирают.** Семья сильнее всего на **китайском и азиатских языках** (CN/JP/KR) — заметно лучше Llama, потому что обучалась на азиатоцентричном корпусе. Обратная сторона: на восточноевропейских языках Qwen слабее. Это типичный пример «локального bias» данных (см. Q31): где много обучающих текстов — там и сила модели.

## Q12. Что такое `QwQ-32B` и как он связан с reasoning?

`QwQ-32B` (Qwen with Questions, нояб 2024) — **reasoning-модель** Alibaba, конкурент `o1-preview` и `DeepSeek-R1`. Это `Qwen 2.5-32B`, дообученная через RL с reward-сигналом за корректные chain-of-thought: модель не просто отвечает, а сначала «думает вслух» длинным CoT.

Что важно знать:
- Перед финальным ответом генерирует длинные блоки `<thinking>` (до 8-16K токенов CoT).
- На `MATH` — 90.6%, на `GPQA Diamond` — 65.2% (близко к o1-mini).
- Лицензия — Apache 2.0 (в отличие от Qwen 72B — полностью свободна для коммерции).
- Inference дорогой: длинные CoT означают в 5-10× больше токенов, чем у обычного dense-ответа.

**Чем актуальна.** `QwQ-32B-Preview` была первой публичной открытой reasoning-моделью уровня o1. Позже `DeepSeek-R1` (671B MoE) обошла её на большинстве задач, но QwQ остаётся востребованной по одной причине: она **умещается на один H100 80GB**, тогда как R1 требует 8× H100.

**Главный смысл для индустрии.** QwQ доказала, что `test-time compute scaling` (см. `reasoning-models-interview.md`) **воспроизводим в open-source** без секретного know-how OpenAI — достаточно RL и хорошего reward-сигнала.

## Q13. В чём лицензионная особенность `Qwen 72B`?

Ключевой нюанс: флагман `Qwen 72B` (и в версии 1.5, и в 2.5) выпущен **не под Apache 2.0**, а под более ограничительной **Qwen License** (раньше Tongyi Qianwen License) — в отличие от младших моделей семьи. Что в ней есть:

- **Коммерция разрешена**, но при **>100 миллионов MAU** нужна отдельная лицензия от Alibaba (тот же приём, что у Meta, только порог ниже — 100M против 700M).
- Конкуренция с Alibaba Cloud и fine-tunes **не запрещены**.
- **Запрещено** обучать другие LLM на output Qwen (anti-distillation clause).
- Есть `acceptable use policy` (без военного применения, без незаконного).

**Что под Apache 2.0.** Все меньшие варианты — 0.5B, 1.5B, 7B, 14B, 32B. Логика та же, что у Llama: Alibaba монетизирует только через пункты, защищающие от прямых конкурентов масштаба BigTech, а массовый сегмент держит максимально открытым.

## Q14. Что такое `DeepSeek-V3` и почему его training cost ($5.5M) шокировал индустрию? `!`

Суть: `DeepSeek-V3` (дек 2024) — 671B MoE (37B active) под MIT-подобной лицензией (`DeepSeek License v1`) — достигла **уровня GPT-4o**, а финальный прогон обучения, по заявлению paper, стоил всего **$5.576M**. Это в ~10× дешевле западных аналогов.

Из чего складывается цифра:
- 14.8T токенов pretraining;
- 2.788M H800-часов (H800 — китайская версия H100 с урезанным NVLink из-за экспортных ограничений США);
- $2/час за H800 → ~$5.5M.

**Почему это шокировало индустрию.** Точкой отсчёта были совсем другие суммы: `Llama 3.1 405B` — ~$60M по разным оценкам, `GPT-4` — $100M+. DeepSeek получил сопоставимое качество за порядок меньше денег, причём на **урезанном** экспортными ограничениями железе — то есть бюджет перестал быть гарантией лидерства.

**За счёт чего это удалось** (набор инженерных оптимизаций):
- **MLA** (Multi-head Latent Attention) — низкоранговое сжатие KV-cache.
- **DeepSeekMoE** с **256 routed-экспертами + 1 shared** на блок, маршрутизация top-8.
- **Auxiliary-loss-free load balancing** — балансировка нагрузки без штрафа за дисбаланс экспертов.
- **FP8-обучение** на большинстве операций (сэкономили вычисления).
- **Multi-Token Prediction** (`MTP`) — предсказание сразу нескольких следующих токенов как вспомогательная цель (auxiliary objective).
- Тщательно проинженеренный пайплайн обучения под H800 (компенсация слабого interconnect).

**Важная оговорка (часто спрашивают именно её).** $5.5M — это **только финальный прогон обучения**, без исследований, неудачных экспериментов, зарплат и инфраструктуры. Полные затраты (sunk cost) команды кратно выше. Но именно **инкрементальная стоимость финального прогона** действительно скромная — и это честная, проверяемая метрика, а не маркетинг.

## Q15. Чем `DeepSeek-R1` отличается от `DeepSeek-V3`?

Коротко: `DeepSeek-R1` (янв 2025) — это **reasoning-версия** V3. Та же база (671B MoE / 37B active), но дообученная через `GRPO` (Group Relative Policy Optimization), чтобы рассуждать длинным chain-of-thought. V3 отвечает прямо, R1 — сначала думает, потом отвечает.

```text
DeepSeek-V3-Base (pretraining)
       ├── SFT / DPO → DeepSeek-V3 (chat)
       └── RL via GRPO → DeepSeek-R1-Zero → SFT → RL → DeepSeek-R1
```

Ключевые различия:

| Аспект | DeepSeek-V3 | DeepSeek-R1 |
|---|---|---|
| Назначение | Общий чат | Reasoning (математика, код, наука) |
| CoT | Короткий, по запросу | Длинный по умолчанию (через `<think>`) |
| Лицензия | DeepSeek License (MIT-подобная) | MIT (явно) |
| Стиль ответа | Прямой ответ | Chain-of-thought → ответ |
| Стоимость API | ~$0.27/1M input | ~$0.55/1M input + дорогие thinking-токены |
| GPQA Diamond | 59.1% | 71.5% |
| AIME 2024 | 39.2% | 79.8% |

**Что показал `DeepSeek-R1-Zero`.** Это экспериментальная версия, обученная **только через RL без SFT**. У неё спонтанно (emergent) возник CoT — модель сама «научилась рассуждать» от reward-сигнала, без размеченных примеров рассуждений. Расплата — плохая читаемость (смешение языков, скачки в логике), поэтому финальная `R1` получила SFT-«полировку» поверх.

**Главный вклад в open-source.** R1 — **первая open-weight reasoning-модель уровня o1-preview**, под MIT и воспроизводимая. После неё все серьёзные лаборатории выкатили собственные открытые reasoning-варианты — R1 фактически открыла категорию.

## Q16. Что такое `DeepSeek-R1-Distill` и почему дистиллированный 70B доступнее R1?

Проблема: оригинал `DeepSeek-R1` — 671B MoE, требует ~700GB VRAM (минимум 8× H100 80GB) под bf16. Для большинства команд это **недоступно**.

Решение — **дистилляция знаний**: команда DeepSeek сгенерировала ~800k длинных CoT-ответов от R1 на разных задачах и **дообучила** на них маленькие готовые модели (Llama, Qwen) методом SFT. По сути большая модель «учит» маленькую рассуждать на своих примерах — reasoning-навык переносится без дорогого RL:

| Дистиллированная модель | Базовая | Размер | Сравнима с |
|---|---|---|---|
| `DeepSeek-R1-Distill-Qwen-1.5B` | Qwen 2.5 1.5B | 1.5B dense | — |
| `DeepSeek-R1-Distill-Qwen-7B` | Qwen 2.5 Math 7B | 7B dense | o1-mini на математике |
| `DeepSeek-R1-Distill-Llama-8B` | Llama 3.1 8B | 8B dense | — |
| `DeepSeek-R1-Distill-Qwen-14B` | Qwen 2.5 14B | 14B dense | — |
| `DeepSeek-R1-Distill-Qwen-32B` | Qwen 2.5 32B | 32B dense | o1-mini |
| `DeepSeek-R1-Distill-Llama-70B` | Llama 3.3 70B | 70B dense | ≈ GPT-4o на ряде задач |

**Ловушка с лицензией.** Лицензия дистиллята **наследуется от базовой модели**, а не от R1: Qwen-дистилляты — Apache 2.0, Llama-дистилляты — Llama Community License. Поэтому Llama-70B дистиллят в коммерческом продукте всё равно подчиняется пункту Llama (700M MAU), хотя «начинка» от MIT-модели R1.

**Что это даёт на практике.** Дистиллированная 70B умещается на 2× A100 80GB или 1× H100 в `fp8` — на порядок доступнее оригинала R1. Компромисс: дистиллят теряет ~10-20% на сложных задачах против полной R1, но взамен добавляет reasoning-возможность к привычной базовой модели на доступном железе.

## Q17. Что входит в семейство `Gemma 3` от Google? `!`

`Gemma 3` (март 2025) — третья итерация open-weight семьи Google. Лицензия — `Gemma Terms of Use` (коммерция разрешена, но с ограничениями).

| Модель | Параметры | Мультимодальность | Контекст | Примечание |
|---|---|---|---|---|
| `Gemma 3 1B` | 1B | Только текст | 32K | Edge / mobile |
| `Gemma 3 4B` | 4B | Vision + текст | 128K | Массовый сегмент |
| `Gemma 3 12B` | 12B | Vision + текст | 128K | Средний сегмент |
| `Gemma 3 27B` | 27B | Vision + текст | 128K | Флагман |

Чем выделяется Gemma 3:
- **Нативная мультимодальность** от 4B и выше (vision encoder `SigLIP` + text decoder) — в одной модели, а не отдельной vision-веткой.
- **Контекст 128K** во всех размерах, кроме 1B.
- Текстовая архитектура `Gemma` — облегчённая адаптация Gemini.
- **140+ языков** из коробки.
- Pre-trained и instruction-tuned варианты.

Для контекста: Gemma 2 (июнь 2024, 9B / 27B) была только текстовой и на тот момент превосходила Llama 3 8B / Qwen 2 7B. Gemma 3 — это та же линейка плюс мультимодальность и больший контекст.

**Про лицензию.** `Gemma Terms of Use` разрешает коммерцию, но **жёстче Apache 2.0**: запрещено обучать другие LLM на output Gemma (anti-distillation) и запрещён ряд сценариев (обман, слежка, вредоносное ПО). То есть «открытая», но с поведенческими ограничениями.

## Q18. Что такое `PaliGemma`, `CodeGemma`, `ShieldGemma`?

Это «сателлиты» Gemma — варианты базовой модели, дообученные под узкие задачи (vision, код, safety). Все под `Gemma Terms of Use`:

- **`PaliGemma`** (2024, обновлён до PaliGemma 2 в 2025) — vision-language модель, база SigLIP + Gemma. Размеры: 3B / 10B / 28B. Используется для описания изображений (captioning), OCR, VQA. До Gemma 3 это был отдельный путь для vision; теперь нативная мультимодальность Gemma 3 сделала PaliGemma более нишевой.
- **`CodeGemma`** (2024) — fine-tune под код. Размеры 2B / 7B. Заточен под автодополнение кода и infilling (`FIM` — fill in the middle).
- **`RecurrentGemma`** (2024) — экспериментальная архитектура на базе `Griffin` (линейная рекуррентность вместо attention). Лучше на длинном контексте при малой памяти, но проигрывает обычной Gemma по качеству.
- **`ShieldGemma`** (2024) — **safety-классификатор** на базе Gemma. Принимает prompt+response, относит к категориям вреда (hate, harassment, sexual, dangerous). Используется как guardrail для других моделей. Аналог `Llama Guard`. Размеры 2B / 9B / 27B.
- **`DataGemma`** — fine-tune для взаимодействия с Google Data Commons.

**Что из этого чаще встречается в production.** ShieldGemma — её ставят как pre/post-фильтр вокруг основной LLM: входной prompt и сгенерированный ответ прогоняются через классификатор, и опасный контент отсекается до пользователя. См. `ai-safety-guardrails-interview.md`.

## Q19. Чем отличается `Phi-4` от `Phi-3.5-MoE`?

Главное про Phi (Microsoft): это серия **маленьких** моделей, где ставка сделана на **качество данных, а не размер**. Все под MIT. Phi-4 и Phi-3.5-MoE — два разных ответвления этой идеи: первое максимизирует reasoning на dense-модели, второе — эффективность через MoE.

| Модель | Размер | Архитектура | Дата | Сильные стороны |
|---|---|---|---|---|
| `Phi-3 mini` | 3.8B | Dense | апр 2024 | Edge, on-device |
| `Phi-3 small` | 7B | Dense | 2024 | Массовый сегмент |
| `Phi-3 medium` | 14B | Dense | 2024 | Средний сегмент |
| `Phi-3.5-mini` | 3.8B | Dense | авг 2024 | Обновлённое обучение |
| `Phi-3.5-MoE` | 42B / 6.6B active | MoE 16×3.8B | авг 2024 | Высокое качество / низкие вычисления |
| `Phi-3.5 vision` | 4.2B | Мультимодальная | авг 2024 | Vision-text |
| `Phi-4` | 14B | Dense | дек 2024 | Фокус на reasoning, MIT |
| `Phi-4-mini` | 3.8B | Dense | 2025 | Компактная |
| `Phi-4 multimodal` | ~5.6B | Мультимодальная | 2025 | Аудио + vision + текст |

**В чём ключевая разница** `Phi-4` (14B dense) и `Phi-3.5-MoE` (42B/6.6B) — это разный выбор компромисса:
- `Phi-4` оптимизирован под **качество reasoning** (математика, наука). Dense, умещается на 1× H100 80GB в bf16.
- `Phi-3.5-MoE` оптимизирован под **эффективность при масштабе**: total 42B даёт качество ~Mixtral 8x7B при вычислениях как у 6.6B. Требует ~85GB VRAM под все веса, но дешевле по compute.
- Показательный результат: Phi-4 на `GPQA Diamond` — 56.1%, обходит даже Llama 3.1 70B, будучи в 5× меньше.

**Философия Phi.** Синтетические данные «качества учебника» + отфильтрованный веб + хорошая reasoning-разметка. Это прямой контрпример принципу «больше параметров = лучше»: при тщательно подобранных данных компактная модель обыгрывает в разы более крупную.

## Q20. Что такое `OLMo` и почему его называют «fully open»? `!`

`OLMo` (Open Language Model, Allen Institute for AI) — **единственная серия**, которая реально соответствует строгому определению open-source (`OSAID`). «Fully open» означает буквально, что открыто всё, нужное для воспроизведения с нуля, — не только веса.

Что именно открыто:
1. **Веса** — все чекпоинты на HuggingFace.
2. **Код обучения** — repo `OLMo` на GitHub с полным пайплайном.
3. **Обучающие данные** — датасет `Dolma` (3T токенов) опубликован.
4. **Рецепты** — конфигурации, гиперпараметры, расписания learning rate.
5. **Промежуточные чекпоинты** — каждые ~500 шагов, для интерпретируемости.
6. **Логи WandB** — полная телеметрия обучения.
7. **Набор для оценки** — `OLMES`, `Paloma`.
8. **Лицензия** — Apache 2.0 на всё.

Размеры: `OLMo 1B / 7B`, `OLMo 2 7B / 13B`, `OLMo 2 32B` (2025).

Сценарии использования:
- **Академия**: воспроизводимые исследования динамики pretraining.
- **Compliance**: для регулируемых отраслей, требующих полного audit trail (EU AI Act `Article 53`).
- **Образование**: первая и единственная LLM, которую можно реально изучить от начала до конца.

**Про качество — важная оговорка.** Оно ниже, чем у Llama / Qwen / DeepSeek, потому что бюджет AI2 кратно меньше, чем у Meta. Но это **осознанный компромисс**, а не баг: OLMo решает не задачу «быть лучшей моделью», а задачу **полной прозрачности**. Сравнивать его по бенчмаркам с frontier-моделями — значит мерить не той линейкой.

Похожие проекты: `Pythia` (EleutherAI, 2023) — старее, меньше, в основном для исследований интерпретируемости. `BLOOM` (BigScience, 2022, 176B) — полностью открытая, но устарела.

## Q21. Какие ещё open-weight семейства стоит знать (Yi, DBRX, Command R+, Granite)?

| Семейство | От кого | Размеры | Лицензия | Особенность |
|---|---|---|---|---|
| `Yi` | 01.AI (Kai-Fu Lee) | 6B / 9B / 34B / Yi-1.5 / Yi-VL | Apache 2.0 | Сильная мультиязычность, был популярен в 2024 как 34B sweet spot |
| `DBRX` | Databricks | 132B / 36B active MoE | Databricks Open Model License | 16 экспертов × 11B, фокус на enterprise |
| `Command R+` | Cohere | 104B dense | CC-BY-NC-4.0 (некоммерческие веса), коммерческий API | Заточен под RAG, tool use |
| `Command R 7B/35B` | Cohere | 7B / 35B | CC-BY-NC-4.0 | Поменьше, фокус на RAG |
| `Granite` | IBM | 3B / 8B / 20B / 34B + MoE 1B/3B | Apache 2.0 | Enterprise-уровень, фокус на коде |
| `Granite Vision` | IBM | 3.4B / 8B | Apache 2.0 | Document AI |
| `Aya` | Cohere for AI | 8B / 23B / 35B | CC-BY-NC-4.0 | 23+ языка, фокус на мультиязычности |
| `Falcon` | TII (ОАЭ) | 7B / 40B / 180B / Falcon 3 | Apache 2.0 / TII Falcon LLM License | Был первой открытой 180B в 2023, сейчас уступил |
| `Snowflake Arctic` | Snowflake | 480B / 17B active MoE | Apache 2.0 | Enterprise SQL/код |
| `Reka` | Reka AI | Reka Flash, Core | Собственная коммерческая | Мультимодальная, не полностью открыта |
| `Nemotron` | NVIDIA | 70B (производная Llama 3.1) | NVIDIA Open Model License | Llama 3.1 70B + обновлённый RLHF от NVIDIA |

**Что из этого реально берут в production.** `DBRX` — в нативных стеках Databricks, `Granite` — у клиентов IBM, `Command R+` — где нужен RAG, `Yi-34B` — как legacy. `Falcon`, бывший первой открытой 180B в 2023, сейчас почти не используют — его вытеснили Qwen / Llama / Mistral. Общий паттерн: эти семейства живут не за счёт лучших бенчмарков, а за счёт привязки к экосистеме конкретного вендора.

## Q22. Почему в MoE-моделях `total params` важен для VRAM, а `active params` — для compute? `!`

Ключевая идея MoE: она **разводит масштаб и вычисления** по разным ресурсам. `MoE` (Mixture of Experts) разбивает FFN-слой на N экспертов, router выбирает top-K на токен. Поэтому модель «большая» по памяти, но «маленькая» по compute — и две метрики отвечают за разное.

Разберём на `DeepSeek-V3` (671B total, 37B active):

**VRAM (память)**:
- Все 671B параметров **должны быть в памяти** во время inference, потому что для каждого токена router выбирает **разные** эксперты — нельзя выгрузить «неиспользуемые».
- В bf16: 671B × 2 байта = **~1.3 TB VRAM**.
- В fp8: ~700 GB.
- В 4-bit: ~340 GB.
- Это минимум для inference, плюс KV-cache, активации.
- → нужен серверный multi-GPU (8× H100 или больше).

**Вычисления (FLOPs)**:
- Прямой проход (forward pass) на каждый токен использует только **active params** = 37B.
- FLOPs ≈ как у dense-модели 37B.
- → **задержка / стоимость inference ≈ dense 37B**, не 671B.

Следствие — экономика MoE:
| Метрика | Dense 70B | MoE 671B/37B active |
|---|---|---|
| VRAM | ~140 GB | ~1300 GB |
| FLOPs/токен | эквив. 70B | эквив. 37B |
| Качество | Baseline | ≫ baseline |
| Throughput при достаточном VRAM | средний | высокий |

**Когда брать MoE.** Окупается, если (а) есть VRAM под все total params и (б) нужно качество выше dense при дешёвом по compute inference. Не окупается на consumer GPU: упираешься в лимит VRAM, а active params роли не играют — в памяти всё равно должны лежать все эксперты.

Поток токена через MoE-блок (на примере 8 экспертов, `top-K=2`):

- `Token` → `Self-Attention` (shared params, общие веса).
- → `Router` (gate network) — выбирает экспертов по токену.
  - `top-K=2 of 8` → `Expert FFN #3`.
  - `top-K=2 of 8` → `Expert FFN #7`.
  - skipped → остальные 6 экспертов: их params лежат в VRAM, но compute по ним не идёт.
- `Expert FFN #3` и `Expert FFN #7` → `weighted sum` (взвешенная сумма выходов).
- → `next layer` (следующий слой).

## Q23. Что такое `routing overhead` в MoE и почему MoE не всегда дешевле dense?

Главная мысль: «дешёвый по active params» ≠ «дешёвый по факту». `Routing overhead` — это все дополнительные затраты MoE, которые не сводятся к FLOPs самих FFN и которые могут съесть экономию от sparse-активации:

1. **Вычисления gate-сети**: на каждый токен router считает softmax по N экспертам — это `O(d × N)` FLOPs. При N=256 (DeepSeek) это не пренебрежимо мало.
2. **All-to-all-коммуникация**: при распределённом inference (`expert parallelism`) токены маршрутизируются между GPU/узлами. На больших батчах это становится bottleneck сильнее, чем сами FFN.
3. **Балансировка нагрузки**: если router отправляет 90% токенов в один эксперт — этот GPU перегружен, остальные простаивают. Нужны вспомогательные лоссы (auxiliary losses) — или подход auxiliary-loss-free, как у DeepSeek-V3 — на этапе обучения, а на inference — capacity factor / token dropping.
4. **Размер KV-cache**: такой же, как у dense-эквивалента по active params.
5. **Пропускная способность памяти**: все эксперты должны быть в HBM; чтение их весов из global memory может стать bottleneck при низкой arithmetic intensity.

Почему MoE может оказаться **не дешевле dense**:
- При **малых размерах батча** all-to-all доминирует в задержке.
- При **inference на одного пользователя** routing overhead не амортизируется.
- На **consumer GPU** total params не помещается, приходится прибегать к swapping → дико медленно.
- При **строгом latency SLA** dense с прогнозируемой задержкой предпочтительнее MoE с джиттером.

**Эмпирическое правило.** MoE выигрывает на **высокопроизводительном батчевом inference** на серверных GPU с быстрым interconnect (`NVLink`/`InfiniBand`), где routing overhead амортизируется большим батчем. На edge или одном GPU при одном пользователе dense часто проще, предсказуемее и быстрее.

## Q24. Когда self-host open-source выгоднее API? Когда — наоборот? `!`

Короткий ответ: **self-host выгоднее при высоком стабильном объёме и/или жёстких требованиях к приватности, задержке, кастомизации; API — при низком/скачкообразном трафике и отсутствии MLOps-команды.** Это в первую очередь вопрос экономики (фикс vs оплата по факту), а не «что технически круче».

Self-host = развёртывание модели на собственной/арендованной инфраструктуре (vLLM + H100 / A100). API = вызов провайдера (OpenAI / Anthropic / DeepSeek / Together / Fireworks).

| Критерий | Self-host выгоднее | API выгоднее |
|---|---|---|
| **Объём** | >5-10M токенов/день стабильно | Низкий или скачкообразный |
| **Задержка** | Нужен стабильный sub-100ms | Приемлемо 200-1000ms |
| **Приватность данных** | Чувствительные (медицина, финансы, PII) | Публичные/внутренние данные |
| **Кастомизация** | Своя дообученная версия, кастомные промпты | Стандартные модели |
| **Compliance** | EU AI Act, требование on-prem | Облако допустимо |
| **Планка качества** | Достаточно open-source модели | Нужна GPT-4o / Claude / o3 |
| **Ресурсы команды** | Есть MLOps-команда | Нет ML-инженеров |
| **Предсказуемость затрат** | Фиксированная месячная ёмкость | Допустима оплата по факту |
| **География** | Нужен локальный хостинг (Россия, Китай) | Глобальный API допустим |

**Считаем на примере `Llama 3.3 70B`** (главный аргумент — точка безубыточности по объёму):
- API через Together/Fireworks: ~$0.60/1M input + $0.60/1M output → $1.20 за 1M токенов.
- Self-host на 1× H100 80GB ($2-3/час on-demand): пропускная способность ~3000 ток/с = 10.8M ток/час.
- Себестоимость токена: $2.5 / 10.8M = **$0.23 за 1M** — в ~5× дешевле API, но только при полной загрузке.
- Реалистичная точка безубыточности с учётом простоев (~30% утилизации): ~3.2M ток/час стабильно = **~77M токенов/день**.

**Вывод по числам.** Меньше ~77M ток/день стабильно — API дешевле (платишь только за реальное использование, не за простаивающий GPU). Больше — или если важны приватность/задержка/кастомизация — self-host.

**Особый случай — DeepSeek API** ($0.07-0.27/1M input). Он **дешевле self-host почти для всех**, потому что DeepSeek субсидирует API из стратегических соображений (геополитика, доля рынка). Цена обманчиво низкая — главный минус в том, что данные уходят в Китай, что для многих сценариев compliance дисквалифицирует вариант сразу.

## Q25. Какой production-стек для self-host open-source LLM на vLLM/TensorRT-LLM?

Типичный production-стек — это слоёный пирог: клиент → шлюз → inference-движок → GPU-кластер → хранилище весов, плюс сквозные observability и safety. Центральный слой — inference-движок (vLLM и аналоги): именно он отвечает за throughput.

```text
┌─────────────────────────────────────────────────┐
│  Client (App / Agent / Pipeline)                │
└────────────────────┬────────────────────────────┘
                     │ OpenAI-compatible HTTP
                     ▼
┌─────────────────────────────────────────────────┐
│  API Gateway (Kong / Envoy)                     │
│   - Rate limit / quota                          │
│   - Auth (API key)                              │
│   - Multi-tenant routing                        │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Inference Serving Layer                        │
│   vLLM (PagedAttention) или                     │
│   TensorRT-LLM / SGLang / TGI                   │
│   - Continuous batching                         │
│   - KV cache management                         │
│   - Speculative decoding                        │
└────────────────────┬────────────────────────────┘
                     │ NCCL / TP / PP
                     ▼
┌─────────────────────────────────────────────────┐
│  GPU Cluster (H100 / A100 / MI300)              │
│   - Tensor parallelism                          │
│   - Optional pipeline parallelism               │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Storage: HuggingFace weights, quantized GGUF/AWQ│
│   - S3 / GCS / local NVMe                       │
│   - safetensors format (preferred)              │
└─────────────────────────────────────────────────┘

Cross-cutting:
   - Observability: Prometheus + Grafana + Langfuse / Arize
   - Orchestration: Kubernetes + KServe / BentoML / Ray Serve
   - CI/CD: model promotion via Argo Workflows
   - Safety: Llama Guard / ShieldGemma as pre/post filter
```

Ключевые компоненты и когда что:
- **`vLLM`** — дефолтный выбор: PagedAttention, continuous batching, OpenAI-совместимый API, open-source. С него и начинают.
- **`TensorRT-LLM`** — берут, когда нужен максимальный throughput на NVIDIA: выигрыш за счёт kernel fusion и скомпилированных движков ценой более сложной эксплуатации.
- **`SGLang`** — конкурент vLLM с упором на структурированную генерацию и prefix caching (выгоден при повторяющихся префиксах промптов).
- **`KServe`/`BentoML`** — оркестрация в Kubernetes: A/B-тесты, canary-деплои.
- **`safetensors`** — формат HuggingFace на замену `.bin/.pt`: без pickle (безопасная загрузка), быстрее через mmap. Предпочтителен всегда.

Связано с `model-serving-interview.md` и `inference-optimization-interview.md`.

## Q26. Что такое `GGUF`, `AWQ`, `GPTQ` и почему их выкладывает community, а не авторы?

Все три — форматы **post-training quantization**: снижение точности весов с fp16/bf16 до 4-/8-bit, чтобы сократить VRAM и ускорить inference. Различаются целевым железом и движком: `GGUF` — для CPU/edge (llama.cpp), `AWQ`/`GPTQ` — для GPU (vLLM и др.).

| Формат | Используется в | Разрядность | Назначение |
|---|---|---|---|
| `GGUF` | `llama.cpp`, `Ollama`, `LM Studio` | 2 / 3 / 4 / 5 / 6 / 8 bit + K-quants | CPU/edge inference, простая загрузка |
| `AWQ` | vLLM, TGI, HF Transformers | 4-bit (типично) | GPU inference, sweet spot качество/скорость |
| `GPTQ` | vLLM, ExLlama, AutoGPTQ | 3-4 bit | GPU, более старый, чем AWQ |
| `EXL2` | ExLlamaV2 | переменная по слоям | GPU, очень тонкая настройка |
| `bitsandbytes` (NF4/INT8) | HF Transformers | 4 / 8 bit | Быстрая квантизация «на лету» |
| `safetensors` (fp16/bf16) | Все | 16-bit | Базовый формат HF |

**Почему квантизует сообщество, а не авторы.** Причина — в разделении ответственности:
- `Meta` / `Google` / `Mistral` / `Qwen` официально выкладывают на HF только fp16/bf16 safetensors — «эталон».
- Квантизация — это **сжатие с потерями**: требует калибровки на репрезентативном датасете, и результат зависит от целевого железа (CPU vs GPU, разные архитектуры). То есть нет одной «правильной» квантизации.
- Контрибьюторы (`TheBloke`, `bartowski`, `lmstudio-community`, `unsloth`) калибруют под популярные форматы и выкладывают на HF.
- Авторам это удобно: жалоба «эта 4-bit версия плохо работает» становится проблемой community-форка, а не официального релиза.

**Сколько теряется в качестве:**
- 8-bit: <1% относительно fp16 — практически бесплатно.
- 4-bit (AWQ / GPTQ-act-order): 1-3% — обычный sweet spot.
- 3-bit и ниже: заметная деградация, особенно на reasoning — берут только при жёстком дефиците VRAM.

**Практический совет.** Читайте README quant-репозитория: там обычно тест perplexity и субъективные заметки о качестве. Не все 4-bit одинаковы — калибровка у разных авторов даёт разный результат.

## Q27. Сравни `Apache 2.0`, `MIT`, `Llama Community`, `Gemma Terms`, `Mistral Research License`. `!`

| Лицензия | Тип | Коммерция | Изменение | Распространение | Ограничения на использование | Anti-distillation |
|---|---|---|---|---|---|---|
| `Apache 2.0` | Одобрена OSI | Да | Да | Да | Нет | Нет |
| `MIT` | Одобрена OSI | Да | Да | Да | Нет | Нет |
| `Llama Community` (3 / 3.1 / 3.2 / 3.3 / 4) | Собственная | Да, кроме >700M MAU | Да | Да, с уведомлением | AUP | Нет (но маркировка output) |
| `Gemma Terms of Use` | Собственная | Да | Да | Да | Gemma AUP | Да (нельзя обучать на output) |
| `Mistral Research License` | Собственная | Нет | Да, для research | Да, для research | Только некоммерческое | — |
| `Qwen License` (72B) | Собственная | Да, кроме >100M MAU | Да | Да | AUP | Неявно |
| `DeepSeek License` | MIT-подобная | Да | Да | Да | Минимальная AUP | Нет (V3); R1 — чистый MIT |
| `Open RAIL` | С ограничением поведения | Да | Да | Да | Конкретные виды поведения | Варьируется |
| `CC-BY-NC-4.0` | Creative Commons | Нет | Да, для NC | Да, для NC | Только некоммерческое | Неявно |

Классификация лицензий по четырём группам:

- **OSI-strict open-source**:
  - `Apache 2.0` — OLMo, Mistral 7B, младшие Qwen, Yi.
  - `MIT` — DeepSeek-R1, Phi.
- **Open-weight commercial-friendly (с clauses)**:
  - `Llama Community` — ограничение >700M MAU.
  - `Gemma Terms` — + AUP, anti-distill.
  - `Qwen License 72B` — >100M MAU.
- **Research / non-commercial only**:
  - `Mistral Research License` — Large 2.
  - `CC-BY-NC` — Command R+, Aya.
- **Behavior-restricted**:
  - `Open RAIL` — BLOOM, Stable Diffusion.

**Как это применять на практике** (лицензии делятся на три корзины по пригодности для коммерции):
- **Зелёная зона — берите без раздумий**: Apache 2.0 / MIT (Mistral 7B, OLMo, Qwen ≤32B, DeepSeek-R1, Phi). Никаких ограничений на использование.
- **Жёлтая зона — можно, но проверяйте**: Llama / Gemma — следите за сценарием использования, порогом MAU и требованием маркировать output.
- **Красная зона — для production B2C не годятся**: Mistral Research License, CC-BY-NC (только некоммерческое).
- **Сквозное правило**: всегда читайте AUP (Acceptable Use Policy) — она ограничивает даже модели под Apache.

## Q28. Что такое `Open RAIL License` и где применяется?

`Open RAIL` (Responsible AI License, от BigScience / Hugging Face, 2022) — лицензия для AI-артефактов, которая разрешает свободное использование, **но запрещает конкретные виды поведения**, перечисленные в `Attachment A`. Идея — «открытость с этическим фильтром»: технически делай что хочешь, но не для перечисленного вреда.

Как устроена:
- **Permissive-ядро**: использование, изменение, распространение и коммерция разрешены.
- **Поведенческие ограничения**: список запрещённых сценариев (клевета, оружие, эксплуатация, медицинская мисдиагностика без контроля и т.п.).
- **Привязка к использованию, а не к коду**: ограничения следуют за моделью и её применением, а не только за source-кодом.

Варианты:
- `OpenRAIL-M` — для моделей (models).
- `OpenRAIL-S` — для исходного кода (source code).
- `OpenRAIL-D` — для данных (data).

Где применяется:
- `BLOOM` (BigScience, 176B, 2022) — первая крупная LLM под Open RAIL.
- `Stable Diffusion` v1, v2 (CreativeML Open RAIL-M).
- `IDEFICS` (мультимодальная от HF).

**Статус в 2026.** Для LLM верхнего эшелона стандартом так и не стала: Meta, Google, Mistral, Qwen, DeepSeek предпочли собственные лицензии или чистый Apache 2.0/MIT. Open RAIL осталась в нише генерации изображений (экосистема Stable Diffusion) и старых LLM (BLOOM).

По той же причине, что Llama и Gemma, OSI считает Open RAIL **не open-source**: любые ограничения на использование нарушают свободу использования.

## Q29. Какие риски security при загрузке моделей с HuggingFace? `!`

Главная мысль для интервью: скачать модель — это **запустить чужой недоверенный код**, если формат допускает исполнение. Веса сами по себе безопасны, опасны форматы и флаги вокруг них. Основные векторы:

1. **Десериализация pickle** (`.bin`, `.pt`):
   - PyTorch `torch.save` использует Python pickle.
   - Pickle позволяет **выполнение произвольного кода** при `load`.
   - Вредоносный файл модели может запустить shell-команды при `torch.load(...)`.
   - HF в 2023 ввели `pickle scanner` (бейдж `safetensors-friendly`), но не все модели мигрированы.
   - **Защита**: используйте формат `.safetensors`. HF Transformers по умолчанию предпочитает safetensors, если есть оба варианта.

2. **Вредоносный код при `trust_remote_code=True`**:
   - Модели с нестандартной архитектурой требуют `trust_remote_code=True`.
   - Это выполняет `modeling_<arch>.py` из репозитория.
   - Может содержать вредоносное ПО, эксфильтрацию данных, бэкдоры.
   - **Защита**: используйте только проверенных авторов (`meta-llama`, `mistralai`, `Qwen`, `google`, `microsoft`, `deepseek-ai`); проверяйте код перед загрузкой; запускайте в изолированном контейнере.

3. **Модели с бэкдором** (research-уровень):
   - Веса могут быть отравлены: на специфическом trigger-промте модель ведёт себя вредоносно (sleeper agent).
   - Невозможно обнаружить без black-box-тестирования.
   - **Защита**: red-team-тестирование, не доверяйте малоизвестным авторам.

4. **Инъекция кода через токенизатор**:
   - `tokenizer.json` обычно безопасен, но `tokenizer_config.py` или Python-код кастомного токенизатора может содержать вредоносный код.

5. **Атаки на цепочку поставок (supply-chain)**:
   - Атакующий захватывает HF-аккаунт популярного автора и заливает троянизированные веса.
   - **Защита**: фиксируйте конкретный commit / revision hash, проверяйте PGP-подписи, если они есть.

Практический workflow:
- Только `safetensors`, не `.bin`.
- `trust_remote_code=False` по умолчанию, явно включать только для проверенных авторов.
- Запуск в песочнице при экспериментах (Docker без сети, без монтирования host-каталогов).
- Зеркалируйте веса в свой artifact registry с фиксацией хешей (hash-pinning).

## Q30. Какие open-source модели реально fine-tune-нуть на consumer GPU (4090 24GB)?

Короткий ответ: на `RTX 4090` (24GB VRAM) реально дообучить модели **до ~14B через QLoRA** и до ~7-8B через обычную LoRA. Ключ к этому — не полный fine-tune, а LoRA/QLoRA: учатся только маленькие адаптеры, а не все веса. Масштабы по методам:

| Размер базовой модели | QLoRA (4-bit база) | LoRA (fp16 база) | Полный fine-tune |
|---|---|---|---|
| 1B-3B | тривиально | OK | OK (с gradient checkpointing) |
| 7B-8B | OK (комфортно) | OK (на грани, gradient checkpoint, batch=1-2) | Нет |
| 13B-14B | OK с подгонкой | впритык | Нет |
| 27B-34B | OK с offload | Нет | Нет |
| 70B | Нет (нужно ≥48GB или 2× 4090) | Нет | Нет |

**Почему QLoRA влезает.** `QLoRA` (Quantized LoRA) держит базовую модель в **4-bit** (bitsandbytes NF4) и учит маленькие LoRA-адаптеры в fp16 поверх неё. База занимает 4× меньше памяти, а градиенты считаются только для адаптеров — отсюда экономия VRAM в разы против полного fp16-дообучения.

Практичные кандидаты на дообучение на 4090:
- `Llama 3.1 8B` (QLoRA, 4-bit) — комфортно.
- `Qwen 2.5 7B / 14B` (QLoRA).
- `Mistral 7B v0.3` (QLoRA / LoRA).
- `Gemma 2 9B` (QLoRA).
- `Phi-3 medium 14B` (QLoRA, впритык).
- `Qwen 2.5 32B` (QLoRA с CPU offload, медленно).

**Что не влезет ни при каком методе:** 70B, MoE с total >40B, любые fp16 ≥ 20B — для них нужны несколько GPU или серверная карта.

**Стек и сроки.** `unsloth` (экономичные ядра) / `axolotl` / `LLaMA-Factory` поверх PyTorch. Ориентир по времени: 7B QLoRA на 50k примерах — 6-12 часов на одной 4090.

Связано с `fine-tuning-llm-interview.md` (LoRA/QLoRA подробно).

## Q31. Какие подводные камни при оценке open-source моделей по бенчмаркам?

Общий принцип: высокая цифра на бенчмарке ≠ высокое качество в вашей задаче. Вот восемь причин, почему опубликованным числам нельзя верить буквально:

1. **Загрязнение данных (contamination)** — тестовые наборы утекли в обучающие данные. Особенно типично для `MMLU`, `GSM8K`: модель «знает ответы», а не рассуждает. У open-source болезненнее всего, потому что корпуса публичны, а фильтрация неидеальна.

2. **Cherry-picking бенчмарков** — авторы выбирают, на каких бенчмарках показывать результат. Модель может быть SOTA на `MATH`, но средней на `MMLU` — в paper покажут только MATH.

3. **Разный промптинг / few-shot-настройка** — `MMLU` в zero-shot против 5-shot CoT даёт разницу 5-15%. Сравнения часто получаются apples-to-oranges (несопоставимые).

4. **Заявленное против воспроизводимого** — официальный paper заявляет 90% на бенчмарке, при попытке воспроизвести получают 85%. Причины: тонкости сэмплинга (temperature, top-p), формат промпта, evaluation harness.

5. **Локальный bias** — китайские модели (Qwen, DeepSeek) показывают высокие цифры на бенчмарках с китайским языком/контентом. На западных бенчмарках цифры могут падать.

6. **Насыщение бенчмарка** — `MMLU` на 90% означает, что 10% «ошибок» — это уже ошибки в самом бенчмарке, а не модели. Дальнейшие улучшения шумные.

7. **Bias в сторону single-turn** — большинство бенчмарков однотерновые. Многотерновый диалог / agentic-петли не покрыты.

8. **Chatbot Arena Elo** — субъективные сравнения по предпочтениям людей. Зависит от типов промптов в пуле (программисты vs креативные авторы vs обычные пользователи).

**Как оценивать правильно** (от самого надёжного к вспомогательному):
- **Кастомная внутренняя оценка** на задачах вашего домена — самое надёжное, потому что не загрязнено и совпадает с реальным применением.
- **Оценка людьми** на слепом A/B (подход LMSYS Arena).
- **Живые бенчмарки** `LiveCodeBench`, `LiveBench` — обновляются ежемесячно, поэтому загрязнение минимально.
- Несколько референсных бенчмарков вместо одного — чтобы не попасться на cherry-picking.

## Q32. Decision tree: как выбрать open-source модель под задачу? `!`

Дерево выбора модели под задачу (последовательность фильтров от `Задача` вниз):

**Шаг 1 — `License OK?` (Commercial? MAU?)**

- `Strict OSI / no clauses` → только Apache/MIT: Mistral 7B, Qwen ≤32B, DeepSeek-R1, Phi, OLMo.
- `OK with clauses` → все open-weight.

**Шаг 2 — `Размер budget?`** (применяется и к ветке Apache/MIT, и к «все open-weight»)

- `≤8B edge` → Llama 3.2 3B, Qwen 2.5 7B, Phi-3.5-mini, Gemma 3 4B.
- `14-32B sweet spot` → Qwen 2.5 14B/32B, Phi-4 14B, Gemma 3 27B, Mistral Small 3 24B.
- `70B production` → Llama 3.3 70B, Qwen 2.5 72B, DeepSeek-R1-Distill 70B.
- `Frontier MoE` → DeepSeek-V3/R1 671B, Mixtral 8x22B, Llama 4 Maverick.

**Шаг 3 — `Task type?`** (применяется к любому выбранному размеру)

- `General chat / RAG` → Llama / Qwen / Mistral.
- `Code` → DeepSeek-Coder, Qwen 2.5 Coder, Codestral.
- `Math / Reasoning` → DeepSeek-R1, QwQ-32B, Qwen 2.5 Math.
- `Multilingual non-EN` → Qwen для CN/JP/KR, Mistral для языков ЕС, Aya для low-resource.
- `Vision-language` → Llama 3.2 Vision, Qwen 2.5-VL, Gemma 3 27B, Pixtral.
- `Safety classifier` → Llama Guard, ShieldGemma.

**Шаг 4 — `Fine-tune needed?`** (для веток chat/RAG, code и math/reasoning)

- `Yes, consumer GPU` → QLoRA на базе 7B-14B.
- `Yes, server` → LoRA / full FT 70B+.
- `No` → использовать как есть через vLLM.

Порядок шагов выбран не случайно: каждый следующий фильтр применяется к уже суженному списку, и самый жёсткий (лицензия) стоит первым, чтобы не тратить время на заведомо неприменимые модели.

1. **Лицензия** — сразу отсекает запрещённые комбинации (MAU, коммерция, AUP). Дальше работаете только с допустимыми.
2. **Бюджет по размеру** — ограничивает выбор доступным железом и целевой задержкой.
3. **Тип задачи** — выбирает семейство с лучшими специализациями (код, reasoning, vision, язык).
4. **Нужен ли fine-tune?** — определяет дополнительные требования (LoRA / QLoRA / полный).

## Q33. Какие code-specific open-source модели есть и какая под что?

| Модель | Размеры | Лицензия | Sweet spot |
|---|---|---|---|
| `DeepSeek-Coder V2` | 16B / 236B MoE / 21B active | DeepSeek License | Сильный код, FIM, многоязычный |
| `Qwen 2.5 Coder` | 0.5B / 1.5B / 3B / 7B / 14B / 32B | Apache 2.0 (кроме 3B — Qwen Research License) | Полный размерный ряд, обходит Codestral |
| `Codestral` (Mistral) | 22B | MNPL (non-prod) | Заточен на однопроходную генерацию кода |
| `Codestral Mamba` | 7B | Apache 2.0 | Код с длинным контекстом (256K), архитектура state-space |
| `Code Llama` | 7B / 13B / 34B / 70B | лицензия Llama 2 | Legacy, заменён производными Llama 3 |
| `StarCoder 2` | 3B / 7B / 15B | BigCode OpenRAIL-M | Автодополнение кода, FIM |
| `WizardCoder` | 7B / 15B / 34B | производная Llama 2 | Instruction-tuned код |
| `Phind-CodeLlama` | 34B | лицензия Llama 2 | Фокус на Q&A по коду |
| `Granite Code` | 3B / 8B / 20B / 34B | Apache 2.0 | IBM enterprise, 116 языков |
| `OpenCoder` | 1.5B / 8B | Apache 2.0 (полная прозрачность) | Воспроизводимая code-LLM |

Сценарии:
- **Автодополнение кода в IDE (FIM)**: `Qwen 2.5 Coder 7B`, `StarCoder 2 7B`, `DeepSeek-Coder 6.7B`. Маленькие, быстрые, с поддержкой fill-in-middle.
- **Чат / объяснение / рефакторинг**: `Qwen 2.5 Coder 32B`, `DeepSeek-Coder V2 16B`. Качественнее, но медленнее.
- **Frontier-reasoning по коду**: `DeepSeek-V3` (общая MoE), `DeepSeek-R1-Distill-Qwen-32B` (reasoning + код).
- **Навигация по длинному контексту**: `Codestral Mamba` (контекст 256K), `Qwen 2.5 Coder 32B` (128K).
- **Enterprise-compliance**: `Granite Code` (Apache 2.0, enterprise-поддержка IBM).

**Где проходит граница с frontier.** `Claude Sonnet 4.5 Coder`, `GPT-4o Code` всё ещё впереди на сложных задачах — рефакторинг по нескольким файлам, agentic-кодинг с длинной цепочкой шагов. Но на однопроходных задачах (HumanEval, MBPP) open-source разрыв уже закрыл: для автодополнения и генерации отдельной функции локальная модель сопоставима с frontier.

## Q34. Outlook 2026: где open-source догоняет frontier, а где отстаёт?

Коротко: к 2026 open-source **сравнялся с frontier на массовых задачах** (чат, однопроходный код, edge), но **отстаёт на верхнем эшелоне reasoning, нативном аудио/видео и длинных agentic-цепочках**. Разрыв сместился из «качества вообще» в «качество на самых сложных задачах».

**Где open-source уже близок к паритету или впереди в нишах**:
- **Общий Q&A / чат** (`MMLU`, `Arena Elo`): `Llama 3.3 70B`, `Qwen 2.5 72B`, `DeepSeek-V3` соревнуются с `GPT-4o` / `Claude Sonnet`.
- **Математический reasoning**: `DeepSeek-R1`, `QwQ-32B` — близко к `o1`.
- **Код (однопроходный)**: `Qwen 2.5 Coder 32B`, `DeepSeek-Coder V2` ≈ GPT-4o.
- **Мультиязычность вне английского**: Qwen для CN, Mistral для языков ЕС — конкурентоспособно.
- **On-device / edge**: `Llama 3.2 1B/3B`, `Phi-3.5-mini`, `Gemma 3 4B` — закрытые frontier-модели здесь не играют (нет деплоя).

**Где frontier (закрытые) сохраняют преимущество**:
- **Reasoning верхнего эшелона** (`o3`, `o4`): сложные многошаговые научные / математические задачи всё ещё за OpenAI.
- **Высококачественная нативная мультимодальность**: аудио GPT-4o, видео Gemini 2, vision Claude. Open-source закрывает изображения, но аудио/видео отстают.
- **Agentic / задачи с длинным горизонтом**: Claude Sonnet 4.5 / o3 превосходят на трейсах в 100 шагов, цепочках tool use.
- **Frontier-обработка контекста**: 1M+ контекст качественно — Gemini, Claude. Open-source формально декларирует 10M (Llama 4 Scout), но качество деградирует.
- **Устойчивость safety / alignment**: закрытые лаборатории много инвестируют в red-teaming и устойчивость к jailbreak.

**Геополитический контекст**:
- **Восхождение китайского open-source**: DeepSeek, Qwen, Yi — стратегически выкладываются как противовес экспортным ограничениям США. По сути «open-source как мягкая сила».
- **Закрытый frontier США**: OpenAI, Anthropic, Google не выкладывают frontier-веса, конкурируют через качество API.
- **Европа**: Mistral пытается занять среднюю позицию (открытая база + коммерческий frontier), но capex несопоставим с гигантами США/Китая.

**Что вероятно к концу 2026**:
- Open-source reasoning уровня `o3` (DeepSeek-R2?).
- Open-source с нативной мультимодальностью по аудио (после Llama 4.x).
- Open-source с production-качеством контекста 1M.
- Стандартизация open-source-экосистемы (HF + vLLM + Llama Stack).
- Регуляторное давление в ЕС / Калифорнии на frontier-safety; open-source-экосистему частично освобождают (`Article 53` EU AI Act освобождает open-source при общих условиях).

Практический вывод: **в 2026 для большинства production-сценариев open-source достаточно**. Frontier зарезервирован для (а) reasoning-критичных задач, (б) передового agentic-применения, (в) случаев, когда не хочется поддерживать собственный ML-стек.

---

## See also

- [llm-basics-interview.md](llm-basics-interview.md) — основы LLM, токенизация, attention, transformer
- [fine-tuning-llm-interview.md](fine-tuning-llm-interview.md) — LoRA, QLoRA, full fine-tune, instruction tuning
- [inference-optimization-interview.md](inference-optimization-interview.md) — vLLM, TensorRT-LLM, quantization, KV-cache
- [model-serving-interview.md](model-serving-interview.md) — KServe, BentoML, production serving stacks
- [reasoning-models-interview.md](reasoning-models-interview.md) — o1/o3/R1, test-time compute, GRPO
- [ai-safety-guardrails-interview.md](ai-safety-guardrails-interview.md) — Llama Guard, ShieldGemma, NeMo Guardrails
- [ai-compliance-governance-interview.md](ai-compliance-governance-interview.md) — EU AI Act, model cards, audit
- [multimodal-ai-interview.md](multimodal-ai-interview.md) — vision-language, PaliGemma, Pixtral, Qwen-VL
- [llm-evaluation-interview.md](llm-evaluation-interview.md) — benchmarks, contamination, eval pitfalls
- [prompt-engineering-interview.md](prompt-engineering-interview.md) — prompt formats, chat templates
- [rag-interview.md](rag-interview.md) — Command R+, RAG-focused models
