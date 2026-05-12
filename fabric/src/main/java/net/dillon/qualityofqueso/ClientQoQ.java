package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.dillon.qualityofqueso.event.FabricClientEvents;
import net.dillon.qualityofqueso.main.ClientMain;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static net.dillon.qualityofqueso.sound.ModSoundEvents.registerBoundSoundEvents;

@Environment(EnvType.CLIENT)
public class ClientQoQ implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Balm.initializeMod(ModUtil.MOD_ID, EmptyLoadContext.INSTANCE, ClientMain::cInitialize);

        FabricClientEvents.registerFabricKeyMappings();
        registerBoundSoundEvents();
    }
}