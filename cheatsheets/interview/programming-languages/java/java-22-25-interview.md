---
title: "Вопросы на собеседовании: Java 22-25"
description: "Руководство по вопросам собеседования на тему Modern Java (22–25, включая LTS 25): instance main methods, module import, flexible constructor bodies, scoped values, stream gatherers, compact object headers, post-quantum crypto."
tags:
  - interview
  - programming-languages
  - java-22-25-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Java 22-25"
  - "Java 25 LTS interview"
  - "Java 25 собеседование"
  - "Java 22 23 24 25 вопросы"
prerequisites: []
next: []
updated: "2026-06-24"
---
# Вопросы на собеседовании: `Java 22-25`

Руководство по возможностям, которые появились в `Java` между LTS-релизами 21 и 25. Включает финализированные фичи `Java 25 LTS`, важные промежуточные изменения 22–24 и практические примеры.

**`Java 25`** (сентябрь 2025) — первая LTS-версия после `Java 21`. Она финализировала фичи, которые годами вызревали через preview: `scoped values`, `module import declarations`, `instance main methods`, `flexible constructor bodies`, а также принесла серьёзные улучшения рантайма — `compact object headers` и `generational Shenandoah`. Промежуточные релизы 22–24 финализировали `stream gatherers`, `Class-File API`, `FFM API` и убрали пиннинг виртуальных потоков на `synchronized`.

Дата последнего обновления: 2026-06-24

## Полезные ссылки

### Официальная документация

- [JDK 25 Release](https://openjdk.org/projects/jdk/25/) — список всех JEP в Java 25 LTS
- [JEPs in JDK 25 since JDK 21](https://openjdk.org/projects/jdk/25/jeps-since-jdk-21) — что накопилось между LTS
- [JDK 24 Release](https://openjdk.org/projects/jdk/24/) — JEP-список Java 24
- [New Features in Java 25 (Baeldung)](https://www.baeldung.com/java-25-features) — обзор нововведений Java 25
- [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512) — упрощённый онбординг
- [JEP 511: Module Import Declarations](https://openjdk.org/jeps/511) — import module
- [JEP 506: Scoped Values](https://openjdk.org/jeps/506) — иммутабельная передача данных по потокам
- [JEP 485: Stream Gatherers](https://openjdk.org/jeps/485) — кастомные промежуточные операции стримов
- [JEP 491: Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491) — конец пиннинга на synchronized

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Обзор**
- [Q1. (!) Что изменилось в Java с 21 по 25 LTS и почему 25 — ключевой релиз?](#q1--что-изменилось-в-java-с-21-по-25-lts-и-почему-25--ключевой-релиз)
- [Q2. (!) Какие фичи финализированы именно в Java 25?](#q2--какие-фичи-финализированы-именно-в-java-25)

**Язык: онбординг и синтаксис**
- [Q3. (!) Что такое compact source files и instance main methods (JEP 512)?](#q3--что-такое-compact-source-files-и-instance-main-methods-jep-512)
- [Q4. Что такое класс java.lang.IO и зачем он нужен?](#q4-что-такое-класс-javalangio-и-зачем-он-нужен)
- [Q5. (!) Что дают module import declarations (JEP 511)?](#q5--что-дают-module-import-declarations-jep-511)
- [Q6. (!) Что разрешают flexible constructor bodies (JEP 513)?](#q6--что-разрешают-flexible-constructor-bodies-jep-513)

**Конкурентность**
- [Q7. (!) Что такое scoped values (JEP 506) и чем они лучше ThreadLocal?](#q7--что-такое-scoped-values-jep-506-и-чем-они-лучше-threadlocal)
- [Q8. Какой статус у structured concurrency в Java 25?](#q8-какой-статус-у-structured-concurrency-в-java-25)
- [Q9. (!) Почему в Java 24 виртуальные потоки перестали пиниться на synchronized (JEP 491)?](#q9--почему-в-java-24-виртуальные-потоки-перестали-пиниться-на-synchronized-jep-491)

**Stream и API**
- [Q10. (!) Что такое stream gatherers (JEP 485) и какие есть встроенные?](#q10--что-такое-stream-gatherers-jep-485-и-какие-есть-встроенные)
- [Q11. Что такое Class-File API (JEP 484) и что он заменяет?](#q11-что-такое-class-file-api-jep-484-и-что-он-заменяет)
- [Q12. Что такое unnamed variables и patterns (JEP 456)?](#q12-что-такое-unnamed-variables-и-patterns-jep-456)
- [Q13. Что такое Foreign Function & Memory API (JEP 454) и чем он лучше JNI?](#q13-что-такое-foreign-function--memory-api-jep-454-и-чем-он-лучше-jni)

**Производительность, JVM и GC**
- [Q14. (!) Что дают compact object headers (JEP 519)?](#q14--что-дают-compact-object-headers-jep-519)
- [Q15. Что такое Generational Shenandoah (JEP 521)?](#q15-что-такое-generational-shenandoah-jep-521)
- [Q16. Что произошло с ZGC в Java 23–24?](#q16-что-произошло-с-zgc-в-java-2324)
- [Q17. Что такое AOT class loading / Project Leyden (JEP 483)?](#q17-что-такое-aot-class-loading--project-leyden-jep-483)

**Безопасность, документация и миграция**
- [Q18. Что такое Key Derivation Function API (JEP 510)?](#q18-что-такое-key-derivation-function-api-jep-510)
- [Q19. Какие post-quantum алгоритмы добавили в Java 24 (ML-KEM, ML-DSA)?](#q19-какие-post-quantum-алгоритмы-добавили-в-java-24-ml-kem-ml-dsa)
- [Q20. Что случилось со String Templates и почему их убрали?](#q20-что-случилось-со-string-templates-и-почему-их-убрали)
- [Q21. Что означает deprecation memory-access методов sun.misc.Unsafe (JEP 471)?](#q21-что-означает-deprecation-memory-access-методов-sunmiscunsafe-jep-471)
- [Q22. (!) Как мигрировать с Java 21 на Java 25: на что смотреть?](#q22--как-мигрировать-с-java-21-на-java-25-на-что-смотреть)

## Q1. (!) Что изменилось в Java с 21 по 25 LTS и почему 25 — ключевой релиз?

`Java 25` (сентябрь 2025) — первая LTS-версия после `Java 21` (сентябрь 2023). Между ними вышли три non-LTS релиза (22, 23, 24). Часть возможностей проходила preview и стала финальной уже в 22 или 24, другая — в 25.

Главные группы изменений:

- **Язык/онбординг:** `instance main methods` и `compact source files`, `module import declarations`, `flexible constructor bodies` — всё финализировано в 25.
- **Конкурентность:** `scoped values` финализированы (25); виртуальные потоки перестали пиниться на `synchronized` (24).
- **Stream/API:** `stream gatherers` и `Class-File API` финализированы в 24; `FFM API` — в 22.
- **Рантайм:** `compact object headers` и `generational Shenandoah` (25), generational `ZGC` по умолчанию (23).

**Почему 25 важен для собеседований:** это следующая LTS после 21 и удобная точка для обсуждения изменений всех промежуточных релизов. При миграции важно знать, в какой версии конкретная возможность стала финальной.

**Итог:** `Java 25` — это «сбор урожая» preview-фич 22–24 плюс новые улучшения памяти и GC.

## Q2. (!) Какие фичи финализированы именно в Java 25?

В `Java 25` стали стандартными (не preview) семь JEP:

| Фича | JEP | Категория |
|---|---|---|
| Scoped Values | 506 | конкурентность |
| Module Import Declarations | 511 | язык |
| Compact Source Files & Instance Main Methods | 512 | язык/онбординг |
| Flexible Constructor Bodies | 513 | язык |
| Key Derivation Function API | 510 | криптография |
| Compact Object Headers | 519 | рантайм/память |
| Generational Shenandoah | 521 | GC |

Что в 25 **ещё НЕ финал** (остаётся preview/experimental) — частый «подвох» на собеседовании:

- **Structured Concurrency** (JEP 505) — всё ещё preview (пятый раунд).
- **Primitive Types in Patterns** (JEP 507) — preview.
- **Stable Values**, **PEM API**, **Vector API** — preview/incubator.

**Итог:** финализированы 7 фич; structured concurrency, несмотря на «возраст», в 25 всё ещё preview — требует `--enable-preview`.

## Q3. (!) Что такое compact source files и instance main methods (JEP 512)?

Это упрощённая форма запуска программы: можно написать `main` **без** обёртки `public static void main(String[] args)` и **без** объявления класса.

```java
// Файл Hello.java целиком:
void main() {
    IO.println("Привет, мир!");
}
```

Что разрешено:

- `main` может быть **instance**-методом (не `static`).
- Можно опустить модификатор `public` и параметр `String[] args`.
- Класс объявлять не обязательно — компилятор оборачивает код в неявный класс (**compact source file**).
- Запуск: `java Hello.java`.

Зачем это: снизить порог входа для новичков и упростить скрипты/прототипы — не нужно объяснять `public static` и массив аргументов раньше времени. Прошло четыре раунда preview (с `Java 21`), финал — в `Java 25`.

**Итог:** меньше церемоний для маленьких программ; на больших проектах ничего не меняется — обычные классы работают как раньше.

## Q4. Что такое класс java.lang.IO и зачем он нужен?

`java.lang.IO` — класс для простого консольного ввода-вывода, добавленный вместе с instance main methods (JEP 512). Как и другие типы `java.lang`, он доступен без импорта в любом исходном файле Java 25; его статические методы вызывают с префиксом `IO.`.

```java
void main() {
    String name = IO.readln("Как тебя зовут? ");
    IO.println("Привет, " + name);
}
```

Методы: `IO.println(...)`, `IO.print(...)`, `IO.readln(prompt)`.

Смысл — не заставлять новичка сразу разбираться с `System.out.println` и `BufferedReader`/`Scanner`. Для реальных приложений по-прежнему используют `System.out`, логгеры и потоки.

**Итог:** `IO` — учебный/скриптовый шорткат, а не замена `System.out` и логирования в проде.

## Q5. (!) Что дают module import declarations (JEP 511)?

Декларация `import module M;` импортирует **публичные top-level типы** из пакетов, которые модуль `M` экспортирует текущему модулю, а также из пакетов транзитивно читаемых модулей. Финал — в `Java 25`.

```java
import module java.base;

// Теперь доступны без отдельных import:
List<String> xs = new ArrayList<>();   // java.util
var path = Path.of("/tmp");            // java.nio.file
var bd = new BigDecimal("1.5");        // java.math
```

Особенности:

- `import module java.base;` открывает доступ к `java.util`, `java.io`, `java.nio`, `java.math` и другим часто используемым пакетам одной строкой.
- В compact source files (JEP 512) `java.base` импортируется неявно.
- При конфликте имён (например `java.util.List` vs `java.awt.List`) нужно дописать обычный single-type import — он имеет приоритет.

**Итог:** меньше «простыни» import'ов для прототипов и скриптов; в больших проектах удобнее точечные импорты для читаемости.

## Q6. (!) Что разрешают flexible constructor bodies (JEP 513)?

Раньше вызов `super(...)` или `this(...)` обязан был быть **первым** оператором конструктора. JEP 513 (финал в `Java 25`) разрешает выполнять операторы **до** него — так называемый пролог.

```java
class PositiveRange extends Range {
    PositiveRange(int lo, int hi) {
        if (lo < 0 || hi < 0)                 // валидация ДО super() — теперь легально
            throw new IllegalArgumentException("negative");
        int normalizedHi = Math.max(lo, hi);  // подготовка аргументов
        super(lo, normalizedHi);
    }
}
```

Что можно в прологе:

- Валидировать и преобразовывать аргументы до `super()/this()`.
- Инициализировать поля **текущего** класса до вызова `super()` (чтобы суперкласс не видел значения по умолчанию при колбэках).

Что **нельзя**: читать `this`, вызывать методы экземпляра или обращаться к полям суперкласса до `super()`.

Зачем: убирает хак со статическими методами-обёртками для валидации аргументов конструктора и закрывает класс багов, когда переопределённый метод вызывается из конструктора суперкласса до инициализации полей.

**Итог:** валидацию и подготовку аргументов теперь пишут прямо в начале конструктора, до `super()`.

## Q7. (!) Что такое scoped values (JEP 506) и чем они лучше ThreadLocal?

`ScopedValue` передаёт данные по стеку вызовов без добавления параметров в каждый метод. Привязка значения неизменна в пределах области действия, но сам объект может быть изменяемым. Финал — в `Java 25`.

```java
private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

ScopedValue.where(CURRENT_USER, user).run(() -> {
    handleRequest();          // внутри:
});
// внутри handleRequest():  CURRENT_USER.get()
```

Чем лучше `ThreadLocal`:

| Аспект | `ThreadLocal` | `ScopedValue` |
|---|---|---|
| Привязка | можно менять через `set` | задаётся на время `run`/`call`, допускает вложенную привязку |
| Завершение области | при повторном использовании потока часто нужен `remove` | привязка снимается при выходе из `run`/`call` |
| Дочерние задачи | наследование зависит от используемого механизма | привязка передаётся задачам structured concurrency |
| Область видимости | весь жизненный цикл потока | строго ограничена блоком `run/call` |

Особенно хорошо сочетается со structured concurrency: дочерние задачи автоматически видят scoped value родителя.

**Итог:** `ScopedValue` полезен для ограниченного по времени контекста запроса, в том числе с виртуальными потоками. Он предотвращает забытое снятие привязки, но не делает переданный объект неизменяемым.

## Q8. Какой статус у structured concurrency в Java 25?

Structured concurrency (`StructuredTaskScope`) в `Java 25` **всё ещё preview** (JEP 505, пятый раунд) — несмотря на то, что API существует с `Java 19`. Для использования нужен `--enable-preview`.

Идея: группа параллельных подзадач рассматривается как единая единица работы — если упала одна, остальные отменяются; область их жизни ограничена блоком.

```java
try (var scope = StructuredTaskScope.open()) {
    var user  = scope.fork(() -> fetchUser());
    var order = scope.fork(() -> fetchOrder());
    scope.join();                       // ждём завершения группы или сообщает о сбое
    return new Response(user.get(), order.get());
}                                       // close дожидается оставшихся задач
```

В 25 API заметно переработали (фабрики `open(...)`, joiner'ы вместо подклассов), поэтому финализацию отложили.

**Итог:** structured concurrency — мощный инструмент, но в 25 это preview; на проде LTS его пока не закладывают без `--enable-preview`.

## Q9. (!) Почему в Java 24 виртуальные потоки перестали пиниться на synchronized (JEP 491)?

До `Java 24`, если виртуальный поток заходил в блок `synchronized` и **блокировался** внутри (например на I/O), он **пинил** (pinning) свой carrier-поток платформы: тот не мог обслуживать другие виртуальные потоки. При массовом использовании `synchronized` это сводило на нет выгоду виртуальных потоков.

JEP 491 (`Java 24`) переработал реализацию монитора так, что виртуальный поток может **отмонтироваться** от carrier даже находясь внутри `synchronized`.

Последствия:

- Больше **не нужно** срочно переписывать `synchronized` на `ReentrantLock` ради виртуальных потоков.
- Пиннинг всё ещё возможен в других случаях: native-кадры (JNI) и `Object.wait` в некоторых ситуациях.

```java
// До Java 24 это пинило carrier; с 24 — нет:
synchronized (lock) {
    blockingIoCall();   // виртуальный поток спокойно отмонтируется
}
```

**Итог:** с `Java 24` блокировка внутри `synchronized` не пинит carrier. Другие причины пиннинга нужно разбирать отдельно.

## Q10. (!) Что такое stream gatherers (JEP 485) и какие есть встроенные?

`Stream.gather(Gatherer)` — новая **промежуточная** операция, которая относится к intermediate-операциям так же, как `collect(Collector)` к терминальным. Финал — в `Java 24` (preview с 22).

Она позволяет писать собственные stateful-операции (окна, группировки, scan), которых не хватало в стандартном API.

```java
import java.util.stream.Gatherers;

// Скользящие окна по 3:
List<List<Integer>> windows =
    Stream.of(1,2,3,4,5).gather(Gatherers.windowSliding(3)).toList();
// [[1,2,3],[2,3,4],[3,4,5]]
```

Встроенные гейзереры (`java.util.stream.Gatherers`):

- `windowFixed(n)` — непересекающиеся окна по `n`.
- `windowSliding(n)` — скользящие окна.
- `fold(init, fn)` — свёртка в один результат.
- `scan(init, fn)` — накопительная свёртка (промежуточные суммы).
- `mapConcurrent(limit, fn)` — параллельный `map` с ограничением конкурентности (через виртуальные потоки).

Кастомный гейзерер обязательно задаёт `integrator` (обрабатывает элемент и передаёт 0..N результатов downstream). `initializer`, `finisher` и `combiner` добавляют, когда они нужны для состояния, завершения или параллельного выполнения.

**Итог:** gatherers закрывают пробел между `map/filter` и `Collectors` — теперь любую stateful-логику можно встроить в середину стрима.

## Q11. Что такое Class-File API (JEP 484) и что он заменяет?

`java.lang.classfile` — стандартный API для парсинга, генерации и трансформации `.class`-файлов. Финал — в `Java 24`.

Внутри JDK он заменяет встроенную копию **ASM**: раньше JDK тащил собственную версию ASM, которая отставала от формата class-file при каждом релизе. Теперь генерация байткода эволюционирует вместе с платформой.

```java
ClassFile.of().build(ClassDesc.of("Hello"), cb -> cb
    .withMethodBody("main", MethodTypeDesc.of(CD_void, CD_String.arrayType()),
        ACC_PUBLIC | ACC_STATIC, code -> code.return_()));
```

Кому полезно: авторам фреймворков, агентов, инструментов байткод-манипуляции (Spring, Hibernate, Mockito, билд-плагины) — стандартный API вместо сторонних ASM/Byte Buddy для базовых задач.

**Итог:** `Class-File API` — официальная замена ASM внутри JDK, синхронизированная с форматом байткода.

## Q12. Что такое unnamed variables и patterns (JEP 456)?

`_` (нижнее подчёркивание) обозначает переменную или паттерн, который нужен **структурно**, но чьё значение не используется. Финал — в `Java 22` (preview-имя JEP 443 в `Java 21`).

```java
// Неиспользуемая переменная исключения:
try { risky(); } catch (NumberFormatException _) { return -1; }

// Неиспользуемая переменная цикла / ресурс ради side-effect:
for (var _ : items) count++;

// Неиспользуемый компонент при деконструкции record:
if (p instanceof Point(int x, _)) use(x);
```

Зачем: подавляет предупреждения «unused variable», делает намерение явным и позволяет в `switch` объединять ветки, где часть компонентов не нужна. С `Java 9` одиночный `_` запрещён как обычный идентификатор — JEP 456 переиспользует его в новом смысле.

**Итог:** `_` = «мне нужен слот, но не значение»; чище, чем фиктивные имена вроде `ignored`.

## Q13. Что такое Foreign Function & Memory API (JEP 454) и чем он лучше JNI?

FFM API (`java.lang.foreign`) позволяет вызывать нативный код и работать с off-heap памятью **без** JNI-обвязки на C. Финал — в `Java 22` (после нескольких incubator/preview).

Ключевые типы:

- `MemorySegment` — безопасный доступ к нативной/off-heap памяти с границами.
- `Arena` — детерминированное управление временем жизни памяти.
- `Linker` + `MethodHandle` — вызов C-функций по сигнатуре.

```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment cString = arena.allocateFrom("hello");
    MethodHandle strlen = Linker.nativeLinker().downcallHandle(
        Linker.nativeLinker().defaultLookup().find("strlen").get(),
        FunctionDescriptor.of(JAVA_LONG, ADDRESS));
    long len = (long) strlen.invoke(cString);
}
```

Чем лучше JNI:

- Не нужно писать C-код и собирать нативную библиотеку-обёртку.
- Безопасность памяти: границы сегментов, контроль времени жизни через `Arena`.
- Не нужна JNI-обвязка на C; производительность зависит от конкретного вызова и способа связывания.

**Итог:** FFM — современная замена JNI: чистый Java-код для интеропа с нативными библиотеками.

## Q14. (!) Что дают compact object headers (JEP 519)?

`Compact Object Headers` уменьшают заголовок объекта в HotSpot с 96–128 бит до **64 бит** на 64-битных платформах. Production-фича в `Java 25` (была экспериментальной в 24).

```bash
java -XX:+UseCompactObjectHeaders -jar app.jar
```

Эффект:

- Меньше памяти на заголовки объектов; выигрыш по heap зависит от состава объектов и выравнивания.
- Лучше cache locality → иногда выше throughput.

Это часть проекта Lilliput. Особенно заметно на приложениях с миллионами мелких объектов (кэши, доменные модели, графы).

**Итог:** одной JVM-опцией можно ощутимо сократить потребление памяти без правок кода.

## Q15. Что такое Generational Shenandoah (JEP 521)?

`Generational Shenandoah` добавляет **поколенческий** режим в GC `Shenandoah`: молодые и старые объекты собираются раздельно. Финал — в `Java 25`.

Зачем поколения: большинство объектов умирают молодыми (weak generational hypothesis). Раздельный сбор молодого поколения дешевле и чаще, что снижает частоту дорогих полных циклов и общее давление на GC.

```bash
java -XX:+UseShenandoahGC -XX:ShenandoahGCMode=generational -jar app.jar
```

Это повторяет путь `ZGC`, который стал поколенческим по умолчанию в `Java 23`. Shenandoah проектируется для коротких пауз; фактические паузы и нагрузку на CPU нужно проверять на своём приложении.

**Итог:** поколенческий режим может уменьшить работу GC для потоков с большим числом недолговечных объектов; в Java 25 он включается отдельно.

## Q16. Что произошло с ZGC в Java 23–24?

`ZGC` прошёл два шага:

- **`Java 23` (JEP 474):** generational-режим стал **режимом по умолчанию**. Раньше включали явно через `-XX:+ZGenerational`.
- **`Java 24` (JEP 490):** non-generational режим **удалён** полностью. Опция `-XX:-ZGenerational` больше не нужна и не поддерживается.

Зачем: поддерживать две реализации (gen и non-gen) дорого, а поколенческий режим лучше использует типичное распределение времени жизни объектов.

Влияние на миграцию: флаг `-XX:±ZGenerational` больше не выбирает режим. В Java 25 HotSpot принимает его как obsolete, выводит предупреждение и продолжает запуск. Флаг лучше удалить из конфигурации.

**Итог:** с `Java 24` `ZGC` — только generational; старые ZGC-флаги поколения надо вычистить из конфигов.

## Q17. Что такое AOT class loading / Project Leyden (JEP 483)?

`Ahead-of-Time Class Loading & Linking` (JEP 483, `Java 24`) — первый шаг проекта **Leyden**, цель которого ускорить старт JVM. Тренировочный запуск записывает конфигурацию загруженных и связанных классов; отдельный шаг создаёт AOT-кэш для последующих запусков.

```bash
# 1) тренировочный прогон — записываем конфигурацию
java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -jar app.jar
# 2) создаём кэш по записанной конфигурации
java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar app.jar
# 3) используем кэш
java -XX:AOTCache=app.aot -jar app.jar
```

Кэш может ускорить старт и time-to-first-request; выигрыш зависит от приложения и состава кэша. Приложение остаётся на HotSpot JVM с JIT, в отличие от нативного образа GraalVM Native Image.

**Итог:** AOT-кэш помогает сократить время старта HotSpot после записи конфигурации и отдельного создания кэша.

## Q18. Что такое Key Derivation Function API (JEP 510)?

`KDF API` (`javax.crypto.KDF`) — стандартный API для функций деривации ключей: получения дополнительных ключей из секрета и контекста. Финал — в `Java 25`.

```java
KDF hkdf = KDF.getInstance("HKDF-SHA256");
AlgorithmParameterSpec params = HKDFParameterSpec.ofExtract()
    .addIKM(secret).addSalt(salt).thenExpand(info, 32);
SecretKey derived = hkdf.deriveKey("AES", params);
```

Поддерживает HKDF (RFC 5869) и расширяем под другие KDF. Раньше HKDF приходилось тащить из BouncyCastle или писать руками — теперь это часть платформы и важная основа для протоколов (TLS 1.3, гибридные post-quantum схемы).

**Итог:** деривация ключей теперь стандартизирована в JDK — меньше зависимости от сторонних крипто-библиотек.

## Q19. Какие post-quantum алгоритмы добавили в Java 24 (ML-KEM, ML-DSA)?

В `Java 24` добавили два квантово-устойчивых алгоритма, стандартизированных NIST:

- **ML-KEM** (JEP 496) — Module-Lattice Key Encapsulation Mechanism (NIST FIPS 203). Механизм инкапсуляции ключей — постквантовая замена обмена ключами (вместо/в дополнение к ECDH).
- **ML-DSA** (JEP 497) — Module-Lattice Digital Signature Algorithm (NIST FIPS 204). Постквантовые цифровые подписи.

Зачем: угроза «harvest now, decrypt later» — зашифрованный сегодня классической криптографией трафик могут расшифровать в будущем квантовым компьютером. Эти алгоритмы доступны через стандартные `KeyPairGenerator`/`KEM`/`Signature`.

**Итог:** JDK получил готовые NIST-стандартизированные post-quantum примитивы для защиты от будущих квантовых атак.

## Q20. Что случилось со String Templates и почему их убрали?

`String Templates` (интерполяция строк) были preview в `Java 21` (JEP 430) и вторым preview в `Java 22` (JEP 459), после чего их **отозвали** — в `Java 23` и далее их нет даже как preview.

Причина: предложение для третьего preview-раунда в Java 23 отозвали; дизайн требовал дальнейшей переработки.

```java
// Так было в preview Java 21–22 (СЕЙЧАС НЕ РАБОТАЕТ):
String msg = STR."Привет, \{name}!";
```

Практический вывод для собеседования: в современной Java **нет** встроенной интерполяции строк. Используют `String.format`, `MessageFormat`, конкатенацию или `StringBuilder`.

**Итог:** String Templates — пример preview-фичи, которую убрали; не закладывайтесь на неё, в 23+ её нет.

## Q21. Что означает deprecation memory-access методов sun.misc.Unsafe (JEP 471)?

В `Java 23` методы доступа к памяти в `sun.misc.Unsafe` помечены **deprecated for removal**. Это легаси-API, которым годами пользовались библиотеки (off-heap буферы, сериализация, конкурентные структуры) в обход публичного API.

Чем заменять:

- **VarHandle** (`java.lang.invoke`) — для атомарных операций над полями/массивами (on-heap).
- **FFM API** (`java.lang.foreign`, JEP 454) — для off-heap памяти.

Зачем: эти методы дают доступ к памяти без гарантий публичных API и мешают развитию JVM. Миграция зависит от вида операции и возможностей поддерживаемой версии Java.

**Итог:** библиотекам пора мигрировать с `Unsafe` на `VarHandle` + FFM; в будущих релизах memory-методы `Unsafe` уберут.

## Q22. (!) Как мигрировать с Java 21 на Java 25: на что смотреть?

`Java 25` — следующая LTS после `Java 21`. Перед миграцией проверьте используемые API, флаги и зависимости:

- **ZGC-флаги:** убрать `-XX:±ZGenerational`: в 25 они дают предупреждение и игнорируются, поскольку non-generational режим удалён.
- **String Templates:** если использовали preview-интерполяцию `STR."..."` из 21 — её больше нет, переписать на `String.format`/конкатенацию.
- **Пиннинг виртуальных потоков:** пересмотреть обходные пути, добавленные только из-за пиннинга на `synchronized` (JEP 491). Не менять выбранную блокировку без проверки её семантики.
- **`sun.misc.Unsafe`:** проверить зависимости на deprecated memory-методы; планировать миграцию на `VarHandle`/FFM.
- **Новые возможности (опционально):** измерить эффект `-XX:+UseCompactObjectHeaders` на своей нагрузке; рассмотреть `ScopedValue` для контекста с ограниченной областью действия.
- **Preview-фичи:** structured concurrency и primitive patterns в 25 всё ещё требуют `--enable-preview` — на проде не включать без причины.

```bash
# Полезно прогнать для поиска проблемных API:
jdeprscan --release 25 app.jar
jdeps --jdk-internals app.jar
```

**Итог:** план миграции зависит от используемых библиотек и preview-возможностей. Старый флаг ZGC стоит удалить, но сам по себе в Java 25 он не останавливает запуск.

---

## See also

- [Java 17-21](java-17-21-interview.md) — предыдущие LTS: records, sealed, pattern matching, virtual threads
- [Java Virtual Threads](java-virtual-threads-interview.md) — детали виртуальных потоков и пиннинга
- [Java Concurrency](java-concurrency-interview.md) — модель памяти, `synchronized`, `ReentrantLock`, `ThreadLocal`
- [Java Stream](java-stream-interview.md) — стримы и связь с gatherers
- [Java Pattern Matching](java-pattern-matching-interview.md) — record patterns и unnamed patterns
- [Java IO/NIO](java-io-nio-interview.md) — память, буферы, связь с FFM API
- [Java Modules](java-modules-interview.md) — JPMS и module import declarations
- [JVM](../../jvm/jvm-interview.md) — устройство HotSpot, заголовки объектов, GC
- [JVM Performance Tuning](../../performance/jvm-performance-tuning-interview.md) — GC-флаги, ZGC, Shenandoah, compact headers
- [GraalVM Native](../../jvm/graalvm-native-interview.md) — альтернативный путь ускорения старта vs Project Leyden
