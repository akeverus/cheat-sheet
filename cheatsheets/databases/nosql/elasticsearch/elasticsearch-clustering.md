---
title: "Elasticsearch: Кластеризация - Управление кластером и масштабирование"
description: "Комплексное руководство по развертыванию и управлению кластерами Elasticsearch: топологии, репликация, балансировка нагрузки, отказоустойчивость и масштабирование."
tags:
  - databases
  - nosql
  - elasticsearch-clustering
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Elasticsearch: Кластеризация - Управление кластером и масштабирование

Комплексное руководство по развертыванию и управлению кластерами **Elasticsearch**: топологии, репликация, балансировка нагрузки, отказоустойчивость и масштабирование.

## Полезные ссылки

### Официальная документация
- [Elasticsearch Clustering](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-cluster.html)
- [Cluster APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster.html)
- [Discovery and Cluster Formation](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery.html)
- [Shard Allocation](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-cluster.html#cluster-shard-allocation)

### Дизайн и архитектура
- [Cluster Architecture](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-cluster.html)
- [Sizing Guide](https://www.elastic.co/guide/en/elasticsearch/reference/current/size-your-shards.html)
- [Production Deployment](https://www.elastic.co/guide/en/elasticsearch/reference/current/deploy.html)

### Инструменты
- [Elasticsearch Cluster APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster.html)
- [Cluster Management Tools](https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-cluster.html)
- [Hot-Warm-Cold Architecture](https://www.elastic.co/guide/en/elasticsearch/reference/current/data-tiers.html)

### См. также
- [[elasticsearch-basics|Основы]] — **Elasticsearch**
- [[elasticsearch-performance|Производительность]] — производительность
- [[elasticsearch-indexing|Индексация]] — индексация документов

## Содержание

- [Архитектура кластера](#архитектура-кластера)
  - [Основные компоненты](#основные-компоненты)
    - [Master-eligible Nodes](#master-eligible-nodes)
    - [Data Nodes](#data-nodes)
  - [Cluster State](#cluster-state)
    - [State Management](#state-management)
- [Типы узлов](#типы-узлов)
  - [Master-eligible Nodes](#master-eligible-nodes-1)
    - [Конфигурация](#конфигурация)
    - [Master Election Process](#master-election-process)
  - [Data Nodes](#data-nodes-1)
    - [Конфигурация](#конфигурация-1)
  - [Coordinating Nodes](#coordinating-nodes)
    - [Load Balancer Configuration](#load-balancer-configuration)
  - [Ingest Nodes](#ingest-nodes)
    - [Data Processing Configuration](#data-processing-configuration)
- [Discovery и формирование кластера](#discovery-и-формирование-кластера)
  - [Seed Hosts](#seed-hosts)
    - [Конфигурация discovery](#конфигурация-discovery)
  - [Cluster Bootstrap](#cluster-bootstrap)
    - [Initial Cluster Formation](#initial-cluster-formation)
    - [Bootstrap Validation](#bootstrap-validation)
  - [Network Configuration](#network-configuration)
    - [Cluster Communication](#cluster-communication)
- [Управление шардами](#управление-шардами)
  - [Shard Allocation](#shard-allocation)
    - [Allocation Strategies](#allocation-strategies)
  - [Shard Rebalancing](#shard-rebalancing)
    - [Automatic Rebalancing](#automatic-rebalancing)
    - [Manual Rebalancing](#manual-rebalancing)
- [Репликация и отказоустойчивость](#репликация-и-отказоустойчивость)
  - [Primary-Replica Model](#primary-replica-model)
    - [Replication Process](#replication-process)
  - [Consistency Levels](#consistency-levels)
    - [Write Consistency](#write-consistency)
  - [Failure Recovery](#failure-recovery)
    - [Replica Failover](#replica-failover)
- [Балансировка нагрузки](#балансировка-нагрузки)
  - [Load Balancing Strategies](#load-balancing-strategies)
    - [Request-Level Load Balancing](#request-level-load-balancing)
  - [Shard-Level Balancing](#shard-level-balancing)
    - [Shard Rebalancing](#shard-rebalancing-1)
- [Масштабирование кластера](#масштабирование-кластера)
  - [Horizontal Scaling](#horizontal-scaling)
    - [Adding Nodes](#adding-nodes)
    - [Automated Node Provisioning](#automated-node-provisioning)
  - [Vertical Scaling](#vertical-scaling)
    - [Node Upgrade](#node-upgrade)
- [Мониторинг кластера](#мониторинг-кластера)
  - [Cluster Health API](#cluster-health-api)
    - [Health Monitoring](#health-monitoring)
    - [Health Response Analysis](#health-response-analysis)
  - [Cluster Stats](#cluster-stats)
    - [Detailed Statistics](#detailed-statistics)
  - [Custom Monitoring](#custom-monitoring)
    - [Cluster Metrics Collector](#cluster-metrics-collector)
- [Hot-Warm-Cold архитектура](#hot-warm-cold-архитектура)
  - [Data Lifecycle Management](#data-lifecycle-management)
    - [Index Lifecycle Policy](#index-lifecycle-policy)
    - [Node Attributes for Data Tiers](#node-attributes-for-data-tiers)
  - [Automated Data Movement](#automated-data-movement)
    - [Data Tier Migration](#data-tier-migration)
- [Cluster APIs](#cluster-apis)
  - [Cluster Management APIs](#cluster-management-apis)
    - [Cluster Settings](#cluster-settings)
    - [Cluster Reroute API](#cluster-reroute-api)
  - [Node APIs](#node-apis)
    - [Node Information](#node-information)
  - [Cluster State API](#cluster-state-api)
    - [State Information](#state-information)
- [Решение проблем](#решение-проблем)
  - [Common Cluster Issues](#common-cluster-issues)
    - [Red Cluster Status](#red-cluster-status)
    - [Yellow Cluster Status](#yellow-cluster-status)
    - [High CPU Usage](#high-cpu-usage)
    - [Memory Issues](#memory-issues)
  - [Node Issues](#node-issues)
    - [Node Not Joining Cluster](#node-not-joining-cluster)
    - [Slow Node Performance](#slow-node-performance)
  - [Index Issues](#index-issues)
    - [Index Creation Failures](#index-creation-failures)
    - [Search Issues](#search-issues)
- [Лучшие практики](#лучшие-практики)
  - [Cluster Planning](#cluster-planning)
    - [1. Sizing Guidelines](#1-sizing-guidelines)
    - [2. Network Configuration](#2-network-configuration)
    - [3. Security Setup](#3-security-setup)
  - [Operational Best Practices](#operational-best-practices)
    - [1. Monitoring Setup](#1-monitoring-setup)
    - [2. Backup Strategy](#2-backup-strategy)
    - [3. Maintenance Windows](#3-maintenance-windows)
  - [Performance Optimization](#performance-optimization)
    - [1. Shard Strategy](#1-shard-strategy)
    - [2. Query Optimization](#2-query-optimization)
    - [3. Resource Management](#3-resource-management)
  - [Disaster Recovery](#disaster-recovery)
    - [1. High Availability](#1-high-availability)
    - [2. Backup and Restore](#2-backup-and-restore)
    - [3. Incident Response](#3-incident-response)
  - [Архитектурные решения:](#архитектурные-решения)
  - [Операционные практики:](#операционные-практики)
  - [Производительность и масштабируемость:](#производительность-и-масштабируемость)
  - [Надежность и отказоустойчивость:](#надежность-и-отказоустойчивость)
  - [Мониторинг и troubleshooting:](#мониторинг-и-troubleshooting)
  - [Best practices:](#best-practices)

## Архитектура кластера

### Основные компоненты

#### Master-eligible Nodes
```java
public class MasterNode {

    private ClusterState clusterState;
    private RoutingTable routingTable;
    private Metadata metadata;

    // Master responsibilities
    public void publishClusterState() {
        // Publish updated cluster state to all nodes
        clusterState.setVersion(clusterState.getVersion() + 1);
        clusterService.publishState(clusterState);
    }

    public void processClusterTask(ClusterTask task) {
        switch (task.getType()) {
            case CREATE_INDEX:
                createIndex((CreateIndexTask) task);
                break;
            case UPDATE_MAPPING:
                updateMapping((UpdateMappingTask) task);
                break;
            case ALLOCATION_DECISION:
                processAllocationDecision((AllocationDecision) task);
                break;
        }
    }

    public void electNewMaster() {
        // Master election process
        List<Node> masterEligibleNodes = clusterState.getMasterEligibleNodes();
        Node newMaster = selectMasterCandidate(masterEligibleNodes);

        if (newMaster != null) {
            clusterState.setMasterNode(newMaster);
            publishClusterState();
        }
    }

    private Node selectMasterCandidate(List<Node> candidates) {
        // Select master based on priority and ID
        return candidates.stream()
            .filter(Node::isMasterEligible)
            .max(Comparator.comparing(Node::getMasterPriority)
                .thenComparing(Node::getNodeId))
            .orElse(null);
    }
}
```

#### Data Nodes
```java
public class DataNode {

    private Map<String, IndexShard> shards;
    private Map<String, Translog> translogs;
    private LuceneIndexManager luceneManager;

    // Data node responsibilities
    public void storeShard(IndexShard shard) {
        String shardId = shard.getShardId();
        shards.put(shardId, shard);

        // Initialize translog for durability
        Translog translog = createTranslog(shardId);
        translogs.put(shardId, translog);

        // Start shard recovery if needed
        recoverShard(shard);
    }

    public SearchResult search(SearchRequest request) {
        // Route search to relevant shards
        List<IndexShard> relevantShards = findRelevantShards(request);

        // Execute search across shards
        List<ShardSearchResult> shardResults = relevantShards.stream()
            .map(shard -> shard.search(request))
            .collect(Collectors.toList());

        // Merge results
        return mergeResults(shardResults);
    }

    public void indexDocument(IndexRequest request) {
        String index = request.index();
        String id = request.id();

        // Route to primary shard
        IndexShard primaryShard = routeToPrimaryShard(index, id);
        primaryShard.indexDocument(request);

        // Replicate to replica shards
        replicateToReplicas(primaryShard, request);
    }

    private IndexShard routeToPrimaryShard(String index, String id) {
        // Consistent hashing for shard routing
        int shardId = Hashing.consistentHash(id, numberOfShards);
        return shards.get(index + "_" + shardId);
    }

    private void replicateToReplicas(IndexShard primaryShard, IndexRequest request) {
        List<IndexShard> replicas = findReplicaShards(primaryShard);

        for (IndexShard replica : replicas) {
            try {
                replica.replicateDocument(request);
            } catch (Exception e) {
                log.error("Failed to replicate to replica shard", e);
                // Handle replication failure
            }
        }
    }
}
```

### Cluster State

#### State Management
```java
public class ClusterStateManager {

    private ClusterState currentState;
    private AtomicLong stateVersion;

    public void updateClusterState(ClusterStateUpdate update) {
        synchronized (this) {
            // Apply update to current state
            ClusterState newState = applyUpdate(currentState, update);
            newState.setVersion(stateVersion.incrementAndGet());

            // Validate new state
            validateState(newState);

            // Publish to all nodes
            publishState(newState);

            // Update current state
            currentState = newState;
        }
    }

    private ClusterState applyUpdate(ClusterState currentState, ClusterStateUpdate update) {
        ClusterState.Builder builder = ClusterState.builder(currentState);

        switch (update.getType()) {
            case NODE_JOINED:
                builder.addNode(update.getNode());
                break;
            case NODE_LEFT:
                builder.removeNode(update.getNode());
                break;
            case INDEX_CREATED:
                builder.addIndex(update.getIndex());
                break;
            case SHARD_ROUTING_CHANGED:
                builder.updateRoutingTable(update.getRoutingTable());
                break;
        }

        return builder.build();
    }

    private void validateState(ClusterState state) {
        // Validate cluster consistency
        if (!isQuorumAvailable(state)) {
            throw new ClusterStateException("No quorum available");
        }

        if (!areShardsAllocated(state)) {
            throw new ClusterStateException("Unassigned shards detected");
        }
    }

    private boolean isQuorumAvailable(ClusterState state) {
        int masterEligibleNodes = state.getMasterEligibleNodes().size();
        int quorum = masterEligibleNodes / 2 + 1;
        return state.getActiveMasterEligibleNodes() >= quorum;
    }

    private boolean areShardsAllocated(ClusterState state) {
        return state.getRoutingTable().getUnassignedShards().isEmpty();
    }

    private void publishState(ClusterState state) {
        // Send state to all nodes
        clusterService.sendToAllNodes(state);
    }
}
```

## Типы узлов

### Master-eligible Nodes

#### Конфигурация
```yaml
# elasticsearch.yml for master-eligible node
node.roles: [ master ]

# Master node settings
discovery.seed_hosts: ["host1:9300", "host2:9300", "host3:9300"]
cluster.initial_master_nodes: ["node1", "node2", "node3"]

# JVM settings for master nodes
-Xms2g
-Xmx2g

# Disable data operations
node.data: false
node.ingest: false
node.ml: false
```

#### Master Election Process
```java
public class MasterElectionService {

    private static final long ELECTION_TIMEOUT_MS = 30000;
    private static final int MINIMUM_MASTER_NODES = 2;

    public Node electMaster(List<Node> candidates) {
        // Filter master-eligible nodes
        List<Node> masterCandidates = candidates.stream()
            .filter(Node::isMasterEligible)
            .filter(Node::isActive)
            .collect(Collectors.toList());

        if (masterCandidates.size() < MINIMUM_MASTER_NODES) {
            throw new InsufficientMasterNodesException(
                "Need at least " + MINIMUM_MASTER_NODES + " master-eligible nodes, got " + masterCandidates.size());
        }

        // Sort by priority and ID for deterministic election
        masterCandidates.sort(Comparator
            .comparing(Node::getMasterPriority, Comparator.reverseOrder())
            .thenComparing(Node::getNodeId));

        Node electedMaster = masterCandidates.get(0);

        log.info("Elected master node: {}", electedMaster.getNodeId());

        return electedMaster;
    }

    public void handleMasterFailure(Node failedMaster) {
        log.warn("Master node {} failed, starting election", failedMaster.getNodeId());

        // Start election timer
        electionTimer.schedule(() -> {
            try {
                Node newMaster = electMaster(clusterState.getNodes());
                clusterState.setMasterNode(newMaster);
                clusterService.publishClusterState(clusterState);
            } catch (Exception e) {
                log.error("Failed to elect new master", e);
                // Retry election or enter split-brain protection
            }
        }, ELECTION_TIMEOUT_MS);
    }
}
```

### Data Nodes

#### Конфигурация
```yaml
# elasticsearch.yml for data node
node.roles: [ data ]

# Data node settings
path.data: /var/lib/elasticsearch/data
path.logs: /var/log/elasticsearch

# JVM settings for data nodes (higher memory)
-Xms16g
-Xmx16g

# Thread pools for data operations
thread_pool.write.size: 8
thread_pool.search.size: 16
thread_pool.bulk.size: 8

# Disable master operations
node.master: false
node.ingest: false
```

### Coordinating Nodes

#### Load Balancer Configuration
```yaml
# elasticsearch.yml for coordinating node
node.roles: [ ]

# Coordinating node settings
http.port: 9200
discovery.seed_hosts: ["data-node1:9300", "data-node2:9300"]

# JVM settings
-Xms2g
-Xmx2g

# Thread pools for coordination
thread_pool.search.size: 4
thread_pool.get.size: 4

# Disable data and master operations
node.data: false
node.master: false
node.ingest: false
```

### Ingest Nodes

#### Data Processing Configuration
```yaml
# elasticsearch.yml for ingest node
node.roles: [ ingest ]

# Ingest node settings
http.enabled: false

# Pipeline configurations
node.ingest: true

# JVM settings
-Xms4g
-Xmx4g

# Thread pools for ingest
thread_pool.write.queue_size: 200
thread_pool.write.size: 4
```

## Discovery и формирование кластера

### Seed Hosts

#### Конфигурация discovery
```yaml
# elasticsearch.yml
discovery.seed_hosts:
  - host1:9300
  - host2:9300
  - host3:9300

# For cloud environments
discovery.seed_providers: ec2

# EC2 discovery settings
discovery.ec2.groups: sg-elasticsearch
discovery.ec2.host_type: private_ip
discovery.ec2.tag.ElasticSearch: elasticsearch-node
```

### Cluster Bootstrap

#### Initial Cluster Formation
```bash
# Start first master-eligible node
bin/elasticsearch -E cluster.name=my-cluster \
  -E node.name=node1 \
  -E discovery.seed_hosts=node1:9300 \
  -E cluster.initial_master_nodes=node1

# Start second master-eligible node
bin/elasticsearch -E cluster.name=my-cluster \
  -E node.name=node2 \
  -E discovery.seed_hosts=node1:9300,node2:9300 \
  -E cluster.initial_master_nodes=node1,node2

# Start third master-eligible node
bin/elasticsearch -E cluster.name=my-cluster \
  -E node.name=node3 \
  -E discovery.seed_hosts=node1:9300,node2:9300,node3:9300 \
  -E cluster.initial_master_nodes=node1,node2,node3
```

#### Bootstrap Validation
```bash
# Check cluster health
curl -X GET "localhost:9200/_cluster/health?pretty"

# Check cluster state
curl -X GET "localhost:9200/_cluster/state"

# List cluster nodes
curl -X GET "localhost:9200/_cat/nodes?v"
```

### Network Configuration

#### Cluster Communication
```yaml
# elasticsearch.yml
network.host: 0.0.0.0
http.port: 9200
http.publish_host: _eth0_

# Transport layer
transport.tcp.port: 9300
transport.publish_host: _eth0_
transport.publish_port: 9300

# Security
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: certs/elastic-certificates.p12
```

## Управление шардами

### Shard Allocation

#### Allocation Strategies
```java
public class ShardAllocationService {

    @Autowired
    private ClusterState clusterState;

    @Autowired
    private RoutingTable routingTable;

    public void allocateUnassignedShards() {
        List<ShardRouting> unassignedShards = routingTable.getUnassignedShards();

        for (ShardRouting shard : unassignedShards) {
            try {
                Node targetNode = findOptimalNode(shard);

                if (targetNode != null) {
                    allocateShardToNode(shard, targetNode);
                } else {
                    log.warn("No suitable node found for shard: {}", shard.getShardId());
                }

            } catch (Exception e) {
                log.error("Failed to allocate shard: {}", shard.getShardId(), e);
            }
        }
    }

    private Node findOptimalNode(ShardRouting shard) {
        List<Node> eligibleNodes = getEligibleNodes(shard);

        return eligibleNodes.stream()
            .min(Comparator.comparing(this::calculateAllocationScore))
            .orElse(null);
    }

    private List<Node> getEligibleNodes(ShardRouting shard) {
        return clusterState.getNodes().stream()
            .filter(Node::isDataNode)
            .filter(node -> canAllocateShard(node, shard))
            .collect(Collectors.toList());
    }

    private boolean canAllocateShard(Node node, ShardRouting shard) {
        // Check node capacity
        if (node.getAllocatedShards() >= node.getMaxShards()) {
            return false;
        }

        // Check disk space
        if (node.getDiskUsage() > 0.85) {
            return false;
        }

        // Check memory usage
        if (node.getMemoryUsage() > 0.8) {
            return false;
        }

        // Check shard allocation rules
        return allocationDecider.canAllocate(shard, node);
    }

    private double calculateAllocationScore(Node node) {
        // Calculate score based on multiple factors
        double diskScore = node.getDiskUsage();
        double memoryScore = node.getMemoryUsage();
        double shardScore = (double) node.getAllocatedShards() / node.getMaxShards();
        double loadScore = node.getCurrentLoad();

        // Weighted average
        return (diskScore * 0.3) + (memoryScore * 0.3) +
               (shardScore * 0.25) + (loadScore * 0.15);
    }

    private void allocateShardToNode(ShardRouting shard, Node node) {
        // Update routing table
        routingTable.allocateShard(shard, node);

        // Update cluster state
        clusterState.updateShardRouting(shard, node);

        // Start shard on node
        node.allocateShard(shard);

        log.info("Allocated shard {} to node {}", shard.getShardId(), node.getNodeId());
    }
}
```

### Shard Rebalancing

#### Automatic Rebalancing
```yaml
# elasticsearch.yml
cluster.routing.allocation.enable: all
cluster.routing.allocation.allow_rebalance: indices_all_active
cluster.routing.rebalance.enable: all

# Rebalancing thresholds
cluster.routing.allocation.cluster_concurrent_rebalance: 2
cluster.routing.allocation.node_concurrent_recoveries: 2
cluster.routing.allocation.node_initial_primaries_recoveries: 4

# Disk-based allocation
cluster.routing.allocation.disk.threshold_enabled: true
cluster.routing.allocation.disk.watermark.low: 85%
cluster.routing.allocation.disk.watermark.high: 90%
cluster.routing.allocation.disk.watermark.flood_stage: 95%
```

#### Manual Rebalancing
```bash
# Check current allocation
curl -X GET "localhost:9200/_cat/shards?v"

# Move shard manually
curl -X POST "localhost:9200/_cluster/reroute" \
  -H "Content-Type: application/json" \
  -d '{
    "commands": [
      {
        "move": {
          "index": "my_index",
          "shard": 0,
          "from_node": "node1",
          "to_node": "node2"
        }
      }
    ]
  }'

# Cancel allocation
curl -X POST "localhost:9200/_cluster/reroute" \
  -H "Content-Type: application/json" \
  -d '{
    "commands": [
      {
        "cancel": {
          "index": "my_index",
          "shard": 0,
          "node": "node1",
          "allow_primary": true
        }
      }
    ]
  }'
```

## Репликация и отказоустойчивость

### Primary-Replica Model

#### Replication Process
```java
public class ReplicationManager {

    @Autowired
    private ClusterState clusterState;

    public void replicateDocument(IndexRequest request, IndexShard primaryShard) {
        List<IndexShard> replicaShards = findReplicaShards(primaryShard);

        // Parallel replication to all replicas
        List<CompletableFuture<Void>> replicationTasks = replicaShards.stream()
            .map(replica -> CompletableFuture.runAsync(() ->
                replicateToReplica(request, primaryShard, replica)))
            .collect(Collectors.toList());

        // Wait for replication completion
        try {
            CompletableFuture.allOf(replicationTasks.toArray(new CompletableFuture[0]))
                .get(30, TimeUnit.SECONDS); // Timeout

        } catch (TimeoutException e) {
            handleReplicationTimeout(request, primaryShard, replicaShards);
        } catch (Exception e) {
            handleReplicationFailure(request, primaryShard, e);
        }
    }

    private void replicateToReplica(IndexRequest request, IndexShard primaryShard, IndexShard replica) {
        try {
            // Send document to replica
            replica.receiveReplicatedDocument(request);

            // Wait for acknowledgment
            waitForReplicaAcknowledgment(replica, request);

        } catch (Exception e) {
            log.error("Replication failed for replica: {}", replica.getNodeId(), e);
            throw new ReplicationException("Failed to replicate to replica", e);
        }
    }

    private void waitForReplicaAcknowledgment(IndexShard replica, IndexRequest request) {
        long timeoutMs = 5000; // 5 seconds
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (replica.hasAcknowledged(request.getId())) {
                return;
            }

            try {
                Thread.sleep(10); // Small delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Replica acknowledgment timeout");
    }

    private List<IndexShard> findReplicaShards(IndexShard primaryShard) {
        return clusterState.getRoutingTable()
            .getReplicaShards(primaryShard.getIndex(), primaryShard.getShardId());
    }

    private void handleReplicationTimeout(IndexRequest request, IndexShard primaryShard,
                                        List<IndexShard> replicas) {
        log.warn("Replication timeout for request: {}", request.getId());

        // Continue with available replicas if consistency allows
        if (clusterState.getWriteConsistency() != ConsistencyLevel.ALL) {
            // Accept partial replication
            return;
        }

        // Fail the operation
        throw new ReplicationTimeoutException("Replication timeout");
    }

    private void handleReplicationFailure(IndexRequest request, IndexShard primaryShard, Exception e) {
        log.error("Replication failure for request: {}", request.getId(), e);

        // Mark replicas as stale
        markReplicasAsStale(primaryShard);

        // Depending on consistency level, either fail or continue
        if (requiresAllReplicas()) {
            throw new ReplicationFailureException("Replication failed", e);
        }
    }

    private boolean requiresAllReplicas() {
        return clusterState.getWriteConsistency() == ConsistencyLevel.ALL;
    }

    private void markReplicasAsStale(IndexShard primaryShard) {
        List<IndexShard> replicas = findReplicaShards(primaryShard);
        replicas.forEach(replica -> replica.markAsStale());
    }
}
```

### Consistency Levels

#### Write Consistency
```java
public enum ConsistencyLevel {
    ONE,        // Wait for 1 replica (including primary)
    QUORUM,     // Wait for majority of replicas
    ALL         // Wait for all replicas
}

public class ConsistencyManager {

    public boolean canAcceptWrite(String index, ConsistencyLevel consistency) {
        ClusterState state = clusterState.getCurrentState();
        IndexMetadata indexMetadata = state.getIndexMetadata(index);

        int totalShards = indexMetadata.getNumberOfShards();
        int replicationFactor = indexMetadata.getNumberOfReplicas();
        int totalCopies = totalShards * (replicationFactor + 1);

        int requiredCopies = calculateRequiredCopies(consistency, totalCopies);

        return getAvailableCopies(index) >= requiredCopies;
    }

    private int calculateRequiredCopies(ConsistencyLevel consistency, int totalCopies) {
        switch (consistency) {
            case ONE:
                return 1;
            case QUORUM:
                return (totalCopies / 2) + 1;
            case ALL:
                return totalCopies;
            default:
                return 1;
        }
    }

    private int getAvailableCopies(String index) {
        // Count currently available shard copies
        return clusterState.getRoutingTable()
            .getActiveShards(index)
            .size();
    }
}
```

### Failure Recovery

#### Replica Failover
```java
public class ReplicaFailoverService {

    @Autowired
    private ClusterState clusterState;

    @Autowired
    private ShardAllocationService allocationService;

    public void handleReplicaFailure(IndexShard failedReplica) {
        log.warn("Replica shard failed: {}", failedReplica.getShardId());

        try {
            // Mark replica as failed
            clusterState.markShardAsFailed(failedReplica);

            // Find new node for replica
            Node newNode = allocationService.findOptimalNode(failedReplica);

            if (newNode != null) {
                // Allocate new replica
                IndexShard newReplica = createReplicaShard(failedReplica, newNode);
                allocationService.allocateShardToNode(newReplica, newNode);

                // Start recovery from primary
                recoverReplicaFromPrimary(newReplica, failedReplica.getPrimaryShard());

            } else {
                log.error("No suitable node found for replica replacement");
                // Alert administrators
                alertService.sendCriticalAlert(
                    "Replica Replacement Failed",
                    "Cannot allocate replacement for failed replica: " + failedReplica.getShardId()
                );
            }

        } catch (Exception e) {
            log.error("Failed to handle replica failure: {}", failedReplica.getShardId(), e);
        }
    }

    private IndexShard createReplicaShard(IndexShard failedReplica, Node newNode) {
        IndexShard replica = new IndexShard(
            failedReplica.getIndex(),
            failedReplica.getShardId(),
            false  // Not primary
        );

        replica.setNode(newNode);
        return replica;
    }

    private void recoverReplicaFromPrimary(IndexShard replica, IndexShard primary) {
        try {
            // Start recovery process
            RecoveryRequest recoveryRequest = new RecoveryRequest(primary, replica);
            recoveryManager.startRecovery(recoveryRequest);

            // Wait for completion
            waitForRecoveryCompletion(recoveryRequest);

            log.info("Replica recovery completed: {}", replica.getShardId());

        } catch (Exception e) {
            log.error("Replica recovery failed: {}", replica.getShardId(), e);
            throw new RecoveryException("Failed to recover replica", e);
        }
    }

    private void waitForRecoveryCompletion(RecoveryRequest request) {
        long timeoutMs = 30 * 60 * 1000; // 30 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (request.isCompleted()) {
                return;
            }

            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Recovery timeout");
    }
}
```

## Балансировка нагрузки

### Load Balancing Strategies

#### Request-Level Load Balancing
```java
public class LoadBalancer {

    @Autowired
    private ClusterState clusterState;

    @Autowired
    private NodeSelector nodeSelector;

    public Node selectNodeForRequest(SearchRequest request) {
        List<Node> eligibleNodes = getEligibleNodes(request);

        if (eligibleNodes.isEmpty()) {
            throw new NoEligibleNodesException("No eligible nodes for request");
        }

        // Select node based on load balancing strategy
        return nodeSelector.selectNode(eligibleNodes, request);
    }

    private List<Node> getEligibleNodes(SearchRequest request) {
        return clusterState.getNodes().stream()
            .filter(Node::isDataNode)
            .filter(node -> canHandleRequest(node, request))
            .collect(Collectors.toList());
    }

    private boolean canHandleRequest(Node node, SearchRequest request) {
        // Check if node has required shards
        List<String> requiredIndices = request.indices();
        RoutingTable routingTable = clusterState.getRoutingTable();

        return requiredIndices.stream()
            .allMatch(index -> routingTable.hasShardsOnNode(index, node));
    }
}

public class NodeSelector {

    public enum LoadBalancingStrategy {
        ROUND_ROBIN,
        LEAST_CONNECTIONS,
        LEAST_RESPONSE_TIME,
        RANDOM,
        ADAPTIVE
    }

    private LoadBalancingStrategy strategy = LoadBalancingStrategy.ADAPTIVE;
    private Map<Node, AtomicInteger> connectionCounts = new ConcurrentHashMap<>();
    private Map<Node, Double> responseTimeAverages = new ConcurrentHashMap<>();

    public Node selectNode(List<Node> eligibleNodes, SearchRequest request) {
        switch (strategy) {
            case ROUND_ROBIN:
                return selectRoundRobin(eligibleNodes);
            case LEAST_CONNECTIONS:
                return selectLeastConnections(eligibleNodes);
            case LEAST_RESPONSE_TIME:
                return selectLeastResponseTime(eligibleNodes);
            case RANDOM:
                return selectRandom(eligibleNodes);
            case ADAPTIVE:
                return selectAdaptive(eligibleNodes, request);
            default:
                return eligibleNodes.get(0);
        }
    }

    private Node selectRoundRobin(List<Node> nodes) {
        // Simple round-robin selection
        int index = roundRobinCounter.getAndIncrement() % nodes.size();
        return nodes.get(index);
    }

    private Node selectLeastConnections(List<Node> nodes) {
        return nodes.stream()
            .min(Comparator.comparing(node ->
                connectionCounts.computeIfAbsent(node, k -> new AtomicInteger(0)).get()))
            .orElse(nodes.get(0));
    }

    private Node selectLeastResponseTime(List<Node> nodes) {
        return nodes.stream()
            .min(Comparator.comparing(node ->
                responseTimeAverages.getOrDefault(node, Double.MAX_VALUE)))
            .orElse(nodes.get(0));
    }

    private Node selectAdaptive(List<Node> nodes, SearchRequest request) {
        // Adaptive selection based on request type and current load
        if (request.isHeavyQuery()) {
            return selectLeastConnections(nodes);
        } else {
            return selectRoundRobin(nodes);
        }
    }

    // Update metrics for adaptive selection
    public void updateConnectionCount(Node node, int delta) {
        connectionCounts.computeIfAbsent(node, k -> new AtomicInteger(0))
            .addAndGet(delta);
    }

    public void updateResponseTime(Node node, long responseTimeMs) {
        double currentAvg = responseTimeAverages.getOrDefault(node, 0.0);
        double newAvg = (currentAvg + responseTimeMs) / 2.0; // Simple moving average
        responseTimeAverages.put(node, newAvg);
    }
}
```

### Shard-Level Balancing

#### Shard Rebalancing
```java
public class ShardRebalancer {

    @Autowired
    private ClusterState clusterState;

    @Autowired
    private ShardAllocationService allocationService;

    public void rebalanceShards() {
        if (!shouldRebalance()) {
            return;
        }

        log.info("Starting shard rebalancing");

        List<RebalanceAction> actions = calculateRebalanceActions();

        for (RebalanceAction action : actions) {
            try {
                executeRebalanceAction(action);
                log.info("Executed rebalance action: {}", action);

            } catch (Exception e) {
                log.error("Failed to execute rebalance action: {}", action, e);
            }
        }

        log.info("Shard rebalancing completed");
    }

    private boolean shouldRebalance() {
        // Check if rebalancing is enabled
        if (!clusterSettings.isRebalancingEnabled()) {
            return false;
        }

        // Check if cluster is stable
        if (clusterState.hasUnassignedShards()) {
            return false; // Wait for allocation first
        }

        // Check rebalancing threshold
        double imbalanceRatio = calculateImbalanceRatio();
        return imbalanceRatio > clusterSettings.getRebalanceThreshold();
    }

    private double calculateImbalanceRatio() {
        List<Node> dataNodes = clusterState.getDataNodes();
        List<Integer> shardCounts = dataNodes.stream()
            .map(Node::getAllocatedShards)
            .sorted()
            .collect(Collectors.toList());

        if (shardCounts.isEmpty()) return 0.0;

        int minShards = shardCounts.get(0);
        int maxShards = shardCounts.get(shardCounts.size() - 1);
        int avgShards = shardCounts.stream().mapToInt(Integer::intValue).sum() / shardCounts.size();

        return avgShards > 0 ? (double) (maxShards - minShards) / avgShards : 0.0;
    }

    private List<RebalanceAction> calculateRebalanceActions() {
        List<RebalanceAction> actions = new ArrayList<>();
        Map<Node, List<ShardRouting>> currentAllocation = clusterState.getShardAllocation();

        // Find overloaded nodes
        List<Node> overloadedNodes = findOverloadedNodes(currentAllocation);

        // Find underloaded nodes
        List<Node> underloadedNodes = findUnderloadedNodes(currentAllocation);

        // Generate move actions
        for (Node source : overloadedNodes) {
            for (Node target : underloadedNodes) {
                if (canMoveShardBetween(source, target)) {
                    List<ShardRouting> movableShards = findMovableShards(source);
                    for (ShardRouting shard : movableShards) {
                        if (wouldBalanceHelp(shard, source, target)) {
                            actions.add(new RebalanceAction(shard, source, target));
                            break; // One action per source-target pair
                        }
                    }
                }
            }
        }

        return actions;
    }

    private List<Node> findOverloadedNodes(Map<Node, List<ShardRouting>> allocation) {
        int avgShards = calculateAverageShards(allocation);
        int threshold = (int) (avgShards * (1 + clusterSettings.getOverloadThreshold()));

        return allocation.entrySet().stream()
            .filter(entry -> entry.getValue().size() > threshold)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private List<Node> findUnderloadedNodes(Map<Node, List<ShardRouting>> allocation) {
        int avgShards = calculateAverageShards(allocation);
        int threshold = (int) (avgShards * (1 - clusterSettings.getUnderloadThreshold()));

        return allocation.entrySet().stream()
            .filter(entry -> entry.getValue().size() < threshold)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private boolean wouldBalanceHelp(ShardRouting shard, Node source, Node target) {
        int sourceShardsAfter = source.getAllocatedShards() - 1;
        int targetShardsAfter = target.getAllocatedShards() + 1;

        int avgShards = calculateAverageShards(clusterState.getShardAllocation());

        // Check if move reduces imbalance
        double imbalanceBefore = Math.abs(source.getAllocatedShards() - avgShards) +
                                Math.abs(target.getAllocatedShards() - avgShards);
        double imbalanceAfter = Math.abs(sourceShardsAfter - avgShards) +
                               Math.abs(targetShardsAfter - avgShards);

        return imbalanceAfter < imbalanceBefore;
    }

    private void executeRebalanceAction(RebalanceAction action) {
        // Move shard from source to target
        routingService.moveShard(action.getShard(), action.getSourceNode(), action.getTargetNode());

        // Update cluster state
        clusterState.updateShardLocation(action.getShard(), action.getTargetNode());

        // Wait for relocation to complete
        waitForRelocation(action.getShard());
    }

    private void waitForRelocation(ShardRouting shard) {
        long timeoutMs = 30 * 60 * 1000; // 30 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (clusterState.isShardRelocated(shard)) {
                return;
            }

            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Shard relocation timeout");
    }

    private int calculateAverageShards(Map<Node, List<ShardRouting>> allocation) {
        int totalShards = allocation.values().stream()
            .mapToInt(List::size)
            .sum();

        return totalShards / allocation.size();
    }
}
```

## Масштабирование кластера

### Horizontal Scaling

#### Adding Nodes
```bash
# 1. Prepare new node
# Configure elasticsearch.yml
cluster.name: my-cluster
node.name: node4
discovery.seed_hosts: ["node1:9300", "node2:9300", "node3:9300"]
network.host: 192.168.1.14

# 2. Start new node
./bin/elasticsearch

# 3. Verify node joined cluster
curl -X GET "localhost:9200/_cluster/health?pretty"

# 4. Check shard allocation
curl -X GET "localhost:9200/_cat/shards?v"

# 5. Force rebalancing if needed
curl -X POST "localhost:9200/_cluster/reroute?retry_failed"
```

#### Automated Node Provisioning
```java
@Service
public class NodeProvisioningService {

    @Autowired
    private InfrastructureProvider infrastructure;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private ClusterManagementService clusterService;

    public Node provisionNewNode(NodeSpecification spec) {
        try {
            log.info("Provisioning new node with spec: {}", spec);

            // 1. Allocate infrastructure
            InfrastructureResource resource = infrastructure.allocate(spec);

            // 2. Configure node
            NodeConfiguration config = createNodeConfiguration(spec, resource);

            // 3. Deploy Elasticsearch
            deployElasticsearch(resource, config);

            // 4. Start node
            startElasticsearch(resource);

            // 5. Wait for node to join cluster
            Node newNode = waitForNodeJoin(resource.getAddress());

            // 6. Verify node health
            verifyNodeHealth(newNode);

            log.info("Successfully provisioned node: {}", newNode.getNodeId());
            return newNode;

        } catch (Exception e) {
            log.error("Failed to provision node", e);
            // Cleanup failed resources
            cleanupFailedProvision(resource);
            throw new ProvisioningException("Node provisioning failed", e);
        }
    }

    private NodeConfiguration createNodeConfiguration(NodeSpecification spec, InfrastructureResource resource) {
        return NodeConfiguration.builder()
            .clusterName(clusterService.getClusterName())
            .nodeName("node-" + resource.getId())
            .nodeRoles(spec.getRoles())
            .networkHost(resource.getAddress())
            .seedHosts(clusterService.getSeedHosts())
            .dataPath(spec.getDataPath())
            .heapSize(spec.getHeapSize())
            .build();
    }

    private void deployElasticsearch(InfrastructureResource resource, NodeConfiguration config) {
        // Copy Elasticsearch binaries
        infrastructure.uploadFile(resource, "elasticsearch.tar.gz", "/opt/");

        // Extract and configure
        infrastructure.executeCommand(resource,
            "tar -xzf /opt/elasticsearch.tar.gz -C /opt/ && " +
            "mv /opt/elasticsearch-* /opt/elasticsearch");

        // Generate configuration file
        String configContent = configService.generateConfigFile(config);
        infrastructure.writeFile(resource, "/opt/elasticsearch/config/elasticsearch.yml", configContent);

        // Setup systemd service
        setupSystemdService(resource);
    }

    private void startElasticsearch(InfrastructureResource resource) {
        infrastructure.executeCommand(resource, "systemctl start elasticsearch");
    }

    private Node waitForNodeJoin(String address) {
        long timeoutMs = 5 * 60 * 1000; // 5 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                Node node = clusterService.getNodeByAddress(address);
                if (node != null && node.isActive()) {
                    return node;
                }

                Thread.sleep(5000); // Wait 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new TimeoutException("Node failed to join cluster within timeout");
    }

    private void verifyNodeHealth(Node node) {
        // Check node is healthy
        if (!clusterService.isNodeHealthy(node)) {
            throw new HealthCheckException("Node health check failed");
        }

        // Check JVM metrics
        if (node.getHeapUsage() > 0.9) {
            log.warn("High heap usage on new node: {}", node.getNodeId());
        }

        // Check disk space
        if (node.getDiskUsage() > 0.8) {
            log.warn("High disk usage on new node: {}", node.getNodeId());
        }
    }

    private void cleanupFailedProvision(InfrastructureResource resource) {
        try {
            infrastructure.deallocate(resource);
        } catch (Exception e) {
            log.error("Failed to cleanup failed provision", e);
        }
    }
}
```

### Vertical Scaling

#### Node Upgrade
```bash
# 1. Disable shard allocation
curl -X PUT "localhost:9200/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "persistent": {
      "cluster.routing.allocation.enable": "none"
    }
  }'

# 2. Stop node
sudo systemctl stop elasticsearch

# 3. Upgrade infrastructure (CPU, RAM, disk)
# This is done at the VM/hypervisor level

# 4. Start node
sudo systemctl start elasticsearch

# 5. Enable shard allocation
curl -X PUT "localhost:9200/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "persistent": {
      "cluster.routing.allocation.enable": "all"
    }
  }'

# 6. Wait for cluster to stabilize
curl -X GET "localhost:9200/_cluster/health?wait_for_status=yellow&timeout=30s"
```

## Мониторинг кластера

### Cluster Health API

#### Health Monitoring
```bash
# Basic cluster health
curl -X GET "localhost:9200/_cluster/health?pretty"

# Detailed health with explanations
curl -X GET "localhost:9200/_cluster/health?level=cluster&pretty"

# Wait for specific status
curl -X GET "localhost:9200/_cluster/health?wait_for_status=green&timeout=50s"

# Health for specific indices
curl -X GET "localhost:9200/_cluster/health/my_index*?pretty"
```

#### Health Response Analysis
```json
{
  "cluster_name": "elasticsearch",
  "status": "yellow",
  "timed_out": false,
  "number_of_nodes": 3,
  "number_of_data_nodes": 3,
  "active_primary_shards": 5,
  "active_shards": 5,
  "relocating_shards": 0,
  "initializing_shards": 0,
  "unassigned_shards": 5,
  "delayed_unassigned_shards": 0,
  "number_of_pending_tasks": 0,
  "number_of_in_flight_fetch": 0,
  "task_max_waiting_in_queue_millis": 0,
  "active_shards_percent_as_number": 50.0
}
```

### Cluster Stats

#### Detailed Statistics
```bash
# Cluster statistics
curl -X GET "localhost:9200/_cluster/stats?pretty"

# Node statistics
curl -X GET "localhost:9200/_nodes/stats?pretty"

# Index statistics
curl -X GET "localhost:9200/_stats?pretty"

# Shard statistics
curl -X GET "localhost:9200/_cat/shards?v"
```

### Custom Monitoring

#### Cluster Metrics Collector
```java
@Service
public class ClusterMetricsCollector {

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private MetricsRegistry registry;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void collectClusterMetrics() {
        try {
            // Collect basic health metrics
            ClusterHealthResponse health = getClusterHealth();
            updateHealthMetrics(health);

            // Collect node metrics
            NodesStatsResponse nodesStats = getNodesStats();
            updateNodeMetrics(nodesStats);

            // Collect index metrics
            IndicesStatsResponse indicesStats = getIndicesStats();
            updateIndexMetrics(indicesStats);

            // Collect JVM metrics
            updateJvmMetrics();

        } catch (Exception e) {
            log.error("Failed to collect cluster metrics", e);
        }
    }

    private ClusterHealthResponse getClusterHealth() throws IOException {
        return client.cluster().health();
    }

    private void updateHealthMetrics(ClusterHealthResponse health) {
        registry.gauge("cluster.status", getStatusValue(health.getStatus()));
        registry.gauge("cluster.nodes.total", health.getNumberOfNodes());
        registry.gauge("cluster.nodes.data", health.getNumberOfDataNodes());
        registry.gauge("cluster.shards.active", health.getActiveShards());
        registry.gauge("cluster.shards.unassigned", health.getUnassignedShards());
        registry.gauge("cluster.shards.relocating", health.getRelocatingShards());
        registry.gauge("cluster.pending_tasks", health.getNumberOfPendingTasks());
    }

    private int getStatusValue(ClusterHealthStatus status) {
        switch (status) {
            case GREEN: return 0;
            case YELLOW: return 1;
            case RED: return 2;
            default: return -1;
        }
    }

    private NodesStatsResponse getNodesStats() throws IOException {
        return client.nodes().stats();
    }

    private void updateNodeMetrics(NodesStatsResponse nodesStats) {
        for (Map.Entry<String, NodeStats> entry : nodesStats.getNodes().entrySet()) {
            String nodeId = entry.getKey();
            NodeStats stats = entry.getValue();

            // JVM metrics
            registry.gauge("node.jvm.heap.used", nodeId, stats.getJvm().getMem().getHeapUsed().getBytes());
            registry.gauge("node.jvm.heap.max", nodeId, stats.getJvm().getMem().getHeapMax().getBytes());

            // OS metrics
            registry.gauge("node.os.cpu.percent", nodeId, stats.getOs().getCpu().getPercent());
            registry.gauge("node.os.load_average", nodeId, stats.getOs().getLoadAverage());

            // Index metrics
            registry.gauge("node.indices.docs.count", nodeId, stats.getIndices().getDocs().getCount());
            registry.gauge("node.indices.store.size", nodeId, stats.getIndices().getStore().getSize().getBytes());
        }
    }

    private IndicesStatsResponse getIndicesStats() throws IOException {
        return client.indices().stats();
    }

    private void updateIndexMetrics(IndicesStatsResponse indicesStats) {
        // Overall indices metrics
        registry.gauge("indices.total.docs", indicesStats.getTotal().getDocs().getCount());
        registry.gauge("indices.total.store.size", indicesStats.getTotal().getStore().getSize().getBytes());
        registry.gauge("indices.total.search.query_total", indicesStats.getTotal().getSearch().getTotal());
        registry.gauge("indices.total.indexing.index_total", indicesStats.getTotal().getIndexing().getIndexTotal());
    }

    private void updateJvmMetrics() {
        // Custom JVM metrics collection
        for (Node node : clusterState.getNodes()) {
            if (node.isActive()) {
                registry.gauge("node.gc.collections", node.getNodeId(), node.getGcCollections());
                registry.gauge("node.gc.collection_time", node.getNodeId(), node.getGcCollectionTime());
            }
        }
    }

    // Alerting methods
    public boolean isClusterHealthy() {
        try {
            ClusterHealthResponse health = getClusterHealth();
            return health.getStatus() == ClusterHealthStatus.GREEN ||
                   health.getStatus() == ClusterHealthStatus.YELLOW;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getHealthIssues() {
        List<String> issues = new ArrayList<>();

        try {
            ClusterHealthResponse health = getClusterHealth();

            if (health.getUnassignedShards() > 0) {
                issues.add("Unassigned shards: " + health.getUnassignedShards());
            }

            if (health.getNumberOfPendingTasks() > 100) {
                issues.add("High pending tasks: " + health.getNumberOfPendingTasks());
            }

            if (health.getStatus() == ClusterHealthStatus.RED) {
                issues.add("Cluster status is RED");
            }

        } catch (Exception e) {
            issues.add("Failed to get cluster health: " + e.getMessage());
        }

        return issues;
    }
}
```

## Hot-`Warm`-Cold архитектура

### Data Lifecycle Management

#### Index Lifecycle Policy
```json
PUT _ilm/policy/hot_warm_cold_policy
{
  "policy": {
    "phases": {
      "hot": {
        "min_age": "0ms",
        "actions": {
          "rollover": {
            "max_size": "50gb",
            "max_age": "30d"
          },
          "set_priority": {
            "priority": 100
          }
        }
      },
      "warm": {
        "min_age": "30d",
        "actions": {
          "allocate": {
            "number_of_replicas": 1,
            "include": {
              "box_type": "warm"
            }
          },
          "forcemerge": {
            "max_num_segments": 1
          },
          "set_priority": {
            "priority": 50
          }
        }
      },
      "cold": {
        "min_age": "90d",
        "actions": {
          "allocate": {
            "number_of_replicas": 0,
            "include": {
              "box_type": "cold"
            }
          },
          "freeze": {},
          "set_priority": {
            "priority": 0
          }
        }
      },
      "delete": {
        "min_age": "365d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

#### Node Attributes for Data Tiers
```yaml
# Hot nodes
node.name: hot-node-1
node.roles: [ data_hot, master ]
node.attr.box_type: hot
node.attr.temperature: hot

# Warm nodes
node.name: warm-node-1
node.roles: [ data_warm ]
node.attr.box_type: warm
node.attr.temperature: warm

# Cold nodes
node.name: cold-node-1
node.roles: [ data_cold ]
node.attr.box_type: cold
node.attr.temperature: cold
```

### Automated Data Movement

#### Data Tier Migration
```java
@Service
public class DataTierMigrationService {

    @Autowired
    private IndexLifecycleService lifecycleService;

    @Autowired
    private ShardAllocationService allocationService;

    public void migrateIndexToWarmTier(String indexName) {
        try {
            log.info("Migrating index {} to warm tier", indexName);

            // 1. Update index settings for warm tier
            updateIndexForWarmTier(indexName);

            // 2. Move shards to warm nodes
            moveShardsToWarmNodes(indexName);

            // 3. Optimize for warm storage
            optimizeForWarmStorage(indexName);

            log.info("Successfully migrated index {} to warm tier", indexName);

        } catch (Exception e) {
            log.error("Failed to migrate index {} to warm tier", indexName, e);
            throw new MigrationException("Index migration failed", e);
        }
    }

    private void updateIndexForWarmTier(String indexName) {
        // Reduce replicas for warm tier
        client.indices().updateSettings(b -> b
            .index(indexName)
            .settings(s -> s
                .numberOfReplicas("1")
                .priority("50")
            )
        );
    }

    private void moveShardsToWarmNodes(String indexName) {
        // Get current shard allocation
        ClusterState state = clusterState.getCurrentState();
        RoutingTable routingTable = state.getRoutingTable();

        // Find warm nodes
        List<Node> warmNodes = clusterState.getNodes().stream()
            .filter(node -> "warm".equals(node.getAttributes().get("box_type")))
            .collect(Collectors.toList());

        // Move shards to warm nodes
        List<ShardRouting> shards = routingTable.getShards(indexName);
        for (ShardRouting shard : shards) {
            if (shard.isPrimary()) {
                Node targetNode = allocationService.selectNodeFromList(warmNodes, shard);
                if (targetNode != null) {
                    moveShard(shard, targetNode);
                }
            }
        }
    }

    private void optimizeForWarmStorage(String indexName) {
        // Force merge for better compression
        client.indices().forcemerge(f -> f
            .index(indexName)
            .maxNumSegments(1)
        );

        // Update refresh interval
        client.indices().updateSettings(b -> b
            .index(indexName)
            .settings(s -> s
                .refreshInterval("30s")
            )
        );
    }

    public void migrateToColdTier(String indexName) {
        // Similar process for cold tier
        updateIndexForColdTier(indexName);
        moveShardsToColdNodes(indexName);
        freezeIndex(indexName);
    }

    private void updateIndexForColdTier(String indexName) {
        client.indices().updateSettings(b -> b
            .index(indexName)
            .settings(s -> s
                .numberOfReplicas("0")
                .priority("0")
            )
        );
    }

    private void freezeIndex(String indexName) {
        client.indices().freeze(f -> f.index(indexName));
    }

    public void scheduleDataMigration() {
        // Schedule periodic migration checks
        List<String> indicesToMigrate = findIndicesForMigration();

        for (String indexName : indicesToMigrate) {
            long indexAge = getIndexAge(indexName);

            if (indexAge > 30 * 24 * 60 * 60 * 1000L) { // 30 days
                migrateIndexToWarmTier(indexName);
            }

            if (indexAge > 90 * 24 * 60 * 60 * 1000L) { // 90 days
                migrateToColdTier(indexName);
            }
        }
    }

    private List<String> findIndicesForMigration() {
        // Find indices older than hot retention period
        return client.indices().get(g -> g.index("*"))
            .result()
            .keySet()
            .stream()
            .filter(this::shouldMigrateIndex)
            .collect(Collectors.toList());
    }

    private boolean shouldMigrateIndex(String indexName) {
        // Check if index is eligible for migration
        return !indexName.startsWith(".") && // Not system index
               getIndexAge(indexName) > 7 * 24 * 60 * 60 * 1000L; // Older than 7 days
    }

    private long getIndexAge(String indexName) {
        // Calculate index age based on creation date
        return System.currentTimeMillis() - getIndexCreationTime(indexName);
    }
}
```

## Cluster APIs

### Cluster Management APIs

#### Cluster Settings
```bash
# Get cluster settings
curl -X GET "localhost:9200/_cluster/settings?pretty"

# Update transient settings
curl -X PUT "localhost:9200/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "transient": {
      "cluster.routing.allocation.enable": "none"
    }
  }'

# Update persistent settings
curl -X PUT "localhost:9200/_cluster/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "persistent": {
      "indices.recovery.max_bytes_per_sec": "50mb"
    }
  }'
```

#### Cluster Reroute API
```bash
# Manual shard allocation
curl -X POST "localhost:9200/_cluster/reroute" \
  -H "Content-Type: application/json" \
  -d '{
    "commands": [
      {
        "allocate_replica": {
          "index": "my_index",
          "shard": 0,
          "node": "node2"
        }
      }
    ]
  }'

# Cancel allocation
curl -X POST "localhost:9200/_cluster/reroute" \
  -H "Content-Type: application/json" \
  -d '{
    "commands": [
      {
        "cancel": {
          "index": "my_index",
          "shard": 0,
          "node": "node1"
        }
      }
    ]
  }'
```

### Node APIs

#### Node Information
```bash
# Node info
curl -X GET "localhost:9200/_nodes"

# Specific node info
curl -X GET "localhost:9200/_nodes/node1"

# Node stats
curl -X GET "localhost:9200/_nodes/stats"

# Node JVM stats
curl -X GET "localhost:9200/_nodes/stats/jvm"

# Node OS stats
curl -X GET "localhost:9200/_nodes/stats/os"
```

### Cluster State API

#### State Information
```bash
# Get cluster state
curl -X GET "localhost:9200/_cluster/state"

# State with specific metrics
curl -X GET "localhost:9200/_cluster/state?metrics=nodes,metadata,routing_table"

# State for specific indices
curl -X GET "localhost:9200/_cluster/state/my_index*"
```

## Решение проблем

### Common Cluster Issues

#### Red Cluster Status
```bash
# Check cluster health
curl -X GET "localhost:9200/_cluster/health?pretty"

# Check unassigned shards
curl -X GET "localhost:9200/_cat/shards?h=index,shard,prirep,state,node,unassigned.reason"

# Common reasons for unassigned shards:
# - NODE_LEFT: Node left the cluster
# - CLUSTER_RECOVERED: Cluster restarted
# - INDEX_CREATED: New index created
# - ALLOCATION_FAILED: Cannot allocate to any node

# Try to allocate unassigned shards
curl -X POST "localhost:9200/_cluster/reroute?retry_failed"
```

#### Yellow Cluster Status
```bash
# Yellow status means some replicas are unassigned
# This is normal for single-node clusters

# Check shard allocation
curl -X GET "localhost:9200/_cat/shards"

# For multi-node clusters, ensure replicas can be allocated
curl -X PUT "localhost:9200/_settings" \
  -H "Content-Type: application/json" \
  -d '{"number_of_replicas": 0}'
```

#### High CPU Usage
```bash
# Check hot threads
curl -X GET "localhost:9200/_nodes/hot_threads"

# Check thread pools
curl -X GET "localhost:9200/_cat/thread_pool?v"

# Check slow queries
curl -X GET "localhost:9200/_cluster/state?filter_path=metadata.persistent.task.task.xpack.searchable_snapshots.cache.prewarming"
```

#### Memory Issues
```bash
# Check JVM memory
curl -X GET "localhost:9200/_nodes/stats/jvm?pretty"

# Check circuit breaker
curl -X GET "localhost:9200/_nodes/stats/breaker?pretty"

# Force garbage collection (use carefully)
curl -X POST "localhost:9200/_nodes/_all/_refresh"
```

### Node Issues

#### Node Not Joining Cluster
```bash
# Check node logs
tail -f /var/log/elasticsearch/elasticsearch.log

# Check network connectivity
telnet node1 9300

# Check cluster name match
grep "cluster.name" /etc/elasticsearch/elasticsearch.yml

# Check seed hosts
curl -X GET "localhost:9200/_nodes"
```

#### Slow Node Performance
```bash
# Check disk I/O
iostat -x 1

# Check network latency
ping node2

# Check JVM garbage collection
curl -X GET "localhost:9200/_nodes/stats/jvm?filter_path=nodes.*.jvm.gc"

# Check thread dumps
jstack $(pgrep -f elasticsearch) > thread_dump.txt
```

### Index Issues

#### Index Creation Failures
```bash
# Check index settings
curl -X GET "localhost:9200/_cluster/settings"

# Check disk space
df -h /var/lib/elasticsearch

# Check shard allocation
curl -X GET "localhost:9200/_cluster/allocation/explain" \
  -H "Content-Type: application/json" \
  -d '{"index": "problematic_index", "shard": 0, "primary": true}'
```

#### Search Issues
```bash
# Enable slow log
curl -X PUT "localhost:9200/_settings" \
  -H "Content-Type: application/json" \
  -d '{
    "index.search.slowlog.threshold.query.warn": "10s",
    "index.search.slowlog.threshold.query.info": "5s"
  }'

# Check search stats
curl -X GET "localhost:9200/_nodes/stats/indices/search?pretty"
```

## Лучшие практики

### Cluster Planning

#### 1. Sizing Guidelines
- **Master nodes**: 3 **dedicated master nodes for production**
- **Data nodes**: **Size based** on **data volume and query load**
- **Coordinating nodes**: **Separate for heavy search workloads**
- **Ingest nodes**: **For data preprocessing pipelines**

#### 2. Network Configuration
- **Use dedicated network for cluster communication**
- **Configure firewall rules properly**
- **Use TLS for inter-node communication**
- **Monitor network latency and bandwidth usage**

#### 3. Security Setup
- **Enable `X-Pack` Security from day one**
- **Configure `TLS` certificates for all nodes**
- **Set up proper authentication and authorization**
- **Regular security audits and updates**

### Operational Best Practices

#### 1. Monitoring Setup
- **Implement comprehensive monitoring with Prometheus**/**Grafana**
- **Set up alerting for critical metrics**
- **Regular health checks and trend analysis**
- **Capacity planning based** on **metrics**

#### 2. Backup Strategy
- **Regular snapshots** to **reliable storage**
- **Test restore procedures regularly**
- **Multiple backup locations for disaster recovery**
- **Encrypt sensitive backups**

#### 3. Maintenance Windows
- **Schedule maintenance during low-traffic periods**
- **Rolling upgrades** to **minimize downtime**
- **Test changes** in **staging environment first**
- **Have rollback plans for failed deployments**

### Performance Optimization

#### 1. Shard Strategy
- **Right-size shards**: 10-50GB **per shard**
- **Avoid oversharding**: **Start with fewer larger shards**
- **Monitor shard performance and rebalance** as **needed**
- **Use index lifecycle for data tiering**

#### 2. Query Optimization
- **Use filters for cached**, **non-scoring queries**
- **Optimize date ranges and numeric ranges**
- **Consider search templates for repeated queries**
- **Monitor slow queries and optimize them**

#### 3. Resource Management
- **Configure `heap` size appropriately** (50% of system memory)
- **Monitor `JVM` metrics and garbage collection**
- **Tune `thread` pools based** on **workload**
- **Use circuit breakers** to **prevent cascading failures**

### Disaster Recovery

#### 1. High Availability
- **Multiple availability zones for geo-redundancy**
- **Replica configuration for fault tolerance**
- **Automated failover procedures**
- **Regular `DR` drills**

#### 2. Backup and Restore
- **Snapshot lifecycle management**
- **Cross-region backups for major disasters**
- **Point-`in-time` recovery capabilities**
- **Backup validation and integrity checks**

#### 3. Incident Response
- **Documented procedures for common failures**
- **Escalation paths and communication plans**
- **Post-mortem analysis for improvement**
- **Continuous learning from incidents**

**Кластеризация **Elasticsearch** — это комплексная тема, охватывающая архитектуру, конфигурацию, мониторинг и эксплуатацию распределенных систем. Ключевые аспекты успешного управления кластером:**

### Архитектурные решения:

1. **Node roles** — правильное разделение **master**, **data**, **coordinating nodes**
2. **Shard allocation** — оптимальное распределение шардов по узлам
3. **Replication strategy** — баланс между **availability** и **performance**
4. **Network configuration** — надежная коммуникация между узлами

### Операционные практики:

1. **Discovery и bootstrap** — автоматическое формирование кластера
2. **Health monitoring** — постоянный контроль состояния кластера
3. **Capacity planning** — планирование роста и ресурсов
4. **Maintenance procedures** — регулярное обслуживание и обновления

### Производительность и масштабируемость:

1. **Horizontal scaling** — добавление узлов для роста
2. **Vertical scaling** — апгрейд существующих ресурсов
3. **Load balancing** — распределение нагрузки между узлами
4. **Hot-warm-cold** — многоуровневое хранение данных

### Надежность и отказоустойчивость:

1. **Master election** — автоматический выбор **master** узла
2. **Failure detection** — обнаружение и обработка отказов
3. **Data replication** — обеспечение доступности данных
4. **Recovery procedures** — восстановление после сбоев

### Мониторинг и troubleshooting:

1. **Cluster APIs** — программный доступ к состоянию кластера
2. **Metrics collection** — сбор и анализ метрик производительности
3. **Alerting** — своевременное оповещение о проблемах
4. **Diagnostic tools** — инструменты для анализа и решения проблем

### Best practices:

1. **Configuration management** — версионирование и автоматизация настроек
2. **Security hardening** — защита кластера от угроз
3. **Backup strategies** — надежное резервное копирование
4. **Documentation** — подробная документация процедур и архитектуры

Управление **Elasticsearch** кластером требует глубокого понимания распределенных систем и постоянного внимания к деталям. Правильная архитектура, мониторинг и процедуры обслуживания обеспечивают высокую производительность, надежность и масштабируемость системы.

**Продолжение следует:**
- **elasticsearch-basics.md** (завершен)
- **elasticsearch-indexing.md** (завершен)
- **elasticsearch-queries.md** (завершен)
- **elasticsearch-aggregations.md** (завершен)
- **elasticsearch-clustering.md** (завершен)
- **elasticsearch-performance.md**

Последний файл — **elasticsearch-performance.md**!


