package com.refactoredsolarpanels.registry;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.recipe.MolecularTransformerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AdvancedSolarPanels.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, AdvancedSolarPanels.MOD_ID);

    public static final RegistryObject<RecipeSerializer<MolecularTransformerRecipe>> MOLECULAR_TRANSFORMING_SERIALIZER = RECIPE_SERIALIZERS.register(
            "molecular_transforming",
            MolecularTransformerRecipe.Serializer::new
    );

    public static final RegistryObject<RecipeType<MolecularTransformerRecipe>> MOLECULAR_TRANSFORMING = RECIPE_TYPES.register(
            "molecular_transforming",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return ResourceLocation.fromNamespaceAndPath(AdvancedSolarPanels.MOD_ID, "molecular_transforming").toString();
                }
            }
    );

    private ModRecipeTypes() {
    }

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
