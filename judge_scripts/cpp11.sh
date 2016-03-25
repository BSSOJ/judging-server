#!/bin/bash

# Compile script for C++11
#
#   ./cpp11.sh [working_directory] [source_file]
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

cd $1

# Compile the file
g++ -o "main" -std=c++11 -O2 "$2"

if [ "$?" != 0 ]; then
    return 6
fi

# Run the file
poermis
./main < "input.txt" > "temp_output.txt" 2> "stderr.txt"

if [ "$?" != 0 ]; then
    return 2
fi

# Compare the results from the run
diff -wBb "output.txt" "temp_output.txt"

if [ "$?" == 0 ]; then
    return 0
else
    return 1
fi