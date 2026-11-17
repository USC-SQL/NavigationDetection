"""Join outcome-blinded pattern matches with withheld evaluation labels.

Run from the NavigationDetection repository root:
    python3 Evaluation_Scripts/calculate_pattern_occurrences.py

The output goes into a NEW results/full_dataset/ directory. Existing
results/patterns/ discovery-stage artifacts are never changed.
"""
import csv
import json
from collections import Counter, defaultdict
from pathlib import Path

BASE = Path('Evaluation_Scripts/LNF_Error_Pattern_Discovery')
MATCHING_INPUTS = BASE / 'matching_inputs'
MATCHING_OUTPUTS = BASE / 'matching_outputs'
EVALUATION_KEY = BASE / 'matching_evaluation_key.csv'
DEFINITIONS = BASE / 'matching_pattern_definitions.json'
ID_MAP = BASE / 'matching_pattern_id_map.json'
DISCOVERY = BASE / 'cross_subject_patterns.json'
DISCOVERY_INPUT = BASE / 'cross_subject_input.json'
DEST = BASE / 'results/full_dataset'
LABELS = ('TP', 'FP', 'FN', 'TN')


def load_json(path):
    return json.loads(path.read_text(encoding='utf-8'))


def write_csv(path, rows, fieldnames):
    with path.open('x', encoding='utf-8-sig', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def normalized_transition(value):
    # Same normalization used when preparing the 66 discovery cases.
    parts = ''.join(value.split()).split('->')
    if len(parts) != 2 or not all(parts):
        raise ValueError(f'Unparseable XPath transition: {value!r}')
    return ' -> '.join(parts)


def main():
    if DEST.exists():
        raise FileExistsError(f'Output already exists: {DEST}')

    definitions = load_json(DEFINITIONS)['patterns']
    neutral_definitions = {p['pattern_id']: p for p in definitions}
    mapping = load_json(ID_MAP)
    neutral_to_discovered = {
        p['neutral_id']: p['discovered_id'] for p in mapping
    }
    discovered = {
        p['pattern_id']: p
        for p in load_json(DISCOVERY)['patterns']
    }
    if (len(neutral_definitions) != 9 or len(neutral_to_discovered) != 9
            or len(discovered) != 9
            or set(neutral_definitions) != set(neutral_to_discovered)
            or set(neutral_to_discovered.values()) != set(discovered)):
        raise ValueError('The nine pattern definitions/maps disagree.')

    with EVALUATION_KEY.open(encoding='utf-8-sig', newline='') as file:
        key_rows = list(csv.DictReader(file))
    keys = {}
    keys_by_subject = defaultdict(set)
    by_transition = {}
    for row in key_rows:
        key = (row['subject'], row['transition_id'])
        if key in keys or row['classification'] not in LABELS:
            raise ValueError(f'Duplicate/invalid evaluation key: {key}')
        keys[key] = row
        keys_by_subject[row['subject']].add(row['transition_id'])
        normalized_key = (row['subject'], normalized_transition(row['transition']))
        if normalized_key in by_transition:
            raise ValueError(f'Ambiguous transition in evaluation key: {normalized_key}')
        by_transition[normalized_key] = key
    if len(keys) != 860 or len(keys_by_subject) != 37:
        raise ValueError(f'Expected 860 transitions over 37 subjects, got {len(keys)} over {len(keys_by_subject)}')

    stats = {pid: Counter() for pid in neutral_definitions}
    subjects_per_pattern = {pid: set() for pid in neutral_definitions}
    observations = []
    decision_by_pair = {}
    seen_subjects = set()

    for subject in sorted(keys_by_subject):
        input_path = MATCHING_INPUTS / f'{subject}.json'
        output_path = MATCHING_OUTPUTS / f'{subject}.json'
        if not input_path.is_file() or not output_path.is_file():
            raise FileNotFoundError(f'Missing matching input/output for {subject}')
        original_input = load_json(input_path)
        output = load_json(output_path)
        if original_input['subject'] != subject or output['subject'] != subject:
            raise ValueError(f'Subject mismatch: {subject}')

        input_candidates = {
            row['transition_id']: row
            for row in original_input['candidate_transitions']
        }
        if set(input_candidates) != keys_by_subject[subject]:
            raise ValueError(f'Candidate transition IDs do not agree for {subject}')
        assessed = set()
        for assessment in output['transition_assessments']:
            tid = assessment['transition_id']
            if tid not in input_candidates or tid in assessed:
                raise ValueError(f'Unknown or repeated assessment: {subject}/{tid}')
            assessed.add(tid)
            source = input_candidates[tid]
            evaluation = keys[(subject, tid)]
            if source['transition'] != evaluation['transition']:
                raise ValueError(f'Transition text mismatch: {subject}/{tid}')

            decisions = {}
            for entry in assessment['matches']:
                pid = entry['pattern_id']
                if pid in decisions or not entry.get('evidence', '').strip():
                    raise ValueError(f'Invalid MATCH: {subject}/{tid}/{pid}')
                decisions[pid] = ('match', entry['evidence'])
            for entry in assessment['uncertain']:
                pid = entry['pattern_id']
                if pid in decisions or not entry.get('reason', '').strip():
                    raise ValueError(f'Invalid UNCERTAIN: {subject}/{tid}/{pid}')
                decisions[pid] = ('uncertain', entry['reason'])
            for pid in assessment['no_match_pattern_ids']:
                if pid in decisions:
                    raise ValueError(f'Repeated decision: {subject}/{tid}/{pid}')
                decisions[pid] = ('no_match', '')
            if set(decisions) != set(neutral_definitions):
                raise ValueError(f'Incomplete pattern decisions: {subject}/{tid}')

            for pid, (decision, evidence) in decisions.items():
                decision_by_pair[(subject, tid, pid)] = decision
                stats[pid][decision] += 1
                if decision != 'no_match':
                    stats[pid][f'{decision}_{evaluation["classification"]}'] += 1
                    observations.append({
                        'discovered_pattern_id': neutral_to_discovered[pid],
                        'neutral_pattern_id': pid,
                        'subject': subject,
                        'transition_id': tid,
                        'decision': decision,
                        'classification': evaluation['classification'],
                        'transition': source['transition'],
                        'source_descriptor': source.get('source_descriptor', ''),
                        'destination_descriptor': source.get('destination_descriptor', ''),
                        'observed_in_stage1': source.get('observed_in_stage1', ''),
                        'browser_boundary': source.get('browser_boundary', ''),
                        'evidence_or_reason': evidence,
                    })
                if decision == 'match':
                    subjects_per_pattern[pid].add(subject)
        if assessed != keys_by_subject[subject]:
            raise ValueError(f'Missing assessments for {subject}: {keys_by_subject[subject] - assessed}')
        seen_subjects.add(subject)

    if len(decision_by_pair) != 860 * 9 or len(seen_subjects) != 37:
        raise ValueError('Mismatch in total validated decisions.')

    # Audit whether original discovery-stage errors still match after
    # the operational definitions were refined. Do not discard disagreements.
    cases = {
        row['case_id']: row
        for row in load_json(DISCOVERY_INPUT)['error_cases']
    }
    discovery_audit = []
    discovery_counts = defaultdict(Counter)
    for discovered_id, pattern in sorted(discovered.items()):
        pid = next(k for k, v in neutral_to_discovered.items() if v == discovered_id)
        for case_id in pattern['matched_case_ids']:
            case = cases[case_id]
            transition_key = (case['subject'], normalized_transition(case['transition']))
            if transition_key not in by_transition:
                raise ValueError(f'Discovery error absent from transition inventory: {case_id}')
            subject, tid = by_transition[transition_key]
            decision = decision_by_pair[(subject, tid, pid)]
            discovery_counts[pid][decision] += 1
            discovery_audit.append({
                'discovered_pattern_id': discovered_id,
                'neutral_pattern_id': pid,
                'case_id': case_id,
                'subject': subject,
                'transition_id': tid,
                'classification': case['classification'],
                'blinded_match_decision': decision,
                'transition': case['transition'],
            })

    summary = []
    for pid in sorted(neutral_definitions):
        original_id = neutral_to_discovered[pid]
        pattern = discovered[original_id]
        target = pattern['error_type']
        counts = stats[pid]
        matched = counts['match']
        uncertain = counts['uncertain']
        if matched + uncertain + counts['no_match'] != 860:
            raise ValueError(f'Incomplete total for {pid}')
        target_matches = counts[f'match_{target}']
        row = {
            'pattern_id': original_id,
            'neutral_pattern_id': pid,
            'pattern_name': pattern['name'],
            'pattern_error_type': target,
            'total_model_matches': matched,
            'matched_subjects': len(subjects_per_pattern[pid]),
            **{f'matched_{label}': counts[f'match_{label}'] for label in LABELS},
            'uncertain_decisions': uncertain,
            **{f'uncertain_{label}': counts[f'uncertain_{label}'] for label in LABELS},
            'no_match_decisions': counts['no_match'],
            'target_error_matches': target_matches,
            'target_error_percent_of_model_matches': (
                round(100 * target_matches / matched, 2) if matched else ''
            ),
            'discovery_stage_error_memberships': len(pattern['matched_case_ids']),
            'discovery_cases_rematched': discovery_counts[pid]['match'],
            'discovery_cases_uncertain': discovery_counts[pid]['uncertain'],
            'discovery_cases_not_matched': discovery_counts[pid]['no_match'],
        }
        summary.append(row)

    # Only begin writing after validation and cross-stage audit succeeded.
    DEST.mkdir(parents=True)
    write_csv(DEST / 'pattern_occurrence_summary.csv', summary, list(summary[0]))
    detail_fields = list(observations[0]) if observations else [
        'discovered_pattern_id', 'neutral_pattern_id', 'subject',
        'transition_id', 'decision', 'classification', 'transition',
        'source_descriptor', 'destination_descriptor',
        'observed_in_stage1', 'browser_boundary', 'evidence_or_reason',
    ]
    write_csv(DEST / 'matched_transitions.csv',
              [row for row in observations if row['decision'] == 'match'], detail_fields)
    write_csv(DEST / 'uncertain_transitions.csv',
              [row for row in observations if row['decision'] == 'uncertain'], detail_fields)
    write_csv(DEST / 'discovery_consistency_audit.csv',
              discovery_audit, list(discovery_audit[0]))

    grouped = defaultdict(list)
    for row in observations:
        if row['decision'] == 'match':
            grouped[(row['discovered_pattern_id'], row['subject'])].append(row)
    for (pattern_id, subject), rows in sorted(grouped.items()):
        folder = DEST / 'patterns' / pattern_id / subject
        folder.mkdir(parents=True, exist_ok=True)
        # Raw XPath transitions only; metadata lives in matched_transitions.csv.
        (folder / 'matching_sequences.txt').write_text(
            ''.join(row['transition'] + '\n' for row in sorted(rows, key=lambda x: x['transition_id'])),
            encoding='utf-8',
        )

    print('\n' + '=' * 85)
    print('PATTERN OCCURRENCE SUMMARY (MODEL MATCHES ONLY)')
    print('=' * 85)
    print(f'{"Pattern":<8} {"TP":>4} {"FP":>4} {"FN":>4} {"TN*":>5} {"Total":>6} {"Uncert.":>8} {"Error %":>9} {"Old matched":>12}')
    for row in summary:
        rate = row['target_error_percent_of_model_matches']
        print(f'{row["pattern_id"]:<8} {row["matched_TP"]:>4} {row["matched_FP"]:>4} '
              f'{row["matched_FN"]:>4} {row["matched_TN"]:>5} '
              f'{row["total_model_matches"]:>6} {row["uncertain_decisions"]:>8} '
              f'{str(rate):>9} {row["discovery_cases_rematched"]:>4}/'
              f'{row["discovery_stage_error_memberships"]:<7}')
    print('\n* TN labels are provisional candidates, not independently verified negatives.')
    print('Error % = FP or FN (according to original pattern type) / model-matched total.')
    print('Uncertain decisions are excluded from that denominator and listed separately.')
    print('Discovery-stage memberships may disagree with refined blinded criteria.')
    print(f'\nSaved: {DEST}')
    print('Raw matching-sequence files:', len(grouped))
    print('Total matched decisions:', sum(s['total_model_matches'] for s in summary))
    print('Total uncertain decisions:', sum(s['uncertain_decisions'] for s in summary))


if __name__ == '__main__':
    main()
