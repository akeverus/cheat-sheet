---
title: "Архитектурные паттерны"
description: "Этот документ охватывает основные архитектурные паттерны и принципы проектирования программных систем. Здесь рассматриваются классические паттерны, современные подходы и best practices для создания масштабируемых, поддерживаемых и надежных приложений."
tags:
  - architecture
  - architecture-patterns
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Архитектурные паттерны

Этот документ охватывает основные архитектурные паттерны и принципы проектирования программных систем. Здесь рассматриваются классические паттерны, современные подходы и **best practices** для создания масштабируемых, поддерживаемых и надежных приложений.

## Полезные ссылки
- [SOLID Principles (Baeldung)](https://www.baeldung.com/solid-principles) — принципы **SOLID**
- [GRASP Patterns (Wikipedia)](https://en.wikipedia.org/wiki/GRASP_(object-oriented_design))
- [Clean Architecture (Uncle Bob)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/) — порты и адаптеры
- [Domain-Driven Design (Martin Fowler)](https://martinfowler.com/bliki/DomainDrivenDesign.html) — **DDD**

## Содержание

- [Основные принципы архитектуры](#основные-принципы-архитектуры)
  - [**SOLID** принципы](#solid-принципы)
  - [**GRASP** принципы](#grasp-принципы)
- [Монолитная архитектура](#монолитная-архитектура)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
  - [Модуляризация монолита](#модуляризация-монолита)
- [Многоуровневая архитектура](#многоуровневая-архитектура)
  - [**Presentation Layer**](#presentation-layer)
  - [**Business Logic Layer**](#business-logic-layer)
  - [**Data Access Layer**](#data-access-layer)
- [Клиент-серверная архитектура](#клиент-серверная-архитектура)
  - [**Thin Client**](#thin-client)
  - [**Thick Client** (**Rich Client**)](#thick-client-rich-client)
- [Трехуровневая архитектура](#трехуровневая-архитектура)
  - [**Presentation Tier**](#presentation-tier)
  - [**Application Tier** (**Business Logic**)](#application-tier-business-logic)
  - [**Data Tier**](#data-tier)
- [Гексагональная архитектура](#гексагональная-архитектура)
  - [**Ports and Adapters**](#ports-and-adapters)
  - [**Adapters**](#adapters)
  - [**Configuration** и **Dependency Injection**](#configuration-и-dependency-injection)
- [Чистая архитектура](#чистая-архитектура)
  - [**Entities** (**Domain Objects**)](#entities-domain-objects)
  - [**Use Cases** (**Application Layer**)](#use-cases-application-layer)
  - [**Interface Adapters**](#interface-adapters)
  - [**Frameworks** & **Drivers**](#frameworks-drivers)
- [Паттерны интеграции](#паттерны-интеграции)
  - [**API Gateway Pattern**](#api-gateway-pattern)
  - [**Circuit Breaker Pattern**](#circuit-breaker-pattern)
  - [**Saga Pattern**](#saga-pattern)
- [Распределенные паттерны](#распределенные-паттерны)
  - [**Bulkhead Pattern**](#bulkhead-pattern)
  - [**Ambassador Pattern**](#ambassador-pattern)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основные принципы архитектуры

### **SOLID** принципы

Ниже — пример **SOLID** принципов (**TypeScript**).
```typescript
// Single Responsibility Principle (SRP)
// Класс должен иметь только одну причину для изменения

class UserService {
  // Правильно: только работа с пользователями
  async createUser(userData: UserData): Promise<User> {
    // Логика создания пользователя
  }

  async getUserById(id: string): Promise<User> {
    // Логика получения пользователя
  }

  // Неправильно: смешивание ответственностей
  async sendWelcomeEmail(user: User): Promise<void> {
    // Логика отправки email - должна быть в другом классе
  }
}

// Open/Closed Principle (OCP)
// Классы должны быть открыты для расширения, но закрыты для модификации

interface PaymentProcessor {
  process(payment: Payment): Promise<PaymentResult>;
}

class StripePaymentProcessor implements PaymentProcessor {
  async process(payment: Payment): Promise<PaymentResult> {
    // Stripe логика
  }
}

// Расширение без модификации существующего кода
class PayPalPaymentProcessor implements PaymentProcessor {
  async process(payment: Payment): Promise<PaymentResult> {
    // PayPal логика
  }
}

// Liskov Substitution Principle (LSP)
// Подклассы должны быть заменяемыми на базовый класс

class Rectangle {
  protected width: number;
  protected height: number;

  setWidth(width: number): void {
    this.width = width;
  }

  setHeight(height: number): void {
    this.height = height;
  }

  getArea(): number {
    return this.width * this.height;
  }
}

// Нарушение LSP: Square не может заменить Rectangle
class Square extends Rectangle {
  setWidth(width: number): void {
    this.width = width;
    this.height = width; // Проблема: изменение поведения
  }

  setHeight(height: number): void {
    this.width = height;
    this.height = height;
  }
}

// Правильный подход: композиция вместо наследования
class Square {
  private side: number;

  constructor(side: number) {
    this.side = side;
  }

  setSide(side: number): void {
    this.side = side;
  }

  getArea(): number {
    return this.side * this.side;
  }
}

// Interface Segregation Principle (ISP)
// Клиенты не должны зависеть от интерфейсов, которые они не используют

// Неправильно: жирный интерфейс
interface Worker {
  work(): void;
  eat(): void;
  sleep(): void;
}

// Правильно: разделение интерфейсов
interface Workable {
  work(): void;
}

interface Eatable {
  eat(): void;
}

interface Sleepable {
  sleep(): void;
}

class HumanWorker implements Workable, Eatable, Sleepable {
  work(): void { /* ... */ }
  eat(): void { /* ... */ }
  sleep(): void { /* ... */ }
}

class RobotWorker implements Workable {
  work(): void { /* ... */ }
  // Робот не ест и не спит
}

// Dependency Inversion Principle (DIP)
// Модули верхнего уровня не должны зависеть от модулей нижнего уровня

// Неправильно: высокоуровневая логика зависит от низкоуровневой
class OrderService {
  private database: MySQLDatabase;

  constructor() {
    this.database = new MySQLDatabase();
  }
}

// Правильно: зависимость от абстракции
interface Database {
  save(order: Order): Promise<void>;
  find(id: string): Promise<Order>;
}

class OrderService {
  constructor(private database: Database) {}

  async createOrder(orderData: OrderData): Promise<Order> {
    const order = new Order(orderData);
    await this.database.save(order);
    return order;
  }
}
```

### **GRASP** принципы
```typescript
// Information Expert
// Объект должен содержать всю информацию, необходимую для выполнения его обязанностей

class Order {
  private items: OrderItem[] = [];
  private taxRate: number = 0.1;

  addItem(item: OrderItem): void {
    this.items.push(item);
  }

  // Order является экспертом по расчету стоимости
  getTotal(): number {
    const subtotal = this.items.reduce((sum, item) => sum + item.getPrice(), 0);
    return subtotal * (1 + this.taxRate);
  }

  // Неправильно: расчет в другом классе
  // getTotal(orderService: OrderService): number
}

// Creator
// Класс B должен создавать экземпляры класса A, если:
// - B содержит A
// - B агрегирует A
// - B использует A
// - B имеет данные инициализации для A

class Order {
  private items: OrderItem[] = [];

  // Order создает OrderItem (содержит)
  addItem(product: Product, quantity: number): void {
    const item = new OrderItem(product, quantity);
    this.items.push(item);
  }
}

// Controller
// Объекты, отвечающие за обработку системных событий

class OrderController {
  constructor(
    private orderService: OrderService,
    private notificationService: NotificationService
  ) {}

  // Controller обрабатывает HTTP запросы
  async createOrder(req: Request, res: Response): Promise<void> {
    try {
      const order = await this.orderService.createOrder(req.body);
      await this.notificationService.sendOrderConfirmation(order);
      res.status(201).json(order);
    } catch (error) {
      res.status(400).json({ error: error.message });
    }
  }
}

// Low Coupling
// Минимизация зависимостей между классами

// Высокая связность
class TightCoupledService {
  constructor() {
    this.database = new MySQLDatabase();
    this.cache = new RedisCache();
    this.logger = new FileLogger();
  }
}

// Низкая связность
class LooselyCoupledService {
  constructor(
    private database: Database,
    private cache: Cache,
    private logger: Logger
  ) {}
}

// High Cohesion
// Класс должен иметь четко определенную ответственность

class UserService {
  // Высокая cohesion: все методы связаны с пользователями
  async createUser(userData: UserData): Promise<User> {}
  async getUserById(id: string): Promise<User> {}
  async updateUser(id: string, userData: UserData): Promise<User> {}
  async deleteUser(id: string): Promise<void> {}
  async changePassword(id: string, newPassword: string): Promise<void> {}
}

// Низкая cohesion: смешивание ответственностей
class MixedService {
  async createUser(userData: UserData): Promise<User> {}
  async sendEmail(to: string, subject: string, body: string): Promise<void> {}
  async processPayment(amount: number, card: CardData): Promise<Payment> {}
}
```

## Монолитная архитектура

### Преимущества и недостатки
```typescript
// Монолитное приложение
class EcommerceApplication {
  private userService: UserService;
  private productService: ProductService;
  private orderService: OrderService;
  private paymentService: PaymentService;

  constructor() {
    this.userService = new UserService();
    this.productService = new ProductService();
    this.orderService = new OrderService();
    this.paymentService = new PaymentService();
  }

  // Все сервисы работают в одном процессе
  async processOrder(orderData: OrderData): Promise<Order> {
    // Валидация пользователя
    const user = await this.userService.getUserById(orderData.userId);
    if (!user) throw new Error('User not found');

    // Проверка продуктов
    for (const item of orderData.items) {
      const product = await this.productService.getProductById(item.productId);
      if (!product || product.stock < item.quantity) {
        throw new Error(`Product ${item.productId} not available`);
      }
    }

    // Создание заказа
    const order = await this.orderService.createOrder(orderData);

    // Обработка платежа
    const payment = await this.paymentService.processPayment(
      order.total,
      orderData.paymentMethod
    );

    return order;
  }
}

// Преимущества монолита:
// - Простота разработки и развертывания
// - Легче тестирование
// - Проще отладка
// - Лучше производительность (нет сетевых вызовов)

// Недостатки монолита:
// - Сложность масштабирования
// - Труднее поддерживать
// - Риск конфликтов зависимостей
// - Дольше время запуска
```

### Модуляризация монолита
```typescript
// Структурированный монолит
src/
├── modules/
│   ├── user/
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   ├── value-objects/
│   │   │   └── repositories/
│   │   ├── application/
│   │   │   ├── services/
│   │   │   ├── commands/
│   │   │   └── queries/
│   │   ├── infrastructure/
│   │   │   ├── controllers/
│   │   │   ├── repositories/
│   │   │   └── external-services/
│   │   └── presentation/
│   │       └── dtos/
│   ├── product/
│   ├── order/
│   └── payment/
├── shared/
│   ├── kernel/
│   ├── infrastructure/
│   └── interfaces/
└── main.ts

// Модульная структура позволяет:
// - Независимую разработку модулей
// - Четкое разделение ответственностей
// - Легче тестирование отдельных модулей
// - Подготовка к будущему разделению на микросервисы
```

## Многоуровневая архитектура

### **Presentation Layer**
```typescript
// Presentation Layer (MVC Pattern)
class UserController {
  constructor(private userService: UserService) {}

  @Get('/users')
  async getUsers(@Query() query: UserQuery): Promise<UserListResponse> {
    const users = await this.userService.getUsers(query);
    return this.mapToResponse(users);
  }

  @Post('/users')
  async createUser(@Body() userData: CreateUserRequest): Promise<UserResponse> {
    const user = await this.userService.createUser(userData);
    return this.mapToResponse(user);
  }

  private mapToResponse(user: User): UserResponse {
    return {
      id: user.id,
      email: user.email,
      name: user.name,
      createdAt: user.createdAt
    };
  }
}

// DTOs для изоляции
interface CreateUserRequest {
  email: string;
  name: string;
  password: string;
}

interface UserResponse {
  id: string;
  email: string;
  name: string;
  createdAt: Date;
}
```

### **Business Logic Layer**
```typescript
// Business Logic Layer (Domain Services)
class UserService {
  constructor(
    private userRepository: UserRepository,
    private emailService: EmailService,
    private passwordHasher: PasswordHasher
  ) {}

  async createUser(userData: CreateUserData): Promise<User> {
    // Валидация бизнес-правил
    await this.validateUserData(userData);

    // Проверка существования пользователя
    const existingUser = await this.userRepository.findByEmail(userData.email);
    if (existingUser) {
      throw new BusinessError('User already exists');
    }

    // Создание пользователя
    const hashedPassword = await this.passwordHasher.hash(userData.password);
    const user = User.create({
      ...userData,
      password: hashedPassword
    });

    await this.userRepository.save(user);

    // Отправка welcome email
    await this.emailService.sendWelcomeEmail(user.email, user.name);

    return user;
  }

  private async validateUserData(userData: CreateUserData): Promise<void> {
    if (userData.email.length < 5) {
      throw new ValidationError('Email too short');
    }
    // Другие бизнес-правила...
  }
}
```

### **Data Access Layer**
```typescript
// Data Access Layer (Repository Pattern)
interface UserRepository {
  save(user: User): Promise<void>;
  findById(id: string): Promise<User | null>;
  findByEmail(email: string): Promise<User | null>;
  findAll(query: UserQuery): Promise<User[]>;
  update(id: string, user: Partial<User>): Promise<User>;
  delete(id: string): Promise<void>;
}

// Implementation
class MongoUserRepository implements UserRepository {
  constructor(private collection: Collection<UserDocument>) {}

  async save(user: User): Promise<void> {
    const doc = this.mapToDocument(user);
    await this.collection.insertOne(doc);
  }

  async findById(id: string): Promise<User | null> {
    const doc = await this.collection.findOne({ _id: new ObjectId(id) });
    return doc ? this.mapToEntity(doc) : null;
  }

  private mapToDocument(user: User): UserDocument {
    return {
      _id: new ObjectId(user.id),
      email: user.email,
      name: user.name,
      password: user.password,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt
    };
  }

  private mapToEntity(doc: UserDocument): User {
    return User.from({
      id: doc._id.toString(),
      email: doc.email,
      name: doc.name,
      password: doc.password,
      createdAt: doc.createdAt,
      updatedAt: doc.updatedAt
    });
  }
}
```

## Клиент-серверная архитектура

### **Thin Client**
```typescript
// Thin Client Architecture
// Клиент только отображает данные и отправляет запросы

class UserClient {
  constructor(private apiClient: ApiClient) {}

  async loadUsers(): Promise<void> {
    try {
      const users = await this.apiClient.get('/users');
      this.renderUsers(users);
    } catch (error) {
      this.showError('Failed to load users');
    }
  }

  async createUser(userData: CreateUserData): Promise<void> {
    try {
      const user = await this.apiClient.post('/users', userData);
      this.addUserToList(user);
      this.showSuccess('User created successfully');
    } catch (error) {
      this.showError('Failed to create user');
    }
  }

  private renderUsers(users: User[]): void {
    // Render logic
  }

  private showError(message: string): void {
    // Error display logic
  }
}

// Сервер обрабатывает всю бизнес-логику
class UserServer {
  @Post('/users')
  async createUser(@Body() userData: CreateUserData): Promise<User> {
    // Валидация
    // Бизнес-логика
    // Сохранение в БД
    return await this.userService.createUser(userData);
  }
}
```

### **Thick Client** (**Rich Client**)
```typescript
// Thick Client Architecture
// Клиент содержит часть бизнес-логики

class RichUserClient {
  constructor(
    private apiClient: ApiClient,
    private userValidator: UserValidator,
    private cache: LocalCache
  ) {}

  async createUser(userData: CreateUserData): Promise<void> {
    // Клиентская валидация перед отправкой
    const validationErrors = this.userValidator.validate(userData);
    if (validationErrors.length > 0) {
      this.showValidationErrors(validationErrors);
      return;
    }

    try {
      // Проверка в кэше
      const cachedUser = this.cache.get(`user_${userData.email}`);
      if (cachedUser) {
        this.showError('User already exists');
        return;
      }

      const user = await this.apiClient.post('/users', userData);
      this.cache.set(`user_${user.email}`, user);
      this.addUserToList(user);
    } catch (error) {
      this.showError('Failed to create user');
    }
  }

  // Offline capabilities
  async saveUserOffline(userData: CreateUserData): Promise<void> {
    // Сохранение в local storage для синхронизации позже
    const offlineUsers = this.getOfflineUsers();
    offlineUsers.push(userData);
    localStorage.setItem('offline_users', JSON.stringify(offlineUsers));
  }

  async syncOfflineUsers(): Promise<void> {
    const offlineUsers = this.getOfflineUsers();
    for (const userData of offlineUsers) {
      try {
        await this.createUser(userData);
        this.removeFromOffline(userData);
      } catch (error) {
        console.error('Failed to sync user:', error);
      }
    }
  }
}
```

## Трехуровневая архитектура

### **Presentation Tier**
```typescript
// Presentation Tier
// Web Layer / API Layer

@RestController
@RequestMapping('/api/users')
class UserController {
  constructor(private userService: UserService) {}

  @GetMapping
  async getUsers(@RequestParam query: UserQuery): Promise<UserListResponse> {
    const users = await this.userService.getUsers(query);
    return {
      data: users.map(u => this.mapToDto(u)),
      pagination: query
    };
  }

  @PostMapping
  async createUser(@RequestBody request: CreateUserRequest): Promise<UserResponse> {
    const user = await this.userService.createUser(request);
    return this.mapToDto(user);
  }

  private mapToDto(user: User): UserResponse {
    return {
      id: user.id,
      email: user.email,
      name: user.name,
      createdAt: user.createdAt
    };
  }
}

// API Gateway Pattern
class ApiGateway {
  constructor(
    private userService: UserServiceClient,
    private productService: ProductServiceClient,
    private orderService: OrderServiceClient
  ) {}

  async getUserProfile(userId: string): Promise<UserProfile> {
    // Агрегация данных из нескольких сервисов
    const [user, orders, recommendations] = await Promise.all([
      this.userService.getUser(userId),
      this.orderService.getUserOrders(userId),
      this.productService.getRecommendations(userId)
    ]);

    return {
      user,
      recentOrders: orders.slice(0, 5),
      recommendations: recommendations.slice(0, 10)
    };
  }
}
```

### **Application Tier** (**Business Logic**)
```typescript
// Application Tier
// Business Logic Layer

@Service
class UserService {
  constructor(
    private userRepository: UserRepository,
    private emailService: EmailService,
    private eventPublisher: EventPublisher
  ) {}

  @Transactional
  async createUser(userData: CreateUserData): Promise<User> {
    // Domain validation
    this.validateUserData(userData);

    // Check business rules
    await this.checkUserDoesNotExist(userData.email);

    // Create user
    const user = User.create(userData);
    await this.userRepository.save(user);

    // Publish domain events
    await this.eventPublisher.publish(new UserCreatedEvent(user.id));

    // Send welcome email (fire and forget)
    this.emailService.sendWelcomeEmail(user.email, user.name)
      .catch(error => console.error('Failed to send welcome email:', error));

    return user;
  }

  async updateUser(id: string, updates: UpdateUserData): Promise<User> {
    const user = await this.userRepository.findById(id);
    if (!user) {
      throw new NotFoundError('User not found');
    }

    // Business rule: email changes require verification
    if (updates.email && updates.email !== user.email) {
      await this.initiateEmailChange(user, updates.email);
      delete updates.email; // Don't update yet
    }

    user.update(updates);
    await this.userRepository.save(user);

    return user;
  }
}
```

### **Data Tier**
```typescript
// Data Tier
// Data Access Layer

interface UserRepository {
  save(user: User): Promise<void>;
  findById(id: string): Promise<User | null>;
  findByEmail(email: string): Promise<User | null>;
  findAll(query: UserQuery): Promise<User[]>;
  update(id: string, updates: Partial<User>): Promise<User>;
  delete(id: string): Promise<void>;
}

// Implementation with ORM
@Repository
class TypeORMUserRepository implements UserRepository {
  constructor(@InjectRepository(UserEntity) private repository: Repository<UserEntity>) {}

  async save(user: User): Promise<void> {
    const entity = this.mapToEntity(user);
    await this.repository.save(entity);
  }

  async findById(id: string): Promise<User | null> {
    const entity = await this.repository.findOne({ where: { id } });
    return entity ? this.mapToDomain(entity) : null;
  }

  async findAll(query: UserQuery): Promise<User[]> {
    const qb = this.repository.createQueryBuilder('user');

    if (query.email) {
      qb.andWhere('user.email LIKE :email', { email: `%${query.email}%` });
    }

    if (query.createdAfter) {
      qb.andWhere('user.createdAt > :createdAfter', { createdAfter: query.createdAfter });
    }

    qb.orderBy('user.createdAt', 'DESC')
      .limit(query.limit)
      .offset(query.offset);

    const entities = await qb.getMany();
    return entities.map(e => this.mapToDomain(e));
  }

  private mapToEntity(user: User): UserEntity {
    const entity = new UserEntity();
    entity.id = user.id;
    entity.email = user.email;
    entity.name = user.name;
    entity.createdAt = user.createdAt;
    entity.updatedAt = user.updatedAt;
    return entity;
  }

  private mapToDomain(entity: UserEntity): User {
    return User.from({
      id: entity.id,
      email: entity.email,
      name: entity.name,
      createdAt: entity.createdAt,
      updatedAt: entity.updatedAt
    });
  }
}
```

## Гексагональная архитектура

### **Ports and Adapters**
```typescript
// Domain Layer (Core Business Logic)
export class User {
  constructor(
    public readonly id: string,
    public email: string,
    public name: string,
    public readonly createdAt: Date
  ) {}

  static create(data: CreateUserData): User {
    return new User(
      uuidv4(),
      data.email,
      data.name,
      new Date()
    );
  }

  update(data: UpdateUserData): void {
    if (data.name) this.name = data.name;
    if (data.email) this.email = data.email;
  }
}

// Ports (Interfaces)
export interface UserRepository {
  save(user: User): Promise<void>;
  findById(id: string): Promise<User | null>;
  findByEmail(email: string): Promise<User | null>;
}

export interface EmailService {
  sendWelcomeEmail(email: string, name: string): Promise<void>;
}

export interface IdGenerator {
  generate(): string;
}

// Application Services (Use Cases)
export class CreateUserUseCase {
  constructor(
    private userRepository: UserRepository,
    private emailService: EmailService,
    private idGenerator: IdGenerator
  ) {}

  async execute(userData: CreateUserData): Promise<User> {
    // Domain logic
    const user = User.create(userData);

    // Persistence
    await this.userRepository.save(user);

    // External communication
    await this.emailService.sendWelcomeEmail(user.email, user.name);

    return user;
  }
}
```

### **Adapters**
```typescript
// Infrastructure Layer (Adapters)

// Repository Adapter
export class MongoUserRepository implements UserRepository {
  constructor(private collection: Collection<UserDocument>) {}

  async save(user: User): Promise<void> {
    await this.collection.insertOne({
      _id: user.id,
      email: user.email,
      name: user.name,
      createdAt: user.createdAt
    });
  }

  async findById(id: string): Promise<User | null> {
    const doc = await this.collection.findOne({ _id: id });
    if (!doc) return null;

    return new User(
      doc._id,
      doc.email,
      doc.name,
      doc.createdAt
    );
  }

  async findByEmail(email: string): Promise<User | null> {
    const doc = await this.collection.findOne({ email });
    if (!doc) return null;

    return new User(
      doc._id,
      doc.email,
      doc.name,
      doc.createdAt
    );
  }
}

// Email Service Adapter
export class SendGridEmailService implements EmailService {
  constructor(private client: SendGridClient) {}

  async sendWelcomeEmail(email: string, name: string): Promise<void> {
    const msg = {
      to: email,
      from: 'welcome@myapp.com',
      subject: 'Welcome to MyApp!',
      html: `<h1>Welcome ${name}!</h1><p>Thank you for joining MyApp.</p>`
    };

    await this.client.send(msg);
  }
}

// ID Generator Adapter
export class UUIDGenerator implements IdGenerator {
  generate(): string {
    return uuidv4();
  }
}
```

### **Configuration** и **Dependency Injection**
```typescript
// Dependency Injection Container
export class Container {
  private services = new Map<string, any>();

  register<T>(token: string, factory: () => T): void {
    this.services.set(token, factory);
  }

  resolve<T>(token: string): T {
    const factory = this.services.get(token);
    if (!factory) {
      throw new Error(`Service ${token} not registered`);
    }
    return factory();
  }
}

// Application Configuration
export function configureContainer(): Container {
  const container = new Container();

  // Infrastructure services
  container.register('UserRepository', () => {
    const client = new MongoClient(process.env.MONGO_URI!);
    const db = client.db('myapp');
    const collection = db.collection('users');
    return new MongoUserRepository(collection);
  });

  container.register('EmailService', () => {
    const client = new SendGridClient(process.env.SENDGRID_API_KEY!);
    return new SendGridEmailService(client);
  });

  container.register('IdGenerator', () => new UUIDGenerator());

  // Application services
  container.register('CreateUserUseCase', () => {
    return new CreateUserUseCase(
      container.resolve('UserRepository'),
      container.resolve('EmailService'),
      container.resolve('IdGenerator')
    );
  });

  return container;
}

// HTTP Adapter (Presentation Layer)
export class UserController {
  constructor(private createUserUseCase: CreateUserUseCase) {}

  async createUser(req: Request, res: Response): Promise<void> {
    try {
      const userData: CreateUserData = req.body;
      const user = await this.createUserUseCase.execute(userData);
      res.status(201).json(user);
    } catch (error) {
      res.status(400).json({ error: error.message });
    }
  }
}

// Application Bootstrap
export async function bootstrap() {
  const container = configureContainer();

  const userController = new UserController(
    container.resolve('CreateUserUseCase')
  );

  // Setup HTTP routes
  const app = express();
  app.post('/users', userController.createUser.bind(userController));

  app.listen(3000, () => {
    console.log('Server running on port 3000');
  });
}
```

## Чистая архитектура

### **Entities** (**Domain Objects**)
```typescript
// Entities (Core Business Rules)
export class User {
  private constructor(
    private readonly _id: UserId,
    private _email: Email,
    private _name: PersonName,
    private readonly _createdAt: DateTime,
    private _updatedAt: DateTime
  ) {}

  static create(data: CreateUserData): User {
    const id = UserId.create();
    const email = Email.create(data.email);
    const name = PersonName.create(data.name);
    const now = DateTime.now();

    return new User(id, email, name, now, now);
  }

  get id(): UserId { return this._id; }
  get email(): Email { return this._email; }
  get name(): PersonName { return this._name; }
  get createdAt(): DateTime { return this._createdAt; }
  get updatedAt(): DateTime { return this._updatedAt; }

  update(data: UpdateUserData): void {
    if (data.name) {
      this._name = PersonName.create(data.name);
    }
    if (data.email) {
      this._email = Email.create(data.email);
    }
    this._updatedAt = DateTime.now();
  }

  canBeDeletedBy(actor: User): boolean {
    return actor.id.equals(this._id) || actor.isAdmin();
  }

  isAdmin(): boolean {
    // Domain logic for admin check
    return this._email.value.endsWith('@admin.company.com');
  }
}

// Value Objects
export class Email {
  private constructor(private readonly value: string) {}

  static create(email: string): Email {
    if (!this.isValid(email)) {
      throw new ValidationError('Invalid email format');
    }
    return new Email(email.toLowerCase());
  }

  static isValid(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  getValue(): string {
    return this.value;
  }

  equals(other: Email): boolean {
    return this.value === other.value;
  }
}

export class UserId {
  private constructor(private readonly value: string) {}

  static create(): UserId {
    return new UserId(uuidv4());
  }

  static fromString(id: string): UserId {
    if (!uuid.validate(id)) {
      throw new ValidationError('Invalid UUID format');
    }
    return new UserId(id);
  }

  getValue(): string {
    return this.value;
  }

  equals(other: UserId): boolean {
    return this.value === other.value;
  }

  toString(): string {
    return this.value;
  }
}
```

### **Use Cases** (**Application Layer**)
```typescript
// Use Cases (Application Business Rules)
export interface CreateUserUseCase {
  execute(data: CreateUserData): Promise<User>;
}

export interface GetUserUseCase {
  execute(id: string): Promise<User>;
}

export interface UpdateUserUseCase {
  execute(id: string, data: UpdateUserData, actor: User): Promise<User>;
}

export interface DeleteUserUseCase {
  execute(id: string, actor: User): Promise<void>;
}

// Implementation
export class CreateUserInteractor implements CreateUserUseCase {
  constructor(
    private userRepository: UserRepository,
    private idGenerator: IdGenerator,
    private domainEventPublisher: DomainEventPublisher
  ) {}

  async execute(data: CreateUserData): Promise<User> {
    // Application business rules
    const existingUser = await this.userRepository.findByEmail(data.email);
    if (existingUser) {
      throw new UserAlreadyExistsError(data.email);
    }

    // Domain logic
    const user = User.create(data);

    // Persistence
    await this.userRepository.save(user);

    // Publish domain events
    await this.domainEventPublisher.publish(new UserCreatedEvent(user.id));

    return user;
  }
}

export class UpdateUserInteractor implements UpdateUserUseCase {
  constructor(
    private userRepository: UserRepository,
    private domainEventPublisher: DomainEventPublisher
  ) {}

  async execute(id: string, data: UpdateUserData, actor: User): Promise<User> {
    const user = await this.userRepository.findById(id);
    if (!user) {
      throw new UserNotFoundError(id);
    }

    // Authorization check
    if (!user.canBeUpdatedBy(actor)) {
      throw new UnauthorizedError('Cannot update this user');
    }

    // Domain logic
    user.update(data);

    // Persistence
    await this.userRepository.save(user);

    // Publish domain events
    await this.domainEventPublisher.publish(new UserUpdatedEvent(user.id));

    return user;
  }
}

// Domain Events
export class UserCreatedEvent {
  constructor(public readonly userId: string) {}
}

export class UserUpdatedEvent {
  constructor(public readonly userId: string) {}
}
```

### **Interface Adapters**
```typescript
// Interface Adapters (Controllers, Presenters)
export class UserController {
  constructor(
    private createUserUseCase: CreateUserUseCase,
    private getUserUseCase: GetUserUseCase,
    private updateUserUseCase: UpdateUserUseCase,
    private deleteUserUseCase: DeleteUserUseCase,
    private userPresenter: UserPresenter
  ) {}

  async createUser(req: Request, res: Response): Promise<void> {
    try {
      const data = this.parseCreateUserRequest(req.body);
      const user = await this.createUserUseCase.execute(data);
      const response = this.userPresenter.presentUser(user);
      res.status(201).json(response);
    } catch (error) {
      this.handleError(error, res);
    }
  }

  async getUser(req: Request, res: Response): Promise<void> {
    try {
      const user = await this.getUserUseCase.execute(req.params.id);
      const response = this.userPresenter.presentUser(user);
      res.json(response);
    } catch (error) {
      this.handleError(error, res);
    }
  }

  async updateUser(req: Request, res: Response): Promise<void> {
    try {
      const actor = await this.getAuthenticatedUser(req);
      const data = this.parseUpdateUserRequest(req.body);
      const user = await this.updateUserUseCase.execute(req.params.id, data, actor);
      const response = this.userPresenter.presentUser(user);
      res.json(response);
    } catch (error) {
      this.handleError(error, res);
    }
  }

  private parseCreateUserRequest(body: any): CreateUserData {
    return {
      email: body.email,
      name: body.name,
      password: body.password
    };
  }

  private parseUpdateUserRequest(body: any): UpdateUserData {
    return {
      name: body.name,
      email: body.email
    };
  }

  private async getAuthenticatedUser(req: Request): Promise<User> {
    // Extract user from JWT token or session
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) {
      throw new UnauthorizedError('No token provided');
    }
    return await this.jwtService.verifyToken(token);
  }

  private handleError(error: Error, res: Response): void {
    if (error instanceof ValidationError) {
      res.status(400).json({ error: error.message });
    } else if (error instanceof NotFoundError) {
      res.status(404).json({ error: error.message });
    } else if (error instanceof UnauthorizedError) {
      res.status(401).json({ error: error.message });
    } else {
      console.error('Unexpected error:', error);
      res.status(500).json({ error: 'Internal server error' });
    }
  }
}

// Presenter
export class UserPresenter {
  presentUser(user: User): UserResponse {
    return {
      id: user.id.getValue(),
      email: user.email.getValue(),
      name: user.name.getValue(),
      createdAt: user.createdAt.toISOString(),
      updatedAt: user.updatedAt.toISOString()
    };
  }

  presentUsers(users: User[]): UserListResponse {
    return {
      users: users.map(user => this.presentUser(user))
    };
  }
}

// Request/Response DTOs
export interface CreateUserData {
  email: string;
  name: string;
  password: string;
}

export interface UpdateUserData {
  name?: string;
  email?: string;
}

export interface UserResponse {
  id: string;
  email: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface UserListResponse {
  users: UserResponse[];
}
```

### **Frameworks** & **Drivers**
```typescript
// Frameworks & Drivers (External Interfaces)

// Repository Implementation
export class PostgresUserRepository implements UserRepository {
  constructor(private connection: DatabaseConnection) {}

  async save(user: User): Promise<void> {
    const query = `
      INSERT INTO users (id, email, name, created_at, updated_at)
      VALUES ($1, $2, $3, $4, $5)
      ON CONFLICT (id) DO UPDATE SET
        email = EXCLUDED.email,
        name = EXCLUDED.name,
        updated_at = EXCLUDED.updated_at
    `;

    await this.connection.execute(query, [
      user.id.getValue(),
      user.email.getValue(),
      user.name.getValue(),
      user.createdAt,
      user.updatedAt
    ]);
  }

  async findById(id: string): Promise<User | null> {
    const query = 'SELECT * FROM users WHERE id = $1';
    const result = await this.connection.query(query, [id]);

    if (result.rows.length === 0) {
      return null;
    }

    const row = result.rows[0];
    return User.from({
      id: row.id,
      email: row.email,
      name: row.name,
      createdAt: row.created_at,
      updatedAt: row.updated_at
    });
  }

  async findByEmail(email: string): Promise<User | null> {
    const query = 'SELECT * FROM users WHERE email = $1';
    const result = await this.connection.query(query, [email]);

    if (result.rows.length === 0) {
      return null;
    }

    const row = result.rows[0];
    return User.from({
      id: row.id,
      email: row.email,
      name: row.name,
      createdAt: row.created_at,
      updatedAt: row.updated_at
    });
  }
}

// Email Service Implementation
export class SESNotificationService implements NotificationService {
  constructor(private sesClient: SESClient) {}

  async sendWelcomeEmail(email: string, name: string): Promise<void> {
    const params = {
      Source: 'welcome@myapp.com',
      Destination: {
        ToAddresses: [email]
      },
      Message: {
        Subject: {
          Data: 'Welcome to MyApp!'
        },
        Body: {
          Html: {
            Data: `<h1>Welcome ${name}!</h1><p>Thank you for joining MyApp.</p>`
          }
        }
      }
    };

    await this.sesClient.sendEmail(params);
  }
}

// Cache Implementation
export class RedisCache implements Cache {
  constructor(private redisClient: RedisClient) {}

  async get(key: string): Promise<string | null> {
    return await this.redisClient.get(key);
  }

  async set(key: string, value: string, ttlSeconds?: number): Promise<void> {
    if (ttlSeconds) {
      await this.redisClient.setex(key, ttlSeconds, value);
    } else {
      await this.redisClient.set(key, value);
    }
  }

  async delete(key: string): Promise<void> {
    await this.redisClient.del(key);
  }
}
```

## Паттерны интеграции

### **API Gateway Pattern**
```typescript
// API Gateway Implementation
class ApiGateway {
  constructor(
    private authService: AuthService,
    private userService: UserService,
    private productService: ProductService,
    private cache: Cache,
    private rateLimiter: RateLimiter
  ) {}

  async handleRequest(req: Request, res: Response): Promise<void> {
    // Rate limiting
    if (!await this.rateLimiter.checkLimit(req.ip)) {
      return res.status(429).json({ error: 'Rate limit exceeded' });
    }

    // Authentication
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) {
      return res.status(401).json({ error: 'No token provided' });
    }

    const user = await this.authService.verifyToken(token);
    if (!user) {
      return res.status(401).json({ error: 'Invalid token' });
    }

    // Route to appropriate service
    const path = req.path;
    const method = req.method;

    if (path.startsWith('/users')) {
      await this.handleUserRequest(method, path, req, res, user);
    } else if (path.startsWith('/products')) {
      await this.handleProductRequest(method, path, req, res, user);
    } else {
      res.status(404).json({ error: 'Not found' });
    }
  }

  private async handleUserRequest(
    method: string,
    path: string,
    req: Request,
    res: Response,
    user: User
  ): Promise<void> {
    // Check cache for GET requests
    if (method === 'GET') {
      const cacheKey = `user:${path}`;
      const cached = await this.cache.get(cacheKey);
      if (cached) {
        return res.json(JSON.parse(cached));
      }
    }

    const result = await this.userService.handleRequest(method, path, req.body, user);

    // Cache successful GET responses
    if (method === 'GET' && result.status === 200) {
      await this.cache.set(`user:${path}`, JSON.stringify(result.data), 300);
    }

    res.status(result.status).json(result.data);
  }
}
```

### **Circuit Breaker Pattern**
```typescript
// Circuit Breaker Implementation
enum CircuitState {
  CLOSED = 'CLOSED',
  OPEN = 'OPEN',
  HALF_OPEN = 'HALF_OPEN'
}

class CircuitBreaker {
  private state: CircuitState = CircuitState.CLOSED;
  private failureCount = 0;
  private lastFailureTime = 0;
  private nextAttemptTime = 0;

  constructor(
    private failureThreshold: number = 5,
    private recoveryTimeout: number = 60000,
    private monitoringPeriod: number = 10000
  ) {}

  async execute<T>(operation: () => Promise<T>): Promise<T> {
    if (this.state === CircuitState.OPEN) {
      if (Date.now() < this.nextAttemptTime) {
        throw new CircuitBreakerError('Circuit breaker is OPEN');
      }
      this.state = CircuitState.HALF_OPEN;
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

  private onSuccess(): void {
    this.failureCount = 0;
    this.state = CircuitState.CLOSED;
  }

  private onFailure(): void {
    this.failureCount++;
    this.lastFailureTime = Date.now();

    if (this.failureCount >= this.failureThreshold) {
      this.state = CircuitState.OPEN;
      this.nextAttemptTime = Date.now() + this.recoveryTimeout;
    }
  }

  getState(): CircuitState {
    return this.state;
  }
}

// Usage with external service
class ExternalServiceClient {
  private circuitBreaker = new CircuitBreaker(3, 30000);

  async callExternalAPI(data: any): Promise<any> {
    return this.circuitBreaker.execute(async () => {
      const response = await fetch('https://api.external-service.com/endpoint', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });

      if (!response.ok) {
        throw new Error(`External API error: ${response.status}`);
      }

      return response.json();
    });
  }
}
```

### **Saga Pattern**
```typescript
// Saga Pattern for distributed transactions
interface SagaStep {
  execute(context: SagaContext): Promise<void>;
  compensate(context: SagaContext): Promise<void>;
}

class SagaContext {
  constructor(public data: Map<string, any> = new Map()) {}

  set(key: string, value: any): void {
    this.data.set(key, value);
  }

  get(key: string): any {
    return this.data.get(key);
  }
}

class Saga {
  constructor(private steps: SagaStep[]) {}

  async execute(initialData: Map<string, any> = new Map()): Promise<void> {
    const context = new SagaContext(initialData);
    const executedSteps: SagaStep[] = [];

    try {
      for (const step of this.steps) {
        await step.execute(context);
        executedSteps.push(step);
      }
    } catch (error) {
      // Compensate in reverse order
      for (let i = executedSteps.length - 1; i >= 0; i--) {
        try {
          await executedSteps[i].compensate(context);
        } catch (compensationError) {
          console.error('Compensation failed:', compensationError);
          // Log compensation failure but continue
        }
      }
      throw error;
    }
  }
}

// Order Processing Saga
class CreateOrderStep implements SagaStep {
  constructor(private orderService: OrderService) {}

  async execute(context: SagaContext): Promise<void> {
    const orderData = context.get('orderData');
    const order = await this.orderService.createOrder(orderData);
    context.set('order', order);
  }

  async compensate(context: SagaContext): Promise<void> {
    const order = context.get('order');
    if (order) {
      await this.orderService.cancelOrder(order.id);
    }
  }
}

class ReserveInventoryStep implements SagaStep {
  constructor(private inventoryService: InventoryService) {}

  async execute(context: SagaContext): Promise<void> {
    const order = context.get('order');
    await this.inventoryService.reserveItems(order.items);
    context.set('inventoryReserved', true);
  }

  async compensate(context: SagaContext): Promise<void> {
    const order = context.get('order');
    if (context.get('inventoryReserved')) {
      await this.inventoryService.releaseItems(order.items);
    }
  }
}

class ProcessPaymentStep implements SagaStep {
  constructor(private paymentService: PaymentService) {}

  async execute(context: SagaContext): Promise<void> {
    const order = context.get('order');
    const payment = await this.paymentService.processPayment(order.total, order.paymentMethod);
    context.set('payment', payment);
  }

  async compensate(context: SagaContext): Promise<void> {
    const payment = context.get('payment');
    if (payment) {
      await this.paymentService.refundPayment(payment.id);
    }
  }
}

// Usage
const orderSaga = new Saga([
  new CreateOrderStep(orderService),
  new ReserveInventoryStep(inventoryService),
  new ProcessPaymentStep(paymentService)
]);

await orderSaga.execute(new Map([['orderData', orderData]]));
```

## Распределенные паттерны

### **Bulkhead Pattern**
```typescript
// Bulkhead Pattern для изоляции ресурсов
class Bulkhead {
  private activeRequests = 0;
  private waitingQueue: Array<() => void> = [];

  constructor(
    private maxConcurrent: number,
    private maxQueueSize: number = 100
  ) {}

  async execute<T>(operation: () => Promise<T>): Promise<T> {
    return new Promise((resolve, reject) => {
      if (this.activeRequests >= this.maxConcurrent) {
        if (this.waitingQueue.length >= this.maxQueueSize) {
          reject(new Error('Bulkhead queue full'));
          return;
        }

        this.waitingQueue.push(() => {
          this.doExecute(operation, resolve, reject);
        });
        return;
      }

      this.doExecute(operation, resolve, reject);
    });
  }

  private async doExecute<T>(
    operation: () => Promise<T>,
    resolve: (value: T) => void,
    reject: (error: any) => void
  ): Promise<void> {
    this.activeRequests++;

    try {
      const result = await operation();
      resolve(result);
    } catch (error) {
      reject(error);
    } finally {
      this.activeRequests--;

      // Process next in queue
      if (this.waitingQueue.length > 0) {
        const next = this.waitingQueue.shift();
        next!();
      }
    }
  }
}

// Usage for database connections
class DatabaseService {
  private bulkhead = new Bulkhead(10, 50); // Max 10 concurrent, queue 50

  async executeQuery(query: string, params: any[]): Promise<any> {
    return this.bulkhead.execute(async () => {
      const connection = await this.getConnection();
      try {
        return await connection.query(query, params);
      } finally {
        connection.release();
      }
    });
  }
}
```

### **Ambassador Pattern**
```typescript
// Ambassador Pattern для service mesh integration
class ServiceAmbassador {
  constructor(
    private circuitBreaker: CircuitBreaker,
    private retryPolicy: RetryPolicy,
    private metricsCollector: MetricsCollector
  ) {}

  async callService(serviceName: string, method: string, data: any): Promise<any> {
    const startTime = Date.now();

    try {
      const result = await this.circuitBreaker.execute(async () => {
        return await this.retryPolicy.execute(async () => {
          return await this.makeRequest(serviceName, method, data);
        });
      });

      this.metricsCollector.recordSuccess(serviceName, Date.now() - startTime);
      return result;

    } catch (error) {
      this.metricsCollector.recordError(serviceName, error, Date.now() - startTime);
      throw error;
    }
  }

  private async makeRequest(serviceName: string, method: string, data: any): Promise<any> {
    const serviceUrl = await this.serviceDiscovery.getServiceUrl(serviceName);

    const response = await fetch(`${serviceUrl}/${method}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Request-ID': this.generateRequestId()
      },
      body: JSON.stringify(data),
      timeout: 5000
    });

    if (!response.ok) {
      throw new ServiceError(`Service ${serviceName} returned ${response.status}`);
    }

    return response.json();
  }
}

// Sidecar pattern implementation
class ServiceSidecar extends ServiceAmbassador {
  constructor(
    private localService: any,
    circuitBreaker: CircuitBreaker,
    retryPolicy: RetryPolicy,
    metricsCollector: MetricsCollector
  ) {
    super(circuitBreaker, retryPolicy, metricsCollector);
  }

  async callService(serviceName: string, method: string, data: any): Promise<any> {
    // Try local service first (service mesh routing)
    try {
      return await this.localService[method](data);
    } catch (error) {
      // Fallback to remote call
      return super.callService(serviceName, method, data);
    }
  }
}
```

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Слои жёстко связаны, сложно менять реализацию | Нарушение инверсии зависимостей (зависимость от конкретики) | Ввести интерфейсы (порты), зависимости направлять к домену; см. гексагональная и чистая архитектура |
| Монолит сложно масштабировать по частям | Всё в одном процессе | Модуляризовать по доменам; при необходимости выделять сервисы (микросервисы), сохраняя чёткие границы |
| Распределённые сбои каскадом | Нет изоляции отказов | Ввести Circuit Breaker, ограничение параллелизма (Bulkhead); таймауты и fallback для внешних вызовов |

## Частые вопросы

**Монолит или микросервисы с самого начала?** Начинать с модульного монолита с чёткими границами; переходить к микросервисам при реальной потребности в независимом масштабировании, развёртывании или командах. Преждевременное разбиение усложняет разработку и эксплуатацию.

**Чем гексагональная архитектура отличается от чистой?** Обе ставят домен в центр и зависят от абстракций. Гексагональная акцент на портах и адаптерах; чистая — на слоях (entities, use cases, interface adapters, frameworks). Идеи совместимы.

**Когда нужен API Gateway?** При множестве клиентов и сервисов: единая точка входа, маршрутизация, аутентификация, ограничение частоты запросов. В простом одном сервисе достаточно одного приложения.
## См. также
- [[microservices|microservices.md]] — микросервисная архитектура
- [[event-driven|event-driven.md]] — **event-driven** паттерны
- [[cqrs|cqrs.md]] — **Command Query Responsibility Segregation**
- [[event-sourcing|event-sourcing.md]] — **Event Sourcing**
- [[ddd|ddd.md]] — **Domain-Driven Design**
