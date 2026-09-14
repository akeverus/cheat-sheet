/**
 * Application entry: page-scoped dynamic imports to keep focus/result bundles lean.
 */
import { registerLifecycle, runInit } from './core/lifecycle.js';
import { initPersonalizationBootstrap } from './settings/personalization-bootstrap.js';
import { initOfflineBanner } from './ui/alerts.js';
import { hydrateProgressBarsFromData } from './core/dom.js';
import { highlightPageCodeBlocks } from './content/highlight.js';
import { initScrollableTables } from './content/table-scroll.js';
import { initStreakBar, initDailyGoalForm } from './quiz/streak.js';
import { initFavorites } from './quiz/favorites.js';
import { initBookmarksPage } from './quiz/bookmarks.js';
import { initDangerousFormGuard, initSubmitOnceGuard } from './ui/form-guards.js';
import { initPendingGuard } from './ui/pending.js';
import { initKeyboardHelp } from './ui/keyboard-help.js';
import { initCopyCode, initCodeToolbar } from './ui/code-toolbar.js';
import { initCycleFocusHandoff } from './ui/cycle-focus.js';
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
  const { initMobileDisclosures } = await import('./ui/mobile-disclosures.js');
  initMobileDisclosures('(max-width: 900px)');
}

async function loadQuizModules() {
  const [
    { initFlashcardShortcuts, initBrowseFlashcardReveal, initRecallTypedEcho },
    { initInterviewForm },
    { initContentTabs },
    { initTypedWorkspace },
    { initMobileDisclosures }
  ] = await Promise.all([
    import('./quiz/flashcard.js'),
    import('./quiz/interview.js'),
    import('./content/materials-tabs.js'),
    import('./quiz/workspace.js'),
    import('./ui/mobile-disclosures.js')
  ]);
  initBrowseFlashcardReveal();
  initFlashcardShortcuts();
  initRecallTypedEcho();
  initInterviewForm();
  initContentTabs();
  initTypedWorkspace();
  // План ответа сворачивается тем же приёмом, что витрина знаков и отбор в
  // аналитике (ui/mobile-disclosures.js), но по своему условию: здесь он стоит
  // НАД полем ввода, поэтому закрывается там, где композиция стала стопкой
  // (уже 1280) или окно низкое (ниже 800) — иначе главное действие уходит за сгиб.
  initMobileDisclosures('(max-width: 1279px), (max-height: 800px)');
}

// Закладки: сворачивание формы отбора на телефоне. Модуль тот же, что у аналитики
// и экрана вопроса, — условие своё (телефон), потому что ограничение здесь
// горизонтальное: на 390 форма кладёт 241px между шапкой и коллекцией.
async function loadBookmarksModules() {
  const { initMobileDisclosures } = await import('./ui/mobile-disclosures.js');
  initMobileDisclosures();
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
      initDailyGoalForm();
      initDangerousFormGuard();
      // Раньше initSubmitOnceGuard: перехват повторного действия обязан стоять
      // на фазе capture до того, как хоть один контрол успеет стать занятым.
      initPendingGuard();
      initSubmitOnceGuard();
      initKeyboardHelp();
      initCopyCode();
      initCycleFocusHandoff();
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
      // Закладки: форма отбора под раскрытием на телефоне.
      if (pageIs('bookmarks-page')) {
        tasks.push(loadBookmarksModules());
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
