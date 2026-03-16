ALTER TABLE answer_options ADD COLUMN prompt_version INTEGER NOT NULL DEFAULT 1;
ALTER TABLE answer_options ADD COLUMN quality_profile_version INTEGER NOT NULL DEFAULT 1;
