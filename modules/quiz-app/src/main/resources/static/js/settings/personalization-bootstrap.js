/**
 * Non-FOUC-critical personalization: applied when module loads (after first paint).
 * Theme / pixel / motion stay in head.html inline bootstrap.
 */

const root = document.documentElement;

function dispatch(name, detail) {
  try { document.dispatchEvent(new CustomEvent(name, { detail })); } catch (_) { /* ignore */ }
}

// --- Reading width ---
const READING_WIDTHS = ['standard', 'wide'];
function readReadingWidth() {
  try {
    const w = localStorage.getItem('readingWidth');
    return READING_WIDTHS.indexOf(w) >= 0 ? w : 'standard';
  } catch (_) { return 'standard'; }
}
let readingWidth = readReadingWidth();
function applyReadingWidth(w) {
  if (w === 'standard') root.removeAttribute('data-reading-width');
  else root.setAttribute('data-reading-width', w);
}
applyReadingWidth(readingWidth);
window.__readingWidth = {
  current() { return readingWidth; },
  list: READING_WIDTHS.slice(),
  set(next) {
    readingWidth = READING_WIDTHS.indexOf(next) >= 0 ? next : 'standard';
    try {
      if (readingWidth === 'standard') localStorage.removeItem('readingWidth');
      else localStorage.setItem('readingWidth', readingWidth);
    } catch (_) { /* private mode */ }
    applyReadingWidth(readingWidth);
    dispatch('readingwidthchange', { readingWidth });
  }
};

// --- Density ---
const DENSITIES = ['spacious', 'compact'];
function readDensity() {
  try {
    const d = localStorage.getItem('density');
    return DENSITIES.indexOf(d) >= 0 ? d : 'spacious';
  } catch (_) { return 'spacious'; }
}
let density = readDensity();
function applyDensity(d) {
  if (d === 'spacious') root.removeAttribute('data-density');
  else root.setAttribute('data-density', d);
}
applyDensity(density);
window.__density = {
  current() { return density; },
  list: DENSITIES.slice(),
  set(next) {
    density = DENSITIES.indexOf(next) >= 0 ? next : 'spacious';
    try {
      if (density === 'spacious') localStorage.removeItem('density');
      else localStorage.setItem('density', density);
    } catch (_) { /* private mode */ }
    applyDensity(density);
    dispatch('densitychange', { density });
  }
};

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
