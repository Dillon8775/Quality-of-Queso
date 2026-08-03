package net.dillon.qualityofqueso.platform.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public class NeoForgeClientQualityOfQuesoPlatform extends ClientQualityOfQuesoPlatform {

    @Override
    public KeyMapping registerKeyMapping(String name, InputConstants.Type type, KeyMapping.Category category, int value) {
        return new KeyMapping(name, value, category);
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return NetworkRegistry.hasChannel(localPlayer.connection, GlowSearchC2SPacket.ID);
    }
}