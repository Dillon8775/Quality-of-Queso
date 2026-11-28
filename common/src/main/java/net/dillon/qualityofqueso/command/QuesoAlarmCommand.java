package net.dillon.qualityofqueso.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.qualityofqueso.timer.Alarm;
import net.dillon.qualityofqueso.timer.Timers;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

import static net.dillon.qualityofqueso.helper.ModConstants.ALARMS;
import static net.dillon.qualityofqueso.timer.Timers.*;

/**
 * The command to create alarms.
 */
public class QuesoAlarmCommand {
    private static final String hourArgument = "hour";
    private static final String minuteArgument = "minute";

    /**
     * Displays helpful information.
     */
    private static <S> int help(S source, Function<S, ClientCommandFeedback> feedback) {
        return Timers.helpTicker(source, feedback, Component.translatable("qualityofqueso.alarm_command.help", helpDiscarded("alarm", "s")));
    }

    /**
     * Creates an alarm.
     */
    private static <S> int create(S source, Function<S, ClientCommandFeedback> feedback, String name, int hour, int minute, String period) {
        return Timers.createTicker(feedback.apply(source), name, ALARMS, Timers.createAlarm(name, hour, minute, period));
    }

    /**
     * Dismisses an alarm.
     */
    private static <S> int dismiss(S source, Function<S, ClientCommandFeedback> feedback, String name) {
        return Timers.dismissTicker(feedback.apply(source), name, ALARMS, Alarm.ID, true);
    }

    /**
     * Renames an alarm.
     */
    private static <S> int rename(S source, Function<S, ClientCommandFeedback> feedback, String name, String newName) {
        return Timers.renameTicker(feedback.apply(source), name, newName, ALARMS, Alarm.ID);
    }

    /**
     * @return the {@code /quesoalarm} command.
     */
    public static <S> LiteralArgumentBuilder<S> create(Function<S, ClientCommandFeedback> feedback) {
        return LiteralArgumentBuilder.<S>literal("queso_alarm")
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
                                        Timers.<S>alarmNameArgument(false)
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
                                        Timers.<S>alarmNameArgument(false)
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
                                        Timers.<S>alarmNameArgument(true)
                                                .then(
                                                        QuesoAlarmCommand.<S>hourArgument()
                                                                .then(
                                                                        QuesoAlarmCommand.<S>minuteArgument()
                                                                                .then(
                                                                                        Timers.<S>periodArgument()
                                                                                                .executes(context -> create(
                                                                                                                context.getSource(),
                                                                                                                feedback,
                                                                                                                getName(context),
                                                                                                                getHour(context),
                                                                                                                getMinute(context),
                                                                                                                getPeriod(context)
                                                                                                        )
                                                                                                )
                                                                                )
                                                                )
                                                )
                                )
                );
    }

    /**
     * @return a created hour argument.
     */
    private static <S> RequiredArgumentBuilder<S, Integer> hourArgument() {
        return RequiredArgumentBuilder.argument(hourArgument, IntegerArgument.integer(1, 12));
    }

    /**
     * @return a created hour argument.
     */
    private static <S> RequiredArgumentBuilder<S, Integer> minuteArgument() {
        return RequiredArgumentBuilder.argument(minuteArgument, IntegerArgument.integer(0, 59));
    }

    /**
     * @return the passed hour argument.
     */
    private static <S> Integer getHour(CommandContext<S> source) {
        return IntegerArgument.getInteger(source, hourArgument);
    }

    /**
     * @return the passed minute argument.
     */
    private static <S> Integer getMinute(CommandContext<S> source) {
        return IntegerArgument.getInteger(source, minuteArgument);
    }
}