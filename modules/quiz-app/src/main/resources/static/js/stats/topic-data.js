/**
 * Чтение потемной статистики, встроенной сервером в #topic-stats-data.
 *
 * Поля блока — ровно те шесть, что читает страница: topic, displayName, total,
 * learned, correct, wrong. Добавлять сюда поле имеет смысл только вместе с местом,
 * которое его показывает (см. TopicStatsChartPayload на бэкенде).
 */

export function getTopicStats() {
  const el = document.getElementById('topic-stats-data');
  const raw = el?.textContent?.trim();
  if (!raw || !raw.startsWith('[')) return [];
  try {
    const data = JSON.parse(raw);
    return Array.isArray(data) ? data : [];
  } catch {
    return [];
  }
}

/**
 * Человекочитаемое имя темы. Сервер присылает displayName для тем из каталога;
 * для остальных разворачиваем slug вида `spring/spring-boot-interview`.
 */
export function displayTopicName(topic) {
  const explicit = String(topic.displayName || '').trim();
  if (explicit) return explicit;
  const slug = topic.topic || '';
  const leaf = slug.split('/').pop().replace(/-interview$/, '');
  const name = leaf.replace(/[-_]+/g, ' ').trim();
  if (!name) return 'Без темы';
  return name.charAt(0).toUpperCase() + name.slice(1);
}
