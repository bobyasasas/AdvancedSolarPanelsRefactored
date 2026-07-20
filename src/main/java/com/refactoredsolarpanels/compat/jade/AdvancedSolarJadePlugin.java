package com.refactoredsolarpanels.compat.jade;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.block.MolecularTransformerBlock;
import com.refactoredsolarpanels.block.MolecularTransformerBlockEntity;
import com.refactoredsolarpanels.block.QuantumGeneratorBlock;
import com.refactoredsolarpanels.block.QuantumGeneratorBlockEntity;
import com.refactoredsolarpanels.block.SolarPanelBlock;
import com.refactoredsolarpanels.block.SolarPanelBlockEntity;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(AdvancedSolarPanels.MOD_ID)
public final class AdvancedSolarJadePlugin implements IWailaPlugin {
    private static final ResourceLocation SOLAR_PANEL_INFO = AdvancedSolarPanels.id("solar_panel");
    private static final ResourceLocation MOLECULAR_TRANSFORMER_INFO = AdvancedSolarPanels.id("molecular_transformer");
    private static final ResourceLocation QUANTUM_GENERATOR_INFO = AdvancedSolarPanels.id("quantum_generator");

    private static final String STORED_KEY = "asprStoredEu";
    private static final String CAPACITY_KEY = "asprCapacityEu";
    private static final String PRODUCTION_KEY = "asprProductionEu";
    private static final String MAX_OUTPUT_KEY = "asprMaxOutputEu";
    private static final String GENERATING_KEY = "asprGenerating";
    private static final String ACTIVE_KEY = "asprActive";
    private static final String TIER_KEY = "asprTier";
    private static final String ENERGY_USED_KEY = "asprEnergyUsed";
    private static final String RECIPE_ENERGY_KEY = "asprRecipeEnergy";
    private static final String LAST_INPUT_KEY = "asprLastInput";
    private static final String INPUT_STACK_KEY = "asprInput";
    private static final String OUTPUT_STACK_KEY = "asprOutput";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SolarDataProvider.INSTANCE, SolarPanelBlockEntity.class);
        registration.registerBlockDataProvider(MolecularDataProvider.INSTANCE, MolecularTransformerBlockEntity.class);
        registration.registerBlockDataProvider(QuantumDataProvider.INSTANCE, QuantumGeneratorBlockEntity.class);
        if (ModList.get().isLoaded("buildcraftcore")) {
            com.refactoredsolarpanels.compat.buildcraft.BuildCraftJadeCompat.registerCommon(registration);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(SOLAR_PANEL_INFO, true);
        registration.addConfig(MOLECULAR_TRANSFORMER_INFO, true);
        registration.addConfig(QUANTUM_GENERATOR_INFO, true);
        registration.registerBlockComponent(SolarComponentProvider.INSTANCE, SolarPanelBlock.class);
        registration.registerBlockComponent(MolecularComponentProvider.INSTANCE, MolecularTransformerBlock.class);
        registration.registerBlockComponent(QuantumComponentProvider.INSTANCE, QuantumGeneratorBlock.class);
        if (ModList.get().isLoaded("buildcraftcore")) {
            com.refactoredsolarpanels.compat.buildcraft.BuildCraftJadeCompat.registerClient(registration);
        }
    }

    private enum SolarDataProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return SOLAR_PANEL_INFO;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof SolarPanelBlockEntity panel) {
                data.putDouble(STORED_KEY, panel.getStoredEu());
                data.putDouble(CAPACITY_KEY, panel.getCapacityEu());
                data.putDouble(PRODUCTION_KEY, panel.getCurrentProductionEuTick());
                data.putInt(MAX_OUTPUT_KEY, panel.getMaxOutputEuTick());
                data.putBoolean(GENERATING_KEY, panel.isGenerating());
            }
        }
    }

    private enum MolecularDataProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return MOLECULAR_TRANSFORMER_INFO;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof MolecularTransformerBlockEntity transformer) {
                int recipeEnergy = transformer.getRecipeEnergy();
                ItemStack input = transformer.getPendingInput();
                ItemStack output = transformer.getPendingOutput();

                if (output.isEmpty()) {
                    ItemStack slotInput = transformer.getInventory().getStackInSlot(0);
                    if (!slotInput.isEmpty()) {
                        Container container = new SimpleContainer(slotInput);
                        var recipe = accessor.getLevel().getRecipeManager().getRecipeFor(ModRecipeTypes.MOLECULAR_TRANSFORMING.get(), container, accessor.getLevel());
                        if (recipe.isPresent()) {
                            input = slotInput.copyWithCount(recipe.get().getInputCount());
                            output = recipe.get().getResult();
                            recipeEnergy = recipe.get().getEnergy();
                        }
                    }
                }

                data.putDouble(STORED_KEY, transformer.getStoredEu());
                data.putDouble(CAPACITY_KEY, transformer.getCapacityEu());
                data.putInt(ENERGY_USED_KEY, transformer.getEnergyUsed());
                data.putInt(RECIPE_ENERGY_KEY, recipeEnergy);
                data.putDouble(LAST_INPUT_KEY, transformer.getLastEnergyInput());
                data.putBoolean(ACTIVE_KEY, transformer.isActive());
                putStack(data, INPUT_STACK_KEY, input);
                putStack(data, OUTPUT_STACK_KEY, output);
            }
        }
    }

    private enum QuantumDataProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return QUANTUM_GENERATOR_INFO;
        }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof QuantumGeneratorBlockEntity generator) {
                data.putInt(PRODUCTION_KEY, generator.getProduction());
                data.putInt(TIER_KEY, generator.getGeneratorTier());
                data.putBoolean(ACTIVE_KEY, generator.isActive());
            }
        }
    }

    private enum SolarComponentProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return SOLAR_PANEL_INFO;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!config.get(this)) {
                return;
            }

            CompoundTag data = accessor.getServerData();
            if (!data.contains(CAPACITY_KEY)) {
                return;
            }

            tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.stored", format(data.getDouble(STORED_KEY)), format(data.getDouble(CAPACITY_KEY))));
            tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.max_output", format(data.getInt(MAX_OUTPUT_KEY))));
            if (data.getBoolean(GENERATING_KEY)) {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.generation", format(data.getDouble(PRODUCTION_KEY))));
            } else {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.idle"));
            }
        }
    }

    private enum MolecularComponentProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return MOLECULAR_TRANSFORMER_INFO;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!config.get(this)) {
                return;
            }

            CompoundTag data = accessor.getServerData();
            if (!data.contains(CAPACITY_KEY)) {
                return;
            }

            tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.stored", format(data.getDouble(STORED_KEY)), format(data.getDouble(CAPACITY_KEY))));
            ItemStack input = getStack(data, INPUT_STACK_KEY);
            ItemStack output = getStack(data, OUTPUT_STACK_KEY);
            int recipeEnergy = data.getInt(RECIPE_ENERGY_KEY);
            if (!input.isEmpty() && !output.isEmpty() && recipeEnergy > 0) {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.recipe", stackName(input), stackName(output)));
                tooltip.add(Component.translatable(
                        "jade.advanced_solar_panels_refactored.progress",
                        format(data.getInt(ENERGY_USED_KEY)),
                        format(recipeEnergy),
                        data.getInt(ENERGY_USED_KEY) * 100 / Math.max(1, recipeEnergy)
                ));
                if (data.getDouble(LAST_INPUT_KEY) > 0.0D) {
                    tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.energy_input", format(data.getDouble(LAST_INPUT_KEY))));
                }
            } else {
                tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.idle"));
            }
        }
    }

    private enum QuantumComponentProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return QUANTUM_GENERATOR_INFO;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!config.get(this)) {
                return;
            }

            CompoundTag data = accessor.getServerData();
            if (!data.contains(PRODUCTION_KEY)) {
                return;
            }

            tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.quantum_output", format(data.getInt(PRODUCTION_KEY))));
            tooltip.add(Component.translatable("jade.advanced_solar_panels_refactored.tier", data.getInt(TIER_KEY) > 5 ? Component.translatable("advanced_solar_panels_refactored.gui.max") : Component.literal(Integer.toString(data.getInt(TIER_KEY)))));
            tooltip.add(data.getBoolean(ACTIVE_KEY) ? Component.translatable("jade.advanced_solar_panels_refactored.active") : Component.translatable("jade.advanced_solar_panels_refactored.redstone_disabled"));
        }
    }

    private static void putStack(CompoundTag data, String key, ItemStack stack) {
        if (!stack.isEmpty()) {
            data.put(key, stack.save(new CompoundTag()));
        }
    }

    private static ItemStack getStack(CompoundTag data, String key) {
        return data.contains(key) ? ItemStack.of(data.getCompound(key)) : ItemStack.EMPTY;
    }

    private static Component stackName(ItemStack stack) {
        return stack.getCount() > 1 ? Component.literal(stack.getCount() + "x ").append(stack.getHoverName()) : stack.getHoverName();
    }

    private static String format(double value) {
        return Long.toString(Math.round(value));
    }
}
