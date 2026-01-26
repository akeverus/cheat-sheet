# kotlinx.datetime

kotlinx.datetime - это библиотека для работы с датами и временем в Kotlin. Предоставляет современный, type-safe API для манипуляции датами, временем и временными интервалами. Полностью совместима с Java Time API, но предоставляет более идиоматичный Kotlin интерфейс.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [kotlinx.datetime](https://github.com/Kotlin/kotlinx-datetime) - GitHub репозиторий
- [kotlinx.datetime Guide](https://github.com/Kotlin/kotlinx-datetime/blob/master/README.md) - Руководство
- [kotlinx.datetime API](https://kotlinlang.org/api/kotlinx-datetime/) - API документация

### См. также
- `../languages/kotlin/kotlin-datetime.md` - Работа с датами в Kotlin
- `../java/java-time-api.md` - Java Time API

## Содержание

- [Основные возможности](#основные-возможности)
  - [Instant (момент времени)](#instant-момент-времени)
  - [LocalDateTime (локальная дата и время)](#localdatetime-локальная-дата-и-время)
  - [LocalDate (только дата)](#localdate-только-дата)
  - [LocalTime (только время)](#localtime-только-время)
  - [TimeZone и ZonedDateTime](#timezone-и-zoneddatetime)
  - [OffsetDateTime](#offsetdatetime)
  - [Duration и Period](#duration-и-period)
  - [DateTimePeriod и DateTimeArithmetic](#datetimeperiod-и-datetimearithmetic)
- [Форматирование и парсинг](#форматирование-и-парсинг)
  - [ISO форматы](#iso-форматы)
  - [Кастомное форматирование](#кастомное-форматирование)
  - [Преопределенные форматы](#преопределенные-форматы)
- [Продвинутые операции](#продвинутые-операции)
  - [Calendar operations](#calendar-operations)
  - [Итерация по датам](#итерация-по-датам)
  - [Конвертация LocalDateTime в Instant и обратно](#конвертация-localdatetime-в-instant-и-обратно)
  - [Работа с устаревшими типами Java](#работа-с-устаревшими-типами-java)
- [Практические примеры](#практические-примеры)
  - [Вычисление разницы между датами](#вычисление-разницы-между-датами)
  - [Работа с временными диапазонами](#работа-с-временными-диапазонами)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration Properties](#configuration-properties)
  - [Service Layer с datetime](#service-layer-с-datetime)
  - [REST Controller с datetime](#rest-controller-с-datetime)
- [Testing](#testing)
  - [Unit Testing с datetime](#unit-testing-с-datetime)
  - [Testing с фиксированным временем](#testing-с-фиксированным-временем)
  - [Integration Testing](#integration-testing)
- [Best Practices](#best-practices)
  - [Immutable DateTime Operations](#immutable-datetime-operations)
  - [Null Safety с datetime](#null-safety-с-datetime)
  - [Time Zone Handling](#time-zone-handling)
  - [Validation Patterns](#validation-patterns)
- [Performance Optimization](#performance-optimization)
  - [Caching parsed patterns](#caching-parsed-patterns)
  - [Avoid excessive conversions](#avoid-excessive-conversions)

## Основные возможности

### Instant (момент времени)
```kotlin
import kotlinx.datetime.*

// Создание Instant
val now = Instant.now()
val fromEpoch = Instant.fromEpochMilliseconds(1640995200000) // 2022-01-01 00:00:00 UTC
val fromString = Instant.parse("2022-01-01T00:00:00Z")

// Преобразования
val epochMillis = now.toEpochMilliseconds()
val epochSeconds = now.epochSeconds

// Арифметика
val future = now.plus(1, DateTimeUnit.HOUR)
val past = now.minus(30, DateTimeUnit.MINUTE)

// Сравнение
val isBefore = now < future
val duration = future - now
```

### LocalDateTime (локальная дата и время)
```kotlin
// Создание LocalDateTime
val now = LocalDateTime.now()
val specific = LocalDateTime(2022, 12, 25, 15, 30, 45, 123_456_789)

// Парсинг
val parsed = LocalDateTime.parse("2022-12-25T15:30:45.123456789")

// Компоненты
val year = now.year
val month = now.month
val day = now.dayOfMonth
val hour = now.hour
val minute = now.minute
val second = now.second
val nanosecond = now.nanosecond

// Модификация
val tomorrow = now.plus(1, DateTimeUnit.DAY)
val nextHour = now.plus(1, DateTimeUnit.HOUR)

// Сравнение
val isAfter = now > tomorrow
```

### LocalDate (только дата)
```kotlin
// Создание LocalDate
val today = LocalDate.now()
val birthday = LocalDate(1990, 5, 15)
val parsed = LocalDate.parse("2022-12-25")

// Свойства
val year = today.year
val month = today.month
val day = today.dayOfMonth

// Операции
val tomorrow = today.plus(1, DateTimeUnit.DAY)
val yesterday = today.minus(1, DateTimeUnit.DAY)
val nextWeek = today.plus(7, DateTimeUnit.DAY)

// День недели
val dayOfWeek = today.dayOfWeek // MONDAY, TUESDAY, etc.

// День года
val dayOfYear = today.dayOfYear

// Проверка високосного года
val isLeapYear = today.isLeapYear()
```

### LocalTime (только время)
```kotlin
// Создание LocalTime
val now = LocalTime.now()
val lunch = LocalTime(12, 30, 45, 123_456_789)
val parsed = LocalTime.parse("15:30:45.123456789")

// Компоненты
val hour = now.hour
val minute = now.minute
val second = now.second
val nanosecond = now.nanosecond

// Операции
val later = now.plus(30, DateTimeUnit.MINUTE)
val earlier = now.minus(15, DateTimeUnit.MINUTE)

// Сравнение
val isBeforeLunch = now < lunch
```

## Продвинутые возможности

### TimeZone и ZonedDateTime
```kotlin
// Работа с часовыми поясами
val utc = TimeZone.UTC
val paris = TimeZone.of("Europe/Paris")
val tokyo = TimeZone.of("Asia/Tokyo")

// Текущий часовой пояс системы
val systemTz = TimeZone.currentSystemDefault()

// ZonedDateTime
val now = ZonedDateTime.now()
val parisTime = now.withZoneSameInstant(paris)
val tokyoTime = now.withZoneSameLocal(tokyo)

// Преобразования
val instant = now.toInstant()
val localDateTime = now.toLocalDateTime()

// Создание ZonedDateTime
val zoned = LocalDateTime(2022, 12, 25, 15, 30).atZone(paris)
```

### OffsetDateTime
```kotlin
// OffsetDateTime с фиксированным смещением
val utcTime = OffsetDateTime.now()
val offsetTime = OffsetDateTime.now(paris) // с смещением Paris

// Создание с конкретным смещением
val utcOffset = UtcOffset(2, 0) // +02:00
val offsetDateTime = LocalDateTime(2022, 12, 25, 15, 30).atOffset(utcOffset)

// Преобразования
val instant = offsetDateTime.toInstant()
val localDateTime = offsetDateTime.toLocalDateTime()
val offset = offsetDateTime.offset
```

### Duration и Period
```kotlin
// Duration для точных временных интервалов
val duration1 = Duration.parse("PT1H30M") // 1 час 30 минут
val duration2 = Duration.ofHours(2) + Duration.ofMinutes(30)
val duration3 = 1.hours + 30.minutes // Kotlin DSL

// Операции с Duration
val total = duration1 + duration2
val half = duration1 / 2
val isPositive = duration1.isPositive()

// Преобразования
val seconds = duration1.inWholeSeconds
val millis = duration1.inWholeMilliseconds
val nanos = duration1.inWholeNanoseconds

// Period для календарных интервалов
val period1 = Period.parse("P1Y2M3D") // 1 год 2 месяца 3 дня
val period2 = Period.of(1, 6, 15) // 1 год 6 месяцев 15 дней

// Комбинация Duration и Period
val dateTime = LocalDateTime.now()
val futureDateTime = dateTime + period1 + duration1
```

### DateTimePeriod и DateTimeArithmetic
```kotlin
// DateTimePeriod для комплексных периодов
val period = DateTimePeriod(
    years = 1,
    months = 2,
    days = 3,
    hours = 4,
    minutes = 5,
    seconds = 6,
    nanoseconds = 7_000_000
)

// Применение к различным типам
val instant = Instant.now()
val newInstant = instant + period

val localDateTime = LocalDateTime.now()
val newLocalDateTime = localDateTime + period

val zonedDateTime = ZonedDateTime.now()
val newZonedDateTime = zonedDateTime + period
```

## Форматирование и парсинг

### ISO форматы
```kotlin
// ISO форматы (по умолчанию)
val instant = Instant.now()
val isoString = instant.toString() // 2022-12-25T15:30:45.123456789Z

val parsedInstant = Instant.parse(isoString)

val localDate = LocalDate.now()
val dateString = localDate.toString() // 2022-12-25

val parsedDate = LocalDate.parse(dateString)
```

### Кастомное форматирование
```kotlin
import kotlinx.datetime.format.*

// Кастомный формат для LocalDateTime
val dateTimeFormat = LocalDateTime.Format {
    date(LocalDate.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
    })
    char(' ')
    time(LocalTime.Format {
        hour()
        char(':')
        minute()
        char(':')
        second()
    })
}

val dateTime = LocalDateTime.now()
val formatted = dateTime.format(dateTimeFormat) // 2022-12-25 15:30:45

val parsed = LocalDateTime.parse(formatted, dateTimeFormat)

// Кастомный формат для даты
val germanDateFormat = LocalDate.Format {
    dayOfMonth()
    char('.')
    monthNumber()
    char('.')
    year()
}

val germanDate = LocalDate.now().format(germanDateFormat) // 25.12.2022
```

### Преопределенные форматы
```kotlin
// ISO форматы
val isoInstantFormat = Instant.Format.ISO
val isoDateFormat = LocalDate.Format.ISO
val isoTimeFormat = LocalTime.Format.ISO

// Форматы с различной точностью
val dateFormatDMY = LocalDate.Format {
    dayOfMonth()
    char('/')
    monthNumber()
    char('/')
    yearTwoDigits(2000) // 2000-2099
}

// Локализованные форматы
val usDateFormat = LocalDate.Format {
    monthNumber()
    char('/')
    dayOfMonth()
    char('/')
    year()
}
```

## Работа с календарем

### Calendar operations
```kotlin
val today = LocalDate.now()

// Начало и конец месяца
val startOfMonth = today.withDayOfMonth(1)
val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

// Начало и конец года
val startOfYear = today.withDayOfYear(1)
val endOfYear = today.withDayOfYear(today.lengthOfYear())

// День недели
val monday = today.previousOrSame(DayOfWeek.MONDAY)
val friday = today.nextOrSame(DayOfWeek.FRIDAY)

// Проверка типа дня
val isWeekend = today.dayOfWeek in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
val isWeekday = today.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

// Количество дней в месяце
val daysInMonth = today.lengthOfMonth()
val daysInYear = today.lengthOfYear()

// Проверка високосного года
val isLeap = today.isLeapYear()
```

### Итерация по датам
```kotlin
// Итерация по дням
val startDate = LocalDate(2022, 12, 20)
val endDate = LocalDate(2022, 12, 25)

val dates = generateSequence(startDate) { date ->
    if (date < endDate) date.plus(1, DateTimeUnit.DAY) else null
}.toList()

// Рабочие дни между датами
val workdays = generateSequence(startDate) { it.plus(1, DateTimeUnit.DAY) }
    .takeWhile { it <= endDate }
    .filter { it.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) }
    .toList()

// Ежемесячные даты
val monthlyDates = (1..12).map { month ->
    LocalDate(2022, month, 15) // 15-е число каждого месяца
}
```

## Конвертация между типами

### Конвертация LocalDateTime в Instant и обратно
```kotlin
// LocalDateTime -> Instant
val localDateTime = LocalDateTime(2022, 12, 25, 15, 30)
val instant = localDateTime.toInstant(TimeZone.UTC)

// Instant -> LocalDateTime
val backToLocal = instant.toLocalDateTime(TimeZone.currentSystemDefault())

// С учетом часового пояса
val zonedDateTime = localDateTime.atZone(TimeZone.of("Europe/Paris"))
val instantFromZoned = zonedDateTime.toInstant()
```

### Работа с устаревшими типами Java
```kotlin
import java.util.*
import java.time.*

// Конвертация из java.time
val instantFromJava = Instant.now().toKotlinInstant()
val localDateFromJava = LocalDate.now().toKotlinLocalDate()
val localDateTimeFromJava = LocalDateTime.now().toKotlinLocalDateTime()

// Конвертация в java.time
val javaInstant = instant.toJavaInstant()
val javaLocalDate = localDate.toJavaLocalDate()
val javaLocalDateTime = localDateTime.toJavaLocalDateTime()

// Работа с java.util.Date
val date = Date()
val instantFromDate = date.toInstant().toKotlinInstant()
val dateFromInstant = instant.toJavaInstant().toDate()
```

## Расчет временных интервалов

### Вычисление разницы между датами
```kotlin
val date1 = LocalDate(2022, 12, 25)
val date2 = LocalDate(2023, 1, 15)

// Разница в днях
val daysBetween = date1.daysUntil(date2) // 21

// Разница в периодах
val period = date1.periodUntil(date2) // P21D
val years = period.years
val months = period.months
val days = period.days

// Для времени
val time1 = LocalTime(10, 30)
val time2 = LocalTime(15, 45)
val minutesBetween = time1.minutesUntil(time2) // 315
```

### Работа с временными диапазонами
```kotlin
// DateRange
val startDate = LocalDate(2022, 12, 1)
val endDate = LocalDate(2022, 12, 31)
val dateRange = startDate..endDate

val decemberDays = dateRange.map { it.dayOfMonth }
val workDays = dateRange.filter { it.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) }

// TimeRange
val startTime = LocalTime(9, 0)
val endTime = LocalTime(17, 0)
val timeRange = startTime..endTime

// Проверка вхождения
val checkDate = LocalDate(2022, 12, 15)
val isInDecember = checkDate in dateRange

val checkTime = LocalTime(14, 30)
val isWorkingHours = checkTime in timeRange
```

## Spring Boot Integration

### Configuration Properties
```kotlin
@ConfigurationProperties("app.scheduling")
data class SchedulingConfig(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val timeZone: String,
    val workDays: List<DayOfWeek>
) {
    fun getTimeZone(): TimeZone = TimeZone.of(timeZone)

    fun isWorkingTime(dateTime: LocalDateTime): Boolean {
        val dayOfWeek = dateTime.dayOfWeek
        val time = dateTime.toLocalTime()

        return dayOfWeek in workDays && time in startTime..endTime
    }
}
```

### Service Layer с datetime
```kotlin
@Service
class EventService(
    private val eventRepository: EventRepository
) {

    fun createEvent(request: CreateEventRequest): Event {
        val now = Instant.now()

        // Валидация дат
        require(request.startTime > now) { "Event cannot start in the past" }
        require(request.endTime > request.startTime) { "End time must be after start time" }

        val event = Event(
            id = 0, // будет сгенерировано БД
            title = request.title,
            description = request.description,
            startTime = request.startTime,
            endTime = request.endTime,
            createdAt = now,
            updatedAt = now
        )

        return eventRepository.save(event)
    }

    fun getUpcomingEvents(): List<Event> {
        val now = Instant.now()
        val nextWeek = now.plus(7, DateTimeUnit.DAY)

        return eventRepository.findByStartTimeBetween(now, nextWeek)
    }

    fun getEventsForDate(date: LocalDate): List<Event> {
        val startOfDay = date.atStartOfDayIn(TimeZone.UTC).toInstant()
        val endOfDay = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.UTC).toInstant()

        return eventRepository.findByStartTimeBetween(startOfDay, endOfDay)
    }

    fun calculateEventDuration(event: Event): Duration {
        return event.endTime - event.startTime
    }

    fun isEventActive(event: Event): Boolean {
        val now = Instant.now()
        return now in event.startTime..event.endTime
    }
}
```

### REST Controller с datetime
```kotlin
@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService
) {

    @PostMapping
    fun createEvent(@RequestBody request: CreateEventRequest): ResponseEntity<EventResponse> {
        try {
            val event = eventService.createEvent(request)
            val response = EventResponse.from(event)
            return ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/upcoming")
    fun getUpcomingEvents(): List<EventResponse> {
        return eventService.getUpcomingEvents()
            .map { EventResponse.from(it) }
    }

    @GetMapping("/date/{date}")
    fun getEventsForDate(@PathVariable date: String): List<EventResponse> {
        val localDate = LocalDate.parse(date)
        return eventService.getEventsForDate(localDate)
            .map { EventResponse.from(it) }
    }

    @GetMapping("/{id}/duration")
    fun getEventDuration(@PathVariable id: Long): ResponseEntity<String> {
        val event = eventService.getEvent(id) ?: return ResponseEntity.notFound().build()

        val duration = eventService.calculateEventDuration(event)
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60

        return ResponseEntity.ok("${hours}h ${minutes}m")
    }
}

data class CreateEventRequest(
    val title: String,
    val description: String?,
    val startTime: Instant,
    val endTime: Instant
)

data class EventResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startTime: Instant,
    val endTime: Instant,
    val duration: String,
    val isActive: Boolean
) {
    companion object {
        fun from(event: Event) = EventResponse(
            id = event.id,
            title = event.title,
            description = event.description,
            startTime = event.startTime,
            endTime = event.endTime,
            duration = "${event.endTime - event.startTime}",
            isActive = Instant.now() in event.startTime..event.endTime
        )
    }
}
```

## Testing

### Unit Testing с datetime
```kotlin
class EventServiceTest {

    private val eventRepository = mockk<EventRepository>()
    private lateinit var eventService: EventService

    @BeforeEach
    fun setUp() {
        eventService = EventService(eventRepository)
    }

    @Test
    fun `should create event successfully`() {
        val startTime = Instant.now().plus(1, DateTimeUnit.HOUR)
        val endTime = startTime.plus(2, DateTimeUnit.HOUR)

        val request = CreateEventRequest(
            title = "Test Event",
            description = "Test Description",
            startTime = startTime,
            endTime = endTime
        )

        val savedEvent = Event(
            id = 1,
            title = request.title,
            description = request.description,
            startTime = request.startTime,
            endTime = request.endTime,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        every { eventRepository.save(any()) } returns savedEvent

        val result = eventService.createEvent(request)

        assertEquals(savedEvent, result)
        verify { eventRepository.save(any()) }
    }

    @Test
    fun `should throw exception for past event`() {
        val pastTime = Instant.now().minus(1, DateTimeUnit.HOUR)

        val request = CreateEventRequest(
            title = "Past Event",
            description = null,
            startTime = pastTime,
            endTime = pastTime.plus(1, DateTimeUnit.HOUR)
        )

        assertThrows<IllegalArgumentException> {
            eventService.createEvent(request)
        }

        verify(exactly = 0) { eventRepository.save(any()) }
    }

    @Test
    fun `should calculate event duration correctly`() {
        val startTime = Instant.parse("2022-12-25T10:00:00Z")
        val endTime = Instant.parse("2022-12-25T12:30:00Z")

        val event = Event(
            id = 1,
            title = "Test",
            description = null,
            startTime = startTime,
            endTime = endTime,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val duration = eventService.calculateEventDuration(event)
        assertEquals(2.hours + 30.minutes, duration)
    }
}
```

### Testing с фиксированным временем
```kotlin
class TimeBasedServiceTest {

    private val fixedTime = Instant.parse("2022-12-25T12:00:00Z")

    @Test
    fun `should handle time-based logic with fixed time`() {
        // Используем фиксированное время вместо Clock
        val pastEvent = createEvent(
            startTime = fixedTime.minus(2, DateTimeUnit.HOUR),
            endTime = fixedTime.minus(1, DateTimeUnit.HOUR)
        )

        val currentEvent = createEvent(
            startTime = fixedTime.minus(30, DateTimeUnit.MINUTE),
            endTime = fixedTime.plus(30, DateTimeUnit.MINUTE)
        )

        val futureEvent = createEvent(
            startTime = fixedTime.plus(1, DateTimeUnit.HOUR),
            endTime = fixedTime.plus(2, DateTimeUnit.HOUR)
        )

        // Проверяем статус событий
        assertFalse(eventService.isEventActive(pastEvent))
        assertTrue(eventService.isEventActive(currentEvent))
        assertFalse(eventService.isEventActive(futureEvent))
    }

    private fun createEvent(startTime: Instant, endTime: Instant) = Event(
        id = 1,
        title = "Test Event",
        description = null,
        startTime = startTime,
        endTime = endTime,
        createdAt = fixedTime,
        updatedAt = fixedTime
    )
}
```

### Integration Testing
```kotlin
@SpringBootTest
@Testcontainers
class DateTimeIntegrationTest {

    @Container
    private val postgres = PostgreSQLContainer<Nothing>("postgres:13").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
    }

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Test
    fun `should handle datetime operations with database`() {
        val now = Instant.now()
        val futureTime = now.plus(1, DateTimeUnit.DAY)

        val event = Event(
            id = 0,
            title = "Database Test Event",
            description = "Testing datetime with database",
            startTime = now,
            endTime = futureTime,
            createdAt = now,
            updatedAt = now
        )

        val saved = eventRepository.save(event)
        val retrieved = eventRepository.findById(saved.id!!)

        assertNotNull(retrieved)
        assertEquals(event.title, retrieved.get().title)
        assertEquals(event.startTime, retrieved.get().startTime)
        assertEquals(event.endTime, retrieved.get().endTime)
    }

    @Test
    fun `should query events by date range`() {
        val baseTime = Instant.parse("2022-12-25T00:00:00Z")

        val events = listOf(
            createEvent(baseTime.plus(1, DateTimeUnit.HOUR)),
            createEvent(baseTime.plus(2, DateTimeUnit.HOUR)),
            createEvent(baseTime.plus(3, DateTimeUnit.HOUR))
        )

        eventRepository.saveAll(events)

        val startRange = baseTime.plus(30, DateTimeUnit.MINUTE)
        val endRange = baseTime.plus(3, DateTimeUnit.HOUR).plus(30, DateTimeUnit.MINUTE)

        val foundEvents = eventRepository.findByStartTimeBetween(startRange, endRange)

        assertEquals(3, foundEvents.size)
    }

    private fun createEvent(startTime: Instant) = Event(
        id = 0,
        title = "Test Event",
        description = null,
        startTime = startTime,
        endTime = startTime.plus(1, DateTimeUnit.HOUR),
        createdAt = startTime,
        updatedAt = startTime
    )
}
```

## Best Practices

### Immutable DateTime Operations
```kotlin
// Предпочитайте immutable операции
val original = LocalDateTime.now()

// Правильно: создает новый объект
val modified = original.plus(1, DateTimeUnit.DAY)

// Неправильно: мутация (если бы была доступна)
val wrong = original.apply { /* mutation */ }
```

### Null Safety с datetime
```kotlin
// Используйте nullable типы аккуратно
data class Event(
    val id: Long,
    val startTime: Instant?,
    val endTime: Instant?
) {
    fun getDuration(): Duration? {
        return if (startTime != null && endTime != null) {
            endTime - startTime
        } else {
            null
        }
    }

    fun isValid(): Boolean {
        return startTime != null && endTime != null && endTime > startTime
    }
}

// Безопасная работа с nullable datetime
fun processEvent(event: Event?) {
    event?.takeIf { it.isValid() }?.let { validEvent ->
        val duration = validEvent.getDuration()
        println("Event duration: $duration")
    } ?: println("Invalid or null event")
}
```

### Time Zone Handling
```kotlin
// Всегда явно указывайте timezone
object TimeZoneHelper {

    private val systemTz = TimeZone.currentSystemDefault()
    private val utc = TimeZone.UTC

    fun toSystemTime(instant: Instant): LocalDateTime =
        instant.toLocalDateTime(systemTz)

    fun toUtcTime(localDateTime: LocalDateTime): Instant =
        localDateTime.toInstant(utc)

    fun convertTimeZone(dateTime: LocalDateTime, from: TimeZone, to: TimeZone): LocalDateTime {
        val instant = dateTime.toInstant(from)
        return instant.toLocalDateTime(to)
    }

    fun getBusinessHours(timeZone: TimeZone): Pair<LocalTime, LocalTime> {
        val now = LocalDateTime.now(timeZone)
        return LocalTime(9, 0) to LocalTime(17, 0)
    }
}
```

### Validation Patterns
```kotlin
object DateTimeValidators {

    fun isFuture(instant: Instant): Boolean =
        instant > Instant.now()

    fun isPast(instant: Instant): Boolean =
        instant < Instant.now()

    fun isWithinRange(instant: Instant, start: Instant, end: Instant): Boolean =
        instant in start..end

    fun isWorkingDay(date: LocalDate): Boolean =
        date.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

    fun isBusinessHours(dateTime: LocalDateTime, timeZone: TimeZone): Boolean {
        val (start, end) = TimeZoneHelper.getBusinessHours(timeZone)
        val time = dateTime.toLocalTime()
        return time in start..end && isWorkingDay(dateTime.toLocalDate())
    }

    fun validateEventTimes(startTime: Instant, endTime: Instant): ValidationResult {
        val errors = mutableListOf<String>()

        if (endTime <= startTime) {
            errors.add("End time must be after start time")
        }

        if (startTime < Instant.now()) {
            errors.add("Start time cannot be in the past")
        }

        val duration = endTime - startTime
        if (duration > 24.hours) {
            errors.add("Event cannot be longer than 24 hours")
        }

        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}
```

## Performance Considerations

### Caching parsed patterns
```kotlin
object DateTimeFormatters {

    // Cache formatters для повторного использования
    val isoDateFormatter = LocalDate.Format { byUnicodePattern("yyyy-MM-dd") }
    val isoDateTimeFormatter = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd'T'HH:mm:ss") }
    val customFormatter = LocalDateTime.Format {
        date(LocalDate.Format {
            dayOfMonth(); char('.'); monthNumber(); char('.'); year()
        })
        char(' ')
        time(LocalTime.Format {
            hour(); char(':'); minute()
        })
    }

    // Extension функции для удобства
    fun LocalDate.formatGerman(): String = format(customFormatter.dateFormat)
    fun LocalDateTime.formatGerman(): String = format(customFormatter)
}

// Использование
val date = LocalDate.now()
val germanDate = date.formatGerman()
```

### Avoid excessive conversions
```kotlin
// Плохо: множественные конвертации
fun badExample(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
    val localDate = localDateTime.toLocalDate()
    return localDate.toString()
}

// Хорошо: минимальные конвертации
fun goodExample(timestamp: Long): String {
    return Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.UTC)
        .toLocalDate()
        .toString()
}

// Еще лучше: inline операции
fun bestExample(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.UTC)
    return dateTime.date.toString()
}
```

## Migration Guide

### From Java Time to kotlinx.datetime
```kotlin
// java.time.Instant
val javaInstant = java.time.Instant.now()
val kotlinInstant = javaInstant.toKotlinInstant()

// Обратно
val backToJava = kotlinInstant.toJavaInstant()

// java.time.LocalDate
val javaLocalDate = java.time.LocalDate.now()
val kotlinLocalDate = javaLocalDate.toKotlinLocalDate()

// java.time.LocalDateTime
val javaLocalDateTime = java.time.LocalDateTime.now()
val kotlinLocalDateTime = javaLocalDateTime.toKotlinLocalDateTime()

// java.time.ZonedDateTime
val javaZonedDateTime = java.time.ZonedDateTime.now()
val kotlinInstant = javaZonedDateTime.toInstant().toKotlinInstant()
val kotlinZonedDateTime = kotlinInstant.toZonedDateTime(TimeZone.currentSystemDefault())
```

### From ThreeTenABP to kotlinx.datetime
```kotlin
// ThreeTenABP (Android)
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate

val threeTenInstant = org.threeten.bp.Instant.now()
val kotlinInstant = Instant.fromEpochMilliseconds(threeTenInstant.toEpochMilli())

// Обратная конвертация
val backToThreeTen = org.threeten.bp.Instant.ofEpochMilli(kotlinInstant.toEpochMilliseconds())
```

### From Joda-Time to kotlinx.datetime
```kotlin
// Joda-Time
import org.joda.time.DateTime
import org.joda.time.LocalDate

val jodaDateTime = DateTime.now()
val kotlinInstant = Instant.fromEpochMilliseconds(jodaDateTime.millis)

// Обратная конвертация
val backToJoda = DateTime(kotlinInstant.toEpochMilliseconds())
```

## Troubleshooting

### Common Issues
```kotlin
object DateTimeTroubleshooting {

    // Проблема: Unexpected timezone conversion
    fun fixTimezoneIssue() {
        // Всегда явно указывайте timezone
        val instant = Instant.now()
        val utcDateTime = instant.toLocalDateTime(TimeZone.UTC) // Явно UTC
        val systemDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()) // Явно системный
    }

    // Проблема: Date parsing failures
    fun safeDateParsing(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            println("Failed to parse date: $dateString, error: ${e.message}")
            null
        }
    }

    // Проблема: Precision loss with milliseconds
    fun handleMillisecondsPrecisely() {
        val instant = Instant.now()

        // Сохраняем миллисекунды
        val millis = instant.toEpochMilliseconds()
        val reconstructed = Instant.fromEpochMilliseconds(millis)

        // Сохраняем наносекунды
        val seconds = instant.epochSeconds
        val nanos = instant.nanosecondsOfSecond
        val preciseReconstructed = Instant.fromEpochSeconds(seconds, nanos)
    }

    // Проблема: Invalid date calculations
    fun safeDateCalculations() {
        val date = LocalDate(2022, 2, 28)

        // Безопасное добавление месяцев
        val nextMonth = try {
            date.plus(1, DateTimeUnit.MONTH)
        } catch (e: Exception) {
            // Handle invalid date (e.g., Feb 31 -> Mar 3)
            date.plus(1, DateTimeUnit.MONTH).let { result ->
                if (result.dayOfMonth != date.dayOfMonth) {
                    // Date was adjusted, handle appropriately
                    result.withDayOfMonth(date.dayOfMonth.coerceAtMost(result.lengthOfMonth()))
                } else {
                    result
                }
            }
        }
    }

    // Проблема: Thread safety issues
    object ThreadSafeOperations {
        // DateTime объекты immutable и thread-safe
        val formatter = LocalDate.Format { byUnicodePattern("yyyy-MM-dd") }

        @Synchronized
        fun formatDate(date: LocalDate): String = date.format(formatter)
    }
}
```

### Debugging datetime
```kotlin
object DateTimeDebugger {

    fun logInstant(instant: Instant, label: String = "Instant") {
        println("$label: $instant")
        println("  Epoch seconds: ${instant.epochSeconds}")
        println("  Milliseconds: ${instant.toEpochMilliseconds()}")
        println("  UTC DateTime: ${instant.toLocalDateTime(TimeZone.UTC)}")
        println("  System DateTime: ${instant.toLocalDateTime(TimeZone.currentSystemDefault())}")
    }

    fun logLocalDateTime(dateTime: LocalDateTime, label: String = "LocalDateTime") {
        println("$label: $dateTime")
        println("  Date: ${dateTime.date}")
        println("  Time: ${dateTime.time}")
        println("  Day of week: ${dateTime.dayOfWeek}")
        println("  Day of year: ${dateTime.dayOfYear}")
    }

    fun logDuration(duration: Duration, label: String = "Duration") {
        println("$label: $duration")
        println("  Seconds: ${duration.inWholeSeconds}")
        println("  Milliseconds: ${duration.inWholeMilliseconds}")
        println("  Minutes: ${duration.inWholeMinutes}")
        println("  Hours: ${duration.inWholeHours}")
    }

    fun compareInstants(instant1: Instant, instant2: Instant, label1: String = "First", label2: String = "Second") {
        logInstant(instant1, label1)
        logInstant(instant2, label2)

        val difference = instant2 - instant1
        logDuration(difference, "Difference ($label2 - $label1)")

        println("Comparison: ${instant1.compareTo(instant2)}") // -1, 0, 1
        println("$label1 is before $label2: ${instant1 < instant2}")
        println("$label1 equals $label2: ${instant1 == instant2}")
    }
}

// Использование
val now = Instant.now()
val future = now.plus(1, DateTimeUnit.HOUR)

DateTimeDebugger.compareInstants(now, future, "Now", "Future")
```

## Experimental Features

### Kotlin 1.6+ Duration API
```kotlin
// Улучшенная поддержка Duration
val duration1 = 1.hours + 30.minutes
val duration2 = 90.seconds

val total = duration1 + duration2
val half = duration1 / 2

// Преобразования
val seconds = duration1.inWholeSeconds
val millis = duration1.inWholeMilliseconds
val minutes = duration1.inWholeMinutes

// Kotlin Duration DSL
val complexDuration = 2.days + 3.hours + 45.minutes + 30.seconds
```

### Contextual Time Operations
```kotlin
// Контекстуальные операции (предполагаемый API)
context(TimeZoneContext)
fun scheduleMeeting(time: LocalDateTime) {
    val instant = time.toInstant()
    // Конвертация происходит автоматически с использованием контекста
}

val meetingTime = LocalDateTime(2023, 1, 15, 14, 30)

TimeZone.UTC {
    scheduleMeeting(meetingTime) // Использует UTC
}

TimeZone.of("Europe/Paris") {
    scheduleMeeting(meetingTime) // Использует Paris timezone
}
```

### Advanced Calendar Operations
```kotlin
// Расширенные календарные операции (предполагаемый API)
val date = LocalDate(2023, 1, 15)

// Рабочие дни
val nextWorkDay = date.nextWorkDay() // Пропускает выходные
val workDaysInMonth = date.workDaysInMonth() // Количество рабочих дней

// Бизнес календарь
val businessCalendar = BusinessCalendar(
    holidays = setOf(LocalDate(2023, 1, 1), LocalDate(2023, 12, 25)),
    workWeek = WorkWeek.MONDAY_TO_FRIDAY
)

val isBusinessDay = businessCalendar.isBusinessDay(date)
val nextBusinessDay = businessCalendar.nextBusinessDay(date)
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация kotlinx.datetime](https://github.com/Kotlin/kotlinx-datetime)
- [Kotlin DateTime Guide](https://kotlinlang.org/api/kotlinx-datetime/)
- [Kotlin Blog - DateTime](https://blog.jetbrains.com/kotlin/2020/10/kotlinx-datetime-0-1-0-released/)
- [Migration from Java Time](https://github.com/Kotlin/kotlinx-datetime#migration-from-javatime)

## См. также
- [Kotlin Basics](kotlin-basics.md) - Основы Kotlin
- [Java Time API](java-basics.md) - Java Date/Time API
- [Spring Boot Time](spring-basics.md) - Spring Boot и время
