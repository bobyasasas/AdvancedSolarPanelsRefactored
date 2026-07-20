package com.refactoredsolarpanels.recipe;

import com.google.gson.JsonObject;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/** A shapeless counterpart to {@link CopyNbtShapedRecipe}. */
public final class CopyNbtShapelessRecipe extends ShapelessRecipe {
    private final Ingredient copyNbtFrom;

    private CopyNbtShapelessRecipe(ResourceLocation id, String group, CraftingBookCategory category,
                                   ItemStack result, NonNullList<Ingredient> ingredients, Ingredient copyNbtFrom) {
        super(id, group, category, result, ingredients);
        this.copyNbtFrom = copyNbtFrom;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(container, registryAccess);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack candidate = container.getItem(slot);
            if (this.copyNbtFrom.test(candidate) && candidate.hasTag()) {
                result.setTag(candidate.getTag().copy());
                break;
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.COPY_NBT_SHAPELESS_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<CopyNbtShapelessRecipe> {
        @Override
        public CopyNbtShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
            ShapelessRecipe base = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
            return wrap(base, Ingredient.fromJson(GsonHelper.getNonNull(json, "copy_nbt_from")));
        }

        @Override
        public CopyNbtShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ShapelessRecipe base = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buffer);
            return wrap(base, Ingredient.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CopyNbtShapelessRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
            recipe.copyNbtFrom.toNetwork(buffer);
        }

        private static CopyNbtShapelessRecipe wrap(ShapelessRecipe base, Ingredient source) {
            return new CopyNbtShapelessRecipe(base.getId(), base.getGroup(), base.category(),
                    base.getResultItem(RegistryAccess.EMPTY).copy(), base.getIngredients(), source);
        }
    }
}
