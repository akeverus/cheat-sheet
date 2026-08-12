#!/usr/bin/env python3
"""
linkage_scan.py — детерминированные сигналы LINKAGE для MCQ-сидеров (ROUND-10).

Контракт, который проверяется:
  пользователь видит "## Q<N> <заголовок>" из markdown + четыре варианта из JSON.
  Поле question_text в UI НЕ показывается (McqJsonLoader его не пишет в БД),
  поэтому единственный настоящий stem — visible-заголовок Q<N>.

Скрипт НЕ заменяет semantic-ревью: он даёт risk-сигналы, а не приёмку (см. §46 методологии).
ERROR — структурные факты, которые проверяются точно.
WARN  — лексические подозрения, которые обязаны уйти на LLM/manual linkage review.

Использование:
    python3 scripts/mcq-linkage-scan.py <seed.json> [...]
    python3 scripts/mcq-linkage-scan.py --all
    python3 scripts/mcq-linkage-scan.py --all --json out.json
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SEED_DIR = ROOT / "modules/quiz-app/src/main/resources/seed/mcq"
MD_DIR = ROOT / "cheatsheets/interview"

# Порог, ниже которого lexical coverage обязана уйти на ручной linkage review (§47).
LOW_COVERAGE = 0.20
# Порог containment заголовка: ниже — question_text ушёл от visible title (§45).
LOW_TITLE_OVERLAP = 0.34
# Насколько соседний Q должен покрывать опции лучше своего, чтобы это считалось утечкой.
LEAK_MARGIN = 0.12

STOP = set("""
и в во не что он на я с со как а то все она так его но да ты к у же вы за бы по только ее мне
было вот от меня еще нет о из ему теперь когда даже ну вдруг ли если уже или ни быть был него до
вас нибудь опять уж вам ведь там потом себя ничего ей может они тут где есть надо ней для мы тебя
их чем была сам чтоб без будто чего раз тоже себе под будет ж тогда кто этот того потому этого
какой совсем ним здесь этом один почти мой тем чтобы нее сейчас были куда зачем всех никогда
можно при наконец два об другой хоть после над больше тот через эти нас про всего них какая много
разве три эту моя впрочем хорошо свою этой перед иногда лучше чуть том нельзя такой им более
всегда конечно всю между это как который которая которые нужно нужен нужна также при этом или
the and for with that this from into your you are not but all any can has have will its it is of
to in on as by be or an at we us if no do does done use used using new old
""".split())

TOKEN_RE = re.compile(r"[A-Za-z_][A-Za-z0-9_]*|[а-яёА-ЯЁ]+", re.UNICODE)
# «Технический» токен: идентификатор, который пользователь обязан был встретить в материале.
TECH_RE = re.compile(
    r"`[^`\n]+`"                       # спаны в обратных кавычках
    r"|@[A-Za-z][A-Za-z0-9_]*"         # аннотации
    r"|\b[A-Za-z][a-z0-9]+(?:[A-Z][A-Za-z0-9]*)+\b"  # CamelCase
    r"|\b[a-zA-Z_][a-zA-Z0-9_]*\(\)"   # вызовы method()
)
# Токены, которые считаем общеязыковыми и не требуем находить в материале дословно.
TECH_ALLOWLIST = {
    "http", "https", "json", "xml", "url", "uri", "api", "rest", "id", "ttl", "tcp",
    "java", "spring", "get", "post", "put", "delete", "patch", "head", "options",
}


def stem(tok: str) -> str:
    """Грубый стеммер: русская морфология срезается усечением до 5 символов."""
    t = tok.lower()
    return t[:5] if re.match(r"^[а-яё]", t) else t


def content_tokens(text: str) -> set[str]:
    out = set()
    for m in TOKEN_RE.finditer(text or ""):
        w = m.group(0).lower()
        if len(w) < 4 or w in STOP:
            continue
        out.add(stem(w))
    return out


def present_in_source(token: str, source: str) -> bool:
    """Встречался ли идентификатор пользователю в материале Q — в любом оформлении.

    Проверяем присутствие по границам слова, а не совпадение размеченных спанов: в шпаргалке
    один и тот же идентификатор бывает и в обратных кавычках, и голым в ячейке таблицы
    (`spring-web`), и внутри кода (`.body(User.class)`). Для вопроса «видел ли это пользователь»
    важно присутствие, а не разметка.
    """
    return re.search(r"(?<![A-Za-z0-9_])" + re.escape(token) + r"(?![A-Za-z0-9_])",
                     source, re.IGNORECASE) is not None


def tech_tokens(text: str) -> set[str]:
    """Технические идентификаторы, нормализованные для сравнения."""
    out = set()
    for m in TECH_RE.finditer(text or ""):
        raw = m.group(0).strip("`").strip()
        for part in re.split(r"[^A-Za-z0-9_@]+", raw):
            part = part.strip()
            if not part or len(part) < 3:
                continue
            if part.lower() in TECH_ALLOWLIST:
                continue
            if re.fullmatch(r"[а-яёА-ЯЁ]+", part):
                continue
            out.add(part)
    return out


def parse_markdown(md_path: Path) -> dict[int, dict]:
    """Разбирает шпаргалку на секции ## Q<N>. Возвращает {N: {title, body}}."""
    text = md_path.read_text(encoding="utf-8")
    lines = text.split("\n")
    heads = []
    for i, line in enumerate(lines):
        m = re.match(r"^##\s+Q(\d+)\.\s*(.*)$", line)
        if m:
            heads.append((i, int(m.group(1)), m.group(2).strip()))
    stop_at = len(lines)
    for i, line in enumerate(lines):
        if re.match(r"^##\s+See also", line) and heads and i > heads[0][0]:
            stop_at = i
            break
    out = {}
    for idx, (line_no, num, title) in enumerate(heads):
        end = heads[idx + 1][0] if idx + 1 < len(heads) else stop_at
        # маркер «важный вопрос» в корпусе стоит и перед заголовком, и после него —
        # это декорация, а не часть вопроса, поэтому снимаем в обеих позициях
        title = re.sub(r"^\(!\)\s*", "", title).strip()
        title = re.sub(r"\s*\(!\)$", "", title).strip()
        out[num] = {
            "title": title,
            "body": "\n".join(lines[line_no + 1:end]),
            "line": line_no + 1,
        }
    return out


def md_for_seed(seed_path: Path) -> Path:
    rel = seed_path.resolve().relative_to(SEED_DIR)
    return MD_DIR / rel.with_suffix(".md")


def coverage(option_text: str, body_tokens: set[str]) -> float:
    toks = content_tokens(option_text)
    if not toks:
        return 1.0
    return len(toks & body_tokens) / len(toks)


def scan_file(seed_path: Path) -> dict:
    seed_path = seed_path.resolve()
    seed = json.loads(seed_path.read_text(encoding="utf-8"))
    md_path = md_for_seed(seed_path)
    res = {
        "seed": str(seed_path.relative_to(ROOT)),
        "markdown": str(md_path.relative_to(ROOT)) if md_path.exists() else None,
        "errors": [], "warnings": [], "blocks": [],
    }
    if not md_path.exists():
        res["errors"].append(f"ORPHAN-FILE: нет шпаргалки {md_path}")
        return res

    qs = parse_markdown(md_path)
    md_numbers = set(qs)
    seed_numbers = {q["q_number"] for q in seed.get("questions", [])}

    for missing in sorted(seed_numbers - md_numbers):
        res["errors"].append(f"ORPHAN-Q: q_number={missing} есть в JSON, но нет ## Q{missing} в шпаргалке")
    for missing in sorted(md_numbers - seed_numbers):
        res["warnings"].append(f"MISSING-Q: ## Q{missing} в шпаргалке не покрыт MCQ")

    # Предвычисляем токены тела каждого Q для проверки neighbor leakage.
    body_tok = {n: content_tokens(q["body"] + " " + q["title"]) for n, q in qs.items()}
    body_raw = {n: q["body"] + " " + q["title"] for n, q in qs.items()}

    for question in seed.get("questions", []):
        n = question["q_number"]
        if n not in qs:
            continue
        title = qs[n]["title"]
        own = body_tok[n]
        own_raw = body_raw[n]
        title_toks = content_tokens(title)

        for block in question.get("blocks", []):
            bi = block.get("block_idx", 0)
            tag = f"q{n}b{bi}"
            qt = block.get("question_text", "")
            qt_toks = content_tokens(qt)
            overlap = (len(qt_toks & title_toks) / len(title_toks)) if title_toks else 1.0

            opts = block.get("options", [])
            covs = [coverage(o.get("text", ""), own) for o in opts]
            all_text = " ".join(o.get("text", "") for o in opts)
            correct_text = " ".join(o.get("text", "") for o in opts if o.get("correct"))
            novel = sorted(t for t in tech_tokens(all_text) if not present_in_source(t, own_raw))
            # Новый идентификатор в ПРАВИЛЬНОМ варианте заставляет пользователя принять факт,
            # которого он не читал, — это ошибка. В дистракторе выдуманное имя обычно и есть
            # иллюстрация ошибки (`/userOrders/456`, `1745073000`), а опровержение приходит из
            # тезиса источника, а не из узнавания этого имени, — поэтому только предупреждение.
            novel_in_correct = sorted(t for t in novel if present_in_source(t, correct_text))
            novel_in_wrong_only = [t for t in novel if t not in novel_in_correct]

            # neighbor leakage: покрывает ли какой-то другой Q опции заметно лучше своего
            leak = None
            if own:
                own_cov = coverage(all_text, own)
                best_n, best_c = None, own_cov
                for other, toks in body_tok.items():
                    if other == n:
                        continue
                    c = coverage(all_text, toks)
                    if c > best_c + LEAK_MARGIN:
                        best_n, best_c = other, c
                if best_n is not None:
                    leak = {"neighbor": best_n, "own": round(own_cov, 3), "neighbor_cov": round(best_c, 3)}

            entry = {
                "tag": tag, "q_number": n, "block_idx": bi,
                "visible_title": title,
                "question_text": qt,
                "title_overlap": round(overlap, 3),
                "opt_coverage_min": round(min(covs), 3) if covs else 0.0,
                "opt_coverage_avg": round(sum(covs) / len(covs), 3) if covs else 0.0,
                "novel_tech_terms": novel,
                "novel_in_correct": novel_in_correct,
                "neighbor_leak": leak,
                "manual_review": False,
            }

            # Точная сверка. ROUND-10 требует, чтобы question_text был нормализованной копией
            # видимого заголовка. Порога по пересечению слов для этого мало: правка заголовка
            # источника вида «RFC 7807» → «RFC 9457» меняет один токен, overlap остаётся высоким,
            # и рассинхрон проходит незамеченным — а блок при этом начинает спорить с прочитанным.
            if qt.strip() != title.strip():
                entry["manual_review"] = True
                res["warnings"].append(
                    f"STEM-MISMATCH {tag}: question_text не совпадает с видимым заголовком дословно.\n"
                    f"    заголовок:     {title}\n"
                    f"    question_text: {qt}")

            if overlap < LOW_TITLE_OVERLAP:
                entry["manual_review"] = True
                res["errors"].append(
                    f"STEM-DRIFT {tag}: question_text ушёл от visible-заголовка "
                    f"(overlap={overlap:.2f} < {LOW_TITLE_OVERLAP}). "
                    f"visible={title!r} vs question_text={qt[:80]!r}")
            if novel_in_correct:
                entry["manual_review"] = True
                res["errors"].append(
                    f"NOVEL-TERM {tag}: в ПРАВИЛЬНОМ варианте есть идентификаторы, которых нет "
                    f"в материале Q{n}: " + ", ".join(novel_in_correct[:8])
                    + ("…" if len(novel_in_correct) > 8 else ""))
            if novel_in_wrong_only:
                entry["manual_review"] = True
                res["warnings"].append(
                    f"NOVEL-TERM-WRONG {tag}: в дистракторах есть идентификаторы вне материала Q{n}: "
                    + ", ".join(novel_in_wrong_only[:8]) + ("…" if len(novel_in_wrong_only) > 8 else "")
                    + " — проверить, что дистрактор опровергается тезисом источника, а не узнаванием имени")
            if leak:
                entry["manual_review"] = True
                res["errors"].append(
                    f"NEIGHBOR-LEAK {tag}: варианты лексически ближе к Q{leak['neighbor']} "
                    f"({leak['neighbor_cov']}), чем к своему Q{n} ({leak['own']})")
            if covs and min(covs) < LOW_COVERAGE:
                entry["manual_review"] = True
                low = [o.get("label") for o, c in zip(opts, covs) if c < LOW_COVERAGE]
                res["warnings"].append(
                    f"LOW-COVERAGE {tag}: варианты {','.join(map(str, low))} почти не пересекаются "
                    f"с материалом Q{n} (min={min(covs):.3f}) — обязателен ручной linkage review")

            res["blocks"].append(entry)
    return res


def main() -> int:
    ap = argparse.ArgumentParser(description="Детерминированные сигналы LINKAGE для MCQ-сидеров")
    ap.add_argument("files", nargs="*", help="пути к seed JSON")
    ap.add_argument("--all", action="store_true", help="просканировать весь корпус")
    ap.add_argument("--json", metavar="OUT", help="записать полный отчёт в JSON")
    ap.add_argument("--quiet", action="store_true", help="только сводка")
    args = ap.parse_args()

    targets = [Path(f) for f in args.files]
    if args.all:
        targets = sorted(SEED_DIR.rglob("*.json"))
    if not targets:
        ap.error("укажи файлы или --all")

    reports, n_err, n_warn, n_blocks, n_manual = [], 0, 0, 0, 0
    for t in targets:
        r = scan_file(t)
        reports.append(r)
        n_err += len(r["errors"])
        n_warn += len(r["warnings"])
        n_blocks += len(r["blocks"])
        n_manual += sum(1 for b in r["blocks"] if b["manual_review"])
        if not args.quiet:
            print(f"\n=== {r['seed']}")
            for e in r["errors"]:
                print(f"  ERROR  {e}")
            for w in r["warnings"]:
                print(f"  WARN   {w}")
            if not r["errors"] and not r["warnings"]:
                print("  OK — детерминированных сигналов нет")

    print(f"\n--- ИТОГ: файлов={len(reports)} блоков={n_blocks} "
          f"errors={n_err} warnings={n_warn} manual_review={n_manual}")
    if args.json:
        Path(args.json).write_text(
            json.dumps(reports, ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"--- отчёт: {args.json}")
    return 1 if n_err else 0


if __name__ == "__main__":
    sys.exit(main())
