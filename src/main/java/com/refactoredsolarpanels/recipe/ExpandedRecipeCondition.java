package com.refactoredsolarpanels.recipe;

import com.google.gson.JsonObject;
import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record ExpandedRecipeCondition(String featureId) implements ICondition {
    private static final ResourceLocation ID = AdvancedSolarPanels.id("expanded_recipe_enabled");

    public static void register() {
        CraftingHelper.register(Serializer.INSTANCE);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return AdvancedSolarCommonConfig.isExpandedRecipeEnabled(this.featureId);
    }

    public enum Serializer implements IConditionSerializer<ExpandedRecipeCondition> {
        INSTANCE;

        @Override
        public void write(JsonObject json, ExpandedRecipeCondition value) {
            json.addProperty("feature", value.featureId());
        }

        @Override
        public ExpandedRecipeCondition read(JsonObject json) {
            return new ExpandedRecipeCondition(GsonHelper.getAsString(json, "feature"));
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
