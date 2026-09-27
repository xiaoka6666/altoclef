# Bench tooling

Scripts that drive TenorClef's `pathbench` command (`src/main/java/.../PathBench.java`)
against the Baritone jar in `libs/`, plus a standalone elytra bench for Ostinato main.

Layout expected: `TenorClef/` and `Ostinato/` (1.21.11) side by side; an `Ostinato` 1.16.1
checkout builds the jar staged at `libs/baritone-unoptimized-fabric-1.16.1.jar`.

## 1.16.1 benches (xvfb, headless)

    OUT=/tmp/bench bash tools/bench/suite.sh     # swim, wreck, column, flow, boat, cliff
    OUT=/tmp/bench bash tools/bench/swim.sh      # one bench; see each script

Each script rewrites `autoRunCommand` in `versions/1.16.1/run/altoclef/altoclef_settings.json`,
launches `./gradlew --offline :1.16.1:runClient` with `-Dtenorclef.seed=12345 -Dtenorclef.warp=N
-Dtenorclef.pathbench.exit=true`, waits for the CSV in `versions/1.16.1/run/pathbench/`, then
prints it. `suite.sh` runs `pkill -9 java` between benches: don't run anything else Java at the time.
`sum.sh` gives a compact summary of the latest wreck run.

Rebuild the jar (Java 8, Gradle 4.9) in the 1.16.1 checkout:

    JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 gradle build -x test
    cp build/libs/baritone-unoptimized-fabric-1.16.5.jar ../TenorClef/libs/baritone-unoptimized-fabric-1.16.1.jar

## Elytra bench (1.21.11, Ostinato main)

TenorClef's 1.21.11 target doesn't compile yet, so `elytrabench/` is its own Fabric mod.

1. Build Ostinato main: `./gradlew :fabric:remapJar`, copy
   `fabric/build/libs/baritone-fabric-*.jar` to `Ostinato/dist/baritone-unoptimized-fabric-ostinato-1.21.11.jar`
   (or pass `-DostinatoJar=path`).
2. Make a world named `bench` in `elytrabench/run/saves/` (e.g. run the vanilla 1.21.11 server once
   with `level-seed=12345` and copy its `world/`).
3. In `run/options.txt` set `onboardAccessibility:false` (the onboarding screen blocks quick play)
   and a low `renderDistance`.
4. `xvfb-run -a -s "-screen 0 640x360x24" ./gradlew runClient`

The bench equips an elytra and 64 rockets at y=200, flies to four goals 1000-1700 blocks away with
`getElytraProcess().pathTo`, and writes `run/pathbench/pathbench_elytra_*.csv`
(result, ticks, rockets used, health). Grep the log for `ELYTRABENCH`.
