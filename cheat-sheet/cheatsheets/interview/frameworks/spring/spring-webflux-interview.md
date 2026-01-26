# Вопросы на собеседовании: Spring WebFlux

**Комплексное руководство по вопросам собеседования на тему Spring WebFlux для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-24


## Полезные ссылки

### Официальная документация
- [Документация по теме](https://docs.oracle.com/)

### См. также
- Связанные темы из основной документации

## Содержание

- [Q1. Что такое Project Reactor и Spring WebFlux?](#q1-что-такое-project-reactor-и-spring-webflux)
- [Q2. (ВАЖНО) - В чем отличие между Project Reactor и Spring WebFlux?](#q2-важно---в-чем-отличие-между-project-reactor-и-spring-webflux)
- [Q3. (ВАЖНО) - Какова архитектура Project Reactor?](#q3-важно---какова-архитектура-project-reactor)
- [Q4. (ВАЖНО) - Какова архитектура Spring WebFlux?](#q4-важно---какова-архитектура-spring-webflux)
- [Q5. Что такое реактивное программирование?](#q5-что-такое-реактивное-программирование)
- [Q6. Какие основные принципы реактивного программирования?](#q6-какие-основные-принципы-реактивного-программирования)
- [Q7. (ВАЖНО) - Как реализована асинхронность в Project Reactor?](#q7-важно---как-реализована-асинхронность-в-project-reactor)
- [Q8. (ВАЖНО) - Как реализована асинхронность в Spring WebFlux?](#q8-важно---как-реализована-асинхронность-в-spring-webflux)
- [Q9. Что такое Flux и Mono в Project Reactor?](#q9-что-такое-flux-и-mono-в-project-reactor)
- [Q10. Какие методы доступны для работы с реактивными потоками в Spring WebFlux?](#q10-какие-методы-доступны-для-работы-с-реактивными-потоками-в-spring-webflux)
- [Q11. Как обрабатывать ошибки в Project Reactor?](#q11-как-обрабатывать-ошибки-в-project-reactor)
- [Q12. Как обрабатывать ошибки в Spring WebFlux?](#q12-как-обрабатывать-ошибки-в-spring-webflux)
- [Q13. Как тестировать реактивные приложения, построенные на Project Reactor и Spring WebFlux?](#q13-как-тестировать-реактивные-приложения-построенные-на-project-reactor-и-spring-webflux)

## Q1. Что такое Project Reactor и Spring WebFlux?

**Project Reactor** и **Spring WebFlux** — это две связанные технологии, которые предоставляют поддержку реактивного программирования в **Java**. **Project Reactor** — это реактивная библиотека, основанная на принципах реактивного программирования. Она предоставляет мощные инструменты для работы с асинхронными и событийно-ориентированными потоками данных. **Project Reactor** предлагает два основных типа данных — **Flux** (поток данных с нулевым или более значениями) и **Mono** (поток данных с нулем или одним значением).

**Spring WebFlux** — это модуль **Spring Framework**, который интегрирует **Project Reactor** и предоставляет возможности реактивного программирования для разработки веб-приложений. Он поддерживает асинхронный и неблокирующий ввод-вывод, что позволяет обрабатывать большое количество одновременных запросов с помощью небольшого количества потоков. **Spring WebFlux** также предоставляет аннотационную модель для определения обработчиков запросов и поддержку реактивных сокетов.

Оба этих инструмента позволяют разработчикам создавать масштабируемые и отзывчивые приложения, которые эффективно обрабатывают большие нагрузки и высокую конкуренцию запросов.



## Q2. (ВАЖНО) - В чем отличие между Project Reactor и Spring WebFlux?

**Project Reactor** и **Spring WebFlux** — это связанные технологии, но существуют некоторые отличия:

1. **Основная цель**: **Project Reactor** является самостоятельной реактивной библиотекой, предоставляющей инструменты для работы с реактивным программированием. С другой стороны, **Spring WebFlux** является модулем **Spring Framework**, который интегрирует **Project Reactor** и предоставляет возможности реактивного программирования для разработки веб-приложений.
2. **Виды данных**: **Project Reactor** предоставляет два основных типа данных — **Flux** (поток данных с нулевым или более значениями) и **Mono** (поток данных с нулем или одним значением). **Spring WebFlux** использует **Flux** и **Mono** для представления данных и реактивного потока веб-запросов и ответов.
3. **API и подход**: **Project Reactor** предоставляет широкий спектр операторов и функций для работы с реактивными потоками данных. **Spring WebFlux** использует аннотационную модель и функциональные интерфейсы для определения обработчиков запросов и маршрутов.
4. **Область применения**: **Project Reactor** может использоваться не только в контексте веб-разработки, но и в других областях разработки приложений, где требуется реактивное программирование. **Spring WebFlux**, с другой стороны, фокусируется на обеспечении реактивности веб-приложений и поддержке реактивного ввода-вывода.

В целом, **Project Reactor** является базовой реактивной библиотекой, в то время как **Spring WebFlux** расширяет ее функциональность, предоставляя интеграцию с **Spring Framework** и обеспечивая реактивные возможности для веб-разработки.



## Q3. (ВАЖНО) - Какова архитектура Project Reactor?


Архитектура **Project Reactor** основана на принципах реактивного программирования и использует несколько ключевых компонентов:

1. **Publisher (Издатель)**: Он представляет собой источник данных, который может генерировать поток элементов. **Publisher** может быть представлен **Flux** (поток данных с нулевым или более значениями) или **Mono** (поток данных с нулем или одним значением).
2. **Subscriber (Подписчик)**: Он представляет компонент, который подписывается на **Publisher** для получения данных. **Subscriber** определяет реакцию на каждый элемент издателя, а также на сигналы об успешном завершении или ошибке.
3. **Processor (Процессор)**: Он представляет промежуточный компонент между **Publisher** и **Subscriber**. Процессор может преобразовывать, фильтровать или комбинировать данные, прежде чем они будут отправлены подписчику.
4. **Scheduler (Планировщик)**: Он представляет собой механизм для планирования и управления выполнением операций в реактивном потоке. С помощью планировщика можно контролировать асинхронность и параллелизм операций.

Архитектура **Project Reactor** основана на протоколе **Reactive Streams**, который определяет стандартные интерфейсы для взаимодействия между компонентами реактивных потоков данных.

Эти компоненты совместно работают для обеспечения эффективной обработки и передачи данных в реактивной среде. Они позволяют разработчикам создавать мощные, отзывчивые и масштабируемые приложения, которые эффективно работают с асинхронными и событийно-ориентированными потоками данных.



## Q4. (ВАЖНО) - Какова архитектура Spring WebFlux?


Архитектура **Spring WebFlux** основана на **Spring Framework** и реактивном программировании, и включает следующие основные компоненты:

1. **DispatcherHandler**: Это центральный компонент в архитектуре **Spring WebFlux**. Он принимает **HTTP**-запросы от клиента и определяет, какие обработчики (handlers) должны быть вызваны для обработки запроса.
2. **HandlerMapping**: Он отображает входящий запрос на соответствующий обработчик (handler). Здесь происходит сопоставление **URL**-шаблонов с соответствующими обработчиками.
3. **HandlerAdapter**: Он вызывает обработчик (handler) и передает в него входящий запрос. Он также отвечает за преобразование входящего запроса в объекты данных, понятные для обработчика.
4. **Handler**: Он представляет методы или функции, которые обрабатывают входящий запрос и возвращают реактивные потоки данных **Flux** или **Mono** для формирования ответа клиенту.
5. **RouterFunction**: Это новый подход, представленный в **Spring 5**, для определения маршрутов и обработчиков. Он позволяет определять маршруты на основе функциональных конструкций и декларативно устанавливать соответствующие обработчики для каждого маршрута.
6. **ServerHttpRequest** и **ServerHttpResponse**: Они представляют входящий запрос от клиента и исходящий ответ на сервер соответственно.
7. **WebClient**: Это компонент, предоставляемый **Spring WebFlux**, который позволяет выполнять асинхронные **HTTP**-запросы к внешним ресурсам.

Архитектура **Spring WebFlux** позволяет разработчикам создавать реактивные веб-приложения, которые обрабатывают **HTTP**-запросы асинхронно и масштабируются эффективно. Она также обеспечивает интеграцию с **Project Reactor**, позволяя использовать мощь и гибкость реактивного программирования в контексте веб-разработки с использованием **Spring Framework**.



## Q5. Что такое реактивное программирование?

**Реактивное программирование (Reactive Programming)** — это программирование, основанное на обработке и реагировании на потоки данных, события и изменения состояния. Главная идея реактивного программирования состоит в том, чтобы создавать асинхронные, отзывчивые и эффективные приложения, которые могут эффективно обрабатывать потоки данных, приходящие из разных источников.

В реактивном программировании основной упор делается на обработку и реагирование на события в реальном времени. Вместо традиционного подхода, где программы ожидают синхронного выполнения задач, реактивные системы стремятся быть более отзывчивыми и гибкими путем предоставления возможности обработки событий асинхронно и параллельно.

Основные концепции и инструменты реактивного программирования включают в себя:

1. Потоки данных (Streams): Они представляют потоки событий или изменений состояния, которые могут быть обработаны и переданы к другим компонентам программы.
2. Функциональные операторы (Functional Operators): Они предоставляют инструменты для преобразования, фильтрации и комбинирования потоков данных, что позволяет эффективно манипулировать данными.
3. **Обратные вызовы (Callbacks) и Подписки (Subscriptions)**: Они позволяют компонентам программы реагировать на поступающие события и подписываться на определенные потоки данных.
4. Асинхронность (Asynchrony): Реактивное программирование активно использует асинхронные операции, чтобы не блокировать выполнение программы и обеспечить отзывчивость.

Реактивное программирование особенно полезно в разработке веб-приложений, микросервисов и других систем, где требуется обработка большого количества асинхронных событий и потоков данных. Оно помогает создавать более гибкие и масштабируемые приложения, которые могут эффективно работать с изменяющимися потоками данных.



## Q6. Какие основные принципы реактивного программирования?


Основные принципы реактивного программирования включают в себя:

1. Асинхронность: Реактивное программирование активно использует асинхронные операции для эффективной обработки множества событий и запросов, не блокируя выполнение программы. Это позволяет создавать отзывчивые системы, способные обрабатывать большое количество одновременных операций.
2. Отзывчивость: Реактивные системы строятся с учетом высокой отзывчивости к запросам и событиям. Они активно используют асинхронность и неблокирующие операции для минимизации времени ожидания и обеспечения мгновенного реагирования на поступающие запросы.
3. Эластичность: Реактивное программирование способствует созданию эластичных систем, которые могут масштабироваться в зависимости от нагрузки. Путем использования асинхронности, автоматической маршрутизации и гибкой обработки данных, реактивные системы могут легко расширяться или уменьшаться для адаптации к изменяющимся условиям.
4. Отказоустойчивость: Реактивные системы строятся с учетом высокой отказоустойчивости. Они активно используют обработку ошибок и механизмы восстановления для гарантии нормальной работы приложения, даже в случае возникновения сбоев.
5. Обработка потоков данных: Реактивное программирование предоставляет инструменты для эффективной обработки потоков данных, изменений состояния и событий. Оно активно использует потоки данных и функциональные операторы, позволяющие преобразовывать, фильтровать и комбинировать данные с помощью декларативного подхода.
6. Обратная связь: Реактивное программирование уделяет внимание обратной связи. Компоненты программы могут подписываться на изменения в потоке данных и получать уведомления о событиях. Это позволяет реагировать на изменения в режиме реального времени и принимать соответствующие действия.

Основные принципы реактивного программирования помогают создавать гибкие, эффективные и масштабируемые системы, которые могут обрабатывать большое количество событий и запросов, и обеспечивать отзывчивость в реальном времени.



## Q7. (ВАЖНО) - Как реализована асинхронность в Project Reactor?


Асинхронность в **Project Reactor** реализуется с помощью использования реактивных потоков данных (**Flux** и **Mono**) и планировщиков (**Schedulers**).

1. Реактивные потоки данных:Flux** и **Mono** представляют собой абстракции над потоками данных. Они предоставляют методы для создания, преобразования и обработки данных в асинхронном режиме. Вместо блокирующих вызовов операции выполняются неблокирующим способом и возвращают реактивные типы данных - **Flux** и **Mono** - которые представляют поток данных, который можно далее обрабатывать.
2. **Планировщики**: Планировщики (**Schedulers**) определяют контекст выполнения операций в **Project Reactor**. Они предоставляют потоки выполнения (например, потоки или пулы потоков), в которых операции будут выполняться. Планировщики также определяют, какие потоки будут использоваться для выполнения операций, и контролируют передачу данных между ними.

Например, планировщик **Schedulers.parallel()** будет использовать множество потоков для выполнения операций параллельно. Планировщик **Schedulers.single()** будет использовать единственный поток для выполнения операций последовательно.

При обработке реактивных потоков данных **Project Reactor** использует паттерн наблюдателя (Observable pattern), при котором поток данных оповещает своих подписчиков о появлении новых данных. Это позволяет выполнять операции неблокирующим способом и обрабатывать данные по мере их поступления.

Благодаря асинхронной реализации **Project Reactor** достигает высокой производительности, эффективно используя ресурсы системы и обеспечивая отзывчивость приложений даже при обработке большого количества данных или одновременных запросов.



## Q8. (ВАЖНО) - Как реализована асинхронность в Spring WebFlux?


Асинхронная обработка в **Spring WebFlux** достигается за счет использования реактивного программирования и реактивного стека.

1. **Реактивные типы данных**: **Spring WebFlux** использует реактивные типы данных, такие как **Flux** и **Mono**, для представления асинхронных операций. **Flux** представляет собой последовательность данных, а **Mono** — результат одиночной операции. Эти типы данных позволяют обрабатывать данные и выполнять операции в асинхронном режиме, позволяя не блокировать поток выполнения и увеличивая масштабируемость системы.
2. **Неблокирующие операции**: **Spring WebFlux** использует неблокирующие операции ввода-вывода для взаимодействия с сетевыми компонентами, такими как **HTTP**-серверы и клиенты. Благодаря этому, процессор не заблокирован, позволяя обрабатывать больше запросов с использованием меньшего количества потоков.
3. **Реактивные аннотации**: В **Spring WebFlux** можно использовать реактивные аннотации для отметки методов, которые должны обрабатываться асинхронно. Например, аннотация `@GetMapping` может быть использована для определения метода обработки **HTTP GET** запроса, который возвращает реактивный тип данных.
4. **Реактивный сервер Netty**: **Spring WebFlux** по умолчанию использует сервер **Netty**, который поддерживает асинхронную и неблокирующую обработку запросов. **Netty** основан на событийной модели и может эффективно управлять большим количеством одновременных соединений, что делает его идеальным для высокопроизводительной асинхронной обработки запросов.

Благодаря этим компонентам и подходам асинхронная обработка в **Spring WebFlux** обеспечивает эффективное использование ресурсов, высокую производительность и отзывчивость при обработке запросов, особенно в условиях высоких нагрузок и неопределенных задержек сети.



## Q9. Что такое Flux и Mono в Project Reactor?

**Flux** и **Mono** — это два основных реактивных типа данных, предоставляемых **Project Reactor**.

1. Flux:Flux** представляет собой поток данных, который может содержать ноль или более элементов. Он представляет собой асинхронную версию коллекции и позволяет работать с данными в режиме "потока".Flux** может быть бесконечным, то есть может продолжать генерировать данные бесконечно, или конечным, имеющим определенное количество элементов.Flux** предоставляет широкие возможности для преобразования, фильтрации и комбинирования данных, а также поддерживает асинхронное или параллельное выполнение операций над ними.Пример использования Flux:

Flux&lt;Integer&gt; numbers = Flux.just(1, 2, 3, 4, 5);

numbers.map(n -> n \* 2).filter(n -> n > 5).subscribe(System.out::println);

1. **Mono**: **Mono** представляет собой реактивный тип данных, который содержит ноль или один элемент. Он аналогичен `Option`, `Maybe` или `Single` из других реактивных библиотек. **Mono** предоставляет возможности для обработки операций над одиночными значениями, такие как преобразования, фильтрация и комбинация. Он также может быть использован для обработки ошибок и выполнения асинхронных операций.

Пример использования Mono:
```java
Mono<String> greeting = Mono.just("Hello");
greeting.map(s -> s + " World").subscribe(System.out::println);
```

Оба типа данных, **Flux** и **Mono**, предоставляют методы для манипуляции с данными, такие как `map`, `filter`, `flatMap`, `reduce` и многие другие. Они также поддерживают асинхронную обработку и композицию операций, что делает их мощным инструментом при работе с реактивным программированием в **Project Reactor**.



## Q10. Какие методы доступны для работы с реактивными потоками в Spring WebFlux?


В **Spring WebFlux** доступно множество методов для работы с реактивными потоками. Некоторые из наиболее часто используемых методов включают:

1. map: Преобразует каждый элемент потока, применяя функцию к нему, и возвращает поток с преобразованными элементами.
2. flatMap: Преобразует каждый элемент потока в другой поток и объединяет их все в один поток.
3. filter: Оставляет только элементы, соответствующие определенному условию.
4. take: Берет определенное количество элементов из потока и завершает его.
5. reduce: Выполняет агрегацию элементов потока, используя указанную функцию.
6. zip: Комбинирует элементы из нескольких потоков, используя указанную функцию.
7. onErrorResume: Позволяет определить альтернативный поток для обработки ошибок.
8. retry: Пытается снова выполнить операцию в случае ошибки.

Это всего лишь некоторые методы доступные в **Spring WebFlux** для работы с реактивными потоками. Их список довольно обширен, и каждый метод предоставляет различные возможности для преобразования и обработки реактивных потоков.

Кроме того, **Spring WebFlux** также предоставляет операторы, которые могут быть использованы вместе с реактивными потоками для выполнения различных операций, таких как сетевые запросы, манипуляция данными, обработка ошибок и другие. Эти операторы включают **WebClient** для выполнения **HTTP**-запросов, **R2DBC** для взаимодействия с базами данных, а также множество других операторов и адаптеров.



## Q11. Как обрабатывать ошибки в Project Reactor?


В **Project Reactor** предоставляются различные методы для обработки ошибок. Некоторые из самых распространенных способов включают:

1. `onError`: Этот метод позволяет указать, что делать при возникновении ошибки в потоке. Вы можете передать лямбда-выражение или обработчик ошибок, который будет выполнен при возникновении ошибки.

flux.onError(err -> {

// обработка ошибки

})

1. onErrorReturn: При возникновении ошибки этот метод позволяет вернуть значение по умолчанию или альтернативное значение вместо ошибки.

flux.onErrorReturn("Default Value")

1. onErrorResume: Этот метод позволяет вернуть альтернативный поток, который будет использован вместо оригинального потока, если произойдет ошибка.

flux.onErrorResume(err -> {

// вернуть альтернативный поток

})

1. retry: Этот метод позволяет повторить выполнение операции при возникновении ошибки. Вы можете задать количество повторов или определенные условия для повтора.

flux.retry(3) // повторить операцию 3 раза

1. doOnError: Этот метод позволяет выполнить действие при возникновении ошибки, но не обрабатывает ее. Он может использоваться для логирования или выполнения других операций при возникновении ошибки.

flux.doOnError(err -> {

// выполнить действие при ошибке

})

Это только несколько примеров методов для обработки ошибок в **Project Reactor**. Важно выбрать подходящий метод, который соответствует вашим потребностям и предоставляет необходимую логику для обработки ошибок в вашем потоке.



## Q12. Как обрабатывать ошибки в Spring WebFlux?


Существует несколько способов обработки ошибок в **Spring WebFlux**. Вот несколько из них:

1. **Обработчики исключений (Exception Handlers)**: С помощью аннотации `@ExceptionHandler` вы можете определить методы, которые будут обрабатывать определенные исключения. В этих методах можно выполнить логирование ошибок, сформировать кастомный ответ или выполнить другие действия для обработки ошибки.

@ExceptionHandler(YourException.class)

public Mono&lt;ServerResponse&gt; handleYourException(YourException ex) {

// обработка исключения

return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Custom error message");

}

1. **Адаптеры WebExceptionHandler**: Этот способ похож на обработчики исключений, но является гибким и допускает большую настройку. Вы можете определить собственный класс, реализующий интерфейс **WebExceptionHandler**, и зарегистрировать его в качестве глобального обработчика исключений для вашего приложения.

@Component

public class CustomExceptionHandler implements WebExceptionHandler {

@Override

public Mono&lt;Void&gt; handle(ServerWebExchange exchange, Throwable ex) {

// обработка исключения

return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Custom error message").then();

}

}

1. Оператор onErrorResume: Этот оператор позволяет предоставить альтернативный поток данных, который будет использоваться в случае возникновения ошибки. Вы можете вернуть альтернативный ответ или поток данных, содержащий ошибку, с помощью метода **onErrorResume.

flux.onErrorResume(ex -> {

// обработка ошибки

return Mono.just("Error occurred");

})

1. Глобальные обработчики исключений: Вы также можете определить глобальные обработчики исключений, которые будут работать для всех запросов в вашем приложении, используя классы, реализующие **HandlerExceptionResolver. Это может быть полезно для предоставления собственных страниц ошибок или обработки исключений определенным образом.

@Component

public class GlobalExceptionHandler implements HandlerExceptionResolver {

@Override

public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

// обработка исключения

ModelAndView modelAndView = new ModelAndView();

modelAndView.addObject("errorMessage", "An error occurred");

modelAndView.setViewName("error");

return modelAndView;

}

}

Это только некоторые из способов обработки ошибок в **Spring WebFlux**. Выбор метода зависит от вашей конкретной ситуации и требований.



## Q13. Как тестировать реактивные приложения, построенные на Project Reactor и Spring WebFlux?


Для тестирования реактивных приложений, построенных на **Project Reactor** и **Spring WebFlux**, вы можете использовать следующие подходы и инструменты:

1. **Встроенный подход в Spring WebFlux**: **Spring Framework** предоставляет встроенный подход для тестирования реактивных контроллеров. Вы можете использовать класс **WebTestClient**, который предоставляет возможности для выполнения **HTTP** запросов и проверки ответов. Это позволяет вам проверить взаимодействие с вашими эндпоинтами, их поведение и корректность возвращаемых результатов.
2. Использование JUnit и Mockito: Вы можете использовать **JUnit** и **Mockito** для тестирования реактивных компонентов, таких как сервисы, репозитории или другие компоненты, которые не связаны напрямую с **HTTP** запросами. Вы можете создать тестовые классы и использовать аннотации **@Mock** и **@InjectMocks** для создания моков и инъекции зависимостей. Затем, используя методы **Mockito, вы можете определить поведение моков и проверить правильность их вызовов.
3. **Использование тестовых баз данных**: Если ваше реактивное приложение взаимодействует с базой данных, вы можете использовать тестовые базы данных, такие как **H2** или **Embedded MongoDB**, для выполнения интеграционных тестов. Это позволит вам проверить правильность работы ваших запросов к базе данных и взаимодействие с ней в контексте реактивности.
4. **Использование StepVerifier**: **StepVerifier** — это инструмент, предоставляемый **Project Reactor**, который упрощает тестирование реактивных потоков. Вы можете создать поток, применить операции и использовать **StepVerifier** для проверки результатов. **StepVerifier** позволяет вам проверить значения, ошибки и завершение потока.

Независимо от выбранного подхода, рекомендуется создать тесты, которые проверяют общую функциональность вашего приложения, а также отдельные тесты для каждой отдельной функции или компонента. Тестирование реактивных приложений требует особого внимания к синхронизации и времени. Запомните, что реактивные потоки могут выполняться асинхронно, и ваш код для тестирования должен учитывать это.

В целом, тестирование реактивных приложений с использованием **Project Reactor** и **Spring WebFlux** следует общим принципам тестирования, но требует дополнительного внимания к асинхронности и объему данных, которые обрабатываются в потоках.

## Практические примеры на Java + Spring

### Пример 1: Создание реактивного REST контроллера

```java
/**
 * Реактивный REST контроллер с использованием Spring WebFlux
 * Демонстрирует использование Mono и Flux для асинхронной обработки
 */
@RestController
@RequestMapping("/api/reactive/users")
public class ReactiveUserController {
 
 @Autowired
 private ReactiveUserService userService;
 
 /**
 * Получение пользователя по ID
 * Возвращает Mono<User> - реактивный тип для одного элемента
 */
 @GetMapping("/{id}")
 public Mono<ResponseEntity<UserDTO>> getUser(@PathVariable Long id) {
 return userService.findById(id).map(user -> ResponseEntity.ok(toDTO(user))).defaultIfEmpty(ResponseEntity.notFound().build());
 }
 
 /**
 * Получение всех пользователей
 * Возвращает Flux<UserDTO> - реактивный поток элементов
 */
 @GetMapping
 public Flux<UserDTO> getAllUsers(
 @RequestParam(defaultValue = "0") int page,
 @RequestParam(defaultValue = "20") int size) {
 
 return userService.findAll(page, size).map(this::toDTO).doOnNext(user -> log.debug("Processing user: {}", user.getId()));
 }
 
 /**
 * Создание пользователя
 * Обработка реактивного потока с валидацией
 */
 @PostMapping
 @ResponseStatus(HttpStatus.CREATED)
 public Mono<ResponseEntity<UserDTO>> createUser(
 @Valid @RequestBody Mono<UserCreateRequest> requestMono) {
 
 return requestMono.flatMap(request -> userService.createUser(request)).map(user -> ResponseEntity.status(HttpStatus.CREATED).body(toDTO(user))).onErrorResume(ValidationException.class, ex -> 
 Mono.just(ResponseEntity.badRequest().build())
 );
 }
 
 /**
 * Обновление пользователя
 */
 @PutMapping("/{id}")
 public Mono<ResponseEntity<UserDTO>> updateUser(
 @PathVariable Long id,
 @Valid @RequestBody Mono<UserUpdateRequest> requestMono) {
 
 return requestMono.flatMap(request -> userService.updateUser(id, request)).map(user -> ResponseEntity.ok(toDTO(user))).defaultIfEmpty(ResponseEntity.notFound().build());
 }
 
 /**
 * Удаление пользователя
 */
 @DeleteMapping("/{id}")
 @ResponseStatus(HttpStatus.NO_CONTENT)
 public Mono<Void> deleteUser(@PathVariable Long id) {
 return userService.deleteUser(id);
 }
 
 /**
 * Поиск пользователей с реактивной обработкой
 */
 @GetMapping("/search")
 public Flux<UserDTO> searchUsers(
 @RequestParam(required = false) String name,
 @RequestParam(required = false) String email) {
 
 return userService.searchUsers(name, email).map(this::toDTO).take(100) // Ограничение количества результатов.doOnComplete(() -> log.info("Search completed"));
 }
 
 private UserDTO toDTO(User user) {
 return UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
 }
}
```

### Пример 2: Реактивный сервис с обработкой ошибок

```java
/**
 * Реактивный сервис для работы с пользователями
 * Демонстрирует обработку ошибок в реактивных потоках
 */
@Service
public class ReactiveUserService {
 
 @Autowired
 private ReactiveUserRepository userRepository;
 
 /**
 * Поиск пользователя с обработкой ошибок
 */
 public Mono<User> findById(Long id) {
 return userRepository.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException(id))).doOnError(error -> log.error("Error finding user: {}", id, error)).retry(3) // Повторная попытка при ошибке.onErrorResume(DataAccessException.class, ex -> {
 log.error("Database error", ex);
 return Mono.error(new ServiceException("Database unavailable", ex));
 });
 }
 
 /**
 * Создание пользователя с валидацией
 */
 public Mono<User> createUser(UserCreateRequest request) {
 return Mono.just(request).flatMap(req -> validateUser(req)).flatMap(req -> {
 User user = new User();
 user.setName(req.getName());
 user.setEmail(req.getEmail());
 return userRepository.save(user);
 }).doOnSuccess(user -> log.info("User created: {}", user.getId())).doOnError(error -> log.error("Error creating user", error));
 }
 
 /**
 * Поиск всех пользователей с пагинацией
 */
 public Flux<User> findAll(int page, int size) {
 return userRepository.findAll().skip(page * size).take(size).doOnNext(user -> log.debug("Processing user: {}", user.getId()));
 }
 
 /**
 * Поиск пользователей с фильтрацией
 */
 public Flux<User> searchUsers(String name, String email) {
 Flux<User> users = userRepository.findAll();
 
 if (name!= null &&!name.isEmpty()) {
 users = users.filter(user -> user.getName().contains(name));
 }
 
 if (email!= null &&!email.isEmpty()) {
 users = users.filter(user -> user.getEmail().contains(email));
 }
 
 return users;
 }
 
 /**
 * Обновление пользователя
 */
 public Mono<User> updateUser(Long id, UserUpdateRequest request) {
 return findById(id).flatMap(user -> {
 user.setName(request.getName());
 user.setEmail(request.getEmail());
 return userRepository.save(user);
 });
 }
 
 /**
 * Удаление пользователя
 */
 public Mono<Void> deleteUser(Long id) {
 return findById(id).flatMap(user -> userRepository.delete(user)).then();
 }
 
 private Mono<UserCreateRequest> validateUser(UserCreateRequest request) {
 if (request.getName() == null || request.getName().isEmpty()) {
 return Mono.error(new ValidationException("Name is required"));
 }
 if (request.getEmail() == null ||!request.getEmail().contains("@")) {
 return Mono.error(new ValidationException("Valid email is required"));
 }
 return Mono.just(request);
 }
}
```

### Пример 3: Использование WebClient для реактивных HTTP запросов

```java
/**
 * Реактивный клиент для вызова других микросервисов
 * Демонстрирует использование WebClient для асинхронных HTTP запросов
 */
@Service
public class ReactiveHttpClientService {
 
 private final WebClient webClient;
 
 public ReactiveHttpClientService(WebClient.Builder webClientBuilder) {
 this.webClient = webClientBuilder.baseUrl("http://api.example.com").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
 }
 
 /**
 * Получение пользователя из внешнего сервиса
 */
 public Mono<UserDTO> getUserFromExternalService(Long userId) {
 return webClient.get().uri("/users/{id}", userId).retrieve().bodyToMono(UserDTO.class).timeout(Duration.ofSeconds(5)) // Таймаут 5 секунд.retry(3) // Повторная попытка при ошибке.onErrorResume(WebClientResponseException.class, ex -> {
 if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
 return Mono.error(new UserNotFoundException(userId));
 }
 return Mono.error(new ServiceException("External service error", ex));
 });
 }
 
 /**
 * Получение списка пользователей
 */
 public Flux<UserDTO> getAllUsersFromExternalService() {
 return webClient.get().uri("/users").retrieve().bodyToFlux(UserDTO.class).take(100) // Ограничение количества.doOnError(error -> log.error("Error fetching users", error));
 }
 
 /**
 * Создание пользователя во внешнем сервисе
 */
 public Mono<UserDTO> createUserInExternalService(UserCreateRequest request) {
 return webClient.post().uri("/users").bodyValue(request).retrieve().bodyToMono(UserDTO.class).timeout(Duration.ofSeconds(10));
 }
 
 /**
 * Параллельные запросы к нескольким сервисам
 */
 public Mono<CombinedUserData> getCombinedUserData(Long userId) {
 Mono<UserDTO> userMono = getUserFromExternalService(userId);
 Mono<List<OrderDTO>> ordersMono = getOrdersForUser(userId);
 Mono<List<PaymentDTO>> paymentsMono = getPaymentsForUser(userId);
 
 // Комбинирование результатов параллельных запросов
 return Mono.zip(userMono, ordersMono, paymentsMono).map(tuple -> CombinedUserData.builder().user(tuple.getT1()).orders(tuple.getT2()).payments(tuple.getT3()).build());
 }
 
 private Mono<List<OrderDTO>> getOrdersForUser(Long userId) {
 return webClient.get().uri("/orders?userId={userId}", userId).retrieve().bodyToFlux(OrderDTO.class).collectList();
 }
 
 private Mono<List<PaymentDTO>> getPaymentsForUser(Long userId) {
 return webClient.get().uri("/payments?userId={userId}", userId).retrieve().bodyToFlux(PaymentDTO.class).collectList();
 }
}
```

### Пример 4: Обработка ошибок в реактивных потоках

```java
/**
 * Глобальный обработчик ошибок для реактивных контроллеров
 */
@ControllerAdvice
public class ReactiveExceptionHandler {
 
 private static final Logger logger = LoggerFactory.getLogger(ReactiveExceptionHandler.class);
 
 /**
 * Обработка UserNotFoundException
 */
 @ExceptionHandler(UserNotFoundException.class)
 public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(
 UserNotFoundException ex) {
 
 ErrorResponse error = ErrorResponse.builder().status(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).timestamp(LocalDateTime.now()).build();
 
 return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
 }
 
 /**
 * Обработка ValidationException
 */
 @ExceptionHandler(ValidationException.class)
 public Mono<ResponseEntity<ErrorResponse>> handleValidation(
 ValidationException ex) {
 
 ErrorResponse error = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).timestamp(LocalDateTime.now()).build();
 
 return Mono.just(ResponseEntity.badRequest().body(error));
 }
 
 /**
 * Обработка всех остальных исключений
 */
 @ExceptionHandler(Exception.class)
 public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
 logger.error("Unhandled exception", ex);
 
 ErrorResponse error = ErrorResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Internal server error").timestamp(LocalDateTime.now()).build();
 
 return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
 }
}
```

## Best Practices для Spring WebFlux

### 1. Использование правильных реактивных типов

**✅ Правильно:
```java
// Используйте Mono для одного элемента
@GetMapping("/{id}")
public Mono<UserDTO> getUser(@PathVariable Long id) {
 return userService.findById(id).map(this::toDTO);
}

// Используйте Flux для потока элементов
@GetMapping
public Flux<UserDTO> getAllUsers() {
 return userService.findAll().map(this::toDTO);
}
```

**❌ Неправильно:
```java
// Не блокируйте реактивные потоки
@GetMapping("/{id}")
public UserDTO getUser(@PathVariable Long id) { // ❌ Блокирующий вызов
 return userService.findById(id) // ❌ block() блокирует поток.block();
}
```

### 2. Обработка ошибок

**✅ Правильно:
```java
// Используйте onErrorResume для обработки ошибок
public Mono<User> findUser(Long id) {
 return userRepository.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException(id))).onErrorResume(DataAccessException.class, ex -> {
 log.error("Database error", ex);
 return Mono.error(new ServiceException("Service unavailable", ex));
 });
}
```

**❌ Неправильно:
```java
// Не используйте try-catch для обработки ошибок в реактивных потоках
public Mono<User> findUser(Long id) {
 try { // ❌ Не работает с реактивными потоками
 return userRepository.findById(id);
 } catch (Exception e) { // ❌ Ошибки не выбрасываются синхронно
 return Mono.error(e);
 }
}
```

### 3. Оптимизация производительности

**✅ Правильно:
```java
// Используйте backpressure для управления потоком данных
public Flux<User> getAllUsers() {
 return userRepository.findAll().limitRate(100) // Ограничение скорости обработки.buffer(50) // Буферизация для батчевой обработки.flatMap(users -> processBatch(users), 4); // Параллельная обработка
}
```

## Troubleshooting Spring WebFlux

### Проблема 1: Блокирующие вызовы в реактивных потоках

**Симптомы:
- Медленная обработка запросов
- Блокировка потоков
- Низкая производительность

**Решение:
```java
// Избегайте блокирующих вызовов
public Mono<User> findUser(Long id) {
 // ❌ Неправильно - блокирующий вызов
 // User user = jdbcTemplate.queryForObject(...);
 
 // ✅ Правильно - реактивный вызов
 return reactiveUserRepository.findById(id);
}

// Если необходимо использовать блокирующий код, оберните его
public Mono<String> processWithBlockingCode() {
 return Mono.fromCallable(() -> {
 // Блокирующий код в отдельном потоке
 return blockingService.process();
 }).subscribeOn(Schedulers.boundedElastic()); // Выполнение в отдельном пуле потоков
}
```

### Проблема 2: Утечки памяти в реактивных потоках

**Симптомы:
- Растущее потребление памяти
- OutOfMemoryError
- Медленная работа приложения

**Решение:
```java
// Используйте ограничения для предотвращения утечек памяти
public Flux<User> getAllUsers() {
 return userRepository.findAll().take(1000) // Ограничение количества элементов.limitRate(100) // Ограничение скорости.doOnDiscard(User.class, user -> log.debug("Discarded user: {}", user.getId()));
}

// Освобождайте ресурсы
public Flux<String> processLargeDataset() {
 return Flux.range(1, 1000000).map(this::processItem).doOnComplete(() -> log.info("Processing completed")).doFinally(signalType -> {
 // Освобождение ресурсов
 cleanup();
 });
}
```

### Проблема 3: Проблемы с таймаутами

**Симптомы:
- Таймауты при вызове внешних сервисов
- Медленные ответы
- Ошибки соединения

**Решение:
```java
// Настройка таймаутов для реактивных запросов
public Mono<UserDTO> getUserWithTimeout(Long userId) {
 return webClient.get().uri("/users/{id}", userId).retrieve().bodyToMono(UserDTO.class).timeout(Duration.ofSeconds(5)) // Таймаут 5 секунд.retry(3) // Повторная попытка.onErrorResume(TimeoutException.class, ex -> {
 log.warn("Request timeout for user: {}", userId);
 return Mono.error(new ServiceTimeoutException("Request timeout", ex));
 });
}
```

## Заключение

Spring WebFlux и Project Reactor предоставляют мощные инструменты для создания реактивных приложений. Понимание реактивного программирования критически важно для разработки масштабируемых и производительных приложений.Ключевые моменты для запоминания:

1. Mono** - для одного элемента или пустого результата
2. Flux** - для потока из нуля или более элементов
3. Неблокирующие операции** - ключ к высокой производительности
4. Обработка ошибок** - onErrorResume, onErrorReturn, retry
5. Backpressure** - управление потоком данных для предотвращения перегрузки

**Рекомендации для собеседования:

- Уметь объяснить разницу между Mono и Flux
- Знать основные операторы реактивных потоков
- Понимать, когда использовать реактивное программирование
- Уметь обрабатывать ошибки в реактивных потоках
- Знать, как тестировать реактивные приложения

### Дополнительные примеры: Использование Schedulers

```java
/**
 * Использование различных Schedulers для управления потоками
 */
@Service
public class SchedulerService {
 
 /**
 * Использование boundedElastic для блокирующих операций
 */
 public Mono<String> processBlockingOperation(String data) {
 return Mono.fromCallable(() -> {
 // Блокирующая операция
 Thread.sleep(1000);
 return "Processed: " + data;
 }).subscribeOn(Schedulers.boundedElastic()).doOnNext(result -> log.info("Result: {}", result));
 }
 
 /**
 * Использование parallel для параллельной обработки
 */
 public Flux<String> processInParallel(List<String> items) {
 return Flux.fromIterable(items).parallel(4).runOn(Schedulers.parallel()).map(item -> processItem(item)).sequential();
 }
}
```

### Дополнительные примеры: Тестирование реактивных потоков

```java
/**
 * Тестирование реактивных контроллеров
 */
@SpringBootTest
@AutoConfigureWebTestClient
class ReactiveUserControllerTest {
 
 @Autowired
 private WebTestClient webTestClient;
 
 @Test
 void testGetUser() {
 webTestClient.get().uri("/api/reactive/users/1").exchange().expectStatus().isOk().expectBody(UserDTO.class).value(user -> assertEquals("John", user.getName()));
 }
 
 @Test
 void testGetAllUsers() {
 webTestClient.get().uri("/api/reactive/users").exchange().expectStatus().isOk().expectBodyList(UserDTO.class).hasSize(10);
 }
}
```

### Дополнительные примеры: Использование WebSocket с WebFlux

```java
/**
 * WebSocket endpoint с WebFlux
 */
@Configuration
@EnableWebFlux
public class WebSocketConfig {
 
 @Bean
 public HandlerMapping webSocketHandlerMapping() {
 Map<String, WebSocketHandler> map = new HashMap<>();
 map.put("/ws", new EchoWebSocketHandler());
 
 SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
 mapping.setUrlMap(map);
 mapping.setOrder(1);
 return mapping;
 }
}
```

### Дополнительные примеры: Использование Router Functions

```java
/**
 * Функциональный подход к маршрутизации
 */
@Configuration
public class RouterConfig {
 
 @Bean
 public RouterFunction<ServerResponse> routes(UserHandler userHandler) {
 return RouterFunctions.route().GET("/api/users/{id}", userHandler::getUser).POST("/api/users", userHandler::createUser).PUT("/api/users/{id}", userHandler::updateUser).DELETE("/api/users/{id}", userHandler::deleteUser).build();
 }
}

/**
 * Handler для обработки запросов
 */
@Component
public class UserHandler {
 
 @Autowired
 private ReactiveUserService userService;
 
 public Mono<ServerResponse> getUser(ServerRequest request) {
 Long id = Long.parseLong(request.pathVariable("id"));
 return userService.findById(id).flatMap(user -> ServerResponse.ok().bodyValue(user)).switchIfEmpty(ServerResponse.notFound().build());
 }
}
```

---

**Последнее обновление: 2026-01-25
