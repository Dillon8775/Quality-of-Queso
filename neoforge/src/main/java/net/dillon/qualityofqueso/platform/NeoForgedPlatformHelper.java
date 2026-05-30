package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.nio.file.Path;

public class NeoForgedPlatformHelper implements PlatformHelper {

    @Override
    public String getModVersion() {
        return net.neoforged.fml.ModList.get()
                .getModContainerById(ModConstants.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString().split("[+-]", 2)[0])
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
        return NetworkRegistry.hasChannel(localPlayer.connection, GlowSearchC2SPacket.ID);
    }
}