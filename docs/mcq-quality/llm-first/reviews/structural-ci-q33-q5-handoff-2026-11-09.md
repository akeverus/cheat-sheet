# Q33 AI Architecture и Q5 Reservation — адресная подготовка для structural CI

Дата: 2026-11-09. Статус: **адресный содержательный коммит опубликован**. Это авторская содержательная проверка двух новых вопросов, не независимое ревью и не FINAL.

На удалённом `master` JSON тем пока заканчиваются Q32 и Q4, а парные Markdown содержат Q33 и Q5. GitHub Actions run [42628284882](https://github.com/akeverus/cheat-sheet/actions/runs/42628284882) завершился ошибкой на шаге проверки всех seed-файлов и Markdown-пар. Локальный структурный PASS ниже относится к рабочему дереву; он не подтверждает исправление удалённого CI.

## Принятый объём

- `ai-ml/ai-application-architecture-interview.json`, Q33:B0. Видимый Q33 и теория описывают RPM/TPM, активные запросы, ограниченную очередь и дедлайн. Ключ B покрывает эти ограничения. A оставляет бесконечную очередь, C приписывает асинхронности рост ёмкости провайдера, D исключает расход токенов. Прочитаны все A–D и все поля их `sections`; связь с теорией и [лимитами OpenAI](https://developers.openai.com/api/docs/guides/rate-limits) сохранена. [Метрики vLLM](https://docs.vllm.ai/en/latest/usage/metrics/) подтверждают разделение выполняющихся и ожидающих запросов. Исправлен только отступ четырёх строк `text` внутри нового Q33.
- `system-design/design-reservation-system-interview.json`, Q5:B0. Видимый Q5 и теория разделяют локальный инвентарь, получение событий OTA и подтверждение после устойчивой записи. Ключ C сохраняет ночной остаток, идемпотентность, порядок подтверждения и сверку. A допускает устаревший остаток, B учитывает повторную доставку как новую продажу, D подтверждает сообщение до локальной записи. Прочитаны все A–D и `sections`; [Booking.com Reservations API](https://developers.booking.com/connectivity/docs/reservations-api/retrieving-new-reservations-ota) описывает отдельное подтверждение, а [OTA_HotelAvailNotif](https://developers.booking.com/connectivity/docs/ota-hotelavailnotif) задаёт `BookingLimit` по типу номера и дате. В Q5 убраны самоссылка в `related` и двусмысленное местоимение в `edge_cases`.

В Q33 длины A/B/C/D: 93/100/96/90 символов; в Q5: 125/139/130/123. У всех четырёх вариантов каждого вопроса по одному предложению и одному `;`, без выделения кодом. Отличия длины не дают надёжной подсказки к ключу. Wrong-варианты остаются полными ошибочными моделями. Ключи и B0 сохранены.

## Границы файлов и хеши

База подготовки: `HEAD 80e6336841d17781a253b7ef335015375ee0be25`. Перед staging HEAD, SSH remote, рабочие SHA и diff были повторно сверены.

| Файл | SHA-256 сейчас | Предлагаемый охват коммита |
|---|---|---|
| `cheatsheets/interview/ai-ml/ai-application-architecture-interview.md` | `f2d50c37c0a82c367e5538546ca2ea24071ab0401b29aecf81b7724aaf39dc3d` | Не менять: Q33 уже в HEAD |
| `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/ai-application-architecture-interview.json` | `7239a9e5e50c8743f740b2fce96d2351d7e3c11c26408a36331bd2c3de133cc5` | Только добавление Q33; Q1–Q32 совпадают с HEAD |
| `cheatsheets/interview/system-design/design-reservation-system-interview.md` | `e159d059ff66bfc840e112552572b547d62eefade1680e43b91076b67aabc9b4` | Не менять: Q5 уже в HEAD |
| `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-reservation-system-interview.json` | `6461285e554436cf1537d43d20899d0b3ca8543cb271f0109b16c69e6eafd765` | Только Q5; в рабочем дереве дополнительно есть чужая строка Q4 |

Для Reservation подготовлен Q5-only вариант поверх текущего HEAD: SHA-256 `7714a1b480bae94cc20de3a49f4fa30c3cdca0fc8333e98a924c89704c916e02`. Его Q1–Q4 побайтно следуют HEAD; чужое уточнение Q4 про Stripe API v1 остаётся в рабочем дереве и в коммит не входит. Канонические SHA-256 объектов вопроса при сериализации с отсортированными ключами: Q33 `faa9f8ca16cafd16335f509ed62500d898696f3d2da63e6f204f17d220b68c8f`, Q5 `0e5fc2db3304d3e9bdb405d9d85e951c243ec70769643f399b249bf6e001c0d0`.

Адресный валидатор на текущем рабочем дереве: `PASS: files=2 questions=38 blocks=38; semantic review NOT_RUN`. `git diff --check` — без ошибок. Fact-freshness обоих файлов остаётся `TODO` на уровне всей темы; его незакоммиченные изменения не входят в этот пакет. Отчёт по Q32 и остальные вопросы AI Architecture не объявлены принятыми.

Содержательный коммит `aa98d01f582ea0cb63e1afc59be8f6d82adc89f4` включает ровно два JSON и 120 добавленных строк. Staged diff содержал только Q33 и Q5; Q4 не входил. После обычного SSH push удалённый `master` был прочитан как `aa98d01f582ea0cb63e1afc59be8f6d82adc89f4`. Этот handoff сохраняется отдельным отчётным коммитом. Новый GitHub-прогон нужно оценить по фактическому статусу и логам; локальный PASS и публикация не равны remote PASS. Статус FINAL этим действием не присваивается.
