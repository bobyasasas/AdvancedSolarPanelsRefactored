package com.refactoredsolarpanels.recipe;

import com.google.gson.JsonObject;
import com.refactoredsolarpanels.registry.ModItems;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import ic2.core.ref.Ic2Items;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

/** Template-free smithing conversion: original IC2 equipment + lapis -> enchantable copy. */
public final class LapisEquipmentSmithingRecipe implements SmithingRecipe {
    private static final TagKey<Item> LAPIS_GEMS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "gems/lapis"));
    private static final Map<Item, Supplier<Item>> CONVERSIONS = createConversions();

    private final ResourceLocation id;

    public LapisEquipmentSmithingRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return container.getItem(0).isEmpty()
                && CONVERSIONS.containsKey(container.getItem(1).getItem())
                && isLapis(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        Supplier<Item> resultItem = CONVERSIONS.get(container.getItem(1).getItem());
        if (resultItem == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(resultItem.get());
        if (container.getItem(1).hasTag()) {
            result.setTag(container.getItem(1).getTag().copy());
        }
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.LAPIS_DRILL.get());
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return CONVERSIONS.containsKey(stack.getItem());
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return isLapis(stack);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.LAPIS_EQUIPMENT_SMITHING_SERIALIZER.get();
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    public List<ItemStack> getBaseItems() {
        List<ItemStack> items = new ArrayList<>(CONVERSIONS.size());
        for (Item item : CONVERSIONS.keySet()) {
            items.add(new ItemStack(item));
        }
        return items;
    }

    public List<ItemStack> getResultItems() {
        List<ItemStack> items = new ArrayList<>(CONVERSIONS.size());
        for (Supplier<Item> item : CONVERSIONS.values()) {
            items.add(new ItemStack(item.get()));
        }
        return items;
    }

    public ItemStack getResultFor(ItemStack base) {
        Supplier<Item> resultItem = CONVERSIONS.get(base.getItem());
        return resultItem == null ? ItemStack.EMPTY : new ItemStack(resultItem.get());
    }

    public Ingredient getLapisIngredient() {
        return Ingredient.of(LAPIS_GEMS);
    }

    private static boolean isLapis(ItemStack stack) {
        return stack.is(Items.LAPIS_LAZULI) || stack.is(LAPIS_GEMS);
    }

    private static Map<Item, Supplier<Item>> createConversions() {
        Map<Item, Supplier<Item>> conversions = new LinkedHashMap<>();
        conversions.put(Ic2Items.CHAINSAW, ModItems.LAPIS_CHAINSAW);
        conversions.put(Ic2Items.DRILL, ModItems.LAPIS_DRILL);
        conversions.put(Ic2Items.DIAMOND_DRILL, ModItems.LAPIS_DIAMOND_DRILL);
        conversions.put(Ic2Items.IRIDIUM_DRILL, ModItems.LAPIS_IRIDIUM_DRILL);
        conversions.put(Ic2Items.ELECTRIC_WRENCH, ModItems.LAPIS_ELECTRIC_WRENCH);
        conversions.put(Ic2Items.ELECTRIC_TREETAP, ModItems.LAPIS_ELECTRIC_TREETAP);
        conversions.put(Ic2Items.MINING_LASER, ModItems.LAPIS_MINING_LASER);
        conversions.put(Ic2Items.NANO_SABER, ModItems.LAPIS_NANO_SABER);
        conversions.put(Ic2Items.JETPACK_ELECTRIC, ModItems.LAPIS_JETPACK_ELECTRIC);
        conversions.put(Ic2Items.BATPACK, ModItems.LAPIS_BATPACK);
        conversions.put(Ic2Items.ADVANCED_BATPACK, ModItems.LAPIS_ADVANCED_BATPACK);
        conversions.put(Ic2Items.ENERGY_PACK, ModItems.LAPIS_ENERGY_PACK);
        conversions.put(Ic2Items.LAPPACK, ModItems.LAPIS_LAPPACK);
        conversions.put(Ic2Items.NIGHT_VISION_GOGGLES, ModItems.LAPIS_NIGHT_VISION_GOGGLES);
        conversions.put(Ic2Items.NANO_BOOTS, ModItems.LAPIS_NANO_BOOTS);
        conversions.put(Ic2Items.NANO_CHESTPLATE, ModItems.LAPIS_NANO_CHESTPLATE);
        conversions.put(Ic2Items.NANO_HELMET, ModItems.LAPIS_NANO_HELMET);
        conversions.put(Ic2Items.NANO_LEGGINGS, ModItems.LAPIS_NANO_LEGGINGS);
        conversions.put(Ic2Items.QUANTUM_BOOTS, ModItems.LAPIS_QUANTUM_BOOTS);
        conversions.put(Ic2Items.QUANTUM_CHESTPLATE, ModItems.LAPIS_QUANTUM_CHESTPLATE);
        conversions.put(Ic2Items.QUANTUM_HELMET, ModItems.LAPIS_QUANTUM_HELMET);
        conversions.put(Ic2Items.QUANTUM_LEGGINGS, ModItems.LAPIS_QUANTUM_LEGGINGS);
        conversions.put(ModItems.ADVANCED_SOLAR_HELMET.get(), ModItems.LAPIS_ADVANCED_SOLAR_HELMET);
        conversions.put(ModItems.HYBRID_SOLAR_HELMET.get(), ModItems.LAPIS_HYBRID_SOLAR_HELMET);
        conversions.put(ModItems.ULTIMATE_SOLAR_HELMET.get(), ModItems.LAPIS_ULTIMATE_SOLAR_HELMET);
        return conversions;
    }

    public static final class Serializer implements RecipeSerializer<LapisEquipmentSmithingRecipe> {
        @Override
        public LapisEquipmentSmithingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new LapisEquipmentSmithingRecipe(id);
        }

        @Override
        public LapisEquipmentSmithingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new LapisEquipmentSmithingRecipe(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LapisEquipmentSmithingRecipe recipe) {
        }
    }
}
