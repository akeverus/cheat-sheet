import { pluralRu } from '../core/dom.js';

let tabsBound = false;

export function initSettingsTabs() {
  if (tabsBound) return;
  const tablist = document.getElementById('settings-tablist');
  if (!tablist) return;
  const tabs = Array.from(tablist.querySelectorAll('[role="tab"]'));
  if (!tabs.length) return;
  const panels = tabs.map((t) => document.getElementById(t.getAttribute('aria-controls')));
  if (panels.some((p) => !p)) return;

  tabsBound = true;
  document.body.classList.add('js-tabs');
  tablist.classList.remove('hidden');

  const STORAGE_KEY = 'settingsTab';
  const activate = (idx, focusTab) => {
    tabs.forEach((t, i) => {
      const on = i === idx;
      t.setAttribute('aria-selected', on ? 'true' : 'false');
      t.tabIndex = on ? 0 : -1;
      panels[i].classList.toggle('is-active', on);
    });
    if (focusTab) tabs[idx].focus();
    try { localStorage.setItem(STORAGE_KEY, tabs[idx].id); } catch (_) { /* private mode */ }
  };

  tabs.forEach((tab, i) => {
    tab.addEventListener('click', () => activate(i, false));
    tab.addEventListener('keydown', (e) => {
      let idx = -1;
      if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % tabs.length;
      else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + tabs.length) % tabs.length;
      else if (e.key === 'Home') idx = 0;
      else if (e.key === 'End') idx = tabs.length - 1;
      if (idx < 0) return;
      e.preventDefault();
      activate(idx, true);
    });
  });

  let initial = 0;
  try {
    const savedIdx = tabs.findIndex((t) => t.id === localStorage.getItem(STORAGE_KEY));
    if (savedIdx >= 0) initial = savedIdx;
  } catch (_) { /* private mode */ }
  activate(initial, false);
}

export function initContentTabs() {
  document.querySelectorAll('.question-materials').forEach((materials) => {
    if (materials.getAttribute('data-content-tabs-ready') === 'true') return;
    const tablist = materials.querySelector('.content-tablist');
    if (!tablist) return;
    const tabs = Array.from(tablist.querySelectorAll('[role="tab"]'));
    if (!tabs.length) return;
    const panels = tabs.map((t) => document.getElementById(t.getAttribute('aria-controls')));
    if (panels.some((p) => !p)) return;

    materials.setAttribute('data-content-tabs-ready', 'true');
    materials.classList.add('js-content-tabs');
    tablist.hidden = false;

    const activate = (idx, focusTab) => {
      tabs.forEach((t, i) => {
        const on = i === idx;
        t.setAttribute('aria-selected', on ? 'true' : 'false');
        t.tabIndex = on ? 0 : -1;
        panels[i].classList.toggle('is-active', on);
      });
      if (focusTab) tabs[idx].focus();
    };

    tabs.forEach((tab, i) => {
      tab.addEventListener('click', () => activate(i, false));
      tab.addEventListener('keydown', (e) => {
        let idx = -1;
        if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % tabs.length;
        else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + tabs.length) % tabs.length;
        else if (e.key === 'Home') idx = 0;
        else if (e.key === 'End') idx = tabs.length - 1;
        if (idx < 0) return;
        e.preventDefault();
        activate(idx, true);
      });
    });

    activate(0, false);
  });
}
