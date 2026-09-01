export const meta = {
  name: 'mcq-author-linkage-first',
  description: 'ROUND-10 LINKAGE-FIRST: авторинг MCQ строго из материала своего ## Q<N>',
  phases: [{ title: 'Authoring', detail: 'по 5-6 Q на агента, correct = anchor claim этого Q' }],
}

const A = args
const SEC_C = ['explanation', 'example', 'when_to_apply', 'edge_cases', 'related']
const SEC_W = ['what_actually', 'source_of_confusion', 'if_it_were_true', 'how_it_should_be']
const obj = (keys) => ({
  type: 'object', required: keys,
  properties: Object.fromEntries(keys.map(k => [k, { type: 'string' }])),
})
const BLOCK_SCHEMA = {
  type: 'object', required: ['blocks'],
  properties: {
    blocks: {
      type: 'array',
      items: {
        type: 'object',
        required: ['q_number', 'anchor_claim', 'correct_text', 'correct_sections', 'wrong'],
        properties: {
          q_number: { type: 'integer' },
          anchor_claim: { type: 'string' },
          correct_text: { type: 'string' },
          correct_sections: obj(SEC_C),
          wrong: {
            type: 'array', minItems: 3, maxItems: 3,
            items: {
              type: 'object', required: ['text', 'axis', ...SEC_W],
              properties: Object.fromEntries(
                [['text', { type: 'string' }], ['axis', { type: 'string' }],
                 ...SEC_W.map(k => [k, { type: 'string' }])]),
            },
          },
        },
      },
    },
  },
}

function prompt(c) {
  return `Ты автор MCQ по контракту ROUND-10 LINKAGE-FIRST. Тема: ${A.topic}.

ЧИТАЙ ИСТОЧНИК: Read инструментом файл \`${A.md_path}\` с offset=${c.off}, limit=${c.lim}.
Твой диапазон — вопросы Q${c.lo}..Q${c.hi} (заголовки вида "## Q<N>. ...").

ГЛАВНЫЙ КОНТРАКТ. Пользователь в UI видит ТОЛЬКО видимый заголовок "## Q<N>" и тело ответа этого Q,
затем 4 варианта A/B/C/D. Поле question_text не показывается. Каждый блок обязан быть отвечаем
ИСКЛЮЧИТЕЛЬНО из материала своего Q<N>.

ЖЁСТКИЕ ПРАВИЛА:
1. correct — прямая проверка ОДНОГО центрального тезиса (anchor claim) именно этого Q, отвечающего
   на его видимый заголовок. НЕ мини-конспект всего Q: если тезис не влезает в одно предложение
   на 12-15 слов, ты выбрал слишком широкий якорь — сузь его.
   ЯКОРЬ = ОТВЕТ НА ВИДИМЫЙ ЗАГОЛОВОК, А НЕ ЛЮБОЙ ФАКТ ИЗ ТЕЛА.
2. Каждый из 3 дистракторов — правдоподобная ОДНООСЕВАЯ инверсия того же тезиса, ложность которой
   устанавливается ИЗ ТЕЛА ЭТОГО ЖЕ Q. Поле axis — назови ось искажения одной фразой.
   Дистрактор отвечает НА ТОТ ЖЕ ВОПРОС, что correct, а не на смежный: не подменяй «что это»
   на «зачем нужно» или «как чинить» — такой вариант отбрасывается слишком легко.
3. НОЛЬ novel facts: не вводи API, классы, механизмы, числа, которых нет в теле этого Q.
4. НОЛЬ neighbor-leak: нельзя опираться на соседние Q.${A.dup_note ? `\n   ОСОБО В ЭТОМ ФАЙЛЕ: ${A.dup_note}` : ''}
5. Версии, годы, номера релизов, проценты и цены — ВНЕ текстов опций.
6. Не пиши слова: якобы, будто, почему-то. Избегай в correct абсолютных маркеров
   (всегда/никогда/только/любой/все/целиком/единственный/лишь/достаточно).
7. correct НИКОГДА не описывает свойство ТЕКСТА шпаргалки («перечислены», «для каждого пункта
   указано», «со ссылкой на вопрос») — только предметный факт.

🔴 8. ВИЗУАЛЬНЫЙ ПАРИТЕТ — САМЫЙ ЧАСТЫЙ ДЕФЕКТ КОРПУСА. Правильный ответ не должен отличаться
   от дистракторов ФОРМОЙ. Замер по корпусу показал: correct выделяется числом частей фразы
   в 59% блоков и длиной в символах в 56%. Держи у всех ЧЕТЫРЁХ опций одинаковыми:
   - число частей фразы: посчитай у correct запятые и тире — у каждого дистрактора столько же;
   - длину В СИМВОЛАХ: коридор 0.85-1.15 от correct (не только в словах!);
   - число код-спанов в обратных кавычках: ±1 от correct (иначе при равной длине у дистрактора
     больше обычных слов и он выдаёт себя);
   - противопоставление («а», «тогда как», тире): либо у всех, либо ни у кого;
   - отрицания («не», «без», «вместо») не должны скапливаться только в дистракторах.
   Проверь себя: закрой правильный ответ и посмотри на четыре строки — если одна заметно
   длиннее и сложнее устроена, читатель выберет её не думая. Это провал независимо от linkage.
9. При этом НЕ клонируй зачин correct в дистракторы: все четыре начинаются по-разному и не имеют
   общих кусков длиннее 4-5 слов подряд. Дистрактор ложен ЦЕЛИКОМ, а не «верное начало + ложный хвост».
10. Не разделяй половины тезиса в correct через ";" или ":" — пиши союзом, запятой или тире.
11. Опции — по-русски, идентификаторы кода латиницей в backticks.

СЕКЦИИ (обучающая проза, 1–3 предложения каждая, по-русски):
- correct_sections: explanation (почему верно, со ссылкой на формулировку тела), example (конкретика
  из тела Q), when_to_apply (когда правило применяют), edge_cases (граница из тела), related (смежный
  тезис ЭТОГО ЖЕ Q).
- у каждого wrong: what_actually (как на самом деле по телу), source_of_confusion (почему ошибка
  правдоподобна), if_it_were_true (что следовало бы наблюдать), how_it_should_be (корректная формулировка).

Верни СТРОГО объект {"blocks":[...]} по схеме, ровно по одному блоку на каждый Q из Q${c.lo}..Q${c.hi},
q_number проставь настоящий. Ничего кроме структурированного ответа.`
}

phase('Authoring')
const res = await parallel(A.chunks.map(c => () =>
  agent(prompt(c), { label: `author:${c.label}`, phase: 'Authoring', schema: BLOCK_SCHEMA })))

const blocks = res.filter(Boolean).flatMap(r => r.blocks || [])
log(`получено блоков: ${blocks.length}`)
return { count: blocks.length, q_numbers: blocks.map(b => b.q_number).sort((a, b) => a - b),
         chunks_ok: res.filter(Boolean).length, chunks_total: A.chunks.length }
