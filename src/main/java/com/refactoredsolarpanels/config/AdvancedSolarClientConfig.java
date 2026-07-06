package com.refactoredsolarpanels.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AdvancedSolarClientConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue MOLECULAR_TRANSFORMER_GLOW_EFFECT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("client");
        MOLECULAR_TRANSFORMER_GLOW_EFFECT = builder
                .comment("Render a client-side blue glow effect inside the Molecular Transformer while it is running.")
                .define("molecularTransformerGlowEffect", true);
        builder.pop();
        SPEC = builder.build();
    }

    private AdvancedSolarClientConfig() {
    }

    public static boolean molecularTransformerGlowEffect() {
        return MOLECULAR_TRANSFORMER_GLOW_EFFECT.get();
    }
}
