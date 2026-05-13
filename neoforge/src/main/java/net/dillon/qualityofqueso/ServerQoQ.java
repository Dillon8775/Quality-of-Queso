package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.dillon.qualityofqueso.main.ServerMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ModConstants.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class ServerQoQ {

    public ServerQoQ(ModContainer container, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initializeMod(ModConstants.MOD_ID, context, ServerMain::sInitialize);
    }
}