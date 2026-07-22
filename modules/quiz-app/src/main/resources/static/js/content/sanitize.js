const ALLOWED_TAGS = new Set([
  'P', 'BR', 'STRONG', 'EM', 'B', 'I', 'UL', 'OL', 'LI', 'CODE', 'PRE', 'A', 'BLOCKQUOTE',
  'H1', 'H2', 'H3', 'H4', 'TABLE', 'THEAD', 'TBODY', 'TR', 'TH', 'TD', 'HR', 'DIV', 'SPAN'
]);
const CLASS_PRESERVE_TAGS = new Set(['DIV', 'SPAN', 'CODE', 'PRE']);

function sanitizeNode(node) {
  if (node.nodeType === Node.TEXT_NODE) {
    return document.createTextNode(node.textContent || '');
  }
  if (node.nodeType !== Node.ELEMENT_NODE) {
    return document.createTextNode('');
  }

  const tagName = node.tagName.toUpperCase();
  if (!ALLOWED_TAGS.has(tagName)) {
    const fragment = document.createDocumentFragment();
    Array.from(node.childNodes).forEach((child) => fragment.appendChild(sanitizeNode(child)));
    return fragment;
  }

  const clean = document.createElement(tagName.toLowerCase());
  if (tagName === 'A') {
    const href = node.getAttribute('href') || '';
    const ok = /^(https?:)/i.test(href)
      || (href.startsWith('/') && !href.startsWith('//'))
      || href.startsWith('#');
    if (ok) clean.setAttribute('href', href);
    clean.setAttribute('rel', 'noopener noreferrer');
  }
  if (CLASS_PRESERVE_TAGS.has(tagName)) {
    const cls = node.getAttribute('class');
    if (cls) clean.setAttribute('class', cls);
  }
  Array.from(node.childNodes).forEach((child) => clean.appendChild(sanitizeNode(child)));
  return clean;
}

export function sanitizeToFragment(html) {
  const fragment = document.createDocumentFragment();
  if (!html) return fragment;
  const parsed = new DOMParser().parseFromString(String(html), 'text/html');
  Array.from(parsed.body.childNodes).forEach((child) => fragment.appendChild(sanitizeNode(child)));
  return fragment;
}

export function sanitizeHtml(html) {
  const wrapper = document.createElement('div');
  wrapper.appendChild(sanitizeToFragment(html));
  return wrapper.innerHTML;
}
