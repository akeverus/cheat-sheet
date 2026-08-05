/**
 * Оболочка страницы: переключатель темы и кнопка «наверх».
 *
 * Оба контрола — прогрессивное улучшение, поэтому и живут в JS: без него тоггл
 * остаётся `hidden` (сервер рендерит его скрытым), а кнопки «наверх» просто нет.
 */
import { motionReduced } from '../core/dom.js';

/**
 * Тоггл темы: раскрывает скрытые кнопки и держит имя в согласии с текущей темой.
 *
 * Кнопок несколько (десктопный topbar и мобильная шапка), состояние у них общее —
 * поэтому синхронизируем разом и по клику, и по событию `themechange`, которое
 * шлёт inline-бутстрап темы (он же владеет `window.__theme`).
 *
 * Это кнопка-действие, а не кнопка-состояние, поэтому `aria-pressed` тут нет.
 * Светлая и тёмная — равноправные режимы, а не «включено/выключено»: у «нажата»
 * не было бы однозначного смысла. Раньше атрибут стоял вместе с меняющимся
 * именем, и в светлой теме скринридер читал «Включить тёмную тему, нажата» —
 * имя обещало включить тёмную, состояние утверждало, что она уже включена.
 * Осталось одно: имя описывает, что произойдёт по нажатию.
 *
 * `title` синхронизируем тем же текстом — иначе всплывающая подсказка мыши и
 * имя для скринридера рассказывали бы про кнопку разное.
 */
function initThemeToggles() {
  const toggles = document.querySelectorAll('[data-theme-toggle]');
  if (!toggles.length || !window.__theme) return;

  const sync = () => {
    const action = window.__theme.resolved() === 'light'
      ? 'Включить тёмную тему'
      : 'Включить светлую тему';
    toggles.forEach((btn) => {
      btn.classList.remove('hidden');
      btn.removeAttribute('aria-pressed');
      btn.setAttribute('aria-label', action);
      btn.setAttribute('title', action);
    });
  };

  sync();
  toggles.forEach((btn) => btn.addEventListener('click', () => window.__theme.toggle()));
  document.addEventListener('themechange', sync);
}

/**
 * Кнопка «наверх»: появляется после ~1.5 экрана прокрутки.
 *
 * Порог относительный, а не фиксированные 400px: на длинной странице абсолютный
 * порог срабатывает почти сразу и кнопка висит всё время. Прокрутка обработана
 * через rAF-тик и переключает только класс — на скролл не приходится ни одного
 * пересчёта стилей сверх этого. После прыжка наверх уводим фокус на начало
 * контента: иначе он остался бы на кнопке внизу, и Tab продолжил бы с конца.
 */
function initBackToTop() {
  const btn = document.createElement('button');
  btn.type = 'button';
  btn.className = 'back-to-top';
  btn.setAttribute('aria-label', 'Наверх');
  btn.setAttribute('title', 'Наверх');
  btn.innerHTML = '<svg class="icon" aria-hidden="true"><use href="#arrow-up"></use></svg>';
  document.body.appendChild(btn);

  let shown = false;
  let ticking = false;
  const update = () => {
    ticking = false;
    const should = window.pageYOffset > Math.max(400, window.innerHeight * 1.5);
    if (should === shown) return;
    shown = should;
    btn.classList.toggle('is-visible', should);
  };

  window.addEventListener('scroll', () => {
    if (ticking) return;
    ticking = true;
    window.requestAnimationFrame(update);
  }, { passive: true });

  btn.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: motionReduced() ? 'auto' : 'smooth' });
    const anchor = document.getElementById('main-content') || document.querySelector('h1, h2');
    if (!anchor) return;
    anchor.setAttribute('tabindex', '-1');
    anchor.focus({ preventScroll: true });
  });

  update();
}

export function initShell() {
  initThemeToggles();
  initBackToTop();
}
