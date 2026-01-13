package net.dillon.qualityofqueso.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side options, only loaded on client side.
 */
@Environment(EnvType.CLIENT)
public class ModClientOptions {
    public boolean enableMod = true;
    public boolean betterGuiExit = true;
    public boolean betterSearching = true;
    public boolean chestSearching = true;
    public boolean searchInventory = true;
    public boolean inventorySearching = true;
    public boolean saveSearchText = false;
    public boolean inventoryManagement = true;
    public boolean shortcutKeys = true;
    public boolean quickDrop = false;
    public boolean swapping = false;
    public boolean includeHotbar = true;
    public boolean legacyQuickMove = false;
    public boolean showButtonOutlines = false;
    public boolean showButtonShortcuts = true;
    public boolean quickEquip = true;
    public boolean preventRageQuitting = false;
    public boolean preventEFromTyping = false;
    public boolean helpfulTooltips = true;
    public List<String> blacklistedServers = new ArrayList<>();
    public boolean serverSpecificConfigs = true;
    public int itemFrameSearchTimer = 0;
    public int itemFrameSearchRadius = 150;
    public QoQButtons qoqButtons = QoQButtons.EVERYWHERE;

    public static final ModOptionsHandler CLIENT_OPTIONS = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<ModClientOptions> {

        protected ModOptionsHandler() {
            super(BaseOptions.DEFAULT_FILE_NAME);
            this.load();
        }

        @Override
        protected ModClientOptions createDefault() {
            return new ModClientOptions();
        }

        @Override
        protected Class<ModClientOptions> getConfigClass() {
            return ModClientOptions.class;
        }
    }
}