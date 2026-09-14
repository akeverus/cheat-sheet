"""Строгая проверка evidence для MCQ-аудита.

Модуль намеренно не меняет исторические записи: они валидируются как факт
чтения, но не считаются доказательством нового стандарта.
"""
from __future__ import annotations
import hashlib
import json
from pathlib import Path

CHECKS = ("CONTENT", "FACTS", "LINKAGE", "SECTIONS", "SFPL", "ANSC", "RU", "INDEPENDENT", "BLIND", "FINAL")
STATUSES = {"NOT_RUN", "IN_PROGRESS", "PARTIAL", "PASS", "FAIL", "STALE", "BLOCKED", "UNVERIFIED"}

def digest_rules(value) -> str:
    return hashlib.sha256(json.dumps(value, sort_keys=True, ensure_ascii=False, separators=(",", ":")).encode()).hexdigest()

def validate_record(record, *, expected_mcq=None, expected_theory=None, current_hashes=None, current_blocks=None):
    """Вернуть нормализованный результат или выбросить ValueError."""
    if not isinstance(record, dict): raise ValueError("evidence must be object")
    required = {"review_id", "reviewer", "model", "date", "kind", "paths", "sha256", "block_hashes", "coverage", "file_checks"}
    missing = required - set(record)
    if missing: raise ValueError("missing evidence fields: " + ",".join(sorted(missing)))
    paths = record["paths"]
    if not isinstance(paths, dict) or not isinstance(paths.get("mcq"), str) or not isinstance(paths.get("theory"), str): raise ValueError("invalid paths")
    if expected_mcq and paths["mcq"] != expected_mcq or expected_theory and paths["theory"] != expected_theory: raise ValueError("wrong evidence pair")
    sh = record["sha256"]
    if not isinstance(sh, dict) or not all(isinstance(sh.get(k), str) and len(sh[k]) == 64 for k in ("mcq", "theory")): raise ValueError("invalid file hashes")
    if not isinstance(record["block_hashes"], dict) or not record["block_hashes"]: raise ValueError("missing block_hashes")
    coverage = record["coverage"]
    if not isinstance(coverage, list) or any(not isinstance(x, dict) or not isinstance(x.get("q_number"), int) or not isinstance(x.get("block_idx"), int) for x in coverage): raise ValueError("invalid coverage")
    checks = record["file_checks"]
    if not isinstance(checks, dict) or set(checks) != set(CHECKS): raise ValueError("file_checks must contain exactly " + ",".join(CHECKS))
    for name, value in checks.items():
        if not isinstance(value, dict) or value.get("status") not in STATUSES: raise ValueError(f"invalid status {name}")
    if current_hashes and sh != current_hashes: raise ValueError("stale file hashes")
    if current_blocks is not None and not set(record["block_hashes"]).issubset(set(current_blocks)): raise ValueError("wrong block evidence")
    return record

def load_records(directory, **kwargs):
    """Загрузить только корректные JSON; missing/corrupt/wrong отклоняются."""
    directory = Path(directory)
    if not directory.is_dir(): return []
    result = []
    for path in sorted(directory.glob("*.json")):
        try: result.append(validate_record(json.loads(path.read_text(encoding="utf-8")), **kwargs))
        except (OSError, ValueError, json.JSONDecodeError) as exc: raise ValueError(f"{path}: {exc}") from exc
    return result

def verdict(checks, covered, required, issues=(), runtime_blocker=False):
    if runtime_blocker: return "BLOCKED"
    if not covered: return "NOT_RUN"
    if checks.get("FINAL", {}).get("status") == "PASS" and issues: return "FAIL"
    if set(covered) != set(required): return "PARTIAL"
    return "PASS" if all(checks[k]["status"] == "PASS" for k in CHECKS if k != "FINAL") and not issues else "FAIL"
