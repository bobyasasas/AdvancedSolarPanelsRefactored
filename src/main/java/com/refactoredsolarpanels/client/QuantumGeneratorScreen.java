package com.refactoredsolarpanels.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refactoredsolarpanels.AdvancedSolarPanels;
import com.refactoredsolarpanels.menu.QuantumGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class QuantumGeneratorScreen extends AbstractContainerScreen<QuantumGeneratorMenu> {
    private static final ResourceLocation TEXTURE = AdvancedSolarPanels.id("textures/gui/quantum_generator.png");

    public QuantumGeneratorScreen(QuantumGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 193;
        this.inventoryLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        int[][] productionButtons = {
                {6, 40, 32, 20, 0},
                {39, 40, 26, 20, 1},
                {66, 40, 20, 20, 2},
                {89, 40, 20, 20, 3},
                {110, 40, 26, 20, 4},
                {137, 40, 32, 20, 5}
        };
        for (int[] spec : productionButtons) {
            this.addRenderableWidget(Button.builder(Component.literal(labelFor(spec[4], false)), button -> sendProductionEvent(spec[4]))
                    .bounds(this.leftPos + spec[0], this.topPos + spec[1], spec[2], spec[3])
                    .build());
        }
        for (int tier = 1; tier <= 6; tier++) {
            int x = tier == 6 ? 138 : 6 + (tier - 1) * 26;
            int width = tier == 6 ? 32 : 24;
            Component label = tier == 6 ? Component.translatable("advanced_solar_panels_refactored.gui.max") : Component.literal(Integer.toString(tier));
            int event = 20 + tier - 1;
            this.addRenderableWidget(Button.builder(label, button -> sendButtonEvent(event))
                    .bounds(this.leftPos + x, this.topPos + 84, width, 20)
                    .build());
        }
    }

    private void sendProductionEvent(int index) {
        sendButtonEvent((Screen.hasShiftDown() ? 10 : 0) + index);
    }

    private void sendButtonEvent(int event) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, event);
        }
    }

    private static String labelFor(int index, boolean shift) {
        int[] normal = {-100, -10, -1, 1, 10, 100};
        int[] shifted = {-500, -50, -5, 5, 50, 500};
        int value = shift ? shifted[index] : normal[index];
        return value > 0 ? "+" + value : Integer.toString(value);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (this.menu.isActive()) {
            graphics.blit(TEXTURE, this.leftPos + 145, this.topPos + 21, 176, 3, 14, 14, 256, 256);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, this.title, this.imageWidth / 2, 7, 0xFFFFFF);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.output").getString(), 88, 25);
        graphics.drawString(this.font, Component.literal(this.menu.getProduction() + " EU/t"), 95, 25, 0xFFFFFF, false);
        drawRightAligned(graphics, Component.translatable("advanced_solar_panels_refactored.gui.tier").getString(), 88, 69);
        graphics.drawString(this.font, this.menu.getGeneratorTier() > 5 ? Component.translatable("advanced_solar_panels_refactored.gui.max") : Component.literal(Integer.toString(this.menu.getGeneratorTier())), 95, 69, 0xFFFFFF, false);
    }

    private void drawRightAligned(GuiGraphics graphics, String text, int rightX, int y) {
        graphics.drawString(this.font, text, rightX - this.font.width(text), y, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
