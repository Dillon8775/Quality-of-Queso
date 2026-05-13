package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.dillon.qualityofqueso.main.ServerMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class ServerQoQ implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        Balm.initializeMod(ModConstants.MOD_ID, EmptyLoadContext.INSTANCE, ServerMain::sInitialize);
    }
}