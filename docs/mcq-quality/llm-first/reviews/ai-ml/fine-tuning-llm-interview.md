# Fine-tuning LLM — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Статус: `VISUAL_PARITY_REVIEWED`; [отдельная ревизия всех A–D](fine-tuning-llm-visual-parity-2026-11-09.md) завершена. Независимое слепое ревью и `FINAL PASS` не проводились.
- Охват: Q1–Q35, 35 видимых заголовков, 35 блоков, все 140 A–D и их sections, парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/fine-tuning-llm-interview.json`; SHA-256 `5ddf79fe4c1e3f4a4438cc5b6188518eddb9060656fd4ee2d83e3aa6dd257861`.
- Markdown: `cheatsheets/interview/ai-ml/fine-tuning-llm-interview.md`; SHA-256 `1820b2a54f7ecee431a6ef680f402b04f962ddbc8a7852ee393f97de6a43017c`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/fine-tuning-llm-interview.json`; SHA-256 `016dfd82f4768ac5412ae7a65b838d96ac29a68c9a3e80e0dfd0fb87f2f90b7d`.

## Смысловые правки

Переписана теория Q1–Q35 и синхронизированы варианты и объяснения всех блоков. Сохранены номера и правильные буквы. Убраны универсальные обещания качества, памяти и стоимости: результат QLoRA для 65B на 48 GB оставлен результатом статьи, а требования к GPU и экономику предложено считать и измерять для своей конфигурации. Отделены обновляемые факты, поведение модели, структурированный вывод и проверка прав.

Исправлены роли DAPT/SFT и маски loss для prompt-completion и conversational данных. Пример TRL приведён к `SFTConfig.max_length`, `processing_class`, `peft_config` и `assistant_only_loss` при подходящем шаблоне. DPO описан как offline-обучение на парах, GRPO — как обучение с новыми ответами и наградой; проверяемая награда не названа единственно возможной. Уточнены границы PEFT, LoRA/DoRA, distillation, merging и multi-LoRA serving. Заголовки Q8, Q15, Q22 и Q25 приведены к содержанию ответов.

Статус провайдеров сверён на ноябрь 2026: OpenAI сворачивает fine-tuning, Anthropic не даёт self-service fine-tuning в Claude API, у Bedrock есть ограниченная поддержка Claude 3 Haiku, Google поддерживает supervised tuning для указанного списка Gemini. Убраны неподтверждённые сведения о внутреннем multi-LoRA OpenAI, старые тарифы и названия моделей как универсальные рекомендации.

После отдельной ревизии визуальной симметрии скорректированы все 35 блоков и question_text приведены к видимым заголовкам. После адресной выборки Q24 переписан со всеми четырьмя названными библиотеками в каждом A–D; теория и sections сверены с первичными руководствами. Формальные метрики, предупреждения и границы проверки записаны в отдельном отчёте.

## Источники и проверка

Сверены [OpenAI SFT](https://developers.openai.com/api/docs/guides/supervised-fine-tuning) и [deprecations](https://developers.openai.com/api/docs/deprecations), [Anthropic](https://platform.claude.com/docs/en/about-claude/glossary), [Amazon Bedrock](https://docs.aws.amazon.com/bedrock/latest/userguide/custom-model-fine-tuning.html), [Google tuning](https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/tuning/supervised-tuning), [TRL SFTTrainer](https://huggingface.co/docs/trl/sft_trainer), [PEFT](https://huggingface.co/docs/peft), статьи [QLoRA](https://arxiv.org/abs/2305.14314), [DPO](https://arxiv.org/abs/2305.18290), [DeepSeek-R1](https://arxiv.org/abs/2501.12948) и [LIMA](https://arxiv.org/abs/2305.11206). Для Q24 также проверены [Unsloth](https://unsloth.ai/docs/get-started/fine-tuning-llms-guide) и [Axolotl](https://docs.axolotl.ai/docs/getting-started.html). Изменчивые факты и риски записаны в fact-freshness.

Адресный валидатор: `PASS: files=1 questions=35 blocks=35; semantic review NOT_RUN`. `git diff --check` для трёх файлов темы прошёл. Для `FINAL` нужен независимый слепой просмотр видимых Q/A–D и проверка sections на указанных SHA.
