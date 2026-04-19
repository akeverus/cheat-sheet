---
title: "Docker и Spring Boot"
description: "Полный перенос раздела Docker and Spring Boot: контейнеризация приложения, сборка образов (Dockerfile, buildpacks, многослойные JAR), доступ к логам, запуск с PostgreSQL через Compose, повторное использование слоёв Docker и кастомизация слоёв. Без сокращений."
tags:
  - platform
  - containers
  - docker-spring-boot
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Docker** и **Spring Boot**

Полный перенос раздела **Docker and Spring Boot**: контейнеризация приложения, сборка образов (**Dockerfile, buildpacks, многослойные JAR**), доступ к логам, запуск с **PostgreSQL** через **Compose**, повторное использование слоёв **Docker** и кастомизация слоёв. Без сокращений.



## Полезные ссылки

### Официальная документация

- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)

### **Baeldung**

- [Docker Tutorial](https://www.baeldung.com/ops/docker-guide)


## Содержание

- [Контейнеризация приложения Spring Boot](#контейнеризация-приложения-spring-boot)
- [Создание образов с помощью Spring Boot](#создание-образов-с-помощью-spring-boot)
- [Доступ к журналам Spring Boot](#доступ-к-журналам-spring-boot)
- [Запуск Spring Boot с PostgreSQL в Docker Compose](#запуск-spring-boot-с-postgresql-в-docker-compose)
- [Повторное использование слоев Docker с Spring Boot](#повторное-использование-слоев-docker-с-spring-boot)

## Контейнеризация приложения **Spring Boot**

В этом руководстве мы сосредоточимся на том, как докеризировать приложение **Spring Boot**, чтобы запустить его в изолированной среде, также известной как контейнер.

Мы узнаем, как создать композицию контейнеров, которые зависят друг от друга и связаны друг с другом в виртуальной частной сети. Мы также увидим, как ими можно управлять вместе с помощью отдельных команд.

Давайте начнем с создания простого приложения **Spring Boot**, которое мы затем запустим в облегченном базовом образе под управлением **Alpine Linux**.

В качестве примера приложения, которое мы можем докеризовать, мы создадим простое приложение **Spring Boot**, **docker-message-server**, которое предоставляет одну конечную точку и возвращает статическое сообщение.

Ниже — пример контроллера **Spring Boot** для докеризации (**Java**).
```java
@RestController
public class DockerMessageController {
    @GetMapping("/messages")
    public String getMessage() {
        return "Hello from Docker!";
    }
}
```

**С правильно настроенным файлом **Maven** мы можем создать исполняемый файл **jar**:**

```bash
mvn clean package
```

**Далее мы запустим приложение **Spring Boot**:**

```bash
java -jar target/docker-message-server-1.0.0.jar
```

Теперь у нас есть работающее приложение **Spring Boot**, к которому мы можем получить доступ по адресу http://localhost:8887.

**Чтобы докеризировать приложение, мы сначала создаем файл с именем **Dockerfile** со следующим содержимым:**

```dockerfile
FROM openjdk:8-jdk-alpine
MAINTAINER baeldung.com
COPY target/docker-message-server-1.0.0.jar message-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]
```

**Этот файл содержит следующую информацию:**
- **FROM**: в качестве основы для нашего образа берем **Alpine Linux** с поддержкой **Java**.
- **MAINTAINER**: хранитель образа.
- **COPY**: копируем **jar**-файл в образ.
- **ENTRYPOINT**: исполняемый файл, запускаемый при загрузке контейнера. Определяем как **JSON-Array**, т.к. будем использовать **ENTRYPOINT** вместе с **CMD**.

**Чтобы создать образ из **Dockerfile**:**

```bash
docker build --tag=message-server:latest .
```

**Запуск контейнера:**

```bash
docker run -p8887:8888 message-server:latest
```

Это запустит приложение в **Docker**, доступ по http://localhost:8887. Важно сопоставить порт на хосте (`8887`) с портом внутри **Docker** (`8888`), указанным в приложении.

**Если порт `8887` занят, выбрать другой. Если запустили контейнер в фоне, детали/остановка/удаление:**

```bash
docker inspect message-server
docker stop message-server
docker rm message-server
```

**Можно менять базовый образ, например **Corretto**:**

```dockerfile
FROM amazoncorretto:11-alpine-jdk
MAINTAINER baeldung.com
COPY target/docker-message-server-1.0.0.jar message-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]
```

Команды **Docker** и **Dockerfiles** подходят для отдельных контейнеров. Для сети изолированных приложений управление усложняется. Помогает **Docker Compose**.

**Создадим второе приложение **Spring Boot docker-product-server**:**

```java
@RestController
public class DockerProductController {
    @GetMapping("/products")
    public String getMessage() {
        return "A brand new product";
    }
}
```

Собираем и запускаем аналогично серверу сообщений.

**Файл `**docker-compose.yml**`:**

```yaml
version: '2'
services:
  message-server:
    container_name: message-server
    build:
      context: docker-message-server
      dockerfile: Dockerfile
    image: message-server:latest
    ports:
      - 18888:8888
    networks:
      - spring-cloud-network
  product-server:
    container_name: product-server
    build:
      context: docker-product-server
      dockerfile: Dockerfile
    image: product-server:latest
    ports:
      - 19999:9999
    networks:
      - spring-cloud-network
networks:
  spring-cloud-network:
    driver: bridge
```

**Разбор ключей:**
- **version**: версия формата (**обязательное поле**).
- **services**: перечень служб/контейнеров (**обязательное поле**).
- **build**: сборка образа из **Dockerfile**.
- **context**: каталог сборки с **Dockerfile**.
- **dockerfile**: альтернативное имя **Dockerfile**.
- **image**: имя образа (**если build**) или поиск в реестре.
- **networks**: именованные сети; секция ниже описывает **bridge**-сеть.

**Проверка синтаксиса:**

```bash
docker-compose config
```

**Сборка, создание контейнеров и запуск:**

```bash
docker-compose up --build
```

**Остановка и очистка:**

```bash
docker-compose down
```

Масштабирование сервисов: убрать `**container_name**`, настроить маппинг портов без конфликтов (**пример диапазона**):

```yaml
ports:
  - 18800-18888:8888
```

**Пример масштабирования:**

```bash
docker-compose --file docker-compose-scale.yml up -d --build --scale message-server=1 --scale product-server=1
docker-compose --file docker-compose-scale.yml up -d --build --scale message-server=3 --scale product-server=2
```

**Собственный базовый образ на **Alpine**:**

```dockerfile
FROM alpine:edge
MAINTAINER baeldung.com
RUN apk add --no-cache openjdk8
```

**Сборка:**

```bash
docker build --tag=alpine-java:base --rm=true .
# Точка в конце — каталог сборки
```

**Spring Boot** `2.3` добавил поддержку **buildpacks**. Вместо ручного **Dockerfile** можно:**

```bash
mvn spring-boot:build-image
# или
./gradlew bootBuildImage
```

Нужен установленный **Docker**. Идея **buildpacks** — опыт как **Heroku**/**Cloud Foundry**: цель `**build-image**` собирает и деплоит артефакт. **Buildpacks** создают многослойный образ и используют развернутую версию **JAR**.

**Пример вывода:**

```
[INFO] Building jar: target/demo-0.0.1-SNAPSHOT.jar
[INFO] Building image 'docker.io/library/demo:0.0.1-SNAPSHOT'
[INFO] > Pulling builder image 'gcr.io/paketo-buildpacks/builder:base-platform-api-0.3' 100%
[INFO] [creator] ===> DETECTING
[INFO] [creator] 5 of 15 buildpacks participating
[INFO] [creator] paketo-buildpacks/bellsoft-liberica 2.8.1
[INFO] [creator] paketo-buildpacks/executable-jar 1.2.8
[INFO] [creator] paketo-buildpacks/apache-tomcat 1.3.1
[INFO] [creator] paketo-buildpacks/dist-zip 1.3.6
[INFO] [creator] paketo-buildpacks/spring-boot 1.9.1
[INFO] Successfully built image 'docker.io/library/demo:0.0.1-SNAPSHOT'
```

**При последующих сборках переиспользуются слои:**

```
[INFO] [creator] Reusing layer 'paketo-buildpacks/executable-jar:class-path'
[INFO] [creator] Reusing layer 'paketo-buildpacks/spring-boot:web-application-type'
[INFO] Successfully built image 'docker.io/library/demo:0.0.1-SNAPSHOT'
```

## Создание образов с помощью **Spring Boot**

**Традиционный способ — **Dockerfile**:**

```dockerfile
FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/demo-app-1.0.0.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

**Проблемы толстых **JAR Spring Boot**:**
- Толстая банка в одном слое → изменение любой строки требует перестройки слоя.
- Время запуска может быть больше.

Разбивая банку перед сборкой, код приложения и сторонние библиотеки получают свой слой → работает кэш **Docker**.

**Пакеты сборки (**buildpacks**) обеспечивают зависимости/рантайм, можно обойтись без **Dockerfile**, автоматически получить разумный образ. **Maven**/**Gradle** поддержка:**

```bash
./mvnw spring-boot:build-image
```

Вывод показывает загрузку **builder Paketo**, определение **buildpacks** и итоговый образ. Время первой сборки выше из-за загрузки **buildpacks**, далее быстрее.

**Если не хотим **buildpacks**, **Spring Boot** поддерживает многослойные **JAR**. Типичная структура толстого **JAR**:**

```
org/springframework/boot/loader/BOOT-INF/classes/lib/
```

**Добавляется `**layers.idx**`, который сопоставляет каталоги со слоями. Слои по умолчанию:**
- **dependencies**: сторонние зависимости
- **snapshot-dependencies**: **snapshot** зависимости
- **spring-boot-loader**: загрузчик
- **application**: код приложения и ресурсы

**Пример **Dockerfile** с извлечением слоёв:**

```dockerfile
FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder dependencies/./
COPY --from=builder snapshot-dependencies/./
COPY --from=builder spring-boot-loader/./
COPY --from=builder application/./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

Каждая директива **COPY** создаёт слой. При изменении кода перестраивается только слой **application**.

**При необходимости можно кастомизировать слои через `**layers.xml**`:**

```xml
<layers xmlns="http://www.springframework.org/schema/boot/layers"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/boot/layers
        https://www.springframework.org/schema/boot/layers/layers-2.3.xsd">
    <application>
        <into layer="spring-boot-loader">
            <include>org/springframework/boot/loader/</include>
        </into>
        <into layer="application" />
    </application>
    <dependencies>
        <into layer="snapshot-dependencies">
            <include>*:*:*SNAPSHOT</include>
        </into>
        <into layer="dependencies" />
    </dependencies>
    <layerOrder>
        <layer>dependencies</layer>
        <layer>spring-boot-loader</layer>
        <layer>snapshot-dependencies</layer>
        <layer>application</layer>
    </layerOrder>
</layers>
```

**Подключение кастомных слоёв в **Maven**:**

```xml
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <layers>
      <enabled>true</enabled>
      <configuration>${project.basedir}/src/layers.xml</configuration>
    </layers>
  </configuration>
</plugin>
```

**Добавление нового слоя для внутренних зависимостей:**

```xml
<into layer="internal-dependencies">
  <include>com.baeldung.docker:*:*</include>
</into>
```

**И в порядке слоёв:**

```xml
<layerOrder>
  <layer>internal-dependencies</layer>
</layerOrder>
```

**В **Dockerfile** добавить копирование **internal-dependencies**:**

```dockerfile
COPY --from=builder internal-dependencies/./
```

**Проверка слоёв:**

```bash
java -Djarmode=layertools -jar target/docker-spring-boot-0.0.1.jar list
java -Djarmode=layertools -jar target/docker-spring-boot-0.0.1.jar extract
docker history --format "{{.ID}} {{.CreatedBy}} {{.Size}}" spring-docker-demo
```

## Доступ к журналам **Spring Boot**

**Сборка образа:**

```bash
mvn spring-boot:build-image
```

**Запуск контейнера и просмотр **STDOUT**:**

```bash
docker run --name=demo-container docker.io/library/spring-boot-docker:0.0.1-SNAPSHOT
```

**Добавление файла журнала в `**application.properties**`:**

```
logging.file.path=logs
```

**Чтение логов через `**tail** -f` внутри контейнера:**

```bash
docker exec -it demo-container tail -f /workspace/logs/spring.log > $HOME/spring.log
```

**Если нужен доступ к логам на хосте, используем том:**

```bash
mvn spring-boot:build-image -v /path-to-host:/workspace/logs
```

**Для **Compose** добавить **volumes**:**

```yaml
network-example-service-available-to-host-on-port-1337:
  image: karthequian/helloworld:latest
  container_name: network-example-service-available-to-host-on-port-1337
  volumes:
    - /path-to-host:/workspace/logs
```

**Запуск:**

```bash
docker-compose up
```

Продвинуто: `**docker logs**` (**driver `json-file`/local/journald**):

```bash
docker ps
docker logs -f 877bb028a143
```

В **Swarm** использовать `**docker service** ps` и `**docker service logs**`.

**Настройка лог-драйвера **GELF** (**Graylog**) глобально `**daemon.json**`:**

```json
{
  "log-driver": "gelf",
  "log-opts": {
    "gelf-address": "udp://1.2.3.4:12201"
  }
}
```

**Переопределение для одного контейнера:**

```bash
docker run --log-driver gelf --log-opt gelf-address=udp://1.2.3.4:12201 alpine echo hello world
```

## Запуск **Spring Boot** с **PostgreSQL** в **Docker Compose**

Цель: запустить **Spring Boot** + **PostgreSQL** через **Docker Compose**.

**Создаём проект **Spring Boot** (**PostgreSQL `Driver`, `Spring Data` JPA**):**

```bash
./mvnw spring-boot:run
```

**Падает, т.к. нет БД:**

```
Failed to configure a DataSource: 'url' attribute is not specified ...
Failed to determine a suitable driver class
```

**Упаковать приложение в **JAR**:**

```bash
./mvnw clean package -DskipTests
```

**JAR** в `**target**` с именем `**docker-`spring-`boot-postgres`-0`.0.1-`SNAPSHOT`.jar**`. Копируем в образ:**

```bash
cp target/docker-spring-boot-postgres-0.0.1-SNAPSHOT.jar src/main/docker
```

**Dockerfile в корне проекта:**

```dockerfile
FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
```

**Compose-файл `docker-compose.yml` в корне проекта:**

```yaml
version: '2'
services:
  app:
    image: 'docker-spring-boot-postgres:latest'
    build:
      context: .
    container_name: app
    depends_on:
      - db
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/compose-postgres
      - SPRING_DATASOURCE_USERNAME=compose-postgres
      - SPRING_DATASOURCE_PASSWORD=compose-postgres
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
  db:
    image: 'postgres:13.1-alpine'
    container_name: db
    environment:
      - POSTGRES_USER=compose-postgres
      - POSTGRES_PASSWORD=compose-postgres
```

**Разбор:**
- Образ приложения `**docker-`spring-boot`-postgres**:**latest**`, собирается из **Dockerfile**.
- `**container_name**: **app**`, зависит от `db`.
- Подключение к PostgreSQL (контейнер **compose-postgres**), креды, **Hibernate ddl-auto**=**update**.
- Сервис db: образ **postgres**:13.1-**alpine**, имя контейнера db, креды **compose-postgres**.

**Запуск:**

```bash
docker-compose up
```

**Сначала соберётся образ приложения, затем запустится **PostgreSQL**, затем приложение. Пример логов запуска:**

```
Starting DemoApplication v0.0.1-SNAPSHOT using Java 11.0.9 ...
Finished Spring Data repository scanning in 28 ms. Found 0 JPA repository interfaces.
Started DemoApplication in 4.751 seconds (JVM running for 6.512)
```

**Остановка:**

```bash
docker-compose down
```

**Добавляем сущность клиента:**

```java
@Entity
@Table(name = "customer")
public class Customer {
    @Id @GeneratedValue
    private long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
}
```

**Репозиторий:**

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {}
```

**Используем в приложении:**

```java
@SpringBootApplication
public class DemoApplication {
    @Autowired
    private CustomerRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        List allCustomers = this.repository.findAll();
        logger.info("Number of customers: " + allCustomers.size());
        Customer newCustomer = new Customer();
        newCustomer.setFirstName("John");
        newCustomer.setLastName("Doe");
        logger.info("Saving new customer");
        this.repository.save(newCustomer);
        allCustomers = this.repository.findAll();
        logger.info("Number of customers: " + allCustomers.size());
    }
}
```

**Пересборка и перезапуск:**

```bash
./mvnw clean package -DskipTests
cp target/docker-spring-boot-postgres-0.0.1-SNAPSHOT.jar src/main/docker
cd src/main/docker
docker-compose down
docker rmi docker-spring-boot-postgres:latest
docker-compose up
```

**Пример логов:**

```
Finished Spring Data repository scanning in 180 ms. Found 1 JPA repository interfaces.
Number of customers: 0
Saving new customer
Number of customers: 1
```

## Повторное использование слоев **Docker** с **Spring Boot**

**Docker** — стандарт де-факто для автономных приложений. С версии `2.3`.0 **Spring Boot** включает улучшения для эффективных образов: слои и **buildpacks**.

Контейнеры **Docker** состоят из базового образа и слоёв. Слои кэшируются; изменения в нижних слоях перестраивают верхние, поэтому редко меняющиеся слои должны быть ниже.

**Spring Boot** отображает содержимое артефакта на слои (**dependencies, `spring-boot-loader`, `snapshot-dependencies`, application**). Код приложения в отдельном слое → при изменении перестраивается только он, загрузчик и зависимости остаются в кэше → быстрее сборка и запуск.

**Традиционный подход «толстая банка» — всё в одном артефакте, любое изменение требует перестройки слоя. Новые функции:**
- **Buildpack**: предоставляет **Java runtime**, можно пропустить **Dockerfile** и автоматически создать образ.
- Многоуровневые **jar**-файлы: дают максимум от слоёв **Docker**.

**Проверка многослойной банки:**

```bash
jar tf target/spring-boot-docker-0.0.1-SNAPSHOT.jar
```

**Видим слой кэша, который сопоставляет зависимости, ресурсы и код приложения по слоям. Перечисление слоёв:**

```bash
java -Djarmode=layertools -jar target/docker-spring-boot-0.0.1.jar list
# dependencies, spring-boot-loader, snapshot-dependencies, application
```

**Извлечение слоёв:**

```bash
java -Djarmode=layertools -jar target/docker-spring-boot-0.0.1.jar extract
ls
# application/, snapshot-dependencies/, dependencies/, spring-boot-loader/
```

**Dockerfile** с использованием извлечённых слоёв:**

```dockerfile
FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder dependencies/./
COPY --from=builder snapshot-dependencies/./
COPY --from=builder spring-boot-loader/./
COPY --from=builder application/./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

При изменении исходного кода перестраивается только слой **application**. Однако слой **dependencies** может быть монолитным. Кастомизация слоёв через `**layers.xml**` (**пример выше**) позволяет выделять **internal-dependencies** и управлять порядком.

**Пример включения **internal-dependencies** в образ:**

```bash
mvn package
docker build -f src/main/docker/Dockerfile --tag spring-docker-demo .
docker history --format "{{.ID}} {{.CreatedBy}} {{.Size}}" spring-docker-demo
```

В истории виден новый слой для **internal-dependencies**, остальные слои переиспользуются кэшем.

