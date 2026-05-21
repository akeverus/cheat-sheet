#!/usr/bin/env bash
# verify-mcq.sh — проверка MCQ-блоков в interview-файлах на соответствие SKILL контракту.
#
# Usage:
#   bash scripts/verify-mcq.sh <file1.md> [file2.md ...]
#   bash scripts/verify-mcq.sh                            # без аргументов: проверить staged files
#
# Возвращает 0 если все проверки прошли, 1 при первом нарушении.
# Выводит точные нарушения с номерами строк (формат grep -n) для удобства IDE-навигации.

set -euo pipefail

# ──────────────── ANSI цвета (без зависимости от tput если non-tty) ──────────
if [[ -t 1 ]]; then
    RED='\033[0;31m'
    YEL='\033[0;33m'
    GRN='\033[0;32m'
    BLU='\033[0;34m'
    NC='\033[0m'
else
    RED='' YEL='' GRN='' BLU='' NC=''
fi

# ──────────────── Сбор списка файлов ──────────────────────────────────────────
declare -a FILES
if [[ $# -gt 0 ]]; then
    FILES=("$@")
else
    # Staged files mode (для pre-commit hook)
    mapfile -t FILES < <(git diff --cached --name-only --diff-filter=AM | grep -E '^cheatsheets/interview/.*\.md$' || true)
    if [[ ${#FILES[@]} -eq 0 ]]; then
        # Если ничего не staged — проверить current state interview/
        mapfile -t FILES < <(find cheatsheets/interview -name '*.md' -type f 2>/dev/null || true)
    fi
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo -e "${BLU}verify-mcq: no interview .md files to check, skipping${NC}"
    exit 0
fi

# ──────────────── Запрещённые паттерны (regex, описание) ──────────────────────
# Каждое правило — массив: [regex] [описание для error message]
declare -a RULES=(
    '^>\s*####\s*[ABCD])|^==BANNED== \#\#\#\# подзаголовки внутри MCQ-callout не парсятся'
    'Противоположное направление|^==BANNED== placeholder из corruption '
    'Это смежное, но отличное понятие|^==BANNED== placeholder из corruption'
    'Альтернативное решение которое не подходит|^==BANNED== placeholder из corruption'
    'Третий вариант который не работает в production|^==BANNED== placeholder из corruption'
    'Правильный ответ \| Корректное описание концепции|^==BANNED== placeholder из corruption'
    'Объяснение концепции 2-3 предложения|^==BANNED== placeholder из corruption'
    '❌ ПОСЛЕДСТВИЕ:|^==BANNED== эмодзи-маркер старого формата'
    '✓ ПРИМЕНЯТЬ:|^==BANNED== эмодзи-маркер старого формата'
    '📋 ПРАВИЛО:|^==BANNED== эмодзи-маркер старого формата'
    '🔗 См\. Q[0-9]|^==BANNED== эмодзи-маркер старого формата'
    '^> - \[x\]\s+\*\*|^==BANNED== bold на correct option-line — нарушает визуальную симметрию опций'
    '^> - \[[ x]\][^|]*\|[[:space:]]*[^[:space:]]|^==BANNED== legacy single-line `[x] text | explanation` — используйте named-секции (Развёрнутое объяснение/Пример/Когда применять/Подводные камни/Связанные вопросы для correct; Что на самом деле/Откуда путаница/Если бы это было правдой/Как было бы правильно для wrong)'
    'mcq_format_version:[[:space:]]*1[^0-9]|^==BANNED== mcq_format_version: 1 deprecated — переведите файл в v2 формат'
)

# ──────────────── Whitelist секций (для информационной проверки) ──────────────
declare -a CORRECT_SECTIONS=(
    'Развёрнутое объяснение'
    'Пример'
    'Когда применять'
    'Подводные камни'
    'Связанные вопросы'
)
declare -a WRONG_SECTIONS=(
    'Что на самом деле'
    'Откуда путаница'
    'Если бы это было правдой'
    'Как было бы правильно'
)
# Любые `**Секция.**` не из этого списка — warning (не fail)
declare -a ALL_WHITELIST=("${CORRECT_SECTIONS[@]}" "${WRONG_SECTIONS[@]}")

# ──────────────── Проход по файлам ────────────────────────────────────────────
ERR=0
WARN=0
for file in "${FILES[@]}"; do
    if [[ ! -f "$file" ]]; then
        echo -e "${YEL}verify-mcq: skip non-existent $file${NC}"
        continue
    fi

    # Только файлы с `> [!mcq]` блоками — пустые/служебные пропускаем
    if ! grep -q '^> \[!mcq\]' "$file" 2>/dev/null; then
        continue
    fi

    file_err=0
    for rule in "${RULES[@]}"; do
        regex="${rule%%|*}"
        desc="${rule#*|}"
        matches=$(grep -nE "$regex" "$file" 2>/dev/null || true)
        if [[ -n "$matches" ]]; then
            echo -e "${RED}FAIL${NC} ${file}: ${desc#==BANNED== }"
            echo "$matches" | sed 's/^/   /'
            file_err=$((file_err + 1))
        fi
    done

    # Legacy `[[Q<N>]]` без префикса файла — FAIL (запрещены).
    # Все ссылки должны быть в Obsidian-native форме `[[<file-name>#Q<N>]]`.
    legacy_links=$(grep -nE '\[\[Q[0-9]+\]\]' "$file" 2>/dev/null || true)
    if [[ -n "$legacy_links" ]]; then
        echo -e "${RED}FAIL${NC} ${file}: legacy [[Q<N>]] links forbidden — use [[<file-name>#Q<N>]]"
        echo "$legacy_links" | sed 's/^/   /'
        file_err=$((file_err + 1))
    fi

    # Проверка whitelist секций — warning, не error.
    # Ищем `**Section.**` строки внутри callout (после `>` + индентация), кроме
    # сильно вложенных (которые могут быть в code block).
    # awk: захватываем имя секции между **...**, после `> ` + ≥2 пробелов.
    sections_found=$(awk '
        /^> *\[!mcq\]/ {in_mcq=1; next}
        /^[^>]/ {in_mcq=0; next}
        in_mcq && /^>[[:space:]]{2,}\*\*[^*]+\*\*/ {
            match($0, /\*\*[^*]+\*\*/)
            s = substr($0, RSTART+2, RLENGTH-4)
            sub(/\.$/, "", s)
            print s
        }
    ' "$file" 2>/dev/null | sort -u || true)
    if [[ -n "$sections_found" ]]; then
        while IFS= read -r section; do
            [[ -z "$section" ]] && continue
            allowed=0
            for w in "${ALL_WHITELIST[@]}"; do
                if [[ "$section" == "$w" ]]; then
                    allowed=1
                    break
                fi
            done
            if [[ $allowed -eq 0 ]]; then
                echo -e "${YEL}WARN${NC} ${file}: non-whitelist section '**${section}.**'"
                echo "   should be one of: ${ALL_WHITELIST[*]}"
                WARN=$((WARN + 1))
            fi
        done <<< "$sections_found"
    fi

    if [[ $file_err -eq 0 ]]; then
        echo -e "${GRN}OK${NC}   ${file}"
    else
        ERR=$((ERR + file_err))
    fi
done

# ──────────────── Итог ────────────────────────────────────────────────────────
echo
if [[ $ERR -gt 0 ]]; then
    echo -e "${RED}verify-mcq: $ERR error(s), $WARN warning(s) — commit blocked${NC}"
    exit 1
elif [[ $WARN -gt 0 ]]; then
    echo -e "${YEL}verify-mcq: $WARN warning(s) — commit allowed but review needed${NC}"
    exit 0
else
    echo -e "${GRN}verify-mcq: all MCQ blocks valid${NC}"
    exit 0
fi
