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
    // Main options
    public boolean enableMod = true;
    public boolean betterGuiExit = true;
    public boolean betterSearching = true;
    public boolean chestSearching = true;
    public boolean searchInventory = true;
    public boolean inventorySearching = true;
    public boolean saveSearchText = false;

    // Inventory management options
    public Transferring transferring = Transferring.SHORTCUT_KEY_OR_BUTTON;
    public ContainerSorting containerSorting = ContainerSorting.SHORTCUT_KEY_OR_BUTTON;
    public QuickDrop quickDrop = QuickDrop.SHORTCUT_KEY_ONLY;
    public Swapping swapping = Swapping.SHORTCUT_KEY_ONLY;
    public boolean dragToSort = true;
    public boolean includeHotbar = true;
    public boolean showButtonShortcuts = true;

    // Misc options
    public boolean legacyQuickMove = false;
    public boolean quickEquip = true;
    public boolean preventRageQuitting = false;
    public boolean preventEFromTyping = false;
    public boolean helpfulTooltips = true;
    public List<String> blacklistedServers = new ArrayList<>();
    public boolean multiServerConfigs = true;
    public int itemFrameSearchGlowDuration = 0;
    public int itemFrameSearchRadius = 150;
    public QoQButtons qoqButtons = QoQButtons.EVERYWHERE;

    public static final ModOptionsHandler CLIENT_OPTIONS = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<ModClientOptions> {

        protected ModOptionsHandler() {
            super(BaseOptions.DEFAULT_CLIENT_FILE_NAME);
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