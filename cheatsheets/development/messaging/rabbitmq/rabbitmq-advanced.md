---
title: "RabbitMQ Advanced"
description: "RabbitMQ - это надежная система обмена сообщениями с открытым исходным кодом, реализующая протокол AMQP. Этот документ охватывает продвинутые концепции, высокую доступность, производительность и enterprise-grade практики работы с RabbitMQ."
tags:
  - development
  - messaging
  - rabbitmq-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# RabbitMQ Advanced

**RabbitMQ** - это надежная система обмена сообщениями с открытым исходным кодом, реализующая протокол **AMQP**. Этот документ охватывает продвинутые концепции, высокую доступность, производительность и **enterprise-grade** практики работы с **RabbitMQ**.

## Полезные ссылки
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [RabbitMQ Clustering](https://www.rabbitmq.com/clustering.html)
- [RabbitMQ Management](https://www.rabbitmq.com/management.html)
- [Spring AMQP](https://docs.spring.io/spring-amqp/reference/)

## Содержание

- [Кластеризация и высокая доступность](#кластеризация-и-высокая-доступность)
  - [Настройка кластера](#настройка-кластера)
- [docker-compose.yml для кластера RabbitMQ](#docker-composeyml-для-кластера-rabbitmq)
  - [Команды кластеризации](#команды-кластеризации)
- [Остановить приложение на узлах](#остановить-приложение-на-узлах)
- [Присоединить узлы к кластеру](#присоединить-узлы-к-кластеру)
- [Запустить приложение на узлах](#запустить-приложение-на-узлах)
- [Проверить статус кластера](#проверить-статус-кластера)
- [Настроить политику высокой доступности](#настроить-политику-высокой-доступности)
- [Продвинутые **exchange** типы](#продвинутые-exchange-типы)
  - [**Headers Exchange**](#headers-exchange)
  - [**Custom Exchange**](#custom-exchange)
- [**Message patterns**](#message-patterns)
  - [**Request-Reply** (**RPC**)](#request-reply-rpc)
  - [**Saga Pattern**](#saga-pattern)
- [**Federation** и **Shovel**](#federation-и-shovel)
  - [**Federation setup**](#federation-setup)
- [Включить federation plugin](#включить-federation-plugin)
- [На upstream сервере создать upstream](#на-upstream-сервере-создать-upstream)
- [На downstream сервере создать upstream](#на-downstream-сервере-создать-upstream)
- [Создать policy для federation](#создать-policy-для-federation)
  - [**Shovel configuration**](#shovel-configuration)
- [advanced.config для shovel](#advancedconfig-для-shovel)
- [**Management** и **monitoring**](#management-и-monitoring)
  - [**Custom management plugin**](#custom-management-plugin)
  - [**Health checks**](#health-checks)
- [**Security**](#security)
  - [**Advanced authentication**](#advanced-authentication)
- [advanced.config для LDAP authentication](#advancedconfig-для-ldap-authentication)
  - [**TLS**/**SSL configuration**](#tlsssl-configuration)
- [rabbitmq.conf для TLS](#rabbitmqconf-для-tls)
- [Management plugin TLS](#management-plugin-tls)
- [Client certificate authentication](#client-certificate-authentication)
  - [**Access control**](#access-control)
- [Создать vhost](#создать-vhost)
- [Создать пользователей](#создать-пользователей)
- [Назначить теги](#назначить-теги)
- [Назначить разрешения](#назначить-разрешения)
- [Создать политики](#создать-политики)
- [**Performance tuning**](#performance-tuning)
  - [**Connection pooling**](#connection-pooling)
  - [**Queue optimization**](#queue-optimization)
- [Решение проблем](#решение-проблем)
  - [Диагностика проблем](#диагностика-проблем)
- [Проверить connections](#проверить-connections)
- [Проверить channels](#проверить-channels)
- [Проверить queues](#проверить-queues)
- [Проверить exchanges](#проверить-exchanges)
- [Проверить bindings](#проверить-bindings)
- [Проверить consumers](#проверить-consumers)
- [Проверить политики](#проверить-политики)
- [Проверить параметры](#проверить-параметры)
- [Очистить queue](#очистить-queue)
- [Reset node](#reset-node)
- [Force reset (для проблемных узлов)](#force-reset-для-проблемных-узлов)
  - [**Log analysis**](#log-analysis)
- [Анализ логов RabbitMQ](#анализ-логов-rabbitmq)
- [Поиск ошибок](#поиск-ошибок)
- [Поиск предупреждений](#поиск-предупреждений)
- [Анализ подключений](#анализ-подключений)
- [Анализ очередей](#анализ-очередей)
  - [**Performance monitoring**](#performance-monitoring)
- [Лучшие практики](#лучшие-практики)
  - [**Production configuration**](#production-configuration)
- [rabbitmq.conf для production](#rabbitmqconf-для-production)
- [Network](#network)
- [Heartbeat](#heartbeat)
- [Connection limits](#connection-limits)
- [Memory](#memory)
- [Disk](#disk)
- [Queues](#queues)
- [Management](#management)
- [Plugins](#plugins)
- [Clustering](#clustering)
  - [**Monitoring dashboard**](#monitoring-dashboard)
- [Prometheus configuration for RabbitMQ](#prometheus-configuration-for-rabbitmq)
- [См. также](#см-также)

## Кластеризация и высокая доступность

### Настройка кластера
Ниже — **docker-compose** для кластера **RabbitMQ** (**YAML**).
```yaml
# docker-compose.yml для кластера RabbitMQ
version: '3.8'
services:
  rabbitmq1:
    image: rabbitmq:3.12-management
    hostname: rabbitmq1
    environment:
      RABBITMQ_ERLANG_COOKIE: 'secret_cookie'
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    volumes:
      - ./rabbitmq1/data:/var/lib/rabbitmq
      - ./rabbitmq1/logs:/var/log/rabbitmq
    ports:
      - "15672:15672"
      - "5672:5672"
    networks:
      - rabbitmq-cluster

  rabbitmq2:
    image: rabbitmq:3.12-management
    hostname: rabbitmq2
    depends_on:
      - rabbitmq1
    environment:
      RABBITMQ_ERLANG_COOKIE: 'secret_cookie'
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    volumes:
      - ./rabbitmq2/data:/var/lib/rabbitmq
      - ./rabbitmq2/logs:/var/log/rabbitmq
    ports:
      - "15673:15672"
      - "5673:5672"
    networks:
      - rabbitmq-cluster

  rabbitmq3:
    image: rabbitmq:3.12-management
    hostname: rabbitmq3
    depends_on:
      - rabbitmq1
    environment:
      RABBITMQ_ERLANG_COOKIE: 'secret_cookie'
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    volumes:
      - ./rabbitmq3/data:/var/lib/rabbitmq
      - ./rabbitmq3/logs:/var/log/rabbitmq
    ports:
      - "15674:15672"
      - "5674:5672"
    networks:
      - rabbitmq-cluster

networks:
  rabbitmq-cluster:
    driver: bridge
```

### Команды кластеризации
```bash
# Остановить приложение на узлах
docker exec rabbitmq2 rabbitmqctl stop_app
docker exec rabbitmq3 rabbitmqctl stop_app

# Присоединить узлы к кластеру
docker exec rabbitmq2 rabbitmqctl join_cluster rabbit@rabbitmq1
docker exec rabbitmq3 rabbitmqctl join_cluster rabbit@rabbitmq1

# Запустить приложение на узлах
docker exec rabbitmq2 rabbitmqctl start_app
docker exec rabbitmq3 rabbitmqctl start_app

# Проверить статус кластера
docker exec rabbitmq1 rabbitmqctl cluster_status

# Настроить политику высокой доступности
docker exec rabbitmq1 rabbitmqctl set_policy ha-all ".*" '{"ha-mode":"all","ha-sync-mode":"automatic"}'
```

## Продвинутые **exchange** типы

### **Headers Exchange**
```java
@Configuration
public class RabbitMQConfig {

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange("headers-exchange");
    }

    @Bean
    public Queue urgentQueue() {
        return new Queue("urgent-queue", true);
    }

    @Bean
    public Queue normalQueue() {
        return new Queue("normal-queue", true);
    }

    @Bean
    public Binding urgentBinding(HeadersExchange headersExchange, Queue urgentQueue) {
        return BindingBuilder.bind(urgentQueue)
                .to(headersExchange)
                .whereAll("priority", "urgent", "type", "alert")
                .match();
    }

    @Bean
    public Binding normalBinding(HeadersExchange headersExchange, Queue normalQueue) {
        return BindingBuilder.bind(normalQueue)
                .to(headersExchange)
                .where("priority").matches("normal");
    }
}
```

```java
@Service
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HeadersExchange headersExchange;

    public void sendUrgentAlert(String message) {
        MessageProperties props = new MessageProperties();
        props.setHeader("priority", "urgent");
        props.setHeader("type", "alert");
        props.setHeader("department", "security");

        Message msg = MessageBuilder.withBody(message.getBytes())
                .andProperties(props)
                .build();

        rabbitTemplate.send(headersExchange.getName(), "", msg);
    }

    public void sendNormalNotification(String message) {
        MessageProperties props = new MessageProperties();
        props.setHeader("priority", "normal");
        props.setHeader("type", "notification");

        Message msg = MessageBuilder.withBody(message.getBytes())
                .andProperties(props)
                .build();

        rabbitTemplate.send(headersExchange.getName(), "", msg);
    }
}
```

### **Custom Exchange**
```java
public class CustomExchange extends AbstractExchange {

    public CustomExchange(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return "x-custom";
    }

    @Override
    public Map<String, Object> getArguments() {
        return Collections.emptyMap();
    }

    @Override
    public boolean shouldReceive(Message message, Delivery delivery,
                                String routingKey, Binding binding) {
        // Custom routing logic
        String customHeader = (String) message.getMessageProperties()
                .getHeaders().get("custom-route");

        if (customHeader == null) {
            return false;
        }

        // Route based on custom logic
        return customHeader.startsWith(binding.getRoutingKey());
    }
}
```

## **Message patterns**

### **Request-Reply** (**RPC**)
```java
@Service
public class RpcClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange rpcExchange;

    public String call(String message) throws Exception {
        // Generate correlation ID
        String correlationId = UUID.randomUUID().toString();

        // Setup reply queue
        String replyQueueName = rabbitTemplate.execute(channel -> {
            return channel.queueDeclare().getQueue();
        });

        // Send request
        Message request = MessageBuilder.withBody(message.getBytes())
                .setCorrelationId(correlationId)
                .setReplyTo(replyQueueName)
                .build();

        rabbitTemplate.send(rpcExchange.getName(), "rpc", request);

        // Wait for reply with timeout
        Message reply = rabbitTemplate.receive(replyQueueName, 5000);

        if (reply != null && correlationId.equals(reply.getMessageProperties().getCorrelationId())) {
            return new String(reply.getBody());
        } else {
            throw new RuntimeException("RPC call timeout or correlation ID mismatch");
        }
    }
}
```

```java
@Service
public class RpcServer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "rpc-queue")
    public void handleRpcRequest(Message request, @Header("amqp_replyTo") String replyTo,
                                @Header("amqp_correlationId") String correlationId) {
        try {
            String requestBody = new String(request.getBody());
            String response = processRequest(requestBody);

            // Send reply
            Message reply = MessageBuilder.withBody(response.getBytes())
                    .setCorrelationId(correlationId)
                    .build();

            rabbitTemplate.send("", replyTo, reply);

        } catch (Exception e) {
            // Send error response
            Message errorReply = MessageBuilder.withBody("ERROR".getBytes())
                    .setCorrelationId(correlationId)
                    .build();

            rabbitTemplate.send("", replyTo, errorReply);
        }
    }

    private String processRequest(String request) {
        // Business logic here
        return "Processed: " + request.toUpperCase();
    }
}
```

### **Saga Pattern**
```java
@Service
public class OrderSagaCoordinator {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void startOrderSaga(Order order) {
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        // Send command to inventory service
        InventoryReservationCommand inventoryCmd = new InventoryReservationCommand(
            order.getId(), order.getItems());

        rabbitTemplate.convertAndSend("inventory-commands", "reserve", inventoryCmd);

        // Start saga timeout
        scheduleSagaTimeout(order.getId());
    }

    @RabbitListener(queues = "order-events")
    public void handleSagaEvents(OrderEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order == null) return;

        switch (event.getType()) {
            case INVENTORY_RESERVED:
                // Proceed to payment
                PaymentCommand paymentCmd = new PaymentCommand(order.getId(), order.getTotal());
                rabbitTemplate.convertAndSend("payment-commands", "charge", paymentCmd);
                break;

            case INVENTORY_FAILED:
                // Compensate - cancel order
                compensateOrder(order);
                break;

            case PAYMENT_SUCCESSFUL:
                // Complete order
                completeOrder(order);
                break;

            case PAYMENT_FAILED:
                // Compensate - release inventory
                InventoryReleaseCommand releaseCmd = new InventoryReleaseCommand(order.getId());
                rabbitTemplate.convertAndSend("inventory-commands", "release", releaseCmd);
                compensateOrder(order);
                break;
        }
    }

    private void compensateOrder(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Send compensation event
        OrderCompensatedEvent event = new OrderCompensatedEvent(order.getId());
        rabbitTemplate.convertAndSend("order-events", event);
    }

    private void completeOrder(Order order) {
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // Send completion event
        OrderCompletedEvent event = new OrderCompletedEvent(order.getId());
        rabbitTemplate.convertAndSend("order-events", event);
    }
}
```

## **Federation** и **Shovel**

### **Federation setup**
```bash
# Включить federation plugin
rabbitmq-plugins enable rabbitmq_federation
rabbitmq-plugins enable rabbitmq_federation_management

# На upstream сервере создать upstream
curl -i -u admin:admin123 -H "content-type:application/json" \
  -X PUT http://localhost:15672/api/parameters/federation-upstream/%2f/my-upstream \
  -d '{"value":{"uri":"amqp://admin:admin123@upstream-server:5672","expires":3600000}}'

# На downstream сервере создать upstream
curl -i -u admin:admin123 -H "content-type:application/json" \
  -X PUT http://localhost:15672/api/parameters/federation-upstream/%2f/my-upstream \
  -d '{"value":{"uri":"amqp://admin:admin123@upstream-server:5672","expires":3600000}}'

# Создать policy для federation
curl -i -u admin:admin123 -H "content-type:application/json" \
  -X PUT http://localhost:15672/api/policies/%2f/federate-me \
  -d '{"pattern":"^federated","definition":{"federation-upstream-set":"all"}}'
```

### **Shovel configuration**
```yaml
# advanced.config для shovel
[
  {rabbitmq_shovel,
    [{shovels,
      [{my_shovel,
        {sources,
          [{brokers, ["amqp://admin:admin123@localhost:5672"]},
           {declarations,
            [{'queue.declare',
              [{queue, <<"source-queue">>}, durable]},
             {'queue.bind',
              [{queue, <<"source-queue">>},
               {exchange, <<"source-exchange">>},
               {routing_key, <<"key">>}]}]},
          {prefetch_count, 1000},
          {ack_mode, on_confirm}]},
        {destinations,
          [{brokers, ["amqp://admin:admin123@remote-server:5672"]},
           {declarations,
            [{'exchange.declare',
              [{exchange, <<"target-exchange">>}, {type, <<"direct">>}, durable]}]}]},
        {queue, <<"shovel-queue">>},
        {publish_fields, [{exchange, <<"target-exchange">>},
                         {routing_key, <<"key">>}]},
        {reconnect_delay, 5}]}]}]}].
```

## **Management** и **monitoring**

### **Custom management plugin**
```java
@Component
public class RabbitMQMetricsCollector {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void collectMetrics() {
        try {
            // Get overview statistics
            RabbitMQManagementTemplate managementTemplate =
                new RabbitMQManagementTemplate(rabbitTemplate.getConnectionFactory());

            OverviewResponse overview = managementTemplate.getOverview();

            // Record queue metrics
            for (QueueInfo queue : overview.getQueues()) {
                meterRegistry.gauge("rabbitmq_queue_messages",
                    Tags.of(
                        "queue", queue.getName(),
                        "vhost", queue.getVhost()
                    ),
                    queue.getMessages());

                meterRegistry.gauge("rabbitmq_queue_consumers",
                    Tags.of(
                        "queue", queue.getName(),
                        "vhost", queue.getVhost()
                    ),
                    queue.getConsumers());
            }

            // Record connection metrics
            for (ConnectionInfo connection : overview.getConnections()) {
                meterRegistry.gauge("rabbitmq_connection_channels",
                    Tags.of("connection", connection.getName()),
                    connection.getChannels());
            }

            // Record node metrics
            for (NodeInfo node : overview.getNodes()) {
                meterRegistry.gauge("rabbitmq_node_memory_used",
                    Tags.of("node", node.getName()),
                    node.getMemoryUsed());

                meterRegistry.gauge("rabbitmq_node_disk_free",
                    Tags.of("node", node.getName()),
                    node.getDiskFree());
            }

        } catch (Exception e) {
            logger.error("Failed to collect RabbitMQ metrics", e);
        }
    }
}
```

### **Health checks**
```java
@Component
public class RabbitMQHealthIndicator implements HealthIndicator {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Health health() {
        try {
            // Test connection
            rabbitTemplate.execute(channel -> {
                channel.queueDeclarePassive("health-check-queue");
                return null;
            });

            // Get cluster status
            RabbitMQManagementTemplate managementTemplate =
                new RabbitMQManagementTemplate(rabbitTemplate.getConnectionFactory());

            ClusterStatus clusterStatus = managementTemplate.getClusterStatus();

            return Health.up()
                .withDetail("clusterName", clusterStatus.getClusterName())
                .withDetail("nodes", clusterStatus.getNodes().size())
                .withDetail("runningNodes", clusterStatus.getRunningNodes().size())
                .build();

        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
```

## **Security**

### **Advanced authentication**
```yaml
# advanced.config для LDAP authentication
[
  {rabbitmq_auth_backend_ldap,
    [{servers, ["ldap.example.com"]},
     {user_dn_pattern, "cn=${username},ou=users,dc=example,dc=com"},
     {group_dn_pattern, "cn=${groupname},ou=groups,dc=example,dc=com"},
     {vhost_access_query,
       {in_group, "cn=admin,ou=groups,dc=example,dc=com"}},
     {resource_access_query,
       {constant, true}}]},

  {rabbitmq_auth_backend_internal, []}
].
```

### **TLS**/**SSL configuration**
```properties
# rabbitmq.conf для TLS
listeners.ssl.default = 5671

ssl_options.cacertfile = /etc/rabbitmq/certs/ca_certificate.pem
ssl_options.certfile = /etc/rabbitmq/certs/server_certificate.pem
ssl_options.keyfile = /etc/rabbitmq/certs/server_key.pem
ssl_options.verify = verify_peer
ssl_options.fail_if_no_peer_cert = false

# Management plugin TLS
management.ssl.certfile = /etc/rabbitmq/certs/server_certificate.pem
management.ssl.keyfile = /etc/rabbitmq/certs/server_key.pem
management.ssl.cacertfile = /etc/rabbitmq/certs/ca_certificate.pem
management.ssl.port = 15671

# Client certificate authentication
auth_mechanisms.1 = EXTERNAL
ssl_options.client_renegotiation = false
```

### **Access control**
```bash
# Создать vhost
rabbitmqctl add_vhost /production

# Создать пользователей
rabbitmqctl add_user producer_user producer_password
rabbitmqctl add_user consumer_user consumer_password
rabbitmqctl add_user admin_user admin_password

# Назначить теги
rabbitmqctl set_user_tags producer_user producer
rabbitmqctl set_user_tags consumer_user consumer
rabbitmqctl set_user_tags admin_user administrator

# Назначить разрешения
rabbitmqctl set_permissions -p /production producer_user ".*" ".*" "orders|inventory"
rabbitmqctl set_permissions -p /production consumer_user "^(?!orders).*" "^(?!orders).*" "orders|notifications"
rabbitmqctl set_permissions -p /production admin_user ".*" ".*" ".*"

# Создать политики
rabbitmqctl set_policy -p /production ha-orders "orders.*" '{"ha-mode":"all"}'
rabbitmqctl set_policy -p /production ttl-logs "logs.*" '{"message-ttl":86400000}'
```

## **Performance tuning**

### **Connection pooling**
```java
@Configuration
public class RabbitMQConnectionConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        // Connection pooling
        connectionFactory.setChannelCacheSize(25);
        connectionFactory.setChannelCheckoutTimeout(10000);

        // Connection recovery
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(10000);
        connectionFactory.setTopologyRecoveryEnabled(true);

        // Publisher confirms
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // Message conversion
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // Retry configuration
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());

        rabbitTemplate.setRetryTemplate(retryTemplate);

        // Mandatory delivery
        rabbitTemplate.setMandatory(true);

        return rabbitTemplate;
    }
}
```

### **Queue optimization**
```java
@Configuration
public class OptimizedQueueConfig {

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable("order-queue")
                .withArgument("x-max-priority", 10)  // Priority queue
                .withArgument("x-message-ttl", 86400000)  // 24 hours TTL
                .withArgument("x-dead-letter-exchange", "dlx-exchange")
                .withArgument("x-dead-letter-routing-key", "order.failed")
                .withArgument("x-overflow", "reject-publish")  // Reject when full
                .withArgument("x-max-length", 10000)  // Max queue length
                .withArgument("x-queue-mode", "lazy")  // Lazy queue for large queues
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("order-dlq")
                .build();
    }

    @Bean
    public Exchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("dlx-exchange")
                .durable(true)
                .build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, Exchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("order.failed")
                .noargs();
    }
}
```

## Решение проблем

### Диагностика проблем
```bash
# Проверить статус кластера
rabbitmqctl cluster_status

# Проверить connections
rabbitmqctl list_connections

# Проверить channels
rabbitmqctl list_channels

# Проверить queues
rabbitmqctl list_queues name messages consumers

# Проверить exchanges
rabbitmqctl list_exchanges

# Проверить bindings
rabbitmqctl list_bindings

# Проверить consumers
rabbitmqctl list_consumers

# Проверить политики
rabbitmqctl list_policies

# Проверить параметры
rabbitmqctl list_parameters

# Очистить queue
rabbitmqctl purge_queue my-queue

# Reset node
rabbitmqctl reset

# Force reset (для проблемных узлов)
rabbitmqctl force_reset
```

### **Log analysis**
```bash
# Анализ логов RabbitMQ
tail -f /var/log/rabbitmq/rabbit@localhost.log

# Поиск ошибок
grep "ERROR" /var/log/rabbitmq/rabbit@localhost.log

# Поиск предупреждений
grep "WARNING" /var/log/rabbitmq/rabbit@localhost.log

# Анализ подключений
grep "connection" /var/log/rabbitmq/rabbit@localhost.log | tail -20

# Анализ очередей
grep "queue" /var/log/rabbitmq/rabbit@localhost.log | tail -20
```

### **Performance monitoring**
```java
@Component
public class RabbitMQPerformanceMonitor {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 60000) // Every minute
    public void monitorPerformance() {
        try {
            RabbitMQManagementTemplate managementTemplate =
                new RabbitMQManagementTemplate(rabbitTemplate.getConnectionFactory());

            // Monitor queue performance
            List<QueueInfo> queues = managementTemplate.getQueues();
            for (QueueInfo queue : queues) {
                // Queue depth
                meterRegistry.gauge("rabbitmq_queue_depth",
                    Tags.of("queue", queue.getName()),
                    queue.getMessages());

                // Consumer utilization
                if (queue.getConsumers() > 0) {
                    double utilization = (double) queue.getConsumerUtilisation() / 100.0;
                    meterRegistry.gauge("rabbitmq_consumer_utilization",
                        Tags.of("queue", queue.getName()),
                        utilization);
                }

                // Message rates
                meterRegistry.gauge("rabbitmq_messages_publish_rate",
                    Tags.of("queue", queue.getName()),
                    queue.getMessageStats() != null ?
                        queue.getMessageStats().getPublishDetails().getRate() : 0);

                meterRegistry.gauge("rabbitmq_messages_deliver_rate",
                    Tags.of("queue", queue.getName()),
                    queue.getMessageStats() != null ?
                        queue.getMessageStats().getDeliverDetails().getRate() : 0);
            }

            // Monitor connection performance
            List<ConnectionInfo> connections = managementTemplate.getConnections();
            for (ConnectionInfo connection : connections) {
                meterRegistry.gauge("rabbitmq_connection_channels",
                    Tags.of("connection", connection.getName()),
                    connection.getChannels());
            }

        } catch (Exception e) {
            logger.error("Failed to monitor RabbitMQ performance", e);
        }
    }
}
```

## **Best practices**

### **Production configuration**
```properties
# rabbitmq.conf для production
# Network
listeners.tcp.default = 5672
tcp_listen_options.backlog = 128
tcp_listen_options.nodelay = true
tcp_listen_options.linger.on = true
tcp_listen_options.linger.timeout = 0

# Heartbeat
heartbeat = 60

# Connection limits
max_connections = 2048
max_channels_per_connection = 2048

# Memory
vm_memory_high_watermark.relative = 0.6
vm_memory_high_watermark_paging_ratio = 0.5

# Disk
disk_free_limit.absolute = 50000000

# Queues
queue_index_embed_msgs_below = 4096

# Management
management.path_prefix = /mgmt
management.listener.port = 15672
management.listener.ssl = false

# Plugins
enabled_plugins_file = /etc/rabbitmq/enabled_plugins

# Clustering
cluster_partition_handling = pause_minority
cluster_keepalive_interval = 10000
```

### **Monitoring dashboard**
```yaml
# Prometheus configuration for RabbitMQ
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'rabbitmq'
    static_configs:
      - targets: ['localhost:15692']
    metrics_path: /api/metrics
    params:
      family: ['rabbitmq_*']
    basic_auth:
      username: 'monitor'
      password: 'monitor123'

  - job_name: 'rabbitmq-exporter'
    static_configs:
      - targets: ['rabbitmq-exporter:9419']
```
## См. также
- [Kafka](../kafka/kafka.md) — альтернативная система сообщений
- [Spring Integration](../../../frameworks/java-frameworks/spring/spring-integration.md) — интеграционные паттерны
- [Prometheus](../../../monitoring/metrics/prometheus.md) — система мониторинга
- [Docker](../../../platform/containers/docker/docker-basics.md) — контейнеризация
