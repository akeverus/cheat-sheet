package com.cheatsheet.quiz.service.diagram;

/**
 * Отличает текстовую схему (ASCII/Unicode-рисунок) от настоящего исходного кода
 * в поле {@code codeSnippet}.
 *
 * <p>Зачем это нужно. Импортёр ({@code MarkdownQuestionParser}) кладёт в
 * {@code codeSnippet} любой не-mermaid блок ```…``` длиннее порога — включая
 * нарисованные схемы, которые авторы шпаргалок пишут прямо в тексте. В интерфейсе
 * такой блок попадал во вкладку «Пример кода» и врал о своём содержимом: на 70
 * вопросах он дословно повторял соседнюю mermaid-схему, ещё на ~90 был
 * единственной схемой вопроса, спрятанной под ярлыком «код».</p>
 *
 * <p>Признак рисунка — символы псевдографики Unicode (U+2500…U+25FF: рамки,
 * блочные элементы, геометрические фигуры). Ни один язык не использует их
 * синтаксически: в корпусе все 185 таких блоков оказались схемами, ни одного
 * исходника.</p>
 *
 * <p>Одиночная стрелка сама по себе признаком <b>не</b> считается, хотя это и
 * выглядит логично. В русских комментариях к коду стрелка — обычная пунктуация
 * («0 → NULL», «данные: канал → буфер»), и на корпусе такой критерий уводил во
 * вкладку «Схема» 383 блока настоящего кода. Засчитывается только строка,
 * состоящая из стрелки целиком — вертикальная связка вида {@code "    ↓"}
 * бывает лишь в нарисованной последовательности (45 блоков, все схемы).</p>
 *
 * <p>Обратный случай — настоящий код с нарисованным комментарием внутри —
 * отсекается вторым условием: если заметная доля строк выглядит как операторы
 * (точка с запятой, скобки, XML-теги, ключевые слова), блок считается кодом
 * независимо от псевдографики.</p>
 */
public final class AsciiDiagramDetector {

    /** Доля «синтаксических» строк, начиная с которой блок считаем кодом. */
    private static final double CODE_LINE_RATIO = 0.3;

    private AsciiDiagramDetector() {
    }

    /**
     * @return {@code true}, если блок — рисунок, а не исходный код
     */
    public static boolean isDrawing(String snippet) {
        if (snippet == null || snippet.isBlank()) {
            return false;
        }
        String[] lines = snippet.split("\n", -1);
        int meaningful = 0;
        int drawn = 0;
        int codeLike = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            meaningful++;
            if (isDrawnLine(line, trimmed)) {
                drawn++;
            }
            if (looksLikeStatement(trimmed)) {
                codeLike++;
            }
        }
        if (drawn == 0 || meaningful == 0) {
            return false;
        }
        return (double) codeLike / meaningful < CODE_LINE_RATIO;
    }

    private static boolean isDrawnLine(String line, String trimmed) {
        return hasBoxGlyph(line) || isArrowOnly(trimmed);
    }

    private static boolean hasBoxGlyph(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            // Box Drawing (U+2500…U+257F), Block Elements (U+2580…U+259F),
            // Geometric Shapes (U+25A0…U+25FF).
            if (c >= '\u2500' && c <= '\u25FF') {
                return true;
            }
        }
        return false;
    }

    /** Строка целиком из стрелок и вертикальных палок — связка нарисованной цепочки. */
    private static boolean isArrowOnly(String trimmed) {
        boolean seenArrow = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c >= '\u2190' && c <= '\u21FF') {
                seenArrow = true;
            } else if (c != '|' && !Character.isWhitespace(c)) {
                return false;
            }
        }
        return seenArrow;
    }

    private static boolean looksLikeStatement(String trimmed) {
        char last = trimmed.charAt(trimmed.length() - 1);
        if (last == ';' || last == '{' || last == '}') {
            return true;
        }
        char first = trimmed.charAt(0);
        if (first == '<' || first == '@' || first == '#') {
            return true;
        }
        return startsWithKeyword(trimmed);
    }

    private static final String[] KEYWORDS = {
            "public ", "private ", "protected ", "class ", "interface ", "enum ",
            "import ", "package ", "def ", "function ", "const ", "let ", "var ",
            "return ", "if ", "if(", "for ", "for(", "while ", "while(", "SELECT ", "select "
    };

    private static boolean startsWithKeyword(String trimmed) {
        for (String kw : KEYWORDS) {
            if (trimmed.startsWith(kw)) {
                return true;
            }
        }
        return false;
    }
}
