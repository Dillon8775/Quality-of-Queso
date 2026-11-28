package net.dillon.qualityofqueso.option;

import java.util.function.Consumer;

import static net.dillon.dillonlib.util.BaseOptions.guardedInstance;

/**
 * Getters for all Quality of Queso's {@code common} option instances.
 */
public class CommonOptionInstances {

    /**
     * @return the common-options.
     */
    public static ModCommonOptions common() {
        return guardedInstance(ModCommonOptions.INSTANCE.get());
    }

    /**
     * Updates the common option instance.
     */
    public static void updateCommon(Consumer<ModCommonOptions> client) {
        ModCommonOptions.INSTANCE.update(client);
    }
}