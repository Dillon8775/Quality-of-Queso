package net.dillon.qualityofqueso.util;

/**
 * Utility class for scrolling to Quality of Queso keybinds.
 */
public class KeybindScrollHelper {
    private static boolean pending;

    public static void requestScroll() {
        pending = true;
    }

    public static boolean consumeRequest() {
        boolean value = pending;
        pending = false;
        return value;
    }
}