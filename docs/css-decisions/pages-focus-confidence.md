# pages-focus-confidence.css — досье решений (ARCH-02)

Полные доводы и история правил, вынесенные из комментариев `pages-focus-confidence.css`
(REF-009): эссе читают при ПЕРЕСМОТРЕ решения, а не при каждом чтении файла.
У правила в CSS остаётся указатель с тезисом; якоря ниже — цель указателей.
Рабочие инварианты («НЕ менять», пороги) остаются в CSS.

<a id="pages-focus-confidence-e01"></a>
## pages-focus-confidence-e01 · pages-focus-confidence.css:8

**Контекст:** `html[data-design] .focus-page .confidence-buttons {`

Кнопки самооценки уверенности (.confidence-*) — ВЫНЕСЕНЫ сюда из pages-result.css
   (Волна 3, ШАГ R / Cluster D). interview.js (attachConfidenceButtons) инъектит их
   ТОЛЬКО на focus-page ПОСЛЕ ответа (grade → /api/confidence для SM-2, не AI-вызов);
   no-JS result.html их не рендерит → focus-only (жили в pages-result ошибочно, туда
   их доносила глобальная загрузка). Заякорены под .focus-page (гейт css-leak-audit);
   @pages-конкурентов нет → анкер behavior-neutral. Стиль единообразен с .flashcard-
   grade-* (та же суть — самооценка после ответа); выбранную кнопку (quiz/interview.js: .selected +
   disabled на все) не гасим — accent-wash, прочие disabled приглушаются. Внешний вид
   частично доминирует pixel-fx (@pixel > @pages) — переезд его не трогает.
