/** Read Spring CSRF token from XSRF-TOKEN cookie (CookieCsrfTokenRepository). */
export function readXsrfToken() {
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
  if (match) return decodeURIComponent(match[1]);
  // Fallback: rare race before materializer cookie lands — not used for XOR form field.
  return null;
}
