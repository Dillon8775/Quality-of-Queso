package net.dillon.qualityofqueso.platform.client;

import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;

public class FabricClientQualityOfQuesoPlatform extends ClientQualityOfQuesoPlatform {

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return ClientPlayNetworking.canSend(GlowSearchC2SPacket.PACKET_TYPE);
    }
}