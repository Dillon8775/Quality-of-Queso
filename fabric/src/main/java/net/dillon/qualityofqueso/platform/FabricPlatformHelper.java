package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.main.CommonMain;
import net.dillon.qualityofqueso.util.ModConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.List;

public class FabricPlatformHelper implements PlatformHelper {

    @Override
    public String getModVersion() {
        return FabricLoader.getInstance()
                .getModContainer(ModConstants.MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString().split("[+-]", 2)[0])
                .orElse("unknown");
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean canSendPacket(LocalPlayer localPlayer) {
        return ClientPlayNetworking.canSend(new ResourceLocation("qualityofqueso", "glow_search"));
    }

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