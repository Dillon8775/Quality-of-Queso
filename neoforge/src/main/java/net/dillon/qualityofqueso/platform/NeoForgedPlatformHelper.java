package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.nio.file.Path;
import java.util.List;

public class NeoForgedPlatformHelper implements PlatformHelper {

    @Override
    public String getModVersion() {
        return net.neoforged.fml.ModList.get()
                .getModContainerById(ModConstants.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString().split("\\+", 2)[0])
                .orElse("unknown");
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.NEOFORGE;
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(PlatformMod mod) {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(mod.getId()) != null;
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return NetworkRegistry.hasChannel(localPlayer.connection, GlowSearchC2SPacket.ID);
    }

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