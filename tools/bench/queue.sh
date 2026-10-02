cd $(cd "$(dirname "$0")/../.." && pwd)
S=versions/1.16.1/run/altoclef/altoclef_settings.json
D=versions/1.16.1/run/pathbench
run() { # label extra mover
  pkill -9 java; sleep 3
  sed -i "s/\"pathbench travel [^\"]* 1\"/\"pathbench travel ${3:-guided} 1\"/" $S
  start=$(date +%s)
  ( export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Daltoclef.seed=12345 -Daltoclef.warp=${WARP:-8} -Daltoclef.pathbench.exit=true $2"; xvfb-run -a -s "-screen 0 640x360x24" ./gradlew --offline :1.16.1:runClient >/dev/null 2>&1 ) &
  sleep 60
  while [ $(( $(date +%s) - start )) -lt 2400 ]; do
    f=$(find $D -name "pathbench_travel_${3:-guided}_*" -newermt "@$start" | sort | tail -1)
    [ -n "$f" ] && [ $(wc -l < "$f") -ge 17 ] && break
    sleep 20
  done
  pkill -9 java; echo "== $1: $f"; cat "$f"
}
run baritone "" baritone
sed -i 's/"pathbench travel [^"]* 1"/"pathbench travel tungsten 1"/' $S
