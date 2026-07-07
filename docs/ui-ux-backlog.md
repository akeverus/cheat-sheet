# UI/UX backlog

Created: 2026-07-01

This backlog captures the next frontend/UI/UX work for the quiz trainer. Keep items small,
testable, and aligned with the current Thymeleaf + vanilla CSS/JS design system.

## P0 - answer fairness and training flow

| ID | Area | Problem | Next action | Status |
|---|---|---|---|---|
| AF-1 | Answer layout | A long or code-heavy option can look more important in a 2-column grid. | Use a single column when option length ratio is above 1.4 or inline code is skewed. | Done |
| AF-2 | Selected state | Selected-before-check must not look like success or error. | Keep pre-check selected neutral; reserve success/error for checked result only. | Done |
| AF-3 | Keyboard | Users can select with keys but could not clear selection without mouse. | Add Escape to clear selected option and keep the submit disabled. | Done |
| AF-4 | Explanation length | Wrong-option explanations can create a wall of text on mobile. | Keep unselected-wrong explanations collapsed/hidden on mobile; later add per-option disclosure on desktop. | Done |

## P1 - analytics

| ID | Area | Problem | Next action | Status |
|---|---|---|---|---|
| AN-1 | Insights | Metrics show state, but do not clearly tell what to train next. | Add a compact "What to do next" block above charts with weakest topic and review CTA. | Done |
| AN-2 | Topic table | Dense table still needs a better mobile reading mode. | Convert low-width rows to stacked topic cards or improve column priority. | Open |
| AN-3 | Filters | Search and filters are functionally related but visually heavy. | Consolidate helper copy and make reset/apply actions more explicit. | Open |

## P1 - settings

| ID | Area | Problem | Next action | Status |
|---|---|---|---|---|
| SE-1 | Design presets | Presets are names without enough preview/meaning. | Add compact descriptions and a visual preview strip for each design preset. | Open |
| SE-2 | Save feedback | It is not always clear whether a setting is applied immediately. | Add a small polite status message for applied personalization changes. | Open |
| SE-3 | Dangerous actions | Reset is protected, but export-before-reset is not emphasized enough. | Add export prompt/copy inside Danger Zone before reset. | Open |

## P2 - accessibility and states

| ID | Area | Problem | Next action | Status |
|---|---|---|---|---|
| AX-1 | Live regions | Some dynamic secondary blocks are inserted with ready content. | Keep live regions in DOM before updates or fill them on the next frame. | Open |
| AX-2 | Font scale | 125-150% font scale needs a full pass across focus, stats, settings. | Verify responsive screenshots and fix overflows. | Open |
| AX-3 | Reduced motion | Motion tokens exist, but every transition still needs periodic audit. | Recheck interactive states under `prefers-reduced-motion`. | Open |
