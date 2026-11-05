# LLM Integration Q14 — распределение общей квоты

Дата: 2026-11-05. Статус: AUTHOR_VERIFIED, Q14:B0, с известным ключом. Прочитаны видимый вопрос, теория Q14, все A/B/C/D и sections. Это ограниченная авторская проверка, не независимый review и не FINAL.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/llm-integration-patterns-interview.json`, SHA-256 `cff06eac4a46165b221b0169ebbf2e6967f66130c52f095167c96fa539e1ca9b`.

Теория: `cheatsheets/interview/ai-ml/llm-integration-patterns-interview.md`, SHA-256 `b5b724b40c48e31aa71f420278927440ed55edf4053140b095f949f8a92693e5`. Markdown не редактировался. Сохранены ключ A, номер, block_idx, question_text и порядок.

A проверяет согласованный допуск по запросам и токенам до отправки с последующей сверкой usage. B учитывает до вызова только запросы; C откладывает входной токенный бюджет; D выдаёт полный лимит провайдера каждой реплике. Все четыре варианта предлагают действие до вызова и различаются моделью учёта, без скрытого условия только в пояснении.

В D.text явно закреплён полный лимит в каждом инстансе: прежнее описание локальных бюджетов допускало безопасное распределение долей и делало wrong защищаемым. A.edge_cases, D.what_actually и D.how_it_should_be теперь разрешают два подхода: общее атомарное состояние или заранее выделенные безопасные доли применимых бюджетов. Деление долей не выдано за алгоритм провайдера; нужно ограничить суммарный допуск, включая burst, и избежать повторной выдачи одной доли при перераспределении. Это инженерная граница локального ограничителя, а не гарантия отсутствия 429.

Finding F-Q14-QUOTA-PARTITIONING из [проверки теории](../../../../interview-coverage/llm-token-bucket-theory-review-2026-11-05.md) исправлен автором; независимое закрытие не заявлено. Остальные 27 вопросов не принимались этим scope; прежняя независимая evidence не пересчитывалась и не объявляется доказательством для изменённого Q14.

Источники перепроверены 2026-11-05: [OpenAI rate limits](https://developers.openai.com/api/docs/guides/rate-limits), [Claude rate limits](https://platform.claude.com/docs/en/api/rate-limits). Точные числа квот не закрепляются. RPM/TPM и раздельные RPM/ITPM/OTPM — правила конкретных провайдеров; локальное резервирование и распределение долей — предложенный дизайн допуска. Provider API, Redis, конкурентное резервирование, stream failures и реальная quota reconciliation NOT_RUN. Новые независимые Phase A/B и FINAL NOT_RUN; работающий сервис не менялся.

Проверки текущего SHA: verify-mcq-json PASS, 28 Q / 28 блоков; semantic review NOT_RUN. У Q14 нет findings anti-tell/linkage/SKEL. По всей теме остаются 27 anti-tell блоков, 21 SKEL hard-fail блок, linkage errors=4 и warnings=36. Answer-parity exit 1: correct-longest=27/28, option-length-ratio=5.71 (Q22), correct/wrong average=1.997, detail-parity=0.786, style-guessability=0.964. Эти результаты показывают, что остальная тема требует отдельного прохода; они не объявлены PASS. Логи и SHA: llm-q14-quota-final-checks.json и check-0..6.log в рабочем пространстве.
