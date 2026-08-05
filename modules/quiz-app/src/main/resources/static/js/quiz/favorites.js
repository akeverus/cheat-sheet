import { apiFetch, apiPostJson, parseApiError } from '../core/api-client.js';
import { API, UI } from '../core/constants.js';
import { clearInlineAlert, setInlineAlert } from '../ui/alerts.js';

let favoritesBound = false;

export function applyFavoriteButtonState(button, isFavorite) {
  if (!button) return;
  const favorite = !!isFavorite;
  const label = favorite ? UI.FAVORITE_REMOVE_LABEL : UI.FAVORITE_ADD_LABEL;
  button.classList.toggle('active', favorite);
  button.title = label;
  button.setAttribute('aria-label', label);
  button.setAttribute('aria-pressed', favorite ? 'true' : 'false');
}

async function toggleFavorite(questionId, button) {
  if (!questionId || !button) return;
  try {
    const resp = await apiPostJson(API.FAVORITE, { questionId: Number(questionId) });
    if (resp.ok) {
      clearInlineAlert();
      const data = await resp.json();
      applyFavoriteButtonState(button, data.favorite);
    } else {
      await parseApiError(resp);
      setInlineAlert('Не удалось обновить избранное — возможно, вопрос устарел. Обнови страницу и повтори.');
    }
  } catch (err) {
    console.error('Favorite toggle failed:', err);
    setInlineAlert('Не удалось обновить избранное. Проверь сеть и повтори.');
  }
}

export function initFavorites() {
  if (favoritesBound) return;
  favoritesBound = true;
  document.querySelectorAll('.btn-favorite').forEach((button) => {
    const favorite = button.getAttribute('aria-pressed') === 'true' || button.classList.contains('active');
    applyFavoriteButtonState(button, favorite);
  });
  document.addEventListener('click', (e) => {
    const fav = e.target.closest('.btn-favorite');
    if (!fav) return;
    e.preventDefault();
    const questionId = fav.getAttribute('data-question-id');
    if (questionId) toggleFavorite(questionId, fav);
  });
}
