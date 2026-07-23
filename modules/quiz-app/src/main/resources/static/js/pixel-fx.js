/**
 * pixel-fx.js — PIXEL DECORATIVE LAYER (calm & honest feedback)
 * Crumbs, floaters, trail, sparks, positive burst, stage intro, portal wipe.
 * Frame cycles prefer CSS steps() on horizontal sprite sheets; JS only spawns/removes.
 * Gated by data-pixel (low|med|high) + motion. No pixel typeface.
 *
 * Feedback contract (инварианты §10–§12):
 *   Правильный ответ — ОДИН спокойный позитив (мягкая вспышка + один положительный
 *     label + coin при реальном XP; particle-burst только на high). Без combo / LEVEL UP.
 *   Неверный ответ — НЕ наказание: одна короткая incorrect-subtle анимация.
 *     Запрещено и удалено: red-flash, red/skull burst, shake, негативные
 *     floating labels (MISS/OUCH/«Ошибка»…), исчезающие «жизни».
 *   Fake HUD (lives / COMBO / псевдо-XP) — НЕ рендерится (§12: без ложных метрик).
 *
 * Intensity tiers:
 *   low  — no fx; ambient off.
 *   med  — короткий status: мягкая вспышка + positive label + coin при реальном XP;
 *          incorrect-subtle на ошибке. БЕЗ particle-burst / coin-rain / click-spark
 *          (GFX-006 / PIXEL_ART_CONTRACT_V2: med = только короткий status).
 *   high — + particle burst / coin-rain / click-spark / ambient floaters / trail
 *          (декоративный слой); поток обратной связи тот же.
 *
 * Asset inventory (decorative PNG under /img/pixel/):
 *   KEEP     — crumbs, dots, confetti, boom, fire, coin*, gem, star, mascot, hero,
 *              tab/radio/checkbox, btn-*, xp-bar, corners, spark, skull, potion,
 *              trophy-pixel, heart-life, hp, portal* (burst/floaters), *-sheet.png
 *   HIGH_ONLY — pad, power, sword, shield, lightning, cloud, flag, cherry, key
 *               (ambient floaters / burst packs only; not functional UI)
 *   MERGE    — portal/coin-spin/heart/fire → *-sheet.png (CSS steps cycles)
 *   REMOVE   — nav-home.png, nav-stats.png, nav-settings.png (→ SVG pixel-sprite)
 */
(function () {
  'use strict';

  function motionAllowed() {
    var root = document.documentElement;
    if (root.getAttribute('data-motion') === 'off') return false;
    if (root.getAttribute('data-motion') === 'on') return true;
    if (window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      return false;
    }
    return true;
  }

  function pixelLevel() {
    var v = document.documentElement.getAttribute('data-pixel');
    return (v === 'low' || v === 'med' || v === 'high') ? v : 'med';
  }

  /** Ambient crumbs / trail / CRT floaters — high only */
  function allowAmbient() {
    return motionAllowed() && pixelLevel() === 'high';
  }

  /** Bursts, celebrate, stage intro, HUD — med + high */
  function allowFx() {
    return motionAllowed() && pixelLevel() !== 'low';
  }

  /** Full arcade wrong feedback (shake, MISS text, skull rain) — high only */
  function allowArcadeWrongFx() {
    return allowFx() && pixelLevel() === 'high';
  }

  /** Декоративные particle-эффекты (burst / coin-rain / click-spark) — HIGH only.
   *  GFX-006 / PIXEL_ART_CONTRACT_V2: med = только короткий status, БЕЗ particles/bursts. */
  function allowBurst() {
    return motionAllowed() && pixelLevel() === 'high';
  }

  var CRUMBS = [
    '/img/pixel/crumb-teal.png', '/img/pixel/crumb-amber.png', '/img/pixel/crumb-purple.png',
    '/img/pixel/crumb-green.png', '/img/pixel/crumb-blue.png', '/img/pixel/crumb-red.png',
    '/img/pixel/star.png', '/img/pixel/gem.png', '/img/pixel/coin.png', '/img/pixel/cherry.png',
    '/img/pixel/key.png', '/img/pixel/hp.png', '/img/pixel/confetti-1.png', '/img/pixel/confetti-2.png'
  ];

  var BURST_OK = [
    '/img/pixel/star-big.png', '/img/pixel/gem.png', '/img/pixel/coin.png', '/img/pixel/coin-2.png',
    '/img/pixel/coin-spin-1.png', '/img/pixel/coin-spin-2.png',
    '/img/pixel/heart.png', '/img/pixel/heart-1.png', '/img/pixel/spark.png', '/img/pixel/potion.png',
    '/img/pixel/cherry.png', '/img/pixel/boom-1.png', '/img/pixel/boom-2.png', '/img/pixel/boom-3.png',
    '/img/pixel/lightning.png', '/img/pixel/crumb-teal.png', '/img/pixel/crumb-amber.png',
    '/img/pixel/confetti-1.png', '/img/pixel/confetti-2.png'
  ];

  // §10: без черепов / punitive-иконографики. Спокойные нейтральные частицы —
  // используется только экспортируемым __pixelFx.burst; на неверный ответ
  // celebrate() больше НЕ вызывает wrong-burst.
  var BURST_BAD = [
    '/img/pixel/crumb-red.png', '/img/pixel/boom-1.png', '/img/pixel/boom-2.png', '/img/pixel/boom-3.png',
    '/img/pixel/crumb-amber.png', '/img/pixel/crumb-purple.png'
  ];

  /** med wrong: no skull / heavy arcade bits — calm crumb+boom only */
  var BURST_BAD_MED = [
    '/img/pixel/crumb-red.png', '/img/pixel/boom-1.png', '/img/pixel/boom-2.png',
    '/img/pixel/flag.png', '/img/pixel/btn-x.png'
  ];

  var TRAIL = [
    '/img/pixel/dot-t.png', '/img/pixel/dot-a.png', '/img/pixel/dot-p.png',
    '/img/pixel/dot-g.png', '/img/pixel/dot-b.png', '/img/pixel/dot-r.png'
  ];

  var FLOATERS = [
    '/img/pixel/star-big.png', '/img/pixel/gem.png', '/img/pixel/coin.png', '/img/pixel/heart-life.png',
    '/img/pixel/heart-1.png', '/img/pixel/potion.png', '/img/pixel/power.png', '/img/pixel/trophy-pixel.png',
    '/img/pixel/pad.png', '/img/pixel/fire-1.png', '/img/pixel/streak-fire.png', '/img/pixel/shield.png',
    '/img/pixel/sword.png', '/img/pixel/portal.png', '/img/pixel/portal-2.png', '/img/pixel/portal-3.png',
    '/img/pixel/cherry.png', '/img/pixel/cloud.png', '/img/pixel/key.png'
  ];

  // high-tier arcade-похвала. НЕ ложные метрики: убраны COMBO!/CRITICAL!/PERFECT!
  // (претендовали на несуществующие combo/crit-механики — §12 «без fake-метрик»,
  //  совпадает с шапкой файла «Без combo / LEVEL UP» и §10 calm-honest). Оставлены
  //  честные позитивы без заявки на механику.
  var OK_TEXT = ['NICE!', 'YEAH!', 'PIXEL WIN!', 'GREAT!', 'CLEAN!', 'SHARP!'];
  var OK_TEXT_MED = ['Верно!', 'Отлично!', 'Так держать!', 'Ещё один!', 'Супер!', 'Хорошо!'];

  var mascotTimer = null;
  var activeTimers = [];

  var trailIdx = 0;
  var trailLast = 0;
  var trailBound = false;
  var clickBound = false;

  /**
   * Integer pixel scale: 8 | 16 | 24 | 32.
   * DPR-aware: на retina предпочитаем больший кадр из пула, без дробных CSS-px.
   */
  function pxScale(prefer) {
    var pool = prefer === 'sm' ? [8, 16] : prefer === 'lg' ? [16, 24, 32] : [8, 16, 24];
    var dpr = (typeof window !== 'undefined' && window.devicePixelRatio) ? window.devicePixelRatio : 1;
    if (dpr >= 2 && pool.length > 1) {
      return pool[pool.length - 1];
    }
    if (dpr >= 1.5 && pool.length > 1) {
      return pool[Math.min(pool.length - 1, Math.floor(pool.length / 2))];
    }
    return pool[Math.floor(Math.random() * pool.length)];
  }

  function later(fn, ms) {
    var id = setTimeout(function () {
      activeTimers = activeTimers.filter(function (t) { return t !== id; });
      fn();
    }, ms);
    activeTimers.push(id);
    return id;
  }

  function clearAllTimers() {
    activeTimers.forEach(function (id) { clearTimeout(id); });
    activeTimers = [];
    if (mascotTimer) {
      clearInterval(mascotTimer);
      mascotTimer = null;
    }
  }

  function preloadPixelAssets() {
    if (!document.body.classList.contains('focus-page')) return;
    [
      '/img/pixel/portal-sheet.png', '/img/pixel/coin-spin-sheet.png',
      '/img/pixel/heart-sheet.png', '/img/pixel/fire-sheet.png',
      '/img/pixel/mascot.png', '/img/pixel/mascot-2.png'
    ].forEach(function (src) {
      var img = new Image();
      img.src = src;
    });
  }

  function clearAmbientLayers() {
    document.querySelectorAll('.pixel-ambient, .pixel-floater, .pixel-trail-dot').forEach(function (el) {
      el.remove();
    });
  }

  function spawnAmbient() {
    if (!allowAmbient()) return;
    if (document.querySelector('.pixel-ambient')) return;
    var layer = document.createElement('div');
    layer.className = 'pixel-ambient';
    layer.setAttribute('aria-hidden', 'true');
    // Плотность к целевому балансу «5-10% декоративные» (инвариант): периферийный
    // фон, не ковёр. Слой за контентом (z-index:0), текст всегда сверху.
    var count = window.innerWidth < 700 ? 10 : 20;
    for (var i = 0; i < count; i++) {
      var img = document.createElement('img');
      img.className = 'pixel-crumb';
      img.src = CRUMBS[i % CRUMBS.length];
      img.alt = '';
      img.setAttribute('aria-hidden', 'true');
      img.style.left = (Math.random() * 100) + '%';
      var s = pxScale('sm');
      // Аспект: часть pixel-ассетов НЕ квадратные (star 40×32, key 20×12, gem 32×28).
      // Квадратный box их сплющивал (искажение pixel-сетки + провал Lighthouse
      // image-aspect-ratio). height:auto — равномерный масштаб по натуральному аспекту.
      img.style.width = s + 'px';
      img.style.height = 'auto';
      img.style.setProperty('--crumb-dur', (7 + Math.random() * 18) + 's');
      img.style.setProperty('--crumb-delay', (-Math.random() * 16) + 's');
      img.style.setProperty('--crumb-drift', ((Math.random() * 140) - 70) + 'px');
      layer.appendChild(img);
    }
    document.body.appendChild(layer);
  }

  function spawnFloaters() {
    if (!allowAmbient()) return;
    if (document.querySelector('.pixel-floater')) return;
    var n = window.innerWidth < 700 ? 4 : 7;
    for (var i = 0; i < n; i++) {
      var img = document.createElement('img');
      img.className = 'pixel-floater';
      img.src = FLOATERS[i % FLOATERS.length];
      img.alt = '';
      img.setAttribute('aria-hidden', 'true');
      img.style.left = (6 + Math.random() * 88) + 'vw';
      img.style.top = (10 + Math.random() * 72) + 'vh';
      var s = pxScale('lg');
      img.style.width = s + 'px';
      img.style.height = 'auto'; // аспект pixel-ассета (pad 39×18, power 36×27) — не сплющивать
      img.style.setProperty('--bob', (2.2 + Math.random() * 3.2) + 's');
      img.style.setProperty('--delay', (-Math.random() * 3) + 's');
      document.body.appendChild(img);
    }
  }

  // §12: fake HUD (исчезающие «жизни» EXAM, декоративный COMBO/XP) удалён целиком —
  // это ложные метрики, запрещённые инвариантом. Осталась только defensive-чистка
  // removeHud() на случай устаревшего .pixel-hud из кэша/старого состояния.
  function removeHud() {
    var hud = document.querySelector('.pixel-hud');
    if (hud) hud.remove();
  }

  // Только позитивные лейблы: неверный ответ не вызывает floatText (§10), поэтому
  // ветка BAD_TEXT (MISS/OUCH/NOPE) удалена. pickFloatLabel зовётся лишь для
  // correct=true (celebrate); combo-лейблы PERFECT/«Серия!» убраны вместе с
  // fake-combo, которого больше нет.
  function floatLabelPack() {
    return pixelLevel() === 'high' ? OK_TEXT : OK_TEXT_MED;
  }

  function pickFloatLabel() {
    var pack = floatLabelPack();
    return pack[Math.floor(Math.random() * pack.length)];
  }

  function readSummaryAccuracy() {
    var scoreEl = document.querySelector('.summary-score-value');
    if (!scoreEl) return null;
    var raw = (scoreEl.textContent || '').replace('%', '').replace(',', '.').trim();
    var n = parseFloat(raw);
    return isNaN(n) ? null : n;
  }

  function summaryTone(acc) {
    if (acc == null) return 'neutral';
    if (acc >= 80) return 'great';
    if (acc >= 50) return 'ok';
    return 'low';
  }

  function floatText(text, correct, x, y) {
    if (!allowFx()) return;
    var el = document.createElement('div');
    el.className = 'pixel-float-text ' + (correct ? 'is-ok' : 'is-bad');
    el.setAttribute('aria-hidden', 'true');
    el.textContent = text;
    el.style.left = (x || window.innerWidth / 2) + 'px';
    el.style.top = (y || window.innerHeight * 0.35) + 'px';
    document.body.appendChild(el);
    later(function () { el.remove(); }, 1200);
  }

  function screenFlash(correct) {
    if (!allowFx()) return;
    var flash = document.createElement('div');
    flash.className = 'pixel-screen-flash ' + (correct ? 'is-ok' : 'is-bad');
    flash.setAttribute('aria-hidden', 'true');
    document.body.appendChild(flash);
    later(function () { flash.remove(); }, 420);
  }

  function coinRain(opts) {
    if (!allowBurst()) return;
    opts = opts || {};
    var layer = document.createElement('div');
    layer.className = 'pixel-coin-rain';
    layer.setAttribute('aria-hidden', 'true');
    var coins = opts.heavy
      ? [
          '/img/pixel/coin.png', '/img/pixel/coin-2.png', '/img/pixel/coin-3.png',
          '/img/pixel/coin-spin-1.png', '/img/pixel/coin-spin-2.png',
          '/img/pixel/star-big.png', '/img/pixel/gem.png',
          '/img/pixel/confetti-1.png', '/img/pixel/confetti-2.png'
        ]
      : [
          '/img/pixel/coin.png', '/img/pixel/coin-2.png', '/img/pixel/coin-3.png',
          '/img/pixel/coin-spin-1.png', '/img/pixel/coin-spin-2.png',
          '/img/pixel/star-big.png', '/img/pixel/gem.png',
          '/img/pixel/confetti-1.png', '/img/pixel/confetti-2.png'
        ];
    // coinRain — high-only (allowBurst), поэтому med-ветка счётчика удалена
    var n = opts.heavy
      ? (window.innerWidth < 700 ? 20 : 48)
      : (window.innerWidth < 700 ? 12 : 36);
    for (var i = 0; i < n; i++) {
      var img = document.createElement('img');
      img.src = coins[i % coins.length];
      img.alt = '';
      img.setAttribute('aria-hidden', 'true');
      img.style.left = (Math.random() * 100) + 'vw';
      img.style.setProperty('--fall', (0.7 + Math.random() * 1.5) + 's');
      img.style.setProperty('--delay', (Math.random() * 0.4) + 's');
      var s = pxScale('lg');
      img.style.width = s + 'px';
      layer.appendChild(img);
    }
    document.body.appendChild(layer);
    later(function () { layer.remove(); }, 2400);
  }

  function burstAt(x, y, correct) {
    if (!allowBurst()) return;
    var layer = document.createElement('div');
    layer.className = 'pixel-burst';
    layer.setAttribute('aria-hidden', 'true');
    var pack = correct ? BURST_OK : (allowArcadeWrongFx() ? BURST_BAD : BURST_BAD_MED);
    // burstAt — high-only (allowBurst), med-ветка счётчика удалена
    var n = correct ? 36 : 28;
    for (var i = 0; i < n; i++) {
      var img = document.createElement('img');
      img.className = 'pixel-burst-bit';
      img.src = pack[i % pack.length];
      img.alt = '';
      img.setAttribute('aria-hidden', 'true');
      img.style.left = x + 'px';
      img.style.top = y + 'px';
      var angle = (Math.PI * 2 * i) / n + Math.random() * 0.55;
      var dist = 55 + Math.random() * (correct ? 220 : 180);
      img.style.setProperty('--bx', Math.cos(angle) * dist + 'px');
      img.style.setProperty('--by', Math.sin(angle) * dist + 'px');
      img.style.setProperty('--burst-dur', (0.7 + Math.random() * 0.7) + 's');
      var s = pxScale(correct ? 'lg' : 'sm');
      img.style.width = s + 'px';
      img.style.height = 'auto'; // burst-биты тоже держат натуральный аспект pixel-ассета
      layer.appendChild(img);
    }
    document.body.appendChild(layer);
    later(function () { layer.remove(); }, 1700);
  }

  function burstFromEl(el, correct) {
    if (!el) {
      burstAt(window.innerWidth / 2, window.innerHeight * 0.35, correct);
      return;
    }
    var r = el.getBoundingClientRect();
    burstAt(r.left + r.width / 2, r.top + r.height / 2, correct);
  }

  function clickSpark(x, y) {
    if (!allowBurst()) return;
    var el = document.createElement('div');
    el.className = 'pixel-click-spark';
    el.setAttribute('aria-hidden', 'true');
    el.style.left = (x - 12) + 'px';
    el.style.top = (y - 12) + 'px';
    document.body.appendChild(el);
    for (var i = 0; i < 6; i++) {
      var bit = document.createElement('span');
      bit.className = 'pixel-click-bit';
      var ang = (Math.PI * 2 * i) / 6;
      bit.style.setProperty('--bx', Math.cos(ang) * 18 + 'px');
      bit.style.setProperty('--by', Math.sin(ang) * 18 + 'px');
      bit.style.background = ['#39d0c7', '#f7b83f', '#9e7bff', '#ff715f', '#35c987', '#5db8ff'][i];
      el.appendChild(bit);
    }
    later(function () { el.remove(); }, 450);
  }

  function onPointerMove(e) {
    if (!allowAmbient()) return;
    var now = performance.now();
    if (now - trailLast < 28) return;
    trailLast = now;
    var img = document.createElement('img');
    img.className = 'pixel-trail-dot';
    img.src = TRAIL[trailIdx++ % TRAIL.length];
    img.alt = '';
    img.setAttribute('aria-hidden', 'true');
    img.style.left = e.clientX + 'px';
    img.style.top = e.clientY + 'px';
    document.body.appendChild(img);
    later(function () { img.remove(); }, 560);
  }

  function onClick(e) {
    if (!allowFx()) return;
    var t = e.target;
    if (!t || !t.closest) return;
    // Press sparks on all interactive chrome: .btn and friends
    if (t.closest(
      'button, .btn, .next-btn, .primary-btn, .secondary-btn, a.btn, .icon-button, ' +
      '.side-action, .btn-favorite, .chip, .filter-chip, .content-tab, .section-tab, ' +
      '.confidence-btn, .flashcard-grade-btn, .skip-btn, .danger-btn, .btn-finish, ' +
      '.btn-finish-small, label[data-option-id], #interview-options label, .option, .options label'
    )) {
      clickSpark(e.clientX, e.clientY);
    }
  }

  function animateMascots() {
    // CSS prefers mascotIdle; optional 2-frame swap only when no CSS class applied
    var imgs = document.querySelectorAll('.brand .avatar img, .error-pixel-art');
    if (!imgs.length || !motionAllowed() || pixelLevel() === 'low') return;
    imgs.forEach(function (img) {
      img.classList.add('pixel-mascot-anim');
      img.setAttribute('aria-hidden', 'true');
    });
  }

  function showStageIntro(opts) {
    opts = opts || {};
    if (!allowFx()) return;
    if (document.querySelector('.pixel-stage-intro')) return;
    var title = opts.title || 'STAGE START';
    var subtitle = opts.subtitle || '';
    if (!subtitle && opts.count != null && opts.count !== '') {
      var n = parseInt(opts.count, 10);
      if (!isNaN(n)) {
        subtitle = n + ' ' + pluralQuestions(n);
      } else {
        subtitle = opts.count + ' вопросов';
      }
    }
    var overlay = document.createElement('div');
    overlay.className = 'pixel-stage-intro';
    overlay.setAttribute('aria-hidden', 'true');
    overlay.innerHTML =
      '<div class="pixel-stage-intro-inner">' +
        '<img class="pixel-stage-intro-mascot" src="/img/pixel/mascot.png" alt="">' +
        '<p class="pixel-stage-intro-title"></p>' +
        '<p class="pixel-stage-intro-sub"></p>' +
      '</div>';
    overlay.querySelector('.pixel-stage-intro-title').textContent = title;
    overlay.querySelector('.pixel-stage-intro-sub').textContent = subtitle;
    document.body.appendChild(overlay);
    later(function () {
      overlay.classList.add('is-out');
      later(function () { overlay.remove(); }, 260);
    }, 700);
  }

  function pluralQuestions(n) {
    var mod10 = n % 10;
    var mod100 = n % 100;
    if (mod10 === 1 && mod100 !== 11) return 'вопрос';
    if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return 'вопроса';
    return 'вопросов';
  }

  function readQuestionCount() {
    var count = null;
    var progressWrap = document.querySelector('[aria-label="Прогресс сессии"]');
    if (progressWrap) {
      var max = progressWrap.getAttribute('aria-valuemax');
      if (max) count = max;
    }
    if (!count) {
      var dataTotal = document.querySelector('[data-session-total], [data-total-questions]');
      if (dataTotal) {
        count = dataTotal.getAttribute('data-session-total')
          || dataTotal.getAttribute('data-total-questions');
      }
    }
    if (!count) {
      var mono = document.querySelector('.topbar .crumb .mono, .topbar .progress-mini + .mono, .topbar b.mono');
      if (mono) {
        var m = (mono.textContent || '').match(/\/\s*(\d+)/);
        if (m) count = m[1];
      }
    }
    if (!count) {
      var totalEl = document.querySelector(
        '.focus-progress-line span:last-child, .session-progress + span, [aria-valuemax]'
      );
      if (totalEl) {
        var m2 = (totalEl.textContent || '').match(/\/\s*(\d+)/);
        if (m2) count = m2[1];
        else if (totalEl.getAttribute && totalEl.getAttribute('aria-valuemax')) {
          count = totalEl.getAttribute('aria-valuemax');
        }
      }
    }
    return count;
  }

  function maybeStageIntroOnFocus() {
    if (!document.body.classList.contains('focus-page')) return;
    var hasSession = !!(
      document.querySelector('.focus-session-rail .session-progress') ||
      document.querySelector('.focus-progress-line .session-progress') ||
      document.querySelector('form.rail-finish, form.rail-pause') ||
      document.querySelector('.topbar .progress-mini')
    );
    if (!hasSession) return;
    var key = 'pixelStageIntroShown';
    try {
      if (sessionStorage.getItem(key) === '1') return;
      sessionStorage.setItem(key, '1');
    } catch (e) { /* private mode — show once per boot anyway */ }

    var topicEl = document.querySelector('.focus-question-topic');
    var topic = topicEl ? (topicEl.textContent || '').trim() : '';
    var count = readQuestionCount();
    var title = topic || 'STAGE START';
    var subtitle = '';
    if (count != null && count !== '') {
      var n = parseInt(count, 10);
      subtitle = !isNaN(n) ? (n + ' ' + pluralQuestions(n)) : (count + ' вопросов');
    } else {
      subtitle = 'Тренировка началась';
    }
    showStageIntro({ title: title, subtitle: subtitle, count: count });
  }

  function celebrateSummary() {
    if (!allowFx()) return;
    if (!document.body.classList.contains('summary-page')) return;
    var hero = document.querySelector('.summary-hero');
    var ring = document.querySelector('.score-ring');
    var acc = readSummaryAccuracy();
    var tone = summaryTone(acc);
    if (hero) {
      hero.classList.add('is-celebrating');
      hero.setAttribute('data-summary-tone', tone);
    }
    if (tone === 'low') {
      if (ring) ring.classList.add('is-muted');
      return;
    }
    if (ring) {
      ring.classList.add('is-portal');
      ring.style.position = ring.style.position || 'relative';
    }
    var label = tone === 'great' ? 'Отлично!' : 'Сессия завершена';
    setTimeout(function () {
      burstFromEl(ring || hero, true);
      if (tone === 'great') {
        coinRain();
        burstAt(window.innerWidth * 0.2, window.innerHeight * 0.25, true);
        burstAt(window.innerWidth * 0.8, window.innerHeight * 0.3, true);
        setTimeout(function () {
          burstAt(window.innerWidth / 2, window.innerHeight * 0.45, true);
        }, 280);
      }
      floatText(label, true);
    }, 350);
  }

  function applyIntensityLayers() {
    clearAmbientLayers();
    removeHud();
    syncTrailListener();
    if (allowAmbient()) {
      spawnAmbient();
      spawnFloaters();
    }
    // §12: fake HUD (исчезающие «жизни», COMBO, псевдо-XP) НЕ спавнится —
    // это ложные метрики, запрещённые инвариантом. removeHud() выше вычищает
    // любой ранее созданный HUD при смене интенсивности.
  }

  function syncTrailListener() {
    if (allowAmbient()) {
      if (!trailBound) {
        document.addEventListener('pointermove', onPointerMove, { passive: true });
        trailBound = true;
      }
    } else if (trailBound) {
      document.removeEventListener('pointermove', onPointerMove);
      trailBound = false;
      document.querySelectorAll('.pixel-trail-dot').forEach(function (el) { el.remove(); });
    }
  }

  function setIntensity(level) {
    if (window.__pixelIntensity && typeof window.__pixelIntensity.set === 'function') {
      window.__pixelIntensity.set(level);
    } else {
      var v = (level === 'low' || level === 'med' || level === 'high') ? level : 'med';
      document.documentElement.setAttribute('data-pixel', v);
    }
    applyIntensityLayers();
  }

  /**
   * §10: неверный ответ — НЕ наказание. Единственный допустимый cue —
   * одна короткая спокойная incorrect-subtle анимация (64px, один проход,
   * respects data-motion / prefers-reduced-motion на CSS-уровне).
   */
  function subtleIncorrect(target) {
    if (!allowFx()) return;
    var cue = document.createElement('span');
    cue.className = 'pixel-incorrect-cue';
    cue.setAttribute('data-sprite-anim', 'incorrect-subtle');
    cue.setAttribute('aria-hidden', 'true');
    var x = window.innerWidth / 2;
    var y = window.innerHeight * 0.32;
    if (target) {
      var r = target.getBoundingClientRect();
      // только видимый target с ненулевым размером; иначе центр экрана
      if (r.width > 0 && r.height > 0) {
        x = r.left + r.width / 2;
        y = r.top + r.height / 2;
      }
    }
    cue.style.position = 'fixed';
    cue.style.left = (x - 32) + 'px';
    cue.style.top = (y - 32) + 'px';
    cue.style.zIndex = '110';
    cue.style.pointerEvents = 'none';
    document.body.appendChild(cue);
    later(function () { cue.remove(); }, 900);
  }

  function celebrate(correct, opts) {
    if (!allowFx()) return;
    opts = opts || {};
    var target = document.querySelector('.result-banner')
      || document.querySelector('.status')
      || document.querySelector('#result-feedback');
    var x = window.innerWidth / 2;
    var y = window.innerHeight * 0.32;
    if (target) {
      var r = target.getBoundingClientRect();
      x = r.left + r.width / 2;
      y = r.top + r.height / 2;
    }
    if (correct) {
      // §11: один спокойный позитивный state — burst + мягкая вспышка + один
      // положительный label (+ coin, если начислён реальный XP). Без combo-каскадов,
      // coinRain и «LEVEL UP» — они держались на fake-combo, которого больше нет.
      burstFromEl(target, true);
      screenFlash(true);
      floatText(pickFloatLabel(), true, x, y - 24);
      var xp = Number(opts.xp || 0);
      if (xp > 0) coinSpinPickup(xp);
    } else {
      // §10/§12: убрано full-screen red flash, red/skull burst, негативные
      // floating labels, shake и исчезающие «жизни». Структурный вердикт
      // (result-surface: «Неверно» → правильный ответ → различие → разбор) —
      // самодостаточная спокойная обратная связь.
      subtleIncorrect(target);
    }
  }

  /** Short portal overlay before navigating to the next question. */
  function portalWipe(done) {
    if (!allowFx() || pixelLevel() === 'low') {
      if (typeof done === 'function') done();
      return;
    }
    if (document.querySelector('.pixel-portal-wipe')) {
      if (typeof done === 'function') done();
      return;
    }
    var overlay = document.createElement('div');
    overlay.className = 'pixel-portal-wipe';
    overlay.setAttribute('aria-hidden', 'true');
    // CSS steps() frames on glyph — no setInterval src-swap; fixed 24px box
    overlay.innerHTML = '<span class="pixel-portal-wipe-glyph" aria-hidden="true"></span>';
    document.body.appendChild(overlay);
    later(function () {
      overlay.classList.add('is-done');
      later(function () {
        overlay.remove();
        if (typeof done === 'function') done();
      }, 60);
    }, 420);
  }

  /** Сдержанный reward-жест при начислении реального XP (xpAwarded > 0):
   *  монета всплывает над вердиктом и гаснет. §12: HUD XP-бара больше нет,
   *  поэтому монета никуда не «летит к бару» — просто короткий позитив. */
  function coinSpinPickup() {
    if (!allowFx()) return;
    var startX = window.innerWidth / 2;
    var startY = window.innerHeight * 0.36;
    var endY = 72;
    var coin = document.createElement('span');
    coin.className = 'pixel-coin-pickup pixel-coin-spin-css';
    coin.setAttribute('aria-hidden', 'true');
    coin.style.left = startX + 'px';
    coin.style.top = startY + 'px';
    document.body.appendChild(coin);
    requestAnimationFrame(function () {
      coin.style.transform = 'translate(0px,' + (endY - startY) + 'px) scale(0.55)';
      coin.style.opacity = '0.15';
    });
    later(function () { coin.remove(); }, 520);
  }

  window.__pixelFx = {
    burst: burstAt,
    burstFrom: burstFromEl,
    celebrate: celebrate,
    showStageIntro: showStageIntro,
    setIntensity: setIntensity,
    portalWipe: portalWipe,
    coinSpinPickup: coinSpinPickup
  };

  function bindClickFx() {
    if (!clickBound) {
      document.addEventListener('click', onClick, { passive: true });
      clickBound = true;
    }
  }

  function boot() {
    preloadPixelAssets();
    applyIntensityLayers();
    animateMascots();
    bindClickFx();
    document.addEventListener('pixelintensitychange', applyIntensityLayers);
    document.addEventListener('motionchange', applyIntensityLayers);

    if (!allowFx()) return;
    var wrong = document.querySelector('.result-wrong, .status-wrong');
    var ok = document.querySelector('.result-correct, .status-correct');
    if (ok) {
      later(function () { celebrate(true); }, 200);
    } else if (wrong) {
      later(function () { celebrate(false); }, 200);
    }
    maybeStageIntroOnFocus();
    celebrateSummary();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();
