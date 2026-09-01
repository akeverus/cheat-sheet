export const meta = {
  name: 'mcq-verify-source-blind',
  description: 'Source-blind верификация MCQ (§32/§79): ревьюер видит видимый Q + материал + 4 непомеченные опции',
  phases: [{ title: 'Verify', detail: 'независимые ревьюеры по чанкам, blind-pick + L0-L4' }],
}
const A = args
const VERDICT_SCHEMA = {
  type: 'object', additionalProperties: false,
  properties: {
    verdicts: {
      type: 'array',
      items: {
        type: 'object', additionalProperties: false,
        properties: {
          q_number: { type: 'integer' },
          picked_label: { type: 'string', enum: ['A', 'B', 'C', 'D'] },
          linkage_level: { type: 'string', enum: ['L0', 'L1', 'L2', 'L3', 'L4'] },
          all_wrong_refutable_from_this_Q: { type: 'boolean' },
          novel_fact_required: { type: 'boolean' },
          same_axis_all_four: { type: 'boolean' },
          note: { type: 'string' },
        },
        required: ['q_number', 'picked_label', 'linkage_level', 'all_wrong_refutable_from_this_Q',
                   'novel_fact_required', 'same_axis_all_four', 'note'],
      },
    },
  },
  required: ['verdicts'],
}
function prompt(c) {
  return `Ты независимый ревьюер MCQ (source-blind, §32/§79). Тема: ${A.topic}.

ЧИТАЙ ДВА ФАЙЛА:
1. \`${A.md_path}\` с offset=${c.off}, limit=${c.lim} — это ИСТОЧНИК: видимые заголовки "## Q<N>"
   и тела ответов. Твой диапазон — Q${c.lo}..Q${c.hi}.
2. \`${A.blind_path}\` — четыре НЕПОМЕЧЕННЫЕ опции для каждого q_number. Какая правильная — НЕ указано.

Ты моделируешь пользователя: он прочитал видимый заголовок ## Q<N> и тело этого Q, после чего
увидел четыре варианта. Больше у него ничего нет.

ДЛЯ КАЖДОГО блока своего диапазона ответь:
- picked_label: какой вариант ты выбираешь, опираясь ТОЛЬКО на материал этого Q;
- linkage_level: L4 (correct и ложность всех дистракторов следуют из Q напрямую), L3 (тот же тезис
  другими словами, без нового знания), L2 (нужен новый факт), L1 (совпадает лишь тема), L0 (другой вопрос);
- all_wrong_refutable_from_this_Q: можно ли КАЖДЫЙ неверный отбросить по телу ИМЕННО ЭТОГО Q;
- novel_fact_required: требуется ли знание извне этого Q;
- same_axis_all_four: все ли четыре отвечают на один и тот же вопрос по одной оси;
- note: кратко — какими фразами тела подтверждается выбор и опровергается каждый неверный;
  ОБЯЗАТЕЛЬНО отметь, если: correct дословно повторяет фразу тела; дистрактор опровергается
  только чтением кода, а не прозы; дистрактор near-truth; правильный ответ заметно длиннее или
  сложнее устроен, чем остальные (визуальная подсказка).

Верни СТРОГО {"verdicts":[...]} для всех блоков Q${c.lo}..Q${c.hi}.`
}
phase('Verify')
const results = await parallel(A.chunks.map(c => () =>
  agent(prompt(c), { label: `verify:${c.label}`, phase: 'Verify', schema: VERDICT_SCHEMA })))
const all = []
for (const r of results) { if (r && r.verdicts) all.push(...r.verdicts) }
all.sort((a, b) => a.q_number - b.q_number)
log(`verdicts: ${all.length}`)
const lv = {}
for (const v of all) lv[v.linkage_level] = (lv[v.linkage_level] || 0) + 1
return {
  count: all.length, levels: lv,
  novel: all.filter(v => v.novel_fact_required).map(v => v.q_number),
  not_refutable: all.filter(v => !v.all_wrong_refutable_from_this_Q).map(v => v.q_number),
  axis_flags: all.filter(v => !v.same_axis_all_four).map(v => v.q_number),
  picks: all.map(v => `${v.q_number}${v.picked_label}`).join(' '),
}
