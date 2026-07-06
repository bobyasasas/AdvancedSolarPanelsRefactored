package com.refactoredsolarpanels.recipe;

import com.google.gson.JsonObject;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MolecularTransformerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int inputCount;
    private final ItemStack result;
    private final int energy;

    public MolecularTransformerRecipe(ResourceLocation id, Ingredient ingredient, int inputCount, ItemStack result, int energy) {
        this.id = id;
        this.ingredient = ingredient;
        this.inputCount = Math.max(1, inputCount);
        this.result = result;
        this.energy = energy;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0);
        return this.ingredient.test(input) && input.getCount() >= this.inputCount;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.ingredient);
        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getInputCount() {
        return this.inputCount;
    }

    public ItemStack getResult() {
        return this.result.copy();
    }

    public int getEnergy() {
        return this.energy;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.MOLECULAR_TRANSFORMING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MOLECULAR_TRANSFORMING.get();
    }

    public static class Serializer implements RecipeSerializer<MolecularTransformerRecipe> {
        @Override
        public MolecularTransformerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            int inputCount = GsonHelper.getAsInt(json, "input_count", 1);
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            int energy = GsonHelper.getAsInt(json, "energy");
            return new MolecularTransformerRecipe(recipeId, ingredient, inputCount, result, energy);
        }

        @Override
        public MolecularTransformerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int inputCount = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            int energy = buffer.readVarInt();
            return new MolecularTransformerRecipe(recipeId, ingredient, inputCount, result, energy);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MolecularTransformerRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeVarInt(recipe.inputCount);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.energy);
        }
    }
}
