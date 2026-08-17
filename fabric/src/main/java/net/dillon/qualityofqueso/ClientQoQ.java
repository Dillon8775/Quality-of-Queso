package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.main.ClientMain;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static net.dillon.qualityofqueso.event.FabricClientEvents.registerFabricClientEvents;
import static net.dillon.qualityofqueso.event.FabricClientEvents.registerFabricKeyBindings;
import static net.dillon.qualityofqueso.sound.ModSoundEvents.registerBoundSoundEvents;

@Environment(EnvType.CLIENT)
public class ClientQoQ implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerFabricClientEvents();
        registerFabricKeyBindings();
        registerBoundSoundEvents();

        Balm.initializeMod(ModConstants.MOD_ID, FabricLoadContext.INSTANCE, ClientMain::cInitialize);
    }
}