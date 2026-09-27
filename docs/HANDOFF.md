# Agent handoff (2026-09-27)

Paste the section below into a new agent session. The session should have the repos vexrypt-rgb/TenorClef and vexrypt-rgb/Ostinato.

---

You are continuing work on TenorClef (an AltoClef fork, preprocessor multi-version, repo vexrypt-rgb/TenorClef) and Ostinato (a Baritone fork, repo vexrypt-rgb/Ostinato).

**Rules**
- Always commit and push. Leave no debug code in commits.
- Branches:
  - TenorClef and Ostinato main: `claude/minecraft-bot-claude-tokens-a1pjwx`.
  - Ostinato version branches: `1.16.1` and `1.21.4`.
- Never run a gradle build while a benchmark is running. It runs out of memory and ruins the results.

**Layout**
- Ostinato branch `1.16.1` (Java 8, gradle 4.9):
  - Build with `gradle build -x test`.
  - Copy `build/libs/baritone-unoptimized-fabric-1.16.5.jar` to `TenorClef/libs/baritone-unoptimized-fabric-1.16.1.jar`, then force-add and commit it.
- Ostinato main (MC 1.21.11, Java 21):
  - Build with `./gradlew :fabric:compileJava`.
  - Tests: `./gradlew test --tests '*PlayerSimTest*'`.
- Ostinato branch `1.21.4`:
  - A backport of main. It builds with `./gradlew :fabric:build -x test`.
  - Copy `fabric/build/libs/baritone-unoptimized-fabric-*.jar` to `TenorClef/libs/baritone-unoptimized-fabric-1.21.4.jar` and force-add it (libs/ is gitignored).
- TenorClef:
  - Main source is 1.21.1; version nodes are defined in `root.gradle.kts`.
  - The `:1.21.4` module compiles (`./gradlew :1.21.4:compileJava`).
  - API changes from 1.21.2 on are guarded with `//#if MC >= 12102`.
  - On 1.21.2+, the vanilla recipe tracker and recipe book are stubbed out, because servers no longer sync recipes to the client.
- Movement benchmark: `pathbench travel <baritone|kinematic|tungsten> 3` on TenorClef 1.16.1. It writes CSVs to `versions/1.16.1/run/pathbench`.

**Kinematic controller**
- Location: Ostinato 1.16.1, `src/main/java/baritone/pathing/kinematic/`. A port also exists on main.
- What it does: it follows Baritone's route but chooses keys by rolling out `PlayerSim` (a vanilla physics copy). It drives walk, diagonal and descend/fall moves of 1–3 blocks, and hands back to Baritone for everything else.
- Enable it with the setting `kinematicTravel`.
- Last clean bench (old jar): kinematic 42/48, avg 431 ticks; baritone 46/48, avg 438.

**Priority order**
1. Rebench kinematic vs baritone on the current 1.16.1 jar, with nothing else running. Find why the kinematic controller fails the goals it fails; look at handbacks and the airborne-landing checks in `rollout`. Fix them until kinematic reaches baritone's success rate while staying faster. Update the README tables (TenorClef "Benchmarking movement"). Update the results artifact if you have access; it's a claude.ai page, so ask the user for the link.
2. Add parkour jumps (MovementParkour) to the kinematic controller's `drivable()`. Search the jump tick with PlayerSim, and reject any rollout that doesn't land on the destination block.
3. Port the kinematic changes to Ostinato main and the `1.21.4` branch. Names are Mojang-mapped there; translate them as in the existing main port.
4. Runtime-test TenorClef 1.21.4 (runClient, join a world, run a travel goal). So far it has only been compile-tested.
5. New feature ("underwater door air pocket"): when underwater and low on air with no surface in reach, place a door (if one is in inventory). A placed door displaces water in its two blocks, which gives an air pocket. Path the player's head into that pocket to refill air, then break the door and pick it back up if it was the only one. Put this in Ostinato as a setting (e.g. `allowDoorAirPockets`), alongside the existing air-management and bubble-column logic, and wire TenorClef's survival or air-management chain to use it.
6. Later: a faithful Tungsten-style physics pathfinder ("option 2").
