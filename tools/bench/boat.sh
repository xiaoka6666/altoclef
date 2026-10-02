cd $(cd "$(dirname "$0")/../.." && pwd)
S=versions/1.16.1/run/altoclef/altoclef_settings.json
D=versions/1.16.1/run/pathbench
pkill -9 java; sleep 3
sed -i 's/"pathbench travel baritone 1"/"pathbench boat - 3"/' $S
start=$(date +%s)
( export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Daltoclef.seed=12345 -Daltoclef.warp=4 -Daltoclef.pathbench.exit=true"; xvfb-run -a -s "-screen 0 640x360x24" ./gradlew --offline :1.16.1:runClient > ${OUT:-/tmp/bench}/boat.log 2>&1 ) &
sleep 60
while [ $(( $(date +%s) - start )) -lt 1500 ]; do
  f=$(find $D -name "pathbench_boat_*" -newermt "@$start" | sort | tail -1)
  [ -n "$f" ] && [ $(wc -l < "$f") -ge 7 ] && break
  sleep 20
done
pkill -9 java; sed -i 's/"pathbench boat - 3"/"pathbench travel baritone 1"/' $S
echo "== $f"; cat "$f"; grep -a "error:\|Boat\|PATHBENCH" ${OUT:-/tmp/bench}/boat.log | tail -40
