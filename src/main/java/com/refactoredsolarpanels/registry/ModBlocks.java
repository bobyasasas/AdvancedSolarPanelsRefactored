package com.refactoredsolarpanels.registry;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.block.MolecularTransformerBlock;
import com.refactoredsolarpanels.block.QuantumGeneratorBlock;
import com.refactoredsolarpanels.block.SolarPanelBlock;
import com.refactoredsolarpanels.block.SolarPanelTier;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedSolarPanels.MOD_ID);
    public static final Map<SolarPanelTier, RegistryObject<SolarPanelBlock>> SOLAR_PANELS = new EnumMap<>(SolarPanelTier.class);

    public static final RegistryObject<SolarPanelBlock> ADVANCED_SOLAR_PANEL = registerSolarPanel(SolarPanelTier.ADVANCED);
    public static final RegistryObject<SolarPanelBlock> HYBRID_SOLAR_PANEL = registerSolarPanel(SolarPanelTier.HYBRID);
    public static final RegistryObject<SolarPanelBlock> ULTIMATE_SOLAR_PANEL = registerSolarPanel(SolarPanelTier.ULTIMATE);
    public static final RegistryObject<SolarPanelBlock> QUANTUM_SOLAR_PANEL = registerSolarPanel(SolarPanelTier.QUANTUM);
    public static final RegistryObject<QuantumGeneratorBlock> QUANTUM_GENERATOR = BLOCKS.register("quantum_generator", QuantumGeneratorBlock::new);
    public static final RegistryObject<MolecularTransformerBlock> MOLECULAR_TRANSFORMER = BLOCKS.register("molecular_transformer", MolecularTransformerBlock::new);

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static Block[] getSolarPanelBlocks() {
        return SOLAR_PANELS.values().stream().map(RegistryObject::get).toArray(Block[]::new);
    }

    private static RegistryObject<SolarPanelBlock> registerSolarPanel(SolarPanelTier tier) {
        RegistryObject<SolarPanelBlock> block = BLOCKS.register(tier.getId(), () -> new SolarPanelBlock(tier));
        SOLAR_PANELS.put(tier, block);
        return block;
    }
}
