# Advanced Solar Panels: Refactored 0.4.0-beta.5

This beta rebalances the Iridium Rotor so its performance better reflects its late-game crafting cost and fixes fall protection for the lapis-upgraded QuantumSuit Boots.

## Changes

- Increased Iridium Rotor durability from `1,209,600` to `12,096,000`, giving it ten times the previous operating life.
- Increased its efficiency coefficient from `1.25` to `1.75`, a 40% increase in efficiency.
- Rotor diameter and supported wind-strength range remain unchanged.
- Fixed lapis-upgraded QuantumSuit Boots taking fall damage because IC2's fall handler only recognized its own registered QuantumSuit Boots.
- Lapis-upgraded QuantumSuit Boots now use IC2's original behavior: when enough charge is available, they consume EU based on the fall distance and prevent the fall damage; otherwise, the wearer takes the normal damage.

## Requirements

- Minecraft `1.20.1`
- Forge `47.4.20` or newer Forge `47.x`
- IndustrialCraft 2: Refactored `2.10.39-ex120` or newer

This is a prerelease build. Back up important worlds before testing and report any balance or compatibility issues you encounter.
