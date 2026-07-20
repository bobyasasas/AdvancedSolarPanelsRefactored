package com.refactoredsolarpanels.recipe;

import com.google.gson.JsonObject;
import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record MachineEnabledCondition(String machineId) implements ICondition {
    private static final ResourceLocation ID = AdvancedSolarPanels.id("machine_enabled");

    public static void register() {
        CraftingHelper.register(Serializer.INSTANCE);
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return AdvancedSolarCommonConfig.isBuildCraftConverterEnabled(this.machineId);
    }

    public enum Serializer implements IConditionSerializer<MachineEnabledCondition> {
        INSTANCE;

        @Override
        public void write(JsonObject json, MachineEnabledCondition value) {
            json.addProperty("machine", value.machineId());
        }

        @Override
        public MachineEnabledCondition read(JsonObject json) {
            return new MachineEnabledCondition(GsonHelper.getAsString(json, "machine"));
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
