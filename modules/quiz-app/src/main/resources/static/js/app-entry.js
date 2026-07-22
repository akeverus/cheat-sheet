/**
 * Application entry: page-scoped dynamic imports to keep focus/result bundles lean.
 */
import { registerLifecycle, runInit } from './core/lifecycle.js';
import { initPersonalizationBootstrap } from './settings/personalization-bootstrap.js';
import { initOfflineBanner } from './ui/alerts.js';
import { hydrateProgressBarsFromData } from './core/dom.js';
import { highlightPageCodeBlocks } from './content/highlight.js';
import { initStreakBar } from './quiz/streak.js';
import { initFavorites } from './quiz/favorites.js';
import { initBookmarksPage } from './quiz/bookmarks.js';
import { initDangerousFormGuard, initSubmitOnceGuard } from './ui/form-guards.js';
import { initKeyboardHelp } from './ui/keyboard-help.js';
import { initCopyCode, initCodeToolbar } from './ui/code-toolbar.js';
import { initReportTriggers } from './ui/modals.js';
import { enhanceOptionExplanationDisclosure } from './content/dynamic-content.js';

let booted = false;

function pageIs(...classes) {
  const body = document.body;
  if (!body) return false;
  return classes.some((c) => body.classList.contains(c));
}

async function loadSettingsModules() {
  const [
    { initShuffleTopic, initSessionModeForm, initLiveFilters, initSessionFormSync },
    { initExportButtons },
    { initPersonalization },
    { initSettingsTabs, initContentTabs }
  ] = await Promise.all([
    import('./settings/session-form.js'),
    import('./ui/export.js'),
    import('./settings/personalization.js'),
    import('./settings/tabs.js')
  ]);
  initShuffleTopic();
  initSessionFormSync();
  initSessionModeForm();
  initLiveFilters();
  initExportButtons();
  initPersonalization();
  initSettingsTabs();
  initContentTabs();
}

async function loadQuizModules() {
  const [
    { initFlashcardShortcuts, initBrowseFlashcardReveal },
    { initInterviewForm }
  ] = await Promise.all([
    import('./quiz/flashcard.js'),
    import('./quiz/interview.js')
  ]);
  initBrowseFlashcardReveal();
  initFlashcardShortcuts();
  initInterviewForm();
}

function boot() {
  if (booted) return;
  booted = true;

  registerLifecycle('app-core', {
    async init() {
      initPersonalizationBootstrap();
      initOfflineBanner();
      hydrateProgressBarsFromData();
      highlightPageCodeBlocks();
      initFavorites();
      initBookmarksPage();
      initStreakBar();
      initDangerousFormGuard();
      initSubmitOnceGuard();
      initKeyboardHelp();
      initCopyCode();
      initCodeToolbar();
      enhanceOptionExplanationDisclosure(document);
      initReportTriggers();

      const tasks = [];
      // Settings surface owns session builder / tabs / export.
      if (pageIs('settings-page') || document.getElementById('session-form') || document.getElementById('settings-tablist')) {
        tasks.push(loadSettingsModules());
      }
      // Quiz interaction modules for focus/result/summary (or any page with interview form).
      if (pageIs('focus-page', 'result-page', 'summary-page')
          || document.getElementById('interview-form')
          || document.querySelector('[data-interview-form]')) {
        tasks.push(loadQuizModules());
      }
      await Promise.all(tasks);
    }
  });
  void runInit();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', boot);
} else {
  boot();
}
