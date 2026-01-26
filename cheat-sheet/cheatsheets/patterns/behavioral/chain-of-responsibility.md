# Chain of Responsibility (Цепочка ответственности)

Паттерн Chain of Responsibility (Цепочка ответственности) — это поведенческий паттерн проектирования, который позволяет передавать запросы по цепочке обработчиков. Каждый обработчик решает, может ли он обработать запрос, и при необходимости передает его дальше по цепочке.

**Дата последнего обновления:** 2025-01-15

## Содержание

- [Полезные ссылки](#�-олезн�-е-�-�-�-лки)
  - [Официальная документация](#�-�-и�-иал�-ная-док�-мен�-а�-ия)
  - [Baeldung](#baeldung)
  - [См. также](#�-м-�-акже)
- [Оглавление](#�-главление)
- [Когда полезен](#�-огда-полезен)
- [Java Implementation](#java-implementation)
  - [Пример: обработка аутентификации](#�-�-име�-об�-або�-ка-а�-�-ен�-и�-ика�-ии)



## Полезные ссылки

### Официальная документация
- [Design Patterns: Elements of Reusable Object-Oriented Software](https://en.wikipedia.org/wiki/Design_Patterns)

### Baeldung
- [Chain of Responsibility Pattern in Java](https://www.baeldung.com/chain-of-responsibility-pattern)
- [Chain of Responsibility Pattern in Kotlin](https://www.baeldung.com/kotlin-chain-of-responsibility-pattern)

### См. также
- [Command (Команда)](command.md)
- [Mediator (Посредник)](mediator.md)

## Оглавление

- [Введение](#введение)
- [Когда полезен](#когда-полезен)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Замечания](#замечания)

## Когда полезен

- Разделение отправителя и получателя запроса
- Динамический выбор обработчика во время выполнения
- Упрощение сложных if/else конструкций

## Java Implementation

### Пример: обработка аутентификации

Базовый обработчик:

```java
public abstract class AuthenticationProcessor {
    protected AuthenticationProcessor nextProcessor;

    public AuthenticationProcessor(AuthenticationProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public abstract boolean isAuthorized(AuthenticationProvider authProvider);
}
```

Два конкретных обработчика:

```java
public class OAuthProcessor extends AuthenticationProcessor {
    public OAuthProcessor(AuthenticationProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public boolean isAuthorized(AuthenticationProvider authProvider) {
        if (authProvider instanceof OAuthTokenProvider) {
            return true;
        } else if (nextProcessor != null) {
            return nextProcessor.isAuthorized(authProvider);
        }
        return false;
    }
}

public class UsernamePasswordProcessor extends AuthenticationProcessor {
    public UsernamePasswordProcessor(AuthenticationProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public boolean isAuthorized(AuthenticationProvider authProvider) {
        if (authProvider instanceof UsernamePasswordProvider) {
            return true;
        } else if (nextProcessor != null) {
            return nextProcessor.isAuthorized(authProvider);
        }
        return false;
    }
}
```

Сборка цепочки и тест:

```java
private static AuthenticationProcessor getChainOfAuthProcessor() {
    AuthenticationProcessor oAuthProcessor = new OAuthProcessor(null);
    return new UsernamePasswordProcessor(oAuthProcessor);
}

@Test
public void givenOAuthProvider_whenCheckingAuthorized_thenSuccess() {
    AuthenticationProcessor processor = getChainOfAuthProcessor();
    assertTrue(processor.isAuthorized(new OAuthTokenProvider()));
}
```

## Kotlin Implementation

В Kotlin паттерн Chain of Responsibility может быть реализован следующим образом:

```kotlin
interface AuthenticationProvider

class OAuthTokenProvider : AuthenticationProvider
class UsernamePasswordProvider : AuthenticationProvider

abstract class AuthenticationProcessor(protected val nextProcessor: AuthenticationProcessor?) {
    abstract fun isAuthorized(authProvider: AuthenticationProvider): Boolean
}

class OAuthProcessor(nextProcessor: AuthenticationProcessor?) : AuthenticationProcessor(nextProcessor) {
    override fun isAuthorized(authProvider: AuthenticationProvider): Boolean {
        return when (authProvider) {
            is OAuthTokenProvider -> true
            else -> nextProcessor?.isAuthorized(authProvider) ?: false
        }
    }
}

class UsernamePasswordProcessor(nextProcessor: AuthenticationProcessor?) : AuthenticationProcessor(nextProcessor) {
    override fun isAuthorized(authProvider: AuthenticationProvider): Boolean {
        return when (authProvider) {
            is UsernamePasswordProvider -> true
            else -> nextProcessor?.isAuthorized(authProvider) ?: false
        }
    }
}
```

Использование:

```kotlin
fun getChainOfAuthProcessor(): AuthenticationProcessor {
    val oAuthProcessor = OAuthProcessor(null)
    return UsernamePasswordProcessor(oAuthProcessor)
}

fun main() {
    val processor = getChainOfAuthProcessor()
    val isAuthorized = processor.isAuthorized(OAuthTokenProvider())
    println(isAuthorized) // true
}
```

## Замечания

- Если обработчик не может обработать запрос, он обязан передать его дальше (если цепочка не заканчивается).
- Хорошая практика — явное поведение при "не обработано" (например, логирование или исключение).
