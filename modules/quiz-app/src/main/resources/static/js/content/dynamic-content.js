import { highlightContainer } from './highlight.js';
import { renderAllMermaid } from './mermaid-renderer.js';

const OPTION_EXPLANATION_COLLAPSE_QUERY = '(max-width: 760px)';

function optionExplanationSummaryText(explanation) {
  if (explanation.classList.contains('explanation-correct')) {
    return 'Почему это правильный ответ';
  }
  return explanation.closest('.option-wrong') ? 'Почему выбранный ответ неверен' : 'Пояснение к варианту';
}

export function enhanceOptionExplanationDisclosure(container) {
  if (!container || !window.matchMedia || !window.matchMedia(OPTION_EXPLANATION_COLLAPSE_QUERY).matches) {
    return;
  }
  container.querySelectorAll('.option-explanation:not([data-disclosure-bound])').forEach((explanation) => {
    if (explanation.closest('.option-explanation-disclosure')) return;

    const details = document.createElement('details');
    details.className = 'option-explanation-disclosure';
    details.classList.add(explanation.classList.contains('explanation-correct')
      ? 'option-explanation-disclosure-correct'
      : 'option-explanation-disclosure-wrong');

    const summary = document.createElement('summary');
    summary.className = 'option-explanation-summary';
    summary.textContent = optionExplanationSummaryText(explanation);

    explanation.parentNode.insertBefore(details, explanation);
    details.appendChild(summary);
    details.appendChild(explanation);
    explanation.setAttribute('data-disclosure-bound', 'true');
  });
}

/** Hydrate hljs + Mermaid inside dynamically inserted answer content. */
export function renderDynamicContent(container) {
  if (!container) return;
  enhanceOptionExplanationDisclosure(container);
  highlightContainer(container);
  renderAllMermaid(false, container);
}
