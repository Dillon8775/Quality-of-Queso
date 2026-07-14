package net.dillon.qualityofqueso.util;

/**
 * A list of mapped {@code mixin class names} with {@code booleans} on whether the mixin should be enabled.
 * <p>If the mapped boolean returns {@code true}, the every mapped mixin class name should be {@code disabled.}</p>
 */
public record PredicateEntry(String[] mixins, boolean condition, String reason) {}