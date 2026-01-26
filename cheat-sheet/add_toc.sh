#!/bin/bash

# Script to add table of contents with links to H2 and H3 headers

add_toc() {
    local file="$1"
    
    # Check if file already has a table of contents
    if grep -q "## Содержание" "$file"; then
        echo "TOC already exists in $file"
        return
    fi
    
    # Extract headers and create TOC
    local toc=""
    local in_code_block=false
    
    while IFS= read -r line; do
        # Check for code blocks
        if [[ $line =~ ^\`\`\` ]]; then
            in_code_block=$(! $in_code_block)
            continue
        fi
        
        if $in_code_block; then
            continue
        fi
        
        # Extract H2 headers
        if [[ $line =~ ^##\ (.*)$ ]]; then
            local header="${BASH_REMATCH[1]}"
            local link=$(echo "$header" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-zа-я0-9]/-/g' | sed 's/--*/-/g' | sed 's/^-//' | sed 's/-$//')
            toc="${toc}- [$header](#$link)"$'\n'
        fi
        
        # Extract H3 headers
        if [[ $line =~ ^###\ (.*)$ ]]; then
            local header="${BASH_REMATCH[1]}"
            local link=$(echo "$header" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-zа-я0-9]/-/g' | sed 's/--*/-/g' | sed 's/^-//' | sed 's/-$//')
            toc="${toc}  - [$header](#$link)"$'\n'
        fi
    done < "$file"
    
    # If no headers found, skip
    if [ -z "$toc" ]; then
        echo "No headers found in $file"
        return
    fi
    
    # Create temporary file
    local temp_file=$(mktemp)
    
    # Add TOC after the first header or description
    local added_toc=false
    while IFS= read -r line; do
        echo "$line" >> "$temp_file"
        
        # Add TOC after the date line or first non-header content
        if [[ $line =~ \*\*Дата\ последнего\ обновления:\*\* ]] && [ "$added_toc" = false ]; then
            echo "" >> "$temp_file"
            echo "## Содержание" >> "$temp_file"
            echo "" >> "$temp_file"
            echo "$toc" >> "$temp_file"
            echo "" >> "$temp_file"
            added_toc=true
        fi
    done < "$file"
    
    # Replace original file
    mv "$temp_file" "$file"
    echo "Added TOC to $file"
}

# Find all markdown files and add TOC
find cheatsheets -name "*.md" -type f | while read -r file; do
    echo "Processing: $file"
    add_toc "$file"
done

echo "Done adding TOC to all files"
