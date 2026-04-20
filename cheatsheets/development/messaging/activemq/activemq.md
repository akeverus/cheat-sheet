---
title: "Apache ActiveMQ"
description: "Apache ActiveMQ — это популярный open-source message broker, реализующий JMS (Java Message Service) спецификацию и поддерживающий множество протоколов обмена сообщениями. ActiveMQ предоставляет надежную, масштабируемую и высокопроизводительную платформу для асинхронной коммуникац"
tags:
  - development
  - messaging
  - activemq
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Apache ActiveMQ

**Apache ActiveMQ** — это популярный **open-source message broker**, реализующий **JMS** (`Java Message Service`) спецификацию и поддерживающий множество протоколов обмена сообщениями. **ActiveMQ** предоставляет надежную, масштабируемую и высокопроизводительную платформу для асинхронной коммуникации между приложениями.

## Полезные ссылки
- [Apache ActiveMQ Documentation](https://activemq.apache.org/components/classic/documentation)
- [ActiveMQ GitHub](https://github.com/apache/activemq)
- [JMS Specification (JSR 914)](https://jakarta.ee/specifications/messaging/)
- [ActiveMQ Web Console](https://activemq.apache.org/components/classic/documentation/web-console) — веб-консоль
- [ActiveMQ Configuration](https://activemq.apache.org/components/classic/documentation/configuration) — конфигурация

### См. также
- [[rabbitmq|RabbitMQ]] — альтернативный **message broker**
- [[kafka|Kafka]] — **high-throughput** messaging
- [[nats|NATS]] — **cloud-native** мессенджинг
- [[event-driven|Event-Driven Architecture]] — **event-driven** паттерны

## Содержание

- [Основы ActiveMQ](#основы-activemq)
  - [Что такое ActiveMQ](#что-такое-activemq)
  - [Ключевые возможности](#ключевые-возможности)
  - [Use Cases](#use-cases)
- [Типичные сценарии использования ActiveMQ](#типичные-сценарии-использования-activemq)
- [Архитектура и компоненты](#архитектура-и-компоненты)
  - [Core Components](#core-components)
  - [Message Flow](#message-flow)
- [Установка и конфигурация](#установка-и-конфигурация)
  - [Установка](#установка)
- [Скачивание и установка ActiveMQ](#скачивание-и-установка-activemq)
- [Запуск](#запуск)
- [Проверка статуса](#проверка-статуса)
- [Остановка](#остановка)
- [Логи](#логи)
  - [Docker установка](#docker-установка)
- [Docker Compose для ActiveMQ](#docker-compose-для-activemq)
  - [Конфигурация activemq.xml](#конфигурация-activemqxml)
- [Протоколы и транспорт](#протоколы-и-транспорт)
  - [Transport Connectors](#transport-connectors)
  - [Failover Transport](#failover-transport)
  - [Network Bridges](#network-bridges)
- [Destinations (Очереди и Топики)](#destinations-очереди-и-топики)
  - [Queues (Очереди)](#queues-очереди)
  - [Topics (Топики)](#topics-топики)
  - [Virtual Destinations](#virtual-destinations)
- [Message Types](#message-types)
  - [Text Messages](#text-messages)
  - [Object Messages](#object-messages)
  - [Bytes Messages и Streams](#bytes-messages-и-streams)
  - [Message Properties и Headers](#message-properties-и-headers)
- [Клиенты и API](#клиенты-и-api)
  - [Java JMS Client](#java-jms-client)
  - [Spring Boot Integration](#spring-boot-integration)
  - [REST API для ActiveMQ](#rest-api-для-activemq)
- [Отправка сообщения через REST API](#отправка-сообщения-через-rest-api)
- [Получение сообщения через REST API](#получение-сообщения-через-rest-api)
- [Получение информации о брокере](#получение-информации-о-брокере)
- [Управление и мониторинг](#управление-и-мониторинг)
  - [Web Console](#web-console)
- [Доступ к веб-консоли](#доступ-к-веб-консоли)
- [URL: http://localhost:8161/admin](#url-httplocalhost8161admin)
- [Default credentials: admin/admin](#default-credentials-adminadmin)
- [Через веб-консоль можно:](#через-веб-консоль-можно)
- [Просматривать очереди и топики](#просматривать-очереди-и-топики)
- [Мониторить соединения и подписки](#мониторить-соединения-и-подписки)
- [Отправлять тестовые сообщения](#отправлять-тестовые-сообщения)
- [Управлять destinations](#управлять-destinations)
- [Просматривать статистику](#просматривать-статистику)
  - [JMX Monitoring](#jmx-monitoring)
  - [Metrics и Alerting](#metrics-и-alerting)
- [Prometheus metrics для ActiveMQ](#prometheus-metrics-для-activemq)
- [ActiveMQ экспортирует метрики через JMX](#activemq-экспортирует-метрики-через-jmx)
- [Prometheus configuration](#prometheus-configuration)
- [Grafana Dashboard для ActiveMQ](#grafana-dashboard-для-activemq)
- [Метрики для мониторинга:](#метрики-для-мониторинга)
- [Broker: TotalMessageCount, TotalConsumerCount, TotalProducerCount](#broker-totalmessagecount-totalconsumercount-totalproducercount)
- [Queues: QueueSize, ConsumerCount, ProducerCount, EnqueueCount, DequeueCount](#queues-queuesize-consumercount-producercount-enqueuecount-dequeuecount)
- [Topics: ConsumerCount, ProducerCount, EnqueueCount](#topics-consumercount-producercount-enqueuecount)
- [System: MemoryUsage, StoreUsage, TempUsage](#system-memoryusage-storeusage-tempusage)
- [Кластеризация и HA](#кластеризация-и-ha)
  - [Master/Slave Configuration](#masterslave-configuration)
  - [Network of Brokers](#network-of-brokers)
  - [Load Balancing](#load-balancing)
- [Интеграция с приложениями](#интеграция-с-приложениями)
  - [Spring Integration](#spring-integration)
  - [Apache Camel Integration](#apache-camel-integration)
  - [Microservices Communication](#microservices-communication)
- [Решение проблем](#решение-проблем)
  - [Common Issues и Solutions](#common-issues-и-solutions)
- [1. Connection refused](#1-connection-refused)
- [Решение: Проверить что ActiveMQ запущен и порт открыт](#решение-проверить-что-activemq-запущен-и-порт-открыт)
- [2. Out of memory errors](#2-out-of-memory-errors)
- [Решение: Увеличить heap size в bin/activemq](#решение-увеличить-heap-size-в-binactivemq)
- [3. Slow performance](#3-slow-performance)
- [Решение: Проверить persistence settings, увеличить memory limits](#решение-проверить-persistence-settings-увеличить-memory-limits)
- [activemq.xml — увеличить systemUsage limits](#activemqxml-увеличить-systemusage-limits)
- [4. Messages not being consumed](#4-messages-not-being-consumed)
- [Решение: Проверить consumer acknowledgment mode](#решение-проверить-consumer-acknowledgment-mode)
- [Проверить что consumer не завис](#проверить-что-consumer-не-завис)
- [5. High CPU usage](#5-high-cpu-usage)
- [Решение: Проверить для deadlock'ов, уменьшить connection limits](#решение-проверить-для-deadlockов-уменьшить-connection-limits)
  - [Monitoring Queries](#monitoring-queries)
- [JMX queries для диагностики](#jmx-queries-для-диагностики)
- [Получить информацию о брокере](#получить-информацию-о-брокере)
- [Проверить состояние очередей](#проверить-состояние-очередей)
- [Проверить соединения](#проверить-соединения)
- [Health check](#health-check)
  - [Performance Tuning](#performance-tuning)
- [Лучшие практики](#лучшие-практики)

## Основы ActiveMQ

### Что такое ActiveMQ
```xml
<!-- ActiveMQ - Message Broker для асинхронной коммуникации -->
<beans xmlns="http://www.springframework.org/schema/beans">
  <bean id="connectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
    <property name="brokerURL" value="tcp://localhost:61616"/>
  </bean>

  <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
    <property name="connectionFactory" ref="connectionFactory"/>
  </bean>
</beans>
```

### Ключевые возможности
- **JMS `1.1` совместимость** — Полная поддержка **Java Message Service**
- **Множество протоколов** — **AMQP**, **MQTT**, **STOMP**, **OpenWire**
- **Persistence** — Поддержка **KahaDB**, **JDBC**, **LevelDB**
- **Security** — Аутентификация, авторизация, шифрование
- **Clustering** — **Network** of **Brokers**, **Master**/**Slave**
- **Web Console** — Управление через веб-интерфейс
- **REST API** — **HTTP** интерфейс для управления

### Use Cases
```yaml
# Типичные сценарии использования ActiveMQ
use_cases:
  - enterprise_integration:
      description: "Интеграция enterprise приложений"
      pattern: "ESB (Enterprise Service Bus)"
      protocols: ["JMS", "AMQP", "MQTT"]

  - iot_communications:
      description: "IoT устройства и сенсоры"
      pattern: "Publish/Subscribe"
      protocols: ["MQTT", "STOMP"]

  - microservices:
      description: "Коммуникация между микросервисами"
      pattern: "Request/Reply, Event-Driven"
      protocols: ["AMQP", "OpenWire"]

  - financial_services:
      description: "Финансовые транзакции"
      pattern: "Guaranteed Delivery"
      features: ["XA Transactions", "Persistent Messages"]

  - e_commerce:
      description: "Заказы, инвентаризация, доставка"
      pattern: "Saga Pattern"
      protocols: ["JMS", "AMQP"]
```

## Архитектура и компоненты

### Core Components
```text
# Архитектура ActiveMQ: брокер, очереди, топики и коннекторы
ActiveMQ Architecture:
├── Message Broker
│   ├── Transport Connectors    # Протоколы (TCP, HTTP, SSL)
│   ├── Persistence Adapter     # Хранение сообщений
│   ├── Security Plugin         # Аутентификация/Авторизация
│   └── Network Connectors      # Кластеризация
│
├── Destinations
│   ├── Queues                  # Point-to-Point
│   ├── Topics                  # Publish/Subscribe
│   └── Temporary Destinations  # Временные очереди/топики
│
├── Messages
│   ├── Headers                 # Метаданные
│   ├── Properties              # Пользовательские свойства
│   └── Body                    # Содержимое
│
└── Clients
    ├── Producers               # Отправители сообщений
    ├── Consumers               # Получатели сообщений
    └── Browsers                # Просмотр сообщений
```

### Message Flow
```java
// Producer отправляет сообщение
public class MessageProducer {
    public void sendMessage() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("MY.QUEUE");

        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("Hello, ActiveMQ!");
        producer.send(message);

        session.close();
        connection.close();
    }
}

// Consumer получает сообщение
public class MessageConsumer {
    public void receiveMessage() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("MY.QUEUE");

        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage message = (TextMessage) consumer.receive();
        System.out.println("Received: " + message.getText());

        session.close();
        connection.close();
    }
}
```

## Установка и конфигурация

### Установка
```bash
# Скачивание и установка ActiveMQ
wget https://downloads.apache.org/activemq/5.17.3/apache-activemq-5.17.3-bin.tar.gz
tar -xzf apache-activemq-5.17.3-bin.tar.gz
cd apache-activemq-5.17.3

# Запуск
./bin/activemq start

# Проверка статуса
./bin/activemq status

# Остановка
./bin/activemq stop

# Логи
tail -f data/activemq.log
```

### Docker установка
```yaml
# Docker Compose для ActiveMQ
version: '3.8'
services:
  activemq:
    image: rmohr/activemq:5.15.9-alpine
    container_name: activemq
    ports:
      - "61616:61616"    # OpenWire
      - "8161:8161"      # Web Console
      - "5672:5672"      # AMQP
      - "61613:61613"    # STOMP
      - "61614:61614"    # WS
      - "1883:1883"      # MQTT
    environment:
      ACTIVEMQ_ADMIN_LOGIN: admin
      ACTIVEMQ_ADMIN_PASSWORD: admin
    volumes:
      - ./data:/data
      - ./conf:/opt/activemq/conf
    networks:
      - messaging

networks:
  messaging:
    driver: bridge
```

### Конфигурация activemq.xml
```xml
<!-- Основная конфигурация ActiveMQ -->
<beans xmlns="http://www.springframework.org/schema/beans">
    <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}">

    <!-- Persistence -->
    <persistenceAdapter>
      <kahaDB directory="${activemq.data}/kahadb"/>
    </persistenceAdapter>

    <!-- Transport Connectors -->
        <transportConnectors>
            <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="stomp" uri="stomp://0.0.0.0:61613?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
            <transportConnector name="ws" uri="ws://0.0.0.0:61614?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/>
        </transportConnectors>

    <!-- System Usage -->
    <systemUsage>
      <systemUsage>
        <memoryUsage>
          <memoryUsage percentOfJvmHeap="70"/>
        </memoryUsage>
        <storeUsage>
          <storeUsage limit="100 gb"/>
        </storeUsage>
        <tempUsage>
          <tempUsage limit="50 gb"/>
        </tempUsage>
      </systemUsage>
    </systemUsage>

    <!-- Security -->
        <plugins>
      <jaasAuthenticationPlugin configuration="activemq"/>
            <authorizationPlugin>
                <map>
                    <authorizationMap>
                        <authorizationEntries>
              <authorizationEntry topic="&gt;" read="admins" write="admins" admin="admins"/>
              <authorizationEntry queue="&gt;" read="users" write="users" admin="admins"/>
                        </authorizationEntries>
                    </authorizationMap>
                </map>
            </authorizationPlugin>
        </plugins>

    </broker>
</beans>
```

## Протоколы и транспорт

### Transport Connectors
```xml
<!-- Различные транспортные коннекторы -->
<transportConnectors>
  <!-- TCP/OpenWire - основной протокол ActiveMQ -->
  <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?transport.connectAttemptTimeout=3000"/>

  <!-- AMQP 1.0 -->
  <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?transport.transformer=jms"/>

  <!-- STOMP -->
  <transportConnector name="stomp" uri="stomp://0.0.0.0:61613?transport.defaultHeartBeat=5000:0"/>

  <!-- MQTT -->
  <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883"/>

  <!-- WebSocket -->
  <transportConnector name="ws" uri="ws://0.0.0.0:61614"/>

  <!-- SSL/TLS -->
  <transportConnector name="ssl" uri="ssl://0.0.0.0:61617?needClientAuth=true"/>

  <!-- HTTP (для REST API) -->
  <transportConnector name="http" uri="http://0.0.0.0:8080"/>
</transportConnectors>
```

### Failover Transport
```java
// Failover для высокой доступности
String failoverUrl = "failover:(tcp://broker1:61616,tcp://broker2:61616,tcp://broker3:61616)" +
                    "?randomize=false" +
                    "&maxReconnectAttempts=5" +
                    "&initialReconnectDelay=1000" +
                    "&maxReconnectDelay=30000";

ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(failoverUrl);
Connection connection = factory.createConnection();
```

### Network Bridges
```xml
<!-- Network Bridges для кластеризации -->
<networkConnectors>
  <networkConnector name="bridge-to-remote"
                   uri="static:(tcp://remote-broker:61616)"
                   duplex="true"
                   decreaseNetworkConsumerPriority="true"
                   networkTTL="3"
                   dynamicOnly="true">
    <excludedDestinations>
      <queue physicalName=">"/>
    </excludedDestinations>
  </networkConnector>
</networkConnectors>
```

## Destinations (Очереди и Топики)

### Queues (Очереди)
```java
// Работа с очередями
public class QueueExample {
    public void workWithQueues() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Создание очереди
        Queue queue = session.createQueue("orders.queue");

        // Producer
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("New order: #12345");
        producer.send(message);

        // Consumer
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(message -> {
            try {
                System.out.println("Received: " + ((TextMessage) message).getText());
                // Обработка сообщения
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        // Не закрываем соединение в примере
    }
}
```

### Topics (Топики)
```java
// Работа с топиками (Publish/Subscribe)
public class TopicExample {
    public void workWithTopics() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Создание топика
        Topic topic = session.createTopic("news.updates");

        // Publisher
        MessageProducer publisher = session.createProducer(topic);
        TextMessage message = session.createTextMessage("Breaking news!");
        publisher.send(message);

        // Subscribers
        for (int i = 0; i < 3; i++) {
            MessageConsumer subscriber = session.createConsumer(topic);
            subscriber.setMessageListener(message -> {
                try {
                    System.out.println("Subscriber received: " +
                                     ((TextMessage) message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
```

### Virtual Destinations
```xml
<!-- Virtual Destinations для гибкой маршрутизации -->
<destinationInterceptors>
  <virtualDestinationInterceptor>
    <virtualDestinations>
      <!-- Composite Queue - сообщения попадают в обе очереди -->
      <compositeQueue name="composite.queue">
        <forwardTo>
          <queue physicalName="queue1"/>
          <queue physicalName="queue2"/>
        </forwardTo>
      </compositeQueue>

      <!-- Virtual Topic - очередь получает копии всех сообщений топика -->
      <virtualTopic name="VirtualTopic.Orders" prefix="Consumer.*.Orders.">
        <forwardTo>
          <topic physicalName="Orders"/>
        </forwardTo>
      </virtualTopic>
    </virtualDestinations>
  </virtualDestinationInterceptor>
</destinationInterceptors>
```

## Message Types

### Text Messages
```java
// TextMessage - наиболее распространенный тип
public void sendTextMessage() throws JMSException {
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Queue queue = session.createQueue("messages.text");

    MessageProducer producer = session.createProducer(queue);

    // Простое текстовое сообщение
    TextMessage textMessage = session.createTextMessage();
    textMessage.setText("Hello, World!");
    textMessage.setStringProperty("messageType", "greeting");
    textMessage.setLongProperty("timestamp", System.currentTimeMillis());

    producer.send(textMessage);

    // Сложное сообщение в формате JSON
    String jsonPayload = """
        {
          "orderId": "12345",
          "customerId": "67890",
          "items": [
            {"productId": "ABC", "quantity": 2},
            {"productId": "XYZ", "quantity": 1}
          ],
          "total": 150.00
        }
        """;

    TextMessage jsonMessage = session.createTextMessage(jsonPayload);
    jsonMessage.setStringProperty("contentType", "application/json");
    jsonMessage.setStringProperty("messageType", "order");

    producer.send(jsonMessage);
}
```

### Object Messages
```java
// ObjectMessage - сериализованные Java объекты
public void sendObjectMessage() throws JMSException {
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Queue queue = session.createQueue("messages.objects");

    MessageProducer producer = session.createProducer(queue);

    // Сериализуемый объект
    Order order = new Order("12345", "67890", 150.00);
    order.addItem(new OrderItem("ABC", "Widget", 2, 50.00));
    order.addItem(new OrderItem("XYZ", "Gadget", 1, 50.00));

    ObjectMessage objectMessage = session.createObjectMessage();
    objectMessage.setObject(order);
    objectMessage.setStringProperty("messageType", "order");

    producer.send(objectMessage);
}

// Получение ObjectMessage
public void receiveObjectMessage() throws JMSException {
    MessageConsumer consumer = session.createConsumer(queue);
    consumer.setMessageListener(message -> {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMsg = (ObjectMessage) message;
                Order order = (Order) objMsg.getObject();

                System.out.println("Received order: " + order.getOrderId());
            // Обработка заказа
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}
```

### Bytes Messages и Streams
```java
// BytesMessage для бинарных данных
public void sendBytesMessage() throws JMSException {
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Queue queue = session.createQueue("messages.binary");

    MessageProducer producer = session.createProducer(queue);

    // Чтение файла и отправка как BytesMessage
    try (FileInputStream fis = new FileInputStream("document.pdf")) {
        BytesMessage bytesMessage = session.createBytesMessage();

        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            bytesMessage.writeBytes(buffer, 0, bytesRead);
        }

        bytesMessage.setStringProperty("filename", "document.pdf");
        bytesMessage.setStringProperty("contentType", "application/pdf");
        bytesMessage.setLongProperty("fileSize", new File("document.pdf").length());

        producer.send(bytesMessage);
    } catch (IOException e) {
        throw new JMSException("Failed to read file: " + e.getMessage());
    }
}
```

### Message Properties и Headers
```java
// Расширенные свойства сообщений
public void sendMessageWithProperties() throws JMSException {
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Queue queue = session.createQueue("messages.enhanced");

    MessageProducer producer = session.createProducer(queue);
    TextMessage message = session.createTextMessage("Enhanced message");

    // Стандартные JMS headers
    message.setJMSType("order.confirmation");
    message.setJMSCorrelationID("order-12345");
    message.setJMSMessageID("msg-67890");
    message.setJMSTimestamp(System.currentTimeMillis());
    message.setJMSExpiration(System.currentTimeMillis() + 3600000); // 1 hour
    message.setJMSPriority(5); // 0-9, higher = more priority
    message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

    // Пользовательские свойства
    message.setStringProperty("orderId", "ORD-2023-001");
    message.setStringProperty("customerType", "premium");
    message.setIntProperty("itemCount", 3);
    message.setDoubleProperty("orderTotal", 299.99);
    message.setBooleanProperty("expressShipping", true);
    message.setLongProperty("createdAt", System.currentTimeMillis());

    // Группировка сообщений
    message.setStringProperty("JMSXGroupID", "customer-12345");
    message.setIntProperty("JMSXGroupSeq", 1);

    producer.send(message);
}
```

## Клиенты и API

### Java JMS Client
```java
// Полный пример JMS клиента
public class JMSClient {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;

    public void initialize() throws JMSException {
        // Создание connection factory
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Настройка connection factory
        ((ActiveMQConnectionFactory) connectionFactory).setTrustAllPackages(true);
        ((ActiveMQConnectionFactory) connectionFactory).setWatchTopicAdvisories(false);

        // Создание соединения
        connection = connectionFactory.createConnection();
        connection.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(JMSException exception) {
                System.err.println("JMS Exception: " + exception.getMessage());
            }
        });
        connection.start();

        // Создание сессии
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void sendMessage(String queueName, String message) throws JMSException {
        Queue queue = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(queue);

        // Настройка producer
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        producer.setPriority(4);
        producer.setTimeToLive(3600000); // 1 hour

        TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);

        producer.close();
    }

    public void receiveMessages(String queueName) throws JMSException {
        Queue queue = session.createQueue(queueName);
        MessageConsumer consumer = session.createConsumer(queue);

        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received: " + textMessage.getText());
            }
        } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void close() throws JMSException {
        if (session != null) session.close();
        if (connection != null) connection.close();
    }
}
```

### Spring Boot Integration
```java
// Spring Boot с ActiveMQ
@SpringBootApplication
@EnableJms
public class Application {

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDeliveryMode(DeliveryMode.PERSISTENT);
        template.setTimeToLive(3600000);
        return template;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// JMS Producer
@Service
public class MessageProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOrder(Order order) {
        jmsTemplate.convertAndSend("orders.queue", order, message -> {
            message.setStringProperty("messageType", "order.created");
            message.setStringProperty("orderId", order.getId());
            return message;
                });
            }
        }

// JMS Consumer
@Component
public class OrderConsumer {

    @JmsListener(destination = "orders.queue")
    public void processOrder(Order order, @Header("orderId") String orderId) {
        System.out.println("Processing order: " + orderId);
        // Обработка заказа
    }
}
```

### REST API для ActiveMQ
```bash
# Отправка сообщения через REST API
curl -X POST "http://localhost:8161/api/message/orders.queue?type=queue" \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{
    "orderId": "12345",
    "customerId": "67890",
    "total": 150.00,
    "items": [
      {"productId": "ABC", "quantity": 2},
      {"productId": "XYZ", "quantity": 1}
    ]
  }'

# Получение сообщения через REST API
curl -X GET "http://localhost:8161/api/message/orders.queue?type=queue" \
  -u admin:admin

# Получение информации о брокере
curl -X GET "http://localhost:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost/TotalMessageCount" \
  -u admin:admin
```

## Управление и мониторинг

### Web Console
```bash
# Доступ к веб-консоли
# URL: http://localhost:8161/admin
# Default credentials: admin/admin

# Через веб-консоль можно:
# - Просматривать очереди и топики
# - Мониторить соединения и подписки
# - Отправлять тестовые сообщения
# - Управлять destinations
# - Просматривать статистику
```

### JMX Monitoring
```java
// JMX мониторинг ActiveMQ
public class ActiveMQMonitor {
    public void monitorBroker() throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        // Получение информации о брокере
        ObjectName brokerObjName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        String brokerName = (String) mbsc.getAttribute(brokerObjName, "BrokerName");
        long totalMessages = (Long) mbsc.getAttribute(brokerObjName, "TotalMessageCount");

        System.out.println("Broker: " + brokerName);
        System.out.println("Total Messages: " + totalMessages);

        // Информация о очередях
        ObjectName queueObjName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=*");
        Set<ObjectName> queues = mbsc.queryNames(queueObjName, null);

        for (ObjectName queue : queues) {
            String queueName = (String) mbsc.getAttribute(queue, "Name");
            long queueSize = (Long) mbsc.getAttribute(queue, "QueueSize");
            long consumers = (Long) mbsc.getAttribute(queue, "ConsumerCount");

            System.out.println("Queue: " + queueName + ", Size: " + queueSize + ", Consumers: " + consumers);
        }

        jmxc.close();
    }
}
```

### Metrics и Alerting
```yaml
# Prometheus metrics для ActiveMQ
# ActiveMQ экспортирует метрики через JMX

# Prometheus configuration
scrape_configs:
  - job_name: 'activemq'
    static_configs:
      - targets: ['localhost:1099']
    metrics_path: '/metrics'
    params:
      m: ['jmx']

# Grafana Dashboard для ActiveMQ
# Метрики для мониторинга:
# - Broker: TotalMessageCount, TotalConsumerCount, TotalProducerCount
# - Queues: QueueSize, ConsumerCount, ProducerCount, EnqueueCount, DequeueCount
# - Topics: ConsumerCount, ProducerCount, EnqueueCount
# - System: MemoryUsage, StoreUsage, TempUsage
```

## Кластеризация и `HA`

### Master/Slave Configuration
```xml
<!-- Shared File System Master/Slave -->
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="master-slave-broker">
  <persistenceAdapter>
    <kahaDB directory="/shared/activemq-data"/>
  </persistenceAdapter>

  <transportConnectors>
    <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
  </transportConnectors>
</broker>

<!-- JDBC Master/Slave -->
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="jdbc-master-slave">
    <persistenceAdapter>
        <jdbcPersistenceAdapter dataSource="#mysql-ds"/>
    </persistenceAdapter>

    <transportConnectors>
        <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
    </transportConnectors>
</broker>

<bean id="mysql-ds" class="org.apache.commons.dbcp2.BasicDataSource">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
  <property name="url" value="jdbc:mysql://localhost/activemq?relaxAutoCommit=true"/>
    <property name="username" value="activemq"/>
  <property name="password" value="password"/>
    <property name="maxTotal" value="200"/>
  <property name="maxIdle" value="30"/>
    <property name="maxWaitMillis" value="10000"/>
</bean>
```

### Network of Brokers
```xml
<!-- Network of Brokers для масштабирования -->
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="broker1">
  <networkConnectors>
    <networkConnector uri="static:(tcp://broker2:61616,tcp://broker3:61616)"
                     name="to-broker2-and-broker3"
                     duplex="true"/>
  </networkConnectors>

  <transportConnectors>
    <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
  </transportConnectors>
</broker>

<!-- Broker 2 -->
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="broker2">
  <networkConnectors>
    <networkConnector uri="static:(tcp://broker1:61616,tcp://broker3:61616)"
                     name="to-broker1-and-broker3"
                     duplex="true"/>
  </networkConnectors>

  <transportConnectors>
    <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
  </transportConnectors>
</broker>
```

### Load Balancing
```xml
<!-- Load Balancing с помощью Network Connectors -->
<networkConnectors>
  <networkConnector name="load-balancing-connector"
                   uri="static:(tcp://broker1:61616,tcp://broker2:61616,tcp://broker3:61616)"
                   duplex="true"
                   decreaseNetworkConsumerPriority="true"
                   consumerPriorityBase="10">
    <!-- Conduit subscriptions для load balancing -->
    <dynamicallyIncludedDestinations>
      <queue physicalName=">"/>
      <topic physicalName=">"/>
    </dynamicallyIncludedDestinations>
  </networkConnector>
</networkConnectors>
```

## Интеграция с приложениями

### Spring Integration
```xml
<!-- Spring Integration с ActiveMQ -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:int="http://www.springframework.org/schema/integration"
       xmlns:int-jms="http://www.springframework.org/schema/integration/jms"
       xmlns:jms="http://www.springframework.org/schema/integration/jms">

  <!-- JMS Connection Factory -->
  <bean id="connectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
    <constructor-arg value="tcp://localhost:61616"/>
    </bean>

  <!-- JMS Inbound Channel Adapter -->
  <int-jms:inbound-channel-adapter
      id="jmsIn"
      channel="inputChannel"
      destination-name="orders.queue"
      connection-factory="connectionFactory"
      acknowledge="auto">
    <int:poller fixed-rate="1000"/>
  </int-jms:inbound-channel-adapter>

  <!-- Message Processing -->
  <int:transformer input-channel="inputChannel" output-channel="processedChannel"
                   expression="payload.toUpperCase()"/>

  <!-- JMS Outbound Channel Adapter -->
  <int-jms:outbound-channel-adapter
      id="jmsOut"
      channel="processedChannel"
      destination-name="processed.queue"
      connection-factory="connectionFactory"/>

</beans>
```

### Apache Camel Integration
```java
// Apache Camel routes с ActiveMQ
public class CamelRoutes extends RouteBuilder {
    @Override
    public void configure() {
        // Route 1: Чтение из очереди и обработка
        from("activemq:queue:orders")
            .log("Received order: ${body}")
            .to("bean:orderProcessor")
            .choice()
                .when(header("orderType").isEqualTo("premium"))
                    .to("activemq:queue:premium-orders")
                .otherwise()
                    .to("activemq:queue:standard-orders");

        // Route 2: REST API к JMS
        rest("/api/orders")
            .post()
            .to("direct:createOrder");

        from("direct:createOrder")
            .log("Creating new order")
            .to("bean:orderValidator")
            .to("activemq:queue:orders");

        // Route 3: File processing
        from("file:/data/input?delay=5000")
            .log("Processing file: ${header.CamelFileName}")
            .to("bean:fileProcessor")
            .to("activemq:queue:processed-files");
    }
}
```

### Microservices Communication
```java
// Service Discovery с Eureka
@Configuration
@EnableDiscoveryClient
public class MessagingConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("failover:(tcp://activemq-service:61616)");
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(new MappingJackson2MessageConverter());
        return template;
    }
}

// Event Publishing Service
@Service
public class EventPublisher {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void publishUserCreated(User user) {
        UserCreatedEvent event = new UserCreatedEvent(user.getId(), user.getEmail());
        jmsTemplate.convertAndSend("events.user.created", event);
    }

    public void publishOrderPlaced(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(order.getId(), order.getTotal());
        jmsTemplate.convertAndSend("events.order.placed", event);
    }
}

// Event Consumer Service
@Component
public class EventConsumer {

    @JmsListener(destination = "events.user.created")
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Processing user created event: " + event.getUserId());
        // Обновление индекса поиска, отправка welcome email, etc.
    }

    @JmsListener(destination = "events.order.placed")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        System.out.println("Processing order placed event: " + event.getOrderId());
        // Обновление инвентаря, отправка подтверждения, etc.
    }
}
```

## Решение проблем

### Common Issues и Solutions
```bash
# 1. Connection refused
# Решение: Проверить что ActiveMQ запущен и порт открыт
netstat -tlnp | grep 61616
./bin/activemq status

# 2. Out of memory errors
# Решение: Увеличить heap size в bin/activemq
ACTIVEMQ_OPTS="-Xms1G -Xmx4G"

# 3. Slow performance
# Решение: Проверить persistence settings, увеличить memory limits
# activemq.xml - увеличить systemUsage limits

# 4. Messages not being consumed
# Решение: Проверить consumer acknowledgment mode
# Проверить что consumer не завис

# 5. High CPU usage
# Решение: Проверить для deadlock'ов, уменьшить connection limits
jstack $(pgrep java) | grep -A 10 -B 10 "deadlock"
```

### Monitoring Queries
```bash
# JMX queries для диагностики
# Получить информацию о брокере
curl -s "http://localhost:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost/TotalMessageCount"

# Проверить состояние очередей
curl -s "http://localhost:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=orders.queue/QueueSize"

# Проверить соединения
curl -s "http://localhost:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost/CurrentConnectionsCount"

# Health check
curl -f "http://localhost:8161/api/health" || echo "ActiveMQ is not healthy"
```

### Performance Tuning
```xml
<!-- Performance tuning конфигурация -->
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="tuned-broker">

  <!-- Оптимизированная persistence -->
    <persistenceAdapter>
    <kahaDB directory="/data/activemq/kahadb"
            indexWriteBatchSize="10000"
            enableIndexWriteAsync="true"
            journalMaxFileLength="32mb"/>
    </persistenceAdapter>

  <!-- Transport optimization -->
  <transportConnectors>
    <transportConnector name="optimized-openwire"
                       uri="tcp://0.0.0.0:61616?wireFormat.maxFrameSize=104857600&amp;transport.useInactivityMonitor=false"/>
  </transportConnectors>

  <!-- Memory tuning -->
    <systemUsage>
        <systemUsage sendFailIfNoSpace="true">
            <memoryUsage>
        <memoryUsage limit="2 gb"/>
            </memoryUsage>
            <storeUsage>
                <storeUsage limit="100 gb"/>
            </storeUsage>
            <tempUsage>
        <tempUsage limit="10 gb"/>
            </tempUsage>
        </systemUsage>
    </systemUsage>

  <!-- Destination policy для performance -->
  <destinationPolicy>
    <policyMap>
      <policyEntries>
        <policyEntry topic=">" advisoryForFastProducers="true" advisoryForConsumed="true"/>
        <policyEntry queue=">" optimizedDispatch="true" useCache="false"/>
      </policyEntries>
    </policyMap>
  </destinationPolicy>

</broker>
```

## Лучшие практики

- **Выбор destination:** очереди — для балансировки нагрузки и гарантированной доставки одному потребителю; топики — для **broadcast** и **pub**/**sub**; не смешивайте сценарии в одном **destination**.
- **Persistence и производительность: KahaDB** для большинства сценариев; **JDBC** для кластера и `HA`; отключайте **persistence** для временных сообщений, если допустима потеря.
- **Кластеризация:** используйте **Network** of **Brokers** или **Master**/**Slave** для `HA`; настройте **failover transport** на клиенте; тестируйте отказ узлов.
- **Безопасность:** включайте аутентификацию и авторизацию; шифруйте транспорт (SSL/TLS); не экспонируйте **broker** в интернет без защиты.
- **Мониторинг:** используйте **Web Console** и **JMX**; отслеживайте глубину очередей, количество потребителей и задержки; настройте алерты на накопление сообщений.