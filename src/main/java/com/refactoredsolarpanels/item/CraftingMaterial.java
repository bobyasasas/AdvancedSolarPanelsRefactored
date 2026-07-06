package com.refactoredsolarpanels.item;

public enum CraftingMaterial {
    SUNNARIUM("sunnarium"),
    SUNNARIUM_PART("sunnarium_part"),
    SUNNARIUM_ALLOY("sunnarium_alloy"),
    IRRADIANT_URANIUM("irradiant_uranium"),
    ENRICHED_SUNNARIUM("enriched_sunnarium"),
    ENRICHED_SUNNARIUM_ALLOY("enriched_sunnarium_alloy"),
    IRRADIANT_GLASS_PANE("irradiant_glass_pane"),
    IRIDIUM_IRON_PLATE("iridium_iron_plate"),
    REINFORCED_IRIDIUM_IRON_PLATE("reinforced_iridium_iron_plate"),
    IRRADIANT_REINFORCED_PLATE("irradiant_reinforced_plate"),
    IRIDIUM_INGOT("iridium_ingot"),
    URANIUM_INGOT("uranium_ingot"),
    MT_CORE("mt_core"),
    QUANTUM_CORE("quantum_core");

    private final String id;

    CraftingMaterial(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
