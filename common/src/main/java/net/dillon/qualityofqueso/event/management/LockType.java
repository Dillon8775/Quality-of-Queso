package net.dillon.qualityofqueso.event.management;

/**
 * Different types of locks to render.
 */
public enum LockType {
    KEY("slot_key"),
    UNLOCK("unlock_slot"),
    LOCK("locked_slot");

    private final String name;

    LockType(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}