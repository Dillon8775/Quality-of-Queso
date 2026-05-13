package net.dillon.qualityofqueso.util;

import net.minecraft.client.gui.components.EditBox;
import java.lang.reflect.Method;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

public class SearchSyncHelper {
    private static String lastText = "";

    public static boolean checkForRecipeMod(String modId) {
        try {
            Class<?> fabric = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object inst = fabric.getMethod("getInstance").invoke(null);
            return (boolean) fabric.getMethod("isModLoaded", String.class).invoke(inst, modId);
        } catch (Exception e) {
            try {
                Class<?> neo = Class.forName("net.neoforged.fml.loading.LoadingModList");
                Object inst = neo.getMethod("get").invoke(null);
                return neo.getMethod("getModFileById", String.class).invoke(inst, modId) != null;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static void tick(EditBox searchBar) {
        SearchSyncMode mode = options().searching.searchSyncMode;
        if (mode == SearchSyncMode.OFF || mode == SearchSyncMode.PUSH) {
            return;
        }

        String external = getExternalText();
        if (!external.equals(lastText)) {
            lastText = external;
            if (!searchBar.getValue().equals(external)) {
                searchBar.setValue(external);
            }
        }
    }

    public static void updateExternal(String text) {
        SearchSyncMode mode = options().searching.searchSyncMode;
        if (mode == SearchSyncMode.OFF || mode == SearchSyncMode.PULL) {
            return;
        }

        if (text.equals(lastText)) {
            return;
        }

        lastText = text;
        updateEMI(text);
        updateREI(text);
    }

    private static String getExternalText() {
        try {
            Class<?> emiApi = Class.forName("dev.emi.emi.api.EmiApi");
            return (String) emiApi.getMethod("getSearchText").invoke(null);
        } catch (Exception ignored) {}

        try {
            Class<?> reiRuntime = Class.forName("me.shedaniel.rei.api.client.REIRuntime");
            Object instance = reiRuntime.getMethod("getInstance").invoke(null);
            Object searchField = reiRuntime.getMethod("getSearchTextField").invoke(instance);
            if (searchField != null) {
                return (String) searchField.getClass().getMethod("getText").invoke(searchField);
            }
        } catch (Exception ignored) {}

        return lastText;
    }

    private static void updateEMI(String text) {
        try {
            Class<?> emiApi = Class.forName("dev.emi.emi.api.EmiApi");
            Method setSearchText = emiApi.getMethod("setSearchText", String.class);
            setSearchText.invoke(null, text);
        } catch (Exception ignored) {}
    }

    private static void updateREI(String text) {
        try {
            Class<?> reiRuntime = Class.forName("me.shedaniel.rei.api.client.REIRuntime");
            Object instance = reiRuntime.getMethod("getInstance").invoke(null);
            Object searchField = reiRuntime.getMethod("getSearchTextField").invoke(instance);
            if (searchField != null) {
                Method setText = searchField.getClass().getMethod("setText", String.class);
                setText.invoke(searchField, text);
            }
        } catch (Exception ignored) {}
    }
}