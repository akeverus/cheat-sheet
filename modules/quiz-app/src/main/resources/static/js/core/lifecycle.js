/**
 * Lightweight init/destroy registry for page modules (Wave 4–7).
 * Modules register once; app-entry calls runInit on DOMContentLoaded.
 * Optional destroy on bfcache/pageshow or SPA teardown.
 */
const modules = new Map();
let initialized = false;

export function registerLifecycle(name, hooks = {}) {
  if (!name || typeof name !== 'string') return;
  modules.set(name, {
    init: typeof hooks.init === 'function' ? hooks.init : null,
    destroy: typeof hooks.destroy === 'function' ? hooks.destroy : null
  });
}

export async function runInit() {
  if (initialized) return;
  initialized = true;
  for (const [name, hooks] of modules) {
    try {
      await hooks.init?.();
    } catch (err) {
      console.error('[lifecycle] init failed:', name, err);
    }
  }
}

export function runDestroy() {
  modules.forEach((hooks, name) => {
    try {
      hooks.destroy?.();
    } catch (err) {
      console.error('[lifecycle] destroy failed:', name, err);
    }
  });
  initialized = false;
}

export function isLifecycleBooted() {
  return initialized;
}
