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

  // Читаем editorial-токены темы с :root, чтобы графики были читаемы в обеих
  // темах (по умолчанию Chart.js рисует оси тёмно-серым — невидимо на тёмном фоне).
  function themePalette() {
    var cs = getComputedStyle(document.documentElement);
    function tok(name, fallback) {
      var v = cs.getPropertyValue(name);
      return v && v.trim() ? v.trim() : fallback;
    }
    return {
      text: tok('--color-text-secondary', '#5A5145'),
      grid: tok('--color-border-secondary', 'rgba(0,0,0,0.08)'),
      learned: tok('--color-status-success', '#2E6B45'),
      remaining: tok('--color-text-tertiary', '#8A8073'),
      accHigh: tok('--color-status-success', '#2E6B45'),
      accMid: tok('--color-status-warning', '#9A6B00'),
      accLow: tok('--color-status-error', '#B3261E')
    };
  }

  function initCharts() {
    var topicStats = getTopicStats();

    // Chart.js рисует в <canvas> bitmap — его JS-анимация вне досягаемости CSS,
    // поэтому общий motion-kill (@media prefers-reduced-motion + html[data-motion=off])
    // её НЕ гасит. Читаем ту же ось «Движение» вручную: off → нет анимации,
    // on → форсим, auto (атрибут отсутствует) → по системной prefers-reduced-motion.
    function motionAllowed() {
      var attr = document.documentElement.getAttribute('data-motion');
      if (attr === 'off') return false;
      if (attr === 'on') return true;
      return !(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
    }

    var progressFallback = document.getElementById('topicProgressChartFallback');
    var accuracyFallback = document.getElementById('topicAccuracyChartFallback');
    function showChartFallback(canvas, fallback, message) {
      if (canvas) canvas.classList.add('hidden');
      if (fallback) {
        fallback.classList.remove('hidden');
        fallback.textContent = message || fallback.textContent;
      }
    }

    // Канвасы рендерятся сервером только когда темы ЕСТЬ (th:if в шаблоне), и мы
    // попадаем сюда лишь при непустом JSON-блоке. Значит пустой topicStats тут =
    // сбой парсинга встроенного JSON: показываем понятный fallback на оставшихся
    // в DOM канвасах вместо немой пустой области. В истинно-пустом случае канвасов
    // в DOM нет — getElementById вернёт null и showChartFallback просто ничего не сделает.
    if (topicStats.length === 0) {
      showChartFallback(document.getElementById('topicProgressChart'), progressFallback, 'Не удалось загрузить данные графика. Используй таблицу ниже.');
      showChartFallback(document.getElementById('topicAccuracyChart'), accuracyFallback, 'Не удалось загрузить данные графика. Используй таблицу ниже.');
      return;
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

    var el1 = document.getElementById('topicProgressChart');
    var el2 = document.getElementById('topicAccuracyChart');
    var charts = [];

    // Полная перерисовка под текущую тему: уничтожаем старые инстансы, читаем
    // свежие токены, строим заново. Вызывается на старте и при смене темы.
    function render() {
      var p = themePalette();
      var anim = motionAllowed();
      charts.forEach(function (c) { try { c.destroy(); } catch (_) {} });
      charts = [];

      var commonOpts = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'bottom', labels: { boxWidth: 12, color: p.text, font: { size: 11 } } } },
        scales: {
          x: { ticks: { maxRotation: 60, color: p.text, font: { size: 10 } }, grid: { color: p.grid }, border: { color: p.grid } },
          y: { beginAtZero: true, ticks: { color: p.text }, grid: { color: p.grid }, border: { color: p.grid } }
        }
      };

      if (el1 && typeof Chart !== 'undefined' && progressData.length > 0) {
        el1.classList.remove('hidden');
        if (progressFallback) progressFallback.classList.add('hidden');
        try {
          charts.push(new Chart(el1, {
            type: 'bar',
            data: {
              labels: progressData.map(function (t) { return t.name; }),
              datasets: [
                { label: 'Выучено', data: progressData.map(function (t) { return t.learned; }), backgroundColor: p.learned, borderRadius: 3 },
                { label: 'Осталось', data: progressData.map(function (t) { return Math.max(0, t.total - t.learned); }), backgroundColor: p.remaining, borderRadius: 3 }
              ]
            },
            options: { animation: anim, scales: { x: Object.assign({}, commonOpts.scales.x, { stacked: true }), y: Object.assign({}, commonOpts.scales.y, { stacked: true }) }, responsive: commonOpts.responsive, maintainAspectRatio: commonOpts.maintainAspectRatio, plugins: commonOpts.plugins }
          }));
        } catch (_) {
          showChartFallback(el1, progressFallback, 'Не удалось отрисовать график прогресса. Используй таблицу ниже.');
        }
      } else if (el1 && typeof Chart !== 'undefined') {
        showChartFallback(el1, progressFallback, 'Пока нет активных тем. Начни отвечать — и здесь появится твой прогресс.');
      } else {
        showChartFallback(el1, progressFallback, 'График прогресса недоступен в текущем окружении. Используй таблицу ниже.');
      }

      if (el2 && typeof Chart !== 'undefined' && accuracyData.length > 0) {
        el2.classList.remove('hidden');
        if (accuracyFallback) accuracyFallback.classList.add('hidden');
        try {
          var accVals = accuracyData.map(function (t) { return t.accuracy; });
          charts.push(new Chart(el2, {
            type: 'bar',
            data: {
              labels: accuracyData.map(function (t) { return t.name; }),
              datasets: [{
                label: 'Точность %',
                data: accVals,
                backgroundColor: accVals.map(function (v) { return v >= 80 ? p.accHigh : v >= 50 ? p.accMid : p.accLow; }),
                borderRadius: 3
              }]
            },
            options: { animation: anim, scales: { x: commonOpts.scales.x, y: Object.assign({}, commonOpts.scales.y, { max: 100 }) }, responsive: commonOpts.responsive, maintainAspectRatio: commonOpts.maintainAspectRatio, plugins: commonOpts.plugins }
          }));
        } catch (_) {
          showChartFallback(el2, accuracyFallback, 'Не удалось отрисовать график точности. Используй таблицу ниже.');
        }
      } else if (el2 && typeof Chart !== 'undefined') {
        showChartFallback(el2, accuracyFallback, 'Пока нет отвеченных вопросов. Ответь на несколько — и увидишь точность по темам.');
      } else {
        showChartFallback(el2, accuracyFallback, 'График точности недоступен в текущем окружении. Используй таблицу ниже.');
      }
    }

    render();

    // Перерисовать графики при ручном переключении темы ИЛИ дизайна (data-theme/
    // data-design на <html>), иначе оси/бары/легенда остаются в цветах прежней
    // палитры до перезагрузки. Графики читают --color-status-* из живого CSS, а
    // переключатель дизайна (Editorial/Swiss/Linear) меняет эти токены на лету.
    if (typeof MutationObserver !== 'undefined') {
      var paletteKey = function () {
        var de = document.documentElement;
        return de.getAttribute('data-theme') + '/' + de.getAttribute('data-design');
      };
      var lastKey = paletteKey();
      new MutationObserver(function () {
        var key = paletteKey();
        if (key !== lastKey) { lastKey = key; render(); }
      }).observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme', 'data-design'] });
      // Чистая смена оси «Движение» (data-motion) не меняет paletteKey → графики
      // не перерисовались бы. head.html шлёт motionchange — перерисуем под него,
      // чтобы вкл/выкл анимации применялись сразу, без перезагрузки.
      document.addEventListener('motionchange', render);
    }
  }

  function initTableSort() {
    var table = document.getElementById('topic-table');
    if (!table) return;
    var sortStatus = document.getElementById('table-sort-status');

    table.querySelectorAll('th.sortable').forEach(function (th) {
      th.classList.add('sortable-cursor');
      // Атрибуты интерактивности навешиваем здесь, а не в шаблоне: без JS
      // заголовки оставались focusable (tabindex) и обещали Enter/Space, которые
      // ничего не делали. Теперь no-JS видит обычные неинтерактивные <th>.
      th.setAttribute('tabindex', '0');
      th.setAttribute('aria-keyshortcuts', 'Enter Space');
      th.setAttribute('aria-sort', 'none');
      var sortByColumn = function () {
        var sortLabel = th.dataset.sortLabel || (th.textContent ? th.textContent.trim() : 'колонка');
        var col = parseInt(th.dataset.col, 10);
        var tbody = table.querySelector('tbody');
        if (!tbody) return;
        var rows = Array.from(tbody.querySelectorAll('tr'));
        var asc = th.dataset.dir !== 'asc';
        th.dataset.dir = asc ? 'asc' : 'desc';
        // C28: имя колонки держим чистым; состояние — только в aria-sort, анонс — в
        // live-region #table-sort-status ниже. aria-label больше не трогаем.
        table.querySelectorAll('th.sortable').forEach(function (h) {
          h.setAttribute('aria-sort', 'none');
        });
        th.setAttribute('aria-sort', asc ? 'ascending' : 'descending');
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

  // /stats не подключает app.js, поэтому свой компактный «?»-оверлей справки
  // (переиспользует CSS .kbd-help-* из base.css). Шорткаты — релевантные
  // именно аналитике: «/» фокусирует поиск, Enter/Space сортируют колонку.
  function initKeyboardHelp() {
    var shortcuts = [
      { keys: ['/'], desc: 'Перейти к поиску по вопросам' },
      { keys: ['Enter', 'Space'], desc: 'Сортировать колонку таблицы (когда в фокусе)' },
      { keys: ['?'], desc: 'Показать / скрыть эту справку' },
      { keys: ['Esc'], desc: 'Закрыть справку' }
    ];
    var overlay = document.createElement('div');
    overlay.className = 'kbd-help-overlay hidden';
    overlay.setAttribute('role', 'dialog');
    overlay.setAttribute('aria-modal', 'true');
    overlay.setAttribute('aria-labelledby', 'kbd-help-title');
    overlay.innerHTML =
      '<div class="kbd-help-modal">' +
      '<h3 id="kbd-help-title">Горячие клавиши</h3>' +
      '<dl class="kbd-help-list">' +
      shortcuts.map(function (s) {
        return '<dt>' + s.keys.map(function (k) { return '<kbd>' + k + '</kbd>'; }).join(' ') + '</dt><dd>' + s.desc + '</dd>';
      }).join('') +
      '</dl><button type="button" class="kbd-help-close" aria-label="Закрыть">×</button></div>';
    document.body.appendChild(overlay);
    var closeBtn = overlay.querySelector('.kbd-help-close');
    var lastFocused = null;
    // Фон под модалкой inert+aria-hidden (aria-modal не держит SR-курсор в
    // browse-mode). Храним проставленные, чтобы снять ровно их при закрытии.
    var inerted = [];
    function close() {
      if (overlay.classList.contains('hidden')) return;
      overlay.classList.add('hidden');
      inerted.forEach(function (el) { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
      inerted = [];
      if (lastFocused && typeof lastFocused.focus === 'function') lastFocused.focus();
      lastFocused = null;
    }
    function open() {
      lastFocused = document.activeElement;
      overlay.classList.remove('hidden');
      inerted = [];
      Array.prototype.forEach.call(document.body.children, function (el) {
        if (el === overlay || el.tagName === 'SCRIPT') return;
        if (el.hasAttribute('inert') || el.getAttribute('aria-hidden') === 'true') return;
        el.setAttribute('inert', '');
        el.setAttribute('aria-hidden', 'true');
        inerted.push(el);
      });
      closeBtn.focus();
    }
    overlay.addEventListener('click', function (e) { if (e.target === overlay) close(); });
    closeBtn.addEventListener('click', close);
    overlay.addEventListener('keydown', function (e) {
      if (e.key !== 'Tab') return;
      var f = overlay.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
      if (!f.length) return;
      var first = f[0], last = f[f.length - 1];
      if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    });
    document.addEventListener('keydown', function (event) {
      var tag = event.target && event.target.tagName;
      if (event.key === 'Escape' && !overlay.classList.contains('hidden')) {
        event.preventDefault();
        close();
        return;
      }
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return;
      if (event.key === '?') {
        event.preventDefault();
        overlay.classList.contains('hidden') ? open() : close();
      } else if (event.key === '/') {
        // Не воровать фокус в поиск, пока открыт модал справки (фокус ушёл бы
        // за оверлей — ловушка для клавиатуры/скринридера).
        if (!overlay.classList.contains('hidden')) return;
        var search = document.querySelector('.search-input');
        if (search) { event.preventDefault(); search.focus(); }
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    hydrateProgressBarsFromData();
    initCharts();
    initTableSort();
    initKeyboardHelp();
  });
})();
