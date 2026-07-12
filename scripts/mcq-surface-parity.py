#!/usr/bin/env python3
"""Surface-parity scanner for MCQ seeders (docs/mcq-quality/surface-parity-rules.md).
Measures FORM only: length-CV, corr_longest, backtick bucket parity, lone-feature tells,
opening-class parity. Does NOT judge meaning. Exit 0 always — a reporting tool, not a gate.

Usage:
  mcq-surface-parity.py --file <json>     per-block breakdown
  mcq-surface-parity.py --rank [dir]      worst-first over all seeders (default seed/mcq root)
"""
import json, re, sys, glob, os, statistics

ROOT = "modules/quiz-app/src/main/resources/seed/mcq"

def feats(t):
    return dict(
        L=len(t),
        bt=t.count("`") // 2,
        semi=t.count(";"),
        colon=t.count(":"),
        num=len(re.findall(r"\d\)", t)),
        arrow=t.count("→"),
        openbt=1 if t.lstrip().startswith("`") else 0,
    )

def bucket(n):
    return 0 if n == 0 else (1 if n <= 2 else 2)

def block_report(opts):
    F = [feats(o["text"]) for o in opts]
    ci = [i for i, o in enumerate(opts) if o["correct"]][0]
    lens = [f["L"] for f in F]
    cv = statistics.pstdev(lens) / statistics.mean(lens) if statistics.mean(lens) else 0
    ratio = max(lens) / min(lens) if min(lens) else 0
    corr_longest = lens[ci] == max(lens)
    tells = []
    # lone binary/count features: correct uniquely has, or uniquely lacks
    c = F[ci]; others = [F[i] for i in range(len(F)) if i != ci]
    for k in ("bt", "semi", "colon", "num", "arrow", "openbt"):
        cv_ = c[k]; ov = [o[k] for o in others]
        if cv_ > 0 and all(o == 0 for o in ov):
            tells.append("+" + k)
        if cv_ == 0 and all(o > 0 for o in ov):
            tells.append("-" + k)
    # backtick bucket spread
    bks = [bucket(f["bt"]) for f in F]
    if max(bks) - min(bks) >= 1:
        tells.append(f"btbucket{min(bks)}-{max(bks)}")
    if corr_longest and ratio > 1.25:
        tells.append(f"lone_long{ratio:.2f}")
    # opening-class parity
    if len({f["openbt"] for f in F}) > 1:
        tells.append("open_mixed")
    return cv, ratio, corr_longest, tells, lens, ci

def scan_file(p):
    d = json.load(open(p))
    worst = 0.0
    per = []
    for q in d["questions"]:
        for b in q["blocks"]:
            cv, ratio, cl, tells, lens, ci = block_report(b["options"])
            score = len(tells) + (cv * 2)
            worst = max(worst, score)
            per.append((q["q_number"], b["block_idx"], cv, ratio, cl, tells, lens, ci, score))
    return per, worst

def main():
    args = sys.argv[1:]
    if "--file" in args:
        p = args[args.index("--file") + 1]
        per, worst = scan_file(p)
        print(f"{p}   worst-block-score={worst:.2f}")
        for qn, bi, cv, ratio, cl, tells, lens, ci, sc in sorted(per, key=lambda x: -x[8]):
            flag = "  <== TELL" if tells or ratio > 1.25 else ""
            print(f" Q{qn} b{bi} cv={cv:.3f} ratio={ratio:.2f} corr_longest={cl} "
                  f"lens={lens} corr=#{ci} tells={tells}{flag}")
        return
    # --rank
    base = ROOT
    if "--rank" in args:
        idx = args.index("--rank")
        if idx + 1 < len(args) and not args[idx + 1].startswith("-"):
            base = args[idx + 1]
    files = glob.glob(os.path.join(base, "**", "*.json"), recursive=True)
    rows = []
    for p in files:
        try:
            per, worst = scan_file(p)
        except Exception:
            continue
        mean = statistics.mean([x[8] for x in per]) if per else 0
        n_tell = sum(1 for x in per if x[5] or x[3] > 1.25)
        rows.append((worst, mean, n_tell, len(per), os.path.relpath(p, base)))
    rows.sort(reverse=True)
    print(f"{'worst':>6} {'mean':>5} {'tellblk':>7} {'blk':>4}  file")
    for worst, mean, nt, nb, rel in rows[:60]:
        print(f"{worst:6.2f} {mean:5.2f} {nt:7} {nb:4}  {rel}")

if __name__ == "__main__":
    main()
