package net.dillon.qualityofqueso.platform;

import net.dillon.dillonlib.platform.PlatformLoader;
import net.dillon.dillonlib.platform.client.ClientModPlatform;
import net.dillon.qualityofqueso.helper.ModConstants;

public class QualityOfQuesoPlatforms {
    private static final QualityOfQuesoPlatform PLATFORM = PlatformLoader.load(QualityOfQuesoPlatform.class, ModConstants.MOD_ID);
    private static final ClientModPlatform CLIENT_PLATFORM = PlatformLoader.load(ClientModPlatform.class, ModConstants.MOD_ID);

    public static QualityOfQuesoPlatform getPlatform() {
        return PLATFORM;
    }

    public static ClientModPlatform getClientPlatform() {
        return CLIENT_PLATFORM;
    }
}