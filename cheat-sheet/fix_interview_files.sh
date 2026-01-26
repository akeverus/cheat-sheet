#!/bin/bash
# Комплексный скрипт для исправления всех косяков в interview markdown файлах
# Использует perl для более мощной обработки

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_DIR="${1:-.}"
PATTERN="${2:-*interview.md}"

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Счетчики
FILES_PROCESSED=0
FILES_MODIFIED=0
TOTAL_FIXES=0

# Логирование
log_info() {
    echo -e "${BLUE}[*]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[✓]${NC} $1"
}

log_error() {
    echo -e "${RED}[✗]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

# Функция для применения исправлений к файлу
fix_file() {
    local file="$1"
    local fixes=0
    
    # Создаем бэкап (опционально)
    # cp "$file" "$file.bak"
    
    # 1. Исправить Key/Value артефакты
    # **Key** → ключ (кроме redis/kafka)
    if [[ "$file" != *"redis"* ]] && [[ "$file" != *"kafka"* ]]; then
        perl -i -pe 's/\*\*Key\*\*(?!\*)/ключ/g' "$file" && ((fixes++))
    fi
    
    # **Value** → значение
    perl -i -pe 's/(?<![a-zA-Z_])\*\*Value\*\*(?![a-zA-Z_])/значение/g' "$file" && ((fixes++))
    
    # 2. Исправить незакрытые теги
    # **Something. → **Something**.
    perl -i -pe 's/\*\*([^*]+)\.\s*$/\*\*$1\*\*\./gm' "$file" && ((fixes++))
    
    # 3. Исправить грязную разметку
    # **/** → /
    perl -i -pe 's/\*\*\/\*\*//g' "$file" && ((fixes++))
    
    # ,** → , **
    perl -i -pe 's/,\*\*/, \*\*/g' "$file" && ((fixes++))
    
    # 4. Исправить форматирование списков
    # - - → -
    perl -i -pe 's/^\- \-/\-/g' "$file" && ((fixes++))
    
    # 5. Убрать двойные пробелы после точки
    perl -i -pe 's/\.  +/\. /g' "$file" && ((fixes++))
    
    # 6. Убрать пробелы перед запятой/точкой
    perl -i -pe 's/\s+([.,;:!?])/$1/g' "$file" && ((fixes++))
    
    # 7. Исправить команды git
    perl -i -pe 's/git config --list\./git config --list/g' "$file" && ((fixes++))
    
    # 8. Убрать лишние пустые строки
    perl -i -pe 's/\n\n\n+/\n\n/g' "$file" && ((fixes++))
    
    # 9. Убедиться, что файл заканчивается одной пустой строкой
    if [[ $(tail -c 2 "$file" | od -An -tx1) != " 0a" ]]; then
        echo "" >> "$file"
        ((fixes++))
    fi
    
    echo "$fixes"
}

# Основная логика
main() {
    echo ""
    log_info "=========================================="
    log_info "Комплексное исправление interview файлов"
    log_info "=========================================="
    echo ""
    log_info "Директория: $TARGET_DIR"
    log_info "Паттерн: $PATTERN"
    echo ""
    
    # Проверяем наличие директории
    if [[ ! -d "$TARGET_DIR" ]]; then
        log_error "Директория не найдена: $TARGET_DIR"
        exit 1
    fi
    
    # Ищем файлы
    mapfile -t files < <(find "$TARGET_DIR" -name "$PATTERN" -type f | sort)
    
    if [[ ${#files[@]} -eq 0 ]]; then
        log_warning "Файлы с паттерном '$PATTERN' не найдены"
        exit 0
    fi
    
    log_info "Найдено файлов: ${#files[@]}"
    echo ""
    
    # Обрабатываем каждый файл
    for file in "${files[@]}"; do
        ((FILES_PROCESSED++))
        
        # Получаем исходный размер/хэш
        local original_hash=$(md5 -q "$file")
        
        # Применяем исправления
        local fixes=$(fix_file "$file")
        
        # Проверяем изменился ли файл
        local new_hash=$(md5 -q "$file")
        
        if [[ "$original_hash" != "$new_hash" ]]; then
            ((FILES_MODIFIED++))
            ((TOTAL_FIXES += fixes))
            log_success "$file (+$fixes исправлений)"
        else
            log_info "  $file (без изменений)"
        fi
    done
    
    # Выводим статистику
    echo ""
    log_info "=========================================="
    log_info "СТАТИСТИКА"
    log_info "=========================================="
    log_info "Обработано файлов: $FILES_PROCESSED"
    log_info "Модифицировано: $FILES_MODIFIED"
    log_info "Всего исправлений: $TOTAL_FIXES"
    log_info "=========================================="
    echo ""
}

main
