---
title: "ULTRATHINK — план генерации interview-материалов"
description: "Master-план следующих итераций: какие шпаргалки писать, в каком порядке, как валидировать."
updated: "2026-05-26"
status: "active"
---

# ULTRATHINK

Master-план генерации interview-материалов через скиллы `/interview-writer` (теория) →
`/mcq-quality-fixer` (MCQ JSON-сидер) → `/interview-options-writer` (deprecated, по умолчанию пропускаем).

После **каждой** итерации:
1. Запустить `bash scripts/verify-md-no-mcq.sh <md>` — должно быть `OK`.
2. Запустить `bash scripts/verify-mcq-json.sh <json>` ИЛИ `ajv validate --spec=draft7 --strict=false -s modules/quiz-app/src/main/resources/seed/mcq-schema.json -d <json>`.
3. Прогнать `./gradlew :quiz-app:test --tests "*McqJsonLoader*" -q` для smoke.
4. Обновить `cheatsheets/interview/TOC.md` (счётчики файлов).
5. Поставить здесь галочку напротив пункта.
6. Сделать `/compact` (пользователь триггерит сам — см. цель).

Цели качества (для каждой шпаргалки):
- ≥ 26 вопросов, ≥ 8 из них помечены `**(!)**` (важные).
- 3-4 mermaid-диаграммы.
- Frontmatter с 6 полями (`title`, `description`, `tags`, `type`, `difficulty`, `aliases`, `updated`).
- Секция `## Содержание` (TOC).
- Секция `## See also` с ≥ 7 markdown-ссылками (без `[[wikilink]]`).
- Русский язык, термины в backticks, конкретные цифры.
- **НИ ОДНОГО** `> [!mcq]` или Tier 1 эмодзи в .md.
- Соответствующий JSON-сидер: ≥ 25 вопросов, rotation ratio max/min ≤ 2, все секции непустые, ajv-валидация green.

---

## Фаза 1 — заполнить пустышки (1 Q → 28+ Q)

- [x] **design-feed-system-interview** ~~(1 Q сейчас)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, BUILD SUCCESSFUL). Завершено 2026-05-26.
- [x] **design-payment-system-interview** ~~(1 Q сейчас)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL). Завершено 2026-05-26.
- [x] **design-rate-limiter-interview** ~~(1 Q сейчас)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 168 KB). Завершено 2026-05-26.

## Фаза 2 — расширить тонкие

- [x] **design-url-shortener-interview** ~~(8 Q → 28+ Q)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 174 KB; очистил file от MCQ-callouts/Tier1 эмодзи/wikilinks). Завершено 2026-05-26.
- [x] **design-typeahead-interview** ~~(25 Q — добить до 30+)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 130 KB; добавлены Q26-Q30: ML ranking LambdaMART, query understanding, multi-language ICU, monitoring metrics, антипаттерны). Завершено 2026-05-26.
- [x] **design-search-interview** ~~(20 Q → 28+)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 153 KB; добавлены Q21-Q30: BM25 vs TF-IDF formulas, hybrid BM25+dense+RRF, faceted refinement, geo geohash/S2/H3, Lucene segments+translog, query understanding pipeline, multi-tenancy strategies, personalization, quality metrics, антипаттерны). Markdown очищен от inline MCQ + Tier1 emoji + wikilinks. Завершено 2026-05-26.

## Фаза 3 — добавить недостающие критичные

- [x] **design-netflix-interview** ~~(NEW)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 179 KB). Покрыто: Open Connect CDN, encoding pipeline per-title/per-chunk, codec ladder H.264/HEVC/VP9/AV1, ABR BOLA + ML, DRM trio, recommendation 2-stage + bandits, artwork personalization, microservices Netflix OSS, resilience patterns, Chaos Monkey/Kong, multi-region active-active, A/B platform, data platform Kafka+Flink+Iceberg, downloads, anti-fraud household graph, cost optimization. Завершено 2026-05-26.
- [x] **design-dropbox-interview** ~~(NEW)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 160 KB). Покрыто: chunking fixed vs CDC через rolling hash, CAS SHA256 + free dedup, Magic Pocket exabyte storage + SMR drives + Reed-Solomon 10+4, delta sync rsync (Adler-32 rolling), sync engine watcher→queue→diff→upload→notify, OS file events (inotify/FSEvents/ReadDirectoryChangesW), conflict resolution .conflict files + CRDT Y.js, multi-region GDPR residency, Smart Sync stub files FUSE, encryption AES-256-GCM + HSM, E2EE trade-offs, anti-abuse DMCA+PhotoDNA, cost optimization 75% saving. Завершено 2026-05-26.
- [x] **design-google-maps-interview** ~~(NEW)~~ → **30 Q + 30 MCQ JSON** (rotation 8/8/7/7, ratio 1.14, schema VALID, BUILD SUCCESSFUL, 175 KB). Покрыто: geo indexing (quadtree/S2/H3/R-tree/geohash), tile pyramid zoom 0-21, vector vs raster tiles, CDN delivery, routing Dijkstra→A*→CH→CRP, OSM ingestion, ETA ML DeepETA, multi-modal RAPTOR, POI search geo+text, geocoding Nominatim, places Spanner, multi-region data sovereignty, offline maps, Street View, traffic probe data Flink, monitoring. Завершено 2026-05-27.
- [ ] **design-pastebin-interview** (NEW). Document storage: anonymous + auth, short URL, syntax highlighting, expiration, raw vs view, abuse mitigation, full-text search.

## Фаза 4 — audit и refresh (опционально)

- [ ] **design-instagram-interview** — проверить актуальность 2026 (Reels, Stories pipeline).
- [ ] **design-twitter-interview** — добавить «X» rename context, current architecture.
- [ ] **design-youtube-interview** — refresh: VP9/AV1, Shorts, recommendation.

## Фаза 5 — AI/ML hardware и edge

- [ ] **ai-hardware-interview** (NEW). GPU vs TPU vs custom (Groq LPU, Cerebras), memory hierarchy (HBM3e, NVLink), CUDA/ROCm, mixed precision (FP16/BF16/FP8/INT8), inference batching, KV cache, MoE routing hardware.
- [ ] **edge-ai-interview** (NEW). On-device inference: ONNX, TFLite, CoreML, model quantization, distillation, pruning, MLC-LLM, llama.cpp.

---

## Состояние

Запущено: 2026-05-26.
Текущая итерация: **Фаза 3** → следующее **design-pastebin-interview** (NEW: document storage, anonymous + auth, short URL, syntax highlighting, expiration).
Закрыто (Фаза 1): design-feed-system, design-payment-system, design-rate-limiter.
Закрыто (Фаза 2): design-url-shortener, design-typeahead, design-search.
Закрыто (Фаза 3): design-netflix, design-dropbox, design-google-maps.
Все: 30 Q + 30 MCQ JSON, BUILD SUCCESSFUL.
