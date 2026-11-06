# Test Foundations Chaos — Phase B review

Независимые review-записи объединены без изменения содержания. Статусы `UNVERIFIED` сохранены там, где первичный источник не был проверен.

## Q1 — выбор D

Прочитаны stem, все четыре option JSON с секциями, q1-theory-0, весь preamble-0/1/2 и начало mcq-quality-standard.md. Правила examiner-review, surface-parity-rules и russian-readability-rules не прочитаны; первичные источники не проверялись. Выводы секций основаны на доступных option records. Числовые сравнения длин не выполнялись.

| Критерий | Статус | Причина |
|---|---|---|
| CONTENT | PASS | Вопрос доступен только по видимому stem: что означает стратегия и зачем она нужна. D отвечает на обе части. A классификацию уровней принимает за обоснование достаточности; B приравнивает стратегию к метрикам и превращает долю успешных тестов в вероятность отказа; C подменяет стратегию отчётом и отделяет обоснование будущих проверок. Каждый дистрактор защищаем как распространённое смешение соседних понятий; его буквальная модель остаётся ошибочной без добавления условий. |
| FACTS | UNVERIFIED | Первичный источник по стратегии в этой фазе не проверен. Теория ссылается на CTFL 4.0.1 §5.1.1, но ссылка/утверждение не проверены независимо. |
| LINKAGE | PASS | Проверенная теория Q1 связывает стратегию с обоснованием выбора и приоритетов по целям качества, рискам, ограничениям и ресурсам; связь вариантов с этим содержанием ясна. Проверка ограничена предоставленным фрагментом теории. |
| SECTIONS | PASS | Каждый из четырёх записей вариантов содержит законченный текст и объяснительные секции, тематически относящиеся к текущему тезису: фактический смысл, источник смешения, следствие и корректировка. У D даны дополнительные объяснительные секции; структурный паритет секций не подтверждён как требование. |
| SFPL | PASS | Все четыре варианта представляют законченное определение и назначение в сопоставимом каркасе и тоне, с близкой конкретностью. Нет формы, которая сама по себе выдаёт ключ. Ошибочность законченной модели не трактуется как неполнота. |
| ANSC | PASS | Вопрос сформулирован ясно и естественно; профессиональный тон и предметная точность новой формулировки приемлемы на доступном материале. |
| RU | PASS | Фразы грамматически связны и читаемы по-русски. Термины «стратегия», «проверки», «риски», «ресурсы» употреблены последовательно; неестественных оборотов не обнаружено. |

| Вариант | Полнота | Истинность | Охваченные части | Защита / проблема |
|---|---|---|---|---|
| A | complete | false | что такое стратегия, зачем нужна | Классификация уровней и инструментов действительно описывает состав проверок; можно защищать вариант как сокращённое понимание стратегии через таксономию и планирование. Но из одних названий уровней не следует достаточность для продукта. Проблема: Подмена обоснованного выбора классификацией и необоснованный вывод о достаточности. |
| B | complete | false | что такое стратегия, зачем нужна | Метрики качества могут входить в планирование стратегии и помогать принимать решения; доля пройденных тестов — реальный показатель выполнения набора. Но сама доля не задаёт вероятность отказа продукта без валидированной модели и данных. Проблема: Отождествляет стратегию с набором метрик и приписывает одному проценту интерпретацию вероятности отказа. |
| C | complete | false | что такое стратегия, зачем нужна | Отчёт действительно фиксирует статусы и дефекты и может информировать пересмотр будущих проверок. Однако он описывает выполнение, а не сам обоснованный подход; вариант явно исключает обоснование из стратегии. Проблема: Смешивает отчёт о результатах с решением о подходе и приоритетах. |
| D | complete | true | что такое стратегия, зачем нужна | Согласуется с предоставленной теорией Q1: выбор проверок и приоритетов обосновывается целями качества, рисками и ограничениями; ресурсы и обратная связь помогают направлять работу. |

Источники:
- https://istqb.org/?download_id=3345&sdm_process_download=1 — Теория указывает CTFL 4.0.1 §5.1.1 как связь тест-плана с целями, ресурсами, процессом и политикой/стратегией; первоисточник и конкретный тезис не сверялись. Проверено: UNVERIFIED; версия: CTFL 4.0.1 (указана в теории; не проверена).

## Q2 — выбор B

Reviewed Q2 stem, all four option records and sections, and both supplied theory chunks. Previously read preamble and standard opening were not reread. No primary source verification was performed; source claims remain UNVERIFIED.

| Критерий | Статус | Причина |
|---|---|---|
| CONTENT | PASS | Q2 asks what the pyramid is and how to apply it. B covers distribution across narrow unit, interaction, and broad E2E checks, with allocation based on risk and feedback cost. A mistakes layers for mandatory execution order, C reverses the pyramid and overuses broad paths, D assumes code coverage makes integration/E2E redundant. Each wrong model is complete and can be defended as a plausible misconception without hidden assumptions. |
| FACTS | UNVERIFIED | The supplied Q2 theory references Mike Cohn’s original unit/service/UI layers and a middle service layer. The primary source URL is recorded but was not independently opened or checked in this review. |
| LINKAGE | PASS | All options connect directly to the visible question and to the supplied theory’s explanation of test boundaries, distribution, and application. Scope is limited to the two supplied theory chunks. |
| SECTIONS | PASS | All four option records have explanatory sections that address the selected claim, its misconception, consequence, and correction. The sections remain about the corresponding option. B has extra instructional sections, consistent with its correct-answer role. |
| SFPL | PASS | All four options are complete sentences with a definition/model and an application or implication. Their framework and specificity are comparable; B’s contextual risk/feedback clause is relevant substance and not by itself a style cue. Tone is professional across all four. |
| ANSC | PASS | The prompt is clear and answerable. The distractors are plausible but clearly incorrect when read literally; no hidden qualification is needed to reject them. |
| RU | PASS | Russian phrasing is grammatical and readable. English testing terms are used consistently with the theory. No material language defect found. |

| Вариант | Полнота | Истинность | Охваченные части | Защита / проблема |
|---|---|---|---|---|
| A | complete | false | что такое Testing Pyramid, как применять | Pipeline tasks are often ordered from fast narrow checks to broader checks, so a team might describe a practical execution sequence this way. However the model itself represents distribution across boundaries and does not make that execution order the definition or proof of sufficiency. Проблема: Turns a distribution model into a required execution order and treats order compliance as evidence of sufficiency. |
| B | complete | true | что такое Testing Pyramid, как применять | Matches the supplied theory: broad base of small isolated tests, interaction checks in the middle, selected broad system paths above, with allocation adapted to risks and feedback cost. |
| C | complete | false | что такое Testing Pyramid, как применять | Broad user paths can cover many behaviors and may be important for system-level confidence. But making them the base and using them for most logic branches reverses the described model and sacrifices narrow local feedback. Проблема: Reverses the common pyramid and overstates broad E2E as the main means of branch-level checking. |
| D | complete | false | что такое Testing Pyramid, как применять | Reducing genuine duplicate assertions can simplify a suite. But equal source-code coverage does not test the same boundary, dependency behavior, or user path, so unit coverage alone does not make broader checks redundant. Проблема: Equates code coverage overlap with equivalence of test purpose and boundary. |

Источники:
- https://www.mountaingoatsoftware.com/agile/the-forgotten-layer-of-the-test-automation-pyramid — The supplied theory cites Cohn for a unit/service/UI pyramid and the importance of a middle service layer; URL not independently opened or checked. Проверено: UNVERIFIED; версия: UNVERIFIED.

## Q23 — выбор A

Reviewed Q23 stem, all four option records and sections, and all supplied q23 theory chunks. Primary source opened and checked. Previously read preamble and rules were not reread; no length metrics were calculated.

| Критерий | Статус | Причина |
|---|---|---|
| CONTENT | PASS | A directly defines Chaos Engineering through a controlled hypothesis-based experiment and comparison against a measured baseline. B mistakes a mocked unit test for evidence about a running distributed system; C wrongly says randomness removes the need for a hypothesis; D wrongly equates credibility with maximum blast radius. Each wrong answer is a complete model that can be defended as a plausible misconception without adding hidden conditions. |
| FACTS | PASS | Principles of Chaos Engineering (last updated March 2019) defines the practice as system experimentation to build confidence, and specifies measurable steady state, a hypothesis in control and experiment groups, introduced real-world variables, and attempted disproof by comparing steady state. It also directs practitioners to minimize and contain blast radius. These claims support A and refute B, C, and D as written. |
| LINKAGE | PASS | The visible question and supplied theory align with the primary source’s controlled-experiment definition. Theory also correctly distinguishes local component tests from system experiments and treats random discovery as potentially useful without equating it to hypothesis testing. |
| SECTIONS | PASS | Each answer record has sections explaining actual meaning, confusion, consequence, and correction; they address the associated variant. D’s consequence is conditional and avoids promising inevitable incident or guaranteed rollback. A’s additional sections offer useful context and are relevant to its claim. |
| SFPL | PASS | The four options share a compact definition/characterization frame and similar level of specificity. A contains the complete canonical experiment elements; its extra completeness is required by the concept and does not alone create an unrelated style cue. All wrong variants still express complete, assessable models. |
| ANSC | PASS | The visible prompt is broad but answerable. A is the only answer consistent with the source. B does not claim ordinary tests are useless; it makes the narrower invalid inference that a successful stub-based unit run confirms a running distributed system under dependency failure. C permits useful exploratory observations but wrongly makes observations themselves the criterion without a prior hypothesis. D’s first experiment at maximum scope is unjustified by the source’s containment principle. |
| RU | PASS | Russian wording is grammatical and professional. Technical terms (hypothesis, steady state, blast radius) are used in context; no material readability issue found. |

| Вариант | Полнота | Истинность | Охваченные части | Защита / проблема |
|---|---|---|---|---|
| A | complete | true | что такое Chaos Engineering | Matches the source’s controlled experiment, hypothesis, measurable normal behavior, fault injection, and comparison. |
| B | complete | false | что такое Chaos Engineering | A unit test with stubs can usefully verify local handling of a dependency error and is a legitimate component check. It cannot establish how a running distributed system behaves under an actual dependency failure. Проблема: Overclaims system-level evidence from an isolated test that substitutes scripted responses. |
| C | complete | false | что такое Chaos Engineering | Random selection of fault variables can help explore a system, and unexpected observations can generate valuable hypotheses. But the claim explicitly removes the prior hypothesis and uses observations alone as the criterion for the experiment. Проблема: Confuses exploratory discovery with a hypothesis-driven experiment and assessment. |
| D | complete | false | что такое Chaos Engineering | A controlled experiment may test a hypothesis and observe a failure; wider exposure could be chosen in some later, justified experiment. The source says to minimize and contain fallout; it does not support maximum exposure as a credibility requirement or first experiment scope. Проблема: Treats maximum blast radius as a source of validity and prescribes all services for the first experiment. |

Источники:
- https://principlesofchaos.org/ — Definition as controlled system experiment; measurable steady state; hypothesis in control and experiment groups; introduce real-world variables; compare outcomes to try to disprove hypothesis; minimize and contain blast radius. Проверено: 2026-11-06 (UTC date; web page opened in this review); версия: Principles of Chaos Engineering, page states last update March 2019.

## Q30 — выбор A

Reviewed Q30 stem, all four option records and sections, and all supplied Q30 theory chunks. Opened Fowler’s primary article and checked its definition and verification distinction. No runtime API was executed.

| Критерий | Статус | Причина |
|---|---|---|
| CONTENT | PASS | The stem asks for a strategy for choosing test doubles. A answers by selecting a real dependency or double based on the contract, model fidelity, feedback, and maintenance cost. B claims a stub verifies real SQL/database constraints; C treats the count of doubles and verifications as proof of quality; D treats call order as equivalent to the computed result. Each wrong answer is a complete, defensible misconception without invented conditions. |
| FACTS | PASS | Fowler’s article distinguishes stubs and mocks by verification style: state verification versus behavior verification. A stub supplies canned answers; a mock verifies expected calls. This supports A’s role-based selection and refutes B and D as equivalences. The theory’s counterexample for B is valid: a stubbed repository can return a value without executing SQL, so it cannot establish SQL or real database constraints. Runtime API behavior was not executed. |
| LINKAGE | PASS | The supplied theory directly answers the visible stem by selecting substitutes under a tested contract, then weighing fidelity, feedback, and maintenance. All options concern that same selection axis. |
| SECTIONS | PASS | All four option records include explanations, confusion source, counterfactual, and correction tied to their claim. The real-database and computation-result examples directly test the wrong options’ implied equivalences. |
| SFPL | PASS | Each option is a complete selection principle with a stated rationale or consequence. Framework, tone, and specificity are comparable. A’s extra nuanced qualifiers match the breadth of a strategy definition and are not a gratuitous length or tone cue. |
| ANSC | PASS | The four options test one coherent axis: what evidence and tradeoffs guide double selection. The wrong models can be defended narrowly: simplicity can matter, many replacements may aid isolation in a particular test, and interaction order may itself be a contract. Those possibilities do not rescue their absolute claims that SQL is checked, quantity proves design quality, or interactions equal all computed results. |
| RU | PASS | Russian prose is coherent and professional. English terms are conventional in this technical context and the option clauses are readable. |

| Вариант | Полнота | Истинность | Охваченные части | Защита / проблема |
|---|---|---|---|---|
| A | complete | true | выбор test doubles и основание выбора | The supplied theory supports selecting a real dependency or appropriate double according to the contract and needed feedback, while considering fidelity and maintenance cost. |
| B | complete | false | выбор test doubles и основание выбора | A stub can make a repository test concise and provide a controlled value for application logic. That is useful for that contract, but it says nothing about SQL execution, transactions, or actual database constraints. A repository stub that always returns a configured object is a direct counterexample. Проблема: Confuses response simulation with verification of the real database implementation. |
| C | complete | false | выбор test doubles и основание выбора | A test with several doubles may isolate the SUT, and verify calls may be appropriate where interaction is part of the contract. But raw count cannot show whether those interactions are important or whether the test is coupled to incidental implementation. Two tests with the same number can verify different contracts. Проблема: Uses quantity as a proxy for isolation and design quality. |
| D | complete | false | выбор test doubles и основание выбора | Expected call order can be a meaningful behavior contract for a collaborator. Yet a SUT can make those calls in the expected order and still compute or return an incorrect value; the checks are distinct and may be combined where useful. Проблема: Treats behavior verification as an equivalent substitute for checking all relevant state/results. |

Источники:
- https://martinfowler.com/articles/mocksArentStubs.html — Mock objects are one form of test double; Fowler distinguishes state verification from behavior verification, and stubs from mocks. This supports separating configured responses from interaction expectations. The local-review counterexample is that a stubbed repository can return canned data without executing SQL; proper real-database verification therefore requires an appropriate real dependency. Проверено: 2026-11-06T00:33:54Z; версия: Article dated 02 January 2007; no separate revision version stated.
