# Facade (Фасад)

Проще говоря, Facade инкапсулирует сложную подсистему за простым интерфейсом. Он скрывает большую часть сложности и упрощает использование подсистемы.

**Last Updated:** 2025-01-15

## Описание

Проще говоря, Facade инкапсулирует сложную подсистему за простым интерфейсом. Он скрывает большую часть сложности и упрощает использование подсистемы. Кроме того, если нам нужно напрямую использовать сложную подсистему, мы все еще можем это сделать; мы не вынуждены использовать фасад все время.

Помимо гораздо более простого интерфейса, есть еще одно преимущество использования этого шаблона проектирования. Он отделяет реализацию клиента от сложной подсистемы. Благодаря этому мы можем вносить изменения в существующую подсистему и не затрагивать клиента.

## Useful Links

### Official Documentation
- [Design Patterns: Elements of Reusable Object-Oriented Software (GoF)](https://en.wikipedia.org/wiki/Design_Patterns)

### Baeldung
- [Facade Pattern in Java](https://www.baeldung.com/java-facade-pattern)
- [Facade Pattern in Kotlin](https://www.baeldung.com/kotlin-facade-pattern)

### See Also
- [Composite Pattern](../structural/composite.md)
- [Flyweight Pattern](../structural/flyweight.md)

## Table of Contents

- [Example: Car Engine](#example-car-engine)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)

## Example: Car Engine

Давайте посмотрим на фасад в действии.

Допустим, мы хотим завести машину. Следующая диаграмма представляет устаревшую систему, которая позволяет нам это сделать:

Как видите, это может быть довольно сложно и требует некоторых усилий для правильного запуска двигателя:

```java
airFlowController.takeAir();
fuelInjector.on();
fuelInjector.inject();
starter.start();
coolingController.setTemperatureUpperLimit(DEFAULT_COOLING_TEMP);
coolingController.run();
catalyticConverter.on();
```

Точно так же остановка двигателя также требует нескольких шагов:

```java
fuelInjector.off();
catalyticConverter.off();
coolingController.cool(MAX_ALLOWED_TEMP);
coolingController.stop();
airFlowController.off();
```

Фасад - это то, что нам здесь нужно. Всю сложность мы спрячем в двух методах: `startEngine()` и `stopEngine()`.

## Java Implementation

Давайте посмотрим, как мы можем это реализовать:

```java
public class CarEngineFacade {
    private static int DEFAULT_COOLING_TEMP = 90;
    private static int MAX_ALLOWED_TEMP = 50;

    private FuelInjector fuelInjector = new FuelInjector();
    private AirFlowController airFlowController = new AirFlowController();
    private Starter starter = new Starter();
    private CoolingController coolingController = new CoolingController();
    private CatalyticConverter catalyticConverter = new CatalyticConverter();

    public void startEngine() {
        fuelInjector.on();
        airFlowController.takeAir();
        fuelInjector.inject();
        starter.start();
        coolingController.setTemperatureUpperLimit(DEFAULT_COOLING_TEMP);
        coolingController.run();
        catalyticConverter.on();
    }

    public void stopEngine() {
        fuelInjector.off();
        catalyticConverter.off();
        coolingController.cool(MAX_ALLOWED_TEMP);
        coolingController.stop();
        airFlowController.off();
    }
}
```

Теперь, чтобы завести и остановить машину, нам нужно всего 2 строчки кода, вместо 13:

```java
CarEngineFacade facade = new CarEngineFacade();
facade.startEngine();
facade.stopEngine();
```

## Kotlin Implementation

В Kotlin паттерн Facade может быть реализован следующим образом:

```kotlin
class CarEngineFacade {
    private val DEFAULT_COOLING_TEMP = 90
    private val MAX_ALLOWED_TEMP = 50

    private val fuelInjector = FuelInjector()
    private val airFlowController = AirFlowController()
    private val starter = Starter()
    private val coolingController = CoolingController()
    private val catalyticConverter = CatalyticConverter()

    fun startEngine() {
        fuelInjector.on()
        airFlowController.takeAir()
        fuelInjector.inject()
        starter.start()
        coolingController.setTemperatureUpperLimit(DEFAULT_COOLING_TEMP)
        coolingController.run()
        catalyticConverter.on()
    }

    fun stopEngine() {
        fuelInjector.off()
        catalyticConverter.off()
        coolingController.cool(MAX_ALLOWED_TEMP)
        coolingController.stop()
        airFlowController.off()
    }
}
```

Использование:

```kotlin
fun main() {
    val facade = CarEngineFacade()
    facade.startEngine()
    facade.stopEngine()
}
```

