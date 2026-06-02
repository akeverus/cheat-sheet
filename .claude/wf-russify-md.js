export const meta = {
  name: 'russify-md',
  description: 'Русифицировать англоязычную ПРОЗУ ответов в interview .md (тело, не заголовки)',
  phases: [{ title: 'Russify', detail: 'один агент на файл: тело-проза EN→RU, ## Q/код/TOC/json неприкосновенны' }],
}

// Батч встраивается прямо сюда (args через границу инструмента приходит строкой —
// ненадёжно; редактируй BATCH и перезапускай Workflow со scriptPath).
const BATCH = [
  {"rel":"architecture/cdn-interview","cyr":0.442,"q":30},
  {"rel":"ai-ml/open-source-llms-interview","cyr":0.444,"q":34},
  {"rel":"data-engineering/apache-airflow-interview","cyr":0.446,"q":28},
]
let parsed = args
if (typeof parsed === 'string') { try { parsed = JSON.parse(parsed) } catch (e) { parsed = null } }
const items = Array.isArray(parsed) && parsed.length ? parsed : BATCH
log(`Русификация прозы: ${items.length} файлов`)

const SCHEMA = {
  type: 'object',
  additionalProperties: false,
  required: ['rel', 'ok', 'q_before', 'q_after', 'edits', 'note'],
  properties: {
    rel: { type: 'string' },
    ok: { type: 'boolean', description: 'true только если все инварианты соблюдены и проза русифицирована' },
    q_before: { type: 'integer' },
    q_after: { type: 'integer' },
    edits: { type: 'integer', description: 'сколько Edit-замен сделано' },
    note: { type: 'string', description: 'кратко: что переведено, любые сомнения' },
  },
}

const PROMPT = (it) => `Ты редактор-переводчик технических шпаргалок. Файл: \`cheatsheets/interview/${it.rel}.md\` (тема для собеседований). Сейчас ПРОЗА ответов написана на английском (cyr-ratio ${it.cyr}). Задача — переписать ПРОЗУ на естественный русский, сохранив техническую точность и СТРОГО сохранив структуру.

ЧТО ПЕРЕВЕСТИ на русский:
- Абзацы-объяснения, пояснительные предложения, буллет-пункты с текстом, жирные подписи-ярлыки (напр. \`**Idea:**\` → \`**Идея:**\`), текст в ячейках таблиц.
- Полные английские фразы вида «Software trusts dependencies implicitly», «Updates auto-applied», «run logic at edge of network» → нормальный русский.

ЧТО ОСТАВИТЬ КАК ЕСТЬ (НЕ ТРОГАТЬ):
1. **Строки заголовков \`## Q<N>. ...\` — НЕ менять ВООБЩЕ** (ни текст, ни номер): их текст формирует GitHub-якоря, по которым линкуются TOC и MCQ-json. Изменишь — порвёшь ссылки и выравнивание.
2. **Блок \`## Содержание\` (TOC) и любые якорные ссылки \`[...](#...)\` — НЕ трогать.**
3. **Код в \`\`\`-блоках — дословно, байт-в-байт, ВКЛЮЧАЯ комментарии.** НЕ добавляй и НЕ переводи комментарии внутри кода. Ни одного символа внутри \`\`\`-блока менять нельзя.
4. **Inline-код \`like_this\`, имена команд/классов/продуктов/API, стандартные аббревиатуры (TLS, CDN, SBOM, RTT, POP, mTLS, JWT и т.п.) — оставлять на английском.** Допустим билингвальный регистр: русское предложение со вкраплением англо-терминов, где это естественно для инженера.
5. **Frontmatter (между --- ---) — НЕ трогать.**
6. **\`## See also\` и секции ссылок — НЕ трогать.**
7. **Число и нумерацию \`## Q\` — сохранить точно (${it.q} штук).**

ЗАПРЕЩЕНО:
- Добавлять MCQ-callouts (\`> [!mcq]\`) или Tier-1 эмодзи-маркеры (❌ ПОСЛЕДСТВИЕ / ✓ ПРИМЕНЯТЬ / 📋 ПРАВИЛО / 🔗 См. Q) — pre-commit это банит.
- Удалять или досочинять смысл. Перевод, а не переписывание содержания. Технические факты сохраняются 1:1.
- Менять JSON-сидер (его не трогаешь вообще).

МЕТОД: работай инструментом Edit точечно, секция за секцией (между \`## Q\`-заголовками). НЕ перезаписывай весь файл через Write (большой файл → риск потерять контент). Inline-код и backtick-спаны переноси в новый русский текст без изменений.

ПОСЛЕ правок самопроверка (Bash, dangerouslyDisableSandbox=true):
\`\`\`
F=cheatsheets/interview/${it.rel}.md
echo "Q=$(grep -cE '^## Q[0-9]+' "$F")"   # должно быть ${it.q}
echo "callouts=$(grep -cE '^> \\[!mcq' "$F")"   # 0
echo "fences=$(($(grep -cE '^\\\`\\\`\\\`' "$F") % 2))"   # 0
python3 - "$F" <<'P'
import sys,re
t=open(sys.argv[1],encoding='utf-8').read()
t=re.sub(r'^---.*?\\n---','',t,count=1,flags=re.S); t=re.sub(r'\\\`\\\`\\\`.*?\\\`\\\`\\\`','',t,flags=re.S)
t=re.sub(r'\\\`[^\\\`]*\\\`','',t); t=re.sub(r'(?m)^#{1,6}\\s.*$','',t)
lat=len(re.findall(r'[a-zA-Z]',t)); cyr=len(re.findall(r'[а-яА-ЯёЁ]',t))
print('cyr=%.3f' % (cyr/(lat+cyr) if lat+cyr else 1))   # цель >=0.55
P
\`\`\`
Если Q≠${it.q}, или callouts≠0, или fences≠0, или cyr<0.55 — доделай, пока не сойдётся.

⚠️ КРИТИЧНО: переведи ВСЮ англоязычную прозу по ВСЕМУ файлу — от первого \`## Q\` до \`## See also\`, все секции, не останавливайся на половине (cyr ДОЛЖЕН стать ≥0.55). Файл большой — работай методично секциями, но дойди до конца.

⚠️ В САМОМ КОНЦЕ ОБЯЗАТЕЛЬНО вызови инструмент StructuredOutput с объектом по схеме (rel="${it.rel}"). Без этого вызова работа не засчитается.`

await pipeline(
  items,
  (it) => agent(PROMPT(it), { label: `russify:${it.rel.split('/').pop()}`, phase: 'Russify', schema: SCHEMA })
    .then((r) => r || { rel: it.rel, ok: false, q_before: it.q, q_after: -1, edits: 0, note: 'agent returned null' })
)

return { processed: items.length }
