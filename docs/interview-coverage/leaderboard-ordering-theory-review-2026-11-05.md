# Авторская проверка теории: leaderboard Q1/Q3

Дата: 2026-11-05. Проверены только изменённые Q1/Q3 из пяти вопросов;
остальные три **NOT REVIEWED**. Q2 прочитан как контекст tie rules,
но его MCQ не принимался. В paired JSON прочитаны Q1:B0/B1 и Q3:B0/B1
полностью, включая все A/B/C/D и sections. Известные ключи, не blind
review; `FINAL` не заявляется.

## Снимок

- MD: `cheatsheets/interview/system-design/design-leaderboard-interview.md`.
- MD SHA-256: `c114522d1b84298c3318fe282741da3fecebf8a4854998f8db60785902f8f420`.
- Seed: `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-leaderboard-interview.json`.
- Seed SHA-256: `b792689b55909aa8d1d9dceeb091754c6bf6c86206b088439c9d8806fb26063b`.

## Содержательная проверка

Q1: Sorted Set хранит один member на игрока, ZADD обновляет score,
ZRANGE REV и ZREVRANK соответствуют описанным top-N и ordinal rank.
Уточнено nil/null при отсутствии ключа/игрока: к отсутствию нельзя
прибавить 1 и объявить первое место. Competition/dense rank остаются
отдельной темой Q2. Сложности команд проверены в Redis docs. Устаревший anchor ссылки Redis Q11
исправлен по текущему заголовку; сама Redis-шпаргалка не изменялась.

Точность integer scores ограничена double; предупреждение о диапазоне
±2^53 включительно подтверждено. Python float показал совпадение
представлений 2^53 и 2^53+1; это **не Redis runtime**. Redis Cluster
асинхронно реплицирует и допускает потерю acknowledged write при
некоторых сбоях, поэтому источник истины и rebuild projection нужны
по отдельному контракту долговечности.

Q3: дельта при повторе начисляется дважды, абсолютный score требует
защиты версии от запоздавшего итога. Источник для outbox заменён на
описание самого паттерна: данные и намерение фиксируются одной
транзакцией, consumer учитывает повторы. Добавлено условие одного
hash slot при multi-key атомарном guard Redis Cluster, с примерами
общего hash tag. Сам tag не гарантирует атомарность или отсутствие ошибок.

Watermark уточнён как event-time progress; late events и политика
оператора отделены от бизнес-дедлайна наград. Ссылки тела Q3 закреплены
на **Flink release-2.3**, страницы маркированы v2.3.0. Непреднамеренная
замена URL в общей шапке отменена; вне Q1/Q3 изменён только updated.

## Согласование MCQ

Четыре прочитанных блока согласованы по основной модели:

- Q1:B0 B — Sorted Set/top/rank; Q1:B1 B — точный integer-порядок при
  выходе score за безопасный диапазон.
- Q3:B0 A — event ID/версия и late policy; Q3:B1 B — outbox и атомарная
  защита версии вместо раздельной проверки и ZADD.

Новые детали nil/null и hash-slot являются дополнением теории,
существенного противоречия ключам не найдено. Это не SFPL/ANSC или
независимая приёмка. JSON не изменялся.

## Первичные источники

- [Redis ZADD](https://redis.io/docs/latest/commands/zadd/),
  [ZRANGE](https://redis.io/docs/latest/commands/zrange/),
  [ZREVRANK](https://redis.io/docs/latest/commands/zrevrank/).
- [Redis Cluster specification](https://redis.io/docs/latest/operate/oss_and_stack/reference/cluster-spec/)
  — write safety, hash tags, multi-key operations.
- [Transactional outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html).
- [Flink 2.3 watermarks](https://nightlies.apache.org/flink/flink-docs-release-2.3/docs/dev/datastream/event-time/generating_watermarks/),
  [allowed lateness](https://nightlies.apache.org/flink/flink-docs-release-2.3/docs/dev/datastream/operators/windows/#allowed-lateness).

## Проверки и ограничения

Структурная проверка paired файлов leaderboard и metrics: **PASS**,
46 Q / 48 блоков, не семантическая приёмка остальных вопросов.
Frontmatter разобран Ruby Psych; номера/IDs, scope, локальные
ссылки/anchors, fences и адресный diff --check проверены.

redis-server/redis-cli локально не найдены. Redis/Cluster, Flink,
outbox worker, failover и нагрузка **NOT RUN**; серверы не запускались,
пакеты не устанавливались, Docker не использовался. Проверка — чтение
источников и ограниченная математика double, не имитация Redis.

Пакет: MD и этот отчёт, точный список и SHA:
`/tmp/prepared-leaderboard-theory-20261105.json`.
Исходная копия: `/tmp/leaderboard-theory-20261105/before/`.
Сторонняя работа, JSON и общие registries/checkpoints не затронуты.
