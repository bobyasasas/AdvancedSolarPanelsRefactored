package com.refactoredsolarpanels.registry;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedSolarPanels.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.advanced_solar_panels_refactored.main"))
            .icon(() -> new ItemStack(ModItems.ADVANCED_SOLAR_PANEL.get()))
            .displayItems((parameters, output) -> {
                ModItems.SOLAR_PANEL_ITEMS.values().forEach(item -> output.accept(item.get()));
                output.accept(ModItems.QUANTUM_GENERATOR.get());
                output.accept(ModItems.MOLECULAR_TRANSFORMER.get());
                ModItems.CRAFTING_MATERIALS.values().forEach(item -> output.accept(item.get()));
                output.accept(ModItems.IRIDIUM_ROTOR_BLADE.get());
                output.accept(ModItems.IRIDIUM_ROTOR.get());
                ModItems.SOLAR_HELMETS.values().forEach(item -> output.accept(item.get()));
                ModItems.ENCHANTABLE_EQUIPMENT.forEach(item -> output.accept(item.get()));
                output.accept(ModItems.DOUBLE_STONE_SLAB.get());
            })
            .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
