package com.refactoredsolarpanels.item;

import ic2.core.item.tool.ItemDrill;
import ic2.core.item.tool.ItemDrillIridium;
import ic2.core.item.tool.ItemElectricToolChainsaw;
import ic2.core.item.tool.ItemNanoSaber;
import ic2.core.item.tool.ItemToolMiningLaser;
import ic2.core.item.tool.ItemToolWrenchElectric;
import ic2.core.item.tool.ItemTreetapElectric;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantableElectricTools {
    private EnchantableElectricTools() {
    }

    private static Item.Properties properties() {
        return new Item.Properties().stacksTo(1);
    }

    public static Item drill() {
        return new Drill(false);
    }

    public static Item diamondDrill() {
        return new Drill(true);
    }

    public static Item iridiumDrill() {
        return new IridiumDrill();
    }

    public static Item chainsaw() {
        return new Chainsaw();
    }

    public static Item electricWrench() {
        return new ElectricWrench();
    }

    public static Item electricTreetap() {
        return new ElectricTreetap();
    }

    public static Item miningLaser() {
        return new MiningLaser();
    }

    public static Item nanoSaber() {
        return new NanoSaber();
    }

    private static final class Drill extends ItemDrill {
        private Drill(boolean diamond) {
            super(properties(), diamond ? 80 : 50, diamond ? Tiers.DIAMOND : Tiers.IRON,
                    30000, 100, 1, diamond ? 16.0F : 8.0F);
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity.getRandom()), entity);
        }
    }

    private static final class IridiumDrill extends ItemDrillIridium {
        private IridiumDrill() {
            super(properties());
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity == null ? net.minecraft.util.RandomSource.create() : entity.getRandom()), entity);
        }
    }

    private static final class Chainsaw extends ItemElectricToolChainsaw {
        private Chainsaw() {
            super(properties());
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.NETHERITE_AXE, Items.NETHERITE_SWORD, Items.SHEARS);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity.getRandom()), entity);
        }
    }

    private static final class ElectricWrench extends ItemToolWrenchElectric {
        private ElectricWrench() {
            super(properties());
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.NETHERITE_PICKAXE);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity.getRandom()), entity);
        }
    }

    private static final class ElectricTreetap extends ItemTreetapElectric {
        private ElectricTreetap() {
            super(properties());
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.NETHERITE_AXE);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity.getRandom()), entity);
        }
    }

    private static final class MiningLaser extends ItemToolMiningLaser {
        private MiningLaser() {
            super(properties());
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.BOW);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity.getRandom()), entity);
        }
    }

    private static final class NanoSaber extends ItemNanoSaber {
        private NanoSaber() {
            super(properties());
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
            return EnchantableItemSupport.canApply(stack, enchantment, Items.NETHERITE_SWORD);
        }

        @Override
        public boolean consumeEnergy(ItemStack stack, double amount, LivingEntity entity) {
            return super.consumeEnergy(stack, EnchantableItemSupport.applyUnbreaking(stack, amount, entity == null ? net.minecraft.util.RandomSource.create() : entity.getRandom()), entity);
        }
    }
}
