package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.LocalPlayer;

import java.nio.file.Path;

public class FabricPlatformHelper implements PlatformHelper {

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return ClientPlayNetworking.canSend(GlowSearchC2SPacket.PACKET_TYPE);
    }
}