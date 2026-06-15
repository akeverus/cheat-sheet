---
title: "Вопросы на собеседовании: Multi-Agent Orchestration"
description: "Multi-agent системы LLM: CrewAI, AutoGen, LangGraph, OpenAI Swarm, Anthropic подход; hierarchical/sequential/mesh, state, observability, anti-patterns."
tags:
  - interview
  - ai-ml
  - multi-agent-orchestration
type: "interview"
difficulty: "advanced"
aliases:
  - "Multi-Agent Orchestration interview"
  - "CrewAI AutoGen LangGraph"
  - "Multi-agent LLM собеседование"
  - "Agent orchestration patterns"
updated: "2026-05-23"
---

# Вопросы на собеседовании: `Multi-Agent Orchestration`

> Дата: 2026-05-23. Multi-agent системы координируют несколько LLM-агентов с разными ролями — фреймворки, шаблоны коммуникации, state management, anti-patterns и производственная эксплуатация.

## Полезные ссылки

- [CrewAI Documentation](https://docs.crewai.com/) — role-based multi-agent фреймворк.
- [AutoGen (Microsoft)](https://microsoft.github.io/autogen/) — conversation-driven агенты, group chat, code execution.
- [LangGraph (LangChain)](https://langchain-ai.github.io/langgraph/) — stateful graph orchestration с циклами и human-in-loop.
- [OpenAI Swarm](https://github.com/openai/swarm) — экспериментальный handoff-фреймворк.
- [Anthropic — Building Effective Agents](https://www.anthropic.com/research/building-effective-agents) — манифест «simple первый, frameworks позже».
- [Microsoft Magentic-One](https://www.microsoft.com/en-us/research/articles/magentic-one-a-generalist-multi-agent-system-for-solving-complex-tasks/) — generalist multi-agent от Microsoft Research.

## Содержание

- Основы и архитектуры (Q1–Q6)
- Frameworks: CrewAI / AutoGen / LangGraph / Swarm (Q7–Q14)
- Communication и State (Q15–Q18)
- Coordination patterns (Q19–Q22)
- Observability и cost (Q23–Q25)
- Production и anti-patterns (Q26–Q30)

---

## Q1. Что такое multi-agent система на базе LLM и чем она отличается от single-agent?

**Multi-agent система** — это несколько LLM-агентов с разными ролями, промптами и tools, которые кооперируются над одной задачей. Каждый агент работает в собственном контексте, обменивается сообщениями с другими и сам решает, что делать дальше. В **single-agent** один LLM крутится в цикле ReAct/планировщика и единолично владеет всеми tools и контекстом.

Главная разница — не в количестве моделей, а в декомпозиции: multi-agent дробит задачу на роли, single-agent держит всё в одной голове.

**Что даёт разбиение на агентов:**

- **Специализация** — узкая роль («researcher», «writer», «critic») с заточенным промптом отвечает точнее, чем один generalist на все случаи жизни.
- **Изоляция контекста** — у каждого свой scratchpad, поэтому контекст не забивается чужими промежуточными данными.
- **Параллелизм** — независимые подзадачи идут одновременно, сокращая время «по часам».

**Чем платим:** больше LLM-вызовов → выше latency, дороже, тяжелее отлаживать.

Ключевая ментальная модель: multi-agent — это **распределённая система на LLM**. Отсюда и весь набор её болячек — координация, обработка отказов, observability, идемпотентность — ровно те же, что и в обычных микросервисах.

## Q2. Когда single-agent достаточно, а когда реально нужен multi-agent? (!)

**Правило по умолчанию:** начинай с single-agent и добавляй агентов, только когда конкретная метрика (качество, latency, изоляция) этого требует. Это прямая эвристика Anthropic из «Building Effective Agents»: сложность вводят последней, а не первой.

**Single-agent достаточно, если:**

- Задача укладывается в **один чёткий промпт** и один набор tools.
- Контекст помещается в window и не мешает рассуждению.
- Нет естественного разбиения на параллельные подзадачи.
- Latency и cost критичны — single-agent дешевле в 3–10×.

**Multi-agent оправдан, если** есть хотя бы одна из причин:

- **Разнородные этапы** с разной экспертизой (research + write + critique + code) — каждому нужен свой заточенный промпт.
- **Разные права и tools** у «работников» (python-sandbox у одного, запись файлов — у другого), и смешивать их в одном агенте опасно.
- **Параллельные ветки** — например, 10 источников исследуются одновременно.
- **Adversarial/debate-паттерн** — writer против critic, генератор против reviewer, где сама идея в противопоставлении ролей.
- Контекст одного агента **переполняется**, и подзадачи хочется развести по изолированным context windows.

**Эмпирическое правило:** если сомневаешься, single или multi — почти всегда ответ «single-agent с хорошими tools». Multi-agent — последнее средство, а не первое.

## Q3. Какие основные архитектуры multi-agent систем существуют? (!)

Три базовые топологии наглядно различаются направлением связей:

- **Hierarchical** — `Manager` раздаёт работу вниз трём подчинённым: `Manager` → `Worker A`, `Manager` → `Worker B`, `Manager` → `Worker C`.
- **Sequential** — линейный конвейер: `Agent 1` → `Agent 2` → `Agent 3`.
- **Mesh** — двусторонние связи каждого с каждым: `Agent A` ↔ `Agent B`, `Agent B` ↔ `Agent C`, `Agent A` ↔ `Agent C`.

Архитектуры различаются тем, **кто кому может слать сообщения** — то есть топологией графа взаимодействий. Пять основных:

- **Hierarchical / Manager-Worker** — manager-агент дробит задачу, делегирует sub-agents и собирает их результаты воедино. Это default-режим CrewAI (`Process.hierarchical`).
- **Sequential / Pipeline** — выход одного агента подаётся на вход следующему, как конвейер. Просто и предсказуемо. CrewAI `Process.sequential`.
- **Hub-and-Spoke / Star** — центральный координатор общается с каждым «лучом», но лучи друг о друге не знают. Удобно для роутинга по специализациям.
- **Network / Mesh / Peer-to-peer** — агенты общаются напрямую, все со всеми (group chat в AutoGen). Максимально гибко, но непредсказуемо.
- **Multi-level hierarchical** — manager → team leads → workers. Для крупных задач, разбитых на подкоманды.

**Как выбирать:** чем выше нужна предсказуемость, тем строже топология — hierarchical или sequential. Mesh оставляют для исследовательских и творческих задач, где как раз ценен непредсказуемый обмен идеями.

## Q4. Что такое «agentic workflow» vs «agent» по терминологии Anthropic? (!)

Разница в **том, кто управляет потоком выполнения** — код или сама модель. Это любимый вопрос на интервью.

- **Workflow** — поток жёстко прописан разработчиком, а LLM встроен как один из компонентов. Control flow детерминированный (prompt chaining, routing, parallelization, orchestrator-workers, evaluator-optimizer); модель принимает только локальные решения, но не выбирает маршрут.
- **Agent** — поток выбирает сама модель: какой tool позвать, когда остановиться, когда повторить. Control flow определяется в runtime и заранее неизвестен.

**Компромисс:**

- Workflow — предсказуемый, легче тестировать и отлаживать, дешевле. Покрывает ~80% production-задач.
- Agent — гибкий, тянет open-ended запросы, но непредсказуем, дороже и тяжелее в observability.

**Рекомендация Anthropic:** agents — только там, где гибкость и решения «от модели» реально нужны. Во всех остальных случаях — workflow.

## Q5. Перечислите типовые «building blocks» из Anthropic-патернов orchestration.

Anthropic выделяет 5 базовых паттернов оркестрации, от простого к сложному. Важно: всё это **workflows** (детерминированный код), а не autonomous agents.

- **Prompt chaining** — цепочка LLM-вызовов, где выход одного идёт на вход следующего. Для задач с чёткими последовательными шагами.
- **Routing** — классификатор определяет тип запроса и направляет его в один из специализированных промптов.
- **Parallelization** — два варианта: *sectioning* (разбить задачу на независимые куски и считать параллельно) или *voting* (N независимых вызовов → ансамблевое решение).
- **Orchestrator-Workers** — центральный LLM динамически дробит задачу, делегирует worker-LLM и затем синтезирует результат. По сути — то же hierarchical multi-agent.
- **Evaluator-Optimizer** — один LLM генерирует, другой оценивает, и так по кругу до выполнения критерия. Аналог reflexion / actor-critic.

**Ключевая мысль:** фреймворки (CrewAI, AutoGen, LangGraph) — это обобщения тех же паттернов. Но на практике один из этих пяти покрывает ~90% задач и без отдельной библиотеки.

## Q6. Что выбрать: hierarchical, sequential или mesh для типовых задач? (!)

Архитектуру диктует **структура задачи**: насколько чётко она раскладывается на шаги и насколько предсказуемым должен быть результат. Грубая шпаргалка по типам задач:

| Тип задачи | Архитектура |
|------------|-------------|
| Конвейер (analyse → summarise → translate) | **Sequential** |
| Декомпозиция большого reasearch-запроса | **Hierarchical** (manager + N workers) |
| Роутинг по специализациям (юрист/финансы/тех) | **Hub-and-spoke** / routing |
| Дебаты, brainstorming, peer-review | **Mesh / group chat** |
| Software development simulation (PM → dev → QA → ops) | **Multi-level hierarchical** |
| Voting/ensemble (N independent → majority) | **Parallel + aggregator** |

**Главное правило:** чем понятнее граф взаимодействий, тем меньше LLM-вызовов уходит на саму координацию. Mesh из 5+ агентов почти всегда — overengineering: модели тратят токены на «болтовню» друг с другом вместо работы.

## Q7. CrewAI — основные концепции и когда подходит. (!)

CrewAI — это Python-фреймворк, построенный вокруг **role-playing**: ты описываешь агентам роли почти как актёрам, и модель «отыгрывает» их. Базовые сущности:

- **`Agent`** — `role`, `goal`, `backstory`, `tools`, `llm`. `backstory` критичен для качества: именно через него модель вживается в роль и держит нужный тон.
- **`Task`** — описание задачи + `expected_output` + `agent` (или агент назначается manager-ом).
- **`Crew`** — оркестратор: список agents + tasks + `process` (`sequential` или `hierarchical`).
- **`Process.hierarchical`** — Crew сам добавляет manager-агента, который раздаёт задачи остальным.

```python
from crewai import Agent, Task, Crew, Process

researcher = Agent(
    role="Senior Research Analyst",
    goal="Найти актуальную информацию по теме {topic}",
    backstory="Эксперт по AI/ML с 10 годами стажа, любит факты и ссылки.",
    tools=[search_tool],
    llm=llm,
)

writer = Agent(
    role="Tech Content Writer",
    goal="Написать читаемую статью на основе данных от researcher",
    backstory="Пишет о технологиях для разработчиков, не любит buzzwords.",
    llm=llm,
)

research_task = Task(
    description="Собрать ключевые источники по {topic}",
    expected_output="Bullet-list из 5 фактов с ссылками",
    agent=researcher,
)
write_task = Task(
    description="Написать статью на 800 слов",
    expected_output="Markdown-статья",
    agent=writer,
    context=[research_task],
)

crew = Crew(
    agents=[researcher, writer],
    tasks=[research_task, write_task],
    process=Process.sequential,
)
result = crew.kickoff(inputs={"topic": "Multi-agent orchestration"})
```

**Сценарий применения:** контент-производство, исследовательские конвейеры, демо и прототипы с понятными ролями. **Минус:** меньше контроля над state machine, чем у LangGraph, — для сложной логики ветвлений и циклов CrewAI быстро упирается в потолок.

## Q8. AutoGen (Microsoft) — что это и какие сценарии? (!)

AutoGen — фреймворк от Microsoft, где координация строится **через диалог**: агенты не вызывают друг друга как функции, а переписываются в общем чате, и из этой переписки рождается решение. Ключевые сущности:

- **`AssistantAgent`** — LLM, играющий роль (кодер, ревьюер и т.п.).
- **`UserProxyAgent`** — псевдо-пользователь: умеет вызывать tools, исполнять код в Docker-sandbox и запрашивать ввод человека. Именно он замыкает диалог на реальные действия.
- **`GroupChat`** + **`GroupChatManager`** — несколько агентов в одной беседе; manager на каждом шаге решает, кому давать слово следующим.

```python
import autogen

config_list = [{"model": "gpt-4o", "api_key": "..."}]

assistant = autogen.AssistantAgent(
    name="coder",
    system_message="Ты пишешь Python-код для решения задач.",
    llm_config={"config_list": config_list},
)
reviewer = autogen.AssistantAgent(
    name="reviewer",
    system_message="Ты ревьюишь код на корректность и стиль.",
    llm_config={"config_list": config_list},
)
user_proxy = autogen.UserProxyAgent(
    name="user_proxy",
    human_input_mode="NEVER",
    code_execution_config={"work_dir": "sandbox", "use_docker": True},
)

groupchat = autogen.GroupChat(
    agents=[user_proxy, assistant, reviewer],
    messages=[],
    max_round=8,
)
manager = autogen.GroupChatManager(groupchat=groupchat, llm_config={"config_list": config_list})
user_proxy.initiate_chat(manager, message="Напиши и протестируй функцию fibonacci(n).")
```

**Плюсы:** исполнение кода из коробки (Docker-sandbox), естественные диалоги между ролями, AutoGen Studio для no-code-сборки. **Минус и подводный камень:** group chat склонен скатываться в длинные «болтливые» сессии, где агенты гоняют сообщения по кругу, — поэтому `max_round` нужно лимитировать жёстко, иначе растут и latency, и счёт.

## Q9. LangGraph — чем отличается от CrewAI/AutoGen и зачем StateGraph? (!)

LangGraph — расширение LangChain, которое моделирует агента как **направленный граф с состоянием и циклами**. В отличие от CrewAI (роли) и AutoGen (диалог), здесь ты явно рисуешь граф переходов и сам владеешь state machine — отсюда и максимум контроля.

Ключевые концепции:

- **State** — типизированный словарь (обычно `TypedDict`), общий контейнер, который пробрасывается между узлами и накапливает данные шага за шагом.
- **Node** — функция (LLM-вызов, tool-вызов или своя логика): принимает state и возвращает его обновление.
- **Edge** — направленный переход. **Conditional edges** дают ветвление по содержимому state — это и есть «решения» графа.
- **Cycles** — в отличие от DAG-фреймворков, циклы разрешены, что и нужно для ReAct-loop, retry и reflection.
- **Checkpointer** — сохраняет state в БД (SQLite/Postgres), за счёт чего возможны pause/resume и human-in-the-loop.

```python
from langgraph.graph import StateGraph, END
from typing import TypedDict, Annotated
import operator

class State(TypedDict):
    messages: Annotated[list, operator.add]
    next_agent: str

def supervisor(state: State):
    decision = llm_route(state["messages"])  # вернёт "researcher" | "writer" | "FINISH"
    return {"next_agent": decision}

def researcher(state: State):
    out = llm_research(state["messages"])
    return {"messages": [out]}

def writer(state: State):
    out = llm_write(state["messages"])
    return {"messages": [out]}

graph = StateGraph(State)
graph.add_node("supervisor", supervisor)
graph.add_node("researcher", researcher)
graph.add_node("writer", writer)
graph.set_entry_point("supervisor")
graph.add_conditional_edges(
    "supervisor",
    lambda s: s["next_agent"],
    {"researcher": "researcher", "writer": "writer", "FINISH": END},
)
graph.add_edge("researcher", "supervisor")
graph.add_edge("writer", "supervisor")

app = graph.compile(checkpointer=memory_saver)
```

**Когда выбирать LangGraph:**

- Нужен **полный контроль над state machine** и циклами, а не «магия» фреймворка.
- Требуется **human-in-the-loop** с pause/resume через checkpointer.
- Строишь **supervisor pattern** с динамическим роутингом между worker-ами.
- Важна интеграция с экосистемой LangChain (LangSmith tracing, retrievers).

## Q10. OpenAI Swarm — что это, для чего, насколько production-ready?

Swarm — экспериментальный учебный фреймворк от OpenAI (2024), который существует ради одной идеи — **handoff**: агент передаёт управление другому, просто вернув из function call объект `Agent`. Это самый наглядный способ понять, как работают передачи контроля.

```python
from swarm import Swarm, Agent

client = Swarm()

def transfer_to_billing():
    return billing_agent

triage = Agent(
    name="Triage",
    instructions="Если вопрос про оплату — передай billing-агенту.",
    functions=[transfer_to_billing],
)
billing_agent = Agent(
    name="Billing",
    instructions="Ты отвечаешь на вопросы по биллингу.",
)

response = client.run(agent=triage, messages=[{"role": "user", "content": "Где мой счёт?"}])
```

**Особенности:**

- Минимализм — всего ~500 строк кода, легко прочитать целиком.
- Stateless: состояние между вызовами не хранится, persistence — забота разработчика.
- Явно **не production-ready** — OpenAI прямо предупреждает: educational only.
- Production-преемник — **OpenAI Agents SDK** (выпущен в 2025).

**Что сказать на интервью:** Swarm — отличный способ понять идею handoff, но в продакшен берут AutoGen, LangGraph, OpenAI Agents SDK или обычный Anthropic-style код, а не Swarm.

## Q11. Anthropic подход «agents-as-tools» и почему он часто лучше фреймворков. (!)

Главный тезис Anthropic из «Building Effective Agents»: специальные multi-agent фреймворки часто **избыточны**. Большинство задач решается обычным кодом + tools + одним хорошим LLM, а «агентом» становится просто очередной вызов с другим промптом.

Паттерн «**orchestrator-workers**» в чистом виде, без библиотеки:

```python
def run_orchestrator(query: str):
    plan = llm.call(orchestrator_prompt, query)  # JSON со списком subtasks
    results = []
    for subtask in plan["subtasks"]:
        worker_result = llm.call(worker_prompt, subtask)
        results.append(worker_result)
    final = llm.call(synthesizer_prompt, query, results)
    return final
```

Это и есть «multi-agent», только без фреймворка: каждый LLM-вызов с другим промптом — уже «другой агент», а tools передаются обычными function-schemas.

**Плюсы подхода:**

- Прозрачный control flow — это обычный Python, который видно глазами.
- Нет vendor lock-in на абстракции фреймворка.
- Retry, circuit-breaker, observability добавляются стандартными библиотеками, а не «магией» крейта.
- Проще code review, отладка и тестирование.

**Когда фреймворк всё-таки оправдан:** долгоживущие stateful-беседы (LangGraph checkpointer), сложные group chats (AutoGen), role-heavy production (CrewAI). В остальных случаях — обычный код.

## Q12. Сравните CrewAI, AutoGen, LangGraph, Swarm.

Коротко: **CrewAI** — про роли, **AutoGen** — про диалог, **LangGraph** — про граф состояний, **Swarm** — про handoff и обучение. Развёрнутое сравнение:

| Критерий | CrewAI | AutoGen | LangGraph | Swarm |
|----------|--------|---------|-----------|-------|
| Парадигма | Role-playing + tasks | Conversation / group chat | Stateful graph | Handoff function calls |
| Control flow | Sequential / hierarchical | Manager-driven turn-taking | Explicit graph + cycles | Agent returns next agent |
| State management | Внутри Crew | Conversation history | Typed StateGraph + checkpoint | Stateless |
| Human-in-the-loop | Базовый | Через `UserProxyAgent` | First-class via checkpointer | Нет |
| Code execution | Через tools | Встроенный Docker sandbox | Через tools | Через tools |
| Production-ready | Да (быстро растёт) | Да (Microsoft prod-use) | Да (LangChain-эко) | Нет (educational) |
| Observability | Базовая + интеграции | AgentOps, AutoGen Bench | LangSmith first-class | Нет |
| Learning curve | Низкая | Средняя | Высокая | Очень низкая |
| Лучший use case | Контент / research crews | Coding/debate agents | Сложные stateful workflows | Прототипы / обучение |

**Эмпирическое правило для продакшена:** LangGraph — для stateful-систем со сложной логикой, CrewAI — для role-heavy задач, AutoGen — когда нужен code-exec sandbox, а Swarm в прод не берут (вместо него OpenAI Agents SDK).

## Q13. Что такое OpenAI Agents SDK и где он в этой картине?

OpenAI Agents SDK (2025) — официальный production-преемник Swarm: та же идея handoff, но обвешанная всем, чего не хватало Swarm для прода.

**Что добавили к идее Swarm:**

- Встроенный **tracing** и observability из коробки.
- **Guardrails** — валидация input/output до и после вызова tools.
- **Handoffs** остались ключевой идеей, но стали структурированнее.
- **Structured output** через Pydantic.
- Production-grade обработка ошибок и retries.

По сути это «Swarm, выросший до прода» и прямой конкурент LangGraph в нише «оркестрация LLM-агентов с tools». На интервью стоит назвать его как современный стандарт от OpenAI.

## Q14. Microsoft Magentic-One — что это и зачем?

Magentic-One — generalist multi-agent система от Microsoft Research (2024): один оркестратор плюс набор готовых «рук» под разные действия. Архитектура:

- **Orchestrator** — центральный агент; ведёт *Task Ledger* (план) и *Progress Ledger* (статус), то есть отдельно держит «что делать» и «что уже сделано».
- **WebSurfer** — управляет браузером.
- **FileSurfer** — работает с файлами и документами.
- **Coder** — пишет код.
- **ComputerTerminal** — исполняет код в sandbox.

**Чему учит:** набор специалистов фиксирован, но оркестратор динамически решает, кого и когда звать. Это образцовый «hierarchical с manager-ом, который ведёт plan/progress state» — и этот приём легко переиспользовать в собственных системах.

## Q15. Какие коммуникационные паттерны между агентами существуют? (!)

Паттерны коммуникации различаются тем, **насколько жёстко связаны агенты** и кто видит чьи сообщения. Основные:

- **Direct function call (handoff)** — агент A вызывает агента B как функцию и ждёт результат. Самый простой и предсказуемый способ. Используется в Swarm и OpenAI Agents SDK.
- **Message passing** — агенты обмениваются сообщениями через очередь/буфер. Именно так работает AutoGen GroupChat.
- **Shared memory / blackboard** — общий state (StateGraph в LangGraph), куда все пишут и читают. Хорош для совместного контекста, но плох для изоляции: каждый видит всё.
- **Broadcast** — сообщение сразу видно всем агентам (group chat).
- **Pub-sub / topic-based** — агент подписан на тип событий. В LLM-системах редко, чаще в backend-микросервисах.
- **Request-response через ledger/queue** — оркестратор кладёт задачу, worker забирает и пишет результат обратно (Task Ledger в Magentic-One).

**Компромисс:** handoff даёт контроль, message passing — гибкость, shared memory — простоту, но создаёт coupling между агентами.

## Q16. Как организовать state в multi-agent системе?

Ключевой вопрос при проектировании state — **что агенты видят вместе, а что держат при себе**. Три уровня организации:

- **Shared state** — общий объект (StateGraph в LangGraph, Crew context в CrewAI), который все видят и обновляют. Плюс — синхронная картина у всех; минус — раздувание контекста (context bloat) и race conditions на параллельных агентах.
- **Per-agent state** — у каждого свой scratchpad/history. Плюс — изоляция и чистый контекст; минус — нужен явный механизм синхронизации между агентами.
- **Conversation history** — может быть общей (group chat) или раздельной (каждый видит только свою ветку диалога).

**Как это делается на практике:**

- LangGraph: `Annotated[list, operator.add]` для накапливаемых полей (messages), обычные поля перезаписываются.
- CrewAI: `context=[previous_task]` пробрасывает output предыдущей задачи дальше.
- Persistence: checkpointer LangGraph → SQLite/Postgres, либо свой механизм через MCP, БД или Redis.

**Главное:** заранее явно решить, что shared, а что private, и не давать агентам видеть лишнего — это вопрос и приватности, и контекст-гигиены (меньше шума → точнее ответы).

## Q17. Типы памяти для multi-agent систем.

Память делят по сроку жизни и характеру данных — как у человека есть «оперативная» и «долгая» память:

- **Short-term (working memory)** — текущий диалог/scratchpad, живёт в state.
- **Long-term episodic** — прошлые задачи и решения; лежат в **vector DB** (pgvector, Pinecone, Qdrant) и достаются по semantic search.
- **Long-term semantic** — устойчивые факты; хранятся в knowledge graph или structured DB.
- **Procedural** — выученные «навыки» (для self-improving агентов вроде Voyager). В проде редко — слишком нестабильно.
- **Shared org memory** — общий «корпоративный мозг» нескольких crew; доступ через MCP-сервер или общий retriever.

**Подводный камень:** запихнуть всю history в системный промпт — тупик: упираешься в context window и в стоимость. Вместо этого комбинируют **summarisation** (сжать старое) + **vector recall** (подтянуть только релевантное).

## Q18. Как организовать handoff между агентами безопасно?

Handoff — это момент, когда один агент передаёт управление другому. Опасен он тремя вещами: потерей контекста при передаче, бесконечным циклом передач (A→B→A→…) и попаданием к неподходящему получателю. Безопасный handoff закрывает каждую из этих дыр.

**Рекомендации:**

- **Явная схема handoff** — агент возвращает `{"handoff_to": "billing", "context": {...}}`, и это валидируется JSON-schema (никаких free-form передач).
- **Разрешённые переходы** — белый список, кому конкретный агент вправе передать управление (по сути state machine).
- **Ограниченная глубина** — счётчик передач с лимитом N; превысил — fail или escalate. Это и есть защита от бесконечного цикла.
- **Сжатие контекста при передаче** — пересылать не всю history, а summary + ключевые факты.
- **Идемпотентные получатели** — на случай повторной передачи при retry.
- **Audit log** — каждый handoff пишется с trace_id, source, target и reason.

В OpenAI Agents SDK и Swarm handoff — first-class concept; в CrewAI/LangGraph он моделируется через manager-агента или conditional edges.

## Q19. Coordination paterns: planner-executor, debate, voting — когда какой? (!)

Три паттерна координации различаются структурой потока:

- **Planner-Executor** — `Planner LLM` строит план и раздаёт его исполнителям: `Planner LLM` → `Executor 1`, `Planner LLM` → `Executor 2`, `Planner LLM` → `Executor 3`.
- **Debate** — два агента подаются на вход критику, тот отдаёт результат финализатору: `Agent A` → `Critic`, `Agent B` → `Critic`, затем `Critic` → `Finalizer`.
- **Voting** — независимые решения сходятся в агрегаторе по большинству: `Agent 1` → `Aggregator / majority`, `Agent 2` → `Aggregator / majority`, `Agent 3` → `Aggregator / majority`.

Паттерны координации различаются тем, **как агенты приходят к итоговому ответу** — через декомпозицию, спор или голосование. Когда какой:

- **Planner-Executor** — один агент строит план, другие выполняют шаги. Для хорошо разложимых задач (research, codegen). Часто планировщик — reasoning model (o1/Claude Opus), а исполнители — модель подешевле (Haiku/Mini): дорогой интеллект тратится только на план.
- **Debate / Critique (actor-critic, reflexion)** — генератор пишет, критик ругает, цикл до сходимости. Заметно поднимает качество на reasoning-heavy задачах, но дорого. Это и есть evaluator-optimizer паттерн Anthropic.
- **Voting / Ensemble** — N независимых агентов решают, побеждает большинство. Снижает разброс (variance) на субъективных задачах ценой N-кратной стоимости.
- **Specialist consultation** — generalist роутит запрос к специалистам (юрист / финансы / медицина). Топология hub-and-spoke.
- **Collaborative refinement** — каждый агент дополняет общий артефакт (blackboard), результат собирается итеративно.

## Q20. Multi-agent + reasoning models — как комбинировать? (!)

Идея проста: **дорогой интеллект — на думанье, дешёвый — на рутину.** Reasoning models (o1, o3, DeepSeek R1, Claude Sonnet с extended thinking) дороги, но сильны в планировании и сложном reasoning. Cheaper models (gpt-4o-mini, Haiku, Gemini Flash) — быстры и дёшевы на массовых операциях. Эффективность достигается их сочетанием, а не выбором одной.

**Рабочие комбинации:**

- **Reasoning as Planner** — o1/R1 декомпозирует и валидирует план, а Haiku/4o-mini исполняют шаги. Снижает cost в 5–10× без потери качества плана.
- **Reasoning as Critic** — основную массу шагов делают cheap models, а финальную проверку качества — reasoning model.
- **Escalation** — cheap-агент пробует сам, и при низкой уверенности эскалирует на reasoning model.

**Антипаттерн:** ставить reasoning model на каждый шаг цикла — это 30+ секунд latency на вызов и счёт в разы выше.

**Cheap-as-router** тоже работает: маленькая модель решает «дёшево достаточно или эскалировать», и большая включается только на действительно сложных случаях.

## Q21. Error handling и failure isolation в multi-agent. (!)

Multi-agent — это распределённая система, поэтому она наследует **все её проблемы отказоустойчивости**, и решаются они теми же приёмами, что в микросервисах:

- **Failure isolation** — падение одного агента не должно ронять всю систему. Каждый вызов в try/catch с fallback-стратегией.
- **Per-agent retry** — exponential backoff на 429/5xx от LLM API и на ошибках парсинга JSON.
- **Circuit breaker** — если агент стабильно падает, временно отключить его, чтобы не тратить вызовы впустую.
- **Timeout per agent** — глобальный таймаут на всю задачу плюс таймаут на каждый шаг.
- **Bounded iterations** — `max_rounds`, `max_steps` обязательны: без них система уходит в бесконечный цикл.
- **Dead-letter / human escalation** — нерешённые задачи уходят в очередь к человеку, а не теряются.
- **Idempotency keys** — повторный вызов с тем же `task_id` не дублирует работу. Критично для tools с side effects: платежи, письма, записи в БД.
- **Compensation / saga** — если многошаговая операция падает посередине, уже сделанное нужно откатить.

CrewAI и AutoGen дают только базовые retries — за production-grade надёжность отвечает разработчик.

## Q22. Что такое orchestrator-workers паттерн и как его сделать самому?

Orchestrator-workers — каноничный hierarchical-паттерн от Anthropic, и собрать его можно за десяток строк без всякого фреймворка. Логика в три шага:

1. Orchestrator-LLM получает задачу и динамически дробит её на subtasks.
2. Каждый subtask уходит worker-LLM — часто параллельно.
3. Orchestrator (или отдельный synthesizer) собирает результаты в финальный ответ.

```python
import asyncio

async def orchestrator_workers(query: str):
    plan = await llm.call(
        prompt=ORCHESTRATOR_PROMPT,
        user=query,
        schema=PlanSchema,  # subtasks: [{id, description, worker_type}]
    )

    async def run_worker(subtask):
        prompt = WORKER_PROMPTS[subtask["worker_type"]]
        return await llm.call(prompt=prompt, user=subtask["description"])

    results = await asyncio.gather(*[run_worker(t) for t in plan["subtasks"]])
    final = await llm.call(
        prompt=SYNTHESIZER_PROMPT,
        user={"query": query, "results": results},
    )
    return final
```

Это уже полноценный multi-agent: три разных промпта — это три «агента», и никакого фреймворка не нужно. Именно на таком простом фундаменте построено ~80% «multi-agent» production-систем.

## Q23. Observability в multi-agent — что трассировать? (!)

Без observability multi-agent — непрозрачный чёрный ящик: когда что-то ломается, ты даже не знаешь, какой из агентов виноват. Минимальный набор того, что нужно трассировать:

- **Distributed tracing** — span на каждый LLM-вызов и tool-вызов с trace_id и parent_span_id. Совместимо с OpenTelemetry, чтобы восстановить полное дерево вызовов.
- **Per-agent metrics** — input/output tokens, latency, error rate, cost по каждому агенту.
- **Cross-agent message log** — кто кому что отправил и в каком state это произошло.
- **Conversation tree** — визуализация handoffs и зависимостей (LangSmith, Langfuse, AgentOps дают из коробки).
- **Tool call audit** — какой агент позвал какой tool, с какими аргументами и что вернулось.
- **Task completion metrics** — success rate end-to-end и по каждому этапу.
- **Cost aggregation** — суммарный $ за конкретную user-task с разбивкой по моделям.

Инструменты: **LangSmith** (LangChain/LangGraph), **Langfuse** (open-source, любой фреймворк), **AgentOps** (multi-framework), **Arize Phoenix** (open-source observability), **Helicone** (LLM gateway с трейсингом).

## Q24. Cost и latency multi-agent vs single-agent.

Главное, что нужно усвоить: **параллелизм лечит latency, но не cost** — общее число вызовов от него не меняется. Реалистичные оценки:

- Multi-agent делает в **3–10× больше LLM-вызовов**, чем single-agent на той же задаче, — cost растёт пропорционально.
- **Параллельные агенты** снижают latency «по часам» (wall-clock), но не cost: сумма вызовов та же.
- **Последовательные** агенты увеличивают и latency, и cost.
- **Reasoning models** в цикле умножают cost ещё в 5–20× из-за длинных thinking traces.

**Стратегии оптимизации:**

- Cheaper models для bulk-операций, reasoning model только для critical decisions.
- **Prompt caching** — Anthropic prompt caching, OpenAI prompt caching экономят 50–90% на повторных system prompts.
- **Bounded iterations** — `max_rounds` жёсткий.
- **Early termination** — критик подтверждает «good enough», loop останавливается.
- **Cost budget per task** — гарду в коде: суммарно ≤ $X на task, иначе fail/escalate.
- Кэширование результатов tools (особенно search, retrieval).

В B2C продуктах одна multi-agent-task может стоить $0.10–$5 — это много. В B2B/enterprise часто приемлемо.

## Q25. Как evaluate multi-agent систему?

Оценивать нужно на двух уровнях: **систему целиком** (доходит ли до верного ответа) и **каждого агента отдельно** (где именно ломается). Метрики:

- **End-to-end task completion rate** — главная: доля задач, где финальный output корректен относительно golden dataset.
- **Per-agent quality** — оценка каждого агента изолированно через unit-test-подобные промпты; помогает локализовать слабое звено.
- **Trace-level metrics** — длина диалога, число handoffs, retries и tool calls.
- **Cost per successful task** — суммарный $ на успешный исход; ключевой показатель эффективности.
- **Latency P50/P95/P99** end-to-end.
- **Failure mode taxonomy** — категоризация отказов: timeout, JSON error, wrong tool, infinite loop, галлюцинация, фактическая ошибка.
- **Human review subset** — 5–10% задач уходят на ручной аудит как страховка от того, что метрики не ловят.

Frameworks: **LangSmith evals**, **AutoGen Bench**, **Phoenix evals**, **Inspect AI** (UK AISI), кастомные harness с pytest.

## Q26. Production-ready чек-лист для multi-agent системы. (!)

Этот список — про то, чтобы система не разорила и не навредила в проде. Прежде чем выкатывать, проверь каждый пункт:

- **Bounded iterations** на все loops (`max_rounds`, `max_handoffs`, `max_tool_calls`).
- **Cost budget guard** — hard limit на задачу, soft warning на 50%.
- **Timeout per agent + global timeout**.
- **Идемпотентные tools** — tools с side effects принимают `idempotency_key`.
- **Retry с exponential backoff** на 429/5xx LLM API.
- **Structured logging + tracing** (OpenTelemetry / LangSmith / Langfuse).
- **Eval suite на golden dataset** — катиться можно только если pass-rate не упал.
- **Feature flags / canary** для нового агента или промпта.
- **Human-in-the-loop escalation** для критичных решений (платежи, удаления, юридические).
- **Sandbox для code execution** — Docker/gVisor/Firecracker, NEVER eval() в проде.
- **PII/secrets redaction** в логах LLM calls.
- **Rate limiting** между агентами (особенно если они в loop).
- **Versioning промптов** + A/B testing.
- **Graceful degradation** — если premium-модель недоступна, fallback на cheaper.
- **Dead-letter queue** для неуспешных задач + alerting.

## Q27. Топ anti-patterns в multi-agent. (!)

Большинство антипаттернов сводятся к одному: **сложность ввели там, где её можно было избежать.** Конкретные грабли:

- **«Слишком много агентов»** — 5+ ролей с пересекающимися обязанностями. Overhead на координацию съедает всю выгоду; часто single-agent с tools работает лучше.
- **Расплывчатые роли** — `goal: "помогать пользователю"` бесполезен. Цели должны быть конкретными и измеримыми, иначе модель «плывёт».
- **Infinite handoff loop** — A → B → A → B без условия остановки. Лечится bounded iterations + state machine с белым списком переходов.
- **Можно было одним вызовом** — overengineering: вместо одного хорошего промпта собрали Crew из 4 агентов.
- **Shared mutable state без локов** — race conditions на параллельных агентах.
- **Полная history в контексте каждого агента** — лишние токены, latency и путаница. Передавать нужно только summary.
- **No observability** — «работает или нет — узнаем от пользователя». В multi-agent это смертельно: без трейсинга невозможно отладить.
- **Manager как bottleneck** — всё гонится через manager-LLM на каждом шаге, отчего система становится последовательной и дорогой.
- **Захардкоженная модель в каждом агенте** — нельзя ни переключить, ни сделать fallback при недоступности.
- **Tool sprawl** — каждому агенту выдали все 50 tools «на всякий случай». Это путает модель, дорого и неконтролируемо; нужен per-agent allowlist.
- **Нет JSON-schema на outputs** — парсинг ad-hoc регулярно ломается. Использовать structured output / function calling.

## Q28. Что такое supervisor pattern в LangGraph?

Supervisor — это специальный узел-диспетчер, через который проходит всё управление: workers не общаются между собой, а каждый раз возвращаются к нему за следующим решением. Цикл работы:

1. Получает текущий state.
2. Решает (через LLM или обычный код), какому worker передать управление, либо вернуть `END`.
3. После завершения worker управление снова возвращается в supervisor.

Граф управления выглядит так:

- `Supervisor` по решению `route` направляет управление одному из workers: `Supervisor` → `Researcher`, `Supervisor` → `Writer`, `Supervisor` → `Critic`.
- После работы каждый worker возвращает управление обратно: `Researcher` → `Supervisor`, `Writer` → `Supervisor`, `Critic` → `Supervisor`.
- Когда supervisor решает завершить, он переходит по ветке `FINISH`: `Supervisor` → `END`.

**Плюсы:**

- Централизованный control flow — всё в одном месте, легко отлаживать.
- Supervisor можно сделать rule-based (дёшево), он не обязан быть LLM.
- Workers вызываются повторно — например, Critic несколько раз подряд.
- У каждого worker свой узкий tool-set, что снижает путаницу.

По сути это LangGraph-аналог CrewAI `Process.hierarchical` и Magentic-One Orchestrator.

## Q29. Real-world примеры multi-agent систем — что почитать. (!)

Эти проекты — учебники по тому, как multi-agent выглядит на практике (и как часто не выглядит):

- **GPT-Researcher** — research crew для deep research по запросу: planner + N параллельных researchers + writer.
- **ChatDev** — симуляция dev-команды: CEO/CTO/Programmer/Reviewer/Tester. Наглядно показывает waterfall, перенесённый в multi-agent.
- **MetaGPT** — симуляция SDLC, более структурированная, чем ChatDev, с SOPs (standard operating procedures).
- **BabyAGI / AutoGPT** — task decomposition + execution loop. Исторически важны, но в проде не используются.
- **Magentic-One (Microsoft)** — generalist-агент с фиксированным набором специалистов.
- **Devin / OpenDevin** — autonomous software engineer, под капотом multi-agent.
- **Anthropic Claude Computer Use** — наоборот, single-agent с мощным computer-use tool. Контраст-пример: вместо оркестра — один сильный агент плюс один сильный инструмент.

**Главный вывод обзора:** «прорывных» multi-agent продакшен-систем мало. В реальности большинство prod-кейсов — это workflows + 1–2 специализированных LLM-вызова, а не «многоагентные оркестры».

## Q30. Что такое «agents-as-tools» паттерн и когда он лучше handoff?

**Agents-as-tools** — это когда вспомогательные агенты подключены к primary-агенту как обычные tools. Primary зовёт `search_specialist(query)` так же, как любой инструмент: под капотом отрабатывает другой LLM-агент, но **primary не отдаёт управление** — получил результат и продолжает работу. Это ключевое отличие от handoff, где control физически переходит к другому агенту.

**Сравнение с handoff:**

| | Handoff | Agents-as-tools |
|--|---------|-----------------|
| Кто owns control | Receiver | Original (caller) |
| Возврат control | Только если receiver сам передаст | Автоматически после tool return |
| Сложность | Выше (state machine) | Простая (обычный function call) |
| Контекст | Передаётся receiver-у | Только через args/return |
| Лучше для | Длинные «переключения роли» | Узкие consultative задачи |

**Рекомендация:** в большинстве примеров Anthropic советует именно agents-as-tools — это проще и безопаснее. Primary-агент остаётся «у руля», а вспомогательные агенты — это инструменты со своими внутренними промптами и моделями, у которых нельзя «потерять» контроль.

```python
def search_specialist(query: str) -> str:
    """Глубокий research по узкой теме."""
    return llm.call(prompt=SPECIALIST_PROMPT, user=query)

tools = [search_specialist, other_tools...]
primary_agent_loop(query, tools)  # primary видит specialist как обычный tool
```

Сегодня это доминирующий «multi-agent» паттерн на практике — без фреймворка, без handoffs, понятный и тестируемый.

---

## See also

- [AI Agents](./ai-agents-interview.md)
- [Model Context Protocol (MCP)](./mcp-interview.md)
- [Function Calling](./function-calling-interview.md)
- [LLM Integration Patterns](./llm-integration-patterns-interview.md)
- [Reasoning Models](./reasoning-models-interview.md)
- [Prompt Engineering](./prompt-engineering-interview.md)
- [System Design Interview](../system-design/system-design-interview.md)
- [Distributed Systems](../architecture/distributed-systems-interview.md)
