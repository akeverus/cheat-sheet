const { test, expect } = require('@playwright/test');

test.skip(process.env.E2E_SECURITY_MULTI !== '1'
  || process.env.E2E_ISOLATED_DATA !== '1'
  || !/^http:\/\/(?:localhost|127\.0\.0\.1):8081\/?$/.test(process.env.E2E_BASE_URL || ''),
  'Requires the isolated application configured with multi security');

for (const width of [375, 1280]) {
  for (const theme of ['dark', 'light']) {
    test(`login has no reserved sidebar and recovers from invalid credentials ${width} ${theme}`, async ({ page }, info) => {
      await page.setViewportSize({ width, height: 812 });
      await page.addInitScript(theme => localStorage.setItem('theme', theme), theme);
      await page.goto('/login');
      await expect(page.getByRole('heading', { name: 'Вход', exact: true })).toBeVisible();
      await expect(page.locator('.sidebar')).toHaveCount(0);
      const layout = await page.locator('main').evaluate(el => {
        const r = el.getBoundingClientRect();
        const canvas = document.body.getBoundingClientRect();
        return { left: r.left - canvas.left, right: canvas.right - r.right,
          overflow: document.documentElement.scrollWidth - document.documentElement.clientWidth };
      });
      expect(Math.abs(layout.left - layout.right)).toBeLessThanOrEqual(1);
      expect(layout.overflow).toBeLessThanOrEqual(1);
      const username = page.getByLabel('Логин', { exact: true });
      const password = page.getByLabel('Пароль', { exact: true });
      await username.fill(process.env.E2E_LOGIN_USERNAME);
      await password.fill('incorrect-test-password');
      await password.press('Enter');
      await expect(page.locator('#login-error')).toContainText('Неверный логин или пароль');
      await expect(username).toHaveAttribute('aria-invalid', 'true');
      await page.screenshot({ path: info.outputPath(`login-${width}-${theme}.png`) });
      await username.fill(process.env.E2E_LOGIN_USERNAME);
      await password.fill(process.env.E2E_LOGIN_PASSWORD);
      await password.press('Enter');
      await expect(page).toHaveURL(/\/\/?$/);
      await expect(page.locator('.home-stage')).toBeVisible();
    });
  }
}
