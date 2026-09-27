# Runs each bench in turn: "mode|command|min csv lines|warp"
cd $(cd "$(dirname "$0")/../.." && pwd); S=versions/1.16.1/run/altoclef/altoclef_settings.json; D=versions/1.16.1/run/pathbench
O=${OUT:-/tmp/bench}
for b in "swim|pathbench swim - 2|17|8" "wreck|pathbench wreck - 5|6|8" "column|pathbench column - 3|4|8" "flow|pathbench flow - 2|5|8" "boat|pathbench boat - 3|7|8" "cliff|pathbench cliff - 2|17|4"; do
  IFS='|' read m cmd n w <<<"$b"
  pkill -9 java; sleep 3
  sed -i "s/\"autoRunCommand\" : \"[^\"]*\"/\"autoRunCommand\" : \"$cmd\"/" $S
  start=$(date +%s)
  ( export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Dtenorclef.seed=12345 -Dtenorclef.warp=$w -Dtenorclef.pathbench.exit=true"; xvfb-run -a -s "-screen 0 640x360x24" ./gradlew --offline :1.16.1:runClient > $O/suite_$m.log 2>&1 ) &
  sleep 60; f=
  while [ $(( $(date +%s) - start )) -lt 3000 ]; do
    f=$(find $D -name "pathbench_${m}_*" -newermt "@$start" | sort | tail -1)
    [ -n "$f" ] && [ $(wc -l < "$f") -ge $n ] && break
    grep -q "PATHBENCH exit requested\|Error executing task on Server" $O/suite_$m.log && sleep 10 && break
    sleep 20
  done
  pkill -9 java; echo "== $m: $f"; [ -n "$f" ] && cat "$f"
done
sed -i 's/"autoRunCommand" : "[^"]*"/"autoRunCommand" : "pathbench travel baritone 1"/' $S
echo "== SUITE DONE"
