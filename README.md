# Advanced Solar Panels: Refactored

Forge addon project for Minecraft 1.20.1 and IndustrialCraft 2: Refactored.

## Version

Current release: `0.4.0-beta.1` (beta)

## Changes since 0.2.0

- Raised the minimum IndustrialCraft 2: Refactored version to `2.10.39-ex120`.
- Added optional BuildCraft `7.99.24.9` integration with eight configurable LV, MV, HV, and EV energy converters.
- Added Electric Engines for EU-to-MJ conversion and Pneumatic Transducers for MJ-to-EU conversion at the Transducers ratio of `5 EU = 2 MJ`.
- Added per-machine and master configuration switches, redstone shutdown, directional EU/MJ connections, IC2 wrench support, recipes, drops, and localized names.
- Added Jade information for EU and MJ buffers, conversion rates, connection sides, and machine status.
- Corrected the Electric Engine model UV mapping for the original `64x32` Transducers texture atlases.
- Added attribution and the GPL-2.0 license for the unmodified Transducers machine textures.

## Runtime requirements

- Minecraft 1.20.1
- Forge 47.4.20 or newer Forge 47.x
- IndustrialCraft 2: Refactored 2.10.39-ex120 or newer

## Optional integrations and dependencies

- BuildCraft 7.99.24.9 for Minecraft 1.20.1 (upstream beta release)
- JEI 15.20.0 or newer
- Jade 11.13.0 or newer
- Just a lot more enchantments 1.6d (optional; its own PML dependency is still required)

The mod runs without BuildCraft, JEI, or Jade. BuildCraft is an optional dependency; JEI and Jade provide the integrations described above.

IC2 electric tools, electric armor, and this add-on's solar helmets can be converted in a smithing table with lapis lazuli into enchantable counterparts. Conversion and compatible upgrade recipes preserve charge, enchantments, custom names, and mode NBT. The new equipment is recognized through vanilla and Forge tool/armor tags and also supports applicable Just a lot more enchantments enchantments when that mod is installed. Mending-style enchantments do not recharge EU.

When BuildCraft is installed, eight configurable IC2/BuildCraft energy converters are available: LV, MV, HV, and EV Electric Engines convert EU to MJ, while the four Pneumatic Transducers convert MJ back to EU. Each machine can be disabled in `advanced_solar_panels_refactored-common.toml`; disabled machines stop operating, disappear from the creative tab, and have their recipes removed after a data reload or restart.

The converters preserve the original Transducers ratio of 5 EU to 2 MJ. Their implementation has been rewritten for the Forge 1.20.1 IC2 EnergyNet and BuildCraft MJ capability APIs.

## Development dependencies

- IndustrialCraft 2: Refactored 2.10.39-ex120 is imported from `libs/ic2-forge-2.10.39-ex120.jar`
- BuildCraft 7.99.24.9 is imported from `libs/buildcraft-7.99.24.9-1.20.1-2026-03-09T02-31-28+0800-all.jar`
- JEI 15.20.0.130 is resolved from Maven for optional integration
- Jade 11.13.2+forge is resolved from Modrinth Maven for optional integration

The converter machine textures are unmodified assets from [adamros/Transducers](https://github.com/adamros/Transducers). See `THIRD_PARTY_NOTICES.md` and `licenses/Transducers-GPL-2.0.txt` for attribution and licensing.

## Build

```bash
./gradlew build
```

The built mod jar is written to `build/libs/`.

## Releases

Pushing a tag like `v0.3.0` runs the GitHub Actions release workflow. It builds the mod jar and sources jar, then attaches both files to the GitHub Release.
