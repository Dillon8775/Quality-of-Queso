package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.dillon.qualityofqueso.main.QualityOfQueso.options;

public class ItemFrameSearcherCommand {

    /**
     * Registers the {@code /itemframesearcher} command.
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
                CommandManager.literal("itemframesearcher")
                        .requires(source -> options().enableMod && source.hasPermissionLevel(0))
                        .then(
                                CommandManager.literal("clear")
                                        .executes(
                                                context -> execute(
                                                        context,
                                                        "",
                                                        true,
                                                        0,
                                                        options().itemFrameSearchRadius
                                                )
                                        )
                                        .then(
                                                CommandManager.argument("radius", IntegerArgumentType.integer(25, 300))
                                                        .executes(
                                                                context -> execute(
                                                                        context,
                                                                        "",
                                                                        true,
                                                                        0,
                                                                        IntegerArgumentType.getInteger(context, "radius")
                                                                )
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("find")
                                        .then(
                                                CommandManager.argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                                        .executes(
                                                                context -> execute(
                                                                        context,
                                                                        ItemStackArgumentType.getItemStackArgument(context, "item").getItem().toString(),
                                                                        false,
                                                                        0,
                                                                        options().itemFrameSearchRadius
                                                                )
                                                        )
                                                        .then(
                                                                CommandManager.argument("radius", IntegerArgumentType.integer(25, 300))
                                                                        .executes(
                                                                                context -> execute(
                                                                                        context,
                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item").getItem().toString(),
                                                                                        false,
                                                                                        0,
                                                                                        IntegerArgumentType.getInteger(context, "radius")
                                                                                )
                                                                        )
                                                                        .then(
                                                                                CommandManager.argument("timer", IntegerArgumentType.integer(0, 180))
                                                                                        .executes(
                                                                                                context -> execute(
                                                                                                        context,
                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "item").getItem().toString(),
                                                                                                        false,
                                                                                                        IntegerArgumentType.getInteger(context, "timer"),
                                                                                                        IntegerArgumentType.getInteger(context, "radius")
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    /**
     * Executes the command and makes respective item frames glow.
     */
    private static int execute(CommandContext<ServerCommandSource> context, String query, boolean clear, int timer, int radius) {
        if (clear) {
            context.getSource().sendFeedback(() -> Text.translatable("qualityofqueso.item_frame_searcher_command.executed.clear", radius), true);
        } else if (timer == 0) {
            context.getSource().sendFeedback(() -> Text.translatable("qualityofqueso.item_frame_searcher_command.executed.without_timer", query, radius), true);
        } else {
            context.getSource().sendFeedback(() -> Text.translatable("qualityofqueso.item_frame_searcher_command.executed.with_timer", query, radius, timer), true);
        }
        ClientPlayNetworking.send(new GlowSearchC2SPayload(query, clear, timer, radius));
        return 0;
    }
}