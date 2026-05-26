---
title: "Вопросы на собеседовании: LLM Inference Optimization"
description: "Inference LLM в production: vLLM/TGI/TensorRT-LLM, KV cache + PagedAttention, continuous batching, speculative decoding, FP8/INT4 quantization, FlashAttention, prefix caching."
tags:
  - interview
  - ai-ml
  - inference-optimization
type: "interview"
difficulty: "advanced"
aliases:
  - "Inference Optimization interview"
  - "vLLM TensorRT-LLM SGLang"
  - "KV cache PagedAttention"
  - "Quantization speculative decoding"
updated: "2026-05-23"
---

# Вопросы на собеседовании: `LLM Inference Optimization`

**Inference optimization** — выжать максимум `tokens/sec` и минимум `latency` из дорогих GPU. Стек 2026: `vLLM`/`SGLang`/`TensorRT-LLM`, `PagedAttention` + `continuous batching`, `speculative decoding`, `FP8`/`INT4` quantization (`AWQ`, `GPTQ`), `FlashAttention v3`, `prefix caching`. Цена ошибки — `×10` к bill за GPU.

**Дата:** 2026-05-23

## Полезные ссылки

### Официальная документация и авторитетные источники

- [vLLM Documentation](https://docs.vllm.ai/) — main inference server
- [PagedAttention paper (Kwon et al., 2023)](https://arxiv.org/abs/2309.06180) — основа vLLM
- [FlashAttention paper (Dao et al., 2022)](https://arxiv.org/abs/2205.14135) — v1
- [FlashAttention-2 (Dao, 2023)](https://arxiv.org/abs/2307.08691)
- [FlashAttention-3 (2024)](https://arxiv.org/abs/2407.08608) — H100 FP8
- [AWQ paper (Lin et al., 2023)](https://arxiv.org/abs/2306.00978) — activation-aware quantization
- [GPTQ paper (Frantar et al., 2022)](https://arxiv.org/abs/2210.17323)
- [Speculative Decoding (Leviathan et al., 2023)](https://arxiv.org/abs/2211.17192)
- [EAGLE paper (Li et al., 2024)](https://arxiv.org/abs/2401.15077) — better draft model
- [Anthropic Prompt Caching](https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching)
- [OpenAI Prompt Caching](https://platform.openai.com/docs/guides/prompt-caching)
- [NVIDIA TensorRT-LLM](https://github.com/NVIDIA/TensorRT-LLM)
- [SGLang RadixAttention](https://lmsys.org/blog/2024-01-17-sglang/)
- [Hugging Face TGI](https://huggingface.co/docs/text-generation-inference/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Метрики и autoregressive nature**
- [Q1. (!) Какие метрики важны для LLM inference?](#q1--какие-метрики-важны-для-llm-inference)
- [Q2. (!) Почему LLM inference медленный? Autoregressive nature.](#q2--почему-llm-inference-медленный-autoregressive-nature)
- [Q3. Prefill vs decode stage — в чём разница?](#q3-prefill-vs-decode-stage--в-чём-разница)
- [Q4. Memory-bound vs compute-bound — что лимитирует decode?](#q4-memory-bound-vs-compute-bound--что-лимитирует-decode)

**KV Cache и PagedAttention**
- [Q5. (!) Что такое KV cache и зачем он нужен?](#q5--что-такое-kv-cache-и-зачем-он-нужен)
- [Q6. (!) Как посчитать размер KV cache?](#q6--как-посчитать-размер-kv-cache)
- [Q7. (!) PagedAttention — что это и почему ×24 throughput?](#q7--pagedattention--что-это-и-почему-24-throughput)
- [Q8. KV cache fragmentation — что это?](#q8-kv-cache-fragmentation--что-это)

**Continuous batching**
- [Q9. (!) Static batching vs continuous batching?](#q9--static-batching-vs-continuous-batching)
- [Q10. Iteration-level scheduling — как работает?](#q10-iteration-level-scheduling--как-работает)
- [Q11. Chunked prefill — зачем смешивать prefill и decode?](#q11-chunked-prefill--зачем-смешивать-prefill-и-decode)

**Speculative decoding**
- [Q12. (!) Speculative decoding — суть алгоритма?](#q12--speculative-decoding--суть-алгоритма)
- [Q13. EAGLE и Medusa heads — чем отличаются от классики?](#q13-eagle-и-medusa-heads--чем-отличаются-от-классики)
- [Q14. Когда speculative decoding НЕ помогает?](#q14-когда-speculative-decoding-не-помогает)

**Quantization (FP8/INT4/GPTQ/AWQ)**
- [Q15. (!) FP16/BF16/FP8/INT8/INT4 — таблица форматов?](#q15--fp16bf16fp8int8int4--таблица-форматов)
- [Q16. (!) GPTQ — как работает post-training quantization?](#q16--gptq--как-работает-post-training-quantization)
- [Q17. (!) AWQ — чем отличается от GPTQ?](#q17--awq--чем-отличается-от-gptq)
- [Q18. FP8 на H100 — почему it's a big deal?](#q18-fp8-на-h100--почему-its-a-big-deal)
- [Q19. Weight-only vs activation quantization?](#q19-weight-only-vs-activation-quantization)
- [Q20. GGUF и llama.cpp — для CPU/edge?](#q20-gguf-и-llamacpp--для-cpuedge)

**FlashAttention и Prefix caching**
- [Q21. (!) FlashAttention — почему 2-3× speedup?](#q21--flashattention--почему-2-3-speedup)
- [Q22. (!) Prefix caching / Automatic Prefix Caching (APC)?](#q22--prefix-caching--automatic-prefix-caching-apc)
- [Q23. (!) Anthropic Prompt Caching API — как использовать?](#q23--anthropic-prompt-caching-api--как-использовать)
- [Q24. OpenAI Prompt Caching — чем отличается?](#q24-openai-prompt-caching--чем-отличается)

**Inference servers**
- [Q25. (!) vLLM vs TensorRT-LLM vs TGI vs SGLang — сравнение?](#q25--vllm-vs-tensorrt-llm-vs-tgi-vs-sglang--сравнение)
- [Q26. vLLM config tuning — какие флаги важны?](#q26-vllm-config-tuning--какие-флаги-важны)
- [Q27. llama.cpp, Ollama, MLX — для local/edge?](#q27-llamacpp-ollama-mlx--для-localedge)

**Distributed serving (TP/PP/MoE)**
- [Q28. (!) Tensor Parallelism vs Pipeline Parallelism?](#q28--tensor-parallelism-vs-pipeline-parallelism)
- [Q29. MoE serving — почему Mixtral/DeepSeek сложнее?](#q29-moe-serving--почему-mixtraldeepseek-сложнее)
- [Q30. Multi-LoRA serving (LoRAX, Punica)?](#q30-multi-lora-serving-lorax-punica)
- [Q31. Disaggregated inference (prefill/decode split)?](#q31-disaggregated-inference-prefilldecode-split)

**Cost optimization**
- [Q32. (!) GPU выбор: A100 vs H100 vs H200 vs MI300X?](#q32--gpu-выбор-a100-vs-h100-vs-h200-vs-mi300x)
- [Q33. (!) Cost per 1M tokens — API vs self-host?](#q33--cost-per-1m-tokens--api-vs-self-host)
- [Q34. Batch API (OpenAI/Anthropic) — когда использовать?](#q34-batch-api-openaianthropic--когда-использовать)
- [Q35. (!) Model routing — cheap для simple, expensive для hard?](#q35--model-routing--cheap-для-simple-expensive-для-hard)
- [Q36. Streaming SSE — снижает ли реальную latency?](#q36-streaming-sse--снижает-ли-реальную-latency)


## Q1. (!) Какие метрики важны для LLM inference?

Не одна `latency`, а целая семья:

| Метрика | Что измеряет | Цель |
|---------|--------------|------|
| `TTFT` (Time To First Token) | Задержка до первого токена | <500ms для UX |
| `TPOT` (Time Per Output Token) | Среднее время на следующий токен | <50ms (=20 tok/sec) |
| `Throughput` | `tokens/sec` aggregate по всем requests | Максимизировать |
| `End-to-end latency` | TTFT + TPOT × N | Зависит от длины ответа |
| `Cost` | `$/1M tokens` | Минимизировать при SLA |
| `Goodput` | Доля requests, уложившихся в SLA | >99% |

**Trade-off:** maximize throughput → растёт `TTFT` (большой batch ждёт prefill). Continuous batching смягчает, но не убирает.

**Production dashboards** должны показывать `p50/p95/p99` отдельно для prefill (`TTFT`) и decode (`TPOT`).


## Q2. (!) Почему LLM inference медленный? Autoregressive nature.

LLM генерирует **по одному токену за раз**:

```
P(token_i | token_1, ..., token_{i-1})
```

Для каждого токена — **полный forward pass** через все слои модели.

**Llama-70B на H100:** ~50 tokens/sec для single request. Ответ 500 токенов = 10 секунд.

**Что нельзя:** распараллелить генерацию токенов внутри одного response — следующий зависит от предыдущего. Можно только параллелить **разные requests** (batching) или **угадывать токены вперёд** (speculative decoding).

**Bottleneck:** на decode-стадии — `memory bandwidth`, не compute. Каждый токен читает все веса модели из HBM. Для Llama-70B FP16 = 140 GB. На H100 (3.35 TB/s HBM) — лимит ~24 tokens/sec для одного запроса при идеальной утилизации.


## Q3. Prefill vs decode stage — в чём разница?

Два радикально разных режима:

**Prefill (prompt processing):**
- Все токены prompt'а обрабатываются **параллельно**
- `compute-bound` — GPU занят матричными умножениями
- Один проход, генерирует первый output token + полный KV cache
- Время ≈ линейно от длины prompt

**Decode (autoregressive generation):**
- Один токен за раз
- `memory-bound` — нужно прочитать все веса для одного токена
- GPU underutilized (compute idle), bandwidth saturated
- Время ≈ линейно от числа output токенов

**Следствие:** оптимизации разные. Prefill — `FlashAttention`, `chunked prefill`. Decode — `continuous batching` (объединить много decode-шагов), `speculative decoding`, KV cache.


## Q4. Memory-bound vs compute-bound — что лимитирует decode?

**Arithmetic intensity** = FLOPs / bytes. Сравни с `peak_FLOPS / peak_bandwidth` GPU.

**H100 ridge point:** ~290 FLOPs/byte (для BF16). Ниже — `memory-bound`, выше — `compute-bound`.

**Decode batch=1:** ~1-2 FLOPs/byte → жёстко `memory-bound`. GPU compute idle ~90%.

**Что помогает:**
- **Большой batch** — амортизирует чтение весов по N requests. Batch=32 → 32 FLOPs/byte.
- **Quantization** — INT4 уменьшает байты весов в 4 раза.
- **Speculative decoding** — больше токенов за один проход весов.
- **FlashAttention** — меньше HBM reads.

**Prefill batch=1, long prompt:** уже `compute-bound`, оптимизации другие.


## Q5. (!) Что такое KV cache и зачем он нужен?

Каждый attention слой считает `Attention(Q, K, V)`. При генерации токена `t`:
- `Q_t` — новый query от токена `t`
- `K_1..K_t`, `V_1..V_t` — keys/values **всех предыдущих токенов**

Без cache: пересчёт `K`, `V` для всех `t` токенов на каждом шаге → `O(n²)` по длине последовательности.

**С KV cache:**
- На шаге `t` считаем только `K_t`, `V_t`, остальные читаем из cache
- Сложность одного шага → `O(n)`
- Trade-off: память растёт линейно с длиной context

**Цена:** для Llama-70B 2K context на FP16 — **~1 GB на один request**. 100 concurrent requests = 100 GB только под KV cache.


## Q6. (!) Как посчитать размер KV cache?

```
kv_cache_bytes = 2 × n_layers × n_kv_heads × head_dim × seq_len × bytes_per_element
```

`2` — отдельно K и V. `bytes_per_element`: FP16=2, FP8=1, INT4=0.5.

**Примеры:**

| Model | Layers | KV heads × dim | Seq 2K (FP16) | Seq 32K (FP16) |
|-------|--------|----------------|---------------|----------------|
| Llama-3-8B | 32 | 8 × 128 | ~256 MB | ~4 GB |
| Llama-3-70B (GQA) | 80 | 8 × 128 | ~640 MB | ~10 GB |
| Llama-3-405B | 126 | 8 × 128 | ~1 GB | ~16 GB |
| Mistral-7B | 32 | 8 × 128 | ~256 MB | ~4 GB |

**GQA (Grouped Query Attention)** в Llama-3/70B радикально сокращает KV cache (8 KV heads вместо 64 Q heads — ×8 экономии vs MHA).

**Long context (128K+)** требует либо много памяти, либо KV quantization (`--kv-cache-dtype fp8`), либо PagedAttention для эффективного использования.


## Q7. (!) PagedAttention — что это и почему ×24 throughput?

**PagedAttention** (Kwon et al., SOSP 2023) — основа vLLM. Inspired by **OS virtual memory paging**.

**Проблема классики:** continuous KV cache требует резервировать `max_seq_len` под каждый request заранее. Реально использует, скажем, 30% → 70% памяти впустую.

**Идея:** KV cache хранится в **блоках** (pages) фиксированного размера (обычно 16 tokens). Logical sequence → table указателей на блоки.

```
Request A (50 tokens): blocks [42, 17, 3, ...]
Request B (50 tokens): blocks [42, 17, 81, ...]  # shared prefix!
```

**Преимущества:**
1. **Нет fragmentation** — все блоки одинаковые
2. **Shared blocks** — beam search, parallel sampling, prefix caching
3. **Memory efficiency** — упаковка >95% (vs ~30% у naive)
4. **Larger batch** → 2-4× throughput от одной памяти

**Результат paper:** до **×24 throughput** vs HuggingFace transformers (без батчинга). Vs TGI — обычно 1.5-2×.

```mermaid
graph TB
    subgraph "Naive KV Cache"
        N1[Request 1: reserved 2K tokens] --> NU1[used 200, wasted 1800]
        N2[Request 2: reserved 2K tokens] --> NU2[used 500, wasted 1500]
    end
    subgraph "PagedAttention"
        P1[Block table A: → 42, 17, 3] --> PB[Block pool 16-token pages]
        P2[Block table B: → 42, 17, 81] --> PB
        PB --> PM[Shared block 42 — system prompt]
    end
```


## Q8. KV cache fragmentation — что это?

**Internal fragmentation** — request зарезервировал место под max_seq_len, использует меньше. Остаток нельзя отдать другому request.

**External fragmentation** — между активными requests дыры разного размера, ни одному новому не влезть, хотя суммарно памяти хватает.

**Pre-PagedAttention миры:**
- HuggingFace transformers: ~20-40% полезного использования KV cache
- Naive batching: padding до max длины батча

**После PagedAttention:**
- Internal: <4% (последний неполный блок)
- External: 0 (все блоки одинаковые)
- Polish: **memory efficiency >95%**

**Эффект:** на той же GPU можно держать в 2-3× больше concurrent requests → выше throughput.


## Q9. (!) Static batching vs continuous batching?

```mermaid
gantt
    title Static vs Continuous Batching
    dateFormat X
    axisFormat %s

    section Static
    Req A 50 tok        :0, 5
    Req B 200 tok       :0, 20
    Req C 30 tok (wait) :0, 20
    Req D (queue)       :20, 5

    section Continuous
    Req A 50 tok        :0, 5
    Req B 200 tok       :0, 20
    Req C 30 tok        :0, 3
    Req D                :3, 8
    Req E                :5, 10
```

**Static batching:** собираем batch из N requests, ждём пока **все** закончат, потом следующий batch.
- ❌ Короткие requests ждут самый длинный
- ❌ GPU underutilized после первых завершений
- ❌ Высокая `TTFT` для новых requests

**Continuous batching** (iteration-level scheduling, Yu et al. 2022 — Orca):
- На каждой итерации (= один decode step) можно добавить новый request
- Завершённый request освобождает slot **сразу**
- Все active requests декодируют параллельно

**Прирост:** 5-10× throughput vs static при том же GPU. Default в vLLM, TGI, TensorRT-LLM, SGLang.


## Q10. Iteration-level scheduling — как работает?

Каждую **iteration** (≈один decode step, ~30-50ms):

1. **Scheduler** смотрит свободные KV blocks
2. Решает: добавить новые requests из queue (если хватает KV) или нет
3. Решает: какие active requests продолжить
4. **Если KV cache переполняется** — **preemption**:
   - `swap` — выгрузить часть на CPU RAM
   - `recompute` — выкинуть KV, при возобновлении пересчитать prefill

**Knobs в vLLM:**
- `--max-num-seqs` — максимум одновременных sequences (default 256)
- `--max-num-batched-tokens` — лимит на batch (обычно 8192-32768)
- `--scheduling-policy` — `fcfs` (FIFO) или `priority`

**Side effect:** TTFT нового request зависит от prefill длины. Длинный prompt (32K) блокирует decode остальных на сотни ms → решение `chunked prefill`.


## Q11. Chunked prefill — зачем смешивать prefill и decode?

**Проблема:** длинный prompt (32K токенов) на prefill занимает GPU на 500ms-1s. В это время все decode requests стоят → `TPOT` всплеск.

**Chunked prefill** (vLLM 0.4+, SGLang):
- Бить prefill на куски по `N` токенов (например, 512)
- В каждом batch — смесь prefill-chunk + decode-tokens активных requests
- GPU занят полезной работой, decode не блокируется

```
Batch N:    [decode_A, decode_B, prefill_C_chunk_1]
Batch N+1:  [decode_A, decode_B, prefill_C_chunk_2]
Batch N+2:  [decode_A, decode_B, decode_C]  # prefill finished
```

**Включить vLLM:** `--enable-chunked-prefill` (с 0.5+ default on для long context).

**Профит:** P99 `TPOT` падает в 3-5×, throughput почти не страдает.


## Q12. (!) Speculative decoding — суть алгоритма?

**Идея** (Leviathan et al., 2023): угадать N токенов **маленькой** моделью, **большая** модель проверит их **за один проход**.

```mermaid
sequenceDiagram
    participant Draft as Draft model (1B)
    participant Target as Target model (70B)

    Note over Draft: Дешёвый шаг
    Draft->>Draft: Generate tokens [t1, t2, t3, t4]
    Note over Target: Один forward pass
    Target->>Target: Verify [t1, t2, t3, t4] параллельно
    Target-->>Draft: Accepted [t1, t2], rejected at t3
    Note over Target: Sample correction for t3
    Target->>Draft: Continue from t3+
```

**Алгоритм:**
1. Draft model генерирует `k` токенов (быстро)
2. Target model делает **один forward pass** на эти `k` токенов параллельно — получает свои probability distributions
3. **Rejection sampling**: токен `i` принят если `target_prob[i] ≥ draft_prob[i]`, иначе reject и пересэмпл target
4. Все принятые токены идут в ответ, после rejection start с draft заново

**Speedup:** 2-3× при `acceptance rate ~70%`. Гарантирует **идентичный** target distribution (mathematically equivalent).

**Кандидаты в draft:**
- Llama-3-8B → Llama-3-70B (×8 разница)
- Llama-3-1B → Llama-3-405B
- `n-gram` draft (без модели вообще, для code/structured)


## Q13. EAGLE и Medusa heads — чем отличаются от классики?

**Medusa** (Cai et al., 2024) — добавить **multiple decoding heads** прямо в target model:
- N дополнительных heads предсказывают tokens t+1, t+2, ..., t+N параллельно
- Tree-based verification — пробует много candidates
- Speedup 2-3× без отдельной draft model
- ❌ Требует fine-tuning target

**EAGLE** (Li et al., 2024) — улучшенный draft на feature level:
- Draft предсказывает не tokens, а **features** второго-к-последнему слоя target
- Acceptance rate выше → 3-4× speedup
- EAGLE-2/3 (2024-2025) — adaptive draft tree

**Lookahead decoding** — n-gram cache из предыдущей генерации, без модели.

**В vLLM 2025:** native `--speculative-model`, `--num-speculative-tokens`. EAGLE через `eagle-config`.


## Q14. Когда speculative decoding НЕ помогает?

**Замедляет или не даёт выигрыша:**

1. **Высокий batch** — большая модель уже compute-bound, спек добавляет overhead draft model
2. **Низкий acceptance rate** (<30%) — draft слишком тупой или task сложный (творческие тексты, long reasoning)
3. **Distillation mismatch** — draft не из той же семьи (Mistral-draft для Llama-target)
4. **Очень короткие ответы** — overhead не амортизируется
5. **Reasoning models** (`o1`, `Claude Sonnet thinking`) с длинными CoT — draft часто промахивается на reasoning steps

**Где работает отлично:**
- Code generation (predictable patterns)
- Translation (deterministic)
- Structured output (JSON, схемы)
- Batch=1 latency-critical chat

**Rule of thumb:** проверить на своём workload — speedup сильно зависит от данных.


## Q15. (!) FP16/BF16/FP8/INT8/INT4 — таблица форматов?

| Format | Bits | Range/Precision | Hardware | Use case |
|--------|------|-----------------|----------|----------|
| `FP32` | 32 | Full | Любой | Legacy training |
| `FP16` | 16 | ±65K, 3 decimal | V100+ | Training/inference |
| `BF16` | 16 | ±3e38, 2 decimal | A100+, TPU | Training (stable) |
| `FP8 E4M3` | 8 | ±448, weights | H100+, MI300 | Weights, fwd pass |
| `FP8 E5M2` | 8 | ±57K, gradients | H100+ | Backward pass |
| `INT8` | 8 | -128..127 | Все GPU | Inference, less accuracy |
| `INT4` | 4 | -8..7 | A100+ (через CUTLASS) | GPTQ/AWQ inference |
| `NF4` | 4 | NormalFloat | QLoRA | Fine-tuning |
| `2-bit` | 2 | research | Custom kernels | Edge, experimental |
| `1.58-bit` | log2(3) | -1/0/+1 | BitNet (research) | Future |

**Память для Llama-3-70B:**
- FP16: 140 GB → 2× H100 80GB
- INT8: 70 GB → 1× H100 80GB
- INT4 (AWQ): 35 GB → 1× A100 40GB or RTX 4090×2

**Качество:** FP16→INT8 типично <1% drop на benchmarks. FP16→INT4 — 1-3% drop (зависит от метода).


## Q16. (!) GPTQ — как работает post-training quantization?

**GPTQ** (Frantar et al., 2022) — quantize **обученную** модель layer-by-layer, **без re-training**.

**Алгоритм:**
1. Берём calibration dataset (128-1024 samples)
2. Для каждого linear layer:
   - Считаем Hessian `H = 2 × X^T X` (importance матрица весов)
   - Квантуем веса колонка за колонкой
   - **Error compensation** — после quantization колонки `i`, размазываем ошибку на оставшиеся колонки (через `H^-1`)
3. Минимизирует `||W × X - W_quant × X||²`

**Параметры:**
- `bits=4`, `group_size=128` — обычный setting
- `desc_act=True` — порядок колонок по important (улучшает quality)

**Quality drop:** Llama-2-70B → INT4 GPTQ: <1% на MMLU.

**Использование:**

```bash
# vLLM serves GPTQ-quantized models
python -m vllm.entrypoints.openai.api_server \
  --model TheBloke/Llama-2-70B-GPTQ \
  --quantization gptq
```

**Готовые модели:** `TheBloke/*-GPTQ` на HuggingFace (тысячи).


## Q17. (!) AWQ — чем отличается от GPTQ?

**AWQ** (Activation-aware Weight Quantization, Lin et al., 2023) — заметили: **0.1-1% весов важны непропорционально**.

**Идея:** не все веса равны. Веса, через которые проходят большие активации, **критичнее**. Их нужно сохранить с большей точностью.

**Алгоритм:**
1. На calibration данных считаем `|X|` per-channel (activation magnitude)
2. Находим `salient` каналы (топ-1%)
3. **Scale** salient веса вверх перед quantization, scale обратно activations:
   ```
   y = (W / s) × (s × x)   # math equivalent
   ```
4. Это сохраняет salient веса в большей точности

**Vs GPTQ:**
- ✓ Лучше perplexity, особенно на instruction-tuned моделях
- ✓ Calibration faster (нет Hessian)
- ✓ Лучше generalizes на out-of-domain
- ✓ Hardware-friendly (нет странных group permutations)

**В vLLM:**

```bash
python -m vllm.entrypoints.openai.api_server \
  --model casperhansen/llama-3-70b-instruct-awq \
  --quantization awq
```

В **2025** AWQ — **default выбор** для INT4 quantization.


## Q18. FP8 на H100 — почему it's a big deal?

**H100** добавил native **FP8 Tensor Cores** — 2× compute throughput vs FP16, half memory.

**Форматы:**
- `E4M3`: 4 exponent + 3 mantissa, range ±448. Для **weights** и forward activations.
- `E5M2`: 5 exponent + 2 mantissa, range ±57K. Для **gradients** (training).

**Per-tensor / per-row scaling:** scaling factor чтобы избежать overflow.

**Inference выгоды:**
- **Throughput** ×1.5-2× vs FP16
- **KV cache** в FP8 → 2× больше batch
- **Quality drop** очень маленький (<0.5%) при правильной calibration

**В vLLM 2025:**

```bash
python -m vllm.entrypoints.openai.api_server \
  --model neuralmagic/Meta-Llama-3-70B-Instruct-FP8 \
  --quantization fp8 \
  --kv-cache-dtype fp8
```

**Где работает:** H100, H200, B100/B200, MI300X (другая поддержка). Не работает на A100 (нет FP8 cores — будет emulation).


## Q19. Weight-only vs activation quantization?

**Weight-only (W4A16):**
- Веса в INT4/INT8, активации остаются FP16
- Перед matmul: dequantize веса в FP16 на лету
- Большинство methods: GPTQ, AWQ, GGUF Q4_K_M
- ✓ Простой, минимальный quality drop
- ✗ Не использует INT tensor cores напрямую

**Weight + Activation (W8A8 / W4A8):**
- И веса, и активации квантуем
- Используем INT8 Tensor Cores → 2× compute
- ✗ Сложнее, larger quality drop (особенно для outliers в activations)
- Examples: `SmoothQuant`, `OmniQuant`

**FP8 W8A8:** comprovesum через H100, без проблем с outliers (range FP8 шире INT8).

**Practical 2025:**
- Decode `memory-bound` → **weight-only выигрывает** (грузит меньше данных)
- Prefill `compute-bound` long context → W8A8 FP8 даёт compute speedup
- Hybrid: `--quantization awq` (W4A16) + `--kv-cache-dtype fp8` — best of both


## Q20. GGUF и llama.cpp — для CPU/edge?

**GGUF** (GPT-Generated Unified Format) — формат `llama.cpp`. Quantizations:

| Type | Bits | Size Llama-3-8B | Use |
|------|------|-----------------|-----|
| `Q8_0` | 8 | 8.5 GB | Server, max quality |
| `Q6_K` | 6.5 | 6.6 GB | Balanced |
| `Q5_K_M` | 5.7 | 5.7 GB | Recommended laptop |
| `Q4_K_M` | 4.8 | 4.9 GB | Default sweet spot |
| `Q3_K_M` | 3.9 | 4.0 GB | Edge, small RAM |
| `Q2_K` | 2.6 | 3.2 GB | Phones, very degraded |

**Особенность:** **mixed precision** — important layers держат больше бит (e.g., `K`/`V` attention в Q6, FFN в Q4).

**Где использовать:**
- **CPU inference** (AVX2, AVX-512)
- **Apple Silicon** через Metal (M1-M4)
- **Mobile** (Android via JNI, iOS)
- **Edge devices** (Jetson, RPi 5)

**Wrapper'ы:** `Ollama`, `LM Studio`, `Jan` — user-friendly UI поверх `llama.cpp`.

**Не для production GPU serving** — там vLLM/TensorRT в разы быстрее.


## Q21. (!) FlashAttention — почему 2-3× speedup?

**Standard attention:**
```
S = Q @ K^T          # write N×N matrix to HBM
P = softmax(S)       # read N×N, write N×N
O = P @ V            # read N×N
```

Каждый шаг — round-trip через HBM (slow memory). Для seq_len=8K → ~16 GB трафика.

**FlashAttention** (Dao et al., 2022):
- **Tiling** — разбить Q/K/V на блоки, помещающиеся в SRAM (~100 KB)
- Вычислять softmax инкрементально (`online softmax`)
- **Recomputation** в backward вместо storing
- Никогда не материализуем полную `N×N` матрицу

**Результат:**
- ×2-3 wall-clock speedup
- ×5-20 memory savings → длиннее context (32K, 128K, 1M)
- IO-aware алгоритм — оптимизирует HBM↔SRAM traffic

**Версии:**
- **v1** (2022) — A100
- **v2** (2023) — лучше parallelism по seq_len
- **v3** (2024) — H100, FP8, asynchronous warp specialization, +1.5-2× vs v2

**Где:** built into vLLM, TGI, TensorRT-LLM, PyTorch SDPA (`torch.nn.functional.scaled_dot_product_attention`).


## Q22. (!) Prefix caching / Automatic Prefix Caching (APC)?

**Сценарий:** RAG, агенты, multi-turn chat — все requests делят общий **system prompt + examples + retrieved docs**. Без cache prefill повторяется каждый раз.

**Prefix caching:**
- Хэшировать prefix tokens
- Cache KV blocks для уже виденных prefixes
- Новый request с тем же prefix — переиспользует KV, prefill идёт **только для нового suffix**

**vLLM:**

```bash
python -m vllm.entrypoints.openai.api_server \
  --model meta-llama/Llama-3-70B-Instruct \
  --enable-prefix-caching
```

**Что кэшируется на уровне PagedAttention блоков** — гранулярность 16 tokens. Hash блока — содержимое + хэш предыдущего → детерминированно.

**SGLang RadixAttention:**
- Trie (radix tree) из всех КЭШированных prefixes
- Эффективный matching длиннейшего общего prefix
- Поддерживает branching (parallel sampling)

**Прирост:** для RAG с 4K shared context — **3-10× throughput**, TTFT падает в 5-50×.


## Q23. (!) Anthropic Prompt Caching API — как использовать?

**Anthropic Prompt Caching** (GA 2024-2025) — server-side caching, **5-минутный TTL** (можно расширить до 1h за extra cost).

```python
response = client.messages.create(
    model="claude-sonnet-4-5",
    max_tokens=1024,
    system=[
        {
            "type": "text",
            "text": "You are an expert in cardiology...",
            "cache_control": {"type": "ephemeral"}  # cache this!
        },
        {
            "type": "text",
            "text": "Patient history: [50K tokens]",
            "cache_control": {"type": "ephemeral"}  # cache this too
        }
    ],
    messages=[{"role": "user", "content": "Diagnose..."}]
)
```

**Pricing:**
- **Cache write**: +25% к стандартной input цене (one-time)
- **Cache read**: −90% от input цены (огромная экономия)
- **Min cacheable**: 1024 токенов (для Haiku — 2048)

**Break-even:** уже на **2-3 reuse** — экономишь.

**Use cases:**
- Многоступенчатые агенты (large system prompt)
- RAG с большим контекстом docs
- Long conversations (cache историю)
- Document Q&A (cache документ один раз)

**Кэшируется:** до 4 cache breakpoints. Каждый — отдельная granularity (нельзя ломать порядок).


## Q24. OpenAI Prompt Caching — чем отличается?

**OpenAI Prompt Caching** (Oct 2024) — **автоматический**, без изменений API.

**Особенности:**
- Cache lookup по prefix матчингу (как RadixAttention)
- **−50%** off cached input tokens (vs Anthropic −90%)
- TTL **5-10 минут** (≤ 1 час off-peak)
- Min cacheable: **1024 tokens**
- **Никаких API изменений** — просто структурируй prompt: static prefix → dynamic suffix
- Поддерживается на `gpt-4o`, `gpt-4o-mini`, `o1`-series

**Vs Anthropic:**

| Feature | OpenAI | Anthropic |
|---------|--------|-----------|
| API change | Нет (auto) | Да (`cache_control`) |
| Cache write cost | 0 | +25% |
| Cache read discount | -50% | -90% |
| TTL | 5-10 min | 5 min (или 1h за надбавку) |
| Granularity | Auto matching | Explicit breakpoints (до 4) |
| Min tokens | 1024 | 1024 (2048 Haiku) |

**Vendor lock-in note:** при self-host через vLLM `--enable-prefix-caching` получаешь **100% бесплатно**, без лимитов TTL.


## Q25. (!) vLLM vs TensorRT-LLM vs TGI vs SGLang — сравнение?

| Server | Source | Strength | Weakness | Use case |
|--------|--------|----------|----------|----------|
| **vLLM** | UC Berkeley OSS | PagedAttention, easy setup, broad model support, prefix caching | Slightly slower than TRT-LLM | Default 2025 |
| **TensorRT-LLM** | NVIDIA | Fastest (kernel fusion, custom ops), best FP8 | Complex build, NVIDIA-only, slower iteration | Max perf prod |
| **TGI** | Hugging Face | Production-ready, Rust core, ecosystem | Catching up on features | HF Hub workflow |
| **SGLang** | LMSYS | RadixAttention prefix, structured output, fastest for chat | Newer, fewer models | Agents, complex apps |
| **llama.cpp** | OSS | CPU/edge/Apple Silicon, GGUF | Slower at scale | Local, embedded |
| **MLX** | Apple | Unified memory, M-series | Apple-only | Mac development |

**Benchmark 2025 (Llama-3-70B, H100, throughput):**
- TensorRT-LLM: ~2200 tok/s
- vLLM (latest): ~1900 tok/s
- SGLang: ~2000 tok/s (с prefix cache hits — выше)
- TGI: ~1500 tok/s

**Decision:**
- Не уверен → **vLLM**
- Maxим perf на H100 → **TensorRT-LLM**
- Heavy prefix sharing (agents) → **SGLang**
- HF ecosystem → **TGI**


## Q26. vLLM config tuning — какие флаги важны?

```bash
python -m vllm.entrypoints.openai.api_server \
  --model meta-llama/Llama-3-70B-Instruct \
  --tensor-parallel-size 2 \
  --max-model-len 32768 \
  --gpu-memory-utilization 0.92 \
  --max-num-seqs 256 \
  --max-num-batched-tokens 16384 \
  --enable-prefix-caching \
  --enable-chunked-prefill \
  --quantization fp8 \
  --kv-cache-dtype fp8 \
  --speculative-model meta-llama/Llama-3-8B \
  --num-speculative-tokens 5 \
  --port 8000
```

**Главные knobs:**

| Flag | Что делает | Tuning hint |
|------|------------|-------------|
| `--tensor-parallel-size` | Split весов по N GPU | =GPU count в node |
| `--max-model-len` | Max context window | Ограничивает KV |
| `--gpu-memory-utilization` | Доля VRAM под KV | 0.85-0.95 |
| `--max-num-seqs` | Concurrent requests | 128-512 |
| `--max-num-batched-tokens` | Token budget per batch | 4K-32K |
| `--enable-prefix-caching` | APC on | Всегда on если есть shared prefix |
| `--enable-chunked-prefill` | Mix prefill+decode | On для long context |
| `--quantization` | `awq`/`gptq`/`fp8`/`bitsandbytes` | По модели |
| `--kv-cache-dtype` | `auto`/`fp8` | FP8 на H100 — 2× batch |
| `--speculative-model` | Draft model | Если acceptance >50% |
| `--swap-space` | CPU swap GB | 4-16 для preemption |


## Q27. llama.cpp, Ollama, MLX — для local/edge?

**llama.cpp:**
- C/C++ inference engine от Georgi Gerganov
- CPU (AVX2/AVX-512), CUDA, Metal, Vulkan, ROCm
- GGUF format
- Quantizations 2-8 bit
- Server mode: `./server -m model.gguf --port 8080` (OpenAI-compatible)

**Ollama** (Go wrapper над llama.cpp):
```bash
ollama run llama3:70b           # auto-download, run, REPL
ollama serve                    # HTTP API at :11434
ollama pull deepseek-r1:32b
```
- Auto model management, simple `Modelfile`
- API совместим с OpenAI клиентами
- Default для local dev в 2025

**MLX** (Apple):
- Native Apple Silicon framework (unified memory)
- MLX-LM библиотека для LLM
- На M3 Max 128GB можно запустить Llama-3-70B FP16
- `mlx_lm.server` — HTTP API

**Когда что:**
- Mac local → **MLX** (быстрее llama.cpp на M-series)
- Linux/Win local → **Ollama** (проще)
- Edge/embedded → **llama.cpp** напрямую
- Production GPU → **vLLM**, не эти


## Q28. (!) Tensor Parallelism vs Pipeline Parallelism?

Когда модель не влезает в одну GPU.

**Tensor Parallelism (TP):**
- Split **внутри каждого слоя** по dim
- Attention heads: разные heads на разных GPU
- FFN: split по hidden dim
- ✓ Latency не растёт (parallel compute)
- ✗ Требует **fast interconnect** (NVLink, не PCIe)
- ✗ Communication каждый layer (`all-reduce`)

**Pipeline Parallelism (PP):**
- Split **по слоям**: GPU 0 — layers 0-19, GPU 1 — 20-39, ...
- Передача активаций между GPU
- ✓ Меньше communication
- ✓ Работает по PCIe / RDMA
- ✗ **Pipeline bubble** — pipeline нужно «прогреть»
- ✗ Latency растёт линейно с #stages

**Sequence Parallelism / Context Parallelism:** для очень длинного context (1M+) — split по seq dim.

**vLLM практика:**
```bash
--tensor-parallel-size 4        # 4 GPU в одном node через NVLink
--pipeline-parallel-size 2      # 2 nodes по 4 GPU = 8 GPU total
```

**Rule of thumb:**
- В одной машине, NVLink — TP
- Кроссноды — PP (или TP+PP combo)
- Сверхдлинный context — добавить SP/CP


## Q29. MoE serving — почему Mixtral/DeepSeek сложнее?

**Mixture of Experts:** sparse activation. Layer имеет `M` экспертов, router выбирает `K` (обычно K=2) per token.

**Mixtral 8x7B:** 8 experts × 7B параметров на FFN, активны 2 → effective compute как 12.9B, но веса 47B надо хранить.

**DeepSeek-V3:** 671B total, 37B active. 256 routed experts + 1 shared, top-9 per token.

**Сложности serving:**
1. **Memory** — нужно держать **все** experts (вес большой), а активна только часть
2. **Routing overhead** — каждый token может уйти к другим experts → нет нормального batching по experts
3. **Load imbalance** — popular experts перегружены, others idle
4. **Expert parallelism** — split experts по GPU, но **all-to-all** communication
5. **Кэширование** — KV cache не зависит от router, но routing decisions дополнительные данные

**Tools:**
- vLLM поддерживает Mixtral/DeepSeek с `--expert-parallel-size`
- SGLang оптимизирован для DeepSeek (LMSYS делает совместно)
- DeepSpeed-MoE — академический baseline

**TPS reality:** active params занижают real cost — нужна большая VRAM для весов всех experts.


## Q30. Multi-LoRA serving (LoRAX, Punica)?

**Сценарий:** SaaS с per-tenant fine-tuning. 1000 клиентов = 1000 fine-tuned моделей. Нельзя держать 1000 копий базы.

**Решение:** **base model + LoRA адаптеры**. Адаптер — пара матриц `A (r × d)` и `B (d × r)` где `r=8-64` (rank). Маленький (~10-100 MB), а база 14-140 GB.

**Multi-LoRA inference:**
- Одна base модель в памяти
- Сотни LoRA адаптеров swap on-demand (hot cache)
- В batch разные requests могут использовать разные адаптеры
- Special kernels (`bgmv`, `sgmv`) выполняют per-request LoRA в одном batched matmul

**Tools:**
- **LoRAX** (Predibase) — production serving multi-LoRA
- **Punica** — academic, основа для batched LoRA kernels
- **vLLM** — `--enable-lora --max-loras 16 --max-lora-rank 64`, динамически грузит адаптеры

```bash
python -m vllm.entrypoints.openai.api_server \
  --model meta-llama/Llama-3-8B \
  --enable-lora \
  --max-loras 32 \
  --lora-modules customer_a=/loras/cust_a customer_b=/loras/cust_b
```

В request: `"model": "customer_a"` — vLLM применит правильный адаптер.


## Q31. Disaggregated inference (prefill/decode split)?

**Проблема:** prefill (compute-bound, нужен FLOPS) и decode (memory-bound, нужен bandwidth) имеют **разные оптимальные конфиги**. Совмещение на одной GPU = компромисс.

**Disaggregated serving:**
- **Prefill instances** — GPU с большим compute (H100), оптимизированы под throughput prefill
- **Decode instances** — GPU с большим bandwidth (H200/MI300X), оптимизированы под decode batching
- Между ними — передача KV cache (NVLink, RDMA)

**Системы:**
- **DistServe** (2024) — академический prototype
- **Mooncake** (Moonshot AI) — production для Kimi
- **TensorRT-LLM** — disaggregation поддержка
- **vLLM** — disaggregated experimental в roadmap

**Профит:**
- TTFT и TPOT тюнятся независимо
- 1.5-2× throughput при том же кол-ве GPU
- Лучше SLA goodput

**Минусы:** complexity, KV transfer overhead, требует жирный interconnect.


## Q32. (!) GPU выбор: A100 vs H100 vs H200 vs MI300X?

| GPU | VRAM | HBM BW | FP16 TFLOPs | FP8 TFLOPs | Year | Use |
|-----|------|--------|-------------|------------|------|-----|
| **A100 80GB** | 80 GB HBM2e | 2 TB/s | 312 | – | 2020 | Workhorse, no FP8 |
| **H100 80GB** | 80 GB HBM3 | 3.35 TB/s | 1979 | 3958 | 2022 | Default 2024-25 |
| **H100 NVL 94GB** | 94 GB | 3.9 TB/s | 1979 | 3958 | 2023 | LLM-tuned variant |
| **H200** | 141 GB HBM3e | 4.8 TB/s | 1979 | 3958 | 2024 | Long context, KV |
| **B100** | 192 GB HBM3e | 8 TB/s | ~3500 | 7000 | 2024 | Blackwell |
| **B200** | 192 GB HBM3e | 8 TB/s | ~4500 | 9000 | 2024-25 | Top tier |
| **AMD MI300X** | 192 GB HBM3 | 5.3 TB/s | 1300 | 2600 | 2024 | NVIDIA alt, ROCm |
| **RTX 4090** | 24 GB GDDR6X | 1 TB/s | 165 | 330 | 2022 | Hobby, single 7-13B |

**Выбор:**
- **Llama-3-8B FP16:** A100 / 4090 / даже Mac M-series
- **Llama-3-70B INT4:** 1× A100 80GB or 1× 4090×2
- **Llama-3-70B FP16:** 2× H100 / 1× H200 / 1× MI300X
- **Llama-3-405B FP8:** 8× H100 / 4× B200 / 4× MI300X
- **Long context 1M:** H200/B200 за HBM, либо distributed

**TPU/Inferentia:** дешевле в Google Cloud / AWS соответственно, но другой toolchain (JAX/XLA / Neuron SDK).


## Q33. (!) Cost per 1M tokens — API vs self-host?

**API pricing (Q2 2026 ориентир):**

| Model | Input / 1M | Output / 1M | Cached input |
|-------|-----------|-------------|--------------|
| `claude-sonnet-4-5` | $3 | $15 | $0.30 |
| `claude-haiku-4-5` | $1 | $5 | $0.10 |
| `gpt-4o` | $2.50 | $10 | $1.25 |
| `gpt-4o-mini` | $0.15 | $0.60 | $0.075 |
| `o1` | $15 | $60 | $7.50 |
| `deepseek-v3` (API) | $0.27 | $1.10 | $0.07 |
| `gemini-2.5-pro` | $1.25 | $10 | – |

**Self-host Llama-3-70B на 1× H100 (AWS p5.48xlarge ~$98/hr per GPU):**
- Throughput vLLM FP8: ~2000 tokens/sec aggregate (output)
- Per hour: 2000 × 3600 = 7.2M tokens
- **$98 / 7.2M = $13.6 / 1M output tokens**

С 50% utilization → $27 / 1M. Output dominated.

**Break-even:**
- API дешевле до ~**100M tokens/month**
- Self-host экономнее при **>500M tokens/month** consistent load
- Compliance / privacy / data residency может перевешивать цену

**Optimization stack:**
1. **Prompt caching** → -50% to -90% на input (huge для RAG)
2. **Batch API** → -50% на не-realtime (Anthropic, OpenAI поддерживают)
3. **Model routing** → дешёвая модель для 80% запросов
4. **Quantization self-host** → INT4 fits 70B на одной 80GB GPU


## Q34. Batch API (OpenAI/Anthropic) — когда использовать?

**Batch API:**
- Submit job (JSONL файл с requests), get результат в течение **24 hours**
- **−50% от обычной цены** input/output
- Не для realtime — для бэкграунда

**Когда подходит:**
- Embedding большого корпуса
- Data labeling / categorization
- Synthetic data generation
- Periodic report generation
- Document processing (overnight)

**Когда НЕ подходит:**
- User-facing realtime chat
- Жёсткий SLA по deadline
- Iterative experiments (24h задержка)

**OpenAI Batch:**
```python
batch = client.batches.create(
    input_file_id=file.id,
    endpoint="/v1/chat/completions",
    completion_window="24h"
)
```

**Anthropic Message Batches:**
```python
batch = client.messages.batches.create(requests=[...])
```

**Self-host эквивалент:** свой queue + vLLM offline mode (`LLM.generate(...)` на массив) — без 50% discount, но нет API лимитов.


## Q35. (!) Model routing — cheap для simple, expensive для hard?

**Идея:** не каждому запросу нужен GPT-4o. ~70% типичного трафика — простые задачи (классификация, простой Q&A, RAG-резюме), которые решит `gpt-4o-mini` или `Haiku` в 10× дешевле.

**Routing strategies:**

1. **Rule-based** — по domain/intent. «summarize → cheap», «code review → strong»
2. **Classifier-based** — маленькая модель классифицирует complexity:
   - `RouteLLM` (LMSYS) — open source router
   - `Martian` (commercial)
3. **Cascade** — попробовать cheap, escalate если low confidence
4. **Multi-armed bandit** — A/B обучение online

**Example RouteLLM:**
```python
from routellm.controller import Controller

router = Controller(
    routers=["mf"],  # matrix factorization router
    strong_model="gpt-4o",
    weak_model="gpt-4o-mini"
)
response = router.completion(messages=[...], model="router-mf-0.116")
```

**Реальные результаты:** 50-80% запросов уходят в weak модель, quality drop <5% при правильной калибровке threshold. **Savings: 3-7×**.


## Q36. Streaming SSE — снижает ли реальную latency?

**Streaming** (Server-Sent Events): сервер шлёт каждый токен сразу как сгенерирован, клиент отображает progressively.

**Не снижает** end-to-end (полный ответ всё ещё `TTFT + N × TPOT`), но **резко снижает perceived latency**:
- Без streaming: пользователь видит **полный ответ через 10s** для 500-token answer
- Со streaming: первый токен через **500ms**, остальные текут → ощущение мгновенности

**Реализация:**

```python
# Server (FastAPI + vLLM)
@app.post("/chat")
async def chat(request: Request):
    return StreamingResponse(
        generate_stream(request),
        media_type="text/event-stream"
    )

# OpenAI client
stream = client.chat.completions.create(
    model="gpt-4o",
    messages=[...],
    stream=True
)
for chunk in stream:
    print(chunk.choices[0].delta.content or "", end="")
```

**Format SSE:**
```
data: {"choices": [{"delta": {"content": "Hello"}}]}

data: {"choices": [{"delta": {"content": " world"}}]}

data: [DONE]
```

**Gotchas:**
- HTTP/2 буферизация — отключить `X-Accel-Buffering: no` на nginx
- `gzip` middleware ломает SSE — exclude
- Reverse proxy (CloudFront, Cloudflare) — нужен flush
- Считай **TTFT отдельно** от total — это главная UX-метрика для chat

---

## See also

- [Model Serving](model-serving-interview.md) — общий обзор serving stack
- [LLM Basics](llm-basics-interview.md) — что такое attention, transformer
- [MLOps](mlops-interview.md) — operations контекст
- [Fine-tuning LLM](fine-tuning-llm-interview.md) — откуда берутся кастомные веса
- [Reasoning Models](reasoning-models-interview.md) — o1/R1 specific inference
- [LLM Integration Patterns](llm-integration-patterns-interview.md) — caching на app уровне
- [RAG](rag-interview.md) — где prefix caching критичен
- [AI Agents](ai-agents-interview.md) — multi-turn = shared prefix
- [Network Performance](../performance/network-performance-interview.md) — SSE, HTTP/2, gRPC
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — auto-scaling, queue
- [Latency Numbers](../architecture/latency-numbers-interview.md) — GPU HBM vs DRAM vs SSD
- [Memory Management](../performance/memory-management-interview.md) — KV cache как memory pool
- [Kubernetes](../devops/kubernetes-interview.md) — GPU node pools, resource limits
