---
title: "Redis: Безопасность"
description: "Полное руководство по безопасности Redis: аутентификация, авторизация, ACL, SSL/TLS, шифрование, best practices"
tags:
  - redis
  - security
  - authentication
  - authorization
  - acl
  - ssl
  - tls
  - encryption
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md"]
next: ["databases/redis-monitoring.md", "databases/redis-troubleshooting.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-performance.md"]
---

# Redis: Безопасность

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Security](https://redis.io/docs/management/security/) — безопасность

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-basics|redis-basics.md]] — основы и администрирование

## Содержание

- [Введение в безопасность **Redis**](#введение-в-безопасность-redis)
  - [Основные аспекты безопасности](#основные-аспекты-безопасности)
- [Аутентификация](#аутентификация)
  - [Простая аутентификация (Password)](#простая-аутентификация-password)
  - [Генерация безопасных паролей](#генерация-безопасных-паролей)
- [**ACL** (Access `Control` List)](#acl-access-control-list)
  - [Создание пользователей](#создание-пользователей)
  - [Категории команд](#категории-команд)
  - [Управление пользователями](#управление-пользователями)
  - [Паттерны ключей](#паттерны-ключей)
- [**SSL**/**TLS**](#ssl-tls)
  - [Настройка **SSL**/**TLS** на сервере](#настройка-ssl-tls-на-сервере)
  - [Подключение с **SSL**/**TLS**](#подключение-с-ssl-tls)
- [Ограничение доступа](#ограничение-доступа)
  - [**Bind** и **Protected Mode**](#bind-и-protected-mode)
  - [**Firewall**](#firewall)
- [Отключение опасных команд](#отключение-опасных-команд)
  - [**Rename Commands**](#rename-commands)
  - [Через **ACL**](#через-acl)
- [Шифрование данных](#шифрование-данных)
  - [Шифрование на уровне приложения](#шифрование-на-уровне-приложения)
- [Аудит и мониторинг](#аудит-и-мониторинг)
  - [Логирование доступа](#логирование-доступа)
  - [Мониторинг подозрительной активности](#мониторинг-подозрительной-активности)
- [Лучшие практики](#лучшие-практики)
  - [Конфигурация](#конфигурация)
  - [Мониторинг](#мониторинг)
  - [Обновления](#обновления)
- [**Advanced Security Configuration**](#advanced-security-configuration)
  - [**ACL Best Practices**](#acl-best-practices)
  - [**SSL**/**TLS Configuration**](#ssl-tls-configuration)
  - [**Network Security**](#network-security)
- [**Security Monitoring**](#security-monitoring)
  - [**Audit Logging**](#audit-logging)
  - [**Intrusion Detection**](#intrusion-detection)
- [**Encryption** at **Rest**](#encryption-at-rest)
  - [**Application-Level Encryption**](#application-level-encryption)
- [**Security Hardening Checklist**](#security-hardening-checklist)
  - [**Configuration Hardening**](#configuration-hardening)
  - [**Network Hardening**](#network-hardening)
  - [**Monitoring and Alerting**](#monitoring-and-alerting)
- [**Advanced Security Configuration**](#advanced-security-configuration-1)
  - [**ACL Best Practices**](#acl-best-practices-1)
  - [**SSL**/**TLS Configuration**](#ssl-tls-configuration-1)
  - [**Network Security**](#network-security-1)
- [**Security Monitoring**](#security-monitoring-1)
  - [**Audit Logging**](#audit-logging-1)
  - [**Intrusion Detection**](#intrusion-detection-1)
- [**Encryption** at **Rest**](#encryption-at-rest-1)
  - [**Application-Level Encryption**](#application-level-encryption-1)

## Введение в безопасность Redis

Безопасность **Redis** критически важна, особенно при развертывании в **production**. По умолчанию **Redis** не имеет встроенной аутентификации, что делает его уязвимым для несанкционированного доступа.

### Основные аспекты безопасности

1. **Аутентификация**: Проверка подлинности пользователей
2. **Авторизация**: Контроль доступа к командам и данным
3. **Шифрование**: Защита данных при передаче
4. **Изоляция**: Ограничение доступа к серверу
5. **Аудит**: Логирование и мониторинг доступа


## Аутентификация

### Простая аутентификация (Password)

```conf
# В redis.conf
requirepass mysecurepassword

# Через CONFIG SET
CONFIG SET requirepass mysecurepassword

# Подключение с паролем
redis-cli -a mysecurepassword

# Или через AUTH команду
redis-cli
AUTH mysecurepassword
```

### Генерация безопасных паролей

```bash
# Генерация случайного пароля
openssl rand -base64 32

# Или через Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```


## ACL (Access `Control` List)

**ACL** позволяет создавать пользователей с различными правами доступа.

### Создание пользователей

```redis
# Создать пользователя с паролем
ACL SETUSER alice on >password123 ~* +@all

# Создать пользователя только для чтения
ACL SETUSER readonly on >readpass ~* +@read +@keyspace

# Создать пользователя для записи
ACL SETUSER writeuser on >writepass ~* +@write +@read

# Создать администратора
ACL SETUSER admin on >adminpass ~* +@all
```

### Категории команд

```redis
# Просмотр категорий
ACL CAT

# Команды в категории
ACL CAT read
ACL CAT write
ACL CAT admin
ACL CAT dangerous
ACL CAT keyspace
ACL CAT string
ACL CAT list
ACL CAT set
ACL CAT sortedset
ACL CAT hash
ACL CAT hyperloglog
ACL CAT stream
ACL CAT bitmap
ACL CAT geo
ACL CAT pubsub
ACL CAT transaction
ACL CAT connection
ACL CAT scripting
```

### Управление пользователями

```redis
# Список пользователей
ACL LIST

# Информация о пользователе
ACL GETUSER alice

# Удаление пользователя
ACL DELUSER alice

# Сохранение ACL в конфигурацию
ACL SAVE

# Загрузка ACL из конфигурации
ACL LOAD
```

### Паттерны ключей

```redis
# Разрешить доступ к определенным ключам
ACL SETUSER user1 on >pass ~user:* ~order:*

# Запретить доступ к определенным ключам
ACL SETUSER user1 on >pass ~* -@all +@read ~user:* -~admin:*

# Использование шаблонов
ACL SETUSER user1 on >pass ~{user}:* ~{order}:*
```


## SSL/TLS

### Настройка SSL/TLS на сервере

```conf
# В redis.conf
port 0
tls-port 6380
tls-cert-file /path/to/redis.crt
tls-key-file /path/to/redis.key
tls-ca-cert-file /path/to/ca.crt
tls-ca-cert-dir /path/to/certs/
tls-protocols "TLSv1.2 TLSv1.3"
tls-ciphers "DEFAULT:!EXPORT:!RC4:!DES:!LOW:!SSLv2:!SSLv3"
tls-prefer-server-ciphers yes
tls-session-caching yes
tls-session-cache-size 20480
tls-session-cache-timeout 60
```

### Подключение с SSL/TLS

```bash
# Через redis-cli
redis-cli --tls --cert /path/to/client.crt --key /path/to/client.key --cacert /path/to/ca.crt -p 6380

# Или через Python
import redis
r = redis.Redis(
    host='localhost',
    port=6380,
    ssl=True,
    ssl_cert_reqs='required',
    ssl_ca_certs='/path/to/ca.crt',
    ssl_certfile='/path/to/client.crt',
    ssl_keyfile='/path/to/client.key'
)
```


## Ограничение доступа

### Bind и Protected Mode

```conf
# Привязка к определенным интерфейсам
bind 127.0.0.1 ::1

# Protected mode (только для localhost по умолчанию)
protected-mode yes
```

### Firewall

```bash
# Ограничение доступа через firewall
# iptables
iptables -A INPUT -p tcp --dport 6379 -s 192.168.1.0/24 -j ACCEPT
iptables -A INPUT -p tcp --dport 6379 -j DROP

# firewalld
firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="192.168.1.0/24" port protocol="tcp" port="6379" accept'
firewall-cmd --reload
```


## Отключение опасных команд

### Rename Commands

```conf
# Отключить опасные команды
rename-command FLUSHDB ""
rename-command FLUSHALL ""
rename-command CONFIG ""

# Или переименовать с паролем
rename-command CONFIG "CONFIG_9cb4d4c8b5a1be8a5b2f3c4d5e6f7a8b9"
```

### Через ACL

```redis
# Запретить команды через ACL
ACL SETUSER user1 on >pass ~* +@all -FLUSHDB -FLUSHALL -CONFIG
```


## Шифрование данных

### Шифрование на уровне приложения

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptedRedis {
    private JedisPool jedisPool;
    private SecretKey secretKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public EncryptedRedis(JedisPool jedisPool, byte[] keyBytes) throws Exception {
        this.jedisPool = jedisPool;
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
        this.encryptCipher = Cipher.getInstance("AES");
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        this.decryptCipher = Cipher.getInstance("AES");
        this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
    }

    public void set(String key, String value) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] encrypted = encryptCipher.doFinal(value.getBytes("UTF-8"));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            jedis.set(key, encryptedBase64);
        }
    }

    public String get(String key) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String encryptedBase64 = jedis.get(key);
            if (encryptedBase64 != null) {
                byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);
                byte[] decrypted = decryptCipher.doFinal(encrypted);
                return new String(decrypted, "UTF-8");
            }
            return null;
        }
    }
}
```


## Аудит и мониторинг

### Логирование доступа

```conf
# Включить логирование
loglevel notice
logfile /var/log/redis/redis-server.log

# Логирование команд
# Можно использовать MONITOR (только для отладки)
```

### Мониторинг подозрительной активности

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.HashMap;
import java.util.Map;

public class SecurityMonitor {
    private JedisPool jedisPool;
    private Map<String, Integer> failedAuths;
    private Map<String, Integer> commandCounts;

    public SecurityMonitor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.failedAuths = new HashMap<>();
        this.commandCounts = new HashMap<>();
    }

    public void monitorAuthFailures() {
        // В реальности это требует кастомной логики
        // или использования Redis модулей
    }

    public void checkSuspiciousCommands() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> commandstats = jedis.info("commandstats");
            for (Map.Entry<String, String> entry : commandstats.entrySet()) {
                String cmd = entry.getKey();
                if (cmd.contains("FLUSH") || cmd.contains("CONFIG")) {
                    System.out.println("WARNING: Suspicious command " + cmd + " used");
                }
            }
        }
    }
}
```


## Лучшие практики

### Конфигурация

1. **Используйте сильные пароли** или **ACL**
2. **Ограничьте доступ** через **bind** и **firewall**
3. **Используйте `SSL`/TLS** для удаленных подключений
4. **Отключите опасные команды** через **rename-command**
5. **Включите protected-mode** для **production**

### Мониторинг

1. **Мониторьте неудачные попытки аутентификации**
2. **Отслеживайте использование опасных команд**
3. **Проверяйте логи** регулярно
4. **Настройте алерты** на подозрительную активность
5. **Регулярно обновляйте Redis**

### Обновления

1. **Регулярно обновляйте Redis** до последней версии
2. **Следите за security advisories**
3. **Тестируйте обновления** перед применением
4. **Имейте план отката** на случай проблем

## Advanced Security Configuration

### ACL Best Practices

```redis
# Создание пользователей с минимальными правами
# Принцип наименьших привилегий

# Пользователь только для чтения определенных ключей
ACL SETUSER reader on >readpass ~user:* ~order:* +@read +@keyspace

# Пользователь для записи в определенные ключи
ACL SETUSER writer on >writepass ~user:* ~order:* +@write +@read +@keyspace

# Пользователь для административных задач
ACL SETUSER admin on >adminpass ~* +@all -FLUSHDB -FLUSHALL -CONFIG
```

### SSL/TLS Configuration

```conf
# Полная конфигурация SSL/TLS
tls-port 6380
tls-cert-file /etc/redis/ssl/redis.crt
tls-key-file /etc/redis/ssl/redis.key
tls-ca-cert-file /etc/redis/ssl/ca.crt
tls-ca-cert-dir /etc/redis/ssl/certs/
tls-protocols "TLSv1.2 TLSv1.3"
tls-ciphers "ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256"
tls-prefer-server-ciphers yes
tls-session-caching yes
tls-session-cache-size 20480
tls-session-cache-timeout 60
```

### Network Security

```bash
# Настройка firewall для Redis
# Разрешить доступ только с определенных IP
iptables -A INPUT -p tcp --dport 6379 -s 192.168.1.0/24 -j ACCEPT
iptables -A INPUT -p tcp --dport 6379 -j DROP

# Использование VPN для доступа
# Настройка Redis только на внутренней сети
bind 10.0.0.10
```

## Security Monitoring

### Audit Logging

```java
// Java пример audit logging
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SecurityAuditor {
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    public SecurityAuditor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.objectMapper = new ObjectMapper();
    }

    public void logAccess(String user, String command, String key, String result) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", Instant.now().toString());
        logEntry.put("user", user);
        logEntry.put("command", command);
        logEntry.put("key", key);
        logEntry.put("result", result);

        // Сохранить в файл
        try (FileWriter writer = new FileWriter("/var/log/redis/audit.log", true)) {
            writer.write(objectMapper.writeValueAsString(logEntry) + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    public void detectAnomalies() {
        // Проверить частые неудачные попытки аутентификации
        // Проверить использование опасных команд
        // Проверить доступ к чувствительным ключам
        // Реализация обнаружения аномалий
    }
}
```

### Intrusion Detection

```bash
#!/bin/bash
# intrusion_detection.sh

# Мониторинг неудачных попыток подключения
tail -f /var/log/redis/redis-server.log | grep -i "auth\|failed\|denied"

# Проверка подозрительной активности
redis-cli MONITOR | grep -E "FLUSH|CONFIG|DEBUG|SHUTDOWN"
```

## Encryption at Rest

### Application-Level Encryption

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.util.Base64;

public class EncryptedRedisStorage {
    private JedisPool jedisPool;
    private SecretKeySpec secretKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public EncryptedRedisStorage(JedisPool jedisPool, String password) throws Exception {
        this.jedisPool = jedisPool;

        // Генерация ключа из пароля
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        this.secretKey = new SecretKeySpec(key, "AES");

        this.encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

        this.decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
    }

    public void set(String key, String value) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] encrypted = encryptCipher.doFinal(value.getBytes("UTF-8"));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            jedis.set(key, encryptedBase64);
        }
    }

    public String get(String key) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String encryptedBase64 = jedis.get(key);
            if (encryptedBase64 != null) {
                byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);
                byte[] decrypted = decryptCipher.doFinal(encrypted);
                return new String(decrypted, "UTF-8");
            }
            return null;
        }
    }
}
```

## Security Hardening Checklist

### Configuration Hardening

- [ ] Установлен сильный пароль (requirepass)
- [ ] Настроен **ACL** для пользователей
- [ ] Отключены опасные команды (FLUSHDB, `FLUSHALL`, CONFIG)
- [ ] Включен **protected-mode**
- [ ] Ограничен доступ через **bind**
- [ ] Настроен **firewall**
- [ ] Включен **SSL**/**TLS** для удаленных подключений
- [ ] Настроено логирование
- [ ] Регулярно обновляется **Redis**

### Network Hardening

- [ ] **Redis** доступен только из внутренней сети
- [ ] Используется **VPN** для удаленного доступа
- [ ] Настроен **firewall** для ограничения доступа
- [ ] Используется **reverse proxy** с аутентификацией
- [ ] Мониторится сетевой трафик

### Monitoring and Alerting

- [ ] Настроен мониторинг неудачных попыток аутентификации
- [ ] Отслеживается использование опасных команд
- [ ] Настроены алерты на подозрительную активность
- [ ] Регулярно проверяются логи
- [ ] Настроен аудит доступа

## Advanced Security Configuration

### ACL Best Practices

```redis
# Создание пользователей с минимальными правами
# Принцип наименьших привилегий

# Пользователь только для чтения определенных ключей
ACL SETUSER reader on >readpass ~user:* ~order:* +@read +@keyspace

# Пользователь для записи в определенные ключи
ACL SETUSER writer on >writepass ~user:* ~order:* +@write +@read +@keyspace

# Пользователь для административных задач
ACL SETUSER admin on >adminpass ~* +@all -FLUSHDB -FLUSHALL -CONFIG
```

### SSL/TLS Configuration

```conf
# Полная конфигурация SSL/TLS
tls-port 6380
tls-cert-file /etc/redis/ssl/redis.crt
tls-key-file /etc/redis/ssl/redis.key
tls-ca-cert-file /etc/redis/ssl/ca.crt
tls-ca-cert-dir /etc/redis/ssl/certs/
tls-protocols "TLSv1.2 TLSv1.3"
tls-ciphers "ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256"
tls-prefer-server-ciphers yes
tls-session-caching yes
tls-session-cache-size 20480
tls-session-cache-timeout 60
```

### Network Security

```bash
# Настройка firewall для Redis
# Разрешить доступ только с определенных IP
iptables -A INPUT -p tcp --dport 6379 -s 192.168.1.0/24 -j ACCEPT
iptables -A INPUT -p tcp --dport 6379 -j DROP

# Использование VPN для доступа
# Настройка Redis только на внутренней сети
bind 10.0.0.10
```

## Security Monitoring

### Audit Logging

```java
// Java пример audit logging
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SecurityAuditor {
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    public SecurityAuditor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.objectMapper = new ObjectMapper();
    }

    public void logAccess(String user, String command, String key, String result) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", Instant.now().toString());
        logEntry.put("user", user);
        logEntry.put("command", command);
        logEntry.put("key", key);
        logEntry.put("result", result);

        // Сохранить в файл
        try (FileWriter writer = new FileWriter("/var/log/redis/audit.log", true)) {
            writer.write(objectMapper.writeValueAsString(logEntry) + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    public void detectAnomalies() {
        // Проверить частые неудачные попытки аутентификации
        // Проверить использование опасных команд
        // Проверить доступ к чувствительным ключам
        // Реализация обнаружения аномалий
    }
}
```

### Intrusion Detection

```bash
#!/bin/bash
# intrusion_detection.sh

# Мониторинг неудачных попыток подключения
tail -f /var/log/redis/redis-server.log | grep -i "auth\|failed\|denied"

# Проверка подозрительной активности
redis-cli MONITOR | grep -E "FLUSH|CONFIG|DEBUG|SHUTDOWN"
```

## Encryption at Rest

### Application-Level Encryption

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.util.Base64;

public class EncryptedRedisStorage {
    private JedisPool jedisPool;
    private SecretKeySpec secretKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public EncryptedRedisStorage(JedisPool jedisPool, String password) throws Exception {
        this.jedisPool = jedisPool;

        // Генерация ключа из пароля
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        this.secretKey = new SecretKeySpec(key, "AES");

        this.encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

        this.decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
    }

    public void set(String key, String value) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] encrypted = encryptCipher.doFinal(value.getBytes("UTF-8"));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            jedis.set(key, encryptedBase64);
        }
    }

    public String get(String key) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String encryptedBase64 = jedis.get(key);
            if (encryptedBase64 != null) {
                byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);
                byte[] decrypted = decryptCipher.doFinal(encrypted);
                return new String(decrypted, "UTF-8");
            }
            return null;
        }
    }
}
```


- [Redis Security](https://redis.io/docs/management/security/)
- [Redis ACL](https://redis.io/docs/management/security/)

