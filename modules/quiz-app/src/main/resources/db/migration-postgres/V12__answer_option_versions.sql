ALTER TABLE answer_options
    ADD COLUMN IF NOT EXISTS prompt_version INTEGER NOT NULL DEFAULT 1;

ALTER TABLE answer_options
    ADD COLUMN IF NOT EXISTS quality_profile_version INTEGER NOT NULL DEFAULT 1;
