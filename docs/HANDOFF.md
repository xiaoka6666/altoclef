# Agent handoff (2026-09-27, updated)

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
- Scripts: build with `JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 gradle build -x test` on 1.16.1. Push to `1.16.1` only after `git fetch` and a rebase, because the user pushes there too.
- Bench modes on TenorClef 1.16.1: `pathbench flow - 3` runs water-climb cases (0: a 1-wide waterfall, 1: a stream down 12 steps, 2: the same steps dry). The swim bench takes the `-Dtenorclef.pathbench.swimAir` property.
- Movement benchmark: `pathbench travel <baritone|kinematic|tungsten> 3` on TenorClef 1.16.1. It writes CSVs to `versions/1.16.1/run/pathbench`.

**Kinematic controller**
- Location: Ostinato 1.16.1, `src/main/java/baritone/pathing/kinematic/`. A port also exists on main.
- What it does: it follows Baritone's route but chooses keys by rolling out `PlayerSim` (a vanilla physics copy). It drives walk, diagonal and descend/fall moves of 1–3 blocks, and hands back to Baritone for everything else.
- Enable it with the setting `kinematicTravel`.
- Last clean bench (old jar): kinematic 42/48, avg 431 ticks; baritone 46/48, avg 438.

**Recent state**
- Swim-pose climbing is on 1.16.1 and main (`MovementSwim`).
  - 1-wide waterfall, 16 blocks up: about 110 ticks, down from 180. That beats walking.
  - Stream up 12 steps: 250–440 ticks, down from 320. Dry stairs take about 100. The current's pushback caps the speed, so don't chase it further.
  - Air-management tuning gave no gain and was reverted.
- Physics pathfinder framework: `baritone/pathing/physics/PhysicsPathfinder.java` on 1.16.1.
  - It runs A* over `PlayerSim` states, with 16 yaws × {sprint, sprint+jump, walk} + idle, each held for 2 ticks. It returns a tick-by-tick key list.
  - `ClientWorld` (a PlayerSim.World adapter over the client world) and `PhysicsTravel` (an executor that plans about 6 land movements ahead and re-plans on more than 0.1 drift) are wired into `PathExecutor` behind the `physicsTravel` setting. Bench it with `pathbench travel physics`.
  - First bench (1 rep): physics reached 16/16 goals at avg 426 ticks; baritone reached 16/16 at 357. It needs tuning. Measured with temporary logging:
    - About 10% of plans fail (budget 1500 nodes). Each failure hands back to Baritone for 20 ticks.
    - About 50% of replans are drift (real vs predicted position differs by more than 0.1 after one tick). The likely cause is that the look rotation lands a tick late, so the sim ticks with a yaw the player doesn't have yet. That turned out not to be the cause; see below.
    - Average plan time is 13 ms, with spikes up to 130 ms.
    - Fixed: the drift came from vanilla's 10-tick held-jump cooldown, which PlayerSim didn't model. With it, physics reached 15/16 goals at avg 375 ticks (baritone 357) and drift replans halved.
    - Remaining drift is about 0.2 horizontally on the first airborne tick after a jump, which looks like a sprint-jump boost mismatch. Check sprint-cancel-on-collision in `PlayerSim.tick`.
    - Nearer-waypoint fallback on plan failure (retry at +2, then +1): 16/16 at 412 ticks when replanning only on reaching the waypoint; 14/16 at 519 with the rolling waypoint. Reverted. Single-rep runs are noisy, so use 3 reps before judging.
    - Tried and worse: drift 0.25 with cooldown 5 and radius 0.5 (14/16, 454 ticks); weighted A* at 1.5 (16/16, 495 ticks).
  - Next steps:
    (a), (b), (d): done.
    (c) Use Baritone's block path as the heuristic corridor, planning to a waypoint about 8 blocks ahead.
    (d) Add a `physicsTravel` setting and a `pathbench travel physics` mode.
    (e) Add swim/sneak inputs.
    (f) Port it to main and 1.21.4.

**Priority order**
1. Rebench kinematic vs baritone on the current 1.16.1 jar, with nothing else running. Find why the kinematic controller fails the goals it fails; look at handbacks and the airborne-landing checks in `rollout`. Fix them until kinematic reaches baritone's success rate while staying faster. Update the README tables (TenorClef "Benchmarking movement"). Update the results artifact if you have access; it's a claude.ai page, so ask the user for the link.
2. Add parkour jumps (MovementParkour) to the kinematic controller's `drivable()`. Search the jump tick with PlayerSim, and reject any rollout that doesn't land on the destination block.
3. Port the kinematic changes to Ostinato main and the `1.21.4` branch. Names are Mojang-mapped there; translate them as in the existing main port.
4. Runtime-test TenorClef 1.21.4 (runClient, join a world, run a travel goal). So far it has only been compile-tested.
5. New feature ("underwater door air pocket"): when underwater and low on air with no surface in reach, place a door (if one is in inventory). A placed door displaces water in its two blocks, which gives an air pocket. Path the player's head into that pocket to refill air, then break the door and pick it back up if it was the only one. Put this in Ostinato as a setting (e.g. `allowDoorAirPockets`), alongside the existing air-management and bubble-column logic, and wire TenorClef's survival or air-management chain to use it.
6. Finish the physics pathfinder (see Recent state), and bench it against baritone and kinematic.
