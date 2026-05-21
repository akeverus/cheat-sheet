# extract-mcq-blocks.awk — извлекает MCQ блоки из markdown в TSV для последующей обработки.
#
# Output (TSV): qNumber<TAB>blockIdx<TAB>optionLabel<TAB>correct<TAB>text<TAB>sectionName<TAB>sectionContent
# Один файл = одна или более записей; запускать через `awk -f extract-mcq-blocks.awk <file.md>`.

BEGIN { current_q = ""; in_mcq = 0; block_idx = 0; opt_label = ""; correct = ""; opt_text = "" }

/^## Q[0-9]+\./ {
    match($0, /^## Q([0-9]+)\.[[:space:]]*(.*)$/, m)
    current_q = m[1]
    current_q_title = m[2]
    # Strip (!) marker and trailing whitespace
    gsub(/\(!\)/, "", current_q_title)
    gsub(/^[[:space:]]+|[[:space:]]+$/, "", current_q_title)
    block_idx = 0
    in_mcq = 0
    # Emit a meta-row so downstream knows the question text
    # Format: q_number\t-1\t-\t-\tQTITLE\t-\t<title>
    printf "%s\t-1\t-\t-\tQTITLE\t-\t%s\n", current_q, current_q_title
    next
}

/^> \[!mcq\]/ {
    # If we were already inside an MCQ block, this is a new sibling block → bump idx.
    if (in_mcq) block_idx++
    in_mcq = 1
    # Reset per-block state so leftover label/text from a previous block doesn't leak
    # into a new (possibly legacy-format) MCQ that has no `> - [x] A.` markers.
    opt_label = ""; correct = ""; opt_text = ""
    next
}

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
    sec_name = m[1]
    # Strip trailing punctuation (. : ;) and whitespace
    gsub(/[[:space:]]*[.:;]+[[:space:]]*$/, "", sec_name)
    sec_content = m[2]
    printf "%s\t%d\t%s\t%s\t%s\t%s\t%s\n", current_q, block_idx, opt_label, correct, opt_text, sec_name, sec_content
    next
}

in_mcq && /^[[:space:]]*$/ { next }
in_mcq && !/^>/ { in_mcq = 0; block_idx++; next }
