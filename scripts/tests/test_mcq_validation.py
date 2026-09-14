"""Контрпримеры приёмки MCQ: данные, охват, ошибки выполнения и форма."""
import copy
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch

SCRIPTS = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPTS))
import mcq_validation as mcq


def load_module(name, file):
    spec = importlib.util.spec_from_file_location(name, SCRIPTS / file)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def seed():
    # Короткие варианты с кодом и отрицанием валидны структурно: метрика не истина.
    texts = ["`x == null`", "`x != null`", "`x > 0`", "`x < 0`"]
    correct_keys = ["explanation", "example", "when_to_apply", "edge_cases", "related"]
    wrong_keys = ["what_actually", "source_of_confusion", "if_it_were_true", "how_it_should_be"]
    return {"topic_slug": "sample", "questions": [{"q_number": 2, "blocks": [{
        "block_idx": 0, "question_text": "Когда x равен null?", "options": [
            {"order": i, "label": label, "text": texts[i], "correct": i == 0,
             "sections": {k: "Содержательное объяснение." for k in
                          (correct_keys if i == 0 else wrong_keys)}}
            for i, label in enumerate("ABCD")]}]}]}


class ValidationTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.validator = mcq.schema_validator()

    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.path = Path(self.temp.name) / "sample.json"
        self.md = self.path.with_suffix(".md")
        self.md.write_text("## Q2. Когда x равен null?\nОтвет.\n")

    def check(self, data, expected):
        self.path.write_text(json.dumps(data, ensure_ascii=False))
        result = mcq.validate_seed(self.path, self.validator, self.md)
        self.assertEqual(result["status"], expected, result["errors"])
        return result

    def test_valid_short_code_options_and_non_contiguous_question_numbers(self):
        self.check(seed(), "PASS")

    def test_visible_title_uses_application_normalization(self):
        self.assertEqual(mcq.question_sections("## Q2. (!) `x` **равен** __null__ (ВАЖНО)\n")[0]["title"],
                         "x равен null")

    def test_skel_pass_does_not_prove_comparable_answer_completeness(self):
        skel = load_module("skel_blind_spot", "mcq-skel-gate.py")
        options = [{"label": label, "text": text} for label, text in zip("ABCD", [
            "Механизм проверяет условие перед запуском и сохраняет результат после завершения.",
            "Механизм всегда срабатывает.", "Механизм никогда не срабатывает.", "Механизм не нужен."])]
        # Одинаковые prose/одно предложение/пунктуация: нужен отдельный SFPL/ANSC review.
        self.assertTrue(skel.block_check(options)["pass"])

    def test_form_queue_retains_content_reviewed_and_no_signal_blocks(self):
        audit = load_module("audit_form_queue", "mcq-audit.py")
        row = {"id": "topic.json", "claimed_status": "AUDITED",
               "all_block_ids": ["Q1:B0", "Q1:B1", "Q2:B0"],
               "reviewed_blocks_current": ["Q1:B0", "Q1:B1", "Q2:B0"],
               "form_precheck": {"signals": {"Q1:B1": {"anti_tell": ["longest"]}}}}
        other = dict(row, id="todo.json", claimed_status="TODO")
        queue = audit.form_review_queue([row, other])
        self.assertEqual(len(queue), 1)
        self.assertEqual(queue[0]["blocks"], ["Q1:B1", "Q1:B0", "Q2:B0"])
        self.assertEqual(queue[0]["status"], "UNVERIFIED")

    def test_readability_covers_visible_question_theory_and_every_option_section(self):
        audit = load_module("audit_readability", "mcq-audit.py")
        data = seed()
        first = data["questions"][0]["blocks"][0]
        first["question_text"] = "HIDDEN_SENTINEL"
        other = copy.deepcopy(first)
        other["block_idx"] = 1
        other["options"][0]["sections"]["example"] = "FLAG: пример написан непонятно."
        other["options"][0]["sections"]["related"] = "FLAG: пояснение ссылки написано непонятно."
        data["questions"][0]["blocks"].append(other)
        observed = []
        def scan(text, where, key=None):
            observed.append((text, where))
            return [{"code": "FIXTURE", "where": where, "detail": "candidate"}] if "FLAG" in text else []
        result = audit.readability_precheck(data, "## Q2. FLAG: вопрос?\nFLAG: теория.\n", scan)
        self.assertEqual(result["checked_fields"], 44)
        self.assertEqual(set(result["signals"]), {"Q2:B0", "Q2:B1"})
        locations = {s["where"].split(".paragraph")[0] for s in result["signals"]["Q2:B1"]}
        self.assertEqual(locations, {"visible_question", "theory", "A.sections.example", "A.sections.related"})
        self.assertFalse(any("HIDDEN_SENTINEL" in t for t, _ in observed))
        self.assertTrue(any(w.startswith("D.sections.how_it_should_be") for _, w in observed))

    def test_readability_respects_code_lists_and_does_not_accept_no_signals(self):
        audit = load_module("audit_readability_code", "mcq-audit.py")
        checker = load_module("russian_heuristics", "audit-mcq-parity.py")
        fragments = list(audit.prose_fragments("```java\nthis is the default operation in the framework\n```\n\n- Первый пункт.\n- Второй пункт."))
        self.assertEqual(fragments, ["- Первый пункт.", "- Второй пункт."])
        text = "Метод `CompletableFuture.thenApply()` преобразует результат вычисления."
        self.assertFalse(checker.readability_scan(text, "A.text", key="text"))
        result = audit.readability_precheck(seed(), "## Q2. Вопрос?\nОтвет.\n", lambda *a, **k: [])
        self.assertEqual(result["status"], "NO_SIGNALS")
        self.assertIn("UNVERIFIED", result["acceptance"])

    def test_file_checklist_cannot_accept_russian_from_clean_automation(self):
        audit = load_module("audit_checklist", "mcq-audit.py")
        row = {"id": "category/sample.json", "mcq": "modules/seed/category/sample.json",
               "claimed_status": "AUDITED", "blocks": 1, "structure": "PASS",
               "reviewed_blocks_initial": ["Q2:B0"], "reviewed_blocks_current": ["Q2:B0"],
               "linkage": {"errors": [], "warnings": 0}, "form_precheck": {"signals": {}},
               "readability_precheck": {"blocks_with_signals": 0}}
        row["checks"] = audit.file_checks(row)
        self.assertEqual(row["checks"]["russian_readability"]["status"], "UNVERIFIED")
        self.assertEqual(row["checks"]["final_acceptance"]["status"], "UNVERIFIED")
        rendered = audit.render_file_checklist({"files": [row]})
        self.assertIn("category/sample.json", rendered)
        self.assertIn("SFPL", rendered)
        self.assertIn("ANSC", rendered)
        self.assertIn("RU", rendered)

    def test_duplicate_ngram_identity_includes_block_index(self):
        parity = load_module("parity_multiblock", "mcq-answer-parity-gate.py")
        data = seed()
        block = data["questions"][0]["blocks"][0]
        for option, text in zip(block["options"], ["one two three", "blue", "green", "gold"]):
            option["text"] = text
        other = copy.deepcopy(block)
        other["block_idx"] = 1
        data["questions"][0]["blocks"].append(other)
        with patch.object(parity, "DUPLICATE_NGRAM_N", 2), patch.object(parity, "DUPLICATE_NGRAM_MAX", 0):
            checks, _ = parity.analyze(data, "sample", False, False)
        self.assertFalse(checks["DUPLICATE_NGRAMS"]["pass"])

    def test_markdown_gate_does_not_swallow_git_or_grep_errors(self):
        mockbin = Path(self.temp.name) / "bin"
        mockbin.mkdir()
        env = dict(os.environ, PATH=str(mockbin) + os.pathsep + os.environ["PATH"])
        for command, args in (("git", []), ("grep", [str(self.md)])):
            script = mockbin / command
            script.write_text("#!/bin/sh\nexit 2\n")
            script.chmod(0o755)
            result = subprocess.run(["bash", str(SCRIPTS / "verify-md-no-mcq.sh"), *args],
                                    env=env, text=True, capture_output=True)
            self.assertEqual(result.returncode, 2, result.stdout + result.stderr)
            script.unlink()

    def test_review_invalidation_follows_block_dependencies(self):
        audit = load_module("audit", "mcq-audit.py")
        data = seed()
        second = copy.deepcopy(data["questions"][0])
        second["q_number"] = 3
        data["questions"].append(second)
        markdown = "# Тема\n## Q2. Первый?\nПервое объяснение.\n## Q3. Второй?\nВторое объяснение.\n"
        hashes = audit.block_hashes(data, markdown)
        review = {"paths": {"theory": "sample.md"}, "sha256": {"mcq": "old", "theory": "old"},
                  "coverage": [{"q_number": n, "block_idx": 0, "sha256": hashes[f"Q{n}:B0"]} for n in (2, 3)]}
        def bound(content):
            return audit.current_coverage(review, audit.block_hashes(data, content),
                                          {"mcq": "new", "theory": "new"}, "sample.md")
        self.assertEqual(bound(markdown), {"Q2:B0", "Q3:B0"})
        self.assertEqual(bound(markdown.replace("Первое объяснение", "Исправленное объяснение")), {"Q3:B0"})
        self.assertEqual(bound(markdown.replace("# Тема", "# Новая общая оговорка")), set())
        data["questions"][1]["blocks"][0]["options"][0]["sections"]["explanation"] = "Правка sections"
        self.assertEqual(bound(markdown), {"Q2:B0"})

    def test_second_block_is_validated(self):
        data = seed()
        second = copy.deepcopy(data["questions"][0]["blocks"][0])
        second["block_idx"] = 1
        second["options"][1]["order"] = 0
        data["questions"][0]["blocks"].append(second)
        self.assertIn("Q2/B1", " ".join(self.check(data, "FAIL")["errors"]))

    def test_semantic_invariants_not_expressed_in_schema(self):
        def two_correct(data):
            opts = data["questions"][0]["blocks"][0]["options"]
            opts[1]["correct"] = True
            opts[1]["sections"] = opts[0]["sections"].copy()
        def no_correct(data):
            opts = data["questions"][0]["blocks"][0]["options"]
            opts[0]["correct"] = False
            opts[0]["sections"] = opts[1]["sections"].copy()
        cases = {
            "two correct": two_correct, "no correct": no_correct,
            "duplicate question": lambda d: d["questions"].append(copy.deepcopy(d["questions"][0])),
            "duplicate block": lambda d: d["questions"][0]["blocks"].append(copy.deepcopy(d["questions"][0]["blocks"][0])),
            "duplicate label": lambda d: d["questions"][0]["blocks"][0]["options"][1].update(label="A"),
            "duplicate order": lambda d: d["questions"][0]["blocks"][0]["options"][1].update(order=0),
            "blank option": lambda d: d["questions"][0]["blocks"][0]["options"][1].update(text=" \t"),
            "duplicate text": lambda d: d["questions"][0]["blocks"][0]["options"][1].update(text="`x == null`"),
            "nul": lambda d: d["questions"][0]["blocks"][0]["options"][1].update(text="a\x00b"),
            "slug": lambda d: d.update(topic_slug="different"),
        }
        for name, mutate in cases.items():
            with self.subTest(name=name):
                data = seed()
                mutate(data)
                self.assertFalse(list(self.validator.iter_errors(data)), name)
                self.check(data, "FAIL")

    def test_role_sections_and_types(self):
        for value in ([], {}, None, {"what_actually": "wrong role"}):
            with self.subTest(value=value):
                data = seed()
                data["questions"][0]["blocks"][0]["options"][0]["sections"] = value
                self.check(data, "FAIL")

    def test_duplicate_json_keys_nonfinite_and_malformed_json(self):
        for raw in ('{"questions": [], "questions": []}', '{"x": NaN}', '{'):
            with self.subTest(raw=raw):
                self.path.write_text(raw)
                self.assertEqual(mcq.validate_seed(self.path, self.validator)["status"], "FAIL")

    def test_markdown_missing_duplicate_and_mismatched_questions(self):
        for text in ("## Q3. Другой?", "## Q2. Вопрос?\n## Q2. Дубль?", "## Q2. "):
            with self.subTest(text=text):
                self.md.write_text(text)
                self.check(seed(), "FAIL")
        self.md.unlink()
        self.check(seed(), "FAIL")

    def test_headers_inside_code_are_not_questions(self):
        self.md.write_text("## Q2. Вопрос?\n```markdown\n## Q999. Пример\n```\n")
        self.check(seed(), "PASS")

    def test_structural_pass_does_not_claim_factual_correctness(self):
        data = seed()
        data["questions"][0]["blocks"][0]["options"][0]["text"] = "2 + 2 = 5"
        self.check(data, "PASS")  # Должен обнаружить содержательный ревьюер.

    def cli(self, *args):
        return subprocess.run(args, cwd=self.temp.name, text=True, capture_output=True)

    def test_missing_input_and_empty_directory_fail_closed(self):
        for script in ("verify-mcq-json.sh", "verify-md-no-mcq.sh", "mcq-tools/gates.sh"):
            with self.subTest(script=script):
                result = self.cli("bash", str(SCRIPTS / script), str(self.path))
                self.assertEqual(result.returncode, 2, result.stdout + result.stderr)
        parity = str(SCRIPTS / "mcq-answer-parity-gate.py")
        for path in (str(self.path), str(Path(self.temp.name) / "empty")):
            if path.endswith("empty"):
                Path(path).mkdir()
            result = self.cli(sys.executable, parity, path, "--json")
            self.assertEqual(result.returncode, 2, result.stdout + result.stderr)

    def test_parity_malformed_json_is_execution_error_in_both_output_modes(self):
        self.path.write_text("{")
        for flags in ([], ["--json"]):
            result = self.cli(sys.executable, str(SCRIPTS / "mcq-answer-parity-gate.py"),
                              str(self.path), *flags)
            self.assertEqual(result.returncode, 2)

    def test_dependency_missing_is_not_pass(self):
        self.path.write_text(json.dumps(seed()))
        result = self.cli(sys.executable, "-S", str(SCRIPTS / "mcq_validation.py"), str(self.path))
        self.assertEqual(result.returncode, 2)
        self.assertIn("jsonschema", result.stderr)

    def test_cli_from_different_directory(self):
        self.path.write_text(json.dumps(seed()))
        result = self.cli("bash", str(SCRIPTS / "verify-mcq-json.sh"), str(self.path), "--json")
        self.assertEqual(result.returncode, 0, result.stderr)
        report = json.loads(result.stdout)
        self.assertEqual(report["files"][0]["theory"], "NOT_RUN")

    def test_non_discriminating_features_contribute_chance(self):
        scan = load_module("struct_scan", "mcq-struct-scan.py")
        data = seed()
        for option in data["questions"][0]["blocks"][0]["options"]:
            option["text"] = "Одинаковая форма."
        self.path.write_text(json.dumps(data))
        total, hits, _ = scan.measure(self.path)
        self.assertEqual(total, 1)
        self.assertTrue(hits)
        self.assertTrue(all(value == .25 for value in hits.values()), hits)

    def test_blind_export_uses_visible_stem_and_never_exports_key(self):
        parity = load_module("parity_blind", "mcq-answer-parity-gate.py")
        data = seed()
        data["questions"][0]["blocks"][0]["question_text"] = "HIDDEN_STEM_SENTINEL"
        self.path.write_text(json.dumps(data))
        target = Path(self.temp.name) / "blind.jsonl"
        # Путь под корнем только для формирования идентификатора экспорта.
        canonical = mcq.SEED / "sample.json"
        with patch.object(mcq, "canonical_theory", return_value=self.md), \
                patch.object(mcq, "read_json", return_value=data), \
                patch.object(mcq, "validate_seed", return_value={"status": "PASS"}), \
                patch.object(parity, "collect", return_value=[str(canonical)]):
            self.assertEqual(parity.blind_export([str(canonical)], target), 1)
        text = target.read_text()
        record = json.loads(text)
        self.assertEqual(record["visible_question"], "Когда x равен null?")
        self.assertNotIn("HIDDEN_STEM_SENTINEL", text)
        self.assertNotIn("correct", text)
        self.assertNotIn("sections", text)

    def test_blind_failure_does_not_overwrite_existing_output(self):
        parity = load_module("parity_blind_fail", "mcq-answer-parity-gate.py")
        target = Path(self.temp.name) / "blind.jsonl"
        target.write_text("existing export")
        with self.assertRaises(ValueError):
            parity.blind_export([str(self.path)], target)
        self.assertEqual(target.read_text(), "existing export")

    def test_rotation_with_only_a_is_not_balanced_and_preserves_answer_content(self):
        rotation = load_module("rotation", "rebalance-mcq-rotation.py")
        self.assertEqual(rotation.ratio({"A": 8}), float("inf"))
        data = seed()
        data["questions"] = [dict(copy.deepcopy(data["questions"][0]), q_number=i + 1) for i in range(8)]
        def content(question):
            return sorted((o["text"], o["correct"], json.dumps(o["sections"], sort_keys=True))
                          for o in question["blocks"][0]["options"])
        before = [content(q) for q in data["questions"]]
        self.path.write_text(json.dumps(data))
        rotation.rebalance(str(self.path))
        after = json.loads(self.path.read_text())
        self.assertLessEqual(rotation.ratio(rotation.compute_rotation(after)), 2)
        self.assertEqual([content(q) for q in after["questions"]], before)


if __name__ == "__main__":
    unittest.main()
