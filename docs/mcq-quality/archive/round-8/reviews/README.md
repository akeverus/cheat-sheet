# MCQ Review Sidecars — ROUND 8

Per-file review records. One JSON per topic slug: `<topic_slug>.json`.

Каждый файл содержит запись на каждый `q_number/block_idx` со флагами проверок
(stem / correct / каждый distractor / parity). `null` = не проверено (никогда не
конвертировать автоматически в `true`). Файл не DONE, пока нет записи на каждый блок.

Схема записи блока — см. `PLAN_INTERVIEW.md` §2 и §4; LLM-поля — §31.5 в
`PROMPT_PLAN_INTERVIEW.md`.

**`SKEL` (HARD, LLM):** в `llm_review` обязательно `skel: true` — все 4
`option.text` на одном surface skeleton. Pre-check:
`python3 scripts/mcq-skel-gate.py <json>` ловит грубые mismatch, но **не**
принимает `skel`. Без LLM-`skel: true` запрещены `four_option_pass: true`, а в
мастер-плане — `PAR=✅` и `FINAL=✅`.
