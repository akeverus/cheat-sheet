---
title: "Вопросы на собеседовании: Apache Airflow"
description: "Apache Airflow: workflow orchestration, DAG, операторы, executors (Sequential, Local, Celery, Kubernetes), XCom, sensors, hooks, scheduling, deployment в production"
tags:
  - interview
  - data-engineering
  - apache-airflow-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Apache Airflow"
  - "Apache Airflow interview"
  - "Airflow interview"
prerequisites: []
next: []
updated: "2026-05-14"
---
# Вопросы на собеседовании: `Apache Airflow`

`Apache Airflow` — самый популярный workflow orchestration в data engineering. Создан **Airbnb** (2014), Apache top-level с 2019. Workflows описываются как **DAG в Python**. Применяется для ETL, ML pipelines, scheduled jobs. Альтернативы: Prefect, Dagster, Argo Workflows, Luigi.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Apache Airflow Documentation](https://airflow.apache.org/docs/)
- [Astronomer Airflow Guides](https://www.astronomer.io/guides/)
- [Airflow vs Prefect vs Dagster](https://www.prefect.io/blog/airflow-vs-prefect-vs-dagster)
- [Best Practices for Apache Airflow](https://airflow.apache.org/docs/apache-airflow/stable/best-practices.html)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Apache Airflow?](#q1--что-такое-apache-airflow)
- [Q2. (!) Что такое DAG?](#q2--что-такое-dag)
- [Q3. Архитектура Airflow — компоненты?](#q3-архитектура-airflow--компоненты)
- [Q4. Зачем metadata DB?](#q4-зачем-metadata-db)

**DAG и tasks**
- [Q5. (!) Как написать простейший DAG?](#q5--как-написать-простейший-dag)
- [Q6. (!) Что такое operator?](#q6--что-такое-operator)
- [Q7. (!) Самые частые operators?](#q7--самые-частые-operators)
- [Q8. TaskFlow API (с Airflow 2.0+)?](#q8-taskflow-api-с-airflow-20)
- [Q9. (!) Зависимости между tasks?](#q9--зависимости-между-tasks)
- [Q10. Dynamic DAG generation?](#q10-dynamic-dag-generation)

**Scheduling**
- [Q11. (!) Как работает scheduling в Airflow?](#q11--как-работает-scheduling-в-airflow)
- [Q12. (!) schedule_interval, start_date, catchup?](#q12--schedule_interval-start_date-catchup)
- [Q13. Backfill?](#q13-backfill)
- [Q14. (!) execution_date vs logical_date?](#q14--execution_date-vs-logical_date)

**Executors**
- [Q15. (!) Какие executors бывают?](#q15--какие-executors-бывают)
- [Q16. SequentialExecutor vs LocalExecutor?](#q16-sequentialexecutor-vs-localexecutor)
- [Q17. (!) CeleryExecutor — как работает?](#q17--celeryexecutor--как-работает)
- [Q18. (!) KubernetesExecutor — почему рекомендуется?](#q18--kubernetesexecutor--почему-рекомендуется)

**Состояние и связь между tasks**
- [Q19. (!) XCom — что это?](#q19--xcom--что-это)
- [Q20. Connections и Variables?](#q20-connections-и-variables)
- [Q21. Hooks и operators?](#q21-hooks-и-operators)

**Sensors**
- [Q22. (!) Что такое sensor?](#q22--что-такое-sensor)
- [Q23. Reschedule mode для long-running sensors?](#q23-reschedule-mode-для-long-running-sensors)

**Production**
- [Q24. (!) Как deploy Airflow в production?](#q24--как-deploy-airflow-в-production)
- [Q25. (!) Сколько DAG'ов / tasks может выдержать?](#q25--сколько-dagов--tasks-может-выдержать)
- [Q26. Best practices для production DAGs?](#q26-best-practices-для-production-dags)

**Альтернативы и сравнения**
- [Q27. (!) Airflow vs Prefect vs Dagster?](#q27--airflow-vs-prefect-vs-dagster)
- [Q28. Какие минусы Airflow?](#q28-какие-минусы-airflow)

## Q1. (!) Что такое Apache Airflow?

`Apache Airflow` — **workflow orchestration platform**. Workflows описываются как **DAG (Directed Acyclic Graph)** в Python.

**Применения:**
- **ETL/ELT pipelines** — extract, transform, load
- **Data pipelines** — Spark/Flink jobs orchestration
- **ML pipelines** — training, evaluation, deployment
- **Scheduled jobs** — backups, reports, data cleanups

**Не для:**
- Real-time streaming (используй Kafka Streams, Flink)
- Long-running interactive workflows
- Job-level orchestration внутри Spark (используй Spark scheduler)


> [!mcq]
> - [ ] Airflow — это real-time streaming engine как Flink | ❌ ПОСЛЕДСТВИЕ: Airflow для batch orchestration; для streaming — Kafka Streams/Flink
> - [x] Airflow = workflow orchestration platform; DAG в Python; batch ETL/ELT pipelines, scheduled jobs, ML training | ✓ ПРИМЕНЯТЬ: координировать batch-job dependencies, retries, monitoring 📋 ПРАВИЛО: Airflow = batch orchestrator, не streaming runtime 🔗 См. Q2
> - [ ] Airflow обрабатывает данные внутри tasks | ❌ ПОСЛЕДСТВИЕ: Airflow координирует tasks, но обработку делегирует Spark/SQL/Python; XCom не для больших данных
> - [ ] Airflow подходит для интерактивных long-running workflows | ❌ ПОСЛЕДСТВИЕ: для interactive — Jupyter/Streamlit; Airflow для scheduled batch с чёткой структурой

## Q2. (!) Что такое DAG?

**DAG (Directed Acyclic Graph)** — граф **tasks** с **направленными** связями и **без циклов**.

```mermaid
graph LR
    A[extract_users] --> B[transform_users]
    A --> C[transform_orders]
    B --> D[load_to_warehouse]
    C --> D
    D --> E[send_notification]
```

В Airflow DAG = Python файл, описывающий tasks и их зависимости.

**Свойства:**
- Acyclic — нет циклов (иначе невозможно определить порядок)
- Directed — стрелки показывают порядок
- DAG идемпотентный должен быть (можно re-run)


> [!mcq]
> - [ ] DAG может содержать циклы для retry | ❌ ПОСЛЕДСТВИЕ: A = Acyclic — циклы запрещены; retry конфигурится через `retries=N`, не циклом
> - [x] DAG = Directed Acyclic Graph tasks в Python с направленными зависимостями без циклов; должен быть идемпотентным для re-runs | ✓ ПРИМЕНЯТЬ: моделирование batch pipeline с явными зависимостями 📋 ПРАВИЛО: DAG = задачи + порядок, идемпотентность обязательна 🔗 См. Q3
> - [ ] DAG = неструктурированный список tasks без зависимостей | ❌ ПОСЛЕДСТВИЕ: смысл DAG именно в направленных зависимостях для порядка выполнения
> - [ ] DAG-файл = JSON manifest, не Python | ❌ ПОСЛЕДСТВИЕ: DAG в Airflow — Python module; декларативные альтернативы — Dagster/Prefect

## Q3. Архитектура Airflow — компоненты?

```mermaid
graph TD
    UI[Web UI / API]
    Scheduler[Scheduler<br/>планирует DAG runs]
    Executor[Executor<br/>запускает tasks]
    Workers[Workers<br/>исполняют tasks]
    DB[(Metadata DB<br/>PostgreSQL/MySQL)]

    UI --> DB
    Scheduler --> DB
    Scheduler --> Executor
    Executor --> Workers
    Workers --> DB
```

- **Scheduler** — читает DAG файлы, планирует runs, отправляет tasks executor'у
- **Executor** — стратегия запуска (Sequential, Local, Celery, K8s)
- **Workers** — фактически выполняют tasks
- **Web UI** — мониторинг, ручной trigger
- **Metadata DB** — стейт DAG runs, tasks, connections, variables


> [!mcq]
> - [x] Airflow = Scheduler (планирует DAG runs) + Executor (стратегия запуска) + Workers (исполняют tasks) + Web UI (мониторинг) + Metadata DB (стейт) | ✓ ПРИМЕНЯТЬ: понимать, что Scheduler ≠ Executor; они разные компоненты 📋 ПРАВИЛО: Scheduler планирует, Executor запускает, Workers исполняют 🔗 См. Q4
> - [ ] Scheduler сам исполняет tasks без Executor | ❌ ПОСЛЕДСТВИЕ: Scheduler решает что запустить, Executor определяет КАК (Local/Celery/K8s)
> - [ ] Workers подключаются к Web UI напрямую | ❌ ПОСЛЕДСТВИЕ: Workers пишут в Metadata DB; Web UI читает из неё
> - [ ] Metadata DB не нужна — Airflow stateless | ❌ ПОСЛЕДСТВИЕ: без metadata DB нет state DAG runs, нет recovery, нет UI

## Q4. Зачем metadata DB?

Хранит:
- Описание DAG runs (started, success, failed)
- Логи (опционально, обычно отдельно)
- Connections (DB credentials)
- Variables (config values)
- XComs (inter-task data)
- Pool configurations

В production — **PostgreSQL** (рекомендуется) или MySQL. **SQLite** только для dev/local.


> [!mcq]
> - [ ] SQLite — рекомендуется для production Airflow | ❌ ПОСЛЕДСТВИЕ: SQLite single-writer и не работает с CeleryExecutor; в prod — PostgreSQL/MySQL
> - [x] Metadata DB хранит DAG runs state, connections, variables, XComs, pool config; PostgreSQL (рекомендуется) или MySQL в prod, SQLite только для dev/local | ✓ ПРИМЕНЯТЬ: backup metadata DB как critical infra, миграции через alembic 📋 ПРАВИЛО: Postgres в prod, SQLite в dev 🔗 См. Q5
> - [ ] Metadata DB хранит сами данные задач | ❌ ПОСЛЕДСТВИЕ: данные обрабатывают workers через Spark/SQL; metadata DB хранит state/metadata, не payload
> - [ ] XComs не хранятся в metadata DB | ❌ ПОСЛЕДСТВИЕ: XCom хранится в metadata DB по умолчанию (есть custom XCom backend для S3)

## Q5. (!) Как написать простейший DAG?

```python
from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'email_on_failure': True,
    'retries': 2,
    'retry_delay': timedelta(minutes=5),
}

with DAG(
    dag_id='my_etl_pipeline',
    default_args=default_args,
    description='Daily ETL',
    schedule_interval='@daily',
    start_date=datetime(2025, 1, 1),
    catchup=False,
    tags=['etl'],
) as dag:

    def extract_data():
        return "data"

    extract = PythonOperator(
        task_id='extract',
        python_callable=extract_data,
    )

    transform = BashOperator(
        task_id='transform',
        bash_command='python /opt/scripts/transform.py',
    )

    load = BashOperator(
        task_id='load',
        bash_command='psql -f /opt/scripts/load.sql',
    )

    extract >> transform >> load
```

DAG-файл должен быть в директории, которую сканирует Scheduler (обычно `dags/`).


> [!mcq]
> - [ ] DAG-файл размещают в произвольной папке | ❌ ПОСЛЕДСТВИЕ: Scheduler сканирует только настроенную dags_folder (обычно `dags/`); другие папки игнорируются
> - [x] DAG = Python с DAG(...) context manager + operators + `>>` для зависимостей; параметры default_args, schedule_interval, start_date, catchup=False; файл в `dags/` | ✓ ПРИМЕНЯТЬ: ETL pipeline daily через `@daily` 📋 ПРАВИЛО: with DAG(...) + operators + extract >> transform >> load 🔗 См. Q6
> - [ ] catchup=True — best practice для всех новых DAG | ❌ ПОСЛЕДСТВИЕ: catchup=True заполнит все backfills с start_date — может перегрузить cluster; для новых обычно False
> - [ ] retries не нужны — ошибки лучше пробрасывать наверх | ❌ ПОСЛЕДСТВИЕ: transient errors (network, rate-limit) self-heal с retries; без них pipeline хрупкий

## Q6. (!) Что такое operator?

`Operator` — **template** для конкретной операции. Один operator = один task.

```python
PythonOperator(task_id='my_task', python_callable=func)
BashOperator(task_id='my_task', bash_command='ls')
EmptyOperator(task_id='dummy')
```

При выполнении operator создаёт **task instance**.

**Типы operators:**
- **Action** — что-то делают (BashOperator, PythonOperator, EmailOperator)
- **Transfer** — переносят данные (S3ToGCSOperator, MySqlToHiveOperator)
- **Sensor** — ждут условия (FileSensor, ExternalTaskSensor)


> [!mcq]
> - [ ] Operator выполняет работу сразу при создании | ❌ ПОСЛЕДСТВИЕ: Operator = template; при выполнении создаётся task instance (TI) — runtime инстанс
> - [ ] Один operator = много tasks | ❌ ПОСЛЕДСТВИЕ: один operator = один task в DAG; для повторов используют loop в DAG-коде или dynamic task mapping
> - [x] Operator = template для конкретной операции (1 operator = 1 task); типы: Action (Python/Bash/Email), Transfer (S3ToGCS), Sensor (FileSensor); runtime создаёт task instance | ✓ ПРИМЕНЯТЬ: каждый шаг pipeline = один operator 📋 ПРАВИЛО: operator = blueprint, task instance = run 🔗 См. Q7
> - [ ] PythonOperator и Sensor взаимозаменяемы | ❌ ПОСЛЕДСТВИЕ: разные категории; Sensor ждёт условие (poke_interval), PythonOperator один раз вызывает функцию

## Q7. (!) Самые частые operators?

| Operator | Назначение |
|----------|------------|
| `PythonOperator` | Вызвать Python функцию |
| `BashOperator` | Выполнить bash команду |
| `SqlOperator` (DB-specific) | SQL query |
| `KubernetesPodOperator` | Запустить под в K8s |
| `SparkSubmitOperator` | Запустить Spark job |
| `S3ToGCSOperator` | S3 → Google Cloud Storage |
| `EmailOperator` | Отправить email |
| `HttpOperator` | HTTP request |
| `BranchPythonOperator` | Условное ветвление |
| `EmptyOperator` (dummy) | Группировка/маркер |
| `TriggerDagRunOperator` | Запустить другой DAG |
| `ExternalTaskSensor` | Дождаться task в другом DAG |

Также — **provider packages** (`apache-airflow-providers-*`) для Snowflake, BigQuery, Redshift, и т.д.


> [!mcq]
> - [x] Частые operators: PythonOperator, BashOperator, SQL operators, KubernetesPodOperator, SparkSubmitOperator, S3ToGCSOperator, BranchPythonOperator, EmptyOperator, TriggerDagRunOperator + provider packages для Snowflake/BQ | ✓ ПРИМЕНЯТЬ: выбрать operator под задачу; provider packages для cloud services 📋 ПРАВИЛО: PythonOperator/BashOperator для generic, специализированные для cloud 🔗 См. Q8
> - [ ] BashOperator и PythonOperator — устаревшие, не использовать | ❌ ПОСЛЕДСТВИЕ: оба активно используются; PythonOperator основной для custom logic
> - [ ] BranchPythonOperator не нужен — есть if/else в Python | ❌ ПОСЛЕДСТВИЕ: if/else в DAG-коде выполняется при парсинге, не runtime; для conditional execution нужен BranchPythonOperator
> - [ ] Provider packages надо устанавливать вручную всегда | ❌ ПОСЛЕДСТВИЕ: основные провайдеры (Postgres, HTTP) идут с airflow; cloud провайдеры — через pip install apache-airflow-providers-*

## Q8. TaskFlow API (с Airflow 2.0+)?

**TaskFlow API** — модернизированный синтаксис через декораторы:

```python
from airflow.decorators import dag, task
from datetime import datetime

@dag(schedule_interval='@daily', start_date=datetime(2025, 1, 1), catchup=False)
def my_pipeline():

    @task
    def extract():
        return {"users": [1, 2, 3]}

    @task
    def transform(data):
        return [u * 2 for u in data["users"]]

    @task
    def load(data):
        print(f"Loading: {data}")

    data = extract()
    transformed = transform(data)
    load(transformed)

my_pipeline()
```

**Преимущества:**
- Похоже на обычный Python код
- XCom передача автоматическая (через return values)
- Меньше boilerplate

В **новых проектах** — TaskFlow API предпочтительнее classical operators.


> [!mcq]
> - [ ] TaskFlow требует ручной передачи XCom через xcom_push/xcom_pull | ❌ ПОСЛЕДСТВИЕ: основная фишка TaskFlow — автоматическая передача через return values; xcom вызовы не нужны
> - [x] TaskFlow API (Airflow 2.0+) = декораторы @dag/@task с Python-natural синтаксисом; автоматический XCom через return values, меньше boilerplate | ✓ ПРИМЕНЯТЬ: новые DAG в 2.x+ — TaskFlow по умолчанию 📋 ПРАВИЛО: TaskFlow для новых, classical operators для legacy 🔗 См. Q9
> - [ ] TaskFlow несовместим с classical operators | ❌ ПОСЛЕДСТВИЕ: можно микшировать; TaskFlow tasks работают с PythonOperator/BashOperator в одном DAG
> - [ ] TaskFlow работает только в Airflow 1.x | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — TaskFlow появился в Airflow 2.0+, в 1.x его нет

## Q9. (!) Зависимости между tasks?

```python
# Через bitshift operators (классический)
extract >> transform >> load

# Несколько tasks
extract >> [transform_a, transform_b] >> load

# Эквивалентно
transform.set_upstream(extract)
load.set_downstream(notify)

# В TaskFlow API — через вызовы
data = extract()
result = transform(data)  # неявная зависимость
load(result)
```

`>>` — `task1 << task2` или `task1 >> task2`. Создаёт edges в DAG.


> [!mcq]
> - [x] Зависимости: `extract >> transform >> load`, `[a, b] >> c` для fan-in, `set_upstream/downstream`; в TaskFlow — через вызовы (`load(transform(extract()))`) — неявные edges | ✓ ПРИМЕНЯТЬ: bitshift для classical, function calls для TaskFlow 📋 ПРАВИЛО: >> создаёт edges в DAG 🔗 См. Q10
> - [ ] `extract >> transform` равно вызову `extract()` сразу | ❌ ПОСЛЕДСТВИЕ: `>>` только настраивает зависимость; выполнение делает scheduler/executor
> - [ ] `<<` запрещен — используйте только `>>` | ❌ ПОСЛЕДСТВИЕ: оба валидны; `a >> b` равно `b << a`
> - [ ] В TaskFlow зависимости задаются `>>` | ❌ ПОСЛЕДСТВИЕ: в TaskFlow зависимости неявные через function calls и return values

## Q10. Dynamic DAG generation?

```python
# Статически
for i in range(10):
    task = PythonOperator(
        task_id=f'task_{i}',
        python_callable=process,
        op_args=[i],
    )

# Dynamic Task Mapping (Airflow 2.3+)
@task
def process_item(item):
    return item * 2

@task
def get_items():
    return [1, 2, 3, 4, 5]

@dag(...)
def my_dag():
    items = get_items()
    process_item.expand(item=items)  # создаст N tasks динамически
```

**Dynamic Task Mapping** — для случаев когда **число tasks неизвестно** до runtime.


> [!mcq]
> - [ ] Dynamic Task Mapping существует с Airflow 1.x | ❌ ПОСЛЕДСТВИЕ: появилось в Airflow 2.3+; в 1.x только статическая генерация через loop
> - [x] Dynamic DAG generation: статически через for loop при парсинге DAG (число tasks известно) или Dynamic Task Mapping `.expand()` (число неизвестно до runtime, Airflow 2.3+) | ✓ ПРИМЕНЯТЬ: обработка списка файлов или partitions переменной длины 📋 ПРАВИЛО: .expand() для runtime-зависимой длины, for loop для known counts 🔗 См. Q11
> - [ ] Dynamic Task Mapping создаёт tasks при парсинге DAG | ❌ ПОСЛЕДСТВИЕ: tasks создаются runtime после выполнения upstream tasks; именно поэтому нужен .expand()
> - [ ] Loop в DAG генерирует tasks динамически runtime | ❌ ПОСЛЕДСТВИЕ: loop выполняется при парсинге; число tasks фиксируется при создании DAG

## Q11. (!) Как работает scheduling в Airflow?

1. Scheduler **сканирует DAG файлы** периодически
2. Для каждого DAG — определяет **next run** на основе `schedule_interval`
3. Когда время приходит → создаёт **DAG run** со статусом `running`
4. Tasks отправляются executor'у в правильном порядке (по dependencies)
5. После завершения всех tasks → DAG run = `success` или `failed`

Scheduler **не сам выполняет** tasks — только отправляет их executor'у.


> [!mcq]
> - [ ] Scheduler сам выполняет tasks | ❌ ПОСЛЕДСТВИЕ: Scheduler отправляет tasks Executor'у; tasks выполняют Workers
> - [x] Scheduler сканирует DAG-файлы → определяет next run по schedule_interval → создаёт DAG run → отправляет tasks Executor'у в порядке dependencies → собирает результаты | ✓ ПРИМЕНЯТЬ: понимание separation Scheduler vs Executor для tuning 📋 ПРАВИЛО: Scheduler планирует и оркестрирует, Workers исполняют 🔗 См. Q12
> - [ ] Scheduler парсит DAG-файлы только один раз при старте | ❌ ПОСЛЕДСТВИЕ: dag_dir_list_interval (default 5 мин) — Scheduler периодически перечитывает DAG-файлы
> - [ ] Все tasks одного DAG идут на один worker | ❌ ПОСЛЕДСТВИЕ: с CeleryExecutor/K8sExecutor каждый task может идти на разные workers

## Q12. (!) schedule_interval, start_date, catchup?

```python
DAG(
    schedule_interval='0 2 * * *',     # cron — каждый день в 2:00
    schedule_interval='@daily',         # preset
    schedule_interval=timedelta(hours=6), # каждые 6 часов
    start_date=datetime(2025, 1, 1),
    catchup=False,
)
```

**`schedule_interval`** — как часто запускать.
**`start_date`** — когда начать.
**`catchup`** — если `True`, Airflow выполнит **все пропущенные** runs от `start_date`.

С `catchup=False` — только **последний** missed run.

**Подвох:** DAG запускается **в конце интервала**, не в начале. Daily DAG со start_date `2025-01-01` запустится **2 января** для данных от 1-го.


> [!mcq]
> - [ ] DAG запускается в начале интервала | ❌ ПОСЛЕДСТВИЕ: DAG запускается в КОНЦЕ интервала; daily DAG со start_date 2025-01-01 первый run на 2025-01-02 (для данных дня 1-го)
> - [x] schedule_interval = cron/preset/timedelta; start_date = начало; catchup=True заполнит все пропущенные runs, catchup=False — только последний missed; DAG запускается в КОНЦЕ интервала | ✓ ПРИМЕНЯТЬ: catchup=False по умолчанию для новых DAG, чтобы не перегрузить cluster 📋 ПРАВИЛО: end-of-interval execution + catchup=False для прозрачности 🔗 См. Q13
> - [ ] catchup=True безопасный default | ❌ ПОСЛЕДСТВИЕ: catchup=True заполнит ВСЕ runs от start_date — может породить сотни одновременных runs
> - [ ] start_date можно ставить datetime.now() | ❌ ПОСЛЕДСТВИЕ: start_date оценивается каждый раз parse → DAG никогда не запустится; всегда фиксированная дата

## Q13. Backfill?

`Backfill` — выполнить DAG для исторических периодов.

```bash
airflow dags backfill -s 2025-01-01 -e 2025-01-31 my_dag
```

Создаёт DAG runs для всех дат в диапазоне. Полезно после изменений логики или для пересчёта старых данных.


> [!mcq]
> - [x] Backfill = `airflow dags backfill -s ... -e ...` создаёт DAG runs для исторических дат; полезно после изменения логики или пересчёта старых данных; требует идемпотентного DAG | ✓ ПРИМЕНЯТЬ: после bugfix в transform логике переcчитать прошлые runs 📋 ПРАВИЛО: backfill = re-run за период; идемпотентность критична 🔗 См. Q14
> - [ ] Backfill = catchup | ❌ ПОСЛЕДСТВИЕ: catchup автоматически заполняет с start_date; backfill — ручная команда для конкретного диапазона
> - [ ] Backfill идёт через scheduler автоматически | ❌ ПОСЛЕДСТВИЕ: backfill — отдельная CLI команда; scheduler идёт только вперёд от now()
> - [ ] Backfill работает на DAG без idempotency | ❌ ПОСЛЕДСТВИЕ: формально работает, но дубли/коррупция данных гарантированы; всегда нужна idempotency

## Q14. (!) execution_date vs logical_date?

В **старых версиях** — `execution_date` означало **начало интервала** (не время запуска).

С Airflow 2.2+ — переименовано в `logical_date` (логическая дата процессинга).

```python
@task
def my_task(**context):
    logical_date = context['logical_date']  # новое имя
    execution_date = context['execution_date']  # старое имя (deprecated)
```

**Подвох:** для daily DAG, `logical_date = 2025-01-01` означает "обработка данных за 1 января", а сам run произошёл 2 января.


> [!mcq]
> - [ ] execution_date — это реальное время запуска DAG | ❌ ПОСЛЕДСТВИЕ: это начало интервала; daily DAG с logical_date=2025-01-01 на самом деле стартовал 2 января
> - [x] execution_date (legacy) переименован в logical_date (2.2+): начало интервала, а не время запуска; daily DAG logical_date 2025-01-01 — обработка данных за 1 января, run 2 января | ✓ ПРИМЕНЯТЬ: использовать logical_date в SQL фильтрах за день обработки 📋 ПРАВИЛО: logical_date = окно данных, не wall-clock 🔗 См. Q15
> - [ ] logical_date всегда совпадает с datetime.now() | ❌ ПОСЛЕДСТВИЕ: при backfill отличается; даже в обычных runs — logical_date в прошлом относительно фактического старта
> - [ ] execution_date теперь полностью удалён | ❌ ПОСЛЕДСТВИЕ: deprecated alias, но всё ещё доступен в context для обратной совместимости

## Q15. (!) Какие executors бывают?

| Executor | Описание | Use case |
|----------|----------|----------|
| **SequentialExecutor** | Один task за раз | Local debug |
| **LocalExecutor** | Параллельно на одной машине | Маленькие installations |
| **CeleryExecutor** | Distributed через Celery + Redis/RabbitMQ | Mid-scale |
| **KubernetesExecutor** | Каждый task — pod в K8s | Cloud-native |
| **CeleryKubernetesExecutor** | Hybrid | Mixed workloads |
| **DaskExecutor** | Dask cluster (deprecated) | — |


> [!mcq]
> - [x] Executors: Sequential (1 task, local debug), Local (multiprocessing one machine), Celery (distributed Redis/RabbitMQ), Kubernetes (pod per task, cloud-native), CeleryKubernetes (hybrid) | ✓ ПРИМЕНЯТЬ: K8sExecutor для cloud production, LocalExecutor для small installs 📋 ПРАВИЛО: dev=Sequential, small=Local, prod=Celery/K8s 🔗 См. Q16
> - [ ] CeleryExecutor работает без external broker | ❌ ПОСЛЕДСТВИЕ: Celery требует Redis или RabbitMQ для очередей tasks
> - [ ] SequentialExecutor подходит для prod | ❌ ПОСЛЕДСТВИЕ: 1 task за раз → нет parallelism; только для dev/debug
> - [ ] KubernetesExecutor требует postоянных workers | ❌ ПОСЛЕДСТВИЕ: K8sExecutor создаёт pod per task; не нужны long-running workers (главное преимущество)

## Q16. SequentialExecutor vs LocalExecutor?

**SequentialExecutor** — один task в один момент. Используется только с **SQLite** (для dev). Production не подходит.

**LocalExecutor** — параллельные tasks через **multiprocessing** на одной машине.

```python
# airflow.cfg
executor = LocalExecutor
sql_alchemy_conn = postgresql+psycopg2://...
```

LocalExecutor хорош для **маленьких installations** (~100 DAGs). Не масштабируется горизонтально.


> [!mcq]
> - [ ] LocalExecutor работает только с SQLite | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — LocalExecutor требует Postgres/MySQL; SQLite работает только с Sequential
> - [x] SequentialExecutor = 1 task, SQLite only, dev/debug; LocalExecutor = parallel multiprocessing на одной машине, требует Postgres/MySQL, до ~100 DAG | ✓ ПРИМЕНЯТЬ: Local для small standalone install без distributed compute 📋 ПРАВИЛО: Sequential+SQLite=dev; Local+Postgres=small prod 🔗 См. Q17
> - [ ] LocalExecutor масштабируется горизонтально | ❌ ПОСЛЕДСТВИЕ: только vertical (CPU cores на одной машине); для horizontal — Celery/K8s
> - [ ] Sequential нормально для production | ❌ ПОСЛЕДСТВИЕ: 1 task за раз = bottleneck; production требует параллелизма

## Q17. (!) CeleryExecutor — как работает?

`CeleryExecutor` использует **Celery** + message broker (RabbitMQ или Redis) для distribution.

```mermaid
graph LR
    Scheduler -->|enqueue task| Broker[Redis / RabbitMQ]
    Broker --> Worker1[Celery Worker 1]
    Broker --> Worker2[Celery Worker 2]
    Broker --> Worker3[Celery Worker 3]
```

**Workers** — отдельные процессы (на разных машинах), которые забирают tasks из брокера.

**Scaling:** добавляешь больше workers.

```bash
airflow celery worker
```

**Минусы:**
- Сложнее operations (broker нужен)
- Worker процессы — long-running (тяжелее update библиотек)


> [!mcq]
> - [x] CeleryExecutor = scheduler enqueue tasks в broker (Redis/RabbitMQ), Celery workers (long-running на разных машинах) забирают и выполняют; horizontal scaling через `airflow celery worker` | ✓ ПРИМЕНЯТЬ: distributed Airflow на VM кластерах, mid-scale prod 📋 ПРАВИЛО: Celery = workers + broker для distribution 🔗 См. Q18
> - [ ] CeleryExecutor не нужен broker | ❌ ПОСЛЕДСТВИЕ: вся идея в распределении через message broker; Redis/RabbitMQ обязателен
> - [ ] Celery workers — ephemeral, создаются на каждый task | ❌ ПОСЛЕДСТВИЕ: это K8sExecutor; Celery workers — long-running процессы
> - [ ] CeleryExecutor проще в operations чем K8s | ❌ ПОСЛЕДСТВИЕ: broker нужен в инфре, worker update сложнее; K8s часто проще в cloud-native окружении

## Q18. (!) KubernetesExecutor — почему рекомендуется?

`KubernetesExecutor` запускает **каждый task** как **отдельный pod** в K8s.

**Преимущества:**
- **Полная изоляция** между tasks (разные dependencies, resource limits)
- **Auto-scaling** через K8s
- Нет постоянных workers (платим только за running tasks)
- Cloud-native deployment

**Недостатки:**
- Pod startup overhead (~5-30 sec на task)
- Не подходит для **очень коротких** tasks (overhead > useful work)

В **2024** KubernetesExecutor — рекомендуемый выбор для production cloud deployments.


> [!mcq]
> - [ ] K8sExecutor хорош для очень коротких tasks (1 сек) | ❌ ПОСЛЕДСТВИЕ: pod startup overhead 5-30 сек → overhead > work; для коротких лучше Celery
> - [x] KubernetesExecutor = каждый task — отдельный pod в K8s: полная изоляция dependencies/resources, auto-scaling, нет idle workers, cloud-native; pod startup overhead 5-30s | ✓ ПРИМЕНЯТЬ: cloud production с K8s; long-running tasks (> minute) 📋 ПРАВИЛО: pod-per-task = isolation + no idle cost 🔗 См. Q19
> - [ ] K8sExecutor требует постоянных workers как Celery | ❌ ПОСЛЕДСТВИЕ: главное преимущество — ephemeral pods, не нужны long-running workers
> - [ ] K8sExecutor не поддерживает custom Docker images per task | ❌ ПОСЛЕДСТВИЕ: можно задать pod_template_file или KubernetesPodOperator с разными images — гибкость

## Q19. (!) XCom — что это?

**XCom (Cross-Communication)** — механизм обмена данными между tasks.

```python
@task
def push():
    return {"key": "value"}  # автоматически push в XCom

@task
def pull(data):  # автоматически pull
    print(data)

push() >> pull()

# Или explicitly
def push_xcom(**context):
    context['ti'].xcom_push(key='my_key', value='my_value')

def pull_xcom(**context):
    value = context['ti'].xcom_pull(key='my_key', task_ids='push')
```

**Подвох:** XCom хранятся в **metadata DB** → не для **больших данных**. Только мета-информация (paths, IDs, статусы).

Для больших данных — пиши в S3/HDFS, передавай **path** через XCom.


> [!mcq]
> - [ ] XCom подходит для передачи больших dataframes между tasks | ❌ ПОСЛЕДСТВИЕ: XCom в metadata DB; большие данные положат БД и снизят performance scheduler
> - [x] XCom = механизм обмена meta-данными между tasks; хранится в metadata DB → только paths/IDs/статусы, не payload; для больших данных — пиши в S3, передавай path через XCom | ✓ ПРИМЕНЯТЬ: передать file path после extract в transform 📋 ПРАВИЛО: XCom для metadata; payload в S3/HDFS 🔗 См. Q20
> - [ ] XCom автоматически persists на S3 | ❌ ПОСЛЕДСТВИЕ: по умолчанию в metadata DB; custom XCom Backend нужно настраивать
> - [ ] TaskFlow не использует XCom | ❌ ПОСЛЕДСТВИЕ: TaskFlow использует XCom за кулисами — return value автоматически push, аргумент функции автоматически pull

## Q20. Connections и Variables?

**Connections** — credentials для внешних систем:

```python
from airflow.hooks.postgres_hook import PostgresHook
hook = PostgresHook(postgres_conn_id='my_postgres')
df = hook.get_pandas_df("SELECT * FROM users")
```

Создаются через UI или CLI. Хранятся в metadata DB (encrypted).

**Variables** — конфигурация:

```python
from airflow.models import Variable
api_key = Variable.get("my_api_key")
config = Variable.get("config", deserialize_json=True)
```

**Best practice:** не хранить секреты в Variables. Использовать **Secrets Backends** (Vault, AWS Secrets Manager).


> [!mcq]
> - [ ] Хранить API keys в Variables — best practice | ❌ ПОСЛЕДСТВИЕ: Variables хранятся в metadata DB и видны в UI; для секретов — Secrets Backend (Vault/Secrets Manager)
> - [x] Connections = credentials для DBs/APIs (encrypted в metadata DB), Variables = config; секреты выносить в Secrets Backend (Vault, AWS Secrets Manager), не в Variables | ✓ ПРИМЕНЯТЬ: postgres_conn_id для DB connections, Variable.get для config 📋 ПРАВИЛО: Connections=creds, Variables=config, Secrets Backend=secrets 🔗 См. Q21
> - [ ] Variables хранят только строки | ❌ ПОСЛЕДСТВИЕ: можно хранить JSON; `Variable.get("config", deserialize_json=True)`
> - [ ] Connections создаются только из Python кода | ❌ ПОСЛЕДСТВИЕ: можно через UI, CLI (`airflow connections add`) или environment variables

## Q21. Hooks и operators?

**Hook** — низкоуровневый wrapper над external system (DB, API, S3).

**Operator** — high-level task, обычно использует hook внутри.

```python
# Hook
hook = PostgresHook(postgres_conn_id='my_postgres')
records = hook.get_records("SELECT * FROM users")

# Operator (использует Hook)
task = PostgresOperator(
    task_id='insert',
    postgres_conn_id='my_postgres',
    sql='INSERT INTO users (name) VALUES (\'Alice\');',
)
```

Hooks — для custom Python tasks. Operators — для declarative workflows.


> [!mcq]
>
> **Вопрос:** В чём разница между Hook и Operator в Airflow, и когда что использовать?
>
> ---
>
> #### A) Hook = low-level wrapper над external system (DB/API/S3) с инкапсулированным connection; Operator = high-level task в DAG, который обычно использует Hook внутри; Hook для custom Python tasks (PythonOperator), Operator для declarative workflows — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Архитектурно Airflow разделяет **транспортный слой** (Hook) и **слой описания pipeline** (Operator). Hook знает как подключиться к Postgres/S3/HTTP, как выполнить базовые операции (`get_records`, `get_pandas_df`, `load_file`). Operator оборачивает Hook в task с `task_id`, retry policy, dependencies.
>
> Когда писать Hook напрямую vs Operator:
> - **Hook внутри PythonOperator/@task** — когда нужна custom логика, multi-step interaction, обработка результата на Python (например, fetch + transform + decide).
> - **Operator (PostgresOperator/S3CreateObjectOperator)** — когда задача атомарная и описывается одним declarative параметром (SQL string, file path, payload).
>
> **Пример:**
> ```python
> from airflow.decorators import task
> from airflow.providers.postgres.hooks.postgres import PostgresHook
> from airflow.providers.postgres.operators.postgres import PostgresOperator
>
> # Hook — нужна логика: fetch + transform + branch
> @task
> def process_users():
>     hook = PostgresHook(postgres_conn_id='analytics_db')
>     df = hook.get_pandas_df("SELECT id, status FROM users WHERE active = true")
>     active = df[df['status'] == 'PREMIUM']
>     if len(active) > 1000:
>         hook.run("INSERT INTO alerts (msg) VALUES ('high premium load')")
>     return active.to_dict()
>
> # Operator — атомарная задача, declarative SQL
> create_summary = PostgresOperator(
>     task_id='create_daily_summary',
>     postgres_conn_id='analytics_db',
>     sql="""
>         INSERT INTO daily_summary (date, total_orders)
>         SELECT CURRENT_DATE, COUNT(*) FROM orders WHERE date = CURRENT_DATE;
>     """,
> )
> ```
>
> **Когда применять:**
> - **Hook напрямую** — в Avito аналитических pipelines, где после SELECT нужны Python-вычисления (pandas, ML feature engineering).
> - **Operator** — в Detmir/Lamoda ETL, где шаги атомарны: "выполни этот SQL", "скопируй файл S3 → S3", "вызови этот HTTP endpoint".
> - **KubernetesPodOperator** — в Yandex.Cloud projects, где task = запуск контейнера со своими dependencies.
>
> **Подводные камни:**
> - **Connection pooling**: Hook создаёт новое соединение на каждый вызов — если в одном task много `hook.run()`, лучше переиспользовать engine через `hook.get_sqlalchemy_engine()`.
> - **Credentials в logs**: Hook автоматически маскирует `*** ***`, но если ты пишешь `print(hook.get_connection().password)` — пароль попадёт в task log.
> - **Lazy connection в Operator**: Operator открывает соединение в `execute()`, не в `__init__()` — поэтому DAG parse не упадёт при недоступной БД.
> - **Custom Hooks**: для legacy систем без provider package можно унаследоваться от `BaseHook` и реализовать `get_conn()`.
>
> **Связанные вопросы:** [[Q6]] — что такое Operator; [[Q20]] — Connections и Variables; [[Q22]] — Sensor как специальный тип Operator.
>
> ---
>
> #### B) Hook — это event-listener для airflow lifecycle (как Django signals) — ❌ Неверно
>
> **Что на самом деле:** Hook в Airflow — это **connection wrapper**, не lifecycle callback. Для lifecycle событий используют `on_success_callback`, `on_failure_callback`, `on_retry_callback` в `default_args` или operators.
>
> **Откуда путаница:** слово "hook" в других фреймворках (React, Git, WordPress) означает event-callback. В Airflow семантика другая — это database/API connection helper.
>
> **Если бы это было правдой:** можно было бы написать `@hook(on_failure)` для глобального обработчика всех ошибок. На практике для этого используют `default_args = {'on_failure_callback': ...}` или Sentry integration.
>
> ---
>
> #### C) Hook и Operator — синонимы; можно использовать любой термин взаимозаменяемо — ❌ Неверно
>
> **Что на самом деле:** это **два разных уровня абстракции**. Hook — низкоуровневый wrapper над external system (DB, API, S3), который инкапсулирует подключение и базовые операции. Operator — высокоуровневый task в DAG, который обычно использует Hook внутри для declarative описания шага pipeline.
>
> **Откуда путаница:** в документации часто оба упоминаются вместе, и `PostgresOperator` действительно использует `PostgresHook` внутри. Но они не взаимозаменяемы в коде: Hook — это Python-объект, Operator — task в DAG.
>
> **Если бы это было правдой:** мы могли бы использовать `PostgresHook(task_id='x')` напрямую в DAG. На практике это бросит exception, потому что Hook не наследует `BaseOperator`.
>
> ---
>
> #### D) Operator всегда быстрее Hook потому что использует C-расширения — ❌ Неверно
>
> **Что на самом деле:** Operator и Hook — обычные Python-классы, никаких C-расширений. Performance в Airflow определяется работой external system (БД, API) и executor overhead, а не выбором Hook vs Operator. Operator может быть даже медленнее, потому что добавляет task instance overhead в metadata DB.
>
> **Откуда путаница:** иногда думают, что "официальные" Operators оптимизированы. На практике большинство Operators — тонкие обёртки над Hooks с дополнительной логикой логирования и templating.
>
> **Если бы это было правдой:** все ETL писали бы только через Operators, избегая custom Python. На практике аналитические pipelines часто используют PythonOperator + Hook именно ради гибкости.

## Q22. (!) Что такое sensor?

**Sensor** — task, который **ждёт** условия (file exists, table updated, etc).

```python
from airflow.sensors.filesystem import FileSensor

wait_for_file = FileSensor(
    task_id='wait_file',
    filepath='/data/input.csv',
    poke_interval=60,    # проверять каждую минуту
    timeout=3600,         # максимум 1 час
)
```

**Common sensors:**
- `FileSensor`, `S3KeySensor` — wait for file
- `ExternalTaskSensor` — wait for task in another DAG
- `SqlSensor` — wait for SQL condition
- `HttpSensor` — wait for HTTP response


> [!mcq]
>
> **Вопрос:** Что такое Sensor в Airflow и чем он отличается от обычного Operator?
>
> ---
>
> #### A) Sensor — это специальный тип Operator, который периодически проверяет условие (`poke()`) и завершается успехом только когда условие истинно; используется для ожидания внешних событий (файл, таблица, другой DAG) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Sensor наследуется от `BaseSensorOperator`, который наследует `BaseOperator`. Ключевое отличие — метод `poke(context) -> bool`: вместо однократного выполнения, sensor вызывает `poke` каждые `poke_interval` секунд, пока тот не вернёт `True` или не сработает `timeout`.
>
> Sensor нужен когда pipeline зависит от **внешнего события**, которое произойдёт в неизвестный момент: появление файла в S3, окончание ETL в другой команде, обновление dimension-таблицы. Без sensor пришлось бы либо опрашивать вручную в PythonOperator (теряя retry/timeout инфраструктуру), либо ставить `time.sleep()` (блокируя worker без контроля).
>
> **Пример:**
> ```python
> from airflow.sensors.filesystem import FileSensor
> from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
> from airflow.sensors.external_task import ExternalTaskSensor
> from airflow.providers.common.sql.sensors.sql import SqlSensor
>
> # Ждать появление файла локально
> wait_input = FileSensor(
>     task_id='wait_input_csv',
>     filepath='/data/incoming/orders_{{ ds }}.csv',
>     poke_interval=60,
>     timeout=3600,
>     mode='reschedule',  # освобождать worker между проверками
> )
>
> # Ждать ключ в S3
> wait_s3 = S3KeySensor(
>     task_id='wait_s3_drop',
>     bucket_name='analytics-raw',
>     bucket_key='events/dt={{ ds }}/_SUCCESS',
>     aws_conn_id='aws_default',
>     poke_interval=300,
>     timeout=6 * 3600,
> )
>
> # Ждать окончание задачи в другом DAG
> wait_upstream = ExternalTaskSensor(
>     task_id='wait_dim_refresh',
>     external_dag_id='dimensions_refresh',
>     external_task_id='dim_users_loaded',
>     execution_delta=None,  # та же logical_date
>     timeout=2 * 3600,
> )
>
> wait_input >> wait_s3 >> wait_upstream >> process_data
> ```
>
> **Когда применять:**
> - **Cross-team dependencies** в Detmir — ETL команды каталога ждёт окончания ETL заказов через `ExternalTaskSensor`.
> - **Late-arriving data** в Lamoda — events DAG ждёт `_SUCCESS` marker в S3 через `S3KeySensor`.
> - **Conditional refresh** в Avito — analytical DAG ждёт пока `SqlSensor` подтвердит наличие строк за нужный день.
>
> **Подводные камни:**
> - **Worker slot exhaustion**: в `mode='poke'` (default) sensor занимает worker всё время ожидания. Десяток sensors с `timeout=24h` могут парализовать кластер — нужен `mode='reschedule'`.
> - **Timeout < poke_interval**: если поставить `poke_interval=300, timeout=60` — sensor упадёт ещё до первой проверки.
> - **Soft-fail vs fail**: `soft_fail=True` переводит sensor в `skipped` вместо `failed` по timeout — полезно для optional dependencies.
> - **Deferrable sensors (Airflow 2.2+)**: `S3KeySensorAsync`, `DateTimeSensorAsync` — используют Triggerer вместо worker slot, ещё эффективнее reschedule.
>
> **Связанные вопросы:** [[Q21]] — Hooks vs Operators (Sensor — частный случай Operator); [[Q23]] — reschedule vs poke mode; [[Q24]] — deploy с deferrable sensors.
>
> ---
>
> #### B) Sensor — это middleware между Scheduler и Executor, который проверяет состояние воркеров — ❌ Неверно
>
> **Что на самом деле:** Sensor — это **task в DAG**, который выполняется как обычный Operator на worker. К состоянию воркеров sensor отношения не имеет — для этого есть Scheduler health checks и Celery flower/Kubernetes metrics.
>
> **Откуда путаница:** в других системах (Prometheus, Kubernetes) "sensor" иногда означает infrastructure-level health probe. В Airflow семантика прикладная — sensor для бизнес-условий.
>
> **Если бы это было правдой:** для добавления нового sensor пришлось бы конфигурировать сам Airflow cluster. На практике sensor — это просто `from airflow.sensors.filesystem import FileSensor` в DAG-коде.
>
> ---
>
> #### C) Sensor — это аналог cron-job, который запускает DAG по расписанию — ❌ Неверно
>
> **Что на самом деле:** запуск DAG по расписанию — это работа **Scheduler** через параметр `schedule_interval` (или `schedule` в 2.4+). Sensor же ждёт условие **внутри** DAG-run, после того как Scheduler уже запустил DAG.
>
> **Откуда путаница:** оба механизма связаны с временем и ожиданием. Но Scheduler — про "когда стартовать DAG", а Sensor — про "когда продолжить task внутри уже запущенного DAG".
>
> **Если бы это было правдой:** мы бы писали `Sensor(cron='0 2 * * *')` для запуска DAG. На практике это делается через `DAG(schedule_interval='0 2 * * *')`.
>
> ---
>
> #### D) Sensor нельзя использовать вместе с TaskFlow API — он только для classical operators — ❌ Неверно
>
> **Что на самом деле:** Sensor прекрасно сочетается с TaskFlow. Sensor создаётся как обычный operator, и его `>>` или результат можно использовать в DAG, в котором остальные task написаны через `@task`.
>
> **Откуда путаница:** TaskFlow внешне выглядит как pure Python функции, и кажется что sensor "из другого мира". Но `@task.sensor` декоратор появился в Airflow 2.5+ — теперь даже sensor можно писать в TaskFlow стиле.
>
> **Если бы это было правдой:** проекты на TaskFlow вынуждены были бы дублировать функциональность FileSensor/S3KeySensor через PythonOperator+sleep. На практике все sensor-классы работают в TaskFlow DAG без изменений.

## Q23. Reschedule mode для long-running sensors?

```python
sensor = FileSensor(
    task_id='wait',
    mode='reschedule',  # вместо 'poke' (default)
    poke_interval=300,
)
```

| Mode | Поведение |
|------|-----------|
| `poke` (default) | Worker слот занят всё время ожидания |
| `reschedule` | Worker слот освобождается между проверками |

**Reschedule** для долгих ожиданий (часы/дни) — иначе воркеры застрянут на сенсорах.


> [!mcq]
>
> **Вопрос:** Чем `mode='reschedule'` отличается от `mode='poke'` (default) для sensor, и когда что выбирать?
>
> ---
>
> #### A) `reschedule` запускает sensor на другом worker, `poke` — на том же; разница только в location — ❌ Неверно
>
> **Что на самом деле:** в **обоих** режимах sensor может попасть на любой worker (это решает executor, не mode). Разница — в **поведении между проверками**: в `poke` worker удерживает task slot, в `reschedule` task завершается со статусом `up_for_reschedule` и через `poke_interval` секунд переотправляется в очередь executor.
>
> **Откуда путаница:** оба термина звучат как "перенаправление", и слово "reschedule" напоминает routing. На деле reschedule = "освободить slot и переподнять task позже", не "переслать на другой узел".
>
> **Если бы это было правдой:** разница была бы только в network topology, но проблема worker exhaustion никуда бы не делась. На практике именно освобождение slot — главное преимущество reschedule.
>
> ---
>
> #### B) `reschedule` работает быстрее потому что не делает реальных проверок — ❌ Неверно
>
> **Что на самом деле:** `reschedule` делает **те же самые проверки** через `poke()` — semantically identical. Просто между проверками task убирается со slot. По latency `reschedule` чуть медленнее: каждая проверка проходит через scheduler queue (~5-30 sec overhead).
>
> **Откуда путаница:** "освободить worker" интуитивно ассоциируется с "быстрее". На практике reschedule оптимизирует **throughput** кластера, а не latency одного sensor.
>
> **Если бы это было правдой:** reschedule был бы default режимом для всех sensors. На самом деле default — `poke`, потому что для коротких ожиданий он быстрее и надёжнее.
>
> ---
>
> #### C) `poke` (default) удерживает worker slot всё время ожидания между проверками; `reschedule` освобождает slot между проверками — task завершается со статусом `up_for_reschedule` и через `poke_interval` снова берётся executor'ом; `reschedule` обязателен для long-running sensors (часы/дни), иначе worker pool забьётся — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> В режиме `poke` task попадает на worker и выполняет цикл `while not poke(): sleep(poke_interval)`. Slot остаётся занят весь timeout. Если у тебя CeleryExecutor с 50 worker slots и 60 файловых sensors с `timeout=24h`, кластер встанет — все слоты будут «спать».
>
> В режиме `reschedule` sensor устроен иначе: вызывает `poke()` один раз; если `False`, кидает `AirflowRescheduleException` со временем следующей попытки (`now + poke_interval`). Task получает статус `up_for_reschedule`, slot освобождается. Scheduler по cron-у переподнимает task в очередь executor.
>
> Trade-off:
> - `poke` — быстрый цикл проверок (`poke_interval=5s` нормально), низкая latency, но слот занят.
> - `reschedule` — overhead на reschedule (минимум 5-30 sec на cycle), но slot свободен для других tasks.
>
> **Пример:**
> ```python
> from airflow.sensors.filesystem import FileSensor
> from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
>
> # КОРОТКОЕ ожидание (минуты) — poke
> wait_marker = FileSensor(
>     task_id='wait_marker',
>     filepath='/data/_READY',
>     poke_interval=10,
>     timeout=300,        # 5 минут максимум
>     mode='poke',
> )
>
> # ДОЛГОЕ ожидание (часы) — reschedule
> wait_s3 = S3KeySensor(
>     task_id='wait_late_data',
>     bucket_name='analytics-raw',
>     bucket_key='events/dt={{ ds }}/_SUCCESS',
>     poke_interval=600,   # каждые 10 минут
>     timeout=24 * 3600,   # до суток
>     mode='reschedule',   # КРИТИЧНО: иначе сожрёт slot на день
> )
>
> # Ещё лучше для AWS — async deferrable sensor (Airflow 2.2+)
> wait_s3_async = S3KeySensor(
>     task_id='wait_s3_async',
>     bucket_name='analytics-raw',
>     bucket_key='events/dt={{ ds }}/_SUCCESS',
>     poke_interval=600,
>     timeout=24 * 3600,
>     deferrable=True,     # уходит в Triggerer, не занимает worker slot вовсе
> )
> ```
>
> **Когда применять:**
> - **`poke`** — короткие ожидания (< 10 минут), быстрые опросы (< 30 сек), когда worker slots в избытке.
> - **`reschedule`** — ожидания > 30 минут, особенно cross-DAG `ExternalTaskSensor`, файловые sensor с late-arriving data.
> - **`deferrable=True`** — production cluster с десятками sensors; экономит слоты ещё сильнее, потому что Triggerer работает асинхронно через asyncio.
>
> **Подводные камни:**
> - **`poke_interval >= 5 min` для reschedule**: слишком частый reschedule даёт большой накладной overhead. Если нужны частые проверки — лучше `poke`.
> - **Idempotency `poke()`**: при reschedule между вызовами state теряется, поэтому `poke()` должен быть чистой функцией без локальных переменных.
> - **Triggerer process** для deferrable: нужен отдельный `airflow triggerer` процесс — не запустится из коробки, надо добавлять в deploy.
> - **External task sensor**: `mode='reschedule'` особенно важен, потому что обычно ждём до конца дня — без reschedule парализует кластер.
>
> **Связанные вопросы:** [[Q22]] — что такое sensor; [[Q25]] — масштабирование DAG count (sensors часто bottleneck); [[Q26]] — best practice mode reschedule по умолчанию.
>
> ---
>
> #### D) `reschedule` — это retry mode для упавших sensor, аналог `retries=N` — ❌ Неверно
>
> **Что на самом деле:** `retries` срабатывает при **failure** (exception), `reschedule` — при **успешном poke=False** (ожидание ещё не завершено). Это разные механизмы: retries — обработка ошибок, reschedule — стратегия ожидания. Sensor можно настроить с обоими параметрами одновременно: `retries=3, mode='reschedule'`.
>
> **Откуда путаница:** оба параметра приводят к "повторному выполнению" task, и можно решить, что они дублируют друг друга. Различие: retry перезапускает с нуля (timeout считается заново), reschedule продолжает существующий sensor (timeout идёт с первой попытки).
>
> **Если бы это было правдой:** мы бы выбирали между `retries=10` и `mode='reschedule'`. На практике используются вместе: retries — на случай transient errors (network), reschedule — на случай долгого ожидания условия.

## Q24. (!) Как deploy Airflow в production?

**Опции:**

1. **Astronomer** (managed Airflow) — самый популярный SaaS
2. **MWAA** (Amazon Managed Workflows for Apache Airflow)
3. **Cloud Composer** (Google managed)
4. **Self-hosted на K8s** через [official Helm chart](https://airflow.apache.org/docs/helm-chart/stable/)

**Рекомендуемая архитектура:**
- KubernetesExecutor
- PostgreSQL для metadata
- S3/GCS для логов
- Secrets backend (Vault, AWS Secrets Manager)
- Sentry / DataDog для мониторинга

```bash
helm install airflow apache-airflow/airflow
```


> [!mcq]
>
> **Вопрос:** Какая архитектура deploy Airflow в production считается рекомендуемой в 2025?
>
> ---
>
> #### A) Один `docker run apache/airflow` на одной VM с SQLite — стандартный production setup — ❌ Неверно
>
> **Что на самом деле:** это **dev/local** setup. SQLite не поддерживает concurrent writes, поэтому SequentialExecutor — единственный возможный (1 task за раз). Для production нужны: Postgres/MySQL как metadata DB, distributed executor (Celery/Kubernetes), отдельные процессы Scheduler/Webserver/Workers, persistent storage для логов.
>
> **Откуда путаница:** `docker run apache/airflow standalone` действительно работает «из коробки» и в документации часто показан как quickstart. Но это explicitly помечено как not-for-production.
>
> **Если бы это было правдой:** mid-size компании запускали бы Airflow на одной t3.medium VM. На практике даже маленький Airflow требует 3-4 контейнера и Postgres.
>
> ---
>
> #### B) Production deploy: managed (Astronomer / MWAA / Cloud Composer) или self-hosted на Kubernetes через official Helm chart; рекомендуемая архитектура — KubernetesExecutor + PostgreSQL для metadata + S3/GCS для логов + Secrets Backend (Vault/AWS SM) + Sentry/DataDog для мониторинга — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Production Airflow — это **набор сервисов**, не одно приложение. Минимальный набор: Scheduler, Webserver, Executor с Workers, Triggerer (для deferrable tasks с 2.2+), Metadata DB. Каждый — отдельный процесс/pod с health-checks и horizontal scaling.
>
> Выбор между managed и self-hosted:
> - **Managed (Astronomer/MWAA/Composer)** — operational overhead минимален, но vendor lock-in и стоимость. Подходит для команд без dedicated platform engineering.
> - **Self-hosted Helm на K8s** — полный контроль, легче кастомизация, нужна команда DevOps. Подходит для enterprise с established K8s practice.
>
> Критические компоненты production setup:
> - **PostgreSQL 13+** для metadata DB (с регулярным VACUUM и индексами).
> - **KubernetesExecutor** для compute (pod-per-task, изоляция dependencies).
> - **Remote logging** на S3/GCS/Azure Blob — иначе логи теряются при ephemeral pod restart.
> - **Secrets Backend** (Vault, AWS Secrets Manager, GCP Secret Manager) — не хранить creds в Variables.
> - **Monitoring**: Sentry для exceptions, DataDog/Prometheus + Grafana для metrics (task duration, scheduler lag).
> - **DAG-as-code through git-sync sidecar** — DAG файлы синхронизируются из git repo автоматически.
>
> **Пример:**
> ```yaml
> # values.yaml для official Helm chart
> executor: "KubernetesExecutor"
>
> postgresql:
>   enabled: false  # внешний managed Postgres
> data:
>   metadataConnection:
>     user: airflow
>     pass: ""  # из secret
>     host: airflow-pg.cluster-xyz.eu-central-1.rds.amazonaws.com
>     port: 5432
>     db: airflow_metadata
>
> logs:
>   persistence:
>     enabled: false
> config:
>   logging:
>     remote_logging: "True"
>     remote_base_log_folder: "s3://airflow-prod-logs/"
>     remote_log_conn_id: "aws_default"
>
> secretsBackend:
>   secrets:
>     backend: "airflow.providers.hashicorp.secrets.vault.VaultBackend"
>     backend_kwargs:
>       url: "https://vault.internal:8200"
>       connections_path: "airflow/connections"
>       variables_path: "airflow/variables"
>
> dags:
>   gitSync:
>     enabled: true
>     repo: "git@github.com:company/airflow-dags.git"
>     branch: "main"
>     subPath: "dags"
>     wait: 60
>
> webserver:
>   replicas: 2
> scheduler:
>   replicas: 2   # HA с Airflow 2.0+
> triggerer:
>   replicas: 2
> ```
>
> ```bash
> helm repo add apache-airflow https://airflow.apache.org
> helm upgrade --install airflow apache-airflow/airflow \
>   --namespace airflow --create-namespace \
>   -f values.yaml
> ```
>
> **Когда применять:**
> - **Astronomer Cloud** — startups и SMB, где Airflow — не core competency.
> - **MWAA** — AWS-only стек, нужна интеграция с IAM/VPC/Secrets Manager.
> - **Helm на K8s** — enterprise с своими K8s, банки, telco, Tinkoff/Avito-уровень.
> - **Cloud Composer** — GCP-only стек, особенно для BigQuery-centric pipelines.
>
> **Подводные камни:**
> - **Single scheduler bottleneck до 2.0**: HA scheduler работает только с 2.0+; для старых версий — single point of failure.
> - **DAG parsing cost**: scheduler перечитывает DAG-файлы каждые `min_file_process_interval` (default 30 сек) — на 1000+ DAG нужно тюнить `parsing_processes`, `max_threads`.
> - **Webserver сессии**: при scale webserver replicas нужен Redis для shared session store, иначе пользователи теряют login.
> - **Helm chart vs custom**: official Helm — хорош для start, но крупные команды часто пишут свой operator (`airflow-on-k8s-operator`) для GitOps workflow.
>
> **Связанные вопросы:** [[Q15]] — выбор executor (Kubernetes для prod); [[Q23]] — reschedule mode для sensors в prod; [[Q25]] — scaling DAG count.
>
> ---
>
> #### C) Production Airflow обязательно требует Astronomer SaaS — самостоятельный deploy невозможен — ❌ Неверно
>
> **Что на самом деле:** Astronomer — самый популярный **managed** Airflow, но не единственный путь. Self-hosted на Kubernetes через [official Helm chart](https://airflow.apache.org/docs/helm-chart/stable/) — полноценный production-grade вариант, используемый многими компаниями (Avito, Tinkoff). Также есть MWAA (AWS) и Cloud Composer (GCP) как managed альтернативы.
>
> **Откуда путаница:** Astronomer активно маркетирует свой продукт. На деле выбор между self-hosted и managed — про operational overhead, не про техническую возможность.
>
> **Если бы это было правдой:** каждая компания была бы вынуждена платить Astronomer. На практике большинство enterprise (банки, telco) запускают Airflow в своём K8s через Helm.
>
> ---
>
> #### D) Production Airflow надо deploy через `docker-compose up` с одним контейнером всех компонентов — ❌ Неверно
>
> **Что на самом деле:** `docker-compose` подход (даже в multi-container варианте) — это dev окружение. Production требует health-checks, auto-restart, horizontal scaling, secret management, persistent storage — всё это решается K8s или managed сервисом. `docker-compose` не даёт rolling updates, нет встроенного service discovery, нет network policies.
>
> **Откуда путаница:** в `docs/docker/docker-compose.yaml` Airflow есть готовый файл, который выглядит "production-ready". На деле он подходит для local dev и POC, не для prod.
>
> **Если бы это было правдой:** все production deploy выглядели бы как `docker-compose up -d`. На практике это рабочий dev-setup, но через 1-2 месяца команды мигрируют на K8s.

## Q25. (!) Сколько DAG'ов / tasks может выдержать?

Зависит от executor и hardware:

- **LocalExecutor:** 50-200 DAG'ов, ~100 параллельных tasks
- **CeleryExecutor:** 1000+ DAG'ов, тысячи параллельных tasks
- **KubernetesExecutor:** ограничено только K8s ресурсами

**Bottlenecks:**
1. **Scheduler** — медленно сканирует много DAG'ов (тысячи)
2. **Metadata DB** — может стать узким местом (нужны indexes, vacuum)
3. **Network** — для distributed executors

В **больших installations** разделяют DAG'и по нескольким Airflow instances.


> [!mcq]
>
> **Вопрос:** Какие реальные scaling limits у одного Airflow instance и что становится bottleneck'ом первым?
>
> ---
>
> #### A) Airflow выдерживает миллионы DAG на одной machine без проблем — горизонтальное масштабирование не нужно — ❌ Неверно
>
> **Что на самом деле:** один Airflow instance практически упирается в **5000-10000 DAG'ов** даже на мощной hardware. Дальше начинаются проблемы: scheduler не успевает парсить DAG-файлы, metadata DB деградирует из-за роста таблиц `task_instance`/`dag_run`, UI становится медленным. Крупные компании (Airbnb, Lyft) разделяют DAG'и по нескольким Airflow installations по доменам.
>
> **Откуда путаница:** документация Airflow редко даёт жёсткие цифры, и кажется, что "просто добавь workers". На деле workers — не bottleneck; bottleneck — scheduler и metadata DB.
>
> **Если бы это было правдой:** Airbnb и Lyft не разделяли бы свои Airflow installations. На практике даже компании-создатели делят по командам.
>
> ---
>
> #### B) Главный bottleneck — workers; добавляешь больше worker нод и любое количество DAG'ов работает — ❌ Неверно
>
> **Что на самом деле:** workers — самый легко масштабируемый компонент (особенно с KubernetesExecutor). Реальные bottleneck'и в порядке возникновения: (1) **Scheduler** — медленно сканирует DAG-файлы, у него есть `parsing_processes` limit; (2) **Metadata DB** — таблица `task_instance` растёт нелинейно при много DAG, нужны индексы и retention policy; (3) **Webserver** — рендеринг UI с тысячами DAG медленный.
>
> **Откуда путаница:** интуитивно scaling = "больше воркеров". Это работает в простых системах (web servers). В Airflow workers — terminal nodes, bottleneck выше по pipeline.
>
> **Если бы это было правдой:** проблема решалась бы добавлением `airflow celery worker` процессов. На практике даже с 100 workers, если scheduler не успевает планировать, кластер простаивает.
>
> ---
>
> #### C) Лимит — около 100 DAG, дальше Airflow не работает физически — ❌ Неверно
>
> **Что на самом деле:** 100 DAG — это слабый LocalExecutor setup. Production CeleryExecutor спокойно держит 1000-5000 DAG, KubernetesExecutor — 5000-10000+ с правильным тюнингом. Лимит 100 — это рекомендация для LocalExecutor на одной VM.
>
> **Откуда путаница:** новички видят, что Airflow "медленно работает" с парой сотен DAG, и делают вывод о hard limit. На деле нужно тюнить `parsing_processes`, `max_active_runs_per_dag`, метаданные.
>
> **Если бы это было правдой:** Detmir/Lamoda не могли бы вести 1000+ pipelines. На практике они работают на одном Airflow + правильная архитектура.
>
> ---
>
> #### D) Лимит зависит от executor и hardware: LocalExecutor ≈ 50-200 DAG / ~100 параллельных tasks; CeleryExecutor ≈ 1000+ DAG / тысячи tasks; KubernetesExecutor ограничен только K8s ресурсами; первые bottleneck'и — Scheduler (DAG parsing), Metadata DB (рост task_instance), Network для distributed; крупные installations разделяют DAG по нескольким Airflow по доменам — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Airflow scaling — это **многомерная задача**, не «один лимит». Нужно отдельно мерить:
> - **DAG count** — сколько Python-файлов сканирует scheduler.
> - **Task instances per day** — сколько строк добавляется в `task_instance` ежедневно.
> - **Concurrent tasks** — сколько одновременно выполняется.
> - **DAG run frequency** — как часто запускаются (минутные DAG — нагрузка на scheduler).
>
> Реальные production числа (по сообществу):
> - **Airbnb** (создатели): ~5000 DAG, разбиты по командам на разные installations.
> - **Lyft**: десятки тысяч DAG, делят по доменам.
> - **Detmir / Avito-уровень**: 1000-3000 DAG на одном instance с тюнингом.
> - **Стандартный enterprise**: 200-500 DAG, никаких проблем без тюнинга.
>
> Bottleneck'и и решения:
> 1. **Scheduler DAG parsing** — `parsing_processes` (default 2 → 8-16 для prod), `min_file_process_interval` (30 → 120 сек), `dag_dir_list_interval`.
> 2. **Metadata DB** — индексы по `dag_id, execution_date`, регулярный `VACUUM ANALYZE`, retention policy на `task_instance`/`log`/`xcom` (например `airflow db clean --clean-before-timestamp`).
> 3. **DAG-Processor isolation (2.3+)** — выделить отдельный процесс `airflow dag-processor` от scheduler для горизонтального scaling парсинга.
> 4. **HA Scheduler (2.0+)** — несколько scheduler replicas (active-active через row-level locking).
>
> **Пример:**
> ```python
> # airflow.cfg для большого install (3000 DAG)
> [scheduler]
> parsing_processes = 16
> min_file_process_interval = 120
> dag_dir_list_interval = 300
> scheduler_heartbeat_sec = 5
> max_dagruns_to_create_per_loop = 50
>
> [core]
> max_active_runs_per_dag = 16
> max_active_tasks_per_dag = 64
> parallelism = 1024   # глобальный лимит concurrent task instances
> dag_concurrency = 64
>
> [database]
> sql_alchemy_pool_size = 30
> sql_alchemy_max_overflow = 60
>
> [logging]
> remote_logging = True
> remote_base_log_folder = s3://airflow-prod/logs/
> ```
>
> ```bash
> # cron job для очистки metadata DB раз в неделю
> airflow db clean --clean-before-timestamp '2025-04-01' \
>   --tables task_instance,xcom,job,log --yes
> ```
>
> **Когда применять:**
> - **Один Airflow на компанию** — до ~500 DAG, простая структура.
> - **Один Airflow на команду / домен** — 500-3000 DAG на instance, при росте организации.
> - **Разделение по environment (dev/prod)** — обязательно, не один Airflow для всех сред.
> - **Federated Airflow** — у Lyft каждая команда self-service, единый UI через aggregator.
>
> **Подводные камни:**
> - **`max_active_runs_per_dag=16` default может быть мало**: при backfill можно получить tasks waiting on slot. Тюнить под рабочую нагрузку.
> - **Long-running TaskInstance retention**: таблица `log` в metadata DB растёт быстрее всех; chunked deletes обязательны.
> - **DAG file size**: каждый DAG-файл парсится при каждом scan; если в файле тяжёлые imports или DB-запросы — это убивает scheduler. Top-level Python код DAG должен быть pure-functional.
> - **Smart Sensors (deprecated)** vs **Deferrable Sensors (2.2+)**: для крупных install Deferrable Sensors почти обязательны — экономят worker slots на сотнях ждущих tasks.
>
> **Связанные вопросы:** [[Q17]] — CeleryExecutor scaling; [[Q18]] — KubernetesExecutor; [[Q24]] — production deploy с правильными конфигами; [[Q26]] — best practices для prod DAGs.

## Q26. Best practices для production DAGs?

1. **Idempotency** — DAG должен быть безопасен к повторному запуску
2. **Atomicity** — task делает одну вещь, можно re-run
3. **Не импортируй тяжёлые libraries** на топ-level DAG (медленный parsing)
4. **Используй Variables/Connections** для конфигурации, не hardcode
5. **Pools** — ограничивать параллелизм для shared resources (DB, API)
6. **Tags** — для организации (`tags=['etl', 'critical']`)
7. **Retries** — настраивать в `default_args`
8. **SLA** — alerts если task не завершился вовремя
9. **Test DAGs** — `pytest` для DAG validation
10. **CI/CD** — деплой DAG'ов через git


> [!mcq]
>
> **Вопрос:** Что важнее всего соблюдать при разработке production DAG, и почему именно idempotency и atomicity?
>
> ---
>
> #### A) Главное — писать DAG как можно короче (1-2 task) для скорости — ❌ Неверно
>
> **Что на самом деле:** короткий DAG **противоречит** atomicity. Если 5 операций уместить в один task, то при failure посередине нельзя re-run только проблемную часть — придётся повторять весь task, что часто невозможно (нет idempotency). Production DAG обычно разбит на 5-15 small atomic tasks.
>
> **Откуда путаница:** меньше tasks = меньше overhead. Это верно для overhead scheduler, но цена — потеря recoverability. Trade-off в пользу granularity.
>
> **Если бы это было правдой:** все ETL писали бы как `BashOperator('python whole_pipeline.py')`. На практике это анти-паттерн — нельзя re-run только failed step.
>
> ---
>
> #### B) Хранить все секреты прямо в DAG-коде для transparency — ❌ Неверно
>
> **Что на самом деле:** **категорически нельзя** хранить secrets в DAG-коде. Во-первых, DAG лежит в git — secret окажется в истории. Во-вторых, DAG-файл доступен из UI через "Code" tab — любой Airflow user увидит пароли. Правильно: Connections (encrypted в metadata DB) + Secrets Backend (Vault, AWS Secrets Manager) для creds, Variables для config (только non-sensitive).
>
> **Откуда путаница:** в туториалах часто показывают `password = "test123"` для простоты. В production это compliance violation.
>
> **Если бы это было правдой:** SOC2/ISO27001 аудит провалился бы при первой проверке. На практике все enterprise Airflow выносят secrets в Vault.
>
> ---
>
> #### C) DAG никогда не нужно тестировать — Airflow сам проверяет валидность — ❌ Неверно
>
> **Что на самом деле:** Airflow проверяет **syntax** при парсинге, но не **бизнес-логику**, не зависимости, не корректность параметров. DAG может пройти парсинг и провалиться в runtime (неверный SQL, отсутствующий column). Production DAGs обязательно покрывают тестами: `pytest` для проверки структуры DAG (количество tasks, dependencies), unit-тесты для Python функций используемых в `@task`, integration-тесты с реальной БД через testcontainers.
>
> **Откуда путаница:** Airflow CLI `airflow dags test` проверяет, что DAG парсится — это уже считается "тестированием" наивно. На практике этого мало.
>
> **Если бы это было правдой:** ETL pipelines падали бы только в production. На практике крупные команды (Detmir, Yandex) имеют 30-50% test coverage на DAG-код и helper-функции.
>
> ---
>
> #### D) Production DAGs должны быть **идемпотентны** (безопасный re-run) и **атомарны** (task = одна операция); не импортировать тяжёлые libs на топ-level (медленный parsing); использовать Variables/Connections (не hardcode); Pools для shared resources; настраивать retries и SLA; писать pytest на DAG; деплоить через CI/CD из git — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Production DAG отличается от dev «работает на моём laptop» именно дисциплиной этих принципов. Idempotency означает: запуск DAG с теми же параметрами (logical_date) дважды даёт тот же результат, без дублей в данных. Atomicity означает: каждый task делает одну логическую операцию и либо успешно завершается, либо «как будто не выполнялся» — нет частичных эффектов.
>
> Без idempotency backfill становится опасным (дубли в warehouse), retries приводят к double-charge в Stripe, а scheduled retry после failure ломает downstream agg.
>
> Без atomicity нельзя re-run только failed step — приходится перезапускать огромную операцию или править данные руками.
>
> **Пример:**
> ```python
> from airflow.decorators import dag, task
> from airflow.models import Variable
> from airflow.providers.postgres.hooks.postgres import PostgresHook
> from datetime import datetime
>
> default_args = {
>     'owner': 'data-platform',
>     'retries': 3,
>     'retry_delay': timedelta(minutes=5),
>     'retry_exponential_backoff': True,
>     'sla': timedelta(hours=2),                  # alert если task > 2h
>     'on_failure_callback': send_to_pagerduty,
>     'on_sla_miss_callback': send_to_slack,
> }
>
> @dag(
>     dag_id='orders_daily_etl',
>     schedule='@daily',
>     start_date=datetime(2025, 1, 1),
>     catchup=False,
>     max_active_runs=1,                          # не позволять concurrent runs
>     default_args=default_args,
>     tags=['orders', 'critical', 'tier1'],
> )
> def orders_etl():
>     @task(pool='warehouse_write_pool', pool_slots=2)
>     def load_to_warehouse(logical_date: str):
>         hook = PostgresHook(postgres_conn_id='warehouse')
>         # IDEMPOTENCY: DELETE+INSERT за день, не append-only
>         hook.run(f"""
>             DELETE FROM analytics.orders WHERE date = '{logical_date}';
>             INSERT INTO analytics.orders
>             SELECT * FROM staging.orders WHERE date = '{logical_date}';
>         """)
>
>     @task
>     def validate_row_count(logical_date: str):
>         hook = PostgresHook(postgres_conn_id='warehouse')
>         (cnt,) = hook.get_first(
>             f"SELECT COUNT(*) FROM analytics.orders WHERE date = '{logical_date}'"
>         )
>         min_expected = int(Variable.get("orders_min_daily", 1000))
>         if cnt < min_expected:
>             raise ValueError(f"Only {cnt} rows, expected >= {min_expected}")
>
>     # ATOMICITY: каждый task — одна логическая операция
>     extracted = extract_from_source('{{ ds }}')
>     transformed = transform(extracted)
>     load = load_to_warehouse('{{ ds }}')
>     transformed >> load >> validate_row_count('{{ ds }}')
>
> orders_etl()
> ```
>
> **Когда применять:**
> - **Idempotency через MERGE/UPSERT или DELETE+INSERT** — для warehouse loads (BigQuery, Snowflake, Postgres).
> - **Idempotency через unique constraint + ON CONFLICT** — для row-level inserts в OLTP.
> - **Pools для shared API rate limits** — Avito ETL читает Bitrix24 API, pool ограничивает 5 concurrent calls.
> - **SLA alerts** — для tier-1 DAG, чтобы команда узнавала о деградации до того, как заметит бизнес.
>
> **Подводные камни:**
> - **`datetime.now()` в DAG ломает idempotency** — используй `{{ ds }}` или `logical_date` из context.
> - **Top-level imports тяжёлых libraries (pandas, requests с API call)** — scheduler парсит DAG-файл часто; долгий import = scheduler hang.
> - **Catchup=True по умолчанию** — при deploy старого DAG с `start_date` 2 года назад создаст сотни runs.
> - **PythonOperator со side-effects на module-level** — выполнится при каждом parse, не только при run.
> - **SLA не работает для очень коротких DAG** — `sla` checks делается по интервалам scheduler, малозаметно для DAG < 5 минут.
>
> **Связанные вопросы:** [[Q13]] — backfill требует idempotency; [[Q20]] — Connections и Variables для конфигурации; [[Q24]] — deploy DAG через git-sync; [[Q25]] — scaling tradeoffs.

## Q27. (!) Airflow vs Prefect vs Dagster?

| Критерий | Airflow | Prefect | Dagster |
|----------|---------|---------|---------|
| Возраст | 2014 | 2018 | 2018 |
| Standard | Apache top | Open-source | Open-source |
| Workflow def | DAG в Python | Tasks + Flows | Assets |
| Type system | Нет | Есть | Сильный (PEP 484) |
| Local dev | Сложный | Простой | Простой |
| UI | Хороший | Modern | Modern |
| Adoption | Доминирующий | Рост | Рост |

**Prefect** и **Dagster** — современные альтернативы, проще в local development. **Airflow** — стандарт de facto, огромная community.

В **новых проектах** часто рассматривают Prefect/Dagster, особенно для dev experience. В **enterprise** — Airflow доминирует.


> [!mcq]
>
> **Вопрос:** Какая ключевая концептуальная разница между Airflow, Prefect и Dagster, определяющая выбор инструмента?
>
> ---
>
> #### A) Airflow и Prefect — это одно и то же; разница только в названии — ❌ Неверно
>
> **Что на самом деле:** это разные продукты с разной философией. Airflow (2014, Apache) — DAG-first, statically defined в Python. Prefect (2018) — task/flow-first, dynamic execution, modern Python API без top-level DAG объекта. Dagster (2018) — **asset-first** (data-aware), думает о produced data, а не о tasks; вводит `Software-Defined Asset` концепцию.
>
> **Откуда путаница:** все три — Python orchestrators для data pipelines. На поверхности задачи похожи, но архитектурно сильно отличаются.
>
> **Если бы это было правдой:** Prefect и Dagster не существовали бы как отдельные проекты с >10k stars. На практике они активно конкурируют с Airflow.
>
> ---
>
> #### B) Только Dagster подходит для production, остальные — toys — ❌ Неверно
>
> **Что на самом деле:** Airflow — **доминирующий** production orchestrator (Airbnb, Lyft, Twitter, тысячи enterprise). Prefect и Dagster — современные альтернативы с лучшим dev experience, но Airflow доминирует в enterprise по количеству установок. Все три production-ready.
>
> **Откуда путаница:** Dagster маркетинг активно подчёркивает "asset-first" преимущества. Это валидный подход, но не делает Airflow toy.
>
> **Если бы это было правдой:** Apache top-level project Airflow не был бы стандартом de facto. На практике большинство Data Engineer вакансий требует Airflow.
>
> ---
>
> #### C) Ключевая разница в **модели описания workflow**: Airflow = DAG-first (статический Python DAG, операторы, дата-агностичный); Prefect = flow/task-first (динамический Python API, async-friendly, модерн UX); Dagster = asset-first (думает о data artifacts, типизация данных, software-defined assets); выбор: Airflow для legacy/enterprise/community, Prefect для Python-heavy modern teams, Dagster для analytics с фокусом на data lineage — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Все три решают одну задачу — orchestration data pipelines — но по-разному отвечают на вопрос «что является primitive абстракцией».
>
> **Airflow** — DAG это центр вселенной. Ты описываешь tasks и зависимости, scheduler запускает DAG по cron. Tasks не знают друг о друге кроме XCom; данные между ними передаются externally (S3, DB). Airflow data-agnostic — он не знает что такое «таблица users», он знает что task X запускается после task Y.
>
> **Prefect** — flow/task через декораторы, как обычный Python код. Нет глобального DAG объекта — flow выполняется как функция, ветвление через if/else работает динамически runtime. Prefect 2.0+ ввёл concept "subflows", "deployments", "blocks" (re-usable конфигурация). Async-first из коробки.
>
> **Dagster** — концепция Asset (data artifact: таблица, файл, ML-model). Ты описываешь не tasks, а **что они produce**. Asset имеет dependencies на upstream assets, type system проверяет совместимость. Dagster знает «table orders зависит от table raw_orders», что даёт data lineage из коробки.
>
> **Пример:**
> ```python
> # Airflow — DAG-first
> from airflow.decorators import dag, task
>
> @dag(schedule='@daily', start_date=datetime(2025, 1, 1))
> def orders_pipeline():
>     @task
>     def extract(): return fetch_orders()
>     @task
>     def transform(data): return clean(data)
>     @task
>     def load(data): write_to_warehouse(data)
>
>     load(transform(extract()))
> orders_pipeline()
>
>
> # Prefect — flow-first
> from prefect import flow, task
>
> @task
> def extract(): return fetch_orders()
> @task
> def transform(data): return clean(data)
> @task
> def load(data): write_to_warehouse(data)
>
> @flow(name="orders-pipeline")
> def orders_pipeline():
>     load(transform(extract()))   # обычный Python — никакого `>>`
>
> if __name__ == "__main__":
>     orders_pipeline.serve(cron="0 2 * * *")
>
>
> # Dagster — asset-first
> from dagster import asset, Definitions
>
> @asset
> def raw_orders():
>     return fetch_orders()
>
> @asset
> def cleaned_orders(raw_orders):       # dependency через аргумент
>     return clean(raw_orders)
>
> @asset
> def warehouse_orders(cleaned_orders):
>     write_to_warehouse(cleaned_orders)
>     return "warehouse://orders"
>
> defs = Definitions(assets=[raw_orders, cleaned_orders, warehouse_orders])
> ```
>
> **Когда применять:**
> - **Airflow** — Tinkoff/Detmir/Сбер enterprise с established stack, многолетние pipelines, hiring data engineers (рынок Airflow > Prefect+Dagster). Лучший выбор для legacy migration.
> - **Prefect** — стартапы и ML teams, где важен dev experience: быстрый local dev, modern Python (Pydantic, async). Yandex.Cloud projects, где Python-heavy stack.
> - **Dagster** — analytics teams с фокусом на data quality / lineage; интеграция с dbt из коробки; data mesh архитектуры. Хорош для warehouse-centric (Snowflake/BigQuery).
> - **Argo Workflows** — pure K8s teams, generic workflow (не data-specific).
>
> **Подводные камни:**
> - **Hiring пул**: Airflow специалистов в 10× больше, чем Prefect/Dagster.
> - **Vendor lock-in**: Prefect Cloud и Dagster Cloud — managed offerings; миграция со self-hosted на cloud и обратно требует усилий.
> - **Community / providers**: Airflow имеет ~80 official providers (Snowflake, BigQuery, AWS, etc), у Prefect/Dagster — меньше готовых интеграций.
> - **Stability vs novelty**: Airflow стабилен и зрел, но имеет накопленный legacy (XCom limits, scheduler quirks). Prefect/Dagster — современная архитектура, но меньше battle-tested patterns.
>
> **Связанные вопросы:** [[Q1]] — Airflow позиционирование как orchestrator; [[Q28]] — минусы Airflow которые компенсируют Prefect/Dagster; [[Q8]] — TaskFlow API как ответ Airflow на современные API.
>
> ---
>
> #### D) Все три — это просто JSON DSL-конфигурации, без Python кода — ❌ Неверно
>
> **Что на самом деле:** **все три** написаны на Python и используют Python как primary DSL. Workflow описывается Python-кодом, не JSON/YAML. Yaml-only конфигурации существуют (Argo Workflows, Tekton), но эти инструменты — другая категория (generic K8s workflow), не data orchestrators.
>
> **Откуда путаница:** в managed offerings (MWAA, Astronomer) есть YAML-конфиги для deployment. Но workflow definition — всегда Python.
>
> **Если бы это было правдой:** Data engineers могли бы не знать Python — достаточно YAML. На практике все три требуют свободного владения Python.

## Q28. Какие минусы Airflow?

1. **Slow local development** — поднять Airflow для теста медленно
2. **Тяжёлая для маленьких задач** — нужна metadata DB, scheduler, executor, web UI
3. **Tight coupling tasks ↔ Airflow API** — нельзя easily unit-test tasks
4. **XCom limit** — не для больших данных
5. **DAG parsing overhead** — Scheduler медленный с сотнями DAG'ов
6. **Sensor scaling** — `poke` mode съедает workers
7. **Не для streaming** — Airflow только для batch
8. **`execution_date` vs `logical_date`** — путаница исторически
9. **Сложности upgrade** — major versions могут ломать DAG'и

В **2024** многие выбирают **Prefect или Dagster** для новых проектов из-за лучшего dev experience. Airflow остаётся стандартом enterprise.


> [!mcq]
>
> **Вопрос:** Какие минусы Airflow в 2025 являются наиболее болезненными для production команд, и какие из них действительно фундаментальны (а не «исторические»)?
>
> ---
>
> #### A) Airflow в 2025 не имеет минусов — все исторические проблемы исправлены в 2.x — ❌ Неверно
>
> **Что на самом деле:** Airflow 2.x действительно сильно улучшил dev experience (TaskFlow API, HA scheduler, deferrable sensors), но **фундаментальные ограничения** остались. XCom через metadata DB всё ещё неподходит для больших payloads. Scheduler-driven model плохо подходит для event-driven workflows. Local dev требует Docker + Postgres + scheduler даже для проверки одного task.
>
> **Откуда путаница:** официальный roadmap активно решает старые проблемы, и кажется что "всё хорошо". На деле некоторые ограничения встроены в архитектуру.
>
> **Если бы это было правдой:** Prefect/Dagster не имели бы рынка. На практике эти продукты растут именно за счёт фундаментальных Airflow weaknesses.
>
> ---
>
> #### B) Основные минусы Airflow: (1) slow local dev — нужен Docker+Postgres+scheduler для теста; (2) тяжеловесность для маленьких задач; (3) tight coupling tasks ↔ Airflow API мешает unit-тестировать; (4) XCom через metadata DB — не для больших данных; (5) DAG parsing overhead на сотнях DAG; (6) `poke` mode sensors съедает workers; (7) не для streaming; (8) исторический баггаж execution_date vs logical_date; (9) сложности major upgrade; фундаментальные — coupling и scheduler-driven model, остальные постепенно решаются — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Минусы Airflow делятся на три категории по «исправимости»:
>
> **Фундаментальные (архитектурные)**:
> - **Tight coupling tasks ↔ Airflow API** — task функция использует `context`, `xcom_pull`, `Variable.get` — без mocks её нельзя запустить вне Airflow. Prefect/Dagster решают это через cleaner abstractions.
> - **Scheduler-driven model** — Airflow думает в терминах cron, а не events. Event-driven workflows требуют либо TriggerDagRunOperator, либо external scheduler (Argo).
> - **DAG-первичность vs Asset-первичность** — Airflow не знает что такое "table users", он знает только task names. Data lineage сложно получить без external tools (DataHub, Marquez).
>
> **Сильно улучшенные в 2.x**:
> - **HA Scheduler** — с 2.0 несколько scheduler replicas active-active.
> - **TaskFlow API** — меньше boilerplate, автоматический XCom.
> - **Deferrable sensors** — sensors не занимают worker slot (с 2.2).
> - **Dynamic Task Mapping** — `.expand()` для runtime количества tasks (с 2.3).
>
> **Operational (тюнятся)**:
> - **DAG parsing overhead** — настраивается через `parsing_processes`, `min_file_process_interval`.
> - **`poke` mode sensors** — решается переходом на `reschedule` или `deferrable`.
> - **Local dev** — Astronomer CLI (`astro dev start`) делает локальный setup быстрым.
>
> **Пример:**
> ```python
> # Tight coupling — этот task сложно unit-тестировать
> from airflow.decorators import task
> from airflow.models import Variable
>
> @task
> def process_orders(**context):
>     date = context['logical_date'].strftime('%Y-%m-%d')
>     api_key = Variable.get('orders_api_key')
>     # Без Airflow runtime context и Variable нельзя вызвать функцию
>     return fetch_orders(api_key, date)
>
>
> # ЛУЧШЕ — отделить бизнес-логику от Airflow
> def fetch_orders_pure(api_key: str, date: str) -> list:
>     """Pure function — легко unit-тестируется"""
>     return requests.get(f"https://api/orders?date={date}", headers={"X-Key": api_key}).json()
>
> @task
> def process_orders_task(date: str, **context):
>     api_key = Variable.get('orders_api_key')
>     return fetch_orders_pure(api_key, date)
>
> # Тест:
> def test_fetch_orders_pure(httpx_mock):
>     httpx_mock.add_response(json=[{"id": 1}])
>     result = fetch_orders_pure("test_key", "2025-01-01")
>     assert result == [{"id": 1}]
> ```
>
> **Когда применять (когда минусы становятся deal-breaker):**
> - **Streaming/event-driven**: Airflow — wrong tool. Kafka Streams, Flink или Prefect events лучше.
> - **ML pipelines с большой передачей данных**: XCom limit заметен, лучше Kubeflow или Metaflow.
> - **Modern dev experience критичен**: small startup без legacy → Prefect/Dagster.
> - **Data lineage as first-class concern**: Dagster выигрывает.
>
> **Подводные камни (mitigations):**
> - **Major version upgrades (1.x → 2.x)** — могут сломать DAG-файлы. Mitigation: тщательное чтение release notes, staging тестирование.
> - **Provider package version drift** — `apache-airflow-providers-*` имеют свои циклы релизов, могут конфликтовать с Airflow core. Mitigation: constraint files.
> - **Backward incompatibility внутри 2.x**: например `schedule_interval` → `schedule`, deprecated providers. Mitigation: следить за DeprecationWarning в логах.
> - **Lock-in на cron-style scheduling**: event-driven через `TriggerDagRunOperator` работает, но громоздко.
>
> **Связанные вопросы:** [[Q19]] — XCom limits подробнее; [[Q23]] — reschedule/deferrable как mitigation для poke; [[Q25]] — scaling и DAG parsing overhead; [[Q27]] — Prefect/Dagster как альтернативы решающие конкретные минусы.
>
> ---
>
> #### C) Главный минус — Airflow медленнее Spark на 10×, поэтому не подходит для больших данных — ❌ Неверно
>
> **Что на самом деле:** **Airflow не обрабатывает данные сам** — он orchestrator. Сравнивать его со Spark бессмысленно: Airflow запускает Spark job, не заменяет его. Performance Airflow измеряется в task throughput (десятки-сотни tasks/min на scheduler), а не в data throughput.
>
> **Откуда путаница:** новички иногда думают что Airflow — это compute engine. На самом деле compute делается в external system (Spark, BigQuery, Python в PythonOperator), Airflow только координирует.
>
> **Если бы это было правдой:** для больших данных был бы запрет на Airflow. На практике Airflow — стандартный способ оркестрировать Spark/BigQuery jobs терабайт-петабайт масштаба.
>
> ---
>
> #### D) Airflow не имеет недостатков — это идеальный инструмент для любых задач — ❌ Неверно
>
> **Что на самом деле:** ни один инструмент не идеален. Airflow — отличный orchestrator для batch ETL, но не для streaming (используй Kafka Streams/Flink), не для интерактивных queries (Jupyter/dbt), не для low-latency event processing (Kafka + microservices). Знание trade-offs — основа staff/principal-level data engineering.
>
> **Откуда путаница:** при долгой работе с Airflow привыкаешь и перестаёшь видеть ограничения. Это называется "Maslow's hammer" — когда есть молоток, всё кажется гвоздём.
>
> **Если бы это было правдой:** не было бы Prefect, Dagster, Argo, Kubeflow, Metaflow, Flyte. Существование этих продуктов доказывает, что Airflow не покрывает все use cases.

---

## See also

- [Apache Spark](apache-spark-interview.md) — Airflow часто оркестрирует Spark jobs
- [Apache Flink](apache-flink-interview.md) — vs Airflow (streaming vs batch)
- [Kafka Streams](kafka-streams-interview.md) — другой стиль (event-driven)
- [dbt](dbt-interview.md) — часто оркестрируется через Airflow
- [Stream Processing](stream-processing-interview.md) — Airflow для batch, не streaming
- [Data Warehousing](data-warehousing-interview.md) — main use case Airflow
- [Data Lake / Lakehouse](data-lake-lakehouse-interview.md) — pipelines в lake/warehouse
- [Kubernetes](../devops/kubernetes-interview.md) — KubernetesExecutor
- [Микросервисы](../architecture/microservices-interview.md) — Airflow vs scheduled jobs
- [PostgreSQL](../databases/postgresql-interview.md) — metadata DB
- [Docker](../devops/docker-interview.md) — деплой Airflow
- [[python-interview|Python]] — DAG = Python код (когда добавим)
