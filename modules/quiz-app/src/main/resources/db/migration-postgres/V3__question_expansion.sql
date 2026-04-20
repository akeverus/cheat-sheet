ALTER TABLE questions ADD COLUMN IF NOT EXISTS source_slug TEXT;

UPDATE questions SET source_slug = slug WHERE source_slug IS NULL;
