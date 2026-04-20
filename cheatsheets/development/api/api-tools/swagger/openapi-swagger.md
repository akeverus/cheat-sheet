---
title: "OpenAPI/Swagger"
description: "OpenAPI Specification (OAS) - это стандарт для описания REST API. Ранее известная как Swagger, эта спецификация позволяет описывать API endpoints, параметры, запросы и ответы в машиночитаемом формате. Этот документ охватывает создание, валидацию и использование OpenAPI спецификац"
tags:
  - development
  - api
  - openapi-swagger
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# OpenAPI/Swagger

**OpenAPI Specification** (OAS) — это стандарт для описания **REST API**. Ранее известная как **Swagger**, эта спецификация позволяет описывать **API endpoints**, параметры, запросы и ответы в машиночитаемом формате. Этот документ охватывает создание, валидацию и использование **OpenAPI** спецификаций для документирования и тестирования **API**.

## Полезные ссылки
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html)
- [OpenAPI 3.0 Guide](https://swagger.io/docs/specification/about/)
- [Swagger Editor](https://editor.swagger.io/)
- [OpenAPI Generator](https://openapi-generator.tech/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [Redoc](https://redocly.com/docs/redoc/)
- [Spectral Linter](https://stoplight.io/open-source/spectral)


### См. также
- [[api-documentation-basics|API Documentation: Основы]]
## Содержание

- [Основы OpenAPI](#основы-openapi)
  - [OpenAPI Versions](#openapi-versions)
  - [Basic Structure](#basic-structure)
- [Структура OpenAPI документа](#структура-openapi-документа)
  - [Info Object](#info-object)
  - [Servers Object](#servers-object)
  - [External Documentation](#external-documentation)
  - [Extensions](#extensions)
- [Paths и Operations](#paths-и-operations)
  - [Path Templates](#path-templates)
  - [Operations](#operations)
  - [Tags и Grouping](#tags-и-grouping)
- [Parameters и Request Bodies](#parameters-и-request-bodies)
  - [Parameter Types](#parameter-types)
  - [Request Bodies](#request-bodies)
- [Responses и Schemas](#responses-и-schemas)
  - [Response Definitions](#response-definitions)
  - [Schema Definitions](#schema-definitions)
  - [Advanced Schemas](#advanced-schemas)
- [Security](#security)
  - [Security Schemes](#security-schemes)
  - [Security Requirements](#security-requirements)
- [Components и Reusability](#components-и-reusability)
  - [Reusable Components](#reusable-components)
  - [Examples](#examples)
- [Validation и Linting](#validation-и-linting)
  - [OpenAPI Validation](#openapi-validation)
  - [Common Validation Issues](#common-validation-issues)
  - [Custom Validation Rules](#custom-validation-rules)
- [Code Generation](#code-generation)
  - [OpenAPI Generator](#openapi-generator)
  - [Swagger Codegen (Legacy)](#swagger-codegen-legacy)
  - [Custom Code Generation](#custom-code-generation)
- [API Testing](#api-testing)
  - [Swagger UI для Manual Testing](#swagger-ui-для-manual-testing)
  - [Automated API Testing](#automated-api-testing)
  - [Contract Testing](#contract-testing)
- [Documentation](#documentation)
  - [Swagger UI Configuration](#swagger-ui-configuration)
  - [Redoc для Clean Documentation](#redoc-для-clean-documentation)
  - [API Documentation Best Practices](#api-documentation-best-practices)
- [Решение проблем](#решение-проблем)
  - [Генератор не создаёт клиент для нужного языка](#генератор-не-создаёт-клиент-для-нужного-языка)
  - [Спецификация не валидируется Spectral](#спецификация-не-валидируется-spectral)
  - [Сгенерированный код не совпадает с реализацией](#сгенерированный-код-не-совпадает-с-реализацией)
  - [Swagger UI не отображает схемы](#swagger-ui-не-отображает-схемы)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Основы OpenAPI

### OpenAPI Versions
Ниже — пример заголовка **OpenAPI** 3.0 (YAML).
```yaml
# OpenAPI 3.0.x (recommended)
openapi: 3.0.3
info:
  title: My API
  version: 1.0.0

# OpenAPI 3.1.x (latest)
openapi: 3.1.0
info:
  title: My API
  version: 1.0.0
jsonSchemaDialect: https://json-schema.org/draft/2020-12/schema
```

### Basic Structure
```yaml
openapi: 3.0.3
info:
  title: User Management API
  description: API for managing users and their data
  version: 2.1.0
  contact:
    name: API Support
    email: api@company.com
    url: https://company.com/support
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.company.com/v2
    description: Production server
  - url: https://staging-api.company.com/v2
    description: Staging server
  - url: http://localhost:3000
    description: Development server

paths:
  /users:
    get:
      summary: Get users
      operationId: getUsers
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UsersResponse'

components:
  schemas:
    UsersResponse:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/User'
        pagination:
          $ref: '#/components/schemas/Pagination'

    User:
      type: object
      required:
        - id
        - email
      properties:
        id:
          type: string
          format: uuid
        email:
          type: string
          format: email
        name:
          type: string
          minLength: 1
          maxLength: 100
        createdAt:
          type: string
          format: date-time
```

## Структура OpenAPI документа

### Info Object
```yaml
info:
  title: E-commerce API
  description: |
    Complete API for e-commerce platform including:
    - User management
    - Product catalog
    - Order processing
    - Payment integration
    - Analytics

    ## Authentication
    All endpoints require Bearer token authentication.

    ## Rate Limiting
    API is rate limited to 1000 requests per hour.
  version: 3.2.1
  termsOfService: https://company.com/terms
  contact:
    name: API Team
    email: api@company.com
    url: https://company.com/api-support
  license:
    name: Apache 2.0
    url: https://www.apache.org/licenses/LICENSE-2.0.html
  x-logo:
    url: https://company.com/logo.png
    altText: Company Logo
  x-api-clients:
    - JavaScript
    - Python
    - Java
    - Go
```

### Servers Object
```yaml
servers:
  - url: https://api.company.com/v3
    description: Production server
    variables:
      version:
        default: v3
        enum: [v1, v2, v3]
        description: API version

  - url: https://{environment}.api.company.com/{version}
    description: Environment-specific server
    variables:
      environment:
        default: staging
        enum: [dev, staging, prod]
        description: Environment name
      version:
        default: v3
        description: API version

  - url: http://localhost:{port}/{basePath}
    description: Local development server
    variables:
      port:
        default: "3000"
        description: Port number
      basePath:
        default: api
        description: Base path
```

### External Documentation
```yaml
externalDocs:
  description: Find more info here
  url: https://docs.company.com/api

# Multiple external docs
x-external-docs:
  - description: API Guide
    url: https://docs.company.com/api/guide
  - description: SDK Documentation
    url: https://docs.company.com/api/sdk
  - description: Changelog
    url: https://docs.company.com/api/changelog
```

### Extensions
```yaml
# OpenAPI extensions (x-*)
openapi: 3.0.3
info:
  title: Extended API
  x-api-id: api-12345
  x-visibility: public
  x-maturity: stable
  x-service-name: user-service

paths:
  /users:
    get:
      summary: Get users
      x-code-samples:
        - lang: curl
          source: |
            curl -X GET "https://api.company.com/users" \
              -H "Authorization: Bearer {token}"
        - lang: python
          source: |
            import requests
            response = requests.get('https://api.company.com/users',
                                  headers={'Authorization': 'Bearer {token}'})
      x-internal: false
      x-rate-limit:
        limit: 1000
        window: 1h
        burst: 100
```

## Paths и Operations

### Path Templates
```yaml
paths:
  # Static paths
  /users:
    # Operations here
  /products:
    # Operations here

  # Path parameters
  /users/{userId}:
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
          format: uuid
    get:
      # Get user by ID
    put:
      # Update user
    delete:
      # Delete user

  # Nested resources
  /users/{userId}/posts:
    parameters:
      - $ref: '#/components/parameters/UserId'
    get:
      summary: Get user's posts
    post:
      summary: Create new post for user

  /users/{userId}/posts/{postId}:
    parameters:
      - $ref: '#/components/parameters/UserId'
      - name: postId
        in: path
        required: true
        schema:
          type: string
          format: uuid
    get:
      summary: Get specific post
    patch:
      summary: Update post
    delete:
      summary: Delete post

  # Query parameters and matrix parameters
  /search:
    get:
      parameters:
        - name: q
          in: query
          schema:
            type: string
          description: Search query
        - name: limit
          in: query
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
        - name: sort
          in: query
          schema:
            type: string
            enum: [name, created, updated]
            default: created

  # Matrix parameters (RFC 3986)
  /products{color,size}:
    get:
      parameters:
        - name: color
          in: path
          style: matrix
          schema:
            type: string
            enum: [red, green, blue]
        - name: size
          in: path
          style: matrix
          schema:
            type: string
            enum: [S, M, L, XL]
```

### Operations
```yaml
/users:
  get:
    operationId: listUsers
    summary: List users
    description: Retrieve a paginated list of users
    tags:
      - Users
    security:
      - bearerAuth: []
    parameters:
      - $ref: '#/components/parameters/Page'
      - $ref: '#/components/parameters/Limit'
      - name: role
        in: query
        schema:
          type: string
          enum: [admin, user, guest]
      - name: createdAfter
        in: query
        schema:
          type: string
          format: date-time
    responses:
      '200':
        $ref: '#/components/responses/UsersList'
      '400':
        $ref: '#/components/responses/BadRequest'
      '401':
        $ref: '#/components/responses/Unauthorized'
      default:
        $ref: '#/components/responses/InternalError'

  post:
    operationId: createUser
    summary: Create user
    description: Create a new user account
    tags:
      - Users
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/UserInput'
          examples:
            user:
              $ref: '#/components/examples/UserExample'
    responses:
      '201':
        $ref: '#/components/responses/UserCreated'
      '400':
        $ref: '#/components/responses/ValidationError'
      '409':
        $ref: '#/components/responses/Conflict'
```

### Tags и Grouping
```yaml
# Tags for logical grouping
tags:
  - name: Users
    description: User management operations
    externalDocs:
      description: User Guide
      url: https://docs.company.com/users
  - name: Products
    description: Product catalog operations
  - name: Orders
    description: Order processing operations
  - name: Admin
    description: Administrative operations
    x-internal: true

# Operations with tags
paths:
  /users:
    get:
      tags: [Users]
    post:
      tags: [Users]
  /products:
    get:
      tags: [Products]
    post:
      tags: [Products, Admin]
  /orders:
    post:
      tags: [Orders]
    get:
      tags: [Orders, Admin]
```

## Parameters и Request Bodies

### Parameter Types
```yaml
# Path parameters
parameters:
  UserId:
    name: userId
    in: path
    required: true
    schema:
      type: string
      format: uuid
    description: Unique identifier of the user
    example: 123e4567-e89b-12d3-a456-426614174000

  Page:
    name: page
    in: query
    schema:
      type: integer
      minimum: 1
      default: 1
    description: Page number for pagination

  Limit:
    name: limit
    in: query
    schema:
      type: integer
      minimum: 1
      maximum: 100
      default: 20
    description: Number of items per page

# Query parameters with arrays
paths:
  /users:
    get:
      parameters:
        - name: roles
          in: query
          schema:
            type: array
            items:
              type: string
              enum: [admin, user, guest]
          style: form
          explode: false
          description: Filter by user roles
          example: admin,user

        - name: tags
          in: query
          schema:
            type: array
            items:
              type: string
          style: form
          explode: true
          description: Filter by tags
          example: ["premium", "active"]

# Header parameters
paths:
  /api/data:
    get:
      parameters:
        - name: X-API-Version
          in: header
          schema:
            type: string
            enum: [v1, v2, v3]
            default: v3
          required: true
          description: API version to use

        - name: Accept-Language
          in: header
          schema:
            type: string
            default: en-US
          description: Preferred language for responses
```

### Request Bodies
```yaml
# Simple request body
paths:
  /users:
    post:
      requestBody:
        description: User data to create
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserInput'
            examples:
              user:
                $ref: '#/components/examples/UserInputExample'

# Multiple content types
paths:
  /uploads:
    post:
      requestBody:
        description: File upload
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  description: File to upload
                metadata:
                  type: string
                  description: JSON metadata for the file
            encoding:
              metadata:
                contentType: application/json

          application/octet-stream:
            schema:
              type: string
              format: binary
            x-examples:
              binaryFile:
                summary: Binary file upload
                value: "binary data here"

# Form data
paths:
  /feedback:
    post:
      requestBody:
        content:
          application/x-www-form-urlencoded:
            schema:
              type: object
              properties:
                name:
                  type: string
                email:
                  type: string
                  format: email
                message:
                  type: string
              required: [name, email, message]
            encoding:
              message:
                contentType: text/plain
                style: form
```

## Responses и Schemas

### Response Definitions
```yaml
# Response components
components:
  responses:
    UsersList:
      description: Successful response with users list
      headers:
        X-Total-Count:
          schema:
            type: integer
          description: Total number of users
        X-Page-Count:
          schema:
            type: integer
          description: Total number of pages
      content:
        application/json:
          schema:
            type: object
            properties:
              data:
                type: array
                items:
                  $ref: '#/components/schemas/User'
              pagination:
                $ref: '#/components/schemas/Pagination'
          examples:
            success:
              $ref: '#/components/examples/UsersListExample'

    UserCreated:
      description: User successfully created
      headers:
        Location:
          schema:
            type: string
            format: uri
          description: URL of the created user
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/User'
          examples:
            created:
              $ref: '#/components/examples/UserCreatedExample'

    BadRequest:
      description: Bad request
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
          examples:
            validationError:
              $ref: '#/components/examples/ValidationErrorExample'

    Unauthorized:
      description: Authentication required
      headers:
        WWW-Authenticate:
          schema:
            type: string
          description: Authentication scheme
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'

    NotFound:
      description: Resource not found
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
```

### Schema Definitions
```yaml
# Schema components
components:
  schemas:
    User:
      type: object
      required:
        - id
        - email
        - createdAt
      properties:
        id:
          type: string
          format: uuid
          description: Unique user identifier
          example: 123e4567-e89b-12d3-a456-426614174000
        email:
          type: string
          format: email
          description: User's email address
          example: user@example.com
        name:
          type: string
          minLength: 1
          maxLength: 100
          description: User's full name
          example: John Doe
        role:
          $ref: '#/components/schemas/UserRole'
        createdAt:
          type: string
          format: date-time
          description: Account creation timestamp
          example: 2023-01-01T00:00:00Z
        updatedAt:
          type: string
          format: date-time
          description: Last update timestamp
          example: 2023-01-15T10:30:00Z
        profile:
          $ref: '#/components/schemas/UserProfile'

    UserInput:
      type: object
      required:
        - email
        - password
      properties:
        email:
          type: string
          format: email
        password:
          type: string
          minLength: 8
          format: password
        name:
          type: string
          minLength: 1
          maxLength: 100
        role:
          $ref: '#/components/schemas/UserRole'
          default: user

    UserRole:
      type: string
      enum: [admin, user, guest]
      default: user
      description: User role in the system

    UserProfile:
      type: object
      properties:
        avatar:
          type: string
          format: uri
          description: URL to user's avatar
        bio:
          type: string
          maxLength: 500
          description: User's biography
        preferences:
          type: object
          properties:
            theme:
              type: string
              enum: [light, dark]
              default: light
            notifications:
              type: boolean
              default: true

    Pagination:
      type: object
      properties:
        currentPage:
          type: integer
          minimum: 1
        totalPages:
          type: integer
          minimum: 0
        totalItems:
          type: integer
          minimum: 0
        itemsPerPage:
          type: integer
          minimum: 1
          maximum: 100

    Error:
      type: object
      required:
        - code
        - message
      properties:
        code:
          type: string
          enum: [VALIDATION_ERROR, UNAUTHORIZED, NOT_FOUND, INTERNAL_ERROR]
        message:
          type: string
        details:
          type: object
          description: Additional error details
```

### Advanced Schemas
```yaml
# Complex schemas with polymorphism
components:
  schemas:
    Animal:
      type: object
      required:
        - type
        - name
      properties:
        type:
          type: string
          enum: [dog, cat, bird]
        name:
          type: string
      discriminator:
        propertyName: type

    Dog:
      allOf:
        - $ref: '#/components/schemas/Animal'
        - type: object
          properties:
            breed:
              type: string
              enum: [golden_retriever, bulldog, poodle]
            goodBoy:
              type: boolean
              default: true

    Cat:
      allOf:
        - $ref: '#/components/schemas/Animal'
        - type: object
          properties:
            livesRemaining:
              type: integer
              minimum: 0
              maximum: 9
              default: 9
            temperament:
              type: string
              enum: [friendly, aloof, aggressive]

    # Recursive schemas
    Category:
      type: object
      properties:
        id:
          type: string
        name:
          type: string
        parent:
          $ref: '#/components/schemas/Category'
        children:
          type: array
          items:
            $ref: '#/components/schemas/Category'

    # OneOf, AnyOf, AllOf
    PaymentMethod:
      oneOf:
        - $ref: '#/components/schemas/CreditCard'
        - $ref: '#/components/schemas/PayPal'
        - $ref: '#/components/schemas/BankTransfer'

    CreditCard:
      type: object
      required: [number, expiryMonth, expiryYear, cvc]
      properties:
        type:
          type: string
          enum: [visa, mastercard, amex]
        number:
          type: string
          pattern: '^\d{13,19}$'
        expiryMonth:
          type: integer
          minimum: 1
          maximum: 12
        expiryYear:
          type: integer
          minimum: 2023
        cvc:
          type: string
          minLength: 3
          maxLength: 4

    # Nullable fields
    Order:
      type: object
      properties:
        id:
          type: string
        customerId:
          type: string
        items:
          type: array
          items:
            $ref: '#/components/schemas/OrderItem'
        shippingAddress:
          nullable: true
          allOf:
            - $ref: '#/components/schemas/Address'
        billingAddress:
          nullable: true
          allOf:
            - $ref: '#/components/schemas/Address'
```

## Security

### Security Schemes
```yaml
# Security schemes
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: JWT Authorization header using the Bearer scheme

    basicAuth:
      type: http
      scheme: basic
      description: Basic HTTP authentication

    apiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
      description: API key authentication

    oauth2Auth:
      type: oauth2
      flows:
        authorizationCode:
          authorizationUrl: https://auth.company.com/oauth/authorize
          tokenUrl: https://auth.company.com/oauth/token
          scopes:
            read:users: Read user information
            write:users: Modify user information
            admin: Grant administrative access

    openIdConnect:
      type: openIdConnect
      openIdConnectUrl: https://auth.company.com/.well-known/openid_configuration
      description: OpenID Connect authentication

    mutualTLS:
      type: mutualTLS
      description: Mutual TLS authentication

# Global security
security:
  - bearerAuth: []
  - apiKeyAuth: []

# Operation-level security
paths:
  /users:
    get:
      security:
        - bearerAuth: [read:users]
        - apiKeyAuth: []
    post:
      security:
        - bearerAuth: [write:users]
```

### Security Requirements
```yaml
# Multiple security requirements (OR logic)
security:
  - bearerAuth: []
    apiKeyAuth: []
  - oauth2Auth: [read:users, write:users]

# Specific scopes
paths:
  /admin/users:
    get:
      security:
        - bearerAuth: []
          oauth2Auth: [admin]
  /users/profile:
    get:
      security:
        - bearerAuth: []
          oauth2Auth: [read:users]
    put:
      security:
        - bearerAuth: []
          oauth2Auth: [write:users]

# No authentication required
paths:
  /health:
    get:
      security: []  # Empty array means no security
```

## Components и Reusability

### Reusable Components
```yaml
# Parameters
components:
  parameters:
    UserId:
      name: userId
      in: path
      required: true
      schema:
        type: string
        format: uuid
      description: Unique identifier of the user
      example: 123e4567-e89b-12d3-a456-426614174000

    Offset:
      name: offset
      in: query
      schema:
        type: integer
        minimum: 0
        default: 0
      description: Number of items to skip

    Include:
      name: include
      in: query
      schema:
        type: array
        items:
          type: string
          enum: [profile, preferences, stats]
      style: form
      explode: false
      description: Related resources to include

# Request bodies
components:
  requestBodies:
    UserUpdate:
      description: User update data
      required: true
      content:
        application/json:
          schema:
            type: object
            properties:
              name:
                type: string
                minLength: 1
                maxLength: 100
              email:
                type: string
                format: email
              preferences:
                $ref: '#/components/schemas/UserPreferences'

# Headers
components:
  headers:
    RateLimitRemaining:
      description: Number of requests remaining in current window
      schema:
        type: integer
      example: 42

    RateLimitReset:
      description: Time when rate limit resets (Unix timestamp)
      schema:
        type: integer
      example: 1640995200

# Callbacks
components:
  callbacks:
    PaymentWebhook:
      '{$request.body#/callbackUrl}':
        post:
          requestBody:
            required: true
            content:
              application/json:
                schema:
                  $ref: '#/components/schemas/PaymentEvent'
          responses:
            '200':
              description: Webhook received successfully

# Links
components:
  links:
    GetUserById:
      operationId: getUser
      parameters:
        userId: '$response.body#/id'

    GetOrderItems:
      operationRef: '#/paths/~1orders~1{orderId}~1items/get'
      parameters:
        orderId: '$response.body#/orderId'
```

### Examples
```yaml
# Examples
components:
  examples:
    UserExample:
      summary: Example user
      value:
        id: 123e4567-e89b-12d3-a456-426614174000
        email: john.doe@example.com
        name: John Doe
        role: user
        createdAt: 2023-01-01T00:00:00Z

    UsersListExample:
      summary: List of users
      value:
        data:
          - $ref: '#/components/examples/UserExample'
          - id: 456e7890-e89b-12d3-a456-426614174001
            email: jane.smith@example.com
            name: Jane Smith
            role: admin
            createdAt: 2023-01-02T00:00:00Z
        pagination:
          currentPage: 1
          totalPages: 5
          totalItems: 100
          itemsPerPage: 20

    ValidationErrorExample:
      summary: Validation error response
      value:
        code: VALIDATION_ERROR
        message: Validation failed
        details:
          - field: email
            message: Must be a valid email address
          - field: password
            message: Must be at least 8 characters long

    UserInputExample:
      summary: User creation input
      value:
        email: newuser@example.com
        password: securePassword123!
        name: New User
        role: user
```

## Validation и Linting

### OpenAPI Validation
```bash
# Validate OpenAPI spec with swagger-cli
npm install -g swagger-cli
swagger-cli validate api-spec.yaml

# Validate with openapi-cli
npm install -g @apidevtools/swagger-cli
swagger-cli validate api-spec.yaml

# Validate specific version
swagger-cli validate --no-schema api-spec.yaml

# Use Spectral for advanced linting
npm install -g @stoplight/spectral
spectral lint api-spec.yaml

# Custom Spectral rules
.spectral.yaml
rules:
  operation-description: error
  operation-tags: warn
  operation-operationId: error
  operation-summary: error
  paths-kebab-case: error
  no-$ref-siblings: error

# Validate against OpenAPI 3.0 schema
npx @apidevtools/swagger-cli validate --schema https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/schemas/v3.0/schema.yaml api-spec.yaml
```

### Common Validation Issues
```yaml
# Fix common issues

# 1. Missing operationId
paths:
  /users:
    get:
      operationId: getUsers  # Add this
      summary: Get users

# 2. Invalid parameter reference
paths:
  /users/{userId}:
    parameters:
      - $ref: '#/components/parameters/UserId'  # Use correct reference
    get:
      # ...

# 3. Missing required properties
components:
  schemas:
    User:
      type: object
      required: [id, email]  # Add required fields
      properties:
        id:
          type: string
        email:
          type: string

# 4. Invalid schema references
responses:
  '200':
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/User'  # Correct reference

# 5. Missing content type
responses:
  '200':
    content:
      application/json:  # Add content type
        schema:
          type: string
```

### Custom Validation Rules
```javascript
// Custom validation with Speccy
const speccy = require('speccy');

const rules = {
  'operation-has-summary': {
    message: 'Operations must have a summary',
    given: '$.paths[*][get,post,put,patch,delete]',
    then: {
      field: 'summary',
      function: 'truthy'
    }
  },

  'parameter-description-required': {
    message: 'Parameters must have descriptions',
    given: '$.paths[*]..parameters[*]',
    then: {
      field: 'description',
      function: 'truthy'
    }
  },

  'schema-examples': {
    message: 'Schemas should have examples',
    given: '$.components.schemas[*]',
    then: {
      field: 'example',
      function: 'truthy'
    }
  }
};

// Validate with custom rules
speccy.lint('api-spec.yaml', { rules })
  .then(results => {
    console.log('Validation results:', results);
  });
```

## Code Generation

### OpenAPI Generator
```bash
# Install OpenAPI Generator
npm install -g @openapitools/openapi-generator-cli

# Generate TypeScript client
openapi-generator generate \
  -i api-spec.yaml \
  -g typescript-axios \
  -o ./generated-client \
  --additional-properties=npmName=@company/api-client,supportsES6=true

# Generate Python client
openapi-generator generate \
  -i api-spec.yaml \
  -g python \
  -o ./generated-client \
  --additional-properties=packageName=company_api_client,projectName=company-api-client

# Generate Java client
openapi-generator generate \
  -i api-spec.yaml \
  -g java \
  -o ./generated-client \
  --additional-properties=groupId=com.company,artifactId=api-client,artifactVersion=1.0.0

# Generate Go client
openapi-generator generate \
  -i api-spec.yaml \
  -g go \
  -o ./generated-client \
  --additional-properties=packageName=companyapiclient

# List available generators
openapi-generator list
```

### Swagger Codegen (Legacy)
```bash
# Install Swagger Codegen
npm install -g swagger-codegen-cli

# Generate client
swagger-codegen generate \
  -i api-spec.yaml \
  -l typescript-angular \
  -o ./client \
  --additional-properties=npmName=@company/api-client

# Generate server
swagger-codegen generate \
  -i api-spec.yaml \
  -l nodejs-server \
  -o ./server
```

### Custom Code Generation
```javascript
// Custom code generation with OpenAPI Parser
const OpenAPI = require('openapi-typescript-codegen');

async function generateCustomClient() {
  const spec = await OpenAPI.parse('api-spec.yaml');

  // Generate custom types
  const types = generateTypes(spec);
  writeFile('types.ts', types);

  // Generate API client
  const client = generateClient(spec);
  writeFile('client.ts', client);

  // Generate tests
  const tests = generateTests(spec);
  writeFile('client.test.ts', tests);
}

function generateTypes(spec) {
  let types = '';

  // Generate types from schemas
  for (const [name, schema] of Object.entries(spec.components.schemas)) {
    types += generateType(name, schema) + '\n';
  }

  return types;
}

function generateClient(spec) {
  let client = 'export class APIClient {\n';
  client += '  constructor(private baseURL: string, private apiKey?: string) {}\n\n';

  // Generate methods from paths
  for (const [path, methods] of Object.entries(spec.paths)) {
    for (const [method, operation] of Object.entries(methods)) {
      if (operation.operationId) {
        client += `  async ${operation.operationId}(params?: any): Promise<any> {\n`;
        client += `    return this.request('${method.toUpperCase()}', '${path}', params);\n`;
        client += '  }\n\n';
      }
    }
  }

  client += '}\n';
  return client;
}
```

## API Testing

### Swagger `UI` для Manual Testing
```html
<!-- Swagger UI HTML -->
<!DOCTYPE html>
<html>
<head>
  <title>API Documentation</title>
  <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@3/swagger-ui.css" />
</head>
<body>
  <div id="swagger-ui"></div>
  <script src="https://unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js"></script>
  <script>
    SwaggerUIBundle({
      url: '/api-spec.yaml',
      dom_id: '#swagger-ui',
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIBundle.presets.standalone
      ],
      plugins: [
        SwaggerUIBundle.plugins.DownloadUrl
      ],
      layout: "BaseLayout",
      validatorUrl: null,
      tryItOutEnabled: true,
      requestInterceptor: function(req) {
        // Add auth headers
        req.headers['Authorization'] = 'Bearer ' + getToken();
        return req;
      },
      responseInterceptor: function(res) {
        // Log responses
        console.log('Response:', res);
        return res;
      }
    });
  </script>
</body>
</html>
```

### Automated API Testing
```javascript
// API testing with Newman (Postman)
const newman = require('newman');

newman.run({
  collection: 'api-tests.postman_collection.json',
  environment: 'staging.postman_environment.json',
  reporters: ['cli', 'html', 'json'],
  reporter: {
    html: {
      export: './test-results.html'
    },
    json: {
      export: './test-results.json'
    }
  }
}, function (err, summary) {
  if (err) {
    console.error('Newman run failed:', err);
    process.exit(1);
  }

  console.log('Newman run completed');
  console.log('Total requests:', summary.run.stats.requests.total);
  console.log('Failed requests:', summary.run.stats.requests.failed);
});

// API testing with Dredd
const dredd = require('dredd');

const dreddConfig = {
  endpoint: 'https://api.company.com',
  path: ['./api-spec.yaml'],
  'hookfiles': ['./hooks.js'],
  'reporter': ['html', 'apiary'],
  'output': ['./dredd-results.html'],
  'header': ['Authorization: Bearer <token>'],
  'level': 'info'
};

dredd.run(dreddConfig, function (err, stats) {
  if (err) {
    console.error('Dredd run failed:', err);
    process.exit(1);
  }

  console.log('Dredd run completed');
  console.log('Stats:', stats);
});
```

### Contract Testing
```javascript
// Pact for consumer-driven contract testing
const { Pact } = require('@pact-foundation/pact');
const { like, term } = require('@pact-foundation/pact').Matchers;

describe('API Consumer', () => {
  const provider = new Pact({
    consumer: 'UserService',
    provider: 'UserAPI',
    port: 1234,
    log: path.resolve(process.cwd(), 'logs', 'pact.log'),
    dir: path.resolve(process.cwd(), 'pacts'),
    logLevel: 'INFO'
  });

  beforeAll(() => provider.setup());
  afterAll(() => provider.finalize());

  describe('when a user is requested', () => {
    beforeAll(() => {
      const interaction = {
        state: 'user exists',
        uponReceiving: 'a request for a user',
        withRequest: {
          method: 'GET',
          path: '/api/users/123',
          headers: {
            'Authorization': 'Bearer token123'
          }
        },
        willRespondWith: {
          status: 200,
          headers: {
            'Content-Type': 'application/json'
          },
          body: {
            id: '123',
            name: like('John Doe'),
            email: term({
              generate: 'user@example.com',
              matcher: '^[^@]+@[^@]+$'
            })
          }
        }
      };
      return provider.addInteraction(interaction);
    });

    it('returns the user', async () => {
      const response = await axios.get(`${provider.mockService.baseUrl}/api/users/123`, {
        headers: { 'Authorization': 'Bearer token123' }
      });

      expect(response.status).toBe(200);
      expect(response.data.id).toBe('123');
      expect(response.data.name).toBe('John Doe');
    });

    afterEach(() => provider.verify());
  });
});
```

## Documentation

### Swagger `UI` Configuration
```javascript
// Express.js with Swagger UI
const swaggerUi = require('swagger-ui-express');
const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'User Management API',
      version: '2.1.0',
      description: 'API for managing users and their data',
    },
    servers: [
      {
        url: 'https://api.company.com/v2',
        description: 'Production server',
      },
      {
        url: 'https://staging-api.company.com/v2',
        description: 'Staging server',
      },
    ],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
        },
      },
    },
    security: [
      {
        bearerAuth: [],
      },
    ],
  },
  apis: ['./routes/*.js'], // Paths to files with OpenAPI definitions
};

const specs = swaggerJsdoc(options);

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(specs, {
  explorer: true,
  swaggerOptions: {
    docExpansion: 'none',
    filter: true,
    showRequestDuration: true,
    syntaxHighlight: {
      activate: true,
      theme: 'arta'
    },
    tryItOutEnabled: true,
    requestInterceptor: (req) => {
      // Add auth token
      req.headers.Authorization = `Bearer ${getAuthToken()}`;
      return req;
    },
    responseInterceptor: (res) => {
      // Log responses for debugging
      console.log('API Response:', res);
      return res;
    },
  },
  customCss: `
    .swagger-ui .topbar { display: none }
    .swagger-ui .info .title { color: #3b4151 }
  `,
  customSiteTitle: 'Company API Documentation',
  customfavIcon: '/favicon.ico'
}));
```

### Redoc для Clean Documentation
```javascript
// Redoc configuration
const redoc = require('redoc-express');

app.use('/docs', redoc({
  title: 'Company API Documentation',
  specUrl: '/api-spec.yaml',
  redocOptions: {
    theme: {
      colors: {
        primary: {
          main: '#3b4151'
        }
      },
      typography: {
        fontFamily: '"Inter", sans-serif',
        headings: {
          fontFamily: '"Inter", sans-serif',
        },
      },
      menu: {
        backgroundColor: '#ffffff',
      },
    },
    hideDownloadButton: false,
    hideHostname: false,
    noAutoAuth: false,
    pathInMiddlePanel: true,
    untrustedSpec: false,
    showExtensions: true,
    sortPropsAlphabetically: true,
    sortTagsAlphabetically: true,
    nativeScrollbars: true,
    hideLoading: false,
    disableSearch: false,
    onlyRequiredInSamples: false,
  },
}));
```

### API Documentation Best Practices
```yaml
# Documentation metadata
info:
  title: "Company API"
  description: |
    ## Overview
    This API provides comprehensive user and data management capabilities.

    ## Authentication
    All requests require Bearer token authentication via the `Authorization` header.

    ## Rate Limiting
    - 1000 requests per hour for read operations
    - 100 requests per hour for write operations
    - 10 requests per minute for administrative operations

    ## Versioning
    API uses URL path versioning (e.g., `/v2/users`).

    ## Support
    - Email: api-support@company.com
    - Slack: #api-support
    - Documentation: https://docs.company.com/api
  version: "2.1.0"
  contact:
    name: "API Support Team"
    email: "api-support@company.com"
    url: "https://company.com/support"
  termsOfService: "https://company.com/terms"
  license:
    name: "MIT"
    url: "https://opensource.org/licenses/MIT"

# Path descriptions
paths:
  /users:
    get:
      summary: "Retrieve users"
      description: |
        Get a paginated list of users with optional filtering.

        Example: GET /users?page=1&limit=20&role=user

        Response example:
        {
          "data": [...],
          "pagination": {
            "currentPage": 1,
            "totalPages": 5,
            "totalItems": 100
          }
        }
      operationId: "getUsers"
      tags: ["Users"]

# Schema descriptions
components:
  schemas:
    User:
      description: |
        User object representing a system user.

        Fields:
        - `id`: Unique identifier (UUID format)
        - `email`: User's email address
        - `name`: Full display name
        - `role`: User role (admin/user/guest)
        - `createdAt`: Account creation timestamp
        - `updatedAt`: Last modification timestamp
      type: object
      required: ["id", "email", "createdAt"]
      properties:
        id:
          type: string
          format: uuid
          description: "Unique user identifier"
          example: "123e4567-e89b-12d3-a456-426614174000"
        email:
          type: string
          format: email
          description: "User's email address"
          example: "user@example.com"
        name:
          type: string
          minLength: 1
          maxLength: 100
          description: "User's full name"
          example: "John Doe"
        role:
          $ref: "#/components/schemas/UserRole"
        createdAt:
          type: string
          format: date-time
          description: "Account creation timestamp"
          example: "2023-01-01T00:00:00Z"
        updatedAt:
          type: string
          format: date-time
          description: "Last update timestamp"
          example: "2023-01-15T10:30:00Z"
```

## Решение проблем

### Генератор не создаёт клиент для нужного языка

**Проблема:** `openapi-generator generate` не находит generator или выдаёт ошибку `Generator 'xxx' not found`.

**Причины:** Устаревшая версия **OpenAPI Generator**; generator переименован (например, `java` `java-client`); не установлен отдельный генератор для конкретного стека.

**Решение:** Обновить до актуальной версии (`openapi-generator-cli` или Maven plugin); проверить список: `openapi-generator list`; использовать корректный идентификатор из [списка генераторов](https://openapi-generator.tech/docs/generators/); для **Java** с **Retrofit** — `java-retrofit2`, для **Spring** — `spring`.

### Спецификация не валидируется Spectral

**Проблема:** Линтер выдаёт десятки предупреждений; неясно, какие правки критичны.

**Причины:** Отсутствуют `operationId`; используются устаревшие ключи; ссылки `$ref` на несуществующие пути; не указан `content-type` в `requestBody`.

**Решение:** Добавить уникальный `operationId` для каждой операции; проверить `$ref` на корректность путей; для `requestBody` указать `content: application/json: schema: ...`; настроить `spectral.config` с отключением некритичных правил; использовать `--fail-severity error` для CI.

### Сгенерированный код не совпадает с реализацией

**Проблема:** Клиент ожидает поля, которых нет в реальных ответах; типы не совпадают с фактическими **JSON**.

**Причины:** Спецификация устарела; сервер возвращает дополнительные поля; используется `oneOf`/`anyOf` без явного `discriminator`; разные схемы для одного endpoint в разных версиях.

**Решение:** Синхронизировать **OpenAPI** с кодом через аннотации (Spring `@Operation`, FastAPI и т.д.) или генерировать спецификацию из реализации; добавить тесты контракта (Pact, Schemathesis); документировать `additionalProperties` если сервер отдаёт лишние поля.

### Swagger UI не отображает схемы

**Проблема:** В UI пустые модели или ошибка `Failed to load schema`.

**Причины:** Неверный `servers` URL; CORS блокирует запросы к спецификации; `$ref` на внешние файлы без корректного `spec-url`; циклические ссылки в схемах.

**Решение:** Убедиться, что `url` в **Swagger UI** указывает на доступный **JSON**/**YAML**; настроить CORS для статического хоста UI; при использовании `$ref` на файлы — собирать один bundle (`swagger-cli bundle`); избегать циклов в `$ref` через промежуточные типы.

## Лучшие практики

- Ведите спецификацию в репозитории рядом с кодом; используйте линтеры (Spectral) для проверки.
- Переиспользуйте схемы через **$ref**; версионируйте **OpenAPI**-документ.
- Описывайте все ответы (в т.ч. ошибки); указывайте примеры запросов и ответов.
- Документируйте безопасность (securitySchemes); генерируйте клиенты и серверы из спецификации.
- Регулярно валидируйте спецификацию и синхронизируйте с реализацией **API**.
## См. также
- [[rest-api-design|rest-api-design.md]] — основы **REST API**
- [[rest-api-best-practices|rest-api-best-practices.md]] — лучшие практики **REST**
- [[graphql|GraphQL]] — альтернативный подход к **API**
- [[grpc|gRPC]] — высокопроизводительные **API**
- [Security](../../../../security/) — безопасность **API**
