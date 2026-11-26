package net.dillon.qualityofqueso.option;

import java.util.function.Consumer;

import static net.dillon.dillonlib.util.BaseOptions.guardedInstance;

/**
 * Getters for all Quality of Queso's option instances.
 */
public class OptionInstances {

    /**
     * @return the client-options.
     */
    public static ModClientOptions client() {
        return guardedInstance(ModClientOptions.INSTANCE.get());
    }

    /**
     * @return the common-options.
     */
    public static ModCommonOptions common() {
        return guardedInstance(ModCommonOptions.INSTANCE.get());
    }

    /**
     * @return universal options, unaffected by server configs.
     */
    public static UniversalOptions universal() {
        return guardedInstance(UniversalOptions.INSTANCE.get());
    }

    /**
     * @return mixin options, unaffected by server configs.
     */
    public static MixinOptions mixins() {
        return guardedInstance(MixinOptions.INSTANCE.get());
    }

    /**
     * @return tracked containers options.
     */
    public static ContainerData containerData() {
        return guardedInstance(ContainerData.INSTANCE.get());
    }

    /**
     * @return locked player slots, respective to the client-player.
     */
    public static LockedPlayerSlots lockedPlayerSlots() {
        return LockedPlayerSlots.INSTANCE.get();
    }

    /**
     * @return locked container slots, for each container in a world.
     */
    public static LockedContainerSlots lockedContainerSlots() {
        return LockedContainerSlots.INSTANCE.get();
    }

    /**
     * Updates the client option instance.
     */
    public static void updateClient(Consumer<ModClientOptions> client) {
        ModClientOptions.INSTANCE.update(client);
    }
}