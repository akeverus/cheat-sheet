# CS-20260828-015: Playwright-прогон (console-clean, reflow, keyboard-quiz) в ci.yml

## Контекст

Follow-up от CS-20260828-002 (Phase 0, INFRASTRUCTURE + TESTING). Формулировка
из `.sdlc/CS-20260828-002/design.md:323-326`:

> Добавить Playwright-прогон (`console-clean`, `reflow`, `keyboard-quiz`) в
> `.github/workflows/ci.yml` как отдельный job на PR: сейчас 6 e2e-спеков не
> выполняются ни в одном автоматическом гейте (проверка — job появился и
> зелёный на PR).

DoD-гейт задачи (`custom`, `ci-e2e-job`):

```
grep -q 'console-clean.spec.js' .github/workflows/ci.yml \
  && grep -q 'reflow.spec.js' .github/workflows/ci.yml \
  && grep -q 'keyboard-quiz.spec.js' .github/workflows/ci.yml
```

### Что есть сейчас (замерено на HEAD `273a8c8d`)

**Дыра №1 — ни один e2e-спек не запускается автоматически.** `ci.yml` (110
строк) содержит ровно два job'а: `build-test` (`ci.yml:28`, `./gradlew build -x
test` + `./gradlew check`) и `docker-build` (`ci.yml:75`, только на push в
master). Шага `npx playwright test` в файле нет. Единственный workflow, который
вообще зовёт Playwright — `visual-goldens.yml`, и он `workflow_dispatch`-only,
гоняет один спек из шести (`visual-matrix.spec.js`).

**Дыра №2 — паттерн триггера не покрывает `e2e/`.** Оба триггера `ci.yml`
фильтруются по `paths` (`ci.yml:6-12` push, `ci.yml:14-20` pull_request):

```yaml
      - 'modules/**'
      - 'gradle/**'
      - '*.gradle.kts'
      - 'gradlew'
      - 'gradlew.bat'
      - '.github/workflows/ci.yml'
```

`e2e/**` в списке нет. То есть правка любого спека или `playwright.config.js`
сегодня не запускает CI вообще — и после добавления job'а не запускала бы тоже.
Именно эта дыра позволила шести спекам молча выпасть из автоматики.

Инвентарь `e2e/` (6 спеков, 599 строк):

| Спек | Тестов | Что держит |
|---|---|---|
| `console-clean.spec.js` (38) | 8 (по одному на поверхность) | нет `console.error/warning`, `pageerror`, ответов ≥400 на `/`, `/train`, `/stats`, `/topics`, `/bookmarks`, `/settings`, `/review`, `/nope-404` |
| `reflow.spec.js` (50) | 28 (7 поверхностей × 4 ширины) | WCAG 1.4.10: нет горизонтального вылета при 320px и на зум-ширинах 1152/1382/1571 |
| `keyboard-quiz.spec.js` (231) | 7 | полный цикл только с клавиатуры: skip-link, MCQ, флешкарта, TYPED_RECALL, STUDY, MARATHON |
| `chart-lazy`, `font-network`, `visual-matrix` | — | вне скоупа задачи |

**Конфиг Playwright** (`e2e/playwright.config.js`): `testDir: '.'`,
`baseURL = process.env.E2E_BASE_URL || 'http://localhost:8080'`, `workers: 1`,
`fullyParallel: false`, `retries: process.env.CI ? 1 : 0` (GitHub Actions ставит
`CI=true` сам → ретрай уже включён), два проекта — `chromium` и `webkit`.
`webServer` поднимается только при `E2E_START_SERVER=1`.

**Проверенная рецептура подъёма приложения в CI** — `visual-goldens.yml:33-99`:
service-контейнер `postgres:16-alpine` (db/user/pass = `interview`) с
`pg_isready`-healthcheck, `setup-java@v4` (temurin 17), `gradle/actions/setup-gradle@v4`,
`setup-node@v4` (node 20, cache npm по `e2e/package-lock.json`), `npm ci` +
`npx playwright install --with-deps chromium`, `./gradlew :quiz-app:bootRun -q >
/tmp/bootRun.log 2>&1 &` в фоне и поллинг `curl -sf /actuator/health || curl -sf /`
90×2s. Дефолты datasource совпадают с этими значениями
(`application.yml:46-48`: `POSTGRES_DB:interview`, `POSTGRES_USER:interview`,
`POSTGRES_PASSWORD:interview`), `management.endpoints.web.exposure.include`
содержит `health` (`application.yml:187-192`) — поллинг рабочий.

**Данные при пустой БД появляются сами.** `StartupRunner`
(`infrastructure/bootstrap/StartupRunner.java:41-52`) безусловно зовёт
`questionImportService.importAll()` — импорт из `cheatsheets/interview/**/*.md`
+ `seed/mcq/**.json` идёт даже при `app.interview-reset-on-startup=false`.
`InterviewPathResolver` (`common/util/InterviewPathResolver.java:30-46`) при
относительном `app.interview-path` поднимается по родительским каталогам, пока
не найдёт `cheatsheets/interview` — поэтому `bootRun` из корня репо и из
`modules/quiz-app` одинаково находит контент. Фикстуры для CI не нужны.

**Конвенция тестов для shell-тулбелта в репо.**
`scripts/lint-scripts.sh` — гейт, `scripts/tests/lint-scripts.selftest.sh` —
самодостаточный раннер без bats/pytest: helpers `assert_exit_code` /
`assert_contains` / `assert_empty`, фикстуры в `mktemp -d` с `trap ... RETURN`,
имена функций `lint_whenCondition_thenOutcome`, массив `TESTS=(...)` в конце.
Ни один намеренно битый файл не лежит в git — только во временных каталогах.
Это точный прототип для теста этой задачи.

**Ограничения среды.** PyYAML в системном python3 отсутствует (`import yaml` →
ModuleNotFoundError), поэтому гейт-скрипт обязан быть на чистом bash/grep —
никаких yaml-библиотек. `.pre-commit-config.yaml` защищён хартией от
автономных правок (§6) — новый гейт туда не вписываем.

## Решение

Три изменения, все — инфраструктура сборки; продуктовый код не трогаем.

### 1. `e2e/**` в оба `paths`-фильтра `ci.yml`

В списки `ci.yml:6-12` и `ci.yml:14-20` добавляются две строки:

```yaml
      - 'e2e/**'
      - 'scripts/verify-ci-e2e-gate.sh'
```

Без первой правка спека не запускает свой же гейт. Вторая — чтобы сломанный
гейт-скрипт не проехал в master без прогона.

`cheatsheets/**` в фильтр НЕ добавляем: пользователь коммитит шпаргалки
десятками раз в день, полный build+e2e на каждый MCQ-коммит съест минуты
раннера без пользы (риск R3 ниже).

### 2. Job `e2e` в `ci.yml`

```yaml
  e2e:
    name: E2E · Playwright (chromium)
    runs-on: ubuntu-latest
    timeout-minutes: 30
    needs: build-test

    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: interview
          POSTGRES_USER: interview
          POSTGRES_PASSWORD: interview
        ports:
          - 5432:5432
        options: >-
          --health-cmd "pg_isready -U interview -d interview"
          --health-interval 5s
          --health-timeout 5s
          --health-retries 20

    steps:
      - uses: actions/checkout@v4
      - setup-java@v4 (temurin 17)
      - gradle/actions/setup-gradle@v4
      - setup-node@v4 (node 20, cache npm, cache-dependency-path e2e/package-lock.json)
      - name: Install e2e deps + Chromium
        working-directory: e2e
        run: |
          npm ci
          npx playwright install --with-deps chromium
      - name: Boot app (background)          # рецептура visual-goldens.yml:75-99
      - name: Run e2e specs
        working-directory: e2e
        env:
          E2E_BASE_URL: http://localhost:8080
        run: |
          npx playwright test \
            console-clean.spec.js \
            reflow.spec.js \
            keyboard-quiz.spec.js \
            --project=chromium
      - upload-artifact (if: failure) — e2e/test-results/, e2e/playwright-report/, /tmp/bootRun.log
      - Stop app (if: always())
```

Решения и отвергнутые альтернативы:

- **`--project=chromium`, без webkit.** Обязательно, а не опционально:
  `playwright install ... chromium` ставит только один движок, и проект `webkit`
  упал бы на «Executable doesn't exist». Установка обоих движков удваивает и
  время (43 теста × 2), и площадь флака; webkit-специфика спеков
  (`Alt+Tab`-обход, poll-нажатие цифры в флешкарте) настраивалась на macOS-webkit.
  Webkit — follow-up отдельным ночным workflow.
- **`needs: build-test`** — отвергнут параллельный запуск: репо явно экономит
  минуты раннера (комментарии `ci.yml:22`, `ci.yml:79`), а на сломанной сборке
  e2e всё равно бессмысленен; заодно gradle-кэш приходит тёплым.
- **Отвергнут `bootJar` + `java -jar`** вместо `bootRun`: рабочая рецептура уже
  есть в `visual-goldens.yml`, дублировать её в другой форме — лишний источник
  расхождений.
- **Отвергнут вынос списка спеков в `npm run test:ci`**: имена спеков обязаны
  лежать в самом `ci.yml` (DoD-гейт грепает по нему), а дублирование списка в
  двух файлах разъезжается.
- **Отвергнут `if:`-фильтр по событию** — job нужен и на PR, и на push в master.

### 3. Гейт `scripts/verify-ci-e2e-gate.sh` + шаг в `build-test`

Одного добавления job'а мало: он снимается одной строкой, и repo вернётся в
сегодняшнее состояние молча. Поэтому — маленький bash-гейт (~70 строк, без
внешних зависимостей), который читает workflow-файл и падает с внятной строкой,
если контракт нарушен.

```
Usage: bash scripts/verify-ci-e2e-gate.sh [path/to/ci.yml]
```

Дефолт аргумента — `.github/workflows/ci.yml` относительно корня репо,
вычисленного от `BASH_SOURCE` (не от `$PWD`) — как в `lint-scripts.sh:17-18`.

Проверки (каждая падает независимо, с именем нарушенного пункта; **строки-комментарии
YAML отбрасываются до проверок** — упоминание спека в комментарии не считается):

| # | Инвариант | Как проверяется |
|---|---|---|
| 1 | файл существует | `[[ -f ]]`, иначе rc=1 «workflow not found» |
| 2 | job `e2e` объявлен | строка `^  e2e:` в блоке `jobs:` |
| 3 | три спека перечислены | `console-clean.spec.js`, `reflow.spec.js`, `keyboard-quiz.spec.js` — каждый отдельным сообщением |
| 4 | `e2e/**` в `paths` триггера `push` | awk-скан блока `on:` (от `^on:` до следующего ключа нулевого отступа) с отслеживанием текущего триггера по `^  (push\|pull_request):` |
| 5 | `e2e/**` в `paths` триггера `pull_request` | там же |
| 6 | у job'а есть postgres-service | `image: postgres:` в файле |

rc=0 и тишина при успехе (конвенция `lint-scripts.sh`), rc=1 и перечень всех
нарушений при провале. Шаг в `build-test` (перед `Build (no tests)`):

```yaml
      - name: Verify e2e gate contract
        run: bash scripts/verify-ci-e2e-gate.sh
```

Зубы у гейта именно из-за этого шага: `.github/workflows/ci.yml` уже есть в
`paths` (`ci.yml:12`, `ci.yml:20`), значит любая правка workflow запускает
`build-test` → гейт → красный, если e2e-job или `e2e/**` вырезали.

**Файлы дельты (4):** `.github/workflows/ci.yml`, `scripts/verify-ci-e2e-gate.sh`
(новый), `scripts/tests/verify-ci-e2e-gate.selftest.sh` (новый),
`.sdlc/CS-20260828-015/design.md`.

## Acceptance Criteria

1. **DoD-гейт зелёный.** `grep -q 'console-clean.spec.js' .github/workflows/ci.yml
   && grep -q 'reflow.spec.js' … && grep -q 'keyboard-quiz.spec.js' …` → rc=0.
2. **YAML валиден и job виден парсеру.**
   `ruby -ryaml -e 'j=YAML.load_file(".github/workflows/ci.yml")["jobs"];
   abort("no e2e job") unless j.key?("e2e")'` → rc=0. (Ruby есть и локально, и на
   ubuntu-раннере; PyYAML в системе нет — проверка именно на ruby.)
3. **Триггер покрывает `e2e/`.** rc=0 у:
   ```
   ruby -ryaml -e 'y=YAML.load_file(".github/workflows/ci.yml"); o=y["on"]||y[true];
     %w[push pull_request].each{|t| abort("no e2e/** in #{t}") unless o[t]["paths"].include?("e2e/**")}'
   ```
   `y["on"]||y[true]` — не описка: Psych читает YAML 1.1, и ключ `on:`
   приходит булевым `true` (проверено на текущем `ci.yml`: `y.keys` →
   `["name", true, "concurrency", "jobs"]`). Сегодня эта команда даёт
   `no e2e/** in push`, rc=1 — красное доказательство пункта.
4. **Гейт-скрипт проходит на реальном репо.** `bash scripts/verify-ci-e2e-gate.sh`
   → rc=0, пустой stdout.
5. **Гейт-скрипт краснеет на сломанном контракте.** На копии `ci.yml` без строки
   `- 'e2e/**'` в блоке `pull_request` → rc=1, в выводе есть `pull_request`.
6. **Селф-тест зелёный.** `bash scripts/tests/verify-ci-e2e-gate.selftest.sh` →
   rc=0; в выводе число пройденных = числу функций в массиве `TESTS`
   (10, см. «Тест-контракт»); ни одного `assertion failed`.
7. **Тулбелт-линт не сломан.** `bash scripts/lint-scripts.sh` → rc=0 (ловит
   синтаксис обоих новых `.sh`).
8. **Три спека реально зелёные на chromium.** Локальный прогон из worktree
   против приложения на 8091 (порт 8080 занят bootRun'ом пользователя — хартия §6):
   ```
   docker compose up -d postgres
   SERVER_PORT=8091 ./gradlew :quiz-app:bootRun --no-daemon &
   cd e2e && npm ci && npx playwright install chromium
   E2E_BASE_URL=http://localhost:8091 npx playwright test \
     console-clean.spec.js reflow.spec.js keyboard-quiz.spec.js --project=chromium
   ```
   Приёмка — хвост вывода Playwright в отчёте реализации: суммарное число тестов
   и `N passed` без `failed`; `skipped` допустимы (спеки сами скипаются на
   флешкарточных ветках). Красный прогон = задача не готова; **чинить спек
   ослаблением ассерта запрещено** — только с парным [guard]-тестом.
9. **Дельта не выходит за 4 файла** из «Решения»: `git status --porcelain -uall`
   не содержит путей вне `.github/workflows/ci.yml`, `scripts/verify-ci-e2e-gate.sh`,
   `scripts/tests/verify-ci-e2e-gate.selftest.sh`, `.sdlc/CS-20260828-015/`.
10. **Продуктовый код не тронут.** `git diff --name-only master...HEAD | grep -c
    '^modules/'` → `0`.

## Тест-контракт

Playwright-спеки (`e2e/console-clean.spec.js`, `e2e/reflow.spec.js`,
`e2e/keyboard-quiz.spec.js`) — существующий актив, **не переписываются и не
дополняются** этой задачей: предмет задачи — их запуск, а не их содержимое.
Новых тел в `e2e/` быть не должно.

### unit · `scripts/tests/verify-ci-e2e-gate.selftest.sh` (скрипт под тестом: `scripts/verify-ci-e2e-gate.sh`)

Стиль — дословно `scripts/tests/lint-scripts.selftest.sh`: `set -uo pipefail`,
helpers `assert_exit_code` / `assert_contains` / `assert_empty`, фикстуры —
копии реального `ci.yml` в `mktemp -d` с одной вырезанной строкой, уборка через
`trap ... RETURN`, финальный массив `TESTS=(...)` и счётчик PASS/FAIL.
Ни одного заведомо битого workflow-файла в git.

- [new]   `gate_whenWorkflowHasE2eJobAndAllThreeSpecs_thenExitsZeroSilently`
- [new]   `gate_whenE2eJobBlockRemoved_thenExitsOneAndNamesTheJob`
- [new]   `gate_whenOneSpecDropped_thenExitsOneAndNamesThatSpec`
- [new]   `gate_whenPushPathsLackE2eGlob_thenExitsOneAndNamesPushTrigger`
- [new]   `gate_whenPullRequestPathsLackE2eGlob_thenExitsOneAndNamesPullRequestTrigger`
- [new]   `gate_whenPostgresServiceRemoved_thenExitsOne`
- [new]   `gate_whenSeveralInvariantsBroken_thenReportsEveryOneNotJustFirst`
- [new]   `gate_whenWorkflowFileMissing_thenExitsOneWithExplanation`
- [new]   `gate_whenInvokedFromUnrelatedCwd_thenStillChecksItsOwnRepoWorkflow`
- [guard] `gate_whenSpecNamesAppearOnlyInCommentedOutLines_thenStillExitsOne`
  — фиксирует, что наивный `grep` по всему файлу не считается прохождением:
  закомментированные `# npx playwright test console-clean.spec.js …` и
  `#  e2e:` обязаны оставить гейт красным, иначе гейт можно «пройти», удалив
  job и оставив его в комментарии.

Прогон целиком: `bash scripts/tests/verify-ci-e2e-gate.selftest.sh` (AC-6).

## Риски

- **R1 — спек красный именно в CI-среде, не локально.** `console-clean` требует
  абсолютно чистой консоли на 8 поверхностях; ubuntu-раннер может добавить своё
  (отсутствующие системные шрифты, иной тайминг сети → `networkidle` раньше
  подгрузки). Митигация: `retries: 1` уже в конфиге; артефакты
  (`test-results/`, `playwright-report/`, `bootRun.log`) выгружаются на падении.
  Если красное воспроизводится — это находка продукта, а не повод ослаблять
  спек: ослабление требует парного [guard]-теста (правило контракта).
- **R2 — `keyboard-quiz` на пустой/иной выборке.** Тест `study:` пинит тему
  `algorithms/algorithmic-paradigms/backtracking-interview` и ждёт ровно 4
  радио-варианта; тест `marathon:` ждёт `Вопрос 1 из N`. Импорт на старте
  наполняет БД (`StartupRunner:50`), но если MCQ-сидер этой темы изменится
  чужим loop'ом — тест покраснеет в CI, а не локально. Митигация: AC-8 требует
  живого зелёного прогона на актуальном HEAD перед сдачей; при расхождении —
  чинить пин, а не выкидывать тест.
- **R3 — минуты раннера.** 43 теста последовательно (`workers: 1`) + сборка +
  импорт ≈ 12–20 минут на PR поверх `build-test`. `timeout-minutes: 30` даёт
  запас; `concurrency.cancel-in-progress` (`ci.yml:23-25`) гасит устаревшие раны.
  Если окажется дорого — сокращать зум-матрицу `reflow`, а не выкидывать job.
- **R4 — `paths`-фильтр остаётся дырявым для контента.** Правка `cheatsheets/**`
  или `seed/mcq/**` (последнее — под `modules/**`, значит триггерит) может
  сломать e2e без прогона в первом случае. Осознанное решение (см. §1); ловится
  на следующем же коммите в `modules/**`.
- **R5 — bash-гейт против YAML.** Построчные проверки не понимают якорей и
  многострочных скаляров YAML. Приемлемо: `ci.yml` пишется руками и плоско, а
  структурная проверка есть в AC-2/AC-3 через ruby. Если workflow усложнится —
  переносить гейт на `actionlint`.

## Follow-ups

- Ночной workflow (`schedule`) с полной e2e-матрицей: оба движка (chromium +
  webkit) и все шесть спеков, включая `chart-lazy`, `font-network`,
  `visual-matrix`; проверка — прогон по расписанию зелёный и артефакты выгружены.
- Описать CI-гейты и e2e-слой в README (сейчас `grep -i 'github actions\|playwright'
  README.md` пуст — зафиксировано в отчёте CS-20260828-002 §14); проверка — грепы
  находят раздел с командами локального прогона.
- Вынести рецептуру «postgres service + bootRun + wait-for-health» из
  `ci.yml` и `visual-goldens.yml` в composite action `.github/actions/boot-app`;
  проверка — оба workflow зовут action, дублирующие шаги удалены, оба зелёные.
