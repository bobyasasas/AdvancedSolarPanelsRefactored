# Advanced Solar Panels: Refactored

Forge addon project for Minecraft 1.20.1 and IndustrialCraft 2: Refactored.

## Version

Current release: `0.1.0`

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

Pushing a tag like `v0.1.0` runs the GitHub Actions release workflow. It builds the mod jar and sources jar, then attaches both files to the GitHub Release.
