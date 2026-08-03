package net.dillon.qualityofqueso.platform.client;

import net.dillon.dillonlib.platform.client.ClientModPlatform;
import net.dillon.qualityofqueso.util.ModConstants;

public abstract class ClientQualityOfQuesoPlatform extends ClientModPlatform {

    @Override
    public String modId() {
        return ModConstants.MOD_ID;
    }
}