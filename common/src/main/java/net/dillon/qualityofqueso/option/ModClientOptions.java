package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.option.eum.accessibility.Tooltips;
import net.dillon.qualityofqueso.option.eum.accessibility.WidgetTheme;
import net.dillon.qualityofqueso.option.eum.effects.Bows;
import net.dillon.qualityofqueso.option.eum.effects.PotionEffects;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.dillon.qualityofqueso.option.eum.misc.EChestButton;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarColor;
import net.dillon.qualityofqueso.option.eum.searching.SearchBarPosition;
import net.dillon.qualityofqueso.util.ModConstants;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModConstants.DEFAULT_LOCKED_SLOT_COLOR;
import static net.dillon.qualityofqueso.util.ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR;

/**
 * Client-side options, only loaded on client side.
 */
public class ModClientOptions {
    public static final ModOptionsHandler INSTANCE = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<ModClientOptions> {

        protected ModOptionsHandler() {
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

    public Searching searching = new Searching();
    public Management management = new Management();
    public Sorting sorting = new Sorting();
    public LockedSlots lockedSlots = new LockedSlots();
    public ButtonDisplayOptions buttonDisplayOptions = new ButtonDisplayOptions();
    public Hud hud = new Hud();
    public ItemCounter itemCounter = new ItemCounter();
    public ElytraAlarm elytraAlarm = new ElytraAlarm();
    public Miscellaneous misc = new Miscellaneous();
    public VisualTime visualTime = new VisualTime();
    public Fog fog = new Fog();
    public FOVEffects fovEffects = new FOVEffects();
    public Accessibility accessibility = new Accessibility();

    public static class Searching {
        public boolean containerSearching = true;
        public boolean inventorySearching = true;
        public QuickSearch quickSearch = QuickSearch.ON;
        public SearchBarPosition searchBarPosition = SearchBarPosition.OVERLAY;
        public SearchBarColor searchBarColor = SearchBarColor.DEFAULT;
        public int searchBarTextColor = DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR;
        public boolean underlineText = false;
        public boolean saveSearchText = false;

        // Config-only
        public boolean searchTransportables = true;
    }

    public static class Management {
        public Transferring transferring = Transferring.BUTTON_OR_KEY;
        public boolean filtering = true;
        public QuickDrop quickDrop = QuickDrop.KEY_ONLY;
        public boolean singularMoving = true;
        public boolean dragSorting = true;
        public boolean dragMoving = true;
        public Swapping swapping = Swapping.OFF;

        public Layout layout = Layout.HORIZONTAL;
        public boolean tradeAll = false;
        public boolean craftAll = false;
        public boolean playSounds = true;

        // Config-only
        public boolean includeHotbar = true;
        public boolean moveMatchingItems = false;
        protected boolean fillStacks = false;
        protected boolean alwaysQuickMove = false;
        public boolean saveExcludedSlots = false;
    }

    /**
     * @return if fill stacks is enabled.
     */
    public boolean isFillStacksEnabled() {
        return this.buttonDisplayOptions.displayFillStacks && this.management.fillStacks;
    }

    /**
     * @return fill stacks.
     */
    public boolean getFillStacks() {
        return this.management.fillStacks;
    }

    /**
     * Toggles fill stacks.
     */
    public void toggleFillStacks() {
        this.management.fillStacks = !this.management.fillStacks;
    }

    /**
     * @return if always quick move is enabled.
     */
    public boolean isAlwaysQuickMove() {
        return this.buttonDisplayOptions.displayAlwaysQuickMove && this.management.alwaysQuickMove;
    }

    /**
     * @return always quick moving.
     */
    public boolean getAlwaysQuickMove() {
        return this.management.alwaysQuickMove;
    }

    /**
     * Toggles always quick moving.
     */
    public void toggleAlwaysQuickMove() {
        this.management.alwaysQuickMove = !this.management.alwaysQuickMove;
    }

    public static class Sorting {
        public net.dillon.qualityofqueso.option.eum.management.sorting.Sorting sortingEnabled = net.dillon.qualityofqueso.option.eum.management.sorting.Sorting.BUTTON_OR_KEY;
        public boolean useGlobalSortingMode = false;
        public CurrentSortingMode currentSortingMode = CurrentSortingMode.ALPHABETICAL;
        public GlobalSortingMode globalSortingMode = GlobalSortingMode.ALPHABETICALLY;
    }

    public static class LockedSlots {
        public boolean enableLockedSlots = true;
        public boolean hardLockSlots = false;
        public ShowLock showLock = ShowLock.EVERYWHERE;
        public boolean lockSound = true;
        public int lockedSlotColor = DEFAULT_LOCKED_SLOT_COLOR;
    }

    public static class ButtonDisplayOptions {
        public IncludeHotbar displayIncludeHotbar = IncludeHotbar.ALWAYS;
        public MoveMatchingItems displayMoveMatchingItems = MoveMatchingItems.ALWAYS;
        public boolean displaySearchTransportables = true;
        public boolean displayAlwaysQuickMove = false;
        public boolean displayFillStacks = false;
        public boolean displayTradeAll = true;
        public boolean displayCraftAll = true;
    }

    public static class Hud {
        public ArmorStatus armorStatus = ArmorStatus.ALWAYS;
        public boolean armorHotbar = true;
        public boolean emptySlots = true;
        public boolean highlightArmor = true;
        public boolean coloredHighlighting = true;
        public boolean warningIndicators = true;
        public double displayTime = 4.0;
        public boolean animations = true;
        public double animationTime = 1.5;
    }

    public static class ItemCounter {
        public net.dillon.qualityofqueso.option.eum.hud.ItemCounter enableItemCounter = net.dillon.qualityofqueso.option.eum.hud.ItemCounter.STACKS;
        public boolean displayOnThrow = true;
        public boolean displayOnPickup = true;
        public boolean displayTotalWithStacks = false;
        public boolean arrowCounter = true;
        public boolean countAllArrows = true;
        public boolean onlyShowArrowCounter = false;
        public boolean alwaysShowArrowCounter = false;
        public boolean onlyCountMatchingItems = false;
        public boolean countContainers = true;
        public boolean countEnderChest = false;
    }

    public static class ElytraAlarm {
        public net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm enableElytraAlarm = net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm.ON;
        public int minFallDistance = 20;
        public int soundDelayTicks = 1;
        public List<String> blacklistedItems = new ArrayList<>(List.of(
                "minecraft:mace",
                "minecraft:wind_charge",
                "minecraft:ender_pearl",
                "minecraft:water_bucket",
                "minecraft:powder_snow_bucket"
        ));
    }

    public static class Miscellaneous {
        public boolean mobHitDing = true;
        public int minMobHitDingDistance = 15;

        public boolean quickGuiExit = true;
        public boolean quickEquip = true;

        public boolean armorDing = true;
        public boolean enchantmentHelper = true;

        public boolean preventRageQuitting = false;
        public boolean alwaysPreventRageQuitting = false;

        public boolean redArmorTint = false;
        public boolean fortniteBattlePass = false;

        public int itemFrameSearchGlowDuration = 0;
        public int itemFrameSearchRadius = 150;
    }

    public static class VisualTime {
        public boolean overrideClientTime = false;
        public int visualTime = 0;
        public int visualTimeSpeed = 0;
        public boolean matchWithIrlTime = false;
    }

    public static class Fog {
        public boolean allFog = true;
        public boolean overworldFog = true;
        public int overworldFogIntensity = 100;
        public boolean netherFog = true;
        public int netherFogIntensity = 100;
    }

    public static class FOVEffects {
        public int sprinting = 100;
        public boolean flying = true;
        public PotionEffects potionEffects = PotionEffects.DEFAULT;
        public Bows bows = Bows.DEFAULT;
        public boolean fluids = true;
    }

    public static class Accessibility {
        public boolean enableMod = true;

        public Tooltips tooltips = Tooltips.DEFAULT;
        public boolean preventEFromTyping = true;

        public boolean autoCloseRecipeBook = true;

        public boolean searchInventory = true;
        public boolean darkerOverlay = false;

        public boolean perpendicularQuickMoving = false;
        public boolean ignoreFabricTags = false;

        public boolean darkDisc = true;

        public EChestButton eChestButton = EChestButton.QOQ_MENU;
        public WidgetTheme widgetTheme = WidgetTheme.VANILLA;
    }
}