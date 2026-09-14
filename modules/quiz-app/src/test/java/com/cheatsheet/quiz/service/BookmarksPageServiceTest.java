package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.interview.service.page.BookmarksPageService;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookmarksPageServiceTest {

    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private TopicCatalogService topicCatalogService;
    @Mock
    private TopicUtils topicUtils;

    private BookmarksPageService service;

    @BeforeEach
    void setUp() {
        service = new BookmarksPageService(bookmarkService, questionRepository, topicCatalogService, topicUtils);
    }

    @Test
    void buildReturnsEnrichedItemsWithoutSeedImportantSync() {
        when(bookmarkService.list()).thenReturn(List.of(
                new Bookmark(1L, 5L, "повтор hash", 10L, 20L)
        ));
        when(questionRepository.findByIds(List.of(5L))).thenReturn(List.of(question(5L, "java-collections", "Question 5 about maps?")));
        when(topicUtils.displayName("java-collections")).thenReturn("Java Collections");
        when(topicCatalogService.groupOptions(anyList())).thenReturn(List.of());

        BookmarksPageService.BookmarksPageState state = service.build(null);

        assertThat(state.total()).isEqualTo(1L);
        assertThat(state.items()).hasSize(1);
        BookmarksPageService.BookmarkItem item = state.items().get(0);
        assertThat(item.questionId()).isEqualTo(5L);
        assertThat(item.topic()).isEqualTo("java-collections");
        assertThat(item.topicDisplayName()).isEqualTo("Java Collections");
        assertThat(item.note()).isEqualTo("повтор hash");
        assertThat(item.questionPreview()).contains("Question 5");
    }

    @Test
    void buildReturnsEmptyWhenNoBookmarks() {
        when(bookmarkService.list()).thenReturn(List.of());

        BookmarksPageService.BookmarksPageState state = service.build(null);

        assertThat(state.items()).isEmpty();
        assertThat(state.total()).isZero();
        assertThat(state.shown()).isZero();
        assertThat(state.filtered()).isFalse();
    }

    /**
     * iter-74. Поиск идёт по ПОЛНОМУ тексту вопроса, а не по превью, обрезанному
     * до 160 символов. Гейт держит именно эту границу: без неё слово, стоящее в
     * вопросе на 200-м символе, не находилось бы, хотя вопрос ровно про него, и
     * коллекция молча теряла бы часть себя для поиска.
     */
    @Test
    void searchLooksBeyondTheTruncatedPreview() {
        String longQuestion = "Вопрос про сериализацию. ".repeat(9) + "Финальное слово: kryo?";
        assertThat(longQuestion.length()).isGreaterThan(200);

        when(bookmarkService.list()).thenReturn(List.of(new Bookmark(1L, 5L, null, 10L, 20L)));
        when(questionRepository.findByIds(List.of(5L))).thenReturn(List.of(question(5L, "java-serialization", longQuestion)));
        when(topicUtils.displayName(anyString())).thenReturn("Java Serialization");
        when(topicCatalogService.groupOptions(anyList())).thenReturn(List.of());

        BookmarksPageService.BookmarksPageState found = service.build(
                new BookmarksPageService.BookmarksQuery("KRYO", null, null, null));

        assertThat(found.items()).hasSize(1);
        assertThat(found.items().get(0).questionPreview()).doesNotContain("kryo");
        assertThat(found.items().get(0).questionText())
                .as("найденное за пределами превью слово доступно при раскрытии без ухода из списка")
                .isEqualTo(longQuestion);
        assertThat(found.shown()).isEqualTo(1L);
        assertThat(found.total()).isEqualTo(1L);
        assertThat(found.filtered()).isTrue();
    }

    /**
     * Знаменатель шапки — размер ВСЕЙ коллекции, а не выборки: строка «1 из 2»
     * без второго числа не отличала бы «нашлась одна» от «всего одна».
     */
    @Test
    void selectionNarrowsItemsButNotTheTotal() {
        when(bookmarkService.list()).thenReturn(List.of(
                new Bookmark(1L, 5L, "с заметкой", 20L, 20L),
                new Bookmark(2L, 6L, "   ", 10L, 10L)
        ));
        when(questionRepository.findByIds(List.of(5L, 6L))).thenReturn(List.of(
                question(5L, "java-collections", "Вопрос про карты?"),
                question(6L, "java-streams", "Вопрос про потоки?")
        ));
        when(topicUtils.displayName(anyString())).thenReturn("Тема");
        when(topicCatalogService.groupOptions(anyList())).thenReturn(List.of());

        BookmarksPageService.BookmarksPageState withNote = service.build(
                new BookmarksPageService.BookmarksQuery(null, null, BookmarksPageService.NOTE_YES, null));

        assertThat(withNote.total()).isEqualTo(2L);
        assertThat(withNote.shown()).isEqualTo(1L);
        assertThat(withNote.items()).singleElement()
                .extracting(BookmarksPageService.BookmarkItem::questionId).isEqualTo(5L);

        // Пробел-заметка — это отсутствие заметки, а не заметка: иначе «без
        // заметки» пропускало бы карточки, у которых поле визуально пустое.
        BookmarksPageService.BookmarksPageState withoutNote = service.build(
                new BookmarksPageService.BookmarksQuery(null, null, BookmarksPageService.NOTE_NO, null));
        assertThat(withoutNote.items()).singleElement()
                .extracting(BookmarksPageService.BookmarkItem::questionId).isEqualTo(6L);
    }

    /**
     * Счётчики разделов считаются по ВСЕЙ коллекции. Иначе после выбора раздела
     * в селекте остался бы ровно один пункт и уйти из него было бы некуда —
     * навигация превратилась бы в тупик. Плюс счётчик обязан быть числом
     * ЗАКЛАДОК раздела, а не числом его тем: список закладок — то, что человек
     * тут считает.
     */
    @Test
    void groupFacetsAreCountedOverTheWholeCollection() {
        when(bookmarkService.list()).thenReturn(List.of(
                new Bookmark(1L, 5L, null, 30L, 30L),
                new Bookmark(2L, 6L, null, 20L, 20L),
                new Bookmark(3L, 7L, null, 10L, 10L)
        ));
        when(questionRepository.findByIds(List.of(5L, 6L, 7L))).thenReturn(List.of(
                question(5L, "java-collections", "Карты?"),
                question(6L, "java-collections", "Списки?"),
                question(7L, "spring-boot", "Автоконфигурация?")
        ));
        when(topicUtils.displayName(anyString())).thenReturn("Тема");
        when(topicCatalogService.detectGroupForTopic("java-collections")).thenReturn(Optional.of("java"));
        when(topicCatalogService.detectGroupForTopic("spring-boot")).thenReturn(Optional.of("spring"));
        when(topicCatalogService.groupOptions(anyList())).thenReturn(List.of(
                new TopicCatalogService.GroupOption("java", "Java", "", 2L, null),
                new TopicCatalogService.GroupOption("spring", "Spring", "", 1L, null)
        ));

        BookmarksPageService.BookmarksPageState state = service.build(
                new BookmarksPageService.BookmarksQuery(null, "spring", null, null));

        assertThat(state.shown()).isEqualTo(1L);
        assertThat(state.groups()).hasSize(2);

        // Список тем, по которому считаются фасеты, приходит С ДУБЛИКАТАМИ:
        // java-collections встречается дважды, и счётчик «Java (2)» — это две
        // закладки, а не две темы.
        org.mockito.ArgumentCaptor<List<String>> topics = org.mockito.ArgumentCaptor.forClass(List.class);
        org.mockito.Mockito.verify(topicCatalogService).groupOptions(topics.capture());
        assertThat(topics.getValue()).containsExactly("java-collections", "java-collections", "spring-boot");
    }

    /**
     * Неизвестные значения отбора не роняют страницу и не показывают пустой
     * селект — они молча становятся умолчанием.
     */
    @Test
    void unknownSelectionValuesFallBackToDefaults() {
        BookmarksPageService.BookmarksQuery normalized =
                new BookmarksPageService.BookmarksQuery("  KaFka ", "  ", "мусор", "по-алфавиту").normalized();

        assertThat(normalized.q()).isEqualTo("kafka");
        assertThat(normalized.group()).isNull();
        assertThat(normalized.note()).isEqualTo(BookmarksPageService.NOTE_ANY);
        assertThat(normalized.sort()).isEqualTo(BookmarksPageService.SORT_NEW);
        // Запрос — сужение, поэтому строка «Отобрано» тут обязана появиться.
        assertThat(normalized.active()).isTrue();

        // А сортировка сужением НЕ является: смена порядка не прячет ни одной
        // закладки, и объявлять «Отобрано: 28 из 28» со ссылкой «Сбросить» было
        // бы враньём о состоянии страницы.
        assertThat(new BookmarksPageService.BookmarksQuery(null, null, null, BookmarksPageService.SORT_TOPIC)
                .normalized().active()).isFalse();
    }

    @Test
    void sortOrderFollowsTheSelectedMode() {
        when(bookmarkService.list()).thenReturn(List.of(
                new Bookmark(1L, 5L, null, 10L, 10L),
                new Bookmark(2L, 6L, null, 30L, 30L),
                new Bookmark(3L, 7L, null, 20L, 20L)
        ));
        when(questionRepository.findByIds(List.of(5L, 6L, 7L))).thenReturn(List.of(
                question(5L, "b-topic", "Первый?"),
                question(6L, "c-topic", "Второй?"),
                question(7L, "a-topic", "Третий?")
        ));
        when(topicUtils.displayName("a-topic")).thenReturn("Альфа");
        when(topicUtils.displayName("b-topic")).thenReturn("Бета");
        when(topicUtils.displayName("c-topic")).thenReturn("Гамма");
        when(topicCatalogService.groupOptions(anyList())).thenReturn(List.of());

        assertThat(ids(service.build(new BookmarksPageService.BookmarksQuery(null, null, null, BookmarksPageService.SORT_NEW))))
                .containsExactly(6L, 7L, 5L);
        assertThat(ids(service.build(new BookmarksPageService.BookmarksQuery(null, null, null, BookmarksPageService.SORT_OLD))))
                .containsExactly(5L, 7L, 6L);
        assertThat(ids(service.build(new BookmarksPageService.BookmarksQuery(null, null, null, BookmarksPageService.SORT_TOPIC))))
                .containsExactly(7L, 5L, 6L);
    }

    private static List<Long> ids(BookmarksPageService.BookmarksPageState state) {
        return state.items().stream().map(BookmarksPageService.BookmarkItem::questionId).toList();
    }

    private static Question question(long id, String topic, String text) {
        return Question.builder()
                .id(id)
                .slug(topic + ".md#Q" + id)
                .sourceSlug(topic + ".md#Q" + id)
                .filePath(topic + ".md")
                .topic(topic)
                .questionText(text)
                .answerMarkdown("Answer " + id)
                .important(true)
                .sourceHash("hash-" + id)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .diagramMermaid(null)
                .takeaway(null)
                .build();
    }
}
