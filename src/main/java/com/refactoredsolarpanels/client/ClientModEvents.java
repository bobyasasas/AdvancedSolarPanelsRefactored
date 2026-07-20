package com.refactoredsolarpanels.client;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.registry.ModBlockEntities;
import com.refactoredsolarpanels.registry.ModMenuTypes;
import com.refactoredsolarpanels.registry.ModItems;
import ic2.core.item.tool.AbstractItemNanoSaber;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AdvancedSolarPanels.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.SOLAR_PANEL.get(), SolarPanelScreen::new);
            MenuScreens.register(ModMenuTypes.QUANTUM_GENERATOR.get(), QuantumGeneratorScreen::new);
            MenuScreens.register(ModMenuTypes.MOLECULAR_TRANSFORMER.get(), MolecularTransformerScreen::new);
            BlockEntityRenderers.register(ModBlockEntities.MOLECULAR_TRANSFORMER.get(), MolecularTransformerGlowRenderer::new);
            ItemProperties.register(ModItems.LAPIS_NANO_SABER.get(),
                    new net.minecraft.resources.ResourceLocation("ic2", "is_activated"),
                    (stack, level, entity, seed) -> AbstractItemNanoSaber.isActive(stack) ? 1.0F : 0.0F);
        });
    }
}
