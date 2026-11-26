package net.dillon.qualityofqueso.timer;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dillon.dillonlib.client.command.ClientCommandFeedback;
import net.dillon.dillonlib.util.TickValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;
import static net.dillon.dillonlib.task.CommonTasks.sendOverlayMessage;
import static net.dillon.dillonlib.task.CommonTasks.sendSystemMessage;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * Helper methods for creating timer objects.
 */
public class Timers {
    private static final String NAME_ARGUMENT = "name";
    private static final String NEW_NAME_ARGUMENT = "new_name";
    private static final String DURATION_ARGUMENT = "duration";
    private static final String UNIT_ARGUMENT = "days|hours|minutes|seconds";
    private static final String GAME_TIME_ARGUMENT = "game_time|real_time";
    private static final String PERIOD_ARGUMENT = "am|pm";

    /**
     * @throws IllegalArgumentException when game time is invalid.
     */
    private static void throwPeriodsException(String gameTime) {
        if (!Timer.GAME_TIME.equalsIgnoreCase(gameTime) && !Timer.REAL_TIME.equalsIgnoreCase(gameTime)) {
            throw new IllegalArgumentException("Invalid argument: use game_time or real_time.");
        }
    }

    /**
     * @return a new {@code timer}.
     */
    public static Timer createTimer(String name, int duration, String gameTime) {
        throwPeriodsException(gameTime);
        return new Timer(name, duration, isGameTime(gameTime));
    }

    /**
     * @return a new {@code stopwatch}.
     */
    public static Stopwatch createStopwatch(String name, String gameTime) {
        throwPeriodsException(gameTime);
        return new Stopwatch(name, 0, isGameTime(gameTime));
    }

    /**
     * @return a new {@code alarm}.
     */
    public static Alarm createAlarm(String name, int hour, int minute, String period) {
        if (!Alarm.AM.equalsIgnoreCase(period) && !Alarm.PM.equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Invalid argument: use am or pm.");
        }

        int militaryHour = isAm(period)
                ? (hour == 12 ? 0 : hour)
                : (hour == 12 ? 12 : hour + 12);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.withHour(militaryHour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);

        if (!end.isAfter(start)) {
            end = end.plusDays(1);
        }

        Duration duration = Duration.between(start, end);
        int time = timeToTicks((int) duration.toSeconds(), Timer.UNIT_SECONDS);

        return new Alarm(name, hour, minute, period, time);
    }

    /**
     * @return if the user can execute a timer command.
     */
    public static boolean canExecuteCommand(ClientCommandFeedback source) {
        if (!modEnabled()) {
            source.sendFailure(Component.translatable("qualityofqueso.command.disabled"));
            return false;
        }

        return true;
    }

    /**
     * @return if a ticker doesn't exist.
     */
    public static <T extends Timer> boolean tickerDoesntExist(ClientCommandFeedback source, String name, List<T> tickers, String id) {
        if (!canExecuteCommand(source)) {
            return true;
        }

        if (!Timers.contains(name, tickers)) {
            source.sendFailure(Component.translatable("qualityofqueso.timer.doesnt_exist", id.toLowerCase()));
            return true;
        }

        return false;
    }

    /**
     * @return if a ticker exists.
     */
    public static <T extends Timer> boolean tickerExists(ClientCommandFeedback source, String name, List<T> tickers, String id) {
        if (!canExecuteCommand(source)) {
            return true;
        }

        if (Timers.contains(name, tickers)) {
            source.sendSuccess(Component.translatable("qualityofqueso.timer.already_exists", id.toLowerCase()));
            return true;
        }

        return false;
    }

    /**
     * @return if a ticker exists.
     */
    public static <T extends Timer> boolean contains(String name, List<T> type) {
        return getTickerByName(name, type) != null;
    }

    /**
     * @return a {@link Timer} by its name.
     */
    public static <T extends Timer> T getTickerByName(String name, List<T> type) {
        for (T ticker : type) {
            if (ticker.name.equals(name)) {
                return ticker;
            }
        }

        return null;
    }

    /**
     * @return a help component for a ticker command.
     */
    public static Component helpComponent(String ticker, String appended) {
        return Component.translatable("qualityofqueso." + ticker + "_command.help", helpGameTime(ticker), helpDiscarded(ticker, appended));
    }

    /**
     * @return the help component for the game_time parameter for a ticker.
     */
    public static Component helpGameTime(String ticker) {
        return Component.translatable("qualityofqueso.timer_command.help.game_time", ticker, ticker, ticker);
    }

    /**
     * @return the discarded message for tickers.
     */
    public static Component helpDiscarded(String ticker, String appended) {
        return Component.translatable("qualityofqueso.timer_command.help.discarded", Component.literal(ticker + appended).withStyle(ChatFormatting.RED));
    }

    /**
     * Displays helpful information for a ticker.
     */
    public static <S> int helpTicker(S source, Function<S, ClientCommandFeedback> feedback, Component message) {
        feedback.apply(source).sendSuccess(message);
        return 0;
    }

    /**
     * Creates a ticker.
     */
    public static <T extends Timer> int createTicker(ClientCommandFeedback source, String name, List<T> tickers, T ticker) {
        return createTicker(source, name, tickers, ticker, () -> Component.translatable("qualityofqueso.timer.created",
                ticker.id().toLowerCase(),
                name
        ));
    }

    /**
     * Creates a ticker with a custom message supplier.
     */
    public static <T extends Timer> int createTicker(ClientCommandFeedback source, String name, List<T> tickers, T ticker, Supplier<Component> message) {
        if (tickerExists(source, name, tickers, ticker.id())) {
            return 0;
        }

        tickers.add(ticker);
        source.sendSuccess(message.get());
        return 1;
    }

    /**
     * Removes a ticker.
     */
    public static <T extends Timer> void removeTicker(String name, List<T> tickers) {
        tickers.remove(Timers.getTickerByName(name, tickers));
    }

    /**
     * Dismisses a ticker with a custom message supplier.
     */
    public static <T extends Timer> int dismissTicker(ClientCommandFeedback source, String name, List<T> tickers, String id, boolean stopSound) {
        if (!canExecuteCommand(source)) {
            return 0;
        }

        if (stopSound) {
            ALARM_TRIGGERED = false;
        }
        removeTicker(name, tickers);
        source.sendSuccess(Component.translatable("qualityofqueso.timer.dismissed",
                id.toLowerCase(),
                name
        ));
        return 1;
    }

    /**
     * Pauses a ticker.
     */
    public static <T extends Timer> int pauseTicker(ClientCommandFeedback source, String name, List<T> tickers, String id) {
        if (tickerDoesntExist(source, name, tickers, id)) {
            return 0;
        }

        T ticker = Timers.getTickerByName(name, tickers);
        if (ticker instanceof Alarm) {
            throw new IllegalArgumentException("Cannot pause an alarm!");
        }

        Objects.requireNonNull(ticker).stopped = !ticker.stopped;
        source.sendSuccess(Component.translatable("qualityofqueso.timer.paused",
                ticker.stopped ? "Paused" : "Unpaused",
                id.toLowerCase(),
                name
        ));
        return 1;
    }

    /**
     * Renames a ticker.
     */
    public static <T extends Timer> int renameTicker(ClientCommandFeedback source, String name, String newName, List<T> tickers, String id) {
        if (tickerDoesntExist(source, name, tickers, id)) {
            return 0;
        }

        T ticker = Timers.getTickerByName(name, tickers);
        Objects.requireNonNull(ticker).name = newName;
        source.sendSuccess(Component.translatable("qualityofqueso.timer.renamed",
                id.toLowerCase(),
                name,
                newName
        ));
        return 1;
    }

    /**
     * @return time to ticks.
     */
    public static int timeToTicks(int duration, String unit) {
        return switch (unit) {
            case Timer.UNIT_DAYS -> new TickValue(duration).toTicksDays();
            case Timer.UNIT_HOURS -> new TickValue(duration).toTicksHours();
            case Timer.UNIT_MINUTES -> new TickValue(duration).toTicksMinutes();
            default -> new TickValue(duration).toTicksSeconds();
        };
    }

    /**
     * Ticks common timers.
     */
    public static void tick() {
        if (ALARM_TRIGGERED_COOLDOWN > 0) {
            ALARM_TRIGGERED_COOLDOWN--;
        }

        LocalPlayer player = getLocalPlayer();
        if (player == null) {
            ALARM_TRIGGERED = false;
        }

        // Tick all real-time stopwatches
        for (Timer stopwatch : STOPWATCHES) {
            if (!stopwatch.gameTime) {
                if (!stopwatch.stopped) {
                    stopwatch.ticks++;
                }
            } else {
                stopwatch.paused = getMinecraft().isPaused() || player == null;
            }
        }

        Timers.tickTickers(player, ALARMS);
        Timers.tickTickers(player, TIMERS);
    }

    /**
     * Ticks a game timer.
     */
    public static <T extends Timer> void tickGameTimer(List<T> list, boolean increment) {
        for (T timer : list) {
            if (!timer.stopped && timer.gameTime) {
                if (increment) {
                    timer.ticks++;
                } else {
                    timer.ticks--;
                }
            }
        }
    }

    /**
     * Ticks all certain types of tickers.
     */
    private static <T extends Timer> void tickTickers(LocalPlayer player, List<T> list) {
        Iterator<T> timers = list.iterator();
        while (timers.hasNext()) {
            T timer = timers.next();

            boolean isAlarm = timer instanceof Alarm;
            String id = timer.id();

            if (!timer.stopped) {
                // Tick all real-time timers
                if (!timer.gameTime) {
                    timer.ticks--;
                } else {
                    timer.paused = getMinecraft().isPaused() || player == null;
                }

                if (player != null) {
                    int ticksRemaining = timer.ticks;
                    boolean threeMinutes = ticksRemaining == new TickValue(3).toTicksMinutes();
                    boolean oneMinute = ticksRemaining == new TickValue(1).toTicksMinutes();

                    Component message = Component.translatable("qualityofqueso.timer.time_remaining", id, timer.name, new TickValue(ticksRemaining).formattedFromTick())
                            .withStyle(oneMinute ? ChatFormatting.RED : ChatFormatting.GOLD);

                    if (threeMinutes || oneMinute) {
                        sendSystemMessage(player, message);
                    }
                }
            }

            if (player != null && timer.ticks == 0) {
                String name = timer.name;
                String command = (isAlarm ? "quesoalarm dismiss " : "quesotimer dismiss ") + quotedName(name);

                sendOverlayMessage(player, Component.translatable("qualityofqueso.timer.triggered_overlay", id, name).withStyle(ChatFormatting.GREEN));
                sendSystemMessage(player, Component.translatable("qualityofqueso.timer.triggered", id, name)
                        .setStyle(
                                Style.EMPTY
                                        .withHoverEvent(
                                                new HoverEvent.ShowText(Component.translatable("qualityofqueso.timer.dismiss_timer", id.toLowerCase(), name))
                                        )
                                        .withClickEvent(
                                                new ClickEvent.RunCommand(command)
                                        )
                        )
                        .withStyle(ChatFormatting.GREEN)
                );

                ALARM_TRIGGERED = true;

                timers.remove();
            }
        }
    }

    /**
     * @return a created name argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> timeNameArgument(boolean create) {
        return nameArgument(TIMERS, create);
    }

    /**
     * @return a created stopwatch argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> stopwatchNameArgument(boolean create) {
        return nameArgument(STOPWATCHES, create);
    }

    /**
     * @return a created alarm argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> alarmNameArgument(boolean create) {
        return nameArgument(ALARMS, create);
    }

    /**
     * @return a created ticker argument, with a create parameter.
     */
    private static <T extends Timer, S> RequiredArgumentBuilder<S, String> nameArgument(List<T> timers, boolean create) {
        RequiredArgumentBuilder<S, String> name = RequiredArgumentBuilder.argument(NAME_ARGUMENT, StringArgument.string());
        if (!create) {
            name.suggests((context, builder) -> {
                for (T timer : timers) {
                    builder.suggest(quotedName(timer.name));
                }
                return builder.buildFuture();
            });
        }
        return name;
    }

    /**
     * @return a created new name argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> newNameArgument() {
        return RequiredArgumentBuilder.argument(NEW_NAME_ARGUMENT, StringArgument.string());
    }

    /**
     * @return a created duration argument.
     */
    public static <S> RequiredArgumentBuilder<S, Integer> durationArgument() {
        return RequiredArgumentBuilder.argument(DURATION_ARGUMENT, IntegerArgument.integer());
    }

    /**
     * @return a created unit argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> unitArgument() {
        return timeArgument(UNIT_ARGUMENT, Timer.UNITS);
    }

    /**
     * @return a created unit argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> gameTimeArgument() {
        return timeArgument(GAME_TIME_ARGUMENT, Set.of(Timer.GAME_TIME, Timer.REAL_TIME));
    }

    /**
     * @return a created period argument.
     */
    public static <S> RequiredArgumentBuilder<S, String> periodArgument() {
        return timeArgument(PERIOD_ARGUMENT, Set.of(Alarm.AM, Alarm.PM));
    }

    /**
     * @return a created time argument.
     */
    private static <S> RequiredArgumentBuilder<S, String> timeArgument(String argument, Set<String> suggestions) {
        return RequiredArgumentBuilder.<S, String>argument(argument, StringArgument.word())
                .suggests((context, builder) -> {
                    suggestions.forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }

    /**
     * @return the passed name argument.
     */
    public static <S> String getName(CommandContext<S> source) {
        return StringArgument.getString(source, NAME_ARGUMENT);
    }

    /**
     * @return the passed new name argument.
     */
    public static <S> String getNewName(CommandContext<S> source) {
        return StringArgument.getString(source, NEW_NAME_ARGUMENT);
    }

    /**
     * @return the passed duration argument.
     */
    public static <S> Integer getDuration(CommandContext<S> source) {
        return IntegerArgument.getInteger(source, DURATION_ARGUMENT);
    }

    /**
     * @return the passed game time argument.
     */
    public static <S> String getGameTime(CommandContext<S> source) {
        return StringArgument.getString(source, GAME_TIME_ARGUMENT);
    }

    /**
     * @return the passed unit argument.
     */
    public static <S> String getUnit(CommandContext<S> source) {
        return StringArgument.getString(source, UNIT_ARGUMENT);
    }

    /**
     * @return the passed period argument.
     */
    public static <S> String getPeriod(CommandContext<S> context) {
        return StringArgument.getString(context, PERIOD_ARGUMENT);
    }

    /**
     * @return if a passed str is game time.
     */
    private static boolean isGameTime(String str) {
        return str.equals(Timer.GAME_TIME);
    }

    /**
     * @return if a passed str is game time.
     */
    private static boolean isAm(String str) {
        return str.equalsIgnoreCase(Alarm.AM);
    }

    /**
     * @return the name in quotes.
     */
    public static String quotedName(String name) {
        return name.contains(" ") ? "\"" + name + "\"" : name;
    }
}