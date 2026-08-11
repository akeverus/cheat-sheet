import { initTablist } from '../ui/tablist.js';

let tabsBound = false;

/** Вкладки /settings: Сессия / Оформление / Данные. Выбор переживает перезагрузку. */
export function initSettingsTabs() {
  if (tabsBound) return;
  const tablist = document.getElementById('settings-tablist');
  const activated = initTablist(tablist, { storageKey: 'settingsTab' });
  if (activated) tabsBound = true;
}
