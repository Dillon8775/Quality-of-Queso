package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.dillon.qualityofqueso.main.Main;
import net.dillon.qualityofqueso.util.ModUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ModUtil.MOD_ID)
public final class QoQ {

    public QoQ(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initializeMod(ModUtil.MOD_ID, context, Main::initialize);


    }
}