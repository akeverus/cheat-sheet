# OptaPlanner

Руководство по использованию OptaPlanner для решения проблем планирования и оптимизации с минимальной настройкой.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [OptaPlanner Documentation](https://www.optaplanner.org/documentation/)
- [OptaPlanner GitHub](https://github.com/kiegroup/optaplanner)

### См. также
- [Жадные алгоритмы](./greedy-algorithms.md)
- [Алгоритмы оптимизации](./hill-climbing.md)

## Содержание

- [Обзор](#обзор)
- [Настройка проекта](#настройка-проекта)
- [Пример: Расписание лекций](#пример-расписание-лекций)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Использование решателя](#использование-решателя)
- [Полный пример](#полный-пример)

## Обзор

OptaPlanner решает проблемы планирования, используя набор алгоритмов с минимальной настройкой. Хотя понимание алгоритмов может дать полезную информацию, инфраструктура выполняет за нас тяжелую работу.

OptaPlanner - это мощный инструмент для решения проблем удовлетворения ограничений, таких как планирование и распределение ресурсов.

## Настройка проекта

Во-первых, мы добавим зависимость Maven для OptaPlanner:

```xml
<dependency>
    <groupId>org.optaplanner</groupId>
    <artifactId>optaplanner-core</artifactId>
    <version>8.24.0.Final</version>
</dependency>
```

## Пример: Расписание лекций

Для решения задачи нам обязательно нужен конкретный пример. Расписание лекций является подходящим примером из-за сложности балансировки ресурсов, таких как помещения, время и учителя.

## Java Implementation

### Класс решения (PlanningSolution)

CourseSchedule содержит комбинацию наших проблемных переменных и объектов планирования, следовательно, это класс решения. В результате мы используем несколько аннотаций для его настройки:

```java
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class CourseSchedule {
    
    @ValueRangeProvider(id = "availableRooms")
    @ProblemFactCollectionProperty
    private List<Integer> roomList;
    
    @ValueRangeProvider(id = "availablePeriods")
    @ProblemFactCollectionProperty
    private List<Integer> periodList;
    
    @ProblemFactCollectionProperty
    private List<Lecture> lectureList;
    
    @PlanningScore
    private HardSoftScore score;
    
    // Геттеры и сеттеры
    @PlanningEntityCollectionProperty
    public List<Lecture> getLectureList() {
        return lectureList;
    }
    
    @ValueRangeProvider(id = "availableRooms")
    @ProblemFactCollectionProperty
    public List<Integer> getRoomList() {
        return roomList;
    }
    
    @ValueRangeProvider(id = "availablePeriods")
    @ProblemFactCollectionProperty
    public List<Integer> getPeriodList() {
        return periodList;
    }
    
    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }
    
    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
```

Аннотация `@PlanningSolution` сообщает OptaPlanner, что этот класс содержит данные для включения решения.

OptaPlanner предполагает наличие этих минимальных компонентов: объект планирования, факты о проблемах и оценка.

### Объект планирования (PlanningEntity)

Лекция, POJO, выглядит так:

```java
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@PlanningEntity
public class Lecture {
    
    @PlanningId
    private Long id;
    
    private Integer roomNumber;
    private Integer period;
    private String teacher;
    
    @PlanningVariable(valueRangeProviderRefs = {"availablePeriods"})
    public Integer getPeriod() {
        return period;
    }
    
    public void setPeriod(Integer period) {
        this.period = period;
    }
    
    @PlanningVariable(valueRangeProviderRefs = {"availableRooms"})
    public Integer getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    // Геттеры и сеттеры для других полей
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTeacher() {
        return teacher;
    }
    
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
```

Наш объект планирования содержит устанавливаемые ограничения. Аннотации `@PlanningVariable` и аннотации `valueRangeProviderRef` связывают ограничения с фактами проблемы.

### Калькулятор оценок

В отличие от того, что мы рассматривали до сих пор, для класса оценки требуется больше пользовательского кода. Это связано с тем, что калькулятор оценок специфичен для проблемы и модели предметной области.

Мы используем простой расчет баллов для решения этой проблемы:

```java
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public class ScoreCalculator implements EasyScoreCalculator<CourseSchedule, HardSoftScore> {
    
    @Override
    public HardSoftScore calculateScore(CourseSchedule courseSchedule) {
        int hardScore = 0;
        int softScore = 0;
        Set<String> occupiedRooms = new HashSet<>();
        
        for (Lecture lecture : courseSchedule.getLectureList()) {
            String roomInUse = lecture.getPeriod().toString() + ":" + 
                              lecture.getRoomNumber().toString();
            
            if (occupiedRooms.contains(roomInUse)) {
                hardScore += -1;
            } else {
                occupiedRooms.add(roomInUse);
            }
        }
        
        return HardSoftScore.of(hardScore, softScore);
    }
}
```

Если мы внимательнее посмотрим на приведенный выше код, важные части станут более ясными. Мы вычисляем оценку в цикле, потому что `List<Lecture>` содержит определенные неуникальные комбинации комнат и периодов.

HashSet используется для сохранения уникального ключа (строки), чтобы мы могли наказывать дубликаты лекций в одной и той же аудитории и периоде.

## Использование решателя

Мы настроили наше решение, решатель и классы задач. Давайте проверим это!

Во-первых, мы делаем некоторые настройки:

```java
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import java.time.Duration;

SolverFactory<CourseSchedule> solverFactory = SolverFactory.create(
    new SolverConfig()
        .withSolutionClass(CourseSchedule.class)
        .withEntityClasses(Lecture.class)
        .withEasyScoreCalculatorClass(ScoreCalculator.class)
        .withTerminationSpentLimit(Duration.ofSeconds(1))
);

Solver<CourseSchedule> solver = solverFactory.buildSolver();
CourseSchedule unsolvedCourseSchedule = new CourseSchedule();
```

Во-вторых, мы вносим данные в коллекцию сущностей планирования и объекты Списка фактов о проблемах.

Наконец, мы тестируем его, вызывая solve:

```java
CourseSchedule solvedCourseSchedule = solver.solve(unsolvedCourseSchedule);

assertNotNull(solvedCourseSchedule.getScore());
assertEquals(-4, solvedCourseSchedule.getScore().getHardScore());
```

Мы проверяем, что у solvedCourseSchedule есть оценка, которая говорит нам, что у нас есть «оптимальное» решение.

## Полный пример

```java
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import java.time.Duration;
import java.util.*;

public class OptaPlannerExample {
    
    public static void main(String[] args) {
        // Создаем решатель
        SolverFactory<CourseSchedule> solverFactory = SolverFactory.create(
            new SolverConfig()
                .withSolutionClass(CourseSchedule.class)
                .withEntityClasses(Lecture.class)
                .withEasyScoreCalculatorClass(ScoreCalculator.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
        );
        
        Solver<CourseSchedule> solver = solverFactory.buildSolver();
        
        // Создаем проблему
        CourseSchedule unsolvedSchedule = createProblem();
        
        // Решаем
        CourseSchedule solvedSchedule = solver.solve(unsolvedSchedule);
        
        // Выводим результат
        printCourseSchedule(solvedSchedule);
    }
    
    private static CourseSchedule createProblem() {
        CourseSchedule schedule = new CourseSchedule();
        
        // Доступные комнаты
        schedule.setRoomList(Arrays.asList(1, 2));
        
        // Доступные периоды
        schedule.setPeriodList(Arrays.asList(1, 2, 3));
        
        // Лекции
        List<Lecture> lectures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Lecture lecture = new Lecture();
            lecture.setId((long) i);
            lecture.setTeacher("Teacher " + i);
            lectures.add(lecture);
        }
        schedule.setLectureList(lectures);
        
        return schedule;
    }
    
    private static void printCourseSchedule(CourseSchedule schedule) {
        schedule.getLectureList().stream()
            .map(lecture -> "Лекция в комнате " + 
                 lecture.getRoomNumber() + 
                 " в период " + lecture.getPeriod())
            .forEach(System.out::println);
    }
}
```

Этот метод отображает:

```
Лекция в комнате 1 в период 1
Лекция в комнате 2 в период 1
Лекция в комнате 1 в период 2
Лекция в комнате 2 в период 2
Лекция в комнате 1 в период 3
Лекция в комнате 2 в период 3
Лекция в комнате 1 в период 1
Лекция в комнате 1 в период 1
Лекция в комнате 1 в период 1
Лекция в комнате 1 в период 1
```

Обратите внимание, как повторяются последние три записи. Это происходит потому, что оптимального решения нашей задачи не существует. Мы выбрали три периода, две аудитории и десять лекций.

Есть только шесть возможных лекций из-за этих фиксированных ресурсов. По крайней мере, этот ответ показывает пользователю, что не хватает комнат или периодов для размещения всех лекций.

## Дополнительные возможности

Наш пример для OptaPlanner, который мы создали, был простым, однако фреймворк добавил функции для более разнообразных вариантов использования. Мы можем захотеть реализовать или изменить наш алгоритм оптимизации, а затем указать структуру для его использования.

Благодаря недавним усовершенствованиям многопоточности в Java, OptaPlanner также дает разработчикам возможность использовать несколько реализаций многопоточности, таких как fork and join, инкрементное решение и многопользовательская среда.

## Kotlin Implementation

### Класс решения (PlanningSolution)

```kotlin
import org.optaplanner.core.api.domain.solution.*
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore

@PlanningSolution
class CourseScheduleK(
    @ValueRangeProvider(id = "availableRooms")
    @ProblemFactCollectionProperty
    val roomList: List<Int>,
    
    @ValueRangeProvider(id = "availablePeriods")
    @ProblemFactCollectionProperty
    val periodList: List<Int>,
    
    @PlanningEntityCollectionProperty
    val lectureList: MutableList<LectureK>,
    
    @PlanningScore
    var score: HardSoftScore? = null
)
```

### Объект планирования (PlanningEntity)

```kotlin
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.variable.PlanningVariable
import org.optaplanner.core.api.domain.lookup.PlanningId

@PlanningEntity
data class LectureK(
    @PlanningId
    val id: Long,
    
    val teacher: String,
    
    @PlanningVariable(valueRangeProviderRefs = ["availablePeriods"])
    var period: Int? = null,
    
    @PlanningVariable(valueRangeProviderRefs = ["availableRooms"])
    var roomNumber: Int? = null
)
```

### Калькулятор оценок

```kotlin
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator

class ScoreCalculatorK : EasyScoreCalculator<CourseScheduleK, HardSoftScore> {
    override fun calculateScore(courseSchedule: CourseScheduleK): HardSoftScore {
        var hardScore = 0
        val occupiedRooms = mutableSetOf<String>()
        
        for (lecture in courseSchedule.lectureList) {
            val roomInUse = "${lecture.period}:${lecture.roomNumber}"
            
            if (occupiedRooms.contains(roomInUse)) {
                hardScore -= 1
            } else {
                occupiedRooms.add(roomInUse)
            }
        }
        
        return HardSoftScore.of(hardScore, 0)
    }
}
```

## Резюме

Платформа OptaPlanner предоставляет разработчикам мощный инструмент для решения проблем удовлетворения ограничений, таких как планирование и распределение ресурсов.

Ключевые моменты:
- OptaPlanner автоматически находит оптимальные решения
- Требуется минимальная настройка через аннотации
- Поддерживает различные типы ограничений (жесткие и мягкие)
- Может работать с большими проблемными пространствами
- Поддерживает многопоточность для ускорения решения

Обратитесь к документации для получения дополнительной информации.
