---
title: "MongoDB: CRUD операции — Создание, чтение, обновление и удаление документов"
description: "Полное руководство по CRUD операциям в MongoDB: insert, find, update, delete с примерами и best practices."
tags:
  - databases
  - nosql
  - mongodb-crud
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# MongoDB: CRUD операции — Создание, чтение, обновление и удаление документов

Полное руководство по **CRUD** операциям в **MongoDB**: **insert**, **find**, **update**, **delete** с примерами и **best practices**.

## Полезные ссылки

### Официальная документация
- [MongoDB CRUD Operations](https://www.mongodb.com/docs/manual/crud/)
- [MongoDB Insert Documents](https://www.mongodb.com/docs/manual/tutorial/insert-documents/)
- [MongoDB Query Documents](https://www.mongodb.com/docs/manual/tutorial/query-documents/)

### Обучающие материалы
- [MongoDB CRUD with Spring Boot](https://www.baeldung.com/spring-data-mongodb-tutorial) — **Spring Data MongoDB**

### См. также
- [[mongodb-basics|Основы]] — **MongoDB**
- [[mongodb-queries|Запросы]] — продвинутые запросы

- [[clickhouse|ClickHouse]]
- [[mongodb-aggregation|MongoDB: Aggregation Framework — Полное руководство по агрегации данных]]
## Содержание

- [Обзор CRUD операций](#обзор-crud-операций)
  - [Особенности CRUD в MongoDB](#особенности-crud-в-mongodb)
    - [Гибкость операций](#гибкость-операций)
    - [Производительность](#производительность)
    - [Распределенные операции](#распределенные-операции)
  - [Write Concern и Read Concern](#write-concern-и-read-concern)
    - [Write Concern уровни:](#write-concern-уровни)
    - [Read Concern уровни:](#read-concern-уровни)
  - [Операции и их характеристики](#операции-и-их-характеристики)
  - [Производительность операций](#производительность-операций)
    - [Факторы влияющие на скорость:](#факторы-влияющие-на-скорость)
    - [Оптимизация производительности:](#оптимизация-производительности)
- [Create (Создание документов)](#create-создание-документов)
  - [insertOne() — Вставка одного документа](#insertone-вставка-одного-документа)
  - [insertMany() — Вставка нескольких документов](#insertmany-вставка-нескольких-документов)
  - [Особенности вставки](#особенности-вставки)
- [Read (Чтение документов)](#read-чтение-документов)
  - [find() — Поиск документов](#find-поиск-документов)
  - [findOne() — Поиск одного документа](#findone-поиск-одного-документа)
- [Update (Обновление документов)](#update-обновление-документов)
  - [updateOne() — Обновление одного документа](#updateone-обновление-одного-документа)
  - [updateMany() — Обновление нескольких документов](#updatemany-обновление-нескольких-документов)
  - [replaceOne() — Замена документа](#replaceone-замена-документа)
- [Операторы обновления](#операторы-обновления)
  - [Операторы для полей](#операторы-для-полей)
  - [Операторы для массивов](#операторы-для-массивов)
  - [Арифметические операторы](#арифметические-операторы)
- [Delete (Удаление документов)](#delete-удаление-документов)
  - [deleteOne() — Удаление одного документа](#deleteone-удаление-одного-документа)
  - [deleteMany() — Удаление нескольких документов](#deletemany-удаление-нескольких-документов)
  - [Bulk Write операции](#bulk-write-операции)
- [Лучшие практики для CRUD операций](#лучшие-практики-для-crud-операций)
  - [Производительность](#производительность-1)
  - [Надежность](#надежность)
  - [Безопасность](#безопасность)
- [Примеры комплексных операций](#примеры-комплексных-операций)
  - [Обновление с условием и расчетом](#обновление-с-условием-и-расчетом)
  - [Условное обновление](#условное-обновление)
  - [Обновление с использованием aggregation pipeline](#обновление-с-использованием-aggregation-pipeline)
  - [Основные принципы:](#основные-принципы)
  - [Лучшие практики:](#лучшие-практики)
  - [Производительность:](#производительность-2)
- [Работа с большими коллекциями](#работа-с-большими-коллекциями)
  - [Пагинация](#пагинация)
  - [Обработка ошибок](#обработка-ошибок)
- [Заключение](#заключение)
  - [Ключевые принципы:](#ключевые-принципы)
  - [Основные методы:](#основные-методы)
- [Решение проблем](#решение-проблем)

## Обзор CRUD операций

**CRUD** (Create, `Read`, `Update`, Delete) — это четыре основные операции для работы с данными в любой базе данных. **MongoDB** предоставляет богатый набор методов для выполнения этих операций с документами.

### Особенности CRUD в MongoDB

#### Гибкость операций
- **Upsert operations**: Создание или обновление в одной операции
- **Bulk operations**: Массовые операции для высокой производительности
- **Atomic operations**: Операции на уровне документа (ACID)
- **Conditional operations**: Операции с условиями

#### Производительность
- **Write Concern**: Контроль уровня консистентности записи
- **Read Concern**: Контроль уровня изоляции чтения
- **Index utilization**: Использование индексов для быстрого доступа
- **In-memory operations**: Операции в памяти для высокой скорости

#### Распределенные операции
- **Sharding awareness**: Операции учитывают шардирование
- **Replica set handling**: Работа с репликами для отказоустойчивости
- **Transaction support**: Многдокументные транзакции

### Write Concern и Read Concern

#### Write Concern уровни:
- **w: 0** — **Fire and forget** (не ждет подтверждения)
- **w: 1** — Ждет подтверждения от **primary**
- **w: "majority"** — Ждет подтверждения от большинства реплик
- **j: true** — Ждет записи в **journal**

#### Read Concern уровни:
- **local** — Читает с **primary** (по умолчанию)
- **available** — Читает доступные данные (максимальная производительность)
- **majority** — Читает **majority-committed** данные
- **linearizable** — Линеаризуемое чтение (строгая консистентность)

### Операции и их характеристики

| Операция | **Atomic** | **Isolated** | **Consistent** | **Durable** |
|----------|--------|----------|------------|---------|
| **insertOne** | | | | |
| **updateOne** | | | | |
| **deleteOne** | | | | |
| **findOneAndUpdate** | | | | |
| **findOneAndDelete** | | | | |
| **Bulk operations** | | | | |
| **Multi-document** tx | | | | |

### Производительность операций

#### Факторы влияющие на скорость:
1. **Index usage**: Правильные индексы ускоряют операции
2. **Document size**: Большие документы медленнее обрабатываются
3. **Write concern**: Более строгие настройки замедляют операции
4. **Locking**: Операции могут блокироваться на уровне документа
5. **Network latency**: Влияет на **distributed** операции

#### Оптимизация производительности:
- **Bulk operations**: Группировка операций для снижения **overhead**
- **Proper indexing**: Индексы на часто используемые поля
- **Connection pooling**: Переиспользование соединений
- **Async operations**: Неблокирующие операции в драйверах

Схема операций **CRUD** в **MongoDB** (insert find update remove).

```mermaid
flowchart LR
    A["CREATE<br/>insert"] --> B["READ<br/>find"]
    B --> C["UPDATE<br/>update"]
    C --> D["DELETE<br/>remove"]
```

## Create (Создание документов)

### insertOne() — Вставка одного документа

Пример сущности и вставки одного документа через **Spring Data MongoDB** (Java).

```java
// Entity класс
@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private Double price;
    private Integer pages;
    private Integer publishedYear;
    private List<String> tags;
    private Publisher publisher;

    // Constructors, getters, setters
}

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
}

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book createBookExample() {
        Book book = new Book();
        book.setTitle("MongoDB Guide");
        book.setAuthor("John Doe");
        book.setPrice(29.99);
        book.setPages(350);
        book.setPublishedYear(2024);
        book.setTags(Arrays.asList("database", "nosql", "mongodb"));

        Publisher publisher = new Publisher();
        publisher.setName("Tech Books Inc");
        publisher.setLocation("New York");
        book.setPublisher(publisher);

        return bookRepository.save(book);
    }
}
```

### insertMany() — Вставка нескольких документов

```java
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> createMultipleBooks() {
        List<Book> books = Arrays.asList(
            createBook("MongoDB Advanced", "Jane Smith", 39.99, 450),
            createBook("NoSQL Databases", "Bob Johnson", 34.99, 380)
        );

        return bookRepository.saveAll(books);
    }

    private Book createBook(String title, String author, Double price, Integer pages) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setPages(pages);
        book.setPublishedYear(2024);
        return book;
    }

    // Bulk insert с WriteConcern
    public List<Book> bulkInsertWithOptions(List<Book> books) {
        // Использование MongoTemplate для дополнительных опций
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, Book.class);

        for (Book book : books) {
            bulkOps.insert(book);
        }

        // Выполнение с WriteConcern
        BulkWriteResult result = bulkOps.execute(
            new BulkWriteOptions()
                .writeConcern(WriteConcern.MAJORITY)
                .ordered(false)  // Неупорядочная вставка для производительности
        );

        return books;
    }
}
```

### Особенности вставки

1. **Автоматическое создание _id**: Если не указан `_id`, **MongoDB** создаст **ObjectId** автоматически
2. **Валидация**: Документы валидируются по схеме (если настроена)
3. **Write Concern**: Уровень подтверждения записи
4. **Ordered/Unordered**: Порядок вставки при ошибках

## Read (Чтение документов)

### find() — Поиск документов

```java
@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    // Найти все книги
    List<Book> findAll();

    // Найти по автору
    List<Book> findByAuthor(String author);

    // Найти по цене меньше указанной
    List<Book> findByPriceLessThan(Double price);

    // Найти по диапазону цен с сортировкой
    @Query("{ 'price' : { $gte: ?0, $lte: ?1 } }")
    List<Book> findByPriceRange(Double minPrice, Double maxPrice);

    // С пагинацией и сортировкой
    Page<Book> findAll(Pageable pageable);

    // Кастомные методы с проекциями
    @Query(value = "{ 'price' : { $lt: ?0 } }",
           fields = "{ 'title' : 1, 'price' : 1, '_id' : 0 }")
    List<BookProjection> findCheapBooks(Double maxPrice);
}

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Найти все книги
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    // Найти книги по автору
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    // Найти книги по цене с проекцией
    public List<BookProjection> findCheapBooks(Double maxPrice) {
        return bookRepository.findCheapBooks(maxPrice);
    }

    // Пагинированный запрос с сортировкой
    public Page<Book> findBooksWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
        return bookRepository.findAll(pageable);
    }

    // Сложный запрос с MongoTemplate
    public List<Book> findBooksComplexQuery() {
        Query query = new Query();
        query.addCriteria(Criteria.where("price").gte(20).lte(40));
        query.with(Sort.by(Sort.Direction.ASC, "price"));
        query.limit(10);

        return mongoTemplate.find(query, Book.class);
    }

    // Использование aggregation для аналитики
    public List<Document> getBooksAnalytics() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("publishedYear").gte(2020)),
            Aggregation.group("author")
                .count().as("bookCount")
                .avg("price").as("avgPrice")
                .max("price").as("maxPrice"),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "bookCount"))
        );

        return mongoTemplate.aggregate(aggregation, "books", Document.class)
                          .getMappedResults();
    }
}
```

### findOne() — Поиск одного документа

```java
@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    // Найти одну книгу по названию
    Optional<Book> findByTitle(String title);

    // Найти по автору с проекцией
    @Query(value = "{ 'author' : ?0 }", fields = "{ 'title' : 1, 'price' : 1, '_id' : 0 }")
    Optional<BookProjection> findBookProjectionByAuthor(String author);
}

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Найти одну книгу
    public Optional<Book> findBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    // Найти с проекцией
    public Optional<BookProjection> findBookProjectionByAuthor(String author) {
        return bookRepository.findBookProjectionByAuthor(author);
    }

    // Работа с курсорами (streaming)
    public void processBooksWithCursor() {
        Query query = new Query(Criteria.where("price").gt(25));

        try (Cursor<Book> cursor = mongoTemplate.stream(query, Book.class)) {
            cursor.forEachRemaining(book -> {
                // Обработка каждой книги
                System.out.println("Processing book: " + book.getTitle());
            });
        }
    }

    // Получить как список (не рекомендуется для больших наборов)
    public List<Book> findBooksAsList() {
        Query query = Query.query(Criteria.where("price").gt(25));
        return mongoTemplate.find(query, Book.class);
    }

    // Подсчет документов
    public long countBooksAbovePrice(Double price) {
        Query query = Query.query(Criteria.where("price").gt(price));
        return mongoTemplate.count(query, Book.class);
    }
}
```

## Update (Обновление документов)

### updateOne() — Обновление одного документа

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Обновление цены книги
    public UpdateResult updateBookPrice(String title, Double newPrice) {
        Query query = new Query(Criteria.where("title").is(title));
        Update update = new Update()
            .set("price", newPrice)
            .currentDate("lastModified");

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // Обновление по ID с результатом
    public UpdateResult updateBookById(String id, Double newPrice) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
            .set("price", newPrice)
            .currentDate("lastModified");

        return mongoTemplate.updateFirst(query, update, Book.class);
        // Результат: UpdateResult с matchedCount, modifiedCount и т.д.
    }
}
```

### updateMany() — Обновление нескольких документов

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Увеличить цену всех книг на 10%
    public UpdateResult increaseAllBookPrices() {
        Query query = new Query(); // Все документы
        Update update = new Update().mul("price", 1.1); // Увеличить на 10%

        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // Добавить тег ко всем книгам определенного автора
    public UpdateResult addTagToAuthorBooks(String author, String tag) {
        Query query = Query.query(Criteria.where("author").is(author));
        Update update = new Update().addToSet("tags", tag);

        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // Сложное обновление с несколькими операторами
    public UpdateResult complexUpdate() {
        Query query = Query.query(Criteria.where("publishedYear").gte(2020));
        Update update = new Update()
            .inc("price", 5.0)        // Увеличить цену на 5
            .set("updatedAt", new Date())  // Установить дату обновления
            .unset("oldField");       // Удалить старое поле

        return mongoTemplate.updateMulti(query, update, Book.class);
    }
}
```

### replaceOne() — Замена документа

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Полная замена документа
    public void replaceBook(String oldTitle, Book newBookData) {
        Query query = Query.query(Criteria.where("title").is(oldTitle));

        // Найти существующий документ для сохранения ID
        Book existingBook = mongoTemplate.findOne(query, Book.class);
        if (existingBook != null) {
            newBookData.setId(existingBook.getId());
            mongoTemplate.save(newBookData);
        }
    }

    // Более контролируемая замена с проверками
    public boolean replaceBookSafe(String oldTitle, Book replacement) {
        Query query = Query.query(Criteria.where("title").is(oldTitle));
        Book existing = mongoTemplate.findOne(query, Book.class);

        if (existing == null) {
            return false; // Документ не найден
        }

        // Сохранить ID и выполнить замену
        replacement.setId(existing.getId());
        mongoTemplate.save(replacement);
        return true;
    }

    // Замена с дополнительными условиями
    public UpdateResult replaceWithConditions(Book searchCriteria, Book replacement) {
        Query query = new Query();

        // Построить критерии поиска
        if (searchCriteria.getTitle() != null) {
            query.addCriteria(Criteria.where("title").is(searchCriteria.getTitle()));
        }
        if (searchCriteria.getAuthor() != null) {
            query.addCriteria(Criteria.where("author").is(searchCriteria.getAuthor()));
        }

        // Использовать replaceFirst для полной замены
        return mongoTemplate.updateFirst(
            query,
            Update.fromDocument(
                mongoTemplate.getConverter().convertToMongo(replacement)
            ),
            Book.class
        );
    }
}
```

## Операторы обновления

### Операторы для полей

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $set - установить значение поля
    public UpdateResult setBookCategory(String title, String category) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().set("category", category);

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $unset - удалить поле
    public UpdateResult removeDeprecatedField(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().unset("deprecated");

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $rename - переименовать поле
    public UpdateResult renameField(String oldField, String newField) {
        Query query = new Query(); // Все документы
        Update update = new Update().rename(oldField, newField);

        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // Множественные операции с полями
    public UpdateResult updateBookFields(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update()
            .set("category", "database")
            .set("updatedAt", new Date())
            .unset("oldField")
            .rename("oldName", "newName");

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $currentDate - установить текущую дату
    public UpdateResult setCurrentDate(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().currentDate("updatedAt");

        return mongoTemplate.updateFirst(query, update, Book.class);
    }
}
```

### Операторы для массивов

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $push - добавить элемент в массив
    public UpdateResult addTagToBook(String title, String tag) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().push("tags", tag);

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $addToSet - добавить уникальный элемент
    public UpdateResult addUniqueTag(String title, String tag) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().addToSet("tags", tag);

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $pop - удалить первый/последний элемент
    public UpdateResult removeFirstTag(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().pop("tags", Update.Position.FIRST); // -1

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $pull - удалить все совпадения
    public UpdateResult removeTag(String title, String tagToRemove) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().pull("tags", tagToRemove);

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $pullAll - удалить несколько значений
    public UpdateResult removeMultipleTags(String title, List<String> tagsToRemove) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().pullAll("tags", tagsToRemove.toArray());

        return mongoTemplate.updateFirst(query, update, Book.class);
    }
}
```

### Арифметические операторы

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $inc - инкремент
    public UpdateResult incrementDownloads(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().inc("downloads", 1);

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // $mul - умножение
    public UpdateResult applyDiscount(String category, double discountFactor) {
        Query query = Query.query(Criteria.where("category").is(category));
        Update update = new Update().mul("price", discountFactor); // 0.9 = 10% скидка

        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // $min / $max - минимум/максимум
    public UpdateResult setMinimumPrice(String title, double minPrice) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update().min("minPrice", minPrice);

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // Комплексное обновление с несколькими арифметическими операторами
    public UpdateResult complexArithmeticUpdate(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Update update = new Update()
            .inc("downloads", 1)      // +1 к скачиваниям
            .inc("views", 5)          // +5 к просмотрам
            .mul("price", 0.95)       // 5% скидка
            .max("maxPrice", 100.0);  // установить максимум

        return mongoTemplate.updateFirst(query, update, Book.class);
    }
}
```

## Delete (Удаление документов)

### deleteOne() — Удаление одного документа

```java
@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    // Удалить по ID
    void deleteById(String id);

    // Удалить по названию
    void deleteByTitle(String title);

    // Удалить по автору и году
    void deleteByAuthorAndPublishedYear(String author, Integer year);
}

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Удалить одну книгу по названию
    public void deleteBookByTitle(String title) {
        bookRepository.deleteByTitle(title);
    }

    // Удалить с использованием MongoTemplate
    public DeleteResult deleteBookByTitleTemplate(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        return mongoTemplate.remove(query, Book.class);
    }

    // Удалить с условием и получить результат
    public DeleteResult deleteBookWithCondition(String title, Double maxPrice) {
        Query query = Query.query(
            Criteria.where("title").is(title)
                    .and("price").lte(maxPrice)
        );
        return mongoTemplate.remove(query, Book.class);
    }
}
```

### deleteMany() — Удаление нескольких документов

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Удалить все книги дешевле указанной цены
    public DeleteResult deleteCheapBooks(Double maxPrice) {
        Query query = Query.query(Criteria.where("price").lt(maxPrice));
        return mongoTemplate.remove(query, Book.class);
    }

    // Удалить книги по нескольким критериям
    public DeleteResult deleteBooksByCriteria(String author, Integer beforeYear) {
        Query query = Query.query(
            Criteria.where("author").is(author)
                    .and("publishedYear").lt(beforeYear)
        );
        return mongoTemplate.remove(query, Book.class);
    }

    // Удалить все книги определенной категории
    public DeleteResult deleteBooksByCategory(String category) {
        Query query = Query.query(Criteria.where("category").is(category));
        return mongoTemplate.remove(query, Book.class);
    }

    // Каскадное удаление (удалить книгу и связанные отзывы)
    @Transactional
    public void deleteBookCascade(String bookId) {
        // Удалить книгу
        bookRepository.deleteById(bookId);

        // Удалить связанные отзывы
        Query reviewQuery = Query.query(Criteria.where("bookId").is(bookId));
        mongoTemplate.remove(reviewQuery, "reviews");
    }
}
```
// Удалить книги определенного автора
**db.books.`deleteMany`({ author: "`Unknown Author`" })
```text

### Удаление всех документов

```
`@Service`
public class `BookService` {

    `@Autowired`
    private `MongoTemplate mongoTemplate`;

    // Удалить все документы из коллекции
    public `DeleteResult deleteAllBooks`() {
        `Query query` = new `Query()`; // Пустой запрос = все документы
        return `mongoTemplate`.remove(query, `Book`.class);
    }

    // Удалить всю коллекцию (более эффективно)
    public void `dropBooksCollection()` {
        `mongoTemplate`.`dropCollection`(`Book`.class);
    }

    // Удалить коллекцию по имени
    public void `dropCollectionByName`(`String collectionName`) {
        `mongoTemplate`.`dropCollection`(`collectionName`);
    }
}
```text

## Write Concern и настройки

### Write Concern уровни

```
`@Configuration`
public class `MongoConfig` {

    `@Bean`
    public `MongoTemplate mongoTemplate`(`MongoDatabaseFactory databaseFactory`) {
        // Настройка `Write Concern` по умолчанию
        `MongoTemplate mongoTemplate` = new `MongoTemplate`(`databaseFactory`);

        // Установка `Write Concern` для всех операций
        `mongoTemplate`.`setWriteConcern`(`WriteConcern`.`ACKNOWLEDGED`);

        return `mongoTemplate`;
    }
}

`@Service`
public class `BookService` {

    `@Autowired`
    private `MongoTemplate mongoTemplate`;

    // Разные уровни подтверждения записи
    public `Book insertWithWriteConcern`(`Book book`, `WriteConcern writeConcern`) {
        // Сохранить текущий `Write Concern`
        `WriteConcern` original = `mongoTemplate`.`getWriteConcern()`;

        try {
            // Установить новый `Write Concern`
            `mongoTemplate`.`setWriteConcern`(`writeConcern`);
            return `mongoTemplate`.save(book);
        } finally {
            // Восстановить оригинальный `Write Concern`
            `mongoTemplate`.`setWriteConcern`(original);
        }
    }

    // Примеры использования разных `Write Concern`
    public `Book insertImportantBook`(`Book book`) {
        return `insertWithWriteConcern`(book, `WriteConcern`.`ACKNOWLEDGED`); // w: 1
    }

    public `Book insertCriticalBook`(`Book book`) {
        return `insertWithWriteConcern`(book, `WriteConcern`.`MAJORITY`); // w: "majority"
    }

    public `Book insertFastBook`(`Book book`) {
        return `insertWithWriteConcern`(book, `WriteConcern`.`UNACKNOWLEDGED`); // w: 0
    }
}
```text

db.books.insertOne(
  document,
  { writeConcern: { w: 0 } }  // Без подтверждения (быстрее, но риск потери)
)
```

### Bulk Write операции

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Bulk write для нескольких операций
    public BulkWriteResult performBulkOperations() {
        List<WriteModel<Book>> bulkOperations = Arrays.asList(
            // Insert operation
            new InsertOneModel<>(new Book("New Book 1", "Author 1", 19.99)),

            // Update operation
            new UpdateOneModel<>(
                Criteria.where("title").is("Existing Book"),
                new Update().set("price", 29.99)
            ),

            // Delete operation
            new DeleteOneModel<>(Criteria.where("title").is("Old Book")),

            // Replace operation
            new ReplaceOneModel<>(
                Criteria.where("title").is("Book to Replace"),
                new Book("Replaced Book", "New Author", 39.99)
            )
        );

        return mongoTemplate.getCollection("books")
                           .bulkWrite(bulkOperations);
    }

    // Оптимизированный bulk insert
    public BulkWriteResult bulkInsertBooks(List<Book> books) {
        List<WriteModel<Book>> inserts = books.stream()
            .map(book -> new InsertOneModel<>(book))
            .collect(Collectors.toList());

        BulkWriteOptions options = new BulkWriteOptions()
            .ordered(false)  // Неупорядочная вставка для производительности
            .bypassDocumentValidation(false);

        return mongoTemplate.getCollection("books")
                           .bulkWrite(inserts, options);
    }
}
```

## Лучшие практики для CRUD операций

### Производительность

1. **Используйте индексы** для условий запросов
2. **Ограничьте проекции** только нужными полями
3. **Используйте bulk операции** для множественных изменений
4. **Настройте write concern** в зависимости от требований

### Надежность

1. **Проверяйте результаты операций** (acknowledged, modifiedCount)
2. **Используйте транзакции** для связанных операций
3. **Валидируйте данные** перед вставкой
4. **Мониторьте производительность** операций

### Безопасность

1. **Валидируйте входные данные** на уровне приложения
2. **Используйте параметризованные запросы** (в драйверах)
3. **Ограничьте права доступа** к коллекциям
4. **Логируйте критичные операции**
5. **Используйте `HTTPS`/TLS** для соединений
6. **Регулярно обновляйте** драйверы и сервер

## Примеры комплексных операций

### Обновление с условием и расчетом

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Увеличить цену книг, проданных более 100 раз
    public UpdateResult increasePriceForBestSellers() {
        Query query = Query.query(Criteria.where("sales").gt(100));

        // Использование aggregation pipeline в update
        AggregationUpdate update = AggregationUpdate.update()
            .set("price").multiplyBy("$price", 1.2)  // Увеличить на 20%
            .set("lastPriceUpdate").dateValueOf(DateOperators.dateFromString("$$NOW"));

        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // Обновление с конвейером агрегации
    public UpdateResult complexPriceUpdate() {
        Query query = Query.query(Criteria.where("category").is("bestseller"));

        AggregationUpdate update = AggregationUpdate.update()
            .set("discountedPrice")
                .multiplyBy("$price", 0.8)  // 20% скидка
            .set("discountApplied").dateValueOf(DateOperators.dateFromString("$$NOW"))
            .set("status").concat("$status", " - DISCOUNTED");

        return mongoTemplate.updateMulti(query, update, Book.class);
    }
}
```

### Условное обновление

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Обновить только если цена ниже порога
    public UpdateResult applyDiscountIfCheap(String title, Double maxPrice, Double discountFactor) {
        Query query = Query.query(
            Criteria.where("title").is(title)
                    .and("price").lt(maxPrice)
        );

        Update update = new Update()
            .set("discount", true)
            .multiply("price", discountFactor)  // Применить скидку
            .currentDate("discountApplied");

        return mongoTemplate.updateFirst(query, update, Book.class);
    }

    // Условное обновление с проверкой нескольких полей
    public UpdateResult conditionalUpdateWithValidation(String bookId) {
        Query query = Query.query(
            Criteria.where("_id").is(bookId)
                    .and("status").is("active")
                    .and("stock").gt(0)
        );

        Update update = new Update()
            .inc("sales", 1)
            .inc("stock", -1)
            .set("lastSold", new Date());

        return mongoTemplate.updateFirst(query, update, Book.class);
    }
}
```

### Обновление с использованием aggregation pipeline

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Обновление с условной логикой через aggregation pipeline
    public UpdateResult categorizeOldBooks() {
        Query query = Query.query(Criteria.where("publishedYear").lt(2020));

        // Условное присвоение категории на основе количества страниц
        AggregationUpdate update = AggregationUpdate.update()
            .set("category").conditional(
                ComparisonOperators.Gte.valueOf("$pages").gteValue(400),
                "reference",  // Если >= 400 страниц
                "guide"       // Иначе
            )
            .set("categorizedAt").dateValueOf(DateOperators.dateFromString("$$NOW"));

        return mongoTemplate.updateMulti(query, update, Book.class);
    }

    // Сложное обновление с несколькими этапами aggregation
    public UpdateResult complexAggregationUpdate() {
        Query query = Query.query(Criteria.where("status").is("active"));

        AggregationUpdate update = AggregationUpdate.update()
            // Вычислить новую цену на основе рейтинга и продаж
            .set("dynamicPrice").multiply(
                Arrays.asList("$basePrice", "$rating", "$sales"),
                Arrays.asList(1.0, 0.1, 0.001)
            )
            // Обновить статистику
            .set("lastCalculated", new Date())
            .set("priceCategory").switchCases(
                CaseOperator.when(ComparisonOperators.Lt.valueOf("$dynamicPrice").ltValue(20.0)).then("budget"),
                CaseOperator.when(ComparisonOperators.Lt.valueOf("$dynamicPrice").ltValue(50.0)).then("standard")
            ).defaultTo("premium");

        return mongoTemplate.updateMulti(query, update, Book.class);
    }
}
```

**MongoDB** предоставляет богатый набор **CRUD** операций для работы с документами. Ключевые особенности:**

### Основные принципы:

1. **Document-based operations**: Все операции работают с **JSON**/**BSON** документами
2. **Flexible updates**: Разнообразные операторы обновления полей и массивов
3. **Atomic operations**: Гарантии атомарности на уровне документа
4. **Bulk operations**: Эффективная обработка множественных операций

### Лучшие практики:

- **Используйте `Repository` pattern** для типичных операций
- **MongoTemplate** для сложных запросов и обновлений
- **Транзакции** для связанных операций (MongoDB 4.0+)
- **Write Concern** в соответствии с требованиями консистентности
- **Индексы** для оптимизации запросов

### Производительность:

- **Bulk operations** для массовых вставок/обновлений
- **Aggregation pipeline** для сложных трансформаций
- **Proper indexing** для эффективных запросов
- **Connection pooling** для масштабируемости

**CRUD** операции в **MongoDB** сочетают простоту использования с мощными возможностями для работы с большими объемами данных.

## Работа с большими коллекциями

### Пагинация

```java
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Пагинация с Pageable
    public Page<Book> getBooksWithPagination(int page, int size, String sortBy, boolean ascending) {
        Sort sort = ascending ?
            Sort.by(sortBy).ascending() :
            Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return bookRepository.findAll(pageable);
    }

    // Пагинация с кастомным запросом
    public Page<Book> getBooksByAuthorWithPagination(String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        // Использование Page для автоматической пагинации
        return bookRepository.findByAuthor(author, pageable);
    }

    // Streaming для больших наборов данных
    public void processBooksInBatches() {
        Query query = new Query();
        query.with(Sort.by("title"));

        try (Cursor<Book> cursor = mongoTemplate.stream(query, Book.class)) {
            Iterator<Book> iterator = cursor.iterator();

            while (iterator.hasNext()) {
                Book book = iterator.next();
                // Обработка книги
                processBook(book);
            }
        }
    }

    private void processBook(Book book) {
        // Логика обработки
    }
}
```

### Обработка ошибок

```java
@Service
public class BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Обработка ошибок при вставке
    public Book insertBookWithErrorHandling(Book book) {
        try {
            Book savedBook = mongoTemplate.save(book);

            // Логирование успешной операции
            logger.info("Book inserted successfully: {}", savedBook.getId());
            return savedBook;

        } catch (DuplicateKeyException e) {
            // Обработка ошибки дублирования ключа
            logger.error("Duplicate book title: {}", book.getTitle());
            throw new BookAlreadyExistsException("Book with this title already exists", e);

        } catch (DataIntegrityViolationException e) {
            // Обработка нарушения целостности данных
            logger.error("Data integrity violation for book: {}", book.getTitle());
            throw new InvalidBookDataException("Invalid book data", e);

        } catch (MongoTimeoutException e) {
            // Обработка таймаута соединения
            logger.error("MongoDB timeout while inserting book: {}", book.getTitle());
            throw new DatabaseTimeoutException("Database operation timed out", e);

        } catch (Exception e) {
            // Обработка других ошибок
            logger.error("Unexpected error while inserting book: {}", book.getTitle(), e);
            throw new BookServiceException("Failed to insert book", e);
        }
    }

    // Безопасное обновление с обработкой конфликтов
    public boolean safeUpdateBook(String bookId, Book updates) {
        try {
            Query query = Query.query(Criteria.where("_id").is(bookId));
            Update update = createUpdateFromBook(updates);

            UpdateResult result = mongoTemplate.updateFirst(query, update, Book.class);
            return result.getModifiedCount() > 0;

        } catch (OptimisticLockingFailureException e) {
            logger.warn("Concurrent modification detected for book: {}", bookId);
            return false;

        } catch (Exception e) {
            logger.error("Error updating book: {}", bookId, e);
            return false;
        }
    }

    private Update createUpdateFromBook(Book book) {
        Update update = new Update();
        if (book.getTitle() != null) update.set("title", book.getTitle());
        if (book.getPrice() != null) update.set("price", book.getPrice());
        // ... другие поля
        update.currentDate("updatedAt");
        return update;
    }
}
```

## Заключение

CRUD операции в MongoDB предоставляют гибкий и мощный интерфейс для работы с документами:

### Ключевые принципы:
- Гибкость: Операции поддерживают сложные условия и обновления
- Производительность: Bulk операции и оптимизации для больших наборов данных
- Надежность: Write concern и транзакции обеспечивают консистентность
- Масштабируемость: Операции работают эффективно в кластерных развертываниях

### Основные методы:
- Create: `insertOne()`, `insertMany()`
- Read: `find()`, `findOne()`
- Update: `updateOne()`, `updateMany()`, `replaceOne()`
- Delete: `deleteOne()`, `deleteMany()`

## Решение проблем

**Duplicate key (E11000):** при вставке с уже существующим `_id` или уникальным индексом возникает ошибка. Используйте `upsert` (`updateOne` с `upsert: true`) при необходимости «вставить или обновить», либо проверяйте наличие документа перед вставкой. Для массовых вставок обрабатывайте дубликаты в коде или используйте `ordered: false` и обрабатывайте ошибки по элементам.

**Медленные обновления/удаления:** создайте индексы по полям в фильтре `updateMany`/`deleteMany`. Избегайте обновления больших подмножеств без индекса. Для массового удаления рассмотрите удаление коллекции или пересоздание с нужными данными.

**Частичное обновление не применяется:** проверьте использование операторов `$set`, `$unset` и т.д.; без них документ может быть заменён целиком. Учитывайте, что `updateMany` обновляет все совпадающие документы — ограничьте выборку при необходимости.

**Транзакционные ошибки (transaction aborted):** убедитесь, что все операции в сессии поддерживают транзакции (replica set, не standalone). Проверьте таймауты и конфликты записи. При повторных попытках используйте idempotent-операции.


Следующие темы:
- [[mongodb-queries|Запросы и фильтры]]
- [[mongodb-indexes|Индексы]]


