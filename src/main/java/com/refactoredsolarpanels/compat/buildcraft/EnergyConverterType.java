package com.refactoredsolarpanels.compat.buildcraft;

public enum EnergyConverterType {
    LV_ELECTRIC_ENGINE("lv_electric_engine", true, 1, 32.0D, 12.8D, 500.0D, 200.0D),
    MV_ELECTRIC_ENGINE("mv_electric_engine", true, 2, 128.0D, 51.2D, 2000.0D, 800.0D),
    HV_ELECTRIC_ENGINE("hv_electric_engine", true, 3, 512.0D, 204.8D, 8000.0D, 3200.0D),
    EV_ELECTRIC_ENGINE("ev_electric_engine", true, 4, 2048.0D, 819.2D, 16000.0D, 6400.0D),
    LV_PNEUMATIC_TRANSDUCER("lv_pneumatic_transducer", false, 1, 32.0D, 12.8D, 640.0D, 1000.0D),
    MV_PNEUMATIC_TRANSDUCER("mv_pneumatic_transducer", false, 2, 128.0D, 51.2D, 2560.0D, 1000.0D),
    HV_PNEUMATIC_TRANSDUCER("hv_pneumatic_transducer", false, 3, 512.0D, 204.8D, 10240.0D, 1000.0D),
    EV_PNEUMATIC_TRANSDUCER("ev_pneumatic_transducer", false, 4, 2048.0D, 819.2D, 40960.0D, 1000.0D);

    private final String id;
    private final boolean electricEngine;
    private final int ic2Tier;
    private final double euRate;
    private final double mjRate;
    private final double euCapacity;
    private final double mjCapacity;

    EnergyConverterType(String id, boolean electricEngine, int ic2Tier, double euRate, double mjRate, double euCapacity, double mjCapacity) {
        this.id = id;
        this.electricEngine = electricEngine;
        this.ic2Tier = ic2Tier;
        this.euRate = euRate;
        this.mjRate = mjRate;
        this.euCapacity = euCapacity;
        this.mjCapacity = mjCapacity;
    }

    public String getId() {
        return this.id;
    }

    public boolean isElectricEngine() {
        return this.electricEngine;
    }

    public int getIc2Tier() {
        return this.ic2Tier;
    }

    public double getEuRate() {
        return this.euRate;
    }

    public double getMjRate() {
        return this.mjRate;
    }

    public double getEuCapacity() {
        return this.euCapacity;
    }

    public double getMjCapacity() {
        return this.mjCapacity;
    }
}
