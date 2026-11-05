# JDBC и пул соединений — авторская редакция MCQ

- Дата: 2026-11-05; исполнитель: Codex (точный model-id не экспортирован).
- Статус: `LLM_READY`. Авторский проход с доступным ключом; независимое ревью и `FINAL PASS` не заявлены.
- Охват: Q1–Q6, шесть блоков B0; прочитаны видимые вопросы, вся теория, A/B/C/D и все sections.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/databases/jdbc-connection-pool-interview.json`; SHA-256 `be7349c8d0014caffcd5a19572b3e8da0e0930cf8cff8a8e46ae832905fcd2b8`.
- Парный Markdown: `cheatsheets/interview/databases/jdbc-connection-pool-interview.md`; SHA-256 `60b809a95e9e2efe04051f2c007b278cc37c3d8561cd181b3d6bfe0374bbdc0f`. Теория не изменялась.

## Изменения

Q1 различает фабрику, необязательный пул, переиспользование физических соединений и границу транзакции. Q2 явно ограничен собственным JDBC с пулом во всех четырёх вариантах и сравнивает способы завершить операцию: GC, try-with-resources, неверный порядок и автоматический возврат. Указана отдельная граница Spring-managed release. Q3 устраняет зависимость от недоказанного rollback при close; ошибка самого commit может оставить неизвестный исход. Q4 отделяет выгоду batch от атомарности, результаты команд от полного отката, а новую транзакцию от устранения дублей. Q5 различает обход строк, fetch hint и фактический режим драйвера; устранена неоднозначность местоимения при закрытии Statement. Q6 дополняет метрики ошибками создания соединений и не выдаёт leak detection за доказательство разрыва связи.

## Сопоставление вариантов

Ключ был доступен. Проверены содержание, полнота, форма и русский текст всех вариантов. Это авторское сравнение, а не независимый blind-проход.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q1/B0 | фабрика и необязательное переиспользование | интерфейсу приписан обязательный пул | пул принят за создание при каждой выдаче | пул принят за общую транзакцию |
| Q2/B0 | GC принят за своевременное закрытие | ограниченный scope и возврат через close | чтение после закрытия Statement | пул сам определяет конец использования |
| Q3/B0 | close принят за гарантированный rollback | rollback должен отменить autocommit | завершение передано следующему пользователю | явное завершение перед освобождением |
| Q4/B0 | исключение принято за полный откат | batch, транзакция, counts и rollback | набор принят за одну атомарную SQL-команду | новая транзакция принята за дедупликацию |
| Q5/B0 | rows.next доказывает streaming | fetch hint принят за обязательный режим | документация и измерение режима | буферизация принята за независимость ResultSet |
| Q6/B0 | выдача, метрики и ошибки создания | acquisition принят за срок SQL | timeout принят за доказательство полной занятости | leak detection принят за доказательство разрыва |

## Первичные источники

- [Java SE 25 DataSource](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/javax/sql/DataSource.html)
- [Java SE 25 Connection](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html)
- [Java SE 25 Statement](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html)
- [Java SE 25 BatchUpdateException](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/BatchUpdateException.html)
- [pgJDBC cursor mode](https://jdbc.postgresql.org/documentation/query/)
- [Spring DataSourceUtils](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/datasource/DataSourceUtils.html)
- [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)

## Проверки и ограничения

`verify-mcq-json`: PASS, files=1, questions=6, blocks=6; semantic review NOT_RUN. Номера Q/B, видимые вопросы, порядок и ключи A/B/D/B/C/A сохранены. Anti-tell: 0/6 блоков с сигналами. Linkage: errors=0, warnings=0, manual_review=0. SKEL pre-check: hard_fails=0, warn_blocks=0; это не независимая приёмка.

`mcq-answer-parity-gate`: exit 1. CORRECT_LONGEST_RATE=0.5, OPTION_LENGTH_RATIO=1.60, STYLE_GUESSABILITY=0.5. Правильный ответ длиннее по словам в 3/6 блоков; эти сигналы оставлены открытыми для независимой проверки. Q2 сравнивает все три ресурса и возврат, Q3 — исход своей транзакции и освобождение, Q5 — способ обхода и его сетевые предположения, Q6 — диагноз одного симптома; правильные варианты не являются единственными, описывающими обе части вопроса. Фразы не растягивались ради порогов и ключи не переставлялись.

`mcq-struct-scan`: chars:max=0.583 на шести блоках; статистика на таком наборе не доказывает неразличимость. `mcq-surface-parity --file`: Q5 отмечен за две против трёх API-ссылок в backticks; третий API у D нужен для ошибочного утверждения о жизненном цикле. В Q5 у всех вариантов одна рекомендация и её обоснование, в Q2 одинаково представлены закрытие ресурсов и судьба соединения. Максимальное отношение символьных длин по блокам 1.20. Пороги и правила качества не менялись.

Документация сверена, но поведение драйвера, HikariCP и Spring в этой проверке не исполнялось. Работающий сервис и БД не изменялись. Теория согласуется с проверенными моделями; отдельных исправлений Markdown не требуется. Независимая SFPL/ANSC/RU-приёмка и слепой пользовательский проход по этим хешам остаются открытыми.
