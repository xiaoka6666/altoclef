# TenorClef 0.22.1

Every build runs on [Ostinato](https://github.com/vexrypt-rgb/Ostinato) (the Baritone fork), not upstream Baritone.
Fixes on top of 0.22.0.

## Downloads

| Minecraft | TenorClef jar | Ostinato |
|---|---|---|
| 1.21.4 | `tenorclef-mc1.21.4-v0.22.1.jar` | Ostinato v1.0.1 `ostinato-mc1.21.4-*.jar` |
| 1.16.1 | `tenorclef-mc1.16.1-v0.22.1.jar` | Ostinato v1.0.1 `ostinato-mc1.16.1-*.jar` |

Install Fabric Loader, Fabric API, the TenorClef jar and the matching Ostinato jar. Do not add a second Baritone jar.

## Fixes

- Dive: the bot sinks under a roof edge instead of jumping into it, and no longer freezes against it.
- Swimming out onto a shallow shore ledge climbs it instead of sinking.
- Chat logs a "swim stall" line when a swim movement is stuck, to make reports easier to diagnose.

# TenorClef 0.22.0

## Highlights

- Freecam: `#freecam` with a GUI toggle, a ghost of the bot showing its current task, and an opacity slider. Includes fixes for respawn and a ghost mixin crash.
- Showcase command and tab: parkour, swim, dive, boat, kinematic and physics demos, with a sealed pool corridor so the bot has to swim.
- Movement fixes from the bundled Ostinato jars: swim entry off shallow ledges, waterfall swimming, boat placement, robust jump templates, scissor underflow.
- Starts on quick-play worlds; 1.21.4 GUI and dropdown fixes.

## Not included

- 1.21.11 is experimental and 26.3 is not supported yet; neither is part of this release.
