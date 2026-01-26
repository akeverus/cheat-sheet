# Cassandra: Кластеризация - Управление узлами и масштабирование

Комплексное руководство по развертыванию, управлению и масштабированию кластеров Apache Cassandra: топологии, стратегии репликации, балансировка нагрузки и отказоустойчивость.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Cassandra Architecture](https://cassandra.apache.org/doc/latest/cassandra/architecture/index.html)
- [Operating Cassandra](https://cassandra.apache.org/doc/latest/cassandra/operating/index.html)
- [Cassandra Cluster Management](https://docs.datastax.com/en/dse/6.8/dse-admin/datastax_enterprise/operations/opsTOC.html)

### Дизайн и архитектура
- [Cassandra Cluster Planning](https://docs.datastax.com/en/dse/6.8/dse-admin/datastax_enterprise/operations/opsPlanningCluster.html)
- [DataStax Architecture Guide](https://docs.datastax.com/en/dse/6.8/dse-arch/)
- [Cassandra Ring Architecture](https://docs.datastax.com/en/dse/6.8/dse-arch/datastax_enterprise/dbArch/archRing.html)

### Инструменты
- [Cassandra nodetool](https://cassandra.apache.org/doc/latest/tools/nodetool/nodetool.html)
- [DataStax OpsCenter](https://docs.datastax.com/en/opscenter/6.8/opscOnline_help/index.html)
- [Cassandra Reaper](https://cassandra-reaper.io/)

### См. также
- `databases/cassandra-basics.md` - Основы Cassandra
- `databases/cassandra-data-modeling.md` - Моделирование данных
- `databases/cassandra-queries.md` - CQL запросы
- `databases/cassandra-performance.md` - Производительность
- `databases/cassandra-admin.md` - Администрирование

## Содержание

- [Архитектура кластера](#архитектура-кластера)
- [Топологии развертывания](#топологии-развертывания)
- [Управление узлами](#управление-узлами)
- [Стратегии репликации](#стратегии-репликации)
- [Балансировка нагрузки](#балансировка-нагрузки)
- [Отказоустойчивость](#отказоустойчивость)
- [Масштабирование кластера](#масштабирование-кластера)
- [Мониторинг кластера](#мониторинг-кластера)
- [Резервное копирование](#резервное-копирование)
- [Восстановление после сбоев](#восстановление-после-сбоев)
- [Лучшие практики](#лучшие-практики)
- [Заключение](#заключение)

## Архитектура кластера

### Ring Architecture

#### Основные компоненты
```java
public class CassandraCluster {

    // Узлы кластера
    private List<Node> nodes;

    // Токены для распределения данных
    private Map<Node, List<Long>> tokenRanges;

    // Ключевые пространства
    private Map<String, Keyspace> keyspaces;

    public CassandraCluster() {
        this.nodes = new ArrayList<>();
        this.tokenRanges = new HashMap<>();
        this.keyspaces = new HashMap<>();
    }

    // Добавление узла в кластер
    public void addNode(Node node) {
        nodes.add(node);

        // Перераспределение токенов
        redistributeTokens();

        // Обновление метаданных
        updateClusterMetadata();
    }

    // Распределение токенов между узлами
    private void redistributeTokens() {
        int totalTokens = 256; // vnodes per node
        int tokensPerNode = totalTokens / nodes.size();

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            List<Long> tokens = new ArrayList<>();

            for (int j = 0; j < tokensPerNode; j++) {
                long token = (long) i * tokensPerNode + j;
                tokens.add(token);
            }

            tokenRanges.put(node, tokens);
        }
    }

    // Обновление метаданных кластера
    private void updateClusterMetadata() {
        for (Node node : nodes) {
            node.updateGossipState(this);
        }
    }
}

class Node {
    private String address;
    private String dataCenter;
    private String rack;
    private NodeStatus status;
    private Map<String, String> gossipState;

    public void updateGossipState(CassandraCluster cluster) {
        // Обновление состояния через gossip протокол
        gossipState.put("cluster_size", String.valueOf(cluster.getNodes().size()));
        gossipState.put("schema_version", cluster.getCurrentSchemaVersion());
    }
}

enum NodeStatus {
    UP, DOWN, JOINING, LEAVING, MOVING
}
```

#### Gossip Protocol
```java
@Service
public class GossipProtocol {

    private static final int GOSSIP_INTERVAL_MS = 1000; // 1 second

    @Autowired
    private Node localNode;

    @Autowired
    private ClusterMetadata metadata;

    @Scheduled(fixedRate = GOSSIP_INTERVAL_MS)
    public void gossipRound() {
        // Выбор случайного узла для gossip
        Node randomNode = selectRandomNode();

        if (randomNode != null) {
            // Обмен информацией о состоянии
            exchangeGossipState(randomNode);
        }
    }

    private void exchangeGossipState(Node remoteNode) {
        // Отправка локального состояния
        Map<String, GossipDigest> localDigests = createGossipDigests();

        // Получение состояния от удаленного узла
        Map<String, GossipDigest> remoteDigests = remoteNode.getGossipState();

        // Сравнение и обновление
        for (Map.Entry<String, GossipDigest> entry : remoteDigests.entrySet()) {
            String nodeId = entry.getKey();
            GossipDigest remoteDigest = entry.getValue();
            GossipDigest localDigest = localDigests.get(nodeId);

            if (needsUpdate(localDigest, remoteDigest)) {
                // Запрос полного состояния
                requestFullState(nodeId);
            }
        }
    }

    private Map<String, GossipDigest> createGossipDigests() {
        Map<String, GossipDigest> digests = new HashMap<>();

        for (Node node : metadata.getAllNodes()) {
            GossipDigest digest = new GossipDigest(
                node.getId(),
                node.getHeartbeatVersion(),
                node.getApplicationState()
            );
            digests.put(node.getId(), digest);
        }

        return digests;
    }

    private boolean needsUpdate(GossipDigest local, GossipDigest remote) {
        return remote.getVersion() > local.getVersion();
    }
}

class GossipDigest {
    private String nodeId;
    private long version;
    private Map<String, String> applicationState;

    // constructor, getters, setters
}
```

### Seed Nodes

#### Конфигурация seed nodes
```yaml
# cassandra.yaml
seed_provider:
    - class_name: org.apache.cassandra.locator.SimpleSeedProvider
      parameters:
          - seeds: "192.168.1.10,192.168.1.11,192.168.1.12"

# Рекомендации по выбору seed nodes
# - 2-3 seed nodes на датацентр
# - Стабильные узлы с хорошей сетевой связностью
# - Не все узлы должны быть seeds
# - Seeds должны быть первыми узлами при развертывании
```

#### Управление seed nodes
```java
@Service
public class SeedNodeManager {

    @Autowired
    private CassandraConfig config;

    public List<String> getOptimalSeedNodes() {
        List<Node> allNodes = config.getAllNodes();
        List<String> seedNodes = new ArrayList<>();

        // Группировка по датацентрам
        Map<String, List<Node>> nodesByDC = allNodes.stream()
            .collect(Collectors.groupingBy(Node::getDataCenter));

        // Выбор 2-3 seed nodes на датацентр
        for (Map.Entry<String, List<Node>> entry : nodesByDC.entrySet()) {
            List<Node> dcNodes = entry.getValue();

            // Сортировка по стабильности (uptime, network quality)
            dcNodes.sort(this::compareNodeStability);

            // Выбор топ 3 самых стабильных узлов
            int seedCount = Math.min(3, dcNodes.size());
            for (int i = 0; i < seedCount; i++) {
                seedNodes.add(dcNodes.get(i).getAddress());
            }
        }

        return seedNodes;
    }

    private int compareNodeStability(Node n1, Node n2) {
        // Сравнение по uptime, network latency, etc.
        return Long.compare(n2.getUptimeHours(), n1.getUptimeHours());
    }

    public void updateSeedConfiguration(List<String> newSeeds) {
        // Обновление cassandra.yaml на всех узлах
        for (Node node : config.getAllNodes()) {
            node.updateCassandraConfig("seed_provider.parameters.seeds",
                                     String.join(",", newSeeds));
        }
    }
}
```

## Топологии развертывания

### Single Data Center

#### Простая топология
```bash
# Топология: 1 датацентр, 3 узла
# Использование: development, small production

# Конфигурация узлов
# Node 1: seed node
listen_address: 192.168.1.10
rpc_address: 192.168.1.10
seeds: "192.168.1.10"

# Node 2
listen_address: 192.168.1.11
rpc_address: 192.168.1.11
seeds: "192.168.1.10"

# Node 3
listen_address: 192.168.1.12
rpc_address: 192.168.1.12
seeds: "192.168.1.10"
```

#### Настройка keyspace
```cql
-- Keyspace для single DC
CREATE KEYSPACE myapp WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 3
};
```

### Multiple Data Centers

#### Active-Active топология
```bash
# Топология: 2 датацентра, по 3 узла в каждом
# Использование: global applications, disaster recovery

# Data Center 1 (US-West)
# Node 1-1: seed node DC1
dc: DC1
rack: RAC1
listen_address: 10.0.1.10
rpc_address: 10.0.1.10
seeds: "10.0.1.10,10.0.2.10"

# Node 1-2
dc: DC1
rack: RAC1
listen_address: 10.0.1.11
rpc_address: 10.0.1.11
seeds: "10.0.1.10,10.0.2.10"

# Node 1-3
dc: DC1
rack: RAC1
listen_address: 10.0.1.12
rpc_address: 10.0.1.12
seeds: "10.0.1.10,10.0.2.10"

# Data Center 2 (US-East)
# Node 2-1: seed node DC2
dc: DC2
rack: RAC2
listen_address: 10.0.2.10
rpc_address: 10.0.2.10
seeds: "10.0.1.10,10.0.2.10"

# Node 2-2
dc: DC2
rack: RAC2
listen_address: 10.0.2.11
rpc_address: 10.0.2.11
seeds: "10.0.1.10,10.0.2.10"

# Node 2-3
dc: DC2
rack: RAC2
listen_address: 10.0.2.12
rpc_address: 10.0.2.12
seeds: "10.0.1.10,10.0.2.10"
```

#### Настройка keyspace для multi-DC
```cql
-- Keyspace для multiple DC с NetworkTopologyStrategy
CREATE KEYSPACE global_app WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'DC1': 3,  -- 3 реплики в DC1
    'DC2': 3   -- 3 реплики в DC2
};
```

### Hybrid Cloud Topologies

#### On-Premise + Cloud
```yaml
# cassandra.yaml для hybrid deployment
# Data Center 1: On-premise
# Data Center 2: AWS
# Data Center 3: Azure

cluster_name: 'HybridCluster'

# Endpoint snitch для определения DC на основе IP
endpoint_snitch: GossipingPropertyFileSnitch

# PropertyFileSnitch для определения DC/rack
# /etc/cassandra/cassandra-topology.properties
# 192.168.1.10=DC1:RAC1
# 192.168.1.11=DC1:RAC1
# 10.0.1.10=DC2:RAC2     # AWS
# 10.0.1.11=DC2:RAC2     # AWS
# 10.0.2.10=DC3:RAC3     # Azure
# 10.0.2.11=DC3:RAC3     # Azure
```

## Управление узлами

### Добавление узла

#### Процесс добавления
```bash
# 1. Подготовка нового узла
# Установка Cassandra
sudo apt-get install cassandra

# 2. Конфигурация cassandra.yaml
cluster_name: 'MyCluster'
seeds: "192.168.1.10,192.168.1.11"
listen_address: 192.168.1.14  # новый узел
rpc_address: 192.168.1.14

# 3. Запуск узла
sudo systemctl start cassandra

# 4. Проверка статуса
nodetool status

# 5. Ожидание завершения bootstrap
nodetool netstats
```

#### Автоматизированное добавление
```java
@Service
public class NodeProvisioningService {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private AnsibleService ansibleService;

    public void addNodeToCluster(String nodeAddress, String dataCenter, String rack) {
        try {
            // 1. Валидация параметров
            validateNodeParameters(nodeAddress, dataCenter, rack);

            // 2. Проверка доступности узла
            checkNodeAvailability(nodeAddress);

            // 3. Генерация конфигурации
            CassandraConfig config = generateNodeConfig(nodeAddress, dataCenter, rack);

            // 4. Развертывание через Ansible
            ansibleService.deployCassandraNode(nodeAddress, config);

            // 5. Запуск узла
            startCassandraNode(nodeAddress);

            // 6. Ожидание завершения bootstrap
            waitForNodeJoin(nodeAddress);

            // 7. Проверка распределения данных
            verifyDataDistribution();

        } catch (Exception e) {
            // Rollback в случае ошибки
            rollbackNodeAddition(nodeAddress);
            throw new NodeProvisioningException("Failed to add node: " + e.getMessage(), e);
        }
    }

    private void validateNodeParameters(String address, String dc, String rack) {
        if (!isValidIPAddress(address)) {
            throw new IllegalArgumentException("Invalid IP address: " + address);
        }

        if (!clusterManager.isValidDataCenter(dc)) {
            throw new IllegalArgumentException("Invalid data center: " + dc);
        }

        if (!clusterManager.isValidRack(dc, rack)) {
            throw new IllegalArgumentException("Invalid rack for DC: " + rack);
        }
    }

    private void waitForNodeJoin(String nodeAddress) {
        long timeoutMs = 30 * 60 * 1000; // 30 minutes
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            NodeStatus status = clusterManager.getNodeStatus(nodeAddress);

            if (status == NodeStatus.UP) {
                // Узел успешно присоединился
                return;
            } else if (status == NodeStatus.DOWN) {
                throw new NodeProvisioningException("Node failed to join cluster");
            }

            // Ожидание перед следующей проверкой
            try {
                Thread.sleep(10000); // 10 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new NodeProvisioningException("Interrupted while waiting for node join");
            }
        }

        throw new NodeProvisioningException("Timeout waiting for node to join cluster");
    }
}
```

### Удаление узла

#### Graceful decommission
```bash
# 1. Проверка возможности удаления
nodetool status

# 2. Запуск decommission
nodetool decommission

# 3. Мониторинг процесса
nodetool netstats
nodetool compactionstats

# 4. Ожидание завершения
# Узел будет автоматически остановлен после завершения

# 5. Удаление из конфигурации (опционально)
# Убрать из seeds если был seed node
```

#### Force remove (для недоступных узлов)
```bash
# Только для недоступных узлов!
# 1. Проверка что узел действительно недоступен
nodetool status

# 2. Удаление узла принудительно
nodetool removenode <node_id>

# 3. Очистка данных (если необходимо)
nodetool cleanup

# 4. Проверка распределения данных
nodetool status
```

#### Автоматизированное удаление
```java
@Service
public class NodeRemovalService {

    @Autowired
    private ClusterManager clusterManager;

    public void removeNode(String nodeAddress, boolean force) {
        try {
            // 1. Валидация
            validateNodeRemoval(nodeAddress);

            // 2. Проверка возможности удаления
            checkRemovalFeasibility(nodeAddress);

            if (!force) {
                // Graceful removal
                performGracefulRemoval(nodeAddress);
            } else {
                // Force removal
                performForceRemoval(nodeAddress);
            }

            // 3. Очистка после удаления
            cleanupAfterRemoval(nodeAddress);

            // 4. Обновление конфигурации
            updateClusterConfiguration();

        } catch (Exception e) {
            handleRemovalFailure(nodeAddress, e);
        }
    }

    private void performGracefulRemoval(String nodeAddress) {
        // Запуск decommission через nodetool
        executeNodetoolCommand(nodeAddress, "decommission");

        // Ожидание завершения
        waitForDecommissionComplete(nodeAddress);

        // Остановка узла
        stopCassandraNode(nodeAddress);
    }

    private void performForceRemoval(String nodeAddress) {
        // Получение node ID
        String nodeId = clusterManager.getNodeId(nodeAddress);

        // Принудительное удаление
        executeNodetoolCommand("removenode " + nodeId);

        // Ожидание завершения
        waitForRemovalComplete(nodeId);
    }

    private void waitForDecommissionComplete(String nodeAddress) {
        long timeoutMs = 2 * 60 * 60 * 1000; // 2 hours
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            String status = getNodeDecommissionStatus(nodeAddress);

            if ("COMPLETED".equals(status)) {
                return;
            } else if ("FAILED".equals(status)) {
                throw new NodeRemovalException("Decommission failed for node: " + nodeAddress);
            }

            try {
                Thread.sleep(30000); // 30 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new NodeRemovalException("Timeout waiting for decommission completion");
    }
}
```

### Замена узла

#### Процесс замены
```bash
# 1. Подготовка нового узла
# Конфигурация как у заменяемого узла
# Но с новым IP адресом

# 2. Запуск нового узла
sudo systemctl start cassandra

# 3. Ожидание присоединения
nodetool status

# 4. Удаление старого узла
nodetool removenode <old_node_id>

# 5. Очистка данных
nodetool cleanup
```

## Стратегии репликации

### NetworkTopologyStrategy

#### Конфигурация для разных топологий
```cql
-- Simple topology (single DC)
CREATE KEYSPACE simple_ks WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 3
};

-- Network topology (multi-DC)
CREATE KEYSPACE network_ks WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'DC1': 3,
    'DC2': 3,
    'DC3': 2
};

-- Hybrid cloud
CREATE KEYSPACE hybrid_ks WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'on_premise': 3,
    'aws': 3,
    'azure': 2
};
```

#### Расчет оптимального replication factor
```java
@Service
public class ReplicationStrategyOptimizer {

    @Autowired
    private ClusterTopology topology;

    public Map<String, Integer> calculateOptimalReplication() {
        Map<String, List<Node>> nodesByDC = topology.getNodesByDataCenter();
        Map<String, Integer> replicationFactors = new HashMap<>();

        for (Map.Entry<String, List<Node>> entry : nodesByDC.entrySet()) {
            String dcName = entry.getKey();
            List<Node> dcNodes = entry.getValue();

            // Оптимальный RF = min(N/2 + 1, node_count)
            // Где N = количество узлов в DC
            int nodeCount = dcNodes.size();
            int optimalRF = Math.min(nodeCount / 2 + 1, nodeCount);

            // Минимум 3 для production
            optimalRF = Math.max(optimalRF, 3);

            replicationFactors.put(dcName, optimalRF);
        }

        return replicationFactors;
    }

    public void applyOptimalReplication(String keyspaceName) {
        Map<String, Integer> optimalRF = calculateOptimalReplication();

        // Создание команды ALTER KEYSPACE
        StringBuilder replicationConfig = new StringBuilder();
        replicationConfig.append("{'class': 'NetworkTopologyStrategy'");

        for (Map.Entry<String, Integer> entry : optimalRF.entrySet()) {
            replicationConfig.append(", '")
                            .append(entry.getKey())
                            .append("': ")
                            .append(entry.getValue());
        }

        replicationConfig.append("}");

        // Применение конфигурации
        String alterCommand = String.format(
            "ALTER KEYSPACE %s WITH replication = %s",
            keyspaceName, replicationConfig.toString()
        );

        executeCqlCommand(alterCommand);

        // Запуск repair для применения изменений
        runRepair(keyspaceName);
    }

    public void runRepair(String keyspaceName) {
        // Запуск repair для распространения изменений
        for (Node node : topology.getAllNodes()) {
            executeNodetoolCommand(node.getAddress(), "repair " + keyspaceName);
        }
    }
}
```

### Consistency Levels

#### Настройка консистентности
```cql
-- Уровни консистентности для чтения
SELECT * FROM users WHERE id = ?;

-- Установка уровня консистентности
CONSISTENCY QUORUM;
CONSISTENCY LOCAL_QUORUM;
CONSISTENCY ONE;

-- Для конкретного запроса
SELECT * FROM users WHERE id = ?
USING CONSISTENCY LOCAL_QUORUM;
```

#### Программная настройка консистентности
```java
@Configuration
public class CassandraConsistencyConfig {

    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("localhost", 9042))
            .withKeyspace("myapp")
            .withLocalDatacenter("DC1")
            .build();
    }

    @Bean
    public DriverConfigLoader driverConfig() {
        return DriverConfigLoader.programmaticBuilder()
            .withString(DefaultDriverOption.REQUEST_CONSISTENCY, "LOCAL_QUORUM")
            .withString(DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, "LOCAL_SERIAL")
            .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
            .build();
    }
}

@Service
public class ConsistencyManager {

    @Autowired
    private CqlSession session;

    public <T> T executeWithConsistency(Supplier<T> operation,
                                      DefaultConsistencyLevel consistency) {
        // Сохранение текущих настроек
        Statement<?> originalStatement = createBaseStatement();

        // Создание statement с нужной консистентностью
        Statement<?> statement = originalStatement.setConsistencyLevel(consistency);

        // Выполнение операции
        return operation.get();
    }

    public void executeBatchWithConsistency(BatchStatement batch,
                                          DefaultConsistencyLevel consistency) {
        BatchStatement configuredBatch = batch.setConsistencyLevel(consistency);
        session.execute(configuredBatch);
    }

    // Адаптивная консистентность
    public DefaultConsistencyLevel determineOptimalConsistency(String keyspace,
                                                             String table,
                                                             OperationType operationType) {
        ClusterTopology topology = getClusterTopology(keyspace);

        switch (operationType) {
            case READ:
                return determineReadConsistency(topology);
            case WRITE:
                return determineWriteConsistency(topology);
            case DELETE:
                return determineDeleteConsistency(topology);
            default:
                return DefaultConsistencyLevel.QUORUM;
        }
    }

    private DefaultConsistencyLevel determineReadConsistency(ClusterTopology topology) {
        if (topology.isMultiDC()) {
            return DefaultConsistencyLevel.LOCAL_QUORUM;
        } else if (topology.getNodeCount() >= 5) {
            return DefaultConsistencyLevel.QUORUM;
        } else {
            return DefaultConsistencyLevel.ONE;
        }
    }

    private DefaultConsistencyLevel determineWriteConsistency(ClusterTopology topology) {
        if (topology.isMultiDC()) {
            return DefaultConsistencyLevel.EACH_QUORUM;
        } else {
            return DefaultConsistencyLevel.QUORUM;
        }
    }
}
```

## Балансировка нагрузки

### Token Distribution

#### Vnodes vs Single Token
```java
public class TokenDistributionManager {

    // Vnodes (рекомендуется)
    private static final int VNODES_PER_NODE = 256;

    public Map<Node, List<Long>> assignVnodes(List<Node> nodes) {
        Map<Node, List<Long>> assignment = new HashMap<>();
        int totalVnodes = nodes.size() * VNODES_PER_NODE;

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            List<Long> vnodes = new ArrayList<>();

            // Равномерное распределение vnodes
            for (int j = 0; j < VNODES_PER_NODE; j++) {
                long vnode = ((long) i * VNODES_PER_NODE + j) % totalVnodes;
                vnodes.add(vnode);
            }

            assignment.put(node, vnodes);
        }

        return assignment;
    }

    // Single token (legacy, не рекомендуется)
    public Map<Node, Long> assignSingleTokens(List<Node> nodes) {
        Map<Node, Long> assignment = new HashMap<>();
        long totalRange = Long.MAX_VALUE - Long.MIN_VALUE + 1;
        long rangePerNode = totalRange / nodes.size();

        for (int i = 0; i < nodes.size(); i++) {
            long token = Long.MIN_VALUE + (i * rangePerNode);
            assignment.put(nodes.get(i), token);
        }

        return assignment;
    }

    public boolean isBalanced(Map<Node, List<Long>> vnodeAssignment) {
        int totalVnodes = vnodeAssignment.values().stream()
            .mapToInt(List::size)
            .sum();

        int averageVnodes = totalVnodes / vnodeAssignment.size();
        int tolerance = averageVnodes / 10; // 10% tolerance

        for (List<Long> vnodes : vnodeAssignment.values()) {
            if (Math.abs(vnodes.size() - averageVnodes) > tolerance) {
                return false;
            }
        }

        return true;
    }
}
```

### Rebalancing

#### Ручное перебалансирование
```bash
# 1. Проверка текущего распределения
nodetool status

# 2. Проверка распределения токенов
nodetool ring

# 3. Перемещение токенов (если необходимо)
nodetool move <new_token>

# 4. Ожидание завершения
nodetool netstats

# 5. Запуск cleanup на всех узлах
nodetool cleanup
```

#### Автоматизированное перебалансирование
```java
@Service
public class ClusterRebalancingService {

    @Autowired
    private ClusterTopology topology;

    @Autowired
    private TokenDistributionManager tokenManager;

    public void rebalanceCluster() {
        try {
            // 1. Анализ текущего распределения
            RebalanceAnalysis analysis = analyzeCurrentDistribution();

            if (!analysis.isBalanced()) {
                // 2. Расчет оптимального распределения
                Map<Node, List<Long>> optimalDistribution =
                    tokenManager.assignVnodes(topology.getAllNodes());

                // 3. Планирование перемещений
                List<TokenMove> moves = planTokenMoves(analysis.getCurrentDistribution(),
                                                     optimalDistribution);

                // 4. Выполнение перемещений
                executeTokenMoves(moves);

                // 5. Ожидание завершения
                waitForRebalancingComplete();

                // 6. Cleanup
                runCleanupOnAllNodes();
            }

        } catch (Exception e) {
            handleRebalancingFailure(e);
        }
    }

    private RebalanceAnalysis analyzeCurrentDistribution() {
        Map<Node, List<Long>> currentDistribution = new HashMap<>();

        for (Node node : topology.getAllNodes()) {
            List<Long> tokens = getNodeTokens(node);
            currentDistribution.put(node, tokens);
        }

        boolean isBalanced = tokenManager.isBalanced(currentDistribution);

        return new RebalanceAnalysis(currentDistribution, isBalanced);
    }

    private List<TokenMove> planTokenMoves(Map<Node, List<Long>> current,
                                         Map<Node, List<Long>> target) {
        List<TokenMove> moves = new ArrayList<>();

        // Сравнение распределений и планирование перемещений
        for (Map.Entry<Node, List<Long>> entry : target.entrySet()) {
            Node node = entry.getKey();
            List<Long> targetTokens = entry.getValue();
            List<Long> currentTokens = current.get(node);

            // Поиск токенов для перемещения
            for (Long token : targetTokens) {
                if (!currentTokens.contains(token)) {
                    // Найти узел, который имеет этот токен
                    Node sourceNode = findNodeWithToken(current, token);
                    if (sourceNode != null) {
                        moves.add(new TokenMove(token, sourceNode, node));
                    }
                }
            }
        }

        return moves;
    }

    private void executeTokenMoves(List<TokenMove> moves) {
        for (TokenMove move : moves) {
            try {
                // nodetool move <token>
                executeNodetoolCommand(move.getTargetNode().getAddress(),
                                     "move " + move.getToken());

                // Ожидание завершения перемещения
                waitForTokenMoveComplete(move);

            } catch (Exception e) {
                log.error("Failed to move token {} to node {}",
                         move.getToken(), move.getTargetNode().getAddress(), e);
            }
        }
    }
}

class TokenMove {
    private Long token;
    private Node sourceNode;
    private Node targetNode;

    // constructor, getters, setters
}
```

## Отказоустойчивость

### Failure Detection

#### Phi Accrual Failure Detector
```java
@Service
public class FailureDetector {

    private static final double PHI_THRESHOLD = 8.0; // Порог для определения отказа
    private static final long SAMPLE_INTERVAL_MS = 1000; // Интервал сэмплирования

    private Map<Node, List<Long>> arrivalTimes;

    @Scheduled(fixedRate = SAMPLE_INTERVAL_MS)
    public void sampleHeartbeat() {
        for (Node node : clusterManager.getAllNodes()) {
            if (node.isReachable()) {
                recordArrivalTime(node, System.currentTimeMillis());
            }
        }
    }

    private void recordArrivalTime(Node node, long arrivalTime) {
        arrivalTimes.computeIfAbsent(node, k -> new ArrayList<>())
                   .add(arrivalTime);

        // Ограничение размера истории
        List<Long> times = arrivalTimes.get(node);
        if (times.size() > 1000) {
            times.remove(0);
        }
    }

    public boolean isNodeSuspected(Node node) {
        List<Long> times = arrivalTimes.get(node);
        if (times == null || times.size() < 2) {
            return false; // Недостаточно данных
        }

        double phi = calculatePhi(times);
        return phi > PHI_THRESHOLD;
    }

    private double calculatePhi(List<Long> arrivalTimes) {
        if (arrivalTimes.size() < 2) return 0.0;

        // Расчет среднего интервала
        long now = System.currentTimeMillis();
        long lastArrival = arrivalTimes.get(arrivalTimes.size() - 1);
        double mean = calculateMeanInterval(arrivalTimes);
        double variance = calculateVariance(arrivalTimes, mean);

        // Расчет вероятности
        double t = now - lastArrival;
        double probability = 1 - gaussianCdf(t, mean, Math.sqrt(variance));

        // Преобразование в phi
        return -Math.log10(probability);
    }

    private double calculateMeanInterval(List<Long> times) {
        long totalInterval = 0;
        for (int i = 1; i < times.size(); i++) {
            totalInterval += times.get(i) - times.get(i - 1);
        }
        return (double) totalInterval / (times.size() - 1);
    }

    private double calculateVariance(List<Long> times, double mean) {
        double sumSquares = 0;
        for (int i = 1; i < times.size(); i++) {
            double diff = (times.get(i) - times.get(i - 1)) - mean;
            sumSquares += diff * diff;
        }
        return sumSquares / (times.size() - 1);
    }

    private double gaussianCdf(double x, double mean, double stdDev) {
        // Приближенная функция распределения
        return 0.5 * (1 + erf((x - mean) / (stdDev * Math.sqrt(2))));
    }

    private double erf(double x) {
        // Приближение функции ошибки
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;

        double sign = (x < 0) ? -1 : 1;
        x = Math.abs(x);

        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

        return sign * y;
    }
}
```

### Hinted Handoff

#### Механизм hinted handoff
```java
@Service
public class HintedHandoffManager {

    private static final long HINT_TTL_HOURS = 3; // Время жизни hint

    @Autowired
    private ClusterManager clusterManager;

    // Хранение hints для недоступных узлов
    private Map<Node, List<Hint>> hintsByNode;

    public HintedHandoffManager() {
        this.hintsByNode = new ConcurrentHashMap<>();
    }

    // Запись hint при недоступности узла
    public void writeHint(Mutation mutation, Node targetNode) {
        Hint hint = new Hint(
            mutation,
            targetNode,
            System.currentTimeMillis() + (HINT_TTL_HOURS * 60 * 60 * 1000)
        );

        hintsByNode.computeIfAbsent(targetNode, k -> new ArrayList<>())
                  .add(hint);

        // Ограничение количества hints
        limitHintsForNode(targetNode);
    }

    // Воспроизведение hints при восстановлении узла
    public void replayHints(Node recoveredNode) {
        List<Hint> hints = hintsByNode.get(recoveredNode);

        if (hints != null && !hints.isEmpty()) {
            List<Hint> validHints = hints.stream()
                .filter(hint -> !hint.isExpired())
                .collect(Collectors.toList());

            // Применение hints
            for (Hint hint : validHints) {
                try {
                    clusterManager.applyMutation(hint.getMutation(), recoveredNode);
                } catch (Exception e) {
                    log.error("Failed to replay hint for node {}", recoveredNode.getAddress(), e);
                }
            }

            // Очистка hints
            hintsByNode.remove(recoveredNode);
        }
    }

    // Очистка просроченных hints
    @Scheduled(fixedRate = 600000) // Каждые 10 минут
    public void cleanupExpiredHints() {
        long now = System.currentTimeMillis();

        for (Map.Entry<Node, List<Hint>> entry : hintsByNode.entrySet()) {
            List<Hint> hints = entry.getValue();
            hints.removeIf(hint -> hint.isExpired(now));

            if (hints.isEmpty()) {
                hintsByNode.remove(entry.getKey());
            }
        }
    }

    private void limitHintsForNode(Node node) {
        List<Hint> hints = hintsByNode.get(node);
        if (hints != null && hints.size() > 10000) { // Максимум 10k hints на узел
            // Удаление самых старых hints
            hints.sort(Comparator.comparing(Hint::getCreatedTime));
            hints.subList(0, hints.size() - 10000).clear();
        }
    }
}

class Hint {
    private Mutation mutation;
    private Node targetNode;
    private long expiryTime;
    private long createdTime;

    public Hint(Mutation mutation, Node targetNode, long expiryTime) {
        this.mutation = mutation;
        this.targetNode = targetNode;
        this.expiryTime = expiryTime;
        this.createdTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return isExpired(System.currentTimeMillis());
    }

    public boolean isExpired(long now) {
        return now > expiryTime;
    }

    // getters
}
```

### Read Repair

#### Механизм read repair
```java
@Service
public class ReadRepairService {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private ConsistencyManager consistencyManager;

    public ReadResult performReadWithRepair(String keyspace, String table,
                                          Object partitionKey,
                                          DefaultConsistencyLevel consistency) {
        // 1. Чтение с требуемым уровнем консистентности
        ReadResult initialResult = readFromReplicas(keyspace, table, partitionKey, consistency);

        // 2. Проверка необходимости repair
        if (needsRepair(initialResult)) {
            // 3. Выполнение background repair
            performBackgroundRepair(keyspace, table, partitionKey, initialResult);
        }

        return initialResult;
    }

    private ReadResult readFromReplicas(String keyspace, String table,
                                      Object partitionKey,
                                      DefaultConsistencyLevel consistency) {
        List<Node> replicas = clusterManager.getReplicasForKey(keyspace, partitionKey);
        ReadResult result = new ReadResult();

        // Чтение с блокирующего кворума
        int requiredResponses = consistencyManager.getRequiredResponses(consistency, replicas.size());

        for (Node replica : replicas) {
            try {
                RowData data = replica.read(keyspace, table, partitionKey);
                result.addResponse(data);

                if (result.getResponseCount() >= requiredResponses) {
                    break; // Достаточно ответов
                }
            } catch (Exception e) {
                result.addFailure(replica, e);
            }
        }

        return result;
    }

    private boolean needsRepair(ReadResult result) {
        if (result.getResponseCount() < 2) {
            return false; // Недостаточно данных для сравнения
        }

        // Сравнение значений из разных реплик
        List<RowData> responses = result.getResponses();
        RowData first = responses.get(0);

        for (int i = 1; i < responses.size(); i++) {
            if (!first.equals(responses.get(i))) {
                return true; // Найдены расхождения
            }
        }

        return false;
    }

    private void performBackgroundRepair(String keyspace, String table,
                                       Object partitionKey, ReadResult result) {
        // Выбор самой свежей версии данных
        RowData mostRecentData = selectMostRecentData(result.getResponses());

        // Запись исправленных данных на все реплики
        List<Node> replicas = clusterManager.getReplicasForKey(keyspace, partitionKey);

        for (Node replica : replicas) {
            if (!result.hasResponseFrom(replica)) {
                // Background write
                executorService.submit(() -> {
                    try {
                        replica.write(keyspace, table, partitionKey, mostRecentData);
                    } catch (Exception e) {
                        log.warn("Failed to repair replica {}", replica.getAddress(), e);
                    }
                });
            }
        }
    }

    private RowData selectMostRecentData(List<RowData> responses) {
        return responses.stream()
            .max(Comparator.comparing(RowData::getTimestamp))
            .orElse(responses.get(0));
    }
}

class ReadResult {
    private List<RowData> responses;
    private Map<Node, Exception> failures;

    public ReadResult() {
        this.responses = new ArrayList<>();
        this.failures = new HashMap<>();
    }

    public void addResponse(RowData data) {
        responses.add(data);
    }

    public void addFailure(Node node, Exception e) {
        failures.put(node, e);
    }

    public int getResponseCount() {
        return responses.size();
    }

    public List<RowData> getResponses() {
        return responses;
    }

    public boolean hasResponseFrom(Node node) {
        // Проверка наличия ответа от конкретного узла
        return responses.stream().anyMatch(data -> data.getSourceNode().equals(node));
    }
}
```

## Масштабирование кластера

### Horizontal Scaling

#### Добавление датацентра
```bash
# 1. Подготовка новых узлов
# Конфигурация для нового DC

# 2. Запуск узлов нового DC
# С правильной конфигурацией DC/rack

# 3. Ожидание присоединения
nodetool status

# 4. Обновление keyspaces для нового DC
ALTER KEYSPACE myapp WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'DC1': 3,
    'DC2': 3
};

# 5. Запуск repair для синхронизации данных
nodetool repair --dc DC2
```

#### Автоматизированное масштабирование
```java
@Service
public class AutoScalingService {

    @Autowired
    private ClusterMetricsCollector metrics;

    @Autowired
    private NodeProvisioningService provisioning;

    @Autowired
    private ClusterTopology topology;

    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void checkScalingNeeds() {
        ScalingDecision decision = analyzeScalingRequirements();

        switch (decision.getAction()) {
            case ADD_NODE:
                addNode(decision.getDataCenter());
                break;
            case REMOVE_NODE:
                removeNode(decision.getNodeAddress());
                break;
            case ADD_DATACENTER:
                addDataCenter(decision.getDataCenterConfig());
                break;
            case NO_ACTION:
                // Ничего не делать
                break;
        }
    }

    private ScalingDecision analyzeScalingRequirements() {
        ClusterMetrics currentMetrics = metrics.collect();

        // Анализ CPU utilization
        if (currentMetrics.getAverageCpuUtilization() > 80.0) {
            return new ScalingDecision(ScalingAction.ADD_NODE,
                                     selectOptimalDataCenter());
        }

        // Анализ disk space
        if (currentMetrics.getAverageDiskUtilization() > 85.0) {
            return new ScalingDecision(ScalingAction.ADD_NODE,
                                     selectOptimalDataCenter());
        }

        // Анализ latency
        if (currentMetrics.getAverageReadLatency() > 100.0) { // ms
            return new ScalingDecision(ScalingAction.ADD_NODE,
                                     selectOptimalDataCenter());
        }

        // Анализ неиспользуемых ресурсов
        if (currentMetrics.getAverageCpuUtilization() < 30.0 &&
            currentMetrics.getNodeCount() > 6) {
            return new ScalingDecision(ScalingAction.REMOVE_NODE,
                                     selectNodeForRemoval());
        }

        return new ScalingDecision(ScalingAction.NO_ACTION);
    }

    private String selectOptimalDataCenter() {
        // Выбор DC с наибольшей нагрузкой
        return topology.getDataCenters().stream()
            .max(Comparator.comparing(dc -> dc.getAverageLoad()))
            .map(DataCenter::getName)
            .orElse("DC1");
    }

    private String selectNodeForRemoval() {
        // Выбор наименее нагруженного узла
        return topology.getAllNodes().stream()
            .min(Comparator.comparing(Node::getCurrentLoad))
            .map(Node::getAddress)
            .orElse(null);
    }

    private void addNode(String dataCenter) {
        try {
            // Генерация адреса для нового узла
            String newNodeAddress = generateNewNodeAddress(dataCenter);

            // Развертывание узла
            provisioning.addNodeToCluster(newNodeAddress, dataCenter,
                                        getRackForDataCenter(dataCenter));

            log.info("Successfully added node {} to datacenter {}", newNodeAddress, dataCenter);

        } catch (Exception e) {
            log.error("Failed to add node to datacenter {}", dataCenter, e);
        }
    }

    private void addDataCenter(DataCenterConfig config) {
        try {
            // Создание нового датацентра
            topology.addDataCenter(config.getName(), config.getInitialNodes());

            // Развертывание начальных узлов
            for (int i = 0; i < config.getInitialNodes(); i++) {
                addNode(config.getName());
            }

            // Обновление keyspace replication
            updateReplicationForNewDC(config.getName());

            log.info("Successfully added datacenter {}", config.getName());

        } catch (Exception e) {
            log.error("Failed to add datacenter {}", config.getName(), e);
        }
    }
}

enum ScalingAction {
    ADD_NODE, REMOVE_NODE, ADD_DATACENTER, NO_ACTION
}

class ScalingDecision {
    private ScalingAction action;
    private String dataCenter;
    private String nodeAddress;
    private DataCenterConfig dataCenterConfig;

    // constructors, getters, setters
}
```

### Vertical Scaling

#### Увеличение ресурсов узла
```bash
# 1. Остановка Cassandra
sudo systemctl stop cassandra

# 2. Увеличение ресурсов (CPU, RAM, Disk)
# На уровне виртуальной машины или физического сервера

# 3. Запуск Cassandra
sudo systemctl start cassandra

# 4. Проверка статуса
nodetool status

# 5. Rebalancing если необходимо
nodetool cleanup
nodetool repair
```

## Мониторинг кластера

### Nodetool Commands

#### Основные команды мониторинга
```bash
# Статус кластера
nodetool status

# Информация об узле
nodetool info

# Статистика ring
nodetool ring

# Статистика таблиц
nodetool tablestats

# Статистика compaction
nodetool compactionstats

# Статистика garbage collection
nodetool gcstats

# Сетевая статистика
nodetool netstats

# Статистика потоков
nodetool tpstats
```

#### Продвинутые команды
```bash
# Детальная информация о таблице
nodetool cfstats keyspace.table

# Статистика кэша
nodetool cachestats

# Статистика proxy
nodetool proxyhistograms

# Снимок heap
nodetool captureheap

# Проверка консистентности
nodetool verify
```

### JMX Monitoring

#### Java Management Extensions
```java
@Service
public class JmxMonitoringService {

    private MBeanServerConnection jmxConnection;

    public void connectToNode(String nodeAddress) throws Exception {
        JMXServiceURL url = new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://" + nodeAddress + ":7199/jmxrmi"
        );

        JMXConnector connector = JMXConnectorFactory.connect(url);
        jmxConnection = connector.getMBeanServerConnection();
    }

    public Map<String, Object> getNodeMetrics() throws Exception {
        Map<String, Object> metrics = new HashMap<>();

        // JVM метрики
        metrics.put("heapUsage", getHeapUsage());
        metrics.put("gcStats", getGCStats());
        metrics.put("threadCount", getThreadCount());

        // Cassandra метрики
        metrics.put("readLatency", getReadLatency());
        metrics.put("writeLatency", getWriteLatency());
        metrics.put("pendingCompactions", getPendingCompactions());

        // Сетевая статистика
        metrics.put("connections", getActiveConnections());

        return metrics;
    }

    private double getHeapUsage() throws Exception {
        ObjectName heapName = new ObjectName("java.lang:type=Memory");
        MemoryUsage heap = (MemoryUsage) jmxConnection.getAttribute(heapName, "HeapMemoryUsage");
        return (double) heap.getUsed() / heap.getMax() * 100;
    }

    private Map<String, Object> getGCStats() throws Exception {
        Map<String, Object> gcStats = new HashMap<>();

        // Young GC
        ObjectName youngGC = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
        gcStats.put("youngGCCount", jmxConnection.getAttribute(youngGC, "CollectionCount"));
        gcStats.put("youngGCTime", jmxConnection.getAttribute(youngGC, "CollectionTime"));

        // Old GC
        ObjectName oldGC = new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep");
        gcStats.put("oldGCCount", jmxConnection.getAttribute(oldGC, "CollectionCount"));
        gcStats.put("oldGCTime", jmxConnection.getAttribute(oldGC, "CollectionTime"));

        return gcStats;
    }

    private long getReadLatency() throws Exception {
        ObjectName latencyName = new ObjectName("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency");
        return (Long) jmxConnection.getAttribute(latencyName, "Mean");
    }

    private long getWriteLatency() throws Exception {
        ObjectName latencyName = new ObjectName("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency");
        return (Long) jmxConnection.getAttribute(latencyName, "Mean");
    }

    private int getPendingCompactions() throws Exception {
        ObjectName compactionName = new ObjectName("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks");
        return (Integer) jmxConnection.getAttribute(compactionName, "Value");
    }

    private int getActiveConnections() throws Exception {
        ObjectName connectionName = new ObjectName("org.apache.cassandra.metrics:type=Client,name=connectedNativeClients");
        return (Integer) jmxConnection.getAttribute(connectionName, "Count");
    }
}
```

### Prometheus Integration

#### Метрики для Prometheus
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'cassandra'
    static_configs:
      - targets: ['cassandra-node1:7070', 'cassandra-node2:7070']
    scrape_interval: 15s

# Cassandra JMX Exporter
# docker run -d \
#   --name cassandra-jmx \
#   -p 7070:7070 \
#   -e JVM_OPTS="-javaagent:/opt/jmx_prometheus_javaagent.jar=7070:/etc/jmx-config.yml" \
#   cassandra:latest
```

#### Конфигурация JMX Exporter
```yaml
# jmx-config.yml
startDelaySeconds: 0
ssl: false
lowercaseOutputName: true
lowercaseOutputLabelNames: true

rules:
  - pattern: "org.apache.cassandra.metrics<type=(.+), name=(.+), scope=(.+)>"
    name: "cassandra_$1_$3_$2"
    labels:
      table: "$3"

  - pattern: "org.apache.cassandra.metrics<type=(.+), name=(.+)>"
    name: "cassandra_$1_$2"

  - pattern: "java.lang<type=(.+), name=(.+)>"
    name: "jvm_$1_$2"

  - pattern: "java.lang<type=(.+)>"
    name: "jvm_$1"
```

## Резервное копирование

### Snapshot-based Backup

#### Создание snapshot
```bash
# 1. Создание snapshot для всех keyspaces
nodetool snapshot

# 2. Создание snapshot для конкретного keyspace
nodetool snapshot myapp

# 3. Создание именованного snapshot
nodetool snapshot -t backup_20231201 myapp

# 4. Проверка snapshots
nodetool listsnapshots

# 5. Копирование файлов snapshot
# Из /var/lib/cassandra/data/ копировать .db файлы
```

#### Восстановление из snapshot
```bash
# 1. Остановка Cassandra
sudo systemctl stop cassandra

# 2. Очистка данных (если необходимо)
sudo rm -rf /var/lib/cassandra/data/*

# 3. Копирование файлов snapshot
# В соответствующие директории таблиц

# 4. Запуск Cassandra
sudo systemctl start cassandra

# 5. Repair для восстановления консистентности
nodetool repair
```

### Incremental Backup

#### Настройка incremental backup
```yaml
# cassandra.yaml
incremental_backups: true

# Автоматическое создание incremental backups
# Каждый compaction создает hard link на SSTable файлы
```

#### Использование incremental backup
```bash
# 1. Поиск incremental backup файлов
find /var/lib/cassandra/data/ -name "backups" -type d

# 2. Копирование backup файлов
# Сохранять вместе со snapshot

# 3. Восстановление
# Сначала восстановить snapshot, затем скопировать incremental файлы
```

### Medusa Backup Tool

#### Настройка Medusa
```yaml
# /etc/medusa/medusa.ini
[cassandra]
seed = 192.168.1.10
use_sudo = true

[storage]
storage_provider = s3
bucket_name = my-cassandra-backups
key_file = /etc/medusa/key.pem
prefix = backups

[monitoring]
monitoring_provider = local

[grpc]
grpc_enabled = true
```

#### Использование Medusa
```bash
# Создание backup
medusa backup --name daily_backup

# Список backups
medusa list-backups

# Восстановление
medusa restore --backup-name daily_backup

# Проверка статуса
medusa status
```

## Восстановление после сбоев

### Node Recovery

#### Восстановление упавшего узла
```bash
# 1. Проверка что узел недоступен
nodetool status

# 2. Запуск узла
sudo systemctl start cassandra

# 3. Ожидание присоединения
nodetool status

# 4. Проверка данных
nodetool verify

# 5. Repair для синхронизации
nodetool repair
```

#### Полное восстановление узла
```bash
# 1. Остановка Cassandra
sudo systemctl stop cassandra

# 2. Очистка данных
sudo rm -rf /var/lib/cassandra/data/*
sudo rm -rf /var/lib/cassandra/commitlog/*
sudo rm -rf /var/lib/cassandra/saved_caches/*

# 3. Восстановление из backup
# Копирование snapshot и incremental файлов

# 4. Запуск Cassandra
sudo systemctl start cassandra

# 5. Ожидание загрузки данных
nodetool netstats

# 6. Repair
nodetool repair
```

### Data Center Recovery

#### Восстановление датацентра
```bash
# 1. Проверка доступности DC
nodetool status

# 2. Восстановление seed nodes
# Запуск seed nodes в пострадавшем DC

# 3. Восстановление остальных nodes
# По одному для предотвращения перегрузки

# 4. Обновление replication factors если необходимо
ALTER KEYSPACE myapp WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'DC1': 3,
    'DC2': 2  -- Временно уменьшенный RF
};

# 5. Синхронизация данных
nodetool rebuild -- datacenter_name

# 6. Восстановление полного RF
ALTER KEYSPACE myapp WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'DC1': 3,
    'DC2': 3
};
```

### Disaster Recovery

#### План disaster recovery
```java
@Service
public class DisasterRecoveryService {

    @Autowired
    private BackupService backupService;

    @Autowired
    private ClusterProvisioningService provisioning;

    public void executeDisasterRecovery(DisasterScenario scenario) {
        try {
            // 1. Оценка ситуации
            DisasterAssessment assessment = assessDisaster(scenario);

            // 2. Активация плана восстановления
            RecoveryPlan plan = createRecoveryPlan(assessment);

            // 3. Восстановление инфраструктуры
            provisionInfrastructure(plan);

            // 4. Восстановление данных
            restoreData(plan);

            // 5. Восстановление приложений
            restoreApplications(plan);

            // 6. Валидация восстановления
            validateRecovery(plan);

        } catch (Exception e) {
            handleRecoveryFailure(scenario, e);
        }
    }

    private DisasterAssessment assessDisaster(DisasterScenario scenario) {
        DisasterAssessment assessment = new DisasterAssessment();

        // Оценка масштаба
        assessment.setAffectedNodes(scenario.getAffectedNodes());
        assessment.setAffectedDataCenters(scenario.getAffectedDataCenters());
        assessment.setDataLoss(scenario.getEstimatedDataLoss());

        // Оценка критичности
        assessment.setBusinessImpact(calculateBusinessImpact(scenario));
        assessment.setRecoveryTime(estimateRecoveryTime(scenario));

        return assessment;
    }

    private RecoveryPlan createRecoveryPlan(DisasterAssessment assessment) {
        RecoveryPlan plan = new RecoveryPlan();

        // Определение стратегии восстановления
        if (assessment.getAffectedDataCenters().size() > 1) {
            plan.setStrategy(RecoveryStrategy.MULTI_DC_RESTORE);
        } else if (assessment.getDataLoss() > 0.1) { // > 10% данных
            plan.setStrategy(RecoveryStrategy.FULL_RESTORE);
        } else {
            plan.setStrategy(RecoveryStrategy.INCREMENTAL_RESTORE);
        }

        // Планирование ресурсов
        plan.setRequiredNodes(assessment.getAffectedNodes().size() * 2);
        plan.setRequiredStorage(assessment.getDataLoss() * 1.5); // Резерв

        return plan;
    }

    private void provisionInfrastructure(RecoveryPlan plan) {
        // Развертывание новых узлов
        for (int i = 0; i < plan.getRequiredNodes(); i++) {
            provisioning.provisionNode(generateNodeConfig(plan));
        }

        // Настройка сети и безопасности
        configureNetworkSecurity(plan);
    }

    private void restoreData(RecoveryPlan plan) {
        switch (plan.getStrategy()) {
            case FULL_RESTORE:
                backupService.restoreFromLatestSnapshot();
                break;
            case INCREMENTAL_RESTORE:
                backupService.restoreFromIncrementalBackup();
                break;
            case MULTI_DC_RESTORE:
                backupService.restoreMultiDataCenter();
                break;
        }
    }

    private void validateRecovery(RecoveryPlan plan) {
        // Проверка целостности данных
        runDataIntegrityChecks();

        // Проверка производительности
        runPerformanceTests();

        // Проверка консистентности
        runConsistencyChecks();
    }
}

enum RecoveryStrategy {
    FULL_RESTORE, INCREMENTAL_RESTORE, MULTI_DC_RESTORE
}

class DisasterAssessment {
    private List<Node> affectedNodes;
    private List<String> affectedDataCenters;
    private double dataLoss;
    private int businessImpact;
    private Duration recoveryTime;

    // getters, setters
}
```

## Лучшие практики

### Планирование кластера

#### 1. Capacity Planning
- **Анализ нагрузки** перед развертыванием
- **Мониторинг роста** данных и нагрузки
- **Планирование масштабирования** заранее
- **Резервирование ресурсов** (20-30%)

#### 2. Топология
- **Минимальный кластер**: 3 узла
- **Multi-DC**: для геораспределенных систем
- **Rack awareness**: для отказоустойчивости
- **Network segregation**: изоляция DC

#### 3. Конфигурация
- **Стандартизация** конфигурации узлов
- **Документирование** всех изменений
- **Версионирование** конфигурационных файлов
- **Автоматизация** развертывания

### Мониторинг и обслуживание

#### 1. Регулярное обслуживание
- **Еженедельный repair** для всех keyspaces
- **Мониторинг disk space** (> 50% free)
- **Ротация логов** и cleanup
- **Обновление версий** Cassandra

#### 2. Производительность
- **Мониторинг latency** (< 10ms reads, < 5ms writes)
- **Оптимизация запросов** (EXPLAIN, индексы)
- **Настройка JVM** (heap, GC)
- **Кэширование** на уровне приложения

#### 3. Безопасность
- **Шифрование** данных в транзите
- **Аутентификация** и авторизация
- **Firewall rules** для узлов
- **Регулярные аудиты** безопасности

### Масштабирование

#### 1. Горизонтальное масштабирование
- **Добавление узлов** по одному
- **Rebalancing** после добавления
- **Мониторинг распределения** нагрузки
- **Оптимизация токенов** (vnodes)

#### 2. Вертикальное масштабирование
- **Увеличение ресурсов** узла
- **Перераспределение данных** при необходимости
- **Тестирование производительности** после изменений

### Резервное копирование

#### 1. Стратегия backup
- **Ежедневные snapshots** для критичных данных
- **Incremental backups** между snapshots
- **Хранение в нескольких локациях**
- **Тестирование восстановления**

#### 2. Disaster Recovery
- **Документированный план** восстановления
- **Регулярные drills** (учения)
- **Автоматизация** восстановления
- **Мониторинг RTO/RPO**

### Troubleshooting

#### 1. Распространенные проблемы
- **Hot spots**: неравномерное распределение данных
- **Compaction storms**: перегрузка при compaction
- **Network partitions**: разделение сети
- **Disk failures**: отказы дисков

#### 2. Диагностика
- **System logs**: анализ логов Cassandra
- **Nodetool commands**: диагностика состояния
- **JMX metrics**: мониторинг производительности
- **Tracing**: анализ медленных запросов

## Заключение

Кластеризация Apache Cassandra — это комплексная задача, требующая глубокого понимания распределенных систем, сетевых технологий и стратегий высокой доступности. Ключевые аспекты успешного управления кластером:

### Архитектурные решения:

1. **Ring Architecture** — распределенная хэш-кольцо для партиционирования
2. **Gossip Protocol** — децентрализованный обмен информацией между узлами
3. **Seed Nodes** — начальные точки для присоединения новых узлов
4. **Failure Detection** — обнаружение отказов с помощью Phi Accrual

### Стратегии развертывания:

1. **Single DC** — простая топология для development/small production
2. **Multi-DC** — геораспределенные кластеры для высокой доступности
3. **Hybrid Cloud** — комбинация on-premise и cloud ресурсов

### Управление жизненным циклом:

1. **Добавление узлов** — автоматизированное provisioning и bootstrapping
2. **Удаление узлов** — graceful decommission и force remove
3. **Замена узлов** — восстановление после отказов

### Репликация и консистентность:

1. **NetworkTopologyStrategy** — оптимальная репликация для DC
2. **Consistency Levels** — баланс между производительностью и надежностью
3. **Hinted Handoff** — обработка временных отказов
4. **Read Repair** — поддержание консистентности данных

### Масштабирование и балансировка:

1. **Horizontal Scaling** — добавление узлов и датацентров
2. **Vertical Scaling** — увеличение ресурсов существующих узлов
3. **Token Distribution** — равномерное распределение данных
4. **Auto-scaling** — автоматизированное управление размером кластера

### Мониторинг и обслуживание:

1. **Nodetool** — основная утилита для управления и диагностики
2. **JMX** — детальный мониторинг через Java Management Extensions
3. **Prometheus** — сбор и анализ метрик
4. **Регулярное обслуживание** — repair, cleanup, upgrades

### Резервное копирование и восстановление:

1. **Snapshots** — point-in-time копии данных
2. **Incremental Backups** — экономичное резервное копирование
3. **Medusa** — enterprise-grade backup tool
4. **Disaster Recovery** — планы и процедуры восстановления

### Лучшие практики:

1. **Capacity Planning** — анализ требований перед развертыванием
2. **Monitoring** — постоянный мониторинг здоровья кластера
3. **Security** — защита данных и инфраструктуры
4. **Automation** — автоматизация рутинных задач управления

Управление Cassandra кластером — это непрерывный процесс, требующий внимания к деталям, регулярного мониторинга и своевременного реагирования на изменения. Правильная архитектура и следование best practices обеспечивают высокую производительность, надежность и масштабируемость системы. 🎯

