(function () {
  function hydrateProgressBarsFromData() {
    document.querySelectorAll('[data-progress]').forEach(function (el) {
      var raw = (el.getAttribute('data-progress') || '0').replace(',', '.');
      var numeric = parseFloat(raw);
      var safe = isNaN(numeric) ? 0 : Math.max(0, Math.min(100, numeric));
      el.style.width = safe.toFixed(1) + '%';
    });
  }

  function getTopicStats() {
    var el = document.getElementById('topic-stats-data');
    if (!el || !el.textContent || !el.textContent.trim()) return [];
    try {
      var raw = el.textContent.trim();
      var data = raw.startsWith('[') ? JSON.parse(raw) : [];
      return Array.isArray(data) ? data : [];
    } catch (e) {
      return [];
    }
  }

  function shortName(t) {
    var name = t.topic || 'Без темы';
    var parts = name.split('/');
    return parts[parts.length - 1].replace(/-interview$/, '');
  }

  function initCharts() {
    var topicStats = getTopicStats();
    if (topicStats.length === 0) return;

    var progressFallback = document.getElementById('topicProgressChartFallback');
    var accuracyFallback = document.getElementById('topicAccuracyChartFallback');
    function showChartFallback(canvas, fallback, message) {
      if (canvas) canvas.classList.add('hidden');
      if (fallback) {
        fallback.classList.remove('hidden');
        fallback.textContent = message || fallback.textContent;
      }
    }

    var labels = topicStats.map(shortName);
    var learned = topicStats.map(function (t) { return t.learned; });
    var total = topicStats.map(function (t) { return t.total || 1; });
    var accuracy = topicStats.map(function (t) {
      var sum = t.correct + t.wrong;
      return sum === 0 ? 0 : Math.round((t.correct / sum) * 100);
    });

    var commonOpts = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } } },
      scales: {
        x: { ticks: { maxRotation: 60, font: { size: 10 } } },
        y: { beginAtZero: true }
      }
    };

    var el1 = document.getElementById('topicProgressChart');
    if (el1 && typeof Chart !== 'undefined') {
      try {
        new Chart(el1, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            { label: 'Выучено', data: learned, backgroundColor: 'rgba(34,197,94,0.6)', borderRadius: 3 },
            { label: 'Осталось', data: total.map(function (t, i) { return Math.max(0, t - learned[i]); }), backgroundColor: 'rgba(148,163,184,0.35)', borderRadius: 3 }
          ]
        },
        options: { scales: { x: Object.assign({}, commonOpts.scales.x, { stacked: true }), y: Object.assign({}, commonOpts.scales.y, { stacked: true }) }, responsive: commonOpts.responsive, maintainAspectRatio: commonOpts.maintainAspectRatio, plugins: commonOpts.plugins }
        });
      } catch (_) {
        showChartFallback(el1, progressFallback, 'Не удалось отрисовать график прогресса. Используй таблицу ниже.');
      }
    } else {
      showChartFallback(el1, progressFallback, 'График прогресса недоступен в текущем окружении. Используй таблицу ниже.');
    }

    var el2 = document.getElementById('topicAccuracyChart');
    if (el2 && typeof Chart !== 'undefined') {
      try {
        new Chart(el2, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Точность %',
            data: accuracy,
            backgroundColor: accuracy.map(function (v) { return v >= 80 ? 'rgba(34,197,94,0.6)' : v >= 50 ? 'rgba(234,179,8,0.6)' : 'rgba(239,68,68,0.6)'; }),
            borderRadius: 3
          }]
        },
        options: { scales: { x: commonOpts.scales.x, y: Object.assign({}, commonOpts.scales.y, { max: 100 }) }, responsive: commonOpts.responsive, maintainAspectRatio: commonOpts.maintainAspectRatio, plugins: commonOpts.plugins }
        });
      } catch (_) {
        showChartFallback(el2, accuracyFallback, 'Не удалось отрисовать график точности. Используй таблицу ниже.');
      }
    } else {
      showChartFallback(el2, accuracyFallback, 'График точности недоступен в текущем окружении. Используй таблицу ниже.');
    }
  }

  function initTableSort() {
    var table = document.getElementById('topic-table');
    if (!table) return;
    var sortStatus = document.getElementById('table-sort-status');

    table.querySelectorAll('th.sortable').forEach(function (th) {
      th.classList.add('sortable-cursor');
      if (!th.dataset.baseAriaLabel) {
        th.dataset.baseAriaLabel = th.getAttribute('aria-label') || '';
      }
      var sortByColumn = function () {
        var sortLabel = th.dataset.sortLabel || (th.textContent ? th.textContent.trim() : 'колонка');
        var col = parseInt(th.dataset.col, 10);
        var tbody = table.querySelector('tbody');
        if (!tbody) return;
        var rows = Array.from(tbody.querySelectorAll('tr'));
        var asc = th.dataset.dir !== 'asc';
        th.dataset.dir = asc ? 'asc' : 'desc';
        table.querySelectorAll('th.sortable').forEach(function (h) {
          h.setAttribute('aria-sort', 'none');
          if (h.dataset.baseAriaLabel) {
            h.setAttribute('aria-label', h.dataset.baseAriaLabel);
          }
        });
        th.setAttribute('aria-sort', asc ? 'ascending' : 'descending');
        th.setAttribute('aria-label', 'Сортировка: ' + sortLabel + ', ' + (asc ? 'по возрастанию' : 'по убыванию'));
        rows.sort(function (a, b) {
          var aText = (a.children[col] && a.children[col].textContent ? a.children[col].textContent.trim() : '') || '';
          var bText = (b.children[col] && b.children[col].textContent ? b.children[col].textContent.trim() : '') || '';
          var aNum = parseFloat(aText.replace(/[^0-9.\-]/g, ''));
          var bNum = parseFloat(bText.replace(/[^0-9.\-]/g, ''));
          if (!isNaN(aNum) && !isNaN(bNum)) return asc ? aNum - bNum : bNum - aNum;
          return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
        });
        rows.forEach(function (r) { tbody.appendChild(r); });
        if (sortStatus) {
          sortStatus.textContent = 'Таблица отсортирована: ' + sortLabel + ', ' + (asc ? 'по возрастанию.' : 'по убыванию.');
        }
      };

      th.addEventListener('click', sortByColumn);
      th.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault();
          sortByColumn();
        }
      });
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    hydrateProgressBarsFromData();
    initCharts();
    initTableSort();
  });
})();
