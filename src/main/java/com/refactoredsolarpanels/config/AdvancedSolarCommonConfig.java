package com.refactoredsolarpanels.config;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;

public final class AdvancedSolarCommonConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue ENABLE_BUILDCRAFT_CONVERTERS;
    private static final Map<String, ForgeConfigSpec.BooleanValue> MACHINE_TOGGLES = new LinkedHashMap<>();
    private static final ForgeConfigSpec.BooleanValue ENABLE_EXPANDED_RECIPES;
    private static final Map<String, ForgeConfigSpec.BooleanValue> RECIPE_GROUP_TOGGLES = new LinkedHashMap<>();
    private static final ForgeConfigSpec.BooleanValue ENABLE_EXPERIMENTAL_RECIPES;
    private static final Map<String, ForgeConfigSpec.BooleanValue> EXPERIMENTAL_RECIPE_TOGGLES = new LinkedHashMap<>();

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

        builder.push("expandedRecipes");
        ENABLE_EXPANDED_RECIPES = builder
                .comment("Master switch for all additional Minecraft 1.20.1 integration recipes. A data reload or restart is required after changing recipe settings.")
                .define("enabled", true);

        builder.push("groups");
        defineRecipeGroup(builder, "materials", "wood, stone, compression, extraction, and food-canning recipes");
        defineRecipeGroup(builder, "ore_processing", "gem ores, Nether quartz, Nether gold, and Ancient Debris processing");
        defineRecipeGroup(builder, "biomass", "post-1.12 crops and plants processed into Bio Chaff");
        defineRecipeGroup(builder, "dye_extraction", "flower and plant dye extraction recipes");
        defineRecipeGroup(builder, "molecular_transformation", "balanced Molecular Transformer transmutations");
        defineRecipeGroup(builder, "intermediate_materials", "crystalline solar lenses and netherite plates");
        builder.pop();

        builder.push("experimental");
        ENABLE_EXPERIMENTAL_RECIPES = builder
                .comment("Master switch for progression-changing transmutations. Individual switches below are also checked.")
                .define("enabled", true);
        defineExperimentalRecipe(builder, "renewable_elytra", "Allow 16 phantom membranes and 250,000,000 EU to create an elytra.");
        defineExperimentalRecipe(builder, "renewable_budding_amethyst", "Allow amethyst blocks to be transformed into budding amethyst.");
        defineExperimentalRecipe(builder, "renewable_echo_shards", "Allow sculk catalysts to be transformed into echo shards.");
        defineExperimentalRecipe(builder, "renewable_shulker_shells", "Allow ender pearls to be transformed into shulker shells.");
        defineExperimentalRecipe(builder, "renewable_hearts_of_the_sea", "Allow nautilus shells to be transformed into hearts of the sea.");
        defineExperimentalRecipe(builder, "netherite_from_diamonds", "Allow diamonds to be transformed into netherite scrap.");
        defineExperimentalRecipe(builder, "gold_from_copper", "Allow copper ingots to be transformed into gold ingots.");
        defineExperimentalRecipe(builder, "diamonds_from_emeralds", "Allow emeralds to be transformed into diamonds.");
        builder.pop();
        builder.pop();
        SPEC = builder.build();
    }

    private AdvancedSolarCommonConfig() {
    }

    public static boolean isBuildCraftConverterEnabled(String machineId) {
        ForgeConfigSpec.BooleanValue machineToggle = MACHINE_TOGGLES.get(machineId);
        return ENABLE_BUILDCRAFT_CONVERTERS.get() && machineToggle != null && machineToggle.get();
    }

    public static boolean isExpandedRecipeEnabled(String featureId) {
        if (!ENABLE_EXPANDED_RECIPES.get()) {
            return false;
        }

        ForgeConfigSpec.BooleanValue recipeGroup = RECIPE_GROUP_TOGGLES.get(featureId);
        if (recipeGroup != null) {
            return recipeGroup.get();
        }

        ForgeConfigSpec.BooleanValue experimentalRecipe = EXPERIMENTAL_RECIPE_TOGGLES.get(featureId);
        return experimentalRecipe != null
                && isRecipeGroupEnabled("molecular_transformation")
                && ENABLE_EXPERIMENTAL_RECIPES.get()
                && experimentalRecipe.get();
    }

    private static void defineMachine(ForgeConfigSpec.Builder builder, String machineId) {
        MACHINE_TOGGLES.put(machineId, builder
                .comment("Enable the " + machineId + " machine, its recipe, and its creative-tab entry.")
                .define(machineId, true));
    }

    private static void defineRecipeGroup(ForgeConfigSpec.Builder builder, String groupId, String description) {
        RECIPE_GROUP_TOGGLES.put(groupId, builder
                .comment("Enable additional " + description + ".")
                .define(groupId, true));
    }

    private static void defineExperimentalRecipe(ForgeConfigSpec.Builder builder, String recipeId, String description) {
        EXPERIMENTAL_RECIPE_TOGGLES.put(recipeId, builder
                .comment(description)
                .define(recipeId, true));
    }

    private static boolean isRecipeGroupEnabled(String groupId) {
        ForgeConfigSpec.BooleanValue recipeGroup = RECIPE_GROUP_TOGGLES.get(groupId);
        return recipeGroup != null && recipeGroup.get();
    }
}
