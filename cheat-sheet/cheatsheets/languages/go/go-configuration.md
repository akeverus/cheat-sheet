---
title: "Go: конфигурация"
description: "Полное руководство по управлению конфигурацией в Go: переменные окружения, файлы конфигурации, viper"
tags: ["go", "golang", "configuration", "env", "viper", "config"]
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2025-01-11"
---

# Go: конфигурация

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

- [Viper Documentation](https://github.com/spf13/viper)
- [Go os Package](https://pkg.go.dev/os)

## Содержание

- [Введение в конфигурацию](#введение-в-конфигурацию)
- [Переменные окружения](#переменные-окружения)
- [Файлы конфигурации](#файлы-конфигурации)
- [Viper](#viper)
- [Лучшие практики](#лучшие-практики)

## Введение в конфигурацию

Управление конфигурацией критично для создания гибких приложений. Go предоставляет несколько способов работы с конфигурацией.

### Основные подходы

1. **Переменные окружения** - простой способ конфигурации
2. **Файлы конфигурации** - JSON, YAML, TOML
3. **Viper** - универсальная библиотека для конфигурации

## Переменные окружения

### Базовое использование

```go
import "os"

func getConfig() {
    port := os.Getenv("PORT")
    if port == "" {
        port = "8080"  // Значение по умолчанию
    }
    
    dbHost := os.Getenv("DB_HOST")
    dbPort := os.Getenv("DB_PORT")
}
```

### LookupEnv

```go
import "os"

func getConfig() {
    port, exists := os.LookupEnv("PORT")
    if !exists {
        port = "8080"
    }
}
```

### Парсинг переменных окружения

```go
import (
    "os"
    "strconv"
)

type Config struct {
    Port     int
    DBHost   string
    DBPort   int
    Debug    bool
}

func loadConfigFromEnv() (*Config, error) {
    config := &Config{}
    
    portStr := os.Getenv("PORT")
    if portStr == "" {
        portStr = "8080"
    }
    port, err := strconv.Atoi(portStr)
    if err != nil {
        return nil, err
    }
    config.Port = port
    
    config.DBHost = os.Getenv("DB_HOST")
    if config.DBHost == "" {
        config.DBHost = "localhost"
    }
    
    dbPortStr := os.Getenv("DB_PORT")
    if dbPortStr == "" {
        dbPortStr = "5432"
    }
    dbPort, err := strconv.Atoi(dbPortStr)
    if err != nil {
        return nil, err
    }
    config.DBPort = dbPort
    
    debugStr := os.Getenv("DEBUG")
    config.Debug = debugStr == "true"
    
    return config, nil
}
```

## Файлы конфигурации

### JSON конфигурация

```go
import (
    "encoding/json"
    "os"
)

type Config struct {
    Port   int    `json:"port"`
    DBHost string `json:"db_host"`
    DBPort int    `json:"db_port"`
}

func loadConfigFromJSON(filename string) (*Config, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }
    
    var config Config
    if err := json.Unmarshal(data, &config); err != nil {
        return nil, err
    }
    
    return &config, nil
}
```

### YAML конфигурация

```go
import "gopkg.in/yaml.v3"

func loadConfigFromYAML(filename string) (*Config, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }
    
    var config Config
    if err := yaml.Unmarshal(data, &config); err != nil {
        return nil, err
    }
    
    return &config, nil
}
```

## Viper

### Установка

```bash
go get github.com/spf13/viper
```

### Базовое использование

```go
import "github.com/spf13/viper"

func init() {
    viper.SetConfigName("config")
    viper.SetConfigType("yaml")
    viper.AddConfigPath(".")
    
    viper.AutomaticEnv()
    
    if err := viper.ReadInConfig(); err != nil {
        // Конфигурационный файл не найден
    }
}

func getConfig() {
    port := viper.GetInt("port")
    dbHost := viper.GetString("db.host")
    dbPort := viper.GetInt("db.port")
}
```

### Приоритет источников

```go
func init() {
    // 1. Переменные окружения
    viper.AutomaticEnv()
    viper.SetEnvPrefix("APP")
    
    // 2. Файл конфигурации
    viper.SetConfigName("config")
    viper.SetConfigType("yaml")
    viper.AddConfigPath(".")
    
    // 3. Значения по умолчанию
    viper.SetDefault("port", 8080)
    viper.SetDefault("db.host", "localhost")
    
    viper.ReadInConfig()
}
```

### Watch конфигурации

```go
func watchConfig() {
    viper.WatchConfig()
    viper.OnConfigChange(func(e fsnotify.Event) {
        fmt.Println("Config file changed:", e.Name)
        // Перезагрузка конфигурации
    })
}
```

### Практические примеры: Структурированная конфигурация

```go
type Config struct {
    Server   ServerConfig   `yaml:"server"`
    Database DatabaseConfig `yaml:"database"`
    Cache    CacheConfig    `yaml:"cache"`
    Logging  LoggingConfig  `yaml:"logging"`
}

type ServerConfig struct {
    Host         string        `yaml:"host"`
    Port         int           `yaml:"port"`
    ReadTimeout  time.Duration `yaml:"read_timeout"`
    WriteTimeout time.Duration `yaml:"write_timeout"`
}

type DatabaseConfig struct {
    Host     string `yaml:"host"`
    Port     int    `yaml:"port"`
    Database string `yaml:"database"`
    Username string `yaml:"username"`
    Password string `yaml:"password"`
    MaxConns int    `yaml:"max_conns"`
}

type CacheConfig struct {
    Type     string        `yaml:"type"`
    Host     string        `yaml:"host"`
    Port     int           `yaml:"port"`
    TTL      time.Duration `yaml:"ttl"`
    MaxItems int           `yaml:"max_items"`
}

type LoggingConfig struct {
    Level  string `yaml:"level"`
    Format string `yaml:"format"`
    Output string `yaml:"output"`
}

func LoadConfig(filename string) (*Config, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }
    
    var config Config
    if err := yaml.Unmarshal(data, &config); err != nil {
        return nil, err
    }
    
    // Переопределение из переменных окружения
    if host := os.Getenv("SERVER_HOST"); host != "" {
        config.Server.Host = host
    }
    if port := os.Getenv("SERVER_PORT"); port != "" {
        if p, err := strconv.Atoi(port); err == nil {
            config.Server.Port = p
        }
    }
    
    return &config, nil
}
```

### Практические примеры: Валидация конфигурации

```go
func (c *Config) Validate() error {
    if c.Server.Port < 1 || c.Server.Port > 65535 {
        return fmt.Errorf("invalid server port: %d", c.Server.Port)
    }
    
    if c.Database.Host == "" {
        return fmt.Errorf("database host is required")
    }
    
    if c.Database.Port < 1 || c.Database.Port > 65535 {
        return fmt.Errorf("invalid database port: %d", c.Database.Port)
    }
    
    if c.Cache.TTL <= 0 {
        return fmt.Errorf("cache TTL must be positive")
    }
    
    validLogLevels := map[string]bool{
        "debug": true, "info": true, "warn": true, "error": true,
    }
    if !validLogLevels[c.Logging.Level] {
        return fmt.Errorf("invalid log level: %s", c.Logging.Level)
    }
    
    return nil
}
```

### Практические примеры: Многоуровневая конфигурация

```go
type ConfigLoader struct {
    defaults map[string]interface{}
    filePath string
    envPrefix string
}

func NewConfigLoader(filePath, envPrefix string) *ConfigLoader {
    return &ConfigLoader{
        defaults: make(map[string]interface{}),
        filePath: filePath,
        envPrefix: envPrefix,
    }
}

func (l *ConfigLoader) SetDefault(key string, value interface{}) {
    l.defaults[key] = value
}

func (l *ConfigLoader) Load() (*Config, error) {
    config := &Config{}
    
    // 1. Загрузка значений по умолчанию
    for key, value := range l.defaults {
        setConfigValue(config, key, value)
    }
    
    // 2. Загрузка из файла
    if l.filePath != "" {
        if err := l.loadFromFile(config, l.filePath); err != nil {
            return nil, err
        }
    }
    
    // 3. Переопределение из переменных окружения
    l.loadFromEnv(config)
    
    // 4. Валидация
    if err := config.Validate(); err != nil {
        return nil, err
    }
    
    return config, nil
}

func (l *ConfigLoader) loadFromFile(config *Config, filename string) error {
    data, err := os.ReadFile(filename)
    if err != nil {
        return err
    }
    
    var fileConfig Config
    if err := yaml.Unmarshal(data, &fileConfig); err != nil {
        return err
    }
    
    // Мердж конфигурации
    mergeConfig(config, &fileConfig)
    return nil
}

func (l *ConfigLoader) loadFromEnv(config *Config) {
    // Загрузка переменных окружения с префиксом
    for _, env := range os.Environ() {
        parts := strings.SplitN(env, "=", 2)
        if len(parts) != 2 {
            continue
        }
        
        key := parts[0]
        value := parts[1]
        
        if l.envPrefix != "" && !strings.HasPrefix(key, l.envPrefix) {
            continue
        }
        
        // Удаление префикса и преобразование в lowercase
        if l.envPrefix != "" {
            key = strings.TrimPrefix(key, l.envPrefix+"_")
        }
        key = strings.ToLower(key)
        key = strings.ReplaceAll(key, "_", ".")
        
        setConfigValueFromString(config, key, value)
    }
}
```

### Практические примеры: Hot reload конфигурации

```go
import "github.com/fsnotify/fsnotify"

type ConfigManager struct {
    config     *Config
    configPath string
    watcher    *fsnotify.Watcher
    mu         sync.RWMutex
    callbacks  []func(*Config)
}

func NewConfigManager(configPath string) (*ConfigManager, error) {
    watcher, err := fsnotify.NewWatcher()
    if err != nil {
        return nil, err
    }
    
    cm := &ConfigManager{
        configPath: configPath,
        watcher:    watcher,
        callbacks:  make([]func(*Config), 0),
    }
    
    if err := cm.loadConfig(); err != nil {
        return nil, err
    }
    
    if err := watcher.Add(configPath); err != nil {
        return nil, err
    }
    
    go cm.watch()
    
    return cm, nil
}

func (cm *ConfigManager) loadConfig() error {
    config, err := LoadConfig(cm.configPath)
    if err != nil {
        return err
    }
    
    cm.mu.Lock()
    cm.config = config
    cm.mu.Unlock()
    
    // Вызов callback'ов
    for _, callback := range cm.callbacks {
        callback(config)
    }
    
    return nil
}

func (cm *ConfigManager) watch() {
    for {
        select {
        case event := <-cm.watcher.Events:
            if event.Op&fsnotify.Write == fsnotify.Write {
                if err := cm.loadConfig(); err != nil {
                    log.Printf("Error reloading config: %v", err)
                } else {
                    log.Println("Config reloaded successfully")
                }
            }
        case err := <-cm.watcher.Errors:
            log.Printf("Config watcher error: %v", err)
        }
    }
}

func (cm *ConfigManager) GetConfig() *Config {
    cm.mu.RLock()
    defer cm.mu.RUnlock()
    return cm.config
}

func (cm *ConfigManager) OnReload(callback func(*Config)) {
    cm.mu.Lock()
    defer cm.mu.Unlock()
    cm.callbacks = append(cm.callbacks, callback)
}
```

### Практические примеры: Конфигурация для разных окружений

```go
type Environment string

const (
    Development Environment = "development"
    Staging     Environment = "staging"
    Production  Environment = "production"
)

func LoadConfigForEnvironment(env Environment) (*Config, error) {
    var configPath string
    
    switch env {
    case Development:
        configPath = "config.dev.yaml"
    case Staging:
        configPath = "config.staging.yaml"
    case Production:
        configPath = "config.prod.yaml"
    default:
        return nil, fmt.Errorf("unknown environment: %s", env)
    }
    
    // Переопределение через переменную окружения
    if path := os.Getenv("CONFIG_PATH"); path != "" {
        configPath = path
    }
    
    return LoadConfig(configPath)
}

func GetEnvironment() Environment {
    env := os.Getenv("ENVIRONMENT")
    switch env {
    case "development", "dev":
        return Development
    case "staging", "stage":
        return Staging
    case "production", "prod":
        return Production
    default:
        return Development
    }
}
```

### Практические примеры: Секреты в конфигурации

```go
import "github.com/spf13/viper"

type SecretManager interface {
    GetSecret(key string) (string, error)
}

type ConfigWithSecrets struct {
    PublicConfig Config
    secrets      SecretManager
}

func LoadConfigWithSecrets(configPath string, secretManager SecretManager) (*ConfigWithSecrets, error) {
    config, err := LoadConfig(configPath)
    if err != nil {
        return nil, err
    }
    
    // Загрузка секретов
    if config.Database.Password == "" {
        password, err := secretManager.GetSecret("database/password")
        if err != nil {
            return nil, err
        }
        config.Database.Password = password
    }
    
    return &ConfigWithSecrets{
        PublicConfig: *config,
        secrets:      secretManager,
    }, nil
}
```

### Практические примеры: Viper с расширенными возможностями

```go
import "github.com/spf13/viper"

func setupViper() {
    // Настройка путей поиска
    viper.SetConfigName("config")
    viper.SetConfigType("yaml")
    viper.AddConfigPath(".")
    viper.AddConfigPath("$HOME/.myapp")
    viper.AddConfigPath("/etc/myapp")
    
    // Переменные окружения
    viper.AutomaticEnv()
    viper.SetEnvPrefix("MYAPP")
    viper.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))
    
    // Значения по умолчанию
    viper.SetDefault("server.port", 8080)
    viper.SetDefault("server.host", "localhost")
    viper.SetDefault("database.max_conns", 10)
    
    // Чтение конфигурации
    if err := viper.ReadInConfig(); err != nil {
        if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
            log.Fatalf("Error reading config: %v", err)
        }
    }
    
    // Watch для hot reload
    viper.WatchConfig()
    viper.OnConfigChange(func(e fsnotify.Event) {
        log.Printf("Config file changed: %s", e.Name)
    })
}

func getConfigFromViper() *Config {
    return &Config{
        Server: ServerConfig{
            Host: viper.GetString("server.host"),
            Port: viper.GetInt("server.port"),
        },
        Database: DatabaseConfig{
            Host:     viper.GetString("database.host"),
            Port:     viper.GetInt("database.port"),
            Database: viper.GetString("database.database"),
            Username: viper.GetString("database.username"),
            Password: viper.GetString("database.password"),
            MaxConns: viper.GetInt("database.max_conns"),
        },
    }
}
```

### Практические примеры: Конфигурация с флагами командной строки

```go
import "flag"

func LoadConfigWithFlags() (*Config, error) {
    var (
        configPath = flag.String("config", "config.yaml", "Path to config file")
        port       = flag.Int("port", 0, "Server port (overrides config)")
        dbHost     = flag.String("db-host", "", "Database host (overrides config)")
    )
    flag.Parse()
    
    config, err := LoadConfig(*configPath)
    if err != nil {
        return nil, err
    }
    
    // Переопределение из флагов
    if *port > 0 {
        config.Server.Port = *port
    }
    if *dbHost != "" {
        config.Database.Host = *dbHost
    }
    
    return config, nil
}
```

### Практические примеры: Конфигурация с валидацией через теги

```go
import "github.com/go-playground/validator/v10"

type ValidatedConfig struct {
    Server   ServerConfig   `validate:"required"`
    Database DatabaseConfig `validate:"required"`
}

func (c *ValidatedConfig) Validate() error {
    validate := validator.New()
    return validate.Struct(c)
}

func LoadAndValidateConfig(filename string) (*ValidatedConfig, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }
    
    var config ValidatedConfig
    if err := yaml.Unmarshal(data, &config); err != nil {
        return nil, err
    }
    
    if err := config.Validate(); err != nil {
        return nil, err
    }
    
    return &config, nil
}
```

### Практические примеры: Конфигурация с типами

```go
type ConfigValue struct {
    value interface{}
    source string
}

type TypedConfig struct {
    values map[string]ConfigValue
    mu     sync.RWMutex
}

func NewTypedConfig() *TypedConfig {
    return &TypedConfig{
        values: make(map[string]ConfigValue),
    }
}

func (c *TypedConfig) Set(key string, value interface{}, source string) {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.values[key] = ConfigValue{value: value, source: source}
}

func (c *TypedConfig) GetString(key string) (string, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    
    cv, exists := c.values[key]
    if !exists {
        return "", fmt.Errorf("key %s not found", key)
    }
    
    str, ok := cv.value.(string)
    if !ok {
        return "", fmt.Errorf("key %s is not a string", key)
    }
    
    return str, nil
}

func (c *TypedConfig) GetInt(key string) (int, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    
    cv, exists := c.values[key]
    if !exists {
        return 0, fmt.Errorf("key %s not found", key)
    }
    
    switch v := cv.value.(type) {
    case int:
        return v, nil
    case string:
        return strconv.Atoi(v)
    default:
        return 0, fmt.Errorf("key %s cannot be converted to int", key)
    }
}
```

### Практические примеры: Hot reload конфигурации

```go
type ConfigWatcher struct {
    config  *Config
    mu      sync.RWMutex
    watcher *fsnotify.Watcher
}

func NewConfigWatcher(configPath string) (*ConfigWatcher, error) {
    watcher, err := fsnotify.NewWatcher()
    if err != nil {
        return nil, err
    }
    
    if err := watcher.Add(configPath); err != nil {
        return nil, err
    }
    
    cw := &ConfigWatcher{
        watcher: watcher,
    }
    
    if err := cw.reload(); err != nil {
        return nil, err
    }
    
    go cw.watch()
    
    return cw, nil
}

func (cw *ConfigWatcher) reload() error {
    // Загрузка и валидация конфигурации
    // ...
    return nil
}

func (cw *ConfigWatcher) watch() {
    for {
        select {
        case event, ok := <-cw.watcher.Events:
            if !ok {
                return
            }
            if event.Op&fsnotify.Write == fsnotify.Write {
                cw.reload()
            }
        case err := <-cw.watcher.Errors:
            log.Printf("Config watcher error: %v", err)
        }
    }
}

func (cw *ConfigWatcher) Get() *Config {
    cw.mu.RLock()
    defer cw.mu.RUnlock()
    return cw.config
}
```

### Практические примеры: Загрузка конфигурации с приоритетами

```go
func LoadConfigWithPriority() (*Config, error) {
    config := &Config{}
    
    // 1. Значения по умолчанию
    config.SetDefaults()
    
    // 2. Загрузка из файла
    if err := config.LoadFromFile("config.yaml"); err != nil && !os.IsNotExist(err) {
        return nil, err
    }
    
    // 3. Переменные окружения (высший приоритет)
    config.LoadFromEnv()
    
    // 4. Валидация
    if err := config.Validate(); err != nil {
        return nil, err
    }
    
    return config, nil
}

func (c *Config) LoadFromEnv() {
    if dbHost := os.Getenv("DB_HOST"); dbHost != "" {
        c.DB.Host = dbHost
    }
    if dbPort := os.Getenv("DB_PORT"); dbPort != "" {
        if port, err := strconv.Atoi(dbPort); err == nil {
            c.DB.Port = port
        }
    }
    // ... больше переменных окружения
}
```

### Практические примеры: Конфигурация с валидацией

```go
type ConfigValidator interface {
    Validate() error
}

func (c *Config) Validate() error {
    if c.DB.Host == "" {
        return fmt.Errorf("DB host is required")
    }
    
    if c.DB.Port < 1 || c.DB.Port > 65535 {
        return fmt.Errorf("DB port must be between 1 and 65535")
    }
    
    if c.Server.Port < 1 || c.Server.Port > 65535 {
        return fmt.Errorf("Server port must be between 1 and 65535")
    }
    
    if c.Server.Timeout < time.Second {
        return fmt.Errorf("Server timeout must be at least 1 second")
    }
    
    return nil
}
```

### Практические примеры: Конфигурация для разных окружений

```go
type Environment string

const (
    EnvDevelopment Environment = "development"
    EnvStaging     Environment = "staging"
    EnvProduction  Environment = "production"
)

func LoadConfigForEnvironment(env Environment) (*Config, error) {
    var configPath string
    
    switch env {
    case EnvDevelopment:
        configPath = "config.dev.yaml"
    case EnvStaging:
        configPath = "config.staging.yaml"
    case EnvProduction:
        configPath = "config.prod.yaml"
    default:
        return nil, fmt.Errorf("unknown environment: %s", env)
    }
    
    envVar := os.Getenv("ENV")
    if envVar != "" {
        configPath = fmt.Sprintf("config.%s.yaml", envVar)
    }
    
    return LoadConfigFromFile(configPath)
}
```

## Лучшие практики

1. **Используйте переменные окружения** - для секретов и чувствительных данных
2. **Предоставляйте значения по умолчанию** - для удобства использования
3. **Валидируйте конфигурацию** - проверяйте корректность значений
4. **Используйте структуры** - для типобезопасной конфигурации
5. **Документируйте конфигурацию** - объясняйте назначение параметров
6. **Используйте hot reload** - для обновления конфигурации без перезапуска
7. **Разделяйте по окружениям** - используйте разные файлы для разных окружений
8. **Используйте приоритеты** - переменные окружения > файл > значения по умолчанию
9. **Храните секреты отдельно** - используйте secret managers для секретов
10. **Используйте валидацию** - проверяйте конфигурацию при загрузке
11. **Используйте hot reload** - для обновления конфигурации во время выполнения
12. **Используйте приоритеты** - правильно определяйте порядок загрузки
13. **Валидируйте конфигурацию** - проверяйте корректность перед использованием
14. **Разделяйте по окружениям** - используйте разные конфигурации для разных сред
15. **Мониторьте изменения** - отслеживайте изменения конфигурации

## Заключение

Управление конфигурацией в Go предоставляет гибкие инструменты для настройки приложений. Понимание переменных окружения, файлов конфигурации, Viper, hot reload, валидации, приоритетов, окружений и практических применений критично для эффективного управления конфигурацией в Go. Правильная настройка конфигурации позволяет создавать гибкие, легко настраиваемые, безопасные приложения, которые можно адаптировать для различных окружений и легко изменять без перезапуска.

## Дополнительные ресурсы

- [Viper Documentation](https://github.com/spf13/viper)
- [Go os Package](https://pkg.go.dev/os)

