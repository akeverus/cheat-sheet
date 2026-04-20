---
title: "Maven Advanced"
description: "Apache Maven - это мощная система управления проектами и сборки с открытым исходным кодом, которая использует декларативный подход для описания проекта и его зависимостей. Этот документ охватывает продвинутые концепции, enterprise паттерны, производительность и best practices для"
tags:
  - development
  - build-tools
  - maven-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Maven Advanced

**Apache Maven** — это мощная система управления проектами и сборки с открытым исходным кодом, которая использует декларативный подход для описания проекта и его зависимостей. Этот документ охватывает продвинутые концепции, **enterprise** паттерны, производительность и **best practices** для крупных проектов.

## Полезные ссылки
- [Maven Documentation](https://maven.apache.org/guides/)
- [Maven Central Repository](https://central.sonatype.com/)
- [Maven Plugins](https://maven.apache.org/plugins/)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/)
- [Maven Wrapper](https://maven.apache.org/wrapper/)


### См. также
- [[maven|Maven (основы)]]
## Содержание

- [Продвинутая конфигурация проекта](#продвинутая-конфигурация-проекта)
  - [Много-модульные проекты](#много-модульные-проекты)
    - [Reactor и build order](#reactor-и-build-order)
    - [Custom reactor](#custom-reactor)
- [Продвинутые плагины](#продвинутые-плагины)
  - [Кастомные плагины](#кастомные-плагины)
  - [Extension development](#extension-development)
- [Управление зависимостями](#управление-зависимостями)
  - [Продвинутые конфигурации зависимостей](#продвинутые-конфигурации-зависимостей)
  - [Dependency locking](#dependency-locking)
- [Профили и конфигурация](#профили-и-конфигурация)
  - [Продвинутые профили](#продвинутые-профили)
  - [Settings.xml profiles](#settingsxml-profiles)
- [Тестирование и качество кода](#тестирование-и-качество-кода)
  - [Продвинутые тестовые конфигурации](#продвинутые-тестовые-конфигурации)
  - [Mutation testing](#mutation-testing)
- [Публикация и доставка](#публикация-и-доставка)
  - [Multi-repository publishing](#multi-repository-publishing)
  - [Release management](#release-management)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Build optimization](#build-optimization)
  - [Maven wrapper](#maven-wrapper)
- [CI/CD интеграция](#cicd-интеграция)
  - [Jenkins pipeline](#jenkins-pipeline)
  - [GitHub Actions](#github-actions)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Build analysis](#build-analysis)
- [Лучшие практики](#лучшие-практики)
  - [Структура и организация](#структура-и-организация)
  - [Производительность](#производительность)
  - [Качество и безопасность](#качество-и-безопасность)
  - [CI/CD](#cicd)
  - [Enterprise POM structure](#enterprise-pom-structure)
  - [Quality gates](#quality-gates)
- [См. также](#см-также-1)

## Продвинутая конфигурация проекта

### Много-модульные проекты

#### Reactor и build order
Ниже — родительский **POM** многомодульного **Maven**-проекта (reactor, build order).
```xml
<!-- parent/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example</groupId>
        <artifactId>enterprise-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../parent/pom.xml</relativePath>
    </parent>

    <artifactId>enterprise-reactor</artifactId>
    <packaging>pom</packaging>

    <name>Enterprise Reactor</name>
    <description>Parent POM for enterprise multi-module project</description>

    <modules>
        <!-- Infrastructure modules (build first) -->
        <module>../shared/shared-api</module>
        <module>../shared/shared-model</module>
        <module>../shared/shared-util</module>

        <!-- Core business modules -->
        <module>../core/core-domain</module>
        <module>../core/core-service</module>
        <module>../core/core-repository</module>

        <!-- Application modules -->
        <module>../web/web-api</module>
        <module>../web/web-service</module>
        <module>../batch/batch-job</module>
        <module>../batch/batch-scheduler</module>

        <!-- Integration modules (build last) -->
        <module>../integration/integration-test</module>
        <module>../distribution/distribution-package</module>
    </modules>

    <!-- Dependency management -->
    <dependencyManagement>
        <dependencies>
            <!-- BOM imports -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <dependency>
                <groupId>com.example</groupId>
                <artifactId>shared-api</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- Plugin management -->
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>repackage</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>

                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.11.0</version>
                    <configuration>
                        <source>17</source>
                        <target>17</target>
                        <encoding>UTF-8</encoding>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

    <!-- Profiles -->
    <profiles>
        <profile>
            <id>development</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <build.profile>development</build.profile>
            </properties>
        </profile>

        <profile>
            <id>production</id>
            <properties>
                <build.profile>production</build.profile>
                <maven.test.skip>true</maven.test.skip>
            </properties>
        </profile>
    </profiles>
</project>
```

#### Custom reactor
```xml
<!-- Custom reactor configuration -->
<project>
    <!-- Custom build order -->
    <modules>
        <module>shared</module>
        <module>core</module>
        <module>web</module>
    </modules>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-invoker-plugin</artifactId>
                <version>3.5.1</version>
                <configuration>
                    <projectsDirectory>.</projectsDirectory>
                    <pomIncludes>
                        <pomInclude>*/pom.xml</pomInclude>
                    </pomIncludes>
                    <preBuildHookScript>prepare-build</preBuildHookScript>
                    <postBuildHookScript>finalize-build</postBuildHookScript>
                    <settingsFile>../settings.xml</settingsFile>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Продвинутые плагины

### Кастомные плагины
```java
// Custom Maven Plugin
@Mojo(name = "validate-project", defaultPhase = LifecyclePhase.VALIDATE)
public class ValidateProjectMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "validate.skip", defaultValue = "false")
    private boolean skip;

    @Parameter
    private List<String> requiredProperties;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping project validation");
            return;
        }

        getLog().info("Validating project: " + project.getName());

        // Validate required properties
        validateRequiredProperties();

        // Validate dependencies
        validateDependencies();

        // Validate plugins
        validatePlugins();

        getLog().info("Project validation completed successfully");
    }

    private void validateRequiredProperties() throws MojoFailureException {
        Properties properties = project.getProperties();

        for (String property : requiredProperties) {
            if (!properties.containsKey(property)) {
                throw new MojoFailureException("Required property not found: " + property);
            }
        }
    }

    private void validateDependencies() throws MojoFailureException {
        Set<Artifact> dependencies = project.getDependencyArtifacts();

        // Check for forbidden dependencies
        for (Artifact dependency : dependencies) {
            if ("forbidden-group".equals(dependency.getGroupId())) {
                throw new MojoFailureException("Forbidden dependency found: " + dependency);
            }
        }
    }

    private void validatePlugins() throws MojoFailureException {
        Build build = project.getBuild();
        List<Plugin> plugins = build.getPlugins();

        // Ensure required plugins are present
        boolean hasSpringBootPlugin = plugins.stream()
            .anyMatch(p -> "org.springframework.boot".equals(p.getGroupId()) &&
                          "spring-boot-maven-plugin".equals(p.getArtifactId()));

        if (!hasSpringBootPlugin && "jar".equals(project.getPackaging())) {
            getLog().warn("Spring Boot plugin not found for JAR packaging");
        }
    }
}
```

### Extension development
```java
// Maven Extension
public class EnterpriseExtension implements Extension {

    @Override
    public void init(ExtensionContext context) throws Exception {
        // Initialize extension
        context.getLogger().info("Enterprise Maven Extension initialized");

        // Register custom lifecycle
        registerCustomLifecycle(context);

        // Configure session
        configureSession(context);
    }

    private void registerCustomLifecycle(ExtensionContext context) {
        // Register custom lifecycle phases
        Lifecycle customLifecycle = new Lifecycle();
        customLifecycle.setId("enterprise");

        List<LifecyclePhase> phases = new ArrayList<>();
        phases.add(new LifecyclePhase("validate-enterprise", "com.example:validate-project-maven-plugin:validate-project"));
        phases.add(new LifecyclePhase("prepare-enterprise", "org.apache.maven.plugins:maven-resources-plugin:resources"));
        phases.add(new LifecyclePhase("compile-enterprise", "org.apache.maven.plugins:maven-compiler-plugin:compile"));
        phases.add(new LifecyclePhase("test-enterprise", "org.apache.maven.plugins:maven-surefire-plugin:test"));
        phases.add(new LifecyclePhase("package-enterprise", "org.apache.maven.plugins:maven-jar-plugin:jar"));

        customLifecycle.setPhases(phases);

        // Register lifecycle
        // This would be done through SPI or configuration
    }

    private void configureSession(ExtensionContext context) {
        // Configure Maven session with enterprise defaults
        Properties userProperties = context.getSession().getUserProperties();

        // Set default properties if not specified
        if (!userProperties.containsKey("maven.compiler.source")) {
            userProperties.setProperty("maven.compiler.source", "17");
        }

        if (!userProperties.containsKey("maven.compiler.target")) {
            userProperties.setProperty("maven.compiler.target", "17");
        }

        if (!userProperties.containsKey("project.build.sourceEncoding")) {
            userProperties.setProperty("project.build.sourceEncoding", "UTF-8");
        }
    }
}
```

## Управление зависимостями

### Продвинутые конфигурации зависимостей
```xml
<!-- Advanced dependency management -->
<dependencyManagement>
    <dependencies>
        <!-- BOM imports -->
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>enterprise-bom</artifactId>
            <version>${project.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!-- Version ranges (not recommended for production) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>[2.15.0,2.16.0)</version>
        </dependency>

        <!-- Custom exclusions -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.hibernate.validator</groupId>
                    <artifactId>hibernate-validator</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- Optional dependencies -->
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>optional-feature</artifactId>
            <version>${project.version}</version>
            <optional>true</optional>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- Dependency convergence -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-enforcer-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <id>enforce-dependency-convergence</id>
                    <goals>
                        <goal>enforce</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <DependencyConvergence/>
                            <requireMavenVersion>
                                <version>3.8.0</version>
                            </requireMavenVersion>
                            <requireJavaVersion>
                                <version>17</version>
                            </requireJavaVersion>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Dependency locking
```xml
<!-- Dependency lock file -->
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-lock-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <id>lock-dependencies</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>lock</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>check-dependencies</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## Профили и конфигурация

### Продвинутые профили
```xml
<!-- Advanced profile configuration -->
<profiles>
    <!-- Development profile -->
    <profile>
        <id>development</id>
        <activation>
            <activeByDefault>true</activeByDefault>
            <property>
                <name>environment</name>
                <value>dev</value>
            </property>
        </activation>

        <properties>
            <spring.profiles.active>development</spring.profiles.active>
            <maven.test.skip>false</maven.test.skip>
            <maven.compiler.debug>true</maven.compiler.debug>
            <maven.compiler.debuglevel>lines,vars,source</maven.compiler.debuglevel>
        </properties>

        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
            </dependency>
        </dependencies>

        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <fork>true</fork>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

    <!-- Testing profile -->
    <profile>
        <id>testing</id>
        <activation>
            <property>
                <name>environment</name>
                <value>test</value>
            </property>
        </activation>

        <properties>
            <spring.profiles.active>testing</spring.profiles.active>
            <maven.test.failure.ignore>false</maven.test.failure.ignore>
        </properties>

        <dependencies>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>junit-jupiter</artifactId>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>postgresql</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </profile>

    <!-- Production profile -->
    <profile>
        <id>production</id>
        <activation>
            <property>
                <name>environment</name>
                <value>prod</value>
            </property>
        </activation>

        <properties>
            <spring.profiles.active>production</spring.profiles.active>
            <maven.test.skip>true</maven.test.skip>
            <maven.compiler.optimize>true</maven.compiler.optimize>
        </properties>

        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <executable>true</executable>
                        <jvmArguments>
                            -Xmx2g
                            -XX:+UseG1GC
                            -XX:+UseContainerSupport
                            -Dspring.profiles.active=production
                        </jvmArguments>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

    <!-- Integration testing profile -->
    <profile>
        <id>integration-test</id>
        <properties>
            <skip.unit.tests>true</skip.unit.tests>
            <skip.integration.tests>false</skip.integration.tests>
        </properties>

        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-failsafe-plugin</artifactId>
                    <version>3.1.2</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>integration-test</goal>
                                <goal>verify</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

### Settings.xml profiles
```xml
<!-- ~/.m2/settings.xml -->
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          https://maven.apache.org/xsd/settings-1.0.0.xsd">

    <profiles>
        <profile>
            <id>enterprise-mirror</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo.example.com/repository/maven-public/</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>daily</updatePolicy>
                    </snapshots>
                </repository>
            </repositories>

            <pluginRepositories>
                <pluginRepository>
                    <id>central</id>
                    <url>https://repo.example.com/repository/maven-public/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>

        <profile>
            <id>enterprise-security</id>
            <properties>
                <maven.wagon.http.ssl.insecure>true</maven.wagon.http.ssl.insecure>
                <maven.wagon.http.ssl.allowall>true</maven.wagon.http.ssl.allowall>
                <maven.wagon.http.ssl.ignore.validity.dates>true</maven.wagon.http.ssl.ignore.validity.dates>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>enterprise-mirror</activeProfile>
        <activeProfile>enterprise-security</activeProfile>
    </activeProfiles>

    <servers>
        <server>
            <id>artifactory</id>
            <username>${env.ARTIFACTORY_USER}</username>
            <password>${env.ARTIFACTORY_PASSWORD}</password>
        </server>
    </servers>
</settings>
```

## Тестирование и качество кода

### Продвинутые тестовые конфигурации
```xml
<!-- Advanced testing configuration -->
<build>
    <plugins>
        <!-- Unit testing -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <skip>${maven.test.skip}</skip>
                <skipTests>${skip.unit.tests}</skipTests>
                <includes>
                    <include>/*Test.java</include>
                    <include>/*Tests.java</include>
                </includes>
                <excludes>
                    <exclude>/*IT.java</exclude>
                    <exclude>/*IntegrationTest.java</exclude>
                </excludes>
                <systemPropertyVariables>
                    <java.util.logging.config.file>${project.build.testOutputDirectory}/logging.properties</java.util.logging.config.file>
                </systemPropertyVariables>
                <argLine>
                    --add-opens java.base/java.lang=ALL-UNNAMED
                    --add-opens java.base/java.util=ALL-UNNAMED
                </argLine>
                <forkCount>1C</forkCount>
                <reuseForks>true</reuseForks>
                <reportFormat>plain</reportFormat>
                <redirectTestOutputToFile>true</redirectTestOutputToFile>
            </configuration>
        </plugin>

        <!-- Integration testing -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <skip>${maven.test.skip}</skip>
                <skipTests>${skip.integration.tests}</skipTests>
                <skipITs>${skip.integration.tests}</skipITs>
                <includes>
                    <include>/*IT.java</include>
                    <include>/*IntegrationTest.java</include>
                </includes>
                <systemPropertyVariables>
                    <it.test>true</it.test>
                </systemPropertyVariables>
                <argLine>
                    -Xmx1024m
                    -Dspring.profiles.active=integration-test
                </argLine>
            </configuration>
            <executions>
                <execution>
                    <id>integration-test</id>
                    <goals>
                        <goal>integration-test</goal>
                    </goals>
                </execution>
                <execution>
                    <id>verify</id>
                    <goals>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- JaCoCo coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.10</version>
            <executions>
                <execution>
                    <id>default-prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>default-report</id>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>default-prepare-agent-integration</id>
                    <goals>
                        <goal>prepare-agent-integration</goal>
                    </goals>
                </execution>
                <execution>
                    <id>default-report-integration</id>
                    <goals>
                        <goal>report-integration</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Code quality -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-checkstyle-plugin</artifactId>
            <version>3.3.0</version>
            <configuration>
                <configLocation>checkstyle.xml</configLocation>
                <encoding>UTF-8</encoding>
                <consoleOutput>true</consoleOutput>
                <failsOnError>true</failsOnError>
                <linkXRef>false</linkXRef>
            </configuration>
            <executions>
                <execution>
                    <id>validate</id>
                    <phase>validate</phase>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Mutation testing
```xml
<!-- PIT mutation testing -->
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.14.1</version>
    <configuration>
        <targetClasses>
            <param>com.example.*</param>
        </targetClasses>
        <targetTests>
            <param>com.example.*Test</param>
        </targetTests>
        <threads>4</threads>
        <outputFormats>
            <outputFormat>XML</outputFormat>
            <outputFormat>HTML</outputFormat>
        </outputFormats>
        <timestampedReports>false</timestampedReports>
        <mutationThreshold>80</mutationThreshold>
        <coverageThreshold>80</coverageThreshold>
        <avoidCallsTo>
            <avoidCallsTo>java.util.logging</avoidCallsTo>
            <avoidCallsTo>org.slf4j</avoidCallsTo>
            <avoidCallsTo>org.apache.log4j</avoidCallsTo>
        </avoidCallsTo>
        <excludedMethods>
            <excludedMethod>equals</excludedMethod>
            <excludedMethod>hashCode</excludedMethod>
            <excludedMethod>toString</excludedMethod>
        </excludedMethods>
        <excludedClasses>
            <excludedClass>com.example.config.*</excludedClass>
            <excludedClass>com.example.entity.*</excludedClass>
        </excludedClasses>
    </configuration>
</plugin>
```

## Публикация и доставка

### Multi-repository publishing
```xml
<!-- Distribution management -->
<distributionManagement>
    <repository>
        <id>releases</id>
        <name>Release Repository</name>
        <url>https://repo.example.com/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <name>Snapshot Repository</name>
        <url>https://repo.example.com/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>

<!-- Advanced publishing configuration -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-deploy-plugin</artifactId>
            <version>3.1.1</version>
            <configuration>
                <skip>true</skip> <!-- Skip default deploy -->
            </configuration>
        </plugin>

        <!-- Nexus staging -->
        <plugin>
            <groupId>org.sonatype.plugins</groupId>
            <artifactId>nexus-staging-maven-plugin</artifactId>
            <version>1.6.13</version>
            <extensions>true</extensions>
            <configuration>
                <serverId>ossrh</serverId>
                <nexusUrl>https://oss.sonatype.org/</nexusUrl>
                <autoReleaseAfterClose>true</autoReleaseAfterClose>
            </configuration>
        </plugin>

        <!-- GPG signing -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-gpg-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                <execution>
                    <id>sign-artifacts</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>sign</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Source and javadoc jars -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <id>attach-sources</id>
                    <goals>
                        <goal>jar</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <id>attach-javadocs</id>
                    <goals>
                        <goal>jar</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <doclint>none</doclint>
                <source>17</source>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Release management
```xml
<!-- Maven release plugin configuration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>3.0.1</version>
    <configuration>
        <preparationGoals>clean verify</preparationGoals>
        <goals>deploy</goals>
        <scmCommentPrefix>[maven-release-plugin] </scmCommentPrefix>
        <tagNameFormat>@{project.version}</tagNameFormat>
        <releaseProfiles>release</releaseProfiles>
        <autoVersionSubmodules>true</autoVersionSubmodules>
        <remoteTagging>true</remoteTagging>
        <pushChanges>true</pushChanges>
        <localCheckout>true</localCheckout>
        <arguments>-DskipTests=true -DskipITs=true</arguments>
        <checkModificationExcludes>
            <checkModificationExclude>pom.xml</checkModificationExclude>
            <checkModificationExclude>/target/</checkModificationExclude>
        </checkModificationExcludes>
    </configuration>
</plugin>
```

## Производительность и оптимизация

### Build optimization
```xml
<!-- Build performance optimization -->
<properties>
    <!-- Parallel builds -->
    <maven.compiler.fork>true</maven.compiler.fork>
    <maven.compiler.compilerReuseStrategy>reuseCreated</maven.compiler.compilerReuseStrategy>

    <!-- Memory settings -->
    <maven.compiler.maxmem>1024m</maven.compiler.maxmem>
    <maven.surefire.maxmem>512m</maven.surefire.maxmem>

    <!-- Skip unnecessary operations -->
    <maven.javadoc.skip>true</maven.javadoc.skip>
    <maven.site.skip>true</maven.site.skip>
    <maven.source.skip>true</maven.source.skip>

    <!-- Dependency resolution -->
    <maven.resolver.transport>wagon</maven.resolver.transport>
    <aether.connector.http.connectionMaxTtl>25</aether.connector.http.connectionMaxTtl>
</properties>

<build>
    <plugins>
        <!-- Parallel test execution -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <forkCount>1C</forkCount>
                <reuseForks>true</reuseForks>
                <parallel>classes</parallel>
                <threadCount>4</threadCount>
            </configuration>
        </plugin>

        <!-- Build cache -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.6.1</version>
            <executions>
                <execution>
                    <id>analyze-dependencies</id>
                    <goals>
                        <goal>analyze</goal>
                    </goals>
                    <configuration>
                        <skip>${maven.dependency.skip}</skip>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- Incremental builds -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <useIncrementalCompilation>true</useIncrementalCompilation>
                <compilerReuseStrategy>reuseCreated</compilerReuseStrategy>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Maven wrapper
```xml
<!-- Maven wrapper plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-wrapper-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        <mavenVersion>3.9.1</mavenVersion>
        <distributionType>bin</distributionType>
    </configuration>
    <executions>
        <execution>
            <id>default</id>
            <goals>
                <goal>wrapper</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## CI/CD интеграция

### Jenkins pipeline
```groovy
// Jenkinsfile
pipeline {
    agent {
        docker {
            image 'maven:3.9.1-openjdk-17-slim'
            args '-v maven-cache:/root/.m2'
        }
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Cache Dependencies') {
            steps {
                sh 'mvn dependency:go-offline -B'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile -T 1C -B'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test -T 1C -B'
                junit '/target/surefire-reports/*.xml'
            }
            post {
                always {
                    jacoco execPattern: '/target/jacoco.exec'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                sh 'mvn verify -P integration-test -Dspring.profiles.active=test -B'
                junit '/target/failsafe-reports/*.xml'
            }
        }

        stage('Code Quality') {
            steps {
                sh 'mvn checkstyle:check pmd:check spotbugs:check -B'
            }
            post {
                always {
                    recordIssues tools: [checkStyle(), pmd(), spotBugs()]
                }
            }
        }

        stage('Security Scan') {
            steps {
                sh 'mvn org.owasp:dependency-check-maven:check -B'
            }
            post {
                always {
                    dependencyCheckPublisher pattern: '/dependency-check-report.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B'
            }
        }

        stage('Publish Artifacts') {
            when {
                anyOf {
                    branch 'main'
                    tag pattern: "v\\d+.*", comparator: "REGEX"
                }
            }
            steps {
                sh 'mvn deploy -DskipTests -B'
            }
        }

        stage('Release') {
            when {
                tag pattern: "v\\d+.*", comparator: "REGEX"
            }
            steps {
                sh 'mvn release:prepare release:perform -B'
            }
        }
    }

    post {
        always {
            sh 'mvn clean -B || true'
            archiveArtifacts artifacts: '/target/*.jar', allowEmptyArchive: true
            publishHTML target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ]
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            sh 'mvn build-info:save -B || true'
        }
    }
}
```

### GitHub Actions
```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    strategy:
      matrix:
        java: [17, 21]

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK ${{ matrix.java }}
      uses: actions/setup-java@v3
      with:
        java-version: ${{ matrix.java }}
        distribution: 'temurin'
        cache: maven

    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2/repository
        key: ${{ runner.os }}-maven-${{ hashFiles('/pom.xml') }}
        restore-keys: |
          ${{ runner.os }}-maven-

    - name: Build with Maven
      run: mvn compile -B -T 1C

    - name: Run tests
      run: mvn test -B -T 1C

    - name: Run integration tests
      run: mvn verify -P integration-test -B

    - name: Code quality checks
      run: mvn checkstyle:check -B

    - name: Security scan
      run: mvn org.owasp:dependency-check-maven:check -B

    - name: Package
      run: mvn package -DskipTests -B

    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results-${{ matrix.java }}
        path: '/target/surefire-reports/*.xml'

    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
      if: success()
      with:
        file: '/target/site/jacoco/jacoco.xml'

    - name: Build and push Docker image
      if: github.ref == 'refs/heads/main' && matrix.java == '17'
      run: |
        docker build -t myapp:${{ github.sha }} .
        docker push myapp:${{ github.sha }}
```

## Решение проблем

### Распространенные проблемы
```bash
# Очистка локального репозитория
mvn clean
rm -rf ~/.m2/repository/com/example

# Debug build
mvn clean compile -X

# Debug dependency resolution
mvn dependency:tree -Dverbose
mvn dependency:analyze

# Debug plugin execution
mvn help:describe -Dplugin=org.apache.maven.plugins:maven-compiler-plugin
mvn help:describe -Dplugin=org.springframework.boot:spring-boot-maven-plugin

# Check for updates
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates

# Analyze dependencies
mvn dependency:analyze
mvn dependency:analyze-duplicate

# Fix dependency versions
mvn versions:use-latest-versions
mvn versions:use-latest-releases

# Memory issues
MAVEN_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g" mvn clean install

# Network issues
mvn clean install -Dmaven.wagon.http.pool=false

# SSL certificate issues
mvn clean install -Dmaven.wagon.http.ssl.insecure=true
```

### Build analysis
```xml
<!-- Build analysis plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.6.1</version>
    <executions>
        <execution>
            <id>analyze-deps</id>
            <goals>
                <goal>analyze</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- Build info plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-buildinfo-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <execution>
            <id>save-build-info</id>
            <goals>
                <goal>save</goal>
            </goals>
            <configuration>
                <outputTimestamp>${maven.build.timestamp}</outputTimestamp>
                <buildInfoProperties>
                    <maven.version>${maven.version}</maven.version>
                    <java.version>${java.version}</java.version>
                    <os.name>${os.name}</os.name>
                </buildInfoProperties>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Лучшие практики

Ниже — практические рекомендации по организации и поддержке **Maven**-проектов в **enterprise**-среде.

### Структура и организация

- **Разделяйте ответственность:** родительский **POM** — только `dependencyManagement` и `pluginManagement`; модули наследуют версии без дублирования.
- **Используйте BOM:** импортируйте `spring-boot-dependencies` или собственный **BOM** для управления версиями зависимостей.
- **Зафиксируйте версии:** используйте `maven-dependency-lock-plugin` или `versions:lock-snapshots` для воспроизводимых сборок.
- **Порядок модулей:** инфраструктурные модули (`shared-api`, `shared-model`) — первыми; интеграционные тесты и дистрибутивы — последними.

### Производительность

- **Параллельная сборка:** `mvn -T 1C` для автоматического числа потоков по ядрам; `-T 4` для фиксированного.
- **Кэширование:** `forkCount=1C` и `reuseForks=true` в `surefire`; `-Dmaven.repo.local` в CI для общих артефактов.
- **Офлайн-режим:** `mvn dependency:go-offline` перед сборкой в CI для ускорения и стабильности.
- **Пропуск лишнего:** `maven.javadoc.skip=true`, `maven.source.skip=true` в CI, если не нужны.

### Качество и безопасность

- **Enforcer plugin:** `requireMavenVersion`, `requireJavaVersion`, `DependencyConvergence`, `banDuplicatePomDependencyVersions`.
- **Сканирование зависимостей:** `dependency-check-maven` или `snyk` в pipeline; блокировать сборку при критических уязвимостях.
- **Тесты:** разделяйте unit-тесты (`*Test.java`) и integration-тесты (`*IT.java`); используйте `failsafe` для IT.

### CI/CD

- **Maven Wrapper:** храните `mvnw` и `mvnw.cmd` в репозитории для единой версии Maven.
- **Артефакты:** публикуйте `sources` и `javadoc` для библиотек; подписывайте релизы (GPG).
- **Release:** используйте `maven-release-plugin` или **GitHub Actions** с семантическим версионированием.

### Enterprise POM structure
```text
enterprise-project/
├── pom.xml (root reactor)
├── parent/
│   ├── pom.xml (parent with dependency/plugin management)
│   └── src/
├── shared/
│   ├── shared-api/pom.xml
│   ├── shared-model/pom.xml
│   └── shared-util/pom.xml
├── core/
│   ├── core-domain/pom.xml
│   ├── core-service/pom.xml
│   └── core-repository/pom.xml
├── web/
│   ├── web-api/pom.xml
│   └── web-service/pom.xml
├── batch/
│   ├── batch-job/pom.xml
│   └── batch-scheduler/pom.xml
├── integration/
│   ├── integration-test/pom.xml
│   └── distribution/pom.xml
├── build-tools/
│   ├── pom.xml (custom plugins and extensions)
│   └── src/
├── docs/
│   ├── README.md
│   └── BUILD.md
├── scripts/
│   ├── build.sh
│   ├── deploy.sh
│   └── release.sh
├── .mvn/
│   ├── jvm.config
│   ├── maven.config
│   └── extensions.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── checkstyle.xml
├── pom.xml
└── README.md
```

### Quality gates
```xml
<!-- Quality gate plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.3.0</version>
    <executions>
        <execution>
            <id>enforce-quality-gates</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <!-- Require minimum Maven version -->
                    <requireMavenVersion>
                        <version>3.8.0</version>
                    </requireMavenVersion>

                    <!-- Require minimum Java version -->
                    <requireJavaVersion>
                        <version>17</version>
                    </requireJavaVersion>

                    <!-- Ban duplicate dependencies -->
                    <banDuplicatePomDependencyVersions/>

                    <!-- Require dependency convergence -->
                    <DependencyConvergence/>

                    <!-- Require plugin versions -->
                    <requirePluginVersions/>

                    <!-- Custom rules -->
                    <evaluateBeanshell>
                        <condition>
                            // Require minimum test coverage
                            def coverage = new File("${project.build.directory}/jacoco.exec")
                            coverage.exists() && coverage.length() > 0
                        </condition>
                        <message>Test coverage report not found</message>
                    </evaluateBeanshell>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```
## См. также
- [[gradle-advanced|Gradle Advanced]] — альтернативная система сборки
- [[spring-boot|Spring Boot]] — **Java** фреймворк
- [[jenkins|Jenkins]] — CI/CD сервер
- [[github-actions|GitHub Actions]] — CI/CD платформа