package net.dillon.qualityofqueso.platform.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.platform.client.ClientModPlatform;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.KeyMapping;

public abstract class ClientQualityOfQuesoPlatform extends ClientModPlatform {

    @Override
    public String modId() {
        return ModConstants.MOD_ID;
    }

    // Unused
    @Override
    public KeyMapping registerKeyMapping(String name, InputConstants.Type type, KeyMapping.Category category, int value) {
        return null;
    }
}