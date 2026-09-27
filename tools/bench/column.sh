cd $(cd "$(dirname "$0")/../.." && pwd); D=versions/1.16.1/run/pathbench; start=$(date +%s)
sed -i 's/"pathbench [^"]*"/"pathbench column - 3"/' $(cd "$(dirname "$0")/../.." && pwd)/versions/1.16.1/run/altoclef/altoclef_settings.json
( export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Dtenorclef.seed=12345 -Dtenorclef.warp=${WARP:-8} ${XOPTS} -Dtenorclef.pathbench.exit=true"; xvfb-run -a -s "-screen 0 640x360x24" ./gradlew --offline :1.16.1:runClient >/dev/null 2>&1 ) &
sleep 60
while [ $(( $(date +%s) - start )) -lt 1500 ]; do
  f=$(find $D -name "pathbench_column_*" -newermt "@$start" | tail -1)
  [ -n "$f" ] && [ $(wc -l < "$f") -ge 4 ] && break; grep -aq "SUMMARY mode=column" versions/1.16.1/run/logs/latest.log && break; sleep 15
done
pkill -9 java; cat "$f"; grep -a "PATHBENCH" versions/1.16.1/run/logs/latest.log | grep -v "XX" | tail -12
