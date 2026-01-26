#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Комплексный скрипт для исправления всех косяков в interview файлах.
Обрабатывает разметку, артефакты Key/Value, разрывы тегов, и прочие проблемы.
"""

import os
import re
import sys
from pathlib import Path
from typing import Tuple

class MarkdownFixer:
    """Класс для исправления проблем в markdown файлах"""
    
    def __init__(self, verbose=False):
        self.verbose = verbose
        self.stats = {
            'files_processed': 0,
            'files_modified': 0,
            'fixes_applied': 0,
            'errors': []
        }
    
    def log(self, message):
        """Логирование сообщений"""
        if self.verbose:
            print(f"[*] {message}")
    
    def fix_key_value_artifacts(self, text: str) -> Tuple[str, int]:
        """Исправить артефакты **Key** и **Value**"""
        fixes = 0
        original = text
        
        # **Key** слово → ключевое слово
        text = re.sub(r'\*\*Key\*\*\s+(?:слов[ао]|ключ)', 'ключевое слово', text)
        fixes += len(re.findall(r'\*\*Key\*\*\s+(?:слов[ао]|ключ)', original))
        
        # **Key** в других контекстах → ключ (кроме Redis, Kafka)
        if 'redis-interview' not in str(text.lower()) and 'kafka-interview' not in str(text.lower()):
            before = text.count('**Key**')
            text = re.sub(r'\b\*\*Key\*\*\b', 'ключ', text)
            fixes += before - text.count('**Key**')
        
        # **Value** → значение
        before = text.count('**Value**')
        text = re.sub(r'(?<![a-zA-Z_])\*\*Value\*\*(?![a-zA-Z_])', 'значение', text)
        fixes += before - text.count('**Value**')
        
        return text, fixes
    
    def fix_unclosed_tags(self, text: str) -> Tuple[str, int]:
        """Исправить незакрытые **теги**"""
        fixes = 0
        
        # **Something. в конце строки → **Something**.
        lines = text.split('\n')
        for i, line in enumerate(lines):
            if re.search(r'\*\*([^*]+)\.\s*$', line):
                lines[i] = re.sub(r'\*\*([^*]+)\.$', r'**\1**.', line)
                fixes += 1
        
        text = '\n'.join(lines)
        return text, fixes
    
    def fix_dirty_markup(self, text: str) -> Tuple[str, int]:
        """Исправить грязную разметку"""
        fixes = 0
        
        # **/** → /
        before = text.count('**/**')
        text = text.replace('**/**', '/')
        fixes += before - text.count('**/**')
        
        # **/* → /*
        text = text.replace('**//*', '/*')
        
        # ,** → , **
        text = text.replace(',**', ', **')
        
        # Двойные ** без содержимого
        text = re.sub(r'\*\*\*\*', '**', text)
        
        return text, fixes
    
    def fix_list_formatting(self, text: str) -> Tuple[str, int]:
        """Исправить форматирование списков"""
        fixes = 0
        
        # - - → -
        before = text.count('- -')
        text = text.replace('- -', '-')
        fixes += before - text.count('- -')
        
        # * * → *
        before = text.count('* *')
        text = text.replace('* *', '*')
        fixes += before - text.count('* *')
        
        return text, fixes
    
    def fix_code_blocks(self, text: str) -> Tuple[str, int]:
        """Исправить кодовые блоки"""
        fixes = 0
        
        # Убрать пустые строки в начале/конце кодовых блоков
        lines = text.split('\n')
        
        for i, line in enumerate(lines):
            # Убрать пробелы после ```
            if line.strip().startswith('``` '):
                lines[i] = line.replace('``` ', '```')
                fixes += 1
        
        text = '\n'.join(lines)
        return text, fixes
    
    def fix_spacing_issues(self, text: str) -> Tuple[str, int]:
        """Исправить проблемы с пробелами"""
        fixes = 0
        
        # Двойные пробелы после точки
        before = text.count('.  ')
        text = re.sub(r'\.\s{2,}', '. ', text)
        fixes += max(0, before - text.count('.  '))
        
        # Пробелы перед запятой/точкой
        text = re.sub(r'\s+([.,;:!?])', r'\1', text)
        
        return text, fixes
    
    def fix_git_commands(self, text: str) -> Tuple[str, int]:
        """Исправить команды git"""
        fixes = 0
        
        # git config --list. → git config --list
        if 'git config --list.' in text:
            text = text.replace('git config --list.', 'git config --list')
            fixes += 1
        
        return text, fixes
    
    def normalize_line_endings(self, text: str) -> Tuple[str, int]:
        """Нормализовать концы строк"""
        fixes = 0
        
        # Убрать лишние пустые строки (более 2 подряд)
        before = text.count('\n\n\n')
        text = re.sub(r'\n{3,}', '\n\n', text)
        fixes += before - text.count('\n\n\n')
        
        # Убедиться, что файл заканчивается одной пустой строкой
        if not text.endswith('\n'):
            text += '\n'
            fixes += 1
        elif text.endswith('\n\n'):
            text = text.rstrip() + '\n'
            fixes += 1
        
        return text, fixes
    
    def apply_all_fixes(self, text: str) -> Tuple[str, int]:
        """Применить все исправления"""
        total_fixes = 0
        
        fixes_list = [
            ('Key/Value артефакты', self.fix_key_value_artifacts),
            ('Unclosed tags', self.fix_unclosed_tags),
            ('Dirty markup', self.fix_dirty_markup),
            ('List formatting', self.fix_list_formatting),
            ('Code blocks', self.fix_code_blocks),
            ('Spacing issues', self.fix_spacing_issues),
            ('Git commands', self.fix_git_commands),
            ('Line endings', self.normalize_line_endings),
        ]
        
        for name, fixer in fixes_list:
            text, fixes = fixer(text)
            if fixes > 0:
                self.log(f"  {name}: +{fixes} исправлений")
                total_fixes += fixes
        
        return text, total_fixes
    
    def process_file(self, filepath: str) -> bool:
        """Обработать один файл"""
        self.stats['files_processed'] += 1
        
        try:
            # Читаем файл
            with open(filepath, 'r', encoding='utf-8') as f:
                original_content = f.read()
            
            # Применяем исправления
            fixed_content, total_fixes = self.apply_all_fixes(original_content)
            
            # Если что-то изменилось, сохраняем
            if fixed_content != original_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(fixed_content)
                
                self.stats['files_modified'] += 1
                self.stats['fixes_applied'] += total_fixes
                
                print(f"✓ {filepath} ({total_fixes} исправлений)")
                return True
            else:
                if self.verbose:
                    print(f"- {filepath} (без изменений)")
                return False
        
        except Exception as e:
            error_msg = f"✗ {filepath}: {str(e)}"
            self.stats['errors'].append(error_msg)
            print(error_msg)
            return False
    
    def process_directory(self, directory: str, pattern: str = "*interview.md") -> None:
        """Обработать все файлы в директории"""
        path = Path(directory)
        
        if not path.exists():
            print(f"Ошибка: директория {directory} не найдена")
            sys.exit(1)
        
        print(f"\n📋 Обработка файлов в: {directory}")
        print(f"🔍 Паттерн поиска: {pattern}\n")
        
        files = sorted(path.rglob(pattern))
        
        if not files:
            print(f"⚠️  Файлы с паттерном '{pattern}' не найдены")
            return
        
        print(f"📦 Найдено файлов: {len(files)}\n")
        
        for filepath in files:
            self.process_file(str(filepath))
        
        self.print_summary()
    
    def print_summary(self) -> None:
        """Выводить сводку"""
        print("\n" + "="*60)
        print("📊 ИТОГОВАЯ СТАТИСТИКА")
        print("="*60)
        print(f"✓ Обработано файлов: {self.stats['files_processed']}")
        print(f"✓ Модифицировано файлов: {self.stats['files_modified']}")
        print(f"✓ Всего исправлений: {self.stats['fixes_applied']}")
        
        if self.stats['errors']:
            print(f"\n⚠️  Ошибок: {len(self.stats['errors'])}")
            for error in self.stats['errors']:
                print(f"  {error}")
        
        print("="*60 + "\n")

def main():
    import argparse
    
    parser = argparse.ArgumentParser(
        description='Комплексное исправление interview markdown файлов'
    )
    parser.add_argument(
        'directory',
        nargs='?',
        default='cheat-sheet/cheatsheets/interview',
        help='Директория для обработки'
    )
    parser.add_argument(
        '--pattern',
        default='*interview.md',
        help='Паттерн поиска файлов'
    )
    parser.add_argument(
        '--verbose', '-v',
        action='store_true',
        help='Подробный вывод'
    )
    
    args = parser.parse_args()
    
    fixer = MarkdownFixer(verbose=args.verbose)
    fixer.process_directory(args.directory, args.pattern)

if __name__ == '__main__':
    main()
