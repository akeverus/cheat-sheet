/**
 * Единственная реализация ARIA-паттерна tablist с roving tabindex.
 *
 * В проекте три группы вкладок — /settings, /stats и материалы вопроса. Различаются
 * они только тем, где хранится выбор и что происходит после активации; клавиатура
 * (←→↑↓, Home, End), aria-selected, tabIndex и переключение панелей у них общие.
 * Три копии этой логики расходились: в одной был deep-link по hash, в другой —
 * localStorage, в третьей ни того ни другого, и правка ARIA доезжала не везде.
 *
 * PE: разметка приходит от сервера развёрнутой — все панели в потоке, первая
 * помечена `is-active`. Что видно, решает CSS по РОЛИ (`[role="tabpanel"]`) под
 * гейтом `[data-js]`, который ставит pre-paint скрипт head, — то есть уже в
 * первом кадре. Раньше это делали три разных класса-маркера, и вешал их этот
 * модуль после загрузки роутового чанка: на /stats вкладочное состояние
 * приезжало через 149 мс после отрисовки (iter-80).
 */

/**
 * @param {HTMLElement|null} tablist контейнер с [role="tab"]
 * @param {object} [options]
 * @param {string} [options.storageKey] область запоминания выбора; под ключом
 *        `tabPanel:<storageKey>` лежит ID ПАНЕЛИ — его же читает pre-paint
 *        скрипт head, которому ID вкладки ничего не сказал бы
 * @param {boolean} [options.syncHash] держать выбранную вкладку в location.hash
 * @param {string} [options.defaultHash] hash, который означает «первая вкладка» и в URL не пишется
 * @param {(index: number) => void} [options.onActivate] побочный эффект после переключения
 * @returns {((index: number) => void)|null} activate или null, если разметка не подошла
 */
export function initTablist(tablist, options = {}) {
  if (!tablist) return null;
  const tabs = Array.from(tablist.querySelectorAll('[role="tab"]'));
  if (!tabs.length) return null;
  const panels = tabs.map((t) => document.getElementById(t.getAttribute('aria-controls')));
  if (panels.some((p) => !p)) return null;

  const { storageKey, syncHash, defaultHash, onActivate } = options;
  const storeKey = storageKey ? `tabPanel:${storageKey}` : null;

  const hashOf = (index) => (tabs[index].getAttribute('data-tab-hash') || '').toLowerCase();

  const indexByHash = (raw) => {
    const hash = (raw || '').replace(/^#/, '').toLowerCase();
    if (!hash) return -1;
    return tabs.findIndex((_, i) => hashOf(i) === hash);
  };

  const activate = (index, { focusTab = false, writeHash = false } = {}) => {
    tabs.forEach((tab, i) => {
      const on = i === index;
      tab.setAttribute('aria-selected', on ? 'true' : 'false');
      tab.tabIndex = on ? 0 : -1;
      panels[i].classList.toggle('is-active', on);
    });
    if (focusTab) tabs[index].focus();
    if (onActivate) onActivate(index);
    if (storeKey) {
      try { localStorage.setItem(storeKey, panels[index].id); } catch { /* приватный режим */ }
    }
    if (writeHash && syncHash && history.replaceState) {
      const hash = hashOf(index);
      const url = hash && hash !== defaultHash
        ? `#${hash}`
        : location.pathname + location.search;
      try { history.replaceState(null, '', url); } catch { /* about:blank и подобное */ }
    }
  };

  tabs.forEach((tab, i) => {
    tab.addEventListener('click', () => activate(i, { writeHash: true }));
    tab.addEventListener('keydown', (event) => {
      let next = -1;
      if (event.key === 'ArrowRight' || event.key === 'ArrowDown') next = (i + 1) % tabs.length;
      else if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') next = (i - 1 + tabs.length) % tabs.length;
      else if (event.key === 'Home') next = 0;
      else if (event.key === 'End') next = tabs.length - 1;
      if (next < 0) return;
      event.preventDefault();
      activate(next, { focusTab: true, writeHash: true });
    });
  });

  if (syncHash) {
    window.addEventListener('hashchange', () => {
      const fromHash = indexByHash(location.hash);
      if (fromHash >= 0) activate(fromHash);
    });
  }

  let initial = syncHash ? indexByHash(location.hash) : -1;
  if (initial < 0 && storeKey) {
    try {
      const saved = localStorage.getItem(storeKey);
      initial = panels.findIndex((p) => p.id === saved);
      // Ключ прежнего формата хранил ID ВКЛАДКИ. Значение по нему больше не
      // читается (findIndex по панелям вернёт -1 → первая вкладка), но лежать
      // в хранилище ему незачем.
      localStorage.removeItem(storageKey);
    } catch { /* приватный режим */ }
  }
  activate(Math.max(0, initial));
  return activate;
}
