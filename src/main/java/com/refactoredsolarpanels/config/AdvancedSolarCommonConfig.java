package com.refactoredsolarpanels.config;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;

public final class AdvancedSolarCommonConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue ENABLE_BUILDCRAFT_CONVERTERS;
    private static final Map<String, ForgeConfigSpec.BooleanValue> MACHINE_TOGGLES = new LinkedHashMap<>();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("buildcraftConverters");
        ENABLE_BUILDCRAFT_CONVERTERS = builder
                .comment("Master switch for all IC2/BuildCraft energy converter machines.")
                .define("enabled", true);

        defineMachine(builder, "lv_electric_engine");
        defineMachine(builder, "mv_electric_engine");
        defineMachine(builder, "hv_electric_engine");
        defineMachine(builder, "ev_electric_engine");
        defineMachine(builder, "lv_pneumatic_transducer");
        defineMachine(builder, "mv_pneumatic_transducer");
        defineMachine(builder, "hv_pneumatic_transducer");
        defineMachine(builder, "ev_pneumatic_transducer");

        builder.pop();
        SPEC = builder.build();
    }

    private AdvancedSolarCommonConfig() {
    }

    public static boolean isBuildCraftConverterEnabled(String machineId) {
        ForgeConfigSpec.BooleanValue machineToggle = MACHINE_TOGGLES.get(machineId);
        return ENABLE_BUILDCRAFT_CONVERTERS.get() && machineToggle != null && machineToggle.get();
    }

    private static void defineMachine(ForgeConfigSpec.Builder builder, String machineId) {
        MACHINE_TOGGLES.put(machineId, builder
                .comment("Enable the " + machineId + " machine, its recipe, and its creative-tab entry.")
                .define(machineId, true));
    }
}
