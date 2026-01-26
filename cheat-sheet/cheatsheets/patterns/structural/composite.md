# Composite (Композиция)

Шаблон Композиция предназначен для того, чтобы можно было одинаково обрабатывать отдельные объекты и композиции объектов или «композиты».

**Last Updated:** 2025-01-15

## Описание

Шаблон Композиция предназначен для того, чтобы можно было одинаково обрабатывать отдельные объекты и композиции объектов или «композиты». Его можно рассматривать как древовидную структуру, состоящую из типов, наследующих базовый тип, и он может представлять собой одну часть или целую иерархию объектов.

## Useful Links

### Official Documentation
- [Design Patterns: Elements of Reusable Object-Oriented Software (GoF)](https://en.wikipedia.org/wiki/Design_Patterns)

### Baeldung
- [Composite Pattern in Java](https://www.baeldung.com/java-composite-pattern)
- [Composite Pattern in Kotlin](https://www.baeldung.com/kotlin-composite-pattern)

### See Also
- [Facade Pattern](../structural/facade.md)
- [Flyweight Pattern](../structural/flyweight.md)

## Table of Contents

- [Components](#components)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)

## Components

Мы можем разбить шаблон на:

- **Component:** это базовый интерфейс для всех объектов в композиции. Это должен быть либо интерфейс, либо абстрактный класс с общими методами для управления дочерними композитами.

- **Leaf:** реализует поведение базового компонента по умолчанию. Он не содержит ссылки на другие объекты.

- **Composite:** имеет листовые элементы. Он реализует методы базового компонента и определяет дочерние операции.

- **Client:** имеет доступ к элементам композиции, используя объект базового компонента.

## Java Implementation

В качестве объекта-компонента мы определим простой интерфейс отдела:

```java
public interface Department {
    void printDepartmentName();
}
```

Для конечных компонентов определим классы для финансового отдела и отдела продаж:

```java
public class FinancialDepartment implements Department {
    private Integer id;
    private String name;

    public FinancialDepartment(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void printDepartmentName() {
        System.out.println(getClass().getSimpleName());
    }
}
```

Второй конечный класс, `SalesDepartment`, аналогичен:

```java
public class SalesDepartment implements Department {
    private Integer id;
    private String name;

    public SalesDepartment(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void printDepartmentName() {
        System.out.println(getClass().getSimpleName());
    }
}
```

Оба класса реализуют метод `printDepartmentName()` из базового компонента, где они печатают имена классов для каждого из них.

Кроме того, поскольку они являются конечными классами, они не содержат других объектов отдела.

Далее, давайте также рассмотрим составной класс.

В качестве составного класса создадим класс `HeadDepartment`:

```java
public class HeadDepartment implements Department {
    private Integer id;
    private String name;
    private List<Department> childDepartments;

    public HeadDepartment(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.childDepartments = new ArrayList<>();
    }

    @Override
    public void printDepartmentName() {
        childDepartments.forEach(Department::printDepartmentName);
    }

    public void addDepartment(Department department) {
        childDepartments.add(department);
    }

    public void removeDepartment(Department department) {
        childDepartments.remove(department);
    }
}
```

Это составной класс, так как он содержит коллекцию компонентов отдела, а также методы добавления и удаления элементов из списка.

Составной метод `printDepartmentName()` реализуется путем перебора списка конечных элементов и вызова соответствующего метода для каждого из них.

В целях тестирования давайте взглянем на класс `CompositeDemo`:

```java
public class CompositeDemo {
    public static void main(String args[]) {
        Department salesDepartment = new SalesDepartment(1, "Sales department");
        Department financialDepartment = new FinancialDepartment(2, "Financial department");

        HeadDepartment headDepartment = new HeadDepartment(3, "Head department");

        headDepartment.addDepartment(salesDepartment);
        headDepartment.addDepartment(financialDepartment);

        headDepartment.printDepartmentName();
    }
}
```

Во-первых, мы создаем два экземпляра для финансового отдела и отдела продаж. После этого мы создаем головной отдел и добавляем к нему ранее созданные экземпляры.

Наконец, мы можем протестировать метод композиции `printDepartmentName()`. Как мы и ожидали, вывод содержит имена классов каждого листового компонента:

```
SalesDepartment
FinancialDepartment
```

## Kotlin Implementation

В Kotlin паттерн Composite может быть реализован следующим образом:

```kotlin
interface Department {
    fun printDepartmentName()
}

class FinancialDepartment(private val id: Int, private val name: String) : Department {
    override fun printDepartmentName() {
        println(this::class.simpleName)
    }
}

class SalesDepartment(private val id: Int, private val name: String) : Department {
    override fun printDepartmentName() {
        println(this::class.simpleName)
    }
}

class HeadDepartment(private val id: Int, private val name: String) : Department {
    private val childDepartments = mutableListOf<Department>()

    override fun printDepartmentName() {
        childDepartments.forEach { it.printDepartmentName() }
    }

    fun addDepartment(department: Department) {
        childDepartments.add(department)
    }

    fun removeDepartment(department: Department) {
        childDepartments.remove(department)
    }
}
```

Использование:

```kotlin
fun main() {
    val salesDepartment = SalesDepartment(1, "Sales department")
    val financialDepartment = FinancialDepartment(2, "Financial department")

    val headDepartment = HeadDepartment(3, "Head department")
    headDepartment.addDepartment(salesDepartment)
    headDepartment.addDepartment(financialDepartment)

    headDepartment.printDepartmentName()
}
```

