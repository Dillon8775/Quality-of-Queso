package net.dillon.qualityofqueso.command;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.qualityofqueso.sound.OtherSoundEvents;
import net.dillon.qualityofqueso.util.CustomItemGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.net.URI;
import java.util.function.Function;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.quoted;
import static net.dillon.qualityofqueso.sound.ModSoundEvents.getSoundFromRegistration;
import static net.dillon.qualityofqueso.util.CustomItemGroup.filePath;
import static net.dillon.qualityofqueso.util.CustomItemGroup.getCustomItemGroups;

/**
 * The command to open the custom item groups file.
 */
public class QuesoCustomItemGroupsCommand {
    private static final String nameArgument = "name";
    private static final String iconArgument = "icon";
    private static final String itemArgument = "item";
    public static final String overwriteArgument = "overwrite";

    /**
     * Executes the command.
     */
    public static void execute() {
        if (!modEnabled()) {
            return;
        }

        Blaze3D.openPath(filePath());

        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            return;
        }

        player.sendSystemMessage(Component.translatable("qualityofqueso.custom_item_groups_command")
                .withStyle(ChatFormatting.GREEN)
                .withStyle(
                        Style.EMPTY
                                .withHoverEvent(
                                        new HoverEvent.ShowText(Component.translatable("qualityofqueso.custom_item_groups_command.open_file"))
                                )
                                .withClickEvent(
                                        new ClickEvent.OpenFile(filePath())
                                )
                )
        );

        player.sendSystemMessage(
                Component.translatable("qualityofqueso.custom_item_groups_command.help")
                        .withStyle(ChatFormatting.GOLD)
                        .withStyle(
                                Style.EMPTY
                                        .withHoverEvent(
                                                new HoverEvent.ShowText(Component.translatable("qualityofqueso.custom_item_groups_command.help.tooltip"))
                                        )
                                        .withClickEvent(
                                                new ClickEvent.OpenUrl(URI.create("https://www.gamergeeks.net/apps/minecraft/give-command-generator"))
                                        )
                        )
        );

        player.sendSystemMessage(
                Component.translatable("qualityofqueso.custom_item_groups_command.more")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );

        ClientTasks.playLocalSoundToLocalPlayer(getSoundFromRegistration(OtherSoundEvents.FILE_OPENED.sound()), 0.2F, 1.0F);
    }

    /**
     * Adds an item to a custom item group.
     */
    private static <S> int addItemToGroup(CommandContext<S> context, Function<S, ClientCommandFeedback> feedback, String overwrite) {
        return CustomItemGroup.addItemToGroup(
                feedback.apply(context.getSource()),
                getName(context),
                getItemAsString(context, itemArgument),
                overwrite
        );
    }

    /**
     * @return a created name argument.
     */
    private static <S> RequiredArgumentBuilder<S, String> name() {
        return name(false);
    }

    /**
     * @return a created name argument.
     */
    private static <S> RequiredArgumentBuilder<S, String> name(boolean create) {
        RequiredArgumentBuilder<S, String> argument = RequiredArgumentBuilder.argument(nameArgument, StringArgument.string());
        if (!create) {
            argument.suggests((context, builder) -> {
                getCustomItemGroups()
                        .stream()
                        .filter(customItemGroup -> !customItemGroup.getName().equals(CustomItemGroup.EXAMPLE_ITEM_GROUP_NAME))
                        .forEach(customItemGroup -> builder.suggest(quoted(customItemGroup.getName(), false)));
                return builder.buildFuture();
            });
        }
        return argument;
    }

    /**
     * @return a created icon argument.
     */
    private static <S> RequiredArgumentBuilder<S, ItemInput> icon() {
        return RequiredArgumentBuilder.argument(iconArgument, ItemArgument.item());
    }

    /**
     * @return a created overwrite argument.
     */
    private static <S> LiteralArgumentBuilder<S> overwrite() {
        return LiteralArgumentBuilder.literal(overwriteArgument);
    }

    /**
     * @return a created removable items argument.
     */
    private static <S> RequiredArgumentBuilder<S, String> removableItems() {
        return RequiredArgumentBuilder.<S, String>argument(itemArgument, StringArgument.string())
                .suggests((context, builder) -> {
                    CustomItemGroup.getRawItems(getName(context)).forEach(item -> builder.suggest(quoted(item, true)));
                    return builder.buildFuture();
                });
    }

    /**
     * @return the passed name argument.
     */
    private static <S> String getName(CommandContext<S> context) {
        return StringArgument.getString(context, nameArgument);
    }

    /**
     * @return a created item from a {@link ItemInput}.
     */
    private static <S> String getItem(CommandContext<S> context) {
        return StringArgument.getString(context, itemArgument);
    }

    /**
     * @return a created icon item from a {@link ItemInput}.
     */
    private static <S> String getIcon(CommandContext<S> context) {
        return getItemAsString(context, iconArgument);
    }

    /**
     * @return a created item from a {@link ItemInput}.
     */
    private static <S> String getItemAsString(CommandContext<S> context, String argumentName) {
        var range = context.getNodes().stream()
                .filter(node -> node.getNode().getName().equals(argumentName))
                .findFirst()
                .orElseThrow()
                .getRange();

        return context.getInput().substring(range.getStart(), range.getEnd());
    }

    /**
     * @return the {@code /quesocustomitemgroups} command.
     */
    public static <S> LiteralArgumentBuilder<S> create(Function<S, ClientCommandFeedback> feedback) {
        return LiteralArgumentBuilder.<S>literal("queso_custom_item_groups")
                .then(
                        LiteralArgumentBuilder.<S>literal("open_file")
                                .executes(context -> {
                                    execute();
                                    return 1;
                                })
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("delete")
                                .then(
                                        QuesoCustomItemGroupsCommand.<S>name()
                                                .executes(context -> CustomItemGroup.deleteGroup(
                                                                feedback.apply(context.getSource()),
                                                                getName(context)
                                                        )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("create")
                                .then(
                                        QuesoCustomItemGroupsCommand.<S>name(true)
                                                .then(
                                                        QuesoCustomItemGroupsCommand.<S>icon()
                                                                .executes(context -> CustomItemGroup.createGroup(
                                                                                feedback.apply(context.getSource()),
                                                                                getName(context),
                                                                                getIcon(context)
                                                                        )
                                                                )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("replace_icon")
                                .then(
                                        QuesoCustomItemGroupsCommand.<S>name()
                                                .then(
                                                        QuesoCustomItemGroupsCommand.<S>icon()
                                                                .executes(context -> CustomItemGroup.replaceIcon(
                                                                                feedback.apply(context.getSource()),
                                                                                getName(context),
                                                                                getIcon(context)
                                                                        )
                                                                )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("remove_item")
                                .then(
                                        QuesoCustomItemGroupsCommand.<S>name()
                                                .then(
                                                        QuesoCustomItemGroupsCommand.<S>removableItems()
                                                                .executes(context -> CustomItemGroup.removeItemFromGroup(
                                                                                feedback.apply(context.getSource()),
                                                                                getName(context),
                                                                                getItem(context)
                                                                        )
                                                                )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("add_item")
                                .then(
                                        QuesoCustomItemGroupsCommand.<S>name()
                                                .then(
                                                        RequiredArgumentBuilder.<S, ItemInput>argument(itemArgument, ItemArgument.item())
                                                                .executes(context -> addItemToGroup(
                                                                                context,
                                                                                feedback,
                                                                                ""
                                                                        )
                                                                )
                                                                .then(
                                                                        QuesoCustomItemGroupsCommand.<S>overwrite()
                                                                                .executes(context -> addItemToGroup(
                                                                                                context,
                                                                                                feedback,
                                                                                                overwriteArgument
                                                                                        )
                                                                                )
                                                                )
                                                )
                                )
                );
    }
}