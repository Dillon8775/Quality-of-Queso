package net.dillon.qualityofqueso.event;

/**
 * A representation of an {@code Mod Event}, which does different things. Events, aka instances, are utility classes for exposing methods and variables, specifically related to Quality of Queso.
 */
public interface ModEvent {

    /**
     * Stores the screen holder for the mod event.
     */
    QuesoScreenHolder holder();
}