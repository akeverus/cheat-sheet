/**
 * Сортировка таблицы тем по клику/Enter/Space на заголовке.
 *
 * Интерактивные атрибуты (`tabindex`, `aria-sort`, `aria-keyshortcuts`) ставит
 * JS, а не шаблон: без скрипта заголовки оставались бы focusable и обещали
 * Enter/Space, которые ничего не делают.
 */
export function initTableSort() {
  const table = document.getElementById('topic-table');
  if (!table) return;
  const status = document.getElementById('table-sort-status');
  const headers = Array.from(table.querySelectorAll('th.sortable'));

  headers.forEach((th) => {
    th.classList.add('sortable-cursor');
    th.setAttribute('tabindex', '0');
    th.setAttribute('aria-keyshortcuts', 'Enter Space');
    th.setAttribute('aria-sort', 'none');

    const sort = () => {
      const tbody = table.querySelector('tbody');
      if (!tbody) return;
      const col = Number.parseInt(th.dataset.col, 10);
      const ascending = th.dataset.dir !== 'asc';
      th.dataset.dir = ascending ? 'asc' : 'desc';

      // Имя колонки держим чистым: состояние живёт в aria-sort, анонс — в
      // live-region ниже. Дописывать «(по возрастанию)» в заголовок не нужно.
      headers.forEach((h) => h.setAttribute('aria-sort', 'none'));
      th.setAttribute('aria-sort', ascending ? 'ascending' : 'descending');

      const cell = (row) => row.children[col]?.textContent?.trim() || '';
      Array.from(tbody.querySelectorAll('tr'))
        .sort((a, b) => {
          const aText = cell(a);
          const bText = cell(b);
          const aNum = Number.parseFloat(aText.replace(/[^0-9.-]/g, ''));
          const bNum = Number.parseFloat(bText.replace(/[^0-9.-]/g, ''));
          if (Number.isFinite(aNum) && Number.isFinite(bNum)) {
            return ascending ? aNum - bNum : bNum - aNum;
          }
          return ascending ? aText.localeCompare(bText) : bText.localeCompare(aText);
        })
        .forEach((row) => tbody.appendChild(row));

      if (status) {
        const label = th.dataset.sortLabel || th.textContent.trim() || 'колонка';
        status.textContent = `Таблица отсортирована: ${label}, `
          + (ascending ? 'по возрастанию.' : 'по убыванию.');
      }
    };

    th.addEventListener('click', sort);
    th.addEventListener('keydown', (event) => {
      if (event.key !== 'Enter' && event.key !== ' ') return;
      event.preventDefault();
      sort();
    });
  });
}
