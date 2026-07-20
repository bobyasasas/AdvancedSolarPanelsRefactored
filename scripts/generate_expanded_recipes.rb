#!/usr/bin/env ruby
# frozen_string_literal: true

require "fileutils"
require "json"

MOD_ID = "advanced_solar_panels_refactored"
OUTPUT_ROOT = File.expand_path("../src/main/resources/data/#{MOD_ID}/recipes/expanded", __dir__)

def ingredient(id, count = nil)
  key = id.start_with?("#") ? "tag" : "item"
  value = { key => id.delete_prefix("#") }
  value["count"] = count if count
  value
end

def item(id, count = nil)
  value = { "item" => id }
  value["count"] = count if count
  value
end

def conditional(feature, recipe)
  {
    "type" => "forge:conditional",
    "recipes" => [
      {
        "conditions" => [
          {
            "type" => "#{MOD_ID}:expanded_recipe_enabled",
            "feature" => feature
          }
        ],
        "recipe" => recipe
      }
    ]
  }
end

def write_recipe(relative_path, feature, recipe)
  path = File.join(OUTPUT_ROOT, "#{relative_path}.json")
  FileUtils.mkdir_p(File.dirname(path))
  File.write(path, JSON.pretty_generate(conditional(feature, recipe)) + "\n")
end

def machine_recipe(type, input_id, output_id, input_count: nil, output_count: nil, **extra)
  {
    "type" => "ic2:#{type}",
    "ingredient" => ingredient(input_id, input_count),
    "result" => item(output_id, output_count)
  }.merge(extra.transform_keys(&:to_s))
end

def molecular_recipe(input_id, output_id, energy:, input_count: 1, output_count: 1)
  recipe = {
    "type" => "#{MOD_ID}:molecular_transforming",
    "ingredient" => ingredient(input_id),
    "result" => item(output_id, output_count == 1 ? nil : output_count),
    "energy" => energy
  }
  recipe["input_count"] = input_count if input_count > 1
  recipe
end

def weighted_macerator(input_id, results)
  {
    "type" => "ic2:macerator",
    "ingredient" => ingredient(input_id),
    "weighted" => true,
    "result" => results.map { |id, count, weight| { "item" => id, "count" => count, "weight" => weight } }
  }
end

# Material and Minecraft-version compatibility recipes.
[
  ["cherry_logs_to_planks", "#minecraft:cherry_logs", "minecraft:cherry_planks"],
  ["crimson_stems_to_planks", "#minecraft:crimson_stems", "minecraft:crimson_planks"],
  ["warped_stems_to_planks", "#minecraft:warped_stems", "minecraft:warped_planks"],
  ["rubber_logs_to_planks", "#ic2:rubber_logs", "ic2:rubber_planks"]
].each do |name, input_id, output_id|
  write_recipe("materials/block_cutter/#{name}", "materials",
               machine_recipe("block_cutter", input_id, output_id, output_count: 6, hardness: 2))
end
write_recipe("materials/block_cutter/bamboo_block_to_planks", "materials",
             machine_recipe("block_cutter", "minecraft:bamboo_block", "minecraft:bamboo_planks", output_count: 3, hardness: 2))
write_recipe("materials/macerator/tuff_to_gravel", "materials",
             machine_recipe("macerator", "minecraft:tuff", "minecraft:gravel"))

colors = %w[white orange magenta light_blue yellow lime pink gray light_gray cyan purple blue brown green red black]
([nil] + colors).each do |color|
  prefix = color ? "#{color}_" : ""
  write_recipe("materials/macerator/#{prefix}terracotta_to_clay_dust", "materials",
               machine_recipe("macerator", "minecraft:#{prefix}terracotta", "ic2:clay_dust", output_count: 4))
end
colors.each do |color|
  write_recipe("materials/macerator/#{color}_concrete_to_powder", "materials",
               machine_recipe("macerator", "minecraft:#{color}_concrete", "minecraft:#{color}_concrete_powder"))
end

write_recipe("materials/macerator/bone_block_to_bone_meal", "materials",
             machine_recipe("macerator", "minecraft:bone_block", "minecraft:bone_meal", output_count: 9))
write_recipe("materials/extractor/magma_block_to_magma_cream", "materials",
             machine_recipe("extractor", "minecraft:magma_block", "minecraft:magma_cream", output_count: 4))
write_recipe("materials/extractor/slime_block_to_slime_balls", "materials",
             machine_recipe("extractor", "minecraft:slime_block", "minecraft:slime_ball", output_count: 9))
write_recipe("materials/extractor/sea_lantern_to_prismarine_crystals", "materials",
             machine_recipe("extractor", "minecraft:sea_lantern", "minecraft:prismarine_crystals", output_count: 5))
write_recipe("materials/extractor/glow_lichen_to_glowstone_dust", "materials",
             machine_recipe("extractor", "minecraft:glow_lichen", "minecraft:glowstone_dust", input_count: 8))
write_recipe("materials/extractor/shroomlight_to_glowstone_dust", "materials",
             machine_recipe("extractor", "minecraft:shroomlight", "minecraft:glowstone_dust", output_count: 4))

[
  ["bone_meal_to_bone_block", "minecraft:bone_meal", 9, "minecraft:bone_block"],
  ["magma_cream_to_magma_block", "minecraft:magma_cream", 4, "minecraft:magma_block"],
  ["slime_balls_to_slime_block", "minecraft:slime_ball", 9, "minecraft:slime_block"],
  ["wheat_to_hay_block", "minecraft:wheat", 9, "minecraft:hay_block"],
  ["dried_kelp_to_block", "minecraft:dried_kelp", 9, "minecraft:dried_kelp_block"],
  ["melon_slices_to_melon", "minecraft:melon_slice", 9, "minecraft:melon"],
  ["prismarine_shards_to_prismarine", "minecraft:prismarine_shard", 4, "minecraft:prismarine"]
].each do |name, input_id, count, output_id|
  write_recipe("materials/compressor/#{name}", "materials",
               machine_recipe("compressor", input_id, output_id, input_count: count))
end

[
  ["cake_filled_tin_can", "minecraft:cake", 1, 14],
  ["pufferfish_filled_tin_can", "minecraft:pufferfish", 1, 1]
].each do |name, food_id, food_count, can_count|
  write_recipe("materials/canner_bottle/#{name}", "materials", {
    "type" => "ic2:canner_bottle",
    "container_ingredient" => ingredient("ic2:tin_can", can_count),
    "fill_ingredient" => ingredient(food_id, food_count == 1 ? nil : food_count),
    "result" => item("ic2:filled_tin_can", can_count)
  })
end

# Ore and Ancient Debris processing.
ore_recipes = {
  "coal_ores_to_coal" => ["#minecraft:coal_ores", [["minecraft:coal", 2, 6], ["minecraft:coal", 3, 4]]],
  "diamond_ores_to_diamonds" => ["#minecraft:diamond_ores", [["minecraft:diamond", 2, 7], ["minecraft:diamond", 3, 3]]],
  "emerald_ores_to_emeralds" => ["#minecraft:emerald_ores", [["minecraft:emerald", 2, 7], ["minecraft:emerald", 3, 3]]],
  "lapis_ores_to_lapis" => ["#minecraft:lapis_ores", [["minecraft:lapis_lazuli", 8, 6], ["minecraft:lapis_lazuli", 12, 3], ["minecraft:lapis_lazuli", 16, 1]]],
  "redstone_ores_to_redstone" => ["#minecraft:redstone_ores", [["minecraft:redstone", 8, 6], ["minecraft:redstone", 12, 3], ["minecraft:redstone", 16, 1]]],
  "nether_quartz_ore_to_quartz" => ["minecraft:nether_quartz_ore", [["minecraft:quartz", 3, 6], ["minecraft:quartz", 4, 3], ["minecraft:quartz", 5, 1]]],
  "nether_gold_ore_to_nuggets" => ["minecraft:nether_gold_ore", [["minecraft:gold_nugget", 9, 6], ["minecraft:gold_nugget", 12, 3], ["minecraft:gold_nugget", 18, 1]]]
}
ore_recipes.each do |name, (input_id, results)|
  write_recipe("ore_processing/macerator/#{name}", "ore_processing", weighted_macerator(input_id, results))
end

write_recipe("ore_processing/blast_furnace/ancient_debris_to_netherite_scrap", "ore_processing", {
  "type" => "ic2:blast_furnace",
  "ingredient" => ingredient("minecraft:ancient_debris"),
  "result" => [item("minecraft:netherite_scrap"), item("ic2:slag")],
  "fluid" => 1,
  "duration" => 12_000
})
write_recipe("ore_processing/macerator/ancient_debris_to_crushed_ancient_debris", "ore_processing",
             machine_recipe("macerator", "minecraft:ancient_debris", "#{MOD_ID}:crushed_ancient_debris", output_count: 2))
write_recipe("ore_processing/ore_washer/crushed_to_purified_ancient_debris", "ore_processing", {
  "type" => "ic2:ore_washer",
  "ingredient" => ingredient("#{MOD_ID}:crushed_ancient_debris"),
  "result" => [
    item("#{MOD_ID}:purified_ancient_debris"),
    item("ic2:small_gold_dust", 2),
    item("ic2:netherrack_dust")
  ],
  "amount" => 1000
})
write_recipe("ore_processing/centrifuge/purified_ancient_debris_to_netherite_scrap", "ore_processing", {
  "type" => "ic2:centrifuge",
  "ingredient" => ingredient("#{MOD_ID}:purified_ancient_debris"),
  "result" => item("minecraft:netherite_scrap"),
  "minHeat" => 2000
})

# Post-1.12 plants processed as IC2 biomass feedstock.
biomass_inputs = [
  ["beetroot", "minecraft:beetroot", 8],
  ["beetroot_seeds", "minecraft:beetroot_seeds", 16],
  ["sweet_berries", "minecraft:sweet_berries", 8],
  ["glow_berries", "minecraft:glow_berries", 8],
  ["kelp", "minecraft:kelp", 8],
  ["dried_kelp", "minecraft:dried_kelp", 8],
  ["bamboo", "minecraft:bamboo", 8],
  ["mangrove_roots", "minecraft:mangrove_roots", 4],
  ["azalea", "minecraft:azalea", 4],
  ["flowering_azalea", "minecraft:flowering_azalea", 4],
  ["torchflower_seeds", "minecraft:torchflower_seeds", 16],
  ["pitcher_pod", "minecraft:pitcher_pod", 16],
  ["torchflower", "minecraft:torchflower", 8],
  ["pitcher_plant", "minecraft:pitcher_plant", 4],
  ["crimson_roots", "minecraft:crimson_roots", 8],
  ["warped_roots", "minecraft:warped_roots", 8],
  ["nether_sprouts", "minecraft:nether_sprouts", 8],
  ["weeping_vines", "minecraft:weeping_vines", 8],
  ["twisting_vines", "minecraft:twisting_vines", 8],
  ["moss_block", "minecraft:moss_block", 2],
  ["moss_carpet", "minecraft:moss_carpet", 8]
]
biomass_inputs.each do |name, input_id, count|
  write_recipe("biomass/macerator/#{name}_to_bio_chaff", "biomass",
               machine_recipe("macerator", input_id, "ic2:bio_chaff", input_count: count))
end

# Dye extraction. Single and double-height flowers use a consistent 2x vanilla yield.
dye_inputs = [
  ["poppy", "red", 2], ["red_tulip", "red", 2], ["beetroot", "red", 2], ["rose_bush", "red", 4],
  ["orange_tulip", "orange", 2], ["torchflower", "orange", 2],
  ["dandelion", "yellow", 2], ["sunflower", "yellow", 4],
  ["sea_pickle", "lime", 2], ["cactus", "green", 2], ["pitcher_plant", "cyan", 4],
  ["blue_orchid", "light_blue", 2], ["cornflower", "blue", 2],
  ["allium", "magenta", 2], ["lilac", "magenta", 4],
  ["pink_tulip", "pink", 2], ["peony", "pink", 4],
  ["lily_of_the_valley", "white", 2],
  ["azure_bluet", "light_gray", 2], ["oxeye_daisy", "light_gray", 2], ["white_tulip", "light_gray", 2],
  ["wither_rose", "black", 2], ["ink_sac", "black", 2], ["cocoa_beans", "brown", 2]
]
dye_inputs.each do |input_name, dye_color, count|
  write_recipe("dye_extraction/extractor/#{input_name}_to_#{dye_color}_dye", "dye_extraction",
               machine_recipe("extractor", "minecraft:#{input_name}", "minecraft:#{dye_color}_dye", output_count: count))
end

# Balanced Molecular Transformer additions.
molecular = [
  ["dirt_to_mud", "molecular_transformation", "minecraft:dirt", 1, "minecraft:mud", 1, 50_000],
  ["sand_to_red_sand", "molecular_transformation", "minecraft:sand", 1, "minecraft:red_sand", 1, 100_000],
  ["cobbled_deepslate_to_tuff", "molecular_transformation", "minecraft:cobbled_deepslate", 1, "minecraft:tuff", 1, 100_000],
  ["tuff_to_calcite", "molecular_transformation", "minecraft:tuff", 1, "minecraft:calcite", 1, 250_000],
  ["calcite_to_amethyst_block", "molecular_transformation", "minecraft:calcite", 4, "minecraft:amethyst_block", 1, 2_000_000],
  ["basalt_to_blackstone", "molecular_transformation", "minecraft:basalt", 1, "minecraft:blackstone", 1, 100_000],
  ["blackstone_to_obsidian", "molecular_transformation", "minecraft:blackstone", 4, "minecraft:obsidian", 1, 500_000],
  ["obsidian_to_crying_obsidian", "molecular_transformation", "minecraft:obsidian", 1, "minecraft:crying_obsidian", 1, 1_000_000],
  ["soul_sand_to_soul_soil", "molecular_transformation", "minecraft:soul_sand", 1, "minecraft:soul_soil", 1, 100_000],
  ["netherrack_to_basalt", "molecular_transformation", "minecraft:netherrack", 4, "minecraft:basalt", 1, 250_000],
  ["glass_to_quartz", "molecular_transformation", "minecraft:glass", 4, "minecraft:quartz", 1, 500_000],
  ["prismarine_crystals_to_amethyst_shard", "molecular_transformation", "minecraft:prismarine_crystals", 4, "minecraft:amethyst_shard", 1, 500_000],
  ["glowstone_to_shroomlight", "molecular_transformation", "minecraft:glowstone", 1, "minecraft:shroomlight", 1, 500_000],
  ["rotten_flesh_to_leather", "molecular_transformation", "minecraft:rotten_flesh", 4, "minecraft:leather", 1, 250_000],
  ["popped_chorus_fruit_to_ender_pearl", "molecular_transformation", "minecraft:popped_chorus_fruit", 4, "minecraft:ender_pearl", 1, 1_000_000],
  ["ender_pearls_to_shulker_shell", "renewable_shulker_shells", "minecraft:ender_pearl", 4, "minecraft:shulker_shell", 1, 25_000_000],
  ["nautilus_shells_to_heart_of_the_sea", "renewable_hearts_of_the_sea", "minecraft:nautilus_shell", 8, "minecraft:heart_of_the_sea", 1, 50_000_000],
  ["phantom_membranes_to_elytra", "renewable_elytra", "minecraft:phantom_membrane", 16, "minecraft:elytra", 1, 250_000_000],
  ["sculk_catalysts_to_echo_shard", "renewable_echo_shards", "minecraft:sculk_catalyst", 8, "minecraft:echo_shard", 1, 50_000_000],
  ["amethyst_block_to_budding_amethyst", "renewable_budding_amethyst", "minecraft:amethyst_block", 1, "minecraft:budding_amethyst", 1, 25_000_000],
  ["diamond_to_netherite_scrap", "netherite_from_diamonds", "minecraft:diamond", 1, "minecraft:netherite_scrap", 1, 25_000_000],
  ["copper_ingot_to_gold_ingot", "gold_from_copper", "minecraft:copper_ingot", 1, "minecraft:gold_ingot", 1, 2_000_000],
  ["emerald_to_diamond", "diamonds_from_emeralds", "minecraft:emerald", 1, "minecraft:diamond", 1, 12_000_000]
]
molecular.each do |name, feature, input_id, input_count, output_id, output_count, energy|
  write_recipe("molecular_transforming/#{name}", feature,
               molecular_recipe(input_id, output_id, input_count: input_count, output_count: output_count, energy: energy))
end

# Optional intermediate materials that connect modern vanilla resources to the add-on's progression.
write_recipe("intermediate_materials/block_cutter/amethyst_block_to_crystalline_solar_lens", "intermediate_materials",
             machine_recipe("block_cutter", "minecraft:amethyst_block", "#{MOD_ID}:crystalline_solar_lens", output_count: 4, hardness: 4))
write_recipe("intermediate_materials/crafting/irradiant_glass_pane_from_lens", "intermediate_materials", {
  "type" => "ic2:shaped",
  "pattern" => ["GGG", "ULU", "GGG"],
  "key" => {
    "G" => ingredient("ic2:reinforced_glass"),
    "U" => ingredient("#{MOD_ID}:irradiant_uranium"),
    "L" => ingredient("#{MOD_ID}:crystalline_solar_lens")
  },
  "result" => item("#{MOD_ID}:irradiant_glass_pane")
})
write_recipe("intermediate_materials/metal_former_rolling/netherite_ingot_to_plate", "intermediate_materials",
             machine_recipe("metal_former_rolling", "minecraft:netherite_ingot", "#{MOD_ID}:netherite_plate"))
write_recipe("intermediate_materials/crafting/reinforced_iridium_iron_plate_from_netherite", "intermediate_materials", {
  "type" => "ic2:shaped",
  "pattern" => ["ACA", "CIC", "ACA"],
  "key" => {
    "A" => ingredient("ic2:alloy"),
    "C" => ingredient("#{MOD_ID}:netherite_plate"),
    "I" => ingredient("#{MOD_ID}:iridium_iron_plate")
  },
  "result" => item("#{MOD_ID}:reinforced_iridium_iron_plate", 2)
})

puts "Generated #{Dir.glob(File.join(OUTPUT_ROOT, "**/*.json")).length} expanded recipe files in #{OUTPUT_ROOT}"
