# CS-20260828-020: фактические числа качества — живой прогон сборки

## 0. Условия прогона

- **SHA прогона (полный):** `27ab9171697bf35b3fe16ffd7aece84917ffbe33` — проверено
  `git cat-file -t 27ab9171697bf35b3fe16ffd7aece84917ffbe33` → `commit`.
- **JVM:** OpenJDK Corretto **22.0.2** (`22.0.2+9-FR`, aarch64, `Amazon.com Inc.`) —
  `java -version`. Launcher JVM Gradle: та же 22.0.2 (`./gradlew --version`).
  **CI использует JDK 17 (Temurin) на `ubuntu-latest`** (`.github/workflows/ci.yml:34-38`,
  `timeout-minutes: 25` — `ci.yml:31`) — время из §5 несопоставимо с CI напрямую:
  другая мажорная версия JVM и другая архитектура (arm64 против x86_64/CI-раннера).
- **Gradle:** 8.9 (wrapper), `OS: Mac OS X 26.3.1 aarch64` (`./gradlew --version`).
- **Docker:** сервер **29.4.0**, API `1.54`. Активный контекст CLI показывает `default`
  (`docker info` → `Context: default`), но это тот же сокет OrbStack — `DOCKER_HOST`
  из окружения (`unix:///Users/sergeyvoronin/.orbstack/run/docker.sock`) переопределяет
  выбор контекста; `docker context ls` подтверждает, что endpoint `default` и endpoint
  `orbstack` совпадают.
- **Testcontainers API-пин:** `JAVA_TOOL_OPTIONS=-Dapi.version=1.41` — присутствовал в
  окружении сессии изначально (не выставлялся вручную по ходу прогона), подтверждено
  `env | grep JAVA_TOOL_OPTIONS`. Известная грабля с рассогласованием версии Docker API
  (`docs/quality-report.md:38-42`) этим пином закрыта заранее — падений на согласовании
  API не было.
- **Холодный `build/`:** да. Перед первым прогоном `find . -maxdepth 3 -type d -name build`
  не находил каталогов — worktree был чист от прошлых сборок.
- **Прогоны (дословные командные строки, из корня worktree):**
  1. `time ./gradlew check --no-daemon` — RC=0, полный вывод в `/tmp/CS-20260828-020-check.log`
     (в репозиторий не кладётся). Внешний замер по `date +%s` (до/после): `DUR=2064s`
     (34м 24с); собственный таймер Gradle в конце лога — `BUILD SUCCESSFUL in 34m 20s`
     (2060s). Разница (~4с) — старт JVM/CLI-обвязка вне таймера Gradle.
  2. `./gradlew :quiz-app:jacocoTestReport --no-daemon` — RC=0, `DUR=710s` (11м 50с),
     лог в `/tmp/CS-20260828-020-jacoco-app.log`. `:quiz-app:test` не был `UP-TO-DATE`
     и выполнился заново целиком (детерминированный итог: тот же счёт тестов, что и
     в прогоне check — 1873/0/0/3 — но иное время: 283.65с суммарно против 723.66с в
     прогоне check, вероятно прогрев JIT/файлового кэша ОС между двумя последовательными
     запусками). **Тестовые артефакты `quiz-app` в этом отчёте (§2, §4, §5, §6) взяты
     из ЭТОГО второго прогона** — исходный `test-results` от прогона check был
     перезаписан переисполнением `:quiz-app:test`. Артефакты `quiz-domain` и
     `quiz-persistence` (§2, §5) остаются от первого прогона (check) — они не
     переисполнялись, `build/test-results/test/*.xml` этих модулей их не трогал.
  3. `./gradlew -I /tmp/CS-20260828-020-jacoco-all.gradle :quiz-domain:jacocoTestReport
     :quiz-persistence:jacocoTestReport --no-daemon` — попытка снять покрытие двух
     модулей вне штатной конфигурации (init-скрипт **вне репозитория**, не коммитится).
     Три варианта init-скрипта подряд:
     - `allprojects { plugins.withId('java') { apply plugin: 'jacoco'; ... } }` →
       `BUILD FAILED`: `Failed to apply plugin 'org.gradle.jacoco'` →
       `class org.gradle.invocation.DefaultGradle_Decorated cannot be cast to class
       org.gradle.api.Project` (реентрантное применение плагина изнутри колбэка
       `plugins.withId` во время конфигурации проекта — известное ограничение Gradle 8.9).
     - `subprojects { apply plugin: 'jacoco'; ... }` → `BUILD FAILED`:
       `Could not find method subprojects() for arguments [...] on build of type
       org.gradle.invocation.DefaultGradle` — у init-скрипта нет метода `subprojects()`,
       только `allprojects()` (объект `Gradle`, не `Project`).
     - `allprojects { apply plugin: 'jacoco'; tasks.withType(JacocoReport)... }`
       (без гейта `plugins.withId('java')`) → `BUILD SUCCESSFUL in 5m 55s`, но
       `:quiz-domain:jacocoTestReport SKIPPED` и `:quiz-persistence:jacocoTestReport
       SKIPPED` — задача `test` в графе задач вообще не появилась (не `UP-TO-DATE`, а
       отсутствует), XML/exec-отчёт не создан ни для одного модуля
       (`find modules/quiz-domain modules/quiz-persistence -iname "*.xml" -path
       "*jacoco*"` → пусто). Причина — `JacocoReport` пропускается без exec-данных, а
       задачу `test` эта команда в аргументах не запрашивала: сам плагин `jacoco`
       никакой автопривязки `jacocoTestReport → test` не даёт, в `quiz-app` эта связь
       прописана руками (`dependsOn(tasks.test)`, `modules/quiz-app/build.gradle.kts:99`).
       В init-скрипте той строки нет.
     Итог: способ не сработал трижды из трёх попыток разными путями — `quiz-domain` и
     `quiz-persistence` помечены `не измерено` в §3 (AC-4), выдумывать числа не стал.

## 1. Итог прогона

`./gradlew check --no-daemon` — **BUILD SUCCESSFUL**, код возврата 0, `34m 20s`
(внутренний таймер Gradle) / `34m 24s` (внешний замер). 16 задач, все выполнены
(`16 actionable tasks: 16 executed`). Гейт `:quiz-app:jacocoTestCoverageVerification`
прошёл без единого сообщения о нарушении правила (ни `BUNDLE`, ни `PACKAGE`) — оба
уровня гейта see §6. Падений и ошибок тестов — 0 по всем трём модулям (§2). Красного
прогона нет — п.13 AC-13 (красный прогон) неприменим.

## 2. Тесты

| Метрика | quiz-domain | quiz-persistence | quiz-app | Итого |
|---|---|---|---|---|
| Тестов | 246 | 119 | 1873 | **2238** |
| Failures | 0 | 0 | 0 | **0** |
| Errors | 0 | 0 | 0 | **0** |
| Skipped | 0 | 0 | 3 | **3** |
| Время (сумма `<testsuite time>`) | 4.54с | 24.39с | 283.65с | **312.58с** |

`failures=0 errors=0 skipped=3`. `BUILD SUCCESSFUL`.

Падений нет.

Пропуски (все три — `org.junit.jupiter.api.Assumptions.assumeTrue`, штатная
условная защита на случай пустой тестовой БД, не дефект):

- `com.cheatsheet.quiz.api.controller.InterviewControllerTest.studyModeLearnPhaseRendersConfirmButtonThenQuizShowsOptions()`
  — `assumeTrue(interviewSession != null, "STUDY-сессия не создана — нет вопросов в тестовой БД")`
  (`InterviewControllerTest.java:258-259`).
- `com.cheatsheet.quiz.api.controller.InterviewControllerApiTest.apiAnswerReturnsCorrectStructure()`
  — `assumeTrue(!options.isEmpty(), "Нет вариантов ответа для вопроса " + q.id())`
  (`InterviewControllerApiTest.java:78`).
- `com.cheatsheet.quiz.api.controller.InterviewControllerApiTest.apiAnswerIncludesSessionCountersWhenSessionExists()`
  — тот же паттерн `firstQuestionOrSkip()`/`assumeTrue` на отсутствие вариантов ответа.

## 3. Покрытие по модулям

| Модуль | INSTRUCTION | LINE | BRANCH | CLASS |
|---|---|---|---|---|
| `quiz-domain` | не измерено — задача `test` не была запрошена в командной строке, `jacocoTestReport` пропущен без exec-данных (`:quiz-domain:jacocoTestReport SKIPPED`, XML не создан); в `quiz-app` эту связь даёт ручной `dependsOn(tasks.test)`, в init-скрипте его нет. Команда: `./gradlew -I /tmp/CS-20260828-020-jacoco-all.gradle :quiz-domain:jacocoTestReport --no-daemon` | — | — | — |
| `quiz-persistence` | не измерено — та же причина (`:quiz-persistence:jacocoTestReport SKIPPED`, XML не создан). Команда: `./gradlew -I /tmp/CS-20260828-020-jacoco-all.gradle :quiz-persistence:jacocoTestReport --no-daemon` | — | — | — |
| `quiz-app` | 23230/25186 = 92.23% | 4616/4992 = 92.47% | 1966/2529 = 77.74% | 221/223 = 99.10% |

Источник `quiz-app`: `modules/quiz-app/build/reports/jacoco/test/jacocoTestReport.xml`,
корневые `<counter>` отчёта.

## 4. Покрытие по пакетам (`quiz-app`)

54 пакета в XML-отчёте (`grep`-подсчёт `<package name=` — см. Приложение A). Колонка
«в PACKAGE-гейте» — по правилу `build.gradle.kts:139-157`: `includes` = `api.*`,
`service.*`, `feature.*`; `excludes` = `*.dto`, `*.dto.*` (пакет исключён, если любой
его сегмент — `dto`).

| Пакет | LINE покрыто/всего | LINE % | BRANCH % | В PACKAGE-гейте |
|---|---|---|---|---|
| `com.cheatsheet.quiz` | 1/3 | 33.3 | — | нет |
| `com.cheatsheet.quiz.api.constants` | 1/1 | 100.0 | — | да |
| `com.cheatsheet.quiz.api.dto.request.interview` | 0/0 | — | — | нет |
| `com.cheatsheet.quiz.api.mapper.request` | 66/66 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.api.mapper.response` | 43/43 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.api.mapper.view` | 254/262 | 96.9 | 82.8 | да |
| `com.cheatsheet.quiz.api.security` | 152/171 | 88.9 | 66.2 | да |
| `com.cheatsheet.quiz.api.validation` | 13/13 | 100.0 | 75.0 | да |
| `com.cheatsheet.quiz.common.constants` | 0/0 | — | — | нет |
| `com.cheatsheet.quiz.common.exception` | 105/142 | 73.9 | 57.4 | нет |
| `com.cheatsheet.quiz.common.util` | 188/200 | 94.0 | 90.1 | нет |
| `com.cheatsheet.quiz.common.web` | 28/31 | 90.3 | 61.1 | нет |
| `com.cheatsheet.quiz.config.app` | 64/64 | 100.0 | — | нет |
| `com.cheatsheet.quiz.config.runtime` | 9/9 | 100.0 | — | нет |
| `com.cheatsheet.quiz.config.web` | 419/464 | 90.3 | 77.9 | нет |
| `com.cheatsheet.quiz.feature.admin.dto.response` | 0/1 | 0.0 | — | нет |
| `com.cheatsheet.quiz.feature.admin.service` | 9/9 | 100.0 | — | да |
| `com.cheatsheet.quiz.feature.auth.controller` | 18/18 | 100.0 | 60.0 | да |
| `com.cheatsheet.quiz.feature.auth.service` | 10/10 | 100.0 | 50.0 | да |
| `com.cheatsheet.quiz.feature.export.controller` | 10/10 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.feature.export.exception` | 7/7 | 100.0 | — | да |
| `com.cheatsheet.quiz.feature.export.service` | 16/16 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.feature.export.usecase` | 14/14 | 100.0 | 83.3 | да |
| `com.cheatsheet.quiz.feature.interview.controller` | 222/253 | 87.7 | 74.2 | да |
| `com.cheatsheet.quiz.feature.interview.dto.response` | 2/2 | 100.0 | — | нет |
| `com.cheatsheet.quiz.feature.interview.dto.response.answer` | 4/4 | 100.0 | — | нет |
| `com.cheatsheet.quiz.feature.interview.dto.response.progress` | 7/7 | 100.0 | — | нет |
| `com.cheatsheet.quiz.feature.interview.dto.response.session` | 3/3 | 100.0 | — | нет |
| `com.cheatsheet.quiz.feature.interview.mapper` | 49/49 | 100.0 | 95.0 | да |
| `com.cheatsheet.quiz.feature.interview.service.command` | 34/39 | 87.2 | 50.0 | да |
| `com.cheatsheet.quiz.feature.interview.service.copy` | 28/33 | 84.8 | 64.5 | да |
| `com.cheatsheet.quiz.feature.interview.service.core` | 205/230 | 89.1 | 78.6 | да |
| `com.cheatsheet.quiz.feature.interview.service.facade` | 12/17 | 70.6 | — | да |
| `com.cheatsheet.quiz.feature.interview.service.flow` | 249/264 | 94.3 | 78.3 | да |
| `com.cheatsheet.quiz.feature.interview.service.insight` | 8/8 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.feature.interview.service.page` | 468/503 | 93.0 | 73.8 | да |
| `com.cheatsheet.quiz.feature.interview.service.progress` | 88/102 | 86.3 | 76.9 | да |
| `com.cheatsheet.quiz.feature.interview.service.report` | 8/8 | 100.0 | 75.0 | да |
| `com.cheatsheet.quiz.feature.interview.service.review` | 261/275 | 94.9 | 85.7 | да |
| `com.cheatsheet.quiz.feature.interview.service.topic` | 293/332 | 88.3 | 71.8 | да |
| `com.cheatsheet.quiz.feature.interview.usecase` | 101/104 | 97.1 | 50.0 | да |
| `com.cheatsheet.quiz.feature.interview.usecase.mvc` | 169/183 | 92.3 | 77.6 | да |
| `com.cheatsheet.quiz.feature.interview.usecase.stats` | 47/47 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.infrastructure.bootstrap` | 36/38 | 94.7 | 87.5 | нет |
| `com.cheatsheet.quiz.infrastructure.health` | 49/54 | 90.7 | 68.8 | нет |
| `com.cheatsheet.quiz.infrastructure.render` | 198/202 | 98.0 | 83.3 | нет |
| `com.cheatsheet.quiz.infrastructure.search` | 18/18 | 100.0 | 90.0 | нет |
| `com.cheatsheet.quiz.service.admin` | 17/20 | 85.0 | 50.0 | да |
| `com.cheatsheet.quiz.service.cache` | 9/9 | 100.0 | 100.0 | да |
| `com.cheatsheet.quiz.service.diagram` | 196/203 | 96.6 | 89.6 | да |
| `com.cheatsheet.quiz.service.event` | 11/11 | 100.0 | — | да |
| `com.cheatsheet.quiz.service.imports` | 348/370 | 94.1 | 76.3 | да |
| `com.cheatsheet.quiz.service.imports.dto` | 4/4 | 100.0 | — | нет |
| `com.cheatsheet.quiz.service.strategy` | 45/46 | 97.8 | 95.5 | да |

**Минимум по гейтуемым пакетам:** `com.cheatsheet.quiz.feature.interview.service.facade`
— LINE 12/17 = **0.706** (70.6%). Это число идёт в обновлённый комментарий
`build.gradle.kts:137-138` (§7).

## 5. Время прогона

- **Wall-clock `./gradlew check --no-daemon`:** `34m 20s` (Gradle) / `34m 24s`
  (внешний `date`-замер), см. §0.
- **Сумма `<testsuite time>` по модулям** (артефакты см. §0 — `quiz-app` из второго
  прогона `jacocoTestReport`, `quiz-domain`/`quiz-persistence` из прогона `check`):
  `quiz-domain` 4.54с, `quiz-persistence` 24.39с, `quiz-app` 283.65с, **итого 312.58с**
  (сумма времени тест-классов не равна wall-clock `check`: тесты внутри модуля частично
  параллелятся JUnit-платформой, плюс wall-clock `check` включает компиляцию всех трёх
  модулей, ArchUnit, jacoco-верификацию и Gradle-обвязку).
- **Топ-10 самых долгих тест-классов** (по сумме `<testsuite time>` их файла):

| # | Модуль | Класс | Время |
|---|---|---|---|
| 1 | `quiz-app` | `com.cheatsheet.quiz.ExportControllerErrorTest` | 58.47с |
| 2 | `quiz-app` | `com.cheatsheet.quiz.api.controller.InterviewControllerTest` | 32.55с |
| 3 | `quiz-app` | `com.cheatsheet.quiz.architecture.LayeredArchitectureTest` | 16.83с |
| 4 | `quiz-app` | `com.cheatsheet.quiz.service.imports.McqVisibleLinkageContractTest` | 15.61с |
| 5 | `quiz-app` | `com.cheatsheet.quiz.service.imports.SeedSchemaContractTest` | 14.11с |
| 6 | `quiz-persistence` | `com.cheatsheet.quiz.persistence.AnswerOptionRepositoryTest` | 11.31с |
| 7 | `quiz-app` | `com.cheatsheet.quiz.service.InterviewServiceTest` | 11.05с |
| 8 | `quiz-app` | `com.cheatsheet.quiz.service.InterviewPageMvcServiceTest` | 9.17с |
| 9 | `quiz-app` | `com.cheatsheet.quiz.service.InterviewFlowMvcServiceTest` | 8.76с |
| 10 | `quiz-app` | `com.cheatsheet.quiz.api.PublicEndpointsSmokeTest` | 6.47с |

- **Сопоставление с CI:** `.github/workflows/ci.yml:31` даёт `timeout-minutes: 25` для
  джобы `build-test`. Локальный `check` занял `34m 20s` — **дольше** CI-таймаута,
  но локальный и CI-прогон не сопоставимы напрямую: другая JVM (22 arm64 против
  17 x86_64 Temurin на `ubuntu-latest`), холодный Gradle-кэш локально против
  прогретого `gradle/actions/setup-gradle` в CI, и `--no-daemon` локально против
  daemon-режима в CI. Прямых оснований считать CI тоже уложится/не уложится в 25 минут
  из этого локального замера нет — это отдельный вопрос (см. Follow-ups в design.md).

## 6. Дельта к замеру 2026-08-04

| Величина | Было (2026-08-04) | Стало (сейчас) | Изменение | Порог | Запас до порога |
|---|---|---|---|---|---|
| BUNDLE LINE | 0.915 (3633/3970) | 0.9247 (4616/4992) | +0.0097 | 0.85 | +0.0747 |
| BUNDLE BRANCH | 0.764 (1366/1788) | 0.7774 (1966/2529) | +0.0134 | 0.70 | +0.0774 |
| Минимум по гейтуемым пакетам | 0.600 (`feature.interview.service.facade`, 9/15) | 0.706 (`feature.interview.service.facade`, 12/17) | +0.106 | 0.55 | +0.156 |

Тот же пакет (`feature.interview.service.facade`) остаётся минимумом спустя почти
месяц разработки — маленький пакет (17 строк), любые несколько непокрытых строк дают
заметный процент. Оба BUNDLE-показателя и минимум по пакетам выросли, запас до порога
увеличился по всем трём величинам — активная разработка (кампании FOLIO/фронт-рефаундации)
не просадила покрытие.

Число тестов также выросло относительно прошлого замера (`docs/quality-report.md:46-53`,
там же — старый замер до синхронизации в §7): было `246 / 117 / 1211` (домен/персистенция/
app), стало `246 / 119 / 1873` — `quiz-persistence` +2, `quiz-app` +662.

## 7. Что обновлено в репозитории

- **`modules/quiz-app/build.gradle.kts:117`** — комментарий с замера 2026-08-04
  (`LINE 0.915 (3633/3970), BRANCH 0.764 (1366/1788)`) заменён на замер 2026-09-01
  (`LINE 0.9247 (4616/4992), BRANCH 0.7774 (1966/2529)`). Пороги, `element`,
  `counter`, `value`, `minimum` — не тронуты.
- **`modules/quiz-app/build.gradle.kts:137-138`** — комментарий с замера 2026-08-04
  по 33 пакетам (минимум `feature.interview.service.facade`, 9/15 = 0.600) заменён на
  замер 2026-09-01 по 35 пакетам (минимум тот же пакет, 12/17 = 0.706). `includes`/
  `excludes` PACKAGE-правила не тронуты.
- **`docs/quality-report.md` §2 (таблица тестов + строка счётчиков)** — числа заменены
  на измеренные (246/119/1873, итого 2238, `skipped=3`); абзац про «22 незакоммиченных
  теста редизайна» (был датирован состоянием ДО коммита этих тестов) удалён — все
  четыре перечисленных класса (`FocusWideCompositionContractTest`,
  `FocusPrimaryActionReachContractTest`, `SessionBuilderLaunchReachContractTest` и т.д.)
  уже в `HEAD` и учтены в текущем totale; утверждение отработало и стало неактуальным.
- **`docs/quality-report.md` §3 (обе таблицы покрытия)** — числа заменены на измеренные
  (INSTRUCTION 23230/25186, LINE 4616/4992, CLASS 221/223, BRANCH 1966/2529; BUNDLE/
  PACKAGE строки — см. §6 этого отчёта). Абзац «До 2026-08-04 гейт был декоративным…»
  сохранён дословно — это историческая проза, не устаревшее число.
- Добавлена строка `Измерено 2026-09-01, SHA 27ab9171697bf35b3fe16ffd7aece84917ffbe33`
  в `docs/quality-report.md` рядом с обновлёнными таблицами.

## Приложение A (воспроизведение чисел)

Все команды — из корня worktree, без пересборки, читают только уже существующие
артефакты (`build/test-results/**/*.xml`, `build/reports/jacoco/test/jacocoTestReport.xml`).

**§2 — тесты (счётчики и падения/пропуски):**

```bash
python3 - <<'PY'
import glob, xml.etree.ElementTree as ET
for mod in ['quiz-domain','quiz-persistence','quiz-app']:
    tests=fail=err=skip=0; time=0.0
    for f in glob.glob(f'modules/{mod}/build/test-results/test/TEST-*.xml'):
        t = ET.parse(f).getroot()
        tests+=int(t.get('tests',0)); fail+=int(t.get('failures',0))
        err+=int(t.get('errors',0)); skip+=int(t.get('skipped',0))
        time+=float(t.get('time',0))
    print(mod, tests, fail, err, skip, f"{time:.2f}s")
PY
```

**§3/§4 — покрытие `quiz-app` (корневые счётчики + число пакетов):**

```bash
X=modules/quiz-app/build/reports/jacoco/test/jacocoTestReport.xml
python3 -c "
import xml.etree.ElementTree as ET
t = ET.parse('$X').getroot()
for c in t.findall('counter'):
    cov, mis = int(c.get('covered')), int(c.get('missed'))
    print(c.get('type'), cov, cov+mis, f'{cov/(cov+mis)*100:.2f}%')
print('packages:', len(t.findall('package')))
"
```

**§4 — таблица по пакетам с признаком PACKAGE-гейта:**

```bash
X=modules/quiz-app/build/reports/jacoco/test/jacocoTestReport.xml
python3 - <<PY
import xml.etree.ElementTree as ET
t = ET.parse("$X").getroot()
for pkg in t.findall('package'):
    name = pkg.get('name').replace('/', '.')
    d = {c.get('type'): (int(c.get('covered')), int(c.get('missed'))) for c in pkg.findall('counter')}
    lc, lm = d.get('LINE', (0,0))
    segs = name.split('.')
    gated = any(name==p or name.startswith(p+'.') for p in
                ['com.cheatsheet.quiz.api','com.cheatsheet.quiz.service','com.cheatsheet.quiz.feature']) \
            and 'dto' not in segs
    print(name, lc, lc+lm, gated)
PY
```

**§5 — топ-10 самых долгих тест-классов:**

```bash
python3 - <<'PY'
import glob, xml.etree.ElementTree as ET
rows=[]
for mod in ['quiz-domain','quiz-persistence','quiz-app']:
    for f in glob.glob(f'modules/{mod}/build/test-results/test/TEST-*.xml'):
        t = ET.parse(f).getroot()
        rows.append((mod, t.get('name'), float(t.get('time',0))))
rows.sort(key=lambda r: -r[2])
for mod,name,tm in rows[:10]:
    print(mod, name, f"{tm:.2f}s")
PY
```
