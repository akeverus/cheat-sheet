package com.cheatsheet.quiz.feature.interview.service.page;

import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View-model страницы закладок и заметок (UI-014).
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarksPageService {
    private static final int QUESTION_PREVIEW_MAX = 160;

    BookmarkService bookmarkService;
    QuestionRepository questionRepository;
    TopicUtils topicUtils;

    /**
     * Строит список закладок пользователя (таблица {@code bookmark}).
     *
     * <p>Не подтягивает seed-{@code is_important}: это контентная метка «ВАЖНО»
     * из markdown, а не пользовательская коллекция. Закладка появляется при
     * звезде на результате ({@link com.cheatsheet.quiz.feature.interview.service.progress.FavoriteService})
     * или при сохранении заметки через API.</p>
     */
    public BookmarksPageState build() {
        List<Bookmark> bookmarks = bookmarkService.list();
        if (bookmarks.isEmpty()) {
            return new BookmarksPageState(List.of(), 0L);
        }
        List<Long> ids = bookmarks.stream().map(Bookmark::questionId).toList();
        Map<Long, Question> byId = new HashMap<>();
        for (Question question : questionRepository.findByIds(ids)) {
            byId.put(question.id(), question);
        }
        List<BookmarkItem> items = new ArrayList<>(bookmarks.size());
        for (Bookmark bookmark : bookmarks) {
            Question question = byId.get(bookmark.questionId());
            if (question == null) {
                continue;
            }
            String topic = question.topic();
            items.add(new BookmarkItem(
                    bookmark.questionId(),
                    topic,
                    topicUtils.displayName(topic),
                    truncatePreview(question.questionText()),
                    bookmark.note(),
                    bookmark.createdAt(),
                    bookmark.updatedAt()
            ));
        }
        return new BookmarksPageState(items, items.size());
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

    @Builder(toBuilder = true)
    public record BookmarksPageState(
            List<BookmarkItem> items,
            long total
    ) {}

    @Builder(toBuilder = true)
    public record BookmarkItem(
            long questionId,
            String topic,
            String topicDisplayName,
            String questionPreview,
            String note,
            long createdAt,
            long updatedAt
    ) {}
}
