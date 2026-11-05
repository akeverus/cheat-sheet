# Independent review: Python packaging Q4:B0

- Coverage: Q4 question, theory body, visible A/B/C/D, and all option sections in the supplied `review-input.json` snapshot.
- Snapshot hashes: MCQ `484b22c…2626f`; theory `98504a4…218bc9`; block hashes are recorded in `review-input.json`.
- Rules reviewed: MCQ quality standard, SFPL/ANSC rules, and Russian readability rules (hashes in `review-input.json`).
- Passes: Phase A blind choice was C before disclosure; Phase B used the revealed key and consulted official pip/PyPA sources.

## Content and evidence

The question asks how to make dependency installation repeatable in CI and deployment. The theory correctly distinguishes compatibility ranges from a locked resolution, calls for pinning direct and transitive versions, and describes hashes as checking downloaded artifact contents. pip's repeatable-installs guide says a pinned requirements file can pin top-level and transitive dependencies, describes hash checking as an additional control, and warns that compiled wheelhouses may be OS/architecture specific. pip's secure-installs guide requires all requirements and dependencies to be pinned and hashed in hash-checking mode. PyPA's `pylock.toml` specification defines a format for dependencies enabling reproducible installation; the existence of the format alone does not establish support in every installer.

| Option | Assessment |
|---|---|
| A | Incorrect but plausible partial strategy. Pinning only direct requirements while resolving transitive dependencies afresh leaves the installed tree free to change. The target-environment check is sensible but cannot repair the missing lock. Its sections state this directly. |
| B | Incorrect. Version ranges allow different releases, and a build backend for the project does not fix external dependency resolution or guarantee immutable index artifacts. Hashes verify particular files; they do not select a single full dependency tree. Sections match this error. |
| C | Correct. A full version tree plus artifact hashes makes the selected set and downloaded files explicit; checking installation against target Python/platform combinations is an appropriate deployment validation. Its edge-case note correctly calls out pip's all-requirements/all-dependencies hash requirement and installer-specific lock support. |
| D | Incorrect. Pinning and hashes do not make binary wheels portable across incompatible OS, architecture, Python ABI, or wheel tags. The official pip guide explicitly cautions that compiled wheelhouses are typically OS/architecture specific. Sections explain the portability confusion. |

All four are defensible as complete, single-strategy answers to the visible question. A, B, and D each make one recognizable but consequential mistake; their `what_actually`, `source_of_confusion`, counterfactual, and correction sections address the current wording. C's explanation/example/application/edge-case/related sections are consistent with its option and the theory.

## Form and completeness

| Label | Type / coverage | Concrete detail and visible form | Completeness |
|---|---|---|---|
| A | Recommendation with procedure and target check | Direct vs transitive dependencies; semicolon; target Python/platform | Complete sentence, similar two-part recipe; wrong because it leaves transitive resolution open |
| B | Recommendation with premise and target check | Version ranges, build backend, hashes; semicolon | Complete sentence, similar two-part recipe; wrong premise about backend/archive stability |
| C | Recommendation with sequence and target check | Full version tree, hashes, target Python/platform | Complete sentence; answers both repeatability and deployment validation |
| D | Recommendation with sequence and compatibility claim | Full tree, hashes, wheelhouse portability; explicit universal claim | Complete sentence; addresses deployment, but its universal portability claim is false |

### SFPL — PASS

The prior finding `Q4-B0-SFPL-01` is resolved. In this revision C is not the lone-longest option: A and D are at least as long in Russian word count, while all four use a comparable recommendation/steps structure and mention target conditions or deployment outcomes. C does not uniquely contain a caveat or example-like detail needed to answer the question; A/B/D are also syntactically complete and specific. The semantic difference that makes C correct is the intended knowledge test, not a detectable completeness or polish advantage. Phase A did note C's form as potentially guessable because it is the balanced/full-tree choice, but that cue does not survive comparison of the revised four texts: D also says “полное дерево” and “hashes”, and A/B have similarly developed clauses. C is shorter than D and not materially more elaborated. The original lone-longest/full-recipe defect no longer applies.

### ANSC — PASS

Each option independently answers the question with a full recommendation and a recognizable rationale/condition. A and B are not fragments or absurd claims: they model common confusions between direct and transitive requirements, project builds and dependency resolution. D is an overgeneralized portability assumption, clearly a plausible but false deployment model. Their sections explain exactly why those models fail, without turning an incorrect option into a second correct answer. C is complete without monopolizing the answer structure.

### Russian readability — PASS

The visible question is clear. The theory and options use understandable technical Russian for an intermediate packaging audience. Terms such as `hashes`, `wheelhouse`, `build backend`, CI and deployment are conventional in this context; option syntax and punctuation are readable. All option sections were reviewed, including `example` and `related`. No language defect makes a wrong answer easier to reject. Minor code-switching is consistent across the block and does not impede comprehension.

## Check results

- Content review: PASS — one defensible correct answer, C; A/B/D are incorrect for the stated reasons.
- Factual correctness: PASS — checked against current official pip repeatable/secure installs and PyPA pylock/wheel specifications.
- Source linkage: PASS — cited theory links directly support pinning, hashes, lock-format scope, and wheelhouse portability; the MCQ's related link points to Q4.
- Sections consistency: PASS — current text and each option's explanatory sections agree; no old option wording is referenced.
- SFPL: PASS — prior finding resolved, see comparison above.
- ANSC: PASS — all four are full, plausible strategies, with one central error in each distractor.
- Russian readability: PASS — full supplied Q4 surface read.
- Independent review: PASS — this review independently assessed the snapshot and used the recorded Phase A response as pre-disclosure evidence, not as proof of quality.
- Blind review: PASS — Phase A selected C before the key was revealed; that result is reported separately from form parity.
- Final acceptance: PASS for this reviewed block, with no open finding.

## Limitations

The supplied snapshot contains Q4's theory but not the full Q2 body referenced in `related`; I did not infer an independent Q2 review. The block's claim about testing a supported `pylock.toml` installer is appropriately qualified and was not evaluated for a named tool/version because none is specified. Review covers only Q4:B0 at the recorded snapshot hashes.
