package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.dillon.qualityofqueso.main.ClientMain;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static net.dillon.qualityofqueso.event.FabricClientEvents.registerFabricClientEvents;
import static net.dillon.qualityofqueso.event.FabricClientEvents.registerFabricKeyBindings;

@Environment(EnvType.CLIENT)
public class ClientQoQ implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Balm.initializeMod(ModUtil.MOD_ID, FabricLoadContext.INSTANCE, ClientMain::cInitialize);

        registerFabricClientEvents();
        registerFabricKeyBindings();
    }
}