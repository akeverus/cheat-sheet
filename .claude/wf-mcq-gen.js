export const meta = {
  name: 'mcq-gen',
  description: 'Generic generate-only MCQ-seed engine: one chunked Python generator per relative-path topic, aligned to md Q-numbers',
  phases: [
    { title: 'Generate', detail: 'chunked Python generator per topic, mirrors md relative path under seed/mcq/' },
  ],
}

// args = array of relative paths (md = cheatsheets/interview/<rel>.md, json = seed/mcq/<rel>.json,
// topic_slug = basename(rel)). Example item: "programming-languages/go-interview" or "testing/junit-interview".
const MD = (rel) => 'cheatsheets/interview/' + rel + '.md'
const JSON_PATH = (rel) => 'modules/quiz-app/src/main/resources/seed/mcq/' + rel + '.json'
const SLUG = (rel) => rel.split('/').pop()

function genPrompt(rel) {
  const slug = SLUG(rel)
  const safe = slug.replace(/[^a-z0-9]+/gi, '_')
  return [
    'Generate a NEW aligned MCQ JSON seed in the cheat-sheet repo at /Users/sergeyvoronin/IdeaProjects/cheat-sheet. Work from that directory. Use dangerouslyDisableSandbox: true on EVERY Bash call.',
    '',
    '## Why alignment matters (read carefully)',
    'The quiz app McqJsonLoader attaches each JSON question option to the markdown question with the SAME q_number. The JSON question_text is NEVER displayed; the user always sees the markdown question. So options for q_number=N MUST answer markdown "## QN", using the markdown answer as source of truth. A q_number with no matching md ## QN is silently dropped (orphan); every md ## QN must get a json question.',
    'Linkage is by file path + topic_slug: the DB topic key is the md path relative to cheatsheets/interview/ (minus .md), and topic_slug MUST equal the filename. So you MUST: (a) set "topic_slug" to exactly "' + slug + '"; (b) write the file to exactly ' + JSON_PATH(rel) + ' (create parent dirs with mkdir -p if needed).',
    '',
    '## CRITICAL — avoid the 32000 output-token limit',
    'Large topics (40+ questions) WILL exceed the 32000 output-token limit if written in one tool call, and the write FAILS silently. Build the JSON with a Python generator in CHUNKS:',
    '1. Write a generator skeleton at /tmp/gen_' + safe + '.py with helper functions plus the FIRST ~8 questions.',
    '2. Edit-append the next ~8 questions at a time (repeat until ALL md questions are covered). Each Edit stays small.',
    '3. The generator builds {"topic_slug": "' + slug + '", "questions": [...]} and dumps via json.dump(obj, open(path, "w", encoding="utf-8"), ensure_ascii=False, indent=2). Use os.makedirs(os.path.dirname(path), exist_ok=True) first.',
    '4. Run: python3 /tmp/gen_' + safe + '.py to write ' + JSON_PATH(rel) + '.',
    '',
    'Match format EXACTLY by reading these committed gold-standard seeds FIRST:',
    '- modules/quiz-app/src/main/resources/seed/mcq/security/supply-chain-security-interview.json',
    '- modules/quiz-app/src/main/resources/seed/mcq/architecture/microservices-interview.json',
    '',
    '## Source of truth',
    'Read ' + MD(rel) + ' fully. Find EVERY "## Q<N>. <title>" heading. For EACH one produce exactly one JSON question with q_number=N whose 4 options answer THAT exact question, with the correct option derived from the markdown answer.',
    '',
    '## Output file',
    JSON_PATH(rel),
    '',
    '## Hard requirements',
    '- Top-level: {"topic_slug": "' + slug + '", "questions": [...]}. One question object per md ## QN, q_number=N.',
    '- Question: {"q_number": N, "blocks": [{"block_idx": 0, "question_text": "<restate the md question in Russian>", "options": [A,B,C,D]}]}.',
    '- Options array ALWAYS in label order A,B,C,D with "order" 0,1,2,3. Exactly ONE option has "correct": true.',
    '- Rotation (STRICT, non-negotiable): the correct option label for question N MUST equal "ABCD"[(N-1)%4] — Q1→A, Q2→B, Q3→C, Q4→D, Q5→A, ... Place the correct option at that array position (A=index0,B=1,C=2,D=3).',
    '- Correct option: 5 non-empty sections: explanation, example, when_to_apply, edge_cases, related.',
    '- Wrong option: 4 non-empty sections: what_actually, source_of_confusion, if_it_were_true, how_it_should_be.',
    '- related = wikilinks to OTHER q_numbers in THIS file: "[[' + slug + '#Q2]] краткое описание; [[' + slug + '#Q5]] краткое описание." Only reference q_numbers that actually exist in the md.',
    '- Russian prose. Technical terms, code, product names in `backticks`. Human-readable, concrete examples (real APIs/algorithms/values where relevant). Single-delta plausible distractors — each a mistake a mid-level engineer could genuinely make. NO duplicate distractor texts within a block. A wrong option text must NOT be identical to its own what_actually. No bold/markdown headers inside option text.',
    '- Info-equivalent length: WITHIN each option, max-section-length / min-section-length ratio MUST be <= 4. Every section a full sentence or two. NO one-word sections (they blow the ratio).',
    '',
    '## Self-validate before finishing (MANDATORY — keep fixing until clean)',
    'Write a small python check that parses ' + MD(rel) + ' and ' + JSON_PATH(rel) + ' and verifies: orphans (json q_numbers not in md)=0; missing (md ## Q numbers absent from json)=0; rotation: for every q, correct label == "ABCD"[(q_number-1)%4], violations=0; within every option max/min section-length ratio <= 4 (over-4 count=0); no empty sections; no duplicate option texts within a block. Keep editing the generator and re-running until ALL checks are clean.',
    '',
    'When fully clean, return ONE short plain-text line (NOT JSON): "' + rel + ': written, <qcount> questions, dist A/B/C/D, all checks clean". If something is still broken, say exactly what.',
  ].join('\n')
}

phase('Generate')
const topics = (Array.isArray(args) ? args : JSON.parse(args))
log('mcq-gen: ' + topics.length + ' topics → generate-only (chunked Python generators)')

const results = await parallel(
  topics.map((rel) => () =>
    agent(genPrompt(rel), { label: 'gen:' + SLUG(rel).replace('-interview', ''), phase: 'Generate' })
  )
)

const done = results.filter(Boolean)
log('Generation finished: ' + done.length + ' / ' + topics.length + ' agents returned')
return { total: topics.length, returned: done.length, summaries: done, topics }
