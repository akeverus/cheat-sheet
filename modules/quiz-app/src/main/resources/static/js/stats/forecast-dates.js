/**
 * Гуманизация дат прогноза повторений.
 *
 * Машинное значение остаётся в `<time datetime>` (семантика и сортируемость),
 * а видимый текст становится «Сегодня» / «Завтра» / «6 июля». Локаль задаём
 * явно: сервер может отдавать что угодно, а читатель у страницы русскоязычный.
 * Без JS остаётся ISO — не празднично, но однозначно.
 */
export function initForecastDates() {
  const days = document.querySelectorAll('.review-forecast .forecast-day');
  if (!days.length) return;

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const asDay = new Intl.DateTimeFormat('ru-RU', { day: 'numeric', month: 'long' });
  const asFullDay = new Intl.DateTimeFormat('ru-RU', { weekday: 'long', day: 'numeric', month: 'long' });

  days.forEach((el) => {
    const iso = el.getAttribute('datetime') || el.textContent.trim();
    const parts = /^(\d{4})-(\d{2})-(\d{2})$/.exec(iso);
    if (!parts) return;
    const date = new Date(+parts[1], +parts[2] - 1, +parts[3]);
    if (Number.isNaN(date.getTime())) return;
    const diff = Math.round((date - today) / 86400000);
    el.textContent = diff === 0 ? 'Сегодня' : diff === 1 ? 'Завтра' : asDay.format(date);
    el.setAttribute('title', asFullDay.format(date));
  });
}
