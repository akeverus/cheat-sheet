---
title: "Redis: Геопространственные данные"
description: "Полное руководство по работе с геопространственными данными в Redis: GEOADD, GEODIST, GEORADIUS, GEOSEARCH, use cases"
tags: ["redis", "geospatial", "geo", "coordinates", "location", "mapping"]
difficulty: "intermediate"
prerequisites: ["databases/redis-basics.md", "databases/redis-data-structures.md"]
next: []
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-data-structures.md"]
---

# **Redis**: Геопространственные данные

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Geospatial](https://redis.io/docs/data-types/geospatial/) — геоданные

### См. также
- [redis-basics.md](redis-basics.md) — основы Redis
- [redis-data-structures.md](redis-data-structures.md) — структуры данных

## Содержание

- [Введение в геопространственные данные](#введение-в-геопространственные-данные)
  - [Основные возможности](#основные-возможности)
- [Базовые операции](#базовые-операции)
  - [Добавление геоданных](#добавление-геоданных)
  - [Получение координат](#получение-координат)
  - [Расчет расстояния](#расчет-расстояния)
- [Поиск в радиусе](#поиск-в-радиусе)
  - [**GEORADIUS**](#georadius)
  - [**GEORADIUSBYMEMBER**](#georadiusbymember)
  - [**GEOSEARCH** (**Redis 6.2+**)](#geosearch-redis-6-2)
- [Геохэши](#геохэши)
  - [Получение геохэша](#получение-геохэша)
- [Программное использование](#программное-использование)
  - [**Java** пример](#java-пример)
- [**Use Cases**](#use-cases)
  - [Поиск ближайших объектов](#поиск-ближайших-объектов)
  - [**Delivery Route Optimization**](#delivery-route-optimization)
- [Лучшие практики](#лучшие-практики)
- [**Advanced Geospatial Operations**](#advanced-geospatial-operations)
  - [**Complex Queries**](#complex-queries)
  - [**Geospatial Indexing**](#geospatial-indexing)
  - [**Performance Optimization**](#performance-optimization)
  - [**Real-World Use Cases**](#real-world-use-cases)

## Введение в геопространственные данные

**Redis** поддерживает работу с географическими координатами через специальные команды, реализованные поверх **Sorted Sets**. Это позволяет эффективно хранить и запрашивать данные о местоположении.

### Основные возможности

- **Хранение координат**: Широта и долгота
- **Расчет расстояний**: Между точками
- **Поиск в радиусе**: Точки в определенном радиусе
- **Геохэши**: Для интеграции с другими системами

---

## Базовые операции

### Добавление геоданных

```redis
# Добавление точек с координатами
GEOADD cities 13.361389 38.115556 "Palermo"
GEOADD cities 15.087269 37.502669 "Catania"
GEOADD cities 12.52441 41.99123 "Rome"
GEOADD cities 2.15899 41.38879 "Barcelona"

# Множественное добавление
GEOADD cities \
  13.361389 38.115556 "Palermo" \
  15.087269 37.502669 "Catania" \
  12.52441 41.99123 "Rome"
```

### Получение координат

```redis
# Получение координат точки
GEOPOS cities Palermo
# Возвращает: ["13.36138933897018433", "38.11555639549629859"]

# Получение координат нескольких точек
GEOPOS cities Palermo Catania Rome
```

### Расчет расстояния

```redis
# Расстояние между двумя точками
GEODIST cities Palermo Catania
# Возвращает расстояние в метрах по умолчанию

# Расстояние в километрах
GEODIST cities Palermo Catania km

# Расстояние в милях
GEODIST cities Palermo Catania mi

# Расстояние в футах
GEODIST cities Palermo Catania ft
```

---

## Поиск в радиусе

### **GEORADIUS**

```redis
# Поиск точек в радиусе от координат
GEORADIUS cities 15 37 200 km

# С координатами
GEORADIUS cities 15 37 200 km WITHCOORD

# С расстояниями
GEORADIUS cities 15 37 200 km WITHDIST

# С координатами и расстояниями
GEORADIUS cities 15 37 200 km WITHCOORD WITHDIST

# С лимитом результатов
GEORADIUS cities 15 37 200 km COUNT 5

# Сортировка по расстоянию
GEORADIUS cities 15 37 200 km WITHDIST ASC
GEORADIUS cities 15 37 200 km WITHDIST DESC
```

### **GEORADIUSBYMEMBER**

```redis
# Поиск в радиусе от существующей точки
GEORADIUSBYMEMBER cities Palermo 200 km

# С координатами и расстояниями
GEORADIUSBYMEMBER cities Palermo 200 km WITHCOORD WITHDIST

# С лимитом
GEORADIUSBYMEMBER cities Palermo 200 km COUNT 10
```

### **GEOSEARCH** (**Redis 6.2+**)

```redis
# Поиск в прямоугольной области
GEOSEARCH cities FROMLONLAT 15 37 BYBOX 400 400 km

# Поиск в радиусе
GEOSEARCH cities FROMMEMBER Palermo BYRADIUS 200 km

# С сортировкой
GEOSEARCH cities FROMMEMBER Palermo BYRADIUS 200 km ASC
```

---

## Геохэши

### Получение геохэша

```redis
# Получение геохэша точки
GEOHASH cities Palermo
# Возвращает: ["sqc8b49rny0"]

# Геохэш можно использовать для поиска в других системах
# или для кэширования координат
```

---

## Программное использование

### **Java** пример

```java
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.GeoRadiusParam;

public class GeoRedisExample {
    private Jedis jedis;
    
    public GeoRedisExample(Jedis jedis) {
        this.jedis = jedis;
    }
    
    public void addLocation(String key, String name, double longitude, double latitude) {
        jedis.geoadd(key, longitude, latitude, name);
    }
    
    public List<GeoRadiusResponse> findNearby(String key, double longitude, 
                                             double latitude, double radiusKm, 
                                             int count) {
        GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
            .withDist()
            .withCoord()
            .count(count)
            .sortAscending();
        
        return jedis.georadius(key, longitude, latitude, radiusKm, 
                              GeoUnit.KM, param);
    }
}
```

---

## **Use Cases**

### Поиск ближайших объектов

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.GeoUnit;
import java.util.ArrayList;
import java.util.List;

public class NearbySearch {
    private JedisPool jedisPool;
    private String key = "locations";
    
    public NearbySearch(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public void addPlace(String placeId, double longitude, double latitude, String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.geoadd(key, longitude, latitude, placeId + ":" + name);
        }
    }
    
    public List<Place> findNearbyPlaces(double userLongitude, double userLatitude, double radiusKm) {
        try (Jedis jedis = jedisPool.getResource()) {
            GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                .withDist()
                .withCoord()
                .count(20)
                .sortAscending();
            
            List<GeoRadiusResponse> results = jedis.georadius(
                key,
                userLongitude,
                userLatitude,
                radiusKm,
                GeoUnit.KM,
                param
            );
            
            List<Place> places = new ArrayList<>();
            for (GeoRadiusResponse result : results) {
                String member = result.getMemberByString();
                String[] parts = member.split(":", 2);
                places.add(new Place(
                    parts[0],
                    parts[1],
                    result.getDistance(),
                    result.getCoordinate()
                ));
            }
            return places;
        }
    }
    
    public static class Place {
        private String id;
        private String name;
        private double distance;
        private redis.clients.jedis.GeoCoordinate coordinates;
        
        public Place(String id, String name, double distance, redis.clients.jedis.GeoCoordinate coordinates) {
            this.id = id;
            this.name = name;
            this.distance = distance;
            this.coordinates = coordinates;
        }
        // Getters...
    }
}
```

### **Delivery Route Optimization**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.GeoUnit;
import java.util.List;

public class DeliveryOptimizer {
    private JedisPool jedisPool;
    private String deliveriesKey = "deliveries";
    private String depotKey = "depots";
    
    public DeliveryOptimizer(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public void addDelivery(String deliveryId, double longitude, double latitude) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.geoadd(deliveriesKey, longitude, latitude, deliveryId);
        }
    }
    
    public DepotInfo findNearestDepot(double longitude, double latitude) {
        try (Jedis jedis = jedisPool.getResource()) {
            GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                .withDist()
                .count(1)
                .sortAscending();
            
            List<GeoRadiusResponse> results = jedis.georadius(
                depotKey,
                longitude,
                latitude,
                50,
                GeoUnit.KM,
                param
            );
            
            if (!results.isEmpty()) {
                GeoRadiusResponse result = results.get(0);
                return new DepotInfo(result.getMemberByString(), result.getDistance());
            }
            return null;
        }
    }
    
    public static class DepotInfo {
        private String depotId;
        private double distance;
        
        public DepotInfo(String depotId, double distance) {
            this.depotId = depotId;
            this.distance = distance;
        }
        // Getters...
    }
}
```

---

## Лучшие практики

1. **Используйте правильные единицы измерения** (**km, mi, m, ft**)
2. **Ограничивайте результаты** через **COUNT** для производительности
3. **Используйте сортировку** для получения ближайших объектов
4. **Кэшируйте результаты** для часто запрашиваемых местоположений
5. **Мониторьте производительность** геопространственных запросов

## **Advanced Geospatial Operations**

### **Complex Queries**

```redis
# Поиск с фильтрацией по дополнительным данным
# (требует комбинации с другими структурами данных)

# Пример: Поиск ресторанов в радиусе с рейтингом > 4
# 1. Найти рестораны в радиусе
GEORADIUS restaurants 15 37 5 km

# 2. Фильтровать по рейтингу через дополнительный Sorted Set
ZRANGEBYSCORE restaurant_ratings 4 +inf
```

### **Geospatial Indexing**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.GeoSearchParam;
import java.util.List;
import java.util.Map;

public class GeospatialIndex {
    private JedisPool jedisPool;
    
    public GeospatialIndex(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public void buildIndex(String key, List<Location> locations) {
        try (Jedis jedis = jedisPool.getResource()) {
            for (Location location : locations) {
                jedis.geoadd(key, location.getLongitude(), location.getLatitude(), location.getId());
            }
        }
    }
    
    public List<String> searchByRegion(double minLon, double minLat, double maxLon, double maxLat) {
        try (Jedis jedis = jedisPool.getResource()) {
            GeoSearchParam param = GeoSearchParam.geoSearchParam()
                .fromLonLat(minLon, minLat)
                .byBox(maxLon - minLon, maxLat - minLat, redis.clients.jedis.args.GeoUnit.KM);
            
            return jedis.geosearch("locations", param);
        }
    }
    
    public static class Location {
        private String id;
        private double longitude;
        private double latitude;
        
        // Constructors, getters...
    }
}
```

### **Performance Optimization**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.GeoUnit;
import com.google.gson.Gson;
import java.util.List;

public class OptimizedGeoSearch {
    private JedisPool jedisPool;
    private Gson gson;
    
    public OptimizedGeoSearch(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.gson = new Gson();
    }
    
    public List<GeoRadiusResponse> cachedSearch(double longitude, double latitude, 
                                               double radiusKm, int cacheTtl) {
        try (Jedis jedis = jedisPool.getResource()) {
            String cacheKey = String.format("geo:%.6f:%.6f:%.2f", longitude, latitude, radiusKm);
            
            // Проверить кэш
            String cached = jedis.get(cacheKey);
            if (cached != null) {
                return gson.fromJson(cached, 
                    new com.google.gson.reflect.TypeToken<List<GeoRadiusResponse>>(){}.getType());
            }
            
            // Выполнить поиск
            GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                .withDist()
                .withCoord();
            
            List<GeoRadiusResponse> results = jedis.georadius(
                "locations",
                longitude,
                latitude,
                radiusKm,
                GeoUnit.KM,
                param
            );
            
            // Сохранить в кэш
            jedis.setex(cacheKey, cacheTtl, gson.toJson(results));
            
            return results;
        }
    }
}
```

### **Real-World Use Cases**

#### **Location-Based Services**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.GeoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationService {
    private JedisPool jedisPool;
    
    public LocationService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public List<Service> findNearbyServices(double userLon, double userLat, 
                                           String serviceType, double radiusKm) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "services:" + serviceType;
            
            GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                .withDist()
                .withCoord()
                .count(20)
                .sortAscending();
            
            List<GeoRadiusResponse> results = jedis.georadius(
                key,
                userLon,
                userLat,
                radiusKm,
                GeoUnit.KM,
                param
            );
            
            List<Service> services = new ArrayList<>();
            for (GeoRadiusResponse result : results) {
                String serviceId = result.getMemberByString();
                double distance = result.getDistance();
                redis.clients.jedis.GeoCoordinate coords = result.getCoordinate();
                
                // Получить дополнительную информацию
                Map<String, String> info = jedis.hgetAll("service:" + serviceId);
                
                services.add(new Service(
                    serviceId,
                    info.get("name"),
                    distance,
                    coords,
                    info.get("rating")
                ));
            }
            
            return services;
        }
    }
    
    public static class Service {
        private String id;
        private String name;
        private double distance;
        private redis.clients.jedis.GeoCoordinate coordinates;
        private String rating;
        
        // Constructors, getters...
    }
}
```

#### **Delivery Optimization**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.GeoUnit;
import java.util.ArrayList;
import java.util.List;

public class DeliveryOptimizer {
    private JedisPool jedisPool;
    
    public DeliveryOptimizer(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public List<String> optimizeRoute(List<DeliveryPoint> deliveryPoints) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Добавить точки доставки
            for (DeliveryPoint point : deliveryPoints) {
                jedis.geoadd("deliveries", point.getLongitude(), point.getLatitude(), point.getId());
            }
            
            // Найти оптимальный маршрут (упрощенный алгоритм)
            List<String> route = new ArrayList<>();
            DeliveryPoint currentPoint = deliveryPoints.get(0);
            List<DeliveryPoint> remainingPoints = new ArrayList<>(deliveryPoints.subList(1, deliveryPoints.size()));
            
            while (!remainingPoints.isEmpty()) {
                // Найти ближайшую точку
                GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                    .withDist()
                    .count(1)
                    .sortAscending();
                
                List<GeoRadiusResponse> nearest = jedis.georadius(
                    "deliveries",
                    currentPoint.getLongitude(),
                    currentPoint.getLatitude(),
                    50,
                    GeoUnit.KM,
                    param
                );
                
                if (!nearest.isEmpty()) {
                    String nextPointId = nearest.get(0).getMemberByString();
                    route.add(nextPointId);
                    
                    // Обновить текущую точку
                    for (DeliveryPoint point : remainingPoints) {
                        if (point.getId().equals(nextPointId)) {
                            currentPoint = point;
                            remainingPoints.remove(point);
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            
            return route;
        }
    }
    
    public static class DeliveryPoint {
        private String id;
        private double longitude;
        private double latitude;
        
        // Constructors, getters...
    }
}
```

---

- [Redis Geospatial](https://redis.io/docs/data-types/geospatial/)

---

**Дата последнего обновления:** 2026-02-06


