# TenorClef 0.22.0

Every build runs on [Ostinato](https://github.com/vexrypt-rgb/Ostinato) (the Baritone fork), not upstream Baritone.
This release rolls up everything since 0.20.0 (the 0.21.0 tag was never published).

## Downloads

| Minecraft | TenorClef jar | Ostinato |
|---|---|---|
| 1.21.4 | `tenorclef-mc1.21.4-v0.22.0.jar` | Ostinato v1.0.1 `ostinato-mc1.21.4-*.jar` |
| 1.16.1 | `tenorclef-mc1.16.1-v0.22.0.jar` | Ostinato v1.0.1 `ostinato-mc1.16.1-*.jar` |

Install Fabric Loader, Fabric API, the TenorClef jar and the matching Ostinato jar. Do not add a second Baritone jar.

## Highlights

- Freecam: `#freecam` with a GUI toggle, a ghost of the bot showing its current task, and an opacity slider. Includes fixes for respawn and a ghost mixin crash.
- Showcase command and tab: parkour, swim, dive, boat, kinematic and physics demos, with a sealed pool corridor so the bot has to swim.
- Movement fixes from the bundled Ostinato jars: swim entry off shallow ledges, waterfall swimming, boat placement, robust jump templates, scissor underflow.
- Starts on quick-play worlds; 1.21.4 GUI and dropdown fixes.

## Not included

- 1.21.11 is experimental and 26.3 is not supported yet; neither is part of this release.
