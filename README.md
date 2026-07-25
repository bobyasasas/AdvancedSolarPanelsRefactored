# Advanced Solar Panels: Refactored

Forge addon project for Minecraft 1.20.1 and IndustrialCraft 2: Refactored.

## Version

Current release: `0.4.0-beta.5` (beta)

## Changes in 0.4.0-beta.5

- Rebalanced the Iridium Rotor to better match its late-game crafting cost:
  - Increased durability from `1,209,600` to `12,096,000` (10x).
  - Increased the efficiency coefficient from `1.25` to `1.75` (40% higher).

## Changes in 0.4.0-beta.4

- Removed the Quantum Generator crafting recipe. The block is now reserved for Creative mode as intended; existing blocks and inventories remain unaffected.
- Added strict Molecular Transformer automation rules for hoppers and item pipes:
  - Items can only be inserted into the input slot, and only valid Molecular Transformer recipe ingredients are accepted.
  - Items can only be extracted from the output slot.
  - The same rules apply on every side of the machine, preventing input and output items from being mixed.

## Changes in 0.4.0-beta.3

- Added JEI support for all 25 template-free lapis equipment smithing conversions, including paired input/output cycling in the vanilla smithing category.
- Fixed Electric Engine rendering by using the cutout render type and disabling full-block occlusion, preventing transparent texture artifacts and see-through faces beneath the model.
- Changed all eight lapis electric tools to reuse their corresponding original IC2 textures while preserving Nano Saber active/inactive model switching.

## Changes in 0.4.0-beta.2

- Added 137 configurable IndustrialCraft 2 machine recipes for Minecraft 1.20.1 materials:
  - 54 wood, stone, compression, extraction, concrete, terracotta, and food-canning recipes.
  - 21 Bio Chaff recipes for newer crops, aquatic plants, Nether plants, moss, and sniffer plants.
  - 24 dye extractor recipes with consistent doubled machine yields.
  - 11 ore-processing recipes for coal, diamond, emerald, lapis, redstone, Nether quartz, Nether gold, and Ancient Debris.
  - 23 Molecular Transformer recipes spanning Overworld geology, Nether resources, End resources, and optional renewable exploration rewards.
  - Four recipes using the new Crystalline Solar Lens and Netherite Plate progression materials.
- Added Crushed Ancient Debris and Purified Ancient Debris. The complete Macerator → Ore Washing Plant → Thermal Centrifuge chain converts one Ancient Debris into two Netherite Scraps; the Blast Furnace remains a faster 1:1 alternative with slag.
- Added Forge item tags for this add-on's iridium and uranium ingots and the new netherite plate, and changed suitable existing ingredients to Forge tags.
- Added a master expanded-recipes switch, six category switches, and individual switches for eight progression-changing Molecular Transformer recipes.

### Expanded recipe configuration

All new recipes are enabled by default in `config/advanced_solar_panels_refactored-common.toml` under `expandedRecipes`.

- `enabled` disables every expanded recipe at once.
- `groups` separately controls material processing, ore processing, biomass, dye extraction, normal Molecular Transformer recipes, and intermediate materials.
- `experimental` has a master switch plus individual switches for renewable elytra, budding amethyst, echo shards, shulker shells, hearts of the sea, diamond-to-netherite, copper-to-gold, and emerald-to-diamond transmutation.

Changing a recipe switch requires a data reload or game/server restart. The four registered intermediate items remain available when their recipes are disabled so existing worlds and inventories remain valid.

## Changes in 0.4.0-beta.1

> `0.4.0-beta.1` is a prerelease. It has passed compilation and dedicated-server startup checks, but it has not yet been tested in normal gameplay.

- Added 25 lapis-upgraded, enchantable equipment variants while preserving the original IC2 item classes, behavior, attributes, energy capacity, and operating modes.
  - Tools: Chainsaw, Mining Drill, Diamond Drill, Iridium Drill, Electric Wrench, Electric Treetap, Mining Laser, and Nano Saber.
  - Wearable equipment: Electric Jetpack, BatPack, Advanced BatPack, Energy Pack, Lappack, and Nightvision Goggles.
  - Armor: complete NanoSuit and QuantumSuit sets.
  - Solar equipment: Advanced, Hybrid, and Ultimate Hybrid Solar Helmets from this add-on.
- Added template-free smithing-table conversion using the original equipment item plus lapis lazuli.
- Preserved EU charge, enchantments, custom names, damage data, and IC2 mode NBT during smithing conversion and compatible equipment upgrades.
- Added matching upgrade recipes for the lapis equipment variants, including drill, Lappack, NanoSuit-to-QuantumSuit, and solar helmet upgrade paths.
- Added corresponding vanilla and Forge axes, pickaxes, shovels, shears, swords, and armor tags for enchantment and mod compatibility.
- Added optional Just a lot more enchantments (JLME) compatibility, including its special Nano Saber anvil applicability check.
- Explicitly excluded vanilla Mending and JLME's `repairable` enchantment; no enchantment directly restores or generates EU.
- Applied vanilla Unbreaking probability rules when reducing EU wear from enchanted electric equipment.
- Added item models, active Nano Saber animation support, English and Chinese localization, creative-tab entries, and lapis-accented item and armor textures.

## Changes in 0.3.0

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
