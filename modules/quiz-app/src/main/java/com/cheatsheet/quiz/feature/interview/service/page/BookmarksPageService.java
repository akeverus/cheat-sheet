package com.cheatsheet.quiz.feature.interview.service.page;

import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * View-model страницы закладок и заметок (UI-014).
 *
 * <p>С iter-74 страница не только перечисляет коллекцию, но и даёт по ней
 * искать. Причина — замер: 28 закладок разворачивались в 4774px на 1280×720
 * (6.6 экрана) и в 10922px на 390×844, а единственным способом найти нужную
 * была прокрутка. Отбор идёт здесь, на сервере: страница рендерится Thymeleaf,
 * и клиентская фильтрация давала бы вторую правду о том, что показано.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarksPageService {
    private static final int QUESTION_PREVIEW_MAX = 160;

    /** Значения параметра {@code sort}; первое — умолчание. */
    public static final String SORT_NEW = "new";
    public static final String SORT_OLD = "old";
    public static final String SORT_TOPIC = "topic";

    /** Значения параметра {@code note}. */
    public static final String NOTE_ANY = "";
    public static final String NOTE_YES = "yes";
    public static final String NOTE_NO = "no";

    BookmarkService bookmarkService;
    QuestionRepository questionRepository;
    TopicCatalogService topicCatalogService;
    TopicUtils topicUtils;

    /**
     * Строит список закладок пользователя (таблица {@code bookmark}) с учётом отбора.
     *
     * <p>Не подтягивает seed-{@code is_important}: это контентная метка «ВАЖНО»
     * из markdown, а не пользовательская коллекция. Закладка появляется при
     * звезде на результате ({@link com.cheatsheet.quiz.feature.interview.service.progress.FavoriteService})
     * или при сохранении заметки через API.</p>
     *
     * <p>Поиск идёт по ПОЛНОМУ тексту вопроса, а не по обрезанному до 160
     * символов превью: иначе слово, стоящее в вопросе на 200-м символе, не
     * находилось бы, хотя вопрос ровно про него.</p>
     */
    public BookmarksPageState build(BookmarksQuery query) {
        BookmarksQuery request = query == null ? BookmarksQuery.empty() : query.normalized();
        List<Bookmark> bookmarks = bookmarkService.list();
        if (bookmarks.isEmpty()) {
            return new BookmarksPageState(List.of(), 0L, 0L, List.of(), request, false);
        }
        List<Long> ids = bookmarks.stream().map(Bookmark::questionId).toList();
        Map<Long, Question> byId = new HashMap<>();
        for (Question question : questionRepository.findByIds(ids)) {
            byId.put(question.id(), question);
        }
        List<BookmarkItem> all = new ArrayList<>(bookmarks.size());
        List<String> topicsWithBookmarks = new ArrayList<>(bookmarks.size());
        for (Bookmark bookmark : bookmarks) {
            Question question = byId.get(bookmark.questionId());
            if (question == null) {
                continue;
            }
            String topic = question.topic();
            topicsWithBookmarks.add(topic);
            all.add(new BookmarkItem(
                    bookmark.questionId(),
                    topic,
                    topicUtils.displayName(topic),
                    truncatePreview(question.questionText()),
                    bookmark.note(),
                    bookmark.createdAt(),
                    bookmark.updatedAt(),
                    normalizeForSearch(question.questionText())
            ));
        }
        // Счётчики разделов — по ВСЕЙ коллекции, а не по выборке: селект должен
        // оставаться навигацией («что ещё у меня отложено»), иначе после выбора
        // раздела в нём остался бы ровно один пункт и уйти было бы некуда.
        // Дубликаты в списке намеренны: groupOptions считает вхождения, поэтому
        // topicCount получается числом ЗАКЛАДОК раздела, а не числом тем.
        List<TopicCatalogService.GroupOption> groups = topicCatalogService.groupOptions(topicsWithBookmarks);
        List<BookmarkItem> selected = new ArrayList<>(all.size());
        for (BookmarkItem item : all) {
            if (matches(item, request)) {
                selected.add(item);
            }
        }
        sort(selected, request.sort());
        return new BookmarksPageState(selected, all.size(), selected.size(), groups, request, request.active());
    }

    private boolean matches(BookmarkItem item, BookmarksQuery request) {
        if (request.group() != null
                && !request.group().equals(topicCatalogService.detectGroupForTopic(item.topic()).orElse(null))) {
            return false;
        }
        boolean hasNote = item.note() != null && !item.note().isBlank();
        if (NOTE_YES.equals(request.note()) && !hasNote) {
            return false;
        }
        if (NOTE_NO.equals(request.note()) && hasNote) {
            return false;
        }
        String needle = request.q();
        if (needle == null) {
            return true;
        }
        return item.searchBlob().contains(needle)
                || normalizeForSearch(item.note()).contains(needle)
                || normalizeForSearch(item.topicDisplayName()).contains(needle);
    }

    private static void sort(List<BookmarkItem> items, String sort) {
        Comparator<BookmarkItem> newest = Comparator
                .comparingLong(BookmarkItem::createdAt).reversed()
                .thenComparing(Comparator.comparingLong(BookmarkItem::questionId).reversed());
        if (SORT_OLD.equals(sort)) {
            items.sort(newest.reversed());
            return;
        }
        if (SORT_TOPIC.equals(sort)) {
            items.sort(Comparator
                    .comparing(BookmarkItem::topicDisplayName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(newest));
            return;
        }
        items.sort(newest);
    }

    private static String normalizeForSearch(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT);
    }

    private static String truncatePreview(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalized = text.replaceAll("\\s+", " ").strip();
        if (normalized.length() <= QUESTION_PREVIEW_MAX) {
            return normalized;
        }
        return normalized.substring(0, QUESTION_PREVIEW_MAX - 1).stripTrailing() + "…";
    }

    /**
     * Отбор страницы закладок. Одна форма — один GET на {@code /bookmarks}
     * (FRD-024), поэтому все четыре условия приходят вместе и вместе же
     * возвращаются в разметку: контролы обязаны показывать своё состояние
     * после перезагрузки по ссылке.
     *
     * @param q     подстрока запроса в нижнем регистре, {@code null} если пусто
     * @param group идентификатор раздела каталога, {@code null} если «Все»
     * @param note  {@link #NOTE_ANY}/{@link #NOTE_YES}/{@link #NOTE_NO}
     * @param sort  {@link #SORT_NEW}/{@link #SORT_OLD}/{@link #SORT_TOPIC}
     */
    @Builder(toBuilder = true)
    public record BookmarksQuery(String q, String group, String note, String sort) {

        public static BookmarksQuery empty() {
            return new BookmarksQuery(null, null, NOTE_ANY, SORT_NEW);
        }

        /**
         * Приводит сырые параметры запроса к допустимым значениям. Неизвестная
         * сортировка не роняет страницу и не показывает пустой селект — она
         * молча становится умолчанием, ровно как неизвестный раздел становится
         * «Все» в {@code normalizeGroup}.
         */
        public BookmarksQuery normalized() {
            String normalizedQuery = q == null || q.isBlank() ? null : q.strip().toLowerCase(Locale.ROOT);
            String normalizedGroup = group == null || group.isBlank() ? null : group.strip();
            String normalizedNote = NOTE_YES.equals(note) || NOTE_NO.equals(note) ? note : NOTE_ANY;
            String normalizedSort = SORT_OLD.equals(sort) || SORT_TOPIC.equals(sort) ? sort : SORT_NEW;
            return new BookmarksQuery(normalizedQuery, normalizedGroup, normalizedNote, normalizedSort);
        }

        /** Сужает ли отбор коллекцию. Сортировка — не сужение, она в счёт не идёт. */
        public boolean active() {
            return q != null || group != null || NOTE_YES.equals(note) || NOTE_NO.equals(note);
        }
    }

    /**
     * @param items    закладки выборки в порядке сортировки
     * @param total    размер всей коллекции (не выборки) — знаменатель шапки
     * @param shown    размер выборки; равен {@code total}, когда отбор не задан
     * @param groups   разделы, в которых есть закладки, со счётчиками по всей коллекции
     * @param query    нормализованный отбор — контролы рендерятся из него
     * @param filtered сужает ли отбор коллекцию (см. {@link BookmarksQuery#active()})
     */
    @Builder(toBuilder = true)
    public record BookmarksPageState(
            List<BookmarkItem> items,
            long total,
            long shown,
            List<TopicCatalogService.GroupOption> groups,
            BookmarksQuery query,
            boolean filtered
    ) {}

    /**
     * @param searchBlob полный текст вопроса в нижнем регистре — цель поиска;
     *                   в разметку не выводится, там стоит {@code questionPreview}
     */
    @Builder(toBuilder = true)
    public record BookmarkItem(
            long questionId,
            String topic,
            String topicDisplayName,
            String questionPreview,
            String note,
            long createdAt,
            long updatedAt,
            String searchBlob
    ) {}
}
