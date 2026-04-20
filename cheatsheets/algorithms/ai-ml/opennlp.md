---
title: "Руководство по Apache OpenNLP"
description: "Руководство по использованию Apache OpenNLP для обработки естественного языка в Java, включая токенизацию, распознавание именованных сущностей, тегирование POS и другие задачи NLP."
tags:
  - algorithms
  - ai-ml
  - opennlp
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Руководство по Apache OpenNLP

Руководство по использованию **Apache OpenNLP** для обработки естественного языка в **Java**, включая токенизацию, распознавание именованных сущностей, тегирование **POS** и другие задачи **NLP**.

## Полезные ссылки

### Официальная документация
- [Apache OpenNLP Documentation](https://opennlp.apache.org/)
- [OpenNLP Models](https://opennlp.apache.org/)

### См. также
- [[ai-libraries|Обзор ИИ библиотек]] — **NLP** и библиотеки
- [[README|Алгоритмы со строками]] — раздел strings

## Содержание

- [Обзор](#обзор)
- [Настройка проекта](#настройка-проекта)
- [Реализация на Java](#реализация-на-java)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)


## Обзор

**Apache OpenNLP** — это библиотека **Java** для обработки естественного языка с открытым исходным кодом.

Он имеет **API** для таких вариантов использования, как распознавание именованных объектов, обнаружение предложений, тегирование **POS** и токенизация.

В этом руководстве мы рассмотрим, как использовать этот **API** для различных вариантов использования.

## Настройка проекта

Зависимость Maven:

```xml
<!-- OpenNLP: токенизация, NER, POS, обнаружение предложений -->
<dependency>
    <groupId>org.apache.opennlp</groupId>
    <artifactId>opennlp-tools</artifactId>
    <version>1.8.4</version>
</dependency>
```

Для некоторых вариантов использования требуются обученные модели. Вы можете скачать готовые модели здесь и подробную информацию об этих моделях здесь.

## Реализация на Java

### Обнаружение предложений (Java)

Начнем с понимания того, что такое предложение.

Обнаружение предложений заключается в определении начала и конца предложения, что обычно зависит от используемого языка. Это также называется «устранение неоднозначности границ предложения» (SBD).

В некоторых случаях определение предложения довольно сложно из-за неоднозначного характера символа точки. Точка обычно обозначает конец предложения, но также может появляться в адресе электронной почты, аббревиатуре, десятичной дроби и многих других местах.

Как и в большинстве задач **NLP**, для обнаружения предложений нам нужна обученная модель в качестве входных данных, которая, как мы ожидаем, будет находиться в папке /**resources**.

**Чтобы реализовать обнаружение предложений, мы загружаем модель и передаем ее в экземпляр **SentenceDetectorME**. Затем мы просто передаем текст в метод **sentDetect**(), чтобы разделить его по границам предложений:**

```java
// OpenNLP: обнаружение границ предложений по обученной модели (SentenceModel), sentDetect() — разбиение текста.
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import static org.assertj.core.api.Assertions.assertThat;

public class SentenceDetectionTest {

    @Test
    public void givenEnglishModel_whenDetect_thenSentencesAreDetected() throws Exception {
        String paragraph = "This is a statement. This is another statement. "
            + "Now is an abstract word for time, "
            + "that is always flying. And my email address is google@gmail.com.";

        InputStream is = getClass().getResourceAsStream("/models/en-sent.bin");
        SentenceModel model = new SentenceModel(is);
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        String sentences[] = sdetector.sentDetect(paragraph);

        assertThat(sentences).contains(
            "This is a statement. ",
            "This is another statement. ",
            "Now is an abstract word for time, that is always flying. ",
            "And my email address is google@gmail.com."
        );
    }
}
```

Примечание: суффикс «`ME`» используется во многих именах классов в **Apache OpenNLP** и представляет собой алгоритм, основанный на «Максимальной энтропии».

## Токенизация

Теперь, когда мы можем разделить корпус текста на предложения, мы можем приступить к более подробному анализу предложения.

Цель токенизации — разделить предложение на более мелкие части, называемые токенами. Обычно эти токены представляют собой слова, цифры или знаки препинания.

В **OpenNLP** доступно три типа токенизаторов.

### TokenizerME

В этом случае нам сначала нужно загрузить модель. Мы можем скачать файл модели отсюда, поместить его в папку /**resources** и загрузить оттуда.

**Далее мы создадим экземпляр **TokenizerME**, используя загруженную модель, и используем метод **tokenize**() для выполнения токенизации любой строки:**

```java
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

@Test
public void givenEnglishModel_whenTokenize_thenTokensAreDetected() throws Exception {
    InputStream inputStream = getClass()
        .getResourceAsStream("/models/en-token.bin");
    TokenizerModel model = new TokenizerModel(inputStream);
    TokenizerME tokenizer = new TokenizerME(model);

    String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");

    assertThat(tokens).contains(
        "Baeldung", "is", "a", "Spring", "Resource", "."
    );
}
```

Как мы видим, токенизатор идентифицировал все слова и символ точки как отдельные токены. Этот токенизатор также можно использовать с настраиваемой обученной моделью.

### WhitespaceTokenizer

**Как следует из названия, этот токенизатор просто разбивает предложение на токены, используя символы пробела в качестве разделителей:**

```java
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Test
public void givenWhitespaceTokenizer_whenTokenize_thenTokensAreDetected() throws Exception {
    WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");

    assertThat(tokens)
        .contains("Baeldung", "is", "a", "Spring", "Resource.");
}
```

Мы видим, что предложение было разделено пробелами, и, следовательно, мы получаем «**Resource**.» (с символом точки в конце) как один токен вместо двух разных токенов для слова «**Resource**» и символа точки.

### SimpleTokenizer

**Этот токенизатор немного сложнее, чем **WhitespaceTokenizer**, и разбивает предложение на слова, цифры и знаки препинания. Это поведение по умолчанию и не требует никакой модели:**

```java
import opennlp.tools.tokenize.SimpleTokenizer;

@Test
public void givenSimpleTokenizer_whenTokenize_thenTokensAreDetected() throws Exception {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");

    assertThat(tokens)
        .contains("Baeldung", "is", "a", "Spring", "Resource", ".");
}
```

## Распознавание именованных сущностей (NER)

Теперь, когда мы поняли токенизацию, давайте рассмотрим первый вариант использования, основанный на успешной токенизации: распознавание именованных объектов (NER).

Цель **NER** — найти именованные объекты, такие как люди, местоположения, организации и другие именованные объекты в заданном тексте.

**OpenNLP** использует предопределенные модели для имен людей, даты и времени, местоположений и организаций. Нам нужно загрузить модель с помощью **TokenNameFinderModel** и передать ее экземпляру **NameFinderME**. Затем мы можем использовать метод **find**() для поиска именованных сущностей в заданном тексте:**

```java
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

@Test
public void givenEnglishPersonModel_whenNER_thenPersonsAreDetected() throws Exception {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize(
        "John is 26 years old. His best friend's "
        + "name is Leonard. He has a sister named Penny."
    );

    InputStream inputStreamNameFinder = getClass()
        .getResourceAsStream("/models/en-ner-person.bin");
    TokenNameFinderModel model = new TokenNameFinderModel(inputStreamNameFinder);
    NameFinderME nameFinderME = new NameFinderME(model);

    List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

    assertThat(spans.toString())
        .isEqualTo("[[0..1) person, [13..14) person, [20..21) person]");
}
```

Как видно из утверждения, результатом является список объектов **Span**, содержащих начальные и конечные индексы токенов, составляющих именованные сущности в тексте.

## Тегирование части речи (POS)

Другой вариант использования, для которого в качестве входных данных требуется список токенов, - это тегирование части речи.

**Часть речи (POS) определяет тип слова. **OpenNLP** использует следующие теги для разных частей речи:**

1. **NN** — существительное в единственном числе или масса
2. **DT** — определитель
3. **VB** — глагол, основная форма
4. **VBD** — глагол в прошедшем времени
5. **VBZ** — глагол в третьем лице единственного числа настоящего времени
6. **IN** — предлог или подчинительный союз
7. **NNP** — имя собственное, единственное число
8. **TO** — слово «to»
9. **JJ** — прилагательное

Это те же теги, что и в банке **Penn Tree Bank**. Полный список см. в этом списке.

**Как и в примере с **NER**, мы загружаем соответствующую модель, а затем используем **POSTaggerME** и его метод **tag**() для набора токенов, чтобы пометить предложение:**

```java
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

@Test
public void givenPOSModel_whenPOSTagging_thenPOSAreDetected() throws Exception {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("John has a sister named Penny.");

    InputStream inputStreamPOSTagger = getClass()
        .getResourceAsStream("/models/en-pos-maxent.bin");
    POSModel posModel = new POSModel(inputStreamPOSTagger);
    POSTaggerME posTagger = new POSTaggerME(posModel);

    String tags[] = posTagger.tag(tokens);

    assertThat(tags).contains("NNP", "VBZ", "DT", "NN", "VBN", "NNP", ".");
}
```

**Метод **tag**() отображает токены в список тегов **POS**. Результат в примере:**

1. «**John**» — **NNP** (имя собственное)
2. «**has**» — **VBZ** (глагол)
3. «a» - `DT` (определитель)
4. «**sister**» - `NN` (существительное)
5. «**named**» — **VBN** (глагол в прошедшем времени)
6. «**Penny**» — **NNP** (имя собственное)
7. «.» — период

## Лемматизация

Теперь, когда у нас есть информация о частях речи токенов в предложении, мы можем еще больше проанализировать текст.

Лемматизация — это процесс сопоставления словоформы, которая может иметь время, род, наклонение или другую информацию, с базовой формой слова, также называемой «леммой».

Лемматизатор принимает токен и его часть речи в качестве входных данных и возвращает лемму слова. Следовательно, перед лемматизацией предложение должно пройти через токенизатор и **POS**-теггер.

**Apache OpenNLP** предоставляет два типа лемматизации:**

1. **Статистический** — требуется модель лемматизатора, построенная с использованием обучающих данных для нахождения леммы заданного слова
2. **На основе словаря** — требуется словарь, который содержит все допустимые комбинации слова, тегов **POS** и соответствующей леммы

Для статистической лемматизации нам нужно обучить модель, тогда как для лемматизации словаря нам просто нужен файл словаря, подобный этому.

**Давайте посмотрим на пример кода с использованием файла словаря:**

```java
import opennlp.tools.lemmatizer.DictionaryLemmatizer;

@Test
public void givenEnglishDictionary_whenLemmatize_thenLemmasAreDetected() throws Exception {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize("John has a sister named Penny.");

    InputStream inputStreamPOSTagger = getClass()
        .getResourceAsStream("/models/en-pos-maxent.bin");
    POSModel posModel = new POSModel(inputStreamPOSTagger);
    POSTaggerME posTagger = new POSTaggerME(posModel);
    String tags[] = posTagger.tag(tokens);

    InputStream dictLemmatizer = getClass()
        .getResourceAsStream("/models/en-lemmatizer.dict");
    DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);

    String[] lemmas = lemmatizer.lemmatize(tokens, tags);

    assertThat(lemmas)
        .contains("O", "have", "a", "sister", "name", "O", "O");
}
```

Как мы видим, мы получаем лемму для каждой фишки. «О» означает, что лемма не может быть определена, так как слово является именем собственным. Итак, у нас нет леммы для «Джон» и «Пенни».

**Но мы определили леммы для других слов предложения:**
- **has** → **have**
- a → a
- **sister** → **sister**
- **named** → **name**

## Чанкинг

Информация о частях речи также важна для разделения предложений на грамматически значимые группы слов, такие как группы существительных или группы глаголов.

**Как и раньше, мы токенизируем предложение и используем теги частей речи для токенов перед вызовом метода **chunk**():**

```java
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;

@Test
public void givenChunkerModel_whenChunk_thenChunksAreDetected() throws Exception {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize(
        "He reckons the current account deficit will narrow to only 8 billion."
    );

    InputStream inputStreamPOSTagger = getClass()
        .getResourceAsStream("/models/en-pos-maxent.bin");
    POSModel posModel = new POSModel(inputStreamPOSTagger);
    POSTaggerME posTagger = new POSTaggerME(posModel);
    String tags[] = posTagger.tag(tokens);

    InputStream inputStreamChunker = getClass()
        .getResourceAsStream("/models/en-chunker.bin");
    ChunkerModel chunkerModel = new ChunkerModel(inputStreamChunker);
    ChunkerME chunker = new ChunkerME(chunkerModel);

    String[] chunks = chunker.chunk(tokens, tags);

    assertThat(chunks).contains(
        "B-NP", "B-VP", "B-NP", "I-NP",
        "I-NP", "I-NP", "B-VP", "I-VP",
        "B-PP", "B-NP", "I-NP", "I-NP", "O"
    );
}
```

Как мы видим, мы получаем вывод для каждого токена из чанкера. «B» представляет начало фрагмента, «I» представляет продолжение фрагмента, а «O» представляет отсутствие фрагмента.

**Разбирая вывод из нашего примера, мы получаем 6 чанков:**

1. «He» — словосочетание (NP)
2. «**reckons**» — глагольная фраза (VP)
3. «**the current account deficit**» — словосочетание (NP)
4. «**will narrow**» — глагольная фраза (VP)
5. «to» — предлог (PP)
6. «**only** 8 **billion**» — словосочетание (NP)

## Определение языка

В дополнение к уже рассмотренным вариантам использования **OpenNLP** также предоставляет **API** для определения языка, который позволяет идентифицировать язык определенного текста.

Для определения языка нам нужен файл обучающих данных. Такой файл содержит строки с предложениями на определенном языке. Каждая строка помечена правильным языком, чтобы обеспечить входные данные для алгоритмов машинного обучения.

Образец файла обучающих данных для определения языка можно скачать здесь.

**Мы можем загрузить файл данных обучения в **LanguageDetectorSampleStream**, определить некоторые параметры данных обучения, создать модель, а затем использовать модель для определения языка текста:**

```java
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

@Test
public void givenLanguageDictionary_whenLanguageDetect_thenLanguageIsDetected()
    throws FileNotFoundException, IOException {

    InputStreamFactory dataIn = new MarkableFileInputStreamFactory(
        new File("src/main/resources/models/DoccatSample.txt")
    );
    ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
    LanguageDetectorSampleStream sampleStream = new LanguageDetectorSampleStream(lineStream);

    TrainingParameters params = new TrainingParameters();
    params.put(TrainingParameters.ITERATIONS_PARAM, 100);
    params.put(TrainingParameters.CUTOFF_PARAM, 5);
    params.put("DataIndexer", "TwoPass");
    params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");

    LanguageDetectorModel model = LanguageDetectorME.train(
        sampleStream, params, new LanguageDetectorFactory()
    );
    LanguageDetector ld = new LanguageDetectorME(model);

    Language[] languages = ld.predictLanguages("estava em uma marcenaria na Rua Bruno");

    assertThat(Arrays.asList(languages))
        .extracting("lang", "confidence")
        .contains(
            tuple("pob", 0.9999999950605625),
            tuple("ita", 4.939427661577956E-9),
            tuple("spa", 9.665954064665144E-15),
            tuple("fra", 8.250349924885834E-25)
        );
}
```

Результатом является список наиболее вероятных языков вместе с оценкой достоверности.

И с богатыми моделями мы можем достичь очень высокой точности с этим типом обнаружения.

## Полный пример обработки текста

```java
public class TextProcessor {
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private NameFinderME nameFinder;

    public TextProcessor() throws IOException {
        // Инициализация моделей
        SentenceModel sentenceModel = new SentenceModel(
            getClass().getResourceAsStream("/models/en-sent.bin")
        );
        sentenceDetector = new SentenceDetectorME(sentenceModel);

        TokenizerModel tokenizerModel = new TokenizerModel(
            getClass().getResourceAsStream("/models/en-token.bin")
        );
        tokenizer = new TokenizerME(tokenizerModel);

        POSModel posModel = new POSModel(
            getClass().getResourceAsStream("/models/en-pos-maxent.bin")
        );
        posTagger = new POSTaggerME(posModel);

        TokenNameFinderModel nameModel = new TokenNameFinderModel(
            getClass().getResourceAsStream("/models/en-ner-person.bin")
        );
        nameFinder = new NameFinderME(nameModel);
    }

    public ProcessingResult processText(String text) {
        // Разделение на предложения
        String[] sentences = sentenceDetector.sentDetect(text);

        ProcessingResult result = new ProcessingResult();

        for (String sentence : sentences) {
            // Токенизация
            String[] tokens = tokenizer.tokenize(sentence);

            // POS тегирование
            String[] tags = posTagger.tag(tokens);

            // Поиск именованных сущностей
            Span[] nameSpans = nameFinder.find(tokens);

            result.addSentence(sentence, tokens, tags, nameSpans);
        }

        return result;
    }
}
```

## Лучшие практики

Используйте предобученные модели (en-sent.bin, en-token.bin и т.д.) из официального репозитория OpenNLP и храните их в resources. Загружайте модель один раз и переиспользуйте детектор/токенизатор; после загрузки закрывайте InputStream. Подбирайте модель под язык текста; для мультиязычности загружайте несколько моделей или используйте Language Detector. Для доменной специфики обучайте собственные модели (SentenceDetectorTrainer, TokenizerTrainer и др.).

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Модель не загружается / InvalidFormatException | Неверный путь или битая модель | Проверить путь к .bin в resources; скачать модель заново с opennlp.apache.org |
| Плохие результаты NER/POS для своего текста | Модель обучена на другом домене/стиле | Обучить свою модель на размеченных данных или попробовать другую предобученную модель |
| OutOfMemory при обработке длинного текста | Весь текст передаётся в память | Разбивать текст на абзацы/чанки; обрабатывать потоково |

## Частые вопросы

**Нужно ли обучать модели самому?** Для типовых задач (новости, общий текст) достаточно предобученных. Для узкого домена (медицина, юридические тексты) или другого языка без готовой модели — обучение своего детектора/токенизатора/NER улучшает качество.

**Чем OpenNLP отличается от Stanford NLP или spaCy?** OpenNLP — легковесная Java-библиотека с готовыми моделями; Stanford — богаче функций, но тяжелее; spaCy — Python. OpenNLP удобен для встраивания в Java-сервисы без Python.

**Как обрабатывать несколько языков?** Загрузить отдельные модели по языкам и выбирать модель по результату Language Detector или по метаданным входного текста.

## Резюме

В этом руководстве мы рассмотрели, как использовать **Apache OpenNLP** для различных задач обработки естественного языка.

**Ключевые моменты:**
- **OpenNLP** предоставляет готовые модели для различных языков
- Поддерживает токенизацию, **POS**-тегирование, **NER** и другие задачи
- Требует предобученных моделей для большинства функций
- Легко интегрируется в **Java**-приложения
- Поддерживает обучение собственных моделей

**Apache OpenNLP** — это инструмент для обработки естественного языка в **Java**, который позволяет быстро добавлять **NLP**-функциональность в приложения.

## Реализация на Kotlin

### Обнаружение предложений (Kotlin)

```kotlin
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import java.io.InputStream

class SentenceDetectionK {
    fun detectSentences(paragraph: String, modelStream: InputStream): Array<String> {
        val model = SentenceModel(modelStream)
        val sdetector = SentenceDetectorME(model)
        return sdetector.sentDetect(paragraph)
    }
}
```

### Токенизация

```kotlin
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import opennlp.tools.tokenize.WhitespaceTokenizer
import opennlp.tools.tokenize.SimpleTokenizer

class TokenizationK {
    fun tokenizeWithModel(text: String, modelStream: InputStream): Array<String> {
        val model = TokenizerModel(modelStream)
        val tokenizer = TokenizerME(model)
        return tokenizer.tokenize(text)
    }

    fun tokenizeWithWhitespace(text: String): Array<String> {
        return WhitespaceTokenizer.INSTANCE.tokenize(text)
    }

    fun tokenizeWithSimple(text: String): Array<String> {
        return SimpleTokenizer.INSTANCE.tokenize(text)
    }
}
```

### Распознавание именованных сущностей

```kotlin
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.util.Span

class NamedEntityRecognitionK {
    fun findNames(tokens: Array<String>, modelStream: InputStream): Array<Span> {
        val model = TokenNameFinderModel(modelStream)
        val nameFinder = NameFinderME(model)
        return nameFinder.find(tokens)
    }
}
```

### Тегирование части речи

```kotlin
import opennlp.tools.postag.POSModel
import opennlp.tools.postag.POSTaggerME

class POSTaggingK {
    fun tag(tokens: Array<String>, modelStream: InputStream): Array<String> {
        val model = POSModel(modelStream)
        val tagger = POSTaggerME(model)
        return tagger.tag(tokens)
    }
}
```
