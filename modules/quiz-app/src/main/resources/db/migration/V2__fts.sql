CREATE VIRTUAL TABLE IF NOT EXISTS questions_fts
USING fts5(question_id UNINDEXED, question_text, answer_markdown);
