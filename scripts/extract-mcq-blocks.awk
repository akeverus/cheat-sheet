# extract-mcq-blocks.awk — извлекает MCQ блоки из markdown в TSV для последующей обработки.
#
# Output (TSV): qNumber<TAB>blockIdx<TAB>optionLabel<TAB>correct<TAB>text<TAB>sectionName<TAB>sectionContent
# Один файл = одна или более записей; запускать через `awk -f extract-mcq-blocks.awk <file.md>`.

BEGIN { current_q = ""; in_mcq = 0; block_idx = 0; opt_label = ""; correct = ""; opt_text = "" }

/^## Q[0-9]+\./ {
    match($0, /^## Q([0-9]+)\./, m); current_q = m[1]; block_idx = 0; in_mcq = 0; next
}

/^> \[!mcq\]/ { in_mcq = 1; next }

in_mcq && /^> - \[[ xX]\]/ {
    match($0, /^> - \[([ xX])\] ?([A-D])\. ?(.+?)$/, m)
    correct = (m[1] == "x" || m[1] == "X") ? "true" : "false"
    opt_label = m[2]
    opt_text = m[3]
    # Strip trailing | <legacy> if present
    sub(/[[:space:]]*\|.*$/, "", opt_text)
    next
}

in_mcq && /^>[[:space:]]+\*\*[^*]+\*\*/ {
    # Section line: capture name + content
    match($0, /^>[[:space:]]+\*\*([^*]+)\*\*[[:space:]]*(.*)$/, m)
    sec_name = m[1]; gsub(/\.$/, "", sec_name)
    sec_content = m[2]
    printf "%s\t%d\t%s\t%s\t%s\t%s\t%s\n", current_q, block_idx, opt_label, correct, opt_text, sec_name, sec_content
    next
}

in_mcq && /^[[:space:]]*$/ { next }
in_mcq && !/^>/ { in_mcq = 0; block_idx++; next }
