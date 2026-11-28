package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.dillonlib.util.TickValue;
import net.dillon.qualityofqueso.timer.Timer;
import net.dillon.qualityofqueso.timer.Timers;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Function;

import static net.dillon.qualityofqueso.helper.ModConstants.TIMERS;
import static net.dillon.qualityofqueso.timer.Timers.*;

/**
 * The command to create timers, for reminders, activities, or whatever.
 */
public class QuesoTimerCommand {

    /**
     * Displays helpful information.
     */
    private static <S> int help(S source, Function<S, ClientCommandFeedback> feedback) {
        return Timers.helpTicker(source, feedback, Timers.helpComponent("timer", "s"));
    }

    /**
     * Dismisses a timer.
     */
    private static <S> int dismiss(S source, Function<S, ClientCommandFeedback> feedback, String name) {
        return Timers.dismissTicker(feedback.apply(source), name, TIMERS, Timer.ID, true);
    }

    /**
     * Pauses a timer.
     */
    private static <S> int pause(S source, Function<S, ClientCommandFeedback> feedback, String name) {
        return Timers.pauseTicker(feedback.apply(source), name, TIMERS, Timer.ID);
    }

    /**
     * Renames a timer.
     */
    private static <S> int rename(S source, Function<S, ClientCommandFeedback> feedback, String name, String newName) {
        return Timers.renameTicker(feedback.apply(source), name, newName, TIMERS, Timer.ID);
    }

    /**
     * @throws IllegalArgumentException when unit is invalid.
     */
    private static void throwUnitException(String unit) {
        for (String validUnit : Timer.UNITS) {
            if (unit.equalsIgnoreCase(validUnit.substring(0, validUnit.length() - 1))) {
                return; // Don't throw exception
            }
        }

        if (!Timer.UNIT_SECONDS.equalsIgnoreCase(unit)
                && !Timer.UNIT_MINUTES.equalsIgnoreCase(unit)
                && !Timer.UNIT_HOURS.equalsIgnoreCase(unit)
                && !Timer.UNIT_DAYS.equalsIgnoreCase(unit)
        ) {
            throw new IllegalArgumentException("Invalid argument: use seconds, minutes, hours or days.");
        }
    }

    /**
     * Creates a new timer.
     */
    private static <S> int create(S source, Function<S, ClientCommandFeedback> feedback, String name, int duration, String unit, String gameTime) {
        throwUnitException(unit);

        int time = timeToTicks(duration, unit);
        Timer timer = Timers.createTimer(name, time, gameTime);

        return Timers.createTicker(feedback.apply(source), name, TIMERS, timer, () -> Component.translatable("qualityofqueso.timer_command.created",
                        new TickValue(time).formattedFromTick(),
                        name
                )
        );
    }

    /**
     * Adds time to a timer.
     */
    private static <S> int addTime(S source, Function<S, ClientCommandFeedback> feedback, String name, int additionalDuration, String unit) {
        throwUnitException(unit);

        if (tickerDoesntExist(feedback.apply(source), name, TIMERS, Timer.ID)) {
            return 0;
        }

        Timer timer = Timers.getTickerByName(name, TIMERS);
        int additionalTime = timeToTicks(additionalDuration, unit);
        Objects.requireNonNull(timer).ticks += additionalTime;
        feedback.apply(source).sendSuccess(Component.translatable("qualityofqueso.timer_command.added_time", new TickValue(additionalTime).formattedFromTick(), name));
        return 1;
    }

    /**
     * @return the {@code /timer} command.
     */
    public static <S> LiteralArgumentBuilder<S> create(Function<S, ClientCommandFeedback> feedback) {
        return LiteralArgumentBuilder.<S>literal("queso_timer")
                .then(
                        LiteralArgumentBuilder.<S>literal("help")
                                .executes(context -> help(
                                                context.getSource(),
                                                feedback
                                        )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("dismiss")
                                .then(
                                        Timers.<S>timeNameArgument(false)
                                                .executes(context -> dismiss(
                                                                context.getSource(),
                                                                feedback,
                                                                getName(context)
                                                        )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("pause")
                                .then(
                                        Timers.<S>timeNameArgument(false)
                                                .executes(context -> pause(
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
                                        Timers.<S>timeNameArgument(false)
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
                        LiteralArgumentBuilder.<S>literal("addtime")
                                .then(
                                        Timers.<S>timeNameArgument(false)
                                                .then(
                                                        Timers.<S>durationArgument()
                                                                .then(
                                                                        Timers.<S>unitArgument()
                                                                                .executes(context -> addTime(
                                                                                                context.getSource(),
                                                                                                feedback,
                                                                                                getName(context),
                                                                                                getDuration(context),
                                                                                                getUnit(context)
                                                                                        )
                                                                                )
                                                                )
                                                )
                                )
                )
                .then(
                        LiteralArgumentBuilder.<S>literal("create")
                                .then(
                                        Timers.<S>timeNameArgument(true)
                                                .then(
                                                        Timers.<S>durationArgument()
                                                                .then(
                                                                        Timers.<S>unitArgument()
                                                                                .then(
                                                                                        Timers.<S>gameTimeArgument()
                                                                                                .executes(context -> create(
                                                                                                                context.getSource(),
                                                                                                                feedback,
                                                                                                                getName(context),
                                                                                                                getDuration(context),
                                                                                                                getUnit(context),
                                                                                                                getGameTime(context)
                                                                                                        )
                                                                                                )
                                                                                )
                                                                )
                                                )
                                )
                );
    }
}