package com.refactoredsolarpanels.item;

import java.util.Arrays;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/** Shared enchanting behavior for the lapis-converted IC2 equipment. */
public final class EnchantableItemSupport {
    private static final int ENCHANTMENT_VALUE = 15;

    private EnchantableItemSupport() {
    }

    public static boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public static int getEnchantmentValue() {
        return ENCHANTMENT_VALUE;
    }

    /**
     * Tests the enchantment against vanilla representatives instead of the unusual IC2
     * implementation class. This also lets modded enchantments that follow Forge's
     * enchanting hook recognize the converted item as its corresponding vanilla type.
     */
    public static boolean canApply(ItemStack stack, Enchantment enchantment, Item... representatives) {
        if (isRepairEnchantment(enchantment)) {
            return false;
        }
        return Arrays.stream(representatives)
                .map(ItemStack::new)
                .anyMatch(enchantment::canApplyAtEnchantingTable);
    }

    /** Applies vanilla Unbreaking semantics to EU spent as tool wear. */
    public static double applyUnbreaking(ItemStack stack, double energyCost, RandomSource random) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (level > 0 && DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(stack, level, random)) {
            return 0.0D;
        }
        return energyCost;
    }

    private static boolean isRepairEnchantment(Enchantment enchantment) {
        if (enchantment == Enchantments.MENDING) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        return id != null && id.getNamespace().equals("jlme") && id.getPath().equals("repairable");
    }
}
