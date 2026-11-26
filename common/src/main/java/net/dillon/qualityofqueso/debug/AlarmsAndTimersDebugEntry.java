package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.util.TickValue;
import net.dillon.qualityofqueso.timer.Alarm;
import net.dillon.qualityofqueso.timer.Stopwatch;
import net.dillon.qualityofqueso.timer.Timer;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

/**
 * Displays {@link Timer}s, {@link Alarm}s and {@link Stopwatch}s on the debug hud.
 */
public class AlarmsAndTimersDebugEntry extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        if (!modEnabled()) {
            return;
        }

        for (Alarm alarm : ALARMS) {
            String name = alarm.name;
            displayer.addFactToGroup(
                    ModDebugScreenEntries.ALARMS_AND_TIMERS_GROUP,
                    client().dingsAndAlarms().showTimeUntilAlarmSounds ? name + " (" + new TickValue(alarm.ticks).formattedFromTick() + ")" : name,
                    fact -> fact.value(alarm.formattedDisplay())
            );
        }

        for (Timer timer : TIMERS) {
            displayer.addFactToGroup(
                    ModDebugScreenEntries.ALARMS_AND_TIMERS_GROUP,
                    timer.name,
                    fact -> fact.value(
                            new TickValue(timer.ticks).formattedFromTick()
                                    + timer.pausedLabel()
                    )
            );
        }

        for (Stopwatch stopwatch : STOPWATCHES) {
            displayer.addFactToGroup(
                    ModDebugScreenEntries.ALARMS_AND_TIMERS_GROUP,
                    stopwatch.name,
                    fact -> fact.value(
                            new TickValue.TimeFormatter(new TickValue(stopwatch.ticks))
                                    .includeMilliseconds(client().dingsAndAlarms().msForStopwatches)
                                    .build()
                                    + stopwatch.pausedLabel()
                    )
            );
        }
    }
}