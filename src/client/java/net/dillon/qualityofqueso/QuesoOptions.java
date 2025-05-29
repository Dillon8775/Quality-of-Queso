package net.dillon.qualityofqueso;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import static net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil.GSON;

public class QuesoOptions {
    public static final String CONFIG = "qualityofqueso-config.json";
    private static File file;
    public static QuesoOptions OPTIONS = getConfig();

    public boolean close_gui_menu_by_clicking_off = true;
    public boolean type_anywhere_to_search = true;

    /**
     * Loads the configuration file.
     */
    public static void loadConfig() {
        File configFile = getConfigFile();

        if (!configFile.exists()) {
            OPTIONS = new QuesoOptions();
        } else {
            readConfig();
        }
        saveConfig();
    }

    /**
     * Reads the configuration file.
     */
    public static void readConfig() {
        OPTIONS = getConfig();
    }

    /**
     * Saves the configuration file.
     */
    public static void saveConfig() {
        File file = getConfigFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(OPTIONS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the {@code OPTIONS} variable to the config.
     */
    public static void setConfig(QuesoOptions config) {
        OPTIONS = config;
        saveConfig();
    }

    /**
     * Gets all the Speedrunner Mod configuration options and returns them.
     */
    public static QuesoOptions getConfig() {
        File file = getConfigFile();
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, QuesoOptions.class);
        } catch (Exception e) {
            QuesoOptions newconfig = new QuesoOptions();
            setConfig(newconfig);
            return newconfig;
        }
    }

    /**
     * Returns the Speedrunner Mod configuration file.
     */
    public static File getConfigFile() {
        if (file == null) {
            file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG);
        }
        return file;
    }
}