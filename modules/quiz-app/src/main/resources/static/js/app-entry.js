/**
 * Application entry: page-scoped dynamic imports to keep focus/result bundles lean.
 */
import { registerLifecycle, runInit } from './core/lifecycle.js';
import { initPersonalizationBootstrap } from './settings/personalization-bootstrap.js';
import { initOfflineBanner } from './ui/alerts.js';
import { hydrateProgressBarsFromData } from './core/dom.js';
import { highlightPageCodeBlocks } from './content/highlight.js';
import { initScrollableTables } from './content/table-scroll.js';
import { initStreakBar } from './quiz/streak.js';
import { initFavorites } from './quiz/favorites.js';
import { initBookmarksPage } from './quiz/bookmarks.js';
import { initDangerousFormGuard, initSubmitOnceGuard } from './ui/form-guards.js';
import { initKeyboardHelp } from './ui/keyboard-help.js';
import { initCopyCode, initCodeToolbar } from './ui/code-toolbar.js';
import { initReportTriggers } from './ui/modals.js';
import { initShell } from './ui/shell.js';

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
    { initSettingsTabs }
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
}

async function loadQuizModules() {
  const [
    { initFlashcardShortcuts, initBrowseFlashcardReveal, initRecallTypedEcho },
    { initInterviewForm },
    { initContentTabs }
  ] = await Promise.all([
    import('./quiz/flashcard.js'),
    import('./quiz/interview.js'),
    import('./content/materials-tabs.js')
  ]);
  initBrowseFlashcardReveal();
  initFlashcardShortcuts();
  initRecallTypedEcho();
  initInterviewForm();
  initContentTabs();
}

async function loadStatsModules() {
  const { initStatsPage } = await import('./stats/index.js');
  initStatsPage();
}

async function loadSummaryModules() {
  const { initSummaryTools } = await import('./summary/share-tools.js');
  initSummaryTools();
}

function boot() {
  if (booted) return;
  booted = true;

  registerLifecycle('app-core', {
    async init() {
      initPersonalizationBootstrap();
      initShell();
      initOfflineBanner();
      hydrateProgressBarsFromData();
      highlightPageCodeBlocks();
      initScrollableTables();
      initFavorites();
      initBookmarksPage();
      initStreakBar();
      initDangerousFormGuard();
      initSubmitOnceGuard();
      initKeyboardHelp();
      initCopyCode();
      initCodeToolbar();
      initReportTriggers();

      // Тяжёлые кластеры грузим по классу body. Признак именно такой, а не
      // «есть ли на странице узел X»: якоря каждого кластера (#session-form,
      // #interview-form, #stats-tablist, .summary-hero) встречаются ровно на
      // одном шаблоне, так что проверка по узлу повторяла бы класс страницы.
      const tasks = [];
      // Настройки: конструктор сессии, вкладки, экспорт.
      if (pageIs('settings-page')) {
        tasks.push(loadSettingsModules());
      }
      // Ответ на вопрос: флешкарты, шорткаты, отправка формы, вкладки материалов.
      if (pageIs('focus-page')) {
        tasks.push(loadQuizModules());
      }
      // Аналитика: вкладки, графики, сортировка таблицы тем.
      if (pageIs('stats-page')) {
        tasks.push(loadStatsModules());
      }
      // Итоги сессии: копирование и печать результата.
      if (pageIs('summary-page')) {
        tasks.push(loadSummaryModules());
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
