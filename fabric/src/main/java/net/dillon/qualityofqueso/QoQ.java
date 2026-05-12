package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.dillon.qualityofqueso.main.Main;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ModInitializer;

import static net.dillon.qualityofqueso.event.FabricCommonEvents.registerFabricCommands;

public class QoQ implements ModInitializer {

    @Override
    public void onInitialize() {
        Balm.initializeMod(ModUtil.MOD_ID, EmptyLoadContext.INSTANCE, Main::initialize);

        registerFabricCommands();
    }
}