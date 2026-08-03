package net.dillon.qualityofqueso.platform.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;

public class FabricClientQualityOfQuesoPlatform extends ClientQualityOfQuesoPlatform {

    @Override
    public KeyMapping registerKeyMapping(String name, InputConstants.Type type, KeyMapping.Category category, int value) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, value, category));
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return ClientPlayNetworking.canSend(GlowSearchC2SPacket.PACKET_TYPE);
    }
}