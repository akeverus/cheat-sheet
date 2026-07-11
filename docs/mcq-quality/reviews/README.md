# MCQ Review Sidecars — ROUND 8

Per-file review records. One JSON per topic slug: `<topic_slug>.json`.

Каждый файл содержит запись на каждый `q_number/block_idx` со флагами проверок
(stem / correct / каждый distractor / parity). `null` = не проверено (никогда не
конвертировать автоматически в `true`). Файл не DONE, пока нет записи на каждый блок.

Схема записи блока — см. `PLAN_INTERVIEW.md` §2 и §4.
