# Independent Phase B review

Review id: 20261105-heaps-five-followup-r2  
Coverage: Q8, Q15, Q16, Q17, Q28; each visible question, theory body, A–D, and all option sections. Key was unavailable in Phase A and available here. The independently recorded Phase A choices were 8-A, 15-A, 16-A, 17-B, 28-D; all match the local key.

Checks per block use the required keys: content_review, factual_correctness, source_linkage, sections_consistency, sfpl, ansc, russian_readability, independent_review, blind_review. Every block has all nine checks; all are PASS. Status vocabulary: PASS / FAIL / UNVERIFIED. No whole-file acceptance is inferred.

## Q8:B0

- factual_accuracy: PASS — A’s height-sum argument is correct; B/C misstate the height distribution; D confuses total work with a one-swap-per-node bound.
- unique_answer: PASS — A is the only defensible explanation for linear buildHeap.
- local_linkage: PASS — the Q8 body gives the same height-count summation and explicitly explains why n·log n is loose.
- distractors: PASS — each wrong option represents a recognizable error (worst-case multiplication, reversing node distribution, or confusing aggregate cost with per-call cost).
- sections: PASS — explanations address the current claims and do not contradict their options.
- sfpl: PASS — each is a one-sentence causal explanation with comparable level and punctuation; no obvious length/completeness tell.
- ansc: PASS — each option directly attempts to explain the complexity. A is not uniquely responsive by virtue of covering a missing subquestion.
- ru: PASS — clear technical Russian; borrowed terms and notation are standard and readable.
- form_cues: PASS — no material cue observed.

## Q15:B0

- factual_accuracy: PASS — A correctly gives logarithmic offer/poll and linear contains/remove. Oracle Java SE 25 states the same bounds.
- unique_answer: PASS — A is the only defensible option.
- local_linkage: PASS — Q15 theory table and prose state these operation costs and explain the search reason.
- distractors: PASS — B swaps search and sift costs; C mistakes heap order for search-tree order; D omits reheapification.
- sections: PASS — the explanations refute the current wrong texts and accurately elaborate A.
- sfpl: PASS — all four share a parallel pair-of-operation-groups structure, similar syntax, and comparable length.
- ansc: PASS — every option covers both operation groups and gives a reason; A is not the only full response.
- ru: PASS — concise, idiomatic technical phrasing.
- form_cues: PASS — no material cue observed.

## Q16:B0

- factual_accuracy: PASS — A is correct. Java SE 25 says iterator traversal has no guaranteed order; ordered traversal can use sorting, and repeated poll retrieves by priority.
- unique_answer: PASS — A alone is defensible.
- local_linkage: PASS — the local body explicitly says iteration guarantees neither insertion nor priority order, and describes poll/sort alternatives. In particular, D’s insertion-order claim is refuted by this body; rejecting it does not depend on recognizing an unfamiliar API or relying on outside terminology.
- distractors: PASS — B confuses iterator with repeated extraction, C mistakes comparator ordering for iterator sorting, D confuses a heap with FIFO/insertion-order traversal.
- sections: PASS — all explanations target the stated claims; A’s edge-case and related details are consistent with the theory.
- sfpl: PASS — all options follow claim → consequence/recommendation, with similar one-sentence forms.
- ansc: PASS — each addresses whether iteration is ordered and what that implies.
- ru: PASS — clear wording and consistent terminology.
- form_cues: PASS — “offer” in D is an API term, but its false insertion-order assertion is directly negated by the local body; no actionable linkage novelty finding.

## Q17:B0

- factual_accuracy: PASS — B is correct. Oracle Java SE 25 documents PriorityQueue as unsynchronized; PriorityBlockingQueue is logically unbounded and provides blocking retrieval; put does not wait for capacity, while take waits for an element. The local theory additionally correctly distinguishes external locking and the JDK 25 delayed-task queue.
- unique_answer: PASS — B alone is defensible.
- local_linkage: PASS — Q17 body contains each condition and distinction needed by the answer.
- distractors: PASS — A confuses thread-safety with blocking, C imports bounded-queue behavior, and D confuses mutual exclusion with condition waiting.
- sections: PASS — all wrong explanations answer the current claims. B’s sections accurately retain caveats.
- sfpl: PASS — visual comparison of all four visible texts finds a shared two-clause contrast structure and similar completeness/detail. The code-excluded OPTION_LENGTH_RATIO=1.50 signal was considered without relaxing any threshold; it does not correspond to a lone visibly fuller or more polished answer on inspection. This is a human form judgment, not a claim that the scanner signal disappeared.
- ansc: PASS — A/B compare PriorityQueue and PriorityBlockingQueue; C fully states its PriorityBlockingQueue capacity/put model; D fully states its external-lock/PriorityQueue model. B is not uniquely complete merely because each distractor focuses on a distinct misconception.
- ru: PASS — readable mixed Russian/API prose; “при совместном изменении” is understandable in the concurrent-access context.
- form_cues: PASS — no material visible cue after direct comparison; preserve the reported automated signal for tool-side accounting.

## Q28:B0

- factual_accuracy: PASS — D accurately describes the standard pairing-heap tree, root linking, and child-subheap pairing after deleteMin. The primary paper describes this self-adjusting heap and its two-pass delete-min approach.
- unique_answer: PASS — D alone is defensible.
- local_linkage: PASS — Q28 body directly explains the tree, merge, and pairing of child subtrees.
- distractors: PASS — A transfers rank consolidation from a different heap model; B describes array binary heap; C omits consolidation of the remaining child subheaps.
- sections: PASS — sections consistently explain each option; D’s example matches the theory’s two-pass variant.
- sfpl: PASS — all options use the same definition + merge + deleteMin skeleton and similar punctuation/detail.
- ansc: PASS — all options attempt a definition and describe both named operations.
- ru: PASS — natural concise Russian with standard technical names.
- form_cues: PASS — no material cue observed.

## Findings

No blocking content or form finding observed in the reviewed five blocks. The Q17 code-excluded ratio signal remains a recorded diagnostic and should not be represented as cleared by a tool run; manual review found no visible completeness disparity. This review did not run repository gates.

## Sources checked (2026-11-05 UTC)

- Oracle Java SE 25 PriorityQueue API — operation complexity, unsynchronized behavior, iterator ordering: https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/PriorityQueue.html
- Oracle Java SE 25 PriorityBlockingQueue API — logically unbounded semantics and blocking retrieval: https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/PriorityBlockingQueue.html
- OpenJDK jdk-25-ga ScheduledThreadPoolExecutor source — JDK 25 implementation / DelayedWorkQueue: https://github.com/openjdk/jdk/blob/jdk-25-ga/src/java.base/share/classes/java/util/concurrent/ScheduledThreadPoolExecutor.java
- Oracle Java SE 25 ConcurrentSkipListMap API — sorted concurrent-map semantics and expected-average O(log n) core operations: https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ConcurrentSkipListMap.html
- Fredman, Sedgewick, Sleator, Tarjan, “The Pairing Heap” primary paper: https://www.cs.cmu.edu/~sleator/papers/pairing-heaps.pdf

Limitations: The CMU PDF was opened as a scanned PDF with no extractable text in the browser result; the primary source was checked visually through the paper’s pages. No code gates or automated ratio calculation were run in this review.
