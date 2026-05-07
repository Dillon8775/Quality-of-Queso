package net.dillon.qualityofqueso.util;

import net.minecraft.client.Minecraft;

import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.screen.option.ListOptionUtil.visualTimeStepToTicks;

/**
 * Tracks the client-side time using this tracker.
 */
public class VisualTimeTracker {
    private static int anchorTime = -1;
    private static int lastConfigBaseTime = -1;
    private static int lastSpeed = -1;
    private static long anchorGameTick = 0L;

    /**
     * @return the visual time to display, {@code client-side.}
     */
    public static long getVisualTime(Minecraft minecraft) {
        int configBaseTime = visualTimeStepToTicks(options().visualTime.visualTime);
        int speed = options().visualTime.visualTimeSpeed;

        if (minecraft.level == null) {
            return configBaseTime;
        }

        long currentGameTick = minecraft.level.getGameTime();

        if (anchorTime == -1) {
            anchorTime = configBaseTime;
            lastConfigBaseTime = configBaseTime;
            lastSpeed = speed;
            anchorGameTick = currentGameTick;
        }

        long currentVisualTime = calculateVisualTime(currentGameTick);

        // Only reset to clientVisualTime when the actual clientVisualTime option changed
        if (configBaseTime != lastConfigBaseTime) {
            anchorTime = configBaseTime;
            lastConfigBaseTime = configBaseTime;
            lastSpeed = speed;
            anchorGameTick = currentGameTick;
            return anchorTime;
        }

        // Freeze at clientVisualTime only when speed is 0
        if (speed == 0) {
            anchorTime = configBaseTime;
            lastConfigBaseTime = configBaseTime;
            lastSpeed = speed;
            anchorGameTick = currentGameTick;
            return anchorTime;
        }

        // Speed changed: preserve current visual time, then continue at new speed
        if (speed != lastSpeed) {
            anchorTime = (int) currentVisualTime;
            lastSpeed = speed;
            anchorGameTick = currentGameTick;
            return anchorTime;
        }

        return currentVisualTime;
    }

    /**
     * Calculates the visual time, based on the speed.
     */
    private static long calculateVisualTime(long currentGameTick) {
        if (lastSpeed <= 0) {
            return anchorTime;
        }

        long elapsedTicks = currentGameTick - anchorGameTick;

        double multiplier = (lastSpeed / 100.0) * 20;
        long addedTicks = (long)(elapsedTicks * multiplier);

        return Math.floorMod(anchorTime + addedTicks, 24000L);
    }
}