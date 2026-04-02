package net.dillon.qualityofqueso.platform;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgedPlatformHelper implements PlatformHelper {

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}