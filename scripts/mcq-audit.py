#!/usr/bin/env python3
"""Воспроизводимый снимок корпуса и применимости исторических заявлений ревью.

stdout — JSON; --output сохраняет снимок. Не переписывает sidecars и контент.
Сравнение Git blob подтверждает версию, но не истинность самого заявления ревью.
"""
from __future__ import annotations

import argparse
from collections import Counter
import hashlib
import importlib.util
import json
from pathlib import Path
import re
import subprocess
import sys

import mcq_validation as mcq
import mcq_evidence as evidence_checks


def sha256(path):
    return hashlib.sha256(path.read_bytes()).hexdigest() if path.is_file() else None


def block_hashes(data, markdown):
    """Зависимости блока: JSON блока, его Q в теории и общий пролог темы."""
    sections = mcq.question_sections(markdown)
    preamble = "\n".join(markdown.splitlines()[:sections[0]["line"] - 1]) if sections else markdown
    theory = {q["q_number"]: {"title": q["title"], "body": q["body"], "preamble": preamble}
              for q in sections}
    def digest(value):
        return hashlib.sha256(json.dumps(value, sort_keys=True, ensure_ascii=False,
                                         separators=(",", ":")).encode()).hexdigest()
    return {f"Q{q['q_number']}:B{b['block_idx']}": {
        "mcq": digest({"topic_slug": data["topic_slug"], "q_number": q["q_number"], "block": b}),
        "theory": digest(theory.get(q["q_number"]))}
        for q in data.get("questions", []) for b in q.get("blocks", [])}


def current_coverage(review, hashes, pair_hashes, theory_path):
    if review["paths"]["theory"] != theory_path:
        return set()
    return {f"Q{b['q_number']}:B{b['block_idx']}" for b in review["coverage"]
            if review["sha256"] == pair_hashes or (
                b.get("sha256") is not None and
                b["sha256"] == hashes.get(f"Q{b['q_number']}:B{b['block_idx']}"))}


def tool_module(filename):
    spec = importlib.util.spec_from_file_location(filename.replace("-", "_"), mcq.ROOT / "scripts" / filename)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def git_blob(path, algorithm):
    if not path.is_file():
        return None
    data = path.read_bytes()
    return hashlib.new(algorithm, f"blob {len(data)}\0".encode() + data).hexdigest()


def declared_commits(value):
    """Только явно названные поля; не угадываем SHA из рассказов о ремонте."""
    found = []
    if isinstance(value, dict):
        for key, item in value.items():
            if key in ("commit", "reviewed_commit") and isinstance(item, str):
                found.append(item)
            elif key == "commits" and isinstance(item, list):
                found.extend(x for x in item if isinstance(x, str))
            elif key in ("audited", "verification"):
                found.extend(declared_commits(item))
    return list(dict.fromkeys(found))


def form_review_queue(rows):
    """Очередь отдельного ревью формы: содержательная вычитка её не сокращает.

    Автосигналы задают порядок. Их отсутствие не подтверждает SFPL/ANSC.
    Подтверждённые формальные ревью будут отдельным источником evidence;
    текущие старые/общие статусы намеренно не интерпретируются как их замена.
    """
    queue = []
    for row in rows:
        if row["claimed_status"] != "AUDITED":
            continue
        signals = row.get("form_precheck", {}).get("signals", {})
        blocks = [b for b in row["all_block_ids"] if any(
            b not in row.get("checks", {}).get(c, {}).get("passed_blocks", []) for c in ("sfpl", "ansc"))]
        if not blocks:
            continue
        flagged = [b for b in blocks if b in signals]
        queue.append({"id": row["id"], "status": evidence_checks.aggregate(row["checks"][c]["status"] for c in ("sfpl", "ansc")) if "checks" in row else "UNVERIFIED",
                      "blocks": flagged + [b for b in blocks if b not in signals],
                      "blocks_with_signals": flagged,
                      "reason": "Отдельно проверить SFPL и ANSC всех A/B/C/D; pre-check PASS не закрывает ревью"})
    return sorted(queue, key=lambda r: (-len(r["blocks_with_signals"]), r["id"]))


def prose_fragments(text):
    """Абзацы прозы: не меряем код и не склеиваем таблицу/список в длинную фразу."""
    pending, fence = [], None
    for raw in text.splitlines():
        line = raw.strip()
        marker = re.match(r"^(`{3,}|~{3,})", line)
        if marker:
            if pending:
                yield " ".join(pending)
                pending = []
            if fence is None:
                fence = marker[1][0]
            elif marker[1][0] == fence:
                fence = None
            continue
        if fence is not None:
            continue
        separate = not line or line.startswith(("|", "#", ">")) or re.match(r"^(?:[-*+] |\d+[.)] )", line)
        if separate:
            if pending:
                yield " ".join(pending)
                pending = []
            if line and not re.fullmatch(r"[|:\-\s]+", line):
                yield line
        else:
            pending.append(line)
    if pending:
        yield " ".join(pending)


def readability_precheck(data, markdown, scanner):
    """Повторно используем language-эвристики; отдельная редакторская приёмка обязательна."""
    questions = {q["q_number"]: q for q in mcq.question_sections(markdown)}
    signals = {}
    checked_fields = 0
    for q in data["questions"]:
        source = questions[q["q_number"]]
        fields = [("visible_question", source["title"], "visible_question"),
                  ("theory", source["body"], "theory")]
        shared = []
        for where, value, key in fields:
            checked_fields += 1
            for paragraph, fragment in enumerate(prose_fragments(value), 1):
                shared.extend(scanner(fragment, f"{where}.paragraph{paragraph}", key=key))
        for block in q["blocks"]:
            found = list(shared)
            for option in block["options"]:
                fields = [(f"{option['label']}.text", option["text"], "text")]
                fields += [(f"{option['label']}.sections.{key}", value, key)
                           for key, value in option["sections"].items() if isinstance(value, str)]
                for where, value, key in fields:
                    checked_fields += 1
                    for paragraph, fragment in enumerate(prose_fragments(value), 1):
                        found.extend(scanner(fragment, f"{where}.paragraph{paragraph}", key=key))
            if found:
                signals[f"Q{q['q_number']}:B{block['block_idx']}"] = found
    return {"status": "SIGNALS" if signals else "NO_SIGNALS", "checked_fields": checked_fields,
            "signals": signals, "blocks_with_signals": len(signals),
            "scope": "visible Markdown title, Q body, all option texts and prose in every section; fenced code excluded",
            "acceptance": "UNVERIFIED: editorial Russian review required even without signals"}


def file_checks(row, records=(), hashes=None, rules=None):
    """Единый источник матрицы: новые evidence и текущая структурная проверка."""
    checks = evidence_checks.evaluate(row, records, hashes or {}, rules or {}, mcq.ROOT)
    blocks = row["all_block_ids"]
    checks["schema_structure"] = {
        "status": row["structure"], "current_blocks": len(blocks) if row["structure"] == "PASS" else 0,
        "required_blocks": len(blocks), "passed_blocks": blocks if row["structure"] == "PASS" else [],
        "remaining_blocks": [] if row["structure"] == "PASS" else blocks,
        "evidence": ["scripts/mcq_validation.py"], "sha256": row.get("sha256"),
        "validator_sha256": sha256(Path(mcq.__file__)), "errors": row.get("errors", [])}
    return checks


def render_file_checklist(snapshot):
    """Читаемое представление того же снимка, не второй редактируемый реестр."""
    lines = ["# Проверки по каждому MCQ-файлу", "",
             "Сгенерировано `scripts/mcq-audit.py`; вручную статусы не менять.",
             "План: [PLAN_INTERVIEW](../../PLAN_INTERVIEW.md). Данные и точные Q/B/поля: [audit-state.json](audit-state.json).", "",
             "Все проверки обязательны для каждой пары JSON + Markdown. Сигналы автоматики — кандидаты на разбор.",
             "U = UNVERIFIED; НП = NOT_RUN. «Прочитано» показывает покрытие, не приёмку содержания.",
             "Авто: JSON = структурный gate; L = linkage errors/warnings; F = блоки с сигналами формы; RU = блоки с языковыми сигналами.",
             "Факты, обучающая связь с источником, согласованность sections, SFPL, ANSC и RU принимаются отдельно.",
             "Исторический AUDITED не даёт галочек. TODO темы обрабатываются после обновлённого множества.", ""]
    for claimed, title in (("AUDITED", "Исторически обновлённые темы"), ("TODO", "Оставшиеся TODO темы"), (None, "Прочие статусы")):
        rows = [r for r in snapshot["files"] if (r["claimed_status"] == claimed if claimed else r["claimed_status"] not in {"AUDITED", "TODO"})]
        if not rows:
            continue
        lines.extend([f"## {title} ({len(rows)})", "",
            "| Файл | Блоки | Автопроверки | Прочитано сейчас | Содержательная приёмка | Форма и язык | Независимость / итог | Следующее действие |",
            "|---|---:|---|---|---|---|---|---|"])
        for row in rows:
            checks = row["checks"]
            status = lambda name: {"UNVERIFIED": "U", "NOT_RUN": "НП"}.get(checks[name]["status"], checks[name]["status"])
            linkage = row["linkage"]
            link = f"{len(linkage['errors'])}/{linkage['warnings']}" if "errors" in linkage else "НП"
            read = f"CONTENT {status('content_review')}: {checks['content_review'].get('current_blocks', 0)}/{row['blocks']}; исторически прочитано {len(row['reviewed_blocks_current'])}"
            form = len(row["form_precheck"]["signals"]) if "signals" in row["form_precheck"] else "НП"
            ru = row["readability_precheck"].get("blocks_with_signals", "НП")
            name = f"[{row['mcq']}](../../{row['mcq']})<br>[{row.get('theory', 'не указана')}](../../{row.get('theory', '')})"
            action = "Сначала структура" if row["structure"] != "PASS" else ("RU-сигналы → RU + SFPL/ANSC" if ru else "RU + SFPL/ANSC")
            if row["claimed_status"] == "TODO":
                action = "После обновлённых тем: полный цикл"
            auto = f"JSON {status('schema_structure')}; L {link}; F {form}; RU {ru}"
            content = f"Факты {status('factual_correctness')}; связь {status('source_linkage')}; sections {status('sections_consistency')}"
            language = f"SFPL {status('sfpl')}; ANSC {status('ansc')}; **RU {status('russian_readability')}**"
            independence = f"Ревью {status('independent_review')}; слепое {status('blind_review')}; итог {status('final_acceptance')}"
            coverage = "; ".join(f"{c}: {v.get('current_blocks', 0)}/{row['blocks']}" for c, v in checks.items() if c != "schema_structure")
            refs = sorted({ref for v in checks.values() for ref in v.get("evidence", [])})
            proof = ", ".join(f"[evidence {i+1}](../../{ref})" for i, ref in enumerate(refs)) or "нет новых evidence"
            version = f"JSON {row.get('sha256', {}).get('mcq', '')[:12]}; MD {row.get('sha256', {}).get('theory', '')[:12]}"
            name += f"<br>{version}; Q={row.get('questions', '?')}<br>{proof}"
            action = row.get("next_actions", [action])[0] if row.get("next_actions") else action
            values = [name, row["blocks"], auto, read, content, language, independence + "<br>" + coverage]
            lines.append("| " + " | ".join(map(str, [*values, action])) + " |")
        lines.append("")
    return "\n".join(lines)


def inventory(topic=None):
    validator = mcq.schema_validator()
    parity = tool_module("mcq-answer-parity-gate.py")
    linkage = tool_module("mcq-linkage-scan.py")
    skel = tool_module("mcq-skel-gate.py")
    anti_tell = tool_module("mcq-anti-tell-scan.py")
    readability = tool_module("audit-mcq-parity.py")
    root = mcq.ROOT
    manifest_path = root / "docs/mcq-quality/linkage-reset-manifest.json"
    manifest = mcq.read_json(manifest_path)
    claims = {entry["mcq_file"]: entry for entry in manifest["files"]}
    if len(claims) != len(manifest["files"]):
        raise ValueError("duplicate mcq_file in historical manifest")
    algorithm = subprocess.check_output(["git", "rev-parse", "--show-object-format"], cwd=root, text=True).strip()
    rows, requests, owners = [], [], []
    evidence_path = root / "docs/mcq-quality/audit-evidence.json"
    evidence = mcq.read_json(evidence_path) if evidence_path.exists() else {"reviews": []}
    records = evidence_checks.load_records(root / "docs/mcq-quality/reviews/checks")
    for record in records:
        record["_evidence_path"] = Path(record["_evidence_path"]).relative_to(root).as_posix()
        if not (root / record["paths"]["mcq"]).is_file():
            raise ValueError(f"evidence seed missing: {record['review_id']}")
    rules = {name: sha256(root / name) for name in evidence_checks.RULE_PATHS}
    reviews = {}
    for review in evidence["reviews"]:
        reviews.setdefault(review["paths"]["mcq"], []).append(review)
    for path in sorted(mcq.SEED.rglob("*.json")):
        relative = path.relative_to(root).as_posix()
        if topic and topic not in (relative, path.relative_to(mcq.SEED).as_posix()):
            continue
        theory = mcq.canonical_theory(path)
        theory_relative = theory.relative_to(root).as_posix()
        claim = claims.get(relative, {})
        sidecar_path = root / "docs/mcq-quality/reviews" / path.name
        sidecar = mcq.read_json(sidecar_path) if sidecar_path.exists() else {}
        freshness_path = root / "docs/mcq-quality/fact-freshness" / path.name
        structural = mcq.validate_seed(path, validator, theory)
        data = mcq.read_json(path) if structural["questions"] else {}
        block_ids = [f"Q{q['q_number']}:B{b['block_idx']}"
                     for q in data.get("questions", []) for b in q.get("blocks", [])]
        hashes = block_hashes(data, theory.read_text(encoding="utf-8")) if theory.is_file() and data else {}
        row = {
            "id": path.relative_to(mcq.SEED).as_posix(),
            "mcq": relative, "theory": theory_relative,
            "sha256": {"mcq": sha256(path), "theory": sha256(theory)},
            "questions": structural["questions"], "blocks": structural["blocks"],
            "options": structural["options"], "structure": structural["status"],
            "errors": structural["errors"],
            "claimed_status": claim.get("status", "UNTRACKED"),
            "work_scope": "CURRENT_UPDATED_SET" if claim.get("status") == "AUDITED" else "TODO_AFTER_UPDATED_SET",
            "sidecar_status": sidecar.get("status", "UNSPECIFIED"),
            "sidecar": sidecar_path.relative_to(root).as_posix() if sidecar else None,
            "freshness_sidecar_present": freshness_path.is_file(),
            "content_binding": "UNVERIFIED", "matching_declared_commits": [],
            "semantic_acceptance": "UNVERIFIED",
            "fact_freshness": "UNVERIFIED: presence of a sidecar is not a fact check",
            "blind_review": "NOT_RUN_IN_THIS_AUDIT",
            "open_issues": [item for item in evidence.get("open_issues", []) if item["mcq"] == relative and item.get("status") not in {"RESOLVED", "CLOSED"}],
            "runtime_findings": [item for item in evidence.get("runtime_findings", []) if item["mcq"] == relative],
            "block_hashes": hashes,
            "reviewed_blocks_initial": [], "reviewed_blocks_current": [],
            "all_block_ids": block_ids,
            "form_acceptance": "UNVERIFIED: separate SFPL/ANSC evidence required",
            "review_records": [], "pending_content_review": block_ids,
            "next_action": "Проверить все A/B/C/D и sections относительно видимого вопроса и источников",
        }
        if structural["status"] == "PASS":
            p = parity.run_file(str(path))
            linked = linkage.scan_file(path)
            row["parity"] = {"status": "PASS" if p["pass"] else "FAIL",
                             "failed_rules": [name for name, check in p["checks"].items() if not check["pass"]]}
            row["linkage"] = {"errors": linked["errors"], "warnings": len(linked["warnings"]),
                              "manual_review_blocks": [f"Q{b['q_number']}:B{b['block_idx']}"
                                                       for b in linked["blocks"] if b["manual_review"]]}
            skeleton = skel.analyze_file(path)
            tells = {b["tag"]: b["tells"] for b in anti_tell.scan(path)["blocks"]}
            signals = {}
            for b in skeleton["results"]:
                anti = tells[f"q{b['q_number']}b{b['block_idx']}"]
                if b["hard"] or b["warn"] or anti:
                    signals[f"Q{b['q_number']}:B{b['block_idx']}"] = {
                        "skel_hard": b["hard"], "skel_warn": b["warn"], "anti_tell": anti}
            row["form_precheck"] = {"skel": "PASS" if skeleton["pass"] else "FAIL",
                                    "skel_hard_blocks": skeleton["hard_fails"],
                                    "skel_warn_blocks": skeleton["warn_blocks"],
                                    "anti_tell_blocks": sum(bool(v) for v in tells.values()),
                                    "signals": signals}
            row["readability_precheck"] = readability_precheck(
                data, theory.read_text(encoding="utf-8"), readability.readability_scan)
        else:
            row["parity"] = {"status": "NOT_RUN", "reason": "structure did not pass"}
            row["linkage"] = {"status": "NOT_RUN", "reason": "structure did not pass"}
            row["form_precheck"] = {"status": "NOT_RUN", "reason": "structure did not pass"}
            row["readability_precheck"] = {"status": "NOT_RUN", "reason": "structure did not pass"}
        initial, current = set(), set()
        for review in reviews.get(relative, []):
            covered = {f"Q{b['q_number']}:B{b['block_idx']}" for b in review["coverage"]}
            if not covered.issubset(block_ids) or len(covered) != len(review["coverage"]):
                raise ValueError(f"invalid/duplicate review coverage: {review['review_id']}")
            initial.update(covered)
            bound = current_coverage(review, hashes, row["sha256"], theory_relative)
            current.update(bound)
            row["review_records"].append({"id": review["review_id"], "kind": review["kind"],
                                          "binding": "CURRENT" if bound == covered else "STALE",
                                          "stale_blocks": sorted(covered - bound),
                                          "acceptance": review["acceptance"]})
        row["reviewed_blocks_initial"] = [b for b in block_ids if b in initial]
        row["reviewed_blocks_current"] = [b for b in block_ids if b in current]
        row["pending_content_review"] = [b for b in block_ids if b not in current]
        row["checks"] = file_checks(row, records, hashes, rules)
        row["pending_content_review"] = row["checks"]["content_review"]["remaining_blocks"]
        row["semantic_acceptance"] = row["checks"]["final_acceptance"]["status"]
        row["fact_freshness"] = row["checks"]["factual_correctness"]["status"]
        row["blind_review"] = row["checks"]["blind_review"]["status"]
        row["form_acceptance"] = {c: row["checks"][c]["status"] for c in ("sfpl", "ansc")}
        row["next_actions"] = [f"{c}: проверить {len(v['remaining_blocks'])} блоков ({v['status']})"
                               for c, v in row["checks"].items() if v["remaining_blocks"]]
        if not row["next_actions"]:
            row["next_actions"] = ["Файл принят; повторить только после изменения зависимостей"]
        row_index = len(rows)
        rows.append(row)
        for ref in dict.fromkeys(declared_commits(claim) + declared_commits(sidecar)):
            # Git expressions из sidecar не выполняются shell. Только commit-ish SHA.
            if not (7 <= len(ref) <= 64 and all(c in "0123456789abcdef" for c in ref.lower())):
                continue
            for surface, p, rel in (("mcq", path, relative), ("theory", theory, theory_relative)):
                requests.append(f"{ref}:{rel}")
                owners.append((row_index, ref, surface, git_blob(p, algorithm)))
    matched, resolved = {}, set()
    if requests:
        result = subprocess.run(["git", "cat-file", "--batch-check=%(objectname) %(objecttype)"],
                                cwd=root, input="\n".join(requests) + "\n", text=True,
                                capture_output=True, check=True)
        lines = result.stdout.splitlines()
        if len(lines) != len(owners):
            raise ValueError("incomplete git object comparison")
        for line, (index, ref, surface, current_blob) in zip(lines, owners):
            parts = line.split()
            if len(parts) == 2 and parts[1] == "blob":
                resolved.add(index)
                if parts[0] == current_blob:
                    matched.setdefault((index, ref), set()).add(surface)
        for (index, ref), surfaces in matched.items():
            if surfaces == {"mcq", "theory"}:
                rows[index]["matching_declared_commits"].append(ref)
    for index, row in enumerate(rows):
        if row["matching_declared_commits"]:
            row["content_binding"] = "COMMIT_MATCH"
        elif index in resolved:
            row["content_binding"] = "STALE"
        if row["structure"] != "PASS":
            row["next_action"] = "Исправить structural errors; затем содержательное ревью"
        elif row["content_binding"] == "STALE":
            row["next_action"] = "Проверить изменения после заявленного ревью и повторить зависимые проверки"
    if not rows:
        raise ValueError("empty MCQ corpus")
    for row in rows:
        if row["sha256"] != {"mcq": sha256(root / row["mcq"]), "theory": sha256(root / row["theory"])}:
            raise ValueError(f"content changed during inventory: {row['id']}; retry after writer finishes")
    names = Counter(Path(row["mcq"]).name for row in rows)
    expected_theory = {row["theory"] for row in rows}
    orphan_theory = []
    for path in sorted(mcq.THEORY.rglob("*.md")):
        relative = path.relative_to(root).as_posix()
        if relative not in expected_theory and mcq.question_sections(path.read_text(encoding="utf-8")):
            orphan_theory.append(relative)
    updated = [row for row in rows if row["claimed_status"] == "AUDITED"]
    return {
        "schema_version": 2,
        "partial_snapshot": bool(topic),
        "head": subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=root, text=True).strip(),
        "standard_sha256": sha256(root / "docs/mcq-quality-standard.md"),
        "validator_sha256": sha256(Path(mcq.__file__)),
        "diagnostic_sha256": {name: sha256(root / "scripts" / name) for name in
                              ("mcq-audit.py", "mcq_evidence.py", "mcq-answer-parity-gate.py", "mcq-linkage-scan.py",
                               "mcq-skel-gate.py", "mcq-anti-tell-scan.py", "audit-mcq-parity.py")},
        "scope": f"working-tree; topic={topic}" if topic else "working-tree; all production MCQ seeds",
        "summary": {
            "accepted_files": sum(r["checks"]["final_acceptance"]["status"] == "PASS" for r in rows),
            "accepted_blocks": sum(r["checks"]["final_acceptance"]["current_blocks"] for r in rows),
            "check_coverage": {c: {"passed_blocks": sum(r["checks"][c]["current_blocks"] for r in rows),
                                   "remaining_blocks": sum(len(r["checks"][c]["remaining_blocks"]) for r in rows),
                                   "file_statuses": dict(Counter(r["checks"][c]["status"] for r in rows))}
                               for c in (*evidence_checks.CHECKS, "schema_structure")},
            "files": len(rows), "questions": sum(r["questions"] for r in rows),
            "paired_markdown_files": sum(r["sha256"]["theory"] is not None for r in rows),
            "review_sidecars": sum(r["sidecar"] is not None for r in rows),
            "freshness_sidecars": sum(r["freshness_sidecar_present"] for r in rows),
            "blocks": sum(r["blocks"] for r in rows), "options": sum(r["options"] for r in rows),
            "claimed_audited_files": len(updated), "claimed_audited_blocks": sum(r["blocks"] for r in updated),
            "structure": dict(Counter(r["structure"] for r in rows)),
            "parity": dict(Counter(r["parity"]["status"] for r in rows)),
            "linkage_errors": sum(len(r["linkage"].get("errors", [])) for r in rows),
            "linkage_warnings": sum(r["linkage"].get("warnings", 0) for r in rows),
            "claimed_audited_content_binding": dict(Counter(r["content_binding"] for r in updated)),
            "reviewed_blocks_initial": sum(len(r["reviewed_blocks_initial"]) for r in updated),
            "reviewed_blocks_current": sum(len(r["reviewed_blocks_current"]) for r in updated),
            "not_reviewed_in_budgeted_pass": sum(r["blocks"] - len(r["reviewed_blocks_initial"]) for r in updated),
            "current_review_pending_including_stale": sum(len(r["pending_content_review"]) for r in updated),
            "form_review_scope_blocks": sum(len(r["all_block_ids"]) for r in updated),
            "form_review_blocks_with_signals": sum(len(r["form_precheck"].get("signals", {})) for r in updated),
            "form_review_status": dict(Counter(r["checks"]["sfpl"]["status"] for r in updated)),
            "readability_blocks_with_signals": sum(r["readability_precheck"].get("blocks_with_signals", 0) for r in rows),
            "updated_readability_blocks_with_signals": sum(r["readability_precheck"].get("blocks_with_signals", 0) for r in updated),
            "semantic_acceptance": dict(Counter(r["checks"]["final_acceptance"]["status"] for r in rows)),
        },
        "basename_collisions": [name for name, count in names.items() if count > 1],
        "theory_without_seed": orphan_theory,
        "manifest_without_seed": sorted(set(claims) - {r["mcq"] for r in rows}),
        "review_queue": [{"id": r["id"], "blocks": r["pending_content_review"],
                          "reason": r["next_action"]} for r in updated if r["pending_content_review"]],
        "check_queue": [{"id": r["id"], "stage": r["work_scope"], "check": c,
                         "status": v["status"], "blocks": v["remaining_blocks"]}
                        for r in sorted(rows, key=lambda r: (r["claimed_status"] != "AUDITED", r["id"]))
                        for c, v in r["checks"].items() if v["remaining_blocks"]],
        "issue_queue": [i for r in rows for i in r["open_issues"]] + [f for r in rows for v in r["checks"].values() for f in v.get("open_findings", [])],
        "form_review_queue": form_review_queue(rows),
        "readability_review_queue": sorted([
            {"id": r["id"], "status": r["checks"]["russian_readability"]["status"], "blocks": sorted(r["checks"]["russian_readability"]["remaining_blocks"], key=lambda b: b not in r["readability_precheck"].get("signals", {})),
             "blocks_with_signals": list(r["readability_precheck"].get("signals", {}))}
            for r in updated if r["checks"]["russian_readability"]["remaining_blocks"]], key=lambda r: (-len(r["blocks_with_signals"]), r["id"])),
        "runtime_findings": evidence.get("runtime_findings", []),
        "files": rows,
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--topic", help="Относительный путь темы; отдельный снимок одной пары")
    parser.add_argument("--checklist-output", type=Path, help="Markdown matrix of required checks for every file")
    args = parser.parse_args()
    try:
        if args.topic and ((args.output and args.output.resolve() == (mcq.ROOT / "docs/mcq-quality/audit-state.json").resolve()) or args.checklist_output):
            raise ValueError("--topic must not replace the global snapshot/checklist")
        result = inventory(args.topic)
    except (ImportError, OSError, ValueError, KeyError, TypeError, subprocess.CalledProcessError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2
    text = json.dumps(result, ensure_ascii=False, indent=2) + "\n"
    # Списки Q/B остаются точными, но не занимают по строке на каждый NOT_RUN.
    text = re.sub(r'\[\n\s+("Q\d+:B\d+"(?:,\n\s+"Q\d+:B\d+")*)\n\s+\]',
                  lambda m: "[" + re.sub(r",\n\s+", ", ", m[1]) + "]", text)
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(text, encoding="utf-8")
        print(json.dumps(result["summary"], ensure_ascii=False))
    else:
        print(text, end="")
    if args.checklist_output:
        args.checklist_output.parent.mkdir(parents=True, exist_ok=True)
        args.checklist_output.write_text(render_file_checklist(result), encoding="utf-8")
    return 1 if any(row["structure"] != "PASS" for row in result["files"]) else 0


if __name__ == "__main__":
    sys.exit(main())
