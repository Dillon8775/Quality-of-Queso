package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.main.ServerMain;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class ServerQoQ implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        Balm.initializeMod(ModConstants.MOD_ID, FabricLoadContext.INSTANCE, ServerMain::sInitialize);
    }
}