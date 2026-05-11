package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.fabricmc.api.ModInitializer;

import static net.dillon.qualityofqueso.event.FabricCommonEvents.registerFabricCommands;

public class QoQ implements ModInitializer {

    @Override
    public void onInitialize() {
        registerFabricCommands();

        Balm.initializeMod(ModConstants.MOD_ID, FabricLoadContext.INSTANCE, CommonMain::initialize);
    }
}