package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.common.util.FilterUtils;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Mapper request-параметров MVC в доменные объекты фильтрации/режимов.
 *
 * <p>Централизует нормализацию topic/group и сборку {@link InterviewFilter}
 * для разных входных сценариев контроллера.</p>
 */
@Component
public class MvcRequestMapper {

    /**
     * Возвращает активный режим сессии либо режим из request-параметра.
     */
    public InterviewMode resolveMode(InterviewSession interviewSession, String modeRaw) {
        return interviewSession != null ? interviewSession.getMode() : InterviewMode.fromString(modeRaw);
    }

    /**
     * Собирает фильтр для focus/settings flow: из сессии (если есть) или query-параметров.
     */
    public InterviewFilter resolveFilter(
            InterviewSession interviewSession,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered
    ) {
        if (interviewSession != null) {
            return new InterviewFilter(
                    interviewSession.getTopic(),
                    interviewSession.getGroup(),
                    interviewSession.getImportantOnly(),
                    interviewSession.getOnlyWrong(),
                    interviewSession.getShuffle(),
                    interviewSession.getOrdered()
            );
        }
        return new InterviewFilter(
                FilterUtils.normalizeTopic(topic),
                FilterUtils.normalizeGroup(group),
                important,
                onlyWrong,
                shuffle,
                ordered
        );
    }

    /**
     * Собирает фильтр для review-flow (always onlyWrong=true).
     */
    public InterviewFilter resolveReviewBaseFilter(
            String topic,
            String group,
            Boolean important,
            Boolean shuffle,
            Boolean ordered
    ) {
        return new InterviewFilter(
                FilterUtils.normalizeTopic(topic),
                FilterUtils.normalizeGroup(group),
                important,
                false,
                shuffle,
                ordered
        );
    }

    /**
     * Собирает фильтр для старта сессии из payload.
     */
    public InterviewFilter resolveStartFilter(StartSessionRequest request) {
        return new InterviewFilter(
                FilterUtils.normalizeTopic(request.getTopic()),
                FilterUtils.normalizeGroup(request.getGroup()),
                request.getImportant(),
                request.getOnlyWrong(),
                request.getShuffle(),
                request.getOrdered(),
                parseDifficulty(request.getDifficulty())
        );
    }

    private static Difficulty parseDifficulty(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Difficulty.valueOf(raw.strip().toUpperCase(Locale.ROOT));
    }

    /**
     * Возвращает режим запуска сессии из payload.
     */
    public InterviewMode resolveStartMode(StartSessionRequest request) {
        return InterviewMode.fromString(request.getMode());
    }

    /**
     * Возвращает контекст параметров для страницы настроек.
     */
    public SettingsRequestContext resolveSettingsContext(
            InterviewSession interviewSession,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered,
            Boolean weakTopics,
            String modeRaw
    ) {
        return resolveSettingsContext(interviewSession, topic, group, important, onlyWrong,
                shuffle, ordered, weakTopics, null, modeRaw);
    }

    public SettingsRequestContext resolveSettingsContext(
            InterviewSession interviewSession,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered,
            Boolean weakTopics,
            String difficultyRaw,
            String modeRaw
    ) {
        InterviewMode selectedMode = interviewSession == null && (modeRaw == null || modeRaw.isBlank())
                ? InterviewMode.MARATHON
                : resolveMode(interviewSession, modeRaw);
        InterviewFilter filter = resolveFilter(interviewSession, topic, group, important, onlyWrong, shuffle, ordered);
        if (interviewSession == null) {
            filter = filter.toBuilder().difficulty(parseDifficulty(difficultyRaw)).build();
        }
        boolean weakTopicsPriority = Boolean.TRUE.equals(weakTopics);
        return new SettingsRequestContext(selectedMode, filter, weakTopicsPriority);
    }

    /**
     * Возвращает контекст параметров для страницы статистики.
     */
    public StatsRequestContext resolveStatsContext(
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean ordered,
            String query
    ) {
        InterviewFilter filter = resolveFilter(null, topic, group, important, onlyWrong, false, ordered);
        return new StatsRequestContext(filter, query);
    }

    @Builder(toBuilder = true)
    public record SettingsRequestContext(
            InterviewMode selectedMode,
            InterviewFilter filter,
            boolean weakTopicsPriority
    ) {
    }

    @Builder(toBuilder = true)
    public record StatsRequestContext(
            InterviewFilter filter,
            String query
    ) {
    }
}
