package net.dillon.qualityofqueso.timer;

/**
 * Stores an alarm.
 */
public class Alarm extends Timer {
    public static final String ID = "Alarm";
    public static final String AM = "am";
    public static final String PM = "pm";
    public final int hour;
    public final int minute;
    public final String period;
    
    protected Alarm(String name, int hour, int minute, String period, int duration) {
        super(name, duration, false);
        this.hour = hour;
        this.minute = minute;
        this.period = period;
    }

    @Override
    public String id() {
        return ID;
    }

    public String formattedDisplay() {
        return hour + ":" + String.format("%02d", minute) + period;
    }
}