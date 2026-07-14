package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.List;

public class ForgePlatformHelper implements PlatformHelper {

    @Override
    public String getModVersion() {
        return ModList.get()
                .getModContainerById(ModConstants.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString().split("[+-]", 2)[0])
                .orElse("unknown");
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.FORGE;
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(PlatformMod mod) {
        return FMLLoader.getLoadingModList().getModFileById(mod.getId()) != null;
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return true;
    }

    @Override
    public void addModIds() {
        for (IModInfo mod : ModList.get().getMods()) {
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