package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.dillon.qualityofqueso.option.eum.accessibility.QoQButtons;
import net.dillon.qualityofqueso.option.eum.effects.Bows;
import net.dillon.qualityofqueso.option.eum.effects.PotionEffects;
import net.dillon.qualityofqueso.option.eum.hud.ArmorStatus;
import net.dillon.qualityofqueso.option.eum.hud.ItemCount;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.searching.QuickSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side options, only loaded on client side.
 */
public class ModClientOptions {
    public static final int DEFAULT_TEXT_COLOR = -12566464;
    public Searching searching = new Searching();
    public InventoryManagement management = new InventoryManagement();
    public InventoryManagement.ButtonDisplayOptions buttonDisplayOptions = new InventoryManagement.ButtonDisplayOptions();
    public Hud hud = new Hud();
    public ElytraAlarm elytraAlarm = new ElytraAlarm();
    public Miscellaneous misc = new Miscellaneous();
    public Fog fog = new Fog();
    public FOVEffects fovEffects = new FOVEffects();
    public Accessibility accessibility = new Accessibility();

    public static class Searching {
        public boolean containerSearching = true;
        public boolean inventorySearching = true;
        public boolean saveSearchText = false;
        public QuickSearch quickSearch = QuickSearch.ON;
        public boolean transparentSearchBar = false;

        // Config-only
        public boolean searchTransportables = true;
    }

    public static class InventoryManagement {
        public ButtonLayout buttonLayout = ButtonLayout.VERTICAL;
        public Transferring transferring = Transferring.SHORTCUT_KEY_OR_BUTTON;
        public ContainerSorting containerSorting = ContainerSorting.SHORTCUT_KEY_OR_BUTTON;
        public boolean containerFiltering = true;
        public QuickDrop quickDrop = QuickDrop.SHORTCUT_KEY_ONLY;
        public Swapping swapping = Swapping.OFF;

        public boolean dragSorting = true;
        public boolean tagSorting = false;

        public ButtonSounds buttonSounds = ButtonSounds.ALL;

        // Config-only
        public boolean alwaysQuickMove = false;
        public boolean saveExcludedSlots = false;
        public boolean fillWhatsPreset = false;
        public boolean includeHotbar = true;

        public static class ButtonDisplayOptions {
            public DisplayIncludeHotbar displayIncludeHotbar = DisplayIncludeHotbar.ALWAYS;
            public DisplayFillWhatsPresent displayFillWhatsPresent = DisplayFillWhatsPresent.ALWAYS;
            public boolean displaySearchTransportables = true;
            public boolean displayAlwaysQuickMove = true;
        }
    }

    public static class Hud {
        public ArmorStatus armorStatus = ArmorStatus.ON;
        public boolean armorHotbar = true;
        public boolean coloredHighlighting = true;
        public boolean warningIndicators = true;

        public ItemCount itemCount = ItemCount.TOTAL;
        public boolean displayOnThrow = true;
        public boolean displayOnPickup = true;
        public boolean countContainers = true;
        public boolean showArrowCount = true;
        public boolean countAllArrows = true;
    }

    public static class ElytraAlarm {
        public net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm elytraAlarm = net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm.ON;
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
        public boolean enchantingHelper = true;

        public boolean preventRageQuitting = false;
        public boolean alwaysPreventRageQuitting = false;

        public boolean fortniteBattlePass = false;

        public int itemFrameSearchGlowDuration = 0;
        public int itemFrameSearchRadius = 150;
    }

    public static class Fog {
        public boolean allFog = true;
        public boolean overworldFog = true;
        public boolean netherFog = true;
        public int netherFogIntensity = 100;
    }

    public static class FOVEffects {
        public boolean sprinting = true;
        public boolean flying = true;
        public PotionEffects potionEffects = PotionEffects.ON;
        public Bows bows = Bows.ON;
        public boolean fluids = true;
    }

    public static class Accessibility {
        public boolean enableMod = true;

        public boolean helpfulTooltips = true;
        public boolean preventEFromTyping = true;

        public boolean showButtonShortcuts = true;
        public boolean searchInventory = true;

        public boolean autoCloseRecipeBook = true;
        public boolean ignoreFabricTags = false;

        public boolean useOldSearchBarTexture = false;
        public int searchBarTextColor = DEFAULT_TEXT_COLOR;

        public boolean onlyCountMatchingItems = false;
        public boolean displayTotalWithStacks = false;

        public boolean perpendicularQuickMoving = false;

        public QoQButtons qoqButtons = QoQButtons.EVERYWHERE;
        public float doNot = 2.0F;
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