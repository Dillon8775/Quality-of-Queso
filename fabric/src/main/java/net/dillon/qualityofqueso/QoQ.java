package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.main.CommonMain;
import net.fabricmc.api.ModInitializer;

public class QoQ implements ModInitializer {

    @Override
    public void onInitialize() {
        Balm.initializeMod(ModConstants.MOD_ID, FabricLoadContext.INSTANCE, CommonMain::initialize);
    }
}