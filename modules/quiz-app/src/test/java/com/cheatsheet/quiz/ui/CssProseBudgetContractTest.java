package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Эссе живут в docs/css-decisions, в CSS — короткий указатель (ARCH-02, REF-009).
 *
 * <p>Замер по static/css: проза занимала 697KB из 977KB корпуса (71%), из них
 * 179 эссе длиннее килобайта — 313KB; pages-focus.css был прозой на 79%. Это
 * тормоз чтения, диффов и грепов: поиск правила выдаёт эссе, дифф правки тонет
 * в перекладке абзацев. Рантайм давно защищён (CssBundleService.stripComments) —
 * дефект чисто исходниковый.</p>
 *
 * <p>Правило переноса — НЕ «комментарии запрещены»: рабочие инварианты («НЕ
 * менять», пороги, каскадные ловушки, однострочные замеры) остаются у правила,
 * потому что читаются В МОМЕНТ правки. Уезжает только ИСТОРИЯ: как было раньше,
 * чем доказано, какие альтернативы отклонены — она нужна при пересмотре решения,
 * а не при каждом чтении файла, и лежит в docs/css-decisions/&lt;файл&gt;.md,
 * на который указатель ссылается по якорю.</p>
 *
 * <p>Гейт держит бюджет только для ПЕРЕВЕДЁННЫХ файлов (список ниже растёт по
 * мере волн): в непереведённых эссе трогать рано, а обратный дрейф в
 * переведённых — это возвращение болота, которое уже осушили.</p>
 */
class CssProseBudgetContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Pattern COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /** Бюджет одного комментария в переведённом файле: указатель + рабочий
     *  инвариант укладываются с запасом; эссе — уже нет. */
    private static final int MAX_COMMENT_BYTES = 1536;

    /** Весь каталог без исключений: inherited-дельта print.css приземлена
     *  (e704b9e5), его эссе уехали в досье (REF-014) — ARCH-02 закрыт целиком. */
    private static final List<String> EXCLUDED = List.of();

    @Test
    void migratedFilesKeepEssaysInDocsNotInCss() throws IOException {
        List<String> migrated;
        try (var files = Files.list(CSS_DIR)) {
            migrated = files.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".css"))
                    .filter(n -> !EXCLUDED.contains(n))
                    .sorted()
                    .toList();
        }
        List<String> offenders = new ArrayList<>();
        for (String name : migrated) {
            String css = Files.readString(CSS_DIR.resolve(name), StandardCharsets.UTF_8);
            Matcher m = COMMENT.matcher(css);
            while (m.find()) {
                int bytes = m.group().getBytes(StandardCharsets.UTF_8).length;
                if (bytes > MAX_COMMENT_BYTES) {
                    int line = (int) css.substring(0, m.start()).chars().filter(c -> c == '\n').count() + 1;
                    offenders.add(name + ":" + line + " — " + bytes + " байт");
                }
            }
            // Тесты quiz-app бегут с CWD modules/quiz-app — корень репозитория на два уровня выше.
            // Досье обязано существовать только у файлов, которые на него ссылаются:
            // файл без эссе досье не заводил.
            if (css.contains("docs/css-decisions/")) {
                Path dossier = Path.of("../../docs/css-decisions/" + name.replace(".css", ".md"));
                assertThat(Files.exists(dossier))
                        .as("%s ссылается на досье %s, а его не существует — указатели ведут в никуда", name, dossier)
                        .isTrue();
            }
        }
        assertThat(offenders)
                .as("эссе вернулись в переведённый CSS — история живёт в docs/css-decisions/<файл>.md, "
                        + "у правила остаётся указатель и рабочий инвариант")
                .isEmpty();
    }
}
