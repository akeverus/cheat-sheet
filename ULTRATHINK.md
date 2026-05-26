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
- [ ] **design-typeahead-interview** (25 Q — добить до 30+, добавить ML-ranking, personalization, multi-language).
- [ ] **design-search-interview** (20 Q → 28+). Inverted index, TF-IDF, BM25, vector search hybrid, faceted search, autocomplete, geo-search.

## Фаза 3 — добавить недостающие критичные

- [ ] **design-netflix-interview** (NEW). Video streaming (ABR/HLS/DASH/CMAF), Open Connect CDN, recommendation system (CF + DL), encoding pipeline (per-title/per-chunk), chaos engineering, multi-region, DRM (Widevine/FairPlay/PlayReady), microservices stack (Eureka/Zuul/Hystrix/Atlas).
- [ ] **design-dropbox-interview** (NEW). File storage + sync: chunking (4 MB blocks), deduplication (content-addressable storage), delta sync (rsync algorithm), conflict resolution, offline mode, sharing model, encryption at rest, mobile uploads.
- [ ] **design-google-maps-interview** (NEW). Geo: quadtree vs S2 vs H3, tile pyramid (zoom 0..21), routing (Dijkstra → CRP/CH/A*), traffic data (real-time + historical), POI search, ETA prediction.
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
Текущая итерация: **Фаза 2** → следующее **design-typeahead-interview** (25 Q → 30+ Q + ML ranking, multilanguage).
Закрыто: design-feed-system, design-payment-system, design-rate-limiter, design-url-shortener (все по 30 Q + 30 MCQ, BUILD SUCCESSFUL).
