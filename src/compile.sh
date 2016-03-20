#!/bin/bash

# [compile.sh]
# Compiles the program based on the language selected
#
# Usage:
#       ./compile.sh [source_file] [language] [output_dir]
#
#

if [ "$2" == "cpp" ]; then
    if g++ -O2 -o "$3/main" "$1" 2> /dev/null; then
        return 0
    else
        return 1
    fi

elif [ "$2" == "cpp11" ]; then
    if g++ -std=c++11 -O2 -o "$3/main" "$1" 2> /dev/null; then
        return 0
    else
        return 1
    fi
elif [ "$2" == "java" ]; then
    if javac "$1" -d "$3" 2> /dev/null; then
        return 0
    else
        return 1
    fi
fi