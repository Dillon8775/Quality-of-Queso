package net.dillon.qualityofqueso.option;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * Forge configuration for the QoQ.
 */
@OnlyIn(Dist.CLIENT)
public class ModClientOptions {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;
    public static final ForgeConfigSpec.BooleanValue BETTER_GUI_EXIT;
    public static final ForgeConfigSpec.BooleanValue BETTER_SEARCHING;
    public static final ForgeConfigSpec.BooleanValue CHEST_SEARCHING;
    public static final ForgeConfigSpec.BooleanValue SEARCH_INVENTORY;
    public static final ForgeConfigSpec.BooleanValue INVENTORY_SEARCHING;
    public static final ForgeConfigSpec.BooleanValue SAVE_SEARCH_TEXT;
    public static final ForgeConfigSpec.BooleanValue INVENTORY_MANAGEMENT;
    public static final ForgeConfigSpec.BooleanValue SHORTCUT_KEYS;
    public static final ForgeConfigSpec.BooleanValue QUICK_DROP;
    public static final ForgeConfigSpec.BooleanValue SWAPPING;
    public static final ForgeConfigSpec.BooleanValue INCLUDE_HOTBAR;
    public static final ForgeConfigSpec.BooleanValue QUICK_EQUIP;
    public static final ForgeConfigSpec.BooleanValue PREVENT_RAGE_QUITTING;
    public static final ForgeConfigSpec.BooleanValue PREVENT_E_FROM_TYPING;
    public static final ForgeConfigSpec.BooleanValue HELPFUL_TOOLTIPS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_SERVERS;
    public static final ForgeConfigSpec.IntValue ITEM_FRAME_SEARCH_TIMER;
    public static final ForgeConfigSpec.IntValue ITEM_FRAME_SEARCH_RADIUS;
    public static final ForgeConfigSpec.EnumValue<QoQButtons> QOQ_BUTTONS;

    static {
        BUILDER.push("Client-side config for Quality of Queso");

        ENABLE_MOD = BUILDER
                .comment("Allows the entire mod to function.")
                .define("enableMod", true);
        BETTER_GUI_EXIT = BUILDER
                .comment("Close any GUI menu when you click anywhere outside of the GUI screen.")
                .define("betterGuiExit", true);
        BETTER_SEARCHING = BUILDER
                .comment("Begin typing no matter what screen you are on to search for items.")
                .define("betterSearching", true);
        CHEST_SEARCHING = BUILDER
                .comment("Adds a search bar to the top of chests and shulker boxes.")
                .define("chestSearching", true);
        SEARCH_INVENTORY = BUILDER
                .comment("Searches the players inventory in addition to searching for items in a container.")
                .define("searchInventory", true);
        INVENTORY_SEARCHING = BUILDER
                .comment("Adds a search bar to the top of your inventory.")
                .define("inventorySearching", true);
        SAVE_SEARCH_TEXT = BUILDER
                .comment("Saves the text in the chest search bar when closing the screen.")
                .define("saveSearchText", false);
        INVENTORY_MANAGEMENT = BUILDER
                .comment("Transfer items from container -> inventory, and vise-versa.")
                .define("inventoryManagement", true);
        SHORTCUT_KEYS = BUILDER
                .comment("Allows quick moving of items by using the CTRL shortcut keys.")
                .define("shortcutKeys", true);
        QUICK_EQUIP = BUILDER
                .comment("Quickly equip the hovered item to the respective equipment slot by right-clicking, or using the Quick Equip key.")
                .define("quickEquip", true);
        QUICK_DROP = BUILDER
                .comment("Quick drop (or throw) all items in a container/inventory.")
                .define("quickDrop", false);
        SWAPPING = BUILDER
                .comment("Swap all items in a container with the corresponding inventory slot.")
                .define("swapping", false);
        INCLUDE_HOTBAR = BUILDER
                .comment("Allows you to include/exclude all items from the hotbar when transferring items from inventory -> container.")
                .define("includeHotbar", true);
        PREVENT_RAGE_QUITTING = BUILDER
                .comment("Prevents the player from quickly pressing the \"disconnect\" button.")
                .define("preventRageQuitting", false);
        PREVENT_E_FROM_TYPING = BUILDER
                .comment("Prevents the \"E\" key from typing in any search menu.")
                .define("prevent_E_fromTyping", false);
        HELPFUL_TOOLTIPS = BUILDER
                .comment("Displays helpful tooltips.")
                .define("helpfulTooltips", true);
        BLACKLISTED_SERVERS = BUILDER
                .comment("All mod features will be disabled on all servers in this list.")
                .defineListAllowEmpty("blacklistedServers", List.of(), obj -> obj instanceof String);
        ITEM_FRAME_SEARCH_TIMER = BUILDER
                .comment("Specifies how long item frames will remain glowing.")
                .defineInRange("itemFrameSearchTimer", 0, 0, Integer.MAX_VALUE);
        ITEM_FRAME_SEARCH_RADIUS = BUILDER.
                comment("Determines the radius for finding item frames. 1m = 1block.")
                .defineInRange("itemFrameSearchRadius", 150, 0, 500);
        QOQ_BUTTONS = BUILDER
                .comment("Displays all Quality of Queso buttons on the game menu screen.")
                .defineEnum("showQoQButtons", QoQButtons.EVERYWHERE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
