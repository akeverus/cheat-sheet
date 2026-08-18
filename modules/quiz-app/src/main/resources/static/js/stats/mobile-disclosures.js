/**
 * Свернуть на телефоне то, что смотрят изредка.
 *
 * Витрина знаков (766px) и форма отбора (345px) на 390×844 лежат между метриками и
 * темами: мимо них проходят каждый раз, а открывают редко. В разметке оба стоят
 * `<details open>` — без скрипта страница остаётся рабочей, просто длинной; это и есть
 * прогрессивное улучшение, а не зависимость.
 *
 * Закрываем ОДИН раз при загрузке и только если человек ещё не трогал раскрытие:
 * `toggle` снимает пометку, и повторный вызов (смена ориентации, resize) уже не
 * закрывает то, что открыли руками.
 */
export function initMobileDisclosures() {
  const phone = window.matchMedia('(max-width: 767px)');
  const targets = [...document.querySelectorAll('details[data-mobile-collapsed]')];
  if (!targets.length) {
    return;
  }

  targets.forEach((details) => {
    details.addEventListener('toggle', () => {
      details.removeAttribute('data-mobile-collapsed');
    }, { once: true });
  });

  const apply = () => {
    if (!phone.matches) {
      return;
    }
    targets.forEach((details) => {
      if (details.hasAttribute('data-mobile-collapsed')) {
        details.open = false;
      }
    });
  };

  apply();
  phone.addEventListener('change', apply);
}
