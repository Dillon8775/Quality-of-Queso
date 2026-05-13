package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.forge.ForgeLoadContext;
import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModConstants.MOD_ID)
public final class QoQ {

    public QoQ(FMLJavaModLoadingContext context) {
        final var forgeLoadContext = new ForgeLoadContext(context.getModEventBus());
        Balm.initializeMod(ModConstants.MOD_ID, forgeLoadContext, CommonMain::initialize);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientQoQ::init);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerQoQ::init);
    }
}