# Advanced Solar Panels: Refactored 0.4.0-beta.4

This beta corrects item acquisition and automation behavior for two special machines.

## Changes

- The Quantum Generator no longer has a crafting recipe and is now a Creative-only item as intended. Existing Quantum Generators are not removed.
- Molecular Transformer item automation now has explicit input and output behavior:
  - Hoppers and item pipes may insert only into the input slot.
  - Automated insertion accepts only items used by a loaded Molecular Transformer recipe.
  - Hoppers and item pipes may extract only from the output slot.
  - These restrictions work from every side, so automation layouts are no longer dependent on an ambiguous machine face.

## Requirements

- Minecraft `1.20.1`
- Forge `47.4.20` or newer Forge `47.x`
- IndustrialCraft 2: Refactored `2.10.39-ex120` or newer

This is a prerelease build. Back up important worlds before testing and report any automation compatibility issues you encounter.
