/** highlight.js helper for dynamically inserted content. */

export function highlightContainer(container) {
  if (!container || typeof hljs === 'undefined') return;
  container.querySelectorAll('pre code').forEach((block) => {
    try { hljs.highlightElement(block); } catch (_) { /* non-critical */ }
  });
}

export function highlightPageCodeBlocks() {
  if (typeof hljs === 'undefined') return;
  document.querySelectorAll('.markdown-content pre code').forEach((block) => {
    try { hljs.highlightElement(block); } catch (_) { /* non-critical */ }
  });
}
