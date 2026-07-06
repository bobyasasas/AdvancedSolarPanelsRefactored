package com.refactoredsolarpanels.network;

import com.refactoredsolarpanels.AdvancedSolarPanels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class AdvancedSolarNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static int nextPacketId;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(AdvancedSolarPanels.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private AdvancedSolarNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(QuantumGeneratorActionPacket.class, nextPacketId++)
                .encoder(QuantumGeneratorActionPacket::encode)
                .decoder(QuantumGeneratorActionPacket::decode)
                .consumerMainThread(QuantumGeneratorActionPacket::handle)
                .add();
    }
}
