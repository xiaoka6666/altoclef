Place remapped Tungsten Fabric jars here. Gradle picks them up for the compiled
modules that support Tungsten (`1.21.4`, experimental `1.21.11`, and the
preprocess-only 1.21 / 1.21.1 steps).

Ostinato Baritone jars (preferred):

- Prefer `../Ostinato/dist` after building the matching Ostinato branch. See
  [docs/OSTINATO_WIRING.md](../docs/OSTINATO_WIRING.md).
- `baritone-unoptimized-fabric-1.21.4.jar` — Ostinato `main` (Minecraft 1.21.4,
  Java 21). Primary pairing.
- `baritone-unoptimized-fabric-ostinato-1.16.1.jar` — Ostinato branch `1.16.1`
  (`method_30621` no-op + `AltoClefSettings`).
- Legacy name: `baritone-unoptimized-fabric-1.16.1.jar` — same 1.16.1 lineage;
  TenorClef also accepts this pattern.

Do not drop a 1.21.11 Ostinato jar into a 1.21.4 TenorClef build, or the
reverse.
