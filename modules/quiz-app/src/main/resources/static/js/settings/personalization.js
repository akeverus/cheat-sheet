let uiBound = false;

function announceSaved(label, value) {
  const statusEl = document.getElementById('personalization-status');
  if (!statusEl) return;
  statusEl.textContent = 'Сохранено на этом устройстве: ' + label + ' — ' + value + '.';
  statusEl.classList.remove('hidden');
}

function wireSegControl(controlId, attr, label, getCurrent, setValue, eventName) {
  const ctl = document.getElementById(controlId);
  if (!ctl) return;
  const btns = Array.from(ctl.querySelectorAll('[' + attr + ']'));
  if (!btns.length) return;
  const sync = () => {
    const cur = getCurrent();
    btns.forEach((b) => {
      const on = b.getAttribute(attr) === cur;
      b.setAttribute('aria-checked', on ? 'true' : 'false');
      b.classList.toggle('is-active', on);
      b.tabIndex = on ? 0 : -1;
    });
  };
  const apply = (button) => {
    const next = button.getAttribute(attr);
    if (getCurrent() === next) return;
    setValue(next);
    announceSaved(label, button.textContent.trim());
  };
  btns.forEach((b, i) => {
    b.addEventListener('click', () => apply(b));
    b.addEventListener('keydown', (e) => {
      let idx = -1;
      if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % btns.length;
      else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + btns.length) % btns.length;
      if (idx < 0) return;
      e.preventDefault();
      apply(btns[idx]);
      btns[idx].focus();
    });
  });
  if (eventName) document.addEventListener(eventName, sync);
  sync();
}

/** Settings UI for theme-adjacent prefs (card on /settings). */
export function initPersonalization() {
  if (uiBound) return;
  const card = document.getElementById('personalization-card');
  if (!card || !window.__theme || !window.__fontScale) return;
  uiBound = true;

  card.classList.remove('hidden');

  wireSegControl('theme-pref-control', 'data-theme-pref', 'Тема оформления',
    () => window.__theme.pref(), (v) => window.__theme.set(v), 'themechange');
  if (window.__motion) {
    wireSegControl('motion-pref-control', 'data-motion-pref', 'Движение',
      () => window.__motion.current(), (v) => window.__motion.set(v), 'motionchange');
  }
  // Три «вкусовые» оси (ширина чтения, плотность, пиксель-эффекты) заменены одним
  // вопросом «под какой экран»: таблица сочетаний живёт в бутстрапе head.html, и
  // второй её копии здесь нет — модуль только связывает контрол с уже готовым API.
  if (window.__screenPreset) {
    wireSegControl('screen-preset-control', 'data-screen-preset', 'Под экран',
      () => window.__screenPreset.current(), (v) => window.__screenPreset.set(v), 'screenpresetchange');
  }

  const valEl = document.getElementById('font-scale-value');
  const decBtn = document.getElementById('font-decrease');
  const incBtn = document.getElementById('font-increase');
  const resetBtn = document.getElementById('font-reset');
  const fs = window.__fontScale;
  const announceFontScale = () => announceSaved('Размер шрифта', Math.round(fs.get() * 100) + '%');
  const syncFont = () => {
    const s = fs.get();
    if (valEl) valEl.textContent = Math.round(s * 100) + '%';
    if (decBtn) decBtn.setAttribute('aria-disabled', s <= fs.min ? 'true' : 'false');
    if (incBtn) incBtn.setAttribute('aria-disabled', s >= fs.max ? 'true' : 'false');
    if (resetBtn) resetBtn.setAttribute('aria-disabled', s === 1 ? 'true' : 'false');
  };
  if (decBtn) decBtn.addEventListener('click', () => {
    if (decBtn.getAttribute('aria-disabled') !== 'true') { fs.stepBy(-fs.STEP); announceFontScale(); }
  });
  if (incBtn) incBtn.addEventListener('click', () => {
    if (incBtn.getAttribute('aria-disabled') !== 'true') { fs.stepBy(fs.STEP); announceFontScale(); }
  });
  if (resetBtn) resetBtn.addEventListener('click', () => {
    if (resetBtn.getAttribute('aria-disabled') !== 'true') { fs.reset(); announceFontScale(); }
  });
  document.addEventListener('fontscalechange', syncFont);
  syncFont();
}
