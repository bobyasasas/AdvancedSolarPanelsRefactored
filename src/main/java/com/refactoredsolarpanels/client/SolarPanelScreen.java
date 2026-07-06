package com.refactoredsolarpanels.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.menu.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends AbstractContainerScreen<SolarPanelMenu> {
    private static final ResourceLocation TEXTURE = AdvancedSolarPanels.id("textures/gui/advanced_solar_panel.png");
    private static final int ENERGY_BAR_X = 19;
    private static final int ENERGY_BAR_Y = 24;
    private static final int ENERGY_BAR_U = 195;
    private static final int ENERGY_BAR_V = 0;
    private static final int ENERGY_BAR_WIDTH = 24;
    private static final int ENERGY_BAR_HEIGHT = 14;

    public SolarPanelScreen(SolarPanelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 194;
        this.imageHeight = 168;
        this.inventoryLabelY = 1000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        int capacity = Math.max(1, this.menu.getCapacityEu());
        int stored = Math.max(0, this.menu.getStoredEu());
        int barWidth = Math.min(ENERGY_BAR_WIDTH, stored * ENERGY_BAR_WIDTH / capacity);
        if (barWidth > 0) {
            graphics.blit(TEXTURE, this.leftPos + ENERGY_BAR_X, this.topPos + ENERGY_BAR_Y, ENERGY_BAR_U, ENERGY_BAR_V, barWidth, ENERGY_BAR_HEIGHT, 256, 256);
        }

        if (this.menu.getGenerationState() == 1) {
            graphics.blit(TEXTURE, this.leftPos + 24, this.topPos + 41, 210, 15, 14, 14, 256, 256);
        } else if (this.menu.getGenerationState() == 2) {
            graphics.blit(TEXTURE, this.leftPos + 24, this.topPos + 41, 195, 15, 14, 14, 256, 256);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, this.title, this.imageWidth / 2, 7, 0xCDDCCD);
        graphics.drawString(this.font, Component.translatable("advanced_solar_panels_refactored.gui.storage").getString() + " " + format(this.menu.getStoredEu()) + "/" + format(this.menu.getCapacityEu()) + " EU", 50, 22, 0xCDDCCD, false);
        graphics.drawString(this.font, Component.translatable("advanced_solar_panels_refactored.gui.maxOutput").getString() + " " + this.menu.getMaxOutputEuTick() + " EU/t", 50, 32, 0xCDDCCD, false);
        graphics.drawString(this.font, Component.translatable("advanced_solar_panels_refactored.gui.generating").getString() + " " + this.menu.getProductionEuTick() + " EU/t", 50, 42, 0xCDDCCD, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private static String format(int value) {
        return Integer.toString(value);
    }
}
