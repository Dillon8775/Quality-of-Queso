package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

/**
 * Constant variables and cooldowns for Quality of Queso.
 */
public class ModConstants {
    // Common constants
    public static final String MOD_ID = "qualityofqueso";
    public static final Component VERSION = Component.literal(MultiLoader.getPlatform().getModVersion()).withStyle(ChatFormatting.GOLD);
    public static final String WIKI_LINK = "https://quality-of-queso.fandom.com/wiki/Quality_of_Queso_Wiki";
    public static final String DISCORD_LINK = "https://discord.gg/vfqEAn4YFy";
    public static final String SHOWCASE_VIDEO_LINK = "https://youtu.be/02wfcgHkPmQ";
    public static final String RESOURCE_PACK_TEMPLATE = "https://1drv.ms/f/c/dde4bd527f59679e/IgAnwmEFKQchQ727NndBW9D9AbeVC4Jl8CuXTXMQ_CWUorw?e=Q5gEW0";
    public static final String OTHER_QOL_MODS_LINK = "https://modrinth.com/collection/PDFZoFqE";
    public static boolean SHOWN_BETA_TOAST = false;

    // Static variables for management
    public static int RENDERED_BUTTONS = 0;
    public static int MOVE_AMOUNT = 1;
    public static Container CURRENT_CONTAINER = null;
    public static final String BUTTON_OR_KEY_SERIALIZED_NAME = "§bButton §r§oor §r§eKey";
    public static final String KEY_ONLY_SERIALIZED_NAME = "§eKey Only";
    public static final String ALWAYS_SERIALIZED_NAME = "§aAlways";
    public static final String ENABLED_SERIALiZED_NAME = "§aEnabled";
    public static final String OFF_SERIALIZED_NAME = "§7OFF";
    public static final String ALPHABETICALLY_SERIALIZED_NAME = "§aAlphabetically";
    public static final String BY_TAG_SERIALIZED_NAME = "§bBy Tag";
    public static final String DESCENDING_SERIALIZED_NAME = "§cDescending";
    public static final String ASCENDING_SERIALIZED_NAME = "§2Ascending";
    public static final String CREATIVE_MENU_SERIALIZED_NAME = "§aC§br§5e§6a§7t§di§ev§9e§r§f Menu";

    // Static variables for management button layout lists
    public static final String TRANSFER_CONTAINER_BUTTON_SERIALIZED_NAME = "Transfer Container Button";
    public static final String TRANSFER_INVENTORY_BUTTON_SERIALIZED_NAME = "Transfer Inventory Button";
    public static final String LOCK_INVENTORY_BUTTON_SERIALIZED_NAME = "Lock Inventory Button";
    public static final String INCLUDE_HOTBAR_BUTTON_SERIALIZED_NAME = "Include Hotbar Button";
    public static final String ALWAYS_QUICK_MOVE_BUTTON_SERIALIZED_NAME = "Always Quick Move Button";
    public static final String FILTERING_BUTTON_SERIALIZED_NAME = "Filtering Button";
    public static final String SORT_BUTTON_SERIALIZED_NAME = "Sort Button";
    public static final String BULK_CRAFT_BUTTON_SERIALIZED_NAME = "Bulk Craft Button";
    public static final String QUICK_DROP_BUTTON_SERIALIZED_NAME = "Quick Drop Button";
    public static final String SWAP_BUTTON_SERIALIZED_NAME = "Swap Button";
    public static final String SEARCH_TRANSPORTABLES_BUTTON_SERIALIZED_NAME = "Search Transportables Button";
    public static final String CLEAR_EXCLUDED_SLOTS_BUTTON_SERIALIZED_NAME = "Clear Excluded Slots Button";

    // Variables for management reference
    public static boolean SEARCHING_TRANSPORTABLES = true;
    public static boolean SAVING_EXCLUDED_SLOTS = false;

    // Button names and paths
    public static final String TRANSFER_INVENTORY_BUTTON_NAME = "transfer_inventory";
    public static final String TRANSFER_INVENTORY_BUTTON_PATH = "transfer/inventory/";
    public static final String TRANSFER_CONTAINER_BUTTON_NAME = "transfer_container";
    public static final String TRANSFER_CONTAINER_BUTTON_PATH = "transfer/container/";
    public static final String QUICK_DROP_BUTTON_NAME = "quick_drop";
    public static final String MOVE_ONE_PATH = "singular_moving/move_one";
    public static final String BASE_BUTTON_NAME = "button";
    public static final String BASE_BUTTON_HOVERED_PATH = "base/hovered/basic_hovered";

    // Cooldowns
    public static final int DEFAULT_LOCKED_SLOT_SOUND_COOLDOWN = 3;
    public static int LOCKED_SLOT_SOUND_COOLDOWN = DEFAULT_LOCKED_SLOT_SOUND_COOLDOWN;
    public static final int DEFAULT_SORT_SOUND_COOLDOWN = 2;
    public static int SORT_SOUND_COOLDOWN = DEFAULT_SORT_SOUND_COOLDOWN;
    public static final int DEFAULT_TRACKED_CONTAINER_COOLDOWN = 2;
    public static int TRACKED_CONTAINER_COOLDOWN = DEFAULT_TRACKED_CONTAINER_COOLDOWN;

    // Other global variables
    public static Map<Integer, Set<Integer>> SAVED_EXCLUDED_SLOTS = new HashMap<>();
    public static CurrentSortingMode GLOBAL_SORTING_MODE = clientOptionsInstance().getSortingOptions().currentSortingMode;

    // Texture constants
    public static final String CHEESE_WHEEL_TEXTURE = "widget/logo";
    public static final String ENABLED_TEXTURE = "sprites/widget/enabled";
    public static final String DISABLED_TEXTURE = "sprites/widget/disabled";
    public static final String OPEN_SCREENSHOTS_DIRECTORY_TEXTURE = "widget/screenshots";
    public static final String OPEN_WORLD_DIRECTORY_TEXTURE = "widget/world_folder";
    public static final String OPEN_CONFIG_DIRECTORY_TEXTURE = "widget/config_folder";
    public static final String ENDER_CHEST = "widget/ender_chest";
    public static final String DISCORD_TEXTURE = "widget/discord";
    public static final String YOUTUBE_TEXTURE = "widget/youtube";
    public static final String LOCKED_TEXTURE = "locked_slot/locked";
    public static final String WIKI_TEXTURE = "widget/wiki";
    public static final String MULTI_CONFIG_TEXTURE = "widget/multi_config";
    public static final ResourceLocation SEARCH_TEXTURE = ResourceLocation.withDefaultNamespace("icon/search");
    public static final ResourceLocation SLOT_CRITICAL = ofQoQ("hud/colored_slot/slot_critical");
    public static final ResourceLocation SLOT_LOW = ofQoQ("hud/colored_slot/slot_low");
    public static final ResourceLocation SLOT_AVERAGE = ofQoQ("hud/colored_slot/slot_average");
    public static final ResourceLocation SLOT_DECENT = ofQoQ("hud/colored_slot/slot_decent");
    public static final ResourceLocation SLOT_GOOD = ofQoQ("hud/colored_slot/slot_good");
    public static final ResourceLocation SLOT_LOCKED = ofQoQ("hud/colored_slot/slot_locked");
    @Deprecated
    public static final ResourceLocation SELECTED_RECIPE = ofQoQ("slot/selected_recipe");

    // Config constants
    public static final String DEFAULT_CONFIG_DIR = "qualityofqueso/global";
    public static final String DEFAULT_SERVER_CONFIG_DIR = "qualityofqueso/server-configs/";
    public static final String DEFAULT_CLIENT_CONFIG_FILE_NAME = "client.json";
    public static final String DEFAULT_COMMON_CONFIG_FILE_NAME = "common.json";
    public static final String DEFAULT_UNIVERSAL_CONFIG_FILE_NAME = "universal.json";
    public static final String DEFAULT_MIXIN_CONFIG_FILE_NAME = "mixins.json";
    public static final String DEFAULT_CONTAINER_DATA_FILE_NAME = "container_data.json";
    public static final String DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME = "locked_container_slots.json";
    public static final String DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME = "locked_player_slots.json";

    // HUD constants
    public static double PLAYER_FALL_DISTANCE;
    public static boolean SHOULD_WARN_OF_ELYTRA;
    public static boolean CAN_ACTUALLY_RENDER_ARMOR_HOTBAR;
    public static final int[] ARMOR_TIMERS = new int[4];
    public static final ItemStack[] LAST_ARMOR_STACKS = new ItemStack[4];

    // Shulker state constants
    public static final String FILTERED = "filtered";
    public static final String TAG_FILTERED = "tag_filtered";
    public static final String FILTER_ITEMS = "filter_items";
    public static final String LOCKED_SLOTS = "locked_slots";
    public static final String SORTING_MODE = "sorting_mode";

    // Search bar constants
    public static String SAVED_TEXT = "";
    public static String SAVED_ITEM_FRAME_TEXT = "";
    public static String SAVED_CREATIVE_MENU_TEXT = "";

    // Default colors
    public static final int DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR = -12566464;
    public static final int DEFAULT_LOCKED_SLOT_COLOR = 1721803007;
    public static final int TAG_COLOR = 0x7FFFFF;
    public static final int ITEM_COLOR = 0x96FFB7;
}