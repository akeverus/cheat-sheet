---
title: "Go: криптография"
description: "Полное руководство по криптографии в Go: хеширование, шифрование, подписи, случайные числа, TLS"
tags:
  - go
  - golang
  - crypto
  - encryption
  - hashing
  - tls
difficulty: "advanced"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: криптография

## Полезные ссылки

- [Go crypto Package](https://pkg.go.dev/crypto)
- [Go crypto/hash](https://pkg.go.dev/crypto/hash)
- [Go crypto/cipher](https://pkg.go.dev/crypto/cipher)

## Содержание

- [Go: криптография](#go-криптография)
- [Введение в криптографию](#введение-в-криптографию)
  - [Основные концепции](#основные-концепции)
- [Хеширование](#хеширование)
  - [MD5 (не рекомендуется для безопасности)](#md5-не-рекомендуется-для-безопасности)
  - [SHA-256](#sha-256)
  - [SHA-512](#sha-512)
  - [HMAC](#hmac)
- [Шифрование](#шифрование)
  - [AES шифрование](#aes-шифрование)
  - [GCM режим](#gcm-режим)
- [Цифровые подписи](#цифровые-подписи)
  - [RSA подписи](#rsa-подписи)
  - [ECDSA подписи](#ecdsa-подписи)
- [Случайные числа](#случайные-числа)
  - [Криптографически стойкие случайные числа](#криптографически-стойкие-случайные-числа)
- [TLS](#tls)
  - [Создание TLS соединения](#создание-tls-соединения)
  - [TLS сервер](#tls-сервер)
  - [Практические примеры: Генерация ключей](#практические-примеры-генерация-ключей)
  - [Практические примеры: Хеширование паролей](#практические-примеры-хеширование-паролей)
  - [Практические примеры: Шифрование с ключом из пароля](#практические-примеры-шифрование-с-ключом-из-пароля)
  - [Практические примеры: Генерация токенов](#практические-примеры-генерация-токенов)
  - [Практические примеры: Проверка целостности данных](#практические-примеры-проверка-целостности-данных)
  - [Практические примеры: Шифрование файлов](#практические-примеры-шифрование-файлов)
  - [Практические примеры: TLS с кастомными сертификатами](#практические-примеры-tls-с-кастомными-сертификатами)
  - [Практические примеры: Хранение и хеширование паролей](#практические-примеры-хранение-и-хеширование-паролей)
  - [Практические примеры: Аутентификация токенов](#практические-примеры-аутентификация-токенов)
  - [Практические примеры: Безопасное хранение секретов](#практические-примеры-безопасное-хранение-секретов)
  - [Практические примеры: Постоянное время сравнения](#практические-примеры-постоянное-время-сравнения)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в криптографию

Go предоставляет мощные инструменты для криптографии через пакет `crypto` и связанные пакеты. Понимание криптографии критично для создания безопасных приложений.

### Основные концепции

1. **Хеширование** — преобразование данных в фиксированный размер
2. **Шифрование** — преобразование данных для защиты конфиденциальности
3. **Цифровые подписи** — проверка подлинности и целостности данных
4. **Случайные числа** — генерация криптографически стойких случайных чисел

## Хеширование

### MD5 (не рекомендуется для безопасности)

```go
import (
    "crypto/md5"
    "fmt"
)

func hashMD5(data []byte) string {
    hash := md5.Sum(data)
    return fmt.Sprintf("%x", hash)
}
```

### SHA-256

```go
import (
    "crypto/sha256"
    "fmt"
)

func hashSHA256(data []byte) string {
    hash := sha256.Sum256(data)
    return fmt.Sprintf("%x", hash)
}
```

### SHA-512

```go
import (
    "crypto/sha512"
    "fmt"
)

func hashSHA512(data []byte) string {
    hash := sha512.Sum512(data)
    return fmt.Sprintf("%x", hash)
}
```

### HMAC

```go
import (
    "crypto/hmac"
    "crypto/sha256"
    "fmt"
)

func hmacSHA256(key, data []byte) string {
    mac := hmac.New(sha256.New, key)
    mac.Write(data)
    return fmt.Sprintf("%x", mac.Sum(nil))
}
```

## Шифрование

### AES шифрование

```go
import (
    "crypto/aes"
    "crypto/cipher"
    "crypto/rand"
    "io"
)

func encryptAES(key, plaintext []byte) ([]byte, error) {
    block, err := aes.NewCipher(key)
    if err != nil {
        return nil, err
    }

    ciphertext := make([]byte, aes.BlockSize+len(plaintext))
    iv := ciphertext[:aes.BlockSize]
    if _, err := io.ReadFull(rand.Reader, iv); err != nil {
        return nil, err
    }

    stream := cipher.NewCFBEncrypter(block, iv)
    stream.XORKeyStream(ciphertext[aes.BlockSize:], plaintext)

    return ciphertext, nil
}

func decryptAES(key, ciphertext []byte) ([]byte, error) {
    block, err := aes.NewCipher(key)
    if err != nil {
        return nil, err
    }

    if len(ciphertext) < aes.BlockSize {
        return nil, fmt.Errorf("ciphertext too short")
    }

    iv := ciphertext[:aes.BlockSize]
    ciphertext = ciphertext[aes.BlockSize:]

    stream := cipher.NewCFBDecrypter(block, iv)
    stream.XORKeyStream(ciphertext, ciphertext)

    return ciphertext, nil
}
```

### GCM режим

```go
import (
    "crypto/aes"
    "crypto/cipher"
    "crypto/rand"
    "io"
)

func encryptGCM(key, plaintext []byte) ([]byte, error) {
    block, err := aes.NewCipher(key)
    if err != nil {
        return nil, err
    }

    gcm, err := cipher.NewGCM(block)
    if err != nil {
        return nil, err
    }

    nonce := make([]byte, gcm.NonceSize())
    if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
        return nil, err
    }

    ciphertext := gcm.Seal(nonce, nonce, plaintext, nil)
    return ciphertext, nil
}

func decryptGCM(key, ciphertext []byte) ([]byte, error) {
    block, err := aes.NewCipher(key)
    if err != nil {
        return nil, err
    }

    gcm, err := cipher.NewGCM(block)
    if err != nil {
        return nil, err
    }

    nonceSize := gcm.NonceSize()
    if len(ciphertext) < nonceSize {
        return nil, fmt.Errorf("ciphertext too short")
    }

    nonce, ciphertext := ciphertext[:nonceSize], ciphertext[nonceSize:]
    return gcm.Open(nil, nonce, ciphertext, nil)
}
```

## Цифровые подписи

### RSA подписи

```go
import (
    "crypto"
    "crypto/rand"
    "crypto/rsa"
    "crypto/sha256"
)

func signRSA(privateKey *rsa.PrivateKey, data []byte) ([]byte, error) {
    hash := sha256.Sum256(data)
    return rsa.SignPKCS1v15(rand.Reader, privateKey, crypto.SHA256, hash[:])
}

func verifyRSA(publicKey *rsa.PublicKey, data, signature []byte) error {
    hash := sha256.Sum256(data)
    return rsa.VerifyPKCS1v15(publicKey, crypto.SHA256, hash[:], signature)
}
```

### ECDSA подписи

```go
import (
    "crypto/ecdsa"
    "crypto/rand"
    "crypto/sha256"
    "math/big"
)

func signECDSA(privateKey *ecdsa.PrivateKey, data []byte) (*big.Int, *big.Int, error) {
    hash := sha256.Sum256(data)
    return ecdsa.Sign(rand.Reader, privateKey, hash[:])
}

func verifyECDSA(publicKey *ecdsa.PublicKey, data []byte, r, s *big.Int) bool {
    hash := sha256.Sum256(data)
    return ecdsa.Verify(publicKey, hash[:], r, s)
}
```

## Случайные числа

### Криптографически стойкие случайные числа

```go
import (
    "crypto/rand"
    "encoding/binary"
)

func randomBytes(n int) ([]byte, error) {
    bytes := make([]byte, n)
    _, err := rand.Read(bytes)
    return bytes, err
}

func randomInt32() (int32, error) {
    var b [4]byte
    if _, err := rand.Read(b[:]); err != nil {
        return 0, err
    }
    return int32(binary.BigEndian.Uint32(b[:])), nil
}
```

## TLS

### Создание TLS соединения

```go
import (
    "crypto/tls"
    "net"
)

func createTLSConnection(addr string) (*tls.Conn, error) {
    config := &tls.Config{
        InsecureSkipVerify: false,
    }

    conn, err := tls.Dial("tcp", addr, config)
    if err != nil {
        return nil, err
    }

    return conn, nil
}
```

### TLS сервер

```go
import (
    "crypto/tls"
    "net/http"
)

func createTLSServer(certFile, keyFile string) (*http.Server, error) {
    cert, err := tls.LoadX509KeyPair(certFile, keyFile)
    if err != nil {
        return nil, err
    }

    config := &tls.Config{
        Certificates: []tls.Certificate{cert},
    }

    server := &http.Server{
        Addr:      ":443",
        TLSConfig: config,
    }

    return server, nil
}
```

### Практические примеры: Генерация ключей

```go
import (
    "crypto/rand"
    "crypto/rsa"
    "crypto/ecdsa"
    "crypto/elliptic"
)

func generateRSAKey(bits int) (*rsa.PrivateKey, error) {
    return rsa.GenerateKey(rand.Reader, bits)
}

func generateECDSAKey() (*ecdsa.PrivateKey, error) {
    return ecdsa.GenerateKey(elliptic.P256(), rand.Reader)
}

func generateAESKey(size int) ([]byte, error) {
    key := make([]byte, size)
    _, err := rand.Read(key)
    return key, err
}
```

### Практические примеры: Хеширование паролей

```go
import (
    "crypto/rand"
    "crypto/subtle"
    "encoding/base64"
    "golang.org/x/crypto/argon2"
)

type PasswordHash struct {
    Hash    []byte
    Salt    []byte
    Time    uint32
    Memory  uint32
    Threads uint8
    KeyLen  uint32
}

func hashPassword(password string) (*PasswordHash, error) {
    salt := make([]byte, 16)
    if _, err := rand.Read(salt); err != nil {
        return nil, err
    }

    hash := argon2.IDKey([]byte(password), salt, 1, 64*1024, 4, 32)

    return &PasswordHash{
        Hash:    hash,
        Salt:    salt,
        Time:    1,
        Memory:  64 * 1024,
        Threads: 4,
        KeyLen:  32,
    }, nil
}

func verifyPassword(password string, ph *PasswordHash) bool {
    hash := argon2.IDKey([]byte(password), ph.Salt, ph.Time, ph.Memory, ph.Threads, ph.KeyLen)
    return subtle.ConstantTimeCompare(hash, ph.Hash) == 1
}
```

### Практические примеры: Шифрование с ключом из пароля

```go
import (
    "crypto/aes"
    "crypto/cipher"
    "crypto/rand"
    "crypto/sha256"
    "io"
)

func deriveKey(password string, salt []byte) []byte {
    hash := sha256.Sum256(append([]byte(password), salt...))
    return hash[:]
}

func encryptWithPassword(password string, plaintext []byte) ([]byte, error) {
    salt := make([]byte, 16)
    if _, err := rand.Read(salt); err != nil {
        return nil, err
    }

    key := deriveKey(password, salt)

    block, err := aes.NewCipher(key)
    if err != nil {
        return nil, err
    }

    gcm, err := cipher.NewGCM(block)
    if err != nil {
        return nil, err
    }

    nonce := make([]byte, gcm.NonceSize())
    if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
        return nil, err
    }

    ciphertext := gcm.Seal(nonce, nonce, plaintext, nil)

    // Добавляем salt в начало
    result := append(salt, ciphertext...)
    return result, nil
}

func decryptWithPassword(password string, ciphertext []byte) ([]byte, error) {
    if len(ciphertext) < 16 {
        return nil, fmt.Errorf("ciphertext too short")
    }

    salt := ciphertext[:16]
    ciphertext = ciphertext[16:]

    key := deriveKey(password, salt)

    block, err := aes.NewCipher(key)
    if err != nil {
        return nil, err
    }

    gcm, err := cipher.NewGCM(block)
    if err != nil {
        return nil, err
    }

    nonceSize := gcm.NonceSize()
    if len(ciphertext) < nonceSize {
        return nil, fmt.Errorf("ciphertext too short")
    }

    nonce, ciphertext := ciphertext[:nonceSize], ciphertext[nonceSize:]
    return gcm.Open(nil, nonce, ciphertext, nil)
}
```

### Практические примеры: Генерация токенов

```go
import (
    "crypto/rand"
    "encoding/base64"
    "encoding/hex"
)

func generateRandomToken(length int) (string, error) {
    bytes := make([]byte, length)
    if _, err := rand.Read(bytes); err != nil {
        return "", err
    }
    return hex.EncodeToString(bytes), nil
}

func generateBase64Token(length int) (string, error) {
    bytes := make([]byte, length)
    if _, err := rand.Read(bytes); err != nil {
        return "", err
    }
    return base64.URLEncoding.EncodeToString(bytes), nil
}
```

### Практические примеры: Проверка целостности данных

```go
import (
    "crypto/hmac"
    "crypto/sha256"
    "encoding/hex"
)

func calculateHMAC(key, data []byte) string {
    mac := hmac.New(sha256.New, key)
    mac.Write(data)
    return hex.EncodeToString(mac.Sum(nil))
}

func verifyHMAC(key, data []byte, expectedMAC string) bool {
    calculatedMAC := calculateHMAC(key, data)
    return hmac.Equal([]byte(calculatedMAC), []byte(expectedMAC))
}

func signData(key, data []byte) ([]byte, error) {
    mac := hmac.New(sha256.New, key)
    mac.Write(data)
    signature := mac.Sum(nil)

    // Добавляем подпись к данным
    return append(signature, data...), nil
}

func verifySignedData(key, signedData []byte) ([]byte, bool) {
    if len(signedData) < 32 {
        return nil, false
    }

    signature := signedData[:32]
    data := signedData[32:]

    mac := hmac.New(sha256.New, key)
    mac.Write(data)
    expectedSignature := mac.Sum(nil)

    if !hmac.Equal(signature, expectedSignature) {
        return nil, false
    }

    return data, true
}
```

### Практические примеры: Шифрование файлов

```go
func encryptFile(key []byte, inputFile, outputFile string) error {
    plaintext, err := os.ReadFile(inputFile)
    if err != nil {
        return err
    }

    ciphertext, err := encryptGCM(key, plaintext)
    if err != nil {
        return err
    }

    return os.WriteFile(outputFile, ciphertext, 0644)
}

func decryptFile(key []byte, inputFile, outputFile string) error {
    ciphertext, err := os.ReadFile(inputFile)
    if err != nil {
        return err
    }

    plaintext, err := decryptGCM(key, ciphertext)
    if err != nil {
        return err
    }

    return os.WriteFile(outputFile, plaintext, 0644)
}
```

### Практические примеры: TLS с кастомными сертификатами

```go
import (
    "crypto/tls"
    "crypto/x509"
    "net/http"
)

func createTLSConfig(certFile, keyFile, caFile string) (*tls.Config, error) {
    cert, err := tls.LoadX509KeyPair(certFile, keyFile)
    if err != nil {
        return nil, err
    }

    caCert, err := os.ReadFile(caFile)
    if err != nil {
        return nil, err
    }

    caCertPool := x509.NewCertPool()
    caCertPool.AppendCertsFromPEM(caCert)

    config := &tls.Config{
        Certificates: []tls.Certificate{cert},
        RootCAs:      caCertPool,
        ClientCAs:    caCertPool,
        ClientAuth:   tls.RequireAndVerifyClientCert,
    }

    return config, nil
}
```

### Практические примеры: Хранение и хеширование паролей

```go
import "golang.org/x/crypto/bcrypt"

func HashPassword(password string) (string, error) {
    hash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
    if err != nil {
        return "", err
    }
    return string(hash), nil
}

func VerifyPassword(hashedPassword, password string) bool {
    err := bcrypt.CompareHashAndPassword([]byte(hashedPassword), []byte(password))
    return err == nil
}

// Использование Argon2 для более современного хеширования
import "golang.org/x/crypto/argon2"

func HashPasswordArgon2(password string, salt []byte) []byte {
    return argon2.IDKey([]byte(password), salt, 1, 64*1024, 4, 32)
}
```

### Практические примеры: Аутентификация токенов

```go
import "github.com/golang-jwt/jwt/v5"

func GenerateJWT(claims jwt.MapClaims, secret []byte) (string, error) {
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    return token.SignedString(secret)
}

func VerifyJWT(tokenString string, secret []byte) (jwt.MapClaims, error) {
    token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
        if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
            return nil, fmt.Errorf("unexpected signing method")
        }
        return secret, nil
    })

    if err != nil {
        return nil, err
    }

    if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
        return claims, nil
    }

    return nil, fmt.Errorf("invalid token")
}
```

### Практические примеры: Безопасное хранение секретов

```go
import "crypto/aes"

type SecretManager struct {
    key []byte
}

func NewSecretManager(key []byte) (*SecretManager, error) {
    if len(key) != 32 {
        return nil, fmt.Errorf("key must be 32 bytes for AES-256")
    }
    return &SecretManager{key: key}, nil
}

func (sm *SecretManager) EncryptSecret(secret []byte) ([]byte, error) {
    block, err := aes.NewCipher(sm.key)
    if err != nil {
        return nil, err
    }

    gcm, err := cipher.NewGCM(block)
    if err != nil {
        return nil, err
    }

    nonce := make([]byte, gcm.NonceSize())
    if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
        return nil, err
    }

    ciphertext := gcm.Seal(nonce, nonce, secret, nil)
    return ciphertext, nil
}

func (sm *SecretManager) DecryptSecret(ciphertext []byte) ([]byte, error) {
    block, err := aes.NewCipher(sm.key)
    if err != nil {
        return nil, err
    }

    gcm, err := cipher.NewGCM(block)
    if err != nil {
        return nil, err
    }

    nonceSize := gcm.NonceSize()
    if len(ciphertext) < nonceSize {
        return nil, fmt.Errorf("ciphertext too short")
    }

    nonce, ciphertext := ciphertext[:nonceSize], ciphertext[nonceSize:]
    return gcm.Open(nil, nonce, ciphertext, nil)
}
```

### Практические примеры: Постоянное время сравнения

```go
import "crypto/subtle"

func ConstantTimeCompare(a, b []byte) bool {
    return subtle.ConstantTimeCompare(a, b) == 1
}

func ConstantTimeEqual(a, b string) bool {
    if len(a) != len(b) {
        return false
    }
    return subtle.ConstantTimeCompare([]byte(a), []byte(b)) == 1
}

// Безопасное сравнение токенов
func ValidateToken(token, expected []byte) bool {
    return subtle.ConstantTimeCompare(token, expected) == 1
}
```

## Лучшие практики

1. **Используйте современные алгоритмы** — **SHA-256**, **AES-256**, **RSA-2048**+
2. **Избегайте устаревших алгоритмов** — **MD5**, **SHA-1**, **DES**
3. **Используйте криптографически стойкие случайные числа** — **crypto**/**rand**
4. **Храните ключи безопасно** — не храните ключи в коде
5. **Используйте правильные режимы шифрования** — **GCM** для аутентифицированного шифрования
6. **Валидируйте подписи** — всегда проверяйте цифровые подписи
7. **Используйте salt для паролей** — всегда используйте уникальный **salt**
8. **Используйте постоянное время сравнения** — для предотвращения **timing attacks**
9. **Проверяйте сертификаты** — не пропускайте проверку сертификатов в **production**
10. **Храните ключи отдельно** — используйте **key management systems**
11. **Используйте bcrypt или Argon2** — для хеширования паролей
12. **Используйте `JWT` правильно** — подписывайте и проверяйте токены
13. **Шифруйте секреты** — используйте шифрование для хранения секретов
14. **Используйте constant time сравнение** — для предотвращения **timing attacks**
15. **Ротация ключей** — регулярно меняйте криптографические ключи


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Криптография в Go предоставляет мощные инструменты для создания безопасных приложений. Понимание хеширования, шифрования, цифровых подписей, случайных чисел, **TLS**, аутентификации, безопасного хранения секретов и практических применений критично для эффективного использования криптографии в Go. Правильное использование криптографии позволяет создавать безопасные, защищенные и устойчивые к атакам приложения, которые обеспечивают конфиденциальность, целостность и аутентичность данных.

## Дополнительные ресурсы

- [Go crypto Package](https://pkg.go.dev/crypto)
- [Go crypto/hash](https://pkg.go.dev/crypto/hash)
- [Go crypto/cipher](https://pkg.go.dev/crypto/cipher)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
