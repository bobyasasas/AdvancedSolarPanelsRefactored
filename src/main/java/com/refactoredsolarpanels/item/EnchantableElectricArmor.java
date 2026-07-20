package com.refactoredsolarpanels.item;

import ic2.core.item.armor.ItemArmorAdvBatpack;
import ic2.core.item.armor.ItemArmorBatpack;
import ic2.core.item.armor.ItemArmorEnergypack;
import ic2.core.item.armor.ItemArmorJetpackElectric;
import ic2.core.item.armor.ItemArmorLappack;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorNightVisionGoggles;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.ref.Ic2ArmorMaterials;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantableElectricArmor {
    private EnchantableElectricArmor() {
    }

    public static Item nano(EquipmentSlot slot) {
        return new NanoSuit(slot);
    }

    public static Item quantum(EquipmentSlot slot) {
        return new QuantumSuit(slot);
    }

    public static Item electricJetpack() {
        return new ElectricJetpack();
    }

    public static Item batpack() {
        return new Batpack();
    }

    public static Item advancedBatpack() {
        return new AdvancedBatpack();
    }

    public static Item energyPack() {
        return new EnergyPack();
    }

    public static Item lappack() {
        return new Lappack();
    }

    public static Item nightVisionGoggles() {
        return new NightVisionGoggles();
    }

    private static Item representative(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Items.NETHERITE_HELMET;
            case CHEST -> Items.NETHERITE_CHESTPLATE;
            case LEGS -> Items.NETHERITE_LEGGINGS;
            case FEET -> Items.NETHERITE_BOOTS;
            default -> Items.AIR;
        };
    }

    private static boolean canApply(ItemStack stack, Enchantment enchantment, EquipmentSlot slot) {
        return EnchantableItemSupport.canApply(stack, enchantment, representative(slot));
    }

    private static final class NanoSuit extends ItemArmorNanoSuit {
        private final EquipmentSlot slot;

        private NanoSuit(EquipmentSlot slot) {
            super(Ic2ArmorMaterials.NANO_SUIT, slot, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
            this.slot = slot;
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, this.slot);
        }

        @Override
        public void damageArmor(LivingEntity entity, ItemStack stack, DamageSource source, double damage, EquipmentSlot slot) {
            super.damageArmor(entity, stack, source,
                    EnchantableItemSupport.applyUnbreaking(stack, damage, entity.getRandom()), slot);
        }
    }

    private static final class QuantumSuit extends ItemArmorQuantumSuit {
        private final EquipmentSlot slot;

        private QuantumSuit(EquipmentSlot slot) {
            super(Ic2ArmorMaterials.QUANTUM_SUIT, slot, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
            this.slot = slot;
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, this.slot);
        }

        @Override
        public void damageArmor(LivingEntity entity, ItemStack stack, DamageSource source, double damage, EquipmentSlot slot) {
            super.damageArmor(entity, stack, source,
                    EnchantableItemSupport.applyUnbreaking(stack, damage, entity.getRandom()), slot);
        }
    }

    private static final class ElectricJetpack extends ItemArmorJetpackElectric {
        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, EquipmentSlot.CHEST);
        }
    }

    private static final class Batpack extends ItemArmorBatpack {
        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, EquipmentSlot.CHEST);
        }
    }

    private static final class AdvancedBatpack extends ItemArmorAdvBatpack {
        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, EquipmentSlot.CHEST);
        }
    }

    private static final class EnergyPack extends ItemArmorEnergypack {
        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, EquipmentSlot.CHEST);
        }
    }

    private static final class Lappack extends ItemArmorLappack {
        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, EquipmentSlot.CHEST);
        }
    }

    private static final class NightVisionGoggles extends ItemArmorNightVisionGoggles {
        private NightVisionGoggles() {
            super(new Item.Properties().stacksTo(1).durability(27));
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return EnchantableItemSupport.isEnchantable(stack);
        }

        @Override
        public int getEnchantmentValue() {
            return EnchantableItemSupport.getEnchantmentValue();
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
            return canApply(stack, enchantment, EquipmentSlot.HEAD);
        }
    }
}
