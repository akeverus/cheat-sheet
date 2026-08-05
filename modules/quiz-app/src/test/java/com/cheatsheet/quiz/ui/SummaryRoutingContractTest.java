package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.NextActionType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Соответствие {@link NextActionType.Destination} и тернарников {@code th:href} на экране
 * итогов сессии.
 *
 * <p>Мотив — найденный дефект. Адрес кнопки «что дальше» выбирался перечислением
 * <b>типов действий</b>: из четырёх констант тернарник называл две, остальные сваливались
 * в ветку «иначе». Само по себе это работало (оба умолчания и правда ведут в тренажёр),
 * но делало невыразимым то, ради чего рядом стоит вторая кнопка: сколько на экране
 * различимых мест назначения. Дедупликация «продолжить тренировку» сравнивала имя типа,
 * а не адрес, и при плане {@code [REVIEW_DUE, KEEP_GOING]} экран показывал «Ждут
 * повторения: N» и «Продолжить тренировку» рядом — две подписи, один {@code href}.
 *
 * <h2>Почему контракт исчерпывающий именно здесь</h2>
 * {@code Destination} — единственный носитель адреса в шаблоне: новая константа без своей
 * ветки молча уедет в {@code /}, и кнопка поведёт не туда, куда обещает подпись. Это
 * <b>не</b> общее правило «каждая константа обязана иметь ветку». Соседний тернарник в
 * том же теге — иконка по {@link NextActionType} — устроен наоборот: там {@code #i-flame}
 * это осознанное умолчание для «просто продолжить», и требовать ветку под каждый тип
 * значило бы краснеть на решении, а не на дефекте.
 *
 * @see ReviewReasonPaletteTest тот же класс дефекта в паре «перечисление ↔ таблица стилей»
 */
class SummaryRoutingContractTest {

    private static final Path SUMMARY = Path.of("src/main/resources/templates/session-summary.html");

    /** Значение атрибута {@code th:href} целиком (может занимать несколько строк). */
    private static final Pattern HREF = Pattern.compile("th:href=\"([^\"]*)\"");

    /** Сравнение с константой назначения внутри тернарника: {@code ${d == 'TOPIC'}}. */
    private static final Pattern DESTINATION_TEST = Pattern.compile("d == '([A-Z_]+)'");

    /** Тернарники, которые выбирают адрес по назначению (прочие {@code th:href} — статические). */
    private List<Set<String>> destinationChains() {
        String html = read();
        Matcher href = HREF.matcher(html);
        List<Set<String>> chains = new java.util.ArrayList<>();
        while (href.find()) {
            Matcher dest = DESTINATION_TEST.matcher(href.group(1));
            Set<String> named = new LinkedHashSet<>();
            while (dest.find()) {
                named.add(dest.group(1));
            }
            if (!named.isEmpty()) {
                chains.add(named);
            }
        }
        return chains;
    }

    @Test
    void everyDestinationIsRoutedExplicitlyOrIsTheSingleFallback() {
        Set<String> declared = Arrays.stream(NextActionType.Destination.values())
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Set<String>> chains = destinationChains();

        assertThat(chains)
                .as("на экране итогов должны остаться тернарники адреса по Destination — "
                        + "если их нет, маршрутизация вернулась к именам типов действий")
                .isNotEmpty();

        chains.forEach(named -> {
            assertThat(declared)
                    .as("тернарник ссылается на несуществующее назначение: %s", named)
                    .containsAll(named);
            assertThat(named)
                    .as("в тернарнике названы %s из %s: непокрытые назначения молча уедут "
                            + "в ветку «иначе» (тренажёр), и кнопка поведёт не туда, "
                            + "куда обещает подпись", named, declared)
                    .hasSize(declared.size() - 1);
        });
    }

    @Test
    void continueTrainingIsDeduplicatedByDestinationNotByTypeName() {
        String html = read();

        assertThat(html)
                .as("дубль «Продолжить тренировку» снимался сравнением имени типа "
                        + "(primaryType != 'KEEP_GOING'); при плане [REVIEW_DUE, KEEP_GOING] "
                        + "это давало две кнопки с одним адресом")
                .doesNotContain("primaryType");

        assertThat(html)
                .as("вторая кнопка обязана гаситься по назначению primary-действия")
                .contains("primaryDest != 'TRAINING'");
    }

    /**
     * Ссылка «Продолжить тренировку» вне плана существует ровно в двух местах: primary при
     * пустом плане и secondary при primary, ведущем не в тренажёр. Третьего быть не должно.
     *
     * <p>Первая попытка починки понизила дубль в меню «Ещё» вместо того, чтобы его убрать.
     * Это выглядело мягче, но оставляло ровно то, против чего вводился контракт: две подписи
     * с одним {@code href}, просто в разных ярусах. Понижение прячет дефект, а не лечит его.
     */
    @Test
    void continueTrainingIsNeverRepeatedInTheMoreMenu() {
        long occurrences = read().lines()
                .filter(line -> line.contains(">Продолжить тренировку<"))
                .count();

        assertThat(occurrences)
                .as("плановая ссылка в тренажёр должна быть одна на экран: primary при пустом "
                        + "плане либо secondary при чужом назначении. Возврат её в «Ещё» рядом "
                        + "с primary, ведущим туда же, — тот же дубль, просто тише")
                .isEqualTo(2);
    }

    private static String read() {
        try {
            return Files.readString(SUMMARY, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
