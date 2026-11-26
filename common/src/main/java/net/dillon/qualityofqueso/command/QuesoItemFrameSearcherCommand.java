package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;
import net.dillon.dillonlib.util.TickValue;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.CommonOptionInstances.common;
import static net.dillon.qualityofqueso.option.ModClientOptions.getFormattedGlowDuration;
import static net.dillon.qualityofqueso.packet.ServerPacketHandlers.handleGlowPacket;

/**
 * The command to search item frames, separate from the GUI screen.
 */
@Dill(env = DillEnv.COMMON)
public class QuesoItemFrameSearcherCommand {
    private static final String ITEM = "item (string)";
    private static final String RADIUS = "radius (1-512)";
    private static final String GLOW_DURATION = "glow duration (seconds, max 180)";

    /**
     * Executes the command and makes respective item frames glow.
     */
    private static int execute(CommandSourceStack source, String query, boolean clear, int timer, int radius) {
        // Only works if item frame searching and the mod are enabled; checked here because
        if (!common().itemFrameSearching || !common().enableMod) {
            source.sendFailure(Component.translatable("qualityofqueso.command.disabled"));
            return 0;
        }

        // Send the "clear" message to the player
        if (clear) {
            source.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.clear", radius), true);
        } else if (timer == 0) { // Send the "searched" message to the player within a specific radius
            source.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.without_timer", query, radius), true);
        } else { // Send the "searched" message, and glow time message to the player, within a specific radius
            source.sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.executed.with_timer", query, radius, getFormattedGlowDuration(timer)), true);
        }
        // Detect if the query was match case
        boolean matchCase = query.startsWith(":");
        // Send the glow packet raw, do not use Balm; substring the query if matchCase to exclude the colon :
        handleGlowPacket(source.getPlayer(), query.substring(matchCase ? 1 : 0), query.startsWith(":"), clear, timer, radius);
        // Return 0
        return 1;
    }

    /**
     * Registers the {@code /itemframesearcher} command.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("quesoitemframesearcher")
                        .then(
                                Commands.literal("help")
                                        .executes(context -> {
                                                    context.getSource().sendSuccess(() -> Component.translatable("qualityofqueso.item_frame_searcher_command.help"), false);
                                                    return 0;
                                                }
                                        )
                        )
                        .then(
                                Commands.literal("clear") // Clear argument, which removes all glow from item frames
                                        .executes(
                                                context -> execute(
                                                        context.getSource(),
                                                        "",
                                                        true,
                                                        0,
                                                        150
                                                )
                                        )
                                        .then( // Radius for the clear argument
                                                radiusArgument()
                                                        .executes(
                                                                context -> execute(
                                                                        context.getSource(),
                                                                        "",
                                                                        true,
                                                                        0,
                                                                        getRadius(context)
                                                                )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("find") // Find argument, which searches item frames
                                        .then(
                                                itemArgument()
                                                        .executes(
                                                                context -> execute(
                                                                        context.getSource(),
                                                                        getItem(context),
                                                                        false,
                                                                        0,
                                                                        150
                                                                )
                                                        )
                                                        .then( // Radius argument for searching item frames
                                                                radiusArgument()
                                                                        .executes(
                                                                                context -> execute(
                                                                                        context.getSource(),
                                                                                        getItem(context),
                                                                                        false,
                                                                                        0,
                                                                                        getRadius(context)
                                                                                )
                                                                        )
                                                                        .then( // Glow duration argument, which determines how long item frames should glow
                                                                                glowDurationArgument()
                                                                                        .executes(
                                                                                                context -> execute(
                                                                                                        context.getSource(),
                                                                                                        getItem(context),
                                                                                                        false,
                                                                                                        getGlowDuration(context),
                                                                                                        getRadius(context)
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    /**
     * @return a created item argument.
     */
    private static RequiredArgumentBuilder<CommandSourceStack, String> itemArgument() {
        return Commands.argument(ITEM, StringArgument.string());
    }

    /**
     * @return a created radius argument.
     */
    private static RequiredArgumentBuilder<CommandSourceStack, Integer> radiusArgument() {
        return Commands.argument(RADIUS, IntegerArgument.integer(1, 512));
    }

    /**
     * @return a created glow duration argument.
     */
    private static RequiredArgumentBuilder<CommandSourceStack, Integer> glowDurationArgument() {
        return Commands.argument(GLOW_DURATION, IntegerArgument.integer(1, 300));
    }

    /**
     * @return the passed item argument.
     */
    private static String getItem(CommandContext<CommandSourceStack> source) {
        return StringArgument.getString(source, ITEM);
    }

    /**
     * @return the passed radius argument.
     */
    private static Integer getRadius(CommandContext<CommandSourceStack> source) {
        return IntegerArgument.getInteger(source, RADIUS);
    }

    /**
     * @return the passed glow duration argument.
     */
    private static Integer getGlowDuration(CommandContext<CommandSourceStack> source) {
        return new TickValue(IntegerArgument.getInteger(source, GLOW_DURATION)).toTicksSeconds();
    }
}