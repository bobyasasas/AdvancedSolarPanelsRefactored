package com.refactoredsolarpanels.compat.jlme;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.registry.ModItems;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Handles JLME's one sword enchantment that hard-codes vanilla sword item IDs. */
@Mod.EventBusSubscriber(modid = AdvancedSolarPanels.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JlmeAnvilCompat {
    private JlmeAnvilCompat() {
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!(right.getItem() instanceof EnchantedBookItem) || !isLapisSword(left)) {
            return;
        }

        Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.getEnchantments(right);
        boolean hasHeadless = bookEnchantments.keySet().stream().anyMatch(JlmeAnvilCompat::isHeadless);
        if (!hasHeadless) {
            return;
        }

        ItemStack output = left.copy();
        Map<Enchantment, Integer> merged = new LinkedHashMap<>(EnchantmentHelper.getEnchantments(left));
        int addedLevels = 0;
        for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!isHeadless(enchantment) && !left.canApplyAtEnchantingTable(enchantment)) {
                continue;
            }
            if (!EnchantmentHelper.isEnchantmentCompatible(merged.keySet(), enchantment)
                    && !merged.containsKey(enchantment)) {
                continue;
            }
            int oldLevel = merged.getOrDefault(enchantment, 0);
            int bookLevel = entry.getValue();
            int nextLevel = oldLevel == bookLevel
                    ? Math.min(enchantment.getMaxLevel(), bookLevel + 1)
                    : Math.min(enchantment.getMaxLevel(), Math.max(oldLevel, bookLevel));
            merged.put(enchantment, nextLevel);
            addedLevels += Math.max(1, nextLevel - oldLevel);
        }
        if (!merged.keySet().stream().anyMatch(JlmeAnvilCompat::isHeadless)) {
            return;
        }

        EnchantmentHelper.setEnchantments(merged, output);
        if (!event.getName().isBlank()) {
            output.setHoverName(net.minecraft.network.chat.Component.literal(event.getName()));
        }
        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(Math.max(1, 2 + addedLevels));
    }

    private static boolean isLapisSword(ItemStack stack) {
        return stack.is(ModItems.LAPIS_NANO_SABER.get()) || stack.is(ModItems.LAPIS_CHAINSAW.get());
    }

    private static boolean isHeadless(Enchantment enchantment) {
        ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        return id != null && id.getNamespace().equals("jlme") && id.getPath().equals("headless");
    }
}
