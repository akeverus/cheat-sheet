---
title: "Компьютерные сети"
description: "Кратко: Комплексное руководство по компьютерным сетям - от физического уровня до сетевых протоколов и архитектуры с практическими примерами на Java."
tags:
  - basics
  - networks
  - networks-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Компьютерные сети

Кратко: Комплексное руководство по компьютерным сетям — от физического уровня до сетевых протоколов и архитектуры с практическими примерами на **Java**.

## Полезные ссылки

### Официальная документация
- [RFC Editor](https://www.rfc-editor.org/) — стандарты интернет-протоколов

### Ресурсы
- [Introduction to Computer Networking](https://www.baeldung.com/cs/networking-basics) — введение в сетевые технологии

### См. также
- [[computer-science-basics|Computer Science]]
- [Операционные системы](../operating-systems/)

## Содержание

- [Введение в компьютерные сети](#введение-в-компьютерные-сети)
  - [Основные концепции сетей](#основные-концепции-сетей)
  - [Характеристики сетей](#характеристики-сетей)
- [Модель OSI](#модель-osi)
  - [Уровни модели OSI](#уровни-модели-osi)
- [TCP/IP стек](#tcpip-стек)
  - [Структура TCP/IP стека](#структура-tcpip-стека)
- [IP адресация](#ip-адресация)
  - [IPv4 адресация](#ipv4-адресация)
  - [IPv6 адресация](#ipv6-адресация)
- [Протоколы транспортного уровня](#протоколы-транспортного-уровня)
  - [TCP (Transmission Control Protocol)](#tcp-transmission-control-protocol)
  - [UDP (User Datagram Protocol)](#udp-user-datagram-protocol)
- [Протоколы прикладного уровня](#протоколы-прикладного-уровня)
  - [HTTP (HyperText Transfer Protocol)](#http-hypertext-transfer-protocol)
  - [DNS (Domain Name System)](#dns-domain-name-system)
- [Беспроводные сети](#беспроводные-сети)
  - [Wi-Fi сети](#wi-fi-сети)
- [Сетевая безопасность](#сетевая-безопасность)
  - [Firewall и NAT](#firewall-и-nat)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые концепции](#ключевые-концепции)
  - [Практические рекомендации](#практические-рекомендации)
  - [Будущие тенденции](#будущие-тенденции)

## Введение в компьютерные сети

Компьютерные сети — это системы взаимосвязанных устройств, которые могут обмениваться данными и ресурсами. Они формируют основу современной цифровой инфраструктуры.

### Основные концепции сетей

```java
/**
 * Основные понятия компьютерных сетей
 */
public class NetworkConcepts {

    /**
     * Типы сетевых топологий
     */
    public enum Topology {
        BUS,        // Шинная топология
        STAR,       // Звездная топология
        RING,       // Кольцевая топология
        MESH,       // Ячеистая топология
        TREE,       // Древовидная топология
        HYBRID      // Гибридная топология
    }

    /**
     * Типы сетей по охвату
     */
    public enum NetworkScope {
        PAN("Personal Area Network", "Сеть персональной области"),
        LAN("Local Area Network", "Локальная сеть"),
        MAN("Metropolitan Area Network", "Городская сеть"),
        WAN("Wide Area Network", "Глобальная сеть"),
        GAN("Global Area Network", "Глобальная сеть"),
        INTERNET("Internet", "Интернет");

        private final String acronym;
        private final String description;

        NetworkScope(String acronym, String description) {
            this.acronym = acronym;
            this.description = description;
        }

        public String getAcronym() { return acronym; }
        public String getDescription() { return description; }
    }

    /**
     * Сетевые устройства
     */
    public enum NetworkDevice {
        HUB("Хаб", "Распространяет сигнал всем портам"),
        SWITCH("Коммутатор", "Интеллектуально направляет трафик"),
        ROUTER("Маршрутизатор", "Направляет трафик между сетями"),
        BRIDGE("Мост", "Соединяет сегменты сети"),
        GATEWAY("Шлюз", "Преобразует протоколы между сетями"),
        FIREWALL("Файрвол", "Фильтрует сетевой трафик"),
        MODEM("Модем", "Модулирует/демодулирует сигналы");

        private final String name;
        private final String description;

        NetworkDevice(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Типы сетевых сред
     */
    public enum TransmissionMedia {
        TWISTED_PAIR("Витая пара", "Дешевая, широко используется"),
        COAXIAL("Коаксиальный кабель", "Хорошая защита от помех"),
        FIBER_OPTIC("Оптоволокно", "Высокая скорость, большое расстояние"),
        WIRELESS("Беспроводная", "Мобильность, удобство"),
        SATELLITE("Спутниковая", "Глобальный охват");

        private final String name;
        private final String description;

        TransmissionMedia(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Демонстрация сетевых концепций
     */
    public void demonstrateConcepts() {
        System.out.println("=== Типы сетей по охвату ===");
        for (NetworkScope scope : NetworkScope.values()) {
            System.out.println(scope.getAcronym() + " - " + scope.getDescription());
        }

        System.out.println("\n=== Сетевые устройства ===");
        for (NetworkDevice device : NetworkDevice.values()) {
            System.out.println(device.name + ": " + device.description);
        }

        System.out.println("\n=== Передающие среды ===");
        for (TransmissionMedia media : TransmissionMedia.values()) {
            System.out.println(media.name + ": " + media.description);
        }
    }
}
```

### Характеристики сетей

```java
/**
 * Характеристики компьютерных сетей
 */
public class NetworkCharacteristics {

    /**
     * Метрики производительности сети
     */
    public static class PerformanceMetrics {
        private double bandwidth;      // Пропускная способность (бит/с)
        private double latency;        // Задержка (мс)
        private double jitter;         // Джиттер (мс)
        private double packetLoss;     // Потери пакетов (%)

        public PerformanceMetrics(double bandwidth, double latency,
                                double jitter, double packetLoss) {
            this.bandwidth = bandwidth;
            this.latency = latency;
            this.jitter = jitter;
            this.packetLoss = packetLoss;
        }

        public void displayMetrics() {
            System.out.println("Пропускная способность: " + bandwidth + " Мбит/с");
            System.out.println("Задержка: " + latency + " мс");
            System.out.println("Джиттер: " + jitter + " мс");
            System.out.println("Потери пакетов: " + packetLoss + "%");
        }

        /**
         * Расчет эффективной пропускной способности
         */
        public double effectiveBandwidth() {
            // Упрощенная формула: учитываем потери и протокол overhead
            double efficiency = (100.0 - packetLoss) / 100.0;
            return bandwidth * efficiency * 0.8; // 80% - типичный overhead TCP/IP
        }
    }

    /**
     * Качество обслуживания (QoS)
     */
    public static class QualityOfService {

        public enum ServiceClass {
            BEST_EFFORT("Best Effort", "Без гарантий"),
            INTEGRATED_SERVICES("IntServ", "Гарантированная QoS"),
            DIFFERENTIATED_SERVICES("DiffServ", "Относительные приоритеты");

            private final String name;
            private final String description;

            ServiceClass(String name, String description) {
                this.name = name;
                this.description = description;
            }
        }

        /**
         * Параметры QoS
         */
        public static class QoSParameters {
            public double bandwidth;     // Гарантированная пропускная способность
            public double delay;         // Максимальная задержка
            public double jitter;        // Максимальный джиттер
            public double packetLoss;    // Максимальные потери

            public QoSParameters(double bandwidth, double delay,
                               double jitter, double packetLoss) {
                this.bandwidth = bandwidth;
                this.delay = delay;
                this.jitter = jitter;
                this.packetLoss = packetLoss;
            }
        }
    }

    /**
     * Надежность сети
     */
    public static class NetworkReliability {

        /**
         * Расчет availability (доступности)
         */
        public static double calculateAvailability(double uptime, double totalTime) {
            return (uptime / totalTime) * 100.0;
        }

        /**
         * Расчет MTBF (Mean Time Between Failures)
         */
        public static double calculateMTBF(double totalUptime, int numberOfFailures) {
            return totalUptime / numberOfFailures;
        }

        /**
         * Расчет MTTR (Mean Time To Repair)
         */
        public static double calculateMTTR(double totalDowntime, int numberOfRepairs) {
            return totalDowntime / numberOfRepairs;
        }

        public void demonstrateReliability() {
            // Пример: сеть с 99.9% доступности ("три девятки")
            double uptime = 365 * 24 * 0.999;  // 99.9% от года в часах
            double totalTime = 365 * 24;
            double availability = calculateAvailability(uptime, totalTime);

            System.out.println("Доступность сети 99.9%:");
            System.out.println("Время работы: " + String.format("%.2f", uptime) + " часов");
            System.out.println("Общее время: " + totalTime + " часов");
            System.out.println("Процент доступности: " + String.format("%.3f", availability) + "%");
            System.out.println("Время простоя: " + String.format("%.2f", totalTime - uptime) + " часов");
        }
    }
}
```

## Модель **OSI**

Модель **OSI** (**Open Systems Interconnection**) — концептуальная модель, описывающая взаимодействие систем в сети.

### Уровни модели **OSI**

```java
/**
 * Реализация модели OSI
 */
public class OSIModel {

    /**
     * Уровни модели OSI
     */
    public enum OSILayer {
        PHYSICAL(1, "Физический", "Передача битов по физической среде"),
        DATA_LINK(2, "Канальный", "Надежная передача данных между соседними узлами"),
        NETWORK(3, "Сетевой", "Маршрутизация и логическая адресация"),
        TRANSPORT(4, "Транспортный", "Надежная передача данных между процессами"),
        SESSION(5, "Сеансовый", "Управление сеансами связи"),
        PRESENTATION(6, "Представительский", "Преобразование форматов данных"),
        APPLICATION(7, "Прикладной", "Интерфейс с пользовательскими приложениями");

        private final int level;
        private final String name;
        private final String description;

        OSILayer(int level, String name, String description) {
            this.level = level;
            this.name = name;
            this.description = description;
        }

        public int getLevel() { return level; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    /**
     * Сообщение, проходящее через уровни OSI
     */
    public static class OSIMessage {
        private String data;
        private Map<OSILayer, String> headers;

        public OSIMessage(String data) {
            this.data = data;
            this.headers = new EnumMap<>(OSILayer.class);
        }

        public void addHeader(OSILayer layer, String header) {
            headers.put(layer, header);
        }

        public String getHeader(OSILayer layer) {
            return headers.get(layer);
        }

        public String getData() {
            return data;
        }

        /**
         * Инкапсуляция данных при прохождении вниз по стеку
         */
        public void encapsulate(OSILayer layer, String encapsulationData) {
            data = encapsulationData + "|" + data;
        }

        /**
         * Декапсуляция данных при прохождении вверх по стеку
         */
        public String decapsulate() {
            int separatorIndex = data.indexOf('|');
            if (separatorIndex != -1) {
                String encapsulation = data.substring(0, separatorIndex);
                data = data.substring(separatorIndex + 1);
                return encapsulation;
            }
            return data;
        }
    }

    /**
     * Узел сети с реализацией OSI стека
     */
    public static class OSINode {
        private String name;
        private Map<OSILayer, Object> layerImplementations;

        public OSINode(String name) {
            this.name = name;
            this.layerImplementations = new EnumMap<>(OSILayer.class);
            initializeLayers();
        }

        private void initializeLayers() {
            // Упрощенная инициализация слоев
            layerImplementations.put(OSILayer.PHYSICAL, new PhysicalLayer());
            layerImplementations.put(OSILayer.DATA_LINK, new DataLinkLayer());
            layerImplementations.put(OSILayer.NETWORK, new NetworkLayer());
            layerImplementations.put(OSILayer.TRANSPORT, new TransportLayer());
            layerImplementations.put(OSILayer.SESSION, new SessionLayer());
            layerImplementations.put(OSILayer.PRESENTATION, new PresentationLayer());
            layerImplementations.put(OSILayer.APPLICATION, new ApplicationLayer());
        }

        /**
         * Отправка сообщения через стек протоколов
         */
        public void sendMessage(OSINode destination, OSIMessage message) {
            System.out.println(name + " отправляет сообщение '" + message.getData() + "'");

            // Проход сверху вниз по стеку протоколов (инкапсуляция)
            for (OSILayer layer : OSILayer.values()) {
                processOutgoing(layer, message);
            }

            // Имитация передачи по сети
            System.out.println("Сообщение передается по сети...");

            // Получатель обрабатывает сообщение снизу вверх (декапсуляция)
            destination.receiveMessage(this, message);
        }

        /**
         * Получение сообщения
         */
        public void receiveMessage(OSINode sender, OSIMessage message) {
            System.out.println(name + " получает сообщение");

            // Проход снизу вверх по стеку протоколов
            OSILayer[] layers = OSILayer.values();
            for (int i = layers.length - 1; i >= 0; i--) {
                processIncoming(layers[i], message);
            }

            System.out.println(name + " обработал сообщение: '" + message.getData() + "'");
        }

        private void processOutgoing(OSILayer layer, OSIMessage message) {
            System.out.println("  " + layer.getName() + " уровень: обработка исходящего сообщения");
            message.encapsulate(layer, layer.name() + "_HEADER");
        }

        private void processIncoming(OSILayer layer, OSIMessage message) {
            System.out.println("  " + layer.getName() + " уровень: обработка входящего сообщения");
            message.decapsulate();
        }
    }

    // Упрощенные реализации слоев (заглушки)
    static class PhysicalLayer {}
    static class DataLinkLayer {}
    static class NetworkLayer {}
    static class TransportLayer {}
    static class SessionLayer {}
    static class PresentationLayer {}
    static class ApplicationLayer {}

    /**
     * Демонстрация модели OSI
     */
    public static void demonstrateOSI() {
        System.out.println("=== Модель OSI ===");

        // Вывод всех уровней
        for (OSILayer layer : OSILayer.values()) {
            System.out.println("Уровень " + layer.getLevel() + ": " + layer.getName());
            System.out.println("  " + layer.getDescription());
        }

        System.out.println("\n=== Передача сообщения по OSI ===");
        OSINode client = new OSINode("Клиент");
        OSINode server = new OSINode("Сервер");

        OSIMessage message = new OSIMessage("Привет, мир!");
        client.sendMessage(server, message);
    }
}
```

## **TCP**/`IP` стек

**TCP**/`IP` - это набор протоколов, на которых построен интернет.

### Структура **TCP**/`IP` стека

```java
/**
 * Реализация TCP/IP стека
 */
public class TCPIPStack {

    /**
     * IP адрес
     */
    public static class IPAddress {
        private int[] octets;

        public IPAddress(String ipString) {
            String[] parts = ipString.split("\\.");
            if (parts.length != 4) throw new IllegalArgumentException("Неверный IP адрес");

            octets = new int[4];
            for (int i = 0; i < 4; i++) {
                octets[i] = Integer.parseInt(parts[i]);
                if (octets[i] < 0 || octets[i] > 255) {
                    throw new IllegalArgumentException("Неверный октет: " + octets[i]);
                }
            }
        }

        @Override
        public String toString() {
            return octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3];
        }

        public boolean isPrivate() {
            // 192.168.x.x, 172.16-31.x.x, 10.x.x.x
            return (octets[0] == 192 && octets[1] == 168) ||
                   (octets[0] == 172 && octets[1] >= 16 && octets[1] <= 31) ||
                   (octets[0] == 10);
        }

        /**
         * Преобразование в целое число
         */
        public long toLong() {
            return (octets[0] << 24) + (octets[1] << 16) + (octets[2] << 8) + octets[3];
        }

        /**
         * Получение класса сети
         */
        public char getClassType() {
            if ((octets[0] & 0x80) == 0) return 'A';      // 0xxx xxxx
            if ((octets[0] & 0xC0) == 0x80) return 'B';  // 10xx xxxx
            if ((octets[0] & 0xE0) == 0xC0) return 'C';  // 110x xxxx
            if ((octets[0] & 0xF0) == 0xE0) return 'D';  // 1110 xxxx
            return 'E';                                   // 1111 xxxx
        }
    }

    /**
     * Порт
     */
    public static class Port {
        private int port;

        public Port(int port) {
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("Неверный порт: " + port);
            }
            this.port = port;
        }

        public boolean isWellKnown() { return port < 1024; }
        public boolean isRegistered() { return port >= 1024 && port < 49152; }
        public boolean isDynamic() { return port >= 49152; }

        public int getValue() { return port; }

        @Override
        public String toString() { return String.valueOf(port); }
    }

    /**
     * TCP соединение
     */
    public static class TCPConnection {
        private IPAddress localIP, remoteIP;
        private Port localPort, remotePort;
        private TCPState state;
        private List<String> dataBuffer;

        public enum TCPState {
            CLOSED, LISTEN, SYN_SENT, SYN_RECEIVED,
            ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2,
            CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT
        }

        public TCPConnection(IPAddress localIP, Port localPort,
                           IPAddress remoteIP, Port remotePort) {
            this.localIP = localIP;
            this.localPort = localPort;
            this.remoteIP = remoteIP;
            this.remotePort = remotePort;
            this.state = TCPState.CLOSED;
            this.dataBuffer = new ArrayList<>();
        }

        /**
         * Трехстороннее рукопожатие
         */
        public void establishConnection() {
            System.out.println("Установление TCP соединения:");
            System.out.println("Локальный адрес: " + localIP + ":" + localPort);
            System.out.println("Удаленный адрес: " + remoteIP + ":" + remotePort);

            // SYN
            state = TCPState.SYN_SENT;
            System.out.println("Клиент: SYN отправлен");

            // SYN-ACK
            state = TCPState.SYN_RECEIVED;
            System.out.println("Сервер: SYN-ACK отправлен");

            // ACK
            state = TCPState.ESTABLISHED;
            System.out.println("Клиент: ACK отправлен");
            System.out.println("Соединение установлено!");
        }

        /**
         * Отправка данных
         */
        public void sendData(String data) {
            if (state == TCPState.ESTABLISHED) {
                dataBuffer.add(data);
                System.out.println("Отправлено: " + data);
            } else {
                throw new IllegalStateException("Соединение не установлено");
            }
        }

        /**
         * Закрытие соединения
         */
        public void closeConnection() {
            System.out.println("Закрытие TCP соединения:");

            // FIN
            state = TCPState.FIN_WAIT_1;
            System.out.println("Клиент: FIN отправлен");

            // ACK
            state = TCPState.FIN_WAIT_2;
            System.out.println("Сервер: ACK отправлен");

            // FIN
            state = TCPState.TIME_WAIT;
            System.out.println("Сервер: FIN отправлен");

            // ACK
            state = TCPState.CLOSED;
            System.out.println("Клиент: ACK отправлен");
            System.out.println("Соединение закрыто!");
        }

        public TCPState getState() { return state; }
    }

    /**
     * HTTP запрос
     */
    public static class HTTPRequest {
        private String method;
        private String path;
        private String version;
        private Map<String, String> headers;
        private String body;

        public HTTPRequest(String method, String path) {
            this.method = method;
            this.path = path;
            this.version = "HTTP/1.1";
            this.headers = new HashMap<>();
        }

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        public void setBody(String body) {
            this.body = body;
            headers.put("Content-Length", String.valueOf(body.length()));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(method).append(" ").append(path).append(" ").append(version).append("\r\n");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }

            sb.append("\r\n");
            if (body != null) sb.append(body);

            return sb.toString();
        }
    }

    /**
     * Демонстрация TCP/IP стека
     */
    public static void demonstrateTCPIP() {
        System.out.println("=== TCP/IP стек ===");

        // IP адреса
        IPAddress clientIP = new IPAddress("192.168.1.100");
        IPAddress serverIP = new IPAddress("192.168.1.200");

        System.out.println("Клиент IP: " + clientIP + " (приватный: " + clientIP.isPrivate() +
                          ", класс: " + clientIP.getClassType() + ")");
        System.out.println("Сервер IP: " + serverIP + " (приватный: " + serverIP.isPrivate() +
                          ", класс: " + serverIP.getClassType() + ")");

        // Порты
        Port clientPort = new Port(49153); // Динамический порт
        Port serverPort = new Port(80);    // HTTP порт

        System.out.println("Клиент порт: " + clientPort + " (динамический: " + clientPort.isDynamic() + ")");
        System.out.println("Сервер порт: " + serverPort + " (well-known: " + serverPort.isWellKnown() + ")");

        // TCP соединение
        TCPConnection connection = new TCPConnection(clientIP, clientPort, serverIP, serverPort);
        connection.establishConnection();

        // Отправка HTTP запроса
        HTTPRequest request = new HTTPRequest("GET", "/index.html");
        request.addHeader("Host", "example.com");
        request.addHeader("User-Agent", "Java HTTP Client");

        connection.sendData(request.toString());

        // Закрытие соединения
        connection.closeConnection();
    }
}
```

## `IP` адресация

`IP` адресация — система адресации устройств в сети.

### **IPv4** адресация

```java
/**
 * Работа с IPv4 адресами
 */
public class IPv4Addressing {

    /**
     * Подсеть (subnet)
     */
    public static class Subnet {
        private IPAddress networkAddress;
        private IPAddress subnetMask;
        private int prefixLength;  // CIDR notation (/24)

        public Subnet(IPAddress network, IPAddress mask, int prefix) {
            this.networkAddress = network;
            this.subnetMask = mask;
            this.prefixLength = prefix;
        }

        /**
         * Проверка, принадлежит ли IP адрес подсети
         */
        public boolean contains(IPAddress ip) {
            long networkLong = networkAddress.toLong();
            long maskLong = subnetMask.toLong();
            long ipLong = ip.toLong();

            return (networkLong & maskLong) == (ipLong & maskLong);
        }

        /**
         * Получение количества хостов в подсети
         */
        public int getHostCount() {
            return (int) Math.pow(2, 32 - prefixLength) - 2; // Минус сеть и broadcast
        }

        /**
         * Получение broadcast адреса
         */
        public IPAddress getBroadcastAddress() {
            long networkLong = networkAddress.toLong();
            long maskLong = subnetMask.toLong();
            long broadcastLong = networkLong | (~maskLong & 0xFFFFFFFFL);

            return longToIPAddress(broadcastLong);
        }

        private IPAddress longToIPAddress(long value) {
            return new IPAddress(
                (int)((value >> 24) & 0xFF) + "." +
                (int)((value >> 16) & 0xFF) + "." +
                (int)((value >> 8) & 0xFF) + "." +
                (int)(value & 0xFF)
            );
        }

        @Override
        public String toString() {
            return networkAddress + "/" + prefixLength;
        }
    }

    /**
     * Преобразование CIDR в маску подсети
     */
    public static IPAddress cidrToMask(int prefixLength) {
        long mask = 0xFFFFFFFFL << (32 - prefixLength);
        return new IPAddress(
            (int)((mask >> 24) & 0xFF) + "." +
            (int)((mask >> 16) & 0xFF) + "." +
            (int)((mask >> 8) & 0xFF) + "." +
            (int)(mask & 0xFF)
        );
    }

    /**
     * Расчет параметров подсети
     */
    public static Subnet calculateSubnet(String networkCIDR) {
        String[] parts = networkCIDR.split("/");
        IPAddress network = new IPAddress(parts[0]);
        int prefix = Integer.parseInt(parts[1]);
        IPAddress mask = cidrToMask(prefix);

        return new Subnet(network, mask, prefix);
    }

    /**
     * Демонстрация IP адресации
     */
    public static void demonstrateIPv4() {
        System.out.println("=== IPv4 адресация ===");

        // Примеры подсетей
        String[] networks = {
            "192.168.1.0/24",    // Домашняя сеть
            "10.0.0.0/8",        // Частная сеть класса A
            "172.16.0.0/12",     // Частная сеть класса B
            "203.0.113.0/24"     // TEST-NET-3 (RFC 5737)
        };

        for (String networkCIDR : networks) {
            Subnet subnet = calculateSubnet(networkCIDR);

            System.out.println("\nПодсеть: " + subnet);
            System.out.println("Маска подсети: " + subnet.subnetMask);
            System.out.println("Количество хостов: " + subnet.getHostCount());
            System.out.println("Broadcast адрес: " + subnet.getBroadcastAddress());

            // Проверка принадлежности адресов
            IPAddress testIP1 = new IPAddress("192.168.1.1");
            IPAddress testIP2 = new IPAddress("192.168.2.1");

            if (subnet.contains(testIP1)) {
                System.out.println(testIP1 + " принадлежит подсети");
            }
            if (!subnet.contains(testIP2)) {
                System.out.println(testIP2 + " НЕ принадлежит подсети");
            }
        }
    }
}
```

### **IPv6** адресация

```java
/**
 * Работа с IPv6 адресами
 */
public class IPv6Addressing {

    /**
     * IPv6 адрес
     */
    public static class IPv6Address {
        private long[] hextets;  // 8 групп по 16 бит

        public IPv6Address(String ipv6String) {
            String[] parts = ipv6String.split(":");
            hextets = new long[8];

            // Обработка сокращений (::)
            int doubleColonIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].isEmpty()) {
                    doubleColonIndex = i;
                    break;
                }
            }

            int index = 0;
            for (int i = 0; i < parts.length; i++) {
                if (i == doubleColonIndex) {
                    // Заполняем нулями пропущенные группы
                    int zerosToAdd = 8 - (parts.length - 1);
                    for (int j = 0; j < zerosToAdd; j++) {
                        hextets[index++] = 0;
                    }
                } else if (!parts[i].isEmpty()) {
                    hextets[index++] = Long.parseLong(parts[i], 16);
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            // Находим самую длинную последовательность нулей для сокращения
            int bestStart = -1;
            int bestLength = 0;
            int currentStart = -1;
            int currentLength = 0;

            for (int i = 0; i < 8; i++) {
                if (hextets[i] == 0) {
                    if (currentStart == -1) {
                        currentStart = i;
                        currentLength = 1;
                    } else {
                        currentLength++;
                    }
                } else {
                    if (currentLength > bestLength) {
                        bestStart = currentStart;
                        bestLength = currentLength;
                    }
                    currentStart = -1;
                    currentLength = 0;
                }
            }

            // Проверяем последнюю последовательность
            if (currentLength > bestLength) {
                bestStart = currentStart;
                bestLength = currentLength;
            }

            // Формируем строку
            for (int i = 0; i < 8; i++) {
                if (i == bestStart && bestLength > 1) {
                    sb.append("::");
                    i += bestLength - 1;
                } else {
                    if (i > 0 && !(i == bestStart + bestLength && bestStart != -1)) {
                        sb.append(":");
                    }
                    sb.append(String.format("%x", hextets[i]));
                }
            }

            return sb.toString();
        }

        /**
         * Проверка типа адреса
         */
        public String getAddressType() {
            if (hextets[0] == 0x2001 && hextets[1] == 0x0db8) return "Документация (TEST-NET-2)";
            if (hextets[0] == 0xfe80) return "Link-local";
            if ((hextets[0] & 0xffc0) == 0xfe80) return "Link-local";
            if ((hextets[0] & 0xff00) == 0xff00) return "Multicast";
            if (hextets[0] == 0 && hextets[1] == 0 && hextets[2] == 0 && hextets[3] == 0 &&
                hextets[4] == 0 && hextets[5] == 0 && hextets[6] == 0 && hextets[7] == 1) {
                return "Loopback (::1)";
            }
            if (hextets[0] == 0 && hextets[1] == 0 && hextets[2] == 0 && hextets[3] == 0) {
                return "IPv4-compatible или IPv4-mapped";
            }
            return "Global unicast";
        }
    }

    /**
     * Демонстрация IPv6 адресации
     */
    public static void demonstrateIPv6() {
        System.out.println("=== IPv6 адресация ===");

        String[] ipv6Examples = {
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334",  // Полный адрес
            "2001:db8:85a3::8a2e:370:7334",              // Сокращенный
            "::1",                                        // Loopback
            "fe80::1",                                    // Link-local
            "ff02::1",                                    // Multicast
            "2001:db8::"                                  // Документация
        };

        for (String ipv6 : ipv6Examples) {
            IPv6Address address = new IPv6Address(ipv6);
            System.out.println("Оригинал: " + ipv6);
            System.out.println("Нормализованный: " + address);
            System.out.println("Тип: " + address.getAddressType());
            System.out.println();
        }

        System.out.println("Преимущества IPv6:");
        System.out.println("• 2^128 адресов (340 undecillion)");
        System.out.println("• Упрощенная автоконфигурация");
        System.out.println("• Встроенная безопасность (IPsec)");
        System.out.println("• Улучшенная поддержка мобильности");
        System.out.println("• Устранение NAT (Network Address Translation)");
    }
}
```

## Протоколы транспортного уровня

**TCP** и **UDP** — основные протоколы транспортного уровня.

### **TCP** (**Transmission `Control` Protocol**)

```java
/**
 * Реализация TCP протокола
 */
public class TCPProtocol {

    /**
     * TCP сегмент
     */
    public static class TCPSegment {
        private int sourcePort;
        private int destinationPort;
        private long sequenceNumber;
        private long acknowledgmentNumber;
        private int dataOffset;
        private TCPFlags flags;
        private int windowSize;
        private int checksum;
        private int urgentPointer;
        private byte[] options;
        private byte[] payload;

        public enum TCPFlags {
            URG(32), ACK(16), PSH(8), RST(4), SYN(2), FIN(1);

            private final int value;
            TCPFlags(int value) { this.value = value; }
        }

        public TCPSegment(int sourcePort, int destPort) {
            this.sourcePort = sourcePort;
            this.destinationPort = destPort;
            this.flags = new TCPFlags[0];
            this.windowSize = 65535;  // Максимальный размер окна
        }

        // Геттеры и сеттеры
        public void setSequenceNumber(long seq) { this.sequenceNumber = seq; }
        public void setAcknowledgmentNumber(long ack) { this.acknowledgmentNumber = ack; }
        public void setFlags(TCPFlags[] flags) { this.flags = flags; }
        public void setPayload(byte[] payload) { this.payload = payload; }

        @Override
        public String toString() {
            return String.format("TCP Segment: %d -> %d, SEQ=%d, ACK=%d",
                               sourcePort, destinationPort, sequenceNumber, acknowledgmentNumber);
        }
    }

    /**
     * TCP соединение с управлением состоянием
     */
    public static class TCPConnectionManager {

        public enum TCPState {
            CLOSED, LISTEN, SYN_SENT, SYN_RECEIVED,
            ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2,
            CLOSE_WAIT, CLOSING, LAST_ACK, TIME_WAIT
        }

        private TCPState state;
        private long sequenceNumber;
        private long acknowledgmentNumber;
        private Map<Long, TCPSegment> sentSegments;
        private Queue<TCPSegment> receivedSegments;

        public TCPConnectionManager() {
            this.state = TCPState.CLOSED;
            this.sequenceNumber = 0;
            this.sentSegments = new HashMap<>();
            this.receivedSegments = new LinkedList<>();
        }

        /**
         * Активное открытие соединения (клиент)
         */
        public List<TCPSegment> activeOpen(int sourcePort, int destPort) {
            List<TCPSegment> segments = new ArrayList<>();

            // SYN
            TCPSegment syn = new TCPSegment(sourcePort, destPort);
            syn.setSequenceNumber(sequenceNumber);
            syn.setFlags(new TCPSegment.TCPFlags[]{TCPSegment.TCPFlags.SYN});
            segments.add(syn);
            sentSegments.put(sequenceNumber, syn);

            state = TCPState.SYN_SENT;
            sequenceNumber++;

            System.out.println("Отправлен SYN сегмент");
            return segments;
        }

        /**
         * Пассивное открытие соединения (сервер)
         */
        public List<TCPSegment> passiveOpen(int port) {
            state = TCPState.LISTEN;
            System.out.println("Сервер перешел в состояние LISTEN на порту " + port);
            return new ArrayList<>();
        }

        /**
         * Обработка входящего сегмента
         */
        public List<TCPSegment> processSegment(TCPSegment segment) {
            List<TCPSegment> responseSegments = new ArrayList<>();

            switch (state) {
                case LISTEN:
                    if (segmentContainsFlag(segment, TCPSegment.TCPFlags.SYN)) {
                        // SYN-ACK
                        TCPSegment synAck = new TCPSegment(
                            segment.destinationPort, segment.sourcePort);
                        synAck.setSequenceNumber(sequenceNumber);
                        synAck.setAcknowledgmentNumber(segment.sequenceNumber + 1);
                        synAck.setFlags(new TCPSegment.TCPFlags[]{
                            TCPSegment.TCPFlags.SYN, TCPSegment.TCPFlags.ACK});
                        responseSegments.add(synAck);
                        sentSegments.put(sequenceNumber, synAck);

                        state = TCPState.SYN_RECEIVED;
                        sequenceNumber++;
                        acknowledgmentNumber = segment.sequenceNumber + 1;

                        System.out.println("Отправлен SYN-ACK сегмент");
                    }
                    break;

                case SYN_SENT:
                    if (segmentContainsFlag(segment, TCPSegment.TCPFlags.SYN) &&
                        segmentContainsFlag(segment, TCPSegment.TCPFlags.ACK)) {
                        // ACK
                        TCPSegment ack = new TCPSegment(
                            segment.destinationPort, segment.sourcePort);
                        ack.setSequenceNumber(sequenceNumber);
                        ack.setAcknowledgmentNumber(segment.sequenceNumber + 1);
                        ack.setFlags(new TCPSegment.TCPFlags[]{TCPSegment.TCPFlags.ACK});
                        responseSegments.add(ack);

                        state = TCPState.ESTABLISHED;
                        acknowledgmentNumber = segment.sequenceNumber + 1;

                        System.out.println("Отправлен ACK сегмент, соединение установлено");
                    }
                    break;

                case SYN_RECEIVED:
                    if (segmentContainsFlag(segment, TCPSegment.TCPFlags.ACK)) {
                        state = TCPState.ESTABLISHED;
                        System.out.println("Соединение установлено");
                    }
                    break;

                case ESTABLISHED:
                    if (segmentContainsFlag(segment, TCPSegment.TCPFlags.FIN)) {
                        // FIN-ACK
                        TCPSegment finAck = new TCPSegment(
                            segment.destinationPort, segment.sourcePort);
                        finAck.setSequenceNumber(sequenceNumber);
                        finAck.setAcknowledgmentNumber(segment.sequenceNumber + 1);
                        finAck.setFlags(new TCPSegment.TCPFlags[]{
                            TCPSegment.TCPFlags.FIN, TCPSegment.TCPFlags.ACK});
                        responseSegments.add(finAck);

                        state = TCPState.LAST_ACK;
                        sequenceNumber++;
                        acknowledgmentNumber = segment.sequenceNumber + 1;

                        System.out.println("Отправлен FIN-ACK сегмент");
                    }
                    break;
            }

            return responseSegments;
        }

        private boolean segmentContainsFlag(TCPSegment segment, TCPSegment.TCPFlags flag) {
            // Упрощенная проверка
            return true; // В реальности проверяем биты флагов
        }

        public TCPState getState() { return state; }
    }

    /**
     * Демонстрация TCP протокола
     */
    public static void demonstrateTCP() {
        System.out.println("=== TCP протокол ===");

        // Клиент и сервер
        TCPConnectionManager client = new TCPConnectionManager();
        TCPConnectionManager server = new TCPConnectionManager();

        // Сервер слушает порт 80
        server.passiveOpen(80);

        // Клиент инициирует соединение
        List<TCPSegment> clientSegments = client.activeOpen(12345, 80);

        // Сервер получает SYN и отвечает SYN-ACK
        for (TCPSegment segment : clientSegments) {
            List<TCPSegment> serverResponse = server.processSegment(segment);
            // Клиент получает SYN-ACK и отвечает ACK
            for (TCPSegment response : serverResponse) {
                List<TCPSegment> clientAck = client.processSegment(response);
                // Сервер получает ACK
                for (TCPSegment ack : clientAck) {
                    server.processSegment(ack);
                }
            }
        }

        System.out.println("Клиент состояние: " + client.getState());
        System.out.println("Сервер состояние: " + server.getState());

        System.out.println("\nОсобенности TCP:");
        System.out.println("• Надежная доставка данных");
        System.out.println("• Управление потоком (flow control)");
        System.out.println("• Управление перегрузкой (congestion control)");
        System.out.println("• Упорядочивание сегментов");
        System.out.println("• Обнаружение и исправление ошибок");
    }
}
```

### **UDP** (**User `Datagram` Protocol**)

```java
/**
 * Реализация UDP протокола
 */
public class UDPProtocol {

    /**
     * UDP датаграмма
     */
    public static class UDPDatagram {
        private int sourcePort;
        private int destinationPort;
        private int length;
        private int checksum;
        private byte[] payload;

        public UDPDatagram(int sourcePort, int destPort, byte[] payload) {
            this.sourcePort = sourcePort;
            this.destinationPort = destPort;
            this.payload = payload.clone();
            this.length = 8 + payload.length;  // Заголовок 8 байт + данные
        }

        @Override
        public String toString() {
            return String.format("UDP Datagram: %d -> %d, Length=%d, Payload=%d bytes",
                               sourcePort, destinationPort, length, payload.length);
        }

        // Геттеры
        public int getSourcePort() { return sourcePort; }
        public int getDestinationPort() { return destinationPort; }
        public byte[] getPayload() { return payload.clone(); }
    }

    /**
     * UDP сокет
     */
    public static class UDPSocket {
        private int localPort;
        private Map<String, Queue<UDPDatagram>> receiveBuffers;
        private Random random;  // Для имитации потерь

        public UDPSocket(int port) {
            this.localPort = port;
            this.receiveBuffers = new HashMap<>();
            this.random = new Random();
        }

        /**
         * Отправка датаграммы
         */
        public void send(String destinationIP, int destPort, byte[] data) {
            UDPDatagram datagram = new UDPDatagram(localPort, destPort, data);
            String destinationKey = destinationIP + ":" + destPort;

            // Имитация отправки (в реальности через сеть)
            System.out.println("Отправлена " + datagram);

            // Имитация потери пакета (5% вероятность)
            if (random.nextInt(100) >= 5) {
                // Пакет доставлен успешно
                receiveBuffers.computeIfAbsent(destinationKey, k -> new LinkedList<>())
                             .offer(datagram);
            } else {
                System.out.println("Пакет потерян в сети!");
            }
        }

        /**
         * Получение датаграммы
         */
        public UDPDatagram receive() {
            // В реальности получаем от сети
            // Здесь имитируем получение из буфера
            for (Queue<UDPDatagram> buffer : receiveBuffers.values()) {
                if (!buffer.isEmpty()) {
                    return buffer.poll();
                }
            }
            return null;
        }

        /**
         * Привязка к адресу (для сервера)
         */
        public void bind(String address) {
            System.out.println("UDP сокет привязан к " + address + ":" + localPort);
        }
    }

    /**
     * Демонстрация UDP протокола
     */
    public static void demonstrateUDP() {
        System.out.println("=== UDP протокол ===");

        // Создание сокетов
        UDPSocket clientSocket = new UDPSocket(12345);
        UDPSocket serverSocket = new UDPSocket(53);  // DNS порт
        serverSocket.bind("127.0.0.1");

        // Клиент отправляет DNS запрос
        String dnsQuery = "www.example.com";
        clientSocket.send("127.0.0.1", 53, dnsQuery.getBytes());

        // Сервер получает запрос
        UDPDatagram received = serverSocket.receive();
        if (received != null) {
            String query = new String(received.getPayload());
            System.out.println("Сервер получил: " + query);

            // Сервер отправляет ответ
            String response = "93.184.216.34";  // IP адрес example.com
            serverSocket.send("127.0.0.1", received.getSourcePort(), response.getBytes());
        }

        // Клиент получает ответ
        UDPDatagram response = clientSocket.receive();
        if (response != null) {
            String ip = new String(response.getPayload());
            System.out.println("Клиент получил IP: " + ip);
        }

        System.out.println("\nОсобенности UDP:");
        System.out.println("• Ненадежная доставка (без гарантий)");
        System.out.println("• Без установления соединения");
        System.out.println("• Низкие накладные расходы");
        System.out.println("• Подходит для real-time приложений");
        System.out.println("• Широко используется для DNS, VoIP, онлайн-игр");
    }
}
```

## Протоколы прикладного уровня

**HTTP**, **DNS**, **SMTP** — популярные протоколы прикладного уровня.

### **HTTP** (**HyperText `Transfer` Protocol**)

```java
/**
 * Реализация HTTP протокола
 */
public class HTTPProtocol {

    /**
     * HTTP запрос
     */
    public static class HTTPRequest {
        private String method;
        private String path;
        private String version;
        private Map<String, String> headers;
        private String body;
        private Map<String, String> queryParams;

        public HTTPRequest(String method, String path) {
            this.method = method;
            this.path = path;
            this.version = "HTTP/1.1";
            this.headers = new HashMap<>();
            this.queryParams = new HashMap<>();
            parsePath(path);
        }

        private void parsePath(String path) {
            int queryIndex = path.indexOf('?');
            if (queryIndex != -1) {
                this.path = path.substring(0, queryIndex);
                String queryString = path.substring(queryIndex + 1);
                parseQueryString(queryString);
            } else {
                this.path = path;
            }
        }

        private void parseQueryString(String queryString) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        public void setBody(String body) {
            this.body = body;
            if (body != null) {
                headers.put("Content-Length", String.valueOf(body.getBytes().length));
            }
        }

        public String getQueryParam(String name) {
            return queryParams.get(name);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(method).append(" ").append(path).append(" ").append(version).append("\r\n");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }

            sb.append("\r\n");
            if (body != null) sb.append(body);

            return sb.toString();
        }
    }

    /**
     * HTTP ответ
     */
    public static class HTTPResponse {
        private String version;
        private int statusCode;
        private String statusMessage;
        private Map<String, String> headers;
        private String body;

        public HTTPResponse(int statusCode, String statusMessage) {
            this.version = "HTTP/1.1";
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.headers = new HashMap<>();
        }

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        public void setBody(String body) {
            this.body = body;
            if (body != null) {
                headers.put("Content-Length", String.valueOf(body.getBytes().length));
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(version).append(" ").append(statusCode).append(" ")
              .append(statusMessage).append("\r\n");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }

            sb.append("\r\n");
            if (body != null) sb.append(body);

            return sb.toString();
        }
    }

    /**
     * Простой HTTP сервер
     */
    public static class HTTPServer {
        private Map<String, String> routes;

        public HTTPServer() {
            this.routes = new HashMap<>();
        }

        public void addRoute(String path, String response) {
            routes.put(path, response);
        }

        public HTTPResponse handleRequest(HTTPRequest request) {
            String path = request.path;
            String responseBody = routes.get(path);

            if (responseBody != null) {
                HTTPResponse response = new HTTPResponse(200, "OK");
                response.addHeader("Content-Type", "text/html");
                response.setBody(responseBody);
                return response;
            } else {
                HTTPResponse response = new HTTPResponse(404, "Not Found");
                response.setBody("<h1>404 Not Found</h1>");
                return response;
            }
        }
    }

    /**
     * Демонстрация HTTP протокола
     */
    public static void demonstrateHTTP() {
        System.out.println("=== HTTP протокол ===");

        // Создание сервера
        HTTPServer server = new HTTPServer();
        server.addRoute("/", "<h1>Добро пожаловать!</h1>");
        server.addRoute("/about", "<h1>О нас</h1><p>Это простой HTTP сервер.</p>");
        server.addRoute("/api/users", "{\"users\": [{\"id\": 1, \"name\": \"Alice\"}]}");

        // Примеры запросов
        String[] requests = {
            "GET /",
            "GET /about",
            "GET /api/users",
            "GET /notfound"
        };

        for (String requestLine : requests) {
            String[] parts = requestLine.split(" ");
            HTTPRequest request = new HTTPRequest(parts[0], parts[1]);

            request.addHeader("Host", "localhost");
            request.addHeader("User-Agent", "Java HTTP Client");

            System.out.println("\n--- Запрос ---");
            System.out.println(request);

            HTTPResponse response = server.handleRequest(request);

            System.out.println("--- Ответ ---");
            System.out.println(response);
        }

        System.out.println("\nHTTP методы:");
        System.out.println("• GET - получение данных");
        System.out.println("• POST - создание данных");
        System.out.println("• PUT - обновление данных");
        System.out.println("• DELETE - удаление данных");
        System.out.println("• HEAD - получение заголовков");
        System.out.println("• OPTIONS - получение поддерживаемых методов");
    }
}
```

### **DNS** (**Domain `Name` System**)

```java
/**
 * Реализация DNS протокола
 */
public class DNSProtocol {

    /**
     * DNS сообщение
     */
    public static class DNSMessage {
        private int id;
        private DNSFlags flags;
        private List<DNSQuestion> questions;
        private List<DNSResourceRecord> answers;
        private List<DNSResourceRecord> authorities;
        private List<DNSResourceRecord> additional;

        public enum DNSFlags {
            QUERY, RESPONSE, RECURSION_DESIRED, RECURSION_AVAILABLE
        }

        public DNSMessage(int id) {
            this.id = id;
            this.questions = new ArrayList<>();
            this.answers = new ArrayList<>();
            this.authorities = new ArrayList<>();
            this.additional = new ArrayList<>();
        }

        public void addQuestion(String name, DNSType type, DNSClass dnsClass) {
            questions.add(new DNSQuestion(name, type, dnsClass));
        }

        public void addAnswer(DNSResourceRecord record) {
            answers.add(record);
        }
    }

    /**
     * DNS вопрос
     */
    public static class DNSQuestion {
        private String name;
        private DNSType type;
        private DNSClass dnsClass;

        public DNSQuestion(String name, DNSType type, DNSClass dnsClass) {
            this.name = name;
            this.type = type;
            this.dnsClass = dnsClass;
        }
    }

    /**
     * Типы DNS записей
     */
    public enum DNSType {
        A(1, "IPv4 адрес"),
        AAAA(28, "IPv6 адрес"),
        CNAME(5, "Каноническое имя"),
        MX(15, "Почтовый сервер"),
        NS(2, "Сервер имен"),
        PTR(12, "Обратное разрешение"),
        SOA(6, "Начало зоны авторитета"),
        TXT(16, "Текстовая запись");

        private final int value;
        private final String description;

        DNSType(int value, String description) {
            this.value = value;
            this.description = description;
        }
    }

    /**
     * DNS классы
     */
    public enum DNSClass {
        IN(1, "Internet"),
        CS(2, "CSNET"),
        CH(3, "CHAOS"),
        HS(4, "Hesiod");

        private final int value;
        private final String description;

        DNSClass(int value, String description) {
            this.value = value;
            this.description = description;
        }
    }

    /**
     * DNS ресурсная запись
     */
    public static class DNSResourceRecord {
        private String name;
        private DNSType type;
        private DNSClass dnsClass;
        private long ttl;  // Time To Live
        private String rdata;  // Resource Data

        public DNSResourceRecord(String name, DNSType type, DNSClass dnsClass, long ttl, String rdata) {
            this.name = name;
            this.type = type;
            this.dnsClass = dnsClass;
            this.ttl = ttl;
            this.rdata = rdata;
        }
    }

    /**
     * DNS кэш
     */
    public static class DNSCache {
        private Map<String, DNSResourceRecord> cache;
        private Map<String, Long> timestamps;

        public DNSCache() {
            this.cache = new HashMap<>();
            this.timestamps = new HashMap<>();
        }

        public DNSResourceRecord get(String name) {
            DNSResourceRecord record = cache.get(name);
            if (record != null) {
                long currentTime = System.currentTimeMillis() / 1000;
                long cacheTime = timestamps.get(name);

                if (currentTime - cacheTime > record.ttl) {
                    // Запись устарела
                    cache.remove(name);
                    timestamps.remove(name);
                    return null;
                }
            }
            return record;
        }

        public void put(DNSResourceRecord record) {
            cache.put(record.name, record);
            timestamps.put(record.name, System.currentTimeMillis() / 1000);
        }
    }

    /**
     * DNS клиент
     */
    public static class DNSClient {
        private DNSCache cache;
        private String serverIP;

        public DNSClient(String serverIP) {
            this.serverIP = serverIP;
            this.cache = new DNSCache();
        }

        public String resolve(String domainName) {
            // Проверяем кэш
            DNSResourceRecord cached = cache.get(domainName);
            if (cached != null) {
                System.out.println("Найдено в кэше: " + cached.rdata);
                return cached.rdata;
            }

            // Имитируем DNS запрос
            System.out.println("Отправка DNS запроса для: " + domainName);

            // Имитируем ответ от DNS сервера
            String ip = simulateDNSLookup(domainName);
            if (ip != null) {
                DNSResourceRecord record = new DNSResourceRecord(
                    domainName, DNSType.A, DNSClass.IN, 3600, ip);
                cache.put(record);
                return ip;
            }

            return null;
        }

        private String simulateDNSLookup(String domain) {
            // Имитируем DNS сервер
            Map<String, String> dnsTable = Map.of(
                "www.google.com", "142.250.184.68",
                "www.github.com", "140.82.121.4",
                "www.stackoverflow.com", "151.101.65.69",
                "localhost", "127.0.0.1"
            );

            return dnsTable.get(domain);
        }
    }

    /**
     * Демонстрация DNS протокола
     */
    public static void demonstrateDNS() {
        System.out.println("=== DNS протокол ===");

        DNSClient client = new DNSClient("8.8.8.8");

        String[] domains = {
            "www.google.com",
            "www.github.com",
            "localhost",
            "www.google.com"  // Повторный запрос - из кэша
        };

        for (String domain : domains) {
            String ip = client.resolve(domain);
            if (ip != null) {
                System.out.println(domain + " -> " + ip);
            } else {
                System.out.println("Не удалось разрешить: " + domain);
            }
            System.out.println();
        }

        System.out.println("Типы DNS записей:");
        for (DNSType type : DNSType.values()) {
            System.out.println(type.name() + " (" + type.value + "): " + type.description);
        }
    }
}
```

## Беспроводные сети

**Wi-Fi**, **Bluetooth**, мобильные сети.

### **Wi-Fi** сети

```java
/**
 * Концепции Wi-Fi сетей
 */
public class WiFiNetworks {

    /**
     * Стандарты Wi-Fi
     */
    public enum WiFiStandard {
        WIFI_802_11B("802.11b", 1999, 11, "2.4 GHz"),
        WIFI_802_11A("802.11a", 1999, 54, "5 GHz"),
        WIFI_802_11G("802.11g", 2003, 54, "2.4 GHz"),
        WIFI_802_11N("802.11n", 2009, 600, "2.4/5 GHz"),
        WIFI_802_11AC("802.11ac", 2013, 1300, "5 GHz"),
        WIFI_802_11AX("802.11ax", 2019, 9600, "2.4/5/6 GHz");

        private final String name;
        private final int year;
        private final int maxSpeed;  // Мбит/с
        private final String frequency;

        WiFiStandard(String name, int year, int maxSpeed, String frequency) {
            this.name = name;
            this.year = year;
            this.maxSpeed = maxSpeed;
            this.frequency = frequency;
        }

        public String getMaxSpeedFormatted() {
            if (maxSpeed >= 1000) {
                return (maxSpeed / 1000) + " Гбит/с";
            } else {
                return maxSpeed + " Мбит/с";
            }
        }
    }

    /**
     * Режимы безопасности Wi-Fi
     */
    public enum WiFiSecurity {
        WEP("WEP", "Устаревший, небезопасный"),
        WPA("WPA", "Улучшенная безопасность"),
        WPA2("WPA2", "Текущий стандарт"),
        WPA3("WPA3", "Самый безопасный");

        private final String name;
        private final String description;

        WiFiSecurity(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Wi-Fi точка доступа
     */
    public static class WiFiAccessPoint {
        private String ssid;
        private String bssid;
        private WiFiStandard standard;
        private WiFiSecurity security;
        private int channel;
        private int signalStrength;  // dBm

        public WiFiAccessPoint(String ssid, String bssid, WiFiStandard standard) {
            this.ssid = ssid;
            this.bssid = bssid;
            this.standard = standard;
            this.security = WiFiSecurity.WPA2;
            this.channel = 6;
            this.signalStrength = -50;
        }

        public void connect(String password) {
            // Имитация подключения
            if (isValidPassword(password)) {
                System.out.println("Подключено к " + ssid);
            } else {
                System.out.println("Неверный пароль для " + ssid);
            }
        }

        private boolean isValidPassword(String password) {
            // Упрощенная проверка
            return password != null && password.length() >= 8;
        }

        @Override
        public String toString() {
            return String.format("WiFi AP: %s (%s), Сигнал: %d dBm, Стандарт: %s",
                               ssid, bssid, signalStrength, standard.name);
        }
    }

    /**
     * Wi-Fi клиент
     */
    public static class WiFiClient {
        private String deviceName;
        private WiFiAccessPoint connectedAP;
        private Map<String, WiFiAccessPoint> knownNetworks;

        public WiFiClient(String deviceName) {
            this.deviceName = deviceName;
            this.knownNetworks = new HashMap<>();
        }

        /**
         * Сканирование доступных сетей
         */
        public List<WiFiAccessPoint> scanNetworks() {
            // Имитация сканирования
            List<WiFiAccessPoint> networks = new ArrayList<>();

            networks.add(new WiFiAccessPoint("MyHomeWiFi", "00:11:22:33:44:55", WiFiStandard.WIFI_802_11AC));
            networks.add(new WiFiAccessPoint("CoffeeShop", "AA:BB:CC:DD:EE:FF", WiFiStandard.WIFI_802_11N));
            networks.add(new WiFiAccessPoint("GuestNetwork", "11:22:33:44:55:66", WiFiStandard.WIFI_802_11AX));

            return networks;
        }

        /**
         * Подключение к сети
         */
        public boolean connectToNetwork(WiFiAccessPoint ap, String password) {
            if (knownNetworks.containsKey(ap.ssid)) {
                // Известная сеть - используем сохраненный пароль
                ap.connect(knownNetworks.get(ap.ssid).toString());  // Упрощенно
                connectedAP = ap;
                return true;
            } else if (password != null) {
                // Новая сеть - пробуем подключиться с паролем
                ap.connect(password);
                connectedAP = ap;
                knownNetworks.put(ap.ssid, ap);
                return true;
            }

            return false;
        }

        /**
         * Отключение от сети
         */
        public void disconnect() {
            if (connectedAP != null) {
                System.out.println("Отключено от " + connectedAP.ssid);
                connectedAP = null;
            }
        }
    }

    /**
     * Демонстрация Wi-Fi сетей
     */
    public static void demonstrateWiFi() {
        System.out.println("=== Wi-Fi сети ===");

        // Создание клиента
        WiFiClient client = new WiFiClient("MyLaptop");

        // Сканирование сетей
        System.out.println("Сканирование доступных сетей:");
        List<WiFiAccessPoint> networks = client.scanNetworks();
        for (WiFiAccessPoint ap : networks) {
            System.out.println(ap);
        }

        // Подключение к сети
        WiFiAccessPoint targetNetwork = networks.get(0);
        boolean connected = client.connectToNetwork(targetNetwork, "mypassword123");

        if (connected) {
            System.out.println("Успешно подключено к " + targetNetwork.ssid);
        }

        // Отключение
        client.disconnect();

        System.out.println("\nЭволюция Wi-Fi стандартов:");
        for (WiFiStandard standard : WiFiStandard.values()) {
            System.out.printf("%s (%d): до %s на %s%n",
                            standard.name, standard.year,
                            standard.getMaxSpeedFormatted(), standard.frequency);
        }
    }
}
```

## Сетевая безопасность

Основные концепции сетевой безопасности.

### **Firewall** и **NAT**

```java
/**
 * Реализация firewall и NAT
 */
public class NetworkSecurity {

    /**
     * Правило firewall
     */
    public static class FirewallRule {
        private String sourceIP;
        private String destinationIP;
        private int sourcePort;
        private int destinationPort;
        private String protocol;
        private FirewallAction action;

        public enum FirewallAction {
            ALLOW, DENY, DROP
        }

        public FirewallRule(String sourceIP, String destIP, int sourcePort,
                           int destPort, String protocol, FirewallAction action) {
            this.sourceIP = sourceIP;
            this.destinationIP = destIP;
            this.sourcePort = sourcePort;
            this.destinationPort = destPort;
            this.protocol = protocol;
            this.action = action;
        }

        /**
         * Проверка соответствия пакета правилу
         */
        public boolean matches(String srcIP, String dstIP, int srcPort, int dstPort, String proto) {
            return (sourceIP.equals("*") || sourceIP.equals(srcIP)) &&
                   (destinationIP.equals("*") || destinationIP.equals(dstIP)) &&
                   (sourcePort == -1 || sourcePort == srcPort) &&
                   (destinationPort == -1 || destinationPort == dstPort) &&
                   (protocol.equals("*") || protocol.equals(proto));
        }

        public FirewallAction getAction() { return action; }

        @Override
        public String toString() {
            return String.format("%s: %s:%d -> %s:%d (%s)",
                               action, sourceIP, sourcePort, destinationIP, destinationPort, protocol);
        }
    }

    /**
     * Firewall
     */
    public static class Firewall {
        private List<FirewallRule> rules;

        public Firewall() {
            this.rules = new ArrayList<>();
            setupDefaultRules();
        }

        private void setupDefaultRules() {
            // Правило по умолчанию: запретить все
            rules.add(new FirewallRule("*", "*", -1, -1, "*", FirewallRule.FirewallAction.DENY));
        }

        public void addRule(FirewallRule rule) {
            // Добавляем перед правилом по умолчанию
            rules.add(rules.size() - 1, rule);
        }

        /**
         * Проверка пакета на соответствие правилам
         */
        public FirewallRule.FirewallAction checkPacket(String srcIP, String dstIP,
                                                      int srcPort, int dstPort, String protocol) {
            for (FirewallRule rule : rules) {
                if (rule.matches(srcIP, dstIP, srcPort, dstPort, protocol)) {
                    return rule.getAction();
                }
            }

            return FirewallRule.FirewallAction.DENY;  // Правило по умолчанию
        }

        public void displayRules() {
            System.out.println("Правила firewall:");
            for (int i = 0; i < rules.size(); i++) {
                System.out.println((i + 1) + ". " + rules.get(i));
            }
        }
    }

    /**
     * NAT (Network Address Translation)
     */
    public static class NAT {
        private String publicIP;
        private Map<String, String> translationTable;  // privateIP -> publicIP:port

        public NAT(String publicIP) {
            this.publicIP = publicIP;
            this.translationTable = new HashMap<>();
        }

        /**
         * Исходящий NAT (SNAT)
         */
        public String translateOutbound(String privateIP, int privatePort) {
            String key = privateIP + ":" + privatePort;
            String publicMapping = translationTable.get(key);

            if (publicMapping == null) {
                // Создаем новое сопоставление
                int publicPort = 1024 + translationTable.size();  // Упрощенная генерация порта
                publicMapping = publicIP + ":" + publicPort;
                translationTable.put(key, publicMapping);
                System.out.println("Создано NAT сопоставление: " + key + " -> " + publicMapping);
            }

            return publicMapping;
        }

        /**
         * Входящий NAT (DNAT)
         */
        public String translateInbound(String publicIP, int publicPort) {
            String publicMapping = publicIP + ":" + publicPort;

            for (Map.Entry<String, String> entry : translationTable.entrySet()) {
                if (entry.getValue().equals(publicMapping)) {
                    return entry.getKey();
                }
            }

            return null;  // Нет сопоставления
        }

        public void displayNATTable() {
            System.out.println("NAT таблица:");
            translationTable.forEach((privateAddr, publicAddr) ->
                System.out.println(privateAddr + " -> " + publicAddr)
            );
        }
    }

    /**
     * Демонстрация сетевой безопасности
     */
    public static void demonstrateSecurity() {
        System.out.println("=== Сетевая безопасность ===");

        // Firewall
        Firewall firewall = new Firewall();

        // Добавляем правила
        firewall.addRule(new FirewallRule("192.168.1.*", "*", -1, 80, "TCP", FirewallRule.FirewallAction.ALLOW));
        firewall.addRule(new FirewallRule("192.168.1.*", "*", -1, 443, "TCP", FirewallRule.FirewallAction.ALLOW));
        firewall.addRule(new FirewallRule("*", "*", -1, 22, "*", FirewallRule.FirewallAction.DENY));

        System.out.println("Тестирование firewall:");
        firewall.displayRules();

        // Тестовые пакеты
        String[][] testPackets = {
            {"192.168.1.100", "93.184.216.34", 12345, 80, "TCP"},   // HTTP - разрешен
            {"192.168.1.100", "93.184.216.34", 12345, 443, "TCP"},  // HTTPS - разрешен
            {"10.0.0.1", "192.168.1.100", 12345, 22, "TCP"},        // SSH - запрещен
            {"192.168.1.100", "8.8.8.8", 12345, 53, "UDP"}          // DNS - запрещен (правило по умолчанию)
        };

        for (String[] packet : testPackets) {
            FirewallRule.FirewallAction action = firewall.checkPacket(
                packet[0], packet[1], Integer.parseInt(packet[2]),
                Integer.parseInt(packet[3]), packet[4]
            );
            System.out.printf("Пакет %s:%s -> %s:%s (%s): %s%n",
                            packet[0], packet[2], packet[1], packet[3], packet[4], action);
        }

        System.out.println("\n=== NAT ===");
        NAT nat = new NAT("203.0.113.1");

        // Исходящие соединения
        String mapping1 = nat.translateOutbound("192.168.1.10", 8080);
        String mapping2 = nat.translateOutbound("192.168.1.20", 9090);

        nat.displayNATTable();

        // Входящий трафик
        String privateAddr1 = nat.translateInbound("203.0.113.1", 1024);
        String privateAddr2 = nat.translateInbound("203.0.113.1", 1025);

        System.out.println("\nВходящий трафик:");
        System.out.println("203.0.113.1:1024 -> " + privateAddr1);
        System.out.println("203.0.113.1:1025 -> " + privateAddr2);
    }
}
```


## Решение проблем

- **Таймауты / медленный ответ** — проверить сеть (traceroute, ping), размер окна TCP, таймауты на стороне клиента и сервера; см. разделы по TCP и HTTP.
- **Ошибки подключения (connection refused / timeout)** — проверить, слушает ли сервер нужный порт; firewall; DNS и доступность хоста.
- **TLS/сертификаты** — проверить цепочку сертификатов, срок действия, соответствие имени хосту; см. раздел по HTTPS.

## Частые вопросы

- **Чем TCP отличается от UDP?** TCP — надёжная доставка, установка соединения, порядок; UDP — датаграммы без гарантий, меньше накладных расходов.
- **Что такое модель OSI?** Семь уровней (прикладной — физический); описание разделения ответственности протоколов; на практике часто используют стек TCP/IP (4 уровня).
- **См. также:** разделы «Стек TCP/IP», «HTTP и HTTPS», «DNS» и «Полезные ссылки» в документе.


## Заключение

Компьютерные сети — это фундамент современной цифровой инфраструктуры. Понимание принципов работы сетей, протоколов и механизмов безопасности критически важно для разработчиков и системных администраторов.

### Ключевые концепции

1. **Модель OSI**: 7 уровней абстракции сетевых функций
2. **TCP/`IP` стек**: Реальная реализация сетевых протоколов
3. **IP адресация**: Система адресации устройств в сети
4. **Протоколы транспортного уровня**: **TCP** для надежности, **UDP** для скорости
5. **Протоколы прикладного уровня**: **HTTP**, **DNS**, **SMTP**
6. **Беспроводные сети**: **Wi-Fi**, мобильные сети
7. **Сетевая безопасность**: **Firewall**, **NAT**, шифрование

### Практические рекомендации

- **Изучайте `RFC` документы**: Спецификации сетевых протоколов
- **Практикуйте настройку**: Создавайте собственные сети для экспериментов
- **Используйте инструменты**: **Wireshark** для анализа трафика, **tcpdump** для мониторинга
- **Изучайте безопасность**: Понимание уязвимостей и защитных механизмов
- **Следите за стандартами**: Новые версии протоколов и технологий

### Будущие тенденции

- **IPv6 массовое внедрение**: Исчерпание **IPv4** адресов
- **Software-`Defined Networking` (**SDN**)**: Программно-управляемые сети
- **5G и IoT**: Высокоскоростные беспроводные сети
- **Квантовая безопасность**: Новые методы шифрования
- **Edge Computing**: Обработка данных на границе сети

Компьютерные сети продолжают эволюционировать, и понимание фундаментальных принципов остается ключом к работе с современными сетевыми технологиями!
