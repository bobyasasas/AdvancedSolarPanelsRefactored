# Advanced Solar Panels: Refactored

Forge addon project for Minecraft 1.20.1 and IndustrialCraft 2: Refactored.

## Version

Current release: `0.2.0`

## Highlights in 0.2.0

- Machines now drop their internal inventory contents when broken or replaced.
- IC2 normal and electric wrenches can remove the mod's machines.
- Molecular Transformer behavior has been corrected: it no longer starts processing with no stored EU, and its lit state now reflects active progress with EU input.
- Solar helmets now inherit IC2 nano or quantum helmet behavior, keeping only this mod's textures and solar charging behavior.
- Added Iridium Rotor and Iridium Rotor Blade for IC2 kinetic generators.

## Runtime requirements

- Minecraft 1.20.1
- Forge 47.4.20 or newer Forge 47.x
- IndustrialCraft 2: Refactored 2.10.34-ex120 or newer

## Optional integrations

- JEI 15.20.0 or newer
- Jade 11.13.0 or newer

The mod runs without JEI or Jade. Install them only if you want recipe lookup and tooltip integration.

## Development dependencies

- IndustrialCraft 2: Refactored 2.10.34-ex120 is imported from `libs/ic2-forge-2.10.34-ex120.jar`
- JEI 15.20.0.130 is resolved from Maven for optional integration
- Jade 11.13.2+forge is resolved from Modrinth Maven for optional integration

## Build

```bash
./gradlew build
```

The built mod jar is written to `build/libs/`.

## Releases

Pushing a tag like `v0.2.0` runs the GitHub Actions release workflow. It builds the mod jar and sources jar, then attaches both files to the GitHub Release.
