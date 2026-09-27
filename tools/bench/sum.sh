#!/bin/bash
# Compact summary of the latest wreck run: CSV rows + top-5 Baritone message kinds (numbers collapsed) + deaths.
R=$(cd "$(dirname "$0")/../.." && pwd)/versions/1.16.1/run; L=$R/logs/latest.log
f=$(ls -t $R/pathbench/pathbench_wreck_* | head -1); tail -n +2 "$f" | cut -d, -f1,6,7,8,10 | tr '\n' ' '; echo
grep -a "Baritone\]" $L | cut -c60-170 | grep -v "Favoring\|coefficient\|loaded chunks\|movements considered\|ng size\|search for path\|Finished finding\|surfacing\|Planning ahead\|cached region" | sed -E 's/-?[0-9]+(\.[0-9]+)?/N/g' | sort | uniq -c | sort -rn | head -${1:-5}
grep -ac "\[CHAT\] Player.*\(drown\|kill\|slain\|impaled\)" $L | sed 's/^/deaths=/'
