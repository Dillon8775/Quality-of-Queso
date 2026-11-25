package net.dillon.qualityofqueso.timer;

/**
 * Stores a stopwatch.
 */
public class Stopwatch extends Timer {
    public static final String ID = "Stopwatch";

    protected Stopwatch(String name, int duration, boolean gameTime) {
        super(name, duration, gameTime);
    }

    @Override
    public String id() {
        return ID;
    }
}