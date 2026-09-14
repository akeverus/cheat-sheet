/** Browser/editor gestures must not become document-level quiz shortcuts. */
export function isEditingTarget(target) {
  if (!(target instanceof Element)) return false;
  if (target.isContentEditable || target.closest('[role="textbox"]')) return true;
  const field = target.closest('input, textarea, select');
  return !!field && (field.tagName !== 'INPUT' || !['radio', 'checkbox'].includes(field.type));
}

export function isShortcutEventIgnored(event) {
  return event.defaultPrevented || event.isComposing || event.keyCode === 229
    || event.metaKey || event.ctrlKey || event.altKey || isEditingTarget(event.target);
}

/** Links, disclosures and secondary forms own their keyboard interaction. */
export function isSecondaryControl(target, allowedRadioGroup) {
  if (!(target instanceof Element)) return false;
  const control = target.closest('input, textarea, select, button, a, summary, [role="button"], [role="dialog"], dialog');
  if (!control) return false;
  return !(control.matches('input[type="radio"]') && allowedRadioGroup?.contains(control));
}
