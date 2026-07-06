package com.refactoredsolarpanels.registry;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.block.MolecularTransformerBlockEntity;
import com.refactoredsolarpanels.block.QuantumGeneratorBlockEntity;
import com.refactoredsolarpanels.block.SolarPanelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AdvancedSolarPanels.MOD_ID);

    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL = BLOCK_ENTITY_TYPES.register(
            "solar_panel",
            () -> BlockEntityType.Builder.of(SolarPanelBlockEntity::new, ModBlocks.getSolarPanelBlocks()).build(null)
    );
    public static final RegistryObject<BlockEntityType<QuantumGeneratorBlockEntity>> QUANTUM_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "quantum_generator",
            () -> BlockEntityType.Builder.of(QuantumGeneratorBlockEntity::new, ModBlocks.QUANTUM_GENERATOR.get()).build(null)
    );
    public static final RegistryObject<BlockEntityType<MolecularTransformerBlockEntity>> MOLECULAR_TRANSFORMER = BLOCK_ENTITY_TYPES.register(
            "molecular_transformer",
            () -> BlockEntityType.Builder.of(MolecularTransformerBlockEntity::new, ModBlocks.MOLECULAR_TRANSFORMER.get()).build(null)
    );

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
