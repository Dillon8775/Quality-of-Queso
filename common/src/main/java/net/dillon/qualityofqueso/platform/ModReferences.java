package net.dillon.qualityofqueso.platform;

import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.platform.info.ModReference;

public class ModReferences {
    public static final ModReference YACL = new ModReference("yet_another_config_lib_v3");
    public static final ModReference VIAFABRICPLUS = new ModReference("viafabricplus");
    public static final ModReference MOD_MENU = new ModReference("mod_menu");

    public static boolean isModLoaded(ModReference reference) {
        return Platforms.getDillonLibMixinPlatform().isModLoaded(reference);
    }
}