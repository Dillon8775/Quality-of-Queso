package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.dillon.qualityofqueso.main.Main;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ModInitializer;

import static net.dillon.qualityofqueso.event.FabricCommonEvents.registerFabricCommands;

/**
 * The server-side (or main) entrypoint for {@code QoQ.}
 */
public class QoQ implements ModInitializer {

    @Override
    public void onInitialize() {
        registerFabricCommands();

        Balm.initializeMod(ModUtil.MOD_ID, FabricLoadContext.INSTANCE, Main::initialize);
    }
}