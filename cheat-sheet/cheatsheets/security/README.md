# Безопасность (Security)

**Комплексные руководства по безопасности приложений и инфраструктуры**

## 📋 Описание

Этот раздел содержит детальные руководства по безопасности: application security, infrastructure security, authentication & authorization patterns, OWASP Top 10, security tools и best practices.

## 📚 Структура раздела

### 🔐 [Application Security](application-security/)
**Безопасность приложений**

- **Authentication** - Аутентификация
- **Authorization** - Авторизация
- **OAuth2** - OAuth2 протокол
- **JWT** - JSON Web Tokens
- **Session Management** - Управление сессиями
- **Input Validation** - Валидация входных данных
- **SQL Injection Prevention** - Защита от SQL инъекций
- **XSS Prevention** - Защита от XSS

### 🏗️ [Infrastructure Security](infrastructure-security/)
**Безопасность инфраструктуры**

- **Network Security** - Сетевая безопасность
- **Container Security** - Безопасность контейнеров
- **Cloud Security** - Облачная безопасность
- **Secrets Management** - Управление секретами

### 🔒 [Data Security](data-security/)
**Безопасность данных**

- **Encryption** - Шифрование
- **Data Masking** - Маскирование данных
- **Backup Security** - Безопасность бэкапов
- **Compliance** - Соответствие требованиям

### 🛡️ [Security Practices](security-practices/)
**Практики безопасности**

- **Security Best Practices** - Лучшие практики
- **Security Auditing** - Аудит безопасности
- **Incident Response** - Реагирование на инциденты
- **Security Testing** - Тестирование безопасности

### 🔧 [Security Tools](security-tools/)
**Инструменты безопасности**

- **Vulnerability Scanning** - Сканирование уязвимостей
- **Dependency Scanning** - Сканирование зависимостей
- **Security Monitoring** - Мониторинг безопасности

## 🎯 Для кого этот раздел

### Security Engineers
- **Application Security** - защита приложений
- **Infrastructure Security** - защита инфраструктуры
- **Security Tools** - инструменты безопасности

### Developers
- **Secure Coding** - безопасное программирование
- **Authentication & Authorization** - аутентификация и авторизация
- **OWASP Top 10** - основные уязвимости

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Application Security Basics
   ├── Authentication
   ├── Authorization
   └── Input Validation

2. OWASP Top 10
   ├── SQL Injection
   ├── XSS
   └── CSRF

3. Security Best Practices
   └── Secure Coding
```

### Для опытных
```
1. Advanced Security
   ├── OAuth2 / JWT
   ├── Secrets Management
   └── Encryption

2. Infrastructure Security
   ├── Container Security
   ├── Cloud Security
   └── Network Security

3. Security Operations
   ├── Security Monitoring
   ├── Incident Response
   └── Security Auditing
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Frameworks](../frameworks/)** - Spring Security
- **[Interview](../interview/security/)** - Вопросы на собеседовании
- **[DevOps](../devops/)** - Infrastructure security
- **[Architecture](../architecture/)** - Security architecture

### Специфичные связи
- **Spring Security** → [Frameworks](../frameworks/java-frameworks/spring/spring-security.md)
- **OAuth2** → [Interview](../interview/security/oauth2-interview.md)
- **OWASP Top 10** → [Interview](../interview/security/owasp-top10-interview.md)

## 📚 Полезные ресурсы

### Официальная документация
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [OAuth2 Specification](https://oauth.net/2/)

### Учебные материалы
- [OWASP Cheat Sheets](https://cheatsheetseries.owasp.org/)
- [Security Best Practices](https://cheatsheetseries.owasp.org/cheatsheets/Index.html)

## 🎯 Следующие шаги

После изучения безопасности:
1. **Изучите Spring Security** → [Spring Security](../frameworks/java-frameworks/spring/spring-security.md)
2. **Освойте OAuth2** → [OAuth2](application-security/)
3. **Подготовьтесь к интервью** → [Interview](../interview/security/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../devops/)

*Обновлено: 2026-01-25*
