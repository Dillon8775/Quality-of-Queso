package net.dillon.qualityofqueso.platform.client;

import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public class NeoForgeClientQualityOfQuesoPlatform extends ClientQualityOfQuesoPlatform {

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return NetworkRegistry.hasChannel(localPlayer.connection, GlowSearchC2SPacket.ID);
    }
}