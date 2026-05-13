package net.dillon.qualityofqueso.util;

import net.minecraft.network.chat.Component;

public class CheckForRecipeMod {

    public static boolean checkForRecipeMod(String modId) {
        try {
            Class<?> fabricLoaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object fabricInstance = fabricLoaderClass.getMethod("getInstance").invoke(null);
            return (boolean) fabricLoaderClass.getMethod("isModLoaded", String.class).invoke(fabricInstance, modId);
        } catch (Exception e) {
            try {
                Class<?> loadingModListClass = Class.forName("net.neoforged.fml.loading.LoadingModList");
                Object neoInstance = loadingModListClass.getMethod("get").invoke(null);
                return loadingModListClass.getMethod("getModFileById", String.class).invoke(neoInstance, modId) != null;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static boolean isAnyViewerInstalled() {
        return checkForRecipeMod("jei") || checkForRecipeMod("rei") || checkForRecipeMod("emi");
    }

    public static Component getStatusComponent(SearchSyncMode value) {
        if (!isAnyViewerInstalled()) {
            return Component.translatable("qualityofqueso.options.sync.off");
        }
        return value.getTranslationKey();
    }
}