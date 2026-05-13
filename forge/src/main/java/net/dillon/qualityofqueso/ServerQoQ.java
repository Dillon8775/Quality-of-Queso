package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.forge.ForgeLoadContext;
import net.dillon.qualityofqueso.main.ServerMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ServerQoQ {

    protected static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        final var context = new ForgeLoadContext(modEventBus);
        Balm.initializeMod(ModConstants.MOD_ID, context, ServerMain::sInitialize);
    }
}