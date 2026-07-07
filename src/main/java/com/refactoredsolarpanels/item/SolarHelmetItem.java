package com.refactoredsolarpanels.item;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.ref.Ic2ArmorMaterials;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

public final class SolarHelmetItem {
    private static final int SKY_CHECK_INTERVAL = 128;
    private static final String TAG_TICKER = "SolarTicker";
    private static final String TAG_STATE = "SolarState";

    private SolarHelmetItem() {
    }

    public static Item create(SolarHelmetTier tier) {
        return switch (tier) {
            case ADVANCED -> new Nano(tier);
            case HYBRID, ULTIMATE -> new Quantum(tier);
        };
    }

    private static Item.Properties properties(SolarHelmetTier tier) {
        return new Item.Properties().stacksTo(1).rarity(tier.getRarity());
    }

    private static void solarTick(SolarHelmetTier tier, ItemStack stack, Level level, Entity entity, int slot, Item sourceItem) {
        if (level.isClientSide || !(entity instanceof Player player) || slot != EquipmentSlot.HEAD.getIndex()) {
            return;
        }
        if (player.getItemBySlot(EquipmentSlot.HEAD) != stack) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        int ticker = tag.getInt(TAG_TICKER) + 1;
        tag.putInt(TAG_TICKER, ticker);
        if (ticker == 1 || ticker % SKY_CHECK_INTERVAL == 0) {
            tag.putInt(TAG_STATE, getGenerationState(level, player.blockPosition()).ordinal());
        }

        GenerationState state = GenerationState.byId(tag.getInt(TAG_STATE));
        double remaining = switch (state) {
            case DAY -> tier.getDayProductionEuTick();
            case NIGHT -> tier.getNightProductionEuTick();
            case NONE -> 0.0D;
        };
        if (remaining <= 0.0D) {
            return;
        }

        int electricTier = ((IElectricItem) sourceItem).getTier(stack);
        remaining = chargeList(player.getInventory().armor, remaining, electricTier, sourceItem);
        remaining = chargeList(player.getInventory().offhand, remaining, electricTier, sourceItem);
        remaining = chargeList(player.getInventory().items, remaining, electricTier, sourceItem);
        if (remaining > 0.0D) {
            ElectricItem.manager.charge(stack, remaining, Integer.MAX_VALUE, true, false);
        }
    }

    private static double chargeList(List<ItemStack> stacks, double amount, int electricTier, Item sourceItem) {
        double remaining = amount;
        for (ItemStack target : stacks) {
            if (remaining <= 0.0D) {
                break;
            }
            if (!target.isEmpty() && target.getItem() instanceof IElectricItem && target.getItem() != sourceItem) {
                remaining -= ElectricItem.manager.charge(target, remaining, electricTier, false, false);
            }
        }
        return Math.max(0.0D, remaining);
    }

    private static GenerationState getGenerationState(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight() || !level.canSeeSky(pos.above())) {
            return GenerationState.NONE;
        }
        return isDaylight(level, pos) ? GenerationState.DAY : GenerationState.NIGHT;
    }

    private static boolean isDaylight(Level level, BlockPos pos) {
        if (!level.isDay() || level.isRainingAt(pos)) {
            return false;
        }
        float sunBrightness = Mth.clamp((float) Math.cos(level.getSunAngle(1.0F)) * 2.0F + 0.2F, 0.0F, 1.0F);
        return level.getBrightness(LightLayer.SKY, pos.above()) > 0 && sunBrightness > 0.0F;
    }

    private static void addSolarTooltip(SolarHelmetTier tier, List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.advanced_solar_panels_refactored.production.day", format(tier.getDayProductionEuTick())).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.advanced_solar_panels_refactored.production.night", format(tier.getNightProductionEuTick())).withStyle(ChatFormatting.GRAY));
    }

    private static String armorTexture(SolarHelmetTier tier, @Nullable String type) {
        String suffix = type == null || tier == SolarHelmetTier.ADVANCED ? "" : "_overlay";
        return AdvancedSolarPanels.MOD_ID + ":textures/armor/" + tier.getId() + suffix + ".png";
    }

    private static String format(double value) {
        return Long.toString(Math.round(value));
    }

    private enum GenerationState {
        NONE,
        NIGHT,
        DAY;

        static GenerationState byId(int id) {
            if (id < 0 || id >= values().length) {
                return NONE;
            }
            return values()[id];
        }
    }

    private static final class Nano extends ItemArmorNanoSuit {
        private final SolarHelmetTier tier;

        private Nano(SolarHelmetTier tier) {
            super(Ic2ArmorMaterials.NANO_SUIT, EquipmentSlot.HEAD, properties(tier));
            this.tier = tier;
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, level, entity, slot, selected);
            solarTick(this.tier, stack, level, entity, slot, this);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            addSolarTooltip(this.tier, tooltip);
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return armorTexture(this.tier, type);
        }
    }

    private static final class Quantum extends ItemArmorQuantumSuit {
        private final SolarHelmetTier tier;

        private Quantum(SolarHelmetTier tier) {
            super(Ic2ArmorMaterials.QUANTUM_SUIT, EquipmentSlot.HEAD, properties(tier));
            this.tier = tier;
        }

        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, level, entity, slot, selected);
            solarTick(this.tier, stack, level, entity, slot, this);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            addSolarTooltip(this.tier, tooltip);
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return armorTexture(this.tier, type);
        }
    }
}
