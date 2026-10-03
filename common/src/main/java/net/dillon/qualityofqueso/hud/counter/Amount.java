package net.dillon.qualityofqueso.hud.counter;

/**
 * Stores an amount and determines if part of the count comes from a transportable container.
 */
public record Amount(
        int count,
        boolean inTransportable
) {
}