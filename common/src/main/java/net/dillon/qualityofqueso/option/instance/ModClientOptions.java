package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.*;
import net.dillon.qualityofqueso.option.base.BaseOptions;

/**
 * Client-side options, only loaded on client side.
 */
public class ModClientOptions {
    public Searching searching = new Searching();
    public InventoryManagement management = new InventoryManagement();
    public Hud hud = new Hud();
    public Miscellaneous misc = new Miscellaneous();
    public Accessibility accessibility = new Accessibility();

    public static class Searching {
        public boolean containerSearching = true;
        public boolean inventorySearching = true;
        public boolean saveSearchText = false;
        public boolean quickSearch = true;

        // Config-only
        public boolean searchTransportables = true;
    }

    public static class InventoryManagement {
        public Transferring transferring = Transferring.SHORTCUT_KEY_OR_BUTTON;
        public ContainerSorting containerSorting = ContainerSorting.SHORTCUT_KEY_OR_BUTTON;
        public boolean containerFiltering = true;
        public QuickDrop quickDrop = QuickDrop.SHORTCUT_KEY_ONLY;
        public Swapping swapping = Swapping.OFF;

        public boolean dragSorting = true;
        public boolean saveExcludedSlots = false;

        // Config-only
        public boolean fillWhatsPreset = false;
        public boolean includeHotbar = true;
    }

    public static class Hud {
        public boolean armorStatus = true;
        public boolean coloredHighlighting = true;
        public boolean warningIndicators = true;

        public ItemCount itemCount = ItemCount.STACKS;
        public boolean displayOnThrow = true;
        public boolean displayOnPickup = true;
        public boolean countContainers = true;
        public boolean showArrowCount = true;
        public boolean countAllArrows = true;
    }

    public static class Miscellaneous {
        public boolean elytraAlarm = true;
        public int minElytraFallDistance = 20;
        public boolean mobHitDing = true;
        public int minMobHitDingDistance = 20;

        public boolean quickGuiExit = true;
        public boolean quickEquip = true;

        public boolean enchantingHelper = true;
        public boolean fog = true;

        public boolean preventRageQuitting = false;
        public boolean alwaysPreventRageQuitting = false;

        public boolean fortniteBattlePass = false;

        public int itemFrameSearchGlowDuration = 0;
        public int itemFrameSearchRadius = 150;
    }

    public static class Accessibility {
        public boolean enableMod = true;

        public boolean helpfulTooltips = true;
        public boolean preventEFromTyping = false;
        public boolean searchInventory = true;
        public boolean showButtonShortcuts = true;
        public boolean autoCloseRecipeBook = true;

        public boolean useOldSearchBarTexture = false;

        public boolean onlyCountMatchingItems = false;
        public boolean displayTotalWithStacks = false;

        public boolean perpendicularQuickMoving = false;
        public MoveItemsIf moveItemsIf = MoveItemsIf.CAN_MOVE_AT_ALL;

        public int elytraAlarmSoundDelay = 1;

        public QoQButtons qoqButtons = QoQButtons.EVERYWHERE;

    }

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