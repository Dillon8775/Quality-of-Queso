package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import java.util.List;

@Mod(ModConstants.MOD_ID)
public final class QoQ {

    public QoQ(ModContainer container, IEventBus modEventBus) {
        for (ModContainer mod : ModList.get().getSortedMods()) {
            String modId = mod.getModId();
            boolean exclude = false;
            List<String> excludedMods = List.of(
                    "java",
                    "mixinextras"
            );
            for (String excludedMod : excludedMods) {
                if (modId.startsWith(excludedMod)) {
                    exclude = true;
                    break;
                }
            }

            if (!exclude) {
                CommonMain.MOD_IDS.add(modId);
            }
        }

        final var context = new NeoForgeLoadContext(container, modEventBus);
        Balm.initializeMod(ModConstants.MOD_ID, context, CommonMain::initialize);
    }
}
