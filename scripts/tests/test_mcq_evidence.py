"""Контрпримеры зачёта: блоки, актуальность, независимость и открытые находки."""
import copy
import importlib.util
import json
from pathlib import Path
import sys
import tempfile
import unittest
sys.path.insert(0, str(Path(__file__).parents[1]))
import mcq_evidence as ev

class EvidenceTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        self.rules = {p: 'a'*64 for p in ev.RULE_PATHS}
        self.hashes = {b: {'mcq': 'b'*64, 'theory': 'c'*64} for b in ('Q1:B0', 'Q1:B1')}
        self.row = {'mcq': 'seed/a.json', 'theory': 'theory/a.md', 'structure': 'PASS',
                    'all_block_ids': list(self.hashes), 'open_issues': []}
    def record(self, blocks=('Q1:B0',), check='sfpl', status='PASS', date='2026-09-14T10:00:00Z'):
        return {'review_id': date, 'reviewer': 'reviewer', 'author': 'author', 'model': 'test',
                'date': date, 'kind': 'independent', 'paths': {k: self.row[k] for k in ('mcq', 'theory')},
                'sha256': {'mcq': 'd'*64, 'theory': 'e'*64}, 'rules_sha256': self.rules,
                'coverage': [{'q_number': int(b.split(':')[0][1:]), 'block_idx': int(b.split(':')[1][1:]),
                              'sha256': copy.deepcopy(self.hashes[b]),
                              'checks': {check: {'status': status, 'reason': 'Проверены все варианты.'}}} for b in blocks],
                'findings': [], 'sources': [], 'limitations': []}
    def evaluate(self, records):
        return ev.evaluate(self.row, records, self.hashes, self.rules, self.root)
    def test_persists_after_two_serializations_and_regenerations(self):
        p = self.root / 'review.json'; p.write_text(json.dumps(self.record(tuple(self.hashes))))
        first = self.evaluate(ev.load_records(self.root))
        second = self.evaluate(ev.load_records(self.root))
        self.assertEqual(first, second)
        self.assertEqual(second['sfpl']['status'], 'PASS')
        self.assertEqual(second['ansc']['remaining_blocks'], list(self.hashes))
    def test_partial_not_pass_and_second_block_separate(self):
        result = self.evaluate([self.record()])
        self.assertEqual(result['sfpl']['status'], 'PARTIAL')
        self.assertEqual(result['sfpl']['remaining_blocks'], ['Q1:B1'])
    def test_dependent_text_becomes_stale_without_resetting_other_block(self):
        r = self.record(tuple(self.hashes)); self.hashes['Q1:B0']['theory'] = 'f'*64
        result = self.evaluate([r])['sfpl']
        self.assertEqual(result['status'], 'STALE')
        self.assertEqual(result['passed_blocks'], ['Q1:B1'])
    def test_unrelated_file_and_full_pair_change_preserve_block_evidence(self):
        r = self.record(tuple(self.hashes))
        self.row['sha256'] = {'mcq': 'f'*64, 'theory': 'f'*64}
        (self.root / 'unrelated').write_text('changed')
        self.assertEqual(self.evaluate([r])['sfpl']['status'], 'PASS')
    def test_changed_rules_stale(self):
        r = self.record(); r['rules_sha256'] = dict(self.rules, **{ev.RULE_PATHS[0]: 'f'*64})
        self.assertEqual(self.evaluate([r])['sfpl']['status'], 'STALE')
    def test_later_open_finding_not_hidden_by_pass_even_after_further_pass(self):
        old = self.record(tuple(self.hashes))
        newer = self.record(date='2026-09-14T11:00:00Z')
        newer['findings'] = [{'id': 'F1', 'check': 'sfpl', 'blocks': ['Q1:B0'], 'status': 'OPEN', 'reason': 'Подсказка формы.'}]
        latest = self.record(tuple(self.hashes), date='2026-09-14T12:00:00Z')
        self.assertEqual(self.evaluate([old,newer,latest])['sfpl']['status'], 'FAIL')
        latest['findings'] = [dict(newer['findings'][0], status='RESOLVED', reason='Исправление перепроверено.')]
        self.assertEqual(self.evaluate([old,newer,latest])['sfpl']['status'], 'PASS')

    def test_duplicate_finding_id_cannot_hide_or_change_existing_finding(self):
        first = self.record(); first['findings'] = [{'id': 'F1', 'check': 'sfpl', 'blocks': ['Q1:B0'], 'status': 'OPEN', 'reason': 'Первое.'}]
        changed = self.record(date='2026-09-14T11:00:00Z'); changed['findings'] = [{'id': 'F1', 'check': 'ansc', 'blocks': ['Q1:B1'], 'status': 'OPEN', 'reason': 'Подмена.'}]
        with self.assertRaises(ValueError): self.evaluate([first, changed])
        duplicate = self.record(); duplicate['findings'] = [dict(first['findings'][0]), dict(first['findings'][0])]
        with self.assertRaises(ValueError): ev.validate_record(duplicate)
    def test_signal_never_editorial_acceptance(self):
        self.row['readability_precheck'] = {'status': 'NO_SIGNALS'}
        self.assertEqual(self.evaluate([])['russian_readability']['status'], 'NOT_RUN')
        with self.assertRaises(ValueError): ev.validate_record(self.record(status='NO_SIGNALS'))

    def test_resolution_without_rechecking_the_same_check_does_not_close_finding(self):
        r = self.record()
        r['findings'] = [{'id': 'F1', 'check': 'sfpl', 'blocks': ['Q1:B0'],
                          'status': 'OPEN', 'reason': 'Разный каркас.'}]
        later = self.record(check='ansc', date='2026-09-14T12:00:00Z')
        later['findings'] = [dict(r['findings'][0], status='RESOLVED')]
        self.assertEqual(self.evaluate([r, later])['sfpl']['status'], 'FAIL')
    def test_missing_corrupt_and_unsuitable_evidence(self):
        self.assertEqual(ev.load_records(self.root / 'missing'), [])
        p = self.root / 'bad.json'; p.write_text('{')
        with self.assertRaises(ValueError): ev.load_records(self.root)
        for mutate in (lambda r:r.pop('sha256'), lambda r:r.update(coverage=[]),
                       lambda r:r.update(kind='automatic'), lambda r:r.update(rules_sha256={}),
                       lambda r:r['coverage'].append(copy.deepcopy(r['coverage'][0]))):
            r=self.record(); mutate(r)
            with self.assertRaises(ValueError): ev.validate_record(r)
    def test_wrong_pair_and_unknown_block_rejected(self):
        r = self.record(); r['paths']['theory'] = 'wrong.md'
        with self.assertRaises(ValueError): self.evaluate([r])
        r = self.record(); r['coverage'][0]['block_idx'] = 5
        with self.assertRaises(ValueError): self.evaluate([r])
    def test_author_cannot_claim_independent_and_fake_blind(self):
        r = self.record(tuple(self.hashes), check='independent_review'); r['reviewer'] = r['author']
        self.assertEqual(self.evaluate([r])['independent_review']['status'], 'UNVERIFIED')
        r = self.record(tuple(self.hashes), check='blind_review')
        self.assertEqual(self.evaluate([r])['blind_review']['status'], 'UNVERIFIED')
    def test_final_requires_explicit_verdict_and_all_checks(self):
        r = self.record(tuple(self.hashes), check='final_acceptance')
        self.assertNotEqual(self.evaluate([r])['final_acceptance']['status'], 'PASS')
    def test_fact_urls_without_checked_claim_rejected(self):
        r = self.record(check='factual_correctness');r['sources'] = [{'url': 'https://example.com'}]
        with self.assertRaises(ValueError): ev.validate_record(r)
    def test_fact_source_checked_at_requires_real_iso_date(self):
        r = self.record(check='factual_correctness')
        r['sources'] = [{'url': 'https://example.com/spec', 'used_for': 'Q1:B0',
                         'version': '1', 'checked_at': '2026-99-99'}]
        with self.assertRaisesRegex(ValueError, 'ISO date'): ev.validate_record(r)

    def test_resolved_finding_id_cannot_be_reused_for_another_check_or_block(self):
        opened = self.record()
        opened['findings'] = [{'id': 'F1', 'check': 'sfpl', 'blocks': ['Q1:B0'],
                               'status': 'OPEN', 'reason': 'Отличается форма.'}]
        resolved = self.record(date='2026-09-14T11:00:00Z')
        resolved['findings'] = [dict(opened['findings'][0], status='RESOLVED',
                                    reason='Повторно проверено после правки.')]
        later = self.record(blocks=('Q1:B1',), check='ansc', date='2026-09-14T12:00:00Z')
        later['findings'] = [{'id': 'F1', 'check': 'ansc', 'blocks': ['Q1:B1'],
                              'status': 'OPEN', 'reason': 'Неполный ответ.'}]
        self.assertEqual(self.evaluate([opened, resolved])['sfpl']['status'], 'PARTIAL')
        with self.assertRaisesRegex(ValueError, 'finding id changed'):
            self.evaluate([opened, resolved, later])
    def test_semantic_dependency_changes_invalidate(self):
        p=self.root/'other.md';p.write_text('definition')
        r=self.record();r['coverage'][0]['dependencies']={'other.md':ev.sha256(p)}
        self.assertEqual(self.evaluate([r])['sfpl']['passed_blocks'],['Q1:B0'])
        p.write_text('new definition')
        self.assertEqual(self.evaluate([r])['sfpl']['status'],'STALE')
    def test_form_queue_removes_only_both_accepted_checks(self):
        spec=importlib.util.spec_from_file_location('audit',Path(__file__).parents[1]/'mcq-audit.py')
        audit=importlib.util.module_from_spec(spec);spec.loader.exec_module(audit)
        r=self.record(check='sfpl'); self.row.update(id='a',claimed_status='AUDITED',form_precheck={'signals':{}})
        self.row['checks']=self.evaluate([r]);self.assertEqual(audit.form_review_queue([self.row])[0]['blocks'],list(self.hashes))
        r['coverage'][0]['checks']['ansc']={'status':'PASS','reason':'Полные ответы.'}
        self.row['checks']=self.evaluate([r]);self.assertEqual(audit.form_review_queue([self.row])[0]['blocks'],['Q1:B1'])

    def test_real_blind_artifact_accepts_current_text_and_rejects_leaked_export(self):
        r = self.record(check='blind_review')
        r.update(kind='independent_two_phase', revealed_at='2026-09-14T09:00:00Z')
        options = [{'label': x, 'text': 'Вариант ' + x} for x in 'ABCD']
        for key in ('mcq', 'theory'):
            (self.root / r['paths'][key]).parent.mkdir(parents=True, exist_ok=True)
        (self.root / r['paths']['mcq']).write_text(json.dumps({'questions': [
            {'q_number': 1, 'blocks': [{'block_idx': 0, 'options': options}]}]}))
        (self.root / r['paths']['theory']).write_text('## Q1. Вопрос?\nТеория.\n')
        exported = {'file': self.row['mcq'], 'q_number': 1, 'block_idx': 0,
                    'visible_question': 'Вопрос?', 'options': options}
        inp = self.root / 'input.jsonl'; inp.write_text(json.dumps(exported) + '\n')
        phase = {'kind': 'blind_phase_a', 'reviewer': 'reviewer',
                 'recorded_at': '2026-09-14T08:00:00Z',
                 'input': {'path': 'input.jsonl', 'sha256': ev.sha256(inp)},
                 'answers': [{'q_number': 1, 'block_idx': 0, 'choice': 'A'}]}
        artifact = self.root / 'phase.json'; artifact.write_text(json.dumps(phase))
        r['phase_a'] = {'path': 'phase.json', 'sha256': ev.sha256(artifact)}
        self.assertEqual(self.evaluate([r])['blind_review']['passed_blocks'], ['Q1:B0'])
        exported['correct'] = 'A'; inp.write_text(json.dumps(exported))
        phase['input']['sha256'] = ev.sha256(inp)
        artifact.write_text(json.dumps(phase)); r['phase_a']['sha256'] = ev.sha256(artifact)
        self.assertEqual(self.evaluate([r])['blind_review']['status'], 'UNVERIFIED')

    def test_blind_accepts_explicit_uncertainty_and_rejects_duplicate_or_outside_answers(self):
        r = self.record(check='blind_review')
        r.update(kind='independent_two_phase', revealed_at='2026-09-14T09:00:00Z')
        options = [{'label': x, 'text': 'Вариант ' + x} for x in 'ABCD']
        for key in ('mcq', 'theory'):
            (self.root / r['paths'][key]).parent.mkdir(parents=True, exist_ok=True)
        (self.root / r['paths']['mcq']).write_text(json.dumps({'questions': [{'q_number': 1, 'blocks': [{'block_idx': 0, 'options': options}]}]}))
        (self.root / r['paths']['theory']).write_text('## Q1. Вопрос?\nТеория.\n')
        inp = self.root / 'input.jsonl'; inp.write_text(json.dumps({'file': self.row['mcq'], 'q_number': 1, 'block_idx': 0, 'visible_question': 'Вопрос?', 'options': options}) + '\n')
        phase = {'kind': 'blind_phase_a', 'reviewer': 'reviewer', 'recorded_at': '2026-09-14T08:00:00Z', 'input': {'path': 'input.jsonl', 'sha256': ev.sha256(inp)}, 'answers': [{'q_number': 1, 'block_idx': 0, 'choice': 'UNSURE'}]}
        artifact = self.root / 'phase.json'; artifact.write_text(json.dumps(phase)); r['phase_a'] = {'path': 'phase.json', 'sha256': ev.sha256(artifact)}
        self.assertEqual(self.evaluate([r])['blind_review']['passed_blocks'], ['Q1:B0'])
        phase['answers'].append({'q_number': 1, 'block_idx': 0, 'choice': 'A'}); artifact.write_text(json.dumps(phase)); r['phase_a']['sha256'] = ev.sha256(artifact)
        self.assertEqual(self.evaluate([r])['blind_review']['status'], 'UNVERIFIED')

    def test_final_pass_cannot_mask_runtime_or_open_issue(self):
        r = self.record(check='final_acceptance')
        self.row['open_issues'] = [{'id': 'runtime', 'q_number': 1, 'block_idx': 0,
                                  'status': 'OPEN', 'next_action': 'Починить выбор блока.'}]
        self.assertEqual(self.evaluate([r])['final_acceptance']['block_statuses']['Q1:B0']['status'], 'BLOCKED')

    def test_missing_review_artifact_makes_result_stale(self):
        r = self.record()
        r['review_artifact'] = {'path': 'missing.json', 'sha256': 'f' * 64}
        self.assertEqual(self.evaluate([r])['sfpl']['status'], 'STALE')

    def test_checklist_regeneration_preserves_pass_from_saved_record(self):
        spec = importlib.util.spec_from_file_location('audit_table', Path(__file__).parents[1] / 'mcq-audit.py')
        audit = importlib.util.module_from_spec(spec); spec.loader.exec_module(audit)
        record = self.record(tuple(self.hashes))
        (self.root / 'review.json').write_text(json.dumps(record))
        row = dict(self.row, id='a.json', claimed_status='AUDITED', blocks=2, questions=1,
                   reviewed_blocks_current=[], linkage={'errors': [], 'warnings': 0},
                   form_precheck={'signals': {}}, readability_precheck={'blocks_with_signals': 0})
        outputs = []
        for _ in range(2):
            row['checks'] = audit.file_checks(row, ev.load_records(self.root), self.hashes, self.rules)
            snapshot = json.loads(json.dumps({'files': [row]}))
            outputs.append(audit.render_file_checklist(snapshot))
        self.assertEqual(outputs[0], outputs[1])
        self.assertIn('SFPL PASS', outputs[1])
        self.assertEqual(row['checks']['ansc']['remaining_blocks'], list(self.hashes))

if __name__ == '__main__': unittest.main()
