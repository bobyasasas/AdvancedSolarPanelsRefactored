package com.refactoredsolarpanels.registry;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.block.SolarPanelTier;
import com.refactoredsolarpanels.item.CraftingMaterial;
import com.refactoredsolarpanels.item.DoubleStoneSlabItem;
import com.refactoredsolarpanels.item.EnchantableElectricArmor;
import com.refactoredsolarpanels.item.EnchantableElectricTools;
import com.refactoredsolarpanels.item.SolarHelmetItem;
import com.refactoredsolarpanels.item.SolarHelmetTier;
import ic2.core.item.resources.ItemWindRotor;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedSolarPanels.MOD_ID);
    public static final Map<SolarPanelTier, RegistryObject<Item>> SOLAR_PANEL_ITEMS = new EnumMap<>(SolarPanelTier.class);
    public static final List<RegistryObject<Item>> ENCHANTABLE_EQUIPMENT = new ArrayList<>();
    public static final Map<SolarHelmetTier, RegistryObject<Item>> ENCHANTABLE_SOLAR_HELMETS = new EnumMap<>(SolarHelmetTier.class);

    public static final RegistryObject<Item> ADVANCED_SOLAR_PANEL = registerSolarPanelItem(SolarPanelTier.ADVANCED);
    public static final RegistryObject<Item> HYBRID_SOLAR_PANEL = registerSolarPanelItem(SolarPanelTier.HYBRID);
    public static final RegistryObject<Item> ULTIMATE_SOLAR_PANEL = registerSolarPanelItem(SolarPanelTier.ULTIMATE);
    public static final RegistryObject<Item> QUANTUM_SOLAR_PANEL = registerSolarPanelItem(SolarPanelTier.QUANTUM);
    public static final RegistryObject<Item> QUANTUM_GENERATOR = ITEMS.register("quantum_generator", () -> new BlockItem(ModBlocks.QUANTUM_GENERATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOLECULAR_TRANSFORMER = ITEMS.register("molecular_transformer", () -> new BlockItem(ModBlocks.MOLECULAR_TRANSFORMER.get(), new Item.Properties()));
    public static final Map<CraftingMaterial, RegistryObject<Item>> CRAFTING_MATERIALS = new EnumMap<>(CraftingMaterial.class);
    public static final Map<SolarHelmetTier, RegistryObject<Item>> SOLAR_HELMETS = new EnumMap<>(SolarHelmetTier.class);
    public static final RegistryObject<Item> SUNNARIUM = registerCraftingMaterial(CraftingMaterial.SUNNARIUM);
    public static final RegistryObject<Item> SUNNARIUM_PART = registerCraftingMaterial(CraftingMaterial.SUNNARIUM_PART);
    public static final RegistryObject<Item> SUNNARIUM_ALLOY = registerCraftingMaterial(CraftingMaterial.SUNNARIUM_ALLOY);
    public static final RegistryObject<Item> IRRADIANT_URANIUM = registerCraftingMaterial(CraftingMaterial.IRRADIANT_URANIUM);
    public static final RegistryObject<Item> ENRICHED_SUNNARIUM = registerCraftingMaterial(CraftingMaterial.ENRICHED_SUNNARIUM);
    public static final RegistryObject<Item> ENRICHED_SUNNARIUM_ALLOY = registerCraftingMaterial(CraftingMaterial.ENRICHED_SUNNARIUM_ALLOY);
    public static final RegistryObject<Item> IRRADIANT_GLASS_PANE = registerCraftingMaterial(CraftingMaterial.IRRADIANT_GLASS_PANE);
    public static final RegistryObject<Item> IRIDIUM_IRON_PLATE = registerCraftingMaterial(CraftingMaterial.IRIDIUM_IRON_PLATE);
    public static final RegistryObject<Item> REINFORCED_IRIDIUM_IRON_PLATE = registerCraftingMaterial(CraftingMaterial.REINFORCED_IRIDIUM_IRON_PLATE);
    public static final RegistryObject<Item> IRRADIANT_REINFORCED_PLATE = registerCraftingMaterial(CraftingMaterial.IRRADIANT_REINFORCED_PLATE);
    public static final RegistryObject<Item> IRIDIUM_INGOT = registerCraftingMaterial(CraftingMaterial.IRIDIUM_INGOT);
    public static final RegistryObject<Item> URANIUM_INGOT = registerCraftingMaterial(CraftingMaterial.URANIUM_INGOT);
    public static final RegistryObject<Item> MT_CORE = registerCraftingMaterial(CraftingMaterial.MT_CORE);
    public static final RegistryObject<Item> QUANTUM_CORE = registerCraftingMaterial(CraftingMaterial.QUANTUM_CORE);
    public static final RegistryObject<Item> ADVANCED_SOLAR_HELMET = registerSolarHelmet(SolarHelmetTier.ADVANCED);
    public static final RegistryObject<Item> HYBRID_SOLAR_HELMET = registerSolarHelmet(SolarHelmetTier.HYBRID);
    public static final RegistryObject<Item> ULTIMATE_SOLAR_HELMET = registerSolarHelmet(SolarHelmetTier.ULTIMATE);
    public static final RegistryObject<Item> IRIDIUM_ROTOR_BLADE = ITEMS.register("iridium_rotor_blade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRIDIUM_ROTOR = ITEMS.register("iridium_rotor", () -> new ItemWindRotor(
            new Item.Properties().durability(1209600),
            10,
            true,
            1.25F,
            24,
            140,
            AdvancedSolarPanels.id("textures/item/rotor/iridium_rotor_model.png")
    ));
    public static final RegistryObject<Item> DOUBLE_STONE_SLAB = ITEMS.register("double_stone_slab", DoubleStoneSlabItem::new);

    public static final RegistryObject<Item> LAPIS_CHAINSAW = registerEnchantable("lapis_chainsaw", EnchantableElectricTools::chainsaw);
    public static final RegistryObject<Item> LAPIS_DRILL = registerEnchantable("lapis_drill", EnchantableElectricTools::drill);
    public static final RegistryObject<Item> LAPIS_DIAMOND_DRILL = registerEnchantable("lapis_diamond_drill", EnchantableElectricTools::diamondDrill);
    public static final RegistryObject<Item> LAPIS_IRIDIUM_DRILL = registerEnchantable("lapis_iridium_drill", EnchantableElectricTools::iridiumDrill);
    public static final RegistryObject<Item> LAPIS_ELECTRIC_WRENCH = registerEnchantable("lapis_electric_wrench", EnchantableElectricTools::electricWrench);
    public static final RegistryObject<Item> LAPIS_ELECTRIC_TREETAP = registerEnchantable("lapis_electric_treetap", EnchantableElectricTools::electricTreetap);
    public static final RegistryObject<Item> LAPIS_MINING_LASER = registerEnchantable("lapis_mining_laser", EnchantableElectricTools::miningLaser);
    public static final RegistryObject<Item> LAPIS_NANO_SABER = registerEnchantable("lapis_nano_saber", EnchantableElectricTools::nanoSaber);

    public static final RegistryObject<Item> LAPIS_JETPACK_ELECTRIC = registerEnchantable("lapis_jetpack_electric", EnchantableElectricArmor::electricJetpack);
    public static final RegistryObject<Item> LAPIS_BATPACK = registerEnchantable("lapis_batpack", EnchantableElectricArmor::batpack);
    public static final RegistryObject<Item> LAPIS_ADVANCED_BATPACK = registerEnchantable("lapis_advanced_batpack", EnchantableElectricArmor::advancedBatpack);
    public static final RegistryObject<Item> LAPIS_ENERGY_PACK = registerEnchantable("lapis_energy_pack", EnchantableElectricArmor::energyPack);
    public static final RegistryObject<Item> LAPIS_LAPPACK = registerEnchantable("lapis_lappack", EnchantableElectricArmor::lappack);
    public static final RegistryObject<Item> LAPIS_NIGHT_VISION_GOGGLES = registerEnchantable("lapis_night_vision_goggles", EnchantableElectricArmor::nightVisionGoggles);
    public static final RegistryObject<Item> LAPIS_NANO_BOOTS = registerEnchantable("lapis_nano_boots", () -> EnchantableElectricArmor.nano(EquipmentSlot.FEET));
    public static final RegistryObject<Item> LAPIS_NANO_CHESTPLATE = registerEnchantable("lapis_nano_chestplate", () -> EnchantableElectricArmor.nano(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> LAPIS_NANO_HELMET = registerEnchantable("lapis_nano_helmet", () -> EnchantableElectricArmor.nano(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> LAPIS_NANO_LEGGINGS = registerEnchantable("lapis_nano_leggings", () -> EnchantableElectricArmor.nano(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> LAPIS_QUANTUM_BOOTS = registerEnchantable("lapis_quantum_boots", () -> EnchantableElectricArmor.quantum(EquipmentSlot.FEET));
    public static final RegistryObject<Item> LAPIS_QUANTUM_CHESTPLATE = registerEnchantable("lapis_quantum_chestplate", () -> EnchantableElectricArmor.quantum(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> LAPIS_QUANTUM_HELMET = registerEnchantable("lapis_quantum_helmet", () -> EnchantableElectricArmor.quantum(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> LAPIS_QUANTUM_LEGGINGS = registerEnchantable("lapis_quantum_leggings", () -> EnchantableElectricArmor.quantum(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> LAPIS_ADVANCED_SOLAR_HELMET = registerEnchantableSolarHelmet(SolarHelmetTier.ADVANCED);
    public static final RegistryObject<Item> LAPIS_HYBRID_SOLAR_HELMET = registerEnchantableSolarHelmet(SolarHelmetTier.HYBRID);
    public static final RegistryObject<Item> LAPIS_ULTIMATE_SOLAR_HELMET = registerEnchantableSolarHelmet(SolarHelmetTier.ULTIMATE);

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static RegistryObject<Item> registerSolarPanelItem(SolarPanelTier tier) {
        RegistryObject<Item> item = ITEMS.register(tier.getId(), () -> new BlockItem(ModBlocks.SOLAR_PANELS.get(tier).get(), new Item.Properties()));
        SOLAR_PANEL_ITEMS.put(tier, item);
        return item;
    }

    private static RegistryObject<Item> registerCraftingMaterial(CraftingMaterial material) {
        RegistryObject<Item> item = ITEMS.register(material.getId(), () -> new Item(new Item.Properties()));
        CRAFTING_MATERIALS.put(material, item);
        return item;
    }

    private static RegistryObject<Item> registerSolarHelmet(SolarHelmetTier tier) {
        RegistryObject<Item> item = ITEMS.register(tier.getId(), () -> SolarHelmetItem.create(tier));
        SOLAR_HELMETS.put(tier, item);
        return item;
    }

    private static RegistryObject<Item> registerEnchantable(String id, Supplier<? extends Item> supplier) {
        RegistryObject<Item> item = ITEMS.register(id, supplier);
        ENCHANTABLE_EQUIPMENT.add(item);
        return item;
    }

    private static RegistryObject<Item> registerEnchantableSolarHelmet(SolarHelmetTier tier) {
        RegistryObject<Item> item = registerEnchantable("lapis_" + tier.getId(), () -> SolarHelmetItem.createEnchantable(tier));
        ENCHANTABLE_SOLAR_HELMETS.put(tier, item);
        return item;
    }
}
