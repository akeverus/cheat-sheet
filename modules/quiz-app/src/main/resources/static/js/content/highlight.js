/** highlight.js helper for dynamically inserted content. */

export function highlightContainer(container) {
  if (!container || typeof hljs === 'undefined') return;
  container.querySelectorAll('pre code').forEach((block) => {
    try { hljs.highlightElement(block); } catch (_) { /* non-critical */ }
  });
}

// `.question-code` — панель «Пример кода», по которой отвечают на вопрос. Она лежит
// вне `.markdown-content` и потому оставалась единственным неподсвеченным листингом
// на странице: highlight.js грузился (~120 КБ) и раскрашивал только разбор ответа.
// Схема из псевдографики сюда не попадает намеренно — там `<pre>` без `<code>`,
// и подсветка раскрасила бы слова внутри рисунка.
const PAGE_CODE_SELECTOR = '.markdown-content pre code, .question-code code';

export function highlightPageCodeBlocks() {
  if (typeof hljs === 'undefined') return;
  document.querySelectorAll(PAGE_CODE_SELECTOR).forEach((block) => {
    try { hljs.highlightElement(block); } catch (_) { /* non-critical */ }
  });
}
