package com.refactoredsolarpanels;

import com.mojang.logging.LogUtils;
import com.refactoredsolarpanels.config.AdvancedSolarClientConfig;
import com.refactoredsolarpanels.config.AdvancedSolarCommonConfig;
import com.refactoredsolarpanels.recipe.MachineEnabledCondition;
import com.refactoredsolarpanels.registry.ModBlockEntities;
import com.refactoredsolarpanels.registry.ModBlocks;
import com.refactoredsolarpanels.registry.ModCreativeTabs;
import com.refactoredsolarpanels.registry.ModItems;
import com.refactoredsolarpanels.registry.ModMenuTypes;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import com.refactoredsolarpanels.network.AdvancedSolarNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AdvancedSolarPanels.MOD_ID)
public final class AdvancedSolarPanels {
    public static final String MOD_ID = "advanced_solar_panels_refactored";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AdvancedSolarPanels(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenuTypes.register(modBus);
        ModRecipeTypes.register(modBus);
        ModCreativeTabs.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AdvancedSolarCommonConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AdvancedSolarClientConfig.SPEC);
        MachineEnabledCondition.register();
        if (ModList.get().isLoaded("buildcraftcore")) {
            com.refactoredsolarpanels.compat.buildcraft.BuildCraftConverters.register(modBus);
        }
        modBus.addListener(this::commonSetup);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(AdvancedSolarNetwork::register);
        LOGGER.info("Loaded Advanced Solar Panels: Refactored");
    }
}
