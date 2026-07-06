package com.refactoredsolarpanels.item;

import com.refactoredsolarpanels.block.SolarPanelTier;
import net.minecraft.world.item.Rarity;

public enum SolarHelmetTier {
    ADVANCED("advanced_solar_helmet", SolarPanelTier.ADVANCED, Rarity.UNCOMMON, 3, 1000000.0D, 3000.0D, 800, 0.9D),
    HYBRID("hybrid_solar_helmet", SolarPanelTier.HYBRID, Rarity.RARE, 4, 10000000.0D, 10000.0D, 2000, 1.0D),
    ULTIMATE("ultimate_solar_helmet", SolarPanelTier.ULTIMATE, Rarity.EPIC, 4, 10000000.0D, 10000.0D, 2000, 1.0D);

    private final String id;
    private final SolarPanelTier panelTier;
    private final Rarity rarity;
    private final int electricTier;
    private final double maxCharge;
    private final double transferLimit;
    private final int energyPerDamage;
    private final double damageAbsorptionRatio;

    SolarHelmetTier(String id, SolarPanelTier panelTier, Rarity rarity, int electricTier, double maxCharge, double transferLimit, int energyPerDamage, double damageAbsorptionRatio) {
        this.id = id;
        this.panelTier = panelTier;
        this.rarity = rarity;
        this.electricTier = electricTier;
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.energyPerDamage = energyPerDamage;
        this.damageAbsorptionRatio = damageAbsorptionRatio;
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

    public int getElectricTier() {
        return this.electricTier;
    }

    public double getMaxCharge() {
        return this.maxCharge;
    }

    public double getTransferLimit() {
        return this.transferLimit;
    }

    public int getEnergyPerDamage() {
        return this.energyPerDamage;
    }

    public double getDamageAbsorptionRatio() {
        return this.damageAbsorptionRatio;
    }
}
