package com.refactoredsolarpanels.network;

import com.refactoredsolarpanels.block.QuantumGeneratorBlockEntity;
import com.refactoredsolarpanels.menu.QuantumGeneratorMenu;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record QuantumGeneratorActionPacket(BlockPos pos, int event) {
    public static void encode(QuantumGeneratorActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeVarInt(packet.event);
    }

    public static QuantumGeneratorActionPacket decode(FriendlyByteBuf buffer) {
        return new QuantumGeneratorActionPacket(buffer.readBlockPos(), buffer.readVarInt());
    }

    public static void handle(QuantumGeneratorActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        if (player != null && player.containerMenu instanceof QuantumGeneratorMenu menu && menu.getBlockPos().equals(packet.pos)
                && player.level().getBlockEntity(packet.pos) instanceof QuantumGeneratorBlockEntity blockEntity) {
            blockEntity.handleGuiEvent(packet.event);
            menu.broadcastChanges();
        }
        context.setPacketHandled(true);
    }
}
