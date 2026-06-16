---
title: "Вопросы на собеседовании: Reasoning Models (o1/o3/R1)"
description: "Reasoning LLMs (OpenAI o1/o3, DeepSeek-R1, Claude extended thinking, Gemini Deep Think): архитектура, test-time compute scaling, GRPO, cost/latency."
tags:
  - interview
  - ai-ml
  - reasoning-models
type: "interview"
difficulty: "advanced"
aliases:
  - "Reasoning Models interview"
  - "o1 o3 R1 собеседование"
  - "Test-time compute scaling"
  - "Extended thinking LLM"
updated: "2026-05-23"
---
# Вопросы на собеседовании: `Reasoning Models (o1/o3/R1)`

С сентября 2024 индустрия LLM получила новую парадигму — **reasoning models**. Они тратят значительную часть compute не на вывод финального ответа, а на длинный internal `chain-of-thought` (CoT). CoT перестал быть prompt-техникой и стал архитектурным свойством, обученным через RL.

На интервью спрашивают: как это устроено, чем отличается от обычных LLM, когда оправдано платить 5-10× больше за токен, что такое `test-time compute scaling`, что сделал DeepSeek-R1 и при чём здесь `GRPO`.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [OpenAI o1 System Card](https://openai.com/index/openai-o1-system-card/) — официальное описание архитектуры и safety
- [OpenAI Reasoning Guide](https://platform.openai.com/docs/guides/reasoning) — API specifics для o-series
- [DeepSeek-R1 Paper (arXiv)](https://arxiv.org/abs/2501.12948) — R1, R1-Zero, GRPO, distillation
- [Anthropic Extended Thinking Docs](https://docs.anthropic.com/en/docs/build-with-claude/extended-thinking) — Claude thinking budget
- [Gemini Deep Think](https://deepmind.google/technologies/gemini/) — Google Deep Think mode
- [Tree of Thoughts paper](https://arxiv.org/abs/2305.10601) — inference-time reasoning
- [Self-Consistency paper](https://arxiv.org/abs/2203.11171) — majority vote CoT

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Эволюция и архитектура**
- [Q1. (!) Что такое reasoning model и чем отличается от обычной LLM?](#q1--что-такое-reasoning-model-и-чем-отличается-от-обычной-llm)
- [Q2. (!) Эволюция reasoning: от `Let's think step by step` до o1/R1?](#q2--эволюция-reasoning-от-lets-think-step-by-step-до-o1r1)
- [Q3. (!) Что такое test-time compute scaling?](#q3--что-такое-test-time-compute-scaling)
- [Q4. Архитектура reasoning model — это новый transformer?](#q4-архитектура-reasoning-model--это-новый-transformer)
- [Q5. Что такое thinking tokens?](#q5-что-такое-thinking-tokens)

**Training (RL, GRPO, distillation)**
- [Q6. (!) Как обучают reasoning model?](#q6--как-обучают-reasoning-model)
- [Q7. (!) Что такое GRPO и чем отличается от PPO?](#q7--что-такое-grpo-и-чем-отличается-от-ppo)
- [Q8. Synthetic reasoning data — как генерируют?](#q8-synthetic-reasoning-data--как-генерируют)
- [Q9. Reward model в reasoning training?](#q9-reward-model-в-reasoning-training)

**DeepSeek-R1 breakthrough**
- [Q10. (!) Что такого революционного в DeepSeek-R1?](#q10--что-такого-революционного-в-deepseek-r1)
- [Q11. R1-Zero vs R1 — в чём разница?](#q11-r1-zero-vs-r1--в-чём-разница)
- [Q12. (!) R1-Distill — как уместить reasoning в 8B модель?](#q12--r1-distill--как-уместить-reasoning-в-8b-модель)

**API specifics (OpenAI/Anthropic/Gemini/DeepSeek)**
- [Q13. (!) OpenAI o-series API — особенности?](#q13--openai-o-series-api--особенности)
- [Q14. (!) Anthropic extended thinking — как работает?](#q14--anthropic-extended-thinking--как-работает)
- [Q15. DeepSeek-R1 API — `<think>` теги?](#q15-deepseek-r1-api--think-теги)
- [Q16. Hidden vs visible thinking — почему OpenAI скрывает CoT?](#q16-hidden-vs-visible-thinking--почему-openai-скрывает-cot)

**Когда использовать / когда нет**
- [Q17. (!) Когда reasoning model даёт реальное преимущество?](#q17--когда-reasoning-model-даёт-реальное-преимущество)
- [Q18. (!) Когда reasoning model избыточна или вредна?](#q18--когда-reasoning-model-избыточна-или-вредна)
- [Q19. Бенчмарки: где reasoning доминирует, где нет?](#q19-бенчмарки-где-reasoning-доминирует-где-нет)
- [Q20. o3 на ARC-AGI — почему это важно?](#q20-o3-на-arc-agi--почему-это-важно)

**Cost/latency trade-offs**
- [Q21. (!) Сравнение o1 vs o3 vs R1 vs Claude 3.7 thinking по cost/latency?](#q21--сравнение-o1-vs-o3-vs-r1-vs-claude-37-thinking-по-costlatency)
- [Q22. Сколько токенов «съедает» reasoning?](#q22-сколько-токенов-съедает-reasoning)

**Inference-time альтернативы**
- [Q23. (!) Best-of-N, Tree-of-Thoughts, Self-Consistency — заменяют ли reasoning model?](#q23--best-of-n-tree-of-thoughts-self-consistency--заменяют-ли-reasoning-model)

**Production patterns**
- [Q24. (!) Routing: когда отправлять в reasoning model?](#q24--routing-когда-отправлять-в-reasoning-model)
- [Q25. (!) Prompt engineering для reasoning models — что меняется?](#q25--prompt-engineering-для-reasoning-models--что-меняется)
- [Q26. Hallucination и rationalization в reasoning models?](#q26-hallucination-и-rationalization-в-reasoning-models)
- [Q27. Streaming thinking — улучшает UX?](#q27-streaming-thinking--улучшает-ux)
- [Q28. Reasoning + tools/function calling — как работает?](#q28-reasoning--toolsfunction-calling--как-работает)
- [Q29. Open-weight reasoning models — что доступно?](#q29-open-weight-reasoning-models--что-доступно)
- [Q30. (!) Reasoning models и agents — future?](#q30--reasoning-models-и-agents--future)

## Q1. (!) Что такое reasoning model и чем отличается от обычной LLM?

**Reasoning model** — это LLM, специально **обученная** генерировать длинный внутренний `chain-of-thought` (CoT) перед финальным ответом. Ключевое отличие: CoT здесь не prompt-трюк, а встроенное в модель поведение, выработанное обучением через RL. Обычную LLM можно *попросить* рассуждать, reasoning model рассуждает *по умолчанию* и умеет это делать гораздо лучше.

**Обычная LLM (gpt-4o, Claude Sonnet, Llama):**
- Получает prompt → сразу пишет ответ.
- Можно попросить `Let's think step by step` — она напишет рассуждение, но это эмерджентное поведение от pre-training/RLHF, не оптимизированное под качество reasoning.
- 1 запрос ≈ 100-500 output tokens.

**Reasoning model (o1, o3, R1, Claude 3.7 thinking):**
- Получает prompt → внутри генерирует тысячи **thinking tokens** (рассуждение, проверка гипотез, self-correction) и только потом выдаёт финальный ответ.
- 1 запрос ≈ 1 000-50 000 output tokens, причём большая часть — это thinking, а не сам ответ.
- Обучена через RL с reward на **корректность финального ответа**, а не на стиль. Поэтому модель сама нащупала стратегии: проверять себя, откатываться назад, пробовать несколько подходов.

**Поток обработки запроса:**
- **Standard LLM:** `Prompt` → `Transformer` → `Answer` (100-500 tokens). Один шаг от вопроса к ответу.
- **Reasoning Model:** `Prompt` → `Transformer` → `Thinking tokens` (1k-50k) → `Final answer` (100-500 tokens). Между моделью и ответом вклинивается длинный этап размышления, и только потом — финальный ответ той же длины, что у обычной LLM.

**Аналогия:** обычная LLM — это студент, который сразу пишет ответ на экзамене; reasoning model — тот, кто исписывает черновик, проверяет себя, потом аккуратно выписывает ответ.

## Q2. (!) Эволюция reasoning: от `Let's think step by step` до o1/R1?

| Год | Событие | Что нового |
|-----|---------|------------|
| 2022 | **Chain-of-Thought prompting** (Wei et al.) | Prompt-trick: `Let's think step by step` → +15-30% на математике для PaLM/GPT-3 |
| 2023 | Tree-of-Thoughts, Self-Consistency | Inference-time techniques: множественные CoT + verifier/voting |
| 2024 сент | **OpenAI o1-preview, o1-mini** | Первая production reasoning model. RL на CoT. Hidden thinking |
| 2024 дек | **OpenAI o1** (GA), **o3-preview** anonced | o3 на ARC-AGI: 75-87% (humans ~85%). FrontierMath: 25% (предыдущие <2%) |
| 2025 янв | **DeepSeek-R1** (open weights!) | Competitive с o1. GRPO. R1-Zero — pure RL без SFT |
| 2025 фев | **Claude 3.7 Sonnet extended thinking** | Visible thinking, configurable budget |
| 2025 март-апр | **Gemini 2.5 Pro Deep Think** | Google reasoning mode |
| 2025 | **QwQ-32B, Llama-3.3-Nemotron-Reasoning** | Open-weight reasoning от Alibaba/Nvidia |

**Суть эволюции:** идею «рассуждать по шагам» (2022) и приёмы поиска/голосования на inference (2023) индустрия сначала навешивала *поверх* модели как prompt-обёртку. Прорыв 2024-2025 в том, что эту способность **зашили внутрь модели через RL** — теперь модель сама решает, как долго и как именно думать. Один сдвиг в одной фразе: CoT превратился из **prompt-техники** в **обученное свойство модели**.

## Q3. (!) Что такое test-time compute scaling?

**Test-time compute scaling** — это эмпирическое наблюдение: если на этапе inference дать модели **больше токенов на reasoning**, качество ответа растёт. Причём растёт предсказуемо — примерно **линейно по логарифму** потраченного compute. Это вторая ось масштабирования вдобавок к классической «больше параметров / больше данных»: теперь можно улучшать результат, ничего не переобучая, просто разрешая модели думать дольше.

**Две оси масштабирования качества:**
- **Train-time scaling (старая парадигма):** к лучшему качеству (`Better quality`) ведут `More params` и `More data` — улучшаешь модель только переобучением.
- **Test-time scaling (новая парадигма):** к лучшему качеству (`Better quality`) ведут `More thinking tokens` и `Best-of-N, search` — результат растёт прямо на inference, без переобучения.

**Закон масштабирования:** `accuracy ≈ a + b · log(compute_at_inference)`.

**Следствия:**
- Качество можно **купить за inference**: больше времени на размышление → лучше ответ, без переобучения модели.
- На сложных задачах (AIME, FrontierMath) разница между «1k thinking tokens» и «100k» составляет десятки процентных пунктов — масштаб эффекта огромен.
- Появляется новый управляющий параметр (knob) для продакшена: `thinking budget` — сколько compute не жалко на конкретный запрос.

**Чем именно занять этот inference-compute — три стратегии:**
1. **Long CoT** — одна длинная непрерывная цепочка рассуждений (o1, R1).
2. **Best-of-N + verifier** — N независимых попыток, лучшую выбирает verifier.
3. **Tree search** (в духе MCTS) — модель исследует разные ветки reasoning и отсекает тупиковые.

OpenAI и DeepSeek сделали ставку на **long CoT** и встроили его в модель через RL — то есть саму стратегию «думать длинно» зашили в веса, а не накручивают снаружи.

## Q4. Архитектура reasoning model — это новый transformer?

**Нет. Архитектурно это всё тот же стандартный decoder-only transformer** — никакого нового слоя или механизма. Вся разница не в железе модели, а в том, как её **обучают** и как с ней **общаются на inference**.

**Что меняется — обучение:**
1. **Pre-training** — обычный next-token prediction на больших корпусах, как у любой LLM.
2. **Post-training** — RL на reasoning traces. Reward = корректность финального ответа (для математики и кода её можно проверить автоматически). Именно на этом этапе модель учится **долго думать**, проверять себя и искать собственные ошибки.

**Что меняется — inference-протокол:**
- Модель сама решает, когда закончить thinking и начать final answer. Граница обычно размечена специальными токенами (`<think>...</think>`, `<|thinking|>...<|answer|>`).
- `max_tokens` нужно ставить большим (десятки тысяч) — иначе reasoning обрежется на середине.
- Streaming разделяет ответ на content-блоки `thinking` и `answer`.
- Provider может скрыть thinking от пользователя (см. Q16).

**Что НЕ меняется:** attention, FFN, positional embeddings, словарь. Это та же трансформерная архитектура — отсюда важный практический вывод: reasoning не требует переписывать инфраструктуру inference, тот же стек (vLLM, SGLang) работает.

## Q5. Что такое thinking tokens?

**Thinking tokens** — это output-токены, которые модель генерирует **до** финального ответа; они и есть её внутреннее рассуждение. Технически это обычные сгенерированные токены, поэтому и важны три практических следствия ниже.

**Что нужно про них помнить:**
- **Платишь за них как за обычный output** — это часть счёта, а не «бесплатное размышление».
- В финальном UI обычно не показываются (зависит от provider — см. Q16).
- На сложных задачах могут составлять **>95%** всего output. То есть основная стоимость запроса — это размышление, а не сам ответ.

**Пример (Anthropic Claude 3.7 extended thinking):**
```json
{
  "content": [
    {"type": "thinking", "thinking": "Let me analyze... if x=5 then... wait, that gives..."},
    {"type": "text", "text": "The answer is 42."}
  ],
  "usage": {
    "input_tokens": 200,
    "output_tokens": 8500,
    "thinking_tokens": 8200,
    "text_tokens": 300
  }
}
```

**В OpenAI o1** thinking tokens видны только в `usage.completion_tokens_details.reasoning_tokens` и кратком summary в трейсе.

## Q6. (!) Как обучают reasoning model?

Коротко: берут обычную base-модель и **доучивают её рассуждать через RL** на задачах с проверяемым ответом. Сердце процесса — шаг 3; остальные этапы либо готовят модель, либо полируют результат.

**Типичный pipeline (упрощённо):**

1. **Pre-training** — обычная base model (Llama-like, GPT-like).
2. **Cold-start SFT** — короткий fine-tune на небольшом наборе human-curated reasoning-примеров. Нужен не для способности рассуждать, а для *читаемости*: задаёт формат, язык и структуру вывода.
3. **Reasoning RL** — главный этап, где и рождается reasoning:
   - Задачи с **проверяемым ответом** (math, code, logic puzzles) — иначе нечем считать reward.
   - Reward = `+1` за верный ответ, `-1` за неверный, плюс штраф за нарушение формата.
   - Алгоритм: `PPO` или `GRPO` (последний — у DeepSeek, см. Q7).
   - Здесь происходит главное: модель **сама обнаруживает**, что длинное рассуждение повышает шанс верного ответа → CoT органически растёт от десятков до тысяч токенов. Никто не просит её «думать дольше» — это выгодно для reward, и она к этому приходит.
4. **General RLHF** — финальная полировка под human preferences (helpfulness, safety).
5. **Distillation** (опционально) — большая RL-обученная модель учит маленькую на своих reasoning traces (см. Q12).

**Эмерджентное поведение** (особенно ярко в R1-Zero): без всяких подсказок модель в процессе RL сама вырабатывает приёмы хорошего решателя:
- Self-verification (`Wait, let me check...`).
- Backtracking (`Hmm, that's wrong, let me try another approach`).
- Несколько попыток решения подряд.

Именно эта самопроявляющаяся «метакогниция» и отличает reasoning model от LLM, которой просто сказали рассуждать по шагам.

## Q7. (!) Что такое GRPO и чем отличается от PPO?

**GRPO (Group Relative Policy Optimization)** — RL-алгоритм из DeepSeek-Math/R1. Главная идея: **избавиться от value model** в PPO, заменив её сравнением ответов внутри группы. Это вдвое снижает память и делает обучение дешевле — что и позволило DeepSeek получить frontier-reasoning относительно недорого.

**PPO (классика RLHF):**
- Требует **value model** — отдельную сеть размером примерно с саму policy, которая оценивает ожидаемую награду (expected return).
- Дорого: эта сеть удваивает потребление памяти и compute.

**GRPO:**
- **Value model не нужна вообще.**
- Для каждого prompt сэмплирует группу из `G` ответов.
- Reward ответа `i` нормализуется относительно среднего и std по группе:

```
advantage_i = (r_i - mean(r_group)) / std(r_group)
```

- Группа сама играет роль baseline: сигнал получается **относительным** («лучше или хуже соседей»), и отдельный критик не нужен.

**Плюсы:**
- ~50% экономии памяти — нет value model.
- Проще тюнить (меньше движущихся частей).
- Отлично сочетается с проверяемыми reward'ами (math, code).

**Минусы:**
- Нужна группа из нескольких rollouts на каждый prompt → больше inference на шаг обучения.
- При шумном reward сигнал может быть нестабильным.

**Аналогия:** PPO спрашивает «насколько хорош этот ответ сам по себе?» и для этого держит отдельного оценщика. GRPO спрашивает «насколько этот ответ лучше остальных из той же пачки?» — и оценщик ему для этого не требуется.

## Q8. Synthetic reasoning data — как генерируют?

Ключевая идея почти всех методов одна: **модель сама генерирует рассуждения, а проверяемость ответа отсеивает мусор**. Раз правильность можно проверить автоматически, можно нагенерировать гору решений и оставить только верные — получается дешёвый и качественный training set.

**Подходы:**

1. **Self-generated CoT** — сильная модель решает задачи с CoT; оставляем только те трейсы, где финальный ответ верный → они и идут в обучение.
2. **Reject sampling** — генерируем N решений на задачу и оставляем только корректные (тот же принцип, акцент на отбраковке).
3. **Process reward models (PRM)** — модель оценивает **каждый шаг** рассуждения, а не только финальный ответ; трейсы фильтруются по корректности на уровне шагов. Дороже, но ловит «правильный ответ из неправильного хода».
4. **Distillation from reasoning model** — R1-Distill: взяли трейсы от R1 (671B) и обучили на них Llama-8B/70B, Qwen-7B/32B (см. Q12).

**Откуда берут сами задачи:**
- Готовые math-датасеты (MATH, GSM8K, AIME).
- Competitive programming (Codeforces, LeetCode).
- Научное рассуждение (GPQA).
- Процедурно сгенерированные головоломки.

**Масштаб:** для R1 заявлены сотни тысяч reasoning traces. OpenAI свои цифры не раскрывает.

## Q9. Reward model в reasoning training?

Reward в reasoning-обучении бывает двух видов, и выбор между ними — фундаментальный компромисс между **честностью** и **охватом задач**.

**1. Rule-based / verifier-based reward** (DeepSeek-R1, AlphaProof) — награда из детерминированной проверки:
- Math — численно сверить ответ.
- Code — прогнать тесты.
- Format — убедиться, что есть `<think>` и финальный answer.
- Дёшево, точно и **неподкупно**: такой reward невозможно «обхитрить» (gaming), потому что он не угадывает качество, а проверяет факт.

**2. Learned reward model** (как в обычном RLHF) — отдельная обученная модель предсказывает оценку качества:
- Process Reward Model (PRM) — оценивает каждый шаг рассуждения.
- Outcome Reward Model (ORM) — оценивает только финальный ответ.

**DeepSeek-R1 сделал ставку на rule-based** — и это одна из причин его успеха: reward честный, нет reward hacking, модель оптимизирует именно правильность, а не «как понравиться оценщику».

**Компромисс:** rule-based работает только там, где ответ проверяем (математика, код, формальная логика). Для creative writing и open-ended рассуждений проверить нечем — там приходится возвращаться к learned reward со всеми его рисками gaming.

## Q10. (!) Что такого революционного в DeepSeek-R1?

**DeepSeek-R1** (январь 2025) — первая **open-weight** reasoning model, сопоставимая по качеству с OpenAI o1. До неё frontier-reasoning был закрытой технологией одной-двух лабораторий; R1 разом открыл и веса, и рецепт обучения.

**Что именно прорвало:**

1. **Открытые веса под MIT-like license** — впервые SOTA-reasoning стал доступен всем, в том числе для self-host и коммерции.
2. **GRPO** — новый RL-алгоритм дешевле PPO (см. Q7), что сделало такое обучение посильным.
3. **R1-Zero** — чистый RL без SFT с эмерджентным reasoning: доказали, что cold-start SFT для *появления* способности не обязателен (см. Q11).
4. **Линейка distill-моделей** — R1-Distill-Llama-8B/70B, R1-Distill-Qwen-1.5B/7B/14B/32B; младшие запускаются на consumer GPU (см. Q12).
5. **Цена** — API примерно в 30× дешевле o1, а self-host опускает её ещё ниже.
6. **Прозрачность** — статья детально описывает pipeline обучения, ablations и провалы, а не только победные цифры.

**Архитектура R1:**
- MoE (Mixture of Experts): **671B параметров всего**, но лишь ~**37B активны** на каждый токен — отсюда приемлемая стоимость inference при огромной ёмкости.
- Контекст 128k.
- Видимый reasoning через теги `<think>...</think>`.

**Эффект на рынок:** за пару месяцев после релиза появились десятки fine-tune'ов и distill'ов, а цена reasoning на API-рынке упала в разы — конкуренты были вынуждены резать прайс.

## Q11. R1-Zero vs R1 — в чём разница?

Разница в одном: **R1-Zero — это чистый эксперимент «может ли RL в одиночку породить reasoning», а R1 — production-модель, где тот же reasoning ещё и причёсан до читаемого вида.**

**R1-Zero:**
- **Чистый RL** поверх base DeepSeek-V3, вообще без supervised fine-tuning (SFT).
- Reward — только correctness + format, ничего больше.
- **Эмерджентный reasoning** — модель сама, без примеров, развила длинные CoT, self-verification и backtracking.
- **Цена этой чистоты — плохая читаемость:** смешивает языки в одном ответе, странно форматирует вывод. Способность есть, но пользоваться неприятно.

**R1 (production)** — добавляет этапы причёсывания вокруг того же RL:
- **Cold-start SFT** на ~тысяче curated-примеров → задаёт читаемую структуру и язык.
- **Затем reasoning RL** (GRPO) — собственно прокачка рассуждения.
- **Затем rejection sampling + SFT** на собственных корректных трейсах.
- **Финальный RLHF** под helpfulness и safety.
- Итог: качество reasoning как у R1-Zero, но при этом читаемый и опрятный вывод.

**Главный урок R1-Zero для индустрии:** способность рассуждать — **эмерджентна**; её рождает связка «достаточно большая модель + правильный RL». SFT нужен не для *появления* reasoning, а только для UX (читаемость, формат).

## Q12. (!) R1-Distill — как уместить reasoning в 8B модель?

**Суть в одной фразе:** маленькую модель не обучают рассуждать с нуля через дорогой RL, а **показывают ей готовые рассуждения большой модели и просят имитировать**. Дорогой RL делается один раз — на teacher; студенты получают навык почти даром, обычным SFT.

**Pipeline R1-Distill:**
1. Teacher — сам R1 (671B).
2. Им сгенерировали ~800k высококачественных reasoning traces (math, code, logic).
3. На этих трейсах делают обычный SFT (supervised fine-tuning) поверх готовых base-моделей:
   - Llama-3.1-8B → **R1-Distill-Llama-8B**.
   - Llama-3.3-70B → **R1-Distill-Llama-70B**.
   - Qwen-2.5-1.5B/7B/14B/32B → R1-Distill-Qwen-*.
4. **Никакого отдельного RL** на этапе distill — только имитация трейсов teacher'а.

**Результаты:**
- R1-Distill-Qwen-32B обходит o1-mini на многих math-бенчмарках.
- R1-Distill-Llama-8B запускается на потребительском GPU (24GB VRAM в 4-bit quant).

**Компромисс:** distilled-модель **хуже оригинального R1** (полноценного RL у неё не было), но **радикально лучше базового Llama-8B** на reasoning-задачах. Для self-host это очень удачное соотношение compute/quality.

**Почему это важно:** reasoning-модели до 10B параметров делают реальным **локальный edge-деплой** — рассуждение без облачного API, без сетевой задержки и без утечки данных наружу.

## Q13. (!) OpenAI o-series API — особенности?

```python
from openai import OpenAI

client = OpenAI()

response = client.chat.completions.create(
    model="o1",  # or o1-mini, o3-mini, o3
    messages=[
        {"role": "user", "content": "Prove that sqrt(2) is irrational."}
    ],
    max_completion_tokens=10000,  # НЕ max_tokens
    reasoning_effort="medium"     # low | medium | high (o3-mini+)
)

print(response.choices[0].message.content)

# Reasoning tokens видны в usage
print(response.usage.completion_tokens_details.reasoning_tokens)
```

**Особенности, на которых легко споткнуться:**
- `max_completion_tokens` вместо `max_tokens` — и он включает в себя и thinking, и сам ответ. Поставишь маленьким — обрежешь рассуждение.
- **`reasoning_effort`** (o3-mini и новее): `low/medium/high` — прямой управляющий рычаг на размер thinking budget.
- **`system`-сообщение** в o1-preview сначала не поддерживалось; сейчас developer/system-роль работает.
- **Streaming** — изначально не было, добавили в 2025.
- **Tools / function calling** — изначально не было, добавили в 2025.
- **Hidden CoT** — пользователь не видит сам thinking-контент, только число токенов в usage и краткое summary (см. Q16).
- **`temperature`, `top_p`** — игнорируются: для стабильности рассуждения они зафиксированы.

**Pricing (на момент 2025):**
- `o1` ~ $15/$60 per 1M input/output (vs gpt-4o $2.50/$10).
- `o1-mini` ~ $3/$12.
- `o3-mini` ~ $1.10/$4.40.

## Q14. (!) Anthropic extended thinking — как работает?

```python
import anthropic

client = anthropic.Anthropic()

response = client.messages.create(
    model="claude-3-7-sonnet-20250219",
    max_tokens=20000,
    thinking={
        "type": "enabled",
        "budget_tokens": 10000  # сколько токенов разрешить на thinking
    },
    messages=[
        {"role": "user", "content": "Solve this Sudoku: ..."}
    ]
)

for block in response.content:
    if block.type == "thinking":
        print("THINKING:", block.thinking)
    elif block.type == "text":
        print("ANSWER:", block.text)
```

Главное отличие от OpenAI: у Anthropic **thinking видимый** — рассуждение возвращается отдельными блоками, и им можно управлять явным бюджетом токенов.

**Особенности:**
- **`budget_tokens`** — потолок на thinking; модель вправе потратить меньше, если задача проще.
- **Блоки `thinking` видны** в ответе — противоположный OpenAI подход.
- **Streaming** работает: сначала идут thinking-дельты, затем text-дельты.
- **Tools** поддерживаются одновременно с thinking.
- **Цена thinking-токенов** равна цене output-токенов — они тоже в счёте.
- В multi-turn **прошлый thinking обратно в контекст не отправляется** — смысла нет, на следующем ходу модель всё равно думает заново, а так экономятся токены.

**Когда брать:**
- Сложные задачи, требующие цепочки рассуждений.
- Когда нужно **проверить ход рассуждения** (thinking виден) — аудит, объяснимость.
- При отладке качества: видно, на каком шаге модель свернула не туда.

## Q15. DeepSeek-R1 API — `<think>` теги?

```python
from openai import OpenAI

# DeepSeek API совместим с OpenAI SDK
client = OpenAI(
    api_key="...",
    base_url="https://api.deepseek.com"
)

response = client.chat.completions.create(
    model="deepseek-reasoner",  # = R1
    messages=[{"role": "user", "content": "What is 17 * 23?"}]
)

content = response.choices[0].message.content
# Содержит и thinking, и answer
# Структура:
# <think>
# Let me compute 17 * 23. 17 * 20 = 340. 17 * 3 = 51. 340 + 51 = 391.
# </think>
# 17 * 23 = 391
```

Главное на практике: API **совместим с OpenAI SDK**, а рассуждение приходит видимым внутри тегов `<think>`.

**Особенности DeepSeek API:**
- **OpenAI-совместимые** эндпоинты — переезд с OpenAI это смена `base_url` и имени модели.
- Теги **`<think>...</think>`** встроены прямо в текст ответа — старый способ требует их парсить вручную.
- В новых версиях SDK для этого добавили отдельное поле `reasoning_content`:
  ```python
  print(response.choices[0].message.reasoning_content)  # thinking
  print(response.choices[0].message.content)            # final answer
  ```
- **Цена кратно ниже** OpenAI: примерно $0.55/$2.19 за 1M input/output.
- Можно **развернуть у себя** — веса открыты под MIT-like license.

## Q16. Hidden vs visible thinking — почему OpenAI скрывает CoT?

Короткий ответ: OpenAI прячет CoT, чтобы конкуренты не растащили его на обучающие данные и чтобы «сырое» размышление не утекало пользователю. Это сознательный выбор в пользу **защиты от distillation** в ущерб прозрачности — и индустрия по этому вопросу разделилась.

**OpenAI — скрывает.** Пользователь видит только финальный ответ и краткое summary thinking в трейсе. Почему (по официальной позиции):
1. **Защита от distillation** — публичный CoT конкуренты могли бы собрать в готовый training-set (ровно то, что сделали с R1).
2. **Safety / alignment** — сырой CoT может содержать «невыровненное» рассуждение: модель вслух обдумывает вредный вариант, прежде чем его отвергнуть, и показывать это нежелательно.
3. **UX** — длинный CoT — это шум; среднему пользователю не нужны 10k токенов размышлений.

**Anthropic — показывает.** Thinking возвращается явно. Почему:
1. **Прозрачность** — пользователь может проверить ход рассуждения.
2. **Отладка** — разработчик видит, где модель ошиблась.
3. **Доверие** — видимому reasoning легче верить.

**DeepSeek — показывает через теги.** Подход близкий к Anthropic: теги `<think>` видны и парсятся. Веса всё равно открыты, поэтому скрывать CoT смысла нет — distillation возможна и так.

**Компромисс:** скрытый CoT даёт конкурентный ров (moat) и чистый UX; видимый — прозрачность и доверие сообщества. Универсально «правильного» выбора пока нет.

## Q17. (!) Когда reasoning model даёт реальное преимущество?

Одной фразой: **там, где ответ нельзя «вспомнить», его нужно вывести** — задача многошаговая, с возможностью самопроверки и отката. Чем длиннее цепочка верных шагов нужна, тем сильнее выигрыш.

| Сценарий | Reasoning model полезен? | Почему |
|----------|--------------------------|--------|
| Математика (олимпиадная) | Сильно (10-50% точности) | Multi-step, требует self-verification |
| Competitive programming | Сильно | Edge cases, planning перед кодом |
| Scientific reasoning (GPQA, физика) | Сильно | Multi-step синтез знаний |
| SWE-Bench (реальные баги) | Сильно (claude 3.7 thinking + o3 — SOTA) | Анализ кода, гипотезы, проверки |
| Planning / agent decomposition | Сильно | Многошаговое планирование |
| Logical puzzles, Sudoku, ARC-AGI | Очень сильно | Где обычная LLM «срывается» в guessing |
| Legal/medical reasoning | Полезно | Нужны цепочки рассуждений |
| Сложные SQL / data analysis | Полезно | Многошаговые joins, edge cases |

**Общий признак всех строк выше:** задача требует **многошагового вывода** с самопроверкой и backtracking. Если же ответ — это просто извлечённый факт, reasoning не добавит почти ничего (см. Q19).

## Q18. (!) Когда reasoning model избыточна или вредна?

Reasoning стоит дороже и медленнее, поэтому он избыточен везде, где задача и так решается обычной LLM за пару секунд. А в латентно-чувствительных и творческих сценариях он не просто бесполезен — он **вреден**: убивает UX задержкой или «засушивает» текст лишним анализом.

| Сценарий | Причина избыточности |
|----------|----------------------|
| Простой Q&A (`Какая столица Франции?`) | Overkill — 10× дороже за тот же ответ |
| Краткие summaries | Reasoning не помогает, добавляет latency |
| Creative writing / fiction | Может «портить flow» излишним анализом стиля |
| Чат small-talk | Латентность 30+ сек убивает UX |
| Real-time UI (autocomplete, suggestions) | Латентность недопустима |
| Простые classification tasks | Обычная маленькая модель справится |
| Translation коротких текстов | Reasoning не нужен |
| Запросы к function calling (single tool call) | Routing к нужному tool не требует CoT |

**Эмпирическое правило:** если задача решается обычной LLM за 1-2 секунды с приемлемой точностью — reasoning model не нужна. Берись за неё только когда нужны планирование, самопроверка или многошаговый синтез.

## Q19. Бенчмарки: где reasoning доминирует, где нет?

Картина по бенчмаркам чёткая: reasoning-модели **отрываются на задачах вывода** (математика, код, наука) и почти **не отличаются от обычных LLM на задачах припоминания фактов**. Дальше — конкретные цифры.

**Где reasoning даёт огромный отрыв:**

| Benchmark | gpt-4o | o1 | o3 | R1 | Прирост |
|-----------|--------|----|----|-----|--------|
| AIME 2024 (math) | ~13% | ~83% | ~96% | ~80% | +60-80% |
| MATH-500 | ~76% | ~94% | ~97% | ~97% | +20% |
| GPQA (PhD-level science) | ~50% | ~78% | ~88% | ~71% | +20-40% |
| Codeforces percentile | ~11% | ~89% | ~99.95% | ~96% | +80+ |
| SWE-Bench Verified | ~33% | ~41% | ~71% | ~49% | +10-40% |
| ARC-AGI | ~5% | ~25% | ~75-87% | ~15% | +70 |
| FrontierMath | <2% | <2% | ~25% | <5% | +20+ |

**Где разница маленькая:**

| Benchmark | gpt-4o | o1 | Разница |
|-----------|--------|----|---------| 
| MMLU (general knowledge) | ~88% | ~92% | +4% |
| TriviaQA | ~92% | ~93% | +1% |
| HellaSwag | ~95% | ~95% | 0 |

**Вывод:** reasoning бесполезен там, где задача — **припомнить факт** (MMLU, TriviaQA, HellaSwag: разница в пределах шума). И решает там, где нужно **синтезировать знание или вывести ответ по шагам**.

## Q20. o3 на ARC-AGI — почему это важно?

**ARC-AGI** (Abstract Reasoning Corpus for AGI, François Chollet, 2019) — бенчмарк, специально сконструированный так, чтобы его **нельзя было взять зубрёжкой**. Задачи — визуальные паттерны (grid → grid): из 2-3 примеров нужно вывести правило и применить к новому входу. Это проверка абстрактного рассуждения, а не объёма памяти.

**До декабря 2024 LLM на нём буксовали:**
- Люди: ~85%.
- gpt-4o: ~5%.
- Лучшие узкоспециализированные решения (после года работы): ~55%.

**o3 (декабрь 2024) — резкий скачок:**
- Low compute mode: **75%**.
- High compute mode: **87%**, но ценой ~$3000 compute на одну задачу.

**Почему это важно:**
1. ARC считался «лакмусовой бумажкой AGI» — LLM долго топтались на единицах процентов.
2. o3 доказал, что связка **test-time compute scaling + reasoning RL** способна решать задачи на абстрактное рассуждение, а не только на знакомые паттерны.
3. Ценник high-compute режима (~$3k за задачу) ярко показал новую реальность: **дорогое размышление становится главным компонентом frontier-моделей**.

**Что важно держать в голове (контраргументы):**
- Бенчмарк обновили: на ARC-AGI-2 модели снова в самом начале пути — значит, прошлый результат не «решённый AGI».
- Цена 87% намекает, что это во многом **brute-force перебор**, а не дешёвое и эффективное рассуждение.

## Q21. (!) Сравнение o1 vs o3 vs R1 vs Claude 3.7 thinking по cost/latency?

| Модель | Cost (per 1M in/out) | Latency (typical) | Open weights | Visible thinking | Лидирует в |
|--------|----------------------|-------------------|--------------|------------------|------------|
| **OpenAI o1** | $15 / $60 | 10-60 сек | No | No | Math, science |
| **OpenAI o1-mini** | $3 / $12 | 5-20 сек | No | No | Code, фоновое reasoning |
| **OpenAI o3** | ~$15 / $60 (o3 high), варьируется | 20-90 сек | No | No | ARC-AGI, FrontierMath, SWE-Bench |
| **OpenAI o3-mini** | $1.10 / $4.40 | 3-15 сек | No | No | Дешёвое reasoning |
| **DeepSeek-R1** | $0.55 / $2.19 | 10-40 сек | **Yes (MIT)** | **Yes** (`<think>`) | Math/code, OSS |
| **Claude 3.7 Sonnet thinking** | $3 / $15 + thinking | 5-30 сек | No | **Yes** | SWE-Bench, structured reasoning |
| **Gemini 2.5 Pro Deep Think** | $1.25 / $10 (var.) | 10-40 сек | No | Partial | Multi-modal reasoning |
| **QwQ-32B** | self-host | 5-20 сек (GPU) | **Yes** | **Yes** | Edge reasoning |

**Как читать таблицу — выводы для выбора модели:**
- **R1 примерно в 30× дешевле o1** при сопоставимой точности на math/code — дефолт, если важна цена и допустим open-weight.
- **o3 — это frontier** (лучшее качество на сложнейших задачах), но дорогой; берут, когда нужно «лучшее любой ценой».
- **Claude 3.7 thinking** — лучший баланс для продакшена: видимый thinking + умеренная цена + приемлемая скорость.
- **o1-mini / o3-mini** — sweet spot для массового inference, где reasoning нужен, но бюджет ограничен.

## Q22. Сколько токенов «съедает» reasoning?

Прямой ответ: **от пары сотен до сотен тысяч токенов — на 2-3 порядка зависит от сложности задачи**. Это и определяет основную статью расходов: платишь в первую очередь за размышление, а не за финальный текст.

| Тип задачи | Thinking tokens (типично) |
|------------|---------------------------|
| Простой вопрос («что такое X») | 100-500 |
| Средняя задача (multi-step math) | 1k-5k |
| Сложная задача (AIME, Codeforces) | 5k-30k |
| ARC-AGI / FrontierMath | 30k-200k+ |

**Что из этого следует на практике:**
- В счёте **основная стоимость — это thinking**, а не финальный output.
- Слишком жёсткий лимит (`max_completion_tokens=4000`) на сложной задаче **обрежет рассуждение на середине** → модель не успеет «додумать» и выдаст плохой ответ. Ставь крупные лимиты (16k-64k).
- `thinking.budget_tokens` (Anthropic) позволяет осознанно балансировать качество против стоимости — больше бюджет — лучше ответ, но дороже.

**Пример расчёта:**
- 1000 запросов в день, средний reasoning 5k tokens, o1 ($60/1M output).
- Месячная стоимость только thinking: `1000 × 30 × 5000 × $60 / 1_000_000 ≈ $9000`.
- Та же нагрузка на R1: `~$330`.

## Q23. (!) Best-of-N, Tree-of-Thoughts, Self-Consistency — заменяют ли reasoning model?

Короткий ответ: **нет, не заменяют — обученная reasoning model сильнее их всех.** Эти inference-техники (Self-Consistency, Best-of-N, ToT) были основным инструментом до 2024-го и остаются полезными поверх обычных LLM, но дают **меньший** прирост, чем модель, у которой стратегии reasoning вшиты в веса.

| Техника | Идея | Прирост vs base LLM | Накладные расходы |
|---------|------|---------------------|---------------|
| **Self-Consistency** | N CoT, majority vote финального ответа | +10-15% на math | ×N |
| **Best-of-N + verifier** | N генераций, выбрать лучшую по verifier model | +15-25% | ×N + verifier |
| **Tree-of-Thoughts** | Дерево reasoning, BFS/DFS по веткам | +20-30% на planning | ×N×depth |
| **PRM-guided search** | Process reward model направляет поиск | +25-35% | High |
| **Dedicated reasoning model (o1)** | RL-trained long CoT | +40-80% | Bundled |

**Почему dedicated-модель выигрывает:**
- Она **обучена самим стратегиям** (self-verification, backtracking) — это умнее, чем случайно сэмплировать N попыток и надеяться на удачу.
- Один inference-вызов вместо N с внешней координацией и голосованием.
- Её reasoning-токены **дешевле**, чем N полных генераций обычной модели.

**Когда inference-техники всё ещё нужны:**
- У тебя только обычная LLM, reasoning-модели нет под рукой.
- Self-hosting на старой архитектуре без reasoning-весов.
- В критичных приложениях — как *дополнительный* verifier поверх reasoning model для двойной проверки (одно другому не мешает).

## Q24. (!) Routing: когда отправлять в reasoning model?

Главная идея: **не гонять всё через дорогую reasoning-модель, а ставить перед ней маршрутизатор**, который дешёвые запросы отдаёт быстрой LLM, а сложные — reasoning. Это и есть способ совместить качество с разумной ценой. Решает «куда отправить» либо набор эвристик, либо лёгкий classifier.

```python
def route_query(query: str) -> str:
    # 1. Эвристики
    if len(query) < 30 and is_simple_factual(query):
        return "gpt-4o-mini"  # быстрый ответ
    if any(kw in query.lower() for kw in ["prove", "solve", "calculate", "design"]):
        return "o1"  # явный reasoning task
    # 2. Classifier (маленькая модель)
    complexity = small_classifier(query)  # easy/medium/hard
    return {
        "easy":   "gpt-4o-mini",
        "medium": "gpt-4o",
        "hard":   "o1"
    }[complexity]
```

**Подходы к маршрутизации в продакшене:**
- **LLM-as-router** — решение принимает маленькая модель (haiku, gpt-4o-mini).
- **Embedding-based router** — кластеризация семантически похожих запросов, маршрут по кластеру.
- **Cost-aware router** — учитывает остаток бюджета на пользователя.
- **Escalation** — сначала пробуем gpt-4o; если уверенность ответа низкая → эскалируем в o1. Часто самый практичный вариант: платим за reasoning только когда дешёвая модель не справилась.

**По каким метрикам оценивать router:**
- Качество ответов именно на «hard»-запросах (не деградировало ли).
- Средняя стоимость на запрос.
- Latency p50/p95.
- Доля ложных срабатываний — отправили в reasoning там, где он был не нужен (переплата).

## Q25. (!) Prompt engineering для reasoning models — что меняется?

Главный сдвиг: с reasoning-моделью **перестаёшь учить её *как* думать и начинаешь чётко формулировать *что* нужно**. Все старые CoT-приёмы («think step by step», подробные инструкции по ходу решения) теперь в лучшем случае бесполезны, в худшем — мешают её собственным, более сильным стратегиям.

**Чего делать НЕ нужно:**
- `Let's think step by step` — **бесполезно**: модель и так думает, причём лучше.
- Подробные CoT-инструкции — могут **перебивать** обученные стратегии и ухудшать результат.
- Few-shot-примеры с reasoning — обычно не помогают, иногда делают хуже.

**Что действительно важно:**
1. **Чёткая постановка задачи** — вход, ожидаемый формат вывода, ограничения.
2. **Role / system prompt** — по-прежнему работает (`You are a math tutor...`).
3. **Формат вывода** — описать явно (`Respond in JSON: {answer: number, confidence: float}`).
4. **Не навязывать путь решения** — модель найдёт его сама лучше тебя.
5. **`reasoning_effort`** (o-series) или **`budget_tokens`** (Anthropic) — главный рычаг качества вместо текстовых уговоров.

**Хороший prompt для reasoning model:**

```
You are an expert competitive programmer.

Task: Solve the following problem. Return Python code that passes all test cases.

Constraints:
- n up to 10^6
- Time limit: 2 seconds

Problem: [описание задачи]
```

**Плохой prompt (over-engineered):**

```
Let's think step by step. First, analyze the problem. 
Then, consider edge cases. Then, write pseudocode. 
Then translate to Python. Use the following template...
```

## Q26. Hallucination и rationalization в reasoning models?

**Парадокс reasoning-моделей:** длинная цепочка рассуждений делает неверный ответ *убедительнее*, а не вернее. Модель может **рационализировать** ошибку правдоподобной аргументацией — и это опаснее короткой ошибки обычной LLM: пользователь видит «солидное обоснование» и доверяет ему.

**Типичные failure modes:**
- **Confirmation bias в CoT** — модель делает ранний guess, а дальше «подгоняет» под него остальные шаги.
- **Hallucinated lemmas** — выдумывает теоремы и факты, на которые потом опирается.
- **Goal misgeneralization** — оптимизирует под reward (например, «выглядеть уверенно»), а не под истинную корректность.
- **Sycophancy** — соглашается с пользователем, даже когда тот неправ.

**Как защищаться:**
- **Verifier model** — отдельная LLM проверяет ответ.
- **Self-critique** — отдельным вызовом: `Critique your previous answer`.
- **Несколько сэмплов + majority vote** (self-consistency поверх reasoning model).
- **Внешняя проверка** — для math/code просто запустить вычисление или тесты (самая надёжная защита, когда применима).
- **Process reward model** — оценивать каждый шаг, если PRM доступна.

**Главное правило:** на критичных задачах **не доверяй ответу без независимой проверки** — даже если thinking выглядит безупречно убедительным. Убедительность ≠ правильность.

## Q27. Streaming thinking — улучшает UX?

**Да, заметно — особенно на длинных рассуждениях.** Корень проблемы: reasoning может занять 60 секунд, и без streaming пользователь всё это время смотрит на «думает...», не понимая, завис процесс или работает. Поток thinking-токенов снимает эту неопределённость.

**Реализация (Anthropic):**

```python
with client.messages.stream(
    model="claude-3-7-sonnet-20250219",
    thinking={"type": "enabled", "budget_tokens": 10000},
    messages=[...]
) as stream:
    for event in stream:
        if event.type == "content_block_delta":
            if event.delta.type == "thinking_delta":
                print("💭", event.delta.thinking, end="", flush=True)
            elif event.delta.type == "text_delta":
                print(event.delta.text, end="", flush=True)
```

**UI-паттерны:**
- **Индикатор размышления** — «Claude is thinking...» с прогрессом (потрачено токенов / бюджет).
- **Сворачиваемая секция thinking** — показывать по запросу, чтобы не шуметь.
- **Summary thinking** — модель сама сжимает своё рассуждение в 1-2 предложения для пользователя.

**OpenAI o-series:** в новых версиях SDK streaming есть, но **сам thinking-контент не отдаётся** — только итоговое summary.

**Почему streaming так помогает — TTFT против воспринимаемой задержки:**
- Полная задержка: 60 сек.
- TTFT (первый thinking-токен): 1-2 сек.
- TTFT финального ответа: 30-50 сек.

Со стримингом **воспринимаемая** задержка падает с 60 секунд до ~TTFT первого токена (1-2 сек) — фактическое время не меняется, но ощущение отзывчивости радикально лучше.

## Q28. Reasoning + tools/function calling — как работает?

Изначально o1 **не поддерживал tools**, но в 2025 поддержка появилась. Ключевая особенность связки reasoning + tools: модель **планирует цепочку вызовов прямо внутри thinking**, а не дёргает инструменты вслепую — отсюда её сила в многошаговых агентных задачах.

**Anthropic Claude 3.7 thinking + tools:**

```python
response = client.messages.create(
    model="claude-3-7-sonnet-20250219",
    thinking={"type": "enabled", "budget_tokens": 5000},
    tools=[{"name": "search_web", "input_schema": {...}}],
    messages=[{"role": "user", "content": "Что сейчас в Москве по погоде и +5 дней?"}]
)
# Модель в thinking планирует tool calls, потом возвращает tool_use blocks
```

**Как это работает:** модель **внутри thinking** строит план (`Мне нужно вызвать search_web, потом проанализировать ответ, потом ещё раз...`) и только затем **возвращает** конкретные tool calls.

**Цикл multi-turn:**
1. User → модель (thinking + tool calls).
2. Приложение выполняет инструменты.
3. Результаты инструментов → модель.
4. Модель снова думает: либо завершает, либо запрашивает ещё tool calls.

**Чем отличается от обычного function calling:**
- Reasoning model **глубже планирует цепочку вызовов** — это её преимущество в multi-step research.
- **Дороже** — за счёт thinking-токенов.
- Выигрывает на **сложных агентных задачах** (research, debugging).
- Избыточна для **одиночного tool call** (простой routing к нужному инструменту CoT не требует).

## Q29. Open-weight reasoning models — что доступно?

С релизом R1 open-weight-reasoning перестал быть пустой нишей: сегодня есть линейка от edge-размеров (1.5B) до frontier-MoE (671B), под пермиссивными лицензиями. Выбор сводится к балансу «размер модели ↔ доступное железо ↔ качество».

| Модель | Параметры | Лицензия | Особенности |
|--------|-----------|----------|-------------|
| **DeepSeek-R1** | 671B MoE / 37B active | MIT-like | Frontier OSS reasoning |
| **R1-Distill-Llama-70B** | 70B | Llama license | Distill, mainstream GPU |
| **R1-Distill-Llama-8B** | 8B | Llama license | Consumer GPU |
| **R1-Distill-Qwen-32B** | 32B | Apache 2.0 | Превосходит o1-mini на math |
| **R1-Distill-Qwen-1.5B** | 1.5B | Apache 2.0 | Edge / mobile |
| **QwQ-32B (Alibaba)** | 32B | Apache 2.0 | Reasoning от Qwen team |
| **Llama-3.3-Nemotron-Reasoning** | 49B | Llama license | Nvidia fine-tune |
| **OpenThinker** | 7B-32B | Apache 2.0 | Open reproduction R1 |

**Как разворачивать:**
- **vLLM, SGLang, TGI** — production-inference.
- **Ollama, LM Studio** — локально на десктопе.
- **Together.ai, Fireworks, DeepInfra** — managed-хостинг open-весов; дешевле закрытых API без своих GPU.

**Компромисс против закрытых reasoning-моделей:**
- **Плюсы:** приватность данных, кастомизация, нет rate limits; дешевле на больших объёмах.
- **Минусы:** уступают o3 на frontier-бенчмарках; ложатся операционные расходы (GPU, эксплуатация).

## Q30. (!) Reasoning models и agents — future?

Прямой ответ: **reasoning-модели — главный фундамент для агентов, и связка «reasoning + agents» определяет повестку 2025-2026.** Причина проста — без способности планировать и проверять себя агент рассыпается на длинной цепочке шагов.

**Куда движется развитие:**
1. **Long-horizon planning** — reasoning-модель планирует и выполняет десятки-сотни шагов в agent loop.
2. **Autonomous research** — модель сама ищет, читает и синтезирует источники (OpenAI Deep Research, Claude research mode).
3. **Self-correcting agents** — reasoning позволяет восстановиться после ошибки инструмента, а не падать.
4. **Multi-agent reasoning** — несколько reasoning-агентов спорят и сотрудничают (debate-фреймворки).
5. **Reasoning + memory** — эпизодическая память + reasoning = агент с непрерывным обучением.

**Где это уже превращается в продукты:**
- **SWE-агенты** (Devin, Cursor agents) — рассуждение о коде + инструменты (читать файлы, гонять тесты).
- **Computer-use агенты** (Claude computer use, OpenAI Operator) — рассуждение об UI + скриншот/клик.
- **Research-агенты** — многошаговый веб-research с рассуждением об источниках.
- **Scientific discovery** — reasoning-модель + автоматизация лаборатории (в духе Coscientist).

**Что пока мешает (открытые вызовы):**
- **Стоимость** — agent loop с reasoning может стоить очень дорого за одну задачу.
- **Верификация** — как проверять корректность long-horizon-планов?
- **Безопасность** — reasoning-модель + автономные действия = высокие риски.
- **Память / контекст** — reasoning поверх 1M+ контекста дорог.

**Главная мысль:** reasoning-модели — фундаментальный enabler агентной парадигмы. Без них агенты ломаются на 3-5 шаге; с ними — осмысленно проходят 30-50 шагов.

---

## See also

- [LLM Basics](llm-basics-interview.md) — фундамент архитектуры
- [Prompt Engineering](prompt-engineering-interview.md) — CoT prompting, advanced techniques
- [AI Agents](ai-agents-interview.md) — agents on top of reasoning models
- [Function Calling](function-calling-interview.md) — tools + reasoning
- [Model Serving](model-serving-interview.md) — для self-hosted R1 / QwQ
- [MLOps](mlops-interview.md) — operations and deployment
- [LLM Integration Patterns](llm-integration-patterns-interview.md) — routing, fallback, gateway
- [RAG](rag-interview.md) — reasoning + retrieval
- [System Design](../system-design/system-design-interview.md) — architecture for reasoning-heavy systems
- [Latency Numbers](../architecture/latency-numbers-interview.md) — что считать «допустимой» latency для reasoning
