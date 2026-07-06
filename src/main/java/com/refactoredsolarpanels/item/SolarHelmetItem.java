package com.refactoredsolarpanels.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.HudMode;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudProvider;
import ic2.api.item.IMetalArmor;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

public class SolarHelmetItem extends ArmorItem implements IElectricItem, IMetalArmor, IItemHudProvider {
    private static final int SKY_CHECK_INTERVAL = 128;
    private static final String TAG_TICKER = "SolarTicker";
    private static final String TAG_STATE = "SolarState";

    private final SolarHelmetTier tier;

    public SolarHelmetItem(SolarHelmetTier tier) {
        super(ArmorMaterials.DIAMOND, Type.HELMET, new Properties().stacksTo(1).rarity(tier.getRarity()));
        this.tier = tier;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        int ticker = tag.getInt(TAG_TICKER) + 1;
        tag.putInt(TAG_TICKER, ticker);
        if (ticker == 1 || ticker % SKY_CHECK_INTERVAL == 0) {
            tag.putInt(TAG_STATE, getGenerationState(level, player.blockPosition()).ordinal());
        }

        if (this.tier != SolarHelmetTier.ADVANCED && player.getAirSupply() < 100 && ElectricItem.manager.canUse(stack, 1000.0D)) {
            player.setAirSupply(player.getMaxAirSupply());
            ElectricItem.manager.use(stack, 1000.0D, player);
        }

        GenerationState state = GenerationState.byId(tag.getInt(TAG_STATE));
        double remaining = switch (state) {
            case DAY -> this.tier.getDayProductionEuTick();
            case NIGHT -> this.tier.getNightProductionEuTick();
            case NONE -> 0.0D;
        };
        if (remaining <= 0.0D) {
            return;
        }

        remaining = chargeList(player.getInventory().armor, remaining);
        remaining = chargeList(player.getInventory().offhand, remaining);
        remaining = chargeList(player.getInventory().items, remaining);
        if (remaining > 0.0D) {
            ElectricItem.manager.charge(stack, remaining, Integer.MAX_VALUE, true, false);
        }
    }

    private double chargeList(List<ItemStack> stacks, double amount) {
        double remaining = amount;
        for (ItemStack target : stacks) {
            if (remaining <= 0.0D) {
                break;
            }
            if (!target.isEmpty() && target.getItem() instanceof IElectricItem && target.getItem() != this) {
                remaining -= ElectricItem.manager.charge(target, remaining, this.tier.getElectricTier(), false, false);
            }
        }
        return Math.max(0.0D, remaining);
    }

    private static GenerationState getGenerationState(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight() || !level.canSeeSky(pos.above())) {
            return GenerationState.NONE;
        }
        if (isDaylight(level, pos)) {
            return GenerationState.DAY;
        }
        return GenerationState.NIGHT;
    }

    private static boolean isDaylight(Level level, BlockPos pos) {
        if (!level.isDay()) {
            return false;
        }
        if (level.isRainingAt(pos)) {
            return false;
        }
        float sunBrightness = Mth.clamp((float) Math.cos(level.getSunAngle(1.0F)) * 2.0F + 0.2F, 0.0F, 1.0F);
        return level.getBrightness(LightLayer.SKY, pos.above()) > 0 && sunBrightness > 0.0F;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.advanced_solar_panels_refactored.production.day", format(this.tier.getDayProductionEuTick())).withStyle(ChatFormatting.GRAY));
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.advanced_solar_panels_refactored.production.night", format(this.tier.getNightProductionEuTick())).withStyle(ChatFormatting.GRAY));
        tooltip.add(net.minecraft.network.chat.Component.literal(ElectricItem.manager.getToolTip(stack)).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.tier.getRarity();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) > 0.0D;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * (float) ElectricItem.manager.getChargeLevel(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb((float) ElectricItem.manager.getChargeLevel(stack) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String suffix = type == null ? "" : "_overlay";
        return "advanced_solar_panels_refactored:textures/armor/" + this.tier.getId() + suffix + ".png";
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return this.tier.getMaxCharge();
    }

    @Override
    public int getTier(ItemStack stack) {
        return this.tier.getElectricTier();
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return this.tier.getTransferLimit();
    }

    @Override
    public boolean isMetalArmor(ItemStack stack, Player player) {
        return true;
    }

    @Override
    public boolean doesProvideHUD(ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) > 0.0D;
    }

    @Override
    public HudMode getHudMode(ItemStack stack) {
        return HudMode.BASIC;
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
}
