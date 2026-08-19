/**
 * Рабочее место печатного режима: счёт плана, редактор кода, секундомер интервью.
 *
 * Всё здесь — прогрессивное улучшение. Без скрипта остаются: план ответа
 * (нативные флажки работают сами), поле ввода и отправка формы; исчезают только
 * счётчики и часы, которые без пересчёта показывали бы неверные числа. Поэтому
 * их разметка приходит с сервера скрытой (`hidden`), а видимой её делает этот
 * модуль — а не наоборот.
 */

/** Порог «в темпе» — типичный устный ответ на собеседовании, секунды. */
const PACE_SECONDS = 120;
/** Порог «затянуто»: дальше ответ перестают слушать. */
const OVER_SECONDS = 180;

function formatClock(totalSeconds) {
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${String(seconds).padStart(2, '0')}`;
}

/**
 * Счёт отмеченных пунктов плана. Число выводится словами и цифрами в одном
 * месте — рядом с планом, а не в шапке страницы: это состояние работы, а не
 * состояние сессии.
 */
function initPlanCount() {
  const plan = document.querySelector('.workspace-plan');
  if (!plan) return;
  const count = plan.querySelector('.workspace-plan-count');
  const boxes = [...plan.querySelectorAll('.workspace-plan-box')];
  if (!count || boxes.length === 0) return;

  const total = Number(count.dataset.total) || boxes.length;
  const render = () => {
    const done = boxes.filter((b) => b.checked).length;
    count.textContent = `покрыто ${done} из ${total}`;
  };
  boxes.forEach((box) => box.addEventListener('change', render));
  render();
  count.hidden = false;
}

/**
 * Редактор кода: колонка номеров строк и счёт написанного.
 *
 * Номера считаются по переводам строки, а не по визуальным строкам: поле кода
 * не переносит длинные строки (`white-space: pre`), поэтому одна логическая
 * строка — это одна нарисованная, и колонка не разъезжается с содержимым.
 */
function initCodeSurface() {
  const surface = document.querySelector('.answer-surface');
  const gutter = surface?.querySelector('.code-gutter');
  const field = surface?.querySelector('textarea');
  if (!surface || !gutter || !field) return;

  const status = document.querySelector('.code-status');
  let lastLines = -1;

  const render = () => {
    const value = field.value;
    const lines = value.length === 0 ? 1 : value.split('\n').length;
    if (lines !== lastLines) {
      lastLines = lines;
      let numbers = '';
      for (let i = 1; i <= lines; i += 1) numbers += `${i}\n`;
      gutter.textContent = numbers;
    }
    if (status) {
      const chars = value.length;
      status.textContent = `${lines} ${plural(lines, 'строка', 'строки', 'строк')}`
        + ` · ${chars} ${plural(chars, 'знак', 'знака', 'знаков')}`;
    }
  };

  // Колонка номеров прокручивается вместе с полем: у неё своей полосы нет.
  const syncScroll = () => {
    gutter.scrollTop = field.scrollTop;
  };

  field.addEventListener('input', render);
  field.addEventListener('scroll', syncScroll, { passive: true });
  render();
  gutter.hidden = false;
  surface.classList.add('is-numbered');
  if (status) status.hidden = false;
}

function plural(n, one, few, many) {
  const mod10 = n % 10;
  const mod100 = n % 100;
  if (mod10 === 1 && mod100 !== 11) return one;
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return few;
  return many;
}

/**
 * Секундомер репетиции устного ответа.
 *
 * Запускается кнопкой: самозапуск при открытии вопроса гнал бы время, пока
 * человек читает задание. Состояние («в темпе» / «пора закруглять» / «затянуто»)
 * названо словом — цвет лишь усиливает уже сказанное.
 */
function initRehearsalTimer() {
  const timer = document.querySelector('.rehearsal-timer');
  if (!timer) return;
  const toggle = timer.querySelector('.rehearsal-timer-toggle');
  const clock = timer.querySelector('.rehearsal-timer-clock');
  const state = timer.querySelector('.rehearsal-timer-state');
  if (!toggle || !clock || !state) return;

  let ticker = null;
  let elapsed = 0;

  const band = () => {
    if (elapsed >= OVER_SECONDS) return ['over', 'Затянуто — на собеседовании тут уже перебивают'];
    if (elapsed >= PACE_SECONDS) return ['wrap', 'Пора закругляться: вывод и пример'];
    return ['pace', 'Идёшь в темпе'];
  };

  const paint = () => {
    clock.textContent = formatClock(elapsed);
    const [key, text] = band();
    if (state.dataset.band !== key) {
      state.dataset.band = key;
      state.textContent = text;
    }
  };

  const stop = () => {
    if (ticker !== null) {
      window.clearInterval(ticker);
      ticker = null;
    }
  };

  toggle.addEventListener('click', () => {
    if (ticker !== null) {
      stop();
      toggle.textContent = 'Продолжить';
      toggle.dataset.state = 'paused';
      return;
    }
    ticker = window.setInterval(() => {
      elapsed += 1;
      paint();
    }, 1000);
    toggle.textContent = 'Пауза';
    toggle.dataset.state = 'running';
    paint();
  });

  // Отправка ответа завершает репетицию: считать дальше нечего.
  timer.closest('.typed-workspace')?.querySelector('form')
    ?.addEventListener('submit', stop);

  timer.hidden = false;
}

/**
 * Точка входа рабочего места. Каждая часть сама проверяет свои узлы: состав
 * поверхности решает сервер по режиму, и модуль не хранит второй копии этой
 * таблицы.
 */
export function initTypedWorkspace() {
  initPlanCount();
  initCodeSurface();
  initRehearsalTimer();
}
