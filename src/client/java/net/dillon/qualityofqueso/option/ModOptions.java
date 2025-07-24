package net.dillon.qualityofqueso.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side options, only loaded on client side.
 */
@Environment(EnvType.CLIENT)
public class ModOptions {
    public boolean enableMod = true;
    public boolean betterGuiExit = true;
    public boolean betterSearching = true;
    public boolean chestSearch = true;
    public boolean searchInventory = true;
    public boolean saveSearchText = false;
    public boolean inventorySorting = true;
    public boolean requireAltToMove = true;
    public boolean includeHotbar = true;
    public boolean quickEquip = true;
    public boolean preventRageQuitting = false;
    public List<String> blacklistedServers = new ArrayList<>();
    public boolean itemFrameSearching = true;
    public int itemFrameSearchTimer = 0;
    public int itemFrameSearchRadius = 150;
    public boolean showConfigButton = true;

    public static final ModOptionsHandler OPTIONS = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<ModOptions> {

        protected ModOptionsHandler() {
            super("qualityofqueso-config.json");
            this.load();
        }

        @Override
        protected ModOptions createDefault() {
            return new ModOptions();
        }

        @Override
        protected Class<ModOptions> getConfigClass() {
            return ModOptions.class;
        }
    }
}