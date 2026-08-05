import { highlightContainer } from './highlight.js';
import { observeDiagrams } from './mermaid-renderer.js';
import { enhanceScrollableTables } from './table-scroll.js';

/** Hydrate hljs + Mermaid inside dynamically inserted answer content. */
export function renderDynamicContent(container) {
  if (!container) return;
  highlightContainer(container);
  // Не рендерим сразу: схема в свежем ответе может лежать ниже сгиба или в закрытой
  // вкладке — ставим её в ту же очередь по видимости, что и SSR-схемы.
  observeDiagrams(container);
  enhanceScrollableTables(container);
}
