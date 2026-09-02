#!/usr/bin/env python3
"""Сводит результаты Workflow из journal.jsonl. Тела в stdout НЕ печатаются — только сводка.
  collect.py blocks   <out.json> <journal...>
  collect.py verdicts <out.json> <journal...>
  collect.py notes    <verdicts.json> [regex]
"""
import json, sys, re
KEEP_C = {"explanation", "example", "when_to_apply", "edge_cases", "related"}
KEEP_W = {"what_actually", "source_of_confusion", "if_it_were_true", "how_it_should_be"}

def load(journals, key):
    by = {}
    for p in journals:
        for line in open(p, encoding="utf-8"):
            o = json.loads(line)
            if o.get("type") == "result":
                for x in (o.get("result") or {}).get(key, []): by[(x["q_number"], x.get("block_idx", 0))] = x
    return by

mode = sys.argv[1]
if mode == "notes":
    V = json.load(open(sys.argv[2], encoding="utf-8"))
    rx = re.compile(sys.argv[3] if len(sys.argv) > 3 else
                    r"дословн|придирк|мелоч|слаб|спорн|натян|риск|нюанс|однако|второй защитимый|смещ|под-?ос|длиннее")
    hits = [v for v in V if rx.search(v["note"])]
    for v in hits:
        print(f"--- Q{v['q_number']} [{v['linkage_level']}] axis={v['same_axis_all_four']}")
        print("   ", v["note"][:500], "\n")
    print(f"нот с оговорками: {len(hits)} из {len(V)}")
    sys.exit()

out, journals = sys.argv[2], sys.argv[3:]
if mode == "blocks":
    by = load(journals, "blocks")
    for b in by.values():
        b["correct_sections"] = {k: v for k, v in b["correct_sections"].items() if k in KEEP_C}
        for w in b["wrong"]:
            for k in list(w):
                if k not in KEEP_W | {"text", "axis"}: del w[k]
    items = [by[n] for n in sorted(by)]
    json.dump({"blocks": items}, open(out, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    got = sorted(by)
    print(f"блоков: {len(items)}" + (f" | Q{got[0]}..Q{got[-1]}" if got else ""))
    qs = sorted({k[0] if isinstance(k, tuple) else k for k in by})
    print("пропуски:", [n for n in range(1, (qs[-1] if qs else 0) + 1) if n not in qs] or "нет")
    print("не 3 дистрактора:", [n for n, b in by.items() if len(b["wrong"]) != 3] or "нет")
else:
    by = load(journals, "verdicts")
    items = [by[n] for n in sorted(by)]
    json.dump(items, open(out, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    lv = {}
    for v in items: lv[v["linkage_level"]] = lv.get(v["linkage_level"], 0) + 1
    print(f"вердиктов: {len(items)} | linkage: {lv}")
    print("novel:", [v["q_number"] for v in items if v["novel_fact_required"]] or "нет")
    print("НЕ опровержимы:", [v["q_number"] for v in items if not v["all_wrong_refutable_from_this_Q"]] or "нет")
    print("same_axis=false:", [v["q_number"] for v in items if not v["same_axis_all_four"]] or "нет")
