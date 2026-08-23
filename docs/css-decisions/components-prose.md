# components-prose.css — досье решений (ARCH-02)

Полные доводы и история правил, вынесенные из комментариев `components-prose.css`
(REF-009): эссе читают при ПЕРЕСМОТРЕ решения, а не при каждом чтении файла.
У правила в CSS остаётся указатель с тезисом; якоря ниже — цель указателей.
Рабочие инварианты («НЕ менять», пороги) остаются в CSS.

<a id="components-prose-e01"></a>
## components-prose-e01 · components-prose.css:1

**Контекст:** `@layer components {`

@layer components — прозо-поверхность отрендеренного markdown (.markdown-content).
   Вынесено из pages-focus.css при page-scope focus (Волна 3, D3e): кластер
   ГЛОБАЛЬНЫЙ — markdown-тело рендерится и на .focus-page (вопрос/ответ), и на
   no-JS result.html; жил в page-файле focus и пропал бы на result при скоупе.

   Каскад-безопасно (замкнутый набор, D2a-правило): все .markdown-content-правила
   были в pages-focus @pages; единственный сосед — reset-layout.css
   `.markdown-content :not(pre) > code{color}` (@reset, инлайн-код) — другое
   свойство, @reset НИЖЕ components (кластер выигрывает и как @pages, и как
   @components). Конкурентов на .markdown-content* в @components/@shell/@utilities/
   @pixel нет → нечему оживать (D3b). Focus-only части смешанного selector-list
   (.focus-question / #interview-options label { overflow-wrap }) и .question-diagram
   оставлены в pages-focus.
