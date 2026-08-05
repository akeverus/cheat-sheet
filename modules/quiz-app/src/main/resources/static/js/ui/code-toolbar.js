import { icon } from '../core/dom.js';

let codeToolbarBound = false;
let copyCodeBound = false;

// Общий visually-hidden aria-live регион для озвучки результата копирования кода
// (используется и toolbar-кнопкой, и плавающей code-copy-btn). WCAG 4.1.3.
function announceCopyStatus(msg) {
  let region = document.getElementById('code-copy-status');
  if (!region) {
    region = document.createElement('div');
    region.id = 'code-copy-status';
    region.className = 'visually-hidden';
    region.setAttribute('role', 'status');
    region.setAttribute('aria-live', 'polite');
    document.body.appendChild(region);
  }
  region.textContent = '';
  requestAnimationFrame(() => { region.textContent = msg; });
}

// .code-viewport ограничивает высоту листинга (pages-focus.css), а прокручиваемая
// область, до которой нельзя дойти с клавиатуры, — недостижимый контент (WCAG 2.1.1).
// Ставим tabindex/role/label ТОЛЬКО когда прокрутка реально есть: у короткого
// сниппета лишний tab-stop перед вариантами ответа не нужен. Пересчитывается при
// переносе строк (меняет высоту) и на resize.
function syncCodeViewportFocusability(viewport) {
  if (!viewport) return;
  const scrolls = viewport.scrollHeight > viewport.clientHeight + 1;
  if (scrolls) {
    viewport.setAttribute('tabindex', '0');
    viewport.setAttribute('role', 'region');
    viewport.setAttribute('aria-label', 'Код к вопросу, прокручиваемая область');
  } else {
    viewport.removeAttribute('tabindex');
    viewport.removeAttribute('role');
    viewport.removeAttribute('aria-label');
  }
}

export function initCodeToolbar() {
  if (codeToolbarBound) return;
  codeToolbarBound = true;
  document.querySelectorAll('.question-code-wrap[data-content-type="CODE"]').forEach((wrap) => {
    if (wrap.getAttribute('data-code-toolbar') === 'ready') return;
    wrap.setAttribute('data-code-toolbar', 'ready');
    const pre = wrap.querySelector('pre.question-code');
    if (!pre) return;
    const viewport = wrap.querySelector('.code-viewport');
    syncCodeViewportFocusability(viewport);
    window.addEventListener('resize', () => syncCodeViewportFocusability(viewport));
    wrap.querySelectorAll('[data-code-action]').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const action = btn.getAttribute('data-code-action');
        if (action === 'wrap') {
          const on = wrap.classList.toggle('is-code-wrap');
          btn.setAttribute('aria-pressed', on ? 'true' : 'false');
          syncCodeViewportFocusability(viewport);
        } else if (action === 'copy') {
          const codeEl = pre.querySelector('code') || pre;
          try {
            await navigator.clipboard.writeText(codeEl.innerText);
            const prev = btn.textContent;
            btn.textContent = 'Скопировано';
            announceCopyStatus('Код скопирован в буфер обмена');
            setTimeout(() => { btn.textContent = prev; }, 1600);
          } catch (_) {
            announceCopyStatus('Не удалось скопировать код');
          }
        } else if (action === 'fullscreen') {
          if (!document.fullscreenElement && wrap.requestFullscreen) wrap.requestFullscreen();
          else if (document.exitFullscreen) document.exitFullscreen();
        }
      });
    });
  });
}

export function initCopyCode() {
  if (copyCodeBound) return;
  if (!navigator.clipboard?.writeText) return;
  copyCodeBound = true;

  const blocks = document.querySelectorAll('.markdown-content pre, pre.question-code');
  if (!blocks.length) return;

  blocks.forEach((pre) => {
    if (pre.parentElement?.classList.contains('code-copy-wrap')) return;
    // У блока со своим тулбаром (.question-code-wrap → кнопка data-code-action="copy",
    // навешивается initCodeToolbar) copy уже есть — не дублируем плавающей кнопкой.
    const toolbarWrap = pre.closest('.question-code-wrap');
    if (toolbarWrap && toolbarWrap.querySelector('[data-code-action="copy"]')) return;
    const wrap = document.createElement('div');
    wrap.className = 'code-copy-wrap';
    pre.parentNode.insertBefore(wrap, pre);
    wrap.appendChild(pre);
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'code-copy-btn';
    btn.setAttribute('aria-label', 'Копировать код');
    btn.title = 'Копировать код';
    const idle = icon('copy') + '<span class="code-copy-label">Копировать</span>';
    btn.innerHTML = idle;
    wrap.appendChild(btn);
    let resetTimer = null;
    const flash = (cls, html) => {
      btn.classList.remove('is-copied', 'is-error');
      if (cls) btn.classList.add(cls);
      btn.innerHTML = html;
      if (resetTimer) clearTimeout(resetTimer);
      resetTimer = setTimeout(() => { btn.classList.remove('is-copied', 'is-error'); btn.innerHTML = idle; }, 2000);
    };
    btn.addEventListener('click', () => {
      const codeEl = pre.querySelector('code') || pre;
      navigator.clipboard.writeText(codeEl.innerText)
        .then(() => {
          flash('is-copied', icon('check') + '<span class="code-copy-label">Скопировано</span>');
          announceCopyStatus('Код скопирован в буфер обмена');
        })
        .catch(() => {
          flash('is-error', icon('x') + '<span class="code-copy-label">Ошибка</span>');
          announceCopyStatus('Не удалось скопировать код');
        });
    });
  });
}
