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
import net.minecraft.world.item.crafting.ShapedRecipe;

/** A shaped upgrade recipe that carries charge, enchantments, name, and mode NBT forward. */
public final class CopyNbtShapedRecipe extends ShapedRecipe {
    private final Ingredient copyNbtFrom;

    private CopyNbtShapedRecipe(ResourceLocation id, String group, CraftingBookCategory category,
                                int width, int height, NonNullList<Ingredient> ingredients,
                                ItemStack result, boolean showNotification, Ingredient copyNbtFrom) {
        super(id, group, category, width, height, ingredients, result, showNotification);
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
        return ModRecipeTypes.COPY_NBT_SHAPED_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<CopyNbtShapedRecipe> {
        @Override
        public CopyNbtShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.fromJson(id, json);
            Ingredient source = Ingredient.fromJson(GsonHelper.getNonNull(json, "copy_nbt_from"));
            return wrap(base, source);
        }

        @Override
        public CopyNbtShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer);
            return wrap(base, Ingredient.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CopyNbtShapedRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
            recipe.copyNbtFrom.toNetwork(buffer);
        }

        private static CopyNbtShapedRecipe wrap(ShapedRecipe base, Ingredient source) {
            return new CopyNbtShapedRecipe(
                    base.getId(), base.getGroup(), base.category(), base.getWidth(), base.getHeight(),
                    base.getIngredients(), base.getResultItem(RegistryAccess.EMPTY).copy(),
                    base.showNotification(), source
            );
        }
    }
}
