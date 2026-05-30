# MCQ existing-seed quality audit (2026-05-30)

Gate sweep over all committed seeds: **85 PASS**, 98 FAIL.


## SEVERE — broken alignment (options don't attach / orphans) — 12 files
Regenerate or restore md headings.

- `algorithms/algorithmic-paradigms/backtracking-interview` — orph=0 miss=1 dup=0 contra=0 empty=0
- `algorithms/data-structures/graphs-interview` — orph=0 miss=2 dup=0 contra=0 empty=0
- `algorithms/data-structures/hash-tables-interview` — orph=0 miss=2 dup=0 contra=0 empty=0
- `frameworks/spring/spring-modulith-interview` — orph=13 miss=0 dup=0 contra=0 empty=0
- `leadership/mentoring-interview` — orph=24 miss=0 dup=0 contra=0 empty=0
- `leadership/technical-decisions-interview` — orph=21 miss=0 dup=0 contra=0 empty=0
- `programming-languages/java/java-8-interview` — orph=0 miss=10 dup=0 contra=0 empty=0
- `programming-languages/java/java-collections-interview` — orph=0 miss=12 dup=0 contra=0 empty=0
- `programming-languages/java/java-core-interview` — orph=0 miss=11 dup=0 contra=0 empty=0
- `programming-languages/java/java-exceptions-interview` — orph=0 miss=35 dup=0 contra=0 empty=0
- `programming-languages/java/java-generics-interview` — orph=0 miss=33 dup=0 contra=0 empty=0
- `programming-languages/java/java-stream-interview` — orph=0 miss=36 dup=0 contra=0 empty=0

## READABILITY — egregious info-ratio (1-word vs paragraph sections) — 23 files
Rebuild sections to info-equivalent length.

- `ai-ml/ai-compliance-governance-interview` — rmax=29.5 over4=26
- `ai-ml/ai-observability-interview` — rmax=14.3 over4=56
- `ai-ml/ai-safety-guardrails-interview` — rmax=14.8 over4=30
- `ai-ml/open-source-llms-interview` — rmax=40.0 over4=44
- `ai-ml/rag-interview` — rmax=7.4 over4=34
- `algorithms/data-structures/heaps-interview` — rmax=9.4 over4=30
- `databases/database-sharding-interview` — rmax=12.9 over4=41
- `databases/mongodb-interview` — rmax=9.8 over4=32
- `databases/postgresql-interview` — rmax=6.9 over4=34
- `design-patterns/design-patterns-interview` — rmax=10.3 over4=70
- `devops/kubernetes-interview` — rmax=7.7 over4=35
- `programming-languages/java/java-concurrency-interview` — rmax=10.2 over4=38
- `system-design/design-dropbox-interview` — rmax=22.3 over4=37
- `system-design/design-feed-system-interview` — rmax=13.2 over4=68
- `system-design/design-google-maps-interview` — rmax=24.9 over4=80
- `system-design/design-key-value-store-interview` — rmax=7.9 over4=33
- `system-design/design-netflix-interview` — rmax=146.7 over4=37
- `system-design/design-pastebin-interview` — rmax=18.9 over4=75
- `system-design/design-payment-system-interview` — rmax=15.7 over4=51
- `system-design/design-rate-limiter-interview` — rmax=11.8 over4=53
- `system-design/design-search-interview` — rmax=19.5 over4=38
- `system-design/design-typeahead-interview` — rmax=14.5 over4=24
- `system-design/design-url-shortener-interview` — rmax=11.2 over4=56

## MINOR — rotation/mild info-ratio on valid MCQ (good distribution) — 63 files
Low priority: deterministic reorder to strict rotation, optional.

- `ai-ml/agentic-patterns-interview` — rot=0 over4=21 rmax=7.3
- `ai-ml/ai-agents-interview` — rot=27 over4=4 rmax=6.3
- `ai-ml/ai-application-architecture-interview` — rot=0 over4=28 rmax=7.2
- `ai-ml/code-agents-interview` — rot=0 over4=22 rmax=7.7
- `ai-ml/embeddings-interview` — rot=22 over4=24 rmax=7.7
- `ai-ml/fine-tuning-llm-interview` — rot=0 over4=25 rmax=8.4
- `ai-ml/function-calling-interview` — rot=22 over4=6 rmax=7.6
- `ai-ml/inference-optimization-interview` — rot=0 over4=21 rmax=8.3
- `ai-ml/llm-basics-interview` — rot=0 over4=19 rmax=6.5
- `ai-ml/llm-evaluation-interview` — rot=0 over4=23 rmax=8.4
- `ai-ml/llm-integration-patterns-interview` — rot=21 over4=6 rmax=5.8
- `ai-ml/long-context-vs-rag-interview` — rot=0 over4=20 rmax=6.9
- `ai-ml/mcp-interview` — rot=21 over4=20 rmax=8.4
- `ai-ml/mlops-interview` — rot=23 over4=17 rmax=7.2
- `ai-ml/model-serving-interview` — rot=2 over4=2 rmax=4.5
- `ai-ml/multi-agent-orchestration-interview` — rot=29 over4=8 rmax=6.2
- `ai-ml/multimodal-ai-interview` — rot=0 over4=23 rmax=6.6
- `ai-ml/prompt-engineering-interview` — rot=23 over4=9 rmax=5.0
- `ai-ml/reasoning-models-interview` — rot=26 over4=11 rmax=5.6
- `ai-ml/vector-databases-interview` — rot=0 over4=10 rmax=5.5
- `algorithms/algorithmic-paradigms/divide-and-conquer-interview` — rot=5 over4=2 rmax=4.2
- `algorithms/algorithmic-paradigms/dynamic-programming-interview` — rot=25 over4=4 rmax=4.5
- `algorithms/algorithms-interview` — rot=11 over4=2 rmax=5.1
- `algorithms/complexity/complexity-analysis-interview` — rot=26 over4=7 rmax=4.7
- `algorithms/data-structures/arrays-strings-interview` — rot=27 over4=1 rmax=4.2
- `algorithms/data-structures/linked-lists-interview` — rot=0 over4=7 rmax=5.7
- `algorithms/data-structures/stacks-queues-interview` — rot=15 over4=13 rmax=5.2
- `algorithms/data-structures/trees-interview` — rot=16 over4=2 rmax=4.5
- `algorithms/data-structures/tries-interview` — rot=19 over4=1 rmax=4.5
- `api/api-versioning-interview` — rot=15 over4=13 rmax=7.1
- `api/rest-maturity-interview` — rot=8 over4=2 rmax=5.5
- `architecture/cdn-interview` — rot=23 over4=26 rmax=8.3
- `architecture/dns-interview` — rot=22 over4=6 rmax=5.4
- `architecture/hexagonal-architecture-interview` — rot=0 over4=28 rmax=9.1
- `architecture/latency-numbers-interview` — rot=22 over4=2 rmax=5.0
- `architecture/reverse-proxy-interview` — rot=21 over4=13 rmax=5.2
- `architecture/service-discovery-interview` — rot=21 over4=5 rmax=5.1
- `behavioral/conflict-stories-interview` — rot=16 over4=2 rmax=4.2
- `behavioral/culture-fit-interview` — rot=17 over4=3 rmax=4.9
- `behavioral/failure-stories-interview` — rot=13 over4=0 rmax=3.2
- `behavioral/leadership-stories-interview` — rot=15 over4=15 rmax=6.6
- `behavioral/star-method-interview` — rot=21 over4=16 rmax=8.0
- `databases/database-replication-interview` — rot=22 over4=25 rmax=8.0
- `databases/hibernate-interview` — rot=0 over4=14 rmax=5.8
- `databases/sql-interview` — rot=0 over4=27 rmax=5.9
- `leadership/conflict-resolution-interview` — rot=16 over4=17 rmax=6.8
- `leadership/estimations-planning-interview` — rot=0 over4=12 rmax=6.0
- `leadership/tech-interviewing-interview` — rot=14 over4=20 rmax=9.2
- `messaging/kafka-interview` — rot=0 over4=27 rmax=9.3
- `messaging/redpanda-interview` — rot=17 over4=0 rmax=3.9
- `programming-languages/java/java-completable-future-interview` — rot=4 over4=0 rmax=3.7
- `programming-languages/java/java-optional-interview` — rot=12 over4=1 rmax=4.2
- `reactive/project-reactor-interview` — rot=0 over4=18 rmax=8.3
- `reactive/rxjava-interview` — rot=0 over4=29 rmax=7.8
- `system-design/design-ecommerce-delivery-interview` — rot=32 over4=0 rmax=3.6
- `system-design/design-elevator-oo-interview` — rot=0 over4=3 rmax=4.4
- `system-design/design-instagram-interview` — rot=20 over4=20 rmax=8.3
- `system-design/design-parking-lot-oo-interview` — rot=21 over4=7 rmax=9.2
- `system-design/design-twitter-interview` — rot=21 over4=15 rmax=5.5
- `system-design/design-uber-interview` — rot=0 over4=20 rmax=5.3
- `system-design/design-vending-machine-oo-interview` — rot=9 over4=13 rmax=5.6
- `system-design/design-web-crawler-interview` — rot=20 over4=0 rmax=3.3
- `system-design/design-youtube-interview` — rot=0 over4=20 rmax=7.3
