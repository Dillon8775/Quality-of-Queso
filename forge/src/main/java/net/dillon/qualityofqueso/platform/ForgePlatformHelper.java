package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformHelper implements PlatformHelper {

    @Override
    public String getModVersion() {
        return ModList.get()
                .getModContainerById(ModConstants.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString().split("\\+", 2)[0])
                .orElse("unknown");
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return true;
    }
}