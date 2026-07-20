package com.refactoredsolarpanels.compat.buildcraft;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import com.refactoredsolarpanels.registry.ModCreativeTabs;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BuildCraftConverters {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedSolarPanels.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedSolarPanels.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AdvancedSolarPanels.MOD_ID);

    public static final Map<EnergyConverterType, RegistryObject<EnergyConverterBlock>> CONVERTER_BLOCKS = new EnumMap<>(EnergyConverterType.class);
    public static final Map<EnergyConverterType, RegistryObject<Item>> CONVERTER_ITEMS = new EnumMap<>(EnergyConverterType.class);
    public static final RegistryObject<BlockEntityType<EnergyConverterBlockEntity>> ENERGY_CONVERTER;

    static {
        for (EnergyConverterType type : EnergyConverterType.values()) {
            RegistryObject<EnergyConverterBlock> block = BLOCKS.register(type.getId(), () -> new EnergyConverterBlock(type));
            CONVERTER_BLOCKS.put(type, block);
            CONVERTER_ITEMS.put(type, ITEMS.register(type.getId(), () -> new BlockItem(block.get(), new Item.Properties())));
        }

        ENERGY_CONVERTER = BLOCK_ENTITY_TYPES.register("energy_converter", () -> BlockEntityType.Builder.of(
                EnergyConverterBlockEntity::new,
                CONVERTER_BLOCKS.values().stream().map(RegistryObject::get).toArray(Block[]::new)
        ).build(null));
    }

    private BuildCraftConverters() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        modBus.addListener(BuildCraftConverters::addCreativeItems);
    }

    private static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(ModCreativeTabs.MAIN.getKey())) {
            return;
        }

        for (EnergyConverterType type : EnergyConverterType.values()) {
            if (AdvancedSolarCommonConfig.isBuildCraftConverterEnabled(type.getId())) {
                event.accept(CONVERTER_ITEMS.get(type));
            }
        }
    }
}
