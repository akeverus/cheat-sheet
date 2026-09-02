export const meta = {
  name: 'mcq-pair-leak-verify',
  description: 'Состязательная проверка утечки к соседу: блок Q_A против тела Q_B из того же кластера',
  phases: [{ title: 'PairVerify', detail: 'по одному агенту на пару (Q_A, Q_B): отвечаем ли блок Q_A из тела Q_B' }],
}
const A = args
const SCHEMA = {
  type: 'object', additionalProperties: false,
  required: ['q_a', 'q_b', 'picked_from_a', 'answerable_from_b_alone', 'picked_from_b', 'options_refuted_only_by_b', 'shared_vocabulary', 'verdict', 'note'],
  properties: {
    q_a: { type: 'integer' }, q_b: { type: 'integer' },
    picked_from_a: { type: 'string', enum: ['A', 'B', 'C', 'D'] },
    answerable_from_b_alone: { type: 'boolean' },
    picked_from_b: { type: 'string', enum: ['A', 'B', 'C', 'D', 'none'] },
    options_refuted_only_by_b: { type: 'array', items: { type: 'string', enum: ['A', 'B', 'C', 'D'] } },
    shared_vocabulary: { type: 'array', items: { type: 'string' } },
    verdict: { type: 'string', enum: ['CLEAN', 'SOFT_LEAK', 'HARD_LEAK'] },
    note: { type: 'string' },
  },
}
function prompt(p) {
  return `Ты состязательный ревьюер MCQ, тема: ${A.topic}. Проверяешь УТЕЧКУ К СОСЕДУ.

Два вопроса из одного кластера темы:
- Q${p.a} — файл \`${A.md_path}\`, offset=${p.a_off}, limit=${p.a_lim}
- Q${p.b} — тот же файл, offset=${p.b_off}, limit=${p.b_lim}
Прочитай ОБА тела целиком (Read).

Блок вариантов принадлежит Q${p.a}. Четыре НЕПОМЕЧЕННЫЕ опции лежат в \`${A.blind_path}\` под q_number=${p.a}.

Задание:
1. Опираясь ТОЛЬКО на тело Q${p.a}, выбери правильный вариант (picked_from_a).
2. Теперь забудь тело Q${p.a}. Опираясь ТОЛЬКО на тело Q${p.b}, попробуй ответить на тот же блок:
   answerable_from_b_alone=true, если по телу Q${p.b} правильный вариант выбирается уверенно; picked_from_b — какой.
3. options_refuted_only_by_b — опции, чью ложность видно из тела Q${p.b}, но НЕ из тела Q${p.a}.
4. shared_vocabulary — термины и обороты, которые встречаются и в опциях блока, и в теле Q${p.b}, но НЕ в теле Q${p.a}.
5. verdict: HARD_LEAK — блок отвечается из Q${p.b} не хуже, чем из Q${p.a}, или его correct — это тезис Q${p.b}, а не Q${p.a};
   SOFT_LEAK — из Q${p.b} блок отвечается частично или шатко, есть общий словарь; CLEAN — из Q${p.b} блок не отвечается.
6. note — кратко, какими фразами КАЖДОГО тела подтверждается вывод.

Верни СТРОГО объект по схеме.`
}
phase('PairVerify')
const results = await parallel(A.pairs.map(p => () =>
  agent(prompt(p), { label: `pair:Q${p.a}~Q${p.b}`, phase: 'PairVerify', schema: SCHEMA })))
const all = results.filter(Boolean)
const hard = all.filter(r => r.verdict === 'HARD_LEAK').map(r => `${r.q_a}~${r.q_b}`)
const soft = all.filter(r => r.verdict === 'SOFT_LEAK').map(r => `${r.q_a}~${r.q_b}`)
const fromB = all.filter(r => r.answerable_from_b_alone).map(r => `${r.q_a}~${r.q_b}`)
log(`пар: ${all.length} | HARD: ${hard.length} | SOFT: ${soft.length}`)
return { count: all.length, hard, soft, answerable_from_b: fromB,
         picks_a: all.map(r => `${r.q_a}${r.picked_from_a}`).join(' ') }
