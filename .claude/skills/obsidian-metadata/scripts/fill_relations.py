#!/usr/bin/env python3
"""
Fill prerequisites/next/aliases in cheatsheet frontmatter using heuristics.

- Builds index of all {basename: relpath} pairs in cheatsheets/.
- Maps file basename -> curated list of prereq/next basenames (RELATIONS).
- For each file, fills prerequisites/next only if currently empty.
- Improves aliases: extracts "Title (English)" -> ["Title", "English"], adds known abbreviations.

Usage:
  python3 fill_relations.py --apply
  python3 fill_relations.py --check
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4] / "cheatsheets"

# basename (no .md) -> (prereq basenames, next basenames)
RELATIONS: dict[str, tuple[list[str], list[str]]] = {
    # architecture
    "saga-pattern":                  (["event-driven"],                ["resilience-patterns", "event-sourcing"]),
    "event-sourcing":                (["event-driven", "ddd"],         ["cqrs"]),
    "cqrs":                          (["ddd", "event-sourcing"],       ["event-driven"]),
    "event-driven":                  (["microservices"],               ["event-sourcing", "saga-pattern"]),
    "ddd":                           ([],                              ["event-sourcing", "cqrs", "hexagonal-architecture"]),
    "hexagonal-architecture":        (["clean-architecture"],          ["ddd"]),
    "clean-architecture":            (["solid-principles"],            ["hexagonal-architecture", "ddd"]),
    "microservices":                 (["distributed-systems-fundamentals"], ["saga-pattern", "resilience-patterns"]),
    "resilience-patterns":           (["distributed-systems-fundamentals"], ["circuit-breaker", "saga-pattern"]),
    "distributed-systems-fundamentals": ([],                           ["microservices", "saga-pattern", "resilience-patterns"]),
    "architecture-patterns":         (["solid-principles"],            ["clean-architecture", "ddd"]),
    "system-design-basics":          (["distributed-systems-fundamentals"], ["microservices"]),
    "enterprise-patterns-overview":  (["architecture-patterns"],       ["caching-patterns"]),
    "caching-patterns":              (["redis-basics"],                ["spring-cache"]),
    "solid-principles":              ([],                              ["clean-code", "design-patterns", "clean-architecture"]),
    "design-principles":             ([],                              ["solid-principles", "design-patterns"]),

    # patterns (GoF)
    "factory-method":                (["design-principles"],           ["abstract-factory", "builder"]),
    "abstract-factory":              (["factory-method"],              ["builder", "prototype"]),
    "builder":                       (["factory-method"],              ["prototype"]),
    "prototype":                     (["builder"],                     ["singleton"]),
    "singleton":                     (["solid-principles"],            ["factory-method"]),
    "adapter":                       (["solid-principles"],            ["bridge", "decorator"]),
    "bridge":                        (["adapter"],                     ["composite"]),
    "composite":                     (["bridge"],                      ["decorator", "iterator"]),
    "decorator":                     (["composite"],                   ["proxy", "chain-of-responsibility"]),
    "facade":                        (["adapter"],                     ["proxy"]),
    "flyweight":                     (["proxy"],                       []),
    "proxy":                         (["decorator"],                   ["facade"]),
    "chain-of-responsibility":       (["decorator"],                   ["command"]),
    "command":                       (["chain-of-responsibility"],     ["mediator", "memento"]),
    "iterator":                      (["composite"],                   ["visitor", "observer"]),
    "mediator":                      (["command"],                     ["observer"]),
    "memento":                       (["command"],                     []),
    "observer":                      (["iterator"],                    ["mediator", "publish-subscribe"]),
    "publish-subscribe":             (["observer"],                    ["event-driven"]),
    "state":                         (["strategy"],                    ["template-method"]),
    "strategy":                      (["solid-principles"],            ["state", "template-method"]),
    "template-method":               (["strategy"],                    ["visitor"]),
    "visitor":                       (["iterator"],                    ["interpreter"]),
    "interpreter":                   (["visitor"],                     []),
    "design-patterns":               (["solid-principles"],            ["enterprise-patterns-overview"]),

    # databases
    "postgres-basics":               ([],                              ["postgres-indexes", "postgres-mvcc", "postgres-types"]),
    "postgres-types":                (["postgres-basics"],             ["postgres-indexes"]),
    "postgres-indexes":              (["postgres-basics"],             ["postgres-tuning", "postgres-mvcc"]),
    "postgres-mvcc":                 (["postgres-basics"],             ["postgres-vacuum", "postgres-tuning"]),
    "postgres-vacuum":               (["postgres-mvcc"],               ["postgres-tuning"]),
    "postgres-tuning":               (["postgres-indexes", "postgres-mvcc"], ["postgres-replication"]),
    "postgres-replication":          (["postgres-basics"],             ["cdc-change-data-capture", "postgres-partitioning"]),
    "postgres-partitioning":         (["postgres-basics"],             ["postgres-tuning"]),
    "postgres-explain":              (["postgres-indexes"],            ["postgres-tuning"]),
    "postgres-jsonb":                (["postgres-basics", "postgres-types"], []),
    "postgres-extensions":           (["postgres-basics"],             []),
    "postgres-roles":                (["postgres-basics"],             []),
    "postgres-fts":                  (["postgres-basics"],             []),
    "postgres-locks":                (["postgres-mvcc"],               ["postgres-tuning"]),
    "pgbouncer":                     (["postgres-basics"],             ["postgres-tuning"]),
    "patroni":                       (["postgres-replication"],        []),
    "sql-basics":                    ([],                              ["postgres-basics", "sql-tuning"]),
    "sql-tuning":                    (["sql-basics", "postgres-indexes"], []),
    "transactions":                  (["sql-basics"],                  ["postgres-mvcc"]),
    "redis-basics":                  ([],                              ["redis-replication", "redis-persistence", "redis-cluster"]),
    "redis-replication":             (["redis-basics"],                ["redis-cluster", "redis-sentinel"]),
    "redis-cluster":                 (["redis-replication"],           []),
    "redis-sentinel":                (["redis-replication"],           ["redis-cluster"]),
    "redis-persistence":             (["redis-basics"],                ["redis-replication"]),
    "redis-pubsub":                  (["redis-basics"],                ["publish-subscribe"]),
    "redis-streams":                 (["redis-basics"],                ["redis-pubsub"]),
    "mongodb-basics":                ([],                              ["mongodb-indexes", "mongodb-replication"]),
    "mongodb-indexes":               (["mongodb-basics"],              ["mongodb-aggregation"]),
    "mongodb-replication":           (["mongodb-basics"],              ["mongodb-sharding"]),
    "mongodb-sharding":              (["mongodb-replication"],         []),
    "mongodb-aggregation":           (["mongodb-basics"],              []),
    "cassandra-basics":               ([],                              ["cassandra-modeling"]),
    "cassandra-modeling":            (["cassandra-basics"],            []),
    "elasticsearch-basics":          ([],                              ["elasticsearch-queries", "elasticsearch-indexes"]),
    "elasticsearch-queries":         (["elasticsearch-basics"],        []),
    "elasticsearch-indexes":         (["elasticsearch-basics"],        []),
    "hibernate-basics":              (["sql-basics"],                  ["hibernate-caching", "hibernate-performance", "spring-data-jpa"]),
    "hibernate-caching":             (["hibernate-basics", "redis-basics"], ["hibernate-performance"]),
    "hibernate-performance":         (["hibernate-basics"],            []),
    "hibernate-fetching":            (["hibernate-basics"],            ["hibernate-performance"]),
    "jpa-basics":                    (["sql-basics"],                  ["hibernate-basics", "spring-data-jpa"]),
    "cdc-change-data-capture":       (["postgres-replication", "kafka"], ["debezium"]),
    "debezium":                      (["cdc-change-data-capture"],     []),

    # frameworks - spring
    "spring-core":                   ([],                              ["spring-mvc", "spring-boot", "spring-data"]),
    "spring-boot":                   (["spring-core"],                 ["spring-actuator", "spring-data-jpa"]),
    "spring-mvc":                    (["spring-core"],                 ["spring-rest", "spring-security", "spring-webflux"]),
    "spring-rest":                   (["spring-mvc"],                  ["spring-security", "spring-hateoas"]),
    "spring-data":                   (["spring-core"],                 ["spring-data-jpa", "spring-data-redis"]),
    "spring-data-jpa":               (["spring-data", "hibernate-basics"], ["spring-cache"]),
    "spring-data-redis":             (["spring-data", "redis-basics"], ["spring-cache"]),
    "spring-security":               (["spring-mvc"],                  ["spring-oauth2", "jwt"]),
    "spring-oauth2":                 (["spring-security"],             ["jwt"]),
    "spring-cache":                  (["spring-core", "redis-basics"], ["caching-patterns"]),
    "spring-actuator":               (["spring-boot"],                 ["spring-boot-admin"]),
    "spring-boot-admin":             (["spring-actuator"],             []),
    "spring-webflux":                (["spring-core"],                 ["spring-reactive"]),
    "spring-reactive":               (["spring-webflux"],              []),
    "spring-kafka":                  (["spring-core", "kafka"],        ["spring-cloud-stream"]),
    "spring-cloud-stream":           (["spring-kafka"],                []),
    "spring-cloud":                  (["spring-boot"],                 ["spring-cloud-stream"]),
    "spring-batch":                  (["spring-core"],                 []),
    "spring-integration":            (["spring-core"],                 ["spring-messaging", "spring-kafka"]),
    "spring-messaging":              (["spring-core"],                 ["spring-kafka"]),
    "spring-websocket":              (["spring-mvc"],                  ["spring-messaging"]),
    "spring-aop":                    (["spring-core"],                 []),
    "spring-transaction":            (["spring-core", "transactions"], ["spring-data-jpa"]),
    "spring-test":                   (["spring-core", "junit"],        ["testcontainers"]),
    "spring-validation":             (["spring-core"],                 []),
    "spring-profiles":               (["spring-core"],                 []),

    "micronaut-basics":              ([],                              ["micronaut-data", "micronaut-security"]),
    "micronaut-data":                (["micronaut-basics"],            []),
    "micronaut-security":            (["micronaut-basics"],            []),
    "quarkus-basics":                ([],                              ["quarkus-data", "quarkus-reactive"]),
    "quarkus-data":                  (["quarkus-basics"],              []),
    "quarkus-reactive":              (["quarkus-basics"],              []),
    "ktor-basics":                   (["kotlin-basics"],               ["ktor-routing"]),
    "ktor-routing":                  (["ktor-basics"],                 []),

    # libraries
    "java-resilience4j":             (["resilience-patterns"],         ["circuit-breaker"]),
    "java-jackson":                  (["java-basics"],                 []),
    "java-lombok":                   (["java-basics"],                 ["mapstruct"]),
    "mapstruct":                     (["java-basics", "java-lombok"],  []),
    "guava":                         (["java-basics"],                 []),
    "rxjava":                        (["java-concurrency-basics"],     ["project-reactor"]),
    "project-reactor":               (["java-concurrency-basics"],     ["spring-webflux"]),

    # languages - java
    "java-basics":                   ([],                              ["java-collections", "java-generics", "java-oop"]),
    "java-oop":                      (["java-basics"],                 ["java-generics", "java-interfaces"]),
    "java-collections":              (["java-basics"],                 ["java-streams", "java-generics"]),
    "java-generics":                 (["java-basics", "java-oop"],     ["java-collections"]),
    "java-streams":                  (["java-collections", "java-lambdas"], ["java-functional-programming"]),
    "java-lambdas":                  (["java-basics"],                 ["java-streams", "java-functional-programming"]),
    "java-functional-programming":   (["java-lambdas", "java-streams"], []),
    "java-optional":                 (["java-basics"],                 []),
    "java-concurrency-basics":       (["java-basics"],                 ["java-threads", "java-executors", "java-concurrency-advanced"]),
    "java-threads":                  (["java-concurrency-basics"],     ["java-executors", "java-synchronization"]),
    "java-executors":                (["java-threads"],                ["java-completablefuture"]),
    "java-synchronization":          (["java-threads"],                ["java-locks"]),
    "java-locks":                    (["java-synchronization"],        []),
    "java-completablefuture":        (["java-executors"],              ["project-reactor"]),
    "java-concurrency-advanced":     (["java-concurrency-basics"],     ["java-virtual-threads"]),
    "java-virtual-threads":          (["java-concurrency-basics"],     []),
    "java-memory-model":             (["java-concurrency-basics"],     []),
    "java-jvm":                      (["java-basics"],                 ["java-gc", "java-memory-model"]),
    "java-gc":                       (["java-jvm"],                    []),
    "java-io":                       (["java-basics"],                 ["java-nio"]),
    "java-nio":                      (["java-io"],                     []),
    "java-reflection":               (["java-basics"],                 []),
    "java-annotations":              (["java-basics"],                 ["java-reflection"]),
    "java-exceptions":               (["java-basics"],                 []),
    "java-records":                  (["java-basics"],                 ["java-sealed"]),
    "java-sealed":                   (["java-records"],                []),
    "java-modules":                  (["java-basics"],                 []),

    # languages - kotlin
    "kotlin-basics":                 ([],                              ["kotlin-coroutines", "kotlin-collections", "kotlin-classes"]),
    "kotlin-classes":                (["kotlin-basics"],                ["kotlin-data-classes", "kotlin-sealed"]),
    "kotlin-data-classes":           (["kotlin-classes"],               []),
    "kotlin-sealed":                 (["kotlin-classes"],               []),
    "kotlin-collections":            (["kotlin-basics"],                ["kotlin-sequences"]),
    "kotlin-sequences":              (["kotlin-collections"],           []),
    "kotlin-coroutines":             (["kotlin-basics"],                ["kotlin-flow", "kotlin-channels"]),
    "kotlin-flow":                   (["kotlin-coroutines"],            ["kotlin-channels"]),
    "kotlin-channels":               (["kotlin-coroutines"],            []),
    "kotlin-delegates":              (["kotlin-basics"],                []),
    "kotlin-extensions":             (["kotlin-basics"],                []),
    "kotlin-functional":             (["kotlin-basics"],                []),
    "kotlin-null-safety":            (["kotlin-basics"],                []),
    "kotlin-generics":               (["kotlin-basics", "kotlin-classes"], []),
    "kotlin-dsl":                    (["kotlin-basics"],                []),
    "kotlin-multiplatform":          (["kotlin-basics"],                []),

    # scala / golang
    "scala-basics":                  ([],                              ["scala-functional", "scala-collections"]),
    "scala-functional":              (["scala-basics"],                ["scala-cats"]),
    "scala-collections":             (["scala-basics"],                []),
    "scala-cats":                    (["scala-functional"],            ["scala-zio"]),
    "scala-zio":                     (["scala-functional"],            []),
    "go-basics":                     ([],                              ["go-concurrency", "go-interfaces"]),
    "go-concurrency":                (["go-basics"],                   ["go-channels"]),
    "go-channels":                   (["go-concurrency"],              []),
    "go-interfaces":                 (["go-basics"],                   []),
    "go-modules":                    (["go-basics"],                   []),
    "go-error-handling":             (["go-basics"],                   []),

    # platform - kubernetes
    "kubernetes-basics":             ([],                              ["kubernetes-pods", "kubernetes-deployments"]),
    "kubernetes-pods":               (["kubernetes-basics"],           ["kubernetes-services", "kubernetes-deployments"]),
    "kubernetes-services":           (["kubernetes-pods"],             ["kubernetes-ingress"]),
    "kubernetes-deployments":        (["kubernetes-pods"],             ["kubernetes-statefulsets"]),
    "kubernetes-statefulsets":       (["kubernetes-deployments"],      []),
    "kubernetes-ingress":            (["kubernetes-services"],         ["istio"]),
    "kubernetes-configmaps":         (["kubernetes-basics"],           ["kubernetes-secrets"]),
    "kubernetes-secrets":            (["kubernetes-configmaps"],       []),
    "kubernetes-storage":            (["kubernetes-basics"],           []),
    "kubernetes-rbac":               (["kubernetes-basics"],           []),
    "kubernetes-networking":         (["kubernetes-basics"],           ["istio"]),
    "kubernetes-monitoring":         (["kubernetes-basics", "prometheus"], []),
    "kubernetes-troubleshooting":    (["kubernetes-basics"],           []),
    "helm":                          (["kubernetes-basics"],           ["argocd", "kustomize"]),
    "kustomize":                     (["kubernetes-basics"],           ["argocd"]),
    "istio":                         (["kubernetes-basics"],           []),
    "argocd":                        (["kubernetes-basics", "helm"],   []),
    "containerization-overview":     ([],                              ["docker-basics", "kubernetes-basics"]),
    "docker-basics":                 ([],                              ["docker-compose", "kubernetes-basics"]),
    "docker-compose":                (["docker-basics"],               ["kubernetes-basics"]),

    # ci/cd
    "github-actions":                (["git-basics"],                  ["argocd", "gitlab-ci"]),
    "gitlab-ci":                     (["git-basics"],                  ["argocd"]),
    "jenkins":                       ([],                              ["github-actions"]),
    "ci-cd-overview":                ([],                              ["github-actions", "gitlab-ci", "argocd"]),
    "iac-overview":                  ([],                              ["terraform", "ansible"]),
    "terraform":                     (["iac-overview"],                ["ansible"]),
    "ansible":                       (["iac-overview"],                []),
    "aws-basics":                    ([],                              ["aws-ec2", "aws-s3"]),

    # messaging
    "kafka":                         ([],                              ["kafka-streams", "spring-kafka", "cdc-change-data-capture"]),
    "kafka-streams":                 (["kafka"],                       []),
    "rabbitmq":                      ([],                              ["spring-messaging"]),

    # monitoring
    "monitoring-best-practices":     ([],                              ["observability-guide", "prometheus"]),
    "observability-guide":           (["monitoring-best-practices"],   ["distributed-tracing", "opentelemetry"]),
    "infrastructure-monitoring":     (["monitoring-best-practices"],   ["prometheus"]),
    "prometheus":                    (["monitoring-best-practices"],   ["grafana", "alertmanager"]),
    "grafana":                       (["prometheus"],                  []),
    "alertmanager":                  (["prometheus"],                  []),
    "distributed-tracing":           (["observability-guide"],         ["opentelemetry", "jaeger"]),
    "opentelemetry":                 (["distributed-tracing"],         ["jaeger", "zipkin"]),
    "jaeger":                        (["distributed-tracing", "opentelemetry"], []),
    "zipkin":                        (["distributed-tracing", "opentelemetry"], []),
    "elk-stack":                     (["monitoring-best-practices"],   ["kibana"]),
    "kibana":                        (["elk-stack"],                   []),
    "logstash":                      (["elk-stack"],                   ["kibana"]),

    # testing
    "junit":                         ([],                              ["mockito", "testcontainers"]),
    "junit-5":                       ([],                              ["mockito", "testcontainers"]),
    "mockito":                       (["junit-5"],                     ["spring-test"]),
    "testcontainers":                (["junit-5", "docker-basics"],    []),
    "tdd":                           (["junit-5"],                     ["bdd"]),
    "bdd":                           (["tdd"],                         ["cucumber"]),
    "cucumber":                      (["bdd", "junit-5"],              []),
    "rest-assured":                  (["junit-5"],                     []),
    "selenium":                      ([],                              ["playwright"]),
    "playwright":                    ([],                              []),
    "wiremock":                      (["junit-5"],                     []),
    "ui-testing-overview":           ([],                              ["selenium", "playwright"]),

    # basics / security / api
    "clean-code":                    ([],                              ["solid-principles", "design-principles"]),
    "git-basics":                    ([],                              ["git-flow", "trunk-based-development"]),
    "security-practices":            ([],                              ["owasp-top10", "jwt", "oauth2"]),
    "owasp-top10":                   (["security-practices"],          ["jwt"]),
    "jwt":                           (["security-practices"],          ["oauth2"]),
    "oauth2":                        (["security-practices", "jwt"],   []),
    "api-gateway":                   (["microservices"],               ["graphql", "rest"]),
    "graphql":                       ([],                              ["api-gateway"]),
    "rest":                          ([],                              ["api-gateway", "graphql"]),
    "openapi":                       (["rest"],                        []),
    "feature-flags":                 (["ci-cd-overview"],              ["argocd"]),
    "gradle":                        ([],                              ["maven"]),
    "maven":                         ([],                              ["gradle"]),

    # algorithms
    "divide-and-conquer":            ([],                              ["merge-sort", "quick-sort"]),
    "dynamic-programming":           ([],                              ["greedy-algorithms", "memoization"]),
    "greedy-algorithms":             ([],                              ["dynamic-programming"]),
    "backtracking":                  (["divide-and-conquer"],          ["branch-and-bound"]),
    "branch-and-bound":              (["backtracking"],                []),
    "bubble-sort":                   ([],                              ["selection-sort", "insertion-sort", "quick-sort"]),
    "selection-sort":                ([],                              ["insertion-sort", "merge-sort"]),
    "insertion-sort":                ([],                              ["shell-sort", "merge-sort"]),
    "shell-sort":                    (["insertion-sort"],              []),
    "merge-sort":                    (["divide-and-conquer"],          ["quick-sort", "heap-sort"]),
    "quick-sort":                    (["divide-and-conquer"],          ["heap-sort", "merge-sort"]),
    "heap-sort":                     (["binary-heap"],                 ["quick-sort", "merge-sort"]),
    "counting-sort":                 ([],                              ["radix-sort", "bucket-sort"]),
    "radix-sort":                    (["counting-sort"],               ["bucket-sort"]),
    "bucket-sort":                   (["counting-sort"],               ["radix-sort"]),
    "binary-search":                 ([],                              ["interpolation-search"]),
    "interpolation-search":          (["binary-search"],               []),
    "linear-search":                 ([],                              ["binary-search"]),
    "binary-heap":                   ([],                              ["heap-sort", "priority-queue"]),
    "priority-queue":                (["binary-heap"],                 []),
    "binary-tree":                   ([],                              ["binary-search-tree", "avl-tree"]),
    "binary-search-tree":            (["binary-tree"],                 ["avl-tree", "red-black-tree"]),
    "avl-tree":                      (["binary-search-tree"],          ["red-black-tree"]),
    "red-black-tree":                (["binary-search-tree"],          ["avl-tree"]),
    "trie":                          ([],                              []),
    "graph-basics":                  ([],                              ["bfs", "dfs", "dijkstra"]),
    "graphs":                        ([],                              ["bfs", "dfs"]),
    "bfs":                           (["graph-basics"],                ["dfs", "dijkstra"]),
    "dfs":                           (["graph-basics"],                ["bfs", "topological-sort"]),
    "dijkstra":                      (["graph-basics"],                ["bellman-ford", "a-star"]),
    "bellman-ford":                  (["graph-basics"],                ["dijkstra"]),
    "a-star":                        (["dijkstra"],                    []),
    "topological-sort":              (["dfs"],                         []),
    "kruskal":                       (["graph-basics"],                ["prim"]),
    "prim":                          (["graph-basics"],                ["kruskal"]),
    "kruskal-algorithm":             (["graph-basics"],                ["prim-algorithm"]),
    "prim-algorithm":                (["graph-basics"],                ["kruskal-algorithm"]),
    "boruvka-algorithm":             (["graph-basics"],                ["kruskal-algorithm"]),
    "a-star-pathfinding":            (["dijkstra"],                    []),
    "monte-carlo-tree-search-tic-tac-toe": (["graph-basics"],          []),
    "balanced-binary-tree-check":    (["binary-tree"],                 []),
    "binary-search-tree-traversal":  (["binary-search-tree"],          []),

    # algorithms — strings
    "balanced-parentheses":          ([],                              []),
    "caesar-cipher":                 ([],                              []),
    "first-non-repeating-character": ([],                              []),
    "large-text-string-search":      ([],                              ["levenshtein-distance"]),
    "levenshtein-distance":          (["dynamic-programming"],         []),
    "longest-word-search":           ([],                              []),
    "multiple-keywords-check":       ([],                              []),
    "palindrome-check":              ([],                              []),

    # algorithms — math
    "factorial-calculation":         ([],                              ["fibonacci-sequence"]),
    "fibonacci-sequence":            (["dynamic-programming"],         []),
    "greatest-common-divisor":       ([],                              ["coprime-numbers"]),
    "coprime-numbers":               (["greatest-common-divisor"],     []),
    "gradient-descent":              (["dynamic-programming"],         []),
    "age-calculation":               ([],                              []),
    "circle-area-calculation":       ([],                              []),
    "distance-between-points":       ([],                              []),

    # algorithms — searching
    "diagonal-array-traversal":      ([],                              []),
    "merge-sorted-sequences":        (["merge-sort"],                  ["merge-two-sorted-arrays"]),
    "merge-two-sorted-arrays":       (["merge-sort"],                  []),
    "pairs-with-given-sum":          ([],                              []),
    "top-n-frequent-elements":       (["binary-heap"],                 []),

    # algorithms — data structures
    "linked-list-middle":            ([],                              ["linked-list-reverse"]),
    "linked-list-reverse":           ([],                              []),
    "collections-big-o":             ([],                              ["collections-complexity"]),
    "collections-complexity":        (["collections-big-o"],           []),
    "collections-lock-free":         (["java-concurrency-basics"],     []),

    # algorithms — problems / overviews
    "algorithms":                    ([],                              ["divide-and-conquer", "dynamic-programming"]),
    "branch-prediction":             ([],                              []),
    "calculator-implementation":     ([],                              []),
    "circular-buffer":               ([],                              []),
    "combinatorial-problems-overview": ([],                            []),
    "credit-card-validation":        ([],                              []),
    "dining-philosophers-problem":   (["java-synchronization"],        []),

    # algorithms — ai/ml
    "ai-libraries":                  ([],                              ["deeplearning4j", "jenetics"]),
    "ant-colony-optimization":       (["graph-basics"],                ["genetic-algorithms"]),
    "cnn-deeplearning4j":            (["deeplearning4j"],              []),
    "deeplearning4j":                (["ai-libraries"],                ["cnn-deeplearning4j"]),
    "genetic-algorithms":            (["ai-libraries"],                ["jenetics"]),
    "hyperloglog":                   ([],                              []),
    "jenetics":                      (["genetic-algorithms"],          []),
    "logistic-regression":           (["ai-libraries"],                []),

    # languages — kotlin (collections / fp / concurrency / dsl)
    "kotlin-collections-list":       (["kotlin-collections"],          ["kotlin-collections-operations"]),
    "kotlin-collections-set":        (["kotlin-collections"],          ["kotlin-collections-operations"]),
    "kotlin-collections-map":        (["kotlin-collections"],          ["kotlin-collections-operations"]),
    "kotlin-collections-grouping":   (["kotlin-collections"],          ["kotlin-collections-operations"]),
    "kotlin-collections-operations": (["kotlin-collections"],          ["kotlin-collections-sequences"]),
    "kotlin-collections-sequences":  (["kotlin-collections"],          []),
    "kotlin-fp-advanced":            (["kotlin-functional"],           []),
    "kotlin-concurrency-advanced":   (["kotlin-coroutines"],           ["kotlin-flow"]),

    # libraries — kotlin
    "kotlin-arrow":                  (["kotlin-functional"],           []),
    "kotlin-exposed":                (["kotlin-basics", "sql-basics"], []),
    "kotlin-klaxon":                 (["kotlin-basics"],               []),
    "kotlin-kodein":                 (["kotlin-basics"],               []),
    "kotlin-konfig":                 (["kotlin-basics"],               []),
    "kotlin-kotlinx-coroutines":     (["kotlin-coroutines"],           ["kotlin-flow"]),
    "kotlin-kotlinx-datetime":       (["kotlin-basics"],               []),
    "kotlin-kotlinx-serialization":  (["kotlin-basics"],               []),

    # platform — cloud-providers
    "aws-iam":                       (["aws-basics"],                  ["aws-services"]),
    "aws-networking":                (["aws-basics"],                  ["aws-services"]),
    "aws-services":                  (["aws-basics"],                  []),
    "azure-basics":                  ([],                              ["azure-services", "azure-networking"]),
    "azure-networking":              (["azure-basics"],                ["azure-services"]),
    "azure-services":                (["azure-basics"],                []),
    "gcp-basics":                    ([],                              []),

    # languages — scala
    "scala-akka-streams":            (["scala-functional"],            []),
    "scala-cats-effect":             (["scala-cats"],                  ["scala-zio"]),
    "scala-futures":                 (["scala-basics"],                ["scala-promises"]),
    "scala-monads":                  (["scala-functional"],            ["scala-cats"]),
    "scala-promises":                (["scala-futures"],               []),
    "scala-scalaz":                  (["scala-functional"],            ["scala-cats"]),
    "scala-shapeless":               (["scala-functional"],            []),
    "scala-tagless-final":           (["scala-cats"],                  []),

    # libraries — scala
    "scala-akka":                    (["scala-basics"],                ["scala-akka-streams"]),
    "scala-circe":                   (["scala-basics"],                []),
    "scala-doobie":                  (["scala-cats", "sql-basics"],    []),
    "scala-scalatest":               (["scala-basics"],                []),
    "scala-slick":                   (["scala-basics", "sql-basics"],  []),

    # monitoring — logging
    "logging-basics":                ([],                              ["logging-best-practices", "structured-logging"]),
    "logging-best-practices":        (["logging-basics"],              ["centralized-logging"]),
    "structured-logging":            (["logging-basics"],              ["centralized-logging"]),
    "centralized-logging":           (["logging-basics"],              ["log-aggregation", "elk-stack"]),
    "log-aggregation":               (["centralized-logging"],         ["elk-stack"]),
    "log4j":                         (["logging-basics"],              ["slf4j", "logback"]),
    "logback":                       (["logging-basics", "slf4j"],     []),
    "slf4j":                         (["logging-basics"],              ["logback", "log4j"]),

    # databases — postgresql (operations)
    "postgres-admin":                (["postgres-basics"],             ["postgres-monitoring"]),
    "postgres-data-ops":             (["postgres-basics"],             ["postgres-queries"]),
    "postgres-joins":                (["postgres-queries"],            []),
    "postgres-monitoring":           (["postgres-basics"],             ["prometheus"]),
    "postgres-queries":              (["postgres-basics"],             ["postgres-explain", "postgres-joins"]),
    "postgres-structure":            (["postgres-basics"],             ["postgres-types"]),
    "postgres-troubleshooting":      (["postgres-basics"],             ["postgres-monitoring"]),

    # databases — mysql
    "mysql-basics":                  ([],                              ["mysql-indexes", "mysql-queries"]),
    "mysql-indexes":                 (["mysql-basics"],                ["mysql-performance"]),
    "mysql-queries":                 (["mysql-basics"],                ["mysql-design"]),
    "mysql-design":                  (["mysql-basics"],                ["mysql-indexes"]),
    "mysql-performance":             (["mysql-indexes"],               ["mysql-replication"]),
    "mysql-replication":             (["mysql-basics"],                []),
    "mysql-admin":                   (["mysql-basics"],                ["mysql-replication"]),

    # databases — cassandra (more)
    "cassandra-admin":               (["cassandra-basics"],            ["cassandra-clustering"]),
    "cassandra-clustering":          (["cassandra-basics"],            ["cassandra-performance"]),
    "cassandra-data-modeling":       (["cassandra-basics"],            ["cassandra-queries"]),
    "cassandra-performance":         (["cassandra-basics"],            []),
    "cassandra-queries":             (["cassandra-data-modeling"],     []),

    # databases — elasticsearch (more)
    "elasticsearch-aggregations":    (["elasticsearch-queries"],       []),
    "elasticsearch-clustering":      (["elasticsearch-basics"],        ["elasticsearch-performance"]),
    "elasticsearch-indexing":        (["elasticsearch-basics"],        ["elasticsearch-queries"]),
    "elasticsearch-performance":     (["elasticsearch-clustering"],    []),

    # frameworks — spring (more)
    "spring-ai":                     (["spring-boot"],                 []),
    "spring-boot-3-migration":       (["spring-boot"],                 []),
    "spring-events":                 (["spring-core"],                 ["publish-subscribe"]),
    "spring-modulith":               (["spring-boot"],                 ["microservices"]),
    "spring-openapi":                (["spring-rest"],                 ["openapi"]),
    "spring-retry":                  (["spring-core"],                 ["java-resilience4j"]),

    # patterns — concurrency
    "active-object":                 (["java-concurrency-basics"],     ["thread-pool"]),
    "double-checked-locking":        (["java-synchronization"],        []),
    "future-task":                   (["java-completablefuture"],      []),
    "producer-consumer":             (["java-synchronization"],        ["thread-pool"]),
    "read-write-lock":               (["java-locks"],                  []),
    "thread-pool":                   (["java-executors"],              []),

    # testing — junit family
    "junit-advanced":                (["junit-5"],                     ["mockito-advanced"]),
    "mockito-advanced":              (["mockito"],                     []),
    "assertj":                       (["junit-5"],                     ["hamcrest"]),
    "hamcrest":                      (["junit-5"],                     ["assertj"]),

    # platform — infra tools
    "consul":                        ([],                              []),
    "infrastructure-tools-overview": ([],                              ["consul", "vagrant"]),
    "nginx-advanced":                ([],                              []),
    "vagrant":                       ([],                              ["docker-basics"]),

    # platform — ci-cd (more)
    "azure-devops":                  ([],                              ["github-actions"]),
    "circleci":                      ([],                              ["github-actions"]),
    "tekton":                        (["kubernetes-basics"],           ["argocd"]),
    "travis-ci":                     ([],                              ["github-actions"]),

    # monitoring — alerting / apm
    "alerting":                      (["monitoring-best-practices"],   ["alertmanager", "pagerduty"]),
    "pagerduty":                     (["alerting"],                    []),
    "slack-alerting":                (["alerting"],                    []),
    "datadog":                       (["monitoring-best-practices"],   []),
    "elastic-apm":                   (["distributed-tracing"],         []),
    "new-relic":                     (["monitoring-best-practices"],   []),

    # platform — docker (more)
    "docker-advanced":               (["docker-basics"],               ["docker-compose"]),
    "docker-containers":             (["docker-basics"],               []),
    "docker-spring-boot":            (["docker-basics", "spring-boot"], []),
}

ABBREVIATIONS: dict[str, list[str]] = {
    "ddd": ["DDD"],
    "cqrs": ["CQRS"],
    "solid-principles": ["SOLID"],
    "test-driven-development": ["TDD"],
    "tdd": ["TDD"],
    "behavior-driven-development": ["BDD"],
    "bdd": ["BDD"],
    "java-jvm": ["JVM"],
    "java-gc": ["GC"],
    "openapi": ["OpenAPI", "Swagger"],
    "owasp-top10": ["OWASP"],
    "jwt": ["JWT"],
    "oauth2": ["OAuth2"],
    "rest": ["REST", "REST API"],
    "graphql": ["GraphQL"],
    "kafka": ["Kafka"],
    "postgres-mvcc": ["MVCC", "Postgres"],
    "kubernetes-basics": ["k8s"],
    "kubernetes-pods": ["k8s pods", "Pod"],
    "kubernetes-services": ["k8s service", "Service"],
    "kubernetes-deployments": ["k8s deploy", "Deployment"],
    "kubernetes-ingress": ["Ingress", "k8s ingress"],
    "kubernetes-configmaps": ["ConfigMap"],
    "kubernetes-secrets": ["Secret"],
    "argocd": ["ArgoCD", "Argo", "GitOps"],
    "github-actions": ["GH Actions", "CI"],
    "gitlab-ci": ["GitLab CI"],
    "terraform": ["Terraform", "tf", "IaC"],
    "iac-overview": ["IaC"],
    "prometheus": ["PromQL"],
    "opentelemetry": ["OTel"],
    "jaeger": ["Jaeger"],
    "saga-pattern": ["Saga"],
    "circuit-breaker": ["Circuit Breaker", "CB"],
    "factory-method": ["Factory"],
    "abstract-factory": ["Abstract Factory"],
    "observer": ["Observer"],
    "strategy": ["Strategy"],
    "decorator": ["Decorator"],
    "spring-boot": ["Spring Boot"],
    "spring-data-jpa": ["Spring Data JPA"],
    "spring-security": ["Spring Security"],
    "spring-actuator": ["Actuator"],
    "spring-aop": ["AOP"],
    "java-streams": ["Stream API"],
    "java-lambdas": ["лямбды"],
    "java-optional": ["Optional"],
    "kotlin-coroutines": ["корутины"],
    "kotlin-flow": ["Flow"],
    "redis-basics": ["Redis"],
    "postgres-basics": ["Postgres", "PG"],
    "mongodb-basics": ["MongoDB"],
    "elasticsearch-basics": ["ES"],
    "hibernate-basics": ["Hibernate"],
    "hibernate-caching": ["L2 cache"],
    "kafka-streams": ["Kafka Streams"],
    "rabbitmq": ["RabbitMQ"],
    "junit-5": ["JUnit"],
    "junit": ["JUnit"],
    "mockito": ["Mockito"],
    "testcontainers": ["Testcontainers"],
    "binary-search": ["bin search", "бинпоиск"],
    "binary-heap": ["heap", "куча"],
    "merge-sort": ["Merge Sort"],
    "quick-sort": ["Quick Sort"],
    "heap-sort": ["Heap Sort"],
    "bubble-sort": ["Bubble Sort", "пузырёк"],
    "dynamic-programming": ["DP"],
    "greedy-algorithms": ["greedy"],
    "divide-and-conquer": ["D&C"],
    "git-basics": ["Git"],
    "clean-code": ["чистый код"],
    "design-patterns": ["GoF"],
    "publish-subscribe": ["Pub/Sub", "PubSub"],
    "event-sourcing": ["Event Sourcing"],
    "hexagonal-architecture": ["Ports & Adapters"],
    "clean-architecture": ["Clean Architecture"],
    "microservices": ["микросервисы"],
    "spring-core": ["Spring", "Spring IoC"],
    "spring-mvc": ["Spring MVC"],
    "spring-rest": ["Spring REST"],
    "spring-data": ["Spring Data"],
    "spring-data-redis": ["Spring Data Redis"],
    "spring-cache": ["Spring Cache"],
    "spring-kafka": ["Spring Kafka"],
    "spring-webflux": ["WebFlux"],
    "spring-batch": ["Spring Batch"],
    "spring-test": ["Spring Test"],
    "spring-transaction": ["Transactional"],
    "spring-validation": ["Validation"],
    "java-resilience4j": ["Resilience4j"],
    "java-jackson": ["Jackson"],
    "java-lombok": ["Lombok"],
    "mapstruct": ["MapStruct"],
    "guava": ["Guava"],
    "rxjava": ["RxJava"],
    "project-reactor": ["Reactor"],
    "java-collections": ["Collections"],
    "java-records": ["Records"],
    "java-sealed": ["Sealed"],
    "java-modules": ["JPMS"],
    "java-virtual-threads": ["VT", "Loom"],
    "java-memory-model": ["JMM"],
    "java-completablefuture": ["CompletableFuture", "CF"],
    "kotlin-basics": ["Kotlin"],
    "kotlin-data-classes": ["data class"],
    "kotlin-sealed": ["sealed class"],
    "kotlin-channels": ["Channel"],
    "kotlin-delegates": ["delegates", "by lazy"],
    "kotlin-extensions": ["extensions"],
    "kotlin-null-safety": ["null safety"],
    "kotlin-multiplatform": ["KMP"],
    "scala-basics": ["Scala"],
    "scala-functional": ["Scala FP"],
    "scala-cats": ["cats"],
    "scala-zio": ["ZIO"],
    "go-basics": ["Go", "Golang"],
    "go-concurrency": ["goroutines"],
    "go-channels": ["go channels"],
    "kubernetes-services": ["Service"],
    "kubernetes-deployments": ["Deployment"],
    "helm": ["Helm"],
    "kustomize": ["Kustomize"],
    "istio": ["Istio", "service mesh"],
    "containerization-overview": ["containers"],
    "docker-basics": ["Docker"],
    "docker-compose": ["Compose"],
    "ci-cd-overview": ["CI/CD"],
    "iac-overview": ["IaC"],
    "ansible": ["Ansible"],
    "aws-basics": ["AWS"],
    "azure-basics": ["Azure"],
    "gcp-basics": ["GCP"],
    "monitoring-best-practices": ["мониторинг"],
    "observability-guide": ["observability"],
    "infrastructure-monitoring": ["infra monitoring"],
    "grafana": ["Grafana"],
    "alertmanager": ["Alertmanager"],
    "distributed-tracing": ["tracing", "трейсинг"],
    "zipkin": ["Zipkin"],
    "elk-stack": ["ELK", "Elastic Stack"],
    "kibana": ["Kibana"],
    "logstash": ["Logstash"],
    "logging-basics": ["logging"],
    "log4j": ["Log4j"],
    "logback": ["Logback"],
    "slf4j": ["SLF4J"],
    "datadog": ["Datadog"],
    "elastic-apm": ["Elastic APM"],
    "new-relic": ["New Relic"],
    "selenium": ["Selenium"],
    "playwright": ["Playwright"],
    "rest-assured": ["REST Assured"],
    "wiremock": ["WireMock"],
    "cucumber": ["Cucumber", "Gherkin"],
    "assertj": ["AssertJ"],
    "hamcrest": ["Hamcrest"],
    "consul": ["Consul"],
    "vagrant": ["Vagrant"],
    "tekton": ["Tekton"],
    "circleci": ["CircleCI"],
    "travis-ci": ["Travis CI"],
    "azure-devops": ["Azure DevOps"],
    "pgbouncer": ["PgBouncer"],
    "patroni": ["Patroni"],
    "cassandra-basics": ["Cassandra"],
    "mongodb-replication": ["MongoDB replica"],
    "elasticsearch-aggregations": ["aggs"],
    "linked-list-reverse": ["reverse list"],
    "linked-list-middle": ["middle node"],
    "binary-search-tree": ["BST"],
    "red-black-tree": ["RB Tree"],
    "avl-tree": ["AVL"],
    "binary-tree": ["binary tree"],
    "trie": ["Trie", "префиксное дерево"],
    "graph-basics": ["graphs"],
    "bfs": ["BFS"],
    "dfs": ["DFS"],
    "topological-sort": ["topo sort"],
    "dijkstra": ["Dijkstra"],
    "a-star": ["A*"],
    "a-star-pathfinding": ["A*"],
    "bellman-ford": ["Bellman-Ford"],
    "bucket-sort": ["Bucket Sort"],
    "radix-sort": ["Radix Sort"],
    "counting-sort": ["Counting Sort"],
    "selection-sort": ["Selection Sort"],
    "insertion-sort": ["Insertion Sort"],
    "shell-sort": ["Shell Sort"],
    "linear-search": ["linear search"],
    "interpolation-search": ["interpolation search"],
    "fibonacci-sequence": ["Fibonacci", "числа Фибоначчи"],
    "factorial-calculation": ["factorial", "факториал"],
    "greatest-common-divisor": ["GCD", "НОД"],
    "levenshtein-distance": ["Levenshtein"],
    "palindrome-check": ["palindrome"],
    "caesar-cipher": ["Caesar Cipher"],
    "balanced-parentheses": ["balanced parens"],
    "first-non-repeating-character": ["non-repeating char"],
    "longest-word-search": ["longest word"],
    "circular-buffer": ["ring buffer"],
    "calculator-implementation": ["calculator"],
    "credit-card-validation": ["Luhn"],
    "dining-philosophers-problem": ["dining philosophers"],
    "branch-prediction": ["branch prediction"],
    "ant-colony-optimization": ["ACO"],
    "genetic-algorithms": ["GA"],
    "logistic-regression": ["logreg"],
    "deeplearning4j": ["DL4J"],
    "cnn-deeplearning4j": ["CNN"],
    "hyperloglog": ["HyperLogLog"],
    "kotlin-arrow": ["Arrow"],
    "kotlin-exposed": ["Exposed"],
    "scala-akka": ["Akka"],
    "scala-akka-streams": ["Akka Streams"],
    "scala-cats-effect": ["cats-effect", "CE"],
    "scala-circe": ["Circe"],
    "scala-doobie": ["Doobie"],
    "scala-slick": ["Slick"],
    "scala-scalatest": ["ScalaTest"],
    "scala-shapeless": ["Shapeless"],
    "scala-tagless-final": ["Tagless Final"],
    "scala-monads": ["monads", "монады"],
    "scala-futures": ["Futures"],
    "scala-promises": ["Promises"],
    "kafka": ["Kafka"],
    "rabbitmq": ["RabbitMQ"],
    "feature-flags": ["feature toggles"],
    "gradle": ["Gradle"],
    "maven": ["Maven"],
    "openapi": ["OpenAPI", "Swagger"],
    "graphql": ["GraphQL"],
    "rest": ["REST", "REST API"],
    "api-gateway": ["API Gateway"],
    "security-practices": ["security"],
    "owasp-top10": ["OWASP"],
    "jwt": ["JWT"],
    "oauth2": ["OAuth2"],
}

FM_RE = re.compile(r"^---\n(.*?)\n---", re.DOTALL)


def build_basename_index(root: Path) -> dict[str, str]:
    idx: dict[str, str] = {}
    for p in root.rglob("*.md"):
        stem = p.stem
        rel = str(p.relative_to(root))
        if stem in idx and "/interview/" in idx[stem]:
            idx[stem] = rel
        elif stem not in idx:
            idx[stem] = rel
    return idx


def parse_fm(text: str):
    m = FM_RE.match(text)
    if not m:
        return None
    return ("---\n", m.group(1), text[m.end():])


def get_field_block(fm_body: str, field: str):
    lines = fm_body.split("\n")
    start = None
    for i, ln in enumerate(lines):
        if re.match(rf"^{re.escape(field)}\s*:", ln):
            start = i
            break
    if start is None:
        return None
    end = len(lines)
    for j in range(start + 1, len(lines)):
        if re.match(r"^[A-Za-z_][A-Za-z0-9_-]*\s*:", lines[j]):
            end = j
            break
    return (start, end)


def replace_list_field(fm_body: str, field: str, items: list[str]) -> str:
    lines = fm_body.split("\n")
    rng = get_field_block(fm_body, field)
    if items:
        new_block = [f"{field}:"] + [f'  - "{it}"' for it in items]
    else:
        new_block = [f"{field}: []"]
    if rng is None:
        upd_idx = None
        for i, ln in enumerate(lines):
            if re.match(r"^updated\s*:", ln):
                upd_idx = i
                break
        insert_at = upd_idx if upd_idx is not None else len(lines)
        new_lines = lines[:insert_at] + new_block + lines[insert_at:]
    else:
        s, e = rng
        new_lines = lines[:s] + new_block + lines[e:]
    return "\n".join(new_lines)


def field_is_empty(fm_body: str, field: str) -> bool:
    rng = get_field_block(fm_body, field)
    if rng is None:
        return True
    lines = fm_body.split("\n")
    s, e = rng
    if re.search(r":\s*\[\s*\]\s*$", lines[s]):
        return True
    items = [ln for ln in lines[s + 1:e] if re.match(r"^\s+-\s+", ln)]
    return len(items) == 0


def get_existing_aliases(fm_body: str) -> list[str]:
    rng = get_field_block(fm_body, "aliases")
    if rng is None:
        return []
    lines = fm_body.split("\n")
    s, e = rng
    out = []
    for ln in lines[s + 1:e]:
        m = re.match(r"^\s+-\s+(.*)$", ln)
        if not m:
            continue
        v = m.group(1).strip()
        if len(v) >= 2 and v[0] == v[-1] and v[0] in ('"', "'"):
            v = v[1:-1]
        if v:
            out.append(v)
    return out


def get_title(fm_body: str) -> str:
    m = re.search(r'^title\s*:\s*"([^"]*)"\s*$', fm_body, re.MULTILINE)
    return m.group(1) if m else ""


def derive_aliases(stem: str, title: str, existing: list[str]) -> list[str]:
    candidates: list[str] = []
    t = (title or "").strip()
    if ":" in t:
        head, _, tail = t.partition(":")
        for part in (head.strip(), tail.strip()):
            if part:
                candidates.append(part)
    else:
        candidates.append(t)
    new_cands: list[str] = []
    for c in candidates:
        m = re.match(r"^(.+?)\s*\(([^)]+)\)\s*$", c)
        if m:
            new_cands.append(m.group(1).strip())
            new_cands.append(m.group(2).strip())
        else:
            new_cands.append(c)
    candidates = new_cands
    for ex in existing:
        if ex and len(ex) <= 30 and ex.lower() not in (c.lower() for c in candidates):
            candidates.append(ex)
    for abbr in ABBREVIATIONS.get(stem, []):
        if abbr.lower() not in (c.lower() for c in candidates):
            candidates.append(abbr)
    seen: set[str] = set()
    out: list[str] = []
    for c in candidates:
        c = c.strip()
        if not c or len(c) > 30:
            continue
        k = c.lower()
        if k in seen:
            continue
        seen.add(k)
        out.append(c)
        if len(out) >= 4:
            break
    return out


def to_wikilinks(basenames: list[str], idx: dict[str, str]) -> list[str]:
    out = []
    for b in basenames:
        if b in idx:
            out.append(f"[[{b}]]")
    return out


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--root", default=str(ROOT))
    args = parser.parse_args(argv)

    root = Path(args.root)
    idx = build_basename_index(root)
    print(f"# index size: {len(idx)}", file=sys.stderr)

    changed = 0
    aliases_changed = 0
    related_filled = 0

    for p in sorted(root.rglob("*.md")):
        text = p.read_text(encoding="utf-8")
        parsed = parse_fm(text)
        if parsed is None:
            continue
        before, fm_body, after = parsed
        new_fm = fm_body
        stem = p.stem
        is_index = (p.name in ("README.md", "TOC.md")) or bool(re.search(r'^type\s*:\s*"(index|rules)"', fm_body, re.MULTILINE))

        if not re.search(r'^type\s*:\s*"rules"', fm_body, re.MULTILINE):
            title = get_title(new_fm)
            existing = get_existing_aliases(new_fm)
            new_aliases = derive_aliases(stem, title, existing)
            if new_aliases and new_aliases != existing:
                new_fm = replace_list_field(new_fm, "aliases", new_aliases)
                aliases_changed += 1

        if not is_index and stem in RELATIONS:
            prereqs, nexts = RELATIONS[stem]
            if field_is_empty(new_fm, "prerequisites"):
                wl = to_wikilinks(prereqs, idx)
                if wl:
                    new_fm = replace_list_field(new_fm, "prerequisites", wl)
                    related_filled += 1
            if field_is_empty(new_fm, "next"):
                wl = to_wikilinks(nexts, idx)
                if wl:
                    new_fm = replace_list_field(new_fm, "next", wl)
                    related_filled += 1

        # interview convention: <topic>-interview.md → prereq [[<topic>]] or [[<topic>-basics]]
        if not is_index and stem.endswith("-interview"):
            base = stem[:-len("-interview")]
            candidates = [base, base + "-basics", base.rsplit("-", 1)[0] if "-" in base else None]
            for c in candidates:
                if c and c in idx and "/interview/" not in idx[c]:
                    if field_is_empty(new_fm, "prerequisites"):
                        new_fm = replace_list_field(new_fm, "prerequisites", [f"[[{c}]]"])
                        related_filled += 1
                    break

        if new_fm == fm_body:
            continue
        changed += 1
        if args.apply:
            new_text = before + new_fm + "\n---" + after
            p.write_text(new_text, encoding="utf-8")

    print(f"changed: {changed}, aliases improved: {aliases_changed}, prereq/next filled: {related_filled}")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
