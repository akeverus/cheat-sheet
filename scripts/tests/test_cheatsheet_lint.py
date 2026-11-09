"""Проверка внутренних ссылок вне interview-раздела."""

import importlib.util
from pathlib import Path
import tempfile
import unittest


SCRIPT = Path(__file__).resolve().parents[1] / "cheatsheet-lint.py"
spec = importlib.util.spec_from_file_location("cheatsheet_lint", SCRIPT)
lint = importlib.util.module_from_spec(spec)
spec.loader.exec_module(lint)


class InternalLinksTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name) / "cheatsheets"
        self.root.mkdir()
        self.doc = self.root / "topic" / "source.md"
        self.doc.parent.mkdir()
        (self.doc.parent / "peer.md").write_text("# Peer\n")
        (self.root / "parent.md").write_text("# Parent\n")

    def violations(self, content):
        self.doc.write_text(content)
        ids = (
            "heading-bold", "site-section", "backtick-link", "md-link-internal",
            "wikilink-broken", "bold-parens", "quotes-ru", "dash-prose",
            "backtick-bold", "emoji", "ascii-diagram", "trailing-ws",
            "code-no-lang",
        )
        rules = {rid: lint.Rule(rid, rid) for rid in ids}
        lint.check_file(self.doc, self.root, lint.build_md_index(self.root), rules)
        return rules

    def test_existing_relative_links_and_external_link_pass(self):
        for link in (
            "[Peer](peer.md)",
            "[Parent](../parent.md)",
            "[Section](peer.md#heading)",
            "[External](https://example.com/peer.md)",
        ):
            with self.subTest(link=link):
                self.assertEqual([], self.violations(link)["md-link-internal"].violations)

    def test_missing_link_is_reported(self):
        errors = self.violations("[Missing](absent.md)")["md-link-internal"].violations
        self.assertEqual(1, len(errors))
        self.assertIn("absent.md", errors[0][2])

    def test_each_missing_link_on_same_line_is_reported(self):
        content = "[One](one.md) [Peer](peer.md) [Two](two.md)"
        errors = self.violations(content)["md-link-internal"].violations
        self.assertEqual(2, len(errors))
        self.assertIn("one.md", errors[0][2])
        self.assertIn("two.md", errors[1][2])

    def test_link_outside_cheatsheets_is_reported(self):
        (Path(self.temp.name) / "outside.md").write_text("# Outside\n")
        errors = self.violations("[Outside](../../outside.md)")["md-link-internal"].violations
        self.assertEqual(1, len(errors))

    def test_absolute_local_link_is_reported(self):
        errors = self.violations("[Absolute](/tmp/peer.md)")["md-link-internal"].violations
        self.assertEqual(1, len(errors))

    def test_code_fence_does_not_create_link_violation(self):
        content = "```markdown\n[Missing](absent.md)\n```\n"
        self.assertEqual([], self.violations(content)["md-link-internal"].violations)

    def test_legacy_wikilinks_keep_broken_link_check(self):
        rules = self.violations("[[peer]] [[absent]]")
        self.assertEqual([], rules["md-link-internal"].violations)
        errors = rules["wikilink-broken"].violations
        self.assertEqual(1, len(errors))
        self.assertIn("absent", errors[0][2])


if __name__ == "__main__":
    unittest.main()
