#!/bin/bash

g++ -o judge judge.cpp -std=c++11 -O2
g++ -o runcode runcode.cpp -std=c++11 -O2
g++ -o compile compile.cpp -std=c++11 -O2

rm /usr/bin/judge
rm /usr/bin/runcode
rm /usr/bin/compile

ln judge /usr/bin/judge
ln runcode /usr/bin/runcode
ln compile /usr/bin/compile