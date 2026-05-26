"""Rebalance MCQ JSON file rotation by swapping correct/text/sections between option positions.

Preserves order/label per option. Only swaps text+correct+sections content.
Target: rotation ratio max/min <= 2 (or <= 4/N options used if fewer than 4 used).
"""
import json
import sys
import collections
import argparse


def compute_rotation(data):
    pos = collections.Counter()
    for q in data['questions']:
        for b in q['blocks']:
            for o in b['options']:
                if o['correct']:
                    pos[o['label']] += 1
    return pos


def ratio(pos):
    vals = [v for v in pos.values() if v > 0]
    if not vals:
        return 0
    return max(vals) / max(min(vals), 1)


def rebalance(file_path, target_ratio=2.0):
    with open(file_path) as f:
        data = json.load(f)

    before = compute_rotation(data)
    before_ratio = ratio(before)
    if before_ratio <= target_ratio:
        print(f"  {file_path.split('/')[-1]}: already balanced (ratio {before_ratio:.2f})")
        return

    # Build flat list of (q_idx, block_idx) and current correct labels
    locations = []
    for qi, q in enumerate(data['questions']):
        for bi, b in enumerate(q['blocks']):
            correct_label = next(o['label'] for o in b['options'] if o['correct'])
            locations.append((qi, bi, correct_label))

    total = len(locations)
    labels_used = set(label for _, _, label in locations)
    n_labels = max(len(labels_used), 4)  # at least 4 since schema requires 4 options
    target_per = total // 4  # at least target_per for each of A,B,C,D
    remainder = total - target_per * 4
    # Distribute remainder: target counts
    target_counts = {'A': target_per, 'B': target_per, 'C': target_per, 'D': target_per}
    # Give remainder to less-used labels first (just for symmetry)
    for label in 'ABCD':
        if remainder <= 0:
            break
        target_counts[label] += 1
        remainder -= 1

    # Greedy: for each over-represented label, swap one of its locations to under-represented
    current = dict(before)
    for label in 'ABCD':
        current.setdefault(label, 0)

    swaps_done = 0
    safety_iters = 0
    max_iters = total * 4
    while ratio(current) > target_ratio and safety_iters < max_iters:
        safety_iters += 1
        # Find over-represented and under-represented labels
        over_label = max(current, key=lambda k: current[k])
        under_label = min(current, key=lambda k: current[k])
        if current[over_label] <= target_counts[over_label] or current[under_label] >= target_counts[under_label]:
            # Can't improve with this pair, try next-most under
            sorted_under = sorted(current, key=lambda k: current[k])
            improved = False
            for u in sorted_under:
                if u == over_label:
                    continue
                if current[u] < target_counts[u]:
                    under_label = u
                    improved = True
                    break
            if not improved:
                break

        # Find a question where correct is on over_label
        for li, (qi, bi, c_label) in enumerate(locations):
            if c_label != over_label:
                continue
            block = data['questions'][qi]['blocks'][bi]
            # Find the option with over_label (currently correct) and under_label (currently wrong)
            opt_over = next(o for o in block['options'] if o['label'] == over_label)
            opt_under = next(o for o in block['options'] if o['label'] == under_label)
            # Swap text, correct, sections
            opt_over['text'], opt_under['text'] = opt_under['text'], opt_over['text']
            opt_over['correct'], opt_under['correct'] = opt_under['correct'], opt_over['correct']
            opt_over['sections'], opt_under['sections'] = opt_under['sections'], opt_over['sections']
            # Update tracking
            current[over_label] -= 1
            current[under_label] += 1
            locations[li] = (qi, bi, under_label)
            swaps_done += 1
            break
        else:
            # No more swappable locations on over_label
            break

    after = compute_rotation(data)
    after_ratio = ratio(after)

    with open(file_path, 'w') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    name = file_path.split('/')[-1]
    print(f"  {name}: {dict(before)} (ratio {before_ratio:.2f}) → {dict(after)} (ratio {after_ratio:.2f}), {swaps_done} swaps")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('files', nargs='+')
    parser.add_argument('--target', type=float, default=2.0)
    args = parser.parse_args()
    for f in args.files:
        rebalance(f, args.target)
