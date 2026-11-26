package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.dillonlib.util.TickValue;
import net.dillon.qualityofqueso.timer.Stopwatch;
import net.dillon.qualityofqueso.timer.Timers;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Function;

import static net.dillon.qualityofqueso.helper.ModConstants.ALARM_TRIGGERED;
import static net.dillon.qualityofqueso.helper.ModConstants.STOPWATCHES;
import static net.dillon.qualityofqueso.timer.Timers.*;

/**
 * The command to create stopwatches.
 */
public class QuesoStopwatchCommand {

    /**
     * Displays helpful information.
     */
    private static <S> int help(S source, Function<S, ClientCommandFeedback> feedback) {
        return Timers.helpTicker(source, feedback, Timers.helpComponent("stopwatch", "es"));
    }

    /**
     * Creates a stopwatch.
     */
    private static <S> int create(S source, Function<S, ClientCommandFeedback> feedback, String name, String gameTime) {
        return Timers.createTicker(feedback.apply(source), name, STOPWATCHES, Timers.createStopwatch(name, gameTime));
    }

    /**
     * Dismisses a stopwatch.
     */
    private static <S> int dismiss(S source, Function<S, ClientCommandFeedback> feedback, String name) {
        ClientCommandFeedback commandFeedback = feedback.apply(source);
        if (!canExecuteCommand(commandFeedback)) {
            return 0;
        }

        Stopwatch stopwatch = Timers.getTickerByName(name, STOPWATCHES);
        if (tickerDoesntExist(commandFeedback, name, STOPWATCHES, Stopwatch.ID)) {
            return 0;
        }

        TickValue ticks = new TickValue(Objects.requireNonNull(stopwatch).ticks);
        ALARM_TRIGGERED = false;
        removeTicker(name, STOPWATCHES);
        commandFeedback.sendSuccess(Component.translatable("qualityofqueso.stopwatch_command.dismissed",
                name,
                new TickValue.TimeFormatter(ticks)
                        .includeMilliseconds(true)
                        .build()
        ));
        return 1;
    }

    /**
     * Pauses a stopwatch.
     */
    private static <S> int pause(S source, Function<S, ClientCommandFeedback> feedback, String name) {
        return Timers.pauseTicker(feedback.apply(source), name, STOPWATCHES, Stopwatch.ID);
    }

    /**
     * Renames a stopwatch.
     */
    private static <S> int rename(S source, Function<S, ClientCommandFeedback> feedback, String name, String newName) {
        return Timers.renameTicker(feedback.apply(source), name, newName, STOPWATCHES, Stopwatch.ID);
    }

    /**
     * Registers the {@code /stopwatch} command.
     */
    public static <S> LiteralArgumentBuilder<S> create(Function<S, ClientCommandFeedback> feedback) {
        return LiteralArgumentBuilder.<S>literal("quesostopwatch")
                .then(
                        LiteralArgumentBuilder.<S>literal("help")
                                .executes(context -> help(
                                                context.getSource(),
                                                feedback
                                        )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("pause")
                                .then(
                                        Timers.<S>stopwatchNameArgument(false)
                                                .executes(context -> pause(
                                                                context.getSource(),
                                                                feedback,
                                                                getName(context)
                                                        )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("dismiss")
                                .then(
                                        Timers.<S>stopwatchNameArgument(false)
                                                .executes(context -> dismiss(
                                                                context.getSource(),
                                                                feedback,
                                                                getName(context)
                                                        )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("rename")
                                .then(
                                        Timers.<S>stopwatchNameArgument(false)
                                                .then(
                                                        Timers.<S>newNameArgument()
                                                                .executes(context -> rename(
                                                                                context.getSource(),
                                                                                feedback,
                                                                                getName(context),
                                                                                getNewName(context)
                                                                        )
                                                                )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("create")
                                .then(
                                        Timers.<S>stopwatchNameArgument(true)
                                                .then(
                                                        Timers.<S>gameTimeArgument()
                                                                .executes(context -> create(
                                                                                context.getSource(),
                                                                                feedback,
                                                                                getName(context),
                                                                                getGameTime(context)
                                                                        )
                                                                )
                                                )
                                )
                );
    }
}