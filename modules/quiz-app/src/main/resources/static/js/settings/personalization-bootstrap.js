/**
 * Non-FOUC-critical personalization: applied when module loads (after first paint).
 * Тема, экранный пресет и движение живут в inline-бутстрапе head.html — они влияют
 * на первый кадр. Здесь остался размер шрифта.
 */

const root = document.documentElement;

function dispatch(name, detail) {
  try { document.dispatchEvent(new CustomEvent(name, { detail })); } catch (_) { /* ignore */ }
}

/* Ширина чтения и плотность жили здесь двумя независимыми осями с собственным
   хранением. Обе переехали в экранный пресет (fragments/head.html): по отдельности
   они давали сочетания, которых никто не проверял, а применялись после первого
   кадра — то есть меняли раскладку на глазах. Здесь остался размер шрифта: он
   единственный из трёх не двигает геометрию блоков и не обязан быть в шапке. */

// --- Font scale ---
const FONT_MIN = 0.85;
const FONT_MAX = 1.4;
const FONT_STEP = 0.1;
function clampFont(v) {
  v = Math.round(v * 100) / 100;
  return Math.min(FONT_MAX, Math.max(FONT_MIN, v));
}
function readFont() {
  try {
    const f = parseFloat(localStorage.getItem('fontScale'));
    return Number.isFinite(f) ? clampFont(f) : 1;
  } catch (_) { return 1; }
}
let fontScale = readFont();
function applyFont(v) {
  fontScale = clampFont(v);
  root.style.fontSize = fontScale === 1 ? '' : (fontScale * 100) + '%';
}
applyFont(fontScale);
window.__fontScale = {
  get() { return fontScale; },
  min: FONT_MIN,
  max: FONT_MAX,
  STEP: FONT_STEP,
  set(v) {
    applyFont(v);
    try {
      if (fontScale === 1) localStorage.removeItem('fontScale');
      else localStorage.setItem('fontScale', String(fontScale));
    } catch (_) { /* private mode */ }
    dispatch('fontscalechange', { scale: fontScale });
  },
  stepBy(delta) { window.__fontScale.set(fontScale + delta); },
  reset() { window.__fontScale.set(1); }
};

let bootstrapDone = false;
export function initPersonalizationBootstrap() {
  if (bootstrapDone) return;
  bootstrapDone = true;
  // Values already applied at module load; hook exists for idempotent app-entry.
}
