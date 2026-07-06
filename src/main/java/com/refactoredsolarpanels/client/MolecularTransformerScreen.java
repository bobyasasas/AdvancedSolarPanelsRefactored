package com.refactoredsolarpanels.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.menu.MolecularTransformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MolecularTransformerScreen extends AbstractContainerScreen<MolecularTransformerMenu> {
    private static final ResourceLocation TEXTURE = AdvancedSolarPanels.id("textures/gui/molecular_transformer.png");
    public static final int PROGRESS_X = 23;
    public static final int PROGRESS_Y = 48;
    public static final int PROGRESS_WIDTH = 10;
    public static final int PROGRESS_HEIGHT = 15;
    private static final int PROGRESS_U = 221;
    private static final int PROGRESS_V = 7;
    private static final int LABEL_RIGHT_X = 107;
    private static final int VALUE_X = 112;

    public MolecularTransformerScreen(MolecularTransformerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 193;
        this.inventoryLabelY = 1000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        int required = Math.max(1, this.menu.getRecipeEnergy());
        int progress = Math.min(PROGRESS_HEIGHT, this.menu.getEnergyUsed() * PROGRESS_HEIGHT / required);
        if (this.menu.getRecipeEnergy() > 0) {
            graphics.blit(TEXTURE, this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y, PROGRESS_U, PROGRESS_V, PROGRESS_WIDTH, progress, 256, 256);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, this.title, this.imageWidth / 2, 9, 0xFFFFFF);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.input").getString(), 26);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.output").getString(), 38);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.energyPerOperation").getString(), 50);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.energyPerTick").getString(), 62);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.progress").getString(), 74);

        graphics.drawString(this.font, itemName(displayInput()), VALUE_X, 26, 0xFFFFFF, false);
        graphics.drawString(this.font, itemName(displayOutput()), VALUE_X, 38, 0xFFFFFF, false);
        graphics.drawString(this.font, this.menu.getRecipeEnergy() <= 0 ? "" : format(this.menu.getRecipeEnergy()) + " EU", VALUE_X, 50, 0xFFFFFF, false);
        graphics.drawString(this.font, this.menu.getRecipeEnergy() <= 0 ? "" : format(this.menu.getLastEnergyInput()) + " EU/t", VALUE_X, 62, 0xFFFFFF, false);
        graphics.drawString(this.font, this.menu.getRecipeEnergy() <= 0 ? "" : (this.menu.getEnergyUsed() * 100 / Math.max(1, this.menu.getRecipeEnergy())) + "%", VALUE_X, 74, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private ItemStack displayInput() {
        ItemStack pendingInput = this.menu.getPendingInputStack();
        return pendingInput.isEmpty() ? this.menu.getInputStack() : pendingInput;
    }

    private ItemStack displayOutput() {
        ItemStack pendingOutput = this.menu.getPendingOutputStack();
        return pendingOutput.isEmpty() ? this.menu.getOutputStack() : pendingOutput;
    }

    private String itemName(ItemStack stack) {
        if (stack.isEmpty()) {
            return "";
        }
        String name = stack.getHoverName().getString();
        return stack.getCount() > 1 ? stack.getCount() + "x " + trim(name) : trim(name);
    }

    private String trim(String text) {
        return this.font.plainSubstrByWidth(text, 100);
    }

    private void drawRightAligned(GuiGraphics graphics, String text, int y) {
        graphics.drawString(this.font, text, LABEL_RIGHT_X - this.font.width(text), y, 0xFFFFFF, false);
    }

    private static String format(int value) {
        return String.format("%,d", value);
    }
}
