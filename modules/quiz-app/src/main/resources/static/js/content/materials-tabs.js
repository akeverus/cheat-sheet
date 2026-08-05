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
    const activated = initTablist(tablist, {
      reveal() {
        materials.classList.add('js-content-tabs');
        tablist.hidden = false;
      }
    });
    if (!activated) return;
    materials.setAttribute('data-content-tabs-ready', 'true');
    // Пока вкладки не разведены, обе панели видны — а ленивая отрисовка схемы
    // смотрит именно на видимость. Модуль вкладок грузится динамически (роутовый
    // чанк), поэтому окно «оба блока раскрыты» реально существует, и без сигнала
    // схема успевала утянуть mermaid для закрытой вкладки. Событие вместо импорта:
    // рендерер есть не на всех страницах с вкладками.
    document.dispatchEvent(new CustomEvent('contenttabsready', { detail: { root: materials } }));
  });
}
