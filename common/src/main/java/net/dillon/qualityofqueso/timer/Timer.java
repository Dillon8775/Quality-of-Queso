package net.dillon.qualityofqueso.timer;

import java.util.Set;

/**
 * Stores a timer.
 */
public class Timer {
    public static final String ID = "Timer";
    public static final String UNIT_SECONDS = "seconds";
    public static final String UNIT_MINUTES = "minutes";
    public static final String UNIT_HOURS = "hours";
    public static final String UNIT_DAYS = "days";
    public static final Set<String> UNITS = Set.of(UNIT_SECONDS, UNIT_MINUTES, UNIT_DAYS, UNIT_HOURS);
    public static final String GAME_TIME = "game_time";
    public static final String REAL_TIME = "real_time";
    public final boolean gameTime;
    public String name;
    public int ticks;
    public boolean stopped;
    public boolean paused;

    protected Timer(String name, int duration, boolean gameTime) {
        this.name = name;
        this.ticks = duration;
        this.gameTime = gameTime;
    }

    /**
     * @return if the timer should be labeled as paused.
     */
    public boolean isPaused() {
        return stopped || paused;
    }

    /**
     * @return the pause label.
     */
    public String pausedLabel() {
        return isPaused() ? " (paused)" : "";
    }

    /**
     * @return the id for this timer.
     */
    public String id() {
        return ID;
    }
}