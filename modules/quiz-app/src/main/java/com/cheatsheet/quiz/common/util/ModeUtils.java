package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.domain.InterviewMode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Преобразование {@link InterviewMode} в человекочитаемое русское название для UI.
 *
 * <p>Единый источник правды для отображаемых имён режимов: раньше
 * {@code session-summary.html} и {@code result.html} выводили сырое имя enum-константы
 * ({@code TRAINING}/{@code MARATHON}/…), хотя {@code settings.html} уже показывал
 * русские лейблы — пользователь, выбравший «Интенсив», на итогах видел «MARATHON»
 * (критика round-01 C22).
 *
 * <p>«Единый источник» до iter-46 был утверждением в этом javadoc, а не свойством кода:
 * подписи режимов жили ещё в трёх местах — в {@code <option>} на {@code settings.html},
 * в карте {@code MODE_CTA} внутри {@code settings/session-form.js} и в захардкоженных
 * ветках {@code MvcModelAttributeMapper}. Разъезд был не гипотетическим — именно он и
 * дал C22. Теперь список {@link #options} отдаётся в модель, а разметка и JS только
 * читают его; расхождению негде возникнуть.
 *
 * <p>Презентационная утилита живёт в app-слое (как {@code topicUtils}), а не в
 * pure-domain enum — чтобы русские UI-строки не протекали в доменную модель.
 */
@Component("modeUtils")
public class ModeUtils {

    /**
     * Порядок режимов в выпадающем списке: от лёгкого к тяжёлому, зачётные режимы
     * (экзамен, интенсив) — в конце. Это презентационное решение, поэтому оно здесь,
     * а не в порядке констант {@link InterviewMode} (от него зависят ordinal'ы).
     *
     * <p>Полноту списка держит гейт: режим, забытый здесь, недостижим из интерфейса.
     */
    private static final List<InterviewMode> DISPLAY_ORDER = List.of(
            InterviewMode.TRAINING,
            InterviewMode.STUDY,
            InterviewMode.FLASHCARD,
            InterviewMode.TYPED_RECALL,
            InterviewMode.INTERVIEW,
            InterviewMode.CODE_CHALLENGE,
            InterviewMode.EXAM,
            InterviewMode.MARATHON
    );

    /**
     * Один пункт выпадающего списка режимов.
     *
     * @param name         имя enum-константы — значение, уходящее на сервер
     * @param label        русская подпись
     * @param startAction  подпись кнопки старта (винительный падеж; из подписи не выводится)
     * @param defaultCount число вопросов, которое подставится при выборе режима
     * @param unlimited    режим без счётчика вопросов — поле «Вопросов» прячется
     * @param selected     текущий выбор
     */
    public record ModeOption(
            String name,
            String label,
            String startAction,
            int defaultCount,
            boolean unlimited,
            boolean selected
    ) {
    }

    /**
     * @param mode режим тестирования (может быть {@code null})
     * @return отображаемое русское имя; для {@code null} — пустая строка
     */
    public String displayName(InterviewMode mode) {
        if (mode == null) {
            return "";
        }
        return switch (mode) {
            case TRAINING -> "Тренировка";
            case STUDY -> "Изучение";
            case FLASHCARD -> "Флешкарты";
            case TYPED_RECALL -> "Печатный ответ";
            case INTERVIEW -> "Интервью";
            case CODE_CHALLENGE -> "Код-челлендж";
            case EXAM -> "Экзамен";
            case MARATHON -> "Интенсив";
        };
    }

    /**
     * Подпись кнопки старта. Отдельный switch, а не «Начать » + {@link #displayName}:
     * винительный падеж совпадает с именительным у семи режимов из восьми
     * («Тренировка» → «тренировку»), и вывести его правилом нельзя.
     *
     * @param mode режим тестирования (может быть {@code null})
     * @return подпись кнопки; для {@code null} — нейтральное «Начать сессию»
     */
    public String startAction(InterviewMode mode) {
        if (mode == null) {
            return "Начать сессию";
        }
        return switch (mode) {
            case TRAINING -> "Начать тренировку";
            case STUDY -> "Начать изучение";
            case FLASHCARD -> "Начать флешкарты";
            case TYPED_RECALL -> "Начать печатный ответ";
            case INTERVIEW -> "Начать интервью";
            case CODE_CHALLENGE -> "Начать код-челлендж";
            case EXAM -> "Начать экзамен";
            case MARATHON -> "Начать интенсив";
        };
    }

    /**
     * Опоры рабочего места печатного режима — короткий разбор того, ЧТО делать,
     * пока эталон закрыт.
     *
     * <p>Три режима семейства {@code usesTypedInput()} до сих пор отличались только
     * подписью поля и моноширинным шрифтом: экран интервью был экраном печатного
     * ответа, у которого поменяли ярлык. Метод режима — это и есть отличие: у
     * интервью опора — структура устного ответа, у код-челленджа — что проверяют в
     * решении, у печатного пересказа — из чего складывается полный ответ.
     *
     * <p>Список статический и принадлежит РЕЖИМУ, а не вопросу: это метод работы,
     * одинаковый для любого вопроса банка. Данных вопроса он не выдумывает.
     *
     * <p>{@code switch} исчерпывающий намеренно: новый режим не соберётся, пока
     * автор не решит, есть ли у него своя опора. Пустой список = «опоры нет»,
     * шаблон тогда не рисует блок вовсе.
     *
     * @param mode режим тестирования (может быть {@code null})
     * @return пункты опоры; пустой список, если у режима её нет
     */
    public List<String> workspaceChecklist(InterviewMode mode) {
        if (mode == null) {
            return List.of();
        }
        return switch (mode) {
            case INTERVIEW -> List.of(
                    "Определение в одном предложении",
                    "Зачем это нужно и какую задачу решает",
                    "Как устроено внутри",
                    "Компромиссы и когда не подходит",
                    "Пример из практики"
            );
            case CODE_CHALLENGE -> List.of(
                    "Сигнатура и контракт",
                    "Основной случай",
                    "Границы: пусто, один элемент, переполнение",
                    "Сложность по времени и памяти"
            );
            case TYPED_RECALL -> List.of(
                    "Суть своими словами",
                    "Ключевые термины",
                    "Чем отличается от соседнего понятия",
                    "Где это встречается на практике"
            );
            case TRAINING, STUDY, FLASHCARD, EXAM, MARATHON -> List.of();
        };
    }

    /**
     * Список пунктов для селекта «Режим» на {@code /settings}.
     *
     * <p>{@code defaultCount} берётся из той же конфигурации, по которой сервер потом
     * считает размер сессии ({@code SessionFlowService.resolveSessionTotal}): экзамен и
     * режимы с развёрнутым вводом — короткий набор, остальные — длинный. Раньше эти
     * восемь чисел были вписаны в разметку руками рядом с комментарием «значения
     * продублированы» — и три из них уже разошлись с правилом, которое тот же
     * комментарий и формулировал.
     *
     * @param selected     текущий режим ({@code null} трактуется как {@link InterviewMode#TRAINING})
     * @param examCount    {@code app.interview.default-exam-count}
     * @param marathonCount {@code app.interview.default-marathon-count}
     */
    public List<ModeOption> options(InterviewMode selected, int examCount, int marathonCount) {
        InterviewMode current = selected != null ? selected : InterviewMode.TRAINING;
        return DISPLAY_ORDER.stream()
                .map(mode -> new ModeOption(
                        mode.name(),
                        displayName(mode),
                        startAction(mode),
                        mode == InterviewMode.EXAM || mode.usesTypedInput() ? examCount : marathonCount,
                        mode.isUnlimited(),
                        mode == current
                ))
                .toList();
    }
}
