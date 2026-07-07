package com.refactoredsolarpanels.item;

import com.refactoredsolarpanels.block.SolarPanelTier;
import net.minecraft.world.item.Rarity;

public enum SolarHelmetTier {
    ADVANCED("advanced_solar_helmet", SolarPanelTier.ADVANCED, Rarity.UNCOMMON),
    HYBRID("hybrid_solar_helmet", SolarPanelTier.HYBRID, Rarity.RARE),
    ULTIMATE("ultimate_solar_helmet", SolarPanelTier.ULTIMATE, Rarity.EPIC);

    private final String id;
    private final SolarPanelTier panelTier;
    private final Rarity rarity;

    SolarHelmetTier(String id, SolarPanelTier panelTier, Rarity rarity) {
        this.id = id;
        this.panelTier = panelTier;
        this.rarity = rarity;
    }

    public String getId() {
        return this.id;
    }

    public double getDayProductionEuTick() {
        return this.panelTier.getDayProductionEuTick();
    }

    public double getNightProductionEuTick() {
        return this.panelTier.getNightProductionEuTick();
    }

    public Rarity getRarity() {
        return this.rarity;
    }
}
