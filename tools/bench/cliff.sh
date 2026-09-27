cd $(cd "$(dirname "$0")/../.." && pwd)
S=versions/1.16.1/run/altoclef/altoclef_settings.json
D=versions/1.16.1/run/pathbench
pkill -9 java; sleep 3
sed -i 's/"pathbench travel baritone 1"/"pathbench cliff - 2"/' $S
start=$(date +%s)
( export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Dtenorclef.seed=12345 -Dtenorclef.warp=4 -Dtenorclef.pathbench.exit=true"; xvfb-run -a -s "-screen 0 640x360x24" ./gradlew --offline :1.16.1:runClient > ${OUT:-/tmp/bench}/cliff.log 2>&1 ) &
sleep 60
while [ $(( $(date +%s) - start )) -lt 3300 ]; do
  f=$(find $D -name "pathbench_cliff_*" -newermt "@$start" | sort | tail -1)
  [ -n "$f" ] && [ $(wc -l < "$f") -ge 17 ] && break
  sleep 20
done
pkill -9 java; sed -i 's/"pathbench cliff - 2"/"pathbench travel baritone 1"/' $S
echo "== $f"; cat "$f"; grep -a "error:\|Boat\|PATHBENCH\|failed" ${OUT:-/tmp/bench}/cliff.log | tail -40
