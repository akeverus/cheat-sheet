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

    // Графики строим не по всем ~305 темам (нечитаемая каша из тонких баров),
    // а по релевантному топу. Полные данные — в сортируемой таблице ниже.
    var MAX_BARS = 25;
    var enriched = topicStats.map(function (t) {
      var attempts = (t.correct || 0) + (t.wrong || 0);
      return {
        name: shortName(t),
        learned: t.learned || 0,
        total: t.total || 1,
        attempts: attempts,
        accuracy: attempts === 0 ? 0 : Math.round(((t.correct || 0) / attempts) * 100),
        activity: (t.learned || 0) + attempts
      };
    });

    // Прогресс: только темы, с которыми работали (выучено/попытки), топ по
    // активности. Нетронутые темы в график не тащим — это и есть основной шум.
    var progressData = enriched
      .filter(function (t) { return t.activity > 0; })
      .sort(function (a, b) { return b.activity - a.activity; })
      .slice(0, MAX_BARS);

    // Точность: только отвеченные темы, слабые первыми (actionable). Тему без
    // попыток НЕ показываем как 0% (красным) — это путало бы «не трогал» с
    // «всё провалил».
    var accuracyData = enriched
      .filter(function (t) { return t.attempts > 0; })
      .sort(function (a, b) { return a.accuracy - b.accuracy || b.attempts - a.attempts; })
      .slice(0, MAX_BARS);

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
    if (el1 && typeof Chart !== 'undefined' && progressData.length > 0) {
      try {
        new Chart(el1, {
        type: 'bar',
        data: {
          labels: progressData.map(function (t) { return t.name; }),
          datasets: [
            { label: 'Выучено', data: progressData.map(function (t) { return t.learned; }), backgroundColor: 'rgba(34,197,94,0.6)', borderRadius: 3 },
            { label: 'Осталось', data: progressData.map(function (t) { return Math.max(0, t.total - t.learned); }), backgroundColor: 'rgba(148,163,184,0.35)', borderRadius: 3 }
          ]
        },
        options: { scales: { x: Object.assign({}, commonOpts.scales.x, { stacked: true }), y: Object.assign({}, commonOpts.scales.y, { stacked: true }) }, responsive: commonOpts.responsive, maintainAspectRatio: commonOpts.maintainAspectRatio, plugins: commonOpts.plugins }
        });
      } catch (_) {
        showChartFallback(el1, progressFallback, 'Не удалось отрисовать график прогресса. Используй таблицу ниже.');
      }
    } else if (el1 && typeof Chart !== 'undefined') {
      showChartFallback(el1, progressFallback, 'Пока нет активных тем. Начни отвечать — и здесь появится твой прогресс.');
    } else {
      showChartFallback(el1, progressFallback, 'График прогресса недоступен в текущем окружении. Используй таблицу ниже.');
    }

    var el2 = document.getElementById('topicAccuracyChart');
    if (el2 && typeof Chart !== 'undefined' && accuracyData.length > 0) {
      try {
        var accVals = accuracyData.map(function (t) { return t.accuracy; });
        new Chart(el2, {
        type: 'bar',
        data: {
          labels: accuracyData.map(function (t) { return t.name; }),
          datasets: [{
            label: 'Точность %',
            data: accVals,
            backgroundColor: accVals.map(function (v) { return v >= 80 ? 'rgba(34,197,94,0.6)' : v >= 50 ? 'rgba(234,179,8,0.6)' : 'rgba(239,68,68,0.6)'; }),
            borderRadius: 3
          }]
        },
        options: { scales: { x: commonOpts.scales.x, y: Object.assign({}, commonOpts.scales.y, { max: 100 }) }, responsive: commonOpts.responsive, maintainAspectRatio: commonOpts.maintainAspectRatio, plugins: commonOpts.plugins }
        });
      } catch (_) {
        showChartFallback(el2, accuracyFallback, 'Не удалось отрисовать график точности. Используй таблицу ниже.');
      }
    } else if (el2 && typeof Chart !== 'undefined') {
      showChartFallback(el2, accuracyFallback, 'Пока нет отвеченных вопросов. Ответь на несколько — и увидишь точность по темам.');
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
