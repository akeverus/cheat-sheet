---
title: "Вопросы на собеседовании: Agentic Design Patterns"
description: "Agentic patterns: ReAct, Reflexion, Plan-Execute, Tree-of-Thoughts, Self-Critique, CoVe, ReWOO, Self-Consistency; когда что выбирать и комбинировать."
tags:
  - interview
  - ai-ml
  - agentic-patterns
type: "interview"
difficulty: "advanced"
aliases:
  - "Agentic Patterns interview"
  - "ReAct Reflexion Tree-of-Thoughts"
  - "Agent design patterns собеседование"
  - "LLM agentic workflows"
updated: "2026-05-23"
---
# Вопросы на собеседовании: `Agentic Design Patterns`

**Agentic pattern** — структурированный способ организации LLM-driven workflow с чередованием **reasoning** и **acting**. Это не одна модель и не один промпт, а **протокол управления** ходом выполнения: как агент думает, когда дергает tool, как обрабатывает ошибку, когда останавливается.

К 2026 индустрия выработала набор канонических паттернов: `ReAct`, `Reflexion`, `Plan-and-Execute`, `Tree-of-Thoughts`, `Self-Refine`, `Chain-of-Verification`, `ReWOO`, `Self-Consistency`, `Best-of-N`, `Voyager`. Реальные agent-системы редко используют один паттерн в чистом виде — комбинируют (`ReAct + Reflexion`, `Plan-Execute + Self-Critique`). На интервью спрашивают: чем отличаются, когда какой брать, как сочетаются с reasoning models (o1/R1), где anti-patterns.

## Полезные ссылки

### Original papers и авторитетные источники

- [ReAct: Synergizing Reasoning and Acting in Language Models (Yao et al. 2022)](https://arxiv.org/abs/2210.03629) — базовый Thought→Action→Observation loop
- [Reflexion: Language Agents with Verbal Reinforcement Learning (Shinn et al. 2023)](https://arxiv.org/abs/2303.11366) — self-reflection без gradient updates
- [Tree of Thoughts (Yao et al. 2023)](https://arxiv.org/abs/2305.10601) — branching reasoning + evaluation
- [Self-Refine (Madaan et al. 2023)](https://arxiv.org/abs/2303.17651) — generate → critique → refine
- [Chain-of-Verification (Dhuliawala et al. 2023)](https://arxiv.org/abs/2309.11495) — снижение hallucinations через verification questions
- [ReWOO (Xu et al. 2023)](https://arxiv.org/abs/2305.18323) — Plan/Worker/Solver без перепрогона LLM на каждом observation
- [Plan-and-Solve Prompting (Wang et al. 2023)](https://arxiv.org/abs/2305.04091) — planner + executor
- [Self-Consistency (Wang et al. 2022)](https://arxiv.org/abs/2203.11171) — majority vote по N CoT
- [Voyager (Wang et al. 2023)](https://voyager.minedojo.org/) — skill library + curriculum
- [Generative Agents (Park et al. 2023)](https://arxiv.org/abs/2304.03442) — memory stream + reflection
- [Toolformer (Schick et al. 2023)](https://arxiv.org/abs/2302.04761) — self-supervised tool use
- [Anthropic: Building Effective Agents](https://www.anthropic.com/research/building-effective-agents) — практические patterns от Anthropic

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое agentic pattern и чем он отличается от prompt-техники?](#q1--что-такое-agentic-pattern-и-чем-он-отличается-от-prompt-техники)
- [Q2. Как классифицируют agentic patterns?](#q2-как-классифицируют-agentic-patterns)
- [Q3. (!) Почему вообще нужны patterns, если есть reasoning model (o1, R1)?](#q3--почему-вообще-нужны-patterns-если-есть-reasoning-model-o1-r1)

**Базовые паттерны (ReAct / CoT / Self-Consistency)**
- [Q4. (!) Что такое ReAct и как выглядит его loop?](#q4--что-такое-react-и-как-выглядит-его-loop)
- [Q5. (!) Покажи pseudo-code ReAct-агента](#q5--покажи-pseudo-code-react-агента)
- [Q6. Что такое Self-Consistency и когда работает?](#q6-что-такое-self-consistency-и-когда-работает)
- [Q7. Auto-CoT vs Manual CoT — в чём разница?](#q7-auto-cot-vs-manual-cot--в-чём-разница)

**Reflection-based (Reflexion / Self-Critique / CoVe)**
- [Q8. (!) Что такое Reflexion и чем отличается от обычного retry?](#q8--что-такое-reflexion-и-чем-отличается-от-обычного-retry)
- [Q9. Self-Refine / Self-Critique — pattern и pseudo-code](#q9-self-refine--self-critique--pattern-и-pseudo-code)
- [Q10. (!) Chain-of-Verification (CoVe) — как снижает hallucinations?](#q10--chain-of-verification-cove--как-снижает-hallucinations)
- [Q11. Чем self-critique без external feedback опасен?](#q11-чем-self-critique-без-external-feedback-опасен)

**Plan-based (Plan-Execute / ReWOO)**
- [Q12. (!) Plan-and-Execute pattern — структура и use case](#q12--plan-and-execute-pattern--структура-и-use-case)
- [Q13. (!) Что такое ReWOO и чем выигрывает у ReAct?](#q13--что-такое-rewoo-и-чем-выигрывает-у-react)
- [Q14. Когда re-planning важнее изначального плана?](#q14-когда-re-planning-важнее-изначального-плана)

**Tree-based (ToT / Best-of-N)**
- [Q15. (!) Tree-of-Thoughts — структура дерева и evaluation](#q15--tree-of-thoughts--структура-дерева-и-evaluation)
- [Q16. BFS vs DFS в ToT — когда что?](#q16-bfs-vs-dfs-в-tot--когда-что)
- [Q17. Best-of-N sampling: pattern и trade-off с verifier](#q17-best-of-n-sampling-pattern-и-trade-off-с-verifier)

**Memory patterns (Voyager / Generative Agents)**
- [Q18. (!) Паттерн Voyager: библиотека навыков, авто-curriculum, итеративный prompting](#q18--паттерн-voyager-библиотека-навыков-авто-curriculum-итеративный-prompting)
- [Q19. Generative Agents (Stanford): поток памяти и reflection](#q19-generative-agents-stanford-поток-памяти-и-reflection)
- [Q20. OODA Loop (Observe-Orient-Decide-Act) — как адаптируется к LLM-агентам?](#q20-ooda-loop-observe-orient-decide-act--как-адаптируется-к-llm-агентам)
- [Q21. Toolformer-подход: учим модель вызывать API через self-supervision](#q21-toolformer-подход-учим-модель-вызывать-api-через-self-supervision)

**Composition и stopping**
- [Q22. (!) Композиция паттернов: ReAct + Reflexion, Plan-Execute + Self-Critique](#q22--композиция-паттернов-react--reflexion-plan-execute--self-critique)
- [Q23. (!) Stopping conditions: как агент решает, что закончить?](#q23--stopping-conditions-как-агент-решает-что-закончить)
- [Q24. Implementation: LangChain / LlamaIndex / Vercel AI SDK — где какой pattern из коробки?](#q24-implementation-langchain--llamaindex--vercel-ai-sdk--где-какой-pattern-из-коробки)

**Anti-patterns и evaluation**
- [Q25. (!) Anti-patterns: pattern без необходимости, recursive без bound, context bloat](#q25--anti-patterns-pattern-без-необходимости-recursive-без-bound-context-bloat)
- [Q26. Сравнительная таблица паттернов: complexity, latency, use case](#q26-сравнительная-таблица-паттернов-complexity-latency-use-case)
- [Q27. Какой pattern когда выбирать: ReAct / Reflexion / ToT / Self-Critique / CoVe](#q27-какой-pattern-когда-выбирать-react--reflexion--tot--self-critique--cove)
- [Q28. (!) Evaluation patterns: harness, метрики (success rate, # steps, tokens)](#q28--evaluation-patterns-harness-метрики-success-rate--steps-tokens)
- [Q29. Reasoning models делают patterns внутри — нужны ли они снаружи?](#q29-reasoning-models-делают-patterns-внутри--нужны-ли-они-снаружи)
- [Q30. Какие паттерны хорошо переносятся на multi-agent системы?](#q30-какие-паттерны-хорошо-переносятся-на-multi-agent-системы)

## Q1. (!) Что такое agentic pattern и чем он отличается от prompt-техники?

Коротко: prompt-техника — это **содержимое одного запроса**, а agentic pattern — это **протокол управления многошаговым процессом**. Первая отвечает на вопрос «как сформулировать промпт», второй — «как организовать цикл из многих вызовов LLM и инструментов».

**Agentic pattern** — структурированный протокол управления LLM-driven workflow, в котором чередуются **reasoning** (модель думает) и **acting** (модель выполняет действие — вызов tool, запрос к памяти, генерация sub-task). Pattern определяет: кто принимает решения, в каком порядке, по каким условиям повторяется цикл, когда останавливается.

**Prompt technique** (CoT, few-shot, role-playing) — статичное содержимое одного промпта. Один запрос — один ответ. Ходом выполнения не управляет, потому что выполнения как такового нет — есть единственный вызов.

| Свойство | Prompt technique | Agentic pattern |
|----------|------------------|-----------------|
| Сколько LLM-вызовов | 1 | N (loop / tree / graph) |
| Управление потоком | Нет | Да (planner / evaluator / stopping) |
| Tool use | Опционально, single-shot | Обычно центральная часть |
| State между вызовами | Нет | Memory, history, scratchpad |
| Примеры | `Let's think step by step`, few-shot, system prompt | `ReAct`, `Reflexion`, `Plan-Execute`, `ToT` |

**Граница размытая.** `Chain-of-Thought` сам по себе — prompt technique. Но стоит навесить поверх него `Self-Consistency` (N сэмплов + majority vote) — и это уже pattern, потому что появляется управление потоком: несколько вызовов и логика выбора между ними.

**Аналогия:** prompt technique — это **формулировка вопроса**, pattern — это **процедура исследования**. Как студент строит работу: один черновик «на сдачу» против цикла план → эксперимент → ревизия.

## Q2. Как классифицируют agentic patterns?

Самая полезная классификация — по доминирующему механизму, то есть по тому, **что** именно паттерн добавляет к голому вызову LLM: цикл, критику, план, поиск или память.

1. **Reasoning-action loops** — `ReAct`, `OODA`. Базовое чередование «думаю — действую».
2. **Reflection-based** — `Reflexion`, `Self-Refine`, `Chain-of-Verification`. Агент критикует собственный вывод.
3. **Plan-based** — `Plan-and-Execute`, `ReWOO`. Сначала план, потом исполнение.
4. **Search/tree-based** — `Tree-of-Thoughts`, `Best-of-N`, `Self-Consistency`. Несколько ветвей и выбор лучшей.
5. **Memory-based** — `Voyager`, `Generative Agents`. Долговременная память, накопление навыков.
6. **Multi-agent** — `Debate`, `Society-of-Mind` (в отдельной шпаргалке про orchestration).

Классы **не взаимоисключающие** — это не таксономия, а оси. Production-агент обычно берёт по одному механизму с разных осей: например, `Plan-Execute` для управления потоком + `ReAct` внутри executor для работы с tools + `Self-Critique` перед финалом для проверки.

## Q3. (!) Почему вообще нужны patterns, если есть reasoning model (o1, R1)?

Короткий ответ: reasoning model заменила **prompt-уровневые** паттерны (то, что происходит внутри одного «думающего» вызова), но **system-уровневые** не тронула. Всё, что связано с внешним миром — tools, реальное состояние, память между сессиями, — модель внутри thinking-блока сделать не может, и эти паттерны остаются нужны.

Reasoning model встроила в себя **CoT** и частично **self-critique** (модель сама думает «wait, let me check»). Это убрало необходимость в нескольких паттернах — но не в большинстве.

**Что reasoning model заменила:**
- `Chain-of-Thought` prompting — встроено в обучение.
- Простой `Self-Refine` — модель сама перепроверяет внутри thinking-block.
- `Self-Consistency` (частично) — internal verification снижает нужду в majority vote.

**Что reasoning model НЕ заменила:**
- **Tool use** — `ReAct`/`ReWOO` нужны для работы с внешним миром (API, БД, файлы). Reasoning model не умеет дёргать tool изнутри thinking-блока.
- **Multi-step planning с external state** — `Plan-Execute` для задач, где между шагами меняется реальность.
- **External verification** — `CoVe` с реальной проверкой фактов (web search, БД) — модель сама себя не поймает на hallucination.
- **Long-horizon memory** — `Voyager`/`Generative Agents` для накопления знаний между сессиями.
- **Cost optimization** — reasoning model дороже в 5-10×; на простых шагах выгоднее дешёвая модель + явный pattern, а reasoning приберечь для сложных шагов.

**Практика:** o1/R1 + ReAct + tools — нормальная и частая комбинация. Reasoning отвечает за «думать», pattern — за «действовать»; они не конкурируют, а делят зоны ответственности.

## Q4. (!) Что такое ReAct и как выглядит его loop?

**ReAct** (Reasoning + Acting, Yao et al. 2022) — самый базовый agentic pattern: модель чередует рассуждение и действие в цикле, пока не дойдёт до ответа. Ключевая идея — **проговорить план словами перед каждым вызовом инструмента**, а не дёргать tools вслепую.

Цикл из трёх шагов:

1. **Thought** — модель пишет, что собирается делать и почему.
2. **Action** — модель вызывает tool (search, calculator, API).
3. **Observation** — система кладёт результат tool обратно в контекст.

Повтор до тех пор, пока модель не выдаст `Final Answer`.

```mermaid
flowchart LR
    Start[User query] --> Thought[Thought<br/>«Мне нужно найти X»]
    Thought --> Action[Action<br/>tool: search«X»]
    Action --> Obs[Observation<br/>результат tool]
    Obs --> Decide{Goal<br/>reached?}
    Decide -->|No| Thought
    Decide -->|Yes| Final[Final Answer]
```

**Пример trace (ReAct на question answering):**
```
Question: Сколько лет назад родился автор Reflexion paper?
Thought: Мне нужно узнать год рождения первого автора (Shinn).
Action: search["Noah Shinn researcher birth year"]
Observation: Noah Shinn, born 1998
Thought: Сейчас 2026, 2026 - 1998 = 28.
Final Answer: 28 лет.
```

**Зачем нужен `Thought`:** два эффекта. Прозрачность — видно мотивацию каждого действия, агент проще отлаживать. И качество выбора tool — LLM реже галлюцинирует имя инструмента и аргументы, когда сначала проговаривает план словами, а не сразу выдаёт вызов.

**Минусы:**
- На каждом шаге идёт полный re-prompt со всей историей → токены растут квадратично от числа шагов.
- Если tool упал с ошибкой, модель узнает об этом только на следующей итерации — реакция всегда с задержкой в один шаг.
- Без бюджета шагов может зациклиться.

## Q5. (!) Покажи pseudo-code ReAct-агента

```python
def react_agent(query, tools, max_steps=10):
    history = [{"role": "user", "content": query}]
    for step in range(max_steps):
        # 1. LLM генерирует Thought + Action (или Final Answer)
        response = llm.complete(
            system=REACT_SYSTEM_PROMPT,  # описание tools, формат
            messages=history,
        )
        history.append({"role": "assistant", "content": response})

        # 2. Парсим ответ
        if "Final Answer:" in response:
            return extract_final_answer(response)

        action_name, action_args = parse_action(response)

        # 3. Выполняем tool
        try:
            observation = tools[action_name](**action_args)
        except Exception as e:
            observation = f"Error: {e}"

        # 4. Кладём observation обратно
        history.append({
            "role": "user",
            "content": f"Observation: {observation}",
        })

    raise StopIteration("max steps reached")
```

**Ключевые места, на которые смотрят на собеседовании:**
- `parse_action` — самое хрупкое место «классического» ReAct: парсинг действия регуляркой по тексту легко ломается. В современных API его заменяют на **function calling** / **tool use** — модель сразу возвращает структурированный JSON `{name, args}`, и парсить ничего не нужно.
- `max_steps` — обязательный bound. Без него цикл бесконечный, это не опциональная защита.
- Ошибку tool отдаём модели обратно как `Observation` — она сама решит, повторить вызов или сменить подход. Не глотаем исключение молча.

Готовые обёртки: в `LangChain` это `create_react_agent` / `AgentExecutor`, в `LlamaIndex` — `ReActAgent`, в Vercel AI SDK — `streamText({ tools, maxSteps })` (он делает этот же loop под капотом).

## Q6. Что такое Self-Consistency и когда работает?

**Self-Consistency** (Wang et al. 2022) — простейший pattern для CoT-задач с детерминированным ответом: вместо одной цепочки рассуждений генерируем несколько разных и берём ответ, который встретился чаще всего. Идея в том, что к **правильному** ответу ведёт много разных корректных путей, а ошибочные пути расходятся в разные стороны — поэтому верный ответ набирает большинство.

Три шага:

1. Сгенерировать `N` независимых CoT-цепочек при `temperature > 0` (разброс нужен, чтобы пути различались).
2. Извлечь финальный ответ из каждой.
3. Взять **majority vote** (для категориальных) или **median** (для численных).

```python
def self_consistency(question, n=20, temperature=0.7):
    answers = []
    for _ in range(n):
        cot = llm.complete(question, temperature=temperature)
        answers.append(extract_answer(cot))
    return Counter(answers).most_common(1)[0][0]
```

**Когда работает:**
- Ответ — короткая величина, которую можно сравнить на равенство (число, выбор из списка). Без этого majority vote просто нечего считать.
- Reasoning допускает несколько путей к решению — именно тогда срабатывает механизм «верный путь сходится, ошибочные расходятся».

**Когда не работает:**
- Open-ended генерация: два развёрнутых ответа почти никогда не совпадают дословно, голосовать не по чему.
- Систематическая ошибка модели: если модель ошибается одинаково, majority будет уверенно неправильным — голосование лишь закрепит ошибку.
- Дорого: N× по latency и N× по токенам.

**Связь с reasoning models:** o1/R1 встроили похожую идею прямо в обучение — модель внутри сама пробует несколько ветвей и выбирает лучшую. Поэтому внешний Self-Consistency поверх o1 даёт заметно меньший прирост, чем поверх gpt-4o: часть работы уже сделана внутри.

## Q7. Auto-CoT vs Manual CoT — в чём разница?

Разница в одном: **кто подбирает примеры рассуждений** для few-shot CoT-промпта — человек или сама модель. Всё остальное (показываем модели образцы `Question + Reasoning + Answer`, чтобы она рассуждала по аналогии) у них общее.

**Manual CoT** — человек руками пишет 8-10 экземпляров `Question + Reasoning + Answer` под конкретный домен. Качество высокое, но это ручной труд под каждый домен — на разнородные задачи не масштабируется.

**Auto-CoT** (Zhang et al. 2022):
1. Берём пул вопросов из задачи.
2. Получаем embedding каждого, кластеризуем (K-means).
3. Из каждого кластера выбираем вопрос-репрезентант.
4. Для каждого репрезентанта генерируем CoT (`Let's think step by step`) — это и есть exemplars.
5. Используем как few-shot для нового вопроса.

**Плюсы Auto-CoT:** покрывает разнообразие задач без ручной работы — кластеризация гарантирует, что примеры не однотипны.
**Минусы:** exemplars сгенерированы самой моделью и могут содержать ошибки в reasoning, а через few-shot эти ошибки переносятся на целевую задачу.

В 2026 практическое значение у обоих снизилось: instruction-tuned модели хорошо справляются с zero-shot CoT (достаточно «давай рассуждать по шагам» без примеров), а reasoning models делают CoT нативно.

## Q8. (!) Что такое Reflexion и чем отличается от обычного retry?

**Reflexion** (Shinn et al. 2023) — это retry, который **учится на своих ошибках**. После неудачной попытки агент не просто пробует заново, а сначала словами разбирает провал («что я сделал не так и что попробовать дальше»), складывает этот разбор в **memory buffer** и подмешивает его в промпт следующей попытки.

Это форма **verbal reinforcement learning** — обучение без обновления весов модели, через текстовые «уроки» в контексте. Роль градиентов здесь играет накапливающийся текст рефлексий.

```mermaid
flowchart LR
    Task[Task] --> Try[Attempt N<br/>ReAct/CoT]
    Try --> Eval{Success?<br/>verifier}
    Eval -->|Yes| Done[Done]
    Eval -->|No| Reflect[Self-reflection<br/>«я провалился потому что…»]
    Reflect --> Memory[Memory buffer<br/>append reflection]
    Memory --> Try
```

**Чем отличается от обычного retry:**
| Свойство | Plain retry | Reflexion |
|----------|-------------|-----------|
| Что передаётся между попытками | Ничего | Verbal reflection (текст) |
| Учится на ошибках | Нет | Да, в контексте |
| Нужен verifier | Опционально | Обязателен (бинарный сигнал) |
| Стоимость | 1×attempt | (attempt + reflection) × N |

**Результаты из статьи:** Reflexion поднял HumanEval с 80% (GPT-4 baseline) до 91%, AlfWorld — с 75% до 97%.

**Что обязательно нужно, чтобы паттерн работал:**
- **Verifier** — бинарный или градуированный сигнал успеха (тесты для кода, симулятор для embodied). Без него агент не знает, провалился он или нет, и рефлексировать не о чем.
- Достаточный **context window** под накапливающуюся историю рефлексий.
- **Bound** на число попыток — иначе цикл бесконечный.

## Q9. Self-Refine / Self-Critique — pattern и pseudo-code

**Self-Refine** (Madaan et al. 2023) — это цикл «сделал → сам себя покритиковал → переделал» **в рамках одной задачи**. В отличие от Reflexion, здесь нет внешнего сигнала «провал/успех»: модель сама выступает и автором, и критиком. Применяют в writing, coding, summarization.

```python
def self_refine(task, max_iters=3):
    output = llm.complete(f"Solve: {task}")
    for i in range(max_iters):
        critique = llm.complete(
            f"Task: {task}\nDraft: {output}\n"
            "Find issues in this draft. If perfect, reply 'OK'."
        )
        if critique.strip().startswith("OK"):
            break
        output = llm.complete(
            f"Task: {task}\nDraft: {output}\nCritique: {critique}\n"
            "Produce improved version."
        )
    return output
```

**Где работает хорошо** — там, где критик может найти проблему, опираясь на текст, а не на внешние факты:
- Coding — модель замечает собственные баги, перечитывая код.
- Long-form writing — стиль, структура, связность.
- Summarization — что упустили из исходника.

**Где не работает** — там, где для критики нужно знание, которого у модели нет:
- Factual QA — модель не знает истинных фактов, поэтому не отличит галлюцинацию от правды.
- Math — без verifier модель часто уверенно «подтверждает» неверный ответ.

**Отличие от Reflexion:**
- Self-Refine обходится **без external verifier** — модель сама себе критик (и в этом же его слабость).
- Reflexion опирается на **бинарный сигнал успеха** и копит уроки **между задачами**, а не внутри одной.

## Q10. (!) Chain-of-Verification (CoVe) — как снижает hallucinations?

**Chain-of-Verification** (Dhuliawala et al. 2023, Meta) снижает галлюцинации, заставляя модель **перепроверить свой ответ по частям, не видя черновика**. Хитрость в изоляции: точечный вопрос «когда родился X» модель отвечает честно, а вот защищая длинный связный черновик — склонна подгонять факты под уже написанное.

4-шаговый pattern для **fact-heavy ответов**:

1. **Draft answer** — модель пишет первичный ответ.
2. **Plan verifications** — модель формулирует список проверочных вопросов (что именно надо перепроверить).
3. **Answer verifications independently** — отвечаем на каждый проверочный вопрос **в отдельном контексте**, без черновика перед глазами — чтобы модель не подстраивалась под него.
4. **Revise** — модель пересматривает черновик с учётом полученных проверочных ответов.

```mermaid
flowchart TB
    Q[User question] --> Draft[1. Draft answer]
    Draft --> Plan[2. Plan verifications<br/>«нужно проверить даты, имена»]
    Plan --> V1[3a. Answer v_q1<br/>independent]
    Plan --> V2[3b. Answer v_q2<br/>independent]
    Plan --> V3[3c. Answer v_q3<br/>independent]
    V1 --> Revise[4. Revise draft<br/>с учётом verifications]
    V2 --> Revise
    V3 --> Revise
    Revise --> Final[Final answer]
```

**Почему помогает:**
- Проверки изолированы — модель отвечает на них с чистого листа и не «защищает» свой черновик.
- Короткий factoid-вопрос галлюцинирует реже, чем длинный связный текст: меньше места для домысливания.

**Пример:**
- Draft: «Эйнштейн родился в 1880, лауреат Нобеля 1922 за теорию относительности».
- Verification q1: «Когда родился Эйнштейн?» → 1879.
- Verification q2: «За что Эйнштейн получил Нобелевку?» → за фотоэффект, не за относительность.
- Revised: «Эйнштейн родился в 1879, Нобелевка 1921 за фотоэффект».

**Стоимость:** N+2 вызовов LLM вместо одного. Оправдано для high-stakes ответов (факты, где ошибка дорого стоит); для обычного чата — избыточно.

## Q11. Чем self-critique без external feedback опасен?

Главная ловушка: модель, которая критикует сама себя, **не имеет независимого источника истины** — её критика опирается на ту же модель, что породила ошибку. Поэтому self-critique без внешнего verifier может не улучшить, а ухудшить ответ. Конкретные проблемы:

1. **Sycophancy / confirmation bias** — модель чаще соглашается со своим же черновиком, особенно длинным и уверенным. Критика вырождается в формальную.
2. **Hallucinated issues** — модель «находит» несуществующую проблему и портит уже корректный ответ.
3. **No factual grounding** — модель не знает истинных фактов, поэтому фактическую ошибку поймать нечем.
4. **Drift** — после нескольких итераций тон и структура уплывают, ответ перестаёт отвечать на исходный вопрос.
5. **False confidence** — после self-refine модель уверена в ответе сильнее, даже если по сути он не улучшился.

**Митигации** — все сводятся к тому, чтобы дать критику внешнюю опору:
- Бинарный сигнал вместо free-form-критики (passes test? yes/no) — меньше места для лести самому себе.
- External verifier: тесты для кода, search для фактов, calculator для math.
- Cap на число итераций (3-5).
- Diff-проверка: если новая версия совпала со старой N итераций подряд — stop.
- Разные модели для generator и critic: judge может быть слабее, главное — что он независим.

**Эмпирика:** на математике чистый self-critique без verifier часто **ухудшает** результат (Huang et al. 2023, "Large Language Models Cannot Self-Correct Reasoning Yet").

## Q12. (!) Plan-and-Execute pattern — структура и use case

**Plan-and-Execute** (Wang et al. 2023, "Plan-and-Solve"; LangChain `PlanAndExecute`) разделяет «думать» и «делать» по ролям: один раз строим весь план целиком, потом дёшево его исполняем. Это противоположность ReAct, где модель заново думает на каждом шаге.

Три роли:

1. **Planner** — сильная (дорогая) модель один раз строит **high-level план**: упорядоченный список шагов.
2. **Executor** — модель попроще и подешевле выполняет каждый шаг по очереди, часто как мини-ReAct.
3. **Re-planner** — если на каком-то шаге пошло не так, planner пересматривает оставшийся план.

```python
def plan_and_execute(task, planner_llm, executor_llm, max_replans=2):
    plan = planner_llm.complete(f"Break into steps: {task}")  # ["step1", "step2", ...]
    results = []
    for i, step in enumerate(plan):
        result = executor_llm.run_react(step, history=results)
        results.append((step, result))
        if needs_replan(result):
            plan = planner_llm.replan(task, plan, results)
            # дальше идём по новому плану
    return synthesize(task, results)
```

**Сценарии применения** — задачи с понятной структурой, которую можно расписать заранее:
- Multi-step research («собери информацию о компании X: финансы, продукт, команда»).
- Code refactoring («разбей класс на 3, добавь тесты, обнови doc»).
- Долгие workflow с известными фазами.

**Сравнение с ReAct:**
| Свойство | ReAct | Plan-Execute |
|----------|-------|--------------|
| Сколько раз думаем | Каждый шаг | План — раз, потом мини-think на шаг |
| Lookahead | 1 шаг | Весь план сразу |
| Стоимость think | High (полная история) | Concentrated в planner |
| Адаптивность | Высокая | Средняя (re-plan) |
| Когда лучше | Exploratory | Известная структура задачи |

## Q13. (!) Что такое ReWOO и чем выигрывает у ReAct?

**ReWOO** (Reasoning WithOut Observation, Xu et al. 2023) — расшифровка названия и есть суть: модель планирует **не глядя на промежуточные observations**. Вместо «вызвал tool → перечитал результат → снова думаю» (как в ReAct) ReWOO строит весь план с tool-вызовами сразу, а результаты подставляет в конце. Главный выигрыш — экономия токенов: LLM не перечитывает растущую историю на каждом шаге.

Три роли:

1. **Planner** — строит **DAG плана** со всеми tool calls **upfront**. Места, куда подставятся результаты, помечает плейсхолдерами (`#E1`, `#E2`).
2. **Worker** — выполняет tool calls (параллельно или последовательно). LLM на этом шаге **не вызывается** — это просто запуск инструментов.
3. **Solver** — берёт исходный вопрос плюс все собранные evidence и синтезирует финальный ответ.

```mermaid
flowchart LR
    subgraph ReAct["ReAct: re-prompt каждый шаг"]
        R1[LLM] --> R2[tool] --> R3[LLM] --> R4[tool] --> R5[LLM]
    end
    subgraph ReWOO["ReWOO: один план, потом исполнение"]
        W1[Planner LLM<br/>DAG with placeholders] --> W2[Worker<br/>execute tools] --> W3[Solver LLM<br/>synthesize]
    end
```

**Пример plan:**
```
Plan:
  E1 = search("CEO of Anthropic")
  E2 = search("founding year of Anthropic")
  E3 = calculator("2026 - " + E2)
Solver: «CEO #E1, компании #E3 лет»
```

**Преимущества перед ReAct:**
- **Меньше токенов** — LLM не перечитывает растущую историю на каждом шаге, она нужна только solver-у в конце.
- **Параллелизуемые tool calls** — независимые узлы DAG можно выполнять одновременно.
- **Дешевле** — авторы заявляют 5-10× снижение токенов на типичных задачах HotpotQA.

**Минусы** — оборотная сторона жёсткого плана:
- План фиксируется заранее. Если реальный observation противоречит ожиданиям planner, нужен fallback (re-plan).
- Не подходит для exploratory-задач, где следующее действие **зависит от того, что вернул предыдущий tool**.

## Q14. Когда re-planning важнее изначального плана?

Re-planning важнее, когда **реальность расходится с тем, как её представлял planner**. Изначальный план хорош ровно настолько, насколько верна модель мира на момент планирования; как только мир изменился или оказался не таким — ценность плана падает, а ценность пересмотра растёт.

Сигналы, что пора re-plan:

- Tool вернул error / пусто / результат, не совпадающий с ожиданием.
- Появились новые факты, меняющие приоритет шагов.
- Бюджет шагов исчерпан, а цель не достигнута.
- Пользователь вмешался с уточнением.
- Проверка цели (goal-predicate) провалилась там, где план считал задачу завершённой.

**Implementation pattern:**
```python
for step in plan:
    result = executor.run(step)
    if is_anomaly(result, expected=step.expected):
        plan = planner.replan(original_task, history, anomaly=result)
        continue
```

**Где re-plan важнее изначального плана:**
- Web automation — страница изменилась, кнопки уже не там.
- Long-horizon embodied agents — Voyager, где план превращается в curriculum и достраивается по ходу.
- Research-задачи с неизвестным объёмом — нашёл новую информацию, поменял подход.

**Где можно жить без re-plan:** структурированные workflow (ETL-подобные), где входные данные известны заранее и сюрпризов не бывает.

## Q15. (!) Tree-of-Thoughts — структура дерева и evaluation

**Tree-of-Thoughts** (Yao et al. 2023) превращает CoT из одной линейной цепочки в **дерево с поиском**: на каждом шаге модель порождает несколько вариантов «следующей мысли», оценивает их и развивает только перспективные ветки, отбрасывая тупиковые. Это даёт то, чего нет у обычного CoT — возможность откатиться и попробовать другой путь.

Каждое состояние — частичное рассуждение, ветви — альтернативные продолжения. Четыре шага:

1. **Thought decomposition** — задаём гранулярность шага: одна мысль = одна строка / одно равенство / один ход в игре.
2. **Thought generator** — LLM предлагает `k` вариантов следующей мысли из текущего состояния.
3. **State evaluator** — LLM в роли судьи оценивает каждое состояние: `sure / likely / impossible` или числовым score.
4. **Search** — обход дерева (BFS / DFS / beam-search) с отсечением веток с низким score.

```mermaid
flowchart TB
    Root["Root<br/>задача"]
    Root --> T1["Thought 1a<br/>score: 0.8"]
    Root --> T2["Thought 1b<br/>score: 0.3 cut"]
    Root --> T3["Thought 1c<br/>score: 0.6"]
    T1 --> T11["Thought 2a<br/>score: 0.9"]
    T1 --> T12["Thought 2b<br/>score: 0.4"]
    T3 --> T31["Thought 2c<br/>score: 0.7"]
    T11 --> Leaf1["Solution<br/>verified"]
```

**Pseudo-code (BFS):**
```python
def tot_bfs(task, k_per_step=3, beam=2, depth=5):
    frontier = [State(task=task, path=[])]
    for _ in range(depth):
        candidates = []
        for s in frontier:
            for thought in llm.generate_thoughts(s, k=k_per_step):
                ns = s.extend(thought)
                ns.score = llm.evaluate(ns)
                if ns.is_solution():
                    return ns
                candidates.append(ns)
        frontier = sorted(candidates, key=lambda x: -x.score)[:beam]
    return max(frontier, key=lambda x: x.score)
```

**Где применяют** — задачи с дискретными шагами, где промежуточное состояние можно оценить и при ошибке откатиться:
- Game-of-24 (из статьи) — GPT-4 на CoT: 4%, на ToT: 74%. Разница огромна именно потому, что нужен перебор.
- Crosswords, мини-puzzles.
- Step-by-step planning с возможностью отката.

**Стоимость:** взрывной рост числа вызовов — до k^depth в худшем случае. Поэтому на практике ToT берут для небольших задач или с агрессивным pruning веток.

## Q16. BFS vs DFS в ToT — когда что?

Выбор сводится к двум вопросам: **известна ли глубина решения** и **насколько надёжен scoring**. BFS исследует все варианты вширь (нужна память и доверие к оценке), DFS идёт вглубь по лучшей ветке (экономнее, когда глубина заранее неизвестна).

**BFS (breadth-first):**
- На каждом уровне расширяем всех кандидатов, сортируем по score, оставляем top-`beam`.
- Хорош, когда оптимальное решение **короткое**, а scoring надёжен.
- Подходит для **game-like** задач с фиксированной глубиной.

**DFS (depth-first):**
- Идём в самую перспективную ветку до конца, при провале откатываемся (backtrack).
- Хорош, когда глубина решения неизвестна и памяти мало.
- Подходит для **path-finding** в рассуждении.

**Beam search** — компромисс между ними: тот же обход вширь, что у BFS, но frontier ограничен фиксированным размером.

**Практика:** в LLM-агентах чаще берут **beam-search с маленьким beam (2-4)** — это баланс между exploration и стоимостью токенов.

## Q17. Best-of-N sampling: pattern и trade-off с verifier

**Best-of-N** — самый прямолинейный способ поднять качество за счёт compute: генерируем `N` независимых вариантов ответа, а **verifier** (отдельная функция или модель) выбирает лучший. Весь паттерн стоит на verifier — он и есть потолок качества: чем точнее verifier отличает хорошее от плохого, тем больше пользы от N вариантов.

```python
def best_of_n(task, n=10, verifier=None):
    candidates = [llm.complete(task, temperature=0.7) for _ in range(n)]
    if verifier:
        return max(candidates, key=verifier)
    # fallback — LLM как judge
    return llm_judge(task, candidates)
```

**Компромиссы:**
- Стоимость генерации растёт в N раз.
- Качество verifier — потолок: плохой verifier выбирает практически случайно, и N вариантов не спасут.
- Diminishing returns: переход 1 → 4 даёт большой прирост, 4 → 64 — уже маленький.

**Где работает** — там, где есть надёжный способ проверить ответ:
- Code generation + unit-tests как verifier.
- Math + символьный verifier.
- RLHF data labeling — reward model выбирает лучший response.

**Отличие от Self-Consistency:** Best-of-N опирается на **verifier** (внешнее знание, что считать хорошим); Self-Consistency — на **agreement** (большинство голосует за ответ, никакого внешнего критерия).

**Отличие от ToT:** в Best-of-N варианты независимы, общего состояния нет; в ToT поиск инкрементальный, с общим деревом и pruning.

## Q18. (!) Паттерн Voyager: библиотека навыков, авто-curriculum, итеративный prompting

**Voyager** (Wang et al. 2023, NVIDIA) — long-horizon embodied agent для Minecraft, главная идея которого — **агент, который накапливает навыки и сам ставит себе цели**, а не решает одну фиксированную задачу. Освоенные умения сохраняются как код и переиспользуются дальше.

Три компонента:

1. **Skill library** — растущая база **исполняемого кода** (JavaScript Mineflayer API). Каждый skill — функция с docstring (`mine_diamond()`, `craft_iron_pickaxe()`). При новой задаче агент:
   - retrieves relevant skills (vector search по docstring),
   - composes их в новый код.
2. **Automatic curriculum** — отдельный LLM-prompt предлагает **следующую достижимую цель** по принципу «exploration novelty + проверка предпосылок». Прогресс не задан hardcoded, а самораскрывается.
3. **Iterative prompting** — генерация кода → запуск в среде → ошибки/feedback (game logs) → правка → повтор. Похоже на ReAct, но действиями являются **программы**, а не одиночные tool-вызовы.

**Что из этого унесли в production-агенты:**
- **Skill library** превратилась в библиотеку одобренных tool-макросов / сохранённых процедур (банковские и девопс-боты).
- **Curriculum** используют для self-improving агентов и генерации синтетических данных.
- **Code as action** — Code Agents (SmolAgents, HuggingFace) реализуют ровно эту идею: вместо вызовов tool-by-tool LLM пишет Python, который исполняется в sandbox.

## Q19. Generative Agents (Stanford): поток памяти и reflection

**Generative Agents** (Park et al. 2023) — симуляция человеческого поведения (городок Smallville). Ключевой вклад для практики — **механика долгой памяти**: как агенту хранить тысячи наблюдений и доставать из них нужное, не упираясь в context window.

Компоненты:

1. **Memory stream** — append-only лог наблюдений, у каждого timestamp и importance score (насколько событие значимо).
2. **Retrieval** — при действии память скорится по формуле `recency × importance × relevance`, где relevance — это embedding-схожесть с текущим контекстом. Так агент вспоминает то, что свежо, важно и уместно.
3. **Reflection** — периодически, когда накопилось много значимых событий, агент сжимает их в **higher-level insights** («Klaus интересуется urban gentrification → ему лучше предложить тему диссертации»).
4. **Planning** — иерархический план: день → почасовой → детальный, с перепланированием по событиям.

**Что унесли в production:**
- **Reflection** как способ сжать длинную history в abstract insights — переиспользуется в **mem0**, **MemGPT**, Anthropic **memory** (2025).
- **Importance scoring** для отбора в long-term память.
- **Hierarchical planning** для долгих agent-сессий.

**Не унесли:** социальную симуляцию (она нужна для исследований, не для tasks).

## Q20. OODA Loop (Observe-Orient-Decide-Act) — как адаптируется к LLM-агентам?

**OODA** — это ReAct, в котором шаг «думать» расщеплён на два: отдельно **интерпретация** ситуации и отдельно **выбор** действия. Эта явная граница и есть его ценность для дизайна агентов.

Фреймворк военного теоретика John Boyd для принятия решений в неопределённости:
1. **Observe** — собрать данные о ситуации.
2. **Orient** — интерпретировать их в свете знаний и контекста (самый ёмкий шаг).
3. **Decide** — выбрать курс действий.
4. **Act** — выполнить.
5. → снова Observe (цикл).

**Применение к LLM-агентам:**
- Observe = чтение state (logs, sensors, tool outputs).
- Orient = LLM reasoning + retrieval из памяти/RAG.
- Decide = выбор tool/action.
- Act = вызов tool.

Полезно это как **мысленная модель** при дизайне long-running агентов (monitoring, incident response), где каждый шаг хочется спроектировать явно: что наблюдаем, как интерпретируем, как решаем. Разделение Orient и Decide заставляет не сваливать всё в один «подумай».

**Практика:** готового «OODA-агента» в LangChain нет — это **архитектурный шаблон**, а не библиотечный примитив. Полезен на whiteboard при проектировании, в коде превращается в обычный loop.

## Q21. Toolformer-подход: учим модель вызывать API через self-supervision

**Toolformer** (Schick et al. 2023, Meta) выбивается из ряда: это паттерн не runtime, а **training-time**. Вместо prompt engineering для tool use он **встраивает умение вызывать API прямо в модель на этапе обучения** — и делает это self-supervised, без ручной разметки, где какой вызов уместен.

Pipeline:
1. Берём корпус текстов.
2. Для каждого текста вставляем кандидаты `[API call]` в потенциально полезные места.
3. Запускаем API, получаем результат.
4. Оставляем только те вставки, которые **снижают perplexity** последующего текста — то есть вызов реально помог предсказать продолжение. Это и есть self-supervised-сигнал: критерий полезности не размечен человеком, а вычислен.
5. Fine-tune модель на этом «обогащённом» корпусе.

**Результат:** небольшая GPT-J научилась пользоваться calculator, calendar, search, QA-системой и обошла GPT-3 на бенчмарках, где инструменты реально помогают.

**Связь с современностью:**
- Концепция «модель сама знает, когда дёрнуть tool» теперь стандартна — function calling в GPT-4, Claude, Gemini обучен по тому же принципу.
- **Tool tokens** (`<tool_call>...</tool_call>`) — прямое наследие Toolformer.
- Паттерн важно понимать ради вывода: tool use — это не только промпт в рантайме, но и сигнал на этапе обучения модели.

## Q22. (!) Композиция паттернов: ReAct + Reflexion, Plan-Execute + Self-Critique

Главный принцип композиции: паттерны комбинируют **по слоям, а не как альтернативы**. Каждый закрывает свой класс задач — управление потоком, проверку, память — поэтому в production обычно стоит 2-3 паттерна, каждый на своём уровне. Типичные связки:

**ReAct + Reflexion**
- Внутри попытки — ReAct loop.
- На уровне попыток — Reflexion (verifier + memory of reflections).
- Use case: coding agent с тестами (SWE-bench пайплайны).

**Plan-Execute + ReAct (в executor)**
- Planner строит шаги.
- Каждый шаг исполняет ReAct-агент с tools.
- Use case: research-агент (план «Найди A, потом B, потом сравни», каждый под-шаг — поиск + чтение).

**Plan-Execute + Self-Critique**
- После execute — критическая ревизия плана/результата перед финализацией.
- Use case: long-form report generation.

**ToT + Self-Consistency**
- На листьях дерева — несколько samples, majority vote.
- Use case: math с множественными возможными подходами.

**ReWOO + CoVe**
- ReWOO даёт план + evidence.
- CoVe на финальном synthesizer-шаге проверяет факты.
- Use case: fact-heavy answer generation.

**Voyager + ReAct**
- Voyager на верхнем уровне (curriculum + skill library).
- Внутри отдельного skill — ReAct для исполнения шагов.

**Вывод:** не выбирайте «ReAct или Reflexion» — спрашивайте, какой паттерн закрывает каждый уровень: что управляет потоком, что проверяет результат, что помнит контекст. Это и есть композиция по слоям.

## Q23. (!) Stopping conditions: как агент решает, что закончить?

Stopping conditions — критический элемент любого agentic pattern: без них агент **зацикливается** или **бесконечно жжёт токены**. Главная мысль, которую хотят услышать на собеседовании: одного `max_iterations` мало. Нужен хотя бы один **позитивный** критерий («задача решена»), а лимит шагов держать как страховку.

Типы условий:

1. **Max iterations** — самый базовый, ОБЯЗАТЕЛЬНЫЙ. `max_steps=10/20/50` в зависимости от задачи.
2. **Final answer marker** — модель явно выдаёт `FINAL_ANSWER` / финальный JSON / вызов `submit_answer` tool.
3. **Verifier passes** — для Reflexion/Best-of-N: внешняя проверка (тесты, predicate).
4. **Goal predicate** — функция от state: `len(results) >= n`, `target_url_visited`, `payment_completed`.
5. **Token budget** — кумулятивный лимит tokens (защита от growing context).
6. **Time budget** — wall clock (особенно для tool-heavy агентов с медленными API).
7. **No progress** — если N последних шагов не меняют state / повторяют действия — stop.
8. **User confirmation** — human-in-the-loop: агент сам спрашивает «продолжать?».
9. **Cost cap** — $-budget; в production важнее token cap.

**Anti-pattern:** stopping только по `max_iterations`. Это не stopping, это **timeout**. Должен быть хотя бы один **успешный** критерий + timeout как safety net.

**Implementation tip:** wrap агент в `AgentExecutor`, который держит **все** stopping conditions централизованно и пишет в лог, какое сработало.

## Q24. Implementation: LangChain / LlamaIndex / Vercel AI SDK — где какой pattern из коробки?

Главное, что стоит запомнить: **ReAct есть из коробки почти везде и выглядит примерно одинаково**, а нетривиальные паттерны (re-plan, reflection, ToT) почти везде придётся собирать вручную — и удобнее всего это делать на LangGraph. Сводка:

| Framework | ReAct | Plan-Execute | Reflexion | ToT | Self-Consistency |
|-----------|-------|--------------|-----------|-----|------------------|
| **LangChain (Python)** | `create_react_agent`, `AgentExecutor` | `PlanAndExecute` (deprecated в LangGraph), сейчас через LangGraph | через LangGraph state | не из коробки | через `RunnableParallel` + voter |
| **LangGraph** | низкоуровневые узлы, есть `create_react_agent` | composable graph (любой план) | state с reflections | можно построить вручную | parallel branches |
| **LlamaIndex** | `ReActAgent` | `StructuredPlannerAgent` | нет нативного | нет | нет |
| **Vercel AI SDK** | `streamText({ tools, maxSteps })` — manual loop | вручную | вручную | вручную | вручную |
| **Anthropic SDK (raw)** | примеры в Anthropic Cookbook | вручную | вручную | вручную | вручную |
| **CrewAI** | role-based, ReAct под капотом | через task planning | нет | нет | нет |
| **AutoGen** | conversation-driven, не строго ReAct | через GroupChat | через CritiquerAgent | нет | нет |

**Современный тренд (2026):** **LangGraph** стал стандартом для нетривиальных patterns — он даёт `nodes + edges + state`, поверх которого описывается любой pattern. ReAct-обёртки во всех фреймворках выглядят похоже; различия проявляются в более сложных композициях (re-plan, reflection, multi-agent).

**Anti-pattern:** тащить тяжёлый framework для простого ReAct из 3 tools — голый цикл из Q5 + function calling короче и понятнее.

## Q25. (!) Anti-patterns: pattern без необходимости, recursive без bound, context bloat

Большинство провалов agent-систем — это не «не тот паттерн», а **отсутствие границ**: нет bound на рекурсию, нет управления контекстом, нет независимого verifier, нет трейсинга. Восемь типичных анти-паттернов и лечение к каждому:

1. **Pattern без необходимости (over-engineering)**
   - Простой Q&A решается одним промптом — а навешено Plan-Execute + Self-Refine + Verifier.
   - Признак: 10 LLM-вызовов там, где хватило бы одного.
   - Лечение: начинать с baseline (single prompt), добавлять pattern только когда видим конкретную проблему.

2. **Recursive без bound**
   - Self-critique вызывает себя без max_iters → бесконечный цикл.
   - Tool A вызывает агента, который снова вызывает tool A.
   - Лечение: stack-depth check, дедупликация недавних состояний.

3. **Context bloat от reflection / history**
   - Каждый шаг добавляет в prompt — после 30 шагов уже 100k токенов, дорого и качество падает.
   - Лечение: **rolling window** (последние K), **summarization** (сжать историю), **scratchpad** в отдельном tool вместо context.

4. **Verifier weaker than generator**
   - Self-Critique со слабой judge-моделью — отбирает не лучшие варианты, а удобные для critic.
   - Лечение: использовать **другой** провайдер/модель для judge; ground-truth verifier там, где возможен.

5. **Implicit state via history**
   - Состояние «закодировано» в длинном history, никто его явно не моделирует.
   - При обрезке окна теряется ключевой факт.
   - Лечение: explicit state (`AgentState`/`scratchpad`), summary блоки.

6. **Tool name collision / ambiguous schemas**
   - Два tools с похожими описаниями → модель путает.
   - Лечение: чёткие docstring, тесты на disambiguation.

7. **No instrumentation**
   - Не логируется trace шагов → невозможно понять, почему agent зациклился.
   - Лечение: structured tracing (LangSmith, Helicone, custom logs), включить с первого дня.

8. **Reflection «для галочки»**
   - Pattern есть, но reflections игнорируются при retry (баг в коде).
   - Лечение: assertion в тестах, что reflections появляются в следующем prompt.

## Q26. Сравнительная таблица паттернов: complexity, latency, use case

Сводная шпаргалка по стоимости и применимости. Читать так: чем больше LLM-вызовов, тем дороже и медленнее, а колонка «Нужен verifier» отделяет паттерны, которые без внешней проверки бесполезны (Reflexion, Best-of-N), от тех, что работают на одной модели.

| Pattern | LLM calls | Cost vs baseline | Latency | Нужен verifier | Хорошо для |
|---------|-----------|------------------|---------|----------------|------------|
| `CoT (prompt)` | 1 | 1× | 1× | Нет | Простые reasoning |
| `Self-Consistency` | N | N× | ~1× (parallel) | Нет (agreement) | Численный ответ |
| `ReAct` | k шагов | k× растущий context | k× | Нет (но желателен) | General tool use |
| `Reflexion` | k × R попыток | k×R | k×R | **Да** | Coding, environment-based |
| `Self-Refine` | 2R+1 (gen+critique+refine) | 2-4× | 2-4× | Нет (рискованно) | Writing, coding draft |
| `Chain-of-Verification` | N+2 | 4-6× | sequential | Нет (LLM-self) | Fact-heavy answers |
| `Plan-and-Execute` | 1 plan + k step | 1+k | 1+k | Нет | Структурированные workflow |
| `ReWOO` | 1 plan + 1 solver | ~2× (без re-prompt) | DAG (parallel tools) | Нет | Token-конкурентные tool-heavy задачи |
| `Tree-of-Thoughts` | k^d (с pruning меньше) | high | high | LLM judge | Math/puzzles |
| `Best-of-N` | N | N× | ~1× (parallel) | **Да** | Code with tests, RLHF |
| `Voyager` | растёт со временем | amortized | long-horizon | env feedback | Embodied / continuous learning |
| `Generative Agents` | high (memory + reflection) | high | continuous | нет | Симуляция, persistent agents |

«Baseline» = одиночный CoT prompt без agentic-надстройки.

## Q27. Какой pattern когда выбирать: ReAct / Reflexion / ToT / Self-Critique / CoVe

Дефолт — `ReAct`; всё остальное добавляют, когда у задачи есть конкретная особенность: верифицируемый результат, известная структура, дискретный перебор, дорогая галлюцинация. Краткий гайд по выбору:

- **`ReAct`** — general-purpose tool use, exploratory задачи. Дефолт для агента «реши задачу X, у тебя есть tools Y».
- **`Reflexion`** — задачи с **верифицируемым результатом** (тесты, симулятор) и где модель **должна учиться на провалах**: coding agents, game-like, RL-like environments.
- **`Plan-and-Execute`** — когда **структура задачи известна** заранее или хочется явный plan для аудита. Долгие workflow с понятными фазами.
- **`ReWOO`** — когда задача состоит из **набора независимых tool calls**, и хочется сэкономить токены / распараллелить. Multi-hop QA, агрегация из нескольких API.
- **`Tree-of-Thoughts`** — задачи с **дискретным reasoning** и возможностью оценить промежуточное состояние: math, puzzles, planning over short horizon.
- **`Best-of-N`** — когда **есть программный verifier** (unit-tests, eval function) и compute не лимит.
- **`Self-Consistency`** — short factual/numeric answers, есть «канонический» ответ для majority vote.
- **`Self-Refine`** — open-ended writing, summarization, draft → polish. Без verifier — осторожно с математикой.
- **`Chain-of-Verification`** — fact-heavy ответы (биография, исторические факты, описание продукта), где hallucination дорого стоит.
- **`Voyager`** — long-horizon agents с накоплением навыков.
- **`Generative Agents`** — persistent agents с долгой памятью и социальной составляющей.

**Композиция:** в продакшене типично 2-3 pattern в стеке. Пример: `Plan-Execute (верх) + ReAct (executor) + CoVe (на финальном synth)`.

## Q28. (!) Evaluation patterns: harness, метрики (success rate, # steps, tokens)

Agentic system **нельзя** оценивать одной метрикой: high success rate ничего не стоит, если агент тратит 50 шагов и стопится по `max_iters`. Поэтому harness меряет качество (решил/не решил) **в связке** с ценой (шаги, токены, latency) и устойчивостью. Минимальный набор:

**Базовые метрики:**
- **Success rate** — % задач, решённых корректно (по ground-truth / verifier).
- **# steps to solution** — среднее/медиана шагов. Меньше — лучше, но не за счёт quality.
- **Tokens per task** — input + output + thinking. Прокси стоимости.
- **Latency (p50/p95)** — wall clock от запроса до финала.
- **Cost per task** — $ при текущих ценах.
- **Tool calls per task** — сколько раз дёрнули внешние API.

**Качественные:**
- **Tool selection accuracy** — % случаев, когда выбран правильный tool.
- **Tool argument accuracy** — корректность аргументов.
- **Hallucination rate** — % ответов с фактами, не подтверждёнными context/tools.
- **Stop-condition triggered** — какое условие сработало (final answer / max_iters / error). Высокая доля `max_iters` — плохой знак.

**Robustness:**
- **Re-run variance** — std success rate при temperature > 0.
- **Adversarial inputs** — поведение на malformed query, prompt injection.
- **Tool failure resilience** — что если tool вернул error / timeout.

**Eval-frameworks:**
- **LangSmith Evaluators** — datasets + LLM-as-judge.
- **OpenAI Evals** — generic harness.
- **AgentBench, SWE-bench, GAIA, WebArena, ALFWorld** — стандартные benchmarks для категорий агентов.
- **PromptFoo** — для regression-тестов prompts/agents.

**Принцип:** строить **regression suite** из ~30-100 задач разной сложности и гонять её на каждое изменение pattern/prompt/model.

## Q29. Reasoning models делают patterns внутри — нужны ли они снаружи?

Это уточнённая версия Q3, и ответ тот же: внутри thinking-блока модель делает только **рассуждение**, а всё, что касается внешнего мира, по-прежнему нужно строить снаружи. Граница проходит ровно по линии «думать против взаимодействовать».

Reasoning models (o1, o3, R1, Claude extended thinking, Gemini Deep Think) выполняют внутри thinking-блока:
- CoT (по определению).
- Частичный Self-Refine («wait, let me check this»).
- Эмерджентный backtracking.
- Иногда поиск по вариантам, похожий на tree-search.

**Что отпадает снаружи:**
- Self-Consistency на простой математике даёт мало прироста.
- Self-Refine без verifier — почти бесполезен, модель внутри это сделала.
- Manual CoT exemplars — лишний noise.

**Что остаётся актуальным:**
- **Tool use** — reasoning model не вызывает tools внутри thinking; нужен внешний ReAct/ReWOO loop.
- **External verification** — модель не знает реальных фактов, нужен Search/DB/CoVe с реальными запросами.
- **Long-horizon planning с реальным state** — между шагами реальность меняется (web automation, embodied).
- **Cost optimization** — reasoning model дорогая; в production обычно **routing**: простые шаги → cheap model + ReAct, сложные шаги → reasoning model.
- **Multi-agent orchestration** — patterns между агентами (debate, hierarchical) не делаются «внутри» одной модели.
- **Memory / skill libraries** — Voyager-like наследие не уходит.

**Резюме:** reasoning model заменила **prompt-уровневые** patterns; **system-уровневые** (tool use, external state, multi-agent, memory) остались полностью актуальны.

## Q30. Какие паттерны хорошо переносятся на multi-agent системы?

Правило простое: паттерн переносится на multi-agent тем легче, чем **естественнее в нём разделяются роли**. Где single-agent уже делит работу на «планировщик и исполнитель» или «автор и критик» — там каждая роль становится отдельным агентом почти даром. Где паттерн опирается на единого решателя — перенос ломается.

Какие single-agent-паттерны переходят естественно:

- **Plan-Execute → Hierarchical multi-agent**: planner-агент + executor-агенты. Manager делегирует таски workers (CrewAI, AutoGen GroupChat manager).
- **ReWOO → DAG-of-agents**: planner строит граф задач, каждая нода — отдельный agent/role. Параллелизация естественна.
- **Self-Refine / Reflexion → Debater + Critic agents**: критик — отдельный агент с другим system-prompt (часто иной моделью). Снимает confirmation bias.
- **Best-of-N → Proposer + Selector agents**: N генераторов, отдельный selector-агент с verifier.
- **Tree-of-Thoughts → Parallel exploration agents**: каждая ветка дерева — отдельный agent run, head-агент агрегирует.
- **Chain-of-Verification → Asker + Verifier agents**: специализированный verifier-agent (другая модель / другой context).
- **Voyager → Specialist library**: вместо skill code — pool of specialist agents (research-agent, code-agent, browse-agent), индексированных по описанию.

**Что НЕ переносится прямолинейно:**
- **Self-Consistency** — теряет суть, если все «голоса» — копии одного агента; для multi-agent имеет смысл, только если агенты **разные** (разные модели, разные prompts).
- **OODA** — это mental model одного решателя, не паттерн распределения.

**Главное усложнение multi-agent:** communication protocol между агентами (message passing, blackboard, debate format) и stopping condition на уровне всей системы. Это уже **orchestration**, а не pattern.

---

## See also

- [AI Agents (общая шпаргалка)](ai-agents-interview.md) — базовые понятия про агентов
- [Multi-Agent Orchestration](multi-agent-orchestration-interview.md) — паттерны взаимодействия между агентами
- [Function Calling](function-calling-interview.md) — tool use, на котором стоят ReAct/ReWOO
- [Reasoning Models](reasoning-models-interview.md) — o1/o3/R1 и как они меняют необходимость patterns
- [Prompt Engineering](prompt-engineering-interview.md) — CoT, few-shot, foundational техники
- [LLM Basics](llm-basics-interview.md) — основы LLM
- [MCP](mcp-interview.md) — Model Context Protocol для tool ecosystem
- [System Design Interview](../system-design/system-design-interview.md) — общий контекст для system-design разговоров
