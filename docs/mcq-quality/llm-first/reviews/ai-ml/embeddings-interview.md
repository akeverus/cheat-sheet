# Embeddings — редакция MCQ

- Дата: 2026-11-04. Модель: GPT-6.
- Статус: `VISUAL_PARITY_REVIEWED`. [Отдельная ревизия всех A–D](embeddings-visual-parity-2026-11-09.md) завершена; независимое ревью и `FINAL PASS` не проводились.
- Охват: все видимые Q1–Q29, 29 блоков B0, варианты A–D и все поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json`; SHA-256 `6394d1057f2dbd2b1756ec31f2328383a10cdbb133d7bd9938ec2c8e3a18db75`.
- Fact freshness: `docs/mcq-quality/fact-freshness/embeddings-interview.json`; SHA-256 `58d705e1d999649b77358fc7126394a5ea56f05369fd68e1cd050a2d81d48b85`.
- Парный Markdown: `cheatsheets/interview/ai-ml/embeddings-interview.md`; SHA-256 `6c670d41e7657e06ab771ba09d92a0a4ec66083c6ccfbcd7df242b9690dbfba4`.

## Основные исправления

- Убраны устаревшие рейтинги, цены, безусловные советы по выбору модели и фиксированные проценты выигрыша или потери качества. Разведены конкретные версии Cohere Embed v3, Voyage 4 и их лимиты.
- Исправлены факты о нормализации, эквивалентности cosine/dot/L2, многоязычном поиске и совместимости моделей. Варианты с переводом при cross-lingual поиске больше не объявляются ложными сами по себе.
- Обновлены сведения о MTEB, поддерживаемых модальностях ImageBind, речевых и аудиомоделях, упаковке бинарных векторов и текущем `SentenceTransformerTrainer`.
- Ключ кеша привязан к модели и параметрам входа. Вопросы о приватности учитывают исследования инверсии embeddings без объявления вектора обратимым архивом.
- В шпаргалке сохранены полезные примеры, обновлены пролог и источники, оглавление связано с явными якорями Q1–Q29. После редакции перечитаны все варианты без объяснений; сложные случаи и изменённые поля `sections` дополнительно сверены с теорией.
- Дополнительная редакторская проверка всех 29 блоков выявила подсказки по форме: правильные ответы были осторожнее и содержательнее неверных. Переписаны дистракторы, особенно в Q3, Q15 и Q25. В Q7 вопрос JSON ошибочно относился к Sentence Transformers при ответах о Voyage AI; формулировки вопросов JSON приведены к парной шпаргалке.
- После внешней выборочной проверки правильный ответ Q3 уточнён до того же механизма обучения, который описывают остальные варианты: сближение positive-пары и отделение negative-пар. Поля `sections` согласованы с новой формулировкой.

## Первичные источники

- [OpenAI Embeddings Guide](https://developers.openai.com/api/docs/guides/embeddings): исходные размерности `text-embedding-3`, параметр `dimensions` и повторная нормализация при ручном усечении.
- [Cohere Embed](https://docs.cohere.com/docs/cohere-embed): модели v3 и v5, размерности, лимиты и совместимость Pro/Fast в v5.
- [Voyage AI Embeddings](https://docs.voyageai.com/docs/embeddings): модели Voyage 4, контекст, `input_type` и совместимость внутри серии.
- [Sentence Transformers: обучение](https://www.sbert.net/docs/sentence_transformer/training_overview.html), [loss](https://sbert.net/docs/package_reference/sentence_transformer/losses.html) и [квантование](https://sbert.net/examples/sentence_transformer/applications/embedding-quantization/README.html): Trainer, in-batch negatives и `np.packbits`.
- [MTEB](https://github.com/embeddings-benchmark/mteb) и [лидерборд](https://github.com/embeddings-benchmark/mteb/blob/main/docs/get_started/usage/leaderboard.md): расширяемый набор задач и текущий адрес результатов.
- [BGE-M3 model card](https://huggingface.co/BAAI/bge-m3): dense, sparse и multi-vector выходы.
- [CLIP](https://github.com/openai/CLIP), [ImageBind](https://github.com/facebookresearch/ImageBind) и [CLAP](https://github.com/LAION-AI/CLAP): API энкодеров и поддерживаемые модальности.
- [Morris et al., EMNLP 2023](https://aclanthology.org/2023.emnlp-main.765/): атаки восстановления текста из embeddings.

После отдельной ревизии формы и смысла вариантов устранены подсказки по длине, пунктуации и категоричности; обновлены Q5:D, Q6:D и их sections. После адресной выборки расширены ошибочные подходы в Q10, Q12, Q16, Q22, Q23 и Q29 и согласованы все затронутые sections. Актуальная таблица всех 29 блоков и границы проверки — в отчёте о визуальной ревизии.

## Открытые вопросы и проверка

Критических неразрешённых вопросов по Q/B после редакции не осталось. Сравнительное качество, цены и выигрыш от квантования для конкретного продукта требуют измерений на его корпусе; файл не выдаёт их за гарантии.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json` → `PASS: files=1 questions=29 blocks=29; semantic review NOT_RUN`.

Самопроверка: 29 формулировок JSON совпадают с Markdown, в каждом блоке один верный ответ, поля `sections` заполнены; `git diff --check` для этой пары файлов прошёл. Общий `git diff --check` сообщает о пробелах в изменённом ранее `docs/interview-coverage/inventory.tsv`; к этой редакции они не относятся.
