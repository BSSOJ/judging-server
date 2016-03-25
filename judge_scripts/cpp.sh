#!/bin/bash

# Compile script for C++ (no C++11 support)
#
#   ./cpp.sh [working_directory] [source_file]
#
# Returns:
#   0: AC (Accepted)
#   1: WA (Wrong Answer)
#   2: RE (Runtime Error)
#   3: TLE (Time Limit Exceeded)
#   4: MLE (Memory Limit Exceeded)
#   5: OLE (Output Limit Exceeded)
#   6: CE (Compile Error)
#   7: IR (Internal Error)
#

# Compile the file
g++ -o "$1main" -std=c++98 -O2 "$1$2"

if [ "$?" != 0 ]; then
    return 6
fi

# Run the file
CMD = "LD_PRELOAD=./EasySandbox.so $1main"
CMD < "$1input.txt" > "$1temp_output.txt" 2> "$1stderr.txt"

if [ "$?" != 0 ]; then
    return 2
fi

# Compare the results from the run
diff -wBb "$1output.txt" "$1temp_output.txt"

if [ "$?" == 0 ]; then
    return 0
elsec
    return 1
fi