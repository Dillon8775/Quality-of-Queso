package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.*;
import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-side options, only loaded on client side.
 */
@OnlyIn(Dist.CLIENT)
public class ModClientOptions {
    // Main options
    public boolean enableMod = true;
    public boolean betterGuiExit = true;
    public boolean quickSearch = true;
    public boolean chestSearching = true;
    public boolean searchInventory = true;
    public boolean inventorySearching = true;
    public boolean saveSearchText = false;

    // Inventory management options
    public Transferring transferring = Transferring.SHORTCUT_KEY_OR_BUTTON;
    public ContainerSorting containerSorting = ContainerSorting.SHORTCUT_KEY_OR_BUTTON;
    public QuickDrop quickDrop = QuickDrop.SHORTCUT_KEY_ONLY;
    public Swapping swapping = Swapping.SHORTCUT_KEY_ONLY;
    public boolean perpendicularQuickMoving = false;
    public MoveItemsIf moveItemsIf = MoveItemsIf.CAN_MOVE_AT_ALL;
    public boolean dragToSort = true;
    public boolean saveExcludedSlots = false;
    public boolean showButtonShortcuts = true;
    public boolean includeHotbar = true;

    // HUD options
    public ArmorStatus armorStatus = ArmorStatus.ON_CHANGE;
    public ItemCount itemCount = ItemCount.STACKS;
    public boolean displayOnThrow = true;
    public boolean displayOnPickup = true;
    public boolean countContainers = true;
    public boolean displayTotalWithStacks = false;

    // Misc options
    public boolean quickEquip = true;
    public boolean preventRageQuitting = false;
    public boolean preventEFromTyping = false;
    public boolean helpfulTooltips = true;
    public boolean fog = true;
    public boolean mobHitDing = true;
    public boolean autoCloseRecipeBook = true;
    public int minMobHitDingDistance = 20;
    public int itemFrameSearchGlowDuration = 0;
    public int itemFrameSearchRadius = 150;
    public QoQButtons qoqButtons = QoQButtons.EVERYWHERE;

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