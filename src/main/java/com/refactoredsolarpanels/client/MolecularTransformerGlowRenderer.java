package com.refactoredsolarpanels.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.refactoredsolarpanels.block.MolecularTransformerBlock;
import com.refactoredsolarpanels.block.MolecularTransformerBlockEntity;
import com.refactoredsolarpanels.config.AdvancedSolarClientConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class MolecularTransformerGlowRenderer implements BlockEntityRenderer<MolecularTransformerBlockEntity> {
    private static final float CENTER = 0.5F;
    private static final float CORE_Y = 0.5F;

    public MolecularTransformerGlowRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MolecularTransformerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!AdvancedSolarClientConfig.molecularTransformerGlowEffect() || !isRunning(blockEntity.getBlockState())) {
            return;
        }

        Level level = blockEntity.getLevel();
        float time = (level == null ? 0.0F : (float) level.getGameTime()) + partialTick;
        float pulse = 0.5F + 0.5F * Mth.sin(time * 0.2F);
        float flicker = 0.5F + 0.5F * Mth.sin(time * 0.73F + 1.6F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();

        renderGlowCore(consumer, pose, pulse, flicker);
        renderElectricArcs(consumer, pose, time);
    }

    private static boolean isRunning(BlockState state) {
        return state.hasProperty(MolecularTransformerBlock.ACTIVE) && state.getValue(MolecularTransformerBlock.ACTIVE);
    }

    private static void renderGlowCore(VertexConsumer consumer, Matrix4f pose, float pulse, float flicker) {
        drawCrossGlow(consumer, pose, 0.31F + pulse * 0.025F, 40, 130, 255, 52);
        drawCrossGlow(consumer, pose, 0.22F + pulse * 0.02F, 42, 220, 255, 105);
        drawCrossGlow(consumer, pose, 0.12F + flicker * 0.018F, 205, 250, 255, 190);
    }

    private static void drawCrossGlow(VertexConsumer consumer, Matrix4f pose, float radius, int red, int green, int blue, int alpha) {
        drawDoubleSidedQuad(consumer, pose,
                CENTER - radius, CORE_Y - radius, CENTER,
                CENTER + radius, CORE_Y - radius, CENTER,
                CENTER + radius, CORE_Y + radius, CENTER,
                CENTER - radius, CORE_Y + radius, CENTER,
                red, green, blue, alpha);
        drawDoubleSidedQuad(consumer, pose,
                CENTER, CORE_Y - radius, CENTER - radius,
                CENTER, CORE_Y - radius, CENTER + radius,
                CENTER, CORE_Y + radius, CENTER + radius,
                CENTER, CORE_Y + radius, CENTER - radius,
                red, green, blue, alpha);
        drawDoubleSidedQuad(consumer, pose,
                CENTER - radius * 0.85F, CORE_Y, CENTER - radius * 0.85F,
                CENTER + radius * 0.85F, CORE_Y, CENTER - radius * 0.85F,
                CENTER + radius * 0.85F, CORE_Y, CENTER + radius * 0.85F,
                CENTER - radius * 0.85F, CORE_Y, CENTER + radius * 0.85F,
                red, green, blue, alpha / 2);
    }

    private static void renderElectricArcs(VertexConsumer consumer, Matrix4f pose, float time) {
        for (int i = 0; i < 5; i++) {
            float phase = time * 0.13F + i * 1.31F;
            float radius = 0.18F + 0.025F * Mth.sin(time * 0.29F + i);
            float angleA = phase;
            float angleB = phase + 1.05F + 0.18F * Mth.sin(time * 0.37F + i * 0.9F);
            float x1 = CENTER + Mth.cos(angleA) * radius;
            float z1 = CENTER + Mth.sin(angleA) * radius;
            float y1 = CORE_Y + Mth.sin(phase * 1.45F) * 0.15F;
            float x3 = CENTER + Mth.cos(angleB) * radius;
            float z3 = CENTER + Mth.sin(angleB) * radius;
            float y3 = CORE_Y + Mth.cos(phase * 1.35F) * 0.15F;
            float x2 = (x1 + x3) * 0.5F + Mth.sin(phase * 2.7F) * 0.045F;
            float y2 = (y1 + y3) * 0.5F + Mth.cos(phase * 2.1F) * 0.045F;
            float z2 = (z1 + z3) * 0.5F + Mth.cos(phase * 2.4F) * 0.045F;
            int alpha = Mth.clamp((int) (105.0F + 45.0F * Mth.sin(time * 0.61F + i)), 70, 150);

            drawBoltSegment(consumer, pose, x1, y1, z1, x2, y2, z2, 0.008F, alpha);
            drawBoltSegment(consumer, pose, x2, y2, z2, x3, y3, z3, 0.008F, alpha);
        }
    }

    private static void drawBoltSegment(VertexConsumer consumer, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float thickness, int alpha) {
        float dx = x2 - x1;
        float dz = z2 - z1;
        float length = Mth.sqrt(dx * dx + dz * dz);
        float nx = length < 0.001F ? thickness : -dz / length * thickness;
        float nz = length < 0.001F ? 0.0F : dx / length * thickness;

        drawDoubleSidedQuad(consumer, pose,
                x1 + nx, y1, z1 + nz,
                x1 - nx, y1, z1 - nz,
                x2 - nx, y2, z2 - nz,
                x2 + nx, y2, z2 + nz,
                95, 235, 255, alpha);
        drawDoubleSidedQuad(consumer, pose,
                x1, y1 + thickness, z1 + nz,
                x1, y1 - thickness, z1 - nz,
                x2, y2 - thickness, z2 - nz,
                x2, y2 + thickness, z2 + nz,
                205, 250, 255, alpha + 30);
    }

    private static void drawDoubleSidedQuad(VertexConsumer consumer, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int red, int green, int blue, int alpha) {
        vertex(consumer, pose, x1, y1, z1, red, green, blue, alpha);
        vertex(consumer, pose, x2, y2, z2, red, green, blue, alpha);
        vertex(consumer, pose, x3, y3, z3, red, green, blue, alpha);
        vertex(consumer, pose, x4, y4, z4, red, green, blue, alpha);

        vertex(consumer, pose, x4, y4, z4, red, green, blue, alpha);
        vertex(consumer, pose, x3, y3, z3, red, green, blue, alpha);
        vertex(consumer, pose, x2, y2, z2, red, green, blue, alpha);
        vertex(consumer, pose, x1, y1, z1, red, green, blue, alpha);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int red, int green, int blue, int alpha) {
        consumer.vertex(pose, x, y, z).color(red, green, blue, Mth.clamp(alpha, 0, 255)).endVertex();
    }
}
