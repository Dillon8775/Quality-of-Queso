package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.main.CommonMain;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.List;

public class FabricQualityOfQuesoPlatform extends QualityOfQuesoPlatform {

    @Override
    public void addModIds() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            String modId = mod.getMetadata().getId();
            boolean exclude = false;
            List<String> excludedMods = List.of(
                    "fabric-",
                    "fabricloader",
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
    }
}