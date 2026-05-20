# Quiz-app data (signal #7)

**DB:** `modules/quiz-app/data/db/interview.db`
**Snapshot:** 2026-05-20

## Главный вывод

- **Вопросов сгенерировано:** 8366
- **Прошёл пользователь:** 0 (review_state.repetitions=0 у всех 8366)
- **daily_activity:** 0 записей
- **user_topic_stats:** 0 записей

**Прямого evidence пробелов из quiz-app НЕТ** — приложение использовалось для генерации, но не для самопроверки.

## Мета-сигнал: % важных вопросов по теме

Прокси приоритетности темы: `is_important / total` — что автор сам считал критичным.

| Тема | Вопросов | Важных | % важности |
|------|----------|--------|------------|
| databases/database-architecture-interview|41|34|83% |
| data-engineering/kafka-streams-interview|28|23|82% |
| algorithms/data-structures/stacks-queues-interview|25|19|76% |
| ai-ml/llm-basics-interview|30|22|73% |
| algorithms/data-structures/trees-interview|34|24|71% |
| algorithms/algorithmic-paradigms/backtracking-interview|27|19|70% |
| frameworks/spring/spring-framework-interview|40|28|70% |
| system-design/design-payment-system-interview|20|14|70% |
| frameworks/spring/spring-cloud-interview|43|30|70% |
| data-engineering/apache-spark-interview|35|24|69% |
| data-engineering/data-lake-lakehouse-interview|28|19|68% |
| programming-languages/go/go-testing-interview|28|19|68% |
| algorithms/sorting-searching/sorting-algorithms-interview|31|21|68% |
| data-engineering/apache-flink-interview|31|21|68% |
| algorithms/algorithmic-paradigms/recursion-interview|27|18|67% |
| programming-languages/go/go-interview|36|24|67% |
| programming-languages/go/go-concurrency-interview|35|23|66% |
| algorithms/data-structures/linked-lists-interview|32|21|66% |
| ai-ml/embeddings-interview|29|19|66% |
| frameworks/jvm-alternatives/micronaut-interview|26|17|65% |

## Рекомендация в план

- Включить в weekly cycle (Чт): прогонять MCQ через quiz-app **из уже сгенерированных** для темы цикла. Не генерировать новые — уже есть.
- После прохождения review_state будет заполняться → следующий месячный re-run даст реальный signal-7.
