#!/usr/bin/env python3
"""extract_mermaid.py <pre_ref> <relpath-under-cheatsheets/interview-without-.md>

Печатает все mermaid-блоки, существовавшие в файле на коммите <pre_ref>
(состояние ДО LANE 8A), вместе с контекстом: под каким `## Q<N>` / под-заголовком
блок жил и какая проза была перед ним. Нужно агенту-реставратору, чтобы понять
ЧТО за диаграмму восстанавливать и КУДА вставлять в текущей версии файла.

Вывод — человекочитаемый отчёт по блокам:
  ### BLOCK <i>  | heading: <ближайший ## или ### заголовок выше>
  --- preceding prose (до 4 непустых строк) ---
  ...
  --- mermaid source ---
  ```mermaid
  ...
  ```
"""
import subprocess
import sys


def git_show(ref, path):
    r = subprocess.run(["git", "show", f"{ref}:{path}"],
                       capture_output=True, text=True)
    if r.returncode != 0:
        sys.stderr.write(r.stderr)
        sys.exit(2)
    return r.stdout.splitlines()


def main():
    if len(sys.argv) != 3:
        sys.stderr.write("usage: extract_mermaid.py <pre_ref> <rel-without-.md>\n")
        sys.exit(1)
    ref = sys.argv[1]
    rel = sys.argv[2]
    path = f"cheatsheets/interview/{rel}.md"
    lines = git_show(ref, path)

    blocks = []
    i = 0
    cur_q = None        # ближайший ## Q-заголовок
    cur_sub = None      # ближайший ### под-заголовок
    in_fence = False
    fence_lang = None
    while i < len(lines):
        ln = lines[i]
        stripped = ln.strip()
        # отслеживаем заголовки только вне fence
        if not in_fence:
            if ln.startswith("## "):
                cur_q = ln[3:].strip()
                cur_sub = None
            elif ln.startswith("### "):
                cur_sub = ln[4:].strip()
        # fence-трекинг
        if stripped.startswith("```"):
            if not in_fence:
                in_fence = True
                fence_lang = stripped[3:].strip().lower()
                if fence_lang == "mermaid":
                    # собрать тело
                    body = []
                    j = i + 1
                    while j < len(lines) and not lines[j].strip().startswith("```"):
                        body.append(lines[j])
                        j += 1
                    # проза перед блоком: до 4 непустых строк назад
                    prose = []
                    k = i - 1
                    while k >= 0 and len(prose) < 6:
                        t = lines[k].strip()
                        if t.startswith("```"):
                            break
                        if t:
                            prose.append(lines[k])
                        if t.startswith("## ") or t.startswith("### "):
                            break
                        k -= 1
                    prose.reverse()
                    blocks.append({
                        "q": cur_q, "sub": cur_sub,
                        "prose": prose, "body": body,
                    })
                    i = j  # перейти к закрывающему ```
                    in_fence = False
                    fence_lang = None
                    i += 1
                    continue
            else:
                in_fence = False
                fence_lang = None
        i += 1

    print(f"FILE: {path}")
    print(f"TOTAL mermaid blocks at {ref}: {len(blocks)}")
    print("=" * 70)
    for idx, b in enumerate(blocks, 1):
        print(f"\n### BLOCK {idx}  | Q-heading: {b['q']}"
              + (f"  | sub: {b['sub']}" if b['sub'] else ""))
        print("--- preceding prose ---")
        for p in b["prose"]:
            print(p)
        print("--- mermaid source ---")
        print("```mermaid")
        for x in b["body"]:
            print(x)
        print("```")
    print("\n" + "=" * 70)
    print(f"END ({len(blocks)} blocks)")


if __name__ == "__main__":
    main()
