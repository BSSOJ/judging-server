#!/bin/bash

# [runcode.sh]
# Runs compiled binary and compares the result with official results
#
# Usage:
#       ./runcode.sh [working_directory] [binary_name] [language] [input_file] [output_file]
#
#

if [ "$3" == "cpp" ] || [ "$3" == "cpp11" ]; then
    cat "$1/$4" | "$1/$2" > "$1/temp_output.txt"
elif [ "$2" == "java" ]; then
    cat "$1/$4" | java "$1/$2" > "$1/temp_output.txt"
fi

if diff "$1/$5" "$1/temp_output.txt" -wBb; then
    echo "AC"
else
    echo "WA"
fi