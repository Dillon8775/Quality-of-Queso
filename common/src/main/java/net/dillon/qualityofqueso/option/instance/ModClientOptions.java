package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.*;
import net.dillon.qualityofqueso.option.base.BaseOptions;

/**
 * Client-side options, only loaded on client side.
 */
public class ModClientOptions {
    // Main options
    public boolean enableMod = true;
    public boolean quickGuiExit = true;
    public boolean quickSearch = true;
    public boolean chestSearching = true;
    public boolean searchInventory = true;
    public boolean inventorySearching = true;
    public boolean saveSearchText = false;
    public boolean searchTransportables = true;
    public boolean useOldSearchBarTexture = false;

    // Inventory management options
    public Transferring transferring = Transferring.SHORTCUT_KEY_OR_BUTTON;
    public ContainerSorting containerSorting = ContainerSorting.SHORTCUT_KEY_OR_BUTTON;
    public boolean containerFiltering = true;
    public QuickDrop quickDrop = QuickDrop.SHORTCUT_KEY_ONLY;
    public Swapping swapping = Swapping.OFF;
    public boolean perpendicularQuickMoving = false;
    public boolean dragSorting = true;
    public boolean fillWhatsPreset = false;
    public boolean saveExcludedSlots = false;
    public boolean showButtonShortcuts = true;
    public boolean includeHotbar = true;

    // HUD options
    public boolean armorStatus = true;
    public ItemCount itemCount = ItemCount.STACKS;
    public boolean displayOnThrow = true;
    public boolean displayOnPickup = true;
    public boolean countContainers = true;
    public boolean displayTotalWithStacks = false;
    public boolean showArrowCount = true;
    public boolean countAllArrows = true;
    public boolean coloredHighlighting = true;
    public boolean warningIndicators = true;

    // Misc options
    public boolean quickEquip = true;
    public boolean preventRageQuitting = false;
    public boolean alwaysPreventRageQuitting = false;
    public boolean preventEFromTyping = false;
    public boolean helpfulTooltips = true;
    public boolean fog = true;
    public boolean elytraAlarm = true;
    public boolean mobHitDing = true;
    public boolean autoCloseRecipeBook = true;
    public int minElytraFallDistance = 20;
    public int minMobHitDingDistance = 20;
    public int itemFrameSearchGlowDuration = 0;
    public int itemFrameSearchRadius = 150;
    public QoQButtons qoqButtons = QoQButtons.EVERYWHERE;
    public boolean fortniteBattlePass = false;

    // Advanced options
    public MoveItemsIf moveItemsIf = MoveItemsIf.CAN_MOVE_AT_ALL;
    public int elytraAlarmSoundDelay = 1;

    public static final ModOptionsHandler CLIENT = new ModOptionsHandler();

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