---
title: "OptaPlanner"
description: "Фреймворк для задач планирования и удовлетворения ограничений: класс решения (@PlanningSolution), сущности планирования (@PlanningEntity), калькулятор оценки (HardSoftScore), решатель с таймаутом. Пример: расписание лекций по комнатам и периодам. Java и Kotlin."
tags:
  - algorithms
  - problems
  - optaplanner
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# OptaPlanner

Фреймворк для задач планирования и удовлетворения ограничений: класс решения (`@PlanningSolution`), сущности планирования (`@PlanningEntity`), калькулятор оценки (HardSoftScore), решатель с таймаутом. Пример: расписание лекций по комнатам и периодам. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [`OptaPlanner Documentation`](https://www.optaplanner.org/)
- [`OptaPlanner GitHub`](https://www.optaplanner.org/)

### См. также
- [Жадные алгоритмы](greedy-algorithms.md) — жадные алгоритмы
- [Алгоритмы оптимизации](../algorithmic-paradigms/) — парадигмы

## Содержание

- [Обзор](#обзор)
- [Настройка проекта](#настройка-проекта)
- [Реализация на Java](#реализация-на-java)
- [Использование решателя](#использование-решателя)
- [Полный пример](#полный-пример)
- [Дополнительные возможности](#дополнительные-возможности)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)


## Обзор

OptaPlanner решает задачи планирования и удовлетворения ограничений (расписание, распределение ресурсов), используя встроенные эвристики и метаэвристики с минимальной настройкой через аннотации. Пользователь задаёт модель (решение, сущности, переменные планирования, оценку), решатель подбирает назначения.

## Настройка проекта

Зависимость Maven: `optaplanner-core`.

```xml
<!-- OptaPlanner: ядро решателя для задач планирования -->
<dependency>
    <groupId>org.optaplanner</groupId>
    <artifactId>optaplanner-core</artifactId>
    <version>8.24.0.Final</version>
</dependency>
```

Пример: расписание лекций — каждой лекции назначаются комната и период; ограничения: одна комната — один период на одну лекцию (жёсткое), при необходимости мягкие предпочтения.

## Реализация на Java

Класс решения (`@PlanningSolution`): списки комнат и периодов с `@ValueRangeProvider`, список лекций с `@PlanningEntityCollectionProperty`, поле оценки `@PlanningScore`. Сущность планирования (`@PlanningEntity`): лекция с `@PlanningVariable` на период и комнату, привязанными к провайдерам диапазона. Калькулятор оценки реализует `EasyScoreCalculator`: обход лекций, подсчёт нарушений (например, дубликат комната+период) и возврат `HardSoftScore`.

```java
// Решение задачи планирования: комнаты, периоды и лекции с аннотациями OptaPlanner.
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

Ключ «период:комната» в `HashSet` позволяет штрафовать повторное использование одной и той же пары.

## Использование решателя

Создать `SolverConfig` с классом решения, сущностями и калькулятором оценки, задать лимит по времени (`withTerminationSpentLimit`), собрать `SolverFactory` и `Solver`. Заполнить незаполненное решение (комнаты, периоды, лекции), вызвать `solver.solve(unsolved)`.

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


```java
CourseSchedule solvedCourseSchedule = solver.solve(unsolvedCourseSchedule);

assertNotNull(solvedCourseSchedule.getScore());
assertEquals(-4, solvedCourseSchedule.getScore().getHardScore());
```

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

Пример вывода (при нехватке ресурсов возможны дубликаты комната+период):

```text
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

При 10 лекциях, 2 комнатах и 3 периодах допустимо только 6 уникальных пар; решатель выдаёт лучшее возможное решение, дубликаты показывают нехватку ресурсов.

## Дополнительные возможности

OptaPlanner поддерживает инкрементальный калькулятор оценки, многопоточность (fork/join), настройку алгоритмов и таймаутов; см. официальную документацию.

## Реализация на Kotlin

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

## Лучшие практики

Жёсткие ограничения (hard) — недопустимые нарушения, мягкие (soft) — желательные. Задавайте диапазоны переменных через `@ValueRangeProvider`, не делайте их избыточно большими. Для сложных правил используйте инкрементальный калькулятор оценки. Задавайте таймаут решения и при необходимости включайте многопоточный решатель.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| NullPointerException в калькуляторе | Переменная планирования ещё не назначена | Проверять на null период/комнату у лекции; начальное решение может быть частично заполнено |
| Решение не улучшается / hard < 0 | Недостаточно ресурсов или конфликт ограничений | Увеличить комнаты/периоды или ослабить ограничения; проверить логику калькулятора |
| Решатель долго работает | Большое пространство поиска или тяжёлый калькулятор | Задать withTerminationSpentLimit; перейти на IncrementalScoreCalculator |

## Частые вопросы

**Чем HardSoftScore отличается от простого int?** Hard и soft разделяют недопустимые нарушения (решение невалидно при hard < 0) и желательные улучшения; решатель сначала минимизирует hard, затем soft.

**Нужно ли задавать начальное решение?** Не обязательно; можно оставить переменные планирования null — решатель сам назначит значения из ValueRangeProvider. Начальное решение может ускорить сходимость.

**Когда переходить на IncrementalScoreCalculator?** Когда EasyScoreCalculator становится узким местом (много сущностей или сложные правила); инкрементальный пересчитывает только изменённую часть оценки.

## Резюме

OptaPlanner решает задачи планирования через аннотированную модель (решение, сущности, переменные, оценка) и встроенный решатель. Минимальная настройка, поддержка жёстких и мягких ограничений, таймаут и многопоточность. Подробнее — в официальной документации.
