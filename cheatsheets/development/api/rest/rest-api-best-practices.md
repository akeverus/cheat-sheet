---
title: "REST API Best Practices"
description: "Этот документ содержит лучшие практики проектирования, реализации и поддержки REST API. Он охватывает HTTP методы, статус коды, версионирование, безопасность, документацию, тестирование и мониторинг. Документ дополняет [[rest-api-design|REST API Design]] практическими рекоменда"
tags:
  - development
  - api
  - rest-api-best-practices
difficulty: "intermediate"
prerequisites:
  - insomnia-basics

next: []

updated: "2026-04-20"
---
# REST API Best Practices

Этот документ содержит лучшие практики проектирования, реализации и поддержки **REST API**. Он охватывает **HTTP** методы, статус коды, версионирование, безопасность, документацию, тестирование и мониторинг. Документ дополняет [[rest-api-design|REST API Design]] практическими рекомендациями.

## Полезные ссылки
- [REST API Design Guidelines](https://www.baeldung.com/rest-api-design-maturity-model)
- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [API Security Best Practices](https://owasp.org/www-project-api-security/)
- [JSON:API Specification](https://jsonapi.org/)
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html)

## Содержание

- [HTTP Methods и Status Codes](#http-methods-и-status-codes)
  - [HTTP Methods Usage](#http-methods-usage)
  - [Status Codes Best Practices](#status-codes-best-practices)
- [API Design Patterns](#api-design-patterns)
  - [Content Negotiation](#content-negotiation)
  - [Pagination](#pagination)
  - [Filtering и Searching](#filtering-и-searching)
  - [Rate Limiting](#rate-limiting)
- [Versioning Strategies](#versioning-strategies)
  - [URL Path Versioning](#url-path-versioning)
  - [Header Versioning](#header-versioning)
  - [Media Type Versioning](#media-type-versioning)
  - [Semantic Versioning for APIs](#semantic-versioning-for-apis)
- [Error Handling](#error-handling)
  - [Consistent Error Responses](#consistent-error-responses)
  - [Error Recovery Patterns](#error-recovery-patterns)
  - [Graceful Degradation](#graceful-degradation)
- [Лучшие практики безопасности](#лучшие-практики-безопасности)
  - [Authentication и Authorization](#authentication-и-authorization)
  - [Input Validation и Sanitization](#input-validation-и-sanitization)
  - [CORS Configuration](#cors-configuration)
  - [API Keys и Tokens](#api-keys-и-tokens)
- [Performance Optimization](#performance-optimization)
  - [Caching Strategies](#caching-strategies)
  - [Database Optimization](#database-optimization)
  - [Response Compression](#response-compression)
  - [Asynchronous Processing](#asynchronous-processing)
- [Documentation](#documentation)
  - [API Documentation Standards](#api-documentation-standards)
  - [Interactive Documentation](#interactive-documentation)
- [Testing Strategies](#testing-strategies)
  - [Unit Testing](#unit-testing)
  - [Integration Testing](#integration-testing)
  - [Load Testing](#load-testing)
- [Monitoring и Analytics](#monitoring-и-analytics)
  - [API Metrics Collection](#api-metrics-collection)
  - [Logging Best Practices](#logging-best-practices)
  - [API Analytics](#api-analytics)
- [API Lifecycle Management](#api-lifecycle-management)
  - [API Versioning Strategy](#api-versioning-strategy)
  - [API Deprecation Process](#api-deprecation-process)
  - [API Governance](#api-governance)
- [Решение проблем](#решение-проблем)
  - [CORS блокирует запросы с фронтенда](#cors-блокирует-запросы-с-фронтенда)
  - [Rate limit превышен — клиент не знает, когда повторять](#rate-limit-превышен-клиент-не-знает-когда-повторять)
  - [Несогласованные форматы ошибок между endpoints](#несогласованные-форматы-ошибок-между-endpoints)
  - [Токен аутентификации не работает после деплоя](#токен-аутентификации-не-работает-после-деплоя)
  - [Breaking changes при версионировании](#breaking-changes-при-версионировании)
- [Лучшие практики (сводка)](#лучшие-практики-сводка)
- [См. также](#см-также)

## HTTP Methods и Status Codes

### HTTP Methods Usage
Ниже — примеры использования **HTTP**-методов для **CRUD** (Node.js/Express).
```javascript
// GET - получение ресурсов
app.get('/api/users', async (req, res) => {
  const users = await User.findAll();
  res.json(users);
});

app.get('/api/users/:id', async (req, res) => {
  const user = await User.findById(req.params.id);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }
  res.json(user);
});

// POST - создание ресурсов
app.post('/api/users', async (req, res) => {
  try {
    const user = new User(req.body);
    await user.save();
    res.status(201).json(user);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// PUT - полное обновление ресурса
app.put('/api/users/:id', async (req, res) => {
  try {
    const user = await User.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true }
    );
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }
    res.json(user);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// PATCH - частичное обновление ресурса
app.patch('/api/users/:id', async (req, res) => {
  try {
    const user = await User.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true }
    );
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }
    res.json(user);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// DELETE - удаление ресурса
app.delete('/api/users/:id', async (req, res) => {
  try {
    const user = await User.findByIdAndDelete(req.params.id);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }
    res.status(204).send();
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});
```

### Status Codes Best Practices
```javascript
// 2xx Success
const SUCCESS_CODES = {
  200: 'OK - успешный запрос',
  201: 'Created - ресурс создан',
  202: 'Accepted - запрос принят для обработки',
  204: 'No Content - успешный запрос без контента'
};

// 3xx Redirection
const REDIRECT_CODES = {
  301: 'Moved Permanently - постоянное перенаправление',
  302: 'Found - временное перенаправление',
  304: 'Not Modified - ресурс не изменялся'
};

// 4xx Client Error
const CLIENT_ERROR_CODES = {
  400: 'Bad Request - некорректный запрос',
  401: 'Unauthorized - требуется аутентификация',
  403: 'Forbidden - доступ запрещен',
  404: 'Not Found - ресурс не найден',
  405: 'Method Not Allowed - метод не поддерживается',
  409: 'Conflict - конфликт с текущим состоянием',
  422: 'Unprocessable Entity - валидационная ошибка',
  429: 'Too Many Requests - превышен лимит запросов'
};

// 5xx Server Error
const SERVER_ERROR_CODES = {
  500: 'Internal Server Error - внутренняя ошибка сервера',
  502: 'Bad Gateway - ошибка шлюза',
  503: 'Service Unavailable - сервис недоступен',
  504: 'Gateway Timeout - таймаут шлюза'
};

// Правильное использование статус кодов
app.post('/api/users', async (req, res) => {
  try {
    // Валидация входных данных
    const errors = validateUserData(req.body);
    if (errors.length > 0) {
      return res.status(422).json({
        error: 'Validation failed',
        details: errors
      });
    }

    // Проверка существования пользователя
    const existingUser = await User.findOne({ email: req.body.email });
    if (existingUser) {
      return res.status(409).json({
        error: 'User already exists',
        message: 'A user with this email already exists'
      });
    }

    const user = new User(req.body);
    await user.save();

    res.status(201).json({
      message: 'User created successfully',
      user: user,
      location: `/api/users/${user._id}`
    });
  } catch (error) {
    console.error('Error creating user:', error);
    res.status(500).json({
      error: 'Internal server error',
      message: 'An unexpected error occurred'
    });
  }
});
```

## API Design Patterns

### Content Negotiation
```javascript
// Accept header negotiation
app.get('/api/users/:id', (req, res) => {
  const accept = req.headers.accept;

  User.findById(req.params.id).then(user => {
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    if (accept.includes('application/xml')) {
      // Convert to XML
      const xml = convertToXML(user);
      res.set('Content-Type', 'application/xml');
      res.send(xml);
    } else if (accept.includes('application/json')) {
      res.json(user);
    } else {
      res.json(user); // Default to JSON
    }
  });
});

// Custom media types
app.get('/api/users/:id', (req, res) => {
  const accept = req.headers.accept;

  if (accept.includes('application/vnd.myapp.user.v2+json')) {
    // Return v2 format
    res.json(formatUserV2(user));
  } else if (accept.includes('application/vnd.myapp.user.v1+json')) {
    // Return v1 format
    res.json(formatUserV1(user));
  } else {
    res.status(406).json({
      error: 'Not Acceptable',
      supported: [
        'application/vnd.myapp.user.v1+json',
        'application/vnd.myapp.user.v2+json'
      ]
    });
  }
});
```

### Pagination
```javascript
// Cursor-based pagination (recommended for large datasets)
app.get('/api/users', async (req, res) => {
  const limit = parseInt(req.query.limit) || 20;
  const cursor = req.query.cursor;
  const direction = req.query.direction || 'next'; // 'next' or 'previous'

  let query = User.find();

  if (cursor) {
    if (direction === 'next') {
      query = query.where('_id').gt(cursor);
    } else {
      query = query.where('_id').lt(cursor);
    }
  }

  const users = await query.limit(limit + 1).sort({ _id: 1 });

  const hasNextPage = users.length > limit;
  const hasPreviousPage = !!cursor && direction === 'previous';

  if (hasNextPage) {
    users.pop(); // Remove extra item
  }

  const nextCursor = hasNextPage ? users[users.length - 1]._id : null;
  const previousCursor = hasPreviousPage ? users[0]._id : null;

  res.json({
    data: users,
    pagination: {
      hasNextPage,
      hasPreviousPage,
      nextCursor,
      previousCursor,
      limit
    }
  });
});

// Offset-based pagination (simpler but less efficient)
app.get('/api/products', async (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const limit = parseInt(req.query.limit) || 10;
  const offset = (page - 1) * limit;

  const [products, total] = await Promise.all([
    Product.find().skip(offset).limit(limit),
    Product.countDocuments()
  ]);

  const totalPages = Math.ceil(total / limit);

  res.json({
    data: products,
    pagination: {
      currentPage: page,
      totalPages,
      totalItems: total,
      itemsPerPage: limit,
      hasNextPage: page < totalPages,
      hasPreviousPage: page > 1
    }
  });
});
```

### Filtering и Searching
```javascript
// Query parameters for filtering
app.get('/api/products', async (req, res) => {
  const {
    category,
    minPrice,
    maxPrice,
    inStock,
    search,
    sortBy = 'createdAt',
    sortOrder = 'desc'
  } = req.query;

  let query = {};

  // Category filter
  if (category) {
    query.category = category;
  }

  // Price range filter
  if (minPrice || maxPrice) {
    query.price = {};
    if (minPrice) query.price.$gte = parseFloat(minPrice);
    if (maxPrice) query.price.$lte = parseFloat(maxPrice);
  }

  // Stock filter
  if (inStock !== undefined) {
    query.stock = inStock === 'true' ? { $gt: 0 } : 0;
  }

  // Text search
  if (search) {
    query.$text = { $search: search };
  }

  // Sorting
  const sort = {};
  sort[sortBy] = sortOrder === 'desc' ? -1 : 1;

  const products = await Product.find(query)
    .sort(sort)
    .limit(50); // Limit results for performance

  res.json({
    data: products,
    filters: {
      applied: Object.keys(req.query),
      total: products.length
    }
  });
});
```

### Rate Limiting
```javascript
// Express rate limiting middleware
const rateLimit = require('express-rate-limit');

// General API rate limiting
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
  message: {
    error: 'Too many requests',
    message: 'Please try again later',
    retryAfter: 900 // seconds
  },
  standardHeaders: true,
  legacyHeaders: false,
  handler: (req, res) => {
    res.status(429).json({
      error: 'Too Many Requests',
      message: 'Rate limit exceeded. Please try again later.',
      retryAfter: Math.ceil(req.rateLimit.resetTime / 1000)
    });
  }
});

// Stricter limits for authentication endpoints
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // Limit each IP to 5 login attempts per windowMs
  message: {
    error: 'Too many login attempts',
    message: 'Account temporarily locked due to too many failed login attempts'
  },
  skipSuccessfulRequests: true, // Don't count successful logins
  skipFailedRequests: false // Count failed attempts
});

// Apply rate limiting
app.use('/api/', apiLimiter);
app.use('/api/auth/login', authLimiter);

// Custom rate limiting logic
const customRateLimit = async (req, res, next) => {
  const clientId = req.user?.id || req.ip;
  const key = `ratelimit:${clientId}`;

  // Use Redis for distributed rate limiting
  const requests = await redis.incr(key);

  if (requests === 1) {
    await redis.expire(key, 60); // 1 minute window
  }

  if (requests > 10) { // 10 requests per minute
    return res.status(429).json({
      error: 'Rate limit exceeded',
      retryAfter: 60
    });
  }

  next();
};
```

## Versioning Strategies

### URL Path Versioning
```javascript
// URL path versioning
app.get('/api/v1/users', (req, res) => {
  // Version 1 implementation
  res.json({ version: 'v1', users: [] });
});

app.get('/api/v2/users', (req, res) => {
  // Version 2 implementation
  res.json({ version: 'v2', data: { users: [] } });
});

// Modular versioning
const v1Routes = require('./routes/v1/users');
const v2Routes = require('./routes/v2/users');

app.use('/api/v1/users', v1Routes);
app.use('/api/v2/users', v2Routes);
```

### Header Versioning
```javascript
// Accept header versioning
app.get('/api/users', (req, res) => {
  const version = req.headers['accept-version'] || 'v1';

  switch (version) {
    case 'v1':
      return res.json(formatUsersV1(users));
    case 'v2':
      return res.json(formatUsersV2(users));
    default:
      return res.status(400).json({
        error: 'Unsupported version',
        supportedVersions: ['v1', 'v2']
      });
  }
});

// Custom header versioning
app.get('/api/users', (req, res) => {
  const apiVersion = req.headers['x-api-version'];

  if (!apiVersion) {
    // Default to latest version
    return res.json(formatUsersLatest(users));
  }

  // Version-specific logic
  const versionedResponse = getVersionedResponse(users, apiVersion);
  res.json(versionedResponse);
});
```

### Media Type Versioning
```javascript
// Content-Type versioning
app.post('/api/users', (req, res) => {
  const contentType = req.headers['content-type'];

  if (contentType === 'application/vnd.myapi.user.v2+json') {
    // Handle v2 format
    const userV2 = parseUserV2(req.body);
    // Process v2 user
  } else if (contentType === 'application/vnd.myapi.user.v1+json') {
    // Handle v1 format
    const userV1 = parseUserV1(req.body);
    // Process v1 user
  } else {
    return res.status(415).json({
      error: 'Unsupported Media Type',
      message: 'Supported types: application/vnd.myapi.user.v1+json, application/vnd.myapi.user.v2+json'
    });
  }
});

// Response content negotiation
app.get('/api/users/:id', (req, res) => {
  const accept = req.headers.accept;

  if (accept.includes('application/vnd.myapi.user.v2+json')) {
    res.set('Content-Type', 'application/vnd.myapi.user.v2+json');
    return res.json(formatUserV2(user));
  } else {
    res.set('Content-Type', 'application/vnd.myapi.user.v1+json');
    return res.json(formatUserV1(user));
  }
});
```

### Semantic Versioning for APIs
```javascript
// Semantic versioning helper
class ApiVersion {
  constructor(version) {
    this.major = version.major || 1;
    this.minor = version.minor || 0;
    this.patch = version.patch || 0;
  }

  toString() {
    return `${this.major}.${this.minor}.${this.patch}`;
  }

  isCompatible(other) {
    return this.major === other.major;
  }
}

// Version comparison middleware
const versionMiddleware = (req, res, next) => {
  const requestedVersion = req.headers['x-api-version'];
  const currentVersion = new ApiVersion({ major: 2, minor: 1, patch: 0 });

  if (requestedVersion) {
    const requested = new ApiVersion(parseVersion(requestedVersion));

    if (!currentVersion.isCompatible(requested)) {
      return res.status(400).json({
        error: 'Incompatible API version',
        requested: requestedVersion,
        current: currentVersion.toString(),
        message: 'Major version mismatch'
      });
    }

    req.apiVersion = requested;
  } else {
    req.apiVersion = currentVersion;
  }

  next();
};

function parseVersion(versionString) {
  const [major, minor, patch] = versionString.split('.').map(Number);
  return { major, minor, patch };
}
```

## Error Handling

### Consistent Error Responses
```javascript
// Error response structure
class ApiError extends Error {
  constructor(code, message, details = null, statusCode = 500) {
    super(message);
    this.code = code;
    this.details = details;
    this.statusCode = statusCode;
    this.isOperational = true;

    Error.captureStackTrace(this, this.constructor);
  }
}

// Error codes
const ERROR_CODES = {
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  AUTHENTICATION_ERROR: 'AUTHENTICATION_ERROR',
  AUTHORIZATION_ERROR: 'AUTHORIZATION_ERROR',
  NOT_FOUND: 'NOT_FOUND',
  CONFLICT: 'CONFLICT',
  RATE_LIMIT_EXCEEDED: 'RATE_LIMIT_EXCEEDED',
  INTERNAL_ERROR: 'INTERNAL_ERROR'
};

// Error response formatter
const formatErrorResponse = (error, includeStack = false) => {
  const response = {
    error: {
      code: error.code || 'INTERNAL_ERROR',
      message: error.message,
      timestamp: new Date().toISOString(),
      requestId: generateRequestId()
    }
  };

  if (error.details) {
    response.error.details = error.details;
  }

  if (includeStack && error.stack) {
    response.error.stack = error.stack;
  }

  return response;
};

// Global error handler
app.use((error, req, res, next) => {
  let statusCode = error.statusCode || 500;
  let includeStack = process.env.NODE_ENV === 'development';

  // Log error
  console.error('API Error:', {
    message: error.message,
    stack: error.stack,
    url: req.url,
    method: req.method,
    ip: req.ip,
    userAgent: req.get('User-Agent')
  });

  // Handle specific error types
  if (error.name === 'ValidationError') {
    statusCode = 422;
    error = new ApiError(
      ERROR_CODES.VALIDATION_ERROR,
      'Validation failed',
      formatValidationErrors(error),
      statusCode
    );
  } else if (error.name === 'UnauthorizedError') {
    statusCode = 401;
    error = new ApiError(
      ERROR_CODES.AUTHENTICATION_ERROR,
      'Authentication required',
      null,
      statusCode
    );
  }

  res.status(statusCode).json(formatErrorResponse(error, includeStack));
});
```

### Error Recovery Patterns
```javascript
// Circuit breaker pattern
class CircuitBreaker {
  constructor(failureThreshold = 5, recoveryTimeout = 60000) {
    this.failureThreshold = failureThreshold;
    this.recoveryTimeout = recoveryTimeout;
    this.failureCount = 0;
    this.state = 'CLOSED'; // CLOSED, OPEN, HALF_OPEN
    this.nextAttempt = 0;
  }

  async execute(operation) {
    if (this.state === 'OPEN') {
      if (Date.now() < this.nextAttempt) {
        throw new Error('Circuit breaker is OPEN');
      }
      this.state = 'HALF_OPEN';
    }

    try {
      const result = await operation();
      this.onSuccess();
      return result;
    } catch (error) {
      this.onFailure();
      throw error;
    }
  }

  onSuccess() {
    this.failureCount = 0;
    this.state = 'CLOSED';
  }

  onFailure() {
    this.failureCount++;
    if (this.failureCount >= this.failureThreshold) {
      this.state = 'OPEN';
      this.nextAttempt = Date.now() + this.recoveryTimeout;
    }
  }
}

// Usage with external API calls
const externalApiBreaker = new CircuitBreaker(3, 30000);

app.get('/api/external-data', async (req, res) => {
  try {
    const data = await externalApiBreaker.execute(async () => {
      return await callExternalAPI();
    });
    res.json(data);
  } catch (error) {
    if (error.message === 'Circuit breaker is OPEN') {
      return res.status(503).json({
        error: 'Service temporarily unavailable',
        retryAfter: Math.ceil((externalApiBreaker.nextAttempt - Date.now()) / 1000)
      });
    }
    throw error;
  }
});
```

### Graceful Degradation
```javascript
// Graceful degradation with fallbacks
app.get('/api/user-profile/:id', async (req, res) => {
  const userId = req.params.id;

  try {
    // Primary data source
    const userProfile = await getUserProfileFromPrimary(userId);
    res.json(userProfile);
  } catch (primaryError) {
    console.warn('Primary data source failed:', primaryError.message);

    try {
      // Fallback to secondary data source
      const userProfile = await getUserProfileFromSecondary(userId);
      res.set('X-Degraded-Response', 'true');
      res.json(userProfile);
    } catch (secondaryError) {
      console.error('Secondary data source also failed:', secondaryError.message);

      // Return cached or default data
      const cachedProfile = await getCachedUserProfile(userId);
      if (cachedProfile) {
        res.set('X-Cached-Response', 'true');
        res.set('X-Degraded-Response', 'true');
        return res.json(cachedProfile);
      }

      // Last resort: return minimal response
      res.status(206).json({
        id: userId,
        status: 'partial',
        message: 'Profile temporarily unavailable',
        available: false
      });
    }
  }
});
```

## Лучшие практики безопасности

### Authentication и Authorization
```javascript
// JWT authentication middleware
const jwt = require('jsonwebtoken');

const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

  if (!token) {
    return res.status(401).json({ error: 'Access token required' });
  }

  jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({ error: 'Invalid or expired token' });
    }

    req.user = user;
    next();
  });
};

// Role-based authorization middleware
const authorize = (...roles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({ error: 'Authentication required' });
    }

    if (!roles.includes(req.user.role)) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }

    next();
  };
};

// Usage
app.get('/api/admin/users',
  authenticateToken,
  authorize('admin', 'superuser'),
  async (req, res) => {
    // Only admins can access this endpoint
    const users = await User.findAll();
    res.json(users);
  }
);
```

### Input Validation и Sanitization
```javascript
// Input validation with Joi
const Joi = require('joi');

const userSchema = Joi.object({
  name: Joi.string()
    .min(2)
    .max(50)
    .pattern(/^[a-zA-Z\s]+$/)
    .required(),

  email: Joi.string()
    .email({ minDomainSegments: 2 })
    .lowercase()
    .required(),

  password: Joi.string()
    .min(8)
    .max(128)
    .pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/)
    .required(),

  age: Joi.number()
    .integer()
    .min(13)
    .max(120),

  role: Joi.string()
    .valid('user', 'admin')
    .default('user')
});

// Validation middleware
const validateUser = (req, res, next) => {
  const { error, value } = userSchema.validate(req.body, {
    abortEarly: false,
    stripUnknown: true
  });

  if (error) {
    const errors = error.details.map(detail => ({
      field: detail.path.join('.'),
      message: detail.message,
      value: detail.context.value
    }));

    return res.status(422).json({
      error: 'Validation failed',
      details: errors
    });
  }

  req.body = value; // Use sanitized values
  next();
};

// XSS protection
const sanitizeInput = (input) => {
  if (typeof input === 'string') {
    return input.replace(/[<>]/g, '');
  }
  return input;
};

// SQL injection protection (with parameterized queries)
app.get('/api/users/search', async (req, res) => {
  const { name } = req.query;

  // Safe parameterized query
  const users = await User.findAll({
    where: {
      name: {
        [Op.iLike]: `%${name}%` // Sequelize parameter binding prevents injection
      }
    }
  });

  res.json(users);
});
```

### CORS Configuration
```javascript
// CORS configuration
const cors = require('cors');

const corsOptions = {
  origin: (origin, callback) => {
    const allowedOrigins = [
      'https://myapp.com',
      'https://www.myapp.com',
      'https://staging.myapp.com'
    ];

    // Allow requests with no origin (mobile apps, etc.)
    if (!origin) return callback(null, true);

    if (allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With'],
  exposedHeaders: ['X-Total-Count', 'X-Rate-Limit-Remaining'],
  maxAge: 86400, // 24 hours
  optionsSuccessStatus: 200
};

app.use(cors(corsOptions));

// Pre-flight request handling
app.options('*', cors(corsOptions));
```

### API Keys и Tokens
```javascript
// API key authentication
const apiKeyAuth = (req, res, next) => {
  const apiKey = req.headers['x-api-key'] || req.query.api_key;

  if (!apiKey) {
    return res.status(401).json({ error: 'API key required' });
  }

  // Validate API key (in production, check against database/cache)
  if (apiKey !== process.env.VALID_API_KEY) {
    return res.status(403).json({ error: 'Invalid API key' });
  }

  req.apiKey = apiKey;
  next();
};

// Rate limiting per API key
const apiKeyRateLimit = (req, res, next) => {
  const key = `apikey:${req.apiKey}`;
  // Implement rate limiting logic per API key
  next();
};

// HMAC signature verification for webhook endpoints
const crypto = require('crypto');

const verifyWebhookSignature = (req, res, next) => {
  const signature = req.headers['x-webhook-signature'];
  const body = JSON.stringify(req.body);
  const expectedSignature = crypto
    .createHmac('sha256', process.env.WEBHOOK_SECRET)
    .update(body)
    .digest('hex');

  if (!crypto.timingSafeEqual(
    Buffer.from(signature, 'hex'),
    Buffer.from(expectedSignature, 'hex')
  )) {
    return res.status(401).json({ error: 'Invalid signature' });
  }

  next();
};
```

## Performance Optimization

### Caching Strategies
```javascript
// HTTP caching headers
app.get('/api/products', (req, res) => {
  res.set({
    'Cache-Control': 'public, max-age=300', // 5 minutes
    'ETag': generateETag(products),
    'Last-Modified': new Date().toUTCString()
  });

  // Check conditional requests
  if (req.headers['if-none-match'] === generateETag(products)) {
    return res.status(304).end();
  }

  res.json(products);
});

// Redis caching layer
const redis = require('redis');
const client = redis.createClient();

const cacheMiddleware = (duration) => {
  return async (req, res, next) => {
    const key = `__express__${req.originalUrl}`;

    // Try to get cached response
    client.get(key, (err, cachedData) => {
      if (cachedData) {
        res.set('X-Cache', 'HIT');
        return res.json(JSON.parse(cachedData));
      }

      // Store original send function
      const originalSend = res.json;

      // Override res.json to cache response
      res.json = function(data) {
        client.setex(key, duration, JSON.stringify(data));
        res.set('X-Cache', 'MISS');
        originalSend.call(this, data);
      };

      next();
    });
  };
};

// Usage
app.get('/api/products', cacheMiddleware(300), getProductsHandler);
```

### Database Optimization
```javascript
// Connection pooling
const { Pool } = require('pg');

const pool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  database: process.env.DB_NAME,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  max: 20, // Maximum number of clients in pool
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 2000,
});

// Query optimization with prepared statements
const getUserById = async (userId) => {
  const client = await pool.connect();

  try {
    // Use prepared statement
    const query = {
      text: 'SELECT id, name, email FROM users WHERE id = $1',
      values: [userId],
    };

    const result = await client.query(query);
    return result.rows[0];
  } finally {
    client.release();
  }
};

// Pagination with LIMIT/OFFSET
app.get('/api/products', async (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const limit = Math.min(parseInt(req.query.limit) || 20, 100); // Max 100 items
  const offset = (page - 1) * limit;

  const [products, total] = await Promise.all([
    pool.query(
      'SELECT * FROM products ORDER BY created_at DESC LIMIT $1 OFFSET $2',
      [limit, offset]
    ),
    pool.query('SELECT COUNT(*) FROM products')
  ]);

  res.json({
    data: products.rows,
    pagination: {
      page,
      limit,
      total: parseInt(total.rows[0].count),
      totalPages: Math.ceil(parseInt(total.rows[0].count) / limit)
    }
  });
});
```

### Response Compression
```javascript
// Compression middleware
const compression = require('compression');

const compressionOptions = {
  level: 6, // Compression level (1-9)
  threshold: 1024, // Only compress responses larger than 1KB
  filter: (req, res) => {
    // Don't compress responses with this request header
    if (req.headers['x-no-compression']) {
      return false;
    }

    // Use default filter function
    return compression.filter(req, res);
  }
};

app.use(compression(compressionOptions));

// Custom compression for specific endpoints
app.get('/api/large-dataset', compression({ level: 9 }), async (req, res) => {
  const data = await getLargeDataset();
  res.json(data);
});
```

### Asynchronous Processing
```javascript
// Background job processing with Bull
const Queue = require('bull');

const emailQueue = new Queue('email', {
  redis: process.env.REDIS_URL
});

// Add job to queue
app.post('/api/users', async (req, res) => {
  const user = new User(req.body);
  await user.save();

  // Send welcome email asynchronously
  await emailQueue.add('welcome', {
    userId: user.id,
    email: user.email,
    name: user.name
  });

  res.status(201).json(user);
});

// Process jobs
emailQueue.process('welcome', async (job) => {
  const { userId, email, name } = job.data;

  try {
    await sendWelcomeEmail(email, name);
    console.log(`Welcome email sent to ${email}`);
  } catch (error) {
    console.error(`Failed to send welcome email to ${email}:`, error);
    throw error; // Job will be retried
  }
});

// Job monitoring
emailQueue.on('completed', (job) => {
  console.log(`Job ${job.id} completed`);
});

emailQueue.on('failed', (job, err) => {
  console.error(`Job ${job.id} failed:`, err.message);
});
```

## Documentation

### API Documentation Standards
```yaml
# API documentation structure
const apiDocs = {
  info: {
    title: 'My API',
    version: '2.1.0',
    description: 'REST API for managing users and products',
    contact: {
      name: 'API Support',
      email: 'api@mycompany.com',
      url: 'https://mycompany.com/support'
    },
    license: {
      name: 'MIT',
      url: 'https://opensource.org/licenses/MIT'
    }
  },
  servers: [
    {
      url: 'https://api.mycompany.com/v2',
      description: 'Production server'
    },
    {
      url: 'https://staging-api.mycompany.com/v2',
      description: 'Staging server'
    }
  ],
  security: [
    {
      bearerAuth: []
    },
    {
      apiKeyAuth: []
    }
  ],
  paths: {
    '/users': {
      get: {
        summary: 'Get users list',
        description: 'Retrieve a paginated list of users',
        parameters: [
          {
            name: 'page',
            in: 'query',
            schema: { type: 'integer', default: 1 },
            description: 'Page number'
          },
          {
            name: 'limit',
            in: 'query',
            schema: { type: 'integer', default: 20, maximum: 100 },
            description: 'Items per page'
          }
        ],
        responses: {
          '200': {
            description: 'Successful response',
            content: {
              'application/json': {
                schema: {
                  type: 'object',
                  properties: {
                    data: {
                      type: 'array',
                      items: { $ref: '#/components/schemas/User' }
                    },
                    pagination: { $ref: '#/components/schemas/Pagination' }
                  }
                }
              }
            }
          },
          '400': { $ref: '#/components/responses/BadRequest' }
        }
      }
    }
  },
  components: {
    schemas: {
      User: {
        type: 'object',
        required: ['id', 'email'],
        properties: {
          id: { type: 'string', format: 'uuid' },
          email: { type: 'string', format: 'email' },
          name: { type: 'string', minLength: 1, maxLength: 100 },
          createdAt: { type: 'string', format: 'date-time' }
        }
      },
      Pagination: {
        type: 'object',
        properties: {
          currentPage: { type: 'integer' },
          totalPages: { type: 'integer' },
          totalItems: { type: 'integer' },
          itemsPerPage: { type: 'integer' }
        }
      }
    },
    responses: {
      BadRequest: {
        description: 'Bad request',
        content: {
          'application/json': {
            schema: { $ref: '#/components/schemas/Error' }
          }
        }
      }
    },
    securitySchemes: {
      bearerAuth: {
        type: 'http',
        scheme: 'bearer',
        bearerFormat: 'JWT'
      },
      apiKeyAuth: {
        type: 'apiKey',
        in: 'header',
        name: 'X-API-Key'
      }
    }
  }
};
```

### Interactive Documentation
```javascript
// Swagger UI setup
const swaggerUi = require('swagger-ui-express');
const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'My API',
      version: '2.1.0',
      description: 'REST API documentation',
    },
    servers: [
      {
        url: 'https://api.mycompany.com/v2',
        description: 'Production server',
      },
    ],
  },
  apis: ['./routes/*.js'], // Path to API route files
};

const specs = swaggerJsdoc(options);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(specs));

// Document routes with JSDoc comments
/
 * @swagger
 * /users:
 *   get:
 *     summary: Retrieve a list of users
 *     description: Retrieve a paginated list of users with optional filtering
 *     tags:
 *       - Users
 *     parameters:
 *       - in: query
 *         name: page
 *         schema:
 *           type: integer
 *           default: 1
 *         description: Page number for pagination
 *       - in: query
 *         name: limit
 *         schema:
 *           type: integer
 *           default: 20
 *           maximum: 100
 *         description: Number of items per page
 *     responses:
 *       200:
 *         description: Successful response
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 data:
 *                   type: array
 *                   items:
 *                     $ref: '#/components/schemas/User'
 *                 pagination:
 *                   $ref: '#/components/schemas/Pagination'
 *       400:
 *         $ref: '#/components/responses/BadRequest'
 */
app.get('/users', getUsersHandler);
```

## Testing Strategies

### Unit Testing
```javascript
// API handler unit test
const request = require('supertest');
const app = require('../app');
const User = require('../models/User');

describe('GET /api/users', () => {
  beforeEach(async () => {
    // Clear database
    await User.deleteMany({});
  });

  it('should return empty array when no users exist', async () => {
    const response = await request(app)
      .get('/api/users')
      .expect(200);

    expect(response.body).toEqual({
      data: [],
      pagination: {
        currentPage: 1,
        totalPages: 0,
        totalItems: 0,
        itemsPerPage: 20,
        hasNextPage: false,
        hasPreviousPage: false
      }
    });
  });

  it('should return users with pagination', async () => {
    // Create test users
    await User.create([
      { name: 'User 1', email: 'user1@test.com' },
      { name: 'User 2', email: 'user2@test.com' },
      { name: 'User 3', email: 'user3@test.com' }
    ]);

    const response = await request(app)
      .get('/api/users?limit=2')
      .expect(200);

    expect(response.body.data).toHaveLength(2);
    expect(response.body.pagination.totalItems).toBe(3);
    expect(response.body.pagination.hasNextPage).toBe(true);
  });

  it('should handle validation errors', async () => {
    const response = await request(app)
      .post('/api/users')
      .send({ name: '', email: 'invalid-email' })
      .expect(422);

    expect(response.body.error).toBe('Validation failed');
    expect(response.body.details).toHaveLength(2);
  });
});
```

### Integration Testing
```javascript
// Database integration test
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

let mongoServer;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  const mongoUri = mongoServer.getUri();
  await mongoose.connect(mongoUri);
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

describe('User API Integration', () => {
  it('should create and retrieve user', async () => {
    // Create user
    const createResponse = await request(app)
      .post('/api/users')
      .send({
        name: 'Test User',
        email: 'test@example.com',
        password: 'password123'
      })
      .expect(201);

    const userId = createResponse.body.user.id;

    // Retrieve user
    const getResponse = await request(app)
      .get(`/api/users/${userId}`)
      .expect(200);

    expect(getResponse.body.name).toBe('Test User');
    expect(getResponse.body.email).toBe('test@example.com');
  });

  it('should handle concurrent requests', async () => {
    const promises = Array(10).fill().map((_, i) =>
      request(app)
        .post('/api/users')
        .send({
          name: `User ${i}`,
          email: `user${i}@test.com`,
          password: 'password123'
        })
    );

    const responses = await Promise.all(promises);

    responses.forEach(response => {
      expect(response.status).toBe(201);
    });
  });
});
```

### Load Testing
```javascript
// Load testing with Artillery
// artillery.yml
config:
  target: 'https://api.mycompany.com'
  phases:
    - duration: 60
      arrivalRate: 10
      name: "Warm up phase"
    - duration: 120
      arrivalRate: 10
      rampTo: 50
      name: "Ramp up phase"
    - duration: 60
      arrivalRate: 50
      name: "Sustained load phase"
  defaults:
    headers:
      Authorization: 'Bearer {{token}}'

scenarios:
  - name: 'Get users'
    weight: 70
    requests:
      - get:
          url: '/api/users'
          qs:
            page: '{{page}}'
            limit: 20

  - name: 'Create user'
    weight: 20
    requests:
      - post:
          url: '/api/users'
          json:
            name: 'Load Test User {{id}}'
            email: 'loadtest{{id}}@example.com'
            password: 'password123'

  - name: 'Get single user'
    weight: 10
    requests:
      - get:
          url: '/api/users/{{userId}}'

// Custom load test script
const autocannon = require('autocannon');

const instance = autocannon({
  url: 'https://api.mycompany.com/api/users',
  connections: 100,    // Number of concurrent connections
  duration: 10,        // Test duration in seconds
  headers: {
    'Authorization': 'Bearer your-token'
  }
}, (err, results) => {
  if (err) {
    console.error('Load test failed:', err);
    return;
  }

  console.log('Load test results:');
  console.log('Requests/sec:', results.requests.average);
  console.log('Latency avg:', results.latency.average);
  console.log('Latency p99:', results.latency.p99);
  console.log('Errors:', results.errors);
});
```

## Monitoring и Analytics

### API Metrics Collection
```javascript
// Prometheus metrics for API
const promClient = require('prom-client');

const register = new promClient.Registry();

// HTTP request duration histogram
const httpRequestDuration = new promClient.Histogram({
  name: 'http_request_duration_seconds',
  help: 'Duration of HTTP requests in seconds',
  labelNames: ['method', 'route', 'status_code'],
  buckets: [0.1, 0.5, 1, 2, 5, 10]
});

// Request counter
const httpRequestsTotal = new promClient.Counter({
  name: 'http_requests_total',
  help: 'Total number of HTTP requests',
  labelNames: ['method', 'route', 'status_code']
});

// Response size histogram
const httpResponseSize = new promClient.Histogram({
  name: 'http_response_size_bytes',
  help: 'Size of HTTP responses in bytes',
  labelNames: ['method', 'route'],
  buckets: promClient.exponentialBuckets(100, 2, 10)
});

// Register metrics
register.registerMetric(httpRequestDuration);
register.registerMetric(httpRequestsTotal);
register.registerMetric(httpResponseSize);

// Metrics middleware
const metricsMiddleware = (req, res, next) => {
  const start = Date.now();

  res.on('finish', () => {
    const duration = (Date.now() - start) / 1000;

    const route = req.route ? req.route.path : req.path;
    const method = req.method;
    const statusCode = res.statusCode;

    // Record metrics
    httpRequestDuration
      .labels(method, route, statusCode)
      .observe(duration);

    httpRequestsTotal
      .labels(method, route, statusCode)
      .inc();

    // Record response size if available
    if (res.get('Content-Length')) {
      const size = parseInt(res.get('Content-Length'));
      httpResponseSize
        .labels(method, route)
        .observe(size);
    }
  });

  next();
};

// Metrics endpoint
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});
```

### Logging Best Practices
```javascript
// Structured logging with Winston
const winston = require('winston');

const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  defaultMeta: { service: 'api-service' },
  transports: [
    // Console logging for development
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.simple()
      )
    }),

    // File logging for production
    new winston.transports.File({
      filename: 'logs/error.log',
      level: 'error'
    }),
    new winston.transports.File({
      filename: 'logs/combined.log'
    })
  ]
});

// Request logging middleware
const requestLogger = (req, res, next) => {
  const start = Date.now();

  res.on('finish', () => {
    const duration = Date.now() - start;

    logger.info('HTTP Request', {
      method: req.method,
      url: req.url,
      statusCode: res.statusCode,
      duration,
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      userId: req.user?.id,
      requestId: req.id
    });
  });

  next();
};

// Error logging
app.use((error, req, res, next) => {
  logger.error('API Error', {
    message: error.message,
    stack: error.stack,
    url: req.url,
    method: req.method,
    ip: req.ip,
    userId: req.user?.id,
    requestId: req.id,
    statusCode: error.statusCode || 500
  });

  // Don't expose stack traces in production
  const includeStack = process.env.NODE_ENV !== 'production';
  res.status(error.statusCode || 500).json(formatErrorResponse(error, includeStack));
});
```

### API Analytics
```javascript
// API usage analytics
const apiAnalytics = (req, res, next) => {
  const start = Date.now();

  res.on('finish', () => {
    const duration = Date.now() - start;

    // Send analytics data to monitoring system
    analytics.track('api_request', {
      method: req.method,
      route: req.route?.path || req.path,
      statusCode: res.statusCode,
      duration,
      userId: req.user?.id,
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      responseSize: parseInt(res.get('Content-Length')) || 0,
      timestamp: new Date().toISOString()
    });
  });

  next();
};

// API health check endpoint
app.get('/health', async (req, res) => {
  const health = {
    status: 'healthy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    version: process.env.npm_package_version,
    checks: {}
  };

  try {
    // Database health check
    await mongoose.connection.db.admin().ping();
    health.checks.database = 'healthy';
  } catch (error) {
    health.checks.database = 'unhealthy';
    health.status = 'degraded';
  }

  try {
    // External service health check
    await checkExternalService();
    health.checks.externalService = 'healthy';
  } catch (error) {
    health.checks.externalService = 'unhealthy';
    health.status = 'degraded';
  }

  const statusCode = health.status === 'healthy' ? 200 : 503;
  res.status(statusCode).json(health);
});
```

## API Lifecycle Management

### API Versioning Strategy
```javascript
// Version management utility
class ApiVersionManager {
  constructor() {
    this.versions = new Map();
    this.deprecationSchedule = new Map();
  }

  registerVersion(version, handlers, deprecationDate = null) {
    this.versions.set(version, handlers);
    if (deprecationDate) {
      this.deprecationSchedule.set(version, deprecationDate);
    }
  }

  getHandler(version) {
    const handlers = this.versions.get(version);
    if (!handlers) {
      throw new ApiError('UNSUPPORTED_VERSION', `API version ${version} is not supported`);
    }

    // Check if version is deprecated
    const deprecationDate = this.deprecationSchedule.get(version);
    if (deprecationDate && new Date() > deprecationDate) {
      throw new ApiError('DEPRECATED_VERSION', `API version ${version} is deprecated`);
    }

    return handlers;
  }

  listVersions() {
    return Array.from(this.versions.keys()).map(version => ({
      version,
      deprecated: this.deprecationSchedule.has(version),
      deprecationDate: this.deprecationSchedule.get(version)
    }));
  }
}

// Usage
const versionManager = new ApiVersionManager();

// Register API versions
versionManager.registerVersion('v1', v1Handlers);
versionManager.registerVersion('v2', v2Handlers, new Date('2024-12-31'));

// Version routing middleware
const versionRouter = (req, res, next) => {
  const version = req.headers['accept-version'] || req.headers['x-api-version'] || 'v1';

  try {
    req.versionHandlers = versionManager.getHandler(version);
    req.apiVersion = version;
    next();
  } catch (error) {
    res.status(400).json(formatErrorResponse(error));
  }
};
```

### API Deprecation Process
```javascript
// Deprecation middleware
const deprecationMiddleware = (version, removalDate) => {
  return (req, res, next) => {
    const warning = `API version ${version} is deprecated and will be removed on ${removalDate.toISOString().split('T')[0]}`;

    res.set({
      'Deprecation': removalDate.toISOString(),
      'Warning': `299 - "${warning}"`,
      'X-API-Version': version,
      'X-API-Deprecated': 'true'
    });

    // Log deprecation usage
    logger.warn('Deprecated API usage', {
      version,
      endpoint: req.path,
      method: req.method,
      userId: req.user?.id,
      ip: req.ip
    });

    next();
  };
};

// Deprecation schedule
const deprecationSchedule = {
  'v1': {
    deprecated: true,
    removalDate: new Date('2024-06-01'),
    migrationGuide: 'https://docs.mycompany.com/api/migration/v1-to-v2'
  }
};

// Apply deprecation middleware
app.use('/api/v1/*', (req, res, next) => {
  const deprecation = deprecationSchedule['v1'];
  if (deprecation.deprecated) {
    deprecationMiddleware('v1', deprecation.removalDate)(req, res, next);
  } else {
    next();
  }
});
```

### API Governance
```yaml
// API governance rules
const apiGovernance = {
  naming: {
    resources: 'snake_case',
    parameters: 'camelCase',
    headers: 'X-Custom-Header'
  },

  limits: {
    maxUriLength: 2048,
    maxPayloadSize: '10MB',
    maxQueryParameters: 50,
    rateLimitPerHour: 1000
  },

  security: {
    requireHttps: true,
    maxTokenLifetime: '24h',
    requireMfaForAdmins: true,
    encryptSensitiveData: true
  },

  documentation: {
    requireOpenApiSpec: true,
    requireExamples: true,
    requireErrorCodes: true,
    updateDocsOnChange: true
  },

  monitoring: {
    requireMetrics: true,
    requireLogging: true,
    alertOnErrors: true,
    trackPerformance: true
  }
};

// Governance validation middleware
const governanceValidator = (req, res, next) => {
  // Check URI length
  if (req.originalUrl.length > apiGovernance.limits.maxUriLength) {
    return res.status(414).json({
      error: 'URI Too Long',
      maxLength: apiGovernance.limits.maxUriLength
    });
  }

  // Check HTTPS
  if (apiGovernance.security.requireHttps && req.protocol !== 'https') {
    return res.status(403).json({
      error: 'HTTPS Required',
      message: 'This API requires HTTPS connections'
    });
  }

  // Check payload size
  const contentLength = parseInt(req.headers['content-length']);
  if (contentLength > parseSize(apiGovernance.limits.maxPayloadSize)) {
    return res.status(413).json({
      error: 'Payload Too Large',
      maxSize: apiGovernance.limits.maxPayloadSize
    });
  }

  next();
};

function parseSize(sizeString) {
  const units = { 'B': 1, 'KB': 1024, 'MB': 1024*1024, 'GB': 1024*1024*1024 };
  const match = sizeString.match(/^(\d+)([BKMG]B?)$/);
  return match ? parseInt(match[1]) * units[match[2]] : 0;
}
```

## Решение проблем

### CORS блокирует запросы с фронтенда

**Проблема:** Браузер отклоняет запросы с `https://app.example.com` к `https://api.example.com` с ошибкой **CORS**.

**Причины:** `Access-Control-Allow-Origin` не включает origin фронтенда; `credentials: true` требует явного `origin` (не `*`); метод или заголовок не разрешены.

**Решение:** Настроить `allowedOrigins` для конкретных доменов; при `credentials: true` не использовать `*`; добавить `allowedMethods` и `allowedHeaders`; отдавать корректные заголовки в ответ на `OPTIONS` preflight.

### Rate limit превышен — клиент не знает, когда повторять

**Проблема:** Ответ `429 Too Many Requests` без информации о времени разблокировки.

**Причины:** Не указаны заголовки `Retry-After` или `X-RateLimit-Reset`; тело ответа не содержит `retryAfter`.

**Решение:** Добавлять `Retry-After` (секунды) или `X-RateLimit-Reset` (Unix timestamp); в теле ошибки включать `retryAfter`; документировать лимиты в `X-RateLimit-Limit`, `X-RateLimit-Remaining`.

### Несогласованные форматы ошибок между endpoints

**Проблема:** Один endpoint возвращает `{ "error": "..." }`, другой — `{ "message": "...", "code": "..." }`; клиенту сложно обрабатывать единообразно.

**Причины:** Отсутствие централизованной обработки; разные middleware или handler для разных маршрутов.

**Решение:** Ввести единый формат (например, RFC 7807 или внутренний стандарт); использовать `@ControllerAdvice` / глобальный error handler; документировать формат в **OpenAPI** (`components.responses`).

### Токен аутентификации не работает после деплоя

**Проблема:** После обновления сервера клиенты получают `401` даже с валидным **JWT**.

**Причины:** Смена секрета подписи; использование нестабильного ключа (например, зависящего от времени запуска); разные инстансы с разными секретами.

**Решение:** Хранить секрет в env/vault; при смене ключа поддерживать grace period с двумя ключами; для кластера — общий секрет или **JWKS** endpoint.

### Breaking changes при версионировании

**Проблема:** Обновление формата ответа ломает старых клиентов без явного перехода на новую версию.

**Причины:** Изменение структуры без смены версии в path/header; несовместимое переименование или удаление полей.

**Решение:** Использовать **URI versioning** (`/v1`, `/v2`) или **header versioning**; при breaking changes — новая мажорная версия; документировать migration guide; отправлять `Deprecation` и `Sunset` заголовки для устаревших версий.

## Лучшие практики (сводка)

- Используйте правильные **HTTP**-методы и коды состояния; придерживайтесь идемпотентности и безопасности методов.
- Версионируйте **API** через **URL** или заголовки; документируйте изменения.
- Обрабатывайте ошибки единообразно (формат ответа, коды); не раскрывайте внутренние детали.
- Применяйте аутентификацию и авторизацию (OAuth2, `API keys`); валидируйте входные данные.
- Документируйте **API** (OpenAPI/Swagger); пишите тесты и мониторьте метрики и логи.
## См. также
- [[rest-api-design|REST API Design]] — основы **REST API**
- [[graphql|GraphQL]] — альтернативный подход к **API**
- [[grpc|gRPC]] — высокопроизводительные **API**
- [OpenAPI / Swagger](https://spec.openapis.org/oas/latest.html) — **API** документация
- [Security](../../../security/) — безопасность **API**
