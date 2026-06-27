package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.option.eum.fov_effects.Bows;
import net.dillon.qualityofqueso.option.eum.fov_effects.PotionEffects;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.dillon.qualityofqueso.option.eum.general.Tooltips;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.DefaultSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.Sorting;
import net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm;
import net.dillon.qualityofqueso.option.eum.misc.ViewLastKnownEnderChestButton;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarColor;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarPosition;
import net.dillon.qualityofqueso.util.ModConstants;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Client-side options, only loaded on client side.
 */
public class ModClientOptions {
    public static final List<String> DEFAULT_HORIZONTAL_BUTTON_LAYOUT = List.of(
            TRANSFER_CONTAINER_BUTTON_SERIALIZED_NAME,
            TRANSFER_INVENTORY_BUTTON_SERIALIZED_NAME,
            LOCK_INVENTORY_BUTTON_SERIALIZED_NAME,
            INCLUDE_HOTBAR_BUTTON_SERIALIZED_NAME,
            ALWAYS_QUICK_MOVE_BUTTON_SERIALIZED_NAME,
            FILTERING_BUTTON_SERIALIZED_NAME,
            SORT_BUTTON_SERIALIZED_NAME,
            BULK_CRAFT_BUTTON_SERIALIZED_NAME,
            QUICK_DROP_BUTTON_SERIALIZED_NAME,
            SWAP_BUTTON_SERIALIZED_NAME,
            SEARCH_TRANSPORTABLES_BUTTON_SERIALIZED_NAME,
            CLEAR_EXCLUDED_SLOTS_BUTTON_SERIALIZED_NAME
    );
    public static final List<String> DEFAULT_VERTICAL_BUTTON_LAYOUT = List.of(
            TRANSFER_INVENTORY_BUTTON_SERIALIZED_NAME,
            TRANSFER_CONTAINER_BUTTON_SERIALIZED_NAME,
            SORT_BUTTON_SERIALIZED_NAME,
            INCLUDE_HOTBAR_BUTTON_SERIALIZED_NAME,
            LOCK_INVENTORY_BUTTON_SERIALIZED_NAME,
            ALWAYS_QUICK_MOVE_BUTTON_SERIALIZED_NAME,
            BULK_CRAFT_BUTTON_SERIALIZED_NAME,
            FILTERING_BUTTON_SERIALIZED_NAME,
            QUICK_DROP_BUTTON_SERIALIZED_NAME,
            SWAP_BUTTON_SERIALIZED_NAME,
            SEARCH_TRANSPORTABLES_BUTTON_SERIALIZED_NAME,
            CLEAR_EXCLUDED_SLOTS_BUTTON_SERIALIZED_NAME
    );
    public static final ClientOptionsHandler INSTANCE = new ClientOptionsHandler();
    private final GeneralOptions generalOptions = new GeneralOptions();
    private final SearchingOptions searchingOptions = new SearchingOptions();
    private final ManagementOptions managementOptions = new ManagementOptions();
    private final SortingOptions sortingOptions = new SortingOptions();
    private final LockedSlotOptions lockedSlotOptions = new LockedSlotOptions();
    private final ButtonDisplayOptions buttonDisplayOptions = new ButtonDisplayOptions();
    private final HudOptions hudOptions = new HudOptions();
    private final ItemCounterOptions itemCounterOptions = new ItemCounterOptions();
    private final ElytraAlarmOptions elytraAlarmOptions = new ElytraAlarmOptions();
    private final MiscellaneousOptions miscellaneousOptions = new MiscellaneousOptions();
    private final VisualTimeOptions visualTimeOptions = new VisualTimeOptions();
    private final FogOptions fogOptions = new FogOptions();
    private final FovEffectOptions forEffects = new FovEffectOptions();
    private final AccessibilityOptions accessibilityOptions = new AccessibilityOptions();

    public GeneralOptions getGeneralOptions() {
        return this.generalOptions;
    }

    public SearchingOptions getSearchingOptions() {
        return this.searchingOptions;
    }

    public ManagementOptions getManagementOptions() {
        return this.managementOptions;
    }

    public SortingOptions getSortingOptions() {
        return this.sortingOptions;
    }

    public LockedSlotOptions getLockedSlotOptions() {
        return this.lockedSlotOptions;
    }

    public ButtonDisplayOptions getButtonDisplayOptions() {
        return this.buttonDisplayOptions;
    }

    public HudOptions getHudOptions() {
        return this.hudOptions;
    }

    public ItemCounterOptions getItemCounterOptions() {
        return this.itemCounterOptions;
    }

    public ElytraAlarmOptions getElytraAlarmOptions() {
        return this.elytraAlarmOptions;
    }

    public MiscellaneousOptions getMiscOptions() {
        return this.miscellaneousOptions;
    }

    public VisualTimeOptions getVisualTimeOptions() {
        return this.visualTimeOptions;
    }

    public FogOptions getFogOptions() {
        return this.fogOptions;
    }

    public FovEffectOptions getFovEffectOptions() {
        return this.forEffects;
    }

    public AccessibilityOptions getAccessibilityOptions() {
        return this.accessibilityOptions;
    }

    public static class GeneralOptions {
        public boolean enableMod = true;
        public Tooltips tooltips = Tooltips.DEFAULT;
        public Theme theme = Theme.VANILLA;
    }

    public static class SearchingOptions {
        public boolean containerSearching = true;
        public boolean inventorySearching = true;

        public SearchBarPosition searchBarPosition = SearchBarPosition.OVERLAY;
        public SearchBarColor searchBarColor = SearchBarColor.DEFAULT;
        public int searchBarTextColor = DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR;
        public boolean underlineText = false;

        public QuickSearch quickSearch = QuickSearch.ENABLED;
        public boolean saveSearchText = false;
    }

    public static class ManagementOptions {
        public Layout layout = Layout.HORIZONTAL;
        public boolean playSounds = true;

        public Transferring transferring = Transferring.BUTTON_OR_KEY;
        public QuickDrop quickDrop = QuickDrop.KEY_ONLY;
        public boolean containerFiltering = true;
        public LockInventory lockInventory = LockInventory.UNLOCKED;
        public boolean quickEquip = true;

        public boolean scrollMoving = true;
        public boolean dragMoving = true;

        public boolean bulkTrade = false;
        public boolean bulkCraft = false;

        public Swapping swapping = Swapping.OFF;
        public boolean dragSorting = true;

        public List<String> horizontalButtonLayout = new ArrayList<>(DEFAULT_HORIZONTAL_BUTTON_LAYOUT);
        public List<String> verticalButtonLayout = new ArrayList<>(DEFAULT_VERTICAL_BUTTON_LAYOUT);

        public boolean includingHotbar = true;
        public FilteringMode filteringMode = FilteringMode.NONE;
        protected boolean alwaysQuickMove = false;
    }

    /**
     * @return if move-matching behavior is enabled.
     */
    public boolean isFiltering() {
        return this.managementOptions.filteringMode.matchingOrCurrentStacks();
    }

    /**
     * @return if fill current stacks behavior is enabled.
     */
    public boolean isFillingCurrentStacks() {
        return this.managementOptions.filteringMode == FilteringMode.CURRENT_STACKS;
    }

    /**
     * Cycles filtering mode for the combined filtering button.
     */
    public void cycleFilteringMode() {
        this.managementOptions.filteringMode = switch (this.managementOptions.filteringMode) {
            case NONE -> FilteringMode.MATCHING;
            case MATCHING -> FilteringMode.CURRENT_STACKS;
            case CURRENT_STACKS -> FilteringMode.NONE;
        };
    }

    /**
     * Cycles locked inventory modes.
     */
    public void cycleLockedInventoryMode() {
        this.managementOptions.lockInventory = switch (this.managementOptions.lockInventory) {
            case UNLOCKED -> LockInventory.LOCKED;
            case LOCKED -> LockInventory.SOFT_LOCKED;
            case SOFT_LOCKED -> LockInventory.UNLOCKED;
        };
    }

    /**
     * @return if always quick move is enabled.
     */
    public boolean isAlwaysQuickMove() {
        return this.buttonDisplayOptions.displayAlwaysQuickMove && this.managementOptions.alwaysQuickMove;
    }

    /**
     * @return always quick moving.
     */
    public boolean getAlwaysQuickMove() {
        return this.managementOptions.alwaysQuickMove;
    }

    /**
     * Toggles always quick moving.
     */
    public void toggleAlwaysQuickMove() {
        this.managementOptions.alwaysQuickMove = !this.managementOptions.alwaysQuickMove;
    }

    public static class SortingOptions {
        public Sorting sorting = Sorting.BUTTON_OR_KEY;
        public CurrentSortingMode currentSortingMode = CurrentSortingMode.ALPHABETICAL;

        public DefaultSortingMode defaultSortingMode = DefaultSortingMode.ALPHABETICALLY;
        public boolean useGlobalSortingMode = false;
        public GlobalSortingMode globalSortingMode = GlobalSortingMode.ALPHABETICALLY;
    }

    public static class LockedSlotOptions {
        public boolean lockedSlots = true;
        public ShowLock showLock = ShowLock.SCREEN_ONLY;
        public boolean lockSound = true;
        public int lockedSlotColor = DEFAULT_LOCKED_SLOT_COLOR;
        public boolean preventDropping = true;
        public boolean hardLockSlots = false;
    }

    public static class ButtonDisplayOptions {
        public boolean displaySearchTransportables = true;
        public boolean displayLockInventory = true;
        public boolean displayBulkCraft = true;
        public boolean displayBulkTrade = true;
        public boolean safeBulk = true;
        public boolean displayAlwaysQuickMove = false;
        public IncludeHotbar displayIncludeHotbar = IncludeHotbar.ALWAYS;
        public FilteringButton displayFiltering = FilteringButton.ALWAYS;
    }

    public static class HudOptions {
        public ArmorStatus armorStatus = ArmorStatus.ALWAYS;
        public boolean armorHotbar = true;
        public boolean emptySlots = true;
        public boolean highlightArmor = true;

        public boolean animations = true;
        public double animationTime = 1.5;

        public boolean coloredHighlighting = true;
        public boolean warningIndicators = true;
        public double displayTime = 4.0;

        public int[] armorStatusPosition = new int[]{0, 0};
        public int otherElementsY = 0;
    }

    public static class ItemCounterOptions {
        public ItemCounter itemCounter = ItemCounter.STACKS;
        public boolean countContainers = true;
        public boolean countEnderChest = false;
        public boolean onlyCountMatchingItems = false;

        public boolean arrowCounter = true;
        public boolean countAllArrows = true;
        public boolean onlyShowArrowCounter = false;
        public boolean alwaysShowArrowCounter = false;

        public boolean displayOnThrow = true;
        public boolean displayOnPickup = true;
        public boolean displayTotalWithStacks = false;

        public int[] itemCounterPosition = new int[]{0, 0};
        public boolean moveItemCounterOver = true;
    }

    public static class ElytraAlarmOptions {
        public ElytraAlarm elytraAlarm = ElytraAlarm.ENABLED;
        public int minElytraAlarmFallDistance = 20;
        public int elytraAlarmSoundDelayTicks = 1;
        public List<String> elytraAlarmBlacklistedItems = new ArrayList<>(List.of(
                "minecraft:mace",
                "minecraft:wind_charge",
                "minecraft:ender_pearl",
                "minecraft:water_bucket",
                "minecraft:powder_snow_bucket"
        ));
    }

    public static class MiscellaneousOptions {
        public boolean mobHitDing = true;
        public int minMobHitDingDistance = 15;
        public boolean armorDing = true;

        public boolean noRecipeBookShift = false;
        public boolean autoCloseRecipeBook = true;

        public boolean antiRageQuit = false;
        public boolean forceAntiRageQuit = false;

        public boolean redArmorTint = false;
        public boolean enchantmentHelper = true;
        public boolean enhancedCursor = true;
        public boolean quickGuiExit = true;
        public boolean fortniteBattlePass = false;

        public int itemFrameSearchGlowDuration = 0;
        public int itemFrameSearchRadius = 150;
    }

    public static class VisualTimeOptions {
        public boolean overrideClientTime = false;
        public int visualTime = 0;
        public int visualTimeSpeed = 0;
        public boolean syncLocalTime = false;
    }

    public static class FogOptions {
        public boolean allFog = true;
        public boolean overworldFog = true;
        public boolean netherFog = true;

        public int overworldFogIntensity = 100;
        public int netherFogIntensity = 100;
    }

    public static class FovEffectOptions {
        public boolean lockFov = false;
        public int sprinting = 100;
        public boolean flying = true;
        @Deprecated
        public boolean fluids = false;
        public PotionEffects potions = PotionEffects.ENABLED;
        public Bows bows = Bows.ENABLED;
    }

    public static class AccessibilityOptions {
        public boolean useLegacyTextures = false;
        public boolean perpendicularQuickMoving = false;
        public boolean preventEFromTyping = true;
        public boolean searchInventory = true;
        public boolean operatorItemsTab = true;
        public boolean betaWarning = true;
        public boolean ignoreFabricTags = false;

        public boolean darkerOverlay = false;
        public boolean darkDisc = true;

        public ViewLastKnownEnderChestButton eChestButton = ViewLastKnownEnderChestButton.QOQ_MENU;
    }

    public static class ClientOptionsHandler extends BaseOptions<ModClientOptions> {

        protected ClientOptionsHandler() {
            super(ModConstants.DEFAULT_CLIENT_CONFIG_FILE_NAME);
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