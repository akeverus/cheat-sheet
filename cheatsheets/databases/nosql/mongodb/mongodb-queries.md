---
title: "MongoDB: Запросы и операторы - Полное руководство по поиску документов"
description: "Комплексное руководство по запросам в MongoDB: операторы сравнения, логические операторы, работа с массивами, текстом и геоданными."
tags:
  - databases
  - nosql
  - mongodb-queries
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **MongoDB**: Запросы и операторы - Полное руководство по поиску документов

Комплексное руководство по запросам в **MongoDB**: операторы сравнения, логические операторы, работа с массивами, текстом и геоданными.

## Полезные ссылки

### Официальная документация
- [MongoDB Query Documents](https://www.mongodb.com/docs/manual/tutorial/query-documents/)
- [MongoDB Query Operators](https://www.mongodb.com/docs/manual/reference/operator/query/)
- [MongoDB Text Search](https://www.mongodb.com/docs/manual/text-search/)

### **Baeldung**
- [MongoDB Query Methods](https://www.baeldung.com/java-mongodb-query) — запросы из **Java**

### См. также
- [[mongodb-crud|CRUD]] — основы **CRUD** операций
- [[mongodb-indexes|Индексы]] — оптимизация запросов

## Содержание

- [Обзор системы запросов **MongoDB**](#обзор-системы-запросов-mongodb)
- [Базовые запросы](#базовые-запросы)
  - [Простые условия](#простые-условия)
  - [Логические операторы](#логические-операторы)
- [Работа с массивами](#работа-с-массивами)
  - [Операторы для массивов](#операторы-для-массивов)
  - [Запросы к массивам объектов](#запросы-к-массивам-объектов)
- [Операторы элементов](#операторы-элементов)
  - [Проверка существования и типа](#проверка-существования-и-типа)
  - [Операторы оценки](#операторы-оценки)
- [Текстовые запросы](#текстовые-запросы)
  - [Текстовый поиск](#текстовый-поиск)
  - [Регулярные выражения](#регулярные-выражения)
- [Геопространственные запросы](#геопространственные-запросы)
  - [Геопространственные индексы](#геопространственные-индексы)
  - [**GeoJSON** запросы](#geojson-запросы)
- [Продвинутые операторы](#продвинутые-операторы)
  - [Операторы оценки значений](#операторы-оценки-значений)
  - [Операторы для работы с битами](#операторы-для-работы-с-битами)
- [Проекции и трансформации](#проекции-и-трансформации)
  - [Выбор полей (**Projection**)](#выбор-полей-projection)
  - [Трансформация результатов](#трансформация-результатов)
- [Оптимизация запросов](#оптимизация-запросов)
  - [**Explain** план выполнения](#explain-план-выполнения)
  - [Использование индексов](#использование-индексов)
  - [Query optimization tips](#query-optimization-tips)
- [Комплексные примеры запросов](#комплексные-примеры-запросов)
  - [Поиск с агрегацией условий](#поиск-с-агрегацией-условий)
  - [Запросы с вложенными условиями](#запросы-с-вложенными-условиями)
  - [Геопространственные запросы с фильтрами](#геопространственные-запросы-с-фильтрами)
- [Заключение](#заключение)
  - [Основные возможности:](#основные-возможности)
  - [Лучшие практики:](#лучшие-практики)
- [Best Practices для запросов](#лучшие-практики-для-запросов)
  - [Производительность](#производительность)
  - [Безопасность](#безопасность)
  - [Поддерживаемость](#поддерживаемость)
  - [Основные категории операторов:](#основные-категории-операторов)
  - [Ключевые принципы:](#ключевые-принципы)
- [Решение проблем](#решение-проблем)

## Обзор системы запросов **MongoDB**

**MongoDB** предоставляет мощный и гибкий язык запросов для поиска документов в коллекциях. Запросы строятся на основе операторов сравнения, логических операций и специальных операторов для работы с различными типами данных.

Схема категорий операторов запросов **MongoDB** (**Comparison, `Logical`, `Element` и др.**).

```text
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Comparison    │ -> │    Logical      │ -> │    Element      │
│   Operators     │    │   Operators     │    │   Operators     │
│ ($eq, $gt, $lt) │    │ ($and, $or, $nor)│    │ ($exists, $type)│
└─────────────────┘    └─────────────────┘    └─────────────────┘
┌─────────────────┐    ┌─────────────────┐    └─────────────────┘
│   Array         │ -> │   Evaluation     │ -> │   Geospatial     │
│   Operators     │    │   Operators      │    │   Operators      │
│ ($in, $all, $size)│   │ ($regex, $mod)    │    │ ($near, $geoWithin)
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Базовые запросы

### Простые условия

Пример репозитория и запросов через **Spring Data MongoDB** (**Java**).

```java
@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    // Равенство
    List<Book> findByAuthor(String author);

    // Несколько полей
    List<Book> findByAuthorAndPublishedYear(String author, Integer publishedYear);

    // Операторы сравнения
    List<Book> findByPriceGreaterThan(Double price);
    List<Book> findByPriceGreaterThanEqual(Double price);
    List<Book> findByPriceLessThan(Double price);
    List<Book> findByPriceLessThanEqual(Double price);
    List<Book> findByPriceNot(Double price);

    // Сложные условия с Criteria
    @Query("{ 'price' : { $gt: ?0, $lt: ?1 } }")
    List<Book> findByPriceRange(Double minPrice, Double maxPrice);
}

@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Равенство
    public List<Book> findBooksByAuthor(String author) {
        return mongoTemplate.find(
            Query.query(Criteria.where("author").is(author)),
            Book.class
        );
    }

    // Операторы сравнения
    public List<Book> findExpensiveBooks(Double minPrice) {
        return mongoTemplate.find(
            Query.query(Criteria.where("price").gt(minPrice)),
            Book.class
        );
    }

    public List<Book> findCheapBooks(Double maxPrice) {
        return mongoTemplate.find(
            Query.query(Criteria.where("price").lt(maxPrice)),
            Book.class
        );
    }

    public List<Book> findBooksInPriceRange(Double minPrice, Double maxPrice) {
        return mongoTemplate.find(
            Query.query(Criteria.where("price").gte(minPrice).lte(maxPrice)),
            Book.class
        );
    }
}
```

### Логические операторы

```java
@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $and - логическое И (неявное по умолчанию)
    public List<Book> findBooksWithAndCondition() {
        return mongoTemplate.find(
            Query.query(Criteria.where("price").gt(20).lt(50)),
            Book.class
        );
    }

    // $or - логическое ИЛИ
    public List<Book> findBooksByAuthorsOr(String author1, String author2) {
        return mongoTemplate.find(
            Query.query(new Criteria().orOperator(
                Criteria.where("author").is(author1),
                Criteria.where("author").is(author2)
            )),
            Book.class
        );
    }

    // $nor - отрицание ИЛИ
    public List<Book> findBooksWithNorCondition() {
        return mongoTemplate.find(
            Query.query(new Criteria().norOperator(
                Criteria.where("price").lt(10),
                Criteria.where("pages").lt(100)
            )),
            Book.class
        );
    }

    // $not - отрицание условия
    public List<Book> findBooksWithNotCondition() {
        return mongoTemplate.find(
            Query.query(Criteria.where("price").not().gt(50)),
            Book.class
        );
    }
}
```

## Работа с массивами

### Операторы для массивов

```java
@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $in - значение в массиве
    public List<Book> findBooksWithTags(List<String> tags) {
        return mongoTemplate.find(
            Query.query(Criteria.where("tags").in(tags)),
            Book.class
        );
    }

    // $nin - значение НЕ в массиве
    public List<Book> findBooksWithoutTags(List<String> excludedTags) {
        return mongoTemplate.find(
            Query.query(Criteria.where("tags").nin(excludedTags)),
            Book.class
        );
    }

    // $all - все значения присутствуют в массиве
    public List<Book> findBooksWithAllTags(List<String> requiredTags) {
        return mongoTemplate.find(
            Query.query(Criteria.where("tags").all(requiredTags)),
            Book.class
        );
    }

    // $size - размер массива
    public List<Book> findBooksWithTagCount(int tagCount) {
        return mongoTemplate.find(
            Query.query(Criteria.where("tags").size(tagCount)),
            Book.class
        );
    }

    // $elemMatch - элементы массива удовлетворяют условию
    public List<Book> findBooksWithGoodRecentReviews() {
        return mongoTemplate.find(
            Query.query(Criteria.where("reviews").elemMatch(
                Criteria.where("rating").gte(4)
                       .and("date").gte(new Date(2024, 0, 1))
            )),
            Book.class
        );
    }

    // Запросы к вложенным массивам объектов
    public List<Book> findBooksWithExpensiveReviews() {
        return mongoTemplate.find(
            Query.query(Criteria.where("reviews.price").gte(50)),
            Book.class
        );
    }
}
```

### Запросы к массивам объектов

```java
// Структура документа с массивом объектов
/*
{
  title: "MongoDB Guide",
  reviews: [
    { user: "john", rating: 5, date: ISODate("2024-01-15") },
    { user: "jane", rating: 4, date: ISODate("2024-01-20") }
  ]
}
*/

@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Найти книги с хотя бы одним отзывом с рейтингом >= 4
    public List<Book> findBooksWithGoodReviews() {
        return mongoTemplate.find(
            Query.query(Criteria.where("reviews.rating").gte(4)),
            Book.class
        );
    }

    // Найти книги где все отзывы имеют рейтинг >= 3
    public List<Book> findBooksWithAllGoodReviews() {
        return mongoTemplate.find(
            Query.query(Criteria.where("reviews").not().elemMatch(
                Criteria.where("rating").lt(3)
            )),
            Book.class
        );
    }

    // Найти книги с отзывами от конкретного пользователя
    public List<Book> findBooksReviewedByUser(String username) {
        return mongoTemplate.find(
            Query.query(Criteria.where("reviews.user").is(username)),
            Book.class
        );
    }
}
```

## Операторы элементов

### Проверка существования и типа

```java
@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $exists - поле существует
    public List<Book> findBooksWithDiscount() {
        return mongoTemplate.find(
            Query.query(Criteria.where("discount").exists(true)),
            Book.class
        );
    }

    // Найти книги без скидки
    public List<Book> findBooksWithoutDiscount() {
        return mongoTemplate.find(
            Query.query(Criteria.where("discount").exists(false)),
            Book.class
        );
    }

    // $type - тип поля
    public List<Book> findBooksWithDoublePrice() {
        return mongoTemplate.find(
            Query.query(Criteria.where("price").type(Type.DOUBLE)),
            Book.class
        );
    }

    // Найти документы с массивами тегов
    public List<Book> findBooksWithTagsArray() {
        return mongoTemplate.find(
            Query.query(Criteria.where("tags").type(Type.ARRAY)),
            Book.class
        );
    }

    // Смешанные условия существования
    public List<Book> findComplexExistenceQuery() {
        return mongoTemplate.find(
            Query.query(new Criteria().andOperator(
                Criteria.where("discount").exists(true),
                Criteria.where("reviews").exists(true),
                Criteria.where("tags").type(Type.ARRAY)
            )),
            Book.class
        );
    }
}
```

### Операторы оценки

```java
@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $regex - регулярные выражения
    public List<Book> findBooksByTitleRegex(String pattern) {
        return mongoTemplate.find(
            Query.query(Criteria.where("title").regex(pattern)),
            Book.class
        );
    }

    // Regex с опциями (case insensitive)
    public List<Book> findBooksCaseInsensitive(String title) {
        return mongoTemplate.find(
            Query.query(Criteria.where("title").regex(title, "i")),
            Book.class
        );
    }

    // $mod - деление по модулю
    public List<Book> findBooksWithEvenPageCount() {
        return mongoTemplate.find(
            Query.query(Criteria.where("pages").mod(2, 0)), // четное количество страниц
            Book.class
        );
    }

    // $where - JavaScript выражения (осторожно использовать!)
    public List<Book> findBooksWithWhereClause() {
        // Использовать только при необходимости, влияет на производительность
        return mongoTemplate.find(
            Query.query(Criteria.where("$where").is("this.price > this.cost * 2")),
            Book.class
        );
    }
}
```

## Текстовые запросы

### Текстовый поиск

```java
@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Текстовый поиск (требует текстового индекса)
    public List<Book> searchBooksByText(String searchText) {
        return mongoTemplate.find(
            Query.query(Criteria.where("$text")
                .is(new Document("$search", searchText))),
            Book.class
        );
    }

    // Текстовый поиск с score
    public List<Book> searchBooksWithScore(String searchText) {
        Query query = Query.query(Criteria.where("$text")
            .is(new Document("$search", searchText)));

        query.fields().include("score")
            .meta("textScore", "score");

        return mongoTemplate.find(query.with(Sort.by("score")), Book.class);
    }

    // Поиск фразы
    public List<Book> searchExactPhrase(String phrase) {
        return mongoTemplate.find(
            Query.query(Criteria.where("$text")
                .is(new Document("$search", "\"" + phrase + "\""))),
            Book.class
        );
    }

    // Исключение слов
    public List<Book> searchWithExclusion(String includeWord, String excludeWord) {
        String searchQuery = includeWord + " -" + excludeWord;
        return mongoTemplate.find(
            Query.query(Criteria.where("$text")
                .is(new Document("$search", searchQuery))),
            Book.class
        );
    }
}
```

### Регулярные выражения

```java
@Service
public class BookQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Regex: начинается с определенной строки
    public List<Book> findBooksStartingWith(String prefix) {
        return mongoTemplate.find(
            Query.query(Criteria.where("title").regex("^" + Pattern.quote(prefix))),
            Book.class
        );
    }

    // Regex: заканчивается на определенную строку
    public List<Book> findBooksEndingWith(String suffix) {
        return mongoTemplate.find(
            Query.query(Criteria.where("title").regex(Pattern.quote(suffix) + "$", "i")),
            Book.class
        );
    }

    // Валидация email через regex
    public List<Book> findValidEmails() {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return mongoTemplate.find(
            Query.query(Criteria.where("email").regex(emailRegex)),
            Book.class
        );
    }

    // Сложные паттерны
    public List<Book> findBooksWithComplexPattern() {
        // Книги с заголовками содержащими "MongoDB" или "Database"
        return mongoTemplate.find(
            Query.query(Criteria.where("title").regex("(MongoDB|Database)", "i")),
            Book.class
        );
    }
}
```

## Геопространственные запросы

### Геопространственные индексы

```java
@Configuration
public class MongoConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void createGeoIndexes() {
        // 2dsphere индекс для GeoJSON
        mongoTemplate.indexOps(Place.class)
            .ensureIndex(new GeospatialIndex("location").typed(IndexType.GEO_2DSPHERE));

        // 2d индекс для legacy координат
        mongoTemplate.indexOps(LegacyPlace.class)
            .ensureIndex(new GeospatialIndex("location").typed(IndexType.GEO_2D));
    }
}

// Entity с геоданными
@Document(collection = "places")
public class Place {
    @Id
    private String id;
    private String name;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Point location; // Spring Data MongoDB Point

    // getters and setters
}
```

### **GeoJSON** запросы

```java
@Service
public class PlaceQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $near - ближайшие точки
    public List<Place> findNearbyPlaces(double longitude, double latitude, double maxDistanceMeters) {
        Point referencePoint = new Point(longitude, latitude);

        NearQuery nearQuery = NearQuery.near(referencePoint)
            .maxDistance(new Distance(maxDistanceMeters, Metrics.KILOMETERS))
            .spherical(true);

        return mongoTemplate.find(nearQuery, Place.class);
    }

    // $geoWithin - точки внутри геометрии
    public List<Place> findPlacesWithinPolygon() {
        // Создаем полигон
        List<Point> points = Arrays.asList(
            new Point(-74.0, 40.7),
            new Point(-73.9, 40.7),
            new Point(-73.9, 40.8),
            new Point(-74.0, 40.8),
            new Point(-74.0, 40.7) // Замыкающая точка
        );

        Polygon polygon = new Polygon(points);

        return mongoTemplate.find(
            Query.query(Criteria.where("location").within(polygon)),
            Place.class
        );
    }

    // $geoIntersects - пересечение с геометрией
    public List<Place> findPlacesIntersectingLine() {
        List<Point> linePoints = Arrays.asList(
            new Point(-74.0, 40.7),
            new Point(-73.9, 40.8)
        );

        LineString lineString = new LineString(linePoints);

        return mongoTemplate.find(
            Query.query(Criteria.where("location").intersects(lineString)),
            Place.class
        );
    }

    // Расстояние до точки
    public List<Document> findPlacesWithDistance(double longitude, double latitude) {
        Point referencePoint = new Point(longitude, latitude);

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("location").near(referencePoint).maxDistance(5000)),
            Aggregation.project()
                .andExpression("location").as("location")
                .and("name").as("name")
                .andExpression("distance").as("distance")
        );

        return mongoTemplate.aggregate(aggregation, "places", Document.class)
                          .getMappedResults();
    }
}
```

## Продвинутые операторы

### Операторы оценки значений

```java
@Service
public class AdvancedQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $expr - использование aggregation expressions в запросах
    public List<Book> findBooksWherePriceGreaterThanCost() {
        return mongoTemplate.find(
            Query.query(Criteria.where("$expr")
                .is(new Document("$gt", Arrays.asList("$price", "$cost")))),
            Book.class
        );
    }

    // $jsonSchema - валидация по JSON Schema
    public List<Book> findBooksByJsonSchema() {
        Document schema = new Document()
            .append("required", Arrays.asList("title", "author"))
            .append("properties", new Document()
                .append("price", new Document()
                    .append("type", "number")
                    .append("minimum", 0)
                )
            );

        return mongoTemplate.find(
            Query.query(Criteria.where("$jsonSchema").is(schema)),
            Book.class
        );
    }

    // Сложные выражения с $expr
    public List<Book> findBooksWithComplexExpressions() {
        // Найти книги где цена > стоимость * 1.5
        return mongoTemplate.find(
            Query.query(Criteria.where("$expr").is(
                new Document("$gt", Arrays.asList(
                    "$price",
                    new Document("$multiply", Arrays.asList("$cost", 1.5))
                ))
            )),
            Book.class
        );
    }
}
```

### Операторы для работы с битами

```java
@Service
public class BitwiseQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // $bitsAllSet - все биты установлены
    public List<Sensor> findSensorsWithAllBitsSet(int[] bits) {
        return mongoTemplate.find(
            Query.query(Criteria.where("flags").bits().allSet(bits)),
            Sensor.class
        );
    }

    // $bitsAnySet - хотя бы один бит установлен
    public List<Sensor> findSensorsWithAnyBitsSet(int[] bits) {
        return mongoTemplate.find(
            Query.query(Criteria.where("flags").bits().anySet(bits)),
            Sensor.class
        );
    }

    // $bitsAllClear - все биты сброшены
    public List<Sensor> findSensorsWithAllBitsClear(int[] bits) {
        return mongoTemplate.find(
            Query.query(Criteria.where("flags").bits().allClear(bits)),
            Sensor.class
        );
    }

    // Комплексные битовые операции
    public List<Sensor> findSensorsWithBitwiseConditions() {
        // Сенсоры где установлены биты 1 ИЛИ 3, но сброшен бит 2
        Criteria criteria = new Criteria().andOperator(
            Criteria.where("flags").bits().anySet(new int[]{1, 3}),
            Criteria.where("flags").bits().allClear(new int[]{2})
        );

        return mongoTemplate.find(Query.query(criteria), Sensor.class);
    }

    // Работа с флагами как с битовой маской
    public List<Sensor> findSensorsByFlagMask(int flagMask) {
        // Найти сенсоры где все биты из маски установлены
        return mongoTemplate.find(
            Query.query(Criteria.where("flags").bits().allSet(flagMask)),
            Sensor.class
        );
    }
}
```

## Проекции и трансформации

### Выбор полей (**Projection**)

```java
@Service
public class BookProjectionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Включить только определенные поля
    public List<BookBasicInfo> findBooksBasicInfo() {
        Query query = new Query();
        query.fields()
            .include("title")
            .include("author")
            .exclude("_id");

        return mongoTemplate.find(query, BookBasicInfo.class, "books");
    }

    // Исключить поля
    public List<Book> findBooksWithoutDetails() {
        Query query = new Query();
        query.fields()
            .exclude("description")
            .exclude("internalId");

        return mongoTemplate.find(query, Book.class);
    }

    // Включить поля массива
    public List<BookReviewProjection> findBooksReviewInfo() {
        Query query = new Query();
        query.fields()
            .include("title")
            .include("reviews.user")
            .include("reviews.rating")
            .exclude("_id");

        return mongoTemplate.find(query, BookReviewProjection.class, "books");
    }

    // Динамическая проекция
    public List<Document> findBooksWithCustomProjection(List<String> includeFields) {
        Query query = new Query();
        Field fields = query.fields();

        for (String field : includeFields) {
            fields.include(field);
        }
        fields.exclude("_id");

        return mongoTemplate.find(query, Document.class, "books");
    }
}
```

### Трансформация результатов

```java
@Service
public class BookTransformationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Вычисляемые поля через aggregation
    public List<Document> findBooksWithDiscountedPrice() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.project()
                .and("title").as("title")
                .and("author").as("author")
                .and(ArithmeticOperators.Multiply.valueOf("price").multiplyBy(0.9))
                    .as("discountedPrice")
                .and("price").as("originalPrice")
        );

        return mongoTemplate.aggregate(aggregation, "books", Document.class)
                          .getMappedResults();
    }

    // Множественные трансформации
    public List<Document> findBooksWithCalculatedFields() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.project()
                .and("title").as("title")
                .and("price").as("price")
                .and(ArithmeticOperators.Multiply.valueOf("price").multiplyBy(0.9))
                    .as("discountedPrice")
                .and(ComparisonOperators.Gt.valueOf("price").gtValue(50.0))
                    .as("isExpensive")
                .and(DateOperators.DateToString.dateOf("publishDate")
                    .dateFormat("%Y-%m-%d")).as("publishDateFormatted")
        );

        return mongoTemplate.aggregate(aggregation, "books", Document.class)
                          .getMappedResults();
    }

    // Условные трансформации
    public List<Document> findBooksWithCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.project()
                .and("title").as("title")
                .and("pages").as("pages")
                .and(ConditionalOperators.ifNull("category")
                    .then(ConditionalOperators.switchCases(
                        CaseOperator.when(ComparisonOperators.Gte.valueOf("pages").gteValue(400))
                            .then("reference"),
                        CaseOperator.when(ComparisonOperators.Gte.valueOf("pages").gteValue(200))
                            .then("guide")
                    ).defaultTo("short")))
                .as("category")
        );

        return mongoTemplate.aggregate(aggregation, "books", Document.class)
                          .getMappedResults();
    }
}
```

## Оптимизация запросов

### **Explain** план выполнения

```java
@Service
public class QueryOptimizationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Анализ плана выполнения
    public Document explainQuery(Query query) {
        return mongoTemplate.getCollection("books")
                           .find(query.getQueryObject())
                           .explain(Document.class);
    }

    // Подробный анализ с executionStats
    public Document explainWithStats(Query query) {
        Document explainCommand = new Document();
        explainCommand.put("explain", new Document()
            .append("find", "books")
            .append("filter", query.getQueryObject())
            .append("verbosity", "executionStats")
        );

        return mongoTemplate.executeCommand(explainCommand);
    }

    // Анализ медленных запросов
    public List<Document> analyzeSlowQueries() {
        // Получить статистику запросов из system.profile
        Query slowQuery = Query.query(Criteria.where("millis").gte(100));
        slowQuery.with(Sort.by(Sort.Direction.DESC, "millis"));
        slowQuery.limit(10);

        return mongoTemplate.find(slowQuery, Document.class, "system.profile");
    }

    // Проверка использования индексов
    public Document checkIndexUsage(String indexName) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("op").is("query")),
            Aggregation.group("$ns")
                .sum(ConditionalOperators.ifNull("nscanned").then(0)).as("totalScanned")
                .sum(ConditionalOperators.ifNull("nreturned").then(0)).as("totalReturned")
                .sum(ConditionalOperators.ifNull("scanAndOrder").then(0)).as("scanAndOrder")
        );

        return mongoTemplate.aggregate(aggregation, "system.profile", Document.class)
                          .getUniqueMappedResult();
    }
    }

    // Все стадии выполнения
    public Document explainAllPlans(Query query) {
        Document explainCommand = new Document();
        explainCommand.put("explain", new Document()
            .append("find", "books")
            .append("filter", query.getQueryObject())
            .append("verbosity", "allPlansExecution")
        );

        return mongoTemplate.executeCommand(explainCommand);
    }
}
```

### Использование индексов

```java
@Configuration
public class IndexConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        // Создание индекса
        mongoTemplate.indexOps(Book.class)
            .ensureIndex(new Index().on("price", Sort.Direction.ASC));

        // Compound индекс
        mongoTemplate.indexOps(Book.class)
            .ensureIndex(new CompoundIndexDefinition(new Document()
                .append("author", 1)
                .append("price", 1)));
    }
}

// Service для работы с индексами
@Service
public class IndexManagementService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Создание индекса программно
    public String createPriceIndex() {
        Index index = new Index().on("price", Sort.Direction.ASC);
        return mongoTemplate.indexOps(Book.class).ensureIndex(index);
    }

    // Compound индекс
    public String createAuthorPriceIndex() {
        CompoundIndexDefinition compoundIndex = new CompoundIndexDefinition(
            new Document()
                .append("author", 1)
                .append("price", 1)
        );
        return mongoTemplate.indexOps(Book.class).ensureIndex(compoundIndex);
    }

    // Текстовый индекс
    public String createTextIndex() {
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
            .on("title", TextIndexDefinition.TextIndexDefinitionBuilder.TextIndexType.TEXT)
            .on("description", TextIndexDefinition.TextIndexDefinitionBuilder.TextIndexType.TEXT)
            .build();
        return mongoTemplate.indexOps(Book.class).ensureIndex(textIndex);
    }

    // Геоспatial индекс
    public String createGeoIndex() {
        GeospatialIndex geoIndex = new GeospatialIndex("location");
        geoIndex.typed(IndexType.GEO_2DSPHERE);
        return mongoTemplate.indexOps(Place.class).ensureIndex(geoIndex);
    }

    // Просмотр существующих индексов
    public List<IndexInfo> getIndexes() {
        return mongoTemplate.indexOps(Book.class).getIndexInfo();
    }

    // Удаление индекса
    public void dropIndex(String indexName) {
        mongoTemplate.indexOps(Book.class).dropIndex(indexName);
    }
}
```

// Текстовый индекс
**db.books.createIndex**({
  **title**: "**text**",
  **description**: "**text**"
})

// Геопространственный индекс
**db.places.`createIndex`(**{ location: "2dsphere" }**)
```

### Query optimization tips

1. Используйте индексы для часто используемых условий
2. Избегайте сканирования коллекции (COLLSCAN)
3. Используйте проекции для уменьшения объема данных
4. Оптимизируйте порядок полей в compound индексах
5. Мониторьте производительность запросов

```

## Комплексные примеры запросов

### Поиск с агрегацией условий

```java
`@Service`
public class `AdvancedBookQueryService` {

    `@Autowired`
    private `MongoTemplate mongoTemplate`;

    // Книги с высоким рейтингом и приемлемой ценой
    public `List`<`Book`> `findHighlyRatedAffordableBooks`() {
        `Criteria criteria` = new `Criteria`().`andOperator`(
            `Criteria`.where("`averageRating`").gte(`4.0`),
            `Criteria`.where("price").lte(`50.0`),
            `Criteria`.where("pages").gte(`200`)
        ).and("tags").in("bestseller", "popular");

        `Query query` = `Query`.query(criteria);
        `query.with`(`Sort`.by(
            `Sort`.`Order`.desc("`averageRating`"),
            `Sort`.`Order`.asc("price")
        ));

        return `mongoTemplate`.find(query, `Book`.class);
    }

    // Комплексный поиск с множественными фильтрами
    public `List`<`Book`> `findBooksWithComplexFilters`(`BookSearchCriteria` criteria) {
        `Criteria mongoCriteria` = new `Criteria`();

        // Динамическое построение критериев
        if (criteria.`getMinRating`() != `null`) {
            `mongoCriteria`.and("`averageRating`").gte(criteria.`getMinRating`());
        }
        if (criteria.`getMaxPrice`() != `null`) {
            `mongoCriteria`.and("price").lte(criteria.`getMaxPrice`());
        }
        if (criteria.`getMinPages`() != `null`) {
            `mongoCriteria`.and("pages").gte(criteria.`getMinPages`());
        }
        if (criteria.`getTags`() != `null` && !criteria.`getTags`().`isEmpty`()) {
            `mongoCriteria`.and("tags").in(criteria.`getTags`());
        }

        `Query query` = `Query`.query(`mongoCriteria`);
        `query.with`(`Sort`.by(`Sort`.`Order`.desc("`averageRating`")));

        return `mongoTemplate`.find(query, `Book`.class);
    }
}
```

### Запросы с вложенными условиями

```java
`@Service`
public class `NestedQueryService` {

    `@Autowired`
    private `MongoTemplate mongoTemplate`;

    // Книги с определенными характеристиками автора
    public `List`<`Book`> `findBooksByAuthorCriteria`() {
        `Criteria authorCriteria` = new `Criteria`().`andOperator`(
            `Criteria`.where("country").is("`USA`"),
            `Criteria`.where("`experienceYears`").gte(10)
        );

        `Criteria publisherCriteria` = `Criteria`.where("`publisher.rating`").gte(`4.5`);

        `Criteria combinedCriteria` = new `Criteria`().`andOperator`(
            `Criteria`.where("author").`elemMatch`(`authorCriteria`),
            `publisherCriteria`
        );

        return `mongoTemplate`.find(`Query`.query(`combinedCriteria`), `Book`.class);
    }

    // Запросы с глубоким вложением
    public `List`<`Book`> `findBooksWithNestedConditions`() {
        // Автор из США с опытом > 10 лет И издатель с рейтингом > `4.5`
        `Criteria authorMatch` = `Criteria`.where("author").`elemMatch`(
            new `Criteria`().`andOperator`(
                `Criteria`.where("country").is("`USA`"),
                `Criteria`.where("`experienceYears`").gte(10)
            )
        );

        `Criteria publisherMatch` = `Criteria`.where("`publisher.rating`").gte(`4.5`);

        `Criteria finalCriteria` = new `Criteria`().`andOperator`(`authorMatch`, `publisherMatch`);

        return `mongoTemplate`.find(`Query`.query(`finalCriteria`), `Book`.class);
    }
}
```

### Геопространственные запросы с фильтрами

```java
`@Service`
public class `RestaurantGeoService` {

    `@Autowired`
    private `MongoTemplate mongoTemplate`;

    // Найти рестораны в радиусе с определенной кухней
    public `List`<`Restaurant`> `findNearbyRestaurants`(double longitude, double latitude) {
        `Point referencePoint` = new `Point`(longitude, latitude);

        `Criteria locationCriteria` = `Criteria`.where("location")
            .near(`referencePoint`)
            .`maxDistance`(`2000`); // 2km

        `Criteria cuisineCriteria` = `Criteria`.where("cuisine")
            .in("`Italian`", "`Chinese`");

        `Criteria ratingCriteria` = `Criteria`.where("rating").gte(`4.0`);

        `Criteria priceCriteria` = `Criteria`.where("`priceRange`").lte(3);

        `Criteria combinedCriteria` = new `Criteria`().`andOperator`(
            `locationCriteria`,
            `cuisineCriteria`,
            `ratingCriteria`,
            `priceCriteria`
        );

        `Query query` = `Query`.query(`combinedCriteria`);
        `query.with`(`Sort`.by(`Sort`.`Order`.desc("rating")));

        return `mongoTemplate`.find(query, `Restaurant`.class);
    }

    // Комплексный гео-запрос с агрегацией
    public `List`<`Document`> `findRestaurantsWithAggregatedData`() {
        `Point timesSquare` = new `Point`(-73.9857, `40.7484`);

        `Aggregation aggregation` = `Aggregation`.`newAggregation`(
            // Фильтр по расстоянию
            `Aggregation`.match(`Criteria`.where("location")
                .near(`timesSquare`).`maxDistance`(`2000`)),

            // Фильтр по кухне и рейтингу
            `Aggregation`.match(`Criteria`.where("cuisine").in("`Italian`", "`Chinese`")
                .and("rating").gte(`4.0`)),

            // Группировка по кухне
            `Aggregation`.group("cuisine")
                .count().as("`restaurantCount`")
                .avg("rating").as("`avgRating`")
                .min("`priceRange`").as("`minPriceRange`")
                .max("`priceRange`").as("`maxPriceRange`"),

            // Сортировка
            `Aggregation`.sort(`Sort`.by(`Sort`.`Direction`.`DESC`, "`restaurantCount`"))
        );

        return `mongoTemplate`.aggregate(aggregation, "restaurants", `Document`.class)
                          .`getMappedResults`();
    }
}
```

MongoDB предоставляет мощный и гибкий язык запросов, позволяющий выполнять сложные операции с документами. Ключевые особенности:

### Основные возможности:

1. Гибкие критерии поиска с поддержкой различных операторов
2. Работа с массивами и вложенными объектами
3. Текстовый поиск с индексами
4. Геопространственные запросы для location-based данных
5. Регулярные выражения для pattern matching
6. Проекции и трансформации результатов

### Оптимизация запросов:

- Использование индексов для ускорения поиска
- Правильные проекции для уменьшения объема данных
- Оптимальные критерии для эффективного выполнения
- Мониторинг производительности через explain()

### Лучшие практики:

1. Проектируйте запросы с учетом доступных индексов
2. Используйте проекции для выборки только нужных полей
3. Оптимизируйте порядок условий в сложных запросах
4. Мониторьте производительность и используйте explain()
5. Кэшируйте часто используемые результаты запросов

MongoDB queries сочетают простоту использования с мощными возможностями для работы с большими объемами данных.

## Лучшие практики для запросов

### Производительность

1. Создавайте индексы для часто используемых запросов
2. Используйте проекции для выборки только нужных полей
3. Оптимизируйте условия запросов
4. Мониторьте explain планы выполнения

### Безопасность

1. Валидируйте входные данные перед построением запросов
2. Избегайте инъекций через параметризацию
3. Ограничивайте права доступа к коллекциям
4. Логируйте подозрительные запросы

### Поддерживаемость

1. Используйте понятные имена полей и операторов
2. Комментируйте сложные запросы
3. Тестируйте запросы на разных объемах данных
4. Документируйте логику запросов

## Заключение

Система запросов MongoDB предоставляет мощные возможности для поиска и фильтрации документов:

### Основные категории операторов:
- Сравнение: `$eq`, `$gt`, `$lt`, `$gte`, `$lte`, `$ne`
- Логические: `$and`, `$or`, `$nor`, `$not`
- Массивы: `$in`, `$nin`, `$all`, `$size`, `$elemMatch`
- Элементы: `$exists`, `$type`
- Оценка: `$regex`, `$mod`, `$where`, `$expr`
- Геопространственные: `$near`, `$geoWithin`, `$geoIntersects`

### Ключевые принципы:
- Гибкость: Поддержка сложных вложенных условий
- Производительность: Оптимизация через индексы
- Масштабируемость: Эффективная работа с большими коллекциями
- JSON-подобный синтаксис: Удобство для разработчиков

## Решение проблем

**Медленные запросы (COLLSCAN):** создайте индексы для полей в `find()` (фильтры, сортировка). Используйте `explain()` и проверьте использование индекса. Избегайте регулярных выражений с начальным wildcard и отрицаний по неиндексированным полям.

**Таймауты (cursor not found / maxTimeMS):** увеличьте таймаут для тяжёлых запросов, но в первую очередь оптимизируйте: ограничьте `limit()`, используйте проекцию только нужных полей, разбейте на несколько запросов. Для больших выборок используйте курсор с batchSize.

**Ошибки матчинга или пустые результаты:** проверьте типы полей (например, строка vs число), вложенность документов и имена полей (регистр, BSON типы). Для массивов используйте корректные операторы (`$elemMatch`, `$in`). Учитывайте, что запрос по несуществующему полю не матчит документы без этого поля.

**Проблемы с агрегацией (memory / timeout):** ограничьте объём данных на стадиях `$match` и `$group`, используйте `allowDiskUse: true` при необходимости. Упростите пайплайн или разбейте на несколько агрегаций.

---

Следующие темы:
- [Индексы](mongodb-indexes.md)
- [Aggregation Framework](mongodb-aggregation.md)


