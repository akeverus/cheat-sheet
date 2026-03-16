---
title: "Go: командная строка"
description: "Полное руководство по созданию CLI приложений в Go: flag package, cobra, urfave/cli"
tags: ["go", "golang", "cli", "command-line", "flags", "cobra"]
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: командная строка

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

- [Go flag Package](https://pkg.go.dev/flag)
- [Cobra Documentation](https://github.com/spf13/cobra)
- [urfave/cli](https://github.com/urfave/cli)

## Содержание

- [Go: командная строка](#go-командная-строка)
- [Введение в **CLI**](#введение-в-cli)
  - [Основные подходы](#основные-подходы)
- [**flag package**](#flag-package)
  - [Базовое использование](#базовое-использование)
  - [Использование переменных](#использование-переменных)
  - [Кастомные типы](#кастомные-типы)
- [**Cobra**](#cobra)
  - [Установка](#установка)
  - [Базовое приложение](#базовое-приложение)
  - [Команды и подкоманды](#команды-и-подкоманды)
  - [Флаги](#флаги)
- [**urfave**/**cli**](#urfavecli)
  - [Команды и флаги](#команды-и-флаги)
  - [Практические примеры: Расширенное использование **flag**](#практические-примеры-расширенное-использование-flag)
  - [Практические примеры: Валидация флагов](#практические-примеры-валидация-флагов)
  - [Практические примеры: **Cobra** с подкомандами](#практические-примеры-cobra-с-подкомандами)
  - [Практические примеры: **Cobra** с конфигурацией](#практические-примеры-cobra-с-конфигурацией)
  - [Практические примеры: **urfave**/**cli** с командами](#практические-примеры-urfavecli-с-командами)
  - [Практические примеры: Интерактивный **CLI**](#практические-примеры-интерактивный-cli)
  - [Практические примеры: **Progress bars**](#практические-примеры-progress-bars)
  - [Практические примеры: **Shell completion**](#практические-примеры-shell-completion)
  - [Практические примеры: Цветной вывод](#практические-примеры-цветной-вывод)
  - [Практические примеры: Табличный вывод](#практические-примеры-табличный-вывод)
  - [Практические примеры: Конфигурация через **CLI**](#практические-примеры-конфигурация-через-cli)
  - [Практические примеры: Прогресс-бары и вывод](#практические-примеры-прогресс-бары-и-вывод)
  - [Практические примеры: Интерактивные команды](#практические-примеры-интерактивные-команды)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **CLI**

Go предоставляет несколько способов создания командных приложений. Понимание работы с **CLI** критично для создания утилит и инструментов.

### Основные подходы

1. **flag package** - стандартный пакет для флагов
2. **Cobra** - популярная библиотека для **CLI**
3. **urfave/cli** - простая библиотека для **CLI**

## **flag package**

### Базовое использование

```go
import (
    "flag"
    "fmt"
)

func main() {
    // Определение флагов
    name := flag.String("name", "World", "Name to greet")
    age := flag.Int("age", 0, "Age")
    verbose := flag.Bool("verbose", false, "Verbose output")
    
    // Парсинг флагов
    flag.Parse()
    
    // Использование значений
    fmt.Printf("Hello, %s! Age: %d\n", *name, *age)
    if *verbose {
        fmt.Println("Verbose mode enabled")
    }
}
```

### Использование переменных

```go
import "flag"

func main() {
    var name string
    var age int
    
    flag.StringVar(&name, "name", "World", "Name to greet")
    flag.IntVar(&age, "age", 0, "Age")
    
    flag.Parse()
    
    fmt.Printf("Hello, %s! Age: %d\n", name, age)
}
```

### Кастомные типы

```go
import "flag"

type URLValue struct {
    URL *url.URL
}

func (v URLValue) String() string {
    if v.URL != nil {
        return v.URL.String()
    }
    return ""
}

func (v URLValue) Set(s string) error {
    u, err := url.Parse(s)
    if err != nil {
        return err
    }
    v.URL = u
    return nil
}

func main() {
    var urlValue URLValue
    flag.Var(&urlValue, "url", "URL to process")
    flag.Parse()
    
    fmt.Println("URL:", urlValue.URL)
}
```

## **Cobra**

### Установка

```bash
go get -u github.com/spf13/cobra/cobra
```

### Базовое приложение

```go
import (
    "github.com/spf13/cobra"
    "fmt"
)

var rootCmd = &cobra.Command{
    Use:   "myapp",
    Short: "My application",
    Long:  "My application description",
    Run: func(cmd *cobra.Command, args []string) {
        fmt.Println("Hello from myapp")
    },
}

func main() {
    if err := rootCmd.Execute(); err != nil {
        fmt.Println(err)
        os.Exit(1)
    }
}
```

### Команды и подкоманды

```go
var versionCmd = &cobra.Command{
    Use:   "version",
    Short: "Print version",
    Run: func(cmd *cobra.Command, args []string) {
        fmt.Println("v1.0.0")
    },
}

var serveCmd = &cobra.Command{
    Use:   "serve",
    Short: "Start server",
    Run: func(cmd *cobra.Command, args []string) {
        port, _ := cmd.Flags().GetInt("port")
        fmt.Printf("Starting server on port %d\n", port)
    },
}

func init() {
    rootCmd.AddCommand(versionCmd)
    rootCmd.AddCommand(serveCmd)
    
    serveCmd.Flags().IntP("port", "p", 8080, "Server port")
}
```

### Флаги

```go
var (
    name    string
    age     int
    verbose bool
)

var rootCmd = &cobra.Command{
    Use:   "myapp",
    Run: func(cmd *cobra.Command, args []string) {
        fmt.Printf("Name: %s, Age: %d\n", name, age)
        if verbose {
            fmt.Println("Verbose mode")
        }
    },
}

func init() {
    rootCmd.Flags().StringVarP(&name, "name", "n", "World", "Name")
    rootCmd.Flags().IntVarP(&age, "age", "a", 0, "Age")
    rootCmd.Flags().BoolVarP(&verbose, "verbose", "v", false, "Verbose")
}
```

## **urfave**/**cli**

### Установка

```bash
go get -u github.com/urfave/cli/v2
```

### Базовое приложение

```go
import (
    "github.com/urfave/cli/v2"
    "log"
    "os"
)

func main() {
    app := &cli.App{
        Name:  "myapp",
        Usage: "My application",
        Action: func(c *cli.Context) error {
            fmt.Println("Hello from myapp")
            return nil
        },
    }
    
    if err := app.Run(os.Args); err != nil {
        log.Fatal(err)
    }
}
```

### Команды и флаги

```go
func main() {
    app := &cli.App{
        Name:  "myapp",
        Commands: []*cli.Command{
            {
                Name:  "serve",
                Usage: "Start server",
                Flags: []cli.Flag{
                    &cli.IntFlag{
                        Name:  "port",
                        Value: 8080,
                        Usage: "Server port",
                    },
                },
                Action: func(c *cli.Context) error {
                    port := c.Int("port")
                    fmt.Printf("Starting server on port %d\n", port)
                    return nil
                },
            },
        },
    }
    
    if err := app.Run(os.Args); err != nil {
        log.Fatal(err)
    }
}
```

### Практические примеры: Расширенное использование **flag**

```go
import (
    "flag"
    "fmt"
    "os"
)

func main() {
    var (
        configFile = flag.String("config", "config.yaml", "Path to config file")
        verbose    = flag.Bool("verbose", false, "Enable verbose output")
        port       = flag.Int("port", 8080, "Server port")
        timeout    = flag.Duration("timeout", 30*time.Second, "Request timeout")
    )
    
    flag.Usage = func() {
        fmt.Fprintf(os.Stderr, "Usage: %s [options]\n\n", os.Args[0])
        fmt.Fprintf(os.Stderr, "Options:\n")
        flag.PrintDefaults()
    }
    
    flag.Parse()
    
    if *verbose {
        fmt.Println("Verbose mode enabled")
    }
    
    fmt.Printf("Config: %s, Port: %d, Timeout: %v\n", 
        *configFile, *port, *timeout)
}
```

### Практические примеры: Валидация флагов

```go
func validateFlags() error {
    port := flag.Int("port", 8080, "Server port")
    flag.Parse()
    
    if *port < 1 || *port > 65535 {
        return fmt.Errorf("port must be between 1 and 65535")
    }
    
    return nil
}

func main() {
    if err := validateFlags(); err != nil {
        fmt.Fprintf(os.Stderr, "Error: %v\n", err)
        os.Exit(1)
    }
}
```

### Практические примеры: **Cobra** с подкомандами

```go
import (
    "github.com/spf13/cobra"
    "fmt"
)

var rootCmd = &cobra.Command{
    Use:   "myapp",
    Short: "My application",
    Long:  "My application is a CLI tool for managing resources",
}

var versionCmd = &cobra.Command{
    Use:   "version",
    Short: "Print version information",
    Run: func(cmd *cobra.Command, args []string) {
        fmt.Println("Version: 1.0.0")
    },
}

var serveCmd = &cobra.Command{
    Use:   "serve",
    Short: "Start the server",
    Long:  "Start the HTTP server on the specified port",
    RunE: func(cmd *cobra.Command, args []string) error {
        port, _ := cmd.Flags().GetInt("port")
        host, _ := cmd.Flags().GetString("host")
        
        fmt.Printf("Starting server on %s:%d\n", host, port)
        // Запуск сервера
        return nil
    },
}

var createCmd = &cobra.Command{
    Use:   "create [name]",
    Short: "Create a new resource",
    Args:  cobra.ExactArgs(1),
    RunE: func(cmd *cobra.Command, args []string) error {
        name := args[0]
        force, _ := cmd.Flags().GetBool("force")
        
        fmt.Printf("Creating resource: %s (force: %v)\n", name, force)
        // Создание ресурса
        return nil
    },
}

func init() {
    rootCmd.AddCommand(versionCmd)
    rootCmd.AddCommand(serveCmd)
    rootCmd.AddCommand(createCmd)
    
    serveCmd.Flags().IntP("port", "p", 8080, "Server port")
    serveCmd.Flags().StringP("host", "H", "localhost", "Server host")
    
    createCmd.Flags().BoolP("force", "f", false, "Force creation")
}

func main() {
    if err := rootCmd.Execute(); err != nil {
        fmt.Fprintf(os.Stderr, "Error: %v\n", err)
        os.Exit(1)
    }
}
```

### Практические примеры: **Cobra** с конфигурацией

```go
import (
    "github.com/spf13/cobra"
    "github.com/spf13/viper"
)

var rootCmd = &cobra.Command{
    Use:   "myapp",
    Short: "My application",
    PersistentPreRun: func(cmd *cobra.Command, args []string) {
        // Инициализация конфигурации
        viper.SetConfigName("config")
        viper.AddConfigPath(".")
        viper.ReadInConfig()
    },
}

func init() {
    // Глобальные флаги
    rootCmd.PersistentFlags().String("config", "", "Config file")
    rootCmd.PersistentFlags().Bool("verbose", false, "Verbose output")
    
    // Привязка к viper
    viper.BindPFlag("verbose", rootCmd.PersistentFlags().Lookup("verbose"))
}
```

### Практические примеры: **urfave**/**cli** с командами

```go
import (
    "github.com/urfave/cli/v2"
    "log"
    "os"
)

func main() {
    app := &cli.App{
        Name:  "myapp",
        Usage: "My application",
        Commands: []*cli.Command{
            {
                Name:  "serve",
                Usage: "Start the server",
                Flags: []cli.Flag{
                    &cli.IntFlag{
                        Name:    "port",
                        Aliases: []string{"p"},
                        Value:   8080,
                        Usage:   "Server port",
                    },
                    &cli.StringFlag{
                        Name:    "host",
                        Aliases: []string{"H"},
                        Value:   "localhost",
                        Usage:   "Server host",
                    },
                },
                Action: func(c *cli.Context) error {
                    port := c.Int("port")
                    host := c.String("host")
                    fmt.Printf("Starting server on %s:%d\n", host, port)
                    return nil
                },
            },
            {
                Name:  "create",
                Usage: "Create a new resource",
                Flags: []cli.Flag{
                    &cli.StringFlag{
                        Name:     "name",
                        Aliases:  []string{"n"},
                        Required: true,
                        Usage:    "Resource name",
                    },
                    &cli.BoolFlag{
                        Name:    "force",
                        Aliases: []string{"f"},
                        Usage:   "Force creation",
                    },
                },
                Action: func(c *cli.Context) error {
                    name := c.String("name")
                    force := c.Bool("force")
                    fmt.Printf("Creating resource: %s (force: %v)\n", name, force)
                    return nil
                },
            },
        },
    }
    
    if err := app.Run(os.Args); err != nil {
        log.Fatal(err)
    }
}
```

### Практические примеры: Интерактивный **CLI**

```go
import (
    "bufio"
    "fmt"
    "os"
    "strings"
)

func interactiveCLI() {
    scanner := bufio.NewScanner(os.Stdin)
    
    for {
        fmt.Print("> ")
        if !scanner.Scan() {
            break
        }
        
        line := strings.TrimSpace(scanner.Text())
        if line == "" {
            continue
        }
        
        parts := strings.Fields(line)
        command := parts[0]
        args := parts[1:]
        
        switch command {
        case "exit", "quit":
            return
        case "help":
            printHelp()
        case "create":
            if len(args) < 1 {
                fmt.Println("Usage: create <name>")
                continue
            }
            createResource(args[0])
        default:
            fmt.Printf("Unknown command: %s\n", command)
        }
    }
}
```

### Практические примеры: **Progress bars**

```go
import "github.com/schollz/progressbar/v3"

func processWithProgress(items []Item) {
    bar := progressbar.Default(int64(len(items)))
    
    for _, item := range items {
        processItem(item)
        bar.Add(1)
    }
    
    bar.Finish()
}

func processWithCustomProgress(items []Item) {
    bar := progressbar.NewOptions(len(items),
        progressbar.OptionSetDescription("Processing"),
        progressbar.OptionSetWidth(50),
        progressbar.OptionShowCount(),
        progressbar.OptionShowIts(),
        progressbar.OptionSetTheme(progressbar.Theme{Saucer: "█", SaucerPadding: "░", BarStart: "|", BarEnd: "|"}),
    )
    
    for _, item := range items {
        processItem(item)
        bar.Add(1)
    }
    
    bar.Finish()
}
```

### Практические примеры: **Shell completion**

```go
// Cobra поддерживает completion автоматически
var rootCmd = &cobra.Command{
    Use: "myapp",
}

func init() {
    rootCmd.AddCommand(&cobra.Command{
        Use:   "completion [bash|zsh|fish|powershell]",
        Short: "Generate completion script",
        Long:  "Generate shell completion script",
        Run: func(cmd *cobra.Command, args []string) {
            switch args[0] {
            case "bash":
                cmd.Root().GenBashCompletion(os.Stdout)
            case "zsh":
                cmd.Root().GenZshCompletion(os.Stdout)
            case "fish":
                cmd.Root().GenFishCompletion(os.Stdout, true)
            case "powershell":
                cmd.Root().GenPowerShellCompletion(os.Stdout)
            }
        },
    })
}
```

### Практические примеры: Цветной вывод

```go
import "github.com/fatih/color"

func coloredOutput() {
    color.Red("Error: something went wrong")
    color.Green("Success: operation completed")
    color.Yellow("Warning: check your input")
    color.Blue("Info: processing data")
    
    // Форматированный вывод
    color.New(color.FgCyan, color.Bold).Println("Bold cyan text")
    
    // Кастомные цвета
    customColor := color.New(color.FgMagenta, color.BgWhite)
    customColor.Println("Custom colored text")
}
```

### Практические примеры: Табличный вывод

```go
import "github.com/jedib0t/go-pretty/table"

func printTable(data [][]string) {
    t := table.NewWriter()
    t.SetOutputMirror(os.Stdout)
    t.AppendHeader(table.Row{"Name", "Age", "Email"})
    
    for _, row := range data {
        t.AppendRow(table.Row{row[0], row[1], row[2]})
    }
    
    t.Render()
}
```

### Практические примеры: Конфигурация через **CLI**

```go
type Config struct {
    Port     int
    Host     string
    Database string
}

func loadConfigFromCLI() (*Config, error) {
    var config Config
    
    flag.IntVar(&config.Port, "port", 8080, "Server port")
    flag.StringVar(&config.Host, "host", "localhost", "Server host")
    flag.StringVar(&config.Database, "db", "postgres", "Database name")
    
    configFile := flag.String("config", "", "Config file path")
    flag.Parse()
    
    // Загрузка из файла если указан
    if *configFile != "" {
        fileConfig, err := loadConfigFromFile(*configFile)
        if err != nil {
            return nil, err
        }
        // Мердж конфигурации
        mergeConfig(&config, fileConfig)
    }
    
    return &config, nil
}
```

### Практические примеры: Прогресс-бары и вывод

```go
import "github.com/schollz/progressbar/v3"

func ProcessWithProgress(items []string) error {
    bar := progressbar.Default(int64(len(items)))
    
    for _, item := range items {
        if err := processItem(item); err != nil {
            return err
        }
        bar.Add(1)
    }
    
    return nil
}

func ProcessWithCustomProgress(total int64, processor func(int) error) error {
    bar := progressbar.NewOptions64(total,
        progressbar.OptionSetWidth(50),
        progressbar.OptionSetDescription("Processing"),
        progressbar.OptionShowCount(),
        progressbar.OptionShowIts(),
    )
    
    for i := 0; i < int(total); i++ {
        if err := processor(i); err != nil {
            return err
        }
        bar.Add(1)
    }
    
    return nil
}
```

### Практические примеры: Цветной вывод

```go
import "github.com/fatih/color"

func ColorfulOutput() {
    color.Red("Error: %s", "Something went wrong")
    color.Green("Success: %s", "Operation completed")
    color.Yellow("Warning: %s", "Please check configuration")
    color.Blue("Info: %s", "Processing data")
    
    // Кастомные цвета
    customColor := color.New(color.FgCyan, color.Bold)
    customColor.Println("Custom message")
}

func ColoredStatus(status string) {
    switch status {
    case "success":
        color.Green("✓ %s", status)
    case "error":
        color.Red("✗ %s", status)
    case "warning":
        color.Yellow("⚠ %s", status)
    default:
        fmt.Println(status)
    }
}
```

### Практические примеры: Интерактивные команды

```go
import "github.com/AlecAivazis/survey/v2"

func InteractiveCommand() error {
    var action string
    prompt := &survey.Select{
        Message: "Choose an action:",
        Options: []string{"create", "read", "update", "delete"},
    }
    survey.AskOne(prompt, &action)
    
    var name string
    namePrompt := &survey.Input{
        Message: "Enter name:",
    }
    survey.AskOne(namePrompt, &name)
    
    var confirm bool
    confirmPrompt := &survey.Confirm{
        Message: "Are you sure?",
    }
    survey.AskOne(confirmPrompt, &confirm)
    
    if !confirm {
        return fmt.Errorf("operation cancelled")
    }
    
    return executeAction(action, name)
}
```

### Практические примеры: Табличный вывод

```go
import "github.com/jedib0t/go-pretty/v6/table"

func PrintTable(data [][]string, headers []string) {
    t := table.NewWriter()
    t.SetOutputMirror(os.Stdout)
    
    headerRow := table.Row{}
    for _, h := range headers {
        headerRow = append(headerRow, h)
    }
    t.AppendHeader(headerRow)
    
    for _, row := range data {
        tableRow := table.Row{}
        for _, cell := range row {
            tableRow = append(tableRow, cell)
        }
        t.AppendRow(tableRow)
    }
    
    t.Render()
}

// Использование
headers := []string{"Name", "Age", "City"}
data := [][]string{
    {"Alice", "30", "New York"},
    {"Bob", "25", "London"},
}
PrintTable(data, headers)
```

## Лучшие практики

1. **Используйте описательные имена** - для команд и флагов
2. **Предоставляйте help** - для всех команд и флагов
3. **Валидируйте входные данные** - проверяйте значения флагов
4. **Используйте конвенции** - короткие и длинные флаги
5. **Обрабатывайте ошибки** - правильно обрабатывайте ошибки парсинга
6. **Используйте структурированный вывод** - для машинной обработки
7. **Добавляйте progress bars** - для длительных операций
8. **Используйте цветной вывод** - для лучшей читаемости
9. **Поддерживайте completion** - для удобства использования
10. **Документируйте команды** - используйте **help** тексты
11. **Используйте интерактивные команды** - для сложного ввода
12. **Используйте табличный вывод** - для структурированных данных
13. **Добавляйте валидацию** - проверяйте входные данные
14. **Используйте конфигурационные файлы** - для сложных настроек
15. **Тестируйте команды** - проверяйте работу команд


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Создание **CLI** приложений в Go предоставляет мощные инструменты для создания утилит и инструментов. Понимание **flag package**, **Cobra**, **urfave**/**cli**, прогресс-баров, цветного вывода, интерактивных команд, табличного вывода и лучших практик критично для эффективного создания **CLI** приложений в Go. Правильное использование **CLI** инструментов позволяет создавать удобные, функциональные, интуитивные и профессиональные командные приложения, которые обеспечивают отличный пользовательский опыт.

## Дополнительные ресурсы

- [Go flag Package](https://pkg.go.dev/flag)
- [Cobra Documentation](https://github.com/spf13/cobra)
- [urfave/cli](https://github.com/urfave/cli)
