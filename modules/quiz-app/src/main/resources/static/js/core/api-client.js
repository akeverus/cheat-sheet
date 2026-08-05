import { readXsrfToken } from './csrf.js';

const DEFAULT_TIMEOUT_MS = 30_000;
const MUTATING = new Set(['POST', 'PUT', 'PATCH', 'DELETE']);

/** Normalized API / ProblemDetail error. */
export class ApiError extends Error {
  constructor(message, meta = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = meta.status ?? 0;
    this.type = meta.type ?? null;
    this.title = meta.title ?? null;
    this.detail = meta.detail ?? null;
    this.instance = meta.instance ?? null;
    this.correlationId = meta.correlationId ?? null;
    this.fieldErrors = meta.fieldErrors ?? null;
    this.raw = meta.raw ?? null;
  }
}

function mergeAbortSignals(signals) {
  const active = signals.filter(Boolean);
  if (!active.length) return null;
  if (active.length === 1) return active[0];
  const controller = new AbortController();
  const onAbort = () => controller.abort();
  active.forEach((s) => {
    if (s.aborted) controller.abort();
    else s.addEventListener('abort', onAbort, { once: true });
  });
  return controller.signal;
}

function pickMessage(payload, status) {
  if (!payload || typeof payload !== 'object') return 'HTTP ' + status;
  return payload.message
    || payload.detail
    || payload.title
    || payload.error
    || ('HTTP ' + status);
}

function normalizeProblemDetail(payload, status, raw) {
  const message = pickMessage(payload, status);
  const fieldErrors = payload?.details ?? payload?.errors ?? payload?.fieldErrors ?? null;
  const correlationId = payload?.correlationId
    ?? payload?.correlation_id
    ?? (typeof payload?.instance === 'string' && payload.instance.includes('correlation')
      ? payload.instance
      : null);
  return new ApiError(message, {
    status: payload?.status ?? status,
    type: payload?.type ?? null,
    title: payload?.title ?? null,
    detail: payload?.detail ?? null,
    instance: payload?.instance ?? null,
    correlationId,
    fieldErrors,
    raw
  });
}

/**
 * Unified fetch: CSRF, timeout, AbortController, ProblemDetail-aware errors.
 */
export async function apiFetch(url, options = {}) {
  const {
    timeoutMs = DEFAULT_TIMEOUT_MS,
    signal: externalSignal,
    headers: optHeaders,
    method: optMethod,
    ...rest
  } = options;

  const method = (optMethod || 'GET').toUpperCase();
  const headers = { ...(optHeaders || {}) };

  if (MUTATING.has(method)) {
    const token = readXsrfToken();
    if (!token) {
      throw new ApiError('CSRF-токен не найден. Обновите страницу и повторите действие.', {
        status: 403,
        type: 'CSRF_MISSING'
      });
    }
    headers['X-XSRF-TOKEN'] = token;
  }

  // Заголовок Idempotency-Key отсюда убран намеренно: сервер его не читает.
  // Ключ идемпотентности POST /api/answer — поле тела `clientAttemptId`. Ветка
  // не имела ни одного вызывающего и обещала защиту, которой не было бы.

  const timeoutController = new AbortController();
  const timeoutId = setTimeout(() => timeoutController.abort(), timeoutMs);
  const signal = mergeAbortSignals([externalSignal, timeoutController.signal]);

  try {
    const response = await fetch(url, {
      credentials: 'same-origin',
      method,
      headers,
      signal,
      ...rest
    });
    return response;
  } catch (err) {
    if (err && err.name === 'AbortError') {
      if (externalSignal?.aborted) throw err;
      throw new ApiError('Превышено время ожидания ответа сервера', { status: 408 });
    }
    throw err;
  } finally {
    clearTimeout(timeoutId);
  }
}

export async function parseApiError(response) {
  const status = response.status;
  let raw = '';
  try {
    raw = await response.text();
  } catch (_) {
    return new ApiError('HTTP ' + status, { status });
  }
  if (!raw) return new ApiError('HTTP ' + status, { status });
  try {
    const payload = JSON.parse(raw);
    return normalizeProblemDetail(payload, status, raw);
  } catch (_) {
    return new ApiError(raw, { status, raw });
  }
}

/**
 * POST JSON на /api/**. Тело сериализуется здесь, чтобы вызывающий код не
 * собирал его руками — раньше каждый вызов строил form-urlencoded по-своему
 * (строкой, URLSearchParams, FormData), и правки контракта приходилось
 * повторять в пяти местах.
 */
export function apiPostJson(url, payload, options = {}) {
  return apiFetch(url, {
    ...options,
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    body: JSON.stringify(payload)
  });
}

export function apiGet(url) {
  return apiFetch(url).then((r) => {
    if (!r.ok) return parseApiError(r).then((err) => { throw err; });
    return r.json();
  });
}
