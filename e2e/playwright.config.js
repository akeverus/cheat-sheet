// @ts-check
const { defineConfig, devices } = require('@playwright/test');

const baseURL = process.env.E2E_BASE_URL || 'http://localhost:8080';

/**
 * TEST-005 visual matrix + TEST-006 keyboard quiz flow.
 * Expects app already up (or set E2E_START_SERVER=1 to boot via Gradle).
 */
module.exports = defineConfig({
  testDir: '.',
  timeout: 60_000,
  expect: {
    timeout: 15_000,
    toHaveScreenshot: {
      maxDiffPixelRatio: 0.03,
      animations: 'disabled',
    },
  },
  // Tracked golden baselines (not under gitignored *-snapshots/)
  snapshotPathTemplate: '{testDir}/visual-baselines/{arg}{ext}',
  fullyParallel: false,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: [['list']],
  use: {
    baseURL,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
    // WebKit гоняет ТОЛЬКО клавиатурный спек: visual-baselines сняты в Chromium,
    // и полный webkit-прогон матрицы дал бы ложные пиксельные диффы.
    { name: 'webkit', use: { ...devices['Desktop Safari'] }, testMatch: /keyboard-quiz\.spec\.js/ },
  ],
  webServer: process.env.E2E_START_SERVER === '1'
    ? {
        command: '../gradlew :quiz-app:bootRun -q',
        cwd: __dirname,
        url: baseURL,
        reuseExistingServer: true,
        timeout: 180_000,
      }
    : undefined,
});
