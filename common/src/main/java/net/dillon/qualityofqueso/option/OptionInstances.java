package net.dillon.qualityofqueso.option;

import java.util.function.Consumer;

/**
 * Getters for all Quality of Queso's option instances.
 */
public class OptionInstances {

    /**
     * @return the client-options.
     */
    public static ModClientOptions client() {
        return ModClientOptions.getInstance().get();
    }

    /**
     * @return the common-options.
     */
    public static ModCommonOptions common() {
        return ModCommonOptions.getInstance().get();
    }

    /**
     * @return universal options, unaffected by server configs.
     */
    public static UniversalOptions universal() {
        return UniversalOptions.getInstance().get();
    }

    /**
     * @return mixin options, unaffected by server configs.
     */
    public static MixinOptions mixins() {
        return MixinOptions.getInstance().get();
    }

    /**
     * @return tracked containers options.
     */
    public static ContainerData containerData() {
        return ContainerData.getInstance().get();
    }

    /**
     * @return locked player slots, respective to the client-player.
     */
    public static LockedPlayerSlots lockedPlayerSlots() {
        return LockedPlayerSlots.getInstance().get();
    }

    /**
     * @return locked container slots, for each container in a world.
     */
    public static LockedContainerSlots lockedContainerSlots() {
        return LockedContainerSlots.getInstance().get();
    }

    /**
     * Updates the client option instance.
     */
    public static void updateClient(Consumer<ModClientOptions> client) {
        ModClientOptions.getInstance().update(client);
    }
}