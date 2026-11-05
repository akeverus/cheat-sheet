# Q25 targeted reassessment — Agile/Scrum test-practice sections

Scope: Q25 only. Re-read the full q25 disclosure block: question, theory, options A–D, and every option section. This is a supplemental review; registered r1 remains unchanged.

Required meaning of the visible question: describe how testing is organized in Agile/Scrum. It is broad and does not specify a team policy, chosen test techniques, or automation maturity.

## Targeted finding

Option C is marked wrong, but its corrective sections contain prescriptive claims:

- `what_actually`: “Регрессия автоматизирована и запускается в CI — вручную её не гоняют.”
- `how_it_should_be`: “CI гоняет регрессию на каждый коммит.”

These statements are too absolute for the visible Agile/Scrum context. The Scrum Guide 2020 says Scrum is purposefully incomplete and that techniques/methods vary by context (pp. 2–3, lines 17–20, 64–71 in the official PDF). It assigns Developers accountability for quality through adherence to the Definition of Done (p. 5, lines 135–140), and states quality does not decrease during the Sprint (p. 7, lines 194–198). It does not require regression automation, CI, manual regression prohibition, or a per-commit cadence. A team may choose those practices, but the sections state them as universal facts. Therefore the assertion that manual regression is not performed and CI runs it every commit is not supportable as a general Scrum rule.

Option B’s `what_actually` says “Exploratory-сессии проводят каждый спринт — time-boxed, с charter”; D’s `edge_cases` likewise says “Exploratory — time-boxed сессии каждый спринт.” The Guide does not mandate exploratory sessions. These may be useful team practices, but “every Sprint” is prescriptive beyond the framework. The wrong text B correctly rejects no use of exploratory testing as a universal claim, yet its correction makes the opposite universal claim. This is a sections-consistency defect and an unverified practice assertion, not evidence that exploratory testing is inappropriate.

Option A’s `what_actually` says “в Scrum тестирование вплетено в каждый шаг спринта.” The Guide supports cross-functional accountability for product-related activities and Developers’ responsibility for quality, but it does not prescribe testing at every step or define a QA role. This is a reasonable Agile recommendation if framed as a practice, not a literal Scrum rule.

D’s visible answer also says “всегда вплетено от планирования до ретро.” The Scrum Guide places the Scrum events within the Sprint and stresses ongoing inspection/adaptation, but does not impose a test-specific workflow spanning those exact stages. As a recommendation, D’s gist is defensible; as a universal claim about Scrum, “всегда” overstates the source. The previous r1 CONTENT=FAIL remains appropriate; the more specific issue is its unsupported universality.

## Criteria

- FACTS: **FAIL** — sections assert universal regression automation/per-commit CI and exploratory sessions every Sprint; the primary source does not require these. They may be local practices, but no such context is given.
- SECTIONS: **FAIL** — corrective material for wrong options B/C/D introduces unsupported universal prescriptions; repeating a claim from local theory does not validate it.
- CONTENT: **FAIL retained** — visible D’s lifecycle guidance is plausible but overstates what Scrum mandates (“always”); the question gives no local policy context.
- LINKAGE: **PASS** — the sections are attached to relevant answers, though their assertions are defective.
- SFPL: **UNVERIFIED** — this targeted pass did not redo the visual form audit.
- ANSC: **FAIL retained** — wrong alternatives are not full competing answers to the broad question.
- RU: **PASS retained** — no new Russian readability issue identified in this targeted review.

Primary source: [The Scrum Guide, November 2020](https://scrumguides.org/docs/scrumguide/v2020/2020-Scrum-Guide-US.pdf), accessed 2026-11-05. Exact scope: framework is intentionally incomplete and methods vary by context; Developers own quality via DoD; quality does not decrease during a Sprint. The guide does not prescribe CI/regression cadence or exploratory sessions.

Input block hashes: mcq `b38936bb58a77040496085eda5d3c62bbe298daea5eb3348563819089635eb66`; theory `4237929954228aeb0c579c10f193146bf8fe527d6d1b61c6d128d6da4c5b54af`.
