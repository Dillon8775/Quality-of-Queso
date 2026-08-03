package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.main.CommonMain;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.List;

public class NeoForgeQualityOfQuesoPlatform extends QualityOfQuesoPlatform {

    @Override
    public void addModIds() {
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
    }
}