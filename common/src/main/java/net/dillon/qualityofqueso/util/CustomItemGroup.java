package net.dillon.qualityofqueso.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.dillonlib.factory.Factories;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.qualityofqueso.command.QuesoCustomItemGroupsCommand;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.CustomItemGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.ValidationWrapper;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static net.dillon.dillonlib.task.ClientTasks.executeIfClientPlayer;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.customItemGroups;
import static net.dillon.qualityofqueso.util.ModOptionUtil.canExecuteCommand;

/**
 * Stores a custom item group.
 */
public class CustomItemGroup {
    private final String name;
    private String icon;
    private final List<String> items;
    public static boolean PARSING_CUSTOM_ITEM_GROUPS = false;
    public static boolean ERROR_PARSING_FILE = false;
    public static String PARSING_ERROR = "";
    public static final String EXAMPLE_ITEM_GROUP_NAME = "Example Group";
    public static final CustomItemGroup EXAMPLE_ITEM_GROUP = new CustomItemGroup(
            EXAMPLE_ITEM_GROUP_NAME,
            "minecraft:grass_block",
            List.of(
                    "dirt",
                    "minecraft:cobblestone",
                    "diamond_sword[damage=50]",
                    "minecraft:netherite_sword[enchantments={protection:4}]"
            )
    );

    /**
     * Build a custom item group.
     * @param name the name for the item group
     * @param icon the icon for the item group
     * @param items the list of items for the item group (can have components)
     */
    public CustomItemGroup(String name, String icon, List<String> items) {
        this.name = name;
        this.icon = icon;
        this.items = items;
    }

    /**
     * @return the name for this group.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the list of items in the group.
     */
    public List<String> getItems() {
        return this.items;
    }

    /**
     * Opens the custom item group file.
     */
    public static Path filePath() {
        return Platforms.getUniversalPlatform().configDir()
                .resolve(ModConstants.DEFAULT_CONFIG_DIR)
                .toFile().toPath()
                .resolve(ModConstants.DEFAULT_CUSTOM_ITEM_GROUPS_CONFIG_FILE_NAME);
    }

    /**
     * @return the list of {@link CustomItemGroup}.
     */
    public static List<CustomItemGroup> getCustomItemGroups() {
        return customItemGroups().customItemGroups;
    }

    /**
     * Saves the custom item groups config.
     */
    private static void writeItemGroups() {
        CustomItemGroups.INSTANCE.save();
    }

    /**
     * Updates a custom item group based on its name.
     */
    public static void updateCustomItemGroup(String name, Consumer<CustomItemGroup> customItemGroupConsumer) {
        customItemGroupConsumer.accept(groupByName(name));
        writeItemGroups();
    }

    /**
     * Updates the list of custom item groups.
     */
    public static void updateCustomItemGroups(Consumer<List<CustomItemGroup>> customItemGroupsConsumer) {
        customItemGroupsConsumer.accept(getCustomItemGroups());
        writeItemGroups();
    }

    /**
     * @return if a custom item group exists.
     */
    public static boolean contains(String name) {
        return groupByName(name) != null;
    }

    /**
     * @return a custom item group by its name.
     */
    public static CustomItemGroup groupByName(String name) {
        for (CustomItemGroup group : getCustomItemGroups()) {
            if (group.getName().equals(name)) {
                return group;
            }
        }

        return null;
    }

    /**
     * @return if an item group doesn't exist.
     */
    public static boolean groupDoesntExist(ClientCommandFeedback feedback, String name) {
        if (!canExecuteCommand(feedback)) {
            return true;
        }

        if (!contains(name)) {
            feedback.sendFailure(Component.translatable("qualityofqueso.custom_item_groups_command.doesnt_exist"));
            return true;
        }

        return false;
    }

    /**
     * @return if an item group already exists.
     */
    public static boolean groupExists(ClientCommandFeedback feedback, String name) {
        if (!canExecuteCommand(feedback)) {
            return true;
        }

        if (contains(name)) {
            feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.already_exists"));
            return true;
        }

        return false;
    }

    /**
     * Creates a custom item group.
     */
    public static int createGroup(ClientCommandFeedback feedback, String groupName, String icon) {
        if (groupExists(feedback, groupName)) {
            return 0;
        }

        CustomItemGroup newGroup = new CustomItemGroup(groupName, icon, List.of());
        updateCustomItemGroups(customItemGroups -> customItemGroups.add(newGroup));
        feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.created", groupName));
        return 1;
    }

    /**
     * Deletes a custom item group.
     */
    public static int deleteGroup(ClientCommandFeedback feedback, String groupName) {
        if (groupDoesntExist(feedback, groupName)) {
            return 0;
        }

        CustomItemGroup group = groupByName(groupName);
        updateCustomItemGroups(customItemGroups -> customItemGroups.remove(group));
        feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.deleted", groupName));
        return 1;
    }

    /**
     * Replaces an icon for a group.
     */
    public static int replaceIcon(ClientCommandFeedback feedback, String groupName, String newIcon) {
        if (groupDoesntExist(feedback, groupName)) {
            return 0;
        }

        String rawIcon = convertToRawItem(newIcon);
        updateCustomItemGroup(groupName, customItemGroup -> customItemGroup.icon = rawIcon);
        feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.replace_icon", groupName, rawIcon));
        return 1;
    }

    /**
     * Converts the passed item to a raw item (ex. minecraft:diamond)
     */
    private static String convertToRawItem(String itemEntryOrArg) {
        int componentIndex = itemEntryOrArg.indexOf("[");
        if (componentIndex != -1) {
            itemEntryOrArg = itemEntryOrArg.substring(0, componentIndex);
        }

        return Identifier.parse(itemEntryOrArg).toString();
    }

    /**
     * @return the list of raw items.
     */
    public static List<String> getRawItems(String groupName) {
        // Convert all entries in group to their raw variant (ex. minecraft:diamond)
        try {
            return groupByName(groupName)
                    .getItems()
                    .stream()
                    .map(CustomItemGroup::convertToRawItem)
                    .toList();
        } catch (NullPointerException o) {
            return List.of();
        }
    }

    /**
     * @return if the passed item is already in the item group.
     */
    private static boolean itemExistsInGroup(String groupName, String rawItem) {
        // Then check if rawItems list contains rawItem and fail if so
        return getRawItems(groupName).contains(rawItem);
    }

    /**
     * @return if the passed item is an actual valid item.
     */
    private static boolean isItemValid(ClientCommandFeedback feedback, String groupName, String itemArgument, boolean add) {
        // Check if item is an actual valid stack
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(convertToRawItem(itemArgument)));
        if (item.getDefaultInstance().isEmpty()) {
            feedback.sendFailure(Component.translatable("qualityofqueso.custom_item_groups_command.item_modify_fail", add ? "add" : "remove", groupName, itemArgument));
            return false;
        }

        return true;
    }

    /**
     * @return if a custom item group can be modified.
     */
    private static boolean canModifyGroup(ClientCommandFeedback feedback, String groupName, String itemArgument, boolean add) {
        if (groupDoesntExist(feedback, groupName)) {
            return false;
        }

        // Check if item is an actual valid stack
        return isItemValid(feedback, groupName, itemArgument, add);
    }

    /**
     * @return the list of item entries in a group.
     */
    private static List<String> getItemEntries(String groupName) {
        return Objects.requireNonNull(groupByName(groupName)).getItems();
    }

    /**
     * @return if the specified custom item group contains the entry passed by item arugment.
     */
    private static boolean groupContainsEntry(String entry) {
        return !entry.isBlank();
    }

    /**
     * @return the entry of a custom item group.
     */
    private static String getEntryFromRawInput(List<String> entries, String rawItem) {
        for (String entry : entries) {
            if (entry.contains(Identifier.parse(rawItem).getPath())) {
                return entry;
            }
        }
        return "";
    }

    /**
     * Adds an item to a custom item group.
     */
    public static int addItemToGroup(ClientCommandFeedback feedback, String groupName, String itemArgument, String overwriteArgument) {
        if (!canModifyGroup(feedback, groupName, itemArgument, true)) {
            return 0;
        }

        // Get rawItem from the user's inputted item argument
        String rawItem = convertToRawItem(itemArgument);

        // Determine if user wants to overwrite existing entry
        boolean overwrite = overwriteArgument.equals(QuesoCustomItemGroupsCommand.overwriteArgument);

        // Attempt to overwrite the entry if specified
        if (overwrite) {
            // Get the list of entries in the specified group
            List<String> entries = getItemEntries(groupName);

            // Get the exact entry from the group based on raw item (could be empty)
            String entry = getEntryFromRawInput(entries, rawItem);

            // If it's not empty, replace it
            if (groupContainsEntry(entry)) {
                updateCustomItemGroup(groupName, customItemGroup -> customItemGroup.getItems().set(
                        entries.indexOf(entry), itemArgument
                ));
                feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.item_add_replace", rawItem, groupName, itemArgument));
                return 1;
            }
        }
        // Otherwise, fail if rawItem does exist in group
        else if (itemExistsInGroup(groupName, rawItem)) {
            feedback.sendFailure(Component.translatable("qualityofqueso.custom_item_groups_command.item_add_fail", groupName, rawItem));
            return 0;
        }

        // Successfully update the list with the new entry if not overwriting and exists.
        updateCustomItemGroup(groupName, customItemGroup -> customItemGroup.getItems().add(itemArgument));
        feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.item_add", rawItem, groupName));
        return 1;
    }

    /**
     * Removes an item from a custom item group.
     */
    public static int removeItemFromGroup(ClientCommandFeedback feedback, String groupName, String itemArgument) {
        if (!canModifyGroup(feedback, groupName, itemArgument, false)) {
            return 0;
        }

        // If item doesn't exist in group, don't add
        String rawItem = convertToRawItem(itemArgument);
        if (!itemExistsInGroup(groupName, rawItem)) {
            return failedToRemoveItem(feedback, groupName, rawItem);
        }

        // Check for valid entry and return if found
        List<String> entries = getItemEntries(groupName);

        // Get the exact entry from the group based on raw item (could be empty)
        String entry = getEntryFromRawInput(entries, rawItem);

        // If it's not empty, remove it
        if (groupContainsEntry(entry)) {
            updateCustomItemGroup(groupName, customItemGroup -> customItemGroup.getItems().remove(entry));
            feedback.sendSuccess(Component.translatable("qualityofqueso.custom_item_groups_command.item_remove", rawItem, groupName));
            return 1;
        }

        // Otherwise, fail
        return failedToRemoveItem(feedback, groupName, rawItem);
    }

    /**
     * @return if the item failed to remove.
     */
    private static int failedToRemoveItem(ClientCommandFeedback feedback, String groupName, String rawItem) {
        feedback.sendFailure(Component.translatable("qualityofqueso.custom_item_groups_command.item_remove_fail", groupName, rawItem));
        return 0;
    }

    /**
     * Registers all custom item groups.
     */
    public static void registerCustomItemGroups() {
        List<CustomItemGroup> groups = getCustomItemGroups();

        // Register each item group
        for (int i = 0; i < groups.size(); i++) {
            CustomItemGroup customItemGroup = groups.get(i);

            // Don't register the example group
            String groupName = customItemGroup.name;
            if (groupName.equalsIgnoreCase(EXAMPLE_ITEM_GROUP_NAME)) {
                continue;
            }

            // Register the group
            String icon = customItemGroup.icon;
            Factories.registerSimpleItemGroupFactory(
                    qoqIdentifier("qoq_item_group_" + i),
                    Component.literal(groupName),
                    () -> parseIcon(groupName, icon),
                    () -> parseItems(groupName, customItemGroup.getItems())
            );
        }
    }

    /**
     * Parses an icon for a custom item group.
     */
    private static ItemLike parseIcon(String groupName, String icon) {
        String rawIcon = convertToRawItem(icon);
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(rawIcon));

        if (item.getDefaultInstance().isEmpty()) {
            executeIfClientPlayer(localPlayer -> localPlayer.sendSystemMessage(
                            Component.translatable("qualityofqueso.custom_item_group_icon_parse_failed", groupName, rawIcon)
                                    .withStyle(ChatFormatting.GOLD)
                    )
            );
            return Items.BARRIER;
        }

        return item;
    }

    /**
     * Parses items from the list of item groups.
     */
    private static List<ItemStack> parseItems(String groupName, List<String> items) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.getConnection() == null) {
            return List.of();
        }

        FeatureFlagSet features = minecraft.level.enabledFeatures();

        ParseContext context = new ParseContext.Simple(
                PermissionSet.ALL_PERMISSIONS,
                minecraft.getConnection().registryAccess(),
                features,
                ValidationWrapper.Provider.IMMEDIATE
        );

        if (items.isEmpty()) {
            executeIfClientPlayer(localPlayer -> localPlayer.sendSystemMessage(
                    Component.translatable("qualityofqueso.custom_item_group_empty", groupName)
                            .withStyle(ChatFormatting.GOLD)
            ));
            return List.of();
        }

        PARSING_CUSTOM_ITEM_GROUPS = true;
        List<ItemStack> stacks = new ArrayList<>();

        for (String item : items) {
            try {
                ItemInput input = ItemArgument.INSTANCE.parse(
                        new StringReader(item),
                        context
                );

                if (stacks.stream().noneMatch(stack -> stack.is(input.item().value()))) {
                    stacks.add(input.createItemStack(1));
                } else {
                    executeIfClientPlayer(localPlayer -> localPlayer.sendSystemMessage(
                                    Component.translatable("qualityofqueso.custom_item_group_duplicate_item_found", groupName, item)
                                            .withStyle(ChatFormatting.RED)
                            )
                    );
                }
            } catch (CommandSyntaxException e) {
                executeIfClientPlayer(localPlayer -> localPlayer.sendSystemMessage(
                                Component.translatable("qualityofqueso.custom_item_group_parse_failed", groupName, item)
                                        .withStyle(ChatFormatting.RED)
                                        .withStyle(
                                                Style.EMPTY
                                                        .withHoverEvent(
                                                                new HoverEvent.ShowText(Component.literal(e.getMessage()))
                                                        )
                                        )
                        )
                );
                ModConstants.LOGGER.error("Failed to parse custom item: {}", item, e);
            }
        }

        PARSING_CUSTOM_ITEM_GROUPS = false;
        return stacks;
    }
}