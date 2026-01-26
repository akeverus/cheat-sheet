# Konfig

Konfig - это type-safe конфигурационная библиотека для Kotlin, предоставляющая DSL для определения и валидации конфигурационных параметров. Поддерживает различные источники конфигурации с compile-time safety.

## Содержание

- [Основные возможности](#основные-возможности)
  - [Basic Configuration Definition](#basic-configuration-definition)
  - [Configuration Sources](#configuration-sources)
  - [Type-Safe Properties](#type-safe-properties)
  - [Custom Property Types](#custom-property-types)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Configuration Groups](#configuration-groups)
  - [Configuration Validation](#configuration-validation)
  - [Environment-Specific Configuration](#environment-specific-configuration)
  - [DSL for Configuration](#dsl-for-configuration)
- [Интеграция с Kotlin](#интеграция-с-kotlin)
  - [Inline Classes для Type Safety](#inline-classes-для-type-safety)
  - [Sealed Classes для Configuration Variants](#sealed-classes-для-configuration-variants)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Configuration Properties](#configuration-properties)
  - [Service Layer с Configuration](#service-layer-с-configuration)
  - [Controller с Configuration](#controller-с-configuration)
- [Тестирование](#тестирование)
  - [Unit Testing Configuration](#unit-testing-configuration)
  - [Integration Testing](#integration-testing)
- [Лучшие практики](#лучшие-практики)
  - [Configuration Structure](#configuration-structure)
  - [Environment Management](#environment-management)
  - [Security Considerations](#security-considerations)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging Configuration Loading](#debugging-configuration-loading)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Typesafe Config to Konfig](#from-typesafe-config-to-konfig)
  - [From Spring Configuration to Konfig](#from-spring-configuration-to-konfig)
  - [From Environment Variables to Konfig](#from-environment-variables-to-konfig)
- [Экспериментальные возможности](#экспериментальные-возможности)
  - [Konfig 2.0+ Features (Future)](#konfig-20-features-future)

## Основные возможности

### Basic Configuration Definition
```kotlin
import com.natpryce.konfig.*

// Определение конфигурации
object AppConfig : Configuration {
    val databaseUrl by stringType
    val databaseUser by stringType
    val databasePassword by stringType
    val port by intType
    val debug by booleanType
    val timeout by durationType
}

// Загрузка из Properties файла
val config = AppConfig.fromPropertiesFile(File("config.properties"))

// Использование
val url = config[AppConfig.databaseUrl]
val port = config[AppConfig.port]
```

### Configuration Sources
```kotlin
// Из Properties файла
val config1 = AppConfig.fromPropertiesFile(File("app.properties"))

// Из System Properties
val config2 = AppConfig.fromSystemProperties()

// Из Environment Variables
val config3 = AppConfig.fromEnvironmentVariables()

// Из Map
val mapConfig = mapOf(
    "database.url" to "jdbc:postgresql://localhost:5432/mydb",
    "database.user" to "admin",
    "database.password" to "secret",
    "port" to "8080",
    "debug" to "true",
    "timeout" to "30s"
)
val config4 = AppConfig.fromMap(mapConfig)

// Из Resources (classpath)
val config5 = AppConfig.fromPropertiesResource("config.properties")
```

### Type-Safe Properties
```kotlin
object ServerConfig : Configuration {
    // Primitive types
    val port by intType
    val host by stringType
    val debug by booleanType
    val maxConnections by longType

    // Complex types
    val timeout by durationType
    val size by sizeType
    val uri by uriType

    // Nullable types
    val optionalHost by stringType.orNull()
    val optionalPort by intType.orNull()

    // List types
    val allowedOrigins by listType(stringType)
    val adminUsers by listType(stringType)

    // Custom types
    val logLevel by enumType<LogLevel>()
    val databaseType by enumType<DatabaseType>()
}

enum class LogLevel { DEBUG, INFO, WARN, ERROR }
enum class DatabaseType { POSTGRESQL, MYSQL, H2 }
```

## Продвинутые возможности

### Custom Property Types
```kotlin
// Кастомный тип для паролей
object PasswordType : PropertyType<String> {
    override fun parseValue(propertyName: String, stringValue: String): String {
        require(stringValue.length >= 8) { "Password must be at least 8 characters" }
        require(stringValue.any { it.isUpperCase() }) { "Password must contain uppercase letter" }
        require(stringValue.any { it.isLowerCase() }) { "Password must contain lowercase letter" }
        require(stringValue.any { it.isDigit() }) { "Password must contain digit" }
        return stringValue
    }

    override fun toString(): String = "password"
}

// Кастомный тип для email
object EmailType : PropertyType<String> {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun parseValue(propertyName: String, stringValue: String): String {
        require(emailRegex.matches(stringValue)) { "Invalid email format: $stringValue" }
        return stringValue.lowercase()
    }

    override fun toString(): String = "email"
}

// Использование кастомных типов
object UserConfig : Configuration {
    val adminEmail by EmailType
    val adminPassword by PasswordType
    val notificationEmails by listType(EmailType)
}
```

### Configuration Groups
```kotlin
// Группировка конфигураций
object DatabaseConfig : Configuration {
    val url by stringType
    val user by stringType
    val password by stringType
    val maxConnections by intType
    val timeout by durationType
}

object ApiConfig : Configuration {
    val baseUrl by uriType
    val timeout by durationType
    val retries by intType
    val apiKey by stringType
}

object AppConfig : Configuration {
    // Вложенные конфигурации
    val database by subconfig(DatabaseConfig)
    val api by subconfig(ApiConfig)

    // Основные настройки
    val port by intType
    val debug by booleanType
}

// Использование
val config = AppConfig.fromPropertiesFile(File("application.properties"))

val dbUrl = config[AppConfig.database.url]
val apiTimeout = config[AppConfig.api.timeout]
```

### Configuration Validation
```kotlin
// Валидация конфигурации
object ValidatedConfig : Configuration {
    val port by intType
    val host by stringType
    val databaseUrl by stringType

    init {
        // Кастомная валидация
        validate {
            val port = this[port]
            check(port in 1024..65535) { "Port must be between 1024 and 65535, got $port" }
        }

        validate {
            val host = this[host]
            check(host.isNotBlank()) { "Host cannot be blank" }
            check(!host.contains("localhost")) { "Production host cannot be localhost" }
        }

        validate {
            val dbUrl = this[databaseUrl]
            check(dbUrl.startsWith("jdbc:")) { "Database URL must start with jdbc:" }
        }
    }
}

// Валидация при загрузке
try {
    val config = ValidatedConfig.fromPropertiesFile(File("config.properties"))
    println("Configuration loaded successfully")
} catch (e: ConfigurationException) {
    println("Configuration error: ${e.message}")
    System.exit(1)
}
```

### Environment-Specific Configuration
```kotlin
// Конфигурация для разных сред
object EnvironmentConfig : Configuration {
    val environment by enumType<Environment>()
    val configFile by stringType.orNull()
}

enum class Environment { DEV, TEST, STAGING, PROD }

fun loadConfiguration(): Configuration {
    val envConfig = EnvironmentConfig.fromEnvironmentVariables()
    val env = envConfig[EnvironmentConfig.environment]

    return when (env) {
        Environment.DEV -> DevConfig.fromPropertiesFile(File("config-dev.properties"))
        Environment.TEST -> TestConfig.fromPropertiesFile(File("config-test.properties"))
        Environment.STAGING -> StagingConfig.fromSystemProperties()
        Environment.PROD -> {
            val configFile = envConfig[EnvironmentConfig.configFile]
            if (configFile != null) {
                ProdConfig.fromPropertiesFile(File(configFile))
            } else {
                ProdConfig.fromEnvironmentVariables()
            }
        }
    }
}

// Environment-specific конфигурации
object DevConfig : Configuration {
    val debug by booleanType.default(true)
    val databaseUrl by stringType.default("jdbc:h2:mem:test")
}

object ProdConfig : Configuration {
    val debug by booleanType.default(false)
    val databaseUrl by stringType // Обязательно в проде
}
```

## Интеграция с Kotlin

### DSL for Configuration
```kotlin
// DSL для создания конфигурации
fun configuration(block: ConfigurationBuilder.() -> Unit): Configuration {
    val builder = ConfigurationBuilder()
    builder.block()
    return builder.build()
}

// DSL builder
class ConfigurationBuilder {
    private val properties = mutableMapOf<Key<*, *>, Any>()

    inline fun <reified T> property(key: Key<T, *>, value: T) {
        properties[key] = value
    }

    fun build(): Configuration {
        return object : Configuration {
            @Suppress("UNCHECKED_CAST")
            override fun <T> get(key: Key<T, *>): T {
                return properties[key] as T
            }
        }
    }
}

// Использование DSL
val config = configuration {
    property(AppConfig.databaseUrl, "jdbc:postgresql://localhost:5432/mydb")
    property(AppConfig.port, 8080)
    property(AppConfig.debug, true)
}
```

### Inline Classes для Type Safety
```kotlin
// Inline classes для type safety
@JvmInline
value class DatabaseUrl(val value: String) {
    init {
        require(value.startsWith("jdbc:")) { "Database URL must start with jdbc:" }
    }
}

@JvmInline
value class Port(val value: Int) {
    init {
        require(value in 1..65535) { "Port must be between 1 and 65535" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
    }
}

// Кастомные типы для inline classes
object DatabaseUrlType : PropertyType<DatabaseUrl> {
    override fun parseValue(propertyName: String, stringValue: String): DatabaseUrl {
        return DatabaseUrl(stringValue)
    }
}

object PortType : PropertyType<Port> {
    override fun parseValue(propertyName: String, stringValue: String): Port {
        return Port(stringValue.toInt())
    }
}

object EmailType : PropertyType<Email> {
    override fun parseValue(propertyName: String, stringValue: String): Email {
        return Email(stringValue)
    }
}

// Использование в конфигурации
object TypeSafeConfig : Configuration {
    val databaseUrl by DatabaseUrlType
    val port by PortType
    val adminEmail by EmailType
}
```

### Sealed Classes для Configuration Variants
```kotlin
// Sealed classes для разных типов конфигурации
sealed class AppConfiguration {
    abstract val database: DatabaseConfig
    abstract val server: ServerConfig

    data class Development(
        override val database: DatabaseConfig,
        override val server: ServerConfig
    ) : AppConfiguration()

    data class Production(
        override val database: DatabaseConfig,
        override val server: ServerConfig,
        val monitoring: MonitoringConfig
    ) : AppConfiguration()

    data class Test(
        override val database: DatabaseConfig,
        override val server: ServerConfig
    ) : AppConfiguration()
}

object DatabaseConfig : Configuration {
    val url by stringType
    val user by stringType
    val password by stringType
}

object ServerConfig : Configuration {
    val port by intType
    val host by stringType
}

object MonitoringConfig : Configuration {
    val enabled by booleanType
    val endpoint by uriType
}

// Функция для загрузки конфигурации
fun loadAppConfiguration(): AppConfiguration {
    val env = System.getenv("APP_ENV") ?: "development"

    val databaseConfig = DatabaseConfig.fromEnvironmentVariables()
    val serverConfig = ServerConfig.fromEnvironmentVariables()

    return when (env.lowercase()) {
        "production", "prod" -> {
            val monitoringConfig = MonitoringConfig.fromEnvironmentVariables()
            AppConfiguration.Production(databaseConfig, serverConfig, monitoringConfig)
        }
        "test" -> AppConfiguration.Test(databaseConfig, serverConfig)
        else -> AppConfiguration.Development(databaseConfig, serverConfig)
    }
}
```

## Интеграция с Spring Boot

### Configuration Properties
```kotlin
@Configuration
@ConfigurationProperties("app")
data class AppProperties(
    val name: String,
    val version: String,
    val database: DatabaseProperties,
    val server: ServerProperties
) {
    data class DatabaseProperties(
        val url: String,
        val user: String,
        val password: String,
        val maxConnections: Int
    )

    data class ServerProperties(
        val port: Int,
        val host: String,
        val ssl: SslProperties
    ) {
        data class SslProperties(
            val enabled: Boolean,
            val keyStore: String?,
            val keyStorePassword: String?
        )
    }
}

// Konfig integration
@Configuration
class KonfigConfig {

    @Bean
    fun konfigConfiguration(): com.natpryce.konfig.Configuration {
        return AppConfig.fromPropertiesResource("application.properties")
    }

    @Bean
    fun appProperties(konfig: com.natpryce.konfig.Configuration): AppProperties {
        return AppProperties(
            name = konfig[AppConfig.app.name],
            version = konfig[AppConfig.app.version],
            database = AppProperties.DatabaseProperties(
                url = konfig[AppConfig.database.url],
                user = konfig[AppConfig.database.user],
                password = konfig[AppConfig.database.password],
                maxConnections = konfig[AppConfig.database.maxConnections]
            ),
            server = AppProperties.ServerProperties(
                port = konfig[AppConfig.server.port],
                host = konfig[AppConfig.server.host],
                ssl = AppProperties.ServerProperties.SslProperties(
                    enabled = konfig[AppConfig.server.ssl.enabled],
                    keyStore = konfig[AppConfig.server.ssl.keyStore],
                    keyStorePassword = konfig[AppConfig.server.ssl.keyStorePassword]
                )
            )
        )
    }
}
```

### Service Layer с Configuration
```kotlin
@Service
class DatabaseService(
    private val config: com.natpryce.konfig.Configuration
) {

    private val database: Database

    init {
        val url = config[AppConfig.database.url]
        val user = config[AppConfig.database.user]
        val password = config[AppConfig.database.password]

        database = Database.connect(url, user, password)
    }

    fun getUsers(): List<User> {
        return transaction(db = database) {
            UserEntity.all().map { it.toUser() }
        }
    }

    fun getUserById(id: Long): User? {
        return transaction(db = database) {
            UserEntity.findById(id)?.toUser()
        }
    }
}

@Service
class ApiService(
    private val config: com.natpryce.konfig.Configuration,
    private val httpClient: HttpClient = HttpClient(CIO)
) {

    private val baseUrl = config[AppConfig.api.baseUrl]
    private val apiKey = config[AppConfig.api.key]
    private val timeout = config[AppConfig.api.timeout]

    suspend fun getExternalData(): ExternalData {
        return httpClient.get("$baseUrl/data") {
            header("Authorization", "Bearer $apiKey")
            timeout {
                requestTimeoutMillis = timeout.toLongMilliseconds()
            }
        }.body()
    }
}
```

### Controller с Configuration
```kotlin
@RestController
@RequestMapping("/api/config")
class ConfigController(
    private val config: com.natpryce.konfig.Configuration
) {

    @GetMapping("/info")
    fun getConfigInfo(): Map<String, Any> {
        return mapOf(
            "app.name" to config[AppConfig.app.name],
            "app.version" to config[AppConfig.app.version],
            "database.url" to config[AppConfig.database.url],
            "server.port" to config[AppConfig.server.port],
            "debug" to config[AppConfig.debug]
        )
    }

    @GetMapping("/validation")
    fun validateConfiguration(): Map<String, Any> {
        return try {
            // Попытка загрузить все конфигурационные значения
            val appName = config[AppConfig.app.name]
            val databaseUrl = config[AppConfig.database.url]
            val port = config[AppConfig.server.port]

            mapOf(
                "valid" to true,
                "message" to "Configuration is valid",
                "values" to mapOf(
                    "appName" to appName,
                    "databaseUrl" to databaseUrl,
                    "port" to port
                )
            )
        } catch (e: Exception) {
            mapOf(
                "valid" to false,
                "message" to "Configuration error: ${e.message}"
            )
        }
    }
}
```

## Тестирование

### Unit Testing Configuration
```kotlin
class ConfigurationTest {

    @Test
    fun `should load configuration from properties file`() {
        // Создание временного файла с конфигурацией
        val configFile = File.createTempFile("test-config", ".properties")
        configFile.writeText("""
            database.url=jdbc:h2:mem:test
            database.user=sa
            database.password=
            port=8080
            debug=true
            timeout=30s
        """.trimIndent())

        try {
            val config = AppConfig.fromPropertiesFile(configFile)

            assertEquals("jdbc:h2:mem:test", config[AppConfig.databaseUrl])
            assertEquals("sa", config[AppConfig.databaseUser])
            assertEquals("", config[AppConfig.databasePassword])
            assertEquals(8080, config[AppConfig.port])
            assertTrue(config[AppConfig.debug])
            assertEquals(30.seconds, config[AppConfig.timeout])
        } finally {
            configFile.delete()
        }
    }

    @Test
    fun `should load configuration from map`() {
        val configMap = mapOf(
            "database.url" to "jdbc:postgresql://localhost:5432/test",
            "port" to "9090",
            "debug" to "false"
        )

        val config = AppConfig.fromMap(configMap)

        assertEquals("jdbc:postgresql://localhost:5432/test", config[AppConfig.databaseUrl])
        assertEquals(9090, config[AppConfig.port])
        assertFalse(config[AppConfig.debug])
    }

    @Test
    fun `should handle missing required properties`() {
        val incompleteConfig = mapOf(
            "database.url" to "jdbc:h2:mem:test"
            // Отсутствуют другие обязательные свойства
        )

        assertThrows<KeyNotFoundException> {
            AppConfig.fromMap(incompleteConfig)[AppConfig.port]
        }
    }

    @Test
    fun `should validate custom property types`() {
        val configMap = mapOf(
            "admin.email" to "invalid-email",
            "admin.password" to "weak"
        )

        assertThrows<IllegalArgumentException> {
            UserConfig.fromMap(configMap)
        }
    }
}
```

### Integration Testing
```kotlin
@SpringBootTest
@TestPropertySource(properties = [
    "app.database.url=jdbc:h2:mem:test",
    "app.database.user=sa",
    "app.database.password=",
    "app.server.port=8080",
    "app.debug=true"
])
class ConfigurationIntegrationTest {

    @Autowired
    private lateinit var konfigConfig: com.natpryce.konfig.Configuration

    @Autowired
    private lateinit var appProperties: AppProperties

    @Test
    fun `should load configuration in Spring context`() {
        assertEquals("jdbc:h2:mem:test", konfigConfig[AppConfig.databaseUrl])
        assertEquals(8080, konfigConfig[AppConfig.port])
        assertTrue(konfigConfig[AppConfig.debug])
    }

    @Test
    fun `should convert to Spring properties correctly`() {
        assertEquals("jdbc:h2:mem:test", appProperties.database.url)
        assertEquals(8080, appProperties.server.port)
        assertTrue(appProperties.debug)
    }

    @Test
    fun `should validate configuration constraints`() {
        // Test через ConfigController
        val mockMvc = MockMvcBuilders.standaloneSetup(ConfigController(konfigConfig)).build()

        mockMvc.perform(get("/api/config/validation"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.values.appName").exists())
    }
}
```

## Лучшие практики

### Configuration Structure
```kotlin
// Рекомендуемая структура конфигурации
object ApplicationConfig : Configuration {
    // Application info
    val name by stringType
    val version by stringType
    val environment by enumType<Environment>()

    // Server configuration
    val server by subconfig(ServerConfig)

    // Database configuration
    val database by subconfig(DatabaseConfig)

    // External services
    val services by subconfig(ServiceConfig)

    // Features flags
    val features by subconfig(FeatureConfig)
}

object ServerConfig : Configuration {
    val port by intType
    val host by stringType
    val ssl by subconfig(SslConfig)
    val cors by subconfig(CorsConfig)
}

object DatabaseConfig : Configuration {
    val url by stringType
    val user by stringType
    val password by stringType
    val maxConnections by intType
    val timeout by durationType
}

object ServiceConfig : Configuration {
    val api by subconfig(ApiConfig)
    val cache by subconfig(CacheConfig)
    val messaging by subconfig(MessagingConfig)
}

object FeatureConfig : Configuration {
    val authentication by booleanType.default(false)
    val metrics by booleanType.default(true)
    val logging by booleanType.default(true)
    val caching by booleanType.default(false)
}
```

### Environment Management
```kotlin
// Управление конфигурацией для разных сред
object EnvironmentManager {

    fun loadConfiguration(): ApplicationConfig {
        val environment = detectEnvironment()

        return when (environment) {
            Environment.DEVELOPMENT -> loadDevelopmentConfig()
            Environment.TESTING -> loadTestConfig()
            Environment.STAGING -> loadStagingConfig()
            Environment.PRODUCTION -> loadProductionConfig()
        }
    }

    private fun detectEnvironment(): Environment {
        return when (System.getProperty("environment")?.lowercase()) {
            "prod", "production" -> Environment.PRODUCTION
            "staging" -> Environment.STAGING
            "test", "testing" -> Environment.TESTING
            else -> Environment.DEVELOPMENT
        }
    }

    private fun loadDevelopmentConfig(): ApplicationConfig {
        return ApplicationConfig.fromPropertiesResource("config-dev.properties")
            .overrideWith(System.getProperties())
            .overrideWith(System.getenv())
    }

    private fun loadTestConfig(): ApplicationConfig {
        return ApplicationConfig.fromPropertiesResource("config-test.properties")
    }

    private fun loadStagingConfig(): ApplicationConfig {
        return ApplicationConfig.fromEnvironmentVariables()
    }

    private fun loadProductionConfig(): ApplicationConfig {
        val configPath = System.getenv("CONFIG_FILE")
            ?: throw IllegalStateException("CONFIG_FILE environment variable is required in production")

        return ApplicationConfig.fromPropertiesFile(File(configPath))
    }
}

// Extension для overriding конфигурации
fun <T : Configuration> T.overrideWith(properties: Map<String, String>): T {
    return object : Configuration by this {
        override fun <U> get(key: Key<U, *>): U {
            val overrideValue = properties[key.name]
            return if (overrideValue != null) {
                // Parse override value using key's type
                when (key) {
                    is Key<String, *> -> overrideValue as U
                    is Key<Int, *> -> overrideValue.toInt() as U
                    is Key<Boolean, *> -> overrideValue.toBoolean() as U
                    else -> this@overrideWith[key] // Fallback to original
                }
            } else {
                this@overrideWith[key]
            }
        }
    }
}
```

### Security Considerations
```kotlin
// Безопасная обработка конфиденциальных данных
object SecureConfig : Configuration {
    val databasePassword by encryptedStringType
    val apiKey by encryptedStringType
    val jwtSecret by encryptedStringType

    // Валидация безопасности
    init {
        validate {
            val password = this[databasePassword]
            check(password.length >= 12) { "Database password must be at least 12 characters" }
            check(password.any { it.isUpperCase() }) { "Password must contain uppercase letter" }
            check(password.any { it.isLowerCase() }) { "Password must contain lowercase letter" }
            check(password.any { it.isDigit() }) { "Password must contain digit" }
        }
    }
}

// Кастомный тип для encrypted строк
object EncryptedStringType : PropertyType<String> {
    override fun parseValue(propertyName: String, stringValue: String): String {
        // В реальном приложении здесь должна быть дешифровка
        // return decrypt(stringValue)
        return stringValue // Заглушка
    }
}

// Функции для шифрования конфигурации
object ConfigEncryption {

    private const val ALGORITHM = "AES"
    private val key = getEncryptionKey()

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(value.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(encrypted: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted))
        return String(decrypted)
    }

    private fun getEncryptionKey(): SecretKey {
        // В реальном приложении ключ должен храниться безопасно
        val keyString = System.getenv("CONFIG_ENCRYPTION_KEY")
            ?: throw IllegalStateException("CONFIG_ENCRYPTION_KEY not set")

        return SecretKeySpec(keyString.toByteArray(), ALGORITHM)
    }
}
```

## Устранение неполадок

### Common Issues
```kotlin
object KonfigTroubleshooting {

    // Проблема: Missing required property
    fun handleMissingProperty() {
        try {
            val config = AppConfig.fromPropertiesFile(File("config.properties"))
            val port = config[AppConfig.port]
        } catch (e: KeyNotFoundException) {
            println("Required property 'port' is missing")
            // Provide default or exit
            System.exit(1)
        }
    }

    // Проблема: Invalid property type
    fun handleInvalidType() {
        try {
            val config = AppConfig.fromMap(mapOf("port" to "invalid"))
            val port = config[AppConfig.port]
        } catch (e: NumberFormatException) {
            println("Port must be a valid integer")
        }
    }

    // Проблема: Configuration validation failure
    fun handleValidationFailure() {
        try {
            val config = ValidatedConfig.fromPropertiesFile(File("config.properties"))
        } catch (e: ConfigurationException) {
            println("Configuration validation failed: ${e.message}")
            // Log detailed errors
            e.cause?.let { log.error("Validation error", it) }
        }
    }

    // Проблема: Environment variable precedence
    fun debugConfigurationSources() {
        val sources = listOf(
            "Properties file" to { AppConfig.fromPropertiesFile(File("app.properties")) },
            "System properties" to { AppConfig.fromSystemProperties() },
            "Environment variables" to { AppConfig.fromEnvironmentVariables() }
        )

        sources.forEach { (name, loader) ->
            try {
                val config = loader()
                println("$name: port = ${config[AppConfig.port]}")
            } catch (e: Exception) {
                println("$name: failed to load - ${e.message}")
            }
        }
    }

    // Проблема: Complex configuration debugging
    fun debugConfiguration(config: Configuration) {
        val debugInfo = mutableMapOf<String, Any>()

        // Try to access all known properties
        listOf(
            "database.url" to AppConfig.databaseUrl,
            "database.user" to AppConfig.databaseUser,
            "port" to AppConfig.port,
            "debug" to AppConfig.debug
        ).forEach { (name, key) ->
            try {
                debugInfo[name] = config[key]
            } catch (e: Exception) {
                debugInfo[name] = "ERROR: ${e.message}"
            }
        }

        println("Configuration Debug Info:")
        debugInfo.forEach { (key, value) ->
            println("  $key = $value")
        }
    }
}
```

### Debugging Configuration Loading
```kotlin
// Расширенный debug для загрузки конфигурации
class ConfigurationLoader {

    private val logger = LoggerFactory.getLogger(ConfigurationLoader::class.java)

    fun loadWithDebug(): Configuration {
        logger.info("Starting configuration loading")

        val configSources = listOf(
            "Properties file" to tryLoad { AppConfig.fromPropertiesResource("application.properties") },
            "System properties" to tryLoad { AppConfig.fromSystemProperties() },
            "Environment variables" to tryLoad { AppConfig.fromEnvironmentVariables() }
        )

        // Найти первый успешный источник
        for ((sourceName, configResult) in configSources) {
            when (configResult) {
                is LoadResult.Success -> {
                    logger.info("Successfully loaded configuration from $sourceName")
                    debugConfiguration(configResult.config)
                    return configResult.config
                }
                is LoadResult.Failure -> {
                    logger.warn("Failed to load from $sourceName: ${configResult.error}")
                }
            }
        }

        throw IllegalStateException("Failed to load configuration from any source")
    }

    private fun tryLoad(loader: () -> Configuration): LoadResult {
        return try {
            LoadResult.Success(loader())
        } catch (e: Exception) {
            LoadResult.Failure(e)
        }
    }

    private fun debugConfiguration(config: Configuration) {
        try {
            logger.debug("Configuration loaded:")
            logger.debug("  database.url: ${config[AppConfig.databaseUrl]}")
            logger.debug("  port: ${config[AppConfig.port]}")
            logger.debug("  debug: ${config[AppConfig.debug]}")
        } catch (e: Exception) {
            logger.warn("Failed to debug configuration: ${e.message}")
        }
    }

    sealed class LoadResult {
        data class Success(val config: Configuration) : LoadResult()
        data class Failure(val error: Exception) : LoadResult()
    }
}
```

## Руководство по миграции

### From Typesafe Config to Konfig
```kotlin
// Typesafe Config (HOCON)
val config = ConfigFactory.load()
val databaseUrl = config.getString("database.url")
val port = config.getInt("server.port")

// Konfig
object AppConfig : Configuration {
    val databaseUrl by stringType
    val port by intType
}

val config = AppConfig.fromPropertiesResource("application.properties")
val databaseUrl = config[AppConfig.databaseUrl]
val port = config[AppConfig.port]
```

### From Spring Configuration to Konfig
```kotlin
// Spring @ConfigurationProperties
@Component
@ConfigurationProperties("app")
data class AppProperties(
    var databaseUrl: String = "",
    var port: Int = 8080
)

// Konfig
object AppConfig : Configuration {
    val databaseUrl by stringType
    val port by intType
}

// Usage
val config = AppConfig.fromSystemProperties()
val databaseUrl = config[AppConfig.databaseUrl]
```

### From Environment Variables to Konfig
```kotlin
// Manual environment variable handling
val databaseUrl = System.getenv("DATABASE_URL") ?: "default"
val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

// Konfig
object AppConfig : Configuration {
    val databaseUrl by stringType.default("default")
    val port by intType.default(8080)
}

val config = AppConfig.fromEnvironmentVariables()
```

## Экспериментальные возможности

### Konfig 2.0+ Features (Future)
```kotlin
// Предполагаемые возможности Konfig 2.0+

// Native support для Kotlin value classes
@JvmInline
value class DatabaseUrl(val value: String)

val config = object : Configuration {
    val databaseUrl by valueClassType<DatabaseUrl>()
}

// Compile-time configuration validation
@ValidatedConfig
object AppConfig : Configuration {
    val port by intType.validate { it in 1024..65535 }
    val email by stringType.validate { it.contains("@") }
}

// Reactive configuration updates
interface ConfigurationWatcher {
    fun watch(key: Key<*, *>, callback: (Any) -> Unit)
}

val config = AppConfig.fromEnvironmentVariables()
config.watch(AppConfig.debug) { newValue ->
    println("Debug mode changed to: $newValue")
}

// Configuration templating
object TemplatedConfig : Configuration {
    val welcomeMessage by stringType.template(
        "Welcome {{user.name}} to {{app.name}} v{{app.version}}"
    )
}

// Structured configuration with defaults
object StructuredConfig : Configuration {
    val server by subconfig {
        port = 8080
        host = "localhost"
        ssl = subconfig {
            enabled = false
            certificate = null
        }
    }
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Konfig](https://github.com/npryce/konfig)
- [Konfig GitHub](https://github.com/npryce/konfig)
- [Configuration Management](https://12factor.net/config)
- [Typesafe Config](https://github.com/lightbend/config)

## См. также
- [Spring Configuration](spring-basics.md) - Spring конфигурация
- [HOCON](libraries.md) - Human-Optimized Config Object Notation
- [Environment Variables](linux/administration.md) - Работа с переменными окружения
