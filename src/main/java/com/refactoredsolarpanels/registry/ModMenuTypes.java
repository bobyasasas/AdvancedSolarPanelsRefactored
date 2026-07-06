package com.refactoredsolarpanels.registry;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.menu.MolecularTransformerMenu;
import com.refactoredsolarpanels.menu.QuantumGeneratorMenu;
import com.refactoredsolarpanels.menu.SolarPanelMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AdvancedSolarPanels.MOD_ID);

    public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL = MENU_TYPES.register(
            "solar_panel",
            () -> IForgeMenuType.create(SolarPanelMenu::new)
    );
    public static final RegistryObject<MenuType<QuantumGeneratorMenu>> QUANTUM_GENERATOR = MENU_TYPES.register(
            "quantum_generator",
            () -> IForgeMenuType.create(QuantumGeneratorMenu::new)
    );
    public static final RegistryObject<MenuType<MolecularTransformerMenu>> MOLECULAR_TRANSFORMER = MENU_TYPES.register(
            "molecular_transformer",
            () -> IForgeMenuType.create(MolecularTransformerMenu::new)
    );

    private ModMenuTypes() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
