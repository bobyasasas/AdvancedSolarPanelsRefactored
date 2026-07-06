package com.refactoredsolarpanels.compat.jei;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.block.SolarPanelTier;
import com.refactoredsolarpanels.client.MolecularTransformerScreen;
import com.refactoredsolarpanels.recipe.MolecularTransformerRecipe;
import com.refactoredsolarpanels.registry.ModItems;
import com.refactoredsolarpanels.registry.ModRecipeTypes;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class AdvancedSolarJeiPlugin implements IModPlugin {
    private static final RecipeType<MolecularTransformerRecipe> MOLECULAR_TRANSFORMING = RecipeType.create(AdvancedSolarPanels.MOD_ID, "molecular_transforming", MolecularTransformerRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return AdvancedSolarPanels.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MolecularTransformerCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (SolarPanelTier tier : SolarPanelTier.values()) {
            registration.addItemStackInfo(
                    new ItemStack(ModItems.SOLAR_PANEL_ITEMS.get(tier).get()),
                    Component.translatable(
                            "jei.advanced_solar_panels_refactored.solar_panel.description",
                            Math.round(tier.getProductionEuTick()),
                            Math.round(tier.getCapacityEu())
                    )
            );
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            List<MolecularTransformerRecipe> recipes = minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.MOLECULAR_TRANSFORMING.get());
            registration.addRecipes(MOLECULAR_TRANSFORMING, recipes);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.MOLECULAR_TRANSFORMER.get()), MOLECULAR_TRANSFORMING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                MolecularTransformerScreen.class,
                MolecularTransformerScreen.PROGRESS_X,
                MolecularTransformerScreen.PROGRESS_Y,
                MolecularTransformerScreen.PROGRESS_WIDTH,
                MolecularTransformerScreen.PROGRESS_HEIGHT,
                MOLECULAR_TRANSFORMING
        );
    }

    private static final class MolecularTransformerCategory implements IRecipeCategory<MolecularTransformerRecipe> {
        private static final ResourceLocation TEXTURE = AdvancedSolarPanels.id("textures/gui/molecular_transformer_jei.png");
        private static final int WIDTH = 176;
        private static final int HEIGHT = 80;
        private static final int LABEL_X = 47;
        private static final int VALUE_RIGHT_X = 171;

        private final IDrawable icon;

        private MolecularTransformerCategory(IGuiHelper guiHelper) {
            this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.MOLECULAR_TRANSFORMER.get()));
        }

        @Override
        public RecipeType<MolecularTransformerRecipe> getRecipeType() {
            return MOLECULAR_TRANSFORMING;
        }

        @Override
        public Component getTitle() {
            return Component.translatable("jei.advanced_solar_panels_refactored.molecular_transformer");
        }

        @Override
        public int getWidth() {
            return WIDTH;
        }

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public IDrawable getIcon() {
            return this.icon;
        }

        @Override
        public void setRecipe(IRecipeLayoutBuilder builder, MolecularTransformerRecipe recipe, IFocusGroup focuses) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 22).addIngredients(recipe.getIngredient());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 8, 57).addItemStack(recipe.getResult());
        }

        @Override
        public void draw(MolecularTransformerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
            graphics.blit(TEXTURE, -5, 16, 3, 12, 170, 64, 256, 256);
            graphics.blit(TEXTURE, 11, 43, 176, 2, 12, 11, 256, 256);

            Font font = Minecraft.getInstance().font;
            drawLine(graphics, font, Component.translatable("advanced_solar_panels_refactored.gui.input").getString(), inputName(recipe), 23);
            drawLine(graphics, font, Component.translatable("advanced_solar_panels_refactored.gui.output").getString(), recipe.getResult().getHoverName().getString(), 40);
            drawLine(graphics, font, Component.translatable("advanced_solar_panels_refactored.gui.energyPerOperation").getString(), String.format("%,d EU", recipe.getEnergy()), 57);
        }

        private static void drawLine(GuiGraphics graphics, Font font, String label, String value, int y) {
            graphics.drawString(font, label, LABEL_X, y, 0xCDEFFF, false);
            int valueX = LABEL_X + font.width(label) + 4;
            graphics.drawString(font, trimToWidth(font, value, VALUE_RIGHT_X - valueX), valueX, y, 0xFFFFFF, false);
        }

        private static String inputName(MolecularTransformerRecipe recipe) {
            ItemStack[] inputs = recipe.getIngredient().getItems();
            if (inputs.length == 0) {
                return "";
            }
            String name = inputs[0].getHoverName().getString();
            if (recipe.getInputCount() > 1) {
                return recipe.getInputCount() + "x " + name;
            }
            return name;
        }

        private static String trimToWidth(Font font, String text, int width) {
            if (font.width(text) <= width) {
                return text;
            }
            String suffix = "...";
            return font.plainSubstrByWidth(text, Math.max(0, width - font.width(suffix))) + suffix;
        }
    }
}
