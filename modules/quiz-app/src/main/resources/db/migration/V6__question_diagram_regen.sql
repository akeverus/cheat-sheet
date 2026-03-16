-- Mermaid-диаграмма для визуализации вопроса (nullable)
ALTER TABLE questions ADD COLUMN diagram_mermaid TEXT;

-- Счётчик перезагрузок вариантов ответа для адаптивной сложности AI
ALTER TABLE questions ADD COLUMN regen_count INTEGER NOT NULL DEFAULT 0;
