"""Публичный жизненный цикл: пакет → blind → evidence → продолжение без LLM/API."""
import copy
import importlib.util
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).parents[1]))
sys.path.insert(0, str(Path(__file__).parent))
from test_mcq_validation import seed
import mcq_evidence as ev

spec = importlib.util.spec_from_file_location('workflow', Path(__file__).parents[1] / 'mcq-workflow.py')
wf = importlib.util.module_from_spec(spec)
spec.loader.exec_module(wf)


class WorkflowTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name).resolve()
        self.patch = patch.object(wf, 'ROOT', self.root)
        self.patch.start()
        self.addCleanup(self.patch.stop)
        self.data = seed()
        second = copy.deepcopy(self.data['questions'][0]['blocks'][0])
        second['block_idx'] = 1
        self.data['questions'][0]['blocks'].append(second)
        self.seed = self.root / wf.SEED / 'sample.json'
        self.md = self.root / 'cheatsheets/interview/sample.md'
        self.save(self.seed, self.data)
        self.md.parent.mkdir(parents=True)
        self.md.write_text('# Общие условия\n\n## Q2. Когда x равен null?\nТеория.\n')
        for p in ev.RULE_PATHS:
            dest = self.root / p
            dest.parent.mkdir(parents=True, exist_ok=True)
            dest.write_text('Правила тестового корпуса.')
        self.save(self.root / wf.QUALITY / 'linkage-reset-manifest.json', {
            'files': [{'mcq_file': wf.rel(self.seed), 'status': 'AUDITED'}]})

    def save(self, path, value):
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(json.dumps(value, ensure_ascii=False))

    def prepare_review(self, run='test-one', blocks='Q2:B0'):
        wf.prepare('sample.json', blocks, run)
        directory = wf.run_dir(run)
        answers = [{'q_number': 2, 'block_idx': int(b.split('B')[1]), 'choice': 'A',
                    'reason': 'Тестовое решение.', 'form_signals': []} for b in blocks.split(',')]
        self.save(self.root / 'answers.json', answers)
        wf.phase_a(run, 'answers.json', 'independent-context-1')
        wf.reveal(run, 'author-context', 'independent-context-1', 'test-model')
        return directory

    def complete_draft(self, directory):
        path = directory / 'disclosure/evidence-draft.json'
        draft = wf.mcq.read_json(path)
        for block in draft['coverage']:
            block['checks'] = {c: {'status': 'PASS', 'reason': 'Синтетическая проверка контракта.'}
                               for c in ev.CHECKS}
        draft['sources'] = [{'url': 'https://example.test/spec', 'version': 'fixture',
                             'checked_at': wf.now(), 'used_for': 'Только синтетические данные теста.'}]
        draft['date'] = wf.now()
        self.save(path, draft)
        return path, draft

    def test_end_to_end_keeps_second_block_pending_and_reuses_saved_acceptance(self):
        directory = self.prepare_review()
        path, _ = self.complete_draft(directory)
        result = wf.record_result(path)
        self.assertTrue((self.root / result['evidence']).is_file())
        for _ in range(2):
            rows = wf.live_rows()
            self.assertEqual(wf.summary(rows)['accepted_blocks'], 1)
            self.assertEqual(wf.summary(rows)['accepted_files'], 0)
            self.assertEqual(wf.next_batch(rows)['blocks'], ['Q2:B1'])

    def test_blind_input_contains_no_key_sections_hashes_or_hidden_question(self):
        wf.prepare('sample.json', 'Q2:B1', 'blind')
        directory = wf.run_dir('blind')
        value = json.loads((directory / 'blind-input.jsonl').read_text())
        self.assertEqual(set(value), {'file', 'q_number', 'block_idx', 'visible_question', 'options'})
        self.assertTrue(all(set(o) == {'label', 'text'} for o in value['options']))
        self.assertEqual(value['block_idx'], 1)
        self.assertFalse((directory / 'review-input.json').exists())

    def test_reveal_requires_phase_a_and_same_independent_reviewer(self):
        wf.prepare('sample.json', 'Q2:B0', 'first')
        with self.assertRaises(OSError): wf.reveal('first', 'author', 'reviewer')
        self.prepare_review('second')
        with self.assertRaises(ValueError): wf.reveal('second', 'author', 'different-reviewer')
        with self.assertRaises(ValueError): wf.reveal('second', 'independent-context-1', 'independent-context-1')

    def test_phase_a_is_immutable_and_cannot_be_rewritten_after_reveal(self):
        directory = self.prepare_review()
        original = (directory / 'phase-a.json').read_bytes()
        with self.assertRaises(ValueError): wf.phase_a('test-one', 'answers.json', 'independent-context-1')
        self.assertEqual((directory / 'phase-a.json').read_bytes(), original)

    def test_changes_after_prepare_block_reveal_but_unrelated_changes_do_not(self):
        self.prepare_review()
        (self.root / 'unrelated.txt').write_text('Новое содержимое.')
        wf.current_run('test-one')
        self.md.write_text(self.md.read_text().replace('Теория.', 'Новая теория.'))
        with self.assertRaisesRegex(ValueError, 'STALE'): wf.current_run('test-one')

    def test_blind_export_tampering_cannot_be_hidden_by_rehash(self):
        wf.prepare('sample.json', 'Q2:B0', 'tampered')
        directory = wf.run_dir('tampered')
        inp = directory / 'blind-input.jsonl'
        exported = json.loads(inp.read_text())
        exported['correct'] = 'A'
        self.save(inp, exported)
        manifest = wf.mcq.read_json(directory / 'manifest.json')
        manifest['input']['sha256'] = ev.sha256(inp)
        self.save(directory / 'manifest.json', manifest)
        with self.assertRaisesRegex(ValueError, 'leaked key'): wf.current_run('tampered')

    def test_evidence_cannot_be_overwritten_or_imported_with_stale_input(self):
        directory = self.prepare_review()
        path, draft = self.complete_draft(directory)
        wf.record_result(path)
        with self.assertRaisesRegex(ValueError, 'already registered'): wf.record_result(path)
        draft['review_id'] = 'test-two'
        draft['sha256']['mcq'] = '0' * 64
        self.save(path, draft)
        with self.assertRaisesRegex(ValueError, 'STALE'): wf.record_result(path)

    def test_duplicate_json_key_cannot_hide_hint_in_blind_text(self):
        wf.prepare('sample.json', 'Q2:B0', 'duplicate')
        directory = wf.run_dir('duplicate')
        inp = directory / 'blind-input.jsonl'
        inp.write_text(inp.read_text().replace('"text":', '"text":"Ответ A правильный", "text":', 1))
        manifest = wf.mcq.read_json(directory / 'manifest.json')
        manifest['input']['sha256'] = ev.sha256(inp)
        self.save(directory / 'manifest.json', manifest)
        with self.assertRaisesRegex(ValueError, 'duplicate JSON key'): wf.current_run('duplicate')

    def test_incomplete_final_or_fact_check_without_sources_not_imported(self):
        directory = self.prepare_review()
        path, draft = self.complete_draft(directory)
        draft['sources'] = []
        self.save(path, draft)
        with self.assertRaisesRegex(ValueError, 'unconfirmed PASS'): wf.record_result(path)
        draft['coverage'][0]['checks'] = {'final_acceptance': {'status': 'PASS', 'reason': 'Неполно.'}}
        self.save(path, draft)
        with self.assertRaisesRegex(ValueError, 'unconfirmed PASS'): wf.record_result(path)
        self.assertFalse((self.root / wf.QUALITY / 'reviews/checks/test-one.json').exists())

    def test_missing_or_duplicate_blocks_and_escaping_paths_rejected(self):
        for blocks in ('Q2:B9', 'Q2:B0,Q2:B0', ''):
            with self.assertRaises(ValueError): wf.prepare('sample.json', blocks, 'bad')
        with self.assertRaises(ValueError): wf.packet('sample.json', 'Q2:B0', '../outside.json')
        with self.assertRaises(ValueError): wf.prepare('sample.json', 'Q2:B0', '../escape')

    def test_two_phase_import_requires_workflow_receipt(self):
        directory = self.prepare_review()
        path, draft = self.complete_draft(directory)
        del draft['workflow_run']
        self.save(path, draft)
        with self.assertRaisesRegex(ValueError, 'require workflow_run'): wf.record_result(path)

    def test_sealed_phase_a_change_is_detected_before_reveal(self):
        wf.prepare('sample.json', 'Q2:B0', 'sealed')
        self.save(self.root / 'answers.json', [{'q_number': 2, 'block_idx': 0, 'choice': 'A',
                                              'reason': 'Причина.', 'form_signals': []}])
        wf.phase_a('sealed', 'answers.json', 'reviewer')
        path = wf.run_dir('sealed') / 'phase-a.json'
        phase = wf.mcq.read_json(path)
        phase['answers'][0]['choice'] = 'B'
        self.save(path, phase)
        with self.assertRaisesRegex(ValueError, 'changed after sealing'): wf.reveal('sealed', 'author', 'reviewer')

    def test_failed_reveal_does_not_publish_receipt_and_can_be_retried(self):
        wf.prepare('sample.json', 'Q2:B0', 'retry')
        self.save(self.root / 'answers.json', [{'q_number': 2, 'block_idx': 0, 'choice': 'A',
                                              'reason': 'Причина.', 'form_signals': []}])
        wf.phase_a('retry', 'answers.json', 'reviewer')
        original = wf.write
        def broken(path, *args, **kwargs):
            if Path(path).name == 'review-input.json': raise OSError('synthetic disk error')
            return original(path, *args, **kwargs)
        with patch.object(wf, 'write', side_effect=broken), self.assertRaises(OSError):
            wf.reveal('retry', 'author', 'reviewer')
        self.assertFalse((wf.run_dir('retry') / 'disclosure').exists())
        wf.reveal('retry', 'author', 'reviewer')
        self.assertTrue((wf.run_dir('retry') / 'disclosure/evidence-draft.json').is_file())

    def test_accepted_topic_not_queued_but_invalidated_dependency_returns(self):
        directory = self.prepare_review(blocks='Q2:B0,Q2:B1')
        path, _ = self.complete_draft(directory)
        wf.record_result(path)
        self.assertTrue(wf.next_batch(wf.live_rows())['complete'])
        self.data['questions'][0]['blocks'][1]['options'][0]['sections']['example'] = 'Изменённый пример.'
        self.save(self.seed, self.data)
        self.assertEqual(wf.next_batch(wf.live_rows())['blocks'], ['Q2:B1'])
        self.assertEqual(wf.summary(wf.live_rows())['accepted_blocks'], 1)


if __name__ == '__main__': unittest.main()
