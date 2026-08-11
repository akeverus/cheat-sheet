import { initTablist } from '../ui/tablist.js';

/**
 * Вкладки материалов вопроса: «Пример кода» / «Схема».
 *
 * Живёт рядом с highlight.js и mermaid-renderer.js, потому что это часть
 * отрисовки контента вопроса, а не настроек. Раньше функция лежала в
 * settings/tabs.js — по соседству с вкладками /settings, с которыми у неё общий
 * только `initTablist`. Из-за этого она попадала в чанк, который грузится
 * исключительно на `body.settings-page`, где `.question-materials` не бывает
 * никогда: вкладки не активировались ни на одной странице, и пользователь всегда
 * видел no-JS-стопку из двух развёрнутых панелей.
 *
 * Блоков на странице может быть несколько, поэтому выбор не запоминается:
 * «активная вкладка» здесь — свойство карточки, а не пользователя.
 */
export function initContentTabs() {
  document.querySelectorAll('.question-materials').forEach((materials) => {
    if (materials.getAttribute('data-content-tabs-ready') === 'true') return;
    const tablist = materials.querySelector('.content-tablist');
    const activated = initTablist(tablist);
    if (!activated) return;
    materials.setAttribute('data-content-tabs-ready', 'true');
    // Ленивая отрисовка схемы смотрит на видимость панели. С iter-80 лишние
    // панели гасит CSS ещё до первого кадра, так что окна «оба блока раскрыты»
    // больше нет, — но событие остаётся: рендерер ждёт именно момента, когда
    // активная вкладка уже выбрана (она может быть и «Схемой»), а не момента
    // загрузки модуля. Событие вместо импорта: рендерер есть не на всех
    // страницах с вкладками.
    document.dispatchEvent(new CustomEvent('contenttabsready', { detail: { root: materials } }));
  });
}
