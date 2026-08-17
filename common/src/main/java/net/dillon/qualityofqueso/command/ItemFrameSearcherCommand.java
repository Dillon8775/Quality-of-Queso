package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.handleGlowPacket;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.common;

/**
 * The command to search item frames, separate from the GUI screen.
 */
@Dill(DillType.COMMON)
public class ItemFrameSearcherCommand {
    private static final String ITEM = "item (string)";
    private static final String RADIUS = "radius (1-300)";
    private static final String GLOW_DURATION = "glow duration (seconds, max 180)";

    /**
     * Executes the command and makes respective item frames glow.
     */
    private static int execute(CommandSourceStack context, String query, boolean clear, int timer, int radius) {
        // Send the "clear" message to the player
        if (clear) {
            context.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.clear", radius), true);
        } else if (timer == 0) { // Send the "searched" message to the player within a specific radius
            context.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.without_timer", query, radius), true);
        } else { // Send the "searched" message, and glow time message to the player, within a specific radius
            context.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.with_timer", query, radius, timer), true);
        }
        // Detect if the query was match case
        boolean matchCase = query.startsWith(":");
        // Send the glow packet raw, do not use Balm; substring the query if matchCase to exclude the colon :
        handleGlowPacket(context.getPlayer(), query.substring(matchCase ? 1 : 0), query.startsWith(":"), clear, timer, radius);
        // Return 0
        return 0;
    }

    /**
     * @return the {@code item frame searcher} command.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> itemFrameSearcherCommand(CommandBuildContext commandBuildContext) {
        return Commands.literal("itemframesearcher")
                // Works if the player has item frame searching enabled and the mod is enabled
                .requires(source -> common().itemFrameSearching && client().general().enableMod)
                .then(
                        Commands.literal("clear") // Clear argument, which removes all glow from item frames
                                .executes(
                                        context -> execute(
                                                context.getSource(),
                                                "",
                                                true,
                                                0,
                                                client().misc().itemFrameSearchRadius
                                        )
                                )
                                .then( // Radius for the clear argument
                                        Commands.argument(RADIUS, IntegerArgumentType.integer(1, 300))
                                                .executes(
                                                        context -> execute(
                                                                context.getSource(),
                                                                "",
                                                                true,
                                                                0,
                                                                IntegerArgumentType.getInteger(context, RADIUS)
                                                        )
                                                )
                                )
                )
                .then(
                        Commands.literal("find") // Find argument, which searches item frames
                                .then(
                                        Commands.argument(ITEM, StringArgumentType.string())
                                                .executes(
                                                        context -> execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, ITEM),
                                                                false,
                                                                0,
                                                                client().misc().itemFrameSearchRadius
                                                        )
                                                )
                                                .then( // Radius argument for searching item frames
                                                        Commands.argument(RADIUS, IntegerArgumentType.integer(1, 300))
                                                                .executes(
                                                                        context -> execute(
                                                                                context.getSource(),
                                                                                StringArgumentType.getString(context, ITEM),
                                                                                false,
                                                                                0,
                                                                                IntegerArgumentType.getInteger(context, RADIUS)
                                                                        )
                                                                )
                                                                .then( // Glow duration argument, which determines how long item frames should glow
                                                                        Commands.argument(GLOW_DURATION, IntegerArgumentType.integer(0, 180))
                                                                                .executes(
                                                                                        context -> execute(
                                                                                                context.getSource(),
                                                                                                StringArgumentType.getString(context, ITEM),
                                                                                                false,
                                                                                                IntegerArgumentType.getInteger(context, GLOW_DURATION),
                                                                                                IntegerArgumentType.getInteger(context, RADIUS)
                                                                                        )
                                                                                )
                                                                )
                                                )
                                )
                );
    }
}