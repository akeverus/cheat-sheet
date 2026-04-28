---
title: "State Machine with Enums"
description: "A guide to implementing finite state machines in Java using Enums, providing a clean and type-safe approach to state management."
tags:
  - algorithms
  - problems
  - state-machine-with-enums
type: "reference"
difficulty: "intermediate"
aliases:
  - "State Machine with Enums"
prerequisites: []
next: []
updated: "2026-04-20"
---
# State Machine with Enums

A **guide** to **implementing finite state machines** in **Java using Enums**, **providing** a **clean and type-safe approach** to **state management**.

## Полезные ссылки

### Официальная документация
- [Java Enums](https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html)
- [State Design Pattern](https://refactoring.guru/design-patterns/state)

### См. также
- [Валидация конечным автоматом](finite-automata-input-validation.md)
- [Паттерны проектирования](../../patterns/)

- [OptaPlanner](optaplanner.md)
- [Задача о рюкзаке (Knapsack Problem)](knapsack-problem.md)
- [Валидация банковских карт (Credit Card Validation)](credit-card-validation.md)
- [Гистограмма частот с Apache Commons (Frequency Histogram Apache Commons)](frequency-histogram-apache-commons.md)
## Содержание

- [Обзор](#обзор)
- [What are Java Enums?](#what-are-java-enums)
- [Basic Enum Example](#basic-enum-example)
- [Enums with Methods](#enums-with-methods)
- [Что такое конечный автомат?](#что-такое-конечный-автомат)
- [Реализация на Java](#реализация-на-java)
  - [State Machine Implementation](#state-machine-implementation)
- [Testing the State Machine](#testing-the-state-machine)
- [Полный пример](#полный-пример)
- [Advantages of Enum-Based State Machines](#advantages-of-enum-based-state-machines)
  - [Comparison with Interface-Based Approach](#comparison-with-interface-based-approach)
- [Лучшие практики](#лучшие-практики)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Enum State Machine](#enum-state-machine)
  - [LeaveRequest Class](#leaverequest-class)
  - [Пример использования](#пример-использования)
- [Заключение](#заключение)

## Обзор

In **this tutorial**, we'll **look** at **finite state machines and how they can** be **implemented** in **Java using Enums**. We'll **also explain the advantages** of **this implementation compared** to **using** an **interface and concrete class for each state**.

## What are Java Enums?

**Java Enum** is a **special type** of **class that defines** a **list** of **constants**. **This allows for** a **type-safe implementation and makes code more readable**.

`As an` **example**, **suppose** we **have** a **software** `HR` **management system that can approve leave requests sent** by **employees**. **This request** is **reviewed** by a **team leader**, **who passes** it to a **department manager**. **The department manager** is **the person responsible for approving the request**.

## Basic Enum Example

**The simplest enumeration that contains leave request states**:**

```java
// Состояния заявки на отпуск: Submitted → Escalated → Approved
public enum LeaveRequestState {
    Submitted,
    Escalated,
    Approved
}
```

**We **can refer** to **the constants** of **this enumeration**:**

```java
LeaveRequestState state = LeaveRequestState.Submitted;
```

## Enums with Methods

**Enumerations can also contain methods**. We **can write** an **abstract method** in an **enumeration**, **which will force each enumeration instance** to **implement this method**. **This** is **very important for implementing finite state machines**, as we'll **see below**.

**Since Java enumerations implicitly extend the** `java.lang.Enum` **class**, **they cannot extend another class**. **However**, **they can implement** an **interface**, **just like any other class**.

**Here**'s an **example enumeration containing** an **abstract method**:**

```java
public enum LeaveRequestState {
    Submitted {
        @Override
        public String responsiblePerson() {
            return "Employee";
        }
    },

    Escalated {
        @Override
        public String responsiblePerson() {
            return "Team Leader";
        }
    },

    Approved {
        @Override
        public String responsiblePerson() {
            return "Department Manager";
        }
    };

    public abstract String responsiblePerson();
}
```

**Note the use** of a **semicolon** at **the end** of **the last enumeration constant**. **The semicolon** is **required when** we **have one** or **more methods following the constants**.

**In **this case**, we've **extended the first example with the** `responsiblePerson()` **method**. **This tells** us **the person responsible for performing each action**. `So if we` **try** to **check the person responsible for the Escalated state**, it **will give** us "**Team Leader**":**

```java
LeaveRequestState state = LeaveRequestState.Escalated;
assertEquals("Team Leader", state.responsiblePerson());
```

**Similarly**, if we **check who** is **responsible for approving the request**, it **will give** us "**Department Manager**":**

```java
LeaveRequestState state = LeaveRequestState.Approved;
assertEquals("Department Manager", state.responsiblePerson());
```

## Что такое конечный автомат?

A **finite state machine**, **also called** a **finite automaton** or **state machine**, is a **computational model used** to **build** an **abstract machine**. **These machines can only** be in **one state** at a **given time**. **Each state** is a **state** of **the system that transitions** to **another state**. **These state changes are called transitions**.

In **mathematics**, **with diagrams and notation**, it **can** be **complex**, **but for** us **programmers**, **everything** is **much simpler**.

**The State pattern** is **one** of **the well-known twenty-three GoF design patterns**. **This pattern borrows the concept from the model** in **mathematics**. It **allows** an **object** to **encapsulate different behavior** of **the same object depending** on **its state**. We **can program the transition between states and then define individual states**.

To **better explain the concept**, we'll **extend our leave request example** to **implement** a **finite state machine**.

We'll **focus** on **implementing enum finite state machines** in **Java**. **Other implementations are possible**, **and** we'll **compare them** in **the following section**.

## Реализация на Java

### State Machine Implementation

**The main point** of **implementing** a **finite state machine using** an **enumeration** is **that** we **don**'t **need** to **deal with explicitly setting states**. **Instead**, we **can simply provide the logic for transitioning from one state** to **another**. **Let**'s **dive right** in:**

```java
public enum LeaveRequestState {
    Submitted {
        @Override
        public LeaveRequestState nextState() {
            return Escalated;
        }

        @Override
        public String responsiblePerson() {
            return "Employee";
        }
    },

    Escalated {
        @Override
        public LeaveRequestState nextState() {
            return Approved;
        }

        @Override
        public String responsiblePerson() {
            return "Team Leader";
        }
    },

    Approved {
        @Override
        public LeaveRequestState nextState() {
            return this;  // Final state, no transition
        }

        @Override
        public String responsiblePerson() {
            return "Department Manager";
        }
    };

    public abstract LeaveRequestState nextState();
    public abstract String responsiblePerson();
}
```

In **this example**, **the finite state machine transitions are implemented using abstract enumeration methods**. **More precisely**, by **using** `nextState()` **for each enumeration constant**, we **specify the transition** to **the next state**. If **needed**, we **can also implement** a `previousState()` **method**.

## Testing the State Machine

**Here**'s a **test** to **verify our implementation**:**

```java
@Test
public void testStateTransitions() {
    LeaveRequestState state = LeaveRequestState.Submitted;

    state = state.nextState();
    assertEquals(LeaveRequestState.Escalated, state);

    state = state.nextState();
    assertEquals(LeaveRequestState.Approved, state);

    state = state.nextState();
    assertEquals(LeaveRequestState.Approved, state);  // Final state
}
```

We **start the leave request** in **the initial state Submitted**. **Then** we **verify the transitions between states using the** `nextState()` **method** we **implemented above**.

**Note that since Approved** is a **final state**, no **other transition can occur**.

## Полный пример

**Here**'s a **complete example with** a **LeaveRequest class that uses the state machine**:**

```java
public class LeaveRequest {
    private LeaveRequestState state;
    private String employeeName;
    private int days;

    public LeaveRequest(String employeeName, int days) {
        this.employeeName = employeeName;
        this.days = days;
        this.state = LeaveRequestState.Submitted;
    }

    public void escalate() {
        if (state == LeaveRequestState.Submitted) {
            state = state.nextState();
            System.out.println("Request escalated to: " + state.responsiblePerson());
        } else {
            System.out.println("Cannot escalate from state: " + state);
        }
    }

    public void approve() {
        if (state == LeaveRequestState.Escalated) {
            state = state.nextState();
            System.out.println("Request approved by: " + state.responsiblePerson());
        } else {
            System.out.println("Cannot approve from state: " + state);
        }
    }

    public LeaveRequestState getState() {
        return state;
    }

    public String getResponsiblePerson() {
        return state.responsiblePerson();
    }

    @Override
    public String toString() {
        return String.format("LeaveRequest[employee=%s, days=%d, state=%s, responsible=%s]",
                employeeName, days, state, state.responsiblePerson());
    }
}
```

**Usage example**:**

```java
public class LeaveRequestDemo {
    public static void main(String[] args) {
        LeaveRequest request = new LeaveRequest("John Doe", 5);
        System.out.println(request);

        request.escalate();
        System.out.println(request);

        request.approve();
        System.out.println(request);

        // Try to escalate again (should fail)
        request.escalate();
    }
}
```

**Output**:

```text
LeaveRequest[employee=John Doe, days=5, state=Submitted, responsible=Employee]
Request escalated to: Team Leader
LeaveRequest[employee=John Doe, days=5, state=Escalated, responsible=Team Leader]
Request approved by: Department Manager
LeaveRequest[employee=John Doe, days=5, state=Approved, responsible=Department Manager]
Cannot escalate from state: Approved
```

## Advantages of Enum-Based State Machines

**Implementing finite state machines with interfaces and implementation classes can require** a **significant amount** of **code** to **develop and maintain**.

**Since** a **Java enumeration** in **its simplest form** is a **list** of **constants**, we **can use** an **enumeration** to **define our states**. **And since** an **enumeration can also contain behavior**, we **can use methods** to **provide the implementation** of **transitions between states**.

**Having all the logic** in a **simple enumeration allows for** a **clean and straightforward solution**.

### Comparison with Interface-Based Approach

**Enum-based approach:**
- **All states defined** in **one place**
- **Type-safe state transitions**
- **Compile-time checking**
- **Less boilerplate code**
- **Easy** to **understand and maintain**

**Interface-based approach:**
- **Requires separate class for each state**
- **More verbose**
- **More files** to **maintain**
- **Runtime state validation**

## Лучшие практики

- **Типобезопасность: Enum** для состояний исключает невалидные значения; переходы задавайте в самом **enum** (метод `nextState` или Map) или в отдельном классе контекста.
- **Иммутабельность состояний:** Не храните изменяемое состояние в **enum**-константах; контекст (текущее состояние, данные) держите в отдельном объекте.
- **Тестирование:** Покройте все переходы и недопустимые переходы (ошибка или игнор); проверяйте начальное и конечное состояния.
- **Расширяемость:** При добавлении состояний обновляйте все переходы; рассмотрите таблицу переходов (Map) для сложных автоматов.
- **Документирование:** Описывайте диаграмму состояний или список допустимых переходов в комментариях или документации.

## Реализация на Kotlin

### Enum State Machine

```kotlin
enum class LeaveRequestStateK {
    Submitted {
        override fun nextState(): LeaveRequestStateK = Escalated
        override fun responsiblePerson(): String = "Employee"
    },

    Escalated {
        override fun nextState(): LeaveRequestStateK = Approved
        override fun responsiblePerson(): String = "Team Leader"
    },

    Approved {
        override fun nextState(): LeaveRequestStateK = this // Final state
        override fun responsiblePerson(): String = "Department Manager"
    };

    abstract fun nextState(): LeaveRequestStateK
    abstract fun responsiblePerson(): String
}
```

### LeaveRequest Class

```kotlin
class LeaveRequestK(
    var state: LeaveRequestStateK = LeaveRequestStateK.Submitted,
    val employeeName: String
) {
    fun nextState() {
        state = state.nextState()
    }

    fun getResponsiblePerson(): String {
        return state.responsiblePerson()
    }
}
```

### Пример использования

```kotlin
fun main() {
    val request = LeaveRequestK(employeeName = "John Doe")

    println("Initial state: ${request.state}")
    println("Responsible: ${request.getResponsiblePerson()}")

    request.nextState()
    println("After escalation: ${request.state}")
    println("Responsible: ${request.getResponsiblePerson()}")

    request.nextState()
    println("After approval: ${request.state}")
    println("Responsible: ${request.getResponsiblePerson()}")
}
```

## Заключение

In **this article**, we've **covered finite state machines and how they can** be **implemented** in **Java using Enums**. We **provided** an **example and tested** it.

In **the end**, we **also discussed the advantages** of **using enumerations for implementing finite state machines**. `As an` **alternative** to **the interface and implementation solution**, **enumerations provide** a **cleaner and more understandable implementation** of **finite state machines**.
