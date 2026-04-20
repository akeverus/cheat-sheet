---
title: "Jest"
description: "Jest — фреймворк для юнит- и интеграционных тестов в экосистеме JavaScript/TypeScript: встроенные моки (jest.fn(), jest.mock()), снапшот-тестирование, покрытие (Istanbul), поддержка ES modules и TypeScript. Часто используется с React (React Testing Library), Vue, Node.js."
tags:
  - testing
  - unit-testing
  - jest
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Jest

**Jest** — фреймворк для юнит- и интеграционных тестов в экосистеме **JavaScript**/**TypeScript**: встроенные моки (`jest.fn()`, `jest.mock()`), снапшот-тестирование, покрытие (Istanbul), поддержка **ES modules** и **TypeScript**. Часто используется с **React** (React Testing Library), **Vue**, **Node.js**.

**Дата:** 2026-02-06

## Полезные ссылки

- [Jest — Getting Started](https://jestjs.io/docs/getting-started) · [API Reference](https://jestjs.io/docs/api) · [Expect](https://jestjs.io/docs/expect)
- [Testing Library — React](https://testing-library.com/docs/react-testing-library/intro/) · [Snapshot Testing](https://jestjs.io/docs/snapshot-testing) · [Mocking](https://jestjs.io/docs/mocking)
- [Unit Testing](../) · [[junit]] · [[pytest]] · [[testing-tools-overview|Testing Tools Overview]]

## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
- [Базовое использование](#базовое-использование)
- [Моки и шпионы](#моки-и-шпионы)
- [Снапшоты](#снапшоты)
- [Покрытие кода](#покрытие-кода)
- [Таймеры и асинхронность](#таймеры-и-асинхронность)
- [Расширенные сценарии](#расширенные-сценарии)
- [CI/CD](#интеграция-с-cicd)
- [Плагины и практики](#плагины-и-расширения)
- [Решение проблем и FAQ](#решение-проблем-и-faq)
- [Заключение](#заключение)


## Введение

**Jest** — фреймворк для тестирования **JavaScript** и **TypeScript** (Meta/Facebook). Моки, снапшоты, измерение покрытия (Istanbul), поддержка **ES modules** и **TypeScript** из коробки. Часто используется с **React** (React Testing Library), **Vue**, **Node.js**, **Vite**, **Webpack**.

### Зачем Jest

- **Нулевая конфигурация** — для многих проектов достаточно `npx jest` или `npm test`.
- **Моки из коробки** — `jest.fn()`, `jest.mock()`, `jest.spyOn()` без доп. библиотек.
- **Снапшоты** — регрессионная проверка вывода (UI, JSON, строки).
- **Покрытие** — встроенная интеграция с **Istanbul** (`--coverage`).
- **Скорость** — параллельный запуск, кэширование.
- **Экосистема** — **React Testing Library**, **Vue Test Utils**, плагины для **Babel**/**TypeScript**.

### Основные концепции

| Концепция | Описание |
|-----------|----------|
| **Test** | Одна проверка: `it('should do something', () => { ... })` или `test(...)` |
| **Describe** | Группа тестов: `describe('MyModule', () => { ... })` |
| **Expect** | Утверждение: `expect(value).toBe(expected)` и матчеры |
| **Mock** | Подмена зависимости: `jest.fn()`, `jest.mock('module')` |
| **Snapshot** | Сохранённый вывод для сравнения: `expect(tree).toMatchSnapshot()` |
| **Coverage** | Отчёт о покрытии строк/ветвлений/функций |


## Установка и настройка

### Требования

- **Node.js** 14+ (рекомендуется 18+), **npm** / **yarn** / **pnpm**.

### Установка

```bash
npm install --save-dev jest
# TypeScript:
npm install --save-dev jest ts-jest @types/jest
```

В `package.json`:

```json
{
  "scripts": {
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage"
  }
}
```

**Create React App** и многие шаблоны **Vite** уже включают Jest. Запуск: `npm test`.

### Конфигурация (jest.config.js)

```javascript
// jest.config.js
module.exports = {
  testEnvironment: 'node', // или 'jsdom' для браузерного кода
  testMatch: ['**/__tests__/**/*.js', '**/*.test.js', '**/*.spec.js'],
  collectCoverageFrom: ['src/**/*.js', '!src/**/*.test.js'],
  coverageThreshold: {
    global: { branches: 80, functions: 80, lines: 80, statements: 80 }
  },
  moduleNameMapper: { '^@/(.*)$': '<rootDir>/src/$1' },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js']
};
```

Для **TypeScript** — `preset: 'ts-jest'` и `testMatch: ['**/*.test.ts', '**/*.spec.ts']`.

Проверка: `npx jest --version`.


## Базовое использование

### Простой тест

```javascript
// sum.test.js
const sum = (a, b) => a + b;

describe('sum', () => {
  it('должна возвращать 3 для 1 + 2', () => {
    expect(sum(1, 2)).toBe(3);
  });
});
```

Запуск: `npm test` или `npx jest sum.test.js`.

### Матчеры expect

| Матчер | Описание | Пример |
|--------|----------|--------|
| `toBe` | Строгое равенство (===) | `expect(1).toBe(1)` |
| `toEqual` | Глубокое равенство объектов | `expect({ a: 1 }).toEqual({ a: 1 })` |
| `toBeNull` / `toBeDefined` | `null` / не `undefined` | `expect(x).toBeDefined()` |
| `toBeTruthy` / `toBeFalsy` | Приведение к boolean | `expect(1).toBeTruthy()` |
| `toContain` | Массив/строка содержит элемент | `expect([1, 2]).toContain(1)` |
| `toMatch` | Строка совпадает с regex | `expect('hello').toMatch(/hel/)` |
| `toThrow` | Функция бросает исключение | `expect(() => fn()).toThrow()` |
| `toHaveLength` | Длина массива/строки | `expect([1, 2]).toHaveLength(2)` |
| `toMatchObject` | Объект содержит подмножество полей | `expect(obj).toMatchObject({ a: 1 })` |

Обратное утверждение: `expect(1).not.toBe(2)`.

### Асинхронные тесты

```javascript
// async/await
it('возвращает данные', async () => {
  const data = await fetchData();
  expect(data).toBe('value');
});
```

### Хуки жизненного цикла

- `beforeAll(fn)` — один раз перед всеми тестами в `describe`.
- `afterAll(fn)` — один раз после всех тестов.
- `beforeEach(fn)` — перед каждым тестом.
- `afterEach(fn)` — после каждого теста.


## Моки и шпионы

### jest.fn()

```javascript
const mockFn = jest.fn();
mockFn.mockReturnValue(42);
mockFn(1, 2);
expect(mockFn).toHaveBeenCalledWith(1, 2);
expect(mockFn).toHaveBeenCalledTimes(1);
expect(mockFn()).toBe(42);
```

### jest.mock()

```javascript
jest.mock('./api');
import { fetchUser } from './api';

it('получает пользователя', async () => {
  fetchUser.mockResolvedValue({ id: 1, name: 'John' });
  const user = await fetchUser(1);
  expect(user.name).toBe('John');
  expect(fetchUser).toHaveBeenCalledWith(1);
});
```

### jest.spyOn()

```javascript
const obj = { method: () => 'original' };
const spy = jest.spyOn(obj, 'method');
obj.method();
expect(spy).toHaveBeenCalled();
spy.mockRestore();
```

- `jest.clearAllMocks()` — очистить историю вызовов.
- `jest.resetAllMocks()` — сбросить реализацию и историю.


## Снапшоты

Сохраняет вывод в `__snapshots__/*.snap`; при следующем запуске сравнивает с сохранённым.

```javascript
it('рендерит компонент', () => {
  const tree = renderer.create(<MyComponent />).toJSON();
  expect(tree).toMatchSnapshot();
});
```

- **Инлайн-снапшот:** вызов `toMatchInlineSnapshot()` с шаблонной строкой в аргументе — снапшот хранится в самом тесте.
- **Обновление:** при ожидаемом изменении вывода: `npm test -- -u`. Использовать для стабильного вывода (сериализованный UI).


## Покрытие кода

```bash
npm test -- --coverage
```

Отчёт в консоль и в `coverage/` (HTML, lcov). Пороги в `jest.config.js`:

```javascript
coverageThreshold: {
  global: { branches: 80, functions: 80, lines: 80, statements: 80 }
}
```

Исключение файлов: `collectCoverageFrom: ['src/**/*.js', '!src/**/*.test.js']`.


## Таймеры и асинхронность

```javascript
jest.useFakeTimers();

it('вызывает callback через 1 с', () => {
  const fn = jest.fn();
  setTimeout(fn, 1000);
  expect(fn).not.toHaveBeenCalled();
  jest.advanceTimersByTime(1000);
  expect(fn).toHaveBeenCalled();
});

jest.useRealTimers();
```

- `jest.runAllTimers()` — выполнить все таймеры.
- `jest.runOnlyPendingTimers()` — только текущие ожидающие.


## Расширенные сценарии

### React (React Testing Library)

```bash
npm install --save-dev @testing-library/react @testing-library/jest-dom
```

В `jest.setup.js`: `import '@testing-library/jest-dom';`

```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import MyComponent from './MyComponent';

it('рендерит и реагирует на клик', () => {
  render(<MyComponent />);
  expect(screen.getByText('Hello')).toBeInTheDocument();
  fireEvent.click(screen.getByRole('button'));
  expect(screen.getByText('Clicked')).toBeInTheDocument();
});
```

### Vue (Vue Test Utils)

```javascript
import { mount } from '@vue/test-utils';
import MyComponent from './MyComponent.vue';

it('рендерит', () => {
  const wrapper = mount(MyComponent, { props: { msg: 'Hello' } });
  expect(wrapper.text()).toContain('Hello');
});
```

### Параметризация (test.each)

```javascript
test.each([
  [1, 2, 3],
  [0, 0, 0],
  [-1, 1, 0]
])('adds %i + %i to equal %i', (a, b, expected) => {
  expect(sum(a, b)).toBe(expected);
});
```

### Прочее

- **Переменные окружения:** `process.env.NODE_ENV = 'test'` или конфиг Jest.
- **Алиасы путей:** `moduleNameMapper` в конфиге.
- **Глобальная подготовка/очистка:** `globalSetup` и `globalTeardown` в конфиге.
- **Изоляция модулей:** `jest.isolateModules(() => { ... })`.


## Интеграция с CI/CD

### GitHub Actions

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      - run: npm ci
      - run: npm test -- --coverage
```

В **Jenkins**, **GitLab CI** после `npm ci` выполнить `npm test -- --coverage`. Пороги покрытия в конфиге приведут к exit code 1 при недостижении — пайплайн упадёт.


## Плагины и расширения

| Плагин | Назначение |
|--------|------------|
| **@testing-library/jest-dom** | Матчеры для DOM: `toBeInTheDocument()`, `toHaveClass()`, `toHaveValue()` |
| **ts-jest** | TypeScript: `preset: 'ts-jest'` в конфиге |
| **jest-extended** | Дополнительные матчеры |
| **jest-sonar-reporter** | Отчёт для SonarQube |
| **jest-html-reporter** | HTML-отчёт результатов |

### Лучшие практики

1. **Один тест — одна проверка** — не объединять несколько разных сценариев в один тест.
2. **Именование** — описательные имена: `it('должен вернуть 404, если пользователь не найден', ...)`.
3. **Изоляция** — не полагаться на порядок тестов; каждый тест независим.
4. **Моки** — мокать внешние зависимости (API, БД); не мокать тестируемый модуль.
5. **Снапшоты** — для стабильного вывода; при частых изменениях — явные ассерты.
6. **Покрытие** — стремиться к 80%+ по ветвлениям и строкам.
7. **Таймеры** — fake timers для `setTimeout`/`setInterval`, чтобы не зависеть от реального времени.


## Решение проблем и FAQ

### Таблица типичных проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Тесты не находятся | Неверный `testMatch` или структура папок | Тесты в `__tests__` или `*.test.js`; проверить конфиг |
| Модуль не мокается | Порядок импортов, ESM | `jest.mock()` до импорта; для ESM — настройка `transform` |
| Снапшоты падают | Изменён вывод | При ожидаемом изменении: `jest -u` |
| Таймауты в асинхронных тестах | Не завершённый Promise | Возвращать Promise или `async/await`; `jest.setTimeout(10000)` |
| Покрытие 0% | Неверный `collectCoverageFrom` | Проверить пути; исключить тестовые файлы |
| Ошибки ESM / TypeScript | Нет трансформера | **ts-jest** или **babel-jest**; проверить `preset` |

### Краткие ответы

- **Jest или Mocha?** Jest — моки, снапшоты, покрытие из коробки; Mocha — минимализм, Chai/Sinon отдельно. Для новых проектов чаще удобнее Jest.
- **Как тестировать приватные методы?** Через публичный API. При необходимости — отдельный экспорт для тестов.
- **Как мокать ES modules?** Jest 27+ с `experimentalVmModules` или **babel-jest**; `jest.mock('module')` с `__esModule: true`.
- **Снапшоты или явные ассерты?** Снапшоты — стабильный вывод (UI, большие объекты); явные ассерты — критичная логика и часто меняющийся код.
- **Один тест:** `npx jest -t "имя теста"` или `npx jest path/to/file.test.js`.
- **Default export:** `jest.mock('./module', () => ({ __esModule: true, default: jest.fn() }));`
- **Таймаут:** `jest.setTimeout(10000)` или третий аргумент `it('name', fn, 10000)`.
- **Пропустить тест:** `it.skip('name', fn)` или `xit`. **Только один:** `it.only('name', fn)` или `fit`.
- **Ошибки:** `expect(() => fn()).toThrow('message')` или `toThrow(ErrorType)`.
- **Очистка моков:** `beforeEach(() => jest.clearAllMocks())` или `afterEach(() => jest.restoreAllMocks())`.

### Ключевые команды и API

| Команда / API | Описание |
|---------------|----------|
| `npm test` | Запуск тестов |
| `npm test -- --watch` | Режим наблюдения |
| `npm test -- --coverage` | С покрытием |
| `npm test -- -u` | Обновить снапшоты |
| `npm test -- -t "name"` | Тесты по имени |
| `describe` / `it` / `test` | Группа тестов / один тест |
| `expect(value).матчер()` | Утверждение |
| `jest.fn()` / `jest.mock()` / `jest.spyOn()` | Моки и шпионы |
| `jest.useFakeTimers()` / `advanceTimersByTime(ms)` | Подмена таймеров |


## Заключение

**Jest** — мощный фреймворк для тестирования **JavaScript** и **TypeScript** с встроенными моками, снапшотами и покрытием. Моки — для изоляции, снапшоты — для стабильного вывода, покрытие — для контроля качества. Для **React** — **React Testing Library**, для **Vue** — **Vue Test Utils**. Документация: [Jest](https://jestjs.io/docs/getting-started), [Unit Testing](../), [[pytest]], [[junit]].

*Дата: 2026-02-06*
