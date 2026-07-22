/**
 * Single Mermaid renderer for SSR fragments and dynamic AJAX content.
 * Exposes window.initMermaid / window.renderAllMermaid for legacy hooks.
 */

let mermaidReady = false;
let themeRerenderTimer = null;
let toolbarDelegated = false;
const THEME_RERENDER_MS = 120;
/** Per-diagram pan/zoom state; survives theme re-render without rebinding listeners. */
const diagramUiState = new WeakMap();

function readThemeVars() {
  const mmStyle = getComputedStyle(document.documentElement);
  const mmTok = (name, fb) => (mmStyle.getPropertyValue(name) || '').trim() || fb;
  return {
    darkMode: document.documentElement.getAttribute('data-theme') !== 'light',
    background: mmTok('--color-bg-secondary', '#0d1b22'),
    fontFamily: mmTok('--font-family-body', 'Inter, system-ui, sans-serif'),
    primaryColor: mmTok('--color-surface-soft', '#102029'),
    primaryTextColor: mmTok('--color-text-primary', '#eef6f5'),
    primaryBorderColor: mmTok('--color-border-strong', '#335463'),
    secondaryColor: mmTok('--color-bg-tertiary', '#12252e'),
    secondaryTextColor: mmTok('--color-text-primary', '#eef6f5'),
    secondaryBorderColor: mmTok('--color-border-strong', '#335463'),
    tertiaryColor: mmTok('--color-bg-tertiary', '#12252e'),
    tertiaryTextColor: mmTok('--color-text-primary', '#eef6f5'),
    tertiaryBorderColor: mmTok('--color-border-strong', '#335463'),
    mainBkg: mmTok('--color-surface-soft', '#102029'),
    nodeBorder: mmTok('--color-border-strong', '#335463'),
    nodeTextColor: mmTok('--color-text-primary', '#eef6f5'),
    lineColor: mmTok('--color-text-tertiary', '#8397a0'),
    edgeLabelBackground: mmTok('--color-bg-secondary', '#0d1b22'),
    clusterBkg: mmTok('--color-bg-secondary', '#0d1b22'),
    clusterBorder: mmTok('--color-border-strong', '#335463'),
    titleColor: mmTok('--color-text-primary', '#eef6f5'),
    labelColor: mmTok('--color-text-primary', '#eef6f5'),
    labelTextColor: mmTok('--color-text-primary', '#eef6f5'),
    textColor: mmTok('--color-text-secondary', '#a8b9bd'),
    accent: mmTok('--color-accent-primary', '#39d0c7')
  };
}

function stripScriptsFromSvg(svg) {
  if (!svg || svg.indexOf('<script') < 0) return svg;
  try {
    const doc = new DOMParser().parseFromString(svg, 'image/svg+xml');
    doc.querySelectorAll('script').forEach((node) => node.remove());
    return new XMLSerializer().serializeToString(doc.documentElement);
  } catch (_) {
    return svg.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
  }
}

function scopedCleanupMermaidArtifacts(root) {
  const scope = root && root.querySelectorAll ? root : document;
  scope.querySelectorAll('[id^="dmmd-"]').forEach((n) => n.remove());
  scope.querySelectorAll('.error-icon').forEach((n) => {
    const p = n.closest('div');
    if (p) p.remove();
  });
}

export function failDiagram(el) {
  const qd = el.closest('.question-diagram');
  const details = qd ? qd.querySelector('.diagram-source-details') : null;
  if (qd && details) {
    qd.setAttribute('data-render-failed', 'true');
    el.textContent = '';
    details.open = true;
  } else if (qd) {
    qd.remove();
  } else {
    el.remove();
  }
}

function applySvgToElement(el, svgString) {
  const parsed = new DOMParser().parseFromString(svgString, 'text/html');
  const svg = parsed.body.querySelector('svg');
  if (!svg) return false;
  svg.removeAttribute('height');
  svg.style.width = '100%';
  svg.style.height = 'auto';
  svg.style.maxHeight = '100%';
  el.replaceChildren(document.importNode(svg, true));
  return true;
}

export function initMermaid() {
  if (typeof mermaid === 'undefined') return;
  mermaid.initialize({
    startOnLoad: false,
    securityLevel: 'strict',
    theme: 'base',
    themeVariables: readThemeVars()
  });
  mermaidReady = true;
}

export async function renderAllMermaid(force, root) {
  if (typeof mermaid === 'undefined') return;
  if (!mermaidReady) initMermaid();

  const scope = root && root.querySelectorAll ? root : document;
  const els = scope.querySelectorAll('.mermaid');

  for (const el of els) {
    if (!force && el.getAttribute('data-rendered') === 'true') continue;
    let source = el.getAttribute('data-mermaid-source');
    if (!source) {
      source = el.textContent;
      el.setAttribute('data-mermaid-source', source);
    }
    try {
      const id = 'mmd-' + Math.random().toString(36).slice(2);
      const res = await mermaid.render(id, source);
      const safeSvg = stripScriptsFromSvg(res.svg);
      if (applySvgToElement(el, safeSvg)) {
        el.setAttribute('data-rendered', 'true');
        const qd = el.closest('.question-diagram');
        if (qd) qd.removeAttribute('data-render-failed');
      } else {
        failDiagram(el);
      }
    } catch (_) {
      scopedCleanupMermaidArtifacts(el.closest('.question-diagram') || el.parentElement || el);
      failDiagram(el);
    }
  }
  wireDiagramToolbar(scope);
}

export function wireDiagramToolbar(root) {
  const scope = root && root.querySelectorAll ? root : document;

  // One-time document delegation — theme re-render must not multiply handlers (P1).
  if (!toolbarDelegated) {
    toolbarDelegated = true;
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('[data-diagram-action]');
      if (!btn) return;
      const qd = btn.closest('.question-diagram');
      if (!qd) return;
      const action = btn.getAttribute('data-diagram-action');
      const state = ensureDiagramState(qd);
      if (action === 'zoom-in') {
        state.scale = Math.min(2.5, Math.round((state.scale + 0.15) * 100) / 100);
        applyDiagramTransform(qd, state);
      } else if (action === 'zoom-out') {
        state.scale = Math.max(0.5, Math.round((state.scale - 0.15) * 100) / 100);
        applyDiagramTransform(qd, state);
      } else if (action === 'fit') {
        state.scale = 1;
        state.panX = 0;
        state.panY = 0;
        applyDiagramTransform(qd, state);
      } else if (action === 'fullscreen') {
        if (!document.fullscreenElement && qd.requestFullscreen) qd.requestFullscreen();
        else if (document.exitFullscreen) document.exitFullscreen();
      }
    });
    document.addEventListener('keydown', (e) => {
      if (e.key !== 'Enter' && e.key !== ' ') return;
      const btn = e.target.closest('[data-diagram-action]');
      if (!btn) return;
      e.preventDefault();
      btn.click();
    });
  }

  scope.querySelectorAll('.question-diagram').forEach((qd) => {
    ensureDiagramState(qd);
    qd.querySelectorAll('[data-diagram-action]').forEach((btn) => {
      if (!btn.hasAttribute('tabindex')) btn.setAttribute('tabindex', '0');
      const actionLabels = {
        'zoom-in': 'Увеличить диаграмму',
        'zoom-out': 'Уменьшить диаграмму',
        fit: 'Вписать диаграмму',
        fullscreen: 'Диаграмма на весь экран'
      };
      const action = btn.getAttribute('data-diagram-action');
      if (action && actionLabels[action] && !btn.getAttribute('aria-label')) {
        btn.setAttribute('aria-label', actionLabels[action]);
      }
    });

    if (qd.getAttribute('data-pan-ready') === 'true') return;
    qd.setAttribute('data-pan-ready', 'true');
    const viewport = qd.querySelector('.diagram-viewport') || qd;
    let dragging = false;
    let startX = 0;
    let startY = 0;
    let originX = 0;
    let originY = 0;

    viewport.addEventListener('pointerdown', (e) => {
      if (e.button !== 0) return;
      if (e.target.closest('button, a, summary, details')) return;
      const state = ensureDiagramState(qd);
      dragging = true;
      startX = e.clientX;
      startY = e.clientY;
      originX = state.panX;
      originY = state.panY;
      viewport.classList.add('is-panning');
      try { viewport.setPointerCapture(e.pointerId); } catch (_) {}
    });
    viewport.addEventListener('pointermove', (e) => {
      if (!dragging) return;
      const state = ensureDiagramState(qd);
      state.panX = originX + (e.clientX - startX);
      state.panY = originY + (e.clientY - startY);
      applyDiagramTransform(qd, state);
    });
    function endPan(e) {
      if (!dragging) return;
      dragging = false;
      viewport.classList.remove('is-panning');
      try { viewport.releasePointerCapture(e.pointerId); } catch (_) {}
    }
    viewport.addEventListener('pointerup', endPan);
    viewport.addEventListener('pointercancel', endPan);
  });
}

function ensureDiagramState(qd) {
  let state = diagramUiState.get(qd);
  if (!state) {
    state = { scale: 1, panX: 0, panY: 0 };
    diagramUiState.set(qd, state);
  }
  return state;
}

function applyDiagramTransform(qd, state) {
  const viewport = qd.querySelector('.diagram-viewport') || qd;
  viewport.setAttribute('data-diagram-scale', String(state.scale));
  const svg = qd.querySelector('.mermaid svg');
  if (svg) {
    svg.style.transform = 'translate(' + state.panX + 'px,' + state.panY + 'px) scale(' + state.scale + ')';
    svg.style.transformOrigin = 'center top';
  }
}

function scheduleThemeRerender() {
  clearTimeout(themeRerenderTimer);
  themeRerenderTimer = setTimeout(() => {
    initMermaid();
    document.querySelectorAll('.mermaid').forEach((el) => {
      if (!el.getAttribute('data-mermaid-source') && el.textContent) {
        el.setAttribute('data-mermaid-source', el.textContent);
      }
      el.removeAttribute('data-rendered');
    });
    // Keep data-pan-ready / delegation — do not rebind listeners on theme change.
    renderAllMermaid(true);
  }, THEME_RERENDER_MS);
}

function bootMermaidModule() {
  if (typeof mermaid === 'undefined') {
    document.addEventListener('DOMContentLoaded', () => {
      document.querySelectorAll('.mermaid').forEach(failDiagram);
    });
    return;
  }

  initMermaid();
  window.initMermaid = initMermaid;
  window.renderAllMermaid = renderAllMermaid;

  document.addEventListener('DOMContentLoaded', () => renderAllMermaid(false));
  document.addEventListener('themechange', scheduleThemeRerender);

  if (typeof MutationObserver !== 'undefined') {
    let lastTheme = document.documentElement.getAttribute('data-theme');
    new MutationObserver(() => {
      const t = document.documentElement.getAttribute('data-theme');
      if (t !== lastTheme) {
        lastTheme = t;
        scheduleThemeRerender();
      }
    }).observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
  }
}

bootMermaidModule();
