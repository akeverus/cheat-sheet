# Go Concurrency Q5 — исправление default GOMAXPROCS

Дата: 2026-11-05. Статус: `AUTHOR_VERIFIED`, только Q5:B0. Прочитаны видимый вопрос, тело Q5, четыре A/B/C/D и все sections. Независимое ревью, слепая фаза и FINAL не заявлены.

Исправлено безусловное default=NumCPU и обязательное применение automaxprocs. Варианты сопоставляют определение и default policy: A — лимит исполнения и современная политика; B — устаревший постоянный default 1; C — ошибочный предел существующих goroutines; D — ошибочный предел I/O. Ключ A, вопрос и порядок сохранены. Явная область «при активной новой политике Go 1.25+» есть во всех вариантах, поэтому условия не спрятаны после выбора.

Объяснения учитывают affinity, Linux quota, округление и minimum-2, language-version/GODEBUG compatibility, explicit override и SetDefaultGOMAXPROCS. NumCPU не объявлен контейнерной CPU-квотой. CPU-пример условный и основан на документации, runtime не запускался.

Источники: [runtime GOMAXPROCS](https://pkg.go.dev/runtime#GOMAXPROCS), [Go 1.25](https://go.dev/doc/go1.25#runtime), [официальный разбор контейнерной политики](https://go.dev/blog/container-aware-gomaxprocs). Проверены 2026-11-05; pkg.go.dev показывал Go 1.27.3, граница новой политики — 1.25.

JSON рабочего дерева: `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/go/go-concurrency-interview.json`, SHA-256 `f42aa5ec267d134aa5406f1b5e4c528a6ea886a74a45186a511e37f476348017`. Теория: `cheatsheets/interview/programming-languages/go/go-concurrency-interview.md`, SHA-256 `67842592b1a6abd00b50de3d076e7fc65b0cf2108728c4411edb2ecec2b43e25`. Q5 (канонический JSON с сортировкой ключей): SHA-256 `84ea023be04fb2c5b20a2ecdc5f2b70c330eb674d729fdc9376c17281cd7d473`. Подготовленный индексный seed SHA-256 `7656358aa6def7d2362d0ec3cac88bb0a6fe1d7fb3aed822da04fd86e846b620` отличается от рабочего файла: в нём изолирован только Q5, а прежние изменения Q14/Q23/Q24/Q25 сохраняются незакоммиченными. Неизменённые baseline-вопросы здесь не проходят новый аудит.

Адресный verify-mcq-json: PASS, 35 Q / 36 блоков, semantic review NOT_RUN. Все варианты сравниваются по одному предмету и условию политики; у correct нет единственного подробного определения на фоне коротких wrong. Это авторская оценка, не независимый SFPL PASS.

[Остаток синхронизации теории](../../../../../interview-coverage/go-theory-packages-2026-11-05.md): отдельно остаются пояснения Q3, Q4 и Q26; пример Q4 пока содержит busy-loop и называет cancel остановкой без отдельного ожидания. В этом пакете они не исправлялись и не принимаются. Go-компилятор не установлен; Docker socket повторно не запрашивался. Теория не изменялась.
