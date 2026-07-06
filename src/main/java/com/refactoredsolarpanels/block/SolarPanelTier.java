package com.refactoredsolarpanels.block;

public enum SolarPanelTier {
    ADVANCED("advanced_solar_panel", 8.0D, 1.0D, 1, 32000.0D),
    HYBRID("hybrid_solar_panel", 64.0D, 8.0D, 2, 100000.0D),
    ULTIMATE("ultimate_solar_panel", 512.0D, 64.0D, 3, 1000000.0D),
    QUANTUM("quantum_solar_panel", 4096.0D, 2048.0D, 5, 10000000.0D);

    private final String id;
    private final double dayProductionEuTick;
    private final double nightProductionEuTick;
    private final int sourceTier;
    private final double capacityEu;

    SolarPanelTier(String id, double dayProductionEuTick, double nightProductionEuTick, int sourceTier, double capacityEu) {
        this.id = id;
        this.dayProductionEuTick = dayProductionEuTick;
        this.nightProductionEuTick = nightProductionEuTick;
        this.sourceTier = sourceTier;
        this.capacityEu = capacityEu;
    }

    public String getId() {
        return this.id;
    }

    public double getProductionEuTick() {
        return this.dayProductionEuTick;
    }

    public double getDayProductionEuTick() {
        return this.dayProductionEuTick;
    }

    public double getNightProductionEuTick() {
        return this.nightProductionEuTick;
    }

    public int getSourceTier() {
        return this.sourceTier;
    }

    public double getCapacityEu() {
        return this.capacityEu;
    }
}
