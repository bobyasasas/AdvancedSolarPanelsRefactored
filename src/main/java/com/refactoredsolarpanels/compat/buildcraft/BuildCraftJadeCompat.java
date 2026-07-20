package com.refactoredsolarpanels.compat.buildcraft;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import java.math.BigDecimal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.config.IPluginConfig;

/** Loaded only when both Jade and BuildCraft are present. */
public final class BuildCraftJadeCompat {
    private static final ResourceLocation CONVERTER_INFO = AdvancedSolarPanels.id("energy_converter");

    private static final String TYPE_KEY = "asprConverterType";
    private static final String STORED_EU_KEY = "asprConverterStoredEu";
    private static final String CAPACITY_EU_KEY = "asprConverterCapacityEu";
    private static final String STORED_MJ_KEY = "asprConverterStoredMj";
    private static final String CAPACITY_MJ_KEY = "asprConverterCapacityMj";
    private static final String EU_RATE_KEY = "asprConverterEuRate";
    private static final String MJ_RATE_KEY = "asprConverterMjRate";
    private static final String ELECTRIC_ENGINE_KEY = "asprElectricEngine";
    private static final String ENABLED_KEY = "asprConverterEnabled";
    private static final String REDSTONE_KEY = "asprConverterRedstone";
    private static final String OPERATIONAL_KEY = "asprConverterOperational";

    private BuildCraftJadeCompat() {
    }

    public static void registerCommon(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ConverterDataProvider.INSTANCE, EnergyConverterBlockEntity.class);
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(CONVERTER_INFO, true);
        registration.registerBlockComponent(ConverterComponentProvider.INSTANCE, EnergyConverterBlock.class);
    }

    private enum ConverterDataProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return CONVERTER_INFO;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof EnergyConverterBlockEntity converter)) {
                return;
            }

            EnergyConverterType type = converter.getConverterType();
            boolean enabled = AdvancedSolarCommonConfig.isBuildCraftConverterEnabled(type.getId());
            boolean redstonePowered = accessor.getLevel().hasNeighborSignal(accessor.getPosition());

            data.putString(TYPE_KEY, type.getId());
            data.putDouble(STORED_EU_KEY, converter.getStoredEu());
            data.putDouble(CAPACITY_EU_KEY, type.getEuCapacity());
            data.putDouble(STORED_MJ_KEY, converter.getStoredMj());
            data.putDouble(CAPACITY_MJ_KEY, type.getMjCapacity());
            data.putDouble(EU_RATE_KEY, type.getEuRate());
            data.putDouble(MJ_RATE_KEY, type.getMjRate());
            data.putBoolean(ELECTRIC_ENGINE_KEY, type.isElectricEngine());
            data.putBoolean(ENABLED_KEY, enabled);
            data.putBoolean(REDSTONE_KEY, redstonePowered);
            data.putBoolean(OPERATIONAL_KEY, converter.isOperational());
        }
    }

    private enum ConverterComponentProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return CONVERTER_INFO;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!config.get(this)) {
                return;
            }

            CompoundTag data = accessor.getServerData();
            if (!data.contains(TYPE_KEY)) {
                return;
            }

            tooltip.add(Component.translatable(
                    "jade.advanced_solar_panels_refactored.converter_stored_eu",
                    format(data.getDouble(STORED_EU_KEY)),
                    format(data.getDouble(CAPACITY_EU_KEY))
            ));
            tooltip.add(Component.translatable(
                    "jade.advanced_solar_panels_refactored.converter_stored_mj",
                    format(data.getDouble(STORED_MJ_KEY)),
                    format(data.getDouble(CAPACITY_MJ_KEY))
            ));

            String rateKey = data.getBoolean(ELECTRIC_ENGINE_KEY)
                    ? "jade.advanced_solar_panels_refactored.converter_rate_eu_to_mj"
                    : "jade.advanced_solar_panels_refactored.converter_rate_mj_to_eu";
            tooltip.add(Component.translatable(
                    rateKey,
                    format(data.getDouble(EU_RATE_KEY)),
                    format(data.getDouble(MJ_RATE_KEY))
            ));
            tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.converter_sides"));

            if (!data.getBoolean(ENABLED_KEY)) {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.converter_config_disabled"));
            } else if (data.getBoolean(REDSTONE_KEY)) {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.redstone_disabled"));
            } else if (data.getBoolean(OPERATIONAL_KEY)) {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.converter_ready"));
            }
        }
    }

    private static String format(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
