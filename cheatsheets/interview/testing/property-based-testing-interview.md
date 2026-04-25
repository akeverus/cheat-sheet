---
title: "Вопросы на собеседовании: Property-based Testing"
description: "Property-based testing: generate test cases automatically, properties vs examples, QuickCheck, Hypothesis (Python), jqwik (Java), shrinking, vs example-based, integration"
tags:
  - interview
  - testing
  - property-based-testing-interview
aliases:
  - "Property-based testing interview"
  - "QuickCheck interview"
  - "Hypothesis interview"
  - "jqwik interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Property-based Testing`

`Property-based testing (PBT)` — testing approach, где tests describe **properties** code должен hold, framework **generates inputs automatically**. Originated с **QuickCheck** (Haskell, 2000). Modern tools: **Hypothesis** (Python), **jqwik** (Java), **fast-check** (JS), **PropEr** (Erlang). Дополняет example-based testing.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Hypothesis (Python)](https://hypothesis.readthedocs.io/)
- [QuickCheck Paper](https://www.cs.tufts.edu/~nr/cs257/archive/john-hughes/quick.pdf)
- [jqwik (Java)](https://jqwik.net/)
- [fast-check (JavaScript)](https://fast-check.dev/)
- [Property-Based Testing — F# for Fun and Profit](https://fsharpforfunandprofit.com/posts/property-based-testing/)
- [Hypothesis — Awesome Python](https://github.com/HypothesisWorks/hypothesis)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое property-based testing?](#q1--что-такое-property-based-testing)
- [Q2. (!) Property-based vs example-based?](#q2--property-based-vs-example-based)
- [Q3. (!) Зачем PBT?](#q3--зачем-pbt)

**Properties**
- [Q4. (!) Какие properties testable?](#q4--какие-properties-testable)
- [Q5. Round-trip property?](#q5-round-trip-property)
- [Q6. Idempotency property?](#q6-idempotency-property)
- [Q7. Invariant properties?](#q7-invariant-properties)

**Tools**
- [Q8. (!) QuickCheck (originalpioneer)?](#q8--quickcheck-originalpioneer)
- [Q9. (!) Hypothesis (Python)?](#q9--hypothesis-python)
- [Q10. (!) jqwik (Java)?](#q10--jqwik-java)
- [Q11. fast-check (JavaScript)?](#q11-fast-check-javascript)

**Generators**
- [Q12. (!) Что такое generator?](#q12--что-такое-generator)
- [Q13. Composing generators?](#q13-composing-generators)
- [Q14. Custom generators?](#q14-custom-generators)

**Shrinking**
- [Q15. (!) Что такое shrinking?](#q15--что-такое-shrinking)
- [Q16. Зачем shrinking важен?](#q16-зачем-shrinking-важен)

**Practice**
- [Q17. (!) Examples reverse list, sorting, parsing?](#q17--examples-reverse-list-sorting-parsing)
- [Q18. Test integration с standard tests?](#q18-test-integration-с-standard-tests)
- [Q19. Stateful PBT (model-based)?](#q19-stateful-pbt-model-based)

**Production**
- [Q20. (!) Когда применять PBT?](#q20--когда-применять-pbt)
- [Q21. (!) Limitations и criticism?](#q21--limitations-и-criticism)

## Q1. (!) Что такое property-based testing?

**Property-based testing (PBT)** — testing approach:

1. **Define property** — invariant code должен hold (для всех inputs)
2. Framework **generates many inputs** randomly
3. Property checked для each input
4. If fails — **shrink** к minimal counter-example

**Example:**
```python
@given(st.lists(st.integers()))
def test_reverse_twice_returns_original(lst):
    assert reverse(reverse(lst)) == lst
```

Hypothesis (Python) generates 100s of random lists, verifies property.

**Если fails** — Hypothesis shrinks к minimal failing case (e.g., `[1, 0]`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Property-based vs example-based? Частая ошибка в реальном коде.

**Example-based** (traditional):
```python
def test_reverse():
    assert reverse([1, 2, 3]) == [3, 2, 1]
    assert reverse([]) == []
    assert reverse([1]) == [1]
```

You write **specific inputs + expected outputs**.

**Property-based:**
```python
@given(st.lists(st.integers()))
def test_reverse_idempotent(lst):
    assert reverse(reverse(lst)) == lst  # property
```

You describe **what should be true**, framework finds inputs.

**Both complement each other.** Examples for known cases, properties для general invariants.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Зачем PBT? Частая ошибка в реальном коде.

**Reasons:**

1. **Find edge cases** you didn't think of (empty list, very large numbers, unicode, ...)
2. **More coverage** with less code (one property test = 100s of examples)
3. **Force you к think про properties** — improves code design
4. **Regressions** caught (failing inputs saved для replay)
5. **Better than fuzz testing** — has assertions

**Famous discoveries** через PBT:
- Bugs в standard libraries (Java Collections)
- Cryptography flaws
- File system inconsistencies


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) Какие properties testable? Частая ошибка в реальном коде.

**Common categories:**

1. **Round-trip:** `decode(encode(x)) == x`
2. **Idempotency:** `f(f(x)) == f(x)`
3. **Invariants:** size, sum, sorted, etc.
4. **Comparable to reference:** `optimized(x) == naive(x)`
5. **Math properties:** commutativity, associativity, distributivity
6. **Known answers:** `f(empty) == empty`
7. **Stability:** result не меняется при resort

**For любой function** — есть properties.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Round-trip property? Частая ошибка в реальном коде.

**`decode(encode(x)) == x`** для все x.

```python
@given(st.dictionaries(st.text(), st.integers()))
def test_json_roundtrip(d):
    assert json.loads(json.dumps(d)) == d
```

**Use cases:**
- Serialization (JSON, Protobuf, XML)
- Encoding (Base64, URL encoding)
- Compression (`decompress(compress(x)) == x`)
- Encryption (`decrypt(encrypt(x, key), key) == x`)

**Found bugs:** JSON libraries failing on unicode, special floats.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Idempotency property? Частая ошибка в реальном коде.

**`f(f(x)) == f(x)`** — calling twice = once.

```python
@given(st.lists(st.integers()))
def test_sort_idempotent(lst):
    assert sorted(sorted(lst)) == sorted(lst)
```

**Use cases:**
- Sorting
- Normalization (canonical forms)
- Cleanup operations


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Invariant properties? Частая ошибка в реальном коде.

**Property holds for all inputs.**

```python
@given(st.lists(st.integers()))
def test_sort_preserves_size(lst):
    assert len(sorted(lst)) == len(lst)

@given(st.lists(st.integers()))
def test_sort_preserves_sum(lst):
    assert sum(sorted(lst)) == sum(lst)
```

**Multiple invariants** caught different aspects.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) QuickCheck (originalpioneer)? Частая ошибка в реальном коде.

**QuickCheck** — original PBT framework (Haskell, 2000, Koen Claessen и John Hughes).

```haskell
prop_reverse :: [Int] -> Bool
prop_reverse xs = reverse (reverse xs) == xs

main = quickCheck prop_reverse
-- +++ OK, passed 100 tests.
```

**Influenced** every modern PBT framework. **Foundational paper** still widely cited.

**Ports к other languages:**
- ScalaCheck (Scala)
- ScalaCheck для Java
- jqwik (Java, modernized)
- Hypothesis (Python)
- fast-check (JS)
- proptest (Rust)
- gopter (Go)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Hypothesis (Python)? Частая ошибка в реальном коде.

**Hypothesis** — Python PBT framework. Most popular, mature.

```python
from hypothesis import given, strategies as st

@given(st.text())
def test_uppercase_lowercase_inverse(s):
    assert s == s.upper().lower() or contains_special(s)

@given(st.integers(min_value=1, max_value=100))
def test_factorial_positive(n):
    assert factorial(n) > 0

@given(st.dictionaries(
    keys=st.text(min_size=1),
    values=st.integers()
))
def test_dict_operations(d):
    assert len(d) >= 0
    assert all(isinstance(k, str) for k in d.keys())
```

**Strategies (`st.*`)** — built-in generators для common types.

**Examples database** — failing cases saved, replayed на next run.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) jqwik (Java)? Частая ошибка в реальном коде.

**jqwik** — modern Java PBT (replaces QuickCheck-Java).

```java
@Property
boolean concatenationLength(@ForAll List<Integer> a, @ForAll List<Integer> b) {
    return concat(a, b).size() == a.size() + b.size();
}

@Property
@StatisticsReport
void sortIsIdempotent(@ForAll @Size(max = 100) List<Integer> list) {
    List<Integer> sorted = new ArrayList<>(list);
    Collections.sort(sorted);
    List<Integer> sortedTwice = new ArrayList<>(sorted);
    Collections.sort(sortedTwice);

    Assertions.assertThat(sortedTwice).isEqualTo(sorted);
}
```

**Annotations:**
- `@Property` — property method
- `@ForAll` — generate value
- `@Size`, `@IntRange` — constrain generators

**JUnit 5 compatible** — runs alongside @Test methods.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. fast-check (JavaScript)? Частая ошибка в реальном коде.

```javascript
import fc from 'fast-check';

test('reverse is involutive', () => {
  fc.assert(
    fc.property(fc.array(fc.integer()), (data) => {
      return reverse(reverse(data)).join(',') === data.join(',');
    })
  );
});
```

**Modern, popular** в JS ecosystem.

**Used by:** ESLint, Babel, Apollo, large JS projects.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Что такое generator? Частая ошибка в реальном коде.

**Generator** = function producing random values.

**Built-in generators (Hypothesis):**
- `st.integers()`, `st.floats()`
- `st.text()`, `st.binary()`
- `st.booleans()`
- `st.lists(elements_strategy)`
- `st.dictionaries(keys_strategy, values_strategy)`
- `st.tuples(strat1, strat2)`
- `st.dates()`, `st.datetimes()`
- `st.from_regex("[a-z]{3,5}")`

**Constraints:**
```python
st.integers(min_value=0, max_value=100)
st.lists(st.integers(), min_size=1, max_size=10)
st.text(alphabet="abc", max_size=5)
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Composing generators? Частая ошибка в реальном коде.

**Combine generators** для complex types:

```python
# Generate User objects
user_strategy = st.builds(
    User,
    name=st.text(min_size=1, max_size=50),
    age=st.integers(min_value=0, max_value=150),
    email=st.emails()
)

@given(user_strategy)
def test_user_validation(user):
    assert validate(user)

# Lists of users
@given(st.lists(user_strategy, min_size=1, max_size=100))
def test_user_collection(users):
    ...
```

**`flatmap`** — generator depending на another:
```python
st.integers().flatmap(lambda n: st.lists(st.integers(), min_size=n, max_size=n))
# Lists of randomly chosen size
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Custom generators? Частая ошибка в реальном коде.

**For domain types:**

```python
@st.composite
def valid_isbn(draw):
    digits = draw(st.lists(st.integers(0, 9), min_size=12, max_size=12))
    check_digit = compute_isbn_check(digits)
    return ''.join(map(str, digits)) + str(check_digit)

@given(valid_isbn())
def test_isbn_validates(isbn):
    assert is_valid_isbn(isbn)
```

**Custom generators** для:
- Domain-specific strings (UUIDs, ISBNs)
- Complex valid objects
- State machines (для stateful PBT)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. (!) Что такое shrinking? Частая ошибка в реальном коде.

**Shrinking** — when property fails, framework **simplifies** failing input.

**Example:**
```python
@given(st.lists(st.integers()))
def test_sort_property(lst):
    assert sorted(lst)[-1] >= sorted(lst)[0]  # max >= min
```

**Bug:** fails на empty list (IndexError).

**Without shrinking:** failure shows `[42, -7, 100, 13, ...]` (random)
**With shrinking:** Hypothesis simplifies → reports failure on `[]`.

**Process:**
1. Found failing input
2. Try smaller versions (shorter list, smaller numbers)
3. Until cannot shrink further
4. Report **minimal counter-example**


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Зачем shrinking важен? Частая ошибка в реальном коде.

**Without shrinking:** debugging hard ("why is `[42, -7, 100, ...]` failing?").

**With shrinking:** **clear cause** ("empty list — case not handled").

**Modern PBT** — sophisticated shrinking algorithms.

**Hypothesis особенно good** — shrinks complex types (dicts, custom objects).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Examples reverse list, sorting, parsing? Частая ошибка в реальном коде.

**Reverse:**
```python
@given(st.lists(st.integers()))
def test_reverse(lst):
    assert reverse(reverse(lst)) == lst
    assert len(reverse(lst)) == len(lst)
    if lst:
        assert reverse(lst)[0] == lst[-1]
```

**Sorting:**
```python
@given(st.lists(st.integers()))
def test_sort(lst):
    sorted_lst = sorted(lst)
    # Properties
    assert len(sorted_lst) == len(lst)
    assert set(sorted_lst) == set(lst)  # same elements
    assert all(sorted_lst[i] <= sorted_lst[i+1] for i in range(len(sorted_lst)-1))
```

**Parsing (round-trip):**
```python
@given(st.dictionaries(st.text(), st.integers()))
def test_json(d):
    assert json.loads(json.dumps(d)) == d
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Test integration с standard tests? Частая ошибка в реальном коде.

**PBT alongside example tests:**

```python
class TestSort:
    # Example tests (specific known cases)
    def test_empty(self):
        assert sort([]) == []

    def test_already_sorted(self):
        assert sort([1, 2, 3]) == [1, 2, 3]

    # Property test (general invariants)
    @given(st.lists(st.integers()))
    def test_invariants(self, lst):
        result = sort(lst)
        assert sorted(lst) == result
```

**Best practice:**
- **Examples** для known boundary cases (specific docs)
- **Properties** для general invariants


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. Stateful PBT (model-based)? Частая ошибка в реальном коде.

**Generate sequences of operations**, verify state remains consistent.

```python
from hypothesis.stateful import RuleBasedStateMachine, rule

class StackMachine(RuleBasedStateMachine):
    def __init__(self):
        super().__init__()
        self.stack = Stack()
        self.model = []

    @rule(value=st.integers())
    def push(self, value):
        self.stack.push(value)
        self.model.append(value)

    @rule()
    def pop(self):
        if self.model:
            assert self.stack.pop() == self.model.pop()

    @invariant()
    def size_matches(self):
        assert self.stack.size() == len(self.model)
```

**Hypothesis** generates sequences (push, push, pop, pop, push, ...) — verifies parity с model.

**Found bugs in:** databases, distributed systems, complex state machines.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Когда применять PBT? Частая ошибка в реальном коде.

**Good fits:**
- **Pure functions** (sort, parse, encode)
- **Data structure operations**
- **Math, algorithms**
- **Serialization**
- **State machines**
- **Library code** (used by many)

**Less ideal:**
- **UI code** (hard to specify properties)
- **Tightly coupled с external** (DB, network)
- **Side-effect heavy** (need careful design)

**Mix PBT + example testing** — PBT не replacement.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. (!) Limitations и criticism? Частая ошибка в реальном коде.

1. **Property design hard** — what to test?
2. **False sense of coverage** — properties may miss issues
3. **Slow** для complex generators
4. **Generators imperfect** — may not cover real-world distribution
5. **Failures hard к debug** sometimes
6. **Not для all code** (UI, integration tests)
7. **Learning curve** — different mindset
8. **Maintenance** — properties evolve с code

**Не silver bullet.** Compliments other testing.

В **2025** PBT — **growing adoption** в quality-focused teams. **Hypothesis** very popular в Python world.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Unit Testing](unit-testing-interview.md) — context Это антипаттерн или неправильный выбор в production.
- [Mutation Testing](mutation-testing-interview.md) — также quality-focused
- [Test Strategies](test-strategies-interview.md)
- [Load Testing](load-testing-interview.md)
- [Chaos Engineering](chaos-engineering-interview.md) — similar mindset (find unknowns)
- [[code-quality-interview|Code Quality]]
- [Mockito](mockito-interview.md) — Java mocking
- [Integration Testing](integration-testing-interview.md)
- [Scala](../programming-languages/scala/scala-interview.md) — ScalaCheck
- [[python-interview|Python]] — Hypothesis
